/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.G2DeviceHandlerImpl;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ShowUpdateDeviceCommand;

public class KX2DeviceHandlerImpl
extends G2DeviceHandlerImpl {
    public KX2DeviceHandlerImpl(Device device) {
        super(device);
    }

    @Override
    public boolean isAbsoluteMouseSupported() {
        if (((RRCScreenContext)this.device.scrContext).getSelectView() != null) {
            return ((RRCScreenContext)this.device.scrContext).getSelectView().getAbsoluteMouseSupported();
        }
        return false;
    }

    @Override
    public boolean isIntelligentMouseSupported() {
        if (((RRCScreenContext)this.device.scrContext).getSelectView() != null) {
            return ((RRCScreenContext)this.device.scrContext).getSelectView().getIntelligentMouseSupported();
        }
        return false;
    }

    @Override
    public boolean isStandardMouseSupported() {
        if (((RRCScreenContext)this.device.scrContext).getSelectView() != null) {
            return ((RRCScreenContext)this.device.scrContext).getSelectView().getStandardMouseSupported();
        }
        return false;
    }

    @Override
    public void doShowDeviceUpdateCommand(ShowUpdateDeviceCommand showUpdateDeviceCommand) {
        int n = 1200;
        Object[] objectArray = this.device.getChildren().values().toArray();
        int n2 = 0;
        try {
            n2 = Integer.parseInt(this.device.getDeviceConnector().databaseRequest("<Database><Count><Select>/System/Device/Port[@Type=\"PowerStrip\"]</Select></Count></Database>").replaceFirst("<Database><Count>", "").replaceFirst("</Count></Database>", ""));
        }
        catch (NumberFormatException numberFormatException) {
            n2 = 0;
        }
        n += 120 * n2;
        for (int i = 0; i < objectArray.length; ++i) {
            if (!(objectArray[i] instanceof Port) || !((Port)objectArray[i]).getPortType().equals("VM")) continue;
            n += 40;
        }
        this.upgradeDuration = (n += n % 60 > 0 ? 60 : 0) / 60;
        this.device.setUpgradeDuration(this.upgradeDuration);
        showUpdateDeviceCommand.setText(true, this.upgradeDuration);
    }
}

