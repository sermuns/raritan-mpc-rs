/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceHandlerInterface;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.commands.DoSwitchCommand;
import com.raritan.rrc.ui.commands.ShowUpdateDeviceCommand;
import com.raritan.rrc.ui.panes.KvmPanel;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import java.util.LinkedHashMap;
import java.util.Map;
import javaclientlib.utils.RRCGeneralException;
import nn.pp.common.ApplicationContext;
import nn.pp.rccore.RCCore;

public abstract class DeviceHandlerImpl
implements DeviceHandlerInterface {
    protected Device device;
    protected int upgradeDuration = 20;

    @Override
    public boolean isAbsoluteMouseSupported() {
        return false;
    }

    @Override
    public boolean isIntelligentMouseSupported() {
        return false;
    }

    @Override
    public boolean isStandardMouseSupported() {
        return false;
    }

    @Override
    public void doSwitch(DoSwitchCommand doSwitchCommand, CommandResult commandResult) {
    }

    @Override
    public void doShowDeviceUpdateCommand(ShowUpdateDeviceCommand showUpdateDeviceCommand) {
        this.device.setUpgradeDuration(this.upgradeDuration);
    }

    protected String getParamValue(Map map, String string, boolean bl) throws RRCGeneralException {
        String string2 = "";
        if (map.get(string) == null && bl) {
            throw new RRCGeneralException(string + " is missing");
        }
        string2 = (String)map.get(string);
        return string2;
    }

    @Override
    public void finishAutoSensing(KvmPanel kvmPanel) {
    }

    @Override
    public boolean hasColorCalibration(Port port) {
        return true;
    }

    @Override
    public LinkedHashMap<String, RCCore.Compression> initializeAllConnSpeeds(RaritanPropertyResourceBundle raritanPropertyResourceBundle) {
        LinkedHashMap<String, RCCore.Compression> linkedHashMap = new LinkedHashMap<String, RCCore.Compression>();
        linkedHashMap.put(raritanPropertyResourceBundle.getString("VideoOptimized.option"), RCCore.Compression.VIDEO_OPTIMIZED);
        linkedHashMap.put(raritanPropertyResourceBundle.getString("Ethernet1G.option"), RCCore.Compression.UNCOMPRESSED);
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
    public void setToolTip(Port port) {
    }

    @Override
    public boolean showCtrlNumlockCommand(Port port) {
        return true;
    }

    @Override
    public boolean canDoTargetScreenCapture() {
        return false;
    }

    @Override
    public void updateHotkeys() {
        ApplicationContext.getInstance().setAttribute("HOTKEYMAP", null);
    }

    @Override
    public boolean isURLPortExecutable() {
        return false;
    }
}

