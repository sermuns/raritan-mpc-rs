//! Compatibility shim: CSC framing now lives in `raritan-common`.
//!
//! Re-exported here so existing `crate::protocol::…` paths keep working.

pub use raritan_common::{display_xml, read_frame, write_frame};
pub use raritan_common::{escape_xml, xml_attribute};
