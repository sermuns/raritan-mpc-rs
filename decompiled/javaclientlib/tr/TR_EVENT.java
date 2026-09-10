/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public interface TR_EVENT {
    public static final int TYPE_EMPTY = 0;
    public static final int TYPE_HEAD = 1;
    public static final int TYPE_NETWORK = 2;
    public static final int TYPE_ADMIN = 3;
    public static final int TYPE_ERROR = 4;
    public static final int TYPE_TEXT = 5;
    public static final int TYPE_SYSTEM = 6;
    public static final int TYPE_MAX = 7;
    public static final int NULL = 0;
    public static final int INIT_FIRST = 1;
    public static final int INIT_MEM_ERROR = 1;
    public static final int INIT_DISK_ERROR = 2;
    public static final int INIT_HARDWARE_ERROR = 3;
    public static final int INIT_NETWORK_ERROR = 4;
    public static final int INIT_DHCP_ERROR = 5;
    public static final int INIT_CERT = 6;
    public static final int INIT_SEVER_CONFIG_ERROR = 7;
    public static final int INIT_USER_CONFIG_ERROR = 8;
    public static final int FATAL_FIRST = 256;
    public static final int FATAL_MEM_ERROR = 256;
    public static final int FATAL_DISK_ERROR = 257;
    public static final int FATAL_TIMEOUT = 258;
    public static final int FATAL_HARDWARE_ERROR = 259;
    public static final int FATAL_NETWORK_ERROR = 260;
    public static final int FATAL_DHCP_ERROR = 261;
    public static final int FATAL_RESET = 262;
    public static final int RUN_FIRST = 512;
    public static final int RUN_MEM_ERROR = 512;
    public static final int RUN_DISK_ERROR = 513;
    public static final int RUN_TIMEOUT = 514;
    public static final int RUN_HARDWARE_ERROR = 515;
    public static final int RUN_NETWORK_ERROR = 516;
    public static final int RUN_CORRUPT_MEM_ERROR = 517;
    public static final int RUN_RESET = 518;
    public static final int GPIO_OPEN_ERROR = 519;
    public static final int LOGIN = 768;
    public static final int LOGIN_FAILED = 769;
    public static final int LOGOUT = 770;
    public static final int CONNECTION_LOST = 771;
    public static final int CONNECTION_TIMEOUT = 772;
    public static final int CONNECTION_DENIED = 773;
    public static final int CONSOLE_LOGIN = 774;
    public static final int CONSOLE_LOGOUT = 775;
    public static final int WRONG_IP = 776;
    public static final int ADD_USER = 1024;
    public static final int SET_USER = 1025;
    public static final int DELETE_USER = 1026;
    public static final int SERVER_PARAMS = 1027;
    public static final int SERVER_RESET = 1028;
    public static final int CHANGED_PASSWORD = 1029;
    public static final int STARTUP = 1281;
    public static final int MODEM_CONNECT = 1282;
    public static final int MODEM_DISCONNECT = 1283;
    public static final int TEXT = 1280;
    public static final int MAX = 1281;
}

