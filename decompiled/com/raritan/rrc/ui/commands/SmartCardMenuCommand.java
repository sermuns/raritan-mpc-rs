/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javax.swing.Action;

public class SmartCardMenuCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "SmartCardMenuCommand";

    public SmartCardMenuCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public boolean isExecutable() {
        KvmPort kvmPort = this.getKvmPort();
        if (kvmPort != null && kvmPort.isConnected()) {
            return this.getAction(kvmPort).isEnabled();
        }
        return false;
    }

    public String getToolTip() {
        KvmPort kvmPort = this.getKvmPort();
        if (kvmPort != null) {
            return (String)this.getAction(kvmPort).getValue("ShortDescription");
        }
        return null;
    }

    private KvmPort getKvmPort() {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList == null || arrayList.size() == 0 || !(arrayList.get(0) instanceof KvmPort)) {
            return null;
        }
        return (KvmPort)arrayList.get(0);
    }

    protected Action getAction(KvmPort kvmPort) {
        return kvmPort.getSmartCardAction();
    }
}

