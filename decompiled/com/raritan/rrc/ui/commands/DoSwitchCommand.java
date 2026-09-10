/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ConnectionCommand;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class DoSwitchCommand
extends ConnectionCommand {
    public static final String COMMAND_KEY = "doSwitchCommand";
    public static final String DO_SWITCH = "doSwitch";
    public static final int errCodeMask = 4095;
    private RaritanPropertyResourceBundle bundle;

    public DoSwitchCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        ArrayList arrayList;
        KvmPort kvmPort;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        if (this.setBusy(true)) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage("");
            return commandResult;
        }
        long l = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
        if (0x300000L >= l) {
            System.runFinalization();
            System.gc();
            l = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
            if (0x300000L >= l) {
                commandResult.setStatusMessage(this.bundle.getString("outOfMemory.error"));
                commandResult.setIsSuccess(false);
                return commandResult;
            }
        }
        if ((kvmPort = (KvmPort)(arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0)).getPortStatus() == 0) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage("[" + kvmPort.getDevice().getNameIP() + "]: " + this.bundle.getString("KVMPort.couldNotConnect.error") + " " + (StringUtils.notNullOrEmpty(kvmPort.getName()) ? kvmPort.getName() : this.bundle.getString("DefaultPortString.error")));
            return commandResult;
        }
        kvmPort.getDevice().getHandler().doSwitch(this, commandResult);
        this.setBusy(false);
        this.scrContext.getLogger().logTextDebug(" Started ");
        return commandResult;
    }

    @Override
    public boolean setBusy(boolean bl) {
        return super.setBusy(bl);
    }

    @Override
    public boolean isExecutable() {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList == null || arrayList.size() == 0 || !(arrayList.get(0) instanceof Port)) {
            return false;
        }
        Port port = (Port)arrayList.get(0);
        return port instanceof KvmPort && !port.equals(port.getDevice().getActiveKvmPort()) && !port.isConnected() && port.getPortStatus() != 0 && !port.isSecondaryPort() && !port.isPrimaryPort();
    }
}

