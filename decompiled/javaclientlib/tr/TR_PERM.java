/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public interface TR_PERM {
    public static final int ENABLED = 1;
    public static final int ADMINISTRATOR = 2;
    public static final int CONTROL = 4;
    public static final int PUBLICVIEW = 8;
    public static final int NETACCESS = 16;
    public static final int RESTRICTIP = 32;
    public static final int MODEMACCESS = 64;
    public static final int CALLBACK = 128;
    public static final int VIDEO = 256;
    public static final int SERIAL = 512;
    public static final int CONSOLE = 1024;
    public static final int THE_ADMIN = 2048;
    public static final int WEBACCESS = 4096;
    public static final int CONNECTED = Integer.MIN_VALUE;
    public static final int AUTHENTICATED = 0x40000000;
    public static final int PASSED_CHALLENGE = 0x20000000;
    public static final int VIDEO_STARTED = 0x10000000;
}

