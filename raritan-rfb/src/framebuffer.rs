//! Framebuffer model: Raw decoding plus dispatch into the LRLE decoder.
//!
//! LRLE internals live in [`crate::lrle`]; this module owns the pixel
//! buffer and the Raw path.

use crate::lrle::decode_lrle_rect;
use crate::proto::{
    ENCODING_AUTO_HW, ENCODING_LRLE_HARD, ENCODING_LRLE_SOFT, ENCODING_MASK, ENCODING_RAW,
};
use eyre::{Result, bail, eyre};

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
    /// Pixel buffer in RGBA byte order (egui/PPM-ready).
    pub rgba: Vec<u8>,
}

/// Hard cap for `width * height * 4` so a corrupt 128 message cannot
/// OOM the process (64 MiB; e.g. 4096×4096 RGBA fits, 65535×65535 does not).
pub const MAX_FRAMEBUFFER_BYTES: usize = 64 * 1024 * 1024;

impl Framebuffer {
    /// Validated constructor: rejects zero dimensions and oversize buffers.
    pub fn try_new(width: u16, height: u16) -> Result<Self> {
        if width == 0 || height == 0 {
            bail!("invalid framebuffer dimensions: {width}x{height}");
        }
        let len = (width as usize)
            .checked_mul(height as usize)
            .and_then(|pixels| pixels.checked_mul(4))
            .ok_or_else(|| eyre!("framebuffer dimensions overflow: {width}x{height}"))?;
        if len > MAX_FRAMEBUFFER_BYTES {
            bail!("framebuffer dimensions too large: {width}x{height}");
        }
        Ok(Self {
            width,
            height,
            rgba: vec![0; len],
        })
    }

    pub fn new(width: u16, height: u16) -> Self {
        Self::try_new(width, height).expect("framebuffer dimensions too large")
    }

    pub fn apply_update(
        &mut self,
        update: &FramebufferUpdate,
        pixel_format: PixelFormat,
    ) -> Result<()> {
        for rectangle in &update.rectangles {
            match (rectangle.encoding as u32 & ENCODING_MASK) as u8 {
                ENCODING_RAW => self.decode_raw(rectangle, pixel_format)?,
                ENCODING_LRLE_SOFT | ENCODING_LRLE_HARD | ENCODING_AUTO_HW => {
                    decode_lrle_rect(self, rectangle, pixel_format)?
                }
                encoding => bail!("unsupported framebuffer encoding {encoding}"),
            }
        }
        Ok(())
    }

    fn decode_raw(&mut self, rectangle: &FramebufferRectangle, format: PixelFormat) -> Result<()> {
        let bytes_per_pixel = match format.bits_per_pixel {
            8 => 1usize,
            16 => 2usize,
            32 => 4usize,
            other => bail!("unsupported pixel format: {other} bits"),
        };
        let expected = (rectangle.width as usize)
            .checked_mul(rectangle.height as usize)
            .and_then(|pixels| pixels.checked_mul(bytes_per_pixel))
            .ok_or_else(|| eyre!("raw rectangle size overflow"))?;
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

    /// Paints one `0xAARRGGBB` pixel (the Java int-pixel convention used
    /// by all decoder tables) into the RGBA buffer.
    pub(crate) fn put_pixel(&mut self, x: usize, y: usize, color: u32) {
        if x >= self.width as usize || y >= self.height as usize {
            return;
        }
        let offset = (y * self.width as usize + x) * 4;
        let [a, r, g, b] = color.to_be_bytes();
        self.rgba[offset..offset + 4].copy_from_slice(&[r, g, b, a]);
    }
}

pub fn rgb(value: u32, format: PixelFormat) -> u32 {
    let red = ((value >> format.red_shift) & u32::from(format.red_max)) * 255
        / u32::from(format.red_max.max(1));
    let green = ((value >> format.green_shift) & u32::from(format.green_max)) * 255
        / u32::from(format.green_max.max(1));
    let blue = ((value >> format.blue_shift) & u32::from(format.blue_max)) * 255
        / u32::from(format.blue_max.max(1));
    0xff00_0000 | (red << 16) | (green << 8) | blue
}

#[cfg(test)]
mod tests {
    use super::*;

    /// Guards the buffer byte order: a raw RGB565 red + blue pair must
    /// land as RGBA bytes (egui/PPM read R,G,B,A in order). Storing the
    /// native `0xAARRGGBB` int directly tints everything red.
    #[test]
    fn pixels_are_stored_in_rgba_order() {
        let mut framebuffer = Framebuffer::new(2, 1);
        framebuffer
            .apply_update(
                &FramebufferUpdate {
                    flags: 0,
                    rectangles: vec![FramebufferRectangle {
                        x: 0,
                        y: 0,
                        width: 2,
                        height: 1,
                        encoding: 0,
                        data: vec![0xF8, 0x00, 0x00, 0x1F],
                    }],
                },
                PixelFormat::RGB565,
            )
            .unwrap();
        assert_eq!(framebuffer.rgba, vec![255, 0, 0, 255, 0, 0, 255, 255]);
    }
}
