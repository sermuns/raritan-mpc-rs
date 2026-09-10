/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import javaclientlib.utils.RRCLogger;

public class HelpDisplayCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "HelpDisplayCommand";

    public HelpDisplayCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        assert (this.scrContext instanceof RRCScreenContext);
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        Port port = rRCScreenContext.getSelectedPort();
        DeviceView deviceView = port.getView();
        if (deviceView != null) {
            if (deviceView.isFullScreenMode()) {
                rRCScreenContext.getHelpManager().showHelp((String)this.getContext().getCommandParameter("HelpTopicIDToDisplay"), rRCScreenContext.getApplication().getContentPane());
                rRCScreenContext.getHelpManager().setHelpWindowOnTop(true);
            } else {
                rRCScreenContext.getHelpManager().showHelp((String)this.getContext().getCommandParameter("HelpTopicIDToDisplay"), deviceView);
                rRCScreenContext.getHelpManager().setHelpWindowOnTop(false);
            }
        } else {
            RRCLogger.log(100, 1, "View is null in HelpDisplayCommand");
        }
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

