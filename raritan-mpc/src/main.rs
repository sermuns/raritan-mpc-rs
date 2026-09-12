use eframe::egui;
use raritan_rdm::{Port, RdmClient};
use raritan_rfb::{Framebuffer, PixelFormat, VideoCommand, eric_code};
use raritan_session::{ConnectionConfig, establish_video};
use std::{
    sync::mpsc::{self, Receiver, Sender},
    thread,
    time::Duration,
};
use tracing::{error, info, warn};
use tracing_subscriber::EnvFilter;

const HOST: &str = "192.168.42.10";
const USER: &str = "admin";
const PASSWORD: &str = "admin";

fn main() -> eframe::Result {
    tracing_subscriber::fmt()
        .with_env_filter(EnvFilter::try_from_default_env().unwrap_or_else(|_| {
            EnvFilter::new("raritan_mpc=info,raritan_rdm=info,raritan_rfb=info,raritan_session=info,raritan_common=info")
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
    /// Outbound commands (key events, calibration, auto-sense) for the
    /// video worker. `None` while no video session is running.
    cmd_tx: Option<Sender<VideoCommand>>,
    texture: Option<egui::TextureHandle>,
    framebuffer_size: Option<(u16, u16)>,
    show_sidebar: bool,
    sort_by_name: bool,
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
                cmd_tx: None,
                texture: None,
                framebuffer_size: None,
                show_sidebar: true,
                sort_by_name: false,
            },
            Err(error) => Self {
                ports: Vec::new(),
                selected_port: None,
                error: Some(format!("{error:?}")),
                connection_status: "Port enumeration failed".to_owned(),
                frames: None,
                cmd_tx: None,
                texture: None,
                framebuffer_size: None,
                show_sidebar: true,
                sort_by_name: false,
            },
        }
    }

    fn start_video(&mut self, port: &Port) {
        let port_id = port.id.clone();
        info!(%port_id, "starting framebuffer worker");
        let (sender, receiver) = mpsc::channel();
        let (cmd_sender, cmd_receiver) = mpsc::channel();
        self.frames = Some(receiver);
        self.cmd_tx = Some(cmd_sender);
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
                // Short read timeout so queued key events are flushed
                // promptly even when the server sends nothing. NOTE: this
                // must stay generous — firing mid-framebuffer-update
                // discards partial bytes and desyncs the stream (20 ms
                // did exactly that over the tunnel: freeze after a few
                // frames, then "failed to fill whole buffer").
                rfb.set_read_timeout(Some(Duration::from_millis(100)))?;
                // Eric codes currently held down on the target. On exit
                // every held key is released so a dropped connection can
                // never leave the target with a key stuck down (which the
                // VM would repeat forever).
                let mut held: Vec<u16> = Vec::new();
                let result = (|| -> eyre::Result<()> {
                    loop {
                        while let Ok(command) = cmd_receiver.try_recv() {
                            match command {
                                VideoCommand::Key { eric, down } => {
                                    rfb.write_key_event(eric, down)?;
                                    if down {
                                        if !held.contains(&eric) {
                                            held.push(eric);
                                        }
                                    } else if let Some(index) =
                                        held.iter().position(|held| *held == eric)
                                    {
                                        held.swap_remove(index);
                                    }
                                }
                                VideoCommand::VideoSettings { setting, value } => {
                                    info!(setting, value, "sending video-settings event");
                                    rfb.write_video_settings_event(setting, value)?;
                                }
                            }
                        }
                        let update = match rfb.read_message() {
                            Ok(update) => update,
                            Err(error) if is_read_timeout(&error) => continue,
                            Err(error) => return Err(error),
                        };
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
                for eric in held {
                    let _ = rfb.write_key_event(eric, false);
                }
                result
            })();
            if let Err(error) = result {
                error!(%error, "framebuffer worker stopped");
                let _ = sender.send(FrameMessage::Error(format!("{error:?}")));
            }
        });
    }

    /// Display order for the port list: enumeration (port-number) order
    /// by default, case-insensitive by name when toggled.
    fn port_order(&self) -> Vec<usize> {
        let mut order: Vec<usize> = (0..self.ports.len()).collect();
        if self.sort_by_name {
            order.sort_by(|&a, &b| {
                let name = |index: usize| {
                    self.ports[index]
                        .name
                        .as_deref()
                        .unwrap_or(&self.ports[index].id)
                        .to_lowercase()
                };
                name(a).cmp(&name(b))
            });
        }
        order
    }
}

/// True when the error is just the worker's read timeout expiring
/// (no server data within the poll window), as opposed to a real failure.
fn is_read_timeout(error: &eyre::Report) -> bool {
    error.downcast_ref::<std::io::Error>().is_some_and(|io| {
        matches!(
            io.kind(),
            std::io::ErrorKind::TimedOut | std::io::ErrorKind::WouldBlock
        )
    })
}

/// Maps an egui key to the Java key code + location the en_US Eric table
/// in `raritan-rfb` expects. Shifted US symbols (e.g. `?`, `!`, `:`)
/// map to their physical base key; the Shift press itself is forwarded
/// as a separate event, exactly like the Java client sends it.
fn java_key(key: egui::Key) -> Option<(i32, i32)> {
    use egui::Key as K;
    let mapped = match key {
        K::ArrowUp => (38, 1),
        K::ArrowDown => (40, 1),
        K::ArrowLeft => (37, 1),
        K::ArrowRight => (39, 1),
        K::Escape => (27, 1),
        K::Tab => (9, 1),
        K::Backspace => (8, 1),
        K::Enter => (10, 1),
        K::Space => (32, 1),
        K::Insert => (155, 1),
        K::Delete => (127, 1),
        K::Home => (36, 1),
        K::End => (35, 1),
        K::PageUp => (33, 1),
        K::PageDown => (34, 1),
        K::ShiftLeft => (16, 2),
        K::ShiftRight => (16, 3),
        K::ControlLeft => (17, 2),
        K::ControlRight => (17, 3),
        K::AltLeft => (18, 2),
        K::AltRight => (18, 3),
        K::SuperLeft => (524, 2),
        K::SuperRight => (268, 3),
        K::Minus => (45, 1),
        K::Equals | K::Plus => (61, 1),
        K::OpenBracket | K::OpenCurlyBracket => (91, 1),
        K::CloseBracket | K::CloseCurlyBracket => (93, 1),
        K::Backslash | K::Pipe => (92, 1),
        K::Semicolon | K::Colon => (59, 1),
        K::Quote => (222, 1),
        K::Comma => (44, 1),
        K::Period => (46, 1),
        K::Slash | K::Questionmark => (47, 1),
        K::Backtick => (192, 1),
        K::Num0 => (48, 1),
        K::Num1 | K::Exclamationmark => (49, 1),
        K::Num2 => (50, 1),
        K::Num3 => (51, 1),
        K::Num4 => (52, 1),
        K::Num5 => (53, 1),
        K::Num6 => (54, 1),
        K::Num7 => (55, 1),
        K::Num8 => (56, 1),
        K::Num9 => (57, 1),
        K::A => (65, 1),
        K::B => (66, 1),
        K::C => (67, 1),
        K::D => (68, 1),
        K::E => (69, 1),
        K::F => (70, 1),
        K::G => (71, 1),
        K::H => (72, 1),
        K::I => (73, 1),
        K::J => (74, 1),
        K::K => (75, 1),
        K::L => (76, 1),
        K::M => (77, 1),
        K::N => (78, 1),
        K::O => (79, 1),
        K::P => (80, 1),
        K::Q => (81, 1),
        K::R => (82, 1),
        K::S => (83, 1),
        K::T => (84, 1),
        K::U => (85, 1),
        K::V => (86, 1),
        K::W => (87, 1),
        K::X => (88, 1),
        K::Y => (89, 1),
        K::Z => (90, 1),
        K::F1 => (112, 1),
        K::F2 => (113, 1),
        K::F3 => (114, 1),
        K::F4 => (115, 1),
        K::F5 => (116, 1),
        K::F6 => (117, 1),
        K::F7 => (118, 1),
        K::F8 => (119, 1),
        K::F9 => (120, 1),
        K::F10 => (121, 1),
        K::F11 => (122, 1),
        K::F12 => (123, 1),
        _ => return None,
    };
    Some(mapped)
}

impl eframe::App for MpcApp {
    fn ui(&mut self, ui: &mut egui::Ui, _frame: &mut eframe::Frame) {
        if let Some(receiver) = &self.frames {
            // Drain the backlog but upload only the freshest frame: when
            // the network outruns the UI, intermediate frames would each
            // cost a texture upload for a pixel that is never displayed.
            // Status/Error messages are still all processed in order.
            let mut latest_frame: Option<(u16, u16, Vec<u8>)> = None;
            while let Ok(message) = receiver.try_recv() {
                match message {
                    FrameMessage::Frame {
                        width,
                        height,
                        rgba,
                    } => {
                        latest_frame = Some((width, height, rgba));
                    }
                    FrameMessage::Status(status) => {
                        self.connection_status = status;
                    }
                    FrameMessage::Error(error) => {
                        warn!(%error, "framebuffer error received by GUI");
                        self.error = Some(error);
                        // Worker is gone; stop queueing commands for it.
                        self.cmd_tx = None;
                    }
                }
            }
            if let Some((width, height, rgba)) = latest_frame {
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
        }
        ui.ctx()
            .request_repaint_after(std::time::Duration::from_millis(33));

        // Forward physical key presses to the KVM target while a video
        // session runs. Text events are deliberately ignored (the Key
        // press/release pair already carries what the target needs).
        if let Some(tx) = &self.cmd_tx {
            let keys: Vec<VideoCommand> = ui.ctx().input(|input| {
                input
                    .events
                    .iter()
                    .filter_map(|event| {
                        if let egui::Event::Key { key, pressed, .. } = event {
                            java_key(*key)
                                .and_then(|(code, location)| eric_code(code, location))
                                .map(|eric| VideoCommand::Key {
                                    eric,
                                    down: *pressed,
                                })
                        } else {
                            None
                        }
                    })
                    .collect()
            });
            for command in keys {
                let _ = tx.send(command);
            }
        }
        egui::Panel::top("header").show(ui, |ui| {
            ui.horizontal(|ui| {
                ui.toggle_value(&mut self.show_sidebar, "Ports")
                    .on_hover_text("Show/hide the port sidebar");
                ui.heading("Raritan MPC");
                ui.label(HOST);
                if let Some(error) = &self.error {
                    ui.colored_label(egui::Color32::RED, error);
                }
            });
        });

        if self.show_sidebar {
            egui::Panel::left("ports")
                .default_size(220.0)
                .show(ui, |ui| {
                    ui.horizontal(|ui| {
                        ui.heading("KVM ports");
                        // Right-align the sort toggle.
                        ui.with_layout(egui::Layout::right_to_left(egui::Align::Center), |ui| {
                            ui.toggle_value(&mut self.sort_by_name, "A–Z")
                                .on_hover_text("Sort by name instead of port number");
                        });
                    });
                    let order = self.port_order();
                    egui::ScrollArea::vertical().show(ui, |ui| {
                        for index in order {
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
                                // Drop focus so Space/Enter go to the KVM
                                // target instead of re-activating this label.
                                if let Some(id) = ui.ctx().memory(|mem| mem.focused()) {
                                    ui.ctx().memory_mut(|mem| mem.surrender_focus(id));
                                }
                            }
                        }
                    });
                });
        }

        egui::CentralPanel::default().show(ui, |ui| {
            if let Some(index) = self.selected_port {
                let port = &self.ports[index];
                ui.horizontal(|ui| {
                    ui.heading(port.name.as_deref().unwrap_or("Selected port"));
                    ui.label(format!("Port ID: {}", port.id));
                    // Manual video actions, mirroring the Java client's
                    // Calibrate Color / Auto Sense menu entries. The
                    // switch auto-calibrates on its own; these are for
                    // when the picture needs a nudge.
                    if self.cmd_tx.is_some() {
                        // Right-align the buttons.
                        ui.with_layout(egui::Layout::right_to_left(egui::Align::Center), |ui| {
                            if ui.button("Disconnect").clicked() {
                                // Drop our ends of the channels: the next
                                // worker send fails and the thread exits
                                // (bounded by the pump's read timeout),
                                // closing the connection.
                                self.frames = None;
                                self.cmd_tx = None;
                                self.texture = None;
                                self.framebuffer_size = None;
                                self.selected_port = None;
                                self.connection_status = "Disconnected".to_owned();
                            }
                            if ui.button("Auto sense").clicked()
                                && let Some(tx) = &self.cmd_tx
                            {
                                let _ = tx.send(VideoCommand::VideoSettings {
                                    setting: 18,
                                    value: 0,
                                });
                            }
                            if ui.button("Calibrate color").clicked()
                                && let Some(tx) = &self.cmd_tx
                            {
                                let _ = tx.send(VideoCommand::VideoSettings {
                                    setting: 19,
                                    value: 0,
                                });
                            }
                        });
                    }
                });
                ui.separator();
                if let Some(texture) = &self.texture {
                    // Scale the framebuffer to fit the remaining panel
                    // area, preserving aspect ratio.
                    let native = texture.size_vec2();
                    let avail = ui.available_size();
                    if avail.x > 0.0 && avail.y > 0.0 {
                        let scale = (avail.x / native.x).min(avail.y / native.y);
                        if scale.is_finite() && scale > 0.0 {
                            ui.centered_and_justified(|ui| {
                                ui.image((texture.id(), native * scale));
                            });
                        }
                    }
                } else {
                    ui.centered_and_justified(|ui| {
                        ui.label(&self.connection_status);
                    });
                }
            } else {
                ui.centered_and_justified(|ui| {
                    ui.heading("Select a KVM port");
                });
            }
        });
    }
}
