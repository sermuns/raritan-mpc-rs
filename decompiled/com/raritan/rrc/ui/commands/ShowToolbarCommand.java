/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class ShowToolbarCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showToolbarCommand";

    public ShowToolbarCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        Boolean bl = (Boolean)this.getContext().getCommandParameter("showToolbarMode");
        boolean bl2 = bl;
        ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectToolBarView(bl2);
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

