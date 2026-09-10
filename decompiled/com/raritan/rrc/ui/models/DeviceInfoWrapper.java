/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.protocol.csc.RRCDeviceInfo;
import java.net.InetAddress;

public class DeviceInfoWrapper {
    private final InetAddress addr;
    private final RRCDeviceInfo devInfo;

    public DeviceInfoWrapper(RRCDeviceInfo rRCDeviceInfo) {
        this.addr = rRCDeviceInfo.getInetAddress();
        this.devInfo = rRCDeviceInfo;
    }

    public RRCDeviceInfo getDeviceInfo() {
        return this.devInfo;
    }

    public int hashCode() {
        return this.addr.hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof DeviceInfoWrapper) {
            DeviceInfoWrapper deviceInfoWrapper = (DeviceInfoWrapper)object;
            return this.addr.equals(deviceInfoWrapper.addr);
        }
        return false;
    }

    public String toString() {
        return "" + this.devInfo;
    }
}

