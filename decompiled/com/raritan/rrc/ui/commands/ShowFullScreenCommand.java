/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCApplication;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandCheckMenuItem;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

public class ShowFullScreenCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showFullScreenCommand";

    public ShowFullScreenCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        Boolean bl = (Boolean)this.getContext().getCommandParameter("showFullScreenMode");
        boolean bl2 = bl;
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        if (this.scrContext.getApplication() instanceof RRCApplication) {
            ((RRCApplication)this.scrContext.getApplication()).setVisible(true);
            if (bl2) {
                ((RRCApplication)this.scrContext.getApplication()).setExtendedState(6);
                ((RRCApplication)this.scrContext.getApplication()).repaint();
            } else {
                ((RRCApplication)this.scrContext.getApplication()).setExtendedState(0);
                ((RRCApplication)this.scrContext.getApplication()).repaint();
            }
        }
        try {
            Port object;
            DeviceView deviceView;
            ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
            if (arrayList != null && arrayList.size() > 0 && (deviceView = (object = (Port)arrayList.get(0)).getView()) != null && deviceView.isTargetScreenResolution() && bl2) {
                deviceView.setTargetScreenResolution(false);
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectToolBarView(!bl2);
        ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectStatusBarView(!bl2);
        ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectNavigatorView(!bl2);
        for (CommandCheckMenuItem commandCheckMenuItem : ((RRCScreenContext)this.scrContext).getMainScreenMediator().getCheckFullScreenMenuItems()) {
            commandCheckMenuItem.setSelected(bl2);
        }
        ((RRCScreenContext)this.scrContext).getMainScreenMediator().getToolBarFullScreenButton().setSelected(bl2);
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

