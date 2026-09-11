use eyre::{Context, OptionExt, bail, eyre};
use openssl::ssl::{SslConnector, SslMethod, SslVerifyMode, SslVersion};
use quick_xml::de::from_str;
use serde::Deserialize;
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
            port.r#type.as_deref().unwrap_or(""),
            port.id,
            port.status.unwrap_or(-1),
            port.stat_available.unwrap_or(-1),
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
    let session: SessionResponse = from_str(&session)?;
    let session_id = session
        .get_session_id
        .session_id
        .ok_or_eyre("response did not contain SessionID")?;

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

#[derive(Debug, Deserialize, PartialEq)]
#[serde(rename_all = "PascalCase")]
struct Port {
    #[serde(rename = "@id")]
    id: String,
    #[serde(rename = "@Class", default)]
    class: Option<String>,
    #[serde(rename = "@Type", default)]
    r#type: Option<String>,
    #[serde(rename = "@index", default)]
    index: Option<i32>,
    #[serde(rename = "@Status", default)]
    status: Option<i32>,
    #[serde(rename = "@StatAvailable", default)]
    stat_available: Option<i32>,
    #[serde(rename = "@Connection", default)]
    connection: Option<String>,
    name: Option<String>,
}

fn parse_ports(xml: &str) -> eyre::Result<Vec<Port>> {
    Ok(from_str::<PortDocument>(xml)?.into_ports())
}

#[derive(Debug, Default, Deserialize)]
#[serde(rename_all = "PascalCase")]
struct PortDocument {
    #[serde(default)]
    port: Vec<Port>,
    #[serde(default)]
    device: Vec<PortDocument>,
    #[serde(default)]
    get: Vec<PortDocument>,
    #[serde(default)]
    data: Vec<PortDocument>,
    #[serde(default)]
    database: Vec<PortDocument>,
    #[serde(default)]
    response: Vec<PortDocument>,
}

impl PortDocument {
    fn into_ports(self) -> Vec<Port> {
        let mut ports = self.port;
        for child in self
            .device
            .into_iter()
            .chain(self.get)
            .chain(self.data)
            .chain(self.database)
            .chain(self.response)
        {
            ports.extend(child.into_ports());
        }
        ports
    }
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "PascalCase")]
struct SessionResponse {
    #[serde(rename = "GetSessionID")]
    get_session_id: SessionData,
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "PascalCase")]
struct SessionData {
    #[serde(rename = "SessionID", default)]
    session_id: Option<String>,
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
            r#"<Database><Get><Data><Device id="D_1"><Port id="P_1" Class="KVM" Type="VM" index="2" Status="1" StatAvailable="1" Connection="D_1"><Name>Rack &amp; 1</Name></Port></Device></Data></Get></Database>"#,
        )
        .unwrap();
        assert_eq!(ports[0].name.as_deref(), Some("Rack & 1"));
        assert_eq!(ports[0].connection.as_deref(), Some("D_1"));
    }
}
