/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser.spi;

import com.raritan.protocol.csc.spi.RRCDeviceInfo;
import java.net.SocketAddress;

public class RRCDeviceInfoEx
extends RRCDeviceInfo {
    protected long lastPingTime = -1L;

    private RRCDeviceInfoEx() {
    }

    public RRCDeviceInfoEx(SocketAddress socketAddress, long l) {
        super(socketAddress);
        this.lastPingTime = l;
    }

    public int hashCode() {
        return this.getSocketAddress().hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof RRCDeviceInfoEx) {
            if (this.getPort() != ((RRCDeviceInfoEx)object).getPort()) {
                return false;
            }
            return this.getSocketAddress().equals(((RRCDeviceInfoEx)object).getSocketAddress());
        }
        return false;
    }
}

