//! `CSC_Test2` challenge/response for the `RDMEvent` session, mirroring
//! `CSCConnect.CSC_Test`: the server sends a base64 `ClearText` challenge,
//! we RC4 it with the session key plus a time-XORed probe; the server
//! echoes our probe back encrypted.

use eyre::{Context, OptionExt, bail};
use raritan_common::{
    decode_base64, encode_base64, event_probe, rc4, read_frame, write_frame, xml_attribute,
};
use std::io::{Read, Write};
use tracing::debug;

/// Requires an already-TLS-upgraded stream.
pub fn csc_test2<S: Read + Write>(tls: &mut S, session_key: &str) -> eyre::Result<()> {
    let challenge = String::from_utf8(read_frame(tls).wrap_err("reading event CSC challenge")?)?;
    let clear_text = xml_attribute(&challenge, "ClearText")
        .ok_or_eyre("event authentication challenge lacks ClearText")?;
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
    let echoed = xml_attribute(&response, "Encrypted")
        .ok_or_eyre("event authentication response lacks Encrypted")?;
    if rc4(&key, &decode_base64(&echoed)?)? != clear {
        bail!("RDM event session authentication failed");
    }
    Ok(())
}
