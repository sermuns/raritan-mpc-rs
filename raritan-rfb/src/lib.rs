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
