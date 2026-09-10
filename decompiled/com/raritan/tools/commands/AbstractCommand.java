/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.commands;

import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import foxtrot.Task;
import java.util.ResourceBundle;

public abstract class AbstractCommand
implements Command {
    protected CommandContext cmdContext;
    protected ScreenContext scrContext;
    private RaritanPropertyResourceBundle bundle;

    public AbstractCommand(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.cmdContext = new CommandContext(this.getKey());
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public CommandContext getContext() {
        return this.getContext(false);
    }

    @Override
    public CommandContext getContext(boolean bl) {
        if (bl) {
            this.cmdContext = new CommandContext(this.getKey());
        }
        return this.cmdContext;
    }

    @Override
    public abstract String getKey();

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started " + this.getClass().getName());
        final CommandResult commandResult = new CommandResult();
        Task task = new Task(){

            @Override
            public Object run() throws Exception {
                AbstractCommand.this.doExecute(commandResult);
                AbstractCommand.this.scrContext.getLogger().logTextDebug(" Finished " + this.getClass().getName());
                return null;
            }
        };
        this.scrContext.getBlockingHelper().executeTaskDialog(task, this.executeWithBlocking());
        return commandResult;
    }

    @Override
    public abstract boolean isExecutable();

    protected void doExecute(CommandResult commandResult) {
    }

    protected boolean executeWithBlocking() {
        return false;
    }

    protected ResourceBundle getBundle() {
        return this.bundle;
    }
}

