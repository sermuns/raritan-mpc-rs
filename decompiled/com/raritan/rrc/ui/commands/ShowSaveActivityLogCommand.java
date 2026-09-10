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

public class ShowSaveActivityLogCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showSaveActivityLogCommand";

    public ShowSaveActivityLogCommand(RRCScreenContext rRCScreenContext) {
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
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList == null || arrayList.size() == 0) {
            return false;
        }
        Device device = (Device)arrayList.get(0);
        if (device != null && device instanceof IPReach && device.isConnected() && ((IPReach)device).isAdministrator()) {
            return device.getHandler().isShowSaveActivityLogExecutable();
        }
        return false;
    }
}

