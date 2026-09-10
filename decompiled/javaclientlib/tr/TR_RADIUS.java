/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public interface TR_RADIUS {
    public static final int ENABLE_AUTH = 1;
    public static final int ENABLE_ACCT = 2;
    public static final int NO_LOCAL_DB = 4;
    public static final int NO_RADIUS_LOCAL = 8;
    public static final int PERMISSION_MASK = 240;
    public static final int USER_PERMISSIONS = 0;
    public static final int ADMIN_PERMISSIONS = 16;
    public static final int NO_PERMISSIONS = 32;
    public static final int TIMEOUT_MASK = 65280;
    public static final int TIMEOUT_DEFAULT = 2;
    public static final int TIMEOUT_SHIFT = 8;
}

