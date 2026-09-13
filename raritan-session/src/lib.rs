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
///
/// Precedence: exact index → exact id → exact name → id suffix → name
/// substring. Empty selectors are rejected (they would otherwise match
/// every port via `contains("")`).
pub fn find_port<'a>(ports: &'a [Port], selector: &str) -> eyre::Result<&'a Port> {
    if selector.is_empty() {
        eyre::bail!("empty port selector");
    }
    // Exact matches first (deterministic, no ambiguity).
    if let Some(port) = ports.iter().find(|port| {
        port.index.is_some_and(|index| index.to_string() == selector)
            || port.id == selector
            || port.name.as_deref() == Some(selector)
    }) {
        return Ok(port);
    }
    // Fuzzy matches: id suffix or name substring. Collect all matches so
    // ambiguous selectors fail loudly instead of picking port 0.
    let fuzzy: Vec<&Port> = ports
        .iter()
        .filter(|port| {
            port.id.ends_with(selector)
                || port
                    .name
                    .as_deref()
                    .is_some_and(|name| name.contains(selector))
        })
        .collect();
    match fuzzy.len() {
        0 => eyre::bail!("no port matches {selector:?}"),
        1 => Ok(fuzzy[0]),
        n => eyre::bail!("{n} ports match {selector:?}; be more specific"),
    }
}

/// Opens the full video path: fresh RDM session → credentials → best-effort
/// RDMEvent session → RFB handshake. The TR grant (cmd 55) is deliberately
/// skipped — the switch never answers it and RFB streams without it.
pub fn establish_video(
    config: &ConnectionConfig,
    port_id: &str,
) -> eyre::Result<RfbStream<TcpStream>> {
    info!(%port_id, "connecting RDM video session");
    // Fetch fresh credentials on this session (also validates the login).
    let mut rdm = RdmClient::connect(&config.host, &config.user, &config.password)?;
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
///
/// Non-update messages (pings, OSD, commands) are consumed by the pump but
/// do not count toward `n`: only messages that actually carry rectangles
/// advance the capture.
pub fn capture_frames(
    rfb: &mut RfbStream<TcpStream>,
    n: usize,
) -> eyre::Result<(Framebuffer, std::collections::HashSet<i32>, usize)> {
    // Bound headless captures: without a read timeout a stalled target
    // would block `--frames N` forever (the GUI sets 100 ms).
    let previous_timeout = rfb
        .inner_read_timeout()
        .unwrap_or(None);
    rfb.set_read_timeout(Some(std::time::Duration::from_secs(10)))?;
    let result = capture_frames_inner(rfb, n);
    // Restore: ignore errors, the stream may be broken anyway.
    let _ = rfb.set_read_timeout(previous_timeout);
    result
}

fn capture_frames_inner(
    rfb: &mut RfbStream<TcpStream>,
    n: usize,
) -> eyre::Result<(Framebuffer, std::collections::HashSet<i32>, usize)> {
    let (width, height) = rfb
        .framebuffer_size()
        .ok_or_eyre("no framebuffer dimensions")?;
    info!(width, height, "handshake complete");
    let mut framebuffer = Framebuffer::try_new(width, height)?;
    let mut seen_encodings = std::collections::HashSet::new();
    let mut total_rects = 0usize;
    let mut updates = 0usize;
    while updates < n {
        let update = rfb.read_message()?;
        if update.rectangles.is_empty() {
            continue;
        }
        updates += 1;
        // The switch can change resolutions mid-session (text mode ↔
        // graphics on session start): a late 128 adopts new dimensions,
        // and the pixel buffer must follow or rects clip/misalign.
        let (width, height) = rfb
            .framebuffer_size()
            .ok_or_eyre("framebuffer dimensions lost")?;
        if framebuffer.width != width || framebuffer.height != height {
            info!(width, height, "framebuffer resized; recreating buffer");
            framebuffer = Framebuffer::try_new(width, height)?;
        }
        for rect in &update.rectangles {
            seen_encodings.insert(rect.encoding);
        }
        total_rects += update.rectangles.len();
        framebuffer.apply_update(&update, PixelFormat::RGB565)?;
        info!(
            update = updates,
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
