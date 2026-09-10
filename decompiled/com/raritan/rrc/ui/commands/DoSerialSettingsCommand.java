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

public class DoSerialSettingsCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doSerialSettingsCommand";

    public DoSerialSettingsCommand(ScreenContext screenContext) {
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
        String string = (String)this.cmdContext.getCommandParameter("codeSet");
        String string2 = (String)this.cmdContext.getCommandParameter("cursorType");
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        Device device = (Device)arrayList.get(0);
        if (device instanceof SerialPort) {
            ((SerialPort)device).setCodeSet(string);
            ((SerialPort)device).setCursorType(Integer.parseInt(string2));
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

