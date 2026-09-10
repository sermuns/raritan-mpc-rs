/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.IPReach;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class DoCancelLoginCommand
extends CancelButtonCommand {
    public static final String COMMAND_KEY = "doCancelLoginCommand";

    public DoCancelLoginCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        IPReach iPReach = (IPReach)this.cmdContext.getCommandParameter("devices");
        if (iPReach == null) {
            return commandResult;
        }
        iPReach.setCancelLogin(true);
        if (iPReach.getDevPrefs() != null && iPReach.getDevPrefs().getModemConnector() != null && iPReach.isModemProfiled()) {
            iPReach.getDevPrefs().getModemConnector().rasHangUp();
            iPReach.getDevPrefs().setModemConnector(null);
            iPReach.setState("UNAVAILABLE");
        }
        if (iPReach.isConnected()) {
            iPReach.disconnect();
        }
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

