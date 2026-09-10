/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.IPReach;

public class Paragon
extends IPReach {
    private String document = null;
    private Device baseDevice = null;

    public Paragon(Device device) {
        this.baseDevice = device;
    }

    public Device getBaseDevice() {
        return this.baseDevice;
    }

    @Override
    public String getDeviceType() {
        return "Paragon";
    }

    @Override
    public String getViewName() {
        return "";
    }

    public String getDocument() {
        return this.document;
    }

    public void setDocument(String string) {
        this.document = string;
    }

    @Override
    public void connect() {
        this.setConnected(true);
        this.setState("CONNECTED");
        this.firePropertyChange("DEVICE_CONNECTED", null, null);
    }

    @Override
    public void disconnect() {
        this.setConnected(false);
        this.setState("AVAILABLE");
        if (this.hasChildren()) {
            this.removeChildren();
        }
        this.firePropertyChange("DEVICE_CONNECTION_LOST", null, null);
    }

    public String getPortal() {
        IPReach iPReach;
        String string;
        Device device = this.getBaseDevice();
        while (device != null && device instanceof Paragon) {
            device = ((Paragon)device).getBaseDevice();
        }
        if (device != null && device instanceof IPReach && (string = (iPReach = (IPReach)device).getKvmSwitchId()) != null) {
            return "//*[@id=" + string + "]";
        }
        return "";
    }
}

