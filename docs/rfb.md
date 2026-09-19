# RFB 1.29 video channel

Reference: decompiled `nn.pp.rccore.impl.rfb.*` (`RfbHandlerV01_29`,
`RfbConstants`, `V01_22`/`V01_27` message classes) plus a decrypted
packet capture of the Java client. Our implementation lives in
`raritan-rfb/src/` (`RfbStream` in `stream.rs`, handshake in
`handshake.rs`, pump in `pump.rs`, decoders in `framebuffer.rs`/`lrle.rs`).

Transport: plain TCP to port 443. No CSC, no TLS, no RC4
(`RemoteConsoleParameters.ssl == false`).

## Handshake (client view)

All multi-byte integers are big-endian.

1. Send `e-RIC AUTH=` (11 B).
2. Read `e-RIC RFB 01.29\n` (16 B), echo it back.
3. Read `[32][caps]`. Require RDM-session bit (`caps & 16`).
   Send login `[32, 16, 6, 0,0,0,0, 's','u','p','e','r', 0]`
   (`RfbLoginMsgV01_22`: method 16, name `"super"`).
4. Read `[33][len][challenge?]` (len is 0 in practice).
   Send `[33][total][`"id":"key"` + NUL]` where `"id":"key"` is the
   `SessionCreds::rdm_session()` pair and `total` **includes** the NUL
   (`RfbChallengeResponseMsgV01_22` + RDM branch of
   `RfbAuthenticatorV01_22`).
5. Event loop until message `128` (mirrors
   `RfbHandler.processProtocol`/`processInitialHandshake`):
    - `34`: auth-ok, `[34][pad][u16][u32 flags]` (7 B after type).
    - `18`: connection parameters, `[18][count][klen,vlen,key,value]*`.
      Must be parsed (length-delimited) to stay aligned; carries encoding
      hints, board info, USB profiles metadata, and the port list.
    - `7`: UTF-8 welcome, `[7][pad][len:u16][bytes]`; reply with client
      init `[7,0,0,4]`, associated tag `[8,0,len:u16, rdm_session]`
      (no NUL), and KVM switch `[137,0,len:u16, port_id]`.
    - `5`: server init, `[5][pad×3][server_id:i32]`.
    - `128`: framebuffer format (see below) → send encodings, pixel
      format, then the session-init tail → handshake done.
    - Anything else (`0,3,4,8,9,16,17,19,131,132,148,149,150,166,167,
      168,170`): parse/skip per the table below; stash early `0` updates.

## Session-init tail (after 128)

Byte-exact copy of the Java client (`jrfb_c.bin` in captures):

1. `SetEncodings`: `[2,0,4, 0x00001080,0,0,0]` — hardware LRLE,
   uncompressed, 16-bit lossless (`RfbEncodingV01_22.getRfbEncodings`
   for HW/UNCOMPRESSED/COLOR_16_BIT; the trailing zeros are the
   fixed 4-slot array).
2. `SetPixelFormat`: `[0,0,0,0, 16,16,1,1, 0,31, 0,63, 0,31, 11,5,0,
   0,0,0]` (RGB565).
3. Three full update requests `[3,0, 0,0, 0,0, W,H]`.
4. `[145,1]` video-settings request, `[134,0]` mouse sync,
   `[5,0,0,0,0,0,0,0]` pointer event,
   `[155,18,8,"current_mouse_mode","absolute"]`,
   `[148,0,0,0, serial=0]` ping request.

The three full requests (not one) plus the tail are what actually makes
the server start sending frames.

## Client → server input

- Key event (`RfbKeyEventMsgV01_27`, type 4): `[4, 0, keysym:u16-be]`
  where `keysym = eric & 0x7FFF | (down ? 0x8000 : 0)` — the high bit
  means key *pressed* (each tap's first frame in the capture has it
  set). The Eric codes
  are Raritan's own numbering from `KeyTranslatorBase` (en_US): H→0x22,
  E→0x11, L→0x25, O→0x17, Space→0x38, W→0x10, R→0x12, D→0x1f,
  Enter→0x1b — verified byte-for-byte against a Java capture typing
  "hello world" + Enter (`input.rs::eric_code`, `write_key_event`).
- Pointer event (`RfbPointerEventMsgV01_22`, type 5):
  `[5, buttons, x:u16-be, y:u16-be, wheel:u16-be]` with the standard
  button mask (bit 0 left, 1 middle, 2 right, toggled per click).
  Wheel-only events carry `x = y = 0` with the signed rotation in
  `wheel`. Verified against a Java capture of mouse moves plus one
  left click (`05 01 01 d1 01 b4 00 00`).
- Pointer event (type 5), mouse sync (134), KVM switch (137) as in the
  session-init tail above.
- Video-settings action (`RfbVideoSettingsC2SMsgV01_22`, type 144):
  `[144, setting, value:u16-be]`. The V01_29 handler uses the V01_27
  table: 19 = color calibration, 18 = auto-sense. These are the manual
  menu actions only — the client never sends them automatically, not
  even on resolution change (`processServerFBFormat` just requests a
  full update). The switch auto-calibrates on its own and bakes any
  "calibration in progress" notice into the video itself.
- On every connect the client releases all key codes 0–137: the switch
  keeps per-target key state across connections, so a modifier whose
  release was lost (focus switch, killed app) would otherwise stay
  down forever. Releases are no-ops for keys that aren't down.

## Steady state

- `request_framebuffer_update(true)`: full-area incremental request
  `[3,1, 0,0, W,H]`, exactly one outstanding per consumed update. It is
  sent immediately after each update **header**, before the body is read
  or decoded (mirrors Java `processFramebufferUpdate`, which requests
  first and reads second, so the server renders the next frame during
  transfer + decode). See `RfbStream::poll_incoming` /
  `read_update_header` / `read_update_body`.
- `read_message()`: returns the next `FramebufferUpdate`; answers ping
  requests (`[149,0,0,0,serial]`), absorbs ping replies, performs the
  bandwidth handshake (`[151,1]` … read … `[151,2]`), adopts late `128`
  format changes, skips everything else.
- Late `128` changes are real: on session start the target can switch
  from text mode (720×400) to graphics (1024×768), verified by replaying
  a Rack 10 capture (211 clipped rects without a resize). Consumers
  must recreate their pixel buffer *and* display texture when
  `framebuffer_size()` changes, or the picture crops/misaligns.

## Message catalog (server → client)

| Type | Name | Layout after type byte |
|---|---|---|
| 0 | FramebufferUpdate | `[flags][count:u16][size:u32]` + optional 8 B timestamp (flags&1) + blob (zlib iff flags&4); rects: `[x,y,w,h:u16][enc:i32][size:u32][data]` |
| 1 | FixColourMapEntries | `[pad][first:u16][count:u16]` + `count`×`[r,g,b:u16]`; sent when the target drops to a palettized mode (e.g. reboot into BIOS). We run true-color, so entries are skipped — the Java client throws here, we survive |
| 3 | UserNotification | `[kind][pad:u16][code:i32]` |
| 4 | PortList | `[pad][count:u16]` + per port `[kvm:u8,vm:u8,idx:u16,nlen:u16,vlen:u16,name,value]` (8 B fixed header) |
| 5 | ServerInit | `[pad×3][server_id:i32]` |
| 7 | Utf8String | `[pad][len:u16][bytes]` |
| 8 | VideoSettingsS2C | 27 B blob |
| 9 | KeyboardLayout | `[pad][len:u16][bytes]` |
| 16 | OSDState (V01_27) | `[blanking][timeout:u16][len:u16][bytes]` |
| 17 | VideoQualityS2C | 2 B |
| 18 | ConnectionParameters | `[count][klen,vlen,key,value]*` |
| 19 | AckPixelFormat | `[pad×3][13 pixfmt bytes][pad×3]` (19 B) |
| 32/33/34 | AuthCaps/Challenge/AuthOk | `[caps]` / `[len][bytes?]` / `[pad][u16][u32]` |
| 128 | ServerFBFormat | `[unsupported][w:u16][h:u16][13 pixfmt bytes][pad×3]` (22 B) |
| 131 | ServerRCMessage (V01_27) | `[pad×3][len:i32][bytes]` |
| 132 | ServerCommand | `[pad][nlen:u16][vlen:u16][name][value]` |
| 148/149 | Ping req/reply | `[0,0,0][serial:u32]` (8 B total) |
| 150 | BandwidthRequest | `[pad][len:u16][bytes]`; reply `[151,1]`, read, `[151,2]` |
| 166 | VMMountsResponse | 7 B |
| 167 | VMShareTable | `[count][klen,vlen,key,value]*` |
| 168 | VirtualMediaConfig | `[count][bytes]` |
| 170 | UsbProfileList | `[count:u16]` + per profile `[nlen:u8][dlen:u16][flags][id:u16][name][desc]` |

## Framebuffer decoding

- Encodings negotiated: Raw (0) and LRLE-HW (128, subencoding in bits
  12–15). Anything zlib-streamed at rect level (`enc & 0xF00`,
  `enc & 0x20000`) is rejected explicitly — we never negotiate it.
- **LRLE rects are 4-byte aligned**: after each non-zlib LRLE rect,
  skip `(4 - size % 4) % 4` pad bytes (`readHardwareEncodingPadding`).
- **LRLE runs wrap at 16×16 tile edges**, not full rows; line-copy uses
  a per-rect `prevLine` indexed by tile column
  (`ImageDecoderLrle.decodeImage`). Subencodings 10–13 are packed grey
  maps (`drawLRLEMap`), not compact runs.
- LRLE colors are format-independent: 15-bit table
  (`c*33/4` per 5-bit channel), 7-bit 125-entry table (+red pad),
  16-entry table, and grey ramps with Java's exact integer math
  (`n*65/16`, `n*17`, `n*73/2`, `n*85`, `n*255`).
