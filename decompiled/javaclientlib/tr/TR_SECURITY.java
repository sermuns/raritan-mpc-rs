/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public interface TR_SECURITY {
    public static final int REMOTEADMIN = 1;
    public static final int RESTRICTIPADDRESS = 2;
    public static final int LOGOUTKVM = 4;
    public static final int ENABLE_SNMP = 8;
    public static final int SSL_EXPORT = 16;
    public static final int SSL_DOMESTIC = 32;
    public static final int KEY_MASK = 240;
    public static final int NULL_WITH_NULL = 256;
    public static final int SSL_WITH_NULL = 512;
    public static final int SSL_WITH_RC4 = 1024;
    public static final int SSL_WITH_SSL = 2048;
    public static final int ENCRYPTION_MASK = 3840;
    public static final int PAP_LOGIN = 0;
    public static final int CHAP_LOGIN = 4096;
    public static final int LOGIN_MASK = 61440;
    public static final int DISABLE_PASSTHROUGH = 65536;
    public static final int HIDEN_MASK = 2;
}

