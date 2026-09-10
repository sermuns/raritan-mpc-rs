/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.ui.commands.SmartCardMenuCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import javax.swing.Action;

public class SelectCardReaderCommand
extends SmartCardMenuCommand {
    public static final String COMMAND_KEY = "SelectCardReaderCommand";

    public SelectCardReaderCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    protected Action getAction(KvmPort kvmPort) {
        return kvmPort.getSelectCardReaderAction();
    }

    @Override
    protected void doExecute(CommandResult commandResult) {
        commandResult.setIsSuccess(true);
    }
}

