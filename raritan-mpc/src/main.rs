use clap::Parser;
use eframe::egui;
use raritan_rdm::{Port, SwitchInfo};
use raritan_rfb::{Framebuffer, PixelFormat, VideoCommand, eric_code};
use raritan_session::{
    Cancelled, ConnectionConfig, ControlLink, PortsResult, RfbPinger, connect_video,
};
use std::{
    sync::{
        Arc,
        atomic::{AtomicBool, Ordering},
        mpsc::{self, Receiver, Sender},
    },
    thread,
    time::{Duration, Instant},
};
use tracing::{debug, error, info, warn};
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
const INPUT_POLL_INTERVAL: Duration = Duration::from_millis(100);
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
    info!("starting Raritan MPC");
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
#[allow(clippy::struct_excessive_bools)]
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

/// Marks the worker as connecting until dropped, so every exit path of a
/// session attempt (including `?` returns) clears the flag.
struct ConnectingGuard<'a>(&'a WorkerFlags);

impl<'a> ConnectingGuard<'a> {
    fn new(worker: &'a WorkerFlags) -> Self {
        worker.connecting.store(true, Ordering::Relaxed);
        Self(worker)
    }
}

impl Drop for ConnectingGuard<'_> {
    fn drop(&mut self) {
        self.0.connecting.store(false, Ordering::Relaxed);
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
    }

    fn start_video(&mut self, port: &Port) {
        let port_id = port.id.clone();
        info!(%port_id, "starting framebuffer worker");
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
                        info!(%error, "video worker superseded; exiting");
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
                            info!(%message, wait_secs = wait.as_secs());
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
        info!(host = %self.host, "refreshing port list");
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
                info!(count = ports.len(), "port list refreshed");
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
    let _guard = ConnectingGuard::new(worker);
    let status = |message: &str| {
        let _ = sender.send(FrameMessage::Status(message.to_owned()));
        info!(%message, "framebuffer connection stage");
    };
    status("Requesting session credentials");
    info!(%port_id, "connecting video session");
    // NOTE: the TR video-stream grant (cmd 55) is skipped (never
    // answered; RFB streams without it). The control thread holds the
    // login and the RDM event session.
    let creds = link.credentials().map_err(|error| eyre::eyre!(error))?;
    status("Connecting RFB");
    let mut rfb = connect_video(link.host(), &creds, port_id, &|| worker.cancelled())?;
    worker.connecting.store(false, Ordering::Relaxed);
    status("RFB connected; waiting for framebuffer");
    let rfb = &mut rfb;
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
    let mut dropped_frames: u64 = 0;
    let mut pinger = RfbPinger::new();
    let result = (|| -> eyre::Result<()> {
        loop {
            // Keeps the video channel alive (Java `PingTimer`); the control
            // thread keeps the RDM session alive.
            pinger.tick(rfb)?;
            // Drain all queued input first; every event goes through, never dropped.
            loop {
                match cmd_receiver.try_recv() {
                    Ok(command) => send_command(rfb, command, &mut held)?,
                    Err(mpsc::TryRecvError::Empty) => break,
                    Err(mpsc::TryRecvError::Disconnected) => {
                        // GUI dropped cmd_tx: exit instead of idling forever.
                        info!("video worker: command channel closed; exiting");
                        return Ok(());
                    }
                }
            }
            // Idle poll: loop back to flush input and observe channel closes.
            if !rfb.wait_for_message(INPUT_POLL_INTERVAL)? {
                continue;
            }
            let Some(update) = rfb.read_one_message()? else {
                continue;
            };
            // Late 128 format changes resize the stream: recreate the pixel
            // buffer or rects clip and misalign.
            if let Some((width, height)) = rfb.framebuffer_size()
                && (framebuffer.width != width || framebuffer.height != height)
            {
                info!(width, height, "framebuffer resized; recreating buffer");
                framebuffer = Framebuffer::try_new(width, height)?;
            }
            debug!(
                rectangles = update.rectangles.len(),
                flags = update.flags,
                "decoded framebuffer update"
            );
            framebuffer.apply_update(&update, format)?;
            // Never block the pump on a slow GUI: drop frames, keep latency
            // low. Only a closed receiver exits the worker.
            match sender.try_send(FrameMessage::Frame {
                width: framebuffer.width,
                height: framebuffer.height,
                rgba: framebuffer.rgba.clone(),
            }) {
                Ok(()) => {}
                Err(mpsc::TrySendError::Full(_)) => {
                    dropped_frames += 1;
                    tracing::trace!(
                        dropped_frames,
                        "dropped video frame; GUI behind, keeping latency low"
                    );
                }
                Err(mpsc::TrySendError::Disconnected(_)) => {
                    info!("video worker: frame receiver closed; exiting");
                    return Ok(());
                }
            }
            rfb.request_framebuffer_update(true)?;
        }
    })();
    for eric in held {
        let _ = rfb.write_key_event(eric, false);
    }
    // Release held mouse buttons too.
    let _ = rfb.write_pointer_event(0, 0, 0, 0);
    result
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
            info!(setting, value, "sending video-settings event");
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

/// Ctrl+Alt+Delete via the `en_US` Eric table: presses in order, releases reversed.
fn cad_sequence() -> Vec<VideoCommand> {
    let held: Vec<u16> = [(17, 2), (18, 2), (127, 1)]
        .into_iter()
        .filter_map(|(code, location)| eric_code(code, location))
        .collect();
    if held.len() != 3 {
        return Vec::new();
    }
    let mut sequence: Vec<VideoCommand> = held
        .iter()
        .map(|&eric| VideoCommand::Key { eric, down: true })
        .collect();
    sequence.extend(
        held.iter()
            .rev()
            .map(|&eric| VideoCommand::Key { eric, down: false }),
    );
    sequence
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
    #[allow(clippy::too_many_lines)]
    fn ui(&mut self, ui: &mut egui::Ui, _frame: &mut eframe::Frame) {
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
            }
        }
        ui.ctx()
            .request_repaint_after(std::time::Duration::from_millis(33));

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
        if self.cmd_tx.is_some() {
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
                                                "{}  👥 {name} (in use)",
                                                port.display_index(),
                                            )
                                        } else {
                                            format!("{}  {}", port.display_index(), name)
                                        };
                                        let selected = self.selected_port == Some(index);
                                        let selected_port = port.clone();
                                        // In-use ports stay clickable; the 👥 marker,
                                        // suffix, and tooltip show someone is on them.
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
                            if ui.button("× Disconnect").clicked() {
                                // Drop our channel ends; the worker exits on its next failed send.
                                self.disconnect_video();
                                "Disconnected".clone_into(&mut self.connection_status);
                            }
                            // Restart the video session on this port (same
                            // path as re-clicking it in the sidebar).
                            if ui
                                .button("↻ Reconnect")
                                .on_hover_text("Restart the video session on this port")
                                .clicked()
                                && let Some(port) = self.ports.get(index).cloned()
                            {
                                self.start_video(&port);
                            }
                            if ui.button("⌨ Ctrl+Alt+Del").clicked() {
                                self.confirm_cad = true;
                            }
                            // Manual video actions, mirroring the Java client
                            // (setting 18 = auto-sense, 19 = calibration).
                            for (label, setting, waiting, hover) in [
                                (
                                    "◎ Auto-adjust video",
                                    18,
                                    "Auto-sensing video…",
                                    "Re-detect the target's video signal and tune sampling",
                                ),
                                (
                                    "🎨 Calibrate color",
                                    19,
                                    "Calibrating color…",
                                    "Re-tune the color gains and offsets",
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
