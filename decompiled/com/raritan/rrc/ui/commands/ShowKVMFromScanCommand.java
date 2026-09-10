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
import javaclientlib.utils.RRCLogger;

public class ShowKVMFromScanCommand
extends ConnectionCommand {
    public static final String COMMAND_KEY = "showKVMFromScanCommand";
    private RaritanPropertyResourceBundle bundle;
    private KvmPort kvmPort;
    private RRCScreenContext ctx;

    public ShowKVMFromScanCommand(ScreenContext screenContext, KvmPort kvmPort) {
        super(screenContext);
        this.ctx = (RRCScreenContext)screenContext;
        this.kvmPort = kvmPort;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
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
        if (this.kvmPort.getPortStatus() == 0) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 5, "Port unavailable " + this.kvmPort.getId());
            }
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage("[" + this.kvmPort.getDevice().getNameIP() + "]: " + this.bundle.getString("KVMPort.couldNotConnect.error") + " " + (StringUtils.notNullOrEmpty(this.kvmPort.getName()) ? this.kvmPort.getName() : this.bundle.getString("DefaultPortString.error")));
            return commandResult;
        }
        this.getContext().setCommandParameter("ports", this.kvmPort);
        RRCLogger.log(300, 1, "execute Finished");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        try {
            if (this.kvmPort != null && this.kvmPort instanceof Port && this.kvmPort.getDeviceClass().equals("KVM") && !this.kvmPort.isConnected() && this.kvmPort.getPortStatus() != 0 && this.kvmPort.getRdmStatAvailable() != 4) {
                System.out.println("Returning True.....");
                return true;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        System.out.println("Returning False.....");
        return false;
    }
}

