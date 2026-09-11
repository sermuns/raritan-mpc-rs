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

const RDM_READ_TIMEOUT: std::time::Duration = std::time::Duration::from_secs(5);

pub struct RdmClient {
    stream: SslStream<TcpStream>,
    host: String,
    user: String,
    password: String,
    session_id: Option<String>,
    session_key: Option<String>,
}

fn read_rdm_command(stream: &mut SslStream<TcpStream>) -> eyre::Result<Vec<u8>> {
    let mut header = [0u8; 2];
    stream.read_exact(&mut header)?;
    let length = u16::from_be_bytes(header) as usize;
    if !(4..=65535).contains(&length) {
        bail!("invalid RDM command length {length}");
    }
    let mut command = vec![0u8; length];
    command[0..2].copy_from_slice(&header);
    stream.read_exact(&mut command[2..])?;
    Ok(command)
}

impl RdmClient {
    pub fn connect(host: impl Into<String>, user: &str, password: &str) -> eyre::Result<Self> {
        let host = host.into();
        info!(%host, "connecting to RDM control channel");
        let mut stream = TcpStream::connect((&*host, 5000))
            .wrap_err_with(|| format!("connecting to {host}:5000"))?;
        stream.set_read_timeout(Some(RDM_READ_TIMEOUT))?;

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
            host,
            user: user.to_owned(),
            password: password.to_owned(),
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
        let portal_id = ports
            .iter()
            .find(|port| port.class.as_deref() == Some("KVM"))
            .map(|port| port.id.clone());
        for port in &mut ports {
            port.portal_id = portal_id.clone();
        }
        let connections: Vec<String> = ports
            .iter()
            .filter_map(|port| port.connection.clone())
            .collect();

        for device_id in connections {
            let response = self.database_query(&format!(
                "<Database><Get><Select>/System/Device[@id='{}']</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>",
                escape_xml(&device_id)
            ))?;
            let mut child_ports = parse_ports(&response)?;
            for port in &mut child_ports {
                port.portal_id = portal_id.clone();
            }
            ports.extend(child_ports);
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
        let packet_id = 1u8;
        command.extend_from_slice(&[55, packet_id]);
        command.extend_from_slice(&video_compression_parameters());
        command.extend_from_slice(xml.as_bytes());
        self.stream.write_all(&command)?;
        self.stream.flush()?;
        debug!(length = length, "sent connect-video-stream command");

        debug!("waiting for connect-video-stream response");
        fn video_compression_parameters() -> [u8; 40] {
            let mut parameters = [0; 40];
            parameters[0..4].copy_from_slice(&56u32.to_be_bytes());
            parameters[4..6].copy_from_slice(&10u16.to_be_bytes());
            parameters[24..28].copy_from_slice(&6u32.to_be_bytes());
            parameters
        }
        loop {
            let response =
                read_rdm_command(&mut self.stream).wrap_err("waiting for video-stream response")?;
            let response_command = response[2];
            let response_packet = response[3];
            debug!(
                length = response.len(),
                command = response_command,
                packet_id = response_packet,
                "received RDM command"
            );
            if response_command == 3 {
                let error = response
                    .get(4..8)
                    .and_then(|bytes| bytes.try_into().ok())
                    .map(u32::from_be_bytes);
                bail!("RDM server rejected command with error {error:?}");
            }
            if response_command == 37 && response_packet == packet_id {
                let device_id = response
                    .get(4)
                    .copied()
                    .ok_or_eyre("video-stream response did not contain device ID")?;
                info!(device_id, "video stream granted");
                return Ok(device_id);
            }
        }

        fn obsolete_command_stream(
            host: &str,
            session_id: &str,
            _session_key: &str,
            user: &str,
            password: &str,
        ) -> eyre::Result<SslStream<TcpStream>> {
            info!(%host, "opening referral RDM command socket");
            let mut socket = TcpStream::connect((host, 5000))?;
            socket.set_read_timeout(Some(RDM_READ_TIMEOUT))?;
            let greeting = read_frame(&mut socket).wrap_err("reading command CSC greeting")?;
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
            write_frame(
                &mut tls,
                &format!(
                    r#"<CSC_Auth UserName="{}" Password="{}"/>"#,
                    escape_xml(user),
                    escape_xml(password)
                ),
            )?;
            if !read_frame(&mut tls)?.starts_with(b"<CSC_Pass") {
                bail!("RDM command authentication failed");
            }
            info!("RDM command authentication succeeded");
            Ok(tls)
        }
    }
}

fn write_handshake_ack(
    stream: &mut SslStream<TcpStream>,
    handshake: &mut [u8; 34],
    signature: i32,
) -> eyre::Result<()> {
    handshake[4..8].copy_from_slice(&signature.to_be_bytes());
    let u32_at = |offset| {
        u64::from(u32::from_be_bytes(
            handshake[offset..offset + 4].try_into().unwrap(),
        ))
    };
    let i32_at = |offset| {
        i64::from(i32::from_be_bytes(
            handshake[offset..offset + 4].try_into().unwrap(),
        ))
    };
    let checksum = i32_at(4)
        + u32_at(8) as i64
        + u64::from(u16::from_be_bytes([handshake[32], handshake[33]])) as i64
        + i32_at(12)
        + i32_at(16)
        + i32_at(20)
        + i32_at(24);
    handshake[28..32].copy_from_slice(&(checksum as i32).to_be_bytes());
    stream.write_all(handshake)?;
    stream.flush()?;
    Ok(())
}

fn binary_rdm_stream(host: &str, user: &str, password: &str) -> eyre::Result<SslStream<TcpStream>> {
    info!(%host, "opening normal RDM video socket");
    let mut socket =
        TcpStream::connect((host, 5000)).wrap_err_with(|| format!("connecting to {host}:5000"))?;
    socket.set_read_timeout(Some(RDM_READ_TIMEOUT))?;

    let greeting = read_frame(&mut socket).wrap_err("reading video CSC greeting")?;
    if !greeting.starts_with(b"<CSC") {
        bail!("unexpected video CSC greeting: {}", display_xml(&greeting));
    }
    write_frame(&mut socket, "<CSC_Ack/>")?;
    let info = read_frame(&mut socket).wrap_err("reading video CSC info")?;
    if !info.starts_with(b"<CSC_Info") {
        bail!("unexpected video CSC info: {}", display_xml(&info));
    }
    write_frame(&mut socket, r#"<CSC_Start_Session ProtocolID="RDM"/>"#)?;
    let mut tls = tls_connector()?.connect(host, socket)?;
    write_frame(
        &mut tls,
        &format!(
            r#"<CSC_Auth UserName="{}" Password="{}"/>"#,
            escape_xml(user),
            escape_xml(password)
        ),
    )?;
    let auth = read_frame(&mut tls).wrap_err("reading video RDM authentication")?;
    if auth != b"<CSC_Pass/>" {
        bail!("video RDM authentication failed: {}", display_xml(&auth));
    }
    debug!("video RDM TLS established");
    Ok(tls)
}

fn tls_connector() -> eyre::Result<SslConnector> {
    let mut builder = SslConnector::builder(SslMethod::tls())?;
    builder.set_cipher_list("DEFAULT:@SECLEVEL=0")?;
    builder.set_min_proto_version(Some(SslVersion::TLS1))?;
    builder.set_max_proto_version(Some(SslVersion::TLS1))?;
    builder.set_verify(SslVerifyMode::NONE);
    Ok(builder.build())
}

fn command_stream(
    host: &str,
    session_id: &str,
    session_key: &str,
    user: &str,
    password: &str,
) -> eyre::Result<SslStream<TcpStream>> {
    return referral_command_stream(host, session_id, session_key);
    info!(%host, "opening referral RDM command socket");
    let mut socket = TcpStream::connect((host, 5000))?;
    socket.set_read_timeout(Some(RDM_READ_TIMEOUT))?;
    let greeting = read_frame(&mut socket)?;
    if !greeting.starts_with(b"<CSC") {
        bail!("unexpected referral CSC greeting");
    }

    fn referral_command_stream(
        host: &str,
        session_id: &str,
        session_key: &str,
    ) -> eyre::Result<SslStream<TcpStream>> {
        info!(%host, "opening referral RDM command socket");
        let mut socket = TcpStream::connect((host, 5000))?;
        socket.set_read_timeout(Some(RDM_READ_TIMEOUT))?;
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
        debug!("referral TLS established; waiting for CSC challenge");
        let challenge =
            String::from_utf8(read_frame(&mut tls).wrap_err("reading referral CSC challenge")?)?;
        let clear_text = xml_attribute(&challenge, "ClearText")
            .ok_or_eyre("referral authentication challenge lacks ClearText")?;
        let key = decode_base64(session_key)?;
        let encrypted = rc4(&key, &decode_base64(&clear_text)?)?;
        let test_string = b"!@%!@#%$%#$%";
        let mut clear = Vec::with_capacity(test_string.len());
        let mut state = current_time_millis();
        for byte in test_string {
            clear.push(*byte ^ state as u8);
            state = (state >> 3) ^ current_time_millis();
        }
        debug!("received referral CSC challenge; sending CSC test");
        write_frame(
            &mut tls,
            &format!(
                r#"<CSC_Test2 Encrypted="{}" ClearText="{}"/>"#,
                STANDARD.encode(encrypted),
                STANDARD.encode(&clear)
            ),
        )?;
        let response = String::from_utf8(
            read_frame(&mut tls).wrap_err("reading referral CSC test response")?,
        )?;
        debug!(
            length = response.len(),
            "received referral CSC test response"
        );
        let encrypted = xml_attribute(&response, "Encrypted")
            .ok_or_eyre("referral authentication response lacks Encrypted")?;
        if rc4(&key, &decode_base64(&encrypted)?)? != clear {
            bail!("referral RDM authentication failed");
        }
        info!("referral RDM session-key authentication succeeded");
        Ok(tls)
    }
    write_frame(&mut socket, "<CSC_Ack/>").wrap_err("writing command CSC ack")?;
    let _info = read_frame(&mut socket).wrap_err("reading command CSC info")?;
    write_frame(
        &mut socket,
        &format!(
            r#"<CSC_Start_Session ProtocolID="RDM" SessionID="{}"/>"#,
            escape_xml(session_id)
        ),
    )
    .wrap_err("writing command CSC session start")?;
    let mut tls = tls_connector()?.connect(host, socket)?;
    write_frame(
        &mut tls,
        &format!(
            r#"<CSC_Auth UserName="{}" Password="{}"/>"#,
            escape_xml(user),
            escape_xml(password)
        ),
    )
    .wrap_err("writing command CSC authentication")?;
    if !read_frame(&mut tls)
        .wrap_err("reading command CSC authentication response")?
        .starts_with(b"<CSC_Pass")
    {
        bail!("RDM command authentication failed");
    }
    debug!("RDM command authentication succeeded");
    let challenge = String::from_utf8(
        read_frame(&mut tls).wrap_err("reading command CSC referral challenge")?,
    )?;
    let clear_text = xml_attribute(&challenge, "ClearText")
        .ok_or_eyre("referral authentication challenge lacks ClearText")?;
    let key = decode_base64(session_key)?;
    let encrypted = rc4(&key, &decode_base64(&clear_text)?)?;
    let test_string = b"!@%!@#%$%#$%";
    let mut clear = Vec::with_capacity(test_string.len());
    let mut state = current_time_millis();
    for byte in test_string {
        clear.push(*byte ^ state as u8);
        state = (state >> 3) ^ current_time_millis();
    }
    write_frame(
        &mut tls,
        &format!(
            r#"<CSC_Test2 Encrypted="{}" ClearText="{}"/>"#,
            STANDARD.encode(encrypted),
            STANDARD.encode(&clear)
        ),
    )
    .wrap_err("writing command CSC referral test")?;
    let response =
        String::from_utf8(read_frame(&mut tls).wrap_err("reading command CSC referral response")?)?;
    let encrypted = xml_attribute(&response, "Encrypted")
        .ok_or_eyre("referral authentication response lacks Encrypted")?;
    if rc4(&key, &decode_base64(&encrypted)?)? != clear {
        bail!("referral RDM authentication failed");
    }
    info!("referral RDM command authentication succeeded");
    Ok(tls)
}

fn referral_command_stream(
    host: &str,
    session_id: &str,
    session_key: &str,
    port: u16,
) -> eyre::Result<SslStream<TcpStream>> {
    info!(%host, port, "opening referral RDM socket");
    let mut socket = TcpStream::connect((host, port))?;
    socket.set_read_timeout(Some(RDM_READ_TIMEOUT))?;
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
    debug!(port, "referral TLS established; waiting for CSC challenge");
    let challenge = String::from_utf8(read_frame(&mut tls)?)?;
    let clear_text = xml_attribute(&challenge, "ClearText")
        .ok_or_eyre("referral authentication challenge lacks ClearText")?;
    let key = decode_base64(session_key)?;
    let encrypted = rc4(&key, &decode_base64(&clear_text)?)?;
    let test_string = b"!@%!@#%$%#$%";
    let mut clear = Vec::with_capacity(test_string.len());
    let mut state = current_time_millis();
    for byte in test_string {
        clear.push(*byte ^ state as u8);
        state = (state >> 3) ^ current_time_millis();
    }
    write_frame(
        &mut tls,
        &format!(
            r#"<CSC_Test2 Encrypted="{}" ClearText="{}"/>"#,
            STANDARD.encode(encrypted),
            STANDARD.encode(&clear)
        ),
    )?;
    let response = String::from_utf8(read_frame(&mut tls)?)?;
    let encrypted = xml_attribute(&response, "Encrypted")
        .ok_or_eyre("referral authentication response lacks Encrypted")?;
    if rc4(&key, &decode_base64(&encrypted)?)? != clear {
        bail!("referral RDM authentication failed");
    }
    info!(port, "referral RDM session-key authentication succeeded");
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

fn current_time_millis() -> i32 {
    std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .expect("system clock is before Unix epoch")
        .as_millis() as i32
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
