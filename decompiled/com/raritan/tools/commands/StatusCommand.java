/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class StatusCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "statusCommand";

    public StatusCommand(ScreenContext screenContext) {
        super(screenContext);
        this.cmdContext = new CommandContext(this.getKey());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        String string = (String)this.getContext().getCommandParameter("logtext");
        this.getContext().setCommandResult("logtext", string);
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

