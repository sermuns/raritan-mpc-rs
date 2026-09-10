/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.ui.MPCScanFrame;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public class ShowScanFrameCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showScanFrameCommand";
    RRCScreenContext rrcScreenContext;

    public ShowScanFrameCommand(ScreenContext screenContext) {
        super(screenContext);
        this.rrcScreenContext = (RRCScreenContext)screenContext;
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        MPCScanFrame mPCScanFrame = new MPCScanFrame(this.rrcScreenContext);
        commandResult.setIsSuccess(true);
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        if (rRCScreenContext != null) {
            return !rRCScreenContext.isScanFrameOpened();
        }
        return false;
    }
}

