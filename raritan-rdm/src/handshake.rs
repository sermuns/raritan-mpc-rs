//! Plaintext CSC handshake shared by the main, unauthenticated, and
//! event sessions: greeting → `<CSC_Ack/>` → info → `StartSession`.

use eyre::{Context, bail};
use raritan_common::{display_xml, read_frame, write_frame};
use std::io::{Read, Write};
use tracing::debug;

/// Performs the pre-TLS CSC exchange on a fresh `:5000` socket and sends
/// a `CSC_Start_Session` for the given protocol (`RDM` / `RDMEvent`).
/// Returns the `<CSC_Info>` payload for callers that log it.
pub fn csc_start_session<S: Read + Write>(
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
            raritan_common::escape_xml(id)
        ),
        None => format!(r#"<CSC_Start_Session ProtocolID="{protocol}"/>"#),
    };
    write_frame(stream, &start).wrap_err("writing CSC start-session")?;
    debug!(protocol, "CSC start-session sent");
    Ok(info)
}

/// Sends `<CSC_Auth>` and expects `<CSC_Pass/>`.
pub fn csc_auth<S: Read + Write>(tls: &mut S, user: &str, password: &str) -> eyre::Result<()> {
    write_frame(
        tls,
        &format!(
            r#"<CSC_Auth UserName="{}" Password="{}"/>"#,
            raritan_common::escape_xml(user),
            raritan_common::escape_xml(password)
        ),
    )
    .wrap_err("writing CSC auth")?;
    let auth = read_frame(tls).wrap_err("reading CSC auth response")?;
    if !auth.starts_with(b"<CSC_Pass") {
        bail!("authentication failed: {}", display_xml(&auth));
    }
    Ok(())
}
