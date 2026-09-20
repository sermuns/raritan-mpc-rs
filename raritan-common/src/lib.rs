//! Shared primitives for the Raritan crates: well-known ports/timeouts,
//! CSC framing, legacy TLS setup, XML helpers, RC4, and big-endian IO.

pub mod crypto;
pub mod csc;
pub mod tls;
pub mod xml;

pub use crypto::{
    current_time_millis, decode_base64, encode_base64, event_probe, rc4, time_xored_probe,
};
pub use csc::{display_xml, read_frame, write_frame};
pub use tls::tls_connector;
pub use xml::{escape_xml, xml_attribute};

// -- Ports and timeouts (previously net.rs) --

/// RDM control channel (CSC + TLS 1.0).
pub const DEFAULT_RDM_PORT: u16 = 5000;
/// RFB video channel (usually plaintext) + short TLS sessions.
pub const DEFAULT_RFB_PORT: u16 = 443;

pub const RDM_READ_TIMEOUT: std::time::Duration = std::time::Duration::from_secs(5);
/// How long the legacy TR grant waits (Java `TRRSP::CONNECT_TIMEOUT` ≈ 20 s).
pub const TR_GRANT_TIMEOUT: std::time::Duration = std::time::Duration::from_secs(25);
pub const EVENT_DRAIN_TIMEOUT: std::time::Duration = std::time::Duration::from_secs(2);
pub const TR_GRANT_POLL_TIMEOUT: std::time::Duration = std::time::Duration::from_secs(5);

// -- Big-endian scalar readers (previously io.rs) --

pub fn read_u8<R: std::io::Read>(reader: &mut R) -> eyre::Result<u8> {
    let mut value = [0; 1];
    reader.read_exact(&mut value)?;
    Ok(value[0])
}

pub fn read_u16<R: std::io::Read>(reader: &mut R) -> eyre::Result<u16> {
    let mut value = [0; 2];
    reader.read_exact(&mut value)?;
    Ok(u16::from_be_bytes(value))
}

pub fn read_u32<R: std::io::Read>(reader: &mut R) -> eyre::Result<u32> {
    let mut value = [0; 4];
    reader.read_exact(&mut value)?;
    Ok(u32::from_be_bytes(value))
}

pub fn read_i32<R: std::io::Read>(reader: &mut R) -> eyre::Result<i32> {
    let mut value = [0; 4];
    reader.read_exact(&mut value)?;
    Ok(i32::from_be_bytes(value))
}

/// RDM session credential pair (`"id":"key"` on the RFB wire).
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct SessionCreds {
    pub session_id: String,
    pub session_key: String,
}

impl SessionCreds {
    pub fn new(session_id: &str, session_key: &str) -> Self {
        Self {
            session_id: session_id.to_owned(),
            session_key: session_key.to_owned(),
        }
    }

    /// The `rdmSessionID` string the Java client authenticates with.
    pub fn rdm_session(&self) -> String {
        format!("\"{}\":\"{}\"", self.session_id, self.session_key)
    }
}
