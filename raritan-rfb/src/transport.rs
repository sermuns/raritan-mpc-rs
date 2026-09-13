//! `RfbStream` transport: TCP connect, plaintext vs legacy-TLS setup,
//! and framebuffer update requests.

use crate::stream::RfbStream;
use eyre::{Result, WrapErr, bail};
use openssl::ssl::SslStream;
use raritan_common::{
    DEFAULT_RFB_PORT, SessionCreds, escape_xml, read_frame, tls_connector, write_frame,
};
use std::{
    collections::VecDeque,
    io::{Read, Write},
    net::TcpStream,
};
use tracing::{debug, info};

use crate::proto::FB_UPDATE_REQUEST;

impl RfbStream<TcpStream> {
    pub fn connect(host: &str) -> Result<Self> {
        let stream = TcpStream::connect((host, DEFAULT_RFB_PORT))
            .wrap_err_with(|| format!("connecting to {host}:{DEFAULT_RFB_PORT}"))?;
        // Latency-sensitive channel (4-byte key events, 10-byte update
        // requests): disable Nagle so small writes go out immediately
        // instead of waiting up to ~200 ms for delayed ACKs.
        stream.set_nodelay(true)?;
        Ok(Self::new(stream))
    }

    /// Sets the socket read timeout. The GUI worker uses a short timeout
    /// so it can interleave outbound key events with the blocking message
    /// pump; timeouts surface as `TimedOut`/`WouldBlock` io errors.
    pub fn set_read_timeout(&self, timeout: Option<std::time::Duration>) -> Result<()> {
        self.stream.set_read_timeout(timeout)?;
        Ok(())
    }

    /// Current socket read timeout (used to save/restore around captures).
    pub fn inner_read_timeout(&self) -> Result<Option<std::time::Duration>> {
        Ok(self.stream.read_timeout()?)
    }

    /// Plaintext video channel on 443, as used by the Java client
    /// (`RemoteConsoleParameters.ssl == false`).
    pub fn connect_raritan(
        host: &str,
        session_id: &str,
        session_key: &str,
        port: &str,
    ) -> Result<Self> {
        let mut stream = Self::connect(host)?;
        stream.handshake(&SessionCreds::new(session_id, session_key), port)?;
        Ok(stream)
    }

    pub fn connect_raritan_tls(
        host: &str,
        session_id: &str,
        session_key: &str,
        port: &str,
    ) -> Result<RfbStream<SslStream<TcpStream>>> {
        let stream = Self::connect_tls_channel(host, session_id)?;
        let mut stream = RfbStream::new(stream);
        stream.handshake(&SessionCreds::new(session_id, session_key), port)?;
        Ok(stream)
    }

    /// Legacy CSC + TLS + RC4 channel setup, kept for setups that require
    /// SSL. The default Java video path is plaintext (see `connect_raritan`).
    fn connect_tls_channel(host: &str, session_id: &str) -> Result<SslStream<TcpStream>> {
        info!(%host, "connecting to RFP CSC channel");
        let mut socket = TcpStream::connect((host, DEFAULT_RFB_PORT))
            .wrap_err_with(|| format!("connecting to {host}:{DEFAULT_RFB_PORT}"))?;
        socket.set_nodelay(true)?;
        let greeting = read_frame(&mut socket)?;
        debug!(length = greeting.len(), "received RFP CSC greeting");
        if !greeting.starts_with(b"<CSC") {
            bail!("unexpected RFP greeting");
        }
        write_frame(&mut socket, "<CSC_Ack/>")?;
        let _info = read_frame(&mut socket)?;
        debug!("received RFP CSC info");
        write_frame(
            &mut socket,
            &format!(
                r#"<CSC_Start_Session ProtocolID="RFP" SessionID="{}"/>"#,
                escape_xml(session_id)
            ),
        )?;
        tls_connector()?.connect(host, socket).map_err(Into::into)
    }
}

impl<S: Read + Write> RfbStream<S> {
    pub fn new(stream: S) -> Self {
        Self {
            stream,
            framebuffer_size: None,
            pending_updates: VecDeque::new(),
        }
    }

    pub fn framebuffer_size(&self) -> Option<(u16, u16)> {
        self.framebuffer_size
    }

    pub fn into_inner(self) -> S {
        self.stream
    }

    /// Framebuffer update request for the full framebuffer area, matching
    /// `RfbFramebufferUpdateRequestMsgV01_22`: `[3, incr, x, y, w, h]`.
    pub fn request_framebuffer_update(&mut self, incremental: bool) -> Result<()> {
        let (width, height) = self
            .framebuffer_size
            .ok_or_else(|| eyre::eyre!("framebuffer size unknown; handshake not finished"))?;
        self.request_region_update(0, 0, width, height, incremental)
    }

    pub fn request_region_update(
        &mut self,
        x: u16,
        y: u16,
        width: u16,
        height: u16,
        incremental: bool,
    ) -> Result<()> {
        let mut request = [0u8; 10];
        request[0] = FB_UPDATE_REQUEST;
        request[1] = u8::from(incremental);
        request[2..4].copy_from_slice(&x.to_be_bytes());
        request[4..6].copy_from_slice(&y.to_be_bytes());
        request[6..8].copy_from_slice(&width.to_be_bytes());
        request[8..10].copy_from_slice(&height.to_be_bytes());
        self.stream.write_all(&request)?;
        self.stream.flush()?;
        Ok(())
    }
}
