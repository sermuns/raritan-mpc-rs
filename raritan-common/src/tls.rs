//! Legacy TLS 1.0 connector for Raritan switches.
//!
//! The switches only speak TLS 1.0 with weak ciphers, so verification is
//! disabled and `SECLEVEL=0` is required. Honors `SSLKEYLOGFILE` for
//! Wireshark captures.

use eyre::Result;
use openssl::ssl::{SslConnector, SslMethod, SslVerifyMode, SslVersion};

pub fn tls_connector() -> Result<SslConnector> {
    let mut builder = SslConnector::builder(SslMethod::tls())?;
    builder.set_cipher_list("DEFAULT:@SECLEVEL=0")?;
    builder.set_min_proto_version(Some(SslVersion::TLS1))?;
    builder.set_max_proto_version(Some(SslVersion::TLS1))?;
    builder.set_verify(SslVerifyMode::NONE);
    if let Ok(path) = std::env::var("SSLKEYLOGFILE") {
        builder.set_keylog_callback(move |_ssl, line| {
            use std::io::Write as _;
            if let Ok(mut file) = std::fs::OpenOptions::new()
                .create(true)
                .append(true)
                .open(&path)
            {
                let _ = writeln!(file, "{line}");
            }
        });
    }
    Ok(builder.build())
}
