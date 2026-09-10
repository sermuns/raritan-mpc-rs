/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.USBProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class USBProfilesInfo {
    private final ArrayList listOfUSBProfiles;
    private final USBProfile activeProfile;
    private final USBProfile preferred;

    public USBProfilesInfo(ArrayList arrayList, USBProfile uSBProfile, USBProfile uSBProfile2) {
        this.listOfUSBProfiles = arrayList;
        assert (arrayList.contains(uSBProfile));
        assert (arrayList.contains(uSBProfile2));
        this.activeProfile = uSBProfile;
        this.preferred = uSBProfile2;
    }

    public List getUSBProfiles() {
        return Collections.unmodifiableList(this.listOfUSBProfiles);
    }

    public USBProfile getActiveUSBProfile() {
        return this.activeProfile;
    }

    public USBProfile getPreferredUSBProfile() {
        return this.preferred;
    }
}

