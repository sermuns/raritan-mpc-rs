/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ConnectionCommand;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.util.ArrayList;
import javaclientlib.clientlib.TRConnection;
import javaclientlib.utils.RRCLogger;

public class ShowKvmPortCommand
extends ConnectionCommand {
    public static final String COMMAND_KEY = "showKvmPortCommand";
    private static final int errCodeMask = 4095;
    private RaritanPropertyResourceBundle bundle;

    public ShowKvmPortCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        if (this.setBusy(true)) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage(this.bundle.getString("TR_ERROR." + Integer.toString(11)));
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
        KvmPort kvmPort = (KvmPort)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 5, "ShowKvmPort " + ((Object)kvmPort).toString() + ", port " + kvmPort.getId() + ", connected " + kvmPort.isConnected() + ", type " + kvmPort.getPortType());
        }
        if (kvmPort.getPortStatus() == 0) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 5, "Port unavailable " + kvmPort.getId());
            }
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage("[" + kvmPort.getDevice().getNameIP() + "]: " + this.bundle.getString("KVMPort.couldNotConnect.error") + " " + (StringUtils.notNullOrEmpty(kvmPort.getName()) ? kvmPort.getName() : this.bundle.getString("DefaultPortString.error")));
            return commandResult;
        }
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 5, "ShowKvmPortCommand connecting, already connected ?" + kvmPort.isConnected());
        }
        kvmPort.connect();
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 5, "ShowKvmPortCommand connect now connected ?" + kvmPort.isConnected() + ", err=" + TRConnection.getLastError());
        }
        this.setBusy(false);
        if (!kvmPort.isConnected()) {
            boolean bl = false;
            int n = TRConnection.getLastError();
            int n2 = n & 0xFFFFF000;
            if ((n &= 0xFFF) == 12) {
                String string;
                RaritanPropertyResourceBundle raritanPropertyResourceBundle;
                if (n2 == 0) {
                    n2 = 0x20001000;
                }
                commandResult.setIsSuccess(false);
                String string2 = null;
                int n3 = 0;
                if (n2 == 0x20001000) {
                    string2 = "TR_ERROR.";
                    n3 = 24;
                }
                boolean bl2 = bl = CommonPopups.showConfirmationDialog((raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale())).getString((string = string2 + Integer.toString(n)) + ".title"), raritanPropertyResourceBundle.getString(string + ".text"), null, this.scrContext) == 2;
                if (bl) {
                    kvmPort.forceConnection();
                    this.setBusy(false);
                } else {
                    return commandResult;
                }
            }
        }
        if (!kvmPort.isConnected()) {
            this.getTRSRVR_ErrorResult(commandResult, kvmPort);
        } else {
            MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, kvmPort);
            commandResult.setIsSuccess(true);
            this.getContext().setCommandParameter("ports", kvmPort);
        }
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        try {
            Port port;
            if (arrayList != null && arrayList.size() > 0 && (port = (Port)arrayList.get(0)) instanceof Port && port.getDeviceClass().equals("KVM") && !port.isConnected() && port.getPortStatus() != 0 && !port.isSecondaryPort() && (((RRCScreenContext)this.scrContext).getOpenPortsObservable().getComponent() == null || ((RRCScreenContext)this.scrContext).getPortByKeyObservable(port) == null)) {
                return true;
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return false;
    }
}

