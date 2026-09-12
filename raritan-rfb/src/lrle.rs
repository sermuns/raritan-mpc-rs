//! LRLE hardware-encoding decoder.
//!
//! Mirrors `ImageDecoderLrle.decodeImage` from the Java client: 16×16
//! tiles, per-tile runs, line-copy via `prevLine`. LRLE colors are
//! format-independent (the pixel format only matters for Raw rects).

use crate::framebuffer::{Framebuffer, FramebufferRectangle, PixelFormat};
use eyre::{Result, bail, eyre};

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

pub(crate) struct SliceReader<'a> {
    data: &'a [u8],
    offset: usize,
}

impl<'a> SliceReader<'a> {
    pub(crate) fn new(data: &'a [u8]) -> Self {
        Self { data, offset: 0 }
    }

    pub(crate) fn read_u8(&mut self) -> Result<u8> {
        let value = *self
            .data
            .get(self.offset)
            .ok_or_else(|| eyre!("truncated LRLE data"))?;
        self.offset += 1;
        Ok(value)
    }
}

pub(crate) fn decode_lrle_rect(
    framebuffer: &mut Framebuffer,
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
    let mut previous = vec![0u32; framebuffer.width as usize];
    for tile_y in (0..height).step_by(16) {
        for tile_x in (0..width).step_by(16) {
            let tile_w = (width - tile_x).min(16);
            let tile_h = (height - tile_y).min(16);
            if conf.map {
                decode_lrle_map(
                    framebuffer,
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
            decode_lrle_run(
                framebuffer,
                &mut reader,
                rectangle,
                &colors,
                &greys,
                &conf,
                &mut previous,
                tile_x,
                tile_y,
                tile_w,
                tile_h,
            )?;
        }
    }
    Ok(())
}

#[allow(clippy::too_many_arguments)]
fn decode_lrle_run(
    framebuffer: &mut Framebuffer,
    reader: &mut SliceReader<'_>,
    rectangle: &FramebufferRectangle,
    colors: &[u32],
    greys: &[u32],
    conf: &LrleConfig,
    previous: &mut [u32],
    tile_x: usize,
    tile_y: usize,
    tile_w: usize,
    tile_h: usize,
) -> Result<()> {
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
            framebuffer.put_pixel(
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
    Ok(())
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
