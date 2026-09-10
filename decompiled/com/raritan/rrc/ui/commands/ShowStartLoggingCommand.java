/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.SerialView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class ShowStartLoggingCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showStartLoggingCommand";

    public ShowStartLoggingCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        Object object;
        this.scrContext.getLogger().logTextDebug(" Started ");
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0) {
            object = (Port)arrayList.get(0);
            this.getContext().setCommandParameter("devices", object);
        }
        object = new CommandResult(true, "");
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return object;
    }

    @Override
    public boolean isExecutable() {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        try {
            SerialView serialView;
            Device device;
            if (arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof Port && device.isConnected() && device.getDeviceClass().equals("Serial") && (serialView = (SerialView)((Port)device).getView()) != null && serialView.getSerialTerminal() != null) {
                return !serialView.getSerialTerminal().isLogging();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return false;
    }
}

