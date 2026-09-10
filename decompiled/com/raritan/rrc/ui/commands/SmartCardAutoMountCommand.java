/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.ui.ScreenContext;
import nn.pp.common.smartcard.SmartCardBean;

public class SmartCardAutoMountCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "SmartCardAutoMountCommand";

    public SmartCardAutoMountCommand(ScreenContext screenContext, SmartCardBean smartCardBean, String string, String string2) {
        super(screenContext);
        this.cmdContext.setCommandParameter("SmartCard_Bean", smartCardBean);
        this.cmdContext.setCommandParameter("SmartCard_CardReader_Name", string);
        this.cmdContext.setCommandParameter("SmartCard_Dialog_Title", string2);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

