/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "logCommand";
    private final SimpleDateFormat dateFormat = null;

    public LogCommand(ScreenContext screenContext) {
        super(screenContext);
        this.cmdContext = new CommandContext(this.getKey());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        String string = null;
        String string2 = (String)this.getContext().getCommandParameter("logtext");
        String string3 = (String)this.getContext().getCommandParameter("logtype");
        this.getContext().setCommandResult("logtext", string2);
        string = this.dateFormat == null ? new Date(System.currentTimeMillis()).toString() : this.dateFormat.format(new Date(System.currentTimeMillis()));
        this.getContext().setCommandResult("logtext", string2);
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

