/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandCheckMenuItem;
import java.util.ArrayList;

public class ShowVideoScaleCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showVideoScaleCommand";

    public ShowVideoScaleCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        DeviceView deviceView;
        Port port;
        if (this.isExecutable() && (port = ((RRCScreenContext)this.scrContext).getSelectedPort()) != null && port.getDeviceClass().equals("KVM") && port.isConnected() && (deviceView = port.getView()) != null) {
            boolean bl = (Boolean)this.getContext().getCommandParameter("scaleVideoMode");
            deviceView.setScaleVideoFlag(bl);
            RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
            for (CommandCheckMenuItem commandCheckMenuItem : rRCScreenContext.getMainScreenMediator().getCheckScaleVideoMenuItems()) {
                commandCheckMenuItem.setSelected(bl);
            }
            rRCScreenContext.getMainScreenMediator().getToolBarScaleVideoButton().setSelected(bl);
        }
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList;
        Device device = null;
        if (this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port && device.isConnected() && (port = (Port)device).getDeviceClass().equalsIgnoreCase("KVM")) {
            DeviceView deviceView = port.getView();
            if (deviceView != null && deviceView.isScaleVideoFlag()) {
                for (CommandCheckMenuItem commandCheckMenuItem : ((RRCScreenContext)this.scrContext).getMainScreenMediator().getCheckScaleVideoMenuItems()) {
                    commandCheckMenuItem.setSelected(true);
                }
                ((RRCScreenContext)this.scrContext).getMainScreenMediator().getToolBarScaleVideoButton().setSelected(true);
            }
            return true;
        }
        return false;
    }
}

