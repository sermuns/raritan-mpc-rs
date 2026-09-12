//! RFB 1.29 message-type constants (`RfbConstants` in the Java client).

// Server → client.
pub const FRAMEBUFFER_UPDATE: u8 = 0;
pub const USER_NOTIFICATION: u8 = 3;
pub const PORT_LIST: u8 = 4;
pub const SERVER_INIT: u8 = 5;
pub const UTF8_STRING: u8 = 7;
pub const VIDEO_SETTINGS_S2C: u8 = 8;
pub const KEYBOARD_LAYOUT: u8 = 9;
pub const OSD_STATE: u8 = 16;
pub const VIDEO_QUALITY_S2C: u8 = 17;
pub const CONNECTION_PARAMETERS: u8 = 18;
pub const ACK_PIXEL_FORMAT: u8 = 19;
pub const AUTH_CAPS: u8 = 32;
pub const SESSION_CHALLENGE: u8 = 33;
pub const AUTH_SUCCESSFUL: u8 = 34;
pub const SERVER_FB_FORMAT: u8 = 128;
pub const SERVER_RC_MESSAGE: u8 = 131;
pub const SERVER_COMMAND: u8 = 132;
pub const PING_REQUEST: u8 = 148;
pub const PING_REPLY: u8 = 149;
pub const BANDWIDTH_REQUEST: u8 = 150;
pub const VM_MOUNTS_RESPONSE: u8 = 166;
pub const VM_SHARE_TABLE: u8 = 167;
pub const VIRTUAL_MEDIA_CONFIG: u8 = 168;
pub const USB_PROFILE_LIST: u8 = 170;

// Client → server.
pub const SET_PIXEL_FORMAT: u8 = 0;
pub const SET_ENCODINGS: u8 = 2;
pub const FB_UPDATE_REQUEST: u8 = 3;
pub const CLIENT_INIT: u8 = 7;
pub const ASSOCIATED_TAG: u8 = 8;
pub const LOGIN: u8 = 32;
pub const CHALLENGE_RESPONSE: u8 = 33;
pub const POINTER_EVENT: u8 = 5;
pub const MOUSE_SYNC_EVENT: u8 = 134;
pub const KVM_SWITCH_EVENT: u8 = 137;
pub const VIDEO_SETTINGS_REQUEST: u8 = 145;
pub const PING_REPLY_OUT: u8 = 149;
pub const BANDWIDTH_REPLY: u8 = 151;
pub const SET_CONNECTION_PARAMETER: u8 = 155;

/// RDM-session auth method bit in the auth-caps message.
pub const AUTH_METHOD_RDM_SESSION: u8 = 16;

/// Default encoding set: hardware LRLE, uncompressed, 16-bit lossless
/// (`0x1080`) plus three zero slots, exactly as the Java client sends
/// (`RfbEncodingV01_22.getRfbEncodings` with HW/UNCOMPRESSED/COLOR_16_BIT).
pub fn default_encodings() -> [u32; 4] {
    [0x0000_1080, 0, 0, 0]
}
