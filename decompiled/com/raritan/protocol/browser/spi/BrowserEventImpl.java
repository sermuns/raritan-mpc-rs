/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser.spi;

import com.raritan.protocol.browser.BrowserEvent;
import com.raritan.protocol.csc.RRCDeviceInfo;

public class BrowserEventImpl
implements BrowserEvent {
    protected static final String[] typeNames = new String[]{"ADD", "DEL", "PING"};
    protected RRCDeviceInfo deviceInfo = null;
    protected int type = -1;

    public BrowserEventImpl(int n, RRCDeviceInfo rRCDeviceInfo) {
        this.type = n;
        this.deviceInfo = rRCDeviceInfo;
    }

    @Override
    public RRCDeviceInfo getDeviceInfo() {
        return this.deviceInfo;
    }

    @Override
    public int getType() {
        return this.type;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("BrowserEvent[ ");
        if (this.type >= 1 && this.type <= 3) {
            stringBuffer.append(typeNames[this.type - 1]);
        } else {
            stringBuffer.append(this.type);
        }
        stringBuffer.append(", DeviceInfo[ Name='");
        stringBuffer.append(this.deviceInfo.getName());
        stringBuffer.append("', Ver='");
        stringBuffer.append(this.deviceInfo.getVersion());
        stringBuffer.append("', Host=");
        stringBuffer.append(this.deviceInfo.getHost());
        stringBuffer.append(':');
        stringBuffer.append(this.deviceInfo.getPort());
        stringBuffer.append(" ] ]");
        return stringBuffer.toString();
    }
}

