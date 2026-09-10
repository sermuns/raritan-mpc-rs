/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class ShowAllDevicesCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showAllDevicesCommand";

    public ShowAllDevicesCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        Boolean bl = (Boolean)this.getContext().getCommandParameter("showAllDevicesMode");
        boolean bl2 = bl;
        ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectBrowseAllDevicesView(bl2);
        ((RRCScreenContext)this.scrContext).getAppSettings().setShowAll(bl2);
        DeviceTreeController deviceTreeController = DeviceTreeController.getInstance((RRCScreenContext)this.scrContext);
        if (!bl2) {
            deviceTreeController.stopBroadcastAndUpdateDevices();
        } else {
            deviceTreeController.startBroadcast();
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        return this.scrContext.getApplicationProperty("connection") == null;
    }
}

