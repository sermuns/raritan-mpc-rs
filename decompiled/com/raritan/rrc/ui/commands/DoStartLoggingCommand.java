/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.SerialView;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.io.File;

public class DoStartLoggingCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doStartLoggingCommand";
    private RaritanPropertyResourceBundle bundle;

    public DoStartLoggingCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult(true, "");
        Port port = (Port)this.cmdContext.getCommandParameter("devices");
        File file = (File)this.getContext().getCommandParameter("selectedFile");
        SerialView serialView = (SerialView)port.getView();
        boolean bl = false;
        if (port.isConnected()) {
            bl = serialView.getSerialTerminal().doStartLogging(file);
        }
        if (!bl) {
            commandResult.setStatusMessage(this.bundle.getString("startLogging.error"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, port);
        commandResult.setIsSuccess(true);
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

