use crate::{
    model::{SessionResponse, parse_ports},
    protocol::{display_xml, escape_xml, read_frame, write_frame},
};
use base64::{Engine as _, engine::general_purpose::STANDARD};
use eyre::{Context, OptionExt, bail};
use openssl::ssl::{SslConnector, SslMethod, SslStream, SslVerifyMode, SslVersion};
use quick_xml::de::from_str;
use std::{
    io::{Read, Write},
    net::TcpStream,
};
use tracing::{debug, info};

pub struct RdmClient {
    stream: SslStream<TcpStream>,
    session_id: Option<String>,
    session_key: Option<String>,
}

impl RdmClient {
    pub fn connect(host: impl Into<String>, user: &str, password: &str) -> eyre::Result<Self> {
        let host = host.into();
        info!(%host, "connecting to RDM control channel");
        let mut stream = TcpStream::connect((&*host, 5000))
            .wrap_err_with(|| format!("connecting to {host}:5000"))?;
        stream.set_read_timeout(Some(std::time::Duration::from_secs(15)))?;

        let greeting = read_frame(&mut stream)?;
        debug!(length = greeting.len(), "received CSC greeting");
        if !greeting.starts_with(b"<CSC") {
            bail!("unexpected CSC greeting: {}", display_xml(&greeting));
        }
        write_frame(&mut stream, "<CSC_Ack/>")?;

        let info = read_frame(&mut stream)?;
        debug!(length = info.len(), "received CSC info");
        if !info.starts_with(b"<CSC_Info") {
            bail!("unexpected CSC info: {}", display_xml(&info));
        }
        write_frame(&mut stream, r#"<CSC_Start_Session ProtocolID="RDM"/>"#)?;

        let mut tls = tls_connector()?.connect(&host, stream)?;
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

        info!(%host, "RDM authentication succeeded");
        Ok(Self {
            stream: tls,
            session_id: None,
            session_key: None,
        })
    }

    pub fn database_query(&mut self, request: &str) -> eyre::Result<String> {
        write_frame(&mut self.stream, request)?;
        let response = read_frame(&mut self.stream)?;
        Ok(String::from_utf8(response)?
            .trim_end_matches('\0')
            .to_owned())
    }

    pub fn enumerate_ports(&mut self) -> eyre::Result<Vec<crate::Port>> {
        info!("requesting RDM session credentials");
        let session: SessionResponse =
            from_str(&self.database_query("<Session><GetSessionID/></Session>")?)?;
        self.session_id = Some(
            session
                .get_session_id
                .session_id
                .or(session.get_session_id.session_id_element)
                .ok_or_eyre("response did not contain SessionID")?,
        );
        self.session_key = session
            .get_session_id
            .session_key
            .or(session.get_session_id.session_key_element);
        info!(
            session_id_present = self.session_id.is_some(),
            session_key_present = self.session_key.is_some(),
            "received RDM session credentials"
        );

        let inventory = self.database_query(
            "<Database><Get><Select>/System/Device[@Type='IP-Reach']/Port</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>",
        )?;
        let mut ports = parse_ports(&inventory)?;
        let connections: Vec<String> = ports
            .iter()
            .filter_map(|port| port.connection.clone())
            .collect();

        for device_id in connections {
            let response = self.database_query(&format!(
                "<Database><Get><Select>/System/Device[@id='{}']</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>",
                escape_xml(&device_id)
            ))?;
            ports.extend(parse_ports(&response)?);
        }

        let ports: Vec<_> = ports
            .into_iter()
            .filter(|port| port.class.as_deref() == Some("KVM"))
            .collect();
        for port in &ports {
            debug!(
                id = %port.id,
                device_id = ?port.device_id,
                connection = ?port.connection,
                "parsed KVM port target metadata"
            );
        }
        info!(count = ports.len(), "enumerated KVM ports");
        Ok(ports)
    }

    pub fn session_credentials(&self) -> eyre::Result<(&str, &str)> {
        Ok((
            self.session_id
                .as_deref()
                .ok_or_eyre("session ID unavailable")?,
            self.session_key
                .as_deref()
                .ok_or_eyre("session key unavailable")?,
        ))
    }

    pub fn connect_video_stream(
        &mut self,
        portal: &str,
        target: &str,
        force: bool,
    ) -> eyre::Result<u8> {
        info!(%portal, %target, force, "requesting video stream");
        let xml = if force {
            format!(
                r#"<Connect ForceConnection="1"><Portal>{}</Portal><Target>{}</Target></Connect>"#,
                escape_xml(portal),
                escape_xml(target)
            )
        } else {
            format!(
                "<Connect><Portal>{}</Portal><Target>{}</Target></Connect>",
                escape_xml(portal),
                escape_xml(target)
            )
        };
        let length = 44usize
            .checked_add(xml.len())
            .ok_or_eyre("video command length overflow")?;
        let length = u16::try_from(length)?;
        let mut command = Vec::with_capacity(length as usize);
        command.extend_from_slice(&length.to_be_bytes());
        command.extend_from_slice(&[55, 1]);
        command.extend_from_slice(&video_compression_parameters());
        command.extend_from_slice(xml.as_bytes());
        let (session_id, session_key) = self.session_credentials()?;
        let mut stream = referral_stream(
            self.stream.get_ref().peer_addr()?.ip().to_string().as_str(),
            session_id,
            session_key,
        )?;
        stream.write_all(&command)?;
        stream.flush()?;
        debug!(length = length, "sent connect-video-stream command");

        let mut header = [0; 4];
        debug!("waiting for connect-video-stream response");
        stream.read_exact(&mut header)?;
        let response_length = u16::from_be_bytes([header[0], header[1]]) as usize;
        if response_length < 5 || response_length > 4096 {
            bail!("invalid video-stream response length {response_length}");
        }

        fn video_compression_parameters() -> [u8; 40] {
            let mut parameters = [0; 40];
            parameters[0..4].copy_from_slice(&4u32.to_be_bytes());
            parameters[8..12].copy_from_slice(&2u32.to_be_bytes());
            parameters[32..36].copy_from_slice(&6u32.to_be_bytes());
            parameters
        }
        let mut response = vec![0; response_length - 4];
        stream.read_exact(&mut response)?;
        debug!(
            length = response_length,
            command = response[0],
            "received video-stream response"
        );
        if response[0] != 37 {
            bail!("unexpected video-stream response command {}", response[0]);
        }

        fn referral_stream(
            host: &str,
            session_id: &str,
            session_key: &str,
        ) -> eyre::Result<SslStream<TcpStream>> {
            info!(%host, "opening referral RDM socket");
            let mut socket = TcpStream::connect((host, 5000))?;
            socket.set_read_timeout(Some(std::time::Duration::from_secs(15)))?;
            let greeting = read_frame(&mut socket)?;
            if !greeting.starts_with(b"<CSC") {
                bail!("unexpected referral CSC greeting");
            }
            write_frame(&mut socket, "<CSC_Ack/>")?;
            let _info = read_frame(&mut socket)?;
            write_frame(
                &mut socket,
                &format!(
                    r#"<CSC_Start_Session ProtocolID="RDM" SessionID="{}"/>"#,
                    escape_xml(session_id)
                ),
            )?;
            let mut tls = tls_connector()?.connect(host, socket)?;
            debug!("referral RDM TLS established");

            let challenge = String::from_utf8(read_frame(&mut tls)?)?;
            let clear_text = xml_attribute(&challenge, "ClearText")
                .ok_or_eyre("referral authentication challenge lacks ClearText")?;
            let key = decode_base64(session_key)?;
            let encrypted = rc4(&key, &decode_base64(&clear_text)?)?;
            let mut clear = Vec::with_capacity(10);
            let mut state = std::time::SystemTime::now()
                .duration_since(std::time::UNIX_EPOCH)?
                .as_millis() as u32;
            for byte in b"1234567890" {
                clear.push(*byte ^ state as u8);
                state = (state >> 3) ^ state.rotate_left(7) ^ 0x5a5a_5a5a;
            }
            write_frame(
                &mut tls,
                &format!(
                    r#"<CSC_Test2 Encrypted="{}" ClearText="{}"/>"#,
                    STANDARD.encode(encrypted),
                    STANDARD.encode(clear)
                ),
            )?;
            let response = String::from_utf8(read_frame(&mut tls)?)?;
            let encrypted = xml_attribute(&response, "Encrypted")
                .ok_or_eyre("referral authentication response lacks Encrypted")?;
            if rc4(&key, &decode_base64(&encrypted)?)? != b"1234567890" {
                bail!("referral RDM authentication failed");
            }
            info!("referral RDM authentication succeeded");
            Ok(tls)
        }

        fn xml_attribute(xml: &str, name: &str) -> Option<String> {
            let marker = format!("{name}=\"");
            let start = xml.find(&marker)? + marker.len();
            let end = xml[start..].find('"')? + start;
            Some(xml[start..end].to_owned())
        }

        fn decode_base64(value: &str) -> eyre::Result<Vec<u8>> {
            STANDARD
                .decode(
                    value
                        .bytes()
                        .filter(|byte| !byte.is_ascii_whitespace())
                        .collect::<Vec<_>>(),
                )
                .wrap_err("invalid base64 in referral authentication exchange")
        }

        fn rc4(key: &[u8], input: &[u8]) -> eyre::Result<Vec<u8>> {
            if key.is_empty() {
                bail!("empty referral session key");
            }
            let mut state = [0u8; 256];
            for (index, byte) in state.iter_mut().enumerate() {
                *byte = index as u8;
            }
            let mut j = 0usize;
            for i in 0..256 {
                j = (j + usize::from(state[i]) + usize::from(key[i % key.len()])) & 255;
                state.swap(i, j);
            }
            let mut i = 0usize;
            j = 0;
            let mut output = Vec::with_capacity(input.len());
            for byte in input {
                i = (i + 1) & 255;
                j = (j + usize::from(state[i])) & 255;
                state.swap(i, j);
                output.push(*byte ^ state[(usize::from(state[i]) + usize::from(state[j])) & 255]);
            }
            Ok(output)
        }
        let device_id = response
            .get(4)
            .copied()
            .ok_or_eyre("video-stream response did not contain device ID")?;
        info!(device_id, "video stream granted");
        Ok(device_id)
    }
}

fn tls_connector() -> eyre::Result<SslConnector> {
    let mut builder = SslConnector::builder(SslMethod::tls())?;
    builder.set_cipher_list("DEFAULT:@SECLEVEL=0")?;
    builder.set_min_proto_version(Some(SslVersion::TLS1))?;
    builder.set_max_proto_version(Some(SslVersion::TLS1))?;
    builder.set_verify(SslVerifyMode::NONE);
    Ok(builder.build())
}
