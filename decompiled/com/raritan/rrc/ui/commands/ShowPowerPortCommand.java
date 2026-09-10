/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.PowerPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class ShowPowerPortCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showPowerPortCommand";
    private RaritanPropertyResourceBundle bundle;

    public ShowPowerPortCommand(ScreenContext screenContext) {
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
        long l = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
        if (0x100000L >= l) {
            commandResult.setStatusMessage(this.bundle.getString("outOfMemory.error"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        PowerPort powerPort = (PowerPort)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
        powerPort.connect();
        if (!powerPort.isConnected()) {
            commandResult.setStatusMessage(this.bundle.getString("PowerPort.busy.error"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        commandResult.setIsSuccess(true);
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null) {
            return !device.isConnected();
        }
        return false;
    }
}

