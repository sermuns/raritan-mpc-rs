use eyre::{Result, WrapErr, bail, eyre};
use flate2::read::ZlibDecoder;
use openssl::ssl::{SslConnector, SslMethod, SslStream, SslVerifyMode, SslVersion};
use std::collections::VecDeque;
use std::io::{Read, Write};
use std::net::TcpStream;
use tracing::{debug, info, trace, warn};

// Server message types (nn.pp.rccore.impl.rfb.RfbConstants).
const FRAMEBUFFER_UPDATE: u8 = 0;
const USER_NOTIFICATION: u8 = 3;
const PORT_LIST: u8 = 4;
const SERVER_INIT: u8 = 5;
const UTF8_STRING: u8 = 7;
const VIDEO_SETTINGS_S2C: u8 = 8;
const KEYBOARD_LAYOUT: u8 = 9;
const OSD_STATE: u8 = 16;
const VIDEO_QUALITY_S2C: u8 = 17;
const CONNECTION_PARAMETERS: u8 = 18;
const ACK_PIXEL_FORMAT: u8 = 19;
const AUTH_CAPS: u8 = 32;
const SESSION_CHALLENGE: u8 = 33;
const AUTH_SUCCESSFUL: u8 = 34;
const SERVER_FB_FORMAT: u8 = 128;
const SERVER_RC_MESSAGE: u8 = 131;
const SERVER_COMMAND: u8 = 132;
const PING_REQUEST: u8 = 148;
const PING_REPLY: u8 = 149;
const BANDWIDTH_REQUEST: u8 = 150;
const VM_MOUNTS_RESPONSE: u8 = 166;
const VM_SHARE_TABLE: u8 = 167;
const VIRTUAL_MEDIA_CONFIG: u8 = 168;
const USB_PROFILE_LIST: u8 = 170;

// Client message types.
const SET_PIXEL_FORMAT: u8 = 0;
const SET_ENCODINGS: u8 = 2;
const FB_UPDATE_REQUEST: u8 = 3;
const CLIENT_INIT: u8 = 7;
const ASSOCIATED_TAG: u8 = 8;
const LOGIN: u8 = 32;
const CHALLENGE_RESPONSE: u8 = 33;
const POINTER_EVENT: u8 = 5;
const MOUSE_SYNC_EVENT: u8 = 134;
const KVM_SWITCH_EVENT: u8 = 137;
const VIDEO_SETTINGS_REQUEST: u8 = 145;
const PING_REPLY_OUT: u8 = 149;
const BANDWIDTH_REPLY: u8 = 151;
const SET_CONNECTION_PARAMETER: u8 = 155;

/// RDM session credentials. On the wire the session is the quoted pair
/// `"id":"key"` (see `RFBProfile.rdmSession` / `ViewFactory.formatSessionID`).
#[derive(Debug, Clone)]
pub struct RfbCredentials {
    pub session_id: String,
    pub session_key: String,
}

impl RfbCredentials {
    pub fn new(session_id: &str, session_key: &str) -> Self {
        Self {
            session_id: session_id.to_owned(),
            session_key: session_key.to_owned(),
        }
    }

    /// The `rdmSessionID` string the Java client authenticates with.
    pub fn rdm_session(&self) -> String {
        format!("\"{}\":\"{}\"", self.session_id, self.session_key)
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct FramebufferUpdate {
    pub flags: u8,
    pub rectangles: Vec<FramebufferRectangle>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct FramebufferRectangle {
    pub x: u16,
    pub y: u16,
    pub width: u16,
    pub height: u16,
    pub encoding: i32,
    pub data: Vec<u8>,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub struct PixelFormat {
    pub bits_per_pixel: u8,
    pub depth: u8,
    pub big_endian: bool,
    pub true_colour: bool,
    pub red_max: u16,
    pub green_max: u16,
    pub blue_max: u16,
    pub red_shift: u8,
    pub green_shift: u8,
    pub blue_shift: u8,
}

impl PixelFormat {
    pub const RGB565: Self = Self {
        bits_per_pixel: 16,
        depth: 16,
        big_endian: true,
        true_colour: true,
        red_max: 31,
        green_max: 63,
        blue_max: 31,
        red_shift: 11,
        green_shift: 5,
        blue_shift: 0,
    };
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Framebuffer {
    pub width: u16,
    pub height: u16,
    pub rgba: Vec<u8>,
}

impl Framebuffer {
    pub fn new(width: u16, height: u16) -> Self {
        Self {
            width,
            height,
            rgba: vec![0; width as usize * height as usize * 4],
        }
    }

    pub fn apply_update(
        &mut self,
        update: &FramebufferUpdate,
        pixel_format: PixelFormat,
    ) -> Result<()> {
        for rectangle in &update.rectangles {
            match rectangle.encoding & 0xff {
                0 => self.decode_raw(rectangle, pixel_format)?,
                11 | 128 | 255 => self.decode_lrle(rectangle, pixel_format)?,
                encoding => bail!("unsupported framebuffer encoding {encoding}"),
            }
        }
        Ok(())
    }

    fn decode_raw(&mut self, rectangle: &FramebufferRectangle, format: PixelFormat) -> Result<()> {
        let bytes_per_pixel = (format.bits_per_pixel / 8) as usize;
        if !matches!(bytes_per_pixel, 1 | 2 | 4) {
            bail!("unsupported pixel format: {} bits", format.bits_per_pixel);
        }
        let expected = rectangle.width as usize * rectangle.height as usize * bytes_per_pixel;
        if rectangle.data.len() != expected {
            bail!(
                "raw rectangle size mismatch: expected {expected}, got {}",
                rectangle.data.len()
            );
        }
        for y in 0..rectangle.height as usize {
            for x in 0..rectangle.width as usize {
                let offset = (y * rectangle.width as usize + x) * bytes_per_pixel;
                let value = if format.big_endian {
                    rectangle.data[offset..offset + bytes_per_pixel]
                        .iter()
                        .fold(0u32, |value, byte| (value << 8) | u32::from(*byte))
                } else {
                    rectangle.data[offset..offset + bytes_per_pixel]
                        .iter()
                        .rev()
                        .fold(0u32, |value, byte| (value << 8) | u32::from(*byte))
                };
                self.put_pixel(
                    rectangle.x as usize + x,
                    rectangle.y as usize + y,
                    rgb(value, format),
                );
            }
        }
        Ok(())
    }

    fn decode_lrle(
        &mut self,
        rectangle: &FramebufferRectangle,
        _format: PixelFormat,
    ) -> Result<()> {
        // Mirrors ImageDecoderLrle.decodeImage: 16x16 tiles, per-tile runs,
        // line-copy via prevLine. LRLE colors are format-independent.
        let subencoding = ((rectangle.encoding as u32 >> 12) & 0xf) as usize;
        let conf = lrle_config(subencoding)?;
        let colors = lrle_colors(&conf);
        let greys = lrle_greys(conf.grey_depth);
        let mut reader = SliceReader::new(&rectangle.data);
        let width = rectangle.width as usize;
        let height = rectangle.height as usize;
        let mut previous = vec![0u32; self.width as usize];
        for tile_y in (0..height).step_by(16) {
            for tile_x in (0..width).step_by(16) {
                let tile_w = (width - tile_x).min(16);
                let tile_h = (height - tile_y).min(16);
                if conf.map {
                    decode_lrle_map(
                        self,
                        &mut reader,
                        rectangle,
                        &greys,
                        conf.grey_depth,
                        tile_x,
                        tile_y,
                        tile_w,
                        tile_h,
                    )?;
                    continue;
                }
                let (mut column, mut row) = (0usize, 0usize);
                let mut color = 0u32;
                let mut copy = false;
                loop {
                    if column == 0 && row == tile_h {
                        break;
                    }
                    let code = reader.read_u8()?;
                    let run: usize;
                    if code & 0xe0 == 0xe0 {
                        copy = true;
                        run = if code == 0xff {
                            reader.read_u8()? as usize
                        } else {
                            (code & 0x1f) as usize
                        };
                    } else if conf.compact {
                        copy = false;
                        if conf.depth <= 3 {
                            color = colors[(code & 7) as usize];
                            run = (code >> 3) as usize;
                        } else {
                            color = colors[(code & 0xf) as usize];
                            run = (code >> 4) as usize;
                        }
                    } else {
                        match code >> 6 {
                            0 | 1 => {
                                let index = if conf.depth > 7 {
                                    (u16::from(code) << 8 | u16::from(reader.read_u8()?)) as usize
                                } else {
                                    code as usize
                                };
                                color = colors[index];
                                run = 0;
                                copy = false;
                            }
                            2 => {
                                color = greys[(code & 0x3f) as usize];
                                run = 0;
                                copy = false;
                            }
                            _ => {
                                run = (code & 0x1f) as usize;
                            }
                        }
                    }
                    for _ in 0..=run {
                        if !copy {
                            previous[column] = color;
                        }
                        self.put_pixel(
                            rectangle.x as usize + tile_x + column,
                            rectangle.y as usize + tile_y + row,
                            previous[column],
                        );
                        column += 1;
                        if column == tile_w {
                            column = 0;
                            row += 1;
                        }
                    }
                }
            }
        }
        Ok(())
    }

    fn put_pixel(&mut self, x: usize, y: usize, color: u32) {
        if x >= self.width as usize || y >= self.height as usize {
            return;
        }
        let offset = (y * self.width as usize + x) * 4;
        self.rgba[offset..offset + 4].copy_from_slice(&color.to_be_bytes());
    }
}

fn rgb(value: u32, format: PixelFormat) -> u32 {
    let red = ((value >> format.red_shift) & u32::from(format.red_max)) * 255
        / u32::from(format.red_max.max(1));
    let green = ((value >> format.green_shift) & u32::from(format.green_max)) * 255
        / u32::from(format.green_max.max(1));
    let blue = ((value >> format.blue_shift) & u32::from(format.blue_max)) * 255
        / u32::from(format.blue_max.max(1));
    0xff00_0000 | red << 16 | green << 8 | blue
}

/// LRLE decoder configuration, mirroring
/// `ImageDecoderLrle.LRLEColorDecoderConf` `(is_map, is_compact, is_grey,
/// depth, grey_depth)` indexed by subencoding.
struct LrleConfig {
    map: bool,
    compact: bool,
    grey: bool,
    depth: usize,
    grey_depth: usize,
}

fn lrle_config(subencoding: usize) -> Result<LrleConfig> {
    let (map, compact, grey, depth, grey_depth) = match subencoding {
        0 | 1 => (false, false, false, 15, 6),
        2 | 3 => (false, false, false, 7, 4),
        4 | 5 => (false, true, false, 4, 4),
        6 | 7 => (false, true, true, 4, 4),
        8 | 9 => (false, true, true, 3, 3),
        10 | 11 => (true, false, true, 2, 2),
        12 | 13 => (true, false, true, 1, 1),
        other => bail!("unsupported LRLE subencoding {other}"),
    };
    Ok(LrleConfig {
        map,
        compact,
        grey,
        depth,
        grey_depth,
    })
}

/// Grey ramp, mirroring `createLRLEColorTables` integer math exactly.
fn lrle_greys(grey_depth: usize) -> Vec<u32> {
    (0..1u32 << grey_depth)
        .map(|n| {
            let channel = match grey_depth {
                1 => n * 255,
                2 => n * 85,
                3 => n * 73 / 2,
                4 => n * 17,
                5 => n * 33 / 4,
                6 => n * 65 / 16,
                _ => 0xff00ff,
            };
            0xff00_0000 | channel << 16 | channel << 8 | channel
        })
        .collect()
}

/// Color table for direct/compact pixels. Grey configs reuse the grey ramp.
fn lrle_colors(conf: &LrleConfig) -> Vec<u32> {
    if conf.grey {
        return lrle_greys(conf.grey_depth);
    }
    match conf.depth {
        15 => (0..1u32 << 15)
            .map(|n| {
                let red = ((n & 0x7c00) >> 10) * 33 / 4;
                let green = ((n & 0x03e0) >> 5) * 33 / 4;
                let blue = (n & 0x001f) * 33 / 4;
                0xff00_0000 | (red << 16) | (green << 8) | blue
            })
            .collect(),
        7 => {
            let levels = [0, 64, 128, 192, 255];
            (0..128)
                .map(|n| {
                    if n < 125 {
                        0xff00_0000
                            | levels[n as usize / 25] << 16
                            | levels[n as usize / 5 % 5] << 8
                            | levels[n as usize % 5]
                    } else {
                        0xffff_0000
                    }
                })
                .collect()
        }
        4 => vec![
            0xff00_0000,
            0xff7f_0000,
            0xff00_7f00,
            0xff7f_7f00,
            0xff00_007f,
            0xff7f_007f,
            0xff00_7f7f,
            0xff7f_7f7f,
            0xffc0_c0c0,
            0xffff_0000,
            0xff00_ff00,
            0xffff_ff00,
            0xff00_00ff,
            0xffff_00ff,
            0xff00_ffff,
            0xffff_ffff,
        ],
        _ => vec![0xff00_0000],
    }
}

/// Packed grey-pixel map path (`drawLRLEMap`): each byte holds
/// `8 / grey_depth` pixels, MSB first.
#[allow(clippy::too_many_arguments)]
fn decode_lrle_map(
    framebuffer: &mut Framebuffer,
    reader: &mut SliceReader<'_>,
    rectangle: &FramebufferRectangle,
    greys: &[u32],
    grey_depth: usize,
    tile_x: usize,
    tile_y: usize,
    tile_w: usize,
    tile_h: usize,
) -> Result<()> {
    let group = 8 / grey_depth;
    let mask = (1u32 << grey_depth) - 1;
    for row in 0..tile_h {
        let full_groups = tile_w / group;
        let remainder = tile_w % group;
        for cluster in 0..full_groups {
            let mut byte = reader.read_u8()?;
            for k in (0..group).rev() {
                let color = greys[(u32::from(byte) & mask) as usize];
                framebuffer.put_pixel(
                    rectangle.x as usize + tile_x + cluster * group + k,
                    rectangle.y as usize + tile_y + row,
                    color,
                );
                byte >>= grey_depth;
            }
        }
        if remainder > 0 {
            let mut byte = reader.read_u8()?;
            for k in (0..remainder).rev() {
                let color = greys[(u32::from(byte) & mask) as usize];
                framebuffer.put_pixel(
                    rectangle.x as usize + tile_x + full_groups * group + k,
                    rectangle.y as usize + tile_y + row,
                    color,
                );
                byte >>= grey_depth;
            }
        }
    }
    Ok(())
}

struct SliceReader<'a> {
    data: &'a [u8],
    offset: usize,
}

impl<'a> SliceReader<'a> {
    fn new(data: &'a [u8]) -> Self {
        Self { data, offset: 0 }
    }

    fn read_u8(&mut self) -> Result<u8> {
        let value = *self
            .data
            .get(self.offset)
            .ok_or_else(|| eyre!("truncated LRLE data"))?;
        self.offset += 1;
        Ok(value)
    }
}
pub struct RfbStream<S> {
    stream: S,
    framebuffer_size: Option<(u16, u16)>,
    pending_updates: VecDeque<FramebufferUpdate>,
}

impl RfbStream<TcpStream> {
    pub fn connect(host: &str) -> Result<Self> {
        let stream = TcpStream::connect((host, 443))
            .wrap_err_with(|| format!("connecting to {host}:443"))?;
        Ok(Self {
            stream,
            framebuffer_size: None,
            pending_updates: VecDeque::new(),
        })
    }

    /// Plaintext video channel on 443, as used by the Java client
    /// (`RemoteConsoleParameters.ssl == false`, see truth.pcapng stream 4).
    pub fn connect_raritan(
        host: &str,
        session_id: &str,
        session_key: &str,
        port: &str,
    ) -> Result<Self> {
        let mut stream = Self::connect(host)?;
        stream.handshake(&RfbCredentials::new(session_id, session_key), port)?;
        Ok(stream)
    }

    pub fn connect_raritan_tls(
        host: &str,
        session_id: &str,
        session_key: &str,
        port: &str,
    ) -> Result<RfbStream<SslStream<TcpStream>>> {
        let stream = Self::connect_tls_channel(host, session_id)?;
        let mut stream = RfbStream::new(stream);
        stream.handshake(&RfbCredentials::new(session_id, session_key), port)?;
        Ok(stream)
    }

    /// Legacy CSC + TLS + RC4 channel setup, kept for setups that require
    /// SSL. The default Java video path is plaintext (see `connect_raritan`).
    fn connect_tls_channel(host: &str, session_id: &str) -> Result<SslStream<TcpStream>> {
        info!(%host, "connecting to RFP CSC channel");
        let mut socket = TcpStream::connect((host, 443))
            .wrap_err_with(|| format!("connecting to {host}:443"))?;
        let greeting = read_csc_frame(&mut socket)?;
        debug!(length = greeting.len(), "received RFP CSC greeting");
        if !greeting.starts_with(b"<CSC") {
            bail!("unexpected RFP greeting");
        }
        write_csc_frame(&mut socket, b"<CSC_Ack/>")?;
        let _info = read_csc_frame(&mut socket)?;
        debug!("received RFP CSC info");
        write_csc_frame(
            &mut socket,
            format!(
                r#"<CSC_Start_Session ProtocolID="RFP" SessionID="{}"/>"#,
                escape_xml(session_id)
            )
            .as_bytes(),
        )?;
        Ok(tls_upgrade(host, socket)?)
    }
}

fn tls_upgrade(host: &str, socket: TcpStream) -> Result<SslStream<TcpStream>> {
    let mut builder = SslConnector::builder(SslMethod::tls())?;
    builder.set_cipher_list("DEFAULT:@SECLEVEL=0")?;
    builder.set_min_proto_version(Some(SslVersion::TLS1))?;
    builder.set_max_proto_version(Some(SslVersion::TLS1))?;
    builder.set_verify(SslVerifyMode::NONE);
    Ok(builder.build().connect(host, socket)?)
}

impl<S: Read + Write> RfbStream<S> {
    pub fn new(stream: S) -> Self {
        Self {
            stream,
            framebuffer_size: None,
            pending_updates: VecDeque::new(),
        }
    }

    pub fn framebuffer_size(&self) -> Option<(u16, u16)> {
        self.framebuffer_size
    }

    pub fn into_inner(self) -> S {
        self.stream
    }

    /// Framebuffer update request for the full framebuffer area, matching
    /// `RfbFramebufferUpdateRequestMsgV01_22`: `[3, incr, x, y, w, h]`.
    pub fn request_framebuffer_update(&mut self, incremental: bool) -> Result<()> {
        let (width, height) = self
            .framebuffer_size
            .ok_or_else(|| eyre!("framebuffer size unknown; handshake not finished"))?;
        self.request_region_update(0, 0, width, height, incremental)
    }

    pub fn request_region_update(
        &mut self,
        x: u16,
        y: u16,
        width: u16,
        height: u16,
        incremental: bool,
    ) -> Result<()> {
        let mut request = [0u8; 10];
        request[0] = FB_UPDATE_REQUEST;
        request[1] = u8::from(incremental);
        request[2..4].copy_from_slice(&x.to_be_bytes());
        request[4..6].copy_from_slice(&y.to_be_bytes());
        request[6..8].copy_from_slice(&width.to_be_bytes());
        request[8..10].copy_from_slice(&height.to_be_bytes());
        self.stream.write_all(&request)?;
        self.stream.flush()?;
        Ok(())
    }

    /// RFB 1.29 handshake, byte-exact per the Java client
    /// (`RfbHandlerV01_29`) and truth.pcapng stream 4.
    pub fn handshake(&mut self, credentials: &RfbCredentials, port: &str) -> Result<PixelFormat> {
        info!(%port, "starting RFB 1.29 handshake");
        // RfbHelloMsgV01_00 / RfbVersionMsgV01_00.
        self.stream.write_all(b"e-RIC AUTH=")?;
        let mut version = [0; 16];
        self.stream.read_exact(&mut version)?;
        if &version != b"e-RIC RFB 01.29\n" {
            bail!(
                "unexpected RFB version: {:?}",
                String::from_utf8_lossy(&version)
            );
        }
        debug!("RFB protocol version negotiated");
        self.stream.write_all(b"e-RIC RFB 01.29\n")?;

        // RfbAuthCapsMsgV01_22 + RfbAuthenticatorV01_22: require the
        // RDM-session method (caps bit 16) and log in as "super".
        let auth_type = read_u8(&mut self.stream)?;
        if auth_type != AUTH_CAPS {
            bail!("expected RFB auth capabilities, got {auth_type}");
        }
        let capabilities = read_u8(&mut self.stream)?;
        info!(capabilities, "received RFB authentication capabilities");
        if capabilities & 16 == 0 {
            bail!("RFB server does not offer RDM-session authentication");
        }
        // RfbLoginMsgV01_22.write(os, "super", 16, 0).
        self.stream
            .write_all(&[LOGIN, 16, 6, 0, 0, 0, 0, 0, b's', b'u', b'p', b'e', b'r', 0])?;

        // RfbSessionChallengeMsgV01_22: [33][len][challenge?].
        let message = read_u8(&mut self.stream)?;
        if message != SESSION_CHALLENGE {
            bail!("expected RFB session challenge, got {message}");
        }
        let size = read_u8(&mut self.stream)? as usize;
        if size > 0 {
            let mut challenge = vec![0; size];
            self.stream.read_exact(&mut challenge)?;
            debug!(size, "received non-empty RFB session challenge");
        }
        // RfbAuthenticatorV01_22 (RDM method): writeChallengeResponse(
        // rdmSessionID + '\0'); RfbChallengeResponseMsgV01_22 writes
        // [33][string.length()][bytes], length includes the NUL.
        let session = format!("{}\0", credentials.rdm_session());
        let session_bytes = session.as_bytes();
        if session_bytes.len() > u8::MAX as usize {
            bail!("RDM session string is too long");
        }
        self.stream.write_all(&[CHALLENGE_RESPONSE])?;
        self.stream.write_all(&[session_bytes.len() as u8])?;
        self.stream.write_all(session_bytes)?;
        self.stream.flush()?;

        // Event loop: the server interleaves auth-ok, connection
        // parameters, UTF-8 welcome, server-init, OSD, keyboard layout,
        // server commands, USB profiles, ... before the 128
        // framebuffer-format message that ends the handshake.
        // Mirrors RfbHandler.processProtocol/processInitialHandshake.
        loop {
            let message_type = read_u8(&mut self.stream)?;
            trace!(message_type, "received RFB handshake message");
            match message_type {
                AUTH_SUCCESSFUL => {
                    // RfbAuthSuccessfulMsgV01_22: pad + u16 + u32 flags.
                    let mut rest = [0; 7];
                    self.stream.read_exact(&mut rest)?;
                    info!("RFB session authentication succeeded");
                }
                CONNECTION_PARAMETERS => {
                    let params = self.read_connection_parameters()?;
                    debug!(count = params.len(), "received connection parameters");
                }
                UTF8_STRING => {
                    let string = self.read_utf8_string()?;
                    debug!(%string, "received UTF-8 welcome string");
                    self.write_client_init(credentials, port)?;
                }
                SERVER_INIT => {
                    // RfbServerInitMsgV01_27: 3 pad bytes + server id.
                    let mut pad = [0; 3];
                    self.stream.read_exact(&mut pad)?;
                    let server_id = read_i32(&mut self.stream)?;
                    debug!(server_id, "received server init");
                }
                SERVER_FB_FORMAT => {
                    let (width, height, pixel_format) = self.read_server_fb_format()?;
                    self.write_set_encodings(&default_encodings())?;
                    self.write_set_pixel_format(PixelFormat::RGB565)?;
                    self.framebuffer_size = Some((width, height));
                    info!(width, height, ?pixel_format, "received framebuffer format");
                    // Session init exactly as the Java client does it:
                    // three full updates, video-settings request,
                    // mouse sync, pointer event, connection parameter
                    // (current mouse mode), and an initial ping.
                    for _ in 0..3 {
                        self.request_region_update(0, 0, width, height, false)?;
                    }
                    self.write_video_settings_request(1)?;
                    self.write_mouse_sync(0)?;
                    self.write_pointer_event(0, 0, 0, 0)?;
                    self.write_set_connection_parameter(
                        "current_mouse_mode",
                        "absolute",
                    )?;
                    self.write_ping_request(0)?;
                    return Ok(pixel_format);
                }
                FRAMEBUFFER_UPDATE => {
                    // Not expected before 128, but keep the stream aligned
                    // and stash it instead of losing sync.
                    let update = self.read_framebuffer_update()?;
                    warn!("received framebuffer update during handshake; stashing");
                    self.pending_updates.push_back(update);
                }
                _ => self.skip_server_message(message_type)?,
            }
        }
    }

    /// Client session init after the server 7 welcome message:
    /// `[7,0,flags=4]` + associated tag (msg 8, the RDM session string,
    /// exactly as the Java client sends) + KVM-switch event (msg 137).
    fn write_client_init(&mut self, credentials: &RfbCredentials, port: &str) -> Result<()> {
        // RfbClientInitMsgV01_29 with MULTI_MONITOR_ASSOCIATION_ID set.
        self.stream.write_all(&[CLIENT_INIT, 0, 0, 4])?;
        // RfbAssociatedTagMsgV01_29: [8,0,len:u16,bytes], no NUL.
        // The captured client sends the RDM session string as the tag.
        self.write_associated_tag(credentials.rdm_session().as_bytes())?;
        self.write_kvm_switch(port)?;
        self.stream.flush()?;
        debug!(%port, "selected KVM port on RFB channel");
        Ok(())
    }

    fn write_associated_tag(&mut self, tag: &[u8]) -> Result<()> {
        if tag.len() > u16::MAX as usize {
            bail!("associated tag is too long");
        }
        self.stream.write_all(&[ASSOCIATED_TAG, 0])?;
        self.stream.write_all(&(tag.len() as u16).to_be_bytes())?;
        self.stream.write_all(tag)?;
        Ok(())
    }

    fn write_kvm_switch(&mut self, port: &str) -> Result<()> {
        let port_bytes = port.as_bytes();
        if port_bytes.len() > u16::MAX as usize {
            bail!("port ID is too long");
        }
        // RfbKvmSwitchEventMsgV01_27: [137,0,len:u16,bytes].
        self.stream.write_all(&[KVM_SWITCH_EVENT, 0])?;
        self.stream
            .write_all(&(port_bytes.len() as u16).to_be_bytes())?;
        self.stream.write_all(port_bytes)?;
        self.stream.flush()?;
        Ok(())
    }

    fn write_set_encodings(&mut self, encodings: &[u32]) -> Result<()> {
        // RfbSetEncodingMsgV01_22: [2,0,count:u16,encodings..].
        if encodings.len() > u16::MAX as usize {
            bail!("too many encodings");
        }
        self.stream.write_all(&[SET_ENCODINGS, 0])?;
        self.stream
            .write_all(&(encodings.len() as u16).to_be_bytes())?;
        for encoding in encodings {
            self.stream.write_all(&encoding.to_be_bytes())?;
        }
        self.stream.flush()?;
        Ok(())
    }

    fn write_set_pixel_format(&mut self, format: PixelFormat) -> Result<()> {
        // RfbSetPixelFormatMsgV01_22: [0,pad*3,pixfmt16,pad*3].
        let mut message = [0u8; 20];
        message[0] = SET_PIXEL_FORMAT;
        message[4] = format.bits_per_pixel;
        message[5] = format.depth;
        message[6] = u8::from(format.big_endian);
        message[7] = u8::from(format.true_colour);
        message[8..10].copy_from_slice(&format.red_max.to_be_bytes());
        message[10..12].copy_from_slice(&format.green_max.to_be_bytes());
        message[12..14].copy_from_slice(&format.blue_max.to_be_bytes());
        message[14] = format.red_shift;
        message[15] = format.green_shift;
        message[16] = format.blue_shift;
        self.stream.write_all(&message)?;
        self.stream.flush()?;
        Ok(())
    }

    pub fn read_message(&mut self) -> Result<FramebufferUpdate> {
        if let Some(update) = self.pending_updates.pop_front() {
            return Ok(update);
        }
        loop {
            let message_type = read_u8(&mut self.stream)?;
            trace!(message_type, "received RFB server message");
            match message_type {
                FRAMEBUFFER_UPDATE => return self.read_framebuffer_update(),
                PING_REQUEST => {
                    let serial = self.read_ping_serial()?;
                    self.write_ping_reply(serial)?;
                }
                PING_REPLY => {
                    let _ = self.read_ping_serial()?;
                }
                BANDWIDTH_REQUEST => {
                    // RfbHandler.processBandwidthRequest: reply(1), read,
                    // reply(2).
                    self.write_bandwidth_reply(1)?;
                    self.read_bandwidth_request()?;
                    self.write_bandwidth_reply(2)?;
                }
                SERVER_FB_FORMAT => {
                    // Late format change: adopt dimensions like
                    // RfbHandler.processServerFBFormat does.
                    let (width, height, _) = self.read_server_fb_format()?;
                    info!(width, height, "framebuffer format changed");
                    self.framebuffer_size = Some((width, height));
                }
                _ => self.skip_server_message(message_type)?,
            }
        }
    }

    fn read_ping_serial(&mut self) -> Result<u32> {
        // Ping messages are 8 bytes: [type,0,0,0,serial:u32].
        let mut pad = [0; 3];
        self.stream.read_exact(&mut pad)?;
        read_u32(&mut self.stream)
    }

    fn write_ping_reply(&mut self, serial: u32) -> Result<()> {
        let mut message = [0u8; 8];
        message[0] = PING_REPLY_OUT;
        message[4..8].copy_from_slice(&serial.to_be_bytes());
        self.stream.write_all(&message)?;
        self.stream.flush()?;
        Ok(())
    }

    /// Client ping request (RfbPingRequestMsgV01_22, 8 bytes).
    pub fn write_ping_request(&mut self, serial: u32) -> Result<()> {
        let mut message = [0u8; 8];
        message[0] = PING_REQUEST;
        message[4..8].copy_from_slice(&serial.to_be_bytes());
        self.stream.write_all(&message)?;
        self.stream.flush()?;
        Ok(())
    }

    /// Video-settings request (RfbVideoSettingsRequestMsgV01_22).
    fn write_video_settings_request(&mut self, kind: u8) -> Result<()> {
        self.stream.write_all(&[VIDEO_SETTINGS_REQUEST, kind])?;
        self.stream.flush()?;
        Ok(())
    }

    /// Mouse sync event (RfbMouseSyncEventMsgV01_22).
    fn write_mouse_sync(&mut self, mode: u8) -> Result<()> {
        self.stream.write_all(&[MOUSE_SYNC_EVENT, mode])?;
        self.stream.flush()?;
        Ok(())
    }

    /// Pointer event (RfbPointerEventMsgV01_22).
    fn write_pointer_event(
        &mut self,
        buttons: u8,
        x: u16,
        y: u16,
        wheel: u16,
    ) -> Result<()> {
        let mut message = [0u8; 8];
        message[0] = POINTER_EVENT;
        message[1] = buttons;
        message[2..4].copy_from_slice(&x.to_be_bytes());
        message[4..6].copy_from_slice(&y.to_be_bytes());
        message[6..8].copy_from_slice(&wheel.to_be_bytes());
        self.stream.write_all(&message)?;
        self.stream.flush()?;
        Ok(())
    }

    /// Set connection parameter (RfbSetConnectionParameterMsgV01_27).
    fn write_set_connection_parameter(
        &mut self,
        name: &str,
        value: &str,
    ) -> Result<()> {
        if name.len() > u8::MAX as usize || value.len() > u8::MAX as usize {
            bail!("connection parameter too long");
        }
        self.stream.write_all(&[
            SET_CONNECTION_PARAMETER,
            name.len() as u8,
            value.len() as u8,
        ])?;
        self.stream.write_all(name.as_bytes())?;
        self.stream.write_all(value.as_bytes())?;
        self.stream.flush()?;
        Ok(())
    }

    fn write_bandwidth_reply(&mut self, stage: u8) -> Result<()> {
        self.stream.write_all(&[BANDWIDTH_REPLY, stage])?;
        self.stream.flush()?;
        Ok(())
    }

    /// RfbConnectionParameterListMsgV01_22: `[18][count][klen,vlen,key,value]*`.
    fn read_connection_parameters(&mut self) -> Result<Vec<(String, String)>> {
        let count = read_u8(&mut self.stream)? as usize;
        let mut params = Vec::with_capacity(count);
        for _ in 0..count {
            let key_len = read_u8(&mut self.stream)? as usize;
            let value_len = read_u8(&mut self.stream)? as usize;
            let mut key = vec![0; key_len];
            let mut value = vec![0; value_len];
            self.stream.read_exact(&mut key)?;
            self.stream.read_exact(&mut value)?;
            params.push((
                String::from_utf8_lossy(&key).into_owned(),
                String::from_utf8_lossy(&value).into_owned(),
            ));
        }
        Ok(params)
    }

    /// RfbUtf8StringMsgV01_22 read: `[7][pad][len:u16be, Java UTF][bytes]`.
    fn read_utf8_string(&mut self) -> Result<String> {
        let _pad = read_u8(&mut self.stream)?;
        let len = read_u16(&mut self.stream)? as usize;
        let mut bytes = vec![0; len];
        self.stream.read_exact(&mut bytes)?;
        Ok(String::from_utf8_lossy(&bytes).into_owned())
    }

    /// RfbServerFBFormatMsgV01_27: `[128][unsupported][w][h][pixfmt16][pad*3]`.
    fn read_server_fb_format(&mut self) -> Result<(u16, u16, PixelFormat)> {
        let unsupported = read_u8(&mut self.stream)?;
        if unsupported != 0 {
            warn!("server reports unsupported framebuffer format");
        }
        let width = read_u16(&mut self.stream)?;
        let height = read_u16(&mut self.stream)?;
        let pixel_format = Self::read_pixel_format(&mut self.stream)?;
        let mut pad = [0; 3];
        self.stream.read_exact(&mut pad)?;
        Ok((width, height, pixel_format))
    }

    fn read_pixel_format<R: Read>(reader: &mut R) -> Result<PixelFormat> {
        Ok(PixelFormat {
            bits_per_pixel: read_u8(reader)?,
            depth: read_u8(reader)?,
            big_endian: read_u8(reader)? != 0,
            true_colour: read_u8(reader)? != 0,
            red_max: read_u16(reader)?,
            green_max: read_u16(reader)?,
            blue_max: read_u16(reader)?,
            red_shift: read_u8(reader)?,
            green_shift: read_u8(reader)?,
            blue_shift: read_u8(reader)?,
        })
    }

    fn read_bandwidth_request(&mut self) -> Result<()> {
        // RfbBandwidthRequestMsgV01_22: pad + len:u16 + bytes.
        let _pad = read_u8(&mut self.stream)?;
        let len = read_u16(&mut self.stream)? as usize;
        let mut bytes = vec![0; len];
        self.stream.read_exact(&mut bytes)?;
        Ok(())
    }

    /// Consume and discard any server message that carries no video data,
    /// keeping the stream aligned. Mirrors the `process*` readers in
    /// `RfbHandler`/`RfbHandlerV01_29`.
    fn skip_server_message(&mut self, message_type: u8) -> Result<()> {
        trace!(message_type, "skipping RFB server message");
        match message_type {
            USER_NOTIFICATION => {
                // [3][kind][pad:u16][code:i32].
                let mut rest = [0; 7];
                self.stream.read_exact(&mut rest)?;
            }
            PORT_LIST => self.skip_port_list()?,
            SERVER_INIT => {
                let mut pad = [0; 3];
                self.stream.read_exact(&mut pad)?;
                let _ = read_i32(&mut self.stream)?;
            }
            UTF8_STRING => {
                let string = self.read_utf8_string()?;
                debug!(%string, "received out-of-sequence UTF-8 string");
            }
            VIDEO_SETTINGS_S2C => {
                // RfbVideoSettingsS2CMsgV01_27: 1 u8 + 8 u8 + 9 u16.
                let mut rest = [0; 27];
                self.stream.read_exact(&mut rest)?;
            }
            KEYBOARD_LAYOUT => {
                let _pad = read_u8(&mut self.stream)?;
                self.skip_blob16()?;
            }
            OSD_STATE => {
                // RfbOSDStateMsgV01_27: blanking + timeout:u16 + len:u16 + text.
                let mut header = [0; 5];
                self.stream.read_exact(&mut header)?;
                let len = u16::from_be_bytes([header[3], header[4]]) as usize;
                let mut text = vec![0; len];
                self.stream.read_exact(&mut text)?;
                debug!(message = %String::from_utf8_lossy(&text), "OSD state");
            }
            VIDEO_QUALITY_S2C => {
                let mut rest = [0; 2];
                self.stream.read_exact(&mut rest)?;
            }
            CONNECTION_PARAMETERS => {
                let params = self.read_connection_parameters()?;
                debug!(count = params.len(), "received late connection parameters");
            }
            ACK_PIXEL_FORMAT => {
                // RfbAckPixelFormatMsgV01_22: 3 pad + 13 pixfmt fields + 3 pad.
                let mut rest = [0; 19];
                self.stream.read_exact(&mut rest)?;
            }
            SERVER_RC_MESSAGE => {
                // V01_27: 3 pad + len:i32 + bytes.
                let mut pad = [0; 3];
                self.stream.read_exact(&mut pad)?;
                let len = read_i32(&mut self.stream)?;
                if len < 0 {
                    bail!("invalid server RC message length {len}");
                }
                let mut bytes = vec![0; len as usize];
                self.stream.read_exact(&mut bytes)?;
                debug!(message = %String::from_utf8_lossy(&bytes), "server RC message");
            }
            SERVER_COMMAND => {
                // [132][pad][name_len:u16][value_len:u16][name][value].
                let _pad = read_u8(&mut self.stream)?;
                let name_len = read_u16(&mut self.stream)? as usize;
                let value_len = read_u16(&mut self.stream)? as usize;
                let mut name = vec![0; name_len];
                let mut value = vec![0; value_len];
                self.stream.read_exact(&mut name)?;
                self.stream.read_exact(&mut value)?;
                debug!(
                    name = %String::from_utf8_lossy(&name),
                    value = %String::from_utf8_lossy(&value),
                    "server command"
                );
            }
            VM_MOUNTS_RESPONSE => {
                // [166][a][b][c][id:i32].
                let mut rest = [0; 7];
                self.stream.read_exact(&mut rest)?;
            }
            VM_SHARE_TABLE => {
                // [167][count][klen,vlen,key,value]*.
                let count = read_u8(&mut self.stream)? as usize;
                for _ in 0..count {
                    let key_len = read_u8(&mut self.stream)? as usize;
                    let value_len = read_u8(&mut self.stream)? as usize;
                    let mut skip = vec![0; key_len + value_len];
                    self.stream.read_exact(&mut skip)?;
                }
            }
            VIRTUAL_MEDIA_CONFIG => {
                let count = read_u8(&mut self.stream)? as usize;
                let mut rest = vec![0; count];
                self.stream.read_exact(&mut rest)?;
            }
            USB_PROFILE_LIST => self.skip_usb_profile_list()?,
            other => bail!("unsupported RFB server message type {other}"),
        }
        Ok(())
    }

    fn skip_blob16(&mut self) -> Result<()> {
        let len = read_u16(&mut self.stream)? as usize;
        let mut bytes = vec![0; len];
        self.stream.read_exact(&mut bytes)?;
        Ok(())
    }

    fn skip_port_list(&mut self) -> Result<()> {
        // RfbPortListMsgV01_27: [4][pad][count:u16] then per-port entries.
        let _pad = read_u8(&mut self.stream)?;
        let count = read_u16(&mut self.stream)? as usize;
        for _ in 0..count {
            let mut fixed = [0; 8];
            self.stream.read_exact(&mut fixed)?;
            let name_len = u16::from_be_bytes([fixed[4], fixed[5]]) as usize;
            let value_len = u16::from_be_bytes([fixed[6], fixed[7]]) as usize;
            let mut rest = vec![0; name_len + value_len];
            self.stream.read_exact(&mut rest)?;
        }
        Ok(())
    }

    fn skip_usb_profile_list(&mut self) -> Result<()> {
        // RfbUsbProfileListMsgV01_26: [170][count:u16] then per-profile
        // [name_len:u8][desc_len:u16][flags:u8][id:u16][name][desc].
        let count = read_u16(&mut self.stream)? as usize;
        for _ in 0..count {
            let name_len = read_u8(&mut self.stream)? as usize;
            let desc_len = read_u16(&mut self.stream)? as usize;
            let mut fixed = [0; 3];
            self.stream.read_exact(&mut fixed)?;
            let mut rest = vec![0; name_len + desc_len];
            self.stream.read_exact(&mut rest)?;
        }
        Ok(())
    }

    pub fn read_framebuffer_update(&mut self) -> Result<FramebufferUpdate> {
        let flags = read_u8(&mut self.stream)?;
        let count = read_u16(&mut self.stream)? as usize;
        let update_size = read_u32(&mut self.stream)? as usize;
        debug!(
            flags,
            rectangles = count,
            update_size,
            "received framebuffer update header"
        );

        if flags & 1 != 0 {
            let _timestamp_seconds = read_u32(&mut self.stream)?;
            let _timestamp_micros = read_u32(&mut self.stream)?;
        }

        let mut encoded = vec![0; update_size];
        self.stream.read_exact(&mut encoded)?;
        let payload = if flags & 4 != 0 {
            let mut decoder = ZlibDecoder::new(encoded.as_slice());
            let mut decoded = Vec::new();
            decoder.read_to_end(&mut decoded)?;
            decoded
        } else {
            encoded
        };
        let mut reader = std::io::Cursor::new(payload);
        let mut rectangles = Vec::with_capacity(count);
        for _ in 0..count {
            let x = read_u16(&mut reader)?;
            let y = read_u16(&mut reader)?;
            let width = read_u16(&mut reader)?;
            let height = read_u16(&mut reader)?;
            let encoding = read_i32(&mut reader)?;
            let mut size = read_u32(&mut reader)? as usize;
            let base_encoding = (encoding as u32 & 0xff) as u8;
            let is_lrle = matches!(base_encoding, 11 | 128);
            if is_lrle && size == 0 {
                // Hardware encoding: true size follows (readHardwareEncodingSize).
                size = read_i32(&mut reader)? as usize;
            }
            if is_lrle
                && ((encoding as u32 & 0xf00) != 0 || (encoding as u32 & 0x20000) != 0)
            {
                bail!("zlib-streamed framebuffer rects are not supported");
            }
            let mut data = vec![0; size];
            reader.read_exact(&mut data)?;
            if is_lrle {
                // readHardwareEncodingPadding: rects are 4-byte aligned.
                let pad = (4 - size % 4) % 4;
                let mut padding = vec![0; pad];
                reader.read_exact(&mut padding)?;
            }
            rectangles.push(FramebufferRectangle {
                x,
                y,
                width,
                height,
                encoding,
                data,
            });
        }

        Ok(FramebufferUpdate { flags, rectangles })
    }
}

fn read_u8<R: Read>(reader: &mut R) -> Result<u8> {
    let mut value = [0; 1];
    reader.read_exact(&mut value)?;
    Ok(value[0])
}

fn read_u16<R: Read>(reader: &mut R) -> Result<u16> {
    let mut value = [0; 2];
    reader.read_exact(&mut value)?;
    Ok(u16::from_be_bytes(value))
}

fn read_u32<R: Read>(reader: &mut R) -> Result<u32> {
    let mut value = [0; 4];
    reader.read_exact(&mut value)?;
    Ok(u32::from_be_bytes(value))
}

fn read_i32<R: Read>(reader: &mut R) -> Result<i32> {
    let mut value = [0; 4];
    reader.read_exact(&mut value)?;
    Ok(i32::from_be_bytes(value))
}

fn read_csc_frame<R: Read>(reader: &mut R) -> Result<Vec<u8>> {
    let length = read_u32(reader)? as usize;
    if !(5..=64 * 1024).contains(&length) {
        bail!("invalid CSC frame length {length}");
    }
    let mut frame = vec![0; length - 4];
    reader.read_exact(&mut frame)?;
    if frame.last() == Some(&0) {
        frame.pop();
    }
    Ok(frame)
}

fn write_csc_frame<W: Write>(writer: &mut W, payload: &[u8]) -> Result<()> {
    let length = u32::try_from(payload.len() + 5)?;
    writer.write_all(&length.to_be_bytes())?;
    writer.write_all(payload)?;
    writer.write_all(&[0])?;
    writer.flush()?;
    Ok(())
}

fn escape_xml(value: &str) -> String {
    value
        .replace('&', "&amp;")
        .replace('"', "&quot;")
        .replace('<', "&lt;")
        .replace('>', "&gt;")
}

/// Default encoding set: hardware LRLE, uncompressed, 16-bit lossless
/// (`0x1080`) plus three zero slots, exactly as the Java client sends
/// (`RfbEncodingV01_22.getRfbEncodings` with HW/UNCOMPRESSED/COLOR_16_BIT).
fn default_encodings() -> [u32; 4] {
    [0x0000_1080, 0, 0, 0]
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::io::Cursor;

    #[test]
    fn parses_rfb_framebuffer_update() {
        let mut bytes = vec![0, 0, 2];
        bytes.extend_from_slice(&36u32.to_be_bytes());
        bytes.extend_from_slice(&[0, 3, 0, 2, 0, 1, 0, 0]);
        bytes.extend_from_slice(&11i32.to_be_bytes());
        bytes.extend_from_slice(&4u32.to_be_bytes());
        bytes.extend_from_slice(&[1, 2, 3, 4]);
        bytes.extend_from_slice(&[1, 2, 0, 3, 0, 4, 0, 5]);
        bytes.extend_from_slice(&0i32.to_be_bytes());
        bytes.extend_from_slice(&0u32.to_be_bytes());

        let mut stream = RfbStream::new(Cursor::new(bytes));
        let update = stream.read_framebuffer_update().unwrap();

        assert_eq!(update.flags, 0);
        assert_eq!(update.rectangles.len(), 2);
        assert_eq!(update.rectangles[0].encoding, 11);
        assert_eq!(update.rectangles[0].data, [1, 2, 3, 4]);
        assert_eq!(update.rectangles[1].width, 4);
    }

    struct FakeStream {
        read: Cursor<Vec<u8>>,
        written: Vec<u8>,
    }

    impl FakeStream {
        fn new(server: Vec<u8>) -> Self {
            Self {
                read: Cursor::new(server),
                written: Vec::new(),
            }
        }
    }

    impl Read for FakeStream {
        fn read(&mut self, buf: &mut [u8]) -> std::io::Result<usize> {
            self.read.read(buf)
        }
    }

    impl Write for FakeStream {
        fn write(&mut self, buf: &[u8]) -> std::io::Result<usize> {
            self.written.extend_from_slice(buf);
            Ok(buf.len())
        }

        fn flush(&mut self) -> std::io::Result<()> {
            Ok(())
        }
    }

    /// Replays a minimal truth.pcapng-style handshake and asserts every
    /// client byte matches the Java client.
    #[test]
    fn handshake_matches_java_client_bytes() {
        let mut server = Vec::new();
        server.extend_from_slice(b"e-RIC RFB 01.29\n");
        server.extend_from_slice(&[32, 0x13]); // auth caps: 1|2|16
        server.extend_from_slice(&[33, 0]); // empty session challenge
        server.extend_from_slice(&[34, 0, 0, 0, 0, 0, 0, 0]); // auth ok
        server.extend_from_slice(&[18, 1, 6, 3]); // 1 conn param
        server.extend_from_slice(b"hw_enc");
        server.extend_from_slice(b"yes");
        server.extend_from_slice(&[7, 0, 0, 5]); // welcome "admin"
        server.extend_from_slice(b"admin");
        server.extend_from_slice(&[5, 0, 0, 0, 0, 0, 0, 17]); // server init
        // Server framebuffer format 1024x768 RGB565.
        server.extend_from_slice(&[128, 0, 0x04, 0x00, 0x03, 0x00]);
        server.extend_from_slice(&[
            16, 16, 1, 1, 0, 31, 0, 63, 0, 31, 11, 5, 0, 0, 0, 0,
        ]);

        let mut stream = RfbStream::new(FakeStream::new(server));
        let format = stream
            .handshake(&RfbCredentials::new("s_1", "k2"), "P_1")
            .unwrap();
        assert_eq!(format, PixelFormat::RGB565);
        assert_eq!(stream.framebuffer_size(), Some((1024, 768)));

        let mut expected = Vec::new();
        expected.extend_from_slice(b"e-RIC AUTH=");
        expected.extend_from_slice(b"e-RIC RFB 01.29\n");
        expected.extend_from_slice(&[
            32, 16, 6, 0, 0, 0, 0, 0, b's', b'u', b'p', b'e', b'r', 0,
        ]);
        // Challenge response: [33][len incl. NUL][`"s_1":"k2"` + NUL].
        expected.extend_from_slice(&[33, 11]);
        expected.extend_from_slice(b"\"s_1\":\"k2\"");
        expected.push(0);
        // Client init + associated tag (RDM session, no NUL) + KVM switch.
        expected.extend_from_slice(&[7, 0, 0, 4]);
        expected.extend_from_slice(&[8, 0, 0, 10]);
        expected.extend_from_slice(b"\"s_1\":\"k2\"");
        expected.extend_from_slice(&[137, 0, 0, 3]);
        expected.extend_from_slice(b"P_1");
        // SetEncodings [0x1080, 0, 0, 0], SetPixelFormat RGB565, full FBU.
        expected.extend_from_slice(&[2, 0, 0, 4]);
        expected.extend_from_slice(&0x0000_1080u32.to_be_bytes());
        expected.extend_from_slice(&0u32.to_be_bytes());
        expected.extend_from_slice(&0u32.to_be_bytes());
        expected.extend_from_slice(&0u32.to_be_bytes());
        expected.extend_from_slice(&[
            0, 0, 0, 0, 16, 16, 1, 1, 0, 31, 0, 63, 0, 31, 11, 5, 0, 0, 0, 0,
        ]);
        // Session init tail: three full updates, video-settings request,
        // mouse sync, pointer event, connection parameter, ping.
        for _ in 0..3 {
            expected.extend_from_slice(&[3, 0, 0, 0, 0, 0, 0x04, 0x00, 0x03, 0x00]);
        }
        expected.extend_from_slice(&[145, 1, 134, 0, 5, 0, 0, 0, 0, 0, 0, 0]);
        expected.extend_from_slice(&[155, 18, 8]);
        expected.extend_from_slice(b"current_mouse_mode");
        expected.extend_from_slice(b"absolute");
        expected.extend_from_slice(&[148, 0, 0, 0, 0, 0, 0, 0]);

        assert_eq!(&stream.stream.written, &expected);
    }

    /// Steady-state pump with real captured bytes: answers a ping request,
    /// then decodes a 16x16 LRLE rect (from truth.pcapng) into pixels.
    #[test]
    fn pump_answers_ping_and_decodes_lrle_rect() {
        let mut server = vec![148, 0, 0, 0, 0x12, 0x34, 0x56, 0x78];
        // Framebuffer update: flags=0, 1 rect, 120 bytes (16 hdr + 101 data
        // + 3 alignment pad).
        server.extend_from_slice(&[0, 0, 0, 1]);
        server.extend_from_slice(&120u32.to_be_bytes());
        server.extend_from_slice(&[
            0x00, 0xa0, 0x02, 0x70, 0x00, 0x10, 0x00, 0x10, 0x00, 0x00, 0x10,
            0x80, 0x00, 0x00, 0x00, 0x65, 0x6f, 0x9c, 0x42, 0x51, 0x83, 0x81,
            0xcb, 0x73, 0xbd, 0x46, 0x72, 0xee, 0x42, 0x71, 0xea, 0x24, 0xe8,
            0xa9, 0xb7, 0xe1, 0x04, 0x41, 0xe7, 0x1c, 0xa6, 0x5e, 0xb7, 0xbb,
            0xbd, 0xc0, 0xe0, 0x46, 0x72, 0xe7, 0x81, 0xaf, 0xbb, 0xbd, 0x77,
            0xde, 0x56, 0xf6, 0xe1, 0x83, 0xe7, 0x62, 0xf9, 0xb9, 0x52, 0xd5,
            0x18, 0xe6, 0x81, 0xea, 0xa9, 0x2d, 0xab, 0x81, 0xc1, 0xe9, 0x81,
            0xc4, 0xe0, 0x42, 0x51, 0xed, 0x6f, 0x9c, 0x3a, 0x30, 0x04, 0x20,
            0xed, 0x3e, 0x50, 0x00, 0x21, 0xed, 0x3e, 0x51, 0xee, 0x3e, 0x50,
            0x00, 0x20, 0xed, 0x42, 0x51, 0xfb, 0x24, 0xe8, 0xa5, 0xe0, 0x3e,
            0x50, 0xe9, 0x2d, 0x2a, 0xb1, 0xbb, 0xbd, 0x00, 0x00, 0x00,
        ]);

        let mut stream = RfbStream::new(FakeStream::new(server));
        let update = stream.read_message().unwrap();
        assert_eq!(update.rectangles.len(), 1);
        assert_eq!(update.rectangles[0].encoding, 0x1080);
        // Ping reply [149,0,0,0,serial] must have been sent first.
        assert_eq!(
            stream.stream.written,
            vec![149, 0, 0, 0, 0x12, 0x34, 0x56, 0x78]
        );

        let mut framebuffer = Framebuffer::new(1024, 768);
        framebuffer
            .apply_update(&update, PixelFormat::RGB565)
            .unwrap();
        // Every pixel carries an opaque alpha once painted; the untouched
        // background stays zero.
        let painted = framebuffer
            .rgba
            .chunks_exact(4)
            .filter(|pixel| pixel[0] != 0)
            .count();
        assert_eq!(painted, 16 * 16);
    }
}
