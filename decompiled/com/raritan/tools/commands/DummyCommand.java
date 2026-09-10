/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class DummyCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "dummyCommand";

    public DummyCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public CommandResult execute() {
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        return false;
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }
}

