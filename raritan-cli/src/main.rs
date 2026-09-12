use clap::Parser;
use raritan_rdm::RdmClient;
use raritan_rfb::{Framebuffer, PixelFormat, RfbStream};
use std::collections::HashSet;
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
    let port = ports
        .iter()
        .find(|port| {
            port.index.is_some_and(|index| index.to_string() == selector)
                || port.id == selector
                || port.id.ends_with(&selector)
                || port.name.as_deref() == Some(&selector)
                || port
                    .name
                    .as_deref()
                    .is_some_and(|name| name.contains(&selector))
        })
        .ok_or_else(|| color_eyre::eyre::eyre!("no port matches {selector:?}"))?;
    info!(id = %port.id, name = ?port.name, "selected port for headless capture");

    let port_id = port.id.clone();

    // Fresh RDM session for the video flow; the listing session above
    // already fetched the RDM session credentials. NOTE: the TR
    // video-stream grant (cmd 55) is intentionally skipped: the switch
    // never answers it, while RFB carries its own KVM-switch event and
    // streams fine without it.
    let (session_id, session_key) = client
        .session_credentials()
        .map(|(id, key)| (id.to_owned(), key.to_owned()))?;
    let rdm = RdmClient::connect(&args.host, &args.user, &args.password)?;
    // The Java client always holds the RDMEvent referral session while
    // connecting video; keep it open for the whole capture.
    if let Err(error) = rdm.open_event_session(&session_id, &session_key) {
        warn!(%error, "RDM event session failed; continuing without it");
    }

    let mut rfb = RfbStream::connect_raritan(&args.host, &session_id, &session_key, &port_id)?;
    let (width, height) = rfb
        .framebuffer_size()
        .ok_or_else(|| color_eyre::eyre::eyre!("no framebuffer dimensions"))?;
    info!(width, height, "handshake complete");
    let format = PixelFormat::RGB565;
    let mut framebuffer = Framebuffer::new(width, height);
    let mut seen_encodings = HashSet::new();
    let mut total_rects = 0usize;
    for i in 0..args.frames {
        let update = rfb.read_message()?;
        for rect in &update.rectangles {
            seen_encodings.insert(rect.encoding);
        }
        total_rects += update.rectangles.len();
        framebuffer.apply_update(&update, format)?;
        info!(
            update = i,
            flags = update.flags,
            rects = update.rectangles.len(),
            total_rects,
            "framebuffer update"
        );
        rfb.request_framebuffer_update(true)?;
    }
    info!(?seen_encodings, total_rects, "capture finished");

    let mut ppm = format!("P6\n{} {}\n255\n", width, height).into_bytes();
    let (pixels, _remainder) = framebuffer.rgba.as_chunks::<4>();
    for pixel in pixels {
        ppm.extend_from_slice(&pixel[1..4]);
    }
    std::fs::write(&args.out, &ppm)?;
    info!(path = %args.out, "wrote frame");
    if framebuffer.rgba.iter().all(|b| *b == 0) {
        warn!("framebuffer is completely black");
    }
    Ok(())
}
