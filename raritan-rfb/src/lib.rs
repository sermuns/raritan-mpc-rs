//! RFB 1.29 video channel (port 443, usually plaintext).
//!
//! Reference: decompiled `nn.pp.rccore.impl.rfb.*` plus a decrypted
//! capture of the Java client. See `docs/rfb.md` for the byte-level map.
//!
//! Layout: [`proto`] (message types), [`framebuffer`] (Raw decode +
//! pixel buffer), [`lrle`] (LRLE tiles), [`transport`] (TCP/TLS setup),
//! [`handshake`], [`pump`].

pub mod framebuffer;
pub mod handshake;
pub mod input;
pub mod lrle;
pub mod proto;
pub mod pump;
pub mod stream;
pub mod transport;

pub use framebuffer::{Framebuffer, FramebufferRectangle, FramebufferUpdate, PixelFormat};
pub use input::{VideoCommand, eric_code};
pub use stream::RfbStream;

#[cfg(test)]
mod tests {
    use super::*;
    use std::io::{Cursor, Read, Write};

    #[test]
    fn parses_rfb_framebuffer_update() {
        let mut bytes = vec![0, 0, 2];
        bytes.extend_from_slice(&36u32.to_be_bytes());
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

    /// A `FixColourMapEntries` (sent when the target drops to a
    /// palettized mode, e.g. during reboot) must not kill the pump:
    /// it is skipped and the next update still parses.
    #[test]
    fn pump_survives_colour_map() {
        let mut server = vec![1, 0]; // type 1 + pad
        server.extend_from_slice(&0u16.to_be_bytes()); // first
        server.extend_from_slice(&2u16.to_be_bytes()); // count
        server.extend_from_slice(&[0xFF, 0xFF, 0, 0, 0, 0]); // white
        server.extend_from_slice(&[0, 0, 0, 0, 0, 0]); // black
        // Empty framebuffer update: type + flags=0, 0 rects, 0 bytes.
        server.extend_from_slice(&[0, 0, 0, 0]);
        server.extend_from_slice(&0u32.to_be_bytes());

        let mut stream = RfbStream::new(Cursor::new(server));
        let update = stream.read_message().unwrap();
        assert_eq!(update.flags, 0);
        assert!(update.rectangles.is_empty());
    }

    struct FakeStream {
        read: Cursor<Vec<u8>>,
        written: Vec<u8>,
    }

    impl FakeStream {
        fn new(server: Vec<u8>) -> Self {
            Self {
                read: Cursor::new(server),
                written: Vec::new(),
            }
        }
    }

    impl Read for FakeStream {
        fn read(&mut self, buf: &mut [u8]) -> std::io::Result<usize> {
            self.read.read(buf)
        }
    }

    impl Write for FakeStream {
        fn write(&mut self, buf: &[u8]) -> std::io::Result<usize> {
            self.written.extend_from_slice(buf);
            Ok(buf.len())
        }

        fn flush(&mut self) -> std::io::Result<()> {
            Ok(())
        }
    }

    /// Replays a minimal truth.pcapng-style handshake and asserts every
    /// client byte matches the Java client.
    #[test]
    fn handshake_matches_java_client_bytes() {
        use raritan_common::SessionCreds;
        let mut server = Vec::new();
        server.extend_from_slice(b"e-RIC RFB 01.29\n");
        server.extend_from_slice(&[32, 0x13]); // auth caps: 1|2|16
        server.extend_from_slice(&[33, 0]); // empty session challenge
        server.extend_from_slice(&[34, 0, 0, 0, 0, 0, 0, 0]); // auth ok
        server.extend_from_slice(&[18, 1, 6, 3]); // 1 conn param
        server.extend_from_slice(b"hw_enc");
        server.extend_from_slice(b"yes");
        server.extend_from_slice(&[7, 0, 0, 5]); // welcome "admin"
        server.extend_from_slice(b"admin");
        server.extend_from_slice(&[5, 0, 0, 0, 0, 0, 0, 17]); // server init
        // Server framebuffer format 1024x768 RGB565.
        server.extend_from_slice(&[128, 0, 0x04, 0x00, 0x03, 0x00]);
        server.extend_from_slice(&[16, 16, 1, 1, 0, 31, 0, 63, 0, 31, 11, 5, 0, 0, 0, 0]);

        let mut stream = RfbStream::new(FakeStream::new(server));
        let format = stream
            .handshake(&SessionCreds::new("s_1", "k2"), "P_1")
            .unwrap();
        assert_eq!(format, PixelFormat::RGB565);
        assert_eq!(stream.framebuffer_size(), Some((1024, 768)));

        let mut expected = Vec::new();
        expected.extend_from_slice(b"e-RIC AUTH=");
        expected.extend_from_slice(b"e-RIC RFB 01.29\n");
        expected.extend_from_slice(&[32, 16, 6, 0, 0, 0, 0, 0, b's', b'u', b'p', b'e', b'r', 0]);
        // Challenge response: [33][len incl. NUL][`"s_1":"k2"` + NUL].
        expected.extend_from_slice(&[33, 11]);
        expected.extend_from_slice(b"\"s_1\":\"k2\"");
        expected.push(0);
        // Client init + associated tag (RDM session, no NUL) + KVM switch.
        expected.extend_from_slice(&[7, 0, 0, 4]);
        expected.extend_from_slice(&[8, 0, 0, 10]);
        expected.extend_from_slice(b"\"s_1\":\"k2\"");
        expected.extend_from_slice(&[137, 0, 0, 3]);
        expected.extend_from_slice(b"P_1");
        // SetEncodings [0x1080, 0, 0, 0], SetPixelFormat RGB565, full FBU.
        expected.extend_from_slice(&[2, 0, 0, 4]);
        expected.extend_from_slice(&0x0000_1080u32.to_be_bytes());
        expected.extend_from_slice(&0u32.to_be_bytes());
        expected.extend_from_slice(&0u32.to_be_bytes());
        expected.extend_from_slice(&0u32.to_be_bytes());
        expected.extend_from_slice(&[
            0, 0, 0, 0, 16, 16, 1, 1, 0, 31, 0, 63, 0, 31, 11, 5, 0, 0, 0, 0,
        ]);
        // Session init tail: three full updates, video-settings request,
        // mouse sync, pointer event, connection parameter, ping.
        for _ in 0..3 {
            expected.extend_from_slice(&[3, 0, 0, 0, 0, 0, 0x04, 0x00, 0x03, 0x00]);
        }
        expected.extend_from_slice(&[145, 1, 134, 0, 5, 0, 0, 0, 0, 0, 0, 0]);
        expected.extend_from_slice(&[155, 18, 8]);
        expected.extend_from_slice(b"current_mouse_mode");
        expected.extend_from_slice(b"absolute");
        expected.extend_from_slice(&[148, 0, 0, 0, 0, 0, 0, 0]);

        assert_eq!(&stream.stream.written, &expected);
    }

    /// Steady-state pump with real captured bytes: answers a ping request,
    /// then decodes a 16x16 LRLE rect (from truth.pcapng) into pixels.
    #[test]
    fn pump_answers_ping_and_decodes_lrle_rect() {
        let mut server = vec![148, 0, 0, 0, 0x12, 0x34, 0x56, 0x78];
        // Framebuffer update: flags=0, 1 rect, 120 bytes (16 hdr + 101 data
        // + 3 alignment pad).
        server.extend_from_slice(&[0, 0, 0, 1]);
        server.extend_from_slice(&120u32.to_be_bytes());
        server.extend_from_slice(&[
            0x00, 0xa0, 0x02, 0x70, 0x00, 0x10, 0x00, 0x10, 0x00, 0x00, 0x10, 0x80, 0x00, 0x00,
            0x00, 0x65, 0x6f, 0x9c, 0x42, 0x51, 0x83, 0x81, 0xcb, 0x73, 0xbd, 0x46, 0x72, 0xee,
            0x42, 0x71, 0xea, 0x24, 0xe8, 0xa9, 0xb7, 0xe1, 0x04, 0x41, 0xe7, 0x1c, 0xa6, 0x5e,
            0xb7, 0xbb, 0xbd, 0xc0, 0xe0, 0x46, 0x72, 0xe7, 0x81, 0xaf, 0xbb, 0xbd, 0x77, 0xde,
            0x56, 0xf6, 0xe1, 0x83, 0xe7, 0x62, 0xf9, 0xb9, 0x52, 0xd5, 0x18, 0xe6, 0x81, 0xea,
            0xa9, 0x2d, 0xab, 0x81, 0xc1, 0xe9, 0x81, 0xc4, 0xe0, 0x42, 0x51, 0xed, 0x6f, 0x9c,
            0x3a, 0x30, 0x04, 0x20, 0xed, 0x3e, 0x50, 0x00, 0x21, 0xed, 0x3e, 0x51, 0xee, 0x3e,
            0x50, 0x00, 0x20, 0xed, 0x42, 0x51, 0xfb, 0x24, 0xe8, 0xa5, 0xe0, 0x3e, 0x50, 0xe9,
            0x2d, 0x2a, 0xb1, 0xbb, 0xbd, 0x00, 0x00, 0x00,
        ]);

        let mut stream = RfbStream::new(FakeStream::new(server));
        let update = stream.read_message().unwrap();
        assert_eq!(update.rectangles.len(), 1);
        assert_eq!(update.rectangles[0].encoding, 0x1080);
        // Ping reply [149,0,0,0,serial] must have been sent first.
        assert_eq!(
            stream.stream.written,
            vec![149, 0, 0, 0, 0x12, 0x34, 0x56, 0x78]
        );

        let mut framebuffer = Framebuffer::new(1024, 768);
        framebuffer
            .apply_update(&update, PixelFormat::RGB565)
            .unwrap();
        // Every pixel carries an opaque alpha (last byte) once painted;
        // the untouched background stays zero.
        let painted = framebuffer
            .rgba
            .chunks_exact(4)
            .filter(|pixel| pixel[3] != 0)
            .count();
        assert_eq!(painted, 16 * 16);
    }
}
