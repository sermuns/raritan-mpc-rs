/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class ShowModifyProfileCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showModifyProfileCommand";

    public ShowModifyProfileCommand(ScreenContext screenContext) {
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
        Device device = null;
        try {
            device = (Device)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
        }
        catch (Exception exception) {
            this.scrContext.getLogger().logTextDebug(exception.getMessage());
        }
        this.getContext(true).setCommandParameter("devices", device);
        commandResult.setIsSuccess(true);
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0) {
            Device device = (Device)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
            if (device instanceof Port) {
                return false;
            }
            if (device != null && device.isProfiled() && !device.isConnected()) {
                return true;
            }
        }
        return false;
    }
}

