/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.RRCRFPClient;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;

public class CancelRFPOperationCommand
extends AbstractCommand {
    private static final String COMMAND_KEY = "cancelRFPOperationCommand";
    private RaritanPropertyResourceBundle bundle;

    public CancelRFPOperationCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
        rRCRFPClient.setScreenContext(this.scrContext);
        rRCRFPClient.cancel();
        return new CommandResult(true, this.bundle.getString("rfpclient.operationcancelled.message"));
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

