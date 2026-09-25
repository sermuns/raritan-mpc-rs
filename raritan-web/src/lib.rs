//! Minimal HTTPS client for the switch `WebUI` (port 443).
//!
//! The Java client has no port-rename feature and RDM has no `Set` verb,
//! so renames go through the same form the `WebUI` posts to
//! (`POST /port_edit.asp`, `application/x-www-form-urlencoded`).
//! The switch only speaks TLS 1.0 with weak ciphers — same setup as the
//! RDM channel ([`raritan_common::tls_connector`]) — and its `GoAhead` server aborts
//! the TLS session abruptly after a POST response, so callers must verify
//! state-changing requests by re-reading state, not from the response.

use eyre::{Context, OptionExt, bail};
use openssl::ssl::SslStream;
use percent_encoding::{AsciiSet, NON_ALPHANUMERIC, utf8_percent_encode};
use std::{
    io::{Read, Write},
    net::{TcpStream, ToSocketAddrs},
    thread,
    time::Duration,
};

/// `WebUI` HTTPS port (TLS 1.0, same weak ciphers as RDM).
pub const DEFAULT_WEB_PORT: u16 = 443;
/// Name of the `WebUI` session cookie set by `POST /auth.asp`.
pub const SESSION_COOKIE: &str = "pp_session_id";

const CONNECT_TIMEOUT: Duration = Duration::from_secs(10);
const READ_TIMEOUT: Duration = Duration::from_secs(25);
/// Refusals clear after a short wait (the switch serialises TLS
/// handshakes); sleeps between reconnect attempts.
const RECONNECT_BACKOFF: [Duration; 3] = [
    Duration::from_secs(10),
    Duration::from_secs(20),
    Duration::from_secs(30),
];
const MAX_HEADER_BYTES: usize = 32 * 1024;
const MAX_BODY_BYTES: usize = 4 * 1024 * 1024;

/// `application/x-www-form-urlencoded` serialisation: like the WHATWG
/// serializer, ASCII alphanumerics plus `* - . _` go verbatim, space
/// becomes `+`, everything else is `%XX`-escaped.
pub fn form_urlencode(pairs: &[(impl AsRef<str>, impl AsRef<str>)]) -> String {
    pairs
        .iter()
        .map(|(key, value)| {
            format!(
                "{}={}",
                percent_encode(key.as_ref()),
                percent_encode(value.as_ref())
            )
        })
        .collect::<Vec<_>>()
        .join("&")
}

/// Bytes the form serializer leaves verbatim beyond alphanumerics.
const FORM_UNRESERVED: &AsciiSet = &NON_ALPHANUMERIC
    .remove(b'*')
    .remove(b'-')
    .remove(b'.')
    .remove(b'_');

fn percent_encode(value: &str) -> String {
    utf8_percent_encode(value, FORM_UNRESERVED)
        .to_string()
        .replace("%20", "+")
}

#[derive(Debug)]
pub struct HttpResponse {
    pub status: u16,
    pub headers: Vec<(String, String)>,
    pub body: Vec<u8>,
}

impl HttpResponse {
    pub fn header(&self, name: &str) -> Option<&str> {
        self.headers
            .iter()
            .find(|(key, _)| key.eq_ignore_ascii_case(name))
            .map(|(_, value)| value.as_str())
    }
}

/// Splits a raw HTTP/1.x response into head and body. Pure helper so the
/// wire quirks (abrupt close, missing `Content-Length`) stay testable.
fn parse_http_response(buffer: &[u8]) -> eyre::Result<HttpResponse> {
    let head_len = buffer
        .windows(4)
        .position(|window| window == b"\r\n\r\n")
        .ok_or_eyre("HTTP response without header terminator")?;
    let head = std::str::from_utf8(&buffer[..head_len]).wrap_err("HTTP head is not UTF-8")?;
    let mut lines = head.lines();
    let status_line = lines.next().ok_or_eyre("empty HTTP response")?;
    let status: u16 = status_line
        .split_whitespace()
        .nth(1)
        .and_then(|code| code.parse().ok())
        .ok_or_eyre(format!("unparseable HTTP status line: {status_line:?}"))?;
    let mut headers = Vec::new();
    for line in lines {
        if let Some((key, value)) = line.split_once(':') {
            headers.push((key.trim().to_owned(), value.trim().to_owned()));
        }
    }
    Ok(HttpResponse {
        status,
        headers,
        body: buffer[head_len + 4..].to_vec(),
    })
}

/// Reads until EOF, tolerating the switch's abrupt TLS close: once the
/// headers are complete, a truncated tail still yields a response.
fn read_response(stream: &mut SslStream<TcpStream>) -> eyre::Result<HttpResponse> {
    let mut buffer = Vec::new();
    let mut chunk = [0_u8; 8192];
    loop {
        if buffer.len() > MAX_HEADER_BYTES + MAX_BODY_BYTES {
            bail!("HTTP response exceeded size cap");
        }
        match stream.read(&mut chunk) {
            Ok(0) => break,
            Ok(read) => buffer.extend_from_slice(&chunk[..read]),
            Err(error)
                if buffer.windows(4).any(|window| window == b"\r\n\r\n")
                    && matches!(
                        error.kind(),
                        std::io::ErrorKind::UnexpectedEof
                            | std::io::ErrorKind::ConnectionReset
                            | std::io::ErrorKind::ConnectionAborted
                    ) =>
            {
                break;
            }
            Err(error) => return Err(error).wrap_err("reading `WebUI` response"),
        }
    }
    parse_http_response(&buffer)
}

fn connect_tls(host: &str, port: u16) -> eyre::Result<SslStream<TcpStream>> {
    let address = (host, port)
        .to_socket_addrs()
        .wrap_err_with(|| format!("resolving {host}:{port}"))?
        .next()
        .ok_or_eyre(format!("no address for {host}:{port}"))?;
    let mut last_error: Option<eyre::Report> = None;
    for (attempt, backoff) in std::iter::once(&Duration::ZERO)
        .chain(RECONNECT_BACKOFF.iter())
        .enumerate()
    {
        if *backoff > Duration::ZERO {
            thread::sleep(*backoff);
        }
        let dial = || -> eyre::Result<SslStream<TcpStream>> {
            let socket = TcpStream::connect_timeout(&address, CONNECT_TIMEOUT)?;
            socket.set_nodelay(true)?;
            socket.set_read_timeout(Some(READ_TIMEOUT))?;
            raritan_common::tls_connector()?
                .connect(host, socket)
                .wrap_err("`WebUI` TLS handshake failed")
        };
        match dial() {
            Ok(stream) => return Ok(stream),
            Err(error) => {
                last_error = Some(error);
                if attempt >= RECONNECT_BACKOFF.len() {
                    break;
                }
            }
        }
    }
    match last_error {
        Some(error) => Err(error),
        None => eyre::bail!("no connection attempts made"),
    }
}

/// Authenticated `WebUI` session (cookie jar holding [`SESSION_COOKIE`]).
pub struct WebSession {
    host: String,
    port: u16,
    cookie: Option<String>,
}

impl WebSession {
    pub fn new(host: &str) -> Self {
        Self {
            host: host.to_owned(),
            port: DEFAULT_WEB_PORT,
            cookie: None,
        }
    }

    /// Logs into the `WebUI` (`POST /auth.asp`); stores [`SESSION_COOKIE`].
    /// Success is a redirect carrying the cookie; a 200 means the login
    /// page was shown again (bad credentials).
    pub fn login(&mut self, user: &str, password: &str) -> eyre::Result<()> {
        let body = form_urlencode(&[
            ("login", user),
            ("password", password),
            ("is_dotnet", "0"),
            ("is_standalone_client", "0"),
            ("action_login", "Login"),
        ]);
        let response = self
            .post_raw("/auth.asp", &body)
            .wrap_err("`WebUI` login request failed")?;
        if response.status == 200 {
            bail!("`WebUI` login failed (login page returned; check credentials)");
        }
        let cookie = response
            .headers
            .iter()
            .filter(|(key, _)| key.eq_ignore_ascii_case("set-cookie"))
            .filter_map(|(_, value)| value.split(';').next())
            .find_map(|pair| {
                let (key, value) = pair.split_once('=')?;
                (key.trim() == SESSION_COOKIE).then(|| value.trim().to_owned())
            })
            .ok_or_eyre("`WebUI` login did not set a session cookie")?;
        self.cookie = Some(cookie);
        Ok(())
    }

    /// Posts a pre-encoded `application/x-www-form-urlencoded` body.
    /// The response is best-effort (see [`read_response`]); verify writes
    /// by re-reading state.
    pub fn post_raw(&self, path: &str, body: &str) -> eyre::Result<HttpResponse> {
        let mut stream = connect_tls(&self.host, self.port)?;
        let mut request = format!(
            "POST {path} HTTP/1.1\r\nHost: {}\r\nConnection: close\r\nContent-Type: application/x-www-form-urlencoded\r\nContent-Length: {}\r\n",
            self.host,
            body.len(),
        );
        if let Some(cookie) = &self.cookie {
            use std::fmt::Write as _;
            let _ = write!(request, "Cookie: {SESSION_COOKIE}={cookie}\r\n");
        }
        request.push_str("\r\n");
        stream
            .write_all(request.as_bytes())
            .wrap_err("writing `WebUI` request head")?;
        stream
            .write_all(body.as_bytes())
            .wrap_err("writing `WebUI` request body")?;
        read_response(&mut stream)
            .wrap_err_with(|| format!("`WebUI` POST {path} got no usable response"))
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn urlencodes_form_bodies_like_the_browser() {
        assert_eq!(
            form_urlencode(&[("FV_0_portedit", "Auriel"), ("_port_id", "P_1_6")]),
            "FV_0_portedit=Auriel&_port_id=P_1_6"
        );
        assert_eq!(
            form_urlencode(&[("blade_hotkey_key", "NumLock + NumLock + SlotNumber")]),
            "blade_hotkey_key=NumLock+%2B+NumLock+%2B+SlotNumber"
        );
        assert_eq!(
            form_urlencode(&[("__templates__", " portedit applytootherports")]),
            "__templates__=+portedit+applytootherports"
        );
        assert_eq!(
            form_urlencode(&[("_usb_profiles", "0,")]),
            "_usb_profiles=0%2C"
        );
    }

    #[test]
    fn parses_redirect_with_cookie() {
        let raw = b"HTTP/1.1 302 Redirect\r\nSet-Cookie: pp_session_id=ABC123; path=/\r\nLocation: https://h/cookiecheck.asp\r\nContent-Length: 0\r\n\r\n";
        let response = parse_http_response(raw).unwrap();
        assert_eq!(response.status, 302);
        assert_eq!(
            response.header("set-cookie"),
            Some("pp_session_id=ABC123; path=/")
        );
        assert_eq!(response.body, [] as [u8; 0]);
    }

    #[test]
    fn rejects_headless_responses() {
        assert!(parse_http_response(b"HTTP/1.1 200 OK\r\nno-terminator").is_err());
        assert!(parse_http_response(b"garbage").is_err());
    }
}
