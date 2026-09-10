/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class DoSortCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "sortCommand";

    public DoSortCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        Device device = (Device)this.getContext().getCommandParameter("sortMode");
        DeviceTreeController deviceTreeController = DeviceTreeController.getInstance((RRCScreenContext)this.scrContext);
        deviceTreeController.sort(device);
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        return this.scrContext.getApplicationProperty("connection") == null;
    }

    public static void main(String[] stringArray) {
    }
}

