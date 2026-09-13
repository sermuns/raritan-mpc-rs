//! RC4 + base64 helpers for the `CSC_Test2` event/referral handshake
//! (`CSCConnect.CSC_Test` in the Java client).

use base64::{Engine as _, engine::general_purpose::STANDARD};
use eyre::{Context, Result, bail};
use rc4::{KeyInit, Rc4, StreamCipher};

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
    // Mirrors Java's 32-bit `int` millisecond clock (wraps roughly every
    // 24 days). Never panics: a pre-epoch clock yields 0.
    std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .map(|duration| duration.as_millis() as u64 as i32)
        .unwrap_or(0)
}

/// One-shot RC4 for the `CSC_Test2` challenge/response.
///
/// Delegates to the [`rc4`] crate (legacy-interop cipher, sync API).
/// Keys must be 1–256 bytes; empty keys are rejected explicitly since the
/// switch's session keys are always non-empty.
pub fn rc4(key: &[u8], input: &[u8]) -> Result<Vec<u8>> {
    if key.is_empty() {
        bail!("empty CSC session key");
    }
    let mut cipher =
        Rc4::new_from_slice(key).map_err(|error| eyre::eyre!("invalid RC4 key: {error}"))?;
    let mut output = input.to_vec();
    cipher.apply_keystream(&mut output);
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
    fn matches_standard_test_vector() {
        // RFC-style vector from the `rc4` crate docs.
        let cipher = rc4(b"Key", b"Plaintext").unwrap();
        assert_eq!(
            cipher,
            [0xBB, 0xF3, 0x16, 0xE8, 0xD9, 0x40, 0xAF, 0x0A, 0xD3]
        );
    }

    #[test]
    fn rejects_empty_key() {
        assert!(rc4(&[], b"x").is_err());
    }
}
