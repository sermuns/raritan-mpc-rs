/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.G2SerialPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.PowerPort;
import com.raritan.rrc.data.SerialPort;
import com.raritan.rrc.data.SerialStream;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.tr.TRSRVR_SERIAL_PARAMS;

public class ShowSerialParametersCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showSerialParametersCommand";

    public ShowSerialParametersCommand(ScreenContext screenContext) {
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
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        Port port = (Port)arrayList.get(0);
        if (port.isConnected()) {
            TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS = new TRSRVR_SERIAL_PARAMS();
            try {
                if (!((SerialStream)port.getStream()).getSerialStream(tRSRVR_SERIAL_PARAMS)) {
                    tRSRVR_SERIAL_PARAMS = null;
                }
            }
            catch (Exception exception) {
                tRSRVR_SERIAL_PARAMS = null;
            }
            this.cmdContext.setCommandParameter("serialParams", tRSRVR_SERIAL_PARAMS);
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof Port && device.isConnected() && device.getDeviceClass().equals("Serial") && !(device instanceof PowerPort) && !(device instanceof G2SerialPort)) {
            SerialPort serialPort = (SerialPort)device;
            return !serialPort.isAdmin() && !serialPort.isDiagnosticPort();
        }
        return false;
    }
}

