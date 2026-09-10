/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.VirtualMediaLocalBean;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.VirtualMediaDisconnectPanel;
import com.raritan.tools.ui.ScreenContext;
import javaclientlib.utils.RRCLogger;

public class VirtualMediaDisconnectLocalPanel
extends VirtualMediaDisconnectPanel {
    private VirtualMediaLocalBean vmLocalBean;

    public VirtualMediaDisconnectLocalPanel(ScreenContext screenContext, String string, VirtualMediaLocalBean virtualMediaLocalBean) {
        super(screenContext, string);
        this.vmLocalBean = virtualMediaLocalBean;
        this.loadShell(virtualMediaLocalBean.getConnectedDrive());
    }

    @Override
    protected void disconnectVm() {
        RRCLogger.log(300, 1, "Disconnecting drive for port " + this.portKey);
        this.vmLocalBean.getVmCore().disconnect(false);
        this.vmLocalBean.setDriveConnected(false);
        this.vmLocalBean.setSwitchedFlag(false);
        ((RRCScreenContext)this.scrContext).toggleMenuBar(this.portKey);
        this.scrContext.getSelectedDrives().remove(this.vmLocalBean.getConnectedDrive());
        this.scrContext.getVirtualMediaLocalMap().remove(this.portKey);
    }
}

