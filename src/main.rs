use eyre::{Context, OptionExt, bail, eyre};
use openssl::ssl::{SslConnector, SslMethod, SslVerifyMode, SslVersion};
use quick_xml::{
    Reader,
    events::{BytesStart, Event},
};
use std::{
    io::{Read, Write},
    net::TcpStream,
};

const DEFAULT_USER: &str = "admin";
const DEFAULT_PASSWORD: &str = "admin";
const MAX_FRAME_SIZE: usize = 1024 * 1024;

fn main() -> eyre::Result<()> {
    let host = std::env::args().nth(1).unwrap();
    let ports = enumerate_ports(&host, DEFAULT_USER, DEFAULT_PASSWORD)?;

    for port in ports {
        println!(
            "{:>3} {:<28} {:<12} id={} status={} available={}",
            port.index.unwrap_or(0),
            port.name.as_deref().unwrap_or(""),
            port.port_type.as_deref().unwrap_or(""),
            port.id,
            port.status.unwrap_or(-1),
            port.available.unwrap_or(-1),
        );
    }

    Ok(())
}

fn enumerate_ports(host: &str, user: &str, password: &str) -> eyre::Result<Vec<Port>> {
    let mut stream =
        TcpStream::connect((host, 5000)).wrap_err_with(|| format!("connecting to {host}:5000"))?;

    let greeting = read_frame(&mut stream)?;
    if !greeting.starts_with(b"<CSC") {
        bail!("unexpected CSC greeting: {}", display_xml(&greeting));
    }
    write_frame(&mut stream, "<CSC_Ack/>")?;

    let info = read_frame(&mut stream)?;
    if !info.starts_with(b"<CSC_Info") {
        bail!("unexpected CSC info: {}", display_xml(&info));
    }
    write_frame(&mut stream, r#"<CSC_Start_Session ProtocolID="RDM"/>"#)?;

    let mut tls = tls_connector()?.connect(host, stream)?;
    write_frame(
        &mut tls,
        &format!(
            r#"<CSC_Auth UserName="{}" Password="{}"/>"#,
            escape_xml(user),
            escape_xml(password)
        ),
    )?;
    let auth = read_frame(&mut tls)?;
    if !auth.starts_with(b"<CSC_Pass") {
        bail!("authentication failed: {}", display_xml(&auth));
    }

    let session = database_query(&mut tls, "<Session><GetSessionID/></Session>")?;
    let session_id =
        xml_text(&session, "SessionID")?.ok_or_eyre("response did not contain SessionID")?;

    let inventory = database_query(
        &mut tls,
        "<Database><Get><Select>/System/Device[@Type='IP-Reach']/Port</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>",
    )?;
    let mut ports = parse_ports(&inventory)?;
    let connections: Vec<String> = ports
        .iter()
        .filter_map(|port| port.connection.clone())
        .collect();

    // The session ID is deliberately fetched before inventory; this also makes
    // failures explicit instead of silently accepting an unauthenticated query.
    let _ = session_id;
    for device_id in connections {
        let response = database_query(
            &mut tls,
            &format!(
                "<Database><Get><Select>/System/Device[@id='{}']</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>",
                escape_xml(&device_id)
            ),
        )?;
        ports.extend(parse_ports(&response)?);
    }

    Ok(ports
        .into_iter()
        .filter(|port| port.class.as_deref() == Some("KVM"))
        .collect())
}

fn tls_connector() -> eyre::Result<SslConnector> {
    let mut builder = SslConnector::builder(SslMethod::tls())?;
    builder.set_cipher_list("DEFAULT:@SECLEVEL=0")?;
    builder.set_min_proto_version(Some(SslVersion::TLS1))?;
    builder.set_max_proto_version(Some(SslVersion::TLS1))?;
    builder.set_verify(SslVerifyMode::NONE);
    Ok(builder.build())
}

fn database_query<S: Read + Write>(stream: &mut S, request: &str) -> eyre::Result<String> {
    write_frame(stream, request)?;
    let response = read_frame(stream)?;
    Ok(String::from_utf8(response)?
        .trim_end_matches('\0')
        .to_owned())
}

fn write_frame<W: Write>(stream: &mut W, payload: &str) -> eyre::Result<()> {
    let payload = payload.as_bytes();
    let frame_len = payload.len().checked_add(5).ok_or_eyre("frame too large")?;
    let frame_len = u32::try_from(frame_len).map_err(|_| eyre!("frame too large"))?;
    stream.write_all(&frame_len.to_be_bytes())?;
    stream.write_all(payload)?;
    stream.write_all(&[0])?;
    stream.flush()?;
    Ok(())
}

fn read_frame<R: Read>(stream: &mut R) -> eyre::Result<Vec<u8>> {
    let mut header = [0; 4];
    stream.read_exact(&mut header)?;
    let frame_len = u32::from_be_bytes(header) as usize;
    if !(5..=MAX_FRAME_SIZE).contains(&frame_len) {
        bail!("invalid CSC frame length: {frame_len}");
    }
    let mut payload = vec![0; frame_len - 4];
    stream.read_exact(&mut payload)?;
    if payload.pop() != Some(0) {
        bail!("CSC frame was not NUL terminated");
    }
    Ok(payload)
}

#[derive(Debug, PartialEq)]
struct Port {
    id: String,
    class: Option<String>,
    port_type: Option<String>,
    index: Option<i32>,
    status: Option<i32>,
    available: Option<i32>,
    connection: Option<String>,
    name: Option<String>,
}

fn parse_ports(xml: &str) -> eyre::Result<Vec<Port>> {
    let mut reader = Reader::from_str(xml);
    reader.config_mut().trim_text(true);
    let mut ports = Vec::new();
    let mut current: Option<Port> = None;
    let mut element = None;

    loop {
        match reader.read_event()? {
            Event::Start(start) if start.name().as_ref() == b"Port" => {
                current = Some(port_from_attributes(&start)?);
            }
            Event::Start(start) if current.is_some() => {
                element = Some(start.name().as_ref().to_vec());
            }
            Event::Text(text) if current.is_some() => {
                if element.as_deref() == Some(b"Name") {
                    if let Some(port) = current.as_mut() {
                        port.name = Some(text.unescape()?.into_owned());
                    }
                }
            }
            Event::End(end) if end.name().as_ref() == b"Port" => {
                if let Some(port) = current.take() {
                    ports.push(port);
                }
                element = None;
            }
            Event::End(_) => element = None,
            Event::Eof => break,
            _ => {}
        }
    }
    Ok(ports)
}

fn port_from_attributes(start: &BytesStart<'_>) -> eyre::Result<Port> {
    let mut id = None;
    let mut port = Port {
        id: String::new(),
        class: None,
        port_type: None,
        index: None,
        status: None,
        available: None,
        connection: None,
        name: None,
    };
    for attribute in start.attributes() {
        let attribute = attribute?;
        let value = attribute.unescape_value()?.into_owned();
        match attribute.key.as_ref() {
            b"id" => id = Some(value),
            b"Class" => port.class = Some(value),
            b"Type" => port.port_type = Some(value),
            b"index" => port.index = value.parse().ok(),
            b"Status" => port.status = value.parse().ok(),
            b"StatAvailable" => port.available = value.parse().ok(),
            b"Connection" => port.connection = Some(value),
            _ => {}
        }
    }
    port.id = id.ok_or_eyre("port has no id")?;
    Ok(port)
}

fn xml_text(xml: &str, wanted: &str) -> eyre::Result<Option<String>> {
    let mut reader = Reader::from_str(xml);
    let mut inside = false;
    loop {
        match reader.read_event()? {
            Event::Start(start) if start.name().as_ref() == wanted.as_bytes() => inside = true,
            Event::Text(text) if inside => return Ok(Some(text.unescape()?.into_owned())),
            Event::End(end) if end.name().as_ref() == wanted.as_bytes() => inside = false,
            Event::Eof => return Ok(None),
            _ => {}
        }
    }
}

fn escape_xml(value: &str) -> String {
    value
        .replace('&', "&amp;")
        .replace('"', "&quot;")
        .replace('<', "&lt;")
        .replace('>', "&gt;")
        .replace('\'', "&apos;")
}

fn display_xml(bytes: &[u8]) -> String {
    String::from_utf8_lossy(bytes)
        .trim_end_matches('\0')
        .to_owned()
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::io::Cursor;

    #[test]
    fn frames_are_big_endian_and_nul_terminated() {
        let mut bytes = Cursor::new(Vec::new());
        write_frame(&mut bytes, "<CSC/>").unwrap();
        assert_eq!(bytes.into_inner(), b"\0\0\0\x0b<CSC/>\0");
    }

    #[test]
    fn parses_kvm_port_attributes_and_name() {
        let ports = parse_ports(
            r#"<Device><Port id="P_1" Class="KVM" Type="VM" index="2" Status="1" StatAvailable="1" Connection="D_1"><Name>Rack &amp; 1</Name></Port></Device>"#,
        )
        .unwrap();
        assert_eq!(ports[0].name.as_deref(), Some("Rack & 1"));
        assert_eq!(ports[0].connection.as_deref(), Some("D_1"));
    }
}
