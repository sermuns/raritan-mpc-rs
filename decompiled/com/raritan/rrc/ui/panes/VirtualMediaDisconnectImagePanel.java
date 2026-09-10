/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.VirtualMediaBean;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.VirtualMediaDisconnectPanel;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.ui.ScreenContext;
import java.io.IOException;
import javaclientlib.utils.RRCLogger;
import nn.pp.rccore.RCException;
import nn.pp.rccore.VMMountRequestResponse;

public class VirtualMediaDisconnectImagePanel
extends VirtualMediaDisconnectPanel {
    private VirtualMediaBean vmImageBean;
    private RFBView rfbView;

    public VirtualMediaDisconnectImagePanel(ScreenContext screenContext, String string, VirtualMediaBean virtualMediaBean, RFBView rFBView) {
        super(screenContext, string);
        this.vmImageBean = virtualMediaBean;
        this.rfbView = rFBView;
        this.loadShell(virtualMediaBean.getConnectedDrive());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected void disconnectVm() {
        RRCLogger.log(300, 1, "Disconnecting drive for port " + this.portKey);
        if (this.vmImageBean.getConnectionType() == 1) {
            this.vmImageBean.getVmCore().disconnect(false);
            this.vmImageBean.setDriveConnected(false);
            this.vmImageBean.setSwitchedFlag(false);
            this.scrContext.getSelectedDrives().remove(this.vmImageBean.getConnectedDrive());
            this.vmImageBean.setConnectionType(0);
        } else if (this.vmImageBean.getConnectionType() == 2) {
            this.vmImageBean.getVmCore().disconnect(false);
            this.vmImageBean.setDriveConnected(false);
            this.vmImageBean.setSwitchedFlag(false);
            this.vmImageBean.setConnectionType(0);
        } else {
            if (this.vmImageBean.getConnectionType() != 3) {
                RRCLogger.log(300, 1, "Unknown Connection Type for Disconnect." + this.portKey);
                return;
            }
            VMMountRequestResponse vMMountRequestResponse = this.vmImageBean.getSambaRequestDrive1();
            if (vMMountRequestResponse == null) {
                RRCLogger.log(300, 1, "disconnectSambaRequest is NULL. Cannot disconnect Remote ISO Image." + this.portKey);
                return;
            }
            vMMountRequestResponse.setOption(0);
            if (this.rfbView != null) {
                try {
                    this.rfbView.getRCCore().mountOrUnmountRemoteIso(vMMountRequestResponse);
                    RRCLogger.log(300, 1, "Sending Remote ISO mount Disconnect request :" + vMMountRequestResponse.toString());
                    this.vmImageBean.setDriveConnected(false);
                }
                catch (IOException iOException) {
                    RRCLogger.log(300, 1, "Error Disconnecting Samba Mount for port " + this.portKey);
                    RRCLogger.logException(iOException);
                }
                catch (RCException rCException) {
                    RRCLogger.log(300, 1, "Error Disconnecting Samba Mount for port " + this.portKey);
                    RRCLogger.logException(rCException);
                }
                if (this.rfbView.isSambaDisconnectSuccessful()) {
                    this.rfbView.setSambaDisconnectSuccessful(false);
                    this.vmImageBean.setSwitchedFlag(false);
                    this.vmImageBean.setConnectionType(0);
                }
            }
        }
        ((RRCScreenContext)this.scrContext).toggleMenuBar(this.portKey);
        if (this.vmImageBean.getConnectionType() != 3) {
            this.scrContext.getVirtualMediaImageMap().remove(this.portKey);
        }
    }
}

