/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class ShowURLPortCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showURLPortCommand";
    private RaritanPropertyResourceBundle bundle;

    public ShowURLPortCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        commandResult.setIsSuccess(true);
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        Port port = (Port)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
        return true;
    }
}

