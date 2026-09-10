/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.MPCScanFrame;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ConnectionCommand;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;

public class ShowKX2KvmPortCommand
extends ConnectionCommand {
    public static final String COMMAND_KEY = "showKX2KvmPortCommand";
    private RaritanPropertyResourceBundle bundle;
    private RRCScreenContext scrContext;
    private boolean allowSecondary;

    public ShowKX2KvmPortCommand(ScreenContext screenContext) {
        super(screenContext);
        this.scrContext = (RRCScreenContext)screenContext;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    public void setAllowSecondary(boolean bl) {
        this.allowSecondary = bl;
    }

    @Override
    public CommandResult execute() {
        KvmPort kvmPort;
        RRCLogger.log(300, 1, "execute Started");
        CommandResult commandResult = new CommandResult(true, "");
        long l = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
        RRCLogger.log(300, 5, "Free memory " + l);
        if (0x300000L >= l) {
            System.runFinalization();
            System.gc();
            l = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
            RRCLogger.log(300, 5, "Free memory after GC " + l);
            if (0x300000L >= l) {
                commandResult.setStatusMessage(this.bundle.getString("outOfMemory.error"));
                commandResult.setIsSuccess(false);
                return commandResult;
            }
        }
        if ((kvmPort = (KvmPort)((ArrayList)this.scrContext.getSelectedDevicesObservable().getComponent()).get(0)).getPortStatus() == 0) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 5, "Port unavailable " + kvmPort.getId());
            }
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage("[" + kvmPort.getDevice().getNameIP() + "]: " + this.bundle.getString("KVMPort.couldNotConnect.error") + " " + (StringUtils.notNullOrEmpty(kvmPort.getName()) ? kvmPort.getName() : this.bundle.getString("DefaultPortString.error")));
            return commandResult;
        }
        if (kvmPort.isPrimaryPort() && kvmPort.getSecondaryPortConfigs().size() == 0 && !MPCUtil.isCCLaunched(this.scrContext)) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage("[" + kvmPort.getDevice().getNameIP() + "]: " + this.bundle.getString("KVMPort.secondaryNotAvailable.error"));
            return commandResult;
        }
        this.getContext().setCommandParameter("ports", kvmPort);
        if (kvmPort.getDevice().isScanSupported() && this.scrContext.getScanFrame() != null) {
            MPCScanFrame mPCScanFrame = this.scrContext.getScanFrame();
            String string = kvmPort.getDevice().getId();
            if (this.scrContext.isScanFrameOpened() && mPCScanFrame.getConnectedDeviceId().equals(string)) {
                mPCScanFrame.getScanConnector().pause();
            }
        }
        RRCLogger.log(300, 1, "execute Finished");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        ArrayList arrayList = (ArrayList)this.scrContext.getSelectedDevicesObservable().getComponent();
        try {
            Port port;
            if (!(arrayList == null || arrayList.size() <= 0 || (port = (Port)arrayList.get(0)) == null || !(port instanceof Port) || !port.getDeviceClass().equals("KVM") || port.isConnected() || port.getPortStatus() == 0 || port.getRdmStatAvailable() == 4 || !this.allowSecondary && port.isSecondaryPort() || this.scrContext.getOpenPortsObservable().getComponent() != null && this.scrContext.getPortByKeyObservable(port) != null)) {
                return true;
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return false;
    }
}

