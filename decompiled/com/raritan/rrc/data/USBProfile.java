/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

public final class USBProfile {
    private final int profileID;
    private final String profileName;
    private final boolean probable;

    public USBProfile(int n, String string, boolean bl) {
        this.profileID = n;
        this.profileName = string;
        this.probable = bl;
    }

    public USBProfile(int n, String string) {
        this(n, string, false);
    }

    public int getProfileID() {
        return this.profileID;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public boolean equals(Object object) {
        if (object instanceof USBProfile) {
            boolean bl;
            USBProfile uSBProfile = (USBProfile)object;
            boolean bl2 = bl = this.profileID == uSBProfile.profileID;
            if (bl) assert (this.profileName.equals(uSBProfile.profileName));
            return bl;
        }
        return false;
    }

    public boolean isProbable() {
        return this.probable;
    }
}

