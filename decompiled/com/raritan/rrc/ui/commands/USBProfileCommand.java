/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.USBProfilesInfo;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class USBProfileCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "USBProfileCommand";

    public USBProfileCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public boolean isExecutable() {
        USBProfilesInfo uSBProfilesInfo;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList == null || arrayList.size() == 0 || !(arrayList.get(0) instanceof KvmPort)) {
            return false;
        }
        KvmPort kvmPort = (KvmPort)arrayList.get(0);
        if (kvmPort.isConnected() && (uSBProfilesInfo = kvmPort.getUsbProfilesInfo()) != null) {
            return uSBProfilesInfo.getUSBProfiles().size() > 0;
        }
        return false;
    }
}

