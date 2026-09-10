/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;

public class ShowUserPasswordCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showUserPasswordCommand";

    public ShowUserPasswordCommand(ScreenContext screenContext) {
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
        commandContext.setCommandParameter("devices", (Device)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0));
        commandContext.setCommandParameter("userInvokedChangePassword", new Boolean(false));
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        try {
            Device device;
            ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
            if (arrayList == null) {
                return false;
            }
            if (arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof IPReach && device.isConnected()) {
                return true;
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return false;
    }
}

