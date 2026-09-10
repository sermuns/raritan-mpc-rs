/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Component;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.MultiMonitorPort;
import com.raritan.rrc.data.Stream;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.RRCShellInternalFrame;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

public abstract class Port
extends Device {
    public static final String KVM_CLASS = "KVM";
    public static final String SERIAL_CLASS = "Serial";
    public static final String OUTLET_CLASS = "Outlet";
    public static final String HTML_CLASS = "HTML";
    public static final String URL_CLASS = "URL";
    public static final String VIRTUAL_CLASS = "Virtual";
    public static final String URL_TYPE = "URL";
    public static final String ADMIN_TYPE = "VT100Admin";
    public static final String POWERPORT_TYPE = "VT100PowerPort";
    public static final String APPLIANCE_OUTLET_TYPE = "Appliance";
    public static final String PARAGONTARGET_OUTLET_TYPE = "ParagonTarget";
    public static final String UMT_PORT_TYPE = "UMT";
    public static final int KVM_ALLOCATION_BYTES = 0x300000;
    public static final int ADMIN_ALLOCATION_BYTES = 0x700000;
    public static final int POWER_ALLOCATION_BYTES = 0x100000;
    private int concurrUsers = 1;
    protected String viewName;
    protected Device device;
    private String portClass;
    private String portType;
    private String targetDeviceId;
    private int targetDeviceIdIntValue;
    private String connectionId;
    private int portIndex;
    private int parentIndex;
    private ArrayList outletIds = new ArrayList();
    private boolean outletPortFlag = false;
    private String statusToolTip = "";
    private String availabilityToolTip = "";
    private boolean viewNameChanged = true;
    private boolean bladePort = false;
    private BladeChassis parentBladeChassis;

    public Port(ScreenContext screenContext) {
        super(screenContext);
        try {
            this.jbInit();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Port() {
        this(null);
    }

    public boolean isBladePort() {
        return this.bladePort;
    }

    public void setBladePort(boolean bl) {
        this.bladePort = bl;
    }

    @Override
    public void setConnected(boolean bl) {
        if (this.isBladePort()) {
            this.getParentBladeChassis().setConnected(bl);
        }
        super.setConnected(bl);
    }

    @Override
    public void setId(String string) {
        String string2 = string;
        if (!string.contains("FG")) {
            this.setUniquePortId(string);
        }
        if (string != null && !string.startsWith("//*[@id=")) {
            string2 = "//*[@id=%s]".replaceFirst("%s", string);
        }
        super.setId(string2);
    }

    public void addOutletId(String string) {
        this.outletIds.add(string);
    }

    public ArrayList getOutletIds() {
        return this.outletIds;
    }

    public void setDeviceClass(String string) {
        this.portClass = string;
    }

    @Override
    public String getDeviceClass() {
        return this.portClass;
    }

    public void setPortType(String string) {
        this.portType = string;
    }

    public String getPortType() {
        return this.portType;
    }

    public boolean isAdmin() {
        return this.portType != null && this.portType.equalsIgnoreCase("admin");
    }

    public boolean isDiag() {
        return this.portType.equalsIgnoreCase(ADMIN_TYPE) && this.portClass.equalsIgnoreCase(SERIAL_CLASS);
    }

    public boolean isURL() {
        return this.portType.equalsIgnoreCase("URL") && this.portClass.equalsIgnoreCase("URL");
    }

    public void setTargetDeviceId(String string) {
        if (StringUtils.notNullOrEmpty(string)) {
            if (string.startsWith("//*[@id")) {
                this.targetDeviceId = string;
            } else {
                try {
                    this.targetDeviceIdIntValue = Integer.parseInt(string);
                }
                catch (NumberFormatException numberFormatException) {
                    this.targetDeviceIdIntValue = 0;
                }
                StringBuffer stringBuffer = new StringBuffer("//*[@id=");
                stringBuffer.append(this.targetDeviceIdIntValue).append("]");
                this.targetDeviceId = stringBuffer.toString();
            }
        }
    }

    public String getStripTargetDeviceId() {
        if (this.targetDeviceId.startsWith("//*[@id")) {
            return this.targetDeviceId.substring(8, this.targetDeviceId.length() - 1);
        }
        return this.targetDeviceId;
    }

    public String getTargetDeviceId() {
        return this.targetDeviceId;
    }

    public int getTargetDeviceIdIntValue() {
        return this.targetDeviceIdIntValue;
    }

    @Override
    public void setPortStatus(int n, int n2) {
        super.setPortStatus(n, n2);
    }

    public int getPortStatus() {
        return super.getStatus();
    }

    public void setPortIndex(String string) {
        int n = 0;
        try {
            this.portIndex = n = Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            n = 0;
        }
    }

    @Override
    public String getStatusToolTip() {
        return super.getStatusToolTip();
    }

    @Override
    public String getAvailabilityToolTip() {
        return super.getAvailabilityToolTip();
    }

    public int getSortPos() {
        if (this.portClass.equalsIgnoreCase(HTML_CLASS)) {
            return 7;
        }
        if (this.portType.equalsIgnoreCase(ADMIN_TYPE) && this.portClass.equalsIgnoreCase(SERIAL_CLASS)) {
            return 8;
        }
        if (this.portType.equalsIgnoreCase("URL")) {
            return 9;
        }
        if (this.portClass.equals(KVM_CLASS)) {
            if (this.isConnected()) {
                return 2;
            }
            if (super.getStatus() == 0) {
                return 6;
            }
            if (super.getStatus() == 1) {
                return 4;
            }
            if (super.getStatus() == 2) {
                return 5;
            }
            return 5;
        }
        return 6;
    }

    public boolean isGhost() {
        Device device = this.getBaseDevice();
        return this.getGhostMode() == 1 && device != null && device.getGhostMode() == 0;
    }

    public String getDisplayName(int n) {
        Object object;
        String string;
        String string2;
        String string3;
        if (this.portIndex < 0 || this.isAdmin() || this.isDiag()) {
            string3 = "";
            string2 = "";
        } else {
            if (this.isBladePort()) {
                int n2 = this.getParentIndex();
                if (this.isURL()) {
                    string = "";
                    switch (this.portIndex) {
                        case 0: {
                            string = "A";
                            break;
                        }
                        case 1: {
                            string = "B";
                            break;
                        }
                        case 2: {
                            string = "C";
                            break;
                        }
                        case 3: {
                            string = "D";
                            break;
                        }
                        default: {
                            string = "A";
                        }
                    }
                    string3 = new Integer(n2 + 1).toString() + "-" + string;
                } else {
                    string3 = new Integer(n2 + 1).toString() + "-" + new Integer(this.portIndex + 1).toString();
                }
            } else {
                string3 = new Integer(this.portIndex + 1).toString();
            }
            object = this.getDevice();
            string2 = ": ";
        }
        string = this.getName();
        object = string == null || string.length() == 0 || this.isGhost() ? "<Unnamed>" : string;
        if (this.isPrimaryPort()) {
            object = (String)object + " - " + RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale()).getString("DualPortPrimary");
        } else if (this.isSecondaryPort()) {
            object = (String)object + " - " + RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale()).getString("DualPortSecondary");
        }
        if (n == 0) {
            return string3 + string2 + (String)object;
        }
        return (String)object + string2 + string3;
    }

    public Device getBaseDevice() {
        Component component = this;
        while (true) {
            Component component2;
            if ((component2 = component.getParent()) == null) {
                if (component instanceof Device) {
                    return component;
                }
                return null;
            }
            component = component2;
        }
    }

    public int getPortIndex() {
        return this.portIndex;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return this.device;
    }

    @Override
    public synchronized String getViewName() {
        if (StringUtils.nullOrEmpty(this.viewName) || this.viewNameChanged) {
            StringBuffer stringBuffer = new StringBuffer(this.getDevice().getName());
            String string = this.getDevice().getDeviceConnector().getInetAddress().getHostAddress();
            if (string != null) {
                if (stringBuffer.length() == 0) {
                    stringBuffer.append(string);
                } else {
                    stringBuffer.append(" at ").append(string);
                }
            }
            stringBuffer.append(" :: ").append(this.getName());
            stringBuffer.insert(0, this.getDevice().getDeviceModel() + " ");
            this.viewName = stringBuffer.toString();
            this.viewNameChanged = false;
        }
        return this.viewName;
    }

    @Override
    public void disconnect() {
        Object object;
        ArrayList arrayList = null;
        if (this.scrContext != null && ((RRCScreenContext)this.scrContext).getListOfOpenPorts() != null) {
            arrayList = ((RRCScreenContext)this.scrContext).getListOfOpenPorts();
        }
        String string = this.getId();
        if (arrayList != null && arrayList.size() > 0) {
            if (((IPReach)this.getDevice()).isKvmSwitch()) {
                string = this.getTargetDeviceId();
            }
            if (this.getView() != null && (object = this.getView().getShellInternalFrame()) != null && !((JInternalFrame)object).isClosed()) {
                ((RRCShellInternalFrame)object).setFrameActivatedCommand(null);
                ((RRCShellInternalFrame)object).setFrameClosedCommand(null);
                ((RRCShellInternalFrame)object).setFrameDeActivatedCommand(null);
                ((JComponent)object).setVisible(false);
                ((JInternalFrame)object).dispose();
            }
            ((RRCScreenContext)this.scrContext).removePortInObservable(this);
        }
        this.arrEvenMsg = null;
        object = this.connector;
        if (object != null && ((DeviceConnector)object).isP2scPort()) {
            ((DeviceConnector)object).disConnect();
            this.connector = null;
        }
        System.gc();
    }

    public abstract Stream getStream();

    public abstract DeviceView getView();

    @Override
    public String getConnectionId() {
        return this.connectionId;
    }

    @Override
    public void setConnectionId(String string) {
        this.connectionId = string;
    }

    public void setUpdateFrequency(long l) {
    }

    public boolean isClass(String string) {
        return this.getDeviceClass().equalsIgnoreCase(string);
    }

    public boolean isType(String string) {
        return this.getPortType().equalsIgnoreCase(string);
    }

    @Override
    public String getPortKey() {
        return this.getViewName() + this.getPortIndex();
    }

    private void jbInit() throws Exception {
    }

    public boolean isOutletPort() {
        return this.outletPortFlag;
    }

    public void setOutLetPort(boolean bl) {
        this.outletPortFlag = bl;
    }

    public int getConcurrUsers() {
        return this.concurrUsers;
    }

    public void setConcurrUsers(int n) {
        this.concurrUsers = n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setName(String string) {
        Port port = this;
        synchronized (port) {
            this.viewNameChanged = true;
            super.setName(string);
        }
        MPCUtil.notifyOpenPortsObservers((RRCScreenContext)this.scrContext);
    }

    public void setParentIndex(int n) {
        this.parentIndex = n;
    }

    public int getParentIndex() {
        return this.parentIndex;
    }

    @Override
    public int getRdmStatAvailable() {
        return super.getRdmStatAvailable();
    }

    public void setParentBladeChassis(BladeChassis bladeChassis) {
        this.parentBladeChassis = bladeChassis;
    }

    public BladeChassis getParentBladeChassis() {
        return this.parentBladeChassis;
    }

    public boolean isPrimaryPort() {
        return this.getDevice().getMultiMonitorPortHandler().isPrimaryPort(this);
    }

    public boolean isPrimaryOrSinglePort() {
        return this.getDevice().getMultiMonitorPortHandler().isPrimaryOrSinglePort(this);
    }

    public boolean isSecondaryPort() {
        return this.getDevice().getMultiMonitorPortHandler().isSecondaryPort(this);
    }

    public Point getMonitorLocation() {
        return this.getDevice().getMultiMonitorPortHandler().getLocation(this);
    }

    public int getMonitorIndex() {
        return this.getDevice().getMultiMonitorPortHandler().getMonitorIndex(this);
    }

    public boolean isTopLeft() {
        Point point = this.getDevice().getMultiMonitorPortHandler().getLocation(this);
        if (point == null || point.x == 0 && point.y == 0) {
            return true;
        }
        for (Port port : this.getAssociatedPorts()) {
            if (port.getPortStatus() == 0) continue;
            return false;
        }
        return true;
    }

    public boolean isMultiMonitorPort() {
        for (Port port : this.getAssociatedPorts()) {
            if (port.getPortStatus() == 0) continue;
            return true;
        }
        return false;
    }

    public List<MultiMonitorPort.PortConfig> getSecondaryPortConfigs() {
        return this.getDevice().getMultiMonitorPortHandler().getSecondaryPorts(this);
    }

    public List<Port> getSecondaryPorts() {
        Vector<Port> vector = new Vector<Port>();
        List<MultiMonitorPort.PortConfig> list = this.getSecondaryPortConfigs();
        if (list != null) {
            for (MultiMonitorPort.PortConfig portConfig : list) {
                Port port = this.getDevice().getPortByTargetDeviceId(portConfig.getPortId());
                if (port == null) continue;
                vector.add(port);
            }
        }
        return vector;
    }

    public MultiMonitorPort.PortConfig getPrimaryPortConfig() {
        return this.getDevice().getMultiMonitorPortHandler().getPrimaryPort(this);
    }

    public Port getPrimaryPort() {
        MultiMonitorPort.PortConfig portConfig = this.getPrimaryPortConfig();
        if (portConfig != null) {
            return this.getDevice().getPortByTargetDeviceId(portConfig.getPortId());
        }
        return null;
    }

    public List<Port> getAssociatedPorts() {
        Port port;
        Vector<Port> vector = new Vector<Port>();
        if (this.isPrimaryPort()) {
            return this.getSecondaryPorts();
        }
        if (this.isSecondaryPort() && (port = this.getPrimaryPort()) != null) {
            vector.add(port);
            for (Port port2 : port.getSecondaryPorts()) {
                if (port2 == this) continue;
                vector.add(port2);
            }
        }
        return vector;
    }

    public String toString() {
        return "Port: id=" + this.getId() + " - portNo=" + this.getPortIndex() + " - targetID=" + this.getTargetDeviceId() + " - name=" + this.getName();
    }
}

