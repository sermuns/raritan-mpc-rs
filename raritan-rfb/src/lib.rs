use eyre::{Result, WrapErr, bail, eyre};
use std::io::{Read, Write};
use std::net::TcpStream;

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
    pub big_endian: bool,
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
        big_endian: true,
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
                128 => self.decode_lrle(rectangle, pixel_format)?,
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
}

impl RfbStream<TcpStream> {
    pub fn connect(host: &str) -> Result<Self> {
        let stream = TcpStream::connect((host, 443))
            .wrap_err_with(|| format!("connecting to {host}:443"))?;
        Ok(Self { stream })
    }
}

impl<S: Read + Write> RfbStream<S> {
    pub fn new(stream: S) -> Self {
        Self { stream }
    }

    pub fn read_message(&mut self) -> Result<FramebufferUpdate> {
        let message_type = read_u8(&mut self.stream)?;
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

        if flags & 1 != 0 {
            let _timestamp_seconds = read_u32(&mut self.stream)?;
            let _timestamp_micros = read_u32(&mut self.stream)?;
        }

        let mut rectangles = Vec::with_capacity(count);
        let mut consumed = 0usize;
        for _ in 0..count {
            let x = read_u16(&mut self.stream)?;
            let y = read_u16(&mut self.stream)?;
            let width = read_u16(&mut self.stream)?;
            let height = read_u16(&mut self.stream)?;
            let encoding = read_i32(&mut self.stream)?;
            let size = read_u32(&mut self.stream)? as usize;
            let mut data = vec![0; size];
            self.stream.read_exact(&mut data)?;
            consumed = consumed
                .checked_add(12 + size)
                .ok_or_else(|| eyre!("framebuffer update size overflow"))?;
            rectangles.push(FramebufferRectangle {
                x,
                y,
                width,
                height,
                encoding,
                data,
            });
        }

        if consumed != update_size {
            bail!("framebuffer update size mismatch: header says {update_size}, read {consumed}");
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

#[cfg(test)]
mod tests {
    use super::*;
    use std::io::Cursor;

    #[test]
    fn parses_rfb_framebuffer_update() {
        let mut bytes = vec![0, 0, 2];
        bytes.extend_from_slice(&28u32.to_be_bytes());
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
