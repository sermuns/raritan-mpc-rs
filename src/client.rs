use crate::{
    model::{SessionResponse, parse_ports},
    protocol::{display_xml, escape_xml, read_frame, write_frame},
};
use eyre::{Context, OptionExt, Result, bail};
use openssl::ssl::{SslConnector, SslMethod, SslStream, SslVerifyMode, SslVersion};
use quick_xml::de::from_str;
use std::net::TcpStream;

pub struct RdmClient {
    stream: SslStream<TcpStream>,
}

impl RdmClient {
    pub fn connect(host: impl Into<String>, user: &str, password: &str) -> Result<Self> {
        let host = host.into();
        let mut stream = TcpStream::connect((&*host, 5000))
            .wrap_err_with(|| format!("connecting to {host}:5000"))?;

        let greeting = read_frame(&mut stream)?;
        if !greeting.starts_with(b"<CSC") {
            bail!("unexpected CSC greeting: {}", display_xml(&greeting));
        }
        write_frame(&mut stream, "<CSC_Ack/>")?;

        let info = read_frame(&mut stream)?;
        if !info.starts_with(b"<CSC_Info") {
            bail!("unexpected CSC info: {}", display_xml(&info));
        }
        write_frame(&mut stream, r#"<CSC_Start_Session ProtocolID="RDM"/>"#)?;

        let mut tls = tls_connector()?.connect(&host, stream)?;
        write_frame(
            &mut tls,
            &format!(
                r#"<CSC_Auth UserName="{}" Password="{}"/>"#,
                escape_xml(user),
                escape_xml(password)
            ),
        )?;
        let auth = read_frame(&mut tls)?;
        if !auth.starts_with(b"<CSC_Pass") {
            bail!("authentication failed: {}", display_xml(&auth));
        }

        Ok(Self { stream: tls })
    }

    pub fn database_query(&mut self, request: &str) -> Result<String> {
        write_frame(&mut self.stream, request)?;
        let response = read_frame(&mut self.stream)?;
        Ok(String::from_utf8(response)?
            .trim_end_matches('\0')
            .to_owned())
    }

    pub fn enumerate_ports(&mut self) -> Result<Vec<crate::Port>> {
        let session: SessionResponse =
            from_str(&self.database_query("<Session><GetSessionID/></Session>")?)?;
        session
            .get_session_id
            .session_id
            .ok_or_eyre("response did not contain SessionID")?;

        let inventory = self.database_query(
            "<Database><Get><Select>/System/Device[@Type='IP-Reach']/Port</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>",
        )?;
        let mut ports = parse_ports(&inventory)?;
        let connections: Vec<String> = ports
            .iter()
            .filter_map(|port| port.connection.clone())
            .collect();

        for device_id in connections {
            let response = self.database_query(&format!(
                "<Database><Get><Select>/System/Device[@id='{}']</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>",
                escape_xml(&device_id)
            ))?;
            ports.extend(parse_ports(&response)?);
        }

        Ok(ports
            .into_iter()
            .filter(|port| port.class.as_deref() == Some("KVM"))
            .collect())
    }
}

fn tls_connector() -> Result<SslConnector> {
    let mut builder = SslConnector::builder(SslMethod::tls())?;
    builder.set_cipher_list("DEFAULT:@SECLEVEL=0")?;
    builder.set_min_proto_version(Some(SslVersion::TLS1))?;
    builder.set_max_proto_version(Some(SslVersion::TLS1))?;
    builder.set_verify(SslVerifyMode::NONE);
    Ok(builder.build())
}
