use eyre::Result;
use raritan_mpc_rs::RdmClient;

const DEFAULT_USER: &str = "admin";
const DEFAULT_PASSWORD: &str = "admin";

fn main() -> Result<()> {
    let host = std::env::args()
        .nth(1)
        .ok_or_else(|| eyre::eyre!("usage: raritan-mpc-rs <host>"))?;
    let mut client = RdmClient::connect(&host, DEFAULT_USER, DEFAULT_PASSWORD)?;

    for port in client.enumerate_ports()? {
        println!(
            "{:>3} {:<28} {:<12} id={} status={} available={}",
            port.index.unwrap_or(0),
            port.name.as_deref().unwrap_or(""),
            port.r#type.as_deref().unwrap_or(""),
            port.id,
            port.status.unwrap_or(-1),
            port.stat_available.unwrap_or(-1),
        );
    }
    Ok(())
}
