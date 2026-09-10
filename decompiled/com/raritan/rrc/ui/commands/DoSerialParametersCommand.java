/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.SerialPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.tr.TRSRVR_SERIAL_PARAMS;

public class DoSerialParametersCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doSerialParametersCommand";

    public DoSerialParametersCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        Device device;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult(true, "");
        TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS = (TRSRVR_SERIAL_PARAMS)this.cmdContext.getCommandParameter("serialParams");
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && (device = (Device)arrayList.get(0)) instanceof SerialPort) {
            ((SerialPort)device).setSerialParameters(tRSRVR_SERIAL_PARAMS);
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

