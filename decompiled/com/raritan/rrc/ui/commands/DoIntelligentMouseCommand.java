/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;

public class DoIntelligentMouseCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "intelligentMouseCommand";

    public DoIntelligentMouseCommand(ScreenContext screenContext) {
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
            RRCLogger.log(100, 1, "View is null in DoIntelligentMouseCommand");
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        ArrayList arrayList;
        Device device = null;
        if (this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)).isConnected() && device instanceof KvmPort) {
            return ((KvmPort)device).getDevice().getHandler().isIntelligentMouseSupported();
        }
        return false;
    }

    public static void main(String[] stringArray) {
    }
}

