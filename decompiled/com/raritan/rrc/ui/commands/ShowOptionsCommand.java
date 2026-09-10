/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class ShowOptionsCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showOptionsCommand";

    public ShowOptionsCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

