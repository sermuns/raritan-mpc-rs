/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser;

import com.raritan.protocol.csc.RRCDeviceInfo;

public interface BrowserEvent {
    public static final int EVENT_ADD_DEVICE = 1;
    public static final int EVENT_DEL_DEVICE = 2;
    public static final int EVENT_PING_DEVICE = 3;

    public int getType();

    public RRCDeviceInfo getDeviceInfo();
}

