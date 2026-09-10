/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.G2SerialPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.PowerPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class SerialPortDummyCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "checkCommand";

    public SerialPortDummyCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        return arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof Port && device.isConnected() && device.getDeviceClass().equals("Serial") && !((Port)device).isAdmin() && !(device instanceof PowerPort) && !(device instanceof G2SerialPort);
    }
}

