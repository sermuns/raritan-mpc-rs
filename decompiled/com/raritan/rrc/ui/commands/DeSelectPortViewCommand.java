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

public class DeSelectPortViewCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "deselectPortViewCommand";

    public DeSelectPortViewCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        ArrayList arrayList;
        Port port = (Port)this.getContext().getCommandParameter("selectedPortDevice");
        Device device = null;
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        if (rRCScreenContext.getSelectedDevicesObservable() != null && rRCScreenContext.getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)rRCScreenContext.getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port) {
            String string;
            Port port2 = (Port)device;
            String string2 = string = port2 == null ? "null" : port2.getViewName();
            if (port != null && port.getView() != null && port.getViewName().equals(string)) {
                port.firePropertyChange("DEVICE_DESELECTED", null, null);
            }
        }
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

