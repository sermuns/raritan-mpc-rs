/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.util.ArrayList;

public class ShowChangePasswordCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showChangePasswordCommand";

    public ShowChangePasswordCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult(true, "");
        CommandContext commandContext = this.getContext();
        Device device = (Device)commandContext.getCommandParameter("devices");
        if (device == null) {
            device = (Device)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
        }
        commandContext.setCommandParameter("devices", device);
        commandContext.setCommandParameter("userInvokedChangePassword", new Boolean(true));
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    public void handleCommandResult(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    public void handleCommandResultErrorDescription(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        if (commandResult.getErrorDescription() != null && commandResult.getErrorDescription().length > 0) {
            for (int i = 0; i < commandResult.getErrorDescription().length; ++i) {
                stringBuffer.append(commandResult.getErrorDescription()[i]);
                stringBuffer.append("\n");
            }
        } else if (commandResult.getStatusMessage() != null) {
            stringBuffer.append(commandResult.getStatusMessage());
        } else {
            stringBuffer.append("No command result info.");
        }
        CommonPopups.showCommandResultErrorMessage(stringBuffer.toString(), null, this.scrContext);
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }
}

