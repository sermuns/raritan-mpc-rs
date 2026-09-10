/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser.spi;

import com.raritan.protocol.browser.HintAddress;
import com.raritan.protocol.browser.spi.RRCDeviceInfoEx;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class HintAddressEx
extends HintAddress
implements Comparable {
    protected InetAddress inetAddress = null;
    protected RRCDeviceInfoEx pingResult = null;
    protected long pingTime = -1L;

    public HintAddressEx(HintAddress hintAddress) {
        super(hintAddress.host, hintAddress.port);
    }

    public HintAddressEx(String string) {
        super(string);
    }

    public HintAddressEx(String string, int n) {
        super(string, n);
    }

    public InetAddress getAddress() throws UnknownHostException {
        if (this.inetAddress == null) {
            this.inetAddress = InetAddress.getByName(this.host);
        }
        return this.inetAddress;
    }

    @Override
    public int compareTo(Object object) {
        return super.compareTo(object);
    }

    @Override
    public boolean equals(Object object) {
        return this.compareTo(object) == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    protected RRCDeviceInfoEx getPingResult() {
        return this.pingResult;
    }

    protected long getPingTime() {
        return this.pingTime;
    }

    protected void setPingResult(RRCDeviceInfoEx rRCDeviceInfoEx) {
        this.pingResult = rRCDeviceInfoEx;
    }

    protected void setPingTime(long l) {
        this.pingTime = l;
    }

    protected boolean equalAddress(RRCDeviceInfoEx rRCDeviceInfoEx) {
        if (rRCDeviceInfoEx == null) {
            return false;
        }
        InetSocketAddress inetSocketAddress = (InetSocketAddress)rRCDeviceInfoEx.getSocketAddress();
        if (inetSocketAddress == null) {
            return false;
        }
        try {
            if (inetSocketAddress.getAddress().equals(this.getAddress()) && inetSocketAddress.getPort() == this.port) {
                return true;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }
}

