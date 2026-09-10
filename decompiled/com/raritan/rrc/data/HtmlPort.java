/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.Stream;
import com.raritan.rrc.ui.panes.AppletView;
import com.raritan.rrc.ui.panes.DeviceView;

public class HtmlPort
extends Port {
    private AppletView appletView;
    private String sessionId;

    @Override
    public DeviceView getView() {
        return this.appletView;
    }

    public void setView(AppletView appletView) {
        this.appletView = appletView;
    }

    @Override
    public void connect() {
        if (this.isConnected()) {
            return;
        }
        if (this.getDeviceConnector() != null) {
            this.sessionId = this.getDeviceConnector().getSessionID();
            this.setConnected(true);
            this.setState("CONNECTED");
            this.firePropertyChange("DEVICE_PORT_VIEW_ADD", null, null);
        }
    }

    @Override
    public void disconnect() {
        this.setConnected(false);
        this.setState("AVAILABLE");
        super.disconnect();
        if (this.appletView != null) {
            ((AppletView)this.getView()).feedCommandContext(null);
            this.appletView.setVisible(false);
            this.appletView = null;
        }
        this.firePropertyChange("DEVICE_PORT_VIEW_REMOVE", null, null);
    }

    public boolean isAdministrator() {
        DeviceConnector deviceConnector = this.device.getDeviceConnector();
        boolean bl = (deviceConnector.getPermissions() & 2L) != 0L;
        boolean bl2 = deviceConnector.getServerID().getProtocolVersion() > 11;
        boolean bl3 = (deviceConnector.getServerID().getSecurityFlags() & 1) != 0;
        return bl && bl2 && bl3;
    }

    @Override
    public Stream getStream() {
        return null;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}

