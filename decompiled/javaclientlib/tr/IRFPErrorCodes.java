/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public interface IRFPErrorCodes {
    public static final int RFP_UPDATE_SUCCESS = 0;
    public static final int RFP_ERROR_CHECKSUM = 1;
    public static final int RFP_ERROR_FILE_FORMAT = 2;
    public static final int RFP_ERROR_DISK_MOUNT = 3;
    public static final int RFP_ERROR_DISK_UNMOUNT = 4;
    public static final int RFP_ERROR_FILE_WRITE = 5;
    public static final int RFP_ERROR_PARTIAL_WRITE = 6;
    public static final int RFP_ERROR_MODEL_MISMATCH = 7;
    public static final int RFP_ERROR_VERSION_MISMATCH = 8;
    public static final int RFP_ERROR_TIMEOUT = 9;
    public static final int RFP_ERROR_CANCEL = 10;
    public static final int RFP_ERROR_REQUEST_FORMAT = 11;
    public static final int RFP_ERROR_FILE_NOT_FOUND = 12;
    public static final int RFP_ERROR_INTERNAL = 13;
    public static final int RFP_ERROR_XML_PARSE = 14;
    public static final int RFP_ERROR_PMCORE_STARTSERIAL = 15;
    public static final int RFP_ERROR_LOCAL_RDM_SYNTAX = 16;
    public static final int RFP_ERROR_LOCAL_FILE_OPEN = 17;
    public static final int RFP_ERROR_LOCAL_FILE_ACCESS = 18;
    public static final int RFP_ERROR_LOCAL_SOCKET_OPEN = 19;
    public static final int RFP_ERROR_LOCAL_SOCKET_CONNECT = 20;
    public static final int RFP_ERROR_LOCAL_SOCKET_WRITE = 21;
    public static final int RFP_ERROR_LOCAL_SOCKET_READ = 22;
    public static final int RFP_ERROR_STOP_SERVER = 23;
    public static final int RFP_PACKET_ACK = 24;
    public static final int RFP_AUTO_REBOOT = 25;
    public static final int RFP_ERROR_NOT_COMPATIBLE_DENSITY = 26;
    public static final int RFP_ERROR_NOT_COMPATIBLE_HARDWARE = 27;
    public static final int RFP_ERROR_MISSING_DENSITY_TAG = 28;
    public static final int RFP_ERROR_MISSING_HARDWARE_TAG = 29;
    public static final int RFP_ERROR_NOT_COMPATIBLE = 30;
    public static final int RFP_ERROR_BAD_SCRIPT = 31;
    public static final int RFP_ERROR_POST_PROCESS_FILE = 32;
    public static final int RFP_ERROR_OUT_OF_MEMORY = 33;
    public static final int RFP_ERROR_MEMORY = 100;
    public static final int RFP_ERROR_TOO_MANY_FILES = 101;
    public static final int RFP_ERROR_NO_FILES = 102;
    public static final int RFP_ERROR_RSA_KEY_BAD = 103;
    public static final int RFP_ERROR_BAD_SIGNATURE = 104;
    public static final int RFP_ERROR_CANNOT_READ_FILE = 105;
    public static final int RFP_ERROR_CANNOT_WRITE_FILE = 106;
    public static final int RFP_ERROR_MAXLENGTH_NOT_LAST = 107;
    public static final int RFP_ERROR_MAXLENGTH_AND_LENGTH = 108;
    public static final int RFP_ERROR_PUBLIC_KEY_MISSING = 109;
    public static final int RFP_ERROR_RSA_KEY_MISSING = 110;
    public static final int RFP_ERROR_RC4_KEY_MISSING = 111;
    public static final int RFP_ERROR_INTERNAL_ERROR = 112;
    public static final int RFP_ERROR_BAD_METAFILE = 113;
    public static final int RFP_ERROR_NO_LENGTH = 114;
    public static final int RFP_ERROR_TOO_MANY_RP_TAGS = 500;
    public static final int RFP_ERROR_TOO_MANY_SELECT_TAGS = 501;
    public static final int RFP_ERROR_TOO_MANY_RPID_TAGS = 502;
    public static final int RFP_ERROR_BAD_RPID = 503;
    public static final int RFP_ERROR_RPID_NOT_FOUND = 504;
    public static final int RFP_ERROR_RESTORE_FAILED = 505;
    public static final int RFP_SKIP_FILE = 1000;
}

