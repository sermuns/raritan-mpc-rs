/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;

public class VirtualBladeChassis
extends BladeChassis {
    public VirtualBladeChassis(Device device) {
        super(device);
    }

    @Override
    public String getDisplayName(int n) {
        String string = this.getName();
        String string2 = string == null || string.length() == 0 ? "<Unnamed>" : string;
        return string2;
    }

    @Override
    public String getDeviceType() {
        return "VirtualBladeChassis";
    }
}

