//! RFB 1.29 handshake, byte-exact per the Java client
//! (`RfbHandlerV01_29`) and truth.pcapng stream 4.

use crate::{
    creds::RfbCredentials,
    framebuffer::PixelFormat,
    proto::{
        ASSOCIATED_TAG, AUTH_CAPS, AUTH_METHOD_RDM_SESSION, AUTH_SUCCESSFUL, CHALLENGE_RESPONSE,
        CLIENT_INIT, CONNECTION_PARAMETERS, FRAMEBUFFER_UPDATE, KVM_SWITCH_EVENT, LOGIN,
        MOUSE_SYNC_EVENT, SERVER_FB_FORMAT, SERVER_INIT, SESSION_CHALLENGE,
        SET_CONNECTION_PARAMETER, SET_ENCODINGS, SET_PIXEL_FORMAT, UTF8_STRING,
        VIDEO_SETTINGS_REQUEST, default_encodings,
    },
    stream::RfbStream,
};
use eyre::{Result, bail};
use raritan_common::{read_i32, read_u8};
use std::io::{Read, Write};
use tracing::{debug, info, trace, warn};

impl<S: Read + Write> RfbStream<S> {
    pub fn handshake(&mut self, credentials: &RfbCredentials, port: &str) -> Result<PixelFormat> {
        info!(%port, "starting RFB 1.29 handshake");
        // RfbHelloMsgV01_00 / RfbVersionMsgV01_00.
        self.stream.write_all(b"e-RIC AUTH=")?;
        let mut version = [0; 16];
        self.stream.read_exact(&mut version)?;
        if &version != b"e-RIC RFB 01.29\n" {
            bail!(
                "unexpected RFB version: {:?}",
                String::from_utf8_lossy(&version)
            );
        }
        debug!("RFB protocol version negotiated");
        self.stream.write_all(b"e-RIC RFB 01.29\n")?;

        // RfbAuthCapsMsgV01_22 + RfbAuthenticatorV01_22: require the
        // RDM-session method and log in as "super".
        let auth_type = read_u8(&mut self.stream)?;
        if auth_type != AUTH_CAPS {
            bail!("expected RFB auth capabilities, got {auth_type}");
        }
        let capabilities = read_u8(&mut self.stream)?;
        info!(capabilities, "received RFB authentication capabilities");
        if (capabilities & AUTH_METHOD_RDM_SESSION) == 0 {
            bail!("RFB server does not offer RDM-session authentication");
        }
        // RfbLoginMsgV01_22.write(os, "super", 16, 0).
        self.stream
            .write_all(&[LOGIN, 16, 6, 0, 0, 0, 0, 0, b's', b'u', b'p', b'e', b'r', 0])?;

        // RfbSessionChallengeMsgV01_22: [33][len][challenge?].
        let message = read_u8(&mut self.stream)?;
        if message != SESSION_CHALLENGE {
            bail!("expected RFB session challenge, got {message}");
        }
        let size = read_u8(&mut self.stream)? as usize;
        if size > 0 {
            let mut challenge = vec![0; size];
            self.stream.read_exact(&mut challenge)?;
            debug!(size, "received non-empty RFB session challenge");
        }
        // RfbAuthenticatorV01_22 (RDM method): writeChallengeResponse(
        // rdmSessionID + '\0'); length includes the NUL.
        let session = format!("{}\0", credentials.rdm_session());
        let session_bytes = session.as_bytes();
        if session_bytes.len() > u8::MAX as usize {
            bail!("RDM session string is too long");
        }
        self.stream.write_all(&[CHALLENGE_RESPONSE])?;
        self.stream.write_all(&[session_bytes.len() as u8])?;
        self.stream.write_all(session_bytes)?;
        self.stream.flush()?;

        // Event loop: the server interleaves auth-ok, connection
        // parameters, UTF-8 welcome, server-init, OSD, keyboard layout,
        // server commands, USB profiles, ... before the 128
        // framebuffer-format message that ends the handshake.
        loop {
            let message_type = read_u8(&mut self.stream)?;
            trace!(message_type, "received RFB handshake message");
            match message_type {
                AUTH_SUCCESSFUL => {
                    let mut rest = [0; 7];
                    self.stream.read_exact(&mut rest)?;
                    info!("RFB session authentication succeeded");
                }
                CONNECTION_PARAMETERS => {
                    let params = self.read_connection_parameters()?;
                    debug!(count = params.len(), "received connection parameters");
                }
                UTF8_STRING => {
                    let string = self.read_utf8_string()?;
                    debug!(%string, "received UTF-8 welcome string");
                    self.write_client_init(credentials, port)?;
                }
                SERVER_INIT => {
                    let mut pad = [0; 3];
                    self.stream.read_exact(&mut pad)?;
                    let server_id = read_i32(&mut self.stream)?;
                    debug!(server_id, "received server init");
                }
                SERVER_FB_FORMAT => {
                    let (width, height, pixel_format) = self.read_server_fb_format()?;
                    self.write_set_encodings(&default_encodings())?;
                    self.write_set_pixel_format(PixelFormat::RGB565)?;
                    self.framebuffer_size = Some((width, height));
                    info!(width, height, ?pixel_format, "received framebuffer format");
                    // Session init exactly as the Java client does it.
                    for _ in 0..3 {
                        self.request_region_update(0, 0, width, height, false)?;
                    }
                    self.write_video_settings_request(1)?;
                    self.write_mouse_sync(0)?;
                    self.write_pointer_event(0, 0, 0, 0)?;
                    self.write_set_connection_parameter("current_mouse_mode", "absolute")?;
                    self.write_ping_request(0)?;
                    // We decode Raw rects with the negotiated format, so
                    // return what is actually in force (RGB565), not the
                    // server's announcement.
                    return Ok(PixelFormat::RGB565);
                }
                FRAMEBUFFER_UPDATE => {
                    let update = self.read_framebuffer_update()?;
                    warn!("received framebuffer update during handshake; stashing");
                    // Bound the stash: a malicious server could flood type 0
                    // before 128 and OOM us.
                    if self.pending_updates.len() >= 64 {
                        bail!("too many early framebuffer updates during handshake");
                    }
                    self.pending_updates.push_back(update);
                }
                _ => self.skip_server_message(message_type)?,
            }
        }
    }

    /// Client session init after the server 7 welcome message:
    /// `[7,0,flags=4]` + associated tag (the RDM session string) +
    /// KVM-switch event.
    fn write_client_init(&mut self, credentials: &RfbCredentials, port: &str) -> Result<()> {
        self.stream.write_all(&[CLIENT_INIT, 0, 0, 4])?;
        self.write_associated_tag(credentials.rdm_session().as_bytes())?;
        self.write_kvm_switch(port)?;
        self.stream.flush()?;
        debug!(%port, "selected KVM port on RFB channel");
        Ok(())
    }

    fn write_associated_tag(&mut self, tag: &[u8]) -> Result<()> {
        if tag.len() > u16::MAX as usize {
            bail!("associated tag is too long");
        }
        self.stream.write_all(&[ASSOCIATED_TAG, 0])?;
        self.stream.write_all(&(tag.len() as u16).to_be_bytes())?;
        self.stream.write_all(tag)?;
        Ok(())
    }

    fn write_kvm_switch(&mut self, port: &str) -> Result<()> {
        let port_bytes = port.as_bytes();
        if port_bytes.len() > u16::MAX as usize {
            bail!("port ID is too long");
        }
        self.stream.write_all(&[KVM_SWITCH_EVENT, 0])?;
        self.stream
            .write_all(&(port_bytes.len() as u16).to_be_bytes())?;
        self.stream.write_all(port_bytes)?;
        self.stream.flush()?;
        Ok(())
    }

    pub(crate) fn write_set_encodings(&mut self, encodings: &[u32]) -> Result<()> {
        if encodings.len() > u16::MAX as usize {
            bail!("too many encodings");
        }
        self.stream.write_all(&[SET_ENCODINGS, 0])?;
        self.stream
            .write_all(&(encodings.len() as u16).to_be_bytes())?;
        for encoding in encodings {
            self.stream.write_all(&encoding.to_be_bytes())?;
        }
        self.stream.flush()?;
        Ok(())
    }

    pub(crate) fn write_set_pixel_format(&mut self, format: PixelFormat) -> Result<()> {
        let mut message = [0u8; 20];
        message[0] = SET_PIXEL_FORMAT;
        message[4] = format.bits_per_pixel;
        message[5] = format.depth;
        message[6] = u8::from(format.big_endian);
        message[7] = u8::from(format.true_colour);
        message[8..10].copy_from_slice(&format.red_max.to_be_bytes());
        message[10..12].copy_from_slice(&format.green_max.to_be_bytes());
        message[12..14].copy_from_slice(&format.blue_max.to_be_bytes());
        message[14] = format.red_shift;
        message[15] = format.green_shift;
        message[16] = format.blue_shift;
        self.stream.write_all(&message)?;
        self.stream.flush()?;
        Ok(())
    }

    pub(crate) fn write_video_settings_request(&mut self, kind: u8) -> Result<()> {
        self.stream.write_all(&[VIDEO_SETTINGS_REQUEST, kind])?;
        self.stream.flush()?;
        Ok(())
    }

    pub(crate) fn write_mouse_sync(&mut self, mode: u8) -> Result<()> {
        self.stream.write_all(&[MOUSE_SYNC_EVENT, mode])?;
        self.stream.flush()?;
        Ok(())
    }

    pub(crate) fn write_set_connection_parameter(&mut self, name: &str, value: &str) -> Result<()> {
        if name.len() > u8::MAX as usize || value.len() > u8::MAX as usize {
            bail!("connection parameter too long");
        }
        self.stream.write_all(&[
            SET_CONNECTION_PARAMETER,
            name.len() as u8,
            value.len() as u8,
        ])?;
        self.stream.write_all(name.as_bytes())?;
        self.stream.write_all(value.as_bytes())?;
        self.stream.flush()?;
        Ok(())
    }
}
