/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.G2DeviceHandlerImpl;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ShowUpdateDeviceCommand;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import java.util.LinkedHashMap;
import nn.pp.rccore.RCCore;

public class KX101G2DeviceHandlerImpl
extends G2DeviceHandlerImpl {
    public KX101G2DeviceHandlerImpl(Device device) {
        super(device);
    }

    @Override
    public void doShowDeviceUpdateCommand(ShowUpdateDeviceCommand showUpdateDeviceCommand) {
        this.upgradeDuration = 3;
        this.device.setUpgradeDuration(this.upgradeDuration);
        showUpdateDeviceCommand.setText(true, this.upgradeDuration);
    }

    @Override
    public LinkedHashMap<String, RCCore.Compression> initializeAllConnSpeeds(RaritanPropertyResourceBundle raritanPropertyResourceBundle) {
        LinkedHashMap<String, RCCore.Compression> linkedHashMap = new LinkedHashMap<String, RCCore.Compression>();
        linkedHashMap.put(raritanPropertyResourceBundle.getString("VideoOptimized.option"), RCCore.Compression.VIDEO_OPTIMIZED);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("Ethernet100.option"), RCCore.Compression.UNCOMPRESSED);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("Ethernet10.option"), RCCore.Compression.UNCOMPRESSED);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("MaxDSL.option"), RCCore.Compression.LEVEL_1);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("FastDSL.option"), RCCore.Compression.LEVEL_2);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("MediumDSL.option"), RCCore.Compression.LEVEL_3);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("SlowDSL.option"), RCCore.Compression.LEVEL_4);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("Cable.option"), RCCore.Compression.LEVEL_5);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("DualISDN.option"), RCCore.Compression.LEVEL_6);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("ISPModem.option"), RCCore.Compression.LEVEL_7);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("FastModem.option"), RCCore.Compression.LEVEL_8);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("SlowModem.option"), RCCore.Compression.LEVEL_9);
        return linkedHashMap;
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
}

