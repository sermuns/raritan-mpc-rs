/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import javaclientlib.utils.RRCLogger;

public class DoModifyConnectionPropertiesCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doModifyConnectionPropertiesCommand";

    public DoModifyConnectionPropertiesCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        Port port = ((RRCScreenContext)this.scrContext).getSelectedPort();
        if (port != null && port.isConnected() && port.getDeviceClass().equals("KVM")) {
            DeviceView deviceView = port.getView();
            if (deviceView != null) {
                deviceView.getCommandHandler().handleCommand(this, this.scrContext);
            } else {
                RRCLogger.log(100, 1, "View is null in DoModifyConnectionPropertiesCommand");
            }
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

