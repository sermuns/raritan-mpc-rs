/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

public class DeviceSecurity {
    public static final int SHARE_MODE_PRIVATE = 0;
    public static final int SHARE_MODE_ALLOWED = 1;
    public static final int SHARE_MODE_ADMIN_ONLY = 2;
    private int sharedMode = 0;
    private boolean singleUserLogin = false;

    public int getSharedMode() {
        return this.sharedMode;
    }

    public void setSharedMode(int n) {
        this.sharedMode = n;
    }

    public boolean isSingleUserLogin() {
        return this.singleUserLogin;
    }

    public void setSingleUserLogin(boolean bl) {
        this.singleUserLogin = bl;
    }
}

