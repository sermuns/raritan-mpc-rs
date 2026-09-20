//! LRLE hardware-encoding decoder, mirroring `ImageDecoderLrle.decodeImage`:
//! 16×16 tiles, per-tile runs, line-copy via `prevLine`. Colors are
//! format-independent (pixel format only matters for Raw rects).

use crate::framebuffer::{Framebuffer, FramebufferRectangle, PixelFormat};
use eyre::{Result, bail, eyre};
use raritan_common::read_u8;
use std::io::Cursor;

/// Decoder configuration mirroring `LRLEColorDecoderConf`,
/// indexed by subencoding.
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

fn grey_color(grey_depth: usize, idx: usize) -> Option<u32> {
    if grey_depth == 0 || grey_depth > 8 || idx >= (1usize << grey_depth) {
        return None;
    }
    let n = idx as u32;
    let channel = match grey_depth {
        1 => n * 255,
        2 => n * 85,
        3 => n * 73 / 2,
        4 => n * 17,
        5 => n * 33 / 4,
        6 => n * 65 / 16,
        _ => return None,
    };
    Some(0xff00_0000 | channel << 16 | channel << 8 | channel)
}

#[expect(dead_code)]
// Keep helper for tests that still want a Vec, but hot path uses `grey_color`.
fn lrle_greys(grey_depth: usize) -> Vec<u32> {
    (0..1usize << grey_depth)
        .filter_map(|idx| grey_color(grey_depth, idx))
        .collect()
}

const COLORS_4: [u32; 16] = [
    0xff00_0000, 0xff7f_0000, 0xff00_7f00, 0xff7f_7f00, 0xff00_007f, 0xff7f_007f, 0xff00_7f7f, 0xff7f_7f7f,
    0xffc0_c0c0, 0xffff_0000, 0xff00_ff00, 0xffff_ff00, 0xff00_00ff, 0xffff_00ff, 0xff00_ffff, 0xffff_ffff,
];

fn lrle_color(conf: &LrleConfig, idx: usize) -> Option<u32> {
    if conf.grey {
        return grey_color(conf.grey_depth, idx);
    }
    match conf.depth {
        15 => {
            if idx >= 1usize << 15 {
                return None;
            }
            let n = idx as u32;
            let red = ((n & 0x7c00) >> 10) * 33 / 4;
            let green = ((n & 0x03e0) >> 5) * 33 / 4;
            let blue = (n & 0x001f) * 33 / 4;
            Some(0xff00_0000 | (red << 16) | (green << 8) | blue)
        }
        7 => {
            if idx >= 128 {
                return None;
            }
            let n = idx as u32;
            if n < 125 {
                const LEVELS: [u32; 5] = [0, 64, 128, 192, 255];
                Some(
                    0xff00_0000
                        | LEVELS[n as usize / 25] << 16
                        | LEVELS[n as usize / 5 % 5] << 8
                        | LEVELS[n as usize % 5],
                )
            } else {
                Some(0xffff_0000)
            }
        }
        4 => COLORS_4.get(idx).copied(),
        _ => None,
    }
}

#[expect(dead_code)]
fn lrle_colors(conf: &LrleConfig) -> Vec<u32> {
    // Retained for tests / non-hot paths; hot path uses `lrle_color` without alloc.
    if conf.grey {
        return lrle_greys(conf.grey_depth);
    }
    match conf.depth {
        15 => (0..1usize << 15).filter_map(|idx| lrle_color(conf, idx)).collect(),
        7 => (0..128).filter_map(|idx| lrle_color(conf, idx)).collect(),
        4 => COLORS_4.to_vec(),
        _ => vec![0xff00_0000],
    }
}

pub(crate) fn decode_lrle_rect(
    framebuffer: &mut Framebuffer,
    rectangle: &FramebufferRectangle,
    _format: PixelFormat,
) -> Result<()> {
    let subencoding = ((rectangle.encoding as u32 >> 12) & 0xf) as usize;
    let conf = lrle_config(subencoding)?;
    let mut reader = Cursor::new(rectangle.data.as_slice());
    let width = rectangle.width as usize;
    let height = rectangle.height as usize;
    // Line-copy state per tile column (Java's tile-scoped `prevLine`).
    let mut previous = [0u32; 16];
    for tile_y in (0..height).step_by(16) {
        for tile_x in (0..width).step_by(16) {
            let tile_w = (width - tile_x).min(16);
            let tile_h = (height - tile_y).min(16);
            if conf.map {
                decode_lrle_map(
                    framebuffer,
                    &mut reader,
                    rectangle,
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

#[expect(clippy::too_many_arguments)]
fn decode_lrle_run(
    framebuffer: &mut Framebuffer,
    reader: &mut Cursor<&[u8]>,
    rectangle: &FramebufferRectangle,
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
        let code = read_u8(reader)?;
        let run: usize;
        if code & 0xe0 == 0xe0 {
            copy = true;
            run = if code == 0xff {
                read_u8(reader)? as usize
            } else {
                (code & 0x1f) as usize
            };
        } else if conf.compact {
            copy = false;
            if conf.depth <= 3 {
                let index = (code & 7) as usize;
                color = lrle_color(conf, index)
                    .ok_or_else(|| eyre!("invalid LRLE compact color index {index}"))?;
                run = (code >> 3) as usize;
            } else {
                let index = (code & 0xf) as usize;
                color = lrle_color(conf, index)
                    .ok_or_else(|| eyre!("invalid LRLE compact color index {index}"))?;
                run = (code >> 4) as usize;
            }
        } else {
            match code >> 6 {
                0 | 1 => {
                    let index = if conf.depth > 7 {
                        (u16::from(code) << 8 | u16::from(read_u8(reader)?)) as usize
                    } else {
                        code as usize
                    };
                    color = lrle_color(conf, index)
                        .ok_or_else(|| eyre!("invalid LRLE color index {index}"))?;
                    run = 0;
                    copy = false;
                }
                2 => {
                    let index = (code & 0x3f) as usize;
                    color = grey_color(conf.grey_depth, index)
                        .ok_or_else(|| eyre!("invalid LRLE grey index {index}"))?;
                    run = 0;
                    copy = false;
                }
                _ => {
                    run = (code & 0x1f) as usize;
                }
            }
        }
        // Bound the run to this tile: a corrupt run must not paint past the
        // edge (clipping would hide the overrun and desync the stream).
        let painted = row * tile_w + column;
        let remaining = tile_w * tile_h - painted;
        let run = run.min(remaining.saturating_sub(1));
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
#[expect(clippy::too_many_arguments)]
fn decode_lrle_map(
    framebuffer: &mut Framebuffer,
    reader: &mut Cursor<&[u8]>,
    rectangle: &FramebufferRectangle,
    grey_depth: usize,
    tile_x: usize,
    tile_y: usize,
    tile_w: usize,
    tile_h: usize,
) -> Result<()> {
    if grey_depth == 0 || grey_depth > 8 {
        bail!("invalid LRLE grey depth {grey_depth}");
    }
    let group = 8 / grey_depth;
    let mask = (1u32 << grey_depth) - 1;
    for row in 0..tile_h {
        let mut col = 0;
        while col < tile_w {
            // A short tail chunk packs into the low bits; pixel `j` sits
            // `(chunk - 1 - j)` slots from the LSB (MSB first).
            let chunk = (tile_w - col).min(group);
            let byte = read_u8(reader)?;
            for j in 0..chunk {
                let index = ((u32::from(byte) >> ((chunk - 1 - j) * grey_depth)) & mask) as usize;
                let color = grey_color(grey_depth, index)
                    .ok_or_else(|| eyre!("invalid LRLE map grey index {index}"))?;
                framebuffer.put_pixel(
                    rectangle.x as usize + tile_x + col + j,
                    rectangle.y as usize + tile_y + row,
                    color,
                );
            }
            col += chunk;
        }
    }
    Ok(())
}

#[cfg(test)]
mod tests {
    use super::*;

    /// 6 px wide, `grey_depth` 2: one full 4-pixel group plus a 2-pixel
    /// tail chunk in the low bits, both MSB first.
    #[test]
    fn map_path_handles_remainder_chunk() {
        let mut framebuffer = Framebuffer::try_new(6, 1).unwrap();
        decode_lrle_rect(
            &mut framebuffer,
            &FramebufferRectangle {
                x: 0,
                y: 0,
                width: 6,
                height: 1,
                encoding: 0xA080, // subencoding 10 (2-bit grey map) + HW base
                data: vec![0x1B, 0x0C],
            },
            PixelFormat::RGB565,
        )
        .unwrap();
        // Grey ramp for depth 2 is n*85: 0, 85, 170, 255.
        assert_eq!(
            framebuffer.rgba,
            vec![
                0, 0, 0, 255, 85, 85, 85, 255, 170, 170, 170, 255, 255, 255, 255, 255, 255, 255,
                255, 255, 0, 0, 0, 255,
            ]
        );
    }
}
