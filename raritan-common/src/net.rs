//! Well-known ports and timeouts (previously magic numbers).

use std::time::Duration;

/// RDM control channel (CSC + TLS 1.0).
pub const DEFAULT_RDM_PORT: u16 = 5000;
/// RFB video channel (usually plaintext) + short TLS sessions.
pub const DEFAULT_RFB_PORT: u16 = 443;

pub const RDM_READ_TIMEOUT: Duration = Duration::from_secs(5);
/// How long the legacy TR grant waits (Java `TRRSP::CONNECT_TIMEOUT` ≈ 20 s).
pub const TR_GRANT_TIMEOUT: Duration = Duration::from_secs(25);
pub const EVENT_DRAIN_TIMEOUT: Duration = Duration::from_secs(2);
pub const TR_GRANT_POLL_TIMEOUT: Duration = Duration::from_secs(5);
