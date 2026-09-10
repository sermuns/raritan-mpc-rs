/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import java.util.ArrayList;

public class ShowSaveTotalConfigurationCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showSaveTotalConfigurationCommand";

    public ShowSaveTotalConfigurationCommand(RRCScreenContext rRCScreenContext) {
        super(rRCScreenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult(true, "");
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof IPReach && device.isConnected() && ((IPReach)device).isAdministrator()) {
            return device.getHandler().isShowSaveTotalConfigurationExecutable();
        }
        return false;
    }
}

