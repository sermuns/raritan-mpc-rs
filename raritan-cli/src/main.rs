use clap::Parser;
use raritan_rdm::RdmClient;
use raritan_session::{ConnectionConfig, capture_frames, encode_ppm, establish_video, find_port, is_black};
use tracing::{info, warn};

const DEFAULT_USER: &str = "admin";
const DEFAULT_PASSWORD: &str = "admin";

#[derive(Parser)]
#[command(version)]
struct Args {
    host: String,
    #[arg(long, default_value = DEFAULT_USER)]
    user: String,
    #[arg(long, default_value = DEFAULT_PASSWORD)]
    password: String,
    /// Capture video headlessly from a port (index, id, or name substring)
    /// instead of just listing ports.
    #[arg(long)]
    video: Option<String>,
    /// Number of framebuffer updates to read before writing the image.
    #[arg(long, default_value_t = 60)]
    frames: usize,
    /// Only open an unauthenticated channel and probe TR, then exit.
    #[arg(long, default_value_t = false)]
    ping_pre_auth: bool,
    /// Where to write the captured frame as PPM.
    #[arg(long, default_value = "/tmp/opencode/frame.ppm")]
    out: String,
    /// Write the raw RDM inventory XML to this file for inspection.
    #[arg(long)]
    dump_xml: Option<String>,
}

fn main() -> color_eyre::Result<()> {
    color_eyre::install()?;
    tracing_subscriber::fmt()
        .with_env_filter(
            tracing_subscriber::EnvFilter::try_from_default_env().unwrap_or_else(|_| {
                tracing_subscriber::EnvFilter::new("raritan_mpc=debug,raritan_rdm=debug,raritan_rfb=debug")
            }),
        )
        .with_target(false)
        .init();
    let args = Args::parse();
    if args.ping_pre_auth {
        let mut client = RdmClient::connect_unauthed(&args.host)?;
        let pinged = client.probe_ping();
        info!(pinged, "pre-auth TR ping probe finished");
        return Ok(());
    }
    let mut client = RdmClient::connect(&args.host, &args.user, &args.password)?;

    if let Some(path) = &args.dump_xml {
        dump_inventory_xml(&mut client, path)?;
    }

    let ports = client.enumerate_ports()?;
    for port in &ports {
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

    let Some(selector) = args.video else {
        return Ok(());
    };
    let port = find_port(&ports, &selector)?;
    info!(id = %port.id, name = ?port.name, "selected port for headless capture");
    let port_id = port.id.clone();
    drop(client);

    // NOTE: the TR video-stream grant (cmd 55) is intentionally skipped:
    // the switch never answers it, while RFB streams fine without it.
    // `establish_video` also holds the RDMEvent session like Java does.
    let config = ConnectionConfig::new(&args.host, &args.user, &args.password);
    let mut rfb = establish_video(&config, &port_id)?;
    let (framebuffer, seen_encodings, total_rects) = capture_frames(&mut rfb, args.frames)?;
    info!(?seen_encodings, total_rects, "capture finished");

    std::fs::write(&args.out, encode_ppm(&framebuffer))?;
    info!(path = %args.out, "wrote frame");
    if is_black(&framebuffer) {
        warn!("framebuffer is completely black");
    }
    Ok(())
}

fn dump_inventory_xml(client: &mut RdmClient, path: &str) -> color_eyre::Result<()> {
    let mut xml = client.raw_inventory()?;
    for device in ["D_000d5d065096", "D_000d5d065095"] {
        xml.push_str(&format!("\n<!-- {device} -->\n"));
        xml.push_str(&client.raw_device(device)?);
    }
    for query in [
        "<Database><Get><Select>/System/Device[@Type='IP-Reach']/DeviceCapabilities</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>",
        "<Database><Get><Select>/System/Device</Select><Nodes> Device </Nodes><SubNodes> Name SerialNo @id @Type @Model @BM @BaseDevice @CalibrationSpeed @ProductCode</SubNodes></Get></Database>",
    ] {
        xml.push_str(&format!("\n<!-- {query} -->\n"));
        xml.push_str(&client.database_query(query)?);
    }
    std::fs::write(path, xml)?;
    Ok(())
}
