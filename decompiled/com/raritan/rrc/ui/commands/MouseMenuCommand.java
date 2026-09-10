/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.KVMPortDummyCommand;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;

public class MouseMenuCommand
extends KVMPortDummyCommand {
    private String tooltip = null;
    public static final String COMMAND_KEY = "mouseMenuCommand";

    public MouseMenuCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        this.scrContext.getLogger().logTextDebug(" Started ");
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        DeviceView deviceView = rRCScreenContext.getSelectView();
        if (deviceView != null) {
            deviceView.getCommandHandler().handleCommand(this, this.scrContext);
        } else {
            RRCLogger.log(100, 1, "View is null in MouseMenuCommand");
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        commandResult.setIsSuccess(true);
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList;
        this.tooltip = null;
        Device device = null;
        if (this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port && device.isConnected() && (port = (Port)device).getDeviceClass().equalsIgnoreCase("KVM")) {
            if (port.isTopLeft()) {
                return true;
            }
            this.tooltip = this.getBundle().getString("MouseMenu.NotTopLeft");
        }
        return false;
    }

    public String getToolTip() {
        return this.tooltip;
    }
}

