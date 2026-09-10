/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public final class Constants {
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final int INT = 4;
    public static final int SHORT = 2;
    public static final int BYTE = 1;
    public static final int BOOLEAN = 1;
    public static final int MAX_FRAME_GRABBER_BUFFERS = 3;
    public static final int MAX_FRAME_GRABBER_H = 1280;
    public static final int MAX_FRAME_GRABBER_V = 1024;
    public static final int FRAME_BUFFER_SIZE = 0x300000;
    public static final int FRAME_BUFFER_FREE = 0;
    public static final int FRAME_BUFFER_HW_BUSY = 1;
    public static final int FRAME_BUFFER_FULL = 2;
    public static final int FRAME_BUFFER_SW_BUSY = 3;
    public static final int ClrDepthVal8BPP = 0;
    public static final int ClrDepthVal16BPP = 1;
    public static final int ClrDepthVal24BPP = 2;
    public static final int FrameModeNormal = 0;
    public static final int FrameModeDouble = 1;
    public static final int TestModeNone = 0;
    public static final int TestModeVert = 1;
    public static final int TestModeHor = 2;
    public static final int FG_FLAG_RGB = 1;
    public static final int FG_FLAG_FGA = 2;
    public static final int FG_FLAG_SERIAL_KME = 4;
    public static final int FG_FLAG_PCI_KME = 8;
    public static final int FG_MODE_RGB = 0;
    public static final int FG_MODE_FGA = 1;
    public static final int RDM_MAX_ID = 16;
    public static final int RDM_MAX_TYPE = 16;
    public static final int RDM_MAX_MODEL = 16;
    public static final int RDM_MAX_NAME = 16;
    public static final int RDM_MAX_CLASS = 16;
    public static final int RDM_ERROR_BASE = -1340;
    public static final int RDM_ERROR_SERVICE_NOT_FOUND = -1339;
    public static final int RDM_ERROR_FUNCTION_NOT_FOUND = -1338;
    public static final int RDM_ERROR_IO_ERROR = -1337;
    public static final int RDM_ERROR_PERMISSION_DENIED = -1336;
    public static final int RDM_ERROR_BAD_PARAMETER = -1335;
    public static final int TR_OS_UNKNOWN = 0;
    public static final int MAX_STR = 64;
    public static final int MAX_SERVER_ID = 32;
    public static final int MAX_SECRET_SZ = 129;
    public static final int MAX_PROTOCOL_SZ = 12;
    public static final int MAX_AUTH_TYPE = 5;
    public static final int MAX_BASE_DN = 129;
    public static final int MAX_BASE_SEARCH = 129;
    public static final int MAX_SERVERS = 2;
    public static final int TR_PROTOCOL_VERSION_300 = 22;
    public static final int TR_PROTOCOL_VERSION_250 = 16;
    public static final int TR_PROTOCOL_VERSION_200 = 11;
    public static final int TR_PROTOCOL_VERSION_ACK = 21;
    public static final int TR_OLDEST_CLIENT_PROTOCOL_VERSION = 21;
    public static final int TR_OLDEST_SERVER_PROTOCOL_VERSION = 11;
    public static final int TR_PROTOCOL_VERSION = 30;
    public static final int TR_PROTOCOL_VERSION_RESUME = 30;
    public static final int TR_PROTOCOL_VERSION_CSC = 20;
    public static final int TR_PROTOCOL_VERSION_RDM2 = 20;
    public static int TR_PORT_BASE = 5000;
    public static final int TR_TOCLIENT_PORT_OFFSET = 0;
    public static final int TR_TOSERVER_PORT_OFFSET = 1;
    public static final int TR_UDP_PORT_OFFSET = 2;
    public static final int TR_MAX_CMD_LENGTH = 65535;
    public static final int TR_MAX_SERVER_NAME = 16;
    public static final int TR_MAX_USER_NAME = 24;
    public static final int TR_MAX_PASSWORD = 24;
    public static final int TR_MAX_RADIUS_USER_NAME = 129;
    public static final int TR_MAX_RADIUS_PASSWORD = 129;
    public static final int TR_MAX_RADIUS_SECRET = 129;
    public static final int TR_MAX_USERACCOUNTS_250 = 64;
    public static final int TR_MAX_USERACCOUNTS = 512;
    public static final int TR_MAX_LOG = 2048;
    public static final int TR_MAX_GET_LOG = 64;
    public static final int TR_MAX_PRIVATE_KEY = 24;
    public static final int TR_MAX_CHANNEL = 16;
    public static final int TR_MAX_CHANNEL_NAME = 32;
    public static final int TR_MAX_DATE_STRING = 28;
    public static final int TR_MAX_DIALBACK = 13;
    public static final int TR_MAX_GROUP_ID = 32;
    public static final int TR_MAX_USER_ID = 32;
    public static final int TR_MAX_GROUP_NAME = 24;
    public static final int TR_MAX_NODE_NAME = 24;
    public static final int TR_MAX_ACCESS_NODE = 32;
    public static final int TR_MAX_ACCESS_TYPE = 32;
    public static final int TR_MAX_PERMISSION = 64;
    public static final int TR_MAX_SECURITYID = 64;
    public static final int TR_MAX_PERM_ALLOWED = 128;
    public static final int TR_MAX_IPACL = 512;
    public static final int TR_MAX_GROUPACCOUNTS = 512;
    public static final int TR_MAX_SERIAL_300 = 8;
    public static final int TR_MAX_SERIAL = 32;
    public static final int TR_MAX_SERIAL_NAME = 32;
    public static final int TR_MAX_STRING = 256;
    public static final int TR_MAX_CELL = 1024;
    public static final int TR_MAX_CELL_1 = 128;
    public static final int TR_MAX_CELL_2 = 256;
    public static final int TR_MAX_KB = 16;
    public static final int TR_SIZE_RANDOM = 64;
    public static final int TR_SIZE_MD5 = 16;
    public static final int TR_SC3_KEY_MAX = 141;
    public static final int TR_KEEP_ALIVE_PERIOD = 58000;
    public static final int TR_KEEP_ALIVE_PERIOD_250 = 180000;
    public static final int TR_PPP_CLIENT_ADDRESS = -553746589;
    public static final int TR_PPP_SERVER_ADDRESS = -553746588;
    public static final int TR_MAX_H = 1280;
    public static final int TR_MAX_V = 1024;
    public static final int PCK_TYPE_CMD = 61440;
    public static final int CMD_LED_SCC_MASK = 1;
    public static final int CMD_LED_LEDC_MASK = 2;
    public static final int CMD_LED_SCR_MASK = 16;
    public static final int CMD_LED_NUM_MASK = 32;
    public static final int CMD_LED_CAP_MASK = 64;
    public static final int CMD_LED_SC_MASK = 12;
    public static final int CMD_LED_SC_SHIFT = 2;
    public static final int CMD_TARGET_SUN = 256;
    public static final int CMD_TARGET_MASK = 3840;
    public static final int TR_GET_NEWEST = -1;
    public static final int TR_GET_OLDEST = -2;
    public static final int TR_ERROR_INVALID_PARAM = 1;
    public static final int TR_ERROR_INVALID_COMMAND = 2;
    public static final int TR_ERROR_USER_ALREADY_EXISTS = 3;
    public static final int TR_ERROR_USER_NOT_FOUND = 4;
    public static final int TR_ERROR_PERMISSION_DENIED = 5;
    public static final int TR_ERROR_PUBLIC_VIEW_DENIED = 6;
    public static final int TR_ERROR_NO_RESOURCES = 7;
    public static final int TR_ERROR_INTERNAL_ERROR = 8;
    public static final int TR_ERROR_CONSOLE_BUSY = 9;
    public static final int TR_ERROR_DEVICE_BUSY = 10;
    public static final int TR_ERROR_SERVER_BUSY = 11;
    public static final int TR_ERROR_FILE_NOT_FOUND = 12;
    public static final int TR_ERROR_GROUP_ALREADY_EXISTS = 13;
    public static final int TR_ERROR_GROUP_NOT_FOUND = 14;
    public static final int TR_ERROR_USER_ASSOCIATED_TO_GROUP = 15;
    public static final int TR_ERROR_GROUP_NOT_SAVED = 16;
    public static final int TR_ERROR_LAST = 17;
    public static final int TRREG_STRING = 1;
    public static final int TRREG_DWORD = 2;
    public static final int TR_SSL_ENABLE = 1;
    public static final int TR_OPTION_NOT_CONFIGURED = 1;
    public static final int TR_OPTION_RFP = 2;
    public static final int TR_OPTION_HID_KB = 4;
    public static final int TR_ID_FLAG_BIGENDIAN = 1;
    public static final int TR_ID_FLAG_TRCWEB = 2;
    public static final int TR_ACCESS_TRY_AGAIN = 1;
    public static final int TR_SYSLOG_ENABLE = 1;
    public static final int TR_REFRESH_COLOR_CALIBRATE = 1;
    public static final int TR_REFRESH_AUTO_COLOR_CALIBRATE = 2;
    public static final int TR_COMP_MODE_LEGACY = 0;
    public static final int TR_COMP_MODE_ENABLE_JBIG = 1;
    public static final int TR_COMP_MODE_ENABLE_LZO = 2;
    public static final int TR_COMP_MODE_ENABLE_LZO_EXTENDED = 4;
    public static final int TR_COMPRESS_LZO = 1;
    public static final int TR_COMPRESS_JBIG = 2;
    public static final int TR_CELL_FLAG_LZO = 1;
    public static final int TR_CELL_FLAG_RLE = 2;
    public static final int TR_CELL_FLAG_PACK = 4;
    public static final int TR_CELL_FLAG_PIPELINE = 8;
    public static final int TR_HANDSHAKE_SIGNATURE = -1958523193;
    public static final int TR_WR_HANDSHAKE_SIGNATURE = 1547540642;
    public static final int TR_RD_HANDSHAKE_SIGNATURE = 1203501117;
    public static final short TR_MAX_RESPONSE = 4108;
    public static final int BI_RGB = 0;
    public static final int MAX_PATH = 255;
    public static final int USER_PASSWORD_EXPIRED = 4096;
    public static String DEFAULT_PASSWORD = "";
    public static String COMPANY_NAME_SHORT = "";
    public static String COMPANY_NAME_NOSPACE = "";
    public static String ADMIN_APPLET_JARFILE = "";
    public static String ADMIN_APPLET_CLASSNAME = "";
    public static final short RFP_MAX_RESPONSE = 4096;

    public static void setPort(int n) {
        TR_PORT_BASE = n;
    }

    public static void setDefaultPassword(String string) {
        DEFAULT_PASSWORD = new String(string);
    }

    public static void setShortCoName(String string) {
        COMPANY_NAME_SHORT = new String(string);
    }

    public static void setNospaceCoName(String string) {
        COMPANY_NAME_NOSPACE = new String(string);
    }

    public static void setAdminAppletJarFile(String string) {
        ADMIN_APPLET_JARFILE = new String(string);
    }

    public static void setAdminAppletClass(String string) {
        ADMIN_APPLET_CLASSNAME = new String(string);
    }
}

