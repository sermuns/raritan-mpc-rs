/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.G2SerialPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class ShowKX2G2SerialPortCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showKX2G2SerialPortCommand";

    public ShowKX2G2SerialPortCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        G2SerialPort g2SerialPort = (G2SerialPort)arrayList.get(0);
        g2SerialPort.connect();
        g2SerialPort.setContext(this.scrContext);
        commandResult.setIsSuccess(true);
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof Port && device.getDeviceClass().equals("Serial") && !device.isConnected()) {
            if (((RRCScreenContext)this.scrContext).getOpenPortsObservable().getComponent() == null) {
                return true;
            }
            if (((RRCScreenContext)this.scrContext).getPortByKeyObservable((Port)device) == null) {
                return true;
            }
        }
        return false;
    }
}

