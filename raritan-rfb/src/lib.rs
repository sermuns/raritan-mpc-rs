use base64::{Engine as _, engine::general_purpose::STANDARD};
use eyre::{Result, WrapErr, bail, eyre};
use flate2::read::ZlibDecoder;
use openssl::ssl::{SslConnector, SslMethod, SslStream, SslVerifyMode, SslVersion};
use std::io::{Read, Write};
use std::net::TcpStream;
use tracing::{debug, info, trace};

const FRAMEBUFFER_UPDATE: u8 = 0;
const PING_REQUEST: u8 = 148;
const PING_REPLY: u8 = 149;

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

    fn decode_lrle(&mut self, rectangle: &FramebufferRectangle, format: PixelFormat) -> Result<()> {
        let subencoding = ((rectangle.encoding as u32 >> 12) & 0xf) as usize;
        let (depth, grey_depth, compact, grey) = match subencoding {
            1 => (15, 6, false, false),
            3 => (7, 4, false, false),
            5 => (4, 4, true, false),
            7 => (4, 4, true, true),
            9 => (3, 3, true, true),
            11 => (2, 2, false, true),
            13 => (1, 1, false, true),
            other => bail!("unsupported LRLE subencoding {other}"),
        };
        let mut reader = SliceReader::new(&rectangle.data);
        let mut previous = vec![0u32; rectangle.width as usize];
        for row in 0..rectangle.height as usize {
            let mut column = 0usize;
            while column < rectangle.width as usize {
                let code = reader.read_u8()?;
                let (color, run) = if code & 0xe0 == 0xe0 {
                    let run = if code == 0xff {
                        reader.read_u8()?
                    } else {
                        code & 0x1f
                    };
                    (None, run as usize + 1)
                } else if compact {
                    let index_mask = if depth <= 3 { 7 } else { 15 };
                    let index = u32::from(code & index_mask);
                    let run = (code >> if depth <= 3 { 3 } else { 4 }) as usize + 1;
                    (Some(color_table(index, depth, grey_depth, grey)), run)
                } else {
                    match code >> 6 {
                        0 | 1 => {
                            let value = if depth > 7 {
                                u16::from(code) << 8 | u16::from(reader.read_u8()?)
                            } else {
                                u16::from(code)
                            };
                            (Some(rgb(u32::from(value), format)), 1)
                        }
                        2 => (Some(grey_value(u32::from(code & 0x3f), grey_depth)), 1),
                        _ => (None, usize::from(code & 0x1f) + 1),
                    }
                };
                let run = run.min(rectangle.width as usize - column);
                for _ in 0..run {
                    if let Some(color) = color {
                        previous[column] = color;
                    }
                    self.put_pixel(
                        rectangle.x as usize + column,
                        rectangle.y as usize + row,
                        previous[column],
                    );
                    column += 1;
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

fn grey_value(value: u32, depth: usize) -> u32 {
    let max = (1u32 << depth) - 1;
    let channel = value * 255 / max.max(1);
    0xff00_0000 | channel << 16 | channel << 8 | channel
}

fn color_table(index: u32, depth: usize, grey_depth: usize, grey: bool) -> u32 {
    if grey {
        return grey_value(index, grey_depth);
    }
    match depth {
        15 => rgb(index, PixelFormat::RGB565),
        7 => {
            let levels = [0, 64, 128, 192, 255];
            let index = index as usize;
            0xff00_0000 | levels[index / 25] << 16 | levels[index / 5 % 5] << 8 | levels[index % 5]
        }
        4 => [
            0xff00_0000,
            0xff80_0000,
            0xffff_0000,
            0xff00_8000,
            0xff80_8000,
            0xffff_ff00,
            0xff00_ff00,
            0xff00_0080,
            0xff80_0080,
            0xff00_ffff,
            0xff80_8080,
            0xffc0_c0c0,
            0xffff_00ff,
            0xff00_ffff,
            0xffff_ffff,
            0xff00_00ff,
        ][index as usize],
        _ => 0xff00_0000,
    }
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
}

impl RfbStream<TcpStream> {
    pub fn connect(host: &str) -> Result<Self> {
        let stream = TcpStream::connect((host, 443))
            .wrap_err_with(|| format!("connecting to {host}:443"))?;
        Ok(Self {
            stream,
            framebuffer_size: None,
        })
    }

    pub fn connect_raritan(host: &str, session_key: &str, port: &str) -> Result<Self> {
        let mut stream = Self::connect(host)?;
        stream.handshake(session_key, port)?;
        Ok(stream)
    }

    pub fn connect_raritan_tls(
        host: &str,
        session_id: &str,
        session_key: &str,
        port: &str,
    ) -> Result<RfbStream<SslStream<TcpStream>>> {
        info!(%host, %port, "connecting to RFP channel");
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

        let mut builder = SslConnector::builder(SslMethod::tls())?;
        builder.set_cipher_list("DEFAULT:@SECLEVEL=0")?;
        builder.set_min_proto_version(Some(SslVersion::TLS1))?;
        builder.set_max_proto_version(Some(SslVersion::TLS1))?;
        builder.set_verify(SslVerifyMode::NONE);
        let mut tls = builder.build().connect(host, socket)?;
        let challenge = String::from_utf8(read_csc_frame(&mut tls)?)?;
        debug!(
            length = challenge.len(),
            "received RFP authentication challenge"
        );
        let clear_text = xml_attribute(&challenge, "ClearText")
            .ok_or_else(|| eyre!("RFP authentication challenge lacks ClearText"))?;
        let key = STANDARD.decode(session_key)?;
        let encrypted = rc4(&key, &STANDARD.decode(clear_text)?)?;
        let random = b"1234567890";
        let mut response = Vec::with_capacity(random.len());
        let mut state = 0x5f37_59dfu32;
        for byte in random {
            state = state.rotate_left(5) ^ 0xa5a5_5a5a;
            response.push(*byte ^ state as u8);
        }
        let response = format!(
            r#"<CSC_Test2 Encrypted="{}" ClearText="{}"/>"#,
            STANDARD.encode(encrypted),
            STANDARD.encode(response)
        );
        write_csc_frame(&mut tls, response.as_bytes())?;
        let verification = String::from_utf8(read_csc_frame(&mut tls)?)?;
        let encrypted = xml_attribute(&verification, "Encrypted")
            .ok_or_else(|| eyre!("RFP authentication response lacks Encrypted"))?;
        if rc4(&key, &STANDARD.decode(encrypted)?)? != random {
            bail!("RFP session authentication failed");
        }
        info!("RFP CSC authentication succeeded");
        let mut stream = RfbStream::new(tls);
        stream.handshake(session_key, port)?;
        Ok(stream)
    }
}

impl<S: Read + Write> RfbStream<S> {
    pub fn new(stream: S) -> Self {
        Self {
            stream,
            framebuffer_size: None,
        }
    }

    pub fn framebuffer_size(&self) -> Option<(u16, u16)> {
        self.framebuffer_size
    }

    pub fn request_framebuffer_update(
        &mut self,
        width: u16,
        height: u16,
        incremental: bool,
    ) -> Result<()> {
        self.stream.write_all(&[
            3,
            u8::from(incremental),
            (width >> 8) as u8,
            width as u8,
            (height >> 8) as u8,
            height as u8,
            0,
            0,
            (width >> 8) as u8,
            width as u8,
            (height >> 8) as u8,
            height as u8,
        ])?;
        self.stream.flush()?;
        Ok(())
    }

    pub fn handshake(&mut self, session_key: &str, port: &str) -> Result<PixelFormat> {
        info!(%port, "starting RFB 1.29 handshake");
        self.stream.write_all(b"e-RIC AUTH=e-RIC RFB 01.29\n")?;
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

        let auth_type = read_u8(&mut self.stream)?;
        if auth_type != 32 {
            bail!("expected RFB auth capabilities, got {auth_type}");
        }
        let capabilities = read_u8(&mut self.stream)?;
        info!(capabilities, "received RFB authentication capabilities");
        if capabilities & 16 == 0 {
            bail!("RFB server does not offer RDM-session authentication");
        }
        self.stream
            .write_all(&[32, 16, 6, 0, 0, 0, 0, b's', b'u', b'p', b'e', b'r', 0])?;

        let message = read_u8(&mut self.stream)?;
        if message != 33 {
            bail!("expected RFB session challenge, got {message}");
        }
        let size = read_u8(&mut self.stream)? as usize;
        let mut challenge = vec![0; size];
        self.stream.read_exact(&mut challenge)?;
        self.stream.write_all(&[33])?;
        self.stream.write_all(session_key.as_bytes())?;
        self.stream.write_all(&[0])?;

        let success = read_u8(&mut self.stream)?;
        if success != 34 {
            bail!("RFB session authentication failed with message {success}");
        }
        info!("RFB session authentication succeeded");
        if read_u8(&mut self.stream)? != 5 {
            bail!("expected RFB server-init message");
        }
        let mut server_id = [0; 4];
        self.stream.read_exact(&mut server_id)?;
        let server_message = read_u8(&mut self.stream)?;
        if server_message != 7 {
            bail!("expected RFB client-init request, got {server_message}");
        }
        self.stream.write_all(&[7, 0, 0, 0])?;
        let port_bytes = port.as_bytes();
        if port_bytes.len() > u16::MAX as usize {
            bail!("port ID is too long");
        }
        self.stream.write_all(&[137, 0])?;
        self.stream
            .write_all(&(port_bytes.len() as u16).to_be_bytes())?;
        self.stream.write_all(port_bytes)?;
        debug!(%port, "selected KVM port on RFB channel");

        let format_type = read_u8(&mut self.stream)?;
        if format_type != 128 {
            bail!("expected RFB framebuffer format, got {format_type}");
        }
        let _unsupported = read_u8(&mut self.stream)?;
        let width = read_u16(&mut self.stream)?;
        let height = read_u16(&mut self.stream)?;
        let pixel_format = PixelFormat {
            bits_per_pixel: read_u8(&mut self.stream)?,
            depth: read_u8(&mut self.stream)?,
            big_endian: read_u8(&mut self.stream)? != 0,
            true_colour: read_u8(&mut self.stream)? != 0,
            red_max: read_u16(&mut self.stream)?,
            green_max: read_u16(&mut self.stream)?,
            blue_max: read_u16(&mut self.stream)?,
            red_shift: read_u8(&mut self.stream)?,
            green_shift: read_u8(&mut self.stream)?,
            blue_shift: read_u8(&mut self.stream)?,
        };
        let mut padding = [0; 3];
        self.stream.read_exact(&mut padding)?;
        self.stream.write_all(&[2, 0, 0, 1])?;
        self.stream.write_all(&0x0000_15ffu32.to_be_bytes())?;
        self.stream.write_all(&[0, 0, 0, 16, 16, 1, 1])?;
        self.stream.write_all(&31u16.to_be_bytes())?;
        self.stream.write_all(&63u16.to_be_bytes())?;
        self.stream.write_all(&31u16.to_be_bytes())?;
        self.stream.write_all(&[11, 5, 0, 0, 0, 0])?;
        self.stream
            .write_all(&[3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])?;
        self.framebuffer_size = Some((width, height));
        info!(width, height, ?pixel_format, "received framebuffer format");
        Ok(pixel_format)
    }

    pub fn read_message(&mut self) -> Result<FramebufferUpdate> {
        let message_type = read_u8(&mut self.stream)?;
        trace!(message_type, "received RFB server message");
        match message_type {
            FRAMEBUFFER_UPDATE => self.read_framebuffer_update(),
            PING_REQUEST => {
                let serial = read_u32(&mut self.stream)?;
                self.stream.write_all(&[PING_REPLY])?;
                self.stream.write_all(&serial.to_be_bytes())?;
                self.stream.flush()?;
                self.read_message()
            }
            PING_REPLY => {
                let _ = read_u32(&mut self.stream)?;
                self.read_message()
            }
            other => bail!("unsupported RFB server message type {other}"),
        }
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
            let size = read_u32(&mut reader)? as usize;
            let mut data = vec![0; size];
            reader.read_exact(&mut data)?;
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

fn xml_attribute(xml: &str, name: &str) -> Option<String> {
    let marker = format!("{name}=\"");
    let start = xml.find(&marker)? + marker.len();
    let end = xml[start..].find('"')? + start;
    Some(xml[start..end].to_owned())
}

fn escape_xml(value: &str) -> String {
    value
        .replace('&', "&amp;")
        .replace('"', "&quot;")
        .replace('<', "&lt;")
        .replace('>', "&gt;")
}

fn rc4(key: &[u8], input: &[u8]) -> Result<Vec<u8>> {
    if key.is_empty() {
        bail!("empty RC4 key");
    }
    let mut state = [0u8; 256];
    for (index, byte) in state.iter_mut().enumerate() {
        *byte = index as u8;
    }
    let mut j = 0usize;
    for i in 0..256 {
        j = (j + usize::from(state[i]) + usize::from(key[i % key.len()])) & 255;
        state.swap(i, j);
    }
    let mut i = 0usize;
    j = 0;
    let mut output = Vec::with_capacity(input.len());
    for byte in input {
        i = (i + 1) & 255;
        j = (j + usize::from(state[i])) & 255;
        state.swap(i, j);
        output.push(*byte ^ state[(usize::from(state[i]) + usize::from(state[j])) & 255]);
    }
    Ok(output)
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
}
