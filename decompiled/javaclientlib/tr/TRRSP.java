/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public interface TRRSP {
    public static final int NOP = 0;
    public static final int HANDSHAKE = 1;
    public static final int ACK = 2;
    public static final int NACK = 3;
    public static final int NOTIFY_250 = 4;
    public static final int PONG = 5;
    public static final int ID = 6;
    public static final int SERVER_CHALLENGE = 7;
    public static final int SERVER_RESPONSE_200 = 8;
    public static final int ACCESS_RESPONSE = 9;
    public static final int VIDEO_PARAMS_200 = 10;
    public static final int COMP_PARAMS = 11;
    public static final int NEW_VIDEO_MODE_200 = 12;
    public static final int CACHE_250 = 13;
    public static final int COMPRESSED_250 = 14;
    public static final int BITPLANE_250 = 15;
    public static final int PACKED = 16;
    public static final int VIDEO_MARKER_200 = 17;
    public static final int KB_STATUS_200 = 18;
    public static final int SERIAL_PARAMS = 19;
    public static final int RECEIVE_SERIAL = 20;
    public static final int SERVER_PARAMS = 21;
    public static final int USER_ACCOUNT = 22;
    public static final int LOG = 23;
    public static final int TIME_DATE_200 = 24;
    public static final int DATA_ITEM_200 = 25;
    public static final int VIDEO_DEVICE = 26;
    public static final int SERIAL_DEVICE = 27;
    public static final int SERVER_RESPONSE = 28;
    public static final int VIDEO_PARAMS = 29;
    public static final int NEW_VIDEO_MODE_250 = 30;
    public static final int VIDEO_MARKER = 31;
    public static final int KB_STATUS = 32;
    public static final int TIME_DATE = 33;
    public static final int DATA_ITEM = 34;
    public static final int RFP_MESSAGE = 35;
    public static final int DATABASE_RESPONSE = 36;
    public static final int VIDEO_CONNECTED = 37;
    public static final int SERIAL_CONNECTED = 38;
    public static final int NEW_VIDEO_MODE = 39;
    public static final int CACHE = 40;
    public static final int COMPRESSED = 41;
    public static final int BITPLANE = 42;
    public static final int NOTIFY = 43;
    public static final int ID_RSP_NO_HEADER = 44;
    public static final int CELL_DATA = 45;
    public static final int RDM_EVENT = 46;
    public static final int TARGET_PARAMS = 47;
    public static final int MAX = 48;
    public static final int CONNECT_TIMEOUT = 20000;
    public static final int POWER_TIMEOUT = 60000;
}

