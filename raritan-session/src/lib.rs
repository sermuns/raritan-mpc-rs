//! End-to-end video session orchestration shared by the CLI and GUI:
//! RDM login → credentials → RFB handshake → pump, with the event session
//! opened alongside. The GUI keeps one login for its lifetime
//! ([`ControlLink`]); the CLI logs in per run ([`establish_video`]).

pub mod control;

pub use control::{ControlLink, PortsResult, RDM_KEEPALIVE_INTERVAL};
pub use raritan_common::SessionCreds;

use eyre::OptionExt;
use raritan_rdm::{Port, RdmClient};
use raritan_rfb::{Framebuffer, PixelFormat, RfbStream};
use std::{
    io::{Read, Write},
    net::TcpStream,
    time::{Duration, Instant},
};
use tracing::{debug, info};

#[derive(Debug, Clone)]
pub struct ConnectionConfig {
    pub host: String,
    pub user: String,
    pub password: String,
}

/// Finds a port by index, id, id suffix, exact name, or name substring.
/// Precedence: exact index → id → name → id suffix → name substring.
/// Empty selectors are rejected (they'd match everything).
pub fn find_port<'a>(ports: &'a [Port], selector: &str) -> eyre::Result<&'a Port> {
    if selector.is_empty() {
        eyre::bail!("empty port selector");
    }
    if let Some(port) = ports.iter().find(|port| {
        port.index
            .is_some_and(|index| index.to_string() == selector)
            || port.id == selector
            || port.name.as_deref() == Some(selector)
    }) {
        return Ok(port);
    }
    // Collect all fuzzy matches so ambiguity fails loudly instead of picking port 0.
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

/// A video session owning its own RDM login (the CLI path). The RDM
/// connection must outlive the video: the switch reaps the session (event
/// socket first, then RFB) once its owner disconnects or idles.
pub struct VideoSession {
    pub rdm: RdmClient,
    pub rfb: RfbStream<TcpStream>,
}

/// Java `PingTimer`: RFB ping request every 20 s.
pub const RFB_PING_INTERVAL: Duration = Duration::from_secs(20);

/// Client-side RFB ping (Java `PingTimer`). Call [`RfbPinger::tick`] from
/// the pump loop at least every few seconds.
pub struct RfbPinger {
    last: Instant,
    serial: u32,
}

impl Default for RfbPinger {
    fn default() -> Self {
        Self::new()
    }
}

impl RfbPinger {
    pub fn new() -> Self {
        Self {
            last: Instant::now(),
            serial: 0,
        }
    }

    pub fn tick<S: Read + Write>(&mut self, rfb: &mut RfbStream<S>) -> eyre::Result<()> {
        if self.last.elapsed() < RFB_PING_INTERVAL {
            return Ok(());
        }
        self.serial = self.serial.wrapping_add(1);
        debug!(serial = self.serial, "sending RFB ping request");
        rfb.write_ping_request(self.serial)?;
        self.last = Instant::now();
        Ok(())
    }
}

/// Returned by [`establish_video`] when `cancelled` fired between stages.
/// Callers should exit quietly (no retry, no error shown).
#[derive(Debug)]
pub struct Cancelled {
    pub stage: &'static str,
}

impl std::fmt::Display for Cancelled {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "video connect cancelled before {}", self.stage)
    }
}

impl std::error::Error for Cancelled {}

fn check_cancel(cancelled: &dyn Fn() -> bool, stage: &'static str) -> eyre::Result<()> {
    if cancelled() {
        return Err(Cancelled { stage }.into());
    }
    Ok(())
}

/// RFB handshake on an existing RDM session, plus the stuck-key release.
/// `cancelled` is polled between stages (see [`establish_video`]).
pub fn connect_video(
    host: &str,
    creds: &SessionCreds,
    port_id: &str,
    cancelled: &dyn Fn() -> bool,
) -> eyre::Result<RfbStream<TcpStream>> {
    check_cancel(cancelled, "RFB connect")?;
    info!(%port_id, "connecting RFB session");
    let started = Instant::now();
    let mut rfb = RfbStream::connect_raritan(host, &creds.session_id, &creds.session_key, port_id)?;
    info!(
        elapsed_ms = started.elapsed().as_millis(),
        "RFB handshake done"
    );
    check_cancel(cancelled, "key release")?;
    // Clear key state stuck from an earlier session: the switch keeps
    // per-target key state, so only the target can release it.
    rfb.release_all_keys()?;
    Ok(rfb)
}

/// Opens the full video path with its own login (RDM → credentials → RFB
/// → event session). The TR grant (cmd 55) is skipped — the switch never
/// answers it.
///
/// `cancelled` is polled between stages: a superseded connect stops
/// within one stage instead of finishing every handshake. Each stage is
/// a TLS 1.0 handshake the switch serialises, so an abandoned connect
/// running alongside a live one roughly doubles the live one's time.
pub fn establish_video(
    config: &ConnectionConfig,
    port_id: &str,
    cancelled: &dyn Fn() -> bool,
) -> eyre::Result<VideoSession> {
    info!(%port_id, "connecting RDM video session");
    let started = Instant::now();
    check_cancel(cancelled, "RDM login")?;
    // Only credentials are needed here, not the inventory.
    let mut rdm = RdmClient::connect(&config.host, &config.user, &config.password)?;
    info!(
        elapsed_ms = started.elapsed().as_millis(),
        "RDM connect done"
    );
    check_cancel(cancelled, "RDM credentials")?;
    rdm.fetch_session_credentials()?;
    let creds = rdm
        .session_credentials()
        .map(|(id, key)| SessionCreds::new(id, key))?;
    let rfb = connect_video(&config.host, &creds, port_id, cancelled)?;
    // The Java client holds the event session while video runs; video works
    // without it, so it is opened off the critical path (its TLS handshake
    // is ~2 s of connect time) and failures only warn.
    rdm.spawn_event_session(&creds.session_id, &creds.session_key);
    Ok(VideoSession { rdm, rfb })
}

/// Reads `n` framebuffer updates into a fresh RGB565 framebuffer.
/// Non-update messages don't count toward `n`; returns the framebuffer,
/// encodings seen, and total rect count.
pub fn capture_frames(
    rfb: &mut RfbStream<TcpStream>,
    n: usize,
) -> eyre::Result<(Framebuffer, std::collections::HashSet<i32>, usize)> {
    // Bound headless captures: a stalled target would otherwise block forever.
    let previous_timeout = rfb.inner_read_timeout().unwrap_or(None);
    rfb.set_read_timeout(Some(std::time::Duration::from_secs(10)))?;
    let result = capture_frames_inner(rfb, n);
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
    let waiting = std::time::Instant::now();
    while updates < n {
        let update = rfb.read_message()?;
        if update.rectangles.is_empty() {
            continue;
        }
        updates += 1;
        if updates == 1 {
            info!(
                elapsed_ms = waiting.elapsed().as_millis(),
                "first framebuffer update received"
            );
        }
        // Late 128 changes dimensions: follow with the pixel buffer or rects misalign.
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
        debug!(
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

pub fn is_black(framebuffer: &Framebuffer) -> bool {
    framebuffer.rgba.iter().all(|b| *b == 0)
}
