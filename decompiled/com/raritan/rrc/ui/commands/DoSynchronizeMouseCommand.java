/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;

public class DoSynchronizeMouseCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doSynchronizeMouseCommand";
    private String tooltipText = null;

    public DoSynchronizeMouseCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        Port port = ((RRCScreenContext)this.scrContext).getSelectedPort();
        if (port != null && port.isConnected() && port.getDeviceClass().equals("KVM")) {
            DeviceView deviceView = port.getView();
            if (deviceView != null) {
                deviceView.getCommandHandler().handleCommand(this, this.scrContext);
            } else {
                RRCLogger.log(100, 1, "View is null in DoSynchronizeMouseCommand");
            }
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList;
        Device device = null;
        this.tooltipText = null;
        if (this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port && device.isConnected() && (port = (Port)device).getDeviceClass().equalsIgnoreCase("KVM")) {
            if (!port.isTopLeft()) {
                this.tooltipText = this.getBundle().getString("SynchronizeMouseAction.NotTopLeft");
                return false;
            }
            DeviceView deviceView = ((RRCScreenContext)this.scrContext).getSelectView();
            if (deviceView instanceof RFBView) {
                RFBView rFBView = (RFBView)deviceView;
                return rFBView.getMouseMode() != 0;
            }
            return true;
        }
        return false;
    }

    public String getTooltipText() {
        return this.tooltipText;
    }
}

