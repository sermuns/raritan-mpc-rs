/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public interface TR_NET {
    public static final int USEDHCP = 1;
    public static final int NETWORK_ENABLED = 2;
    public static final int DIALIN_ENABLED = 4;
    public static final int WEB_ENABLED = 8;
    public static final int SERIAL_ENABLED = 16;
    public static final int MODE_MASK = 1792;
    public static final int MODE_AUTO = 0;
    public static final int MODE_10_HALF = 256;
    public static final int MODE_10_FULL = 512;
    public static final int MODE_100_HALF = 768;
    public static final int MODE_100_FULL = 1024;
    public static final int PPP = 32768;
    public static final int IP_FAILOVER_ENABLED = 4096;
    public static final int POWER_PORT_DISABLE = 8192;
}

