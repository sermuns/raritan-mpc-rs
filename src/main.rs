use eyre::OptionExt;
use openssl::ssl::{SslConnector, SslMethod, SslVerifyMode, SslVersion};
use std::{
    io::{Read, Write},
    net::TcpStream,
};

const USER_AGENT: &str = concat!(env!("CARGO_PKG_NAME"), "/", env!("CARGO_PKG_VERSION"));

fn main() -> eyre::Result<()> {
    let host = "192.168.42.10";
    let port = 443;

    let mut builder = SslConnector::builder(SslMethod::tls())?;

    builder.set_cipher_list("DEFAULT:@SECLEVEL=0")?;
    builder.set_min_proto_version(Some(SslVersion::TLS1))?;
    builder.set_verify(SslVerifyMode::NONE);

    let connector = builder.build();

    let stream = TcpStream::connect((host, port))?;

    let mut tls_stream = connector.connect(host, stream)?;

    let path = "/home.asp";
    let request = format!(
        "GET {path} HTTP/1.0\r\n\
         Host: {host}\r\n\
         Authorization: Basic YWRtaW46YWRtaW4=\r\n\
         User-Agent: {USER_AGENT}\r\n\r\n",
    );

    tls_stream.write_all(request.as_bytes())?;
    tls_stream.flush()?;

    let mut response_bytes = Vec::new();
    tls_stream.read_to_end(&mut response_bytes)?;

    let response_text = str::from_utf8(&response_bytes)?;

    let (headers, body) = response_text
        .split_once("\r\n\r\n")
        .ok_or_eyre("no headers in response!?")?;
    println!("--- HEADERS ---\n{}", headers);
    println!("\n--- BODY ---\n{}", body);

    Ok(())
}
