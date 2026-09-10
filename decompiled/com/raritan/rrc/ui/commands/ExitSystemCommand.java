/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class ExitSystemCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "exitSystemCommand";

    public ExitSystemCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public CommandResult execute() {
        DeviceTreeController.getInstance((RRCScreenContext)this.scrContext).stop();
        System.exit(0);
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }
}

