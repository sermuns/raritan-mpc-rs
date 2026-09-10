/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class DoSendFixedMacroCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doSendFixedMacroCommand";
    private String macroSequence;

    public DoSendFixedMacroCommand(ScreenContext screenContext, String string) {
        super(screenContext);
        this.macroSequence = string;
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        DeviceView deviceView;
        this.scrContext.getLogger().logTextDebug(" Started ");
        Port port = ((RRCScreenContext)this.scrContext).getSelectedPort();
        if (port != null && port.isConnected() && port.getDeviceClass().equalsIgnoreCase("KVM") && (deviceView = port.getView()) != null) {
            deviceView.sendKeyboardMacro(this.macroSequence);
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList;
        Device device = null;
        return this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port && device.isConnected() && (port = (Port)device).getDeviceClass().equalsIgnoreCase("KVM");
    }
}

