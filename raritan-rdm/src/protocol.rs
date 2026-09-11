use eyre::{OptionExt, Result, eyre};
use std::io::{Read, Write};

const MAX_FRAME_SIZE: usize = 1024 * 1024;

pub fn write_frame<W: Write>(stream: &mut W, payload: &str) -> Result<()> {
    let payload = payload.as_bytes();
    let frame_len = payload.len().checked_add(5).ok_or_eyre("frame too large")?;
    let frame_len = u32::try_from(frame_len).map_err(|_| eyre!("frame too large"))?;
    stream.write_all(&frame_len.to_be_bytes())?;
    stream.write_all(payload)?;
    stream.write_all(&[0])?;
    stream.flush()?;
    Ok(())
}

pub fn read_frame<R: Read>(stream: &mut R) -> Result<Vec<u8>> {
    let mut header = [0; 4];
    stream.read_exact(&mut header)?;
    let frame_len = u32::from_be_bytes(header) as usize;
    if !(5..=MAX_FRAME_SIZE).contains(&frame_len) {
        return Err(eyre!("invalid CSC frame length: {frame_len}"));
    }
    let mut payload = vec![0; frame_len - 4];
    stream.read_exact(&mut payload)?;
    if payload.pop() != Some(0) {
        return Err(eyre!("CSC frame was not NUL terminated"));
    }
    Ok(payload)
}

pub fn display_xml(bytes: &[u8]) -> String {
    String::from_utf8_lossy(bytes)
        .trim_end_matches('\0')
        .to_owned()
}

pub fn escape_xml(value: &str) -> String {
    value
        .replace('&', "&amp;")
        .replace('"', "&quot;")
        .replace('<', "&lt;")
        .replace('>', "&gt;")
        .replace('\'', "&apos;")
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::io::Cursor;

    #[test]
    fn frames_are_big_endian_and_nul_terminated() {
        let mut bytes = Cursor::new(Vec::new());
        write_frame(&mut bytes, "<CSC/>").unwrap();
        assert_eq!(bytes.into_inner(), b"\0\0\0\x0b<CSC/>\0");
    }
}
