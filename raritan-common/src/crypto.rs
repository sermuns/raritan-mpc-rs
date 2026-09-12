//! RC4 + base64 helpers for the `CSC_Test2` event/referral handshake
//! (`CSCConnect.CSC_Test` in the Java client).

use base64::{Engine as _, engine::general_purpose::STANDARD};
use eyre::{Context, Result, bail};

pub fn encode_base64(bytes: &[u8]) -> String {
    STANDARD.encode(bytes)
}

pub fn decode_base64(value: &str) -> Result<Vec<u8>> {
    STANDARD
        .decode(
            value
                .bytes()
                .filter(|byte| !byte.is_ascii_whitespace())
                .collect::<Vec<_>>(),
        )
        .wrap_err("invalid base64 in CSC_Test2 exchange")
}

/// Time-XORed probe (`"1234567890"` for the event session). Mirrors
/// `CSCConnect.CSC_Test`: each probe byte is XORed with a millisecond
/// clock that is re-sampled and shifted per byte.
pub fn time_xored_probe(probe: &[u8]) -> Vec<u8> {
    let mut clear = Vec::with_capacity(probe.len());
    let mut state = current_time_millis();
    for byte in probe {
        clear.push(*byte ^ state as u8);
        state = (state >> 3) ^ current_time_millis();
    }
    clear
}

/// The event-session probe string from the Java client.
pub fn event_probe() -> Vec<u8> {
    time_xored_probe(b"1234567890")
}

pub fn current_time_millis() -> i32 {
    std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .expect("system clock is before Unix epoch")
        .as_millis() as i32
}

pub fn rc4(key: &[u8], input: &[u8]) -> Result<Vec<u8>> {
    if key.is_empty() {
        bail!("empty CSC session key");
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

    #[test]
    fn rc4_roundtrips() {
        let key = b"secret";
        let plain = b"hello world";
        let cipher = rc4(key, plain).unwrap();
        assert_ne!(cipher, plain);
        assert_eq!(rc4(key, &cipher).unwrap(), plain);
    }

    #[test]
    fn rejects_empty_key() {
        assert!(rc4(&[], b"x").is_err());
    }
}
