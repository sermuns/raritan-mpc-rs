use eframe::egui;
use raritan_rdm::{Port, RdmClient};
use raritan_rfb::{Framebuffer, PixelFormat};
use raritan_session::{ConnectionConfig, establish_video};
use std::{
    sync::mpsc::{self, Receiver},
    thread,
};
use tracing::{error, info, warn};
use tracing_subscriber::EnvFilter;

const HOST: &str = "192.168.42.10";
const USER: &str = "admin";
const PASSWORD: &str = "admin";

fn main() -> eframe::Result {
    tracing_subscriber::fmt()
        .with_env_filter(EnvFilter::try_from_default_env().unwrap_or_else(|_| {
            EnvFilter::new("raritan_mpc=debug,raritan_rdm=debug,raritan_rfb=trace")
        }))
        .with_target(false)
        .init();
    info!("starting Raritan MPC");
    let native_options = eframe::NativeOptions {
        renderer: eframe::Renderer::Glow,
        ..Default::default()
    };

    eframe::run_native(
        "Raritan MPC",
        native_options,
        Box::new(|creation_context| Ok(Box::new(MpcApp::new(creation_context)))),
    )
}

struct MpcApp {
    ports: Vec<Port>,
    selected_port: Option<usize>,
    error: Option<String>,
    connection_status: String,
    frames: Option<Receiver<FrameMessage>>,
    texture: Option<egui::TextureHandle>,
    framebuffer_size: Option<(u16, u16)>,
}

enum FrameMessage {
    Frame {
        width: u16,
        height: u16,
        rgba: Vec<u8>,
    },
    Status(String),
    Error(String),
}

impl MpcApp {
    fn new(_creation_context: &eframe::CreationContext<'_>) -> Self {
        match RdmClient::connect(HOST, USER, PASSWORD)
            .and_then(|mut client| client.enumerate_ports())
        {
            Ok(ports) => Self {
                ports: ports
                    .into_iter()
                    .filter(|port| port.status == Some(1))
                    .collect(),
                selected_port: None,
                error: None,
                connection_status: "Ready".to_owned(),
                frames: None,
                texture: None,
                framebuffer_size: None,
            },
            Err(error) => Self {
                ports: Vec::new(),
                selected_port: None,
                error: Some(format!("{error:?}")),
                connection_status: "Port enumeration failed".to_owned(),
                frames: None,
                texture: None,
                framebuffer_size: None,
            },
        }
    }

    fn start_video(&mut self, port: &Port) {
        let port_id = port.id.clone();
        info!(%port_id, "starting framebuffer worker");
        let (sender, receiver) = mpsc::channel();
        self.frames = Some(receiver);
        self.texture = None;
        self.framebuffer_size = None;
        self.error = None;
        self.connection_status = "Starting framebuffer worker".to_owned();
        thread::spawn(move || {
            let result = (|| -> eyre::Result<()> {
                let status = |message: &str| {
                    let _ = sender.send(FrameMessage::Status(message.to_owned()));
                    info!(%message, "framebuffer connection stage");
                };
                status("Connecting to RDM");
                info!(%port_id, "connecting video session");
                // NOTE: the TR video-stream grant (cmd 55) is intentionally
                // skipped: the switch never answers it, while RFB carries
                // its own KVM-switch event and streams fine without it.
                // `establish_video` also holds the RDM event session.
                let config = ConnectionConfig::new(HOST, USER, PASSWORD);
                let mut rfb = establish_video(&config, &port_id)?;
                status("RFB connected; waiting for framebuffer");
                let (width, height) = rfb
                    .framebuffer_size()
                    .ok_or_else(|| eyre::eyre!("RFB did not provide framebuffer dimensions"))?;
                let format = PixelFormat::RGB565;
                let mut framebuffer = Framebuffer::new(width, height);
                loop {
                    let update = rfb.read_message()?;
                    info!(
                        rectangles = update.rectangles.len(),
                        flags = update.flags,
                        "decoded framebuffer update"
                    );
                    framebuffer.apply_update(&update, format)?;
                    sender.send(FrameMessage::Frame {
                        width,
                        height,
                        rgba: framebuffer.rgba.clone(),
                    })?;
                    rfb.request_framebuffer_update(true)?;
                }
            })();
            if let Err(error) = result {
                error!(%error, "framebuffer worker stopped");
                let _ = sender.send(FrameMessage::Error(format!("{error:?}")));
            }
        });
    }
}

impl eframe::App for MpcApp {
    fn ui(&mut self, ui: &mut egui::Ui, _frame: &mut eframe::Frame) {
        if let Some(receiver) = &self.frames {
            while let Ok(message) = receiver.try_recv() {
                match message {
                    FrameMessage::Frame {
                        width,
                        height,
                        rgba,
                    } => {
                        self.framebuffer_size = Some((width, height));
                        let image = egui::ColorImage::from_rgba_unmultiplied(
                            [width as usize, height as usize],
                            &rgba,
                        );
                        if let Some(texture) = &mut self.texture {
                            texture.set(image, egui::TextureOptions::LINEAR);
                        } else {
                            self.texture = Some(ui.ctx().load_texture(
                                "framebuffer",
                                image,
                                egui::TextureOptions::LINEAR,
                            ));
                        }
                    }
                    FrameMessage::Status(status) => {
                        self.connection_status = status;
                    }
                    FrameMessage::Error(error) => {
                        warn!(%error, "framebuffer error received by GUI");
                        self.error = Some(error)
                    }
                }
            }
        }
        ui.ctx()
            .request_repaint_after(std::time::Duration::from_millis(33));
        ui.heading("Raritan MPC");
        ui.label(HOST);
        ui.separator();

        if let Some(error) = &self.error {
            ui.colored_label(egui::Color32::RED, error);
        }

        let available_width = ui.available_width();
        ui.horizontal(|ui| {
            ui.set_width(available_width * 0.3);
            ui.vertical(|ui| {
                ui.heading("KVM ports");
                for index in 0..self.ports.len() {
                    let port = &self.ports[index];
                    let label = format!(
                        "{}  {}",
                        port.index
                            .map_or_else(|| "?".to_owned(), |value| value.to_string()),
                        port.name.as_deref().unwrap_or(&port.id),
                    );
                    if ui
                        .selectable_label(self.selected_port == Some(index), label)
                        .clicked()
                    {
                        self.selected_port = Some(index);
                        let selected_port = port.clone();
                        self.start_video(&selected_port);
                    }
                }
            });

            ui.separator();
            ui.vertical_centered(|ui| {
                if let Some(index) = self.selected_port {
                    let port = &self.ports[index];
                    ui.heading(port.name.as_deref().unwrap_or("Selected port"));
                    ui.label(format!("Port ID: {}", port.id));
                    ui.separator();
                    if let Some(texture) = &self.texture {
                        let size = texture.size_vec2();
                        ui.image((texture.id(), size));
                    } else {
                        ui.label(&self.connection_status);
                    }
                } else {
                    ui.heading("Select a KVM port");
                }
            });
        });
    }
}
