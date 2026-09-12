//! RFB credential pair (thin wrapper over [`raritan_common::SessionCreds`]
//! to keep the public `RfbCredentials` API stable).

use raritan_common::SessionCreds;

/// RDM session credentials. On the wire the session is the quoted pair
/// `"id":"key"` (see `RFBProfile.rdmSession` / `ViewFactory.formatSessionID`).
#[derive(Debug, Clone)]
pub struct RfbCredentials {
    inner: SessionCreds,
}

impl RfbCredentials {
    pub fn new(session_id: &str, session_key: &str) -> Self {
        Self {
            inner: SessionCreds::new(session_id, session_key),
        }
    }

    pub fn session_id(&self) -> &str {
        &self.inner.session_id
    }

    pub fn session_key(&self) -> &str {
        &self.inner.session_key
    }

    /// The `rdmSessionID` string the Java client authenticates with.
    pub fn rdm_session(&self) -> String {
        self.inner.rdm_session()
    }
}
