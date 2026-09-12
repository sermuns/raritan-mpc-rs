//! Legacy binary TR layer (diagnostics only).
//!
//! The RFB video path intentionally bypasses the TR video-stream grant
//! (cmd 55): the switch never answers it, and video works without it
//! (see `docs/rdm.md`). These helpers remain for capture-time debugging:
//! `probe_ping` checks the TR layer is alive, `request_video_grant`
//! sends the byte-identical Java `TRVideoStream.connectVideoStream`
//! request and waits for grant 37.

use eyre::{Context, OptionExt, bail};
use openssl::ssl::SslStream;
use raritan_common::{TR_GRANT_POLL_TIMEOUT, escape_xml};
use std::{
    io::{Read, Write},
    net::TcpStream,
};
use tracing::{debug, info};

/// TR command numbers.
pub const TR_PING: u8 = 3;
pub const TR_PING_ALT: u8 = 2;
pub const TR_CONNECT_VIDEO_STREAM: u8 = 55;
pub const TR_VIDEO_CONNECTED: u8 = 37;

/// Reads one `[len:u16-be][cmd][packet][payload...]` TR command.
pub fn read_tr_command(stream: &mut SslStream<TcpStream>) -> eyre::Result<Vec<u8>> {
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

/// Sends TR PINGs with several packet IDs; true if any response arrived.
pub fn probe_ping(stream: &mut SslStream<TcpStream>) -> bool {
    let _ = stream
        .get_ref()
        .set_read_timeout(Some(std::time::Duration::from_secs(3)));
    for (cmd, packet_id) in [
        (TR_PING, 1u8),
        (TR_PING, 0),
        (TR_PING, 9),
        (TR_PING_ALT, 1),
        (TR_PING_ALT, 0),
    ] {
        let command = [0u8, 4, cmd, packet_id];
        if stream.write_all(&command).is_err() {
            return false;
        }
        let _ = stream.flush();
        match read_tr_command(stream) {
            Ok(response) => {
                info!(
                    length = response.len(),
                    command = response.get(2).copied(),
                    packet = response.get(3).copied(),
                    packet_id,
                    "TR ping answered"
                );
                return true;
            }
            Err(error) => {
                info!(packet_id, error = %format!("{error:#}"), "TR ping unanswered");
            }
        }
    }
    false
}

/// 40-byte compression parameters, byte-identical to the Java client
/// (`TRCMD_CONNECT_VIDEO_STREAM_DATA`).
pub fn video_compression_parameters() -> [u8; 40] {
    let mut parameters = [0; 40];
    parameters[0..4].copy_from_slice(&56u32.to_be_bytes());
    parameters[4..6].copy_from_slice(&10u16.to_be_bytes());
    parameters[24..28].copy_from_slice(&6u32.to_be_bytes());
    parameters
}

/// Sends cmd 55 and waits until `timeout` for grant 37. Returns the
/// granted device ID.
pub fn request_video_grant(
    stream: &mut SslStream<TcpStream>,
    portal: &str,
    target: &str,
    force: bool,
    timeout: std::time::Duration,
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
    let length = u16::try_from(
        44usize
            .checked_add(xml.len())
            .ok_or_eyre("video command length overflow")?,
    )?;
    let mut command = Vec::with_capacity(length as usize);
    command.extend_from_slice(&length.to_be_bytes());
    let packet_id = 1u8;
    command.extend_from_slice(&[TR_CONNECT_VIDEO_STREAM, packet_id]);
    command.extend_from_slice(&video_compression_parameters());
    command.extend_from_slice(xml.as_bytes());
    stream.write_all(&command)?;
    stream.flush()?;
    debug!(length, "sent connect-video-stream command");

    // Like Java's Monitor.waiting(20000): keep reading until the deadline.
    let deadline = std::time::Instant::now() + timeout;
    stream
        .get_ref()
        .set_read_timeout(Some(TR_GRANT_POLL_TIMEOUT))?;
    loop {
        let response = match read_tr_command(stream) {
            Ok(response) => response,
            Err(error) => {
                debug!(error = %format!("{error:#}"), "RDM read while waiting for grant");
                if std::time::Instant::now() >= deadline {
                    return Err(error).wrap_err("waiting for video-stream response");
                }
                continue;
            }
        };
        let response_command = response[2];
        let response_packet = response[3];
        debug!(
            length = response.len(),
            command = response_command,
            packet_id = response_packet,
            "received RDM command"
        );
        if response_command == TR_PING {
            let error = response
                .get(4..8)
                .and_then(|bytes| bytes.try_into().ok())
                .map(u32::from_be_bytes);
            bail!("RDM server rejected command with error {error:?}");
        }
        if response_command == TR_VIDEO_CONNECTED && response_packet == packet_id {
            let device_id = response
                .get(4)
                .copied()
                .ok_or_eyre("video-stream response did not contain device ID")?;
            info!(device_id, "video stream granted");
            return Ok(device_id);
        }
    }
}
