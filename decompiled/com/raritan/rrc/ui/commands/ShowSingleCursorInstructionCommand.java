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
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;

public class ShowSingleCursorInstructionCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showSingleCursorInstructionCommand";
    private String tooltipText = null;

    public ShowSingleCursorInstructionCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        DeviceView deviceView = rRCScreenContext.getSelectView();
        if (deviceView != null) {
            deviceView.getCommandHandler().handleCommand(this, this.scrContext);
        } else {
            RRCLogger.log(100, 1, "View is null in ShowSingleCursorInstructionCommand");
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList;
        Device device = null;
        this.tooltipText = null;
        if (this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port && device.isConnected() && (port = (Port)device).getDeviceClass().equalsIgnoreCase("KVM")) {
            if (!port.isTopLeft()) {
                this.tooltipText = this.getBundle().getString("SingleMouseCursorAction.NotTopLeft");
                return false;
            }
            if (port.getView() != null) {
                return port.getView().isCommandOperable(this);
            }
        }
        return false;
    }

    public String getTooltipText() {
        return this.tooltipText;
    }
}

