use eframe::egui;
use raritan_rdm::{Port, RdmClient};

const HOST: &str = "192.168.42.10";
const USER: &str = "admin";
const PASSWORD: &str = "admin";

fn main() -> eframe::Result {
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
            },
            Err(error) => Self {
                ports: Vec::new(),
                selected_port: None,
                error: Some(format!("{error:?}")),
            },
        }
    }
}

impl eframe::App for MpcApp {
    fn ui(&mut self, ui: &mut egui::Ui, _frame: &mut eframe::Frame) {
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
                for (index, port) in self.ports.iter().enumerate() {
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
                    }
                }
            });

            ui.separator();
            ui.vertical_centered(|ui| {
                if let Some(index) = self.selected_port {
                    let port = &self.ports[index];
                    ui.heading(port.name.as_deref().unwrap_or("Selected port"));
                    ui.label(format!("Port ID: {}", port.id));
                    ui.label("Framebuffer transport is the next connection layer.");
                    ui.separator();
                    ui.label("No framebuffer connected yet");
                } else {
                    ui.heading("Select a KVM port");
                }
            });
        });
    }
}
