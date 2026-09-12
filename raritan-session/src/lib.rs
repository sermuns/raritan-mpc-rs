//! End-to-end video session orchestration shared by the CLI and GUI.
//!
//! This used to be copy-pasted between `raritan-cli` and `raritan-mpc`:
//! fresh RDM login → credentials → background RDMEvent session (best
//! effort) → plaintext RFB handshake → framebuffer pump.

use eyre::OptionExt;
use raritan_rdm::{Port, RdmClient};
use raritan_rfb::{Framebuffer, PixelFormat, RfbStream};
use std::net::TcpStream;
use tracing::{info, warn};

/// Login triple for both the listing and the video RDM sessions.
#[derive(Debug, Clone)]
pub struct ConnectionConfig {
    pub host: String,
    pub user: String,
    pub password: String,
}

impl ConnectionConfig {
    pub fn new(host: &str, user: &str, password: &str) -> Self {
        Self {
            host: host.to_owned(),
            user: user.to_owned(),
            password: password.to_owned(),
        }
    }
}

/// Finds a port by index, id, id suffix, exact name, or name substring
/// (same selector language as the CLI `--video` flag).
pub fn find_port<'a>(ports: &'a [Port], selector: &str) -> eyre::Result<&'a Port> {
    ports
        .iter()
        .find(|port| {
            port.index.is_some_and(|index| index.to_string() == selector)
                || port.id == selector
                || port.id.ends_with(selector)
                || port.name.as_deref() == Some(selector)
                || port
                    .name
                    .as_deref()
                    .is_some_and(|name| name.contains(selector))
        })
        .ok_or_else(|| eyre::eyre!("no port matches {selector:?}"))
}

/// Opens the full video path: fresh RDM session → credentials → best-effort
/// RDMEvent session → RFB handshake. The TR grant (cmd 55) is deliberately
/// skipped — the switch never answers it and RFB streams without it.
pub fn establish_video(
    config: &ConnectionConfig,
    port_id: &str,
) -> eyre::Result<RfbStream<TcpStream>> {
    info!(%port_id, "connecting RDM video session");
    let rdm = RdmClient::connect(&config.host, &config.user, &config.password)?;
    // Fetch fresh credentials on this session (also validates the login).
    let mut rdm = rdm;
    rdm.enumerate_ports()?;
    let (session_id, session_key) = rdm
        .session_credentials()
        .map(|(id, key)| (id.to_owned(), key.to_owned()))?;
    // The Java client always holds the event session while video runs;
    // video works without it, so a failure only warns.
    if let Err(error) = rdm.open_event_session(&session_id, &session_key) {
        warn!(%error, "RDM event session failed; continuing without it");
    }
    info!(%port_id, "connecting RFB session");
    let mut rfb = RfbStream::connect_raritan(&config.host, &session_id, &session_key, port_id)?;
    // Clear any key state stuck down from an earlier session (e.g. a
    // modifier held while the app lost focus or died): the switch keeps
    // per-target key state across connections, so only the target can
    // release it. Releases are no-ops for keys that aren't down.
    rfb.release_all_keys()?;
    info!(%port_id, "cleared stuck keys");
    Ok(rfb)
}

/// Reads `n` framebuffer updates, applying each to a fresh RGB565
/// framebuffer and re-requesting incrementally. Returns the framebuffer
/// plus the set of encodings seen and total rect count.
pub fn capture_frames(
    rfb: &mut RfbStream<TcpStream>,
    n: usize,
) -> eyre::Result<(Framebuffer, std::collections::HashSet<i32>, usize)> {
    let (width, height) = rfb
        .framebuffer_size()
        .ok_or_eyre("no framebuffer dimensions")?;
    info!(width, height, "handshake complete");
    let mut framebuffer = Framebuffer::new(width, height);
    let mut seen_encodings = std::collections::HashSet::new();
    let mut total_rects = 0usize;
    for i in 0..n {
        let update = rfb.read_message()?;
        for rect in &update.rectangles {
            seen_encodings.insert(rect.encoding);
        }
        total_rects += update.rectangles.len();
        framebuffer.apply_update(&update, PixelFormat::RGB565)?;
        info!(
            update = i,
            flags = update.flags,
            rects = update.rectangles.len(),
            total_rects,
            "framebuffer update"
        );
        rfb.request_framebuffer_update(true)?;
    }
    info!(?seen_encodings, total_rects, "capture finished");
    Ok((framebuffer, seen_encodings, total_rects))
}

/// Encodes an RGBA framebuffer as binary PPM (P6), dropping alpha.
pub fn encode_ppm(framebuffer: &Framebuffer) -> Vec<u8> {
    let mut ppm = format!("P6\n{} {}\n255\n", framebuffer.width, framebuffer.height).into_bytes();
    let (pixels, _remainder) = framebuffer.rgba.as_chunks::<4>();
    for pixel in pixels {
        ppm.extend_from_slice(&pixel[0..3]);
    }
    ppm
}

/// True when every byte is zero (nothing was ever painted).
pub fn is_black(framebuffer: &Framebuffer) -> bool {
    framebuffer.rgba.iter().all(|b| *b == 0)
}
