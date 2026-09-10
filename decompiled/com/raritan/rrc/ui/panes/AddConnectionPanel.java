/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoNewProfileCommand;
import com.raritan.rrc.ui.panes.AddModifyConnectionPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.util.Constants;
import java.net.InetAddress;
import java.util.List;

public class AddConnectionPanel
extends AddModifyConnectionPanel {
    private static final long serialVersionUID = 1L;
    private DevicePreferences devPrefs = null;
    private IPReach device = null;

    public AddConnectionPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.setShell(this.bundle.getString("AddConnectionDialog.title"));
        this.ok.setCommand(new DoNewProfileCommand(this.scrContext));
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.device = (IPReach)commandContext.getCommandParameter("devices");
        if (this.device == null) {
            this.devPrefs = new DevicePreferences();
            this.devPrefs.setPort(Integer.parseInt(Constants.NETWORKCONFIG_DEFAULT_PORT));
        } else {
            if (this.device.getDevPrefs() == null) {
                this.devPrefs = new DevicePreferences();
                this.devPrefs.setDescription(this.device.getDescription());
                this.devPrefs.setName(this.device.getName());
                this.devPrefs.setDnsName(this.device.getDnsName());
                List list = this.device.getAddressList();
                assert (list.size() > 0);
                this.devPrefs.setIp(((InetAddress)list.get(0)).getHostAddress());
                this.devPrefs.setPort((int)this.device.getIPPort());
            } else {
                DevicePreferences devicePreferences = this.device.getDevPrefs();
                this.devPrefs = new DevicePreferences();
                this.devPrefs.setDescription(this.device.getDescription());
                this.devPrefs.setProductType(devicePreferences.getProductType());
                this.devPrefs.setName(this.device.getName());
                this.devPrefs.setDnsName(this.device.getDnsName());
                this.devPrefs.setIp(devicePreferences.getIp());
                this.devPrefs.setPort(Integer.parseInt(Constants.NETWORKCONFIG_DEFAULT_PORT));
                this.devPrefs.setConnectionSpeed(devicePreferences.getConnectionSpeed());
                this.devPrefs.setColorDepth(devicePreferences.getColorDepth());
                this.devPrefs.setProgressiveUpdate(devicePreferences.isProgressiveUpdate());
                this.devPrefs.setFlowControl(devicePreferences.isFlowControl());
                this.devPrefs.setSmoothing(devicePreferences.getSmoothing());
                this.devPrefs.setFramesPerSecond(devicePreferences.getFramesPerSecond());
            }
            this.devPrefs.setConnectionType(2);
            this.devPrefs.setFindBy(this.getProfileBy(((RRCScreenContext)this.scrContext).getCreateProfileBy()));
        }
        this.connectPanel.setCommandKey(commandContext.getCommandKey());
        this.connectPanel.fillDevicePreferences(this.devPrefs);
        this.compressionPanel.fillDevicePreferences(this.devPrefs);
        this.securityPanel.fillDevicePreferences(this.devPrefs);
        this.addConnectionTabbedPane.setSelectedIndex(0);
        this.connectPanel.setDefaultFocussedComponent();
        this.setG2Panels(false);
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        this.connectPanel.feedDevicePreferences(this.devPrefs);
        this.compressionPanel.feedDevicePreferences(this.devPrefs);
        this.securityPanel.feedDevicePreferences(this.devPrefs);
        commandContext.setCommandParameter("connectionInfo", this.devPrefs);
        commandContext.setCommandParameter("devices", this.device);
    }

    public void setG2Panels(boolean bl) {
        this.addConnectionTabbedPane.setEnabledAt(1, bl);
        this.addConnectionTabbedPane.setEnabledAt(2, bl);
    }

    private int getProfileBy(int n) {
        switch (n) {
            case 0: {
                return 1;
            }
            case 1: {
                return 0;
            }
            case 2: {
                return 2;
            }
        }
        return 1;
    }
}

