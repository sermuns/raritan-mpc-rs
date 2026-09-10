/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;

public class DoDeleteProfileCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doDeleteProfileCommand";

    public DoDeleteProfileCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0) {
            IPReach iPReach = (IPReach)arrayList.get(0);
            String string = this.calculateExportKey(iPReach);
            if ("".equals(string)) {
                RRCLogger.log(-1, 300, "Node name is null in Preferences");
                CommandResult commandResult = new CommandResult();
                commandResult.setIsSuccess(false);
                return commandResult;
            }
            DeviceTreeController.getInstance((RRCScreenContext)this.scrContext).removeDevice(iPReach);
            DevicePreferences.deleteNode(this.calculateExportKey(iPReach));
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        return arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device.isProfiled() && !device.isConnected();
    }

    private String calculateExportKey(IPReach iPReach) {
        String string = iPReach.getDevPrefs().getIp();
        if (iPReach.getDevPrefs().getConnectionType() == 2) {
            if (iPReach.getDevPrefs().getFindBy() == 1) {
                string = iPReach.getDevPrefs().getName();
            } else if (iPReach.getDevPrefs().getFindBy() == 2) {
                string = iPReach.getDevPrefs().getDnsName();
            }
        } else {
            string = iPReach.getDevPrefs().getPhone();
        }
        return string;
    }
}

