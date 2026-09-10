/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.csc;

import java.net.InetAddress;
import java.net.SocketAddress;

public interface RRCDeviceInfo {
    public String getType();

    public String getModel();

    public String getVersion();

    public String getName();

    public String getClusterId();

    public String getHost();

    public InetAddress getInetAddress();

    public SocketAddress getSocketAddress();

    public String getDeviceID();

    public String getDnsName();

    public int getPort();

    public String getProductName();
}

