/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class ShowDialbackMessageCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "ShowDialbackMessageCommand";

    public ShowDialbackMessageCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started " + this.getClass().getName());
        this.scrContext.getPanelMediator().showPanel(this.getContext());
        return new CommandResult(true, "");
    }
}

