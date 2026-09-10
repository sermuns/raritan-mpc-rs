/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public interface TRCMD {
    public static final int NOP = 0;
    public static final int HANDSHAKE_ACK_200 = 1;
    public static final int QUERY_ID = 2;
    public static final int PING = 3;
    public static final int CLIENT_CHALLENGE = 4;
    public static final int CLIENT_RESPONSE = 5;
    public static final int ACCESS_REQUEST = 6;
    public static final int LOGOUT = 7;
    public static final int ENUM_VIDEO_DEVICES = 8;
    public static final int START_VIDEO_STREAM_200 = 9;
    public static final int STOP_VIDEO_STREAM = 10;
    public static final int GET_VIDEO_PARAMS = 11;
    public static final int SET_VIDEO_PARAMS_200 = 12;
    public static final int GET_COMP_PARAMS = 13;
    public static final int SET_COMP_PARAMS_200 = 14;
    public static final int REFRESH_200 = 15;
    public static final int VIDEO_SYNC_200 = 16;
    public static final int KB = 17;
    public static final int MOUSE = 18;
    public static final int RAW_MOUSE = 19;
    public static final int SYNC_MOUSE_200 = 20;
    public static final int ENUM_SERIAL_DEVICES = 21;
    public static final int START_SERIAL_STREAM = 22;
    public static final int STOP_SERIAL_STREAM = 23;
    public static final int SEND_SERIAL = 24;
    public static final int SET_SERIAL_PARAMS = 25;
    public static final int GET_SERIAL_PARAMS = 26;
    public static final int SET_SERVER_PARAMS = 27;
    public static final int GET_SERVER_PARAMS = 28;
    public static final int ADD_USER_ACCOUNT = 29;
    public static final int SET_USER_ACCOUNT = 30;
    public static final int GET_USER_ACCOUNT = 31;
    public static final int ENUM_USER_ACCOUNT = 32;
    public static final int DELETE_USER_ACCOUNT = 33;
    public static final int GET_LOG = 34;
    public static final int CLEAR_LOG = 35;
    public static final int RESET_SERVER = 36;
    public static final int GET_TIME_DATE = 37;
    public static final int SET_TIME_DATE_200 = 38;
    public static final int CHANGE_PASSWORD = 39;
    public static final int SET_DATA_ITEM_200 = 40;
    public static final int GET_DATA_ITEM_200 = 41;
    public static final int IDENTIFY_REMOTE = 42;
    public static final int HANDSHAKE_ACK = 43;
    public static final int START_VIDEO_STREAM = 44;
    public static final int SET_VIDEO_PARAMS = 45;
    public static final int SET_COMP_PARAMS = 46;
    public static final int REFRESH = 47;
    public static final int VIDEO_SYNC = 48;
    public static final int SYNC_MOUSE = 49;
    public static final int SET_TIME_DATE = 50;
    public static final int SET_DATA_ITEM = 51;
    public static final int GET_DATA_ITEM = 52;
    public static final int RFP_MESSAGE = 53;
    public static final int DATABASE_REQUEST = 54;
    public static final int CONNECT_VIDEO_STREAM = 55;
    public static final int CONNECT_SERIAL_STREAM = 56;
    public static final int SWITCH_VIDEO_STREAM = 57;
    public static final int SUN_BREAK = 58;
    public static final int GET_TARGET_INTERFACE = 59;
    public static final int SET_TARGET_INTERFACE = 60;
    public static final byte PAUSE_VIDEO_STREAM = 62;
    public static final byte RESUME_VIDEO_STREAM = 63;
    public static final int RADIUS_PACKET = 61;
    public static final int MAX = 62;
}

