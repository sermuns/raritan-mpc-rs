/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.KvmStream;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.KvmView;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;

public class ShowPropertiesCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showPropertiesCommand";

    public ShowPropertiesCommand(ScreenContext screenContext) {
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
        this.cmdContext.setCommandParameter("deviceNode", port.getDevice());
        DeviceView deviceView = port.getView();
        if (!(deviceView instanceof RFBView) && port.isConnected()) {
            TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = ((KvmStream)port.getStream()).getCompressionParams();
            this.cmdContext.setCommandParameter("compParams", tRSRVR_COMP_PARAMS);
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList;
        Device device = null;
        if (this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port && device.isConnected() && (port = (Port)device).getView() != null && (port.getView() instanceof KvmView || port.getView() instanceof RFBView)) {
            return port.getView().isCommandOperable(this);
        }
        return false;
    }
}

