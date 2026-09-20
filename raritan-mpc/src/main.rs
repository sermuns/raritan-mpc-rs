use clap::Parser;
use eframe::egui;
use nucleo::{
    Config as NucleoConfig, Matcher, Utf32Str,
    pattern::{CaseMatching, Normalization, Pattern},
};
use raritan_rdm::{Port, SwitchInfo};
use raritan_rfb::{Framebuffer, PixelFormat, VideoCommand, eric_code};
use raritan_session::{Cancelled, ConnectionConfig, ControlLink, PortsResult, connect_video};
use std::{
    sync::{
        Arc,
        atomic::{AtomicBool, Ordering},
        mpsc::{self, Receiver, Sender},
    },
    thread,
    time::{Duration, Instant},
};
use tracing::{debug, error, warn};
use tracing_subscriber::EnvFilter;

/// Default switch address; editable in the UI and persisted.
const DEFAULT_HOST: &str = "";
/// Factory login (fields stay disabled unless "custom credentials" is ticked).
const DEFAULT_USER: &str = "admin";
const DEFAULT_PASSWORD: &str = "admin";

/// Persisted UI preferences, stored as one RON blob under [`eframe::APP_KEY`].
/// Missing fields fall back to [`PersistedState::default`], so old blobs
/// stay loadable when fields are added.
#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(default)]
struct PersistedState {
    host: String,
    user: String,
    password: String,
    custom_credentials: bool,
    auto_refresh: bool,
}

impl Default for PersistedState {
    fn default() -> Self {
        Self {
            host: DEFAULT_HOST.to_owned(),
            user: DEFAULT_USER.to_owned(),
            password: DEFAULT_PASSWORD.to_owned(),
            custom_credentials: false,
            auto_refresh: true,
        }
    }
}

/// Loads the persisted preferences: the RON blob first, then the legacy
/// individual string keys (pre-blob installs), then defaults.
fn load_persisted(storage: Option<&dyn eframe::Storage>) -> PersistedState {
    let Some(storage) = storage else {
        return PersistedState::default();
    };
    if let Some(state) = eframe::get_value(storage, eframe::APP_KEY) {
        return state;
    }
    let mut state = PersistedState::default();
    if let Some(host) = storage.get_string("host") {
        state.host = host;
    }
    if let Some(user) = storage.get_string("username") {
        state.user = user;
    }
    if let Some(password) = storage.get_string("password") {
        state.password = password;
    }
    state.custom_credentials = storage
        .get_string("custom_credentials")
        .is_some_and(|value| value == "1");
    state.auto_refresh = storage
        .get_string("auto_refresh")
        .is_none_or(|value| value == "1");
    state
}

/// Video session attempts per port selection (initial try + backoff retries).
const MAX_VIDEO_ATTEMPTS: u32 = 4;
/// How often the pump checks for queued input while the switch is idle.
/// Short (the Java client writes input straight from its event thread with
/// no queue at all): every keypress/mouse event otherwise waits up to this
/// long in the command channel before reaching the wire. Measured pickup
/// delay on loopback: ~45 ms avg at 100 ms, ~3 ms avg at 5 ms.
const INPUT_POLL_INTERVAL: Duration = Duration::from_millis(5);
/// Longest stall tolerated inside one RFB message before the session is
/// treated as dead (the Java TR socket uses 174 s).
const RFB_BODY_TIMEOUT: Duration = Duration::from_mins(1);

/// Port-list auto-refresh interval. The Java client refreshes only on
/// demand; polling keeps busy markers fresh. It is a few small queries on
/// the held control connection, not a login, so the switch sees no
/// session churn from it.
const PORT_AUTO_REFRESH_INTERVAL: Duration = Duration::from_secs(30);

/// Command-line overrides for this run: `--help`/`--version` exit early;
/// the flags win without overwriting the persisted sidebar values.
#[derive(Parser)]
#[command(version, about = "Raritan MPC graphical KVM client")]
struct Args {
    /// Switch address.
    #[arg(long)]
    host: Option<String>,
    /// Login username.
    #[arg(long)]
    user: Option<String>,
    /// Login password.
    #[arg(long)]
    password: Option<String>,
}

fn main() -> eframe::Result {
    tracing_subscriber::fmt()
        .with_env_filter(EnvFilter::try_from_default_env().unwrap_or_else(|_| {
            EnvFilter::new("raritan_mpc=info,raritan_rdm=info,raritan_rfb=info,raritan_session=info,raritan_common=info")
        }))
        .with_target(false)
        .init();
    debug!("starting Raritan MPC");
    let args = Args::parse();
    let mut viewport = egui::ViewportBuilder::default();
    if let Some(icon) = load_app_icon() {
        viewport = viewport.with_icon(icon);
    } else {
        warn!("embedded app icon is unreadable; using toolkit default");
    }
    let native_options = eframe::NativeOptions {
        renderer: eframe::Renderer::Glow,
        viewport,
        ..Default::default()
    };

    eframe::run_native(
        "Raritan MPC",
        native_options,
        Box::new(move |creation_context| {
            Ok(Box::new(MpcApp::new(
                creation_context,
                args.host,
                args.user,
                args.password,
            )))
        }),
    )
}

// Flat egui state: grouping the flags into sub-structs would add
// indirection at every use site for no gain.
#[expect(clippy::struct_excessive_bools)]
struct MpcApp {
    /// Switch address (sidebar-editable, persisted).
    host: String,
    user: String,
    password: String,
    /// Tick to edit user/password; unticked means factory admin/admin.
    custom_credentials: bool,
    ports: Vec<Port>,
    selected_port: Option<usize>,
    error: Option<String>,
    connection_status: String,
    frames: Option<Receiver<FrameMessage>>,
    /// Outbound commands for the video worker; `None` with no session.
    cmd_tx: Option<Sender<VideoCommand>>,
    /// Shared with the current video worker; `None` with no session.
    worker: Option<Arc<WorkerFlags>>,
    /// The one RDM login (port enumeration, video credentials, keepalive).
    /// Created on demand from the sidebar fields; replaced by Connect.
    control: Option<ControlLink>,
    texture: Option<egui::TextureHandle>,
    framebuffer_size: Option<(u16, u16)>,
    show_sidebar: bool,
    sort_order: SortOrder,
    /// In-flight port-list refresh; the buttons wait on it.
    port_refresh: Option<Receiver<PortsResult>>,
    /// When the last refresh launched (manual or automatic); drives auto-refresh.
    last_refresh: Instant,
    /// Whether the port list re-enumerates by itself (persisted).
    auto_refresh: bool,
    /// Switch identity from `<CSC_Info>`; `None` until first enumeration.
    switch_info: Option<SwitchInfo>,
    confirm_cad: bool,
    paste_open: bool,
    paste_text: String,
    tty_fn: u8,
    /// Last frame's image rect, for mapping pointer to target pixels.
    viewport: Option<egui::Rect>,
    /// Held mouse buttons (RFB mask).
    mouse_buttons: u8,
    /// Last pointer state sent; moves only go out on change.
    last_pointer: Option<(u8, u16, u16)>,
    /// Sub-notch wheel remainder.
    wheel_remainder: f32,
    /// Manual video action awaiting resumed frames; cleared by the next
    /// decoded frame (the switch pauses the stream while working).
    pending_video_action: Option<String>,
    palette_open: bool,
    palette_query: String,
    palette_selected: usize,
    is_fullscreen: bool,
}

/// GUI ↔ video-worker flags. `cancel` stops a superseded connect between
/// stages; `connecting` holds off the port-list refresh (another TLS
/// login) while the worker's own handshakes are in flight.
#[derive(Default)]
struct WorkerFlags {
    cancel: AtomicBool,
    connecting: AtomicBool,
}

impl WorkerFlags {
    fn cancelled(&self) -> bool {
        self.cancel.load(Ordering::Relaxed)
    }
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Default)]
enum SortOrder {
    #[default]
    PortNumber,
    Name,
}

impl SortOrder {
    fn label(self) -> &'static str {
        match self {
            SortOrder::PortNumber => "Port number",
            SortOrder::Name => "Name",
        }
    }
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
    fn new(
        creation_context: &eframe::CreationContext<'_>,
        host_override: Option<String>,
        user_override: Option<String>,
        password_override: Option<String>,
    ) -> Self {
        // Restored preferences; CLI flags win for this run without
        // overwriting what is stored.
        let mut persisted = load_persisted(creation_context.storage);
        if user_override.is_some() || password_override.is_some() {
            if let Some(user_flag) = user_override {
                persisted.user = user_flag;
            }
            if let Some(password_flag) = password_override {
                persisted.password = password_flag;
            }
            persisted.custom_credentials = true;
        }
        let mut app = Self {
            host: host_override.unwrap_or(persisted.host),
            user: persisted.user,
            password: persisted.password,
            custom_credentials: persisted.custom_credentials,
            ports: Vec::new(),
            selected_port: None,
            error: None,
            connection_status: "Ready".to_owned(),
            frames: None,
            cmd_tx: None,
            worker: None,
            control: None,
            texture: None,
            framebuffer_size: None,
            show_sidebar: true,
            sort_order: SortOrder::default(),
            port_refresh: None,
            last_refresh: Instant::now(),
            auto_refresh: persisted.auto_refresh,
            switch_info: None,
            confirm_cad: false,
            paste_open: false,
            paste_text: String::new(),
            tty_fn: 2,
            palette_open: false,
            palette_query: String::new(),
            palette_selected: 0,
            is_fullscreen: false,
            viewport: None,
            mouse_buttons: 0,
            last_pointer: None,
            wheel_remainder: 0.0,
            pending_video_action: None,
        };
        // Enumerate off-thread so a slow switch can't block startup.
        app.refresh_ports();
        app
    }

    /// Clears per-session video state (texture, size cache, pointer).
    fn clear_frame_state(&mut self) {
        self.texture = None;
        self.framebuffer_size = None;
        self.viewport = None;
        self.mouse_buttons = 0;
        self.last_pointer = None;
        self.wheel_remainder = 0.0;
        self.pending_video_action = None;
    }

    /// Drops the video session and its UI state.
    fn disconnect_video(&mut self) {
        self.frames = None;
        self.cmd_tx = None;
        if let Some(worker) = self.worker.take() {
            worker.cancel.store(true, Ordering::Relaxed);
        }
        self.clear_frame_state();
        self.selected_port = None;
        self.confirm_cad = false;
        self.paste_open = false;
        self.palette_open = false;
    }

    fn start_video(&mut self, port: &Port) {
        let port_id = port.id.clone();
        debug!(%port_id, "starting framebuffer worker");
        // Stop a superseded worker's connect between stages so its
        // handshakes don't compete with the new one on the switch.
        if let Some(worker) = self.worker.take() {
            worker.cancel.store(true, Ordering::Relaxed);
        }
        let worker = Arc::new(WorkerFlags::default());
        worker.connecting.store(true, Ordering::Relaxed);
        self.worker = Some(Arc::clone(&worker));
        // Restart the auto-refresh clock: its login would only slow the
        // connect down, and the busy markers are 30 s old at most anyway.
        self.last_refresh = Instant::now();
        // Bounded channel so a fast network can't pile up 3 MiB frames.
        let (sender, receiver) = mpsc::sync_channel(2);
        let (cmd_sender, cmd_receiver) = mpsc::channel();
        self.frames = Some(receiver);
        self.cmd_tx = Some(cmd_sender);
        self.clear_frame_state();
        self.error = None;
        "Starting framebuffer worker".clone_into(&mut self.connection_status);
        // Shares the one login: the worker only does the RFB handshake.
        let link = self.control_link().clone();
        thread::spawn(move || {
            // The pump only exits on error; reboots are survived inline,
            // so these retries cover hard drops (failover, network blips).
            for attempt in 1..=MAX_VIDEO_ATTEMPTS {
                if worker.cancelled() {
                    return;
                }
                // Drop stale queued input so a reconnect doesn't replay it.
                while cmd_receiver.try_recv().is_ok() {}
                match run_video_session(&link, &port_id, &worker, &sender, &cmd_receiver) {
                    Ok(()) => return,
                    Err(error) if error.downcast_ref::<Cancelled>().is_some() => {
                        debug!(%error, "video worker superseded; exiting");
                        return;
                    }
                    Err(error) => {
                        error!(%error, attempt, "video session failed");
                        if attempt < MAX_VIDEO_ATTEMPTS {
                            // 1, 2, 4 s: the switch reaps the old session
                            // within about a second, so the first retry
                            // needn't wait longer.
                            let wait = Duration::from_secs(1 << (attempt - 1).min(2));
                            let message = format!(
                                "Connection lost — retrying ({attempt}/{MAX_VIDEO_ATTEMPTS})"
                            );
                            debug!(%message, wait_secs = wait.as_secs());
                            let _ = sender.send(FrameMessage::Status(message));
                            thread::sleep(wait);
                        } else {
                            error!(%error, "framebuffer worker stopped");
                            let _ = sender.send(FrameMessage::Error(format!("{error:?}")));
                        }
                    }
                }
            }
        });
    }

    /// Port-list display order: enumeration order, or case-insensitive by name.
    fn port_order(&self) -> Vec<usize> {
        let mut order: Vec<usize> = (0..self.ports.len()).collect();
        if self.sort_order == SortOrder::Name {
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

    /// The control link, started from the sidebar fields if there is none.
    /// Later field edits apply on Connect, which replaces it.
    fn control_link(&mut self) -> &ControlLink {
        self.control.get_or_insert_with(|| {
            ControlLink::spawn(ConnectionConfig {
                host: self.host.clone(),
                user: self.user.clone(),
                password: self.password.clone(),
            })
        })
    }

    /// Re-runs port enumeration on the control thread; inert while one is
    /// in flight.
    fn refresh_ports(&mut self) {
        if self.port_refresh.is_some() {
            return;
        }
        debug!(host = %self.host, "refreshing port list");
        self.port_refresh = Some(self.control_link().request_ports());
        self.last_refresh = Instant::now();
        "Refreshing ports".clone_into(&mut self.connection_status);
        self.error = None;
    }

    /// Drops the control link (closing the login), and with it the ports.
    fn drop_switch(&mut self) {
        self.disconnect_video();
        self.control = None;
        self.port_refresh = None;
        self.ports.clear();
        self.switch_info = None;
    }

    fn reconnect(&mut self) {
        self.drop_switch();
        self.refresh_ports();
    }

    fn disconnect_switch(&mut self) {
        self.drop_switch();
        "Disconnected".clone_into(&mut self.connection_status);
    }

    /// Applies a finished refresh, keeping the selection if possible.
    fn apply_refresh(&mut self, result: PortsResult) {
        self.port_refresh = None;
        match result {
            Ok((ports, switch_info)) => {
                debug!(count = ports.len(), "port list refreshed");
                let selected_id = self
                    .selected_port
                    .and_then(|index| self.ports.get(index))
                    .map(|port| port.id.clone());
                self.ports = ports;
                self.switch_info = Some(switch_info);
                self.selected_port =
                    selected_id.and_then(|id| self.ports.iter().position(|port| port.id == id));
                "Ready".clone_into(&mut self.connection_status);
            }
            Err(error) => {
                warn!(%error, "port list refresh failed");
                self.error = Some(error);
                "Port refresh failed".clone_into(&mut self.connection_status);
            }
        }
    }

    /// Pointer commands for this frame. Button transitions always go out
    /// (falling back to the last known position so releases can't be lost);
    /// moves only when hovering and changed.
    fn pointer_commands(&mut self, ui: &egui::Ui) -> Vec<VideoCommand> {
        let Some(size) = self.framebuffer_size else {
            return Vec::new();
        };
        let (button_changes, wheel_lines, hover) = ui.ctx().input(|input| {
            let mut changes = Vec::new();
            let mut lines = 0.0;
            for event in &input.events {
                match event {
                    egui::Event::PointerButton {
                        button, pressed, ..
                    } => {
                        changes.push((pointer_bit(*button), *pressed));
                    }
                    egui::Event::MouseWheel { unit, delta, .. } => {
                        lines += match unit {
                            egui::MouseWheelUnit::Line => delta.y,
                            egui::MouseWheelUnit::Point => delta.y / 50.0,
                            egui::MouseWheelUnit::Page => delta.y * 3.0,
                        };
                    }
                    _ => {}
                }
            }
            (changes, lines, input.pointer.hover_pos())
        });
        for (bit, pressed) in &button_changes {
            if *pressed {
                self.mouse_buttons |= bit;
            } else {
                self.mouse_buttons &= !bit;
            }
        }
        let buttons = self.mouse_buttons;
        let mut commands = Vec::new();
        let mapped =
            hover.and_then(|pos| self.viewport.and_then(|rect| map_pointer(rect, size, pos)));
        let fallback = self.last_pointer.map(|(_, x, y)| (x, y)).or(Some((0, 0)));
        // Plain moves only go out when the position actually changed.
        let target: Option<(u16, u16)> = if button_changes.is_empty() {
            mapped.filter(|&(x, y)| self.last_pointer != Some((buttons, x, y)))
        } else {
            mapped.or(fallback)
        };
        if let Some((x, y)) = target {
            commands.push(VideoCommand::Pointer {
                buttons,
                x,
                y,
                wheel: 0,
            });
            self.last_pointer = Some((buttons, x, y));
        }
        // Java counts wheel-up as negative rotation; egui reports +y.
        self.wheel_remainder += wheel_lines;
        let mut steps = self.wheel_remainder.trunc() as i32;
        if steps != 0 {
            self.wheel_remainder -= steps as f32;
            while steps != 0 {
                let step = steps.signum();
                steps -= step;
                commands.push(VideoCommand::Pointer {
                    buttons,
                    x: 0,
                    y: 0,
                    wheel: (-step) as i16 as u16,
                });
            }
        }
        commands
    }
}

/// One video session: credentials from the control link, RFB handshake,
/// then the pump loop until the first hard error.
fn run_video_session(
    link: &ControlLink,
    port_id: &str,
    worker: &WorkerFlags,
    sender: &mpsc::SyncSender<FrameMessage>,
    cmd_receiver: &mpsc::Receiver<VideoCommand>,
) -> eyre::Result<()> {
    worker.connecting.store(true, Ordering::Relaxed);
    let result = (|| -> eyre::Result<()> {
        let status = |message: &str| {
            let _ = sender.send(FrameMessage::Status(message.to_owned()));
            debug!(%message, "framebuffer connection stage");
        };
        status("Requesting session credentials");
        debug!(%port_id, "connecting video session");
        // NOTE: the TR video-stream grant (cmd 55) is skipped (never
        // answered; RFB streams without it). The control thread holds the
        // login and the RDM event session.
        let creds = link.credentials().map_err(|error| eyre::eyre!(error))?;
        status("Connecting RFB");
        let mut rfb = connect_video(link.host(), &creds, port_id, &|| worker.cancelled())?;
        worker.connecting.store(false, Ordering::Relaxed);
        status("RFB connected; waiting for framebuffer");
        let (width, height) = rfb
            .framebuffer_size()
            .ok_or_else(|| eyre::eyre!("RFB did not provide framebuffer dimensions"))?;
        let format = PixelFormat::RGB565;
        let mut framebuffer = Framebuffer::try_new(width, height)?;
        // Idle polling goes through `wait_for_message`, so this only bounds a
        // stall inside a message (a dead connection). A short timeout here used
        // to fire mid-update and desync the stream.
        rfb.set_read_timeout(Some(RFB_BODY_TIMEOUT))?;
        // Keys held on the target; all are released on exit so none stays stuck down.
        let mut held: Vec<u16> = Vec::new();
        let inner = run_pump(&mut rfb, cmd_receiver, sender, &mut framebuffer, format, &mut held);
        for eric in held {
            let _ = rfb.write_key_event(eric, false);
        }
        // Release held mouse buttons too.
        let _ = rfb.write_pointer_event(0, 0, 0, 0);
        inner
    })();
    if result.is_err() {
        worker.connecting.store(false, Ordering::Relaxed);
    }
    result
}

/// Steady-state video pump: drains GUI input, serves the RFB message flow
/// with the next update requested while each body is still on the wire
/// (Java `processFramebufferUpdate`), and forwards decoded frames.
/// Exits when a channel closes or the stream errors.
fn run_pump(
    rfb: &mut raritan_rfb::RfbStream<std::net::TcpStream>,
    cmd_receiver: &mpsc::Receiver<VideoCommand>,
    sender: &mpsc::SyncSender<FrameMessage>,
    framebuffer: &mut Framebuffer,
    format: PixelFormat,
    held: &mut Vec<u16>,
) -> eyre::Result<()> {
    let mut dropped_frames: u64 = 0;
    let mut ping_last = Instant::now();
    let mut ping_serial: u32 = 0;
        let mut drain = |rfb: &mut raritan_rfb::RfbStream<std::net::TcpStream>| -> eyre::Result<bool> {
            loop {
                match cmd_receiver.try_recv() {
                    Ok(command) => send_command(rfb, command, &mut *held)?,
                    Err(mpsc::TryRecvError::Empty) => return Ok(false),
                    Err(mpsc::TryRecvError::Disconnected) => {
                        // GUI dropped cmd_tx: exit instead of idling forever.
                        debug!("video worker: command channel closed; exiting");
                        return Ok(true);
                    }
                }
            }
        };
        // Applies one decoded update to the pixel buffer and forwards it
        // to the GUI; true when the frame receiver went away. Never blocks
        // the pump: full channels drop frames to keep latency low.
        let mut handle = |update: &raritan_rfb::FramebufferUpdate,
                          size: Option<(u16, u16)>|
         -> eyre::Result<bool> {
            // Late 128 format changes resize the stream: recreate the pixel
            // buffer or rects clip and misalign.
            if let Some((width, height)) = size
                && (framebuffer.width != width || framebuffer.height != height)
            {
                debug!(width, height, "framebuffer resized; recreating buffer");
                *framebuffer = Framebuffer::try_new(width, height)?;
            }
            debug!(
                rectangles = update.rectangles.len(),
                flags = update.flags,
                "decoded framebuffer update"
            );
            framebuffer.apply_update(update, format)?;
            match sender.try_send(FrameMessage::Frame {
                width: framebuffer.width,
                height: framebuffer.height,
                rgba: framebuffer.rgba.clone(),
            }) {
                Ok(()) => Ok(false),
                Err(mpsc::TrySendError::Full(_)) => {
                    dropped_frames += 1;
                    tracing::trace!(
                        dropped_frames,
                        "dropped video frame; GUI behind, keeping latency low"
                    );
                    Ok(false)
                }
                Err(mpsc::TrySendError::Disconnected(_)) => {
                    debug!("video worker: frame receiver closed; exiting");
                    Ok(true)
                }
            }
        };
        loop {
            if ping_last.elapsed() >= Duration::from_secs(20) {
                ping_serial = ping_serial.wrapping_add(1);
                rfb.write_ping_request(ping_serial)?;
                ping_last = Instant::now();
            }
            if drain(rfb)? {
                return Ok(());
            }
            // Idle poll: loop back to flush input and observe channel closes.
            if !rfb.wait_for_message(INPUT_POLL_INTERVAL)? {
                continue;
            }
            // Input that arrived during the wait goes out before blocking
            // on the (possibly large) update body.
            if drain(rfb)? {
                return Ok(());
            }
            match rfb.poll_incoming()? {
                raritan_rfb::Incoming::Stashed(update) => {
                    rfb.request_framebuffer_update(true)?;
                    if handle(&update, rfb.framebuffer_size())? {
                        return Ok(());
                    }
                }
                raritan_rfb::Incoming::Live(header) => {
                    // Request while the body is still on the wire, like
                    // Java's `processFramebufferUpdate` (request before
                    // reading): the server renders the next frame during
                    // this one's transfer and decode.
                    rfb.request_framebuffer_update(true)?;
                    let update = rfb.read_update_body(&header)?;
                    if handle(&update, rfb.framebuffer_size())? {
                        return Ok(());
                    }
                }
                raritan_rfb::Incoming::Handled => {}
            }
        }
    }

/// Writes one GUI command to the target, tracking held keys so they can
/// be released when the session ends.
fn send_command(
    rfb: &mut raritan_rfb::RfbStream<std::net::TcpStream>,
    command: VideoCommand,
    held: &mut Vec<u16>,
) -> eyre::Result<()> {
    match command {
        VideoCommand::Key { eric, down } => {
            rfb.write_key_event(eric, down)?;
            if down {
                if !held.contains(&eric) {
                    held.push(eric);
                }
            } else if let Some(index) = held.iter().position(|held| *held == eric) {
                held.swap_remove(index);
            }
        }
        VideoCommand::VideoSettings { setting, value } => {
            debug!(setting, value, "sending video-settings event");
            rfb.write_video_settings_event(setting, value)?;
        }
        VideoCommand::Pointer {
            buttons,
            x,
            y,
            wheel,
        } => {
            tracing::trace!(buttons, x, y, wheel, "sending pointer event");
            rfb.write_pointer_event(buttons, x, y, wheel)?;
        }
    }
    Ok(())
}

/// RFB button bit for an egui pointer button.
fn pointer_bit(button: egui::PointerButton) -> u8 {
    match button {
        egui::PointerButton::Primary => 1,
        egui::PointerButton::Middle => 2,
        egui::PointerButton::Secondary => 4,
        egui::PointerButton::Extra1 => 8,
        egui::PointerButton::Extra2 => 16,
    }
}

/// Maps a window position to target pixels. `None` outside the image.
fn map_pointer(viewport: egui::Rect, size: (u16, u16), pos: egui::Pos2) -> Option<(u16, u16)> {
    if !viewport.contains(pos) {
        return None;
    }
    let (width, height) = (f32::from(size.0), f32::from(size.1));
    if width <= 0.0 || height <= 0.0 || viewport.width() <= 0.0 || viewport.height() <= 0.0 {
        return None;
    }
    let x = (((pos.x - viewport.min.x) / viewport.width()) * width) as u16;
    let y = (((pos.y - viewport.min.y) / viewport.height()) * height) as u16;
    Some((
        x.min(size.0.saturating_sub(1)),
        y.min(size.1.saturating_sub(1)),
    ))
}

fn chord_sequence(codes: &[(i32, i32)]) -> Vec<VideoCommand> {
    let held: Vec<u16> = codes
        .iter()
        .filter_map(|&(code, loc)| eric_code(code, loc))
        .collect();
    if held.len() != codes.len() {
        return Vec::new();
    }
    let mut seq: Vec<VideoCommand> = held
        .iter()
        .map(|&eric| VideoCommand::Key { eric, down: true })
        .collect();
    seq.extend(held.iter().rev().map(|&eric| VideoCommand::Key { eric, down: false }));
    seq
}

/// Ctrl+Alt+Delete via the `en_US` Eric table: presses in order, releases reversed.
fn cad_sequence() -> Vec<VideoCommand> {
    chord_sequence(&[(17, 2), (18, 2), (127, 1)])
}

fn tty_sequence(n: u8) -> Vec<VideoCommand> {
    if !(1..=12).contains(&n) {
        return Vec::new();
    }
    chord_sequence(&[(17, 2), (18, 2), (112 + i32::from(n) - 1, 1)])
}

/// Converts pasted text to a sequence of Eric key press/release pairs,
/// holding shift across consecutive shifted characters (like the Java
/// client's paste). Unknown characters are skipped.
fn paste_sequence(text: &str) -> Vec<VideoCommand> {
    let shift_eric = eric_code(16, 2); // left shift (41)
    let mut out = Vec::new();
    let mut shift_held = false;
    for ch in text.chars() {
        let Some((code, loc, need_shift)) = paste_char_to_java(ch) else {
            continue;
        };
        if need_shift != shift_held && let Some(eric) = shift_eric {
            out.push(VideoCommand::Key {
                eric,
                down: need_shift,
            });
            shift_held = need_shift;
        }
        if let Some(eric) = eric_code(code, loc) {
            out.push(VideoCommand::Key { eric, down: true });
            out.push(VideoCommand::Key { eric, down: false });
        }
    }
    if shift_held && let Some(eric) = shift_eric {
        out.push(VideoCommand::Key { eric, down: false });
    }
    out
}

fn paste_char_to_java(ch: char) -> Option<(i32, i32, bool)> {
    // Returns (java_code, location, needs_shift). Mirrors the en_US KeyTranslator.
    match ch {
        'a'..='z' => Some((ch as i32 - 32, 1, false)), // 'a' (97) -> 65
        'A'..='Z' => Some((ch as i32, 1, true)),
        '0' => Some((48, 1, false)),
        '1' => Some((49, 1, false)),
        '2' => Some((50, 1, false)),
        '3' => Some((51, 1, false)),
        '4' => Some((52, 1, false)),
        '5' => Some((53, 1, false)),
        '6' => Some((54, 1, false)),
        '7' => Some((55, 1, false)),
        '8' => Some((56, 1, false)),
        '9' => Some((57, 1, false)),
        ' ' => Some((32, 1, false)),
        '\n' | '\r' => Some((10, 1, false)),
        '\t' => Some((9, 1, false)),
        '-' => Some((45, 1, false)),
        '_' => Some((45, 1, true)),
        '=' => Some((61, 1, false)),
        '+' => Some((61, 1, true)),
        '[' => Some((91, 1, false)),
        '{' => Some((91, 1, true)),
        ']' => Some((93, 1, false)),
        '}' => Some((93, 1, true)),
        '\\' => Some((92, 1, false)),
        '|' => Some((92, 1, true)),
        ';' => Some((59, 1, false)),
        ':' => Some((59, 1, true)),
        '\'' => Some((222, 1, false)),
        '"' => Some((222, 1, true)),
        ',' => Some((44, 1, false)),
        '<' => Some((44, 1, true)),
        '.' => Some((46, 1, false)),
        '>' => Some((46, 1, true)),
        '/' => Some((47, 1, false)),
        '?' => Some((47, 1, true)),
        '`' => Some((192, 1, false)),
        '~' => Some((192, 1, true)),
        '!' => Some((49, 1, true)),
        '@' => Some((50, 1, true)),
        '#' => Some((51, 1, true)),
        '$' => Some((52, 1, true)),
        '%' => Some((53, 1, true)),
        '^' => Some((54, 1, true)),
        '&' => Some((55, 1, true)),
        '*' => Some((56, 1, true)),
        '(' => Some((57, 1, true)),
        ')' => Some((48, 1, true)),
        _ => None,
    }
}

#[derive(Debug, Clone, Copy)]
enum PaletteAction {
    ConnectPort(usize),
    DisconnectVideo,
    ReconnectVideo,
    Paste,
    ChangeTty(u8),
    Cad,
    AutoAdjust,
    Calibrate,
    ToggleSidebar,
    RefreshPorts,
    ConnectSwitch,
    DisconnectSwitch,
}

impl MpcApp {
    fn palette_entries(&self) -> Vec<(String, PaletteAction)> {
        let mut items = Vec::new();
        // Ports — fuzzy connect (same 🔌 as KVM Connect button)
        for idx in self.port_order() {
            if let Some(port) = self.ports.get(idx) {
                let name = port.name.as_deref().unwrap_or(&port.id);
                let label = format!("🔌 Connect to port {} - {}", port.display_index(), name);
                items.push((label, PaletteAction::ConnectPort(idx)));
            }
        }
        // Video / input actions (available when video session exists, but show always with hint)
        if self.cmd_tx.is_some() {
            items.push((String::from("⌨ Paste text — type clipboard as keystrokes"), PaletteAction::Paste));
            for n in 1..=12 {
                items.push((format!("🖥 Change TTY — Ctrl+Alt+F{n}"), PaletteAction::ChangeTty(n)));
            }
            items.push((String::from("⌨ Send Ctrl+Alt+Delete"), PaletteAction::Cad));
            items.push((String::from("◎ Auto-adjust video"), PaletteAction::AutoAdjust));
            items.push((String::from("🎨 Calibrate color"), PaletteAction::Calibrate));
            items.push((String::from("× Disconnect video"), PaletteAction::DisconnectVideo));
            items.push((String::from("↻ Reconnect video"), PaletteAction::ReconnectVideo));
        }
        // Switch / sidebar
        items.push((String::from("↻ Refresh ports"), PaletteAction::RefreshPorts));
        items.push((String::from("◀ Toggle sidebar"), PaletteAction::ToggleSidebar));
        if self.switch_info.is_some() {
            items.push((String::from("× Disconnect switch"), PaletteAction::DisconnectSwitch));
        } else {
            items.push((String::from("🔌 Connect to switch"), PaletteAction::ConnectSwitch));
        }
        items
    }

    fn execute_palette(&mut self, action: PaletteAction) {
        match action {
            PaletteAction::ConnectPort(idx) => {
                if let Some(port) = self.ports.get(idx).cloned() {
                    self.selected_port = Some(idx);
                    self.start_video(&port);
                }
            }
            PaletteAction::DisconnectVideo => self.disconnect_video(),
            PaletteAction::ReconnectVideo => {
                if let Some(idx) = self.selected_port
                    && let Some(port) = self.ports.get(idx).cloned()
                {
                    self.start_video(&port);
                }
            }
            PaletteAction::Paste => {
                self.paste_text.clear();
                self.paste_open = true;
            }
            PaletteAction::ChangeTty(n) => {
                if let Some(tx) = &self.cmd_tx {
                    for cmd in tty_sequence(n) {
                        let _ = tx.send(cmd);
                    }
                }
            }
            PaletteAction::Cad => self.confirm_cad = true,
            PaletteAction::AutoAdjust => {
                if let Some(tx) = &self.cmd_tx {
                    let _ = tx.send(VideoCommand::VideoSettings { setting: 18, value: 0 });
                    self.pending_video_action = Some(String::from("Auto-sensing video…"));
                }
            }
            PaletteAction::Calibrate => {
                if let Some(tx) = &self.cmd_tx {
                    let _ = tx.send(VideoCommand::VideoSettings { setting: 19, value: 0 });
                    self.pending_video_action = Some(String::from("Calibrating color…"));
                }
            }
            PaletteAction::ToggleSidebar => self.show_sidebar = !self.show_sidebar,
            PaletteAction::RefreshPorts => self.refresh_ports(),
            PaletteAction::ConnectSwitch => self.reconnect(),
            PaletteAction::DisconnectSwitch => self.disconnect_switch(),
        }
    }
}

fn filter_palette(query: &str, items: &[(String, PaletteAction)]) -> Vec<(usize, u32)> {
    if query.trim().is_empty() {
        return items.iter().enumerate().map(|(i, _)| (i, 0)).collect();
    }
    let pattern = Pattern::parse(query, CaseMatching::Smart, Normalization::Smart);
    let mut matcher = Matcher::new(NucleoConfig::DEFAULT);
    let mut buf = Vec::new();
    let mut scored: Vec<(usize, u32)> = items
        .iter()
        .enumerate()
        .filter_map(|(idx, (label, _))| {
            let score = pattern.score(Utf32Str::new(label, &mut buf), &mut matcher)?;
            Some((idx, score))
        })
        .collect();
    scored.sort_by_key(|(_, score)| std::cmp::Reverse(*score));
    scored
}

/// Maps an egui key to the (code, location) the Eric table expects.
/// Shifted symbols map to their physical base key; Shift goes as its own event.
fn java_key(key: egui::Key) -> Option<(i32, i32)> {
    use egui::Key as K;
    Some(match key {
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
    })
}

/// Window icon from the embedded `media/icon-128.png` (`None` keeps the
/// toolkit default; a broken asset must never block startup).
fn load_app_icon() -> Option<std::sync::Arc<egui::IconData>> {
    let mut reader = png::Decoder::new(std::io::Cursor::new(include_bytes!(
        "../../media/icon-128.png"
    )))
    .read_info()
    .ok()?;
    let mut buf = vec![0; reader.output_buffer_size()?];
    let info = reader.next_frame(&mut buf).ok()?;
    let rgba = match info.color_type {
        png::ColorType::Rgba => buf,
        png::ColorType::Rgb => {
            let mut rgba = Vec::with_capacity(buf.len() / 3 * 4);
            let (pixels, _) = buf.as_chunks::<3>();
            for pixel in pixels {
                rgba.extend_from_slice(&[pixel[0], pixel[1], pixel[2], 0xFF]);
            }
            rgba
        }
        _ => return None,
    };
    Some(std::sync::Arc::new(egui::IconData {
        rgba,
        width: info.width,
        height: info.height,
    }))
}

/// Short git sha for the version stamp (via `build.rs`); `"unknown"`
/// outside git, `*` marking a dirty tree.
fn short_sha() -> String {
    match option_env!("VERGEN_GIT_SHA") {
        Some(sha) => {
            let short: String = sha.chars().take(7).collect();
            if option_env!("VERGEN_GIT_DIRTY") == Some("true") {
                format!("{short}*")
            } else {
                short
            }
        }
        None => "unknown".to_owned(),
    }
}

/// Sidebar toggle: the arrow points where the sidebar goes. Labeled
/// "Sidebar" (not "Ports") since it holds the connection too.
fn sidebar_toggle_label(expanded: bool) -> &'static str {
    if expanded {
        "◀ Sidebar"
    } else {
        "Sidebar ▶"
    }
}

fn sidebar_toggle_hover(expanded: bool) -> &'static str {
    if expanded {
        "Collapse the sidebar"
    } else {
        "Expand the sidebar"
    }
}

impl eframe::App for MpcApp {
    /// Persists the connection values as one RON blob (written on exit).
    /// Note: the password is stored in plaintext, like the CLI flags.
    fn save(&mut self, storage: &mut dyn eframe::Storage) {
        eframe::set_value(
            storage,
            eframe::APP_KEY,
            &PersistedState {
                host: self.host.clone(),
                user: self.user.clone(),
                password: self.password.clone(),
                custom_credentials: self.custom_credentials,
                auto_refresh: self.auto_refresh,
            },
        );
    }

    // Single `ui()` owns the whole frame: sidebar, top bar, and video area.
    #[expect(clippy::too_many_lines)]
    fn ui(&mut self, ui: &mut egui::Ui, _frame: &mut eframe::Frame) {
        // Command palette shortcut: F1 or Ctrl+P — fuzzy search actions/ports
        let palette_toggle = ui.ctx().input(|i| {
            i.key_pressed(egui::Key::F1) || (i.modifiers.ctrl && i.key_pressed(egui::Key::P))
        });
        if palette_toggle && !self.palette_open {
            self.palette_open = true;
            self.palette_query.clear();
            self.palette_selected = 0;
        }
        // Fullscreen toggle: F11
        if ui.ctx().input(|i| i.key_pressed(egui::Key::F11)) {
            self.is_fullscreen = !self.is_fullscreen;
            ui.ctx()
                .send_viewport_cmd(egui::ViewportCommand::Fullscreen(self.is_fullscreen));
        }
        if let Some(receiver) = &self.frames {
            // Upload only the freshest frame; skipped uploads never display.
            // Status/Error messages are still all processed.
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
                        self.cmd_tx = None;
                    }
                }
            }
            if let Some((width, height, rgba)) = latest_frame {
                // Fresh frames clear any "waiting for video" notice.
                self.pending_video_action = None;
                // Drop the texture on resize so it recreates instead of stretching.
                if self.framebuffer_size != Some((width, height)) {
                    self.texture = None;
                }
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
                // A frame arrived: repaint immediately so the next one is
                // picked up without waiting for the fallback tick below.
                // Idle (no frames) falls back to the 33 ms tick, no spin.
                ui.ctx().request_repaint();
            }
        }
        ui.ctx()
            .request_repaint_after(std::time::Duration::from_millis(33));

        // Command palette overlay — fuzzy search actions/ports via `nucleo-matcher`
        if self.palette_open {
            // Build entries once per frame (owned, no borrow of self afterwards)
            let entries = self.palette_entries();
            let mut should_close = false;
            let mut should_execute: Option<usize> = None;
            // We need filtered for rendering, but query may change inside modal.
            // Recompute inside modal after editing palette_query directly to avoid one-frame lag.
            egui::containers::Modal::new("palette_modal".into()).show(ui.ctx(), |ui| {
                if ui.ctx().input(|i| i.key_pressed(egui::Key::Escape)) {
                    should_close = true;
                }
                ui.set_width(520.0);
                ui.vertical_centered(|ui| {
                    ui.label(
                        egui::RichText::new("↑/↓ navigate | Enter: run | Esc: close | F1 / Ctrl+P: toggle")
                            .family(egui::FontFamily::Monospace)
                            .small()
                            .weak(),
                    );
                });
                let response = ui.add(
                    egui::TextEdit::singleline(&mut self.palette_query)
                        .desired_width(f32::INFINITY),
                );
                // Auto-focus when just opened
                if self.palette_query.is_empty() {
                    // Only request focus once per open; persistent focus is fine
                    response.request_focus();
                }
                // Reset selection when query changes — detect via response.changed()
                if response.changed() {
                    self.palette_selected = 0;
                }
                let filtered = filter_palette(&self.palette_query, &entries);
                if self.palette_selected >= filtered.len() {
                    self.palette_selected = filtered.len().saturating_sub(1);
                }
                // Arrow navigation
                if ui.ctx().input(|i| i.key_pressed(egui::Key::ArrowDown)) && !filtered.is_empty() {
                    self.palette_selected = (self.palette_selected + 1).min(filtered.len() - 1);
                }
                if ui.ctx().input(|i| i.key_pressed(egui::Key::ArrowUp)) && !filtered.is_empty() {
                    self.palette_selected = self.palette_selected.saturating_sub(1);
                }
                if ui.ctx().input(|i| i.key_pressed(egui::Key::Enter))
                    && let Some((idx, _)) = filtered.get(self.palette_selected)
                {
                    should_execute = Some(*idx);
                }
                ui.add_space(8.0);
                egui::ScrollArea::vertical().max_height(420.0).show(ui, |ui| {
                    if filtered.is_empty() {
                        ui.label("No matches");
                    } else {
                        for (filtered_idx, (orig_idx, _)) in filtered.iter().enumerate() {
                            let label = &entries[*orig_idx].0;
                            let selected = filtered_idx == self.palette_selected;
                            let resp = ui.selectable_label(selected, label);
                            if resp.clicked() {
                                should_execute = Some(*orig_idx);
                            }
                            if resp.hovered() {
                                self.palette_selected = filtered_idx;
                            }
                        }
                    }
                });
            });
            if should_close {
                self.palette_open = false;
            }
            if let Some(idx) = should_execute {
                self.palette_open = false;
                // Need to get action without borrowing self again after mutable borrow above.
                // Re-build entries (cheap) to avoid borrow conflict with closure.
                let entries2 = self.palette_entries();
                if let Some((_, action)) = entries2.get(idx) {
                    let act = match action {
                        PaletteAction::ConnectPort(i) => PaletteAction::ConnectPort(*i),
                        PaletteAction::DisconnectVideo => PaletteAction::DisconnectVideo,
                        PaletteAction::ReconnectVideo => PaletteAction::ReconnectVideo,
                        PaletteAction::Paste => PaletteAction::Paste,
                        PaletteAction::ChangeTty(n) => PaletteAction::ChangeTty(*n),
                        PaletteAction::Cad => PaletteAction::Cad,
                        PaletteAction::AutoAdjust => PaletteAction::AutoAdjust,
                        PaletteAction::Calibrate => PaletteAction::Calibrate,
                        PaletteAction::ToggleSidebar => PaletteAction::ToggleSidebar,
                        PaletteAction::RefreshPorts => PaletteAction::RefreshPorts,
                        PaletteAction::ConnectSwitch => PaletteAction::ConnectSwitch,
                        PaletteAction::DisconnectSwitch => PaletteAction::DisconnectSwitch,
                    };
                    self.execute_palette(act);
                }
            }
        }

        // Auto-refresh the port list so busy markers stay fresh. Only while
        // connected (`switch_info` is cleared by Disconnect and absent until
        // the first success, so this never fights those states or spams
        // errors for an empty host); in-flight refreshes stay inert.
        // Skipped while a video worker is still in its handshakes: the
        // enumeration would queue ahead of its credential request.
        let video_connecting = self
            .worker
            .as_ref()
            .is_some_and(|worker| worker.connecting.load(Ordering::Relaxed));
        if self.auto_refresh
            && self.switch_info.is_some()
            && !self.host.trim().is_empty()
            && !video_connecting
            && self.last_refresh.elapsed() >= PORT_AUTO_REFRESH_INTERVAL
        {
            self.refresh_ports();
        }

        // Forward key presses to the target while a session runs (text events
        // ignored: the press/release pair suffices; pointer events share the channel).
        // Suppressed while command palette is open so typing filters instead of reaching the KVM.
        if !self.palette_open && self.cmd_tx.is_some() {
            let mut commands: Vec<VideoCommand> = ui.ctx().input(|input| {
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
            commands.extend(self.pointer_commands(ui));
            if let Some(tx) = &self.cmd_tx {
                for command in commands {
                    let _ = tx.send(command);
                }
            }
        }

        if self.show_sidebar {
            // Apply a finished background refresh before drawing.
            if let Some(receiver) = self.port_refresh.take() {
                match receiver.try_recv() {
                    Ok(result) => self.apply_refresh(result),
                    Err(mpsc::TryRecvError::Empty) => {
                        self.port_refresh = Some(receiver);
                    }
                    Err(mpsc::TryRecvError::Disconnected) => {
                        self.port_refresh = None;
                    }
                }
            }
            // Hug the content: widest fixed row ("Sort by:" + combo ≈ 152 pt)
            // plus margins. `max_size` enforces it: egui persists panel widths
            // and the stored value wins over the default, so a stale 220 pt
            // would otherwise stick forever.
            egui::Panel::left("ports")
                .default_size(180.0)
                .max_size(180.0)
                .resizable(false)
                .show(ui, |ui| {
                    ui.heading("Switch");
                    ui.horizontal(|ui| {
                        ui.label("Host:");
                        ui.text_edit_singleline(&mut self.host);
                    });
                    // Unticking restores the factory login.
                    if ui
                        .checkbox(&mut self.custom_credentials, "Custom credentials")
                        .changed()
                        && !self.custom_credentials
                    {
                        DEFAULT_USER.clone_into(&mut self.user);
                        DEFAULT_PASSWORD.clone_into(&mut self.password);
                    }
                    ui.add_enabled(
                        self.custom_credentials,
                        egui::TextEdit::singleline(&mut self.user).hint_text("Username"),
                    );
                    ui.add_enabled(
                        self.custom_credentials,
                        egui::TextEdit::singleline(&mut self.password)
                            .password(true)
                            .hint_text("Password"),
                    );
                    ui.horizontal(|ui| {
                        let busy = self.port_refresh.is_some();
                        if ui
                            .add_enabled(!busy, egui::Button::new("🔌 Connect"))
                            .on_hover_text("Enumerate ports on the switch above")
                            .clicked()
                        {
                            self.reconnect();
                        }
                        if ui
                            .add_enabled(!busy, egui::Button::new("↻ Refresh"))
                            .on_hover_text("Re-enumerate ports on the switch")
                            .clicked()
                        {
                            self.refresh_ports();
                        }
                    });
                    ui.checkbox(&mut self.auto_refresh, "Auto-refresh ports")
                        .on_hover_text("Re-enumerate ports every 30 seconds");
                    // Connected switch identity (CSC_Info); cloned since Disconnect mutates `self`.
                    if let Some(info) = self.switch_info.clone() {
                        ui.separator();
                        ui.label(format!(
                            "{} ({})",
                            info.name.as_deref().unwrap_or("Switch"),
                            info.model.as_deref().unwrap_or("unknown model"),
                        ));
                        if let Some(version) = &info.version {
                            ui.small(format!("Firmware {version}"));
                        }
                        if let Some(address) = &info.ip_address {
                            ui.small(address);
                        }
                        if ui
                            .button("× Disconnect switch")
                            .on_hover_text("Drop the video session and forget this switch")
                            .clicked()
                        {
                            self.disconnect_switch();
                        }
                    }
                    ui.separator();
                    ui.heading("Ports");
                    ui.horizontal(|ui| {
                        ui.label("Sort by:");
                        egui::ComboBox::from_id_salt("port_sort")
                            .selected_text(self.sort_order.label())
                            .show_ui(ui, |ui| {
                                ui.selectable_value(
                                    &mut self.sort_order,
                                    SortOrder::PortNumber,
                                    SortOrder::PortNumber.label(),
                                );
                                ui.selectable_value(
                                    &mut self.sort_order,
                                    SortOrder::Name,
                                    SortOrder::Name.label(),
                                );
                            });
                    });
                    // The spinner stays mounted (invisible when idle) so the
                    // rows below never jump when a refresh starts or stops.
                    ui.horizontal(|ui| {
                        ui.add_visible(self.port_refresh.is_some(), egui::Spinner::new());
                    });
                    // Footer as a nested bottom panel so it reserves only its own height.
                    egui::Panel::bottom("version").show(ui, |ui| {
                        ui.small(format!("v{} · {}", env!("CARGO_PKG_VERSION"), short_sha()));
                    });
                    let order = self.port_order();
                    egui::ScrollArea::vertical()
                        .auto_shrink(false)
                        .show(ui, |ui| {
                            // Justified: every port row spans the full list width.
                            // Each row gets a zero-margin frame so odd rows can
                            // carry the stripe background (same shade and parity
                            // as `egui::Grid::striped`); the frame paints behind
                            // its content, so selection/hover stay on top.
                            ui.with_layout(
                                egui::Layout::top_down_justified(egui::Align::LEFT),
                                |ui| {
                                    for (row, index) in order.into_iter().enumerate() {
                                        // Owned copies up front: the closures below
                                        // take `&mut self`, so no `self.ports`
                                        // borrow may reach into them.
                                        let port = &self.ports[index];
                                        let name =
                                            port.name.as_deref().unwrap_or(&port.id).to_owned();
                                        let label = if port.is_busy() {
                                            format!(
                                                "{}  {name} (in use)",
                                                port.display_index(),
                                            )
                                        } else {
                                            format!("{}  {}", port.display_index(), name)
                                        };
                                        let selected = self.selected_port == Some(index);
                                        let selected_port = port.clone();
                                        // In-use ports stay clickable; the suffix
                                        // and tooltip show someone is on them.
                                        let busy_tip = port.busy_tooltip();
                                        // In-use rows get the theme's warning color.
                                        let warn = ui.visuals().warn_fg_color;
                                        let mut text = egui::RichText::new(label);
                                        if busy_tip.is_some() {
                                            text = text.color(warn);
                                        }
                                        let stripe = if row % 2 == 1 {
                                            ui.visuals().faint_bg_color
                                        } else {
                                            egui::Color32::TRANSPARENT
                                        };
                                        egui::Frame::new().fill(stripe).show(ui, |ui| {
                                            ui.with_layout(
                                                egui::Layout::top_down_justified(egui::Align::LEFT),
                                                |ui| {
                                                    let mut response =
                                                        ui.selectable_label(selected, text);
                                                    if let Some(tip) = busy_tip {
                                                        response = response.on_hover_text(tip);
                                                    }
                                                    if response.clicked() {
                                                        self.selected_port = Some(index);
                                                        self.start_video(&selected_port);
                                                        // Drop focus so Space/Enter go to the KVM
                                                        // target instead of re-activating this label.
                                                        if let Some(id) =
                                                            ui.ctx().memory(egui::Memory::focused)
                                                        {
                                                            ui.ctx().memory_mut(|mem| {
                                                                mem.surrender_focus(id);
                                                            });
                                                        }
                                                    }
                                                },
                                            );
                                        });
                                    }
                                },
                            );
                        });
                });
        }

        egui::Panel::bottom("hints").show(ui, |ui| {
            ui.horizontal(|ui| {
                ui.small(
                    egui::RichText::new("F1 / Ctrl+P: palette  |  F11: fullscreen  |  Esc: close")
                        .weak()
                        .monospace(),
                );
                if self.cmd_tx.is_some() {
                    ui.small(
                        egui::RichText::new("  •  ⌨ Paste  •  🖥 TTY  •  ⌨ Ctrl+Alt+Del  •  ◎/🎨 video")
                            .weak()
                            .monospace(),
                    );
                }
            });
        });

        egui::CentralPanel::default().show(ui, |ui| {
            if let Some(index) = self.selected_port {
                // Clone: the closure below mutates `self`, so it can't also borrow it.
                let port_name = self.ports[index]
                    .name
                    .clone()
                    .unwrap_or_else(|| "Selected port".to_owned());
                ui.horizontal(|ui| {
                    let expanded = self.show_sidebar;
                    ui.toggle_value(&mut self.show_sidebar, sidebar_toggle_label(expanded))
                        .on_hover_text(sidebar_toggle_hover(expanded));
                    ui.strong(&port_name);
                    if self.cmd_tx.is_some() {
                        ui.with_layout(egui::Layout::right_to_left(egui::Align::Center), |ui| {
                            // Session group (rightmost)
                            if ui.button("× Disconnect").clicked() {
                                // Drop our channel ends; the worker exits on its next failed send.
                                self.disconnect_video();
                                "Disconnected".clone_into(&mut self.connection_status);
                            }
                            if ui
                                .button("↻ Reconnect")
                                .on_hover_text("Restart the video session on this port")
                                .clicked()
                                && let Some(port) = self.ports.get(index).cloned()
                            {
                                self.start_video(&port);
                            }
                            ui.separator();
                            // Input group — keyboard actions to the remote
                            if ui.button("⌨ Ctrl+Alt+Del").clicked() {
                                self.confirm_cad = true;
                            }
                            egui::ComboBox::from_id_salt("tty_combo")
                                .selected_text("🖥 Change TTY")
                                .show_ui(ui, |ui| {
                                    for n in 1..=12 {
                                        let label = format!("F{n}  Ctrl+Alt+F{n}");
                                        if ui.selectable_value(&mut self.tty_fn, n, &label).clicked()
                                            && let Some(tx) = &self.cmd_tx
                                        {
                                            for command in tty_sequence(n) {
                                                let _ = tx.send(command);
                                            }
                                        }
                                    }
                                });
                            if ui.button("⌨ Paste text").on_hover_text("Paste text as keystrokes to the selected port").clicked() {
                                self.paste_text.clear();
                                self.paste_open = true;
                            }
                            ui.separator();
                            // Video group — tuning the picture
                            for (label, setting, waiting, hover) in [
                                (
                                    "🎨 Calibrate color",
                                    19,
                                    "Calibrating color…",
                                    "Re-tune the color gains and offsets",
                                ),
                                (
                                    "◎ Auto-adjust video",
                                    18,
                                    "Auto-sensing video…",
                                    "Re-detect the target's video signal and tune sampling",
                                ),
                            ] {
                                if ui.button(label).on_hover_text(hover).clicked()
                                    && let Some(tx) = &self.cmd_tx
                                {
                                    let _ =
                                        tx.send(VideoCommand::VideoSettings { setting, value: 0 });
                                    // The switch pauses frames while working; notice clears on resume.
                                    self.pending_video_action = Some(waiting.to_owned());
                                }
                            }
                        });
                    }
                });
                if let Some(error) = &self.error {
                    ui.colored_label(egui::Color32::RED, error);
                }
                if let Some(notice) = &self.pending_video_action {
                    ui.horizontal(|ui| {
                        ui.spinner();
                        ui.label(format!("{notice} Waiting for video to resume…"));
                    });
                }
                ui.separator();
                // Confirm the Secure Attention Sequence before sending.
                if self.confirm_cad {
                    egui::containers::Modal::new("cad_confirm".into()).show(ui.ctx(), |ui| {
                        if ui.ctx().input(|i| i.key_pressed(egui::Key::Escape)) {
                            self.confirm_cad = false;
                        }
                        ui.heading("Send Ctrl+Alt+Delete?");
                        ui.label("Send Ctrl+Alt+Delete to the selected port?");
                        ui.horizontal(|ui| {
                            if ui.button("✔ Yes").clicked() {
                                if let Some(tx) = &self.cmd_tx {
                                    for command in cad_sequence() {
                                        let _ = tx.send(command);
                                    }
                                }
                                self.confirm_cad = false;
                            }
                            if ui.button("× No").clicked() {
                                self.confirm_cad = false;
                            }
                        });
                    });
                }
                if self.paste_open {
                    egui::containers::Modal::new("paste_modal".into()).show(ui.ctx(), |ui| {
                        if ui.ctx().input(|i| i.key_pressed(egui::Key::Escape)) {
                            self.paste_open = false;
                        }
                        ui.set_width(420.0);
                        ui.heading("Paste text as keystrokes");
                        ui.label("Text will be typed into the selected port, character by character.");
                        let edit = egui::TextEdit::multiline(&mut self.paste_text)
                            .hint_text("Paste text here…")
                            .desired_width(f32::INFINITY)
                            .desired_rows(6);
                        ui.add(edit);
                        ui.horizontal(|ui| {
                            let can_send = !self.paste_text.is_empty() && self.cmd_tx.is_some();
                            if ui.add_enabled(can_send, egui::Button::new("✔ Type it")).clicked() {
                                if let Some(tx) = &self.cmd_tx {
                                    for command in paste_sequence(&self.paste_text) {
                                        let _ = tx.send(command);
                                    }
                                }
                                self.paste_open = false;
                            }
                            if ui.button("× Cancel").clicked() {
                                self.paste_open = false;
                            }
                        });
                    });
                }
                if let Some(texture) = &self.texture {
                    // Fit the framebuffer to the panel, preserving aspect ratio.
                    let native = texture.size_vec2();
                    let texture_id = texture.id();
                    let avail = ui.available_size();
                    if avail.x > 0.0 && avail.y > 0.0 {
                        let scale = (avail.x / native.x).min(avail.y / native.y);
                        if scale.is_finite() && scale > 0.0 {
                            let rect = ui
                                .centered_and_justified(|ui| {
                                    ui.image((texture_id, native * scale)).rect
                                })
                                .inner;
                            // Remember the image rect for pointer mapping.
                            self.viewport = Some(rect);
                        }
                    }
                } else {
                    ui.centered_and_justified(|ui| {
                        ui.label(&self.connection_status);
                    });
                }
            } else {
                ui.horizontal(|ui| {
                    let expanded = self.show_sidebar;
                    ui.toggle_value(&mut self.show_sidebar, sidebar_toggle_label(expanded))
                        .on_hover_text(sidebar_toggle_hover(expanded));
                    if let Some(error) = &self.error {
                        ui.colored_label(egui::Color32::RED, error);
                    }
                });
                ui.separator();
                ui.centered_and_justified(|ui| {
                    ui.heading("Select a KVM port");
                });
            }
        });
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use eframe::Storage;

    #[test]
    fn embedded_icon_decodes_to_rgba() {
        let icon = load_app_icon().expect("media/icon-128.png must decode");
        assert_eq!((icon.width, icon.height), (128, 128));
        assert_eq!(icon.rgba.len(), 128 * 128 * 4);
    }

    #[derive(Default)]
    struct MemStorage {
        strings: std::collections::HashMap<String, String>,
    }

    impl eframe::Storage for MemStorage {
        fn get_string(&self, key: &str) -> Option<String> {
            self.strings.get(key).cloned()
        }
        fn set_string(&mut self, key: &str, value: String) {
            self.strings.insert(key.to_owned(), value);
        }
        fn remove_string(&mut self, key: &str) {
            self.strings.remove(key);
        }
        fn flush(&mut self) {}
    }

    #[test]
    fn persisted_state_roundtrips_through_storage() {
        let mut storage = MemStorage::default();
        eframe::set_value(
            &mut storage,
            eframe::APP_KEY,
            &PersistedState {
                host: "switch".to_owned(),
                user: "admin".to_owned(),
                password: "secret".to_owned(),
                custom_credentials: true,
                auto_refresh: false,
            },
        );
        let loaded: PersistedState =
            eframe::get_value(&storage, eframe::APP_KEY).expect("blob must decode");
        assert_eq!(loaded.host, "switch");
        assert_eq!(loaded.password, "secret");
        assert!(loaded.custom_credentials);
        assert!(!loaded.auto_refresh);
    }

    #[test]
    fn legacy_string_keys_migrate() {
        let mut storage = MemStorage::default();
        storage.set_string("host", "old-switch".to_owned());
        storage.set_string("custom_credentials", "1".to_owned());
        let loaded = load_persisted(Some(&storage));
        assert_eq!(loaded.host, "old-switch");
        assert!(loaded.custom_credentials);
        assert!(loaded.auto_refresh);
    }
}
