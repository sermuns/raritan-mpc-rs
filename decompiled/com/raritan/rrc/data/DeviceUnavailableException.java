/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

public class DeviceUnavailableException
extends Exception {
    private String deviceName;

    public DeviceUnavailableException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public DeviceUnavailableException(String string, Throwable throwable, String string2) {
        this(string, throwable);
        this.deviceName = string2;
    }

    public String getDeviceName() {
        return this.deviceName;
    }
}

