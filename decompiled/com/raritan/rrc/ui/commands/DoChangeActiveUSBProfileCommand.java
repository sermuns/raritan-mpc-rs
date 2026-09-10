/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.USBProfile;
import com.raritan.rrc.data.USBProfilesInfo;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class DoChangeActiveUSBProfileCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "DoChangeActiveUSBProfileCommand";

    public DoChangeActiveUSBProfileCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public CommandResult execute() {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        assert (arrayList != null);
        assert (arrayList.size() > 0);
        assert (arrayList.get(0) instanceof KvmPort);
        KvmPort kvmPort = (KvmPort)arrayList.get(0);
        USBProfilesInfo uSBProfilesInfo = kvmPort.getUsbProfilesInfo();
        assert (uSBProfilesInfo != null);
        USBProfile uSBProfile = (USBProfile)this.getContext().getCommandParameter("SelectedUSBProfile");
        if (!uSBProfile.equals(uSBProfilesInfo.getActiveUSBProfile())) {
            RFBView rFBView = (RFBView)kvmPort.getView();
            rFBView.changeActiveUSBProfile(uSBProfile);
        }
        return new CommandResult(true, "");
    }
}

