use crate::{
    event::csc_test2,
    handshake::{csc_auth, csc_start_session},
    model::{SessionResponse, parse_ports},
    tr::{probe_ping as tr_probe_ping, request_video_grant},
};
use eyre::{Context, OptionExt};
use openssl::ssl::SslStream;
use quick_xml::de::from_str;
use raritan_common::{
    DEFAULT_RDM_PORT, EVENT_DRAIN_TIMEOUT, RDM_READ_TIMEOUT, TR_GRANT_TIMEOUT, display_xml,
    escape_xml, read_frame, tls_connector,
};
use std::net::TcpStream;
use tracing::{debug, info};

const SELECT_IP_REACH_PORTS: &str = "<Database><Get><Select>/System/Device[@Type='IP-Reach']/Port</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>";
const SELECT_SESSION_ID: &str = "<Session><GetSessionID/></Session>";

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

    fn tls_channel(host: &str, protocol: &str, session: Option<&str>) -> eyre::Result<(SslStream<TcpStream>, Vec<u8>)> {
        let mut plain = Self::tcp_connect(host)?;
        let info = csc_start_session(&mut plain, protocol, session)?;
        let tls = Self::tls_upgrade(host, plain)?;
        Ok((tls, info))
    }

    pub fn connect(host: impl Into<String>, user: &str, password: &str) -> eyre::Result<Self> {
        let host = host.into();
        info!(%host, "connecting to RDM control channel");
        let (mut tls, info) = Self::tls_channel(&host, "RDM", None)?;
        debug!(length = info.len(), "received CSC info");
        csc_auth(&mut tls, user, password)?;
        info!(%host, "RDM authentication succeeded");
        Ok(Self {
            stream: tls,
            host,
            session_id: None,
            session_key: None,
        })
    }

    /// Opens the CSC+TLS channel without authenticating (diagnostic aid).
    pub fn connect_unauthed(host: impl Into<String>) -> eyre::Result<Self> {
        let host = host.into();
        let (tls, _) = Self::tls_channel(&host, "RDM", None)?;
        Ok(Self {
            stream: tls,
            host,
            session_id: None,
            session_key: None,
        })
    }

    pub fn database_query(&mut self, request: &str) -> eyre::Result<String> {
        raritan_common::write_frame(&mut self.stream, request)?;
        let response = read_frame(&mut self.stream)?;
        Ok(String::from_utf8(response)?
            .trim_end_matches('\0')
            .to_owned())
    }

    /// Raw XML of the top-level IP-Reach port inventory (debugging aid).
    pub fn raw_inventory(&mut self) -> eyre::Result<String> {
        self.database_query(SELECT_IP_REACH_PORTS)
    }

    /// Raw XML of one device subtree (debugging aid).
    pub fn raw_device(&mut self, device_id: &str) -> eyre::Result<String> {
        self.database_query(&select_device(device_id))
    }

    pub fn enumerate_ports(&mut self) -> eyre::Result<Vec<crate::Port>> {
        info!("requesting RDM session credentials");
        let session: SessionResponse = from_str(&self.database_query(SELECT_SESSION_ID)?)?;
        let data = session.get_session_id;
        self.session_id = Some(
            data.session_id
                .or(data.session_id_element)
                .ok_or_eyre("response did not contain SessionID")?,
        );
        self.session_key = data.session_key.or(data.session_key_element);
        info!(
            session_id_present = self.session_id.is_some(),
            session_key_present = self.session_key.is_some(),
            "received RDM session credentials"
        );

        let inventory = self.database_query(SELECT_IP_REACH_PORTS)?;
        let mut ports = parse_ports(&inventory)?;
        let portal_id = ports
            .iter()
            .find(|port| port.class.as_deref() == Some("KVM"))
            .map(|port| port.id.clone());
        for port in &mut ports {
            port.portal_id = portal_id.clone();
        }
        // Fan out to each distinct connected device once (N ports on the
        // same D_… device share one query). Guard against self-references
        // and cycles by skipping already-seen ids.
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

        for device_id in connections {
            let mut child_ports = parse_ports(&self.database_query(&select_device(&device_id))?)?;
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

    /// Opens the RDMEvent referral session the Java client always holds
    /// while connecting video: fresh `:5000` connection, `StartSession`
    /// with `RDMEvent` + our session ID, TLS upgrade, then the RC4
    /// `CSC_Test2` dance keyed by the RDM session key. The stream is
    /// drained in the background (mirrors Java's always-on event loop).
    pub fn open_event_session(&self, session_id: &str, session_key: &str) -> eyre::Result<()> {
        info!(%session_id, "opening RDM event session");
        let mut socket = Self::tcp_connect(&self.host)?;
        csc_start_session(&mut socket, "RDMEvent", Some(session_id))?;
        let mut tls = Self::tls_upgrade(&self.host, socket)?;
        csc_test2(&mut tls, session_key)?;
        info!("RDM event session established");
        // NOTE: this spawns a detached drain thread that owns the event
        // socket for the lifetime of the video session (mirrors Java's
        // always-on event loop). Callers that retry `establish_video`
        // should be aware each attempt opens one more session.
        std::thread::spawn(move || {
            if let Err(error) = tls
                .get_ref()
                .set_read_timeout(Some(EVENT_DRAIN_TIMEOUT))
            {
                info!(error = %format!("{error:#}"), "RDM event drain: cannot set read timeout; exiting");
                return;
            }
            loop {
                match read_frame(&mut tls) {
                    Ok(frame) => info!(
                        length = frame.len(),
                        payload = %display_xml(&frame),
                        "RDM event",
                    ),
                    Err(error) => {
                        // Walk the eyre chain: SslStream errors may wrap the
                        // underlying io error instead of exposing it directly.
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
                            info!(error = %format!("{error:#}"), "RDM event session closed");
                            return;
                        }
                    }
                }
            }
        });
        Ok(())
    }

    /// Sends TR PINGs and waits briefly for a PONG, purely to check
    /// whether the server's TR layer processes our binary commands.
    /// Returns true if any response arrived.
    pub fn probe_ping(&mut self) -> bool {
        let answered = tr_probe_ping(&mut self.stream);
        let _ = self
            .stream
            .get_ref()
            .set_read_timeout(Some(RDM_READ_TIMEOUT));
        answered
    }

    /// Legacy TR video-stream grant (cmd 55). Utterly silent on current
    /// firmware — kept for debugging only; the RFB path skips it.
    pub fn connect_video_stream(
        &mut self,
        portal: &str,
        target: &str,
        force: bool,
    ) -> eyre::Result<u8> {
        request_video_grant(&mut self.stream, portal, target, force, TR_GRANT_TIMEOUT)
    }

    pub fn connect_video_stream_timeout(
        &mut self,
        portal: &str,
        target: &str,
        force: bool,
        timeout_secs: u64,
    ) -> eyre::Result<u8> {
        request_video_grant(
            &mut self.stream,
            portal,
            target,
            force,
            std::time::Duration::from_secs(timeout_secs),
        )
    }
}
