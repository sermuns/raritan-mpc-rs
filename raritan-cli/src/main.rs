use clap::Parser;
use raritan_rdm::RdmClient;

const DEFAULT_USER: &str = "admin";
const DEFAULT_PASSWORD: &str = "admin";

#[derive(Parser)]
#[command(version)]
struct Args {
    host: String,
}

fn main() -> color_eyre::Result<()> {
    color_eyre::install()?;
    let Args { host } = Args::parse();
    let mut client = RdmClient::connect(&host, DEFAULT_USER, DEFAULT_PASSWORD)?;

    for port in client.enumerate_ports()? {
        println!(
            "{:>3} {:<28} {:<12} id={:?} status={:?} available={:?}",
            port.index.unwrap_or(0),
            port.name.as_deref().unwrap_or(""),
            port.r#type.as_deref().unwrap_or(""),
            port.id,
            port.status,
            port.stat_available,
        );
    }

    Ok(())
}
