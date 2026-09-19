//! Shared primitives for the Raritan crates: well-known ports/timeouts,
//! CSC framing, legacy TLS setup, XML helpers, RC4, and big-endian IO.

pub mod crypto;
pub mod csc;
pub mod io;
pub mod net;
pub mod tls;
pub mod xml;

pub use crypto::{
    current_time_millis, decode_base64, encode_base64, event_probe, rc4, time_xored_probe,
};
pub use csc::{display_xml, read_frame, write_frame};
pub use io::{read_i32, read_u8, read_u16, read_u32};
pub use net::{
    DEFAULT_RDM_PORT, DEFAULT_RFB_PORT, EVENT_DRAIN_TIMEOUT, RDM_READ_TIMEOUT,
    TR_GRANT_POLL_TIMEOUT, TR_GRANT_TIMEOUT,
};
pub use tls::tls_connector;
pub use xml::{escape_xml, xml_attribute};

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
