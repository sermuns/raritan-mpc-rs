//! Client → server input events: keyboard, pointer, video settings.
//!
//! Key events (`RfbKeyEventMsgV01_27`): `[4, 0, keysym:u16-be]`, the Eric code
//! with the high bit set for presses (confirmed against a Java capture).
//!
//! Pointer events (`RfbPointerEventMsgV01_22`):
//! `[5, buttons, x:u16-be, y:u16-be, wheel:u16-be]` with the standard RFB
//! button mask; wheel-only events carry `x = y = 0` (also verified by capture).
//!
//! Eric codes are Raritan's own numbering (`KeyTranslatorBase.addKeys`, `en_US`),
//! keyed by Java key code + location (1 = standard, 2 = left, 3 = right, 4 = numpad).

use crate::{
    proto::{KEY_EVENT, POINTER_EVENT},
    stream::RfbStream,
};
use eyre::{Result, bail};
use std::io::{Read, Write};

/// Outbound commands for the video worker.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum VideoCommand {
    Key {
        eric: u16,
        down: bool,
    },
    Pointer {
        buttons: u8,
        x: u16,
        y: u16,
        wheel: u16,
    },
    VideoSettings {
        setting: u8,
        value: u16,
    },
}

impl<S: Read + Write> RfbStream<S> {
    pub fn write_key_event(&mut self, eric: u16, down: bool) -> Result<()> {
        if eric & 0x8000 != 0 {
            bail!("Eric code out of range: {eric:#x}");
        }
        let code = eric | (u16::from(down) << 15);
        self.stream.write_all(&[KEY_EVENT, 0])?;
        self.stream.write_all(&code.to_be_bytes())?;
        self.stream.flush()?;
        Ok(())
    }

    pub fn write_pointer_event(&mut self, buttons: u8, x: u16, y: u16, wheel: u16) -> Result<()> {
        let mut message = [0u8; 8];
        message[0] = POINTER_EVENT;
        message[1] = buttons;
        message[2..4].copy_from_slice(&x.to_be_bytes());
        message[4..6].copy_from_slice(&y.to_be_bytes());
        message[6..8].copy_from_slice(&wheel.to_be_bytes());
        self.stream.write_all(&message)?;
        self.stream.flush()?;
        Ok(())
    }

    /// Video-settings action (type 144): 19 = color calibration, 18 = auto-sense.
    pub fn write_video_settings_event(&mut self, setting: u8, value: u16) -> Result<()> {
        self.stream.write_all(&[144, setting])?;
        self.stream.write_all(&value.to_be_bytes())?;
        self.stream.flush()?;
        Ok(())
    }

    /// Releases every key code (0–137): the switch holds per-target key state,
    /// so a lost release would stick forever. No-op for keys not down, safe
    /// on every connect.
    pub fn release_all_keys(&mut self) -> Result<()> {
        // One write for all 138 releases instead of 138 tiny packets.
        let mut batch = Vec::with_capacity(138 * 4);
        for eric in 0..=137u16 {
            batch.extend_from_slice(&[KEY_EVENT, 0, (eric >> 8) as u8, eric as u8]);
        }
        self.stream.write_all(&batch)?;
        self.stream.flush()?;
        Ok(())
    }
}

/// Java key code + location → wire Eric code, mirroring
/// `KeyTranslator.translateKeyEvent` (location 0 falls back to 1).
pub fn eric_code(java_code: i32, location: i32) -> Option<u16> {
    if let Some(eric) = eric_by_code(java_code, location) {
        return Some(eric);
    }
    if location == 0 {
        return eric_by_code(java_code, 1);
    }
    None
}

// Lookup table mirroring the Java key map: one arm per entry, so identical
// bodies are expected rather than merged.
#[allow(clippy::too_many_lines, clippy::match_same_arms)]
fn eric_by_code(java_code: i32, location: i32) -> Option<u16> {
    let eric = match (java_code, location) {
        // Top row (backquote … equals) + editing keys.
        (192, 1) => 0,
        (49, 1) => 1,
        (50, 1) => 2,
        (51, 1) => 3,
        (52, 1) => 4,
        (53, 1) => 5,
        (54, 1) => 6,
        (55, 1) => 7,
        (56, 1) => 8,
        (57, 1) => 9,
        (48, 1) => 10,
        (45, 1) => 11,
        (61, 1) => 12,
        (8, 1) => 13,
        (9, 1) => 14,
        // QWERTY row + Enter.
        (81, 1) => 15,
        (87, 1) => 16,
        (69, 1) => 17,
        (82, 1) => 18,
        (84, 1) => 19,
        (89, 1) => 20,
        (85, 1) => 21,
        (73, 1) => 22,
        (79, 1) => 23,
        (80, 1) => 24,
        (91, 1) => 25,
        (93, 1) => 26,
        (10, 1) => 27,
        // Home row.
        (20, 1) => 28,
        (65, 1) => 29,
        (83, 1) => 30,
        (68, 1) => 31,
        (70, 1) => 32,
        (71, 1) => 33,
        (72, 1) => 34,
        (74, 1) => 35,
        (75, 1) => 36,
        (76, 1) => 37,
        (59, 1) => 38,
        (222, 1) => 39,
        (92, 1) => 40,
        // Left shift + bottom row.
        (16, 2) => 41,
        (90, 1) => 43,
        (88, 1) => 44,
        (67, 1) => 45,
        (86, 1) => 46,
        (66, 1) => 47,
        (78, 1) => 48,
        (77, 1) => 49,
        (44, 1) => 50,
        (46, 1) => 51,
        (47, 1) => 52,
        // Right shift, control, alt, space.
        (16, 3) => 53,
        (272, 3) => 53,
        (17, 2) => 54,
        (18, 2) => 55,
        (32, 1) => 56,
        (65406, 3) => 57,
        (65406, 1) => 57,
        (18, 3) => 57,
        (17, 3) => 58,
        (273, 3) => 58,
        // Escape + function keys.
        (27, 1) => 59,
        (112, 1) => 60,
        (113, 1) => 61,
        (114, 1) => 62,
        (115, 1) => 63,
        (116, 1) => 64,
        (117, 1) => 65,
        (118, 1) => 66,
        (119, 1) => 67,
        (120, 1) => 68,
        (121, 1) => 69,
        (122, 1) => 70,
        (123, 1) => 71,
        // Navigation block.
        (154, 1) => 72,
        (145, 1) => 73,
        (19, 1) => 74,
        (155, 1) => 75,
        (36, 1) => 76,
        (33, 1) => 77,
        (127, 1) => 78,
        (35, 1) => 79,
        (34, 1) => 80,
        (38, 1) => 81,
        (37, 1) => 82,
        (40, 1) => 83,
        (39, 1) => 84,
        // Numpad.
        (224, 4) => 87,
        (226, 4) => 91,
        (225, 4) => 96,
        (227, 4) => 93,
        (144, 4) => 85,
        (103, 4) => 86,
        (104, 4) => 87,
        (105, 4) => 88,
        (36, 4) => 86,
        (38, 4) => 87,
        (33, 4) => 88,
        (107, 4) => 89,
        (111, 4) => 90,
        (100, 4) => 91,
        (101, 4) => 92,
        (102, 4) => 93,
        (37, 4) => 91,
        (12, 4) => 92,
        (65368, 4) => 92,
        (39, 4) => 93,
        (106, 4) => 94,
        (97, 4) => 95,
        (98, 4) => 96,
        (99, 4) => 97,
        (35, 4) => 95,
        (40, 4) => 96,
        (34, 4) => 97,
        (10, 4) => 98,
        (109, 4) => 99,
        (96, 4) => 100,
        (155, 4) => 100,
        (110, 4) => 101,
        (127, 4) => 101,
        (61, 4) => 132,
        (317, 4) => 132,
        // Meta / Windows / Sun keys.
        (157, 0) => 105,
        (157, 2) => 105,
        (268, 3) => 107,
        (524, 2) => 105,
        (525, 1) => 106,
        (243, 0) => 0,
        (244, 0) => 0,
        (29, 0) => 108,
        (28, 0) => 109,
        (241, 0) => 110,
        (242, 0) => 110,
        (245, 0) => 110,
        (262, 0) => 115,
        (263, 0) => 116,
        (65480, 0) => 117,
        (65481, 0) => 118,
        (65483, 0) => 119,
        (65489, 0) => 120,
        (65485, 0) => 121,
        (65487, 0) => 122,
        (65488, 0) => 123,
        (65482, 0) => 127,
        (156, 0) => 129,
        (65312, 0) => 130,
        (456, 0) => 117,
        (457, 0) => 118,
        (459, 0) => 119,
        (465, 0) => 120,
        (461, 0) => 121,
        (463, 0) => 122,
        (464, 0) => 123,
        (468, 0) => 124,
        (469, 0) => 125,
        (470, 0) => 126,
        (458, 0) => 127,
        (466, 0) => 128,
        (471, 0) => 130,
        (467, 0) => 131,
        (128, 1) => 0,
        (129, 1) => 39,
        (61440, 1) => 134,
        (61441, 1) => 135,
        (61442, 1) => 136,
        (61443, 1) => 137,
        _ => return None,
    };
    Some(eric)
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::io::Cursor;

    #[test]
    fn hello_world_enter_matches_capture() {
        let cases = [
            (72, 0x22),
            (69, 0x11),
            (76, 0x25),
            (79, 0x17),
            (32, 0x38),
            (87, 0x10),
            (82, 0x12),
            (68, 0x1f),
            (10, 0x1b),
        ];
        for (java_code, eric) in cases {
            assert_eq!(eric_code(java_code, 1), Some(eric), "java {java_code}");
        }
    }

    #[test]
    fn location_zero_falls_back_to_standard() {
        assert_eq!(eric_code(69, 0), Some(17));
        assert_eq!(eric_code(16, 2), Some(41));
        assert_eq!(eric_code(16, 3), Some(53));
        assert_eq!(eric_code(999, 1), None);
    }

    #[test]
    fn key_event_bytes_match_java_client() {
        let mut stream = RfbStream::new(Cursor::new(Vec::new()));
        stream.write_key_event(0x22, true).unwrap();
        stream.write_key_event(0x22, false).unwrap();
        assert_eq!(
            stream.stream.into_inner(),
            vec![4, 0, 0x80, 0x22, 4, 0, 0, 0x22]
        );
    }

    #[test]
    fn release_all_clears_every_code() {
        let mut stream = RfbStream::new(Cursor::new(Vec::new()));
        stream.release_all_keys().unwrap();
        let bytes = stream.stream.into_inner();
        assert_eq!(bytes.len(), 138 * 4);
        assert_eq!(&bytes[0..4], &[4, 0, 0, 0]);
        assert_eq!(&bytes[bytes.len() - 4..], &[4, 0, 0, 137]);
        for chunk in bytes.as_chunks::<4>().0 {
            assert_eq!(chunk[0], 4);
            assert_eq!(chunk[1], 0);
            assert_eq!(chunk[2] & 0x80, 0);
        }
    }

    #[test]
    fn pointer_event_bytes_match_java_client() {
        // Real captured move + left click at (465, 436).
        let mut stream = RfbStream::new(Cursor::new(Vec::new()));
        stream.write_pointer_event(0, 0xb5, 0, 0).unwrap();
        stream.write_pointer_event(1, 0x01d1, 0x01b4, 0).unwrap();
        assert_eq!(
            stream.stream.into_inner(),
            vec![5, 0, 0, 0xb5, 0, 0, 0, 0, 5, 1, 1, 0xd1, 1, 0xb4, 0, 0]
        );
    }

    #[test]
    fn calibration_event_bytes_match_java_client() {
        // `requestVideoColorCalibration` → `writeVideoSettingsEvent(19, 0)`.
        let mut stream = RfbStream::new(Cursor::new(Vec::new()));
        stream.write_video_settings_event(19, 0).unwrap();
        stream.write_video_settings_event(18, 0).unwrap();
        assert_eq!(
            stream.stream.into_inner(),
            vec![144, 19, 0, 0, 144, 18, 0, 0]
        );
    }
}
