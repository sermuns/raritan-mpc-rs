/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class CancelButtonCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "cancelButtonCommand";

    public CancelButtonCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug("CancelButton Command executed ");
        return new CommandResult();
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

