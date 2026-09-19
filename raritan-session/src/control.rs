//! Long-lived RDM control connection, like the Java client's single
//! `TRConnection`: one login per program run, credentials fetched once, the
//! event session held, a keepalive query when idle. Port enumeration and
//! video credentials are requests served over a channel, so nothing else
//! ever logs in. (Each extra login is a TLS 1.0 handshake the switch
//! serialises, and its logout is a session reap right next to the live
//! video session.)

use crate::ConnectionConfig;
use raritan_common::SessionCreds;
use raritan_rdm::{Port, RdmClient, SwitchInfo};
use std::{
    sync::mpsc::{self, Receiver, RecvTimeoutError, Sender},
    thread,
    time::{Duration, Instant},
};
use tracing::{info, warn};

/// Java `TRKeepAliveThread`: 58 s "not responding" limit, ping at half.
pub const RDM_KEEPALIVE_INTERVAL: Duration = Duration::from_secs(29);
/// Longest a caller waits on the control thread: a login plus an
/// enumeration queued ahead of it, on a slow switch.
pub const CONTROL_REPLY_TIMEOUT: Duration = Duration::from_mins(1);

/// Listed ports plus switch identity, or a message for the UI.
pub type PortsResult = Result<(Vec<Port>, SwitchInfo), String>;

enum Request {
    Ports(Sender<PortsResult>),
    Credentials(Sender<Result<SessionCreds, String>>),
}

/// Handle to the control thread. Clones share the connection; the thread
/// exits (closing the connection) when the last handle is dropped.
#[derive(Clone)]
pub struct ControlLink {
    requests: Sender<Request>,
    host: String,
}

impl ControlLink {
    /// Starts the control thread. It logs in lazily, on the first request.
    pub fn spawn(config: ConnectionConfig) -> Self {
        let host = config.host.clone();
        let (requests, inbox) = mpsc::channel();
        thread::spawn(move || Control::new(config).run(&inbox));
        Self { requests, host }
    }

    /// The switch this link is bound to (RFB connects go to the same host).
    pub fn host(&self) -> &str {
        &self.host
    }

    /// Asks for the listed ports. Poll the receiver: the GUI thread must
    /// not block, and a dropped thread shows up as `Disconnected`.
    pub fn request_ports(&self) -> Receiver<PortsResult> {
        let (reply, receiver) = mpsc::channel();
        let _ = self.requests.send(Request::Ports(reply));
        receiver
    }

    /// Session credentials for an RFB handshake, verified live first.
    /// Blocks up to [`CONTROL_REPLY_TIMEOUT`].
    pub fn credentials(&self) -> Result<SessionCreds, String> {
        let (reply, receiver) = mpsc::channel();
        self.requests
            .send(Request::Credentials(reply))
            .map_err(|_| "control connection thread has exited".to_owned())?;
        match receiver.recv_timeout(CONTROL_REPLY_TIMEOUT) {
            Ok(result) => result,
            Err(RecvTimeoutError::Timeout) => {
                Err("timed out waiting for the control connection".to_owned())
            }
            Err(RecvTimeoutError::Disconnected) => {
                Err("control connection thread has exited".to_owned())
            }
        }
    }
}

struct Control {
    config: ConnectionConfig,
    client: Option<RdmClient>,
    last_activity: Instant,
}

impl Control {
    fn new(config: ConnectionConfig) -> Self {
        Self {
            config,
            client: None,
            last_activity: Instant::now(),
        }
    }

    fn run(mut self, inbox: &Receiver<Request>) {
        loop {
            let wait = RDM_KEEPALIVE_INTERVAL.saturating_sub(self.last_activity.elapsed());
            match inbox.recv_timeout(wait) {
                Ok(Request::Ports(reply)) => {
                    let _ = reply.send(self.ports());
                }
                Ok(Request::Credentials(reply)) => {
                    let _ = reply.send(self.credentials());
                }
                Err(RecvTimeoutError::Timeout) => self.keepalive(),
                Err(RecvTimeoutError::Disconnected) => {
                    info!(host = %self.config.host, "closing RDM control connection");
                    return;
                }
            }
        }
    }

    fn connect(config: &ConnectionConfig) -> eyre::Result<RdmClient> {
        info!(host = %config.host, "opening RDM control connection");
        let mut client = RdmClient::connect(&config.host, &config.user, &config.password)?;
        client.fetch_session_credentials()?;
        let creds = session_creds(&client)?;
        // Held for the connection's lifetime, like the Java event loop.
        client.spawn_event_session(&creds.session_id, &creds.session_key);
        Ok(client)
    }

    /// The live connection, logging in first if there is none.
    fn client(&mut self) -> eyre::Result<&mut RdmClient> {
        let client = match self.client.take() {
            Some(client) => client,
            None => Self::connect(&self.config)?,
        };
        self.last_activity = Instant::now();
        Ok(self.client.insert(client))
    }

    /// Runs `op` on the live connection. Any failure drops the connection
    /// so the next request logs in afresh: the switch reaps a session
    /// whose control channel broke, so its credentials are useless anyway.
    fn with_client<T>(
        &mut self,
        op: impl FnOnce(&mut RdmClient) -> eyre::Result<T>,
    ) -> Result<T, String> {
        let result = self.client().and_then(op);
        if let Err(error) = &result {
            warn!(error = %format!("{error:#}"), "RDM control connection failed; dropping it");
            self.client = None;
        }
        result.map_err(|error| format!("{error:?}"))
    }

    fn ports(&mut self) -> PortsResult {
        self.with_client(|client| {
            let ports = client
                .enumerate_ports()?
                .into_iter()
                .filter(Port::is_listed)
                .collect();
            Ok((ports, client.switch_info().clone()))
        })
    }

    fn credentials(&mut self) -> Result<SessionCreds, String> {
        // Verified with a round trip first: a video reconnect after a
        // session reap must get fresh credentials, not those of the
        // session that just died.
        self.with_client(|client| {
            client.keepalive()?;
            session_creds(client)
        })
    }

    fn keepalive(&mut self) {
        if self.client.is_some() {
            let _ = self.with_client(RdmClient::keepalive);
        }
        // Nothing to keep alive: just rearm the timer (no busy loop).
        self.last_activity = Instant::now();
    }
}

fn session_creds(client: &RdmClient) -> eyre::Result<SessionCreds> {
    let (id, key) = client.session_credentials()?;
    Ok(SessionCreds::new(id, key))
}

#[cfg(test)]
mod tests {
    use super::*;

    /// An unreachable host must answer every request with an error (not
    /// hang), and the thread must keep serving after a failed login.
    #[test]
    fn control_link_reports_login_failure() {
        let link = ControlLink::spawn(ConnectionConfig {
            host: String::new(),
            user: "admin".to_owned(),
            password: "admin".to_owned(),
        });
        assert_eq!(link.host(), "");
        let ports = link
            .request_ports()
            .recv_timeout(Duration::from_secs(10))
            .expect("control thread replied");
        assert!(ports.is_err());
        assert!(link.credentials().is_err());
    }
}
