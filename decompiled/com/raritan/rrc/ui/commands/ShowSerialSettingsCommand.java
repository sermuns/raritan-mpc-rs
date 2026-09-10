/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.SerialPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class ShowSerialSettingsCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showSerialSettingsCommand";

    public ShowSerialSettingsCommand(ScreenContext screenContext) {
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
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        SerialPort serialPort = (SerialPort)arrayList.get(0);
        this.cmdContext.setCommandParameter("codeSet", serialPort.getCodeSet());
        this.cmdContext.setCommandParameter("cursorType", String.valueOf(serialPort.getCursorType()));
        commandResult.setIsSuccess(true);
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

