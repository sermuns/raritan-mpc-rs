/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.URLPort;
import com.raritan.rrc.data.VirtualBladeChassis;
import com.raritan.rrc.ui.models.DeviceDisplayLabelExtractor;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.util.image.DualImageFactory;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import nn.pp.common.ResourceLoader;

public class NavigatorRenderer
extends DefaultTreeCellRenderer {
    public static final String PROFILED_STATE = "PROFILED";
    public static final String NO_PROFILE_STATE = "NOT_PROFILED";
    public static final String MODEM_PROFILE_STATE = "MODEM_PROFILED";
    protected static final String DEVICE_CONNECTED_STATE = "CONNECTED";
    protected static final String DEVICE_UNAVAILABLE_STATE = "UNAVAILABLE";
    protected static final String DEVICE_DISCONNECTED_STATE = "AVAILABLE";
    public static final int UNAVAILABLE = 0;
    public static final int AVAILABLE = 1;
    public static final int BUSY = 2;
    private static final long serialVersionUID = -3067401733113474686L;
    protected ScreenContext scrContext;
    private String rootLabelString;
    private JLabel rootLabel = null;
    private String rootIcon;
    private String deviceConnected;
    private String deviceDisonnected;
    private String deviceUnavailable;
    private String profiledDevice;
    private String modemProfiledDevice;
    private String notProfiledDevice;
    private String virtualMedia;
    private String serialPort;
    private String serialPortActive;
    private String paragonDevice;
    private String paragonDeviceActive;
    private String kvmPort;
    private String portUnavalable;
    private String portOccupied;
    private String kvmPortActive;
    private String htmlPort;
    private boolean flag;
    private String htmlPortActive;
    private DeviceDisplayLabelExtractor labelExtractor = null;

    public NavigatorRenderer(ScreenContext screenContext, boolean bl) {
        this.scrContext = screenContext;
        this.flag = bl;
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.rootLabelString = raritanPropertyResourceBundle.getString("NavigatorRoot.name");
        this.rootIcon = raritanPropertyResourceBundle.getString("NavigatorRoot.image");
        this.deviceConnected = raritanPropertyResourceBundle.getString("DeviceConnected.image");
        this.deviceDisonnected = raritanPropertyResourceBundle.getString("DeviceDisconnected.image");
        this.deviceUnavailable = raritanPropertyResourceBundle.getString("DeviceUnavailable.image");
        this.profiledDevice = raritanPropertyResourceBundle.getString("NewProfileAction.image");
        this.modemProfiledDevice = raritanPropertyResourceBundle.getString("modemProfiled.image");
        this.notProfiledDevice = raritanPropertyResourceBundle.getString("notProfiledDevice.image");
        this.virtualMedia = raritanPropertyResourceBundle.getString("virtualMedia.image");
        this.serialPort = raritanPropertyResourceBundle.getString("SerialPort.image");
        this.serialPortActive = raritanPropertyResourceBundle.getString("SerialPortActive.image");
        this.kvmPort = raritanPropertyResourceBundle.getString("KvmPort.image");
        this.portUnavalable = raritanPropertyResourceBundle.getString("PortUnavalable.image");
        this.portOccupied = raritanPropertyResourceBundle.getString("PortOccupied.image");
        this.kvmPortActive = raritanPropertyResourceBundle.getString("KvmPortActive.image");
        this.htmlPort = raritanPropertyResourceBundle.getString("HtmlPort.image");
        this.htmlPortActive = raritanPropertyResourceBundle.getString("HtmlPortActive.image");
        this.paragonDevice = raritanPropertyResourceBundle.getString("Paragon.image");
        this.paragonDeviceActive = raritanPropertyResourceBundle.getString("ParagonActive.image");
        this.rootLabel = new JLabel(this.rootLabelString, this.scrContext.getImageIcon(this.rootIcon), 0);
    }

    public ImageIcon getIcon(String string, String string2, int n, boolean bl, boolean bl2) {
        if (string.equals("Paragon")) {
            if (string2.equals(DEVICE_CONNECTED_STATE)) {
                return ResourceLoader.loadImageIcon(this.paragonDeviceActive);
            }
            return ResourceLoader.loadImageIcon(this.paragonDevice);
        }
        if (string.equals("BladeChassis")) {
            if (string2.equals(DEVICE_CONNECTED_STATE)) {
                return ResourceLoader.loadImageIcon(this.kvmPortActive);
            }
            if (n == 0) {
                return ResourceLoader.loadImageIcon(this.portUnavalable);
            }
            if (n == 2) {
                return ResourceLoader.loadImageIcon(this.portOccupied);
            }
            return ResourceLoader.loadImageIcon(this.kvmPort);
        }
        if (string.equals("Serial")) {
            if (string2.equals(DEVICE_CONNECTED_STATE)) {
                return ResourceLoader.loadImageIcon(this.serialPortActive);
            }
            return ResourceLoader.loadImageIcon(this.serialPort);
        }
        if (string.equals("KVM")) {
            if (string2.equals(DEVICE_CONNECTED_STATE)) {
                return ResourceLoader.loadImageIcon(this.kvmPortActive);
            }
            if (n == 0) {
                return ResourceLoader.loadImageIcon(this.portUnavalable);
            }
            if (n == 2) {
                return ResourceLoader.loadImageIcon(this.portOccupied);
            }
            return ResourceLoader.loadImageIcon(this.kvmPort);
        }
        if (string.equals("HTML")) {
            if (bl) {
                return ResourceLoader.loadImageIcon(this.htmlPortActive);
            }
            return ResourceLoader.loadImageIcon(this.htmlPort);
        }
        if (string.equals("URL") || string.equals("VirtualBladeChassis")) {
            return null;
        }
        if (bl) {
            return ResourceLoader.loadImageIcon(this.deviceConnected);
        }
        if (string2.equals(DEVICE_UNAVAILABLE_STATE)) {
            return ResourceLoader.loadImageIcon(this.deviceUnavailable);
        }
        return ResourceLoader.loadImageIcon(this.deviceDisonnected);
    }

    public ImageIcon getConnectionIcon(boolean bl, boolean bl2, String string) {
        if (string.equals("BladeChassis") || string.equals("VirtualBladeChassis")) {
            return null;
        }
        if (bl2) {
            return ResourceLoader.loadImageIcon(this.modemProfiledDevice);
        }
        if (bl) {
            return ResourceLoader.loadImageIcon(this.profiledDevice);
        }
        return ResourceLoader.loadImageIcon(this.notProfiledDevice);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree jTree, Object object, boolean bl, boolean bl2, boolean bl3, int n, boolean bl4) {
        Device device = null;
        if (object != null && object instanceof DefaultMutableTreeNode) {
            if (((DefaultMutableTreeNode)object).isRoot()) {
                return this.rootLabel;
            }
            this.setToolTipText("");
            device = (Device)((DefaultMutableTreeNode)object).getUserObject();
            if (device instanceof Port) {
                Port port = (Port)device;
                String string = port.getDisplayName(device.getSortType());
                if (bl) {
                    if (port.isConnected() && port.hasFocus()) {
                        this.setFont(this.getFont().deriveFont(1));
                    } else {
                        this.setFont(this.getFont().deriveFont(0));
                    }
                } else {
                    this.setFont(this.getFont().deriveFont(0));
                }
                super.getTreeCellRendererComponent(jTree, string, bl, bl2, bl3, n, bl4);
                String string2 = "<html><font>";
                String string3 = "<b>Port Name: </b>" + string;
                String string4 = "";
                String string5 = "<br><b>Status: </b>" + port.getStatusToolTip();
                String string6 = "<br><b>Availability: </b>" + port.getAvailabilityToolTip();
                if (((Port)device).getPortType().equals("VM")) {
                    string4 = string4 + "<br><b>Type: </b>Virtual Media";
                }
                this.setIcon(this.getIcon(((Port)device).getDeviceClass(), device.getState(), port.getPortStatus(), ((Port)device).isConnected(), ((Port)device).isActive()));
                if (port instanceof URLPort) {
                    String string7 = "" + ((URLPort)port).getLink();
                    string2 = string2 + "<b> Name: </b>" + string + "<br><b> URL: " + string7 + "</b>";
                } else {
                    string2 = string2 + string3 + string4 + string5 + string6;
                }
                this.setToolTipText(string2);
            } else {
                String string = "";
                if (device instanceof IPReach) {
                    string = this.labelExtractor.getDisplayLabel(device);
                    this.setToolTipText(this.labelExtractor.getToolTip(device));
                } else if (device instanceof VirtualBladeChassis) {
                    VirtualBladeChassis virtualBladeChassis = (VirtualBladeChassis)device;
                    string = virtualBladeChassis.getDisplayName(device.getSortType());
                    this.setToolTipText(null);
                } else if (device instanceof BladeChassis) {
                    BladeChassis bladeChassis = (BladeChassis)device;
                    string = bladeChassis.getDisplayName(device.getSortType());
                    String string8 = "<html><font>";
                    string8 = string8 + "<b>Blade Chassis Name: </b>" + string;
                    string8 = string8 + "<br><b>Status: </b>" + bladeChassis.getStatusToolTip();
                    string8 = string8 + "<br><b>Availability: </b>" + bladeChassis.getAvailabilityToolTip();
                    this.setToolTipText(string8);
                }
                super.getTreeCellRendererComponent(jTree, string.trim(), bl, bl2, bl3, n, bl4);
                this.setFont(this.getFont().deriveFont(0));
                this.setIcon(DualImageFactory.getDualImageIcon(this.getConnectionIcon(device.isProfiled(), device.isModemProfiled(), device.getDeviceType()), this.getIcon(device.getDeviceType(), device.getState(), device.getStatus(), device.isConnected(), device.isActive())));
            }
        }
        return this;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setFlag(boolean bl) {
        this.flag = bl;
    }

    public void setDeviceDisplayLabelExtractor(DeviceDisplayLabelExtractor deviceDisplayLabelExtractor) {
        this.labelExtractor = deviceDisplayLabelExtractor;
    }
}

