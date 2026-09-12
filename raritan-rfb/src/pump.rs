//! Steady-state message pump: pings, bandwidth handshake, late format
//! changes, framebuffer updates, and skipping everything else.

use crate::{
    framebuffer::{FramebufferRectangle, FramebufferUpdate, PixelFormat},
    proto::{
        ACK_PIXEL_FORMAT, BANDWIDTH_REPLY, BANDWIDTH_REQUEST, CONNECTION_PARAMETERS,
        FRAMEBUFFER_UPDATE, KEYBOARD_LAYOUT, OSD_STATE, PING_REPLY, PING_REPLY_OUT, PING_REQUEST,
        PORT_LIST, SERVER_COMMAND, SERVER_FB_FORMAT, SERVER_INIT, SERVER_RC_MESSAGE,
        USB_PROFILE_LIST, USER_NOTIFICATION, UTF8_STRING, VIDEO_QUALITY_S2C, VIDEO_SETTINGS_S2C,
        VIRTUAL_MEDIA_CONFIG, VM_MOUNTS_RESPONSE, VM_SHARE_TABLE,
    },
    stream::RfbStream,
};
use eyre::{Result, bail};
use flate2::read::ZlibDecoder;
use raritan_common::{read_i32, read_u8, read_u16, read_u32};
use std::io::{Read, Write};
use tracing::{debug, info, trace, warn};

impl<S: Read + Write> RfbStream<S> {
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
                    // RfbHandler.processBandwidthRequest: reply(1), read, reply(2).
                    self.write_bandwidth_reply(1)?;
                    self.read_bandwidth_request()?;
                    self.write_bandwidth_reply(2)?;
                }
                SERVER_FB_FORMAT => {
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

    fn write_bandwidth_reply(&mut self, stage: u8) -> Result<()> {
        self.stream.write_all(&[BANDWIDTH_REPLY, stage])?;
        self.stream.flush()?;
        Ok(())
    }

    /// `[18][count][klen,vlen,key,value]*`.
    pub(crate) fn read_connection_parameters(&mut self) -> Result<Vec<(String, String)>> {
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

    /// `[7][pad][len:u16be][bytes]`.
    pub(crate) fn read_utf8_string(&mut self) -> Result<String> {
        let _pad = read_u8(&mut self.stream)?;
        let len = read_u16(&mut self.stream)? as usize;
        let mut bytes = vec![0; len];
        self.stream.read_exact(&mut bytes)?;
        Ok(String::from_utf8_lossy(&bytes).into_owned())
    }

    /// `[128][unsupported][w][h][pixfmt16][pad*3]`.
    pub(crate) fn read_server_fb_format(&mut self) -> Result<(u16, u16, PixelFormat)> {
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
        let _pad = read_u8(&mut self.stream)?;
        let len = read_u16(&mut self.stream)? as usize;
        let mut bytes = vec![0; len];
        self.stream.read_exact(&mut bytes)?;
        Ok(())
    }

    /// Consume and discard any server message that carries no video data,
    /// keeping the stream aligned.
    pub(crate) fn skip_server_message(&mut self, message_type: u8) -> Result<()> {
        trace!(message_type, "skipping RFB server message");
        match message_type {
            USER_NOTIFICATION => {
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
                let mut rest = [0; 27];
                self.stream.read_exact(&mut rest)?;
            }
            KEYBOARD_LAYOUT => {
                let _pad = read_u8(&mut self.stream)?;
                self.skip_blob16()?;
            }
            OSD_STATE => {
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
                let mut rest = [0; 19];
                self.stream.read_exact(&mut rest)?;
            }
            SERVER_RC_MESSAGE => {
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
                let mut rest = [0; 7];
                self.stream.read_exact(&mut rest)?;
            }
            VM_SHARE_TABLE => {
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
                // Hardware encoding: true size follows.
                size = read_i32(&mut reader)? as usize;
            }
            if is_lrle && ((encoding as u32 & 0xf00) != 0 || (encoding as u32 & 0x20000) != 0) {
                bail!("zlib-streamed framebuffer rects are not supported");
            }
            let mut data = vec![0; size];
            reader.read_exact(&mut data)?;
            if is_lrle {
                // Rects are 4-byte aligned.
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
