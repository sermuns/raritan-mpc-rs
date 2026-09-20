use crate::{
    model::{SessionResponse, SwitchInfo, parse_ports, parse_switch_info},
    tr::{probe_ping as tr_probe_ping, request_video_grant},
};
use eyre::{Context, OptionExt, bail};
use openssl::ssl::SslStream;
use quick_xml::de::from_str;
use raritan_common::{
    DEFAULT_RDM_PORT, EVENT_DRAIN_TIMEOUT, RDM_READ_TIMEOUT, TR_GRANT_TIMEOUT,
    decode_base64, display_xml, encode_base64, escape_xml, event_probe, rc4, read_frame,
    tls_connector, write_frame, xml_attribute,
};
use std::{
    io::{Read, Write},
    net::TcpStream,
};
use tracing::{debug, info, warn};

const SELECT_IP_REACH_PORTS: &str = "<Database><Get><Select>/System/Device[@Type='IP-Reach']/Port</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>";
const SELECT_SESSION_ID: &str = "<Session><GetSessionID/></Session>";
/// Java `TRConnection.GET_DEVICE_ID`: the cheap query its event loop
/// sends to keep the control session alive.
const SELECT_DEVICE_ID: &str = "<Database><Get><Select>/System/Device</Select><Nodes> Device </Nodes><SubNodes> Name SerialNo @id</SubNodes></Get></Database>";

fn select_device(device_id: &str) -> String {
    format!(
        "<Database><Get><Select>/System/Device[@id='{}']</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>",
        escape_xml(device_id)
    )
}

pub struct RdmClient {
    stream: SslStream<TcpStream>,
    host: String,
    session_id: Option<String>,
    session_key: Option<String>,
    switch_info: SwitchInfo,
}

impl RdmClient {
    fn tcp_connect(host: &str) -> eyre::Result<TcpStream> {
        let stream = TcpStream::connect((host, DEFAULT_RDM_PORT))
            .wrap_err_with(|| format!("connecting to {host}:{DEFAULT_RDM_PORT}"))?;
        // Small request/response frames: skip Nagle's ACK-wait delay.
        stream.set_nodelay(true)?;
        stream.set_read_timeout(Some(RDM_READ_TIMEOUT))?;
        Ok(stream)
    }

    fn tls_upgrade(host: &str, stream: TcpStream) -> eyre::Result<SslStream<TcpStream>> {
        tls_connector()?.connect(host, stream).map_err(Into::into)
    }

    fn tls_channel(
        host: &str,
        protocol: &str,
        session: Option<&str>,
    ) -> eyre::Result<(SslStream<TcpStream>, Vec<u8>)> {
        let mut plain = Self::tcp_connect(host)?;
        let info = csc_start_session(&mut plain, protocol, session)?;
        let tls = Self::tls_upgrade(host, plain)?;
        Ok((tls, info))
    }

    pub fn connect(host: impl Into<String>, user: &str, password: &str) -> eyre::Result<Self> {
        let host = host.into();
        info!(%host, "connecting to RDM control channel");
        let (mut tls, info) = Self::tls_channel(&host, "RDM", None)?;
        let switch_info = parse_switch_info(&String::from_utf8_lossy(&info));
        debug!(?switch_info, "received CSC info");
        csc_auth(&mut tls, user, password)?;
        info!(%host, "RDM authentication succeeded");
        Ok(Self {
            stream: tls,
            host,
            session_id: None,
            session_key: None,
            switch_info,
        })
    }

    /// Opens the CSC+TLS channel without authenticating (diagnostic aid).
    pub fn connect_unauthed(host: impl Into<String>) -> eyre::Result<Self> {
        let host = host.into();
        let (tls, info) = Self::tls_channel(&host, "RDM", None)?;
        let switch_info = parse_switch_info(&String::from_utf8_lossy(&info));
        Ok(Self {
            stream: tls,
            host,
            session_id: None,
            session_key: None,
            switch_info,
        })
    }

    pub fn host(&self) -> &str {
        &self.host
    }

    pub fn switch_info(&self) -> &SwitchInfo {
        &self.switch_info
    }

    pub fn database_query(&mut self, request: &str) -> eyre::Result<String> {
        raritan_common::write_frame(&mut self.stream, request)?;
        let response = read_frame(&mut self.stream)?;
        Ok(String::from_utf8(response)?
            .trim_end_matches('\0')
            .to_owned())
    }

    /// Session keepalive: the switch reaps idle sessions (event socket
    /// first, then RFB), so the Java client polls `GET_DEVICE_ID` from
    /// its event loop and TR-pings from `TRKeepAliveThread`. The TR layer
    /// is silent on current firmware, so only the query is sent.
    pub fn keepalive(&mut self) -> eyre::Result<()> {
        let response = self.database_query(SELECT_DEVICE_ID)?;
        debug!(length = response.len(), "RDM keepalive answered");
        Ok(())
    }

    /// Raw XML of the top-level IP-Reach port inventory (debugging aid).
    pub fn raw_inventory(&mut self) -> eyre::Result<String> {
        self.database_query(SELECT_IP_REACH_PORTS)
    }

    /// Fetches just the session credentials (one query): the video path
    /// needs no port inventory.
    pub fn fetch_session_credentials(&mut self) -> eyre::Result<()> {
        // Once per connection, like the Java client: a live video stream
        // authenticated with these, so they must not be rotated under it.
        if self.session_id.is_some() {
            debug!("reusing RDM session credentials");
            return Ok(());
        }
        debug!("requesting RDM session credentials");
        let session: SessionResponse = from_str(&self.database_query(SELECT_SESSION_ID)?)?;
        let data = session.get_session_id;
        self.session_id = Some(
            data.session_id
                .or(data.session_id_element)
                .ok_or_eyre("response did not contain SessionID")?,
        );
        self.session_key = data.session_key.or(data.session_key_element);
        debug!(
            session_id_present = self.session_id.is_some(),
            session_key_present = self.session_key.is_some(),
            "received RDM session credentials"
        );
        Ok(())
    }

    pub fn enumerate_ports(&mut self) -> eyre::Result<Vec<crate::Port>> {
        self.fetch_session_credentials()?;

        let inventory = self.database_query(SELECT_IP_REACH_PORTS)?;
        let ports = parse_ports(&inventory)?;
        // Fan out once per distinct connected device (ports on one device
        // share a query); skip seen ids to guard against cycles.
        let mut seen_devices = std::collections::HashSet::new();
        let mut connections: Vec<String> = Vec::new();
        for port in &ports {
            if let Some(connection) = port.connection.clone()
                && connection != port.device_id.as_deref().unwrap_or("")
                && seen_devices.insert(connection.clone())
            {
                connections.push(connection);
            }
        }

        let mut ports = ports;
        for device_id in connections {
            ports.extend(parse_ports(
                &self.database_query(&select_device(&device_id))?,
            )?);
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

    /// Opens the `RDMEvent` referral session the Java client holds while
    /// connecting video: `:5000` → `StartSession(RDMEvent)` → TLS → RC4
    /// `CSC_Test2`. Drained in the background like Java's event loop.
    pub fn open_event_session(&self, session_id: &str, session_key: &str) -> eyre::Result<()> {
        Self::open_event_session_on(&self.host, session_id, session_key)
    }

    /// `open_event_session` off the critical path: its TLS handshake costs
    /// ~2 s on the switch and video does not depend on it, so the connect
    /// runs on its own thread and failures only warn.
    pub fn spawn_event_session(&self, session_id: &str, session_key: &str) {
        let host = self.host.clone();
        let session_id = session_id.to_owned();
        let session_key = session_key.to_owned();
        std::thread::spawn(move || {
            if let Err(error) = Self::open_event_session_on(&host, &session_id, &session_key) {
                warn!(error = %format!("{error:#}"), "RDM event session failed; continuing without it");
            }
        });
    }

    fn open_event_session_on(host: &str, session_id: &str, session_key: &str) -> eyre::Result<()> {
        debug!(%session_id, "opening RDM event session");
        let mut socket = Self::tcp_connect(host)?;
        csc_start_session(&mut socket, "RDMEvent", Some(session_id))?;
        let mut tls = Self::tls_upgrade(host, socket)?;
        csc_test2(&mut tls, session_key)?;
        debug!("RDM event session established");
        // NOTE: detached drain thread owns the event socket, so each
        // `establish_video` attempt opens one more session.
        std::thread::spawn(move || {
            if let Err(error) = tls.get_ref().set_read_timeout(Some(EVENT_DRAIN_TIMEOUT)) {
                debug!(error = %format!("{error:#}"), "RDM event drain: cannot set read timeout; exiting");
                return;
            }
            loop {
                match read_frame(&mut tls) {
                    Ok(frame) => debug!(
                        length = frame.len(),
                        payload = %display_xml(&frame),
                        "RDM event",
                    ),
                    Err(error) => {
                        // SslStream may wrap the io error instead of exposing it directly.
                        let idle = error
                            .chain()
                            .find_map(|cause| cause.downcast_ref::<std::io::Error>())
                            .is_some_and(|io| {
                                matches!(
                                    io.kind(),
                                    std::io::ErrorKind::WouldBlock | std::io::ErrorKind::TimedOut
                                )
                            });
                        if !idle {
                            debug!(error = %format!("{error:#}"), "RDM event session closed");
                            return;
                        }
                    }
                }
            }
        });
        Ok(())
    }

    /// Sends TR PINGs to check whether the server's TR layer answers.
    pub fn probe_ping(&mut self) -> bool {
        let answered = tr_probe_ping(&mut self.stream);
        let _ = self
            .stream
            .get_ref()
            .set_read_timeout(Some(RDM_READ_TIMEOUT));
        answered
    }

    /// Legacy TR video-stream grant (cmd 55), silent on current firmware;
    /// kept for debugging only (the RFB path skips it).
    pub fn connect_video_stream(
        &mut self,
        portal: &str,
        target: &str,
        force: bool,
    ) -> eyre::Result<u8> {
        request_video_grant(&mut self.stream, portal, target, force, TR_GRANT_TIMEOUT)
    }
}

// --- CSC handshake (previously handshake.rs) ---

fn csc_start_session<S: Read + Write>(
    stream: &mut S,
    protocol: &str,
    session_id: Option<&str>,
) -> eyre::Result<Vec<u8>> {
    let greeting = read_frame(stream).wrap_err("reading CSC greeting")?;
    if !greeting.starts_with(b"<CSC") {
        bail!("unexpected CSC greeting: {}", display_xml(&greeting));
    }
    write_frame(stream, "<CSC_Ack/>").wrap_err("writing CSC ack")?;
    let info = read_frame(stream).wrap_err("reading CSC info")?;
    if !info.starts_with(b"<CSC_Info") {
        bail!("unexpected CSC info: {}", display_xml(&info));
    }
    let start = match session_id {
        Some(id) => format!(
            r#"<CSC_Start_Session ProtocolID="{protocol}" SessionID="{}"/>"#,
            escape_xml(id)
        ),
        None => format!(r#"<CSC_Start_Session ProtocolID="{protocol}"/>"#),
    };
    write_frame(stream, &start).wrap_err("writing CSC start-session")?;
    debug!(protocol, "CSC start-session sent");
    Ok(info)
}

fn csc_auth<S: Read + Write>(tls: &mut S, user: &str, password: &str) -> eyre::Result<()> {
    write_frame(
        tls,
        &format!(
            r#"<CSC_Auth UserName="{}" Password="{}"/>"#,
            escape_xml(user),
            escape_xml(password)
        ),
    )
    .wrap_err("writing CSC auth")?;
    let auth = read_frame(tls).wrap_err("reading CSC auth response")?;
    if !auth.starts_with(b"<CSC_Pass") {
        bail!("authentication failed: {}", display_xml(&auth));
    }
    Ok(())
}

// --- CSC_Test2 event handshake (previously event.rs) ---

fn csc_test2<S: Read + Write>(tls: &mut S, session_key: &str) -> eyre::Result<()> {
    let challenge = String::from_utf8(read_frame(tls).wrap_err("reading event CSC challenge")?)?;
    let clear_text =
        xml_attribute(&challenge, "ClearText").ok_or_eyre("event challenge lacks ClearText")?;
    let key = decode_base64(session_key)?;
    let encrypted = rc4(&key, &decode_base64(&clear_text)?)?;
    let clear = event_probe();
    write_frame(
        tls,
        &format!(
            r#"<CSC_Test2 Encrypted="{}" ClearText="{}"/>"#,
            encode_base64(&encrypted),
            encode_base64(&clear)
        ),
    )
    .wrap_err("writing event CSC test")?;
    let response = String::from_utf8(read_frame(tls).wrap_err("reading event CSC response")?)?;
    debug!(length = response.len(), "received event CSC test response");
    let echoed =
        xml_attribute(&response, "Encrypted").ok_or_eyre("event response lacks Encrypted")?;
    if rc4(&key, &decode_base64(&echoed)?)? != clear {
        bail!("RDM event session authentication failed");
    }
    Ok(())
}
