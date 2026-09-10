/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.commands.DoModifyProfileCommand;
import com.raritan.rrc.ui.panes.AddModifyConnectionPanel;
import com.raritan.rrc.ui.panes.CompressionPanel;
import com.raritan.rrc.ui.panes.ConnectPanel;
import com.raritan.rrc.ui.panes.SecurityPanel;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ModifyConnectionPanel
extends AddModifyConnectionPanel {
    private static final long serialVersionUID = 4310895560293477283L;
    private DevicePreferences devPrefs = null;
    private IPReach device = null;

    public ModifyConnectionPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.setShell(this.bundle.getString("ModifyConnectionDialog.title"));
        this.ok.setCommand(new DoModifyProfileCommand(this.scrContext));
        this.apply.setCommand(new DoModifyProfileCommand(this.scrContext));
    }

    @Override
    public void makeLayout() {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.addConnectionTabbedPane = new JTabbedPane();
        String string = raritanPropertyResourceBundle.getString("ConnectTab.name");
        this.connectPanel = new ConnectPanel(false, this.scrContext);
        this.addConnectionTabbedPane.add(string, this.connectPanel);
        this.compressionPanel = new CompressionPanel(false, this.scrContext);
        this.compressionPanel.setFocusable(true);
        string = raritanPropertyResourceBundle.getString("CompressionTab.name");
        this.addConnectionTabbedPane.add(string, this.compressionPanel);
        this.securityPanel = new SecurityPanel(false, this.scrContext);
        this.securityPanel.setFocusable(true);
        string = raritanPropertyResourceBundle.getString("SecurityTab.name");
        this.addConnectionTabbedPane.add(string, this.securityPanel);
        this.add((Component)this.addConnectionTabbedPane, "Center");
        this.add((Component)this.doButtonWidget(), "Last");
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public void fillComponents(CommandContext commandContext) {
        this.device = (IPReach)commandContext.getCommandParameter("devices");
        DevicePreferences devicePreferences = this.device.getDevPrefs();
        this.devPrefs = new DevicePreferences();
        this.devPrefs.setDescription(this.device.getDescription());
        this.devPrefs.setConnectionType(devicePreferences.getConnectionType());
        this.devPrefs.setProductType(devicePreferences.getProductType());
        this.devPrefs.setFindBy(devicePreferences.getFindBy());
        switch (devicePreferences.getConnectionType()) {
            case 1: {
                this.devPrefs.setPhone(devicePreferences.getPhone());
                this.devPrefs.setModem(devicePreferences.getModem());
                break;
            }
            case 2: {
                List list = this.device.getAddressList();
                switch (devicePreferences.getFindBy()) {
                    case 0: {
                        this.devPrefs.setIp(devicePreferences.getIp());
                        this.devPrefs.setName(this.device.getName());
                        this.devPrefs.setDnsName(this.device.getDnsName());
                        break;
                    }
                    case 1: {
                        this.devPrefs.setName(devicePreferences.getName());
                        if (list.size() > 0) {
                            this.devPrefs.setIp(((InetAddress)list.get(0)).getHostAddress());
                        }
                        this.devPrefs.setDnsName(this.device.getDnsName());
                        break;
                    }
                    case 2: {
                        this.devPrefs.setDnsName(devicePreferences.getDnsName());
                        this.devPrefs.setName(this.device.getName());
                        if (list.size() <= 0) break;
                        this.devPrefs.setIp(((InetAddress)list.get(0)).getHostAddress());
                    }
                }
                break;
            }
        }
        this.devPrefs.setPort(devicePreferences.getPort());
        this.devPrefs.setHttpsPort(devicePreferences.getHttpsPort());
        this.devPrefs.setConnectionSpeed(devicePreferences.getConnectionSpeed());
        this.devPrefs.setColorDepth(devicePreferences.getColorDepth());
        this.devPrefs.setProgressiveUpdate(devicePreferences.isProgressiveUpdate());
        this.devPrefs.setFlowControl(devicePreferences.isFlowControl());
        this.devPrefs.setSmoothing(devicePreferences.getSmoothing());
        this.devPrefs.setFramesPerSecond(devicePreferences.getFramesPerSecond());
        this.devPrefs.setKey(devicePreferences.getKey());
        this.devPrefs.setUserName(devicePreferences.getUserName());
        this.devPrefs.setPassword(devicePreferences.getPassword());
        this.connectPanel.setCommandKey(commandContext.getCommandKey());
        this.connectPanel.fillDevicePreferences(this.devPrefs);
        this.compressionPanel.fillDevicePreferences(this.devPrefs);
        this.securityPanel.fillDevicePreferences(this.devPrefs);
        this.addConnectionTabbedPane.setSelectedIndex(0);
        this.connectPanel.setDefaultFocussedComponent();
        this.connectPanel.fillComponents(commandContext);
        this.apply.setEnabled(false);
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        this.connectPanel.feedDevicePreferences(this.devPrefs);
        this.compressionPanel.feedDevicePreferences(this.devPrefs);
        this.securityPanel.feedDevicePreferences(this.devPrefs);
        commandContext.setCommandParameter("connectionInfo", this.devPrefs);
        commandContext.setCommandParameter("devices", this.device);
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.addActionListener(this);
        this.apply = new CommandButton(this.bundle.getString("ModifyConnection.applyLabel"), this.scrContext);
        this.apply.addActionListener(this);
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        jPanel.add(this.apply);
        return jPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
        Object object = actionEvent.getSource();
        if (object instanceof JComboBox) {
            this.apply.setEnabled(true);
        } else {
            this.apply.setEnabled(false);
        }
    }

    public void setApplyButton(boolean bl) {
        this.apply.setEnabled(bl);
    }

    public void setG2Panels(boolean bl) {
        this.addConnectionTabbedPane.setEnabledAt(1, bl);
        this.addConnectionTabbedPane.setEnabledAt(2, bl);
    }

    @Override
    protected void setVisibleAfterCommand(Command command) {
        if (command != null && this.apply != null && this.ok != null && command.equals(this.apply.getCommand())) {
            super.setVisibleAfterCommand(this.ok.getCommand());
            return;
        }
        super.setVisibleAfterCommand(command);
    }
}

