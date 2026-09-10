/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.swing.JApplet
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.ui.RRCApplet;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import javax.swing.JApplet;

public class CloseAppletCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "closeFrameCommand";

    public CloseAppletCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        DeviceTreeController.getInstance((RRCScreenContext)this.scrContext).stop();
        if ("true".equals(this.scrContext.getApplicationProperty("embeddedInFrame"))) {
            ((RRCApplet)this.scrContext.getApplication()).disconnect();
        } else {
            ((JApplet)this.scrContext.getApplication()).stop();
        }
        commandResult.setIsSuccess(true);
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return this.scrContext.getApplication() instanceof RRCApplet;
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }
}

