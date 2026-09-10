/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.G1DeviceHandlerImpl;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.panes.KvmPanel;

public class KX101G1DeviceHandlerImpl
extends G1DeviceHandlerImpl {
    public KX101G1DeviceHandlerImpl(Device device) {
        super(device);
    }

    @Override
    public void finishAutoSensing(KvmPanel kvmPanel) {
        kvmPanel.forceExistingVideoModeNotify();
    }

    @Override
    public boolean hasColorCalibration(Port port) {
        return false;
    }
}

