/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common;

public interface TRServer {
    public static final int TR_PROTOCOL_VERSION_200 = 11;
    public static final int TR_VIDEO_AUTOSENSE_NORMAL = 0;
    public static final int TR_VIDEO_AUTOSENSE_FAST = 1;
    public static final int TR_SPEED_25MB = 25000000;
    public static final int TR_SPEED_5MB = 5000000;
    public static final int TR_SPEED_1500KB = 1500000;
    public static final int TR_SPEED_1100KB = 1100000;
    public static final int TR_SPEED_512KB = 512000;
    public static final int TR_SPEED_384KB = 384000;
    public static final int TR_SPEED_256KB = 256000;
    public static final int TR_SPEED_128KB = 128000;
    public static final int TR_SPEED_56KB = 56000;
    public static final int TR_SPEED_33KB = 33000;
    public static final int TR_SPEED_24KB = 24000;
    public static final int TR_COMP_FLAGS_AUTO_SPEED = 1;
    public static final int TR_COMP_FLAGS_AUTO_COLOR = 2;
    public static final int TR_COMP_FLAGS_AUTO_FEATURE = 4;
    public static final int TR_COMP_FLAGS_MIN_FRAME_TIME = 8;
    public static final int TR_COMP_FLAGS_MAX_FRAME_TIME = 16;
    public static final int TR_COMP_FLAGS_COMP_ALL_FIELDS = 32;
    public static final int TR_COMP_FLAGS_CURSOR_AREA = 64;
    public static final int TR_COMP_FLAGS_FLOW_CONTROL = 128;
    public static final int TR_COMP_FLAGS_PROGRESSIVE_UPDATE = 32768;
    public static final int TR_COMP_MODE_ENABLE_LZO = 2;
    public static final int TR_PERM_ADMINISTRATOR = 2;
    public static final int TR_SECURITY_REMOTEADMIN = 1;
}

