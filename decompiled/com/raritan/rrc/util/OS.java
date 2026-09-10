/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.rrc.util.ModemOSSupport;

public final class OS {
    public static final OS WINDOWS = new OS("Windows", false);
    public static final OS LINUX = new OS("Linux", true);
    public static final OS SOLARIS = new OS("Sun", true);
    public static final OS MAC = new OS("Mac", true);
    private static final OS[] STORAGE = new OS[]{WINDOWS, LINUX, SOLARIS, MAC};
    private String name = null;
    private boolean unixOS = false;

    private OS(String string, boolean bl) {
        this.name = string;
        this.unixOS = bl;
    }

    public static OS valueOf(String string) {
        int n = OS.indexOf(string);
        return n != -1 ? STORAGE[n] : null;
    }

    public static OS getCurrent() {
        return OS.valueOf(ModemOSSupport.getOSname());
    }

    private static int indexOf(String string) {
        if (string == null) {
            return -1;
        }
        for (int i = 0; i < STORAGE.length; ++i) {
            OS oS = STORAGE[i];
            if (!string.equals(oS.name)) continue;
            return i;
        }
        return -1;
    }

    public boolean isUnixOS() {
        return this.unixOS;
    }

    public String toString() {
        return this.name;
    }
}

