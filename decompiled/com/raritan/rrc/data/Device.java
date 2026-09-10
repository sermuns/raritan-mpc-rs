/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Component;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.DeviceHandlerInterface;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.DeviceSecurity;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.MultiMonitorPort;
import com.raritan.rrc.data.MultiMonitorPortHandler;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.VirtualBladeChassis;
import com.raritan.rrc.ui.MPCScanFrame;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.ErrorHandlerImpl;
import com.raritan.rrc.util.DeviceComparator;
import com.raritan.rrc.util.StringUtils;
import com.raritan.rrc.util.TR_NOTIFY_MSG_CACHE;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javaclientlib.clientlib.ITRConnection;
import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.tr.TRSRVR_SERVER_ID;
import javaclientlib.utils.RRCLogger;
import javax.swing.JInternalFrame;
import javax.swing.event.SwingPropertyChangeSupport;

public abstract class Device
extends Component
implements ITRConnection {
    public static final String IP_REACH_TYPE = "IP-Reach";
    public static final String PARAGON_TYPE = "Paragon";
    public static final String KVMSWITCH_TYPE = "KVMSwitch";
    public static final String KVMSWITCH_MODEL = "KVMSwitch";
    public static final String SX_TYPE = "SX";
    public static final String KX2_TYPE = "Dominion_KX2";
    public static final String POWERSTRIP_TYPE = "PowerStrip";
    public static final String BLADE_CHASSIS_TYPE = "BladeChassis";
    public static final String BLADE_CHASSIS_MODEL = "BladeChassis";
    public static final String TIER_DEVICE_TYPE = "TierDevice";
    public static final String VIRTUAL_BLADE_CHASSIS_TYPE = "VirtualBladeChassis";
    public static final String PORT_GROUP_TYPE = "PortGroup";
    public static final String MULTI_MONITOR_PORT_TYPE = "MultiMonitorPort";
    public static final String DKX_MODEL = "DKX";
    public static final String TR36X_MODEL = "TR36x";
    public static final String TR01_MODEL = "TR01";
    public static final String RX440_MODEL = "RX440";
    public static final String RX880_MODEL = "RX880";
    public static final String COMPUSWITCH_MODEL = "CompuSwitch";
    public static final String UMT832_MODEL = "UMT832";
    public static final String UMT1664_MODEL = "UMT1664";
    public static final String PCR8_MODEL = "PCR8";
    public static final String PCS12_MODEL = "PCS12";
    public static final String PCS20_MODEL = "PCS20";
    public static final String DISCONNECTED_STATE = "AVAILABLE";
    public static final String CONNECTED_STATE = "CONNECTED";
    public static final String UNAVAILABLE_STATE = "UNAVAILABLE";
    public static final String PROFILED_STATE = "PROFILED";
    public static final String NO_PROFILE_STATE = "NOT_PROFILED";
    public static final String MODEM_PROFILE_STATE = "MODEM_PROFILED";
    public static final String DEVICE_CONNECTED = "DEVICE_CONNECTED";
    public static final String DEVICE_AUTHENTICATED = "DEVICE_AUTHENTICATED";
    public static final String DEVICE_CONNECTION_LOST = "DEVICE_CONNECTION_LOST";
    public static final String DEVICE_DISCONNECTED = "DEVICE_DISCONNECTED";
    public static final String DEVICE_PORTS_ADDED = "DEVICE_PORTS_ADD";
    public static final String DEVICE_PORTS_REMOVE = "DEVICE_PORTS_REMOVE";
    public static final String DEVICE_PORT_VIEW_ADD = "DEVICE_PORT_VIEW_ADD";
    public static final String DEVICE_PORT_VIEW_REMOVE = "DEVICE_PORT_VIEW_REMOVE";
    public static final String DEVICE_STATE_CHANGED = "DEVICE_STATE_CHANGED";
    public static final String DEVICE_NAME_CHANGED = "DEVICE_NAME_CHANGED";
    public static final String DEVICE_PROFILE_STATE_CHANGED = "DEVICE_PROFILE_STATE_CHANGED";
    public static final String DEVICE_ACTIVATED = "DEVICE_ACTIVATED";
    public static final String DEVICE_SELECTED = "DEVICE_SELECTED";
    public static final String DEVICE_DESELECTED = "DEVICE_DESELECTED";
    public static final int UNAVAILABLE = 0;
    public static final int AVAILABLE = 1;
    public static final int BUSY = 2;
    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;
    private String uniquePortId;
    boolean virtual = false;
    public static final int SORT_ActiveDevice = 1;
    public static final int SORT_ActiveChannel = 2;
    public static final int SORT_InactiveDevice = 3;
    public static final int SORT_Available = 4;
    public static final int SORT_Unavailable = 5;
    public static final int SORT_Unequipped = 6;
    public static final int SORT_Admin = 7;
    public static final int SORT_Diagnostics = 8;
    public static final int SORT_URL = 9;
    public static final int SORT_TYPE_CHANNEL = 0;
    public static final int SORT_TYPE_NAME = 1;
    public static final int SORT_TYPE_STATUS = 2;
    private static final String[] ENUM_DEVICE_TYPES = new String[]{"IP-Reach", "Paragon", "KVMSwitch", "SX", "PowerStrip"};
    private static final String[] ENUM_MODEL_TYPES = new String[]{"DKX", "TR36x", "TR01", "RX440", "RX880", "CompuSwitch", "UMT1664", "UMT832", "PCR8", "PCS12", "PCS20"};
    private static final String[] ENUM_STATES = new String[]{"AVAILABLE", "CONNECTED", "UNAVAILABLE"};
    protected ArrayList arrEvenMsg = null;
    protected DevicePreferences devPrefs = null;
    protected DeviceConnector connector;
    protected SwingPropertyChangeSupport deviceListeners = new SwingPropertyChangeSupport(this);
    protected ScreenContext scrContext = null;
    private String name;
    protected String description;
    private String dnsName;
    protected boolean profiled = false;
    protected boolean isModemProfiled = false;
    private String id = "0";
    private String rdmId = "";
    private long ipPort;
    private String deviceType;
    private String deviceModel = "";
    private boolean rootDevice;
    private volatile boolean connected;
    private String state = "AVAILABLE";
    private boolean active;
    private String notifyMsg;
    private DeviceSecurity security;
    protected RaritanPropertyResourceBundle bundle;
    private KvmPort activeKvmPort;
    private String kvmSwitchId;
    private boolean kvmSwitchEnabled;
    private boolean cancelLogin = false;
    private ArrayList addressList = new ArrayList();
    private static int sortType = 0;
    private int colorCalibSpeed = 60000;
    private DeviceView deviceView;
    private int isGhostingEnabled;
    private String rdmSessionId = null;
    private String rdmSessionKey = null;
    private Map appletParameters = null;
    private DeviceComparator comparator = new DeviceComparator();
    private DeviceHandlerInterface handler;
    private LinkedHashMap<VirtualBladeChassis, Vector<String>> portsWithGroupsMap;
    private MultiMonitorPortHandler multiMonitorHandler = new MultiMonitorPortHandler();
    private int upgradeDuration = 20;
    private File rscFileHandle;
    private boolean requireRSAAcceptance = false;
    private boolean rsaEnabled = false;
    private boolean rsaAccepted = false;
    private boolean scanSupported = false;
    private boolean doNotshowErrorAgain = false;
    private String statusToolTip = "";
    private String availabilityToolTip = "";
    private int rdmStatAvailable = -1;
    private int status;
    public static final int STATAVAILABLE_AVAILABLE = 0;
    public static final int STATAVAILABLE_OCCUPIED = 1;
    public static final int STATAVAILABLE_BUSY = 2;
    public static final int STATAVAILABLE_UNAVAILABLE = 3;
    public static final int STATAVAILABLE_INACCESSIBLE = 4;

    public boolean isDoNotshowErrorAgain() {
        return this.doNotshowErrorAgain;
    }

    public void setDoNotshowErrorAgain(boolean bl) {
        this.doNotshowErrorAgain = bl;
    }

    public void showCommunicationError(String string, String string2) {
        RRCLogger.log(200, "Received Communication Error:" + string + " Showing Popup:" + this.isDoNotshowErrorAgain());
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)((RRCScreenContext)this.scrContext).getPanelMediator().getParent();
        if (!this.isDoNotshowErrorAgain()) {
            boolean bl = ErrorHandlerImpl.getInstance().showErrorWithOptionToRemember(raritanDesktopPane, string, string2);
            this.setDoNotshowErrorAgain(bl);
        }
    }

    public void setUpgradeDuration(int n) {
        this.upgradeDuration = n;
    }

    public int getUpgradeDuration() {
        return this.upgradeDuration;
    }

    public Device() {
        this(null);
    }

    public Device(ScreenContext screenContext) {
        this.scrContext = screenContext;
        if (this.scrContext != null) {
            this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        }
    }

    private boolean isType(String string) {
        return string.equals(this.deviceModel);
    }

    public boolean isModelKX() {
        return this.isType(DKX_MODEL);
    }

    public static boolean isFixedSortPos(int n) {
        return n == 7 || n == 8 || n == 9;
    }

    public int getColorCalibSpeed() {
        return this.colorCalibSpeed;
    }

    public void setColorCalibSpeed(int n) {
        this.colorCalibSpeed = n;
    }

    public void setName(String string) {
        String string2 = this.name;
        this.name = string;
        this.firePropertyChange(DEVICE_STATE_CHANGED, null, null);
        this.firePropertyChange(DEVICE_NAME_CHANGED, string2, string);
        DeviceTreeController.getInstance((RRCScreenContext)this.scrContext).treeNodeChanged(this);
    }

    public String getName() {
        return this.name;
    }

    public void setId(String string) {
        if (StringUtils.notNullOrEmpty(string)) {
            this.id = string;
        }
    }

    public String getId() {
        return this.id;
    }

    public void setRdmId(String string) {
        if (StringUtils.notNullOrEmpty(string)) {
            this.rdmId = string;
        }
    }

    public String getRdmId() {
        return this.rdmId;
    }

    private final String getIPAddress() {
        if (this.addressList.size() > 0) {
            return ((InetAddress)this.addressList.get(0)).getHostAddress();
        }
        return "";
    }

    public String getIPAddressStringNoPhone() {
        return this.getIPAddress();
    }

    public void setIPPort(long l) {
        this.ipPort = l;
    }

    public long getIPPort() {
        if (this.isProfiled()) {
            this.getDevPrefs().getPort();
        }
        return this.ipPort;
    }

    public int getHttpsPort() {
        if (this.isProfiled()) {
            return this.getDevPrefs().getHttpsPort();
        }
        return ((RRCScreenContext)this.getContext()).getAppSettings().getDefaultHttpsPort();
    }

    public void setDeviceType(String string) {
        if (StringUtils.findInStringArray(string, ENUM_DEVICE_TYPES)) {
            this.deviceType = string;
        }
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setGhostMode(int n) {
        this.isGhostingEnabled = n;
    }

    public int getGhostMode() {
        return this.isGhostingEnabled;
    }

    public String getDeviceClass() {
        return "";
    }

    public void setDeviceModel(String string) {
        this.deviceModel = string;
    }

    public String getDeviceModel() {
        return this.deviceModel;
    }

    public void setRootDevice(boolean bl) {
        this.rootDevice = bl;
    }

    public boolean isRootDevice() {
        return this.rootDevice;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void setConnected(boolean bl) {
        this.connected = bl;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean bl) {
        this.active = bl;
        if (this.active) {
            this.firePropertyChange(DEVICE_ACTIVATED, null, null);
        }
    }

    public boolean isProfiled() {
        return this.profiled;
    }

    public void setProfiled(boolean bl) {
        this.profiled = bl;
        this.firePropertyChange(DEVICE_STATE_CHANGED, null, null);
    }

    public void setState(String string) {
        if (StringUtils.findInStringArray(string, ENUM_STATES) && !string.equals(this.state)) {
            String string2 = this.state;
            this.state = string;
            this.firePropertyChange(DEVICE_STATE_CHANGED, string2, this.state);
            DeviceTreeController.getInstance((RRCScreenContext)this.scrContext).treeNodeChanged(this);
        } else {
            DeviceTreeController.getInstance((RRCScreenContext)this.scrContext).treeNodeChanged(this);
        }
    }

    public String getState() {
        return this.state;
    }

    public abstract String getViewName();

    public void setConnectionId(String string) {
        if (StringUtils.notNullOrEmpty(string)) {
            this.id = string;
        }
    }

    public String getConnectionId() {
        return this.id;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int n) {
        sortType = n;
    }

    @Override
    public boolean login(TRSRVR_SERVER_ID tRSRVR_SERVER_ID, TRLIB_USERINFO tRLIB_USERINFO, boolean bl) {
        return false;
    }

    public TRLIB_USERINFO login() {
        return null;
    }

    @Override
    public boolean loginChallenge(int n, byte[] byArray, byte[] byArray2) {
        return false;
    }

    @Override
    public void notify(int n, int n2) {
    }

    @Override
    public void connect() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void disconnect() {
        Object object;
        try {
            object = this.getChildren();
            if (object != null) {
                Map map = object;
                synchronized (map) {
                    Iterator iterator = object.keySet().iterator();
                    while (iterator.hasNext()) {
                        Device device = (Device)object.get(iterator.next());
                        String string = null;
                        if (device instanceof BladeChassis) {
                            Map map2;
                            BladeChassis bladeChassis = (BladeChassis)device;
                            if (bladeChassis != null && (map2 = bladeChassis.getChildren()) != null) {
                                Map map3 = map2;
                                synchronized (map3) {
                                    Iterator iterator2 = map2.keySet().iterator();
                                    while (iterator2.hasNext()) {
                                        Device device2 = (Device)map2.get(iterator2.next());
                                        if (device2 == null) continue;
                                        string = device2.getPortKey();
                                        if (!device2.isConnected()) continue;
                                        device2.disconnect();
                                    }
                                }
                            }
                        } else if (device != null) {
                            string = device.getPortKey();
                            if (device.isConnected()) {
                                device.disconnect();
                            }
                        }
                        this.clearEntriesInVMMaps(string);
                    }
                }
                this.arrEvenMsg = null;
            }
            this.setState(DISCONNECTED_STATE);
        }
        finally {
            this.rscFileHandle = null;
            if (this.scrContext != null) {
                object = ((RRCScreenContext)this.scrContext).getScanFrame();
                if (object != null && ((MPCScanFrame)object).getConnectedDeviceId().equals(this.getId())) {
                    try {
                        ((JInternalFrame)object).setClosed(true);
                    }
                    catch (PropertyVetoException propertyVetoException) {}
                }
                if (((RRCScreenContext)this.scrContext).getSelectedPort() == null) {
                    ((RRCScreenContext)this.scrContext).resetDesktopFocus();
                }
            }
        }
        if (this.portsWithGroupsMap != null) {
            this.portsWithGroupsMap.clear();
            this.portsWithGroupsMap = null;
        }
        this.requireRSAAcceptance = false;
        this.rsaEnabled = false;
        this.rsaAccepted = false;
        this.doNotshowErrorAgain = false;
    }

    private void clearEntriesInVMMaps(String string) {
        if (string != null) {
            if (this.scrContext != null) {
                if (this.scrContext.getVirtualMediaImageMap() != null && this.scrContext.getVirtualMediaImageMap().containsKey(string)) {
                    this.scrContext.getVirtualMediaImageMap().remove(string);
                    RRCLogger.log(300, 4, "Removing PortKey from VirtualMediaImageMap:" + string);
                }
                if (this.scrContext.getVirtualMediaLocalMap() != null && this.scrContext.getVirtualMediaLocalMap().containsKey(string)) {
                    this.scrContext.getVirtualMediaLocalMap().remove(string);
                    RRCLogger.log(300, 4, "Removing PortKey from VirtualMediaLocalMap:" + string);
                }
            } else {
                RRCLogger.log(300, 4, "ScreenContext is NULL in Device while Disconnecting");
            }
        }
    }

    public synchronized DeviceConnector getDeviceConnector() {
        return this.connector;
    }

    public synchronized void setDeviceConnector(DeviceConnector deviceConnector) {
        this.connector = deviceConnector;
    }

    private boolean isListenerAdded(PropertyChangeListener propertyChangeListener) {
        boolean bl = false;
        PropertyChangeListener[] propertyChangeListenerArray = this.deviceListeners.getPropertyChangeListeners();
        for (int i = 0; i < propertyChangeListenerArray.length; ++i) {
            if (!propertyChangeListenerArray[i].getClass().getName().equalsIgnoreCase(propertyChangeListener.getClass().getName())) continue;
            bl = true;
            break;
        }
        return bl;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        boolean bl = this.isListenerAdded(propertyChangeListener);
        if (!bl) {
            this.deviceListeners.addPropertyChangeListener(propertyChangeListener);
        }
    }

    public void addPropertyChangeListener(String string, PropertyChangeListener propertyChangeListener) {
        boolean bl = this.isListenerAdded(propertyChangeListener);
        if (!bl) {
            this.deviceListeners.addPropertyChangeListener(string, propertyChangeListener);
        }
    }

    public void firePropertyChange(String string, Object object, Object object2) {
        this.deviceListeners.firePropertyChange(string, object, object2);
    }

    protected void displayDeviceStatus(int n) {
        this.notifyMsg = this.getNotifyMessage(n);
        this.setNotifyMsg(this.notifyMsg);
    }

    protected void addNotifyMessage(int n) {
        if (this.arrEvenMsg == null) {
            this.arrEvenMsg = new ArrayList();
        }
        if (!this.arrEvenMsg.contains(new Integer(n))) {
            this.arrEvenMsg.add(new Integer(n));
        }
    }

    protected void printMsg(int n) {
        if (this.scrContext != null) {
            this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
            if (this.bundle != null) {
                String string = this.bundle.getString("error.unknown");
                this.scrContext.getLogger().logTextInfo("[" + Device.getActiveNameIP(this, string) + "]: " + this.bundle.getMessage("Device.message" + n, n));
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel("[" + Device.getActiveNameIP(this, string) + "]: " + this.bundle.getMessage("Device.message" + n, n));
            } else {
                this.scrContext.getLogger().logTextInfo("[" + this.getNameIP() + "]: TR Event" + n);
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel("[" + this.getNameIP() + "]:  TR Event" + n);
            }
        }
    }

    public String getNotifyMessage(int n) {
        StringBuffer stringBuffer = new StringBuffer();
        this.notifyMsg = TR_NOTIFY_MSG_CACHE.getNotifyMessage(n);
        stringBuffer.append(" " + (this.notifyMsg == null ? String.valueOf(n) : this.notifyMsg));
        return stringBuffer.toString();
    }

    public String getNotifyMsg() {
        return this.notifyMsg;
    }

    public void setNotifyMsg(String string) {
        this.notifyMsg = string;
    }

    public boolean isModemProfiled() {
        return this.isModemProfiled;
    }

    public void setModemProfiled(boolean bl) {
        this.isModemProfiled = bl;
    }

    public DevicePreferences getDevPrefs() {
        return this.devPrefs;
    }

    public void setDevPrefs(DevicePreferences devicePreferences) {
        this.devPrefs = devicePreferences;
    }

    public ScreenContext getContext() {
        return this.scrContext;
    }

    public void setContext(ScreenContext screenContext) {
        this.scrContext = screenContext;
    }

    public void setDevView(DeviceView deviceView) {
        this.deviceView = deviceView;
    }

    public boolean hasFocus() {
        return this.deviceView != null && this.deviceView.hasFocus();
    }

    public DeviceSecurity getSecurity() {
        return this.security;
    }

    public void setSecurity(DeviceSecurity deviceSecurity) {
        this.security = deviceSecurity;
    }

    public ArrayList getArrEvenMsg() {
        return this.arrEvenMsg;
    }

    public void setArrEvenMsg(ArrayList arrayList) {
        this.arrEvenMsg = arrayList;
    }

    public String getDescription() {
        if (StringUtils.notNullOrEmpty(this.description)) {
            return this.description;
        }
        return this.name;
    }

    public void setDescription(String string) {
        this.description = string;
    }

    public void setActiveKvmPort(KvmPort kvmPort) {
        if (kvmPort != null && this.activeKvmPort != null) {
            this.activeKvmPort.setActive(false);
            this.activeKvmPort.disconnect();
        }
        this.activeKvmPort = kvmPort;
        if (this.activeKvmPort != null && !this.activeKvmPort.isActive()) {
            this.activeKvmPort.setActive(true);
        }
    }

    public Port getActiveKvmPort() {
        return this.activeKvmPort;
    }

    public void enableKvmSwitch(boolean bl) {
        this.kvmSwitchEnabled = bl;
    }

    public boolean isKvmSwitch() {
        return this.kvmSwitchEnabled;
    }

    public void setKvmSwitchId(String string) {
        this.kvmSwitchId = string;
    }

    public String getKvmSwitchId() {
        return this.kvmSwitchId;
    }

    public void setPortStatus(int n, int n2) {
        this.rdmStatAvailable = n2;
        if (n == 0) {
            if (n2 == 0) {
                this.status = 0;
                this.statusToolTip = "Down";
                this.availabilityToolTip = "Idle";
            } else if (n2 == 1) {
                this.status = 2;
                this.statusToolTip = "Down";
                this.availabilityToolTip = "Connected";
            } else if (n2 == 2) {
                this.status = 2;
                this.statusToolTip = "Down";
                this.availabilityToolTip = "Busy";
            } else if (n2 == 3) {
                this.status = 2;
                this.statusToolTip = "Down";
                this.availabilityToolTip = "Unavailable";
            } else if (n2 == 4) {
                this.status = 2;
                this.statusToolTip = "Down";
                this.availabilityToolTip = "All Channels In Use";
            } else {
                this.status = 0;
                this.statusToolTip = "Down";
                this.availabilityToolTip = "Idle";
            }
        }
        if (n == 1) {
            if (n2 == 0) {
                this.status = 1;
                this.statusToolTip = "Up";
                this.availabilityToolTip = "Idle";
            } else if (n2 == 1) {
                this.status = 2;
                this.statusToolTip = "Up";
                this.availabilityToolTip = "Connected";
            } else if (n2 == 2) {
                this.status = 2;
                this.statusToolTip = "Up";
                this.availabilityToolTip = "Busy";
            } else if (n2 == 3) {
                this.status = 2;
                this.statusToolTip = "Up";
                this.availabilityToolTip = "Unavailable";
            } else if (n2 == 4) {
                this.status = 1;
                this.statusToolTip = "Up";
                this.availabilityToolTip = "All Channels In Use";
            } else {
                this.status = 1;
                this.statusToolTip = "Up";
                this.availabilityToolTip = "Idle";
            }
        }
        if (n == 0) {
            this.setState(UNAVAILABLE_STATE);
        } else if (this.isConnected()) {
            this.setState(CONNECTED_STATE);
        } else {
            this.setState(DISCONNECTED_STATE);
        }
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "Port.setPortStatus(int, int) [" + this.toString() + ", " + this.getName() + "], " + this.status);
        }
    }

    public int getRdmStatAvailable() {
        return this.rdmStatAvailable;
    }

    public String getStatusToolTip() {
        return this.statusToolTip;
    }

    public String getAvailabilityToolTip() {
        return this.availabilityToolTip;
    }

    public void setToolTipText() {
        this.statusToolTip = this.isConnected() ? "Connected" : "Not Connected";
        if (this.status == 1) {
            this.availabilityToolTip = "Available";
        }
        if (this.status == 0) {
            this.availabilityToolTip = "Unavailable";
        }
        if (this.status == 2) {
            this.availabilityToolTip = "Busy";
        }
    }

    public int getStatus() {
        return this.status;
    }

    public void setPortStatus(String string) {
        int n = Integer.parseInt(string);
        if (n >= 0 && n <= 2) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "Port.setPortStatus(String) [" + this.toString() + ", " + this.getName() + "], " + string);
            }
            this.status = n;
            this.setToolTipText();
            this.setPortStatus(n);
        }
    }

    public void setPortStatus(int n) {
        if (n >= 0 && n <= 2) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "Port.setPortStatus(int) [" + this.toString() + ", " + this.getName() + "], " + n);
            }
            this.status = n;
            this.setToolTipText();
            if (n == 0) {
                this.setState(UNAVAILABLE_STATE);
            } else if (this.isConnected()) {
                this.setState(CONNECTED_STATE);
            } else {
                this.setState(DISCONNECTED_STATE);
            }
        }
    }

    public BladeChassis getBladeChassisByTargetDeviceId(String string) {
        Map map = this.getChildren();
        if (map != null) {
            Iterator iterator = map.keySet().iterator();
            Object var4_4 = null;
            while (iterator.hasNext()) {
                BladeChassis bladeChassis;
                Object object = iterator.next();
                if (object == null || (object = map.get(object)) == null || !(object instanceof BladeChassis) || (bladeChassis = (BladeChassis)object) == null || !bladeChassis.getId().equals(string)) continue;
                return bladeChassis;
            }
        }
        return null;
    }

    public Port getPortByTargetDeviceId(String string) {
        Map map = this.getChildren();
        if (map != null) {
            Iterator iterator = map.keySet().iterator();
            Port port = null;
            while (iterator.hasNext()) {
                Device device;
                Object object = iterator.next();
                if (object == null || (object = map.get(object)) == null || !(object instanceof Port ? (port = (Port)object) != null && port.getTargetDeviceId().equals(string) : (object instanceof Paragon ? (port = (device = (Paragon)object).getPortByTargetDeviceId(string)) != null : (object instanceof BladeChassis || object instanceof VirtualBladeChassis) && (port = (device = (BladeChassis)object).getPortByTargetDeviceId(string)) != null))) continue;
                return port;
            }
        }
        return null;
    }

    public String getBladeChassisForDeviceId(String string) {
        Map map = this.getChildren();
        if (map != null) {
            Iterator iterator = map.keySet().iterator();
            BladeChassis bladeChassis = null;
            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (object == null || (object = map.get(object)) == null || !(object instanceof BladeChassis) || (bladeChassis = (BladeChassis)object) == null || !bladeChassis.getConnectionId().equals(string)) continue;
                return bladeChassis.getId();
            }
        }
        return null;
    }

    public String getIP() {
        String string;
        block9: {
            block8: {
                string = null;
                if (!this.isProfiled() || this.isModemProfiled()) break block8;
                switch (this.getDevPrefs().getFindBy()) {
                    case 2: {
                        try {
                            string = new String(InetAddress.getByName(this.getDevPrefs().getDnsName()).getHostAddress());
                        }
                        catch (UnknownHostException unknownHostException) {
                            string = null;
                            RRCLogger.log(300, 4, "Unknown Host Exception when connecting to " + this.getDevPrefs().getDnsName());
                        }
                        break;
                    }
                    case 1: {
                        string = this.getIPAddress();
                        if ("".equals(string)) {
                            string = null;
                            RRCLogger.log(300, 4, "Unable to get IP for device named " + this.getDevPrefs().getName());
                            break;
                        }
                        break block9;
                    }
                    case 0: {
                        string = new String(StringUtils.formatString(this.getDevPrefs().getIp()));
                        break;
                    }
                    default: {
                        RRCLogger.log(100, 4, "Unable to determine IP Address of device: " + this.getName());
                        break;
                    }
                }
                break block9;
            }
            string = this.getIPAddress();
        }
        return string;
    }

    public String getNameIP() {
        String string = this.getIP();
        if (string == null) {
            if (this.bundle == null) {
                this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
            }
            string = this.bundle.getString("error.unknownHostname");
        }
        return (StringUtils.formatString(this.name) + " " + string).trim();
    }

    public boolean isCancelLogin() {
        return this.cancelLogin;
    }

    public void setCancelLogin(boolean bl) {
        this.cancelLogin = bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sort(int n) {
        sortType = n;
        this.comparator.setSortType(n);
        Map map = this.getChildren();
        if (map != null) {
            Map map2 = map;
            synchronized (map2) {
                if (map.entrySet() != null) {
                    ArrayList arrayList = new ArrayList(map.entrySet());
                    Collections.sort(arrayList, this.comparator);
                    Iterator iterator = arrayList.iterator();
                    LinkedHashMap<String, Device> linkedHashMap = new LinkedHashMap<String, Device>();
                    int n2 = 0;
                    while (iterator.hasNext()) {
                        ++n2;
                        Device device = (Device)((Map.Entry)iterator.next()).getValue();
                        String string = "";
                        if (device instanceof Port) {
                            string = ((Port)device).getTargetDeviceId();
                        } else if (device instanceof BladeChassis || device instanceof VirtualBladeChassis) {
                            device.sort(this.getSortType());
                        } else {
                            string = Integer.toString(n2);
                        }
                        linkedHashMap.put((device.getName() + string).toLowerCase(), device);
                    }
                    this.addChildren(linkedHashMap);
                    linkedHashMap.clear();
                    linkedHashMap = null;
                }
            }
        }
    }

    public DeviceHandlerInterface getHandler() {
        return this.handler;
    }

    public void setHandler(DeviceHandlerInterface deviceHandlerInterface) {
        this.handler = deviceHandlerInterface;
    }

    public String getRdmSessionId() {
        return this.rdmSessionId;
    }

    public void setRdmSessionId(String string) {
        this.rdmSessionId = string;
    }

    public String getRdmSessionKey() {
        return this.rdmSessionKey;
    }

    public void setRdmSessionKey(String string) {
        this.rdmSessionKey = string;
    }

    public Map<String, String> getAppletParameters() {
        return this.appletParameters;
    }

    public void setAppletParameters(Map<String, String> map) {
        this.appletParameters = map;
    }

    public DeviceView getDeviceView() {
        return this.deviceView;
    }

    public void setDeviceView(DeviceView deviceView) {
        this.deviceView = deviceView;
    }

    public File getRscFileHandle() {
        return this.rscFileHandle;
    }

    public void setRscFileHandle(File file) {
        this.rscFileHandle = file;
    }

    public void setAddressList(List list) {
        this.addressList.clear();
        this.addressList.addAll(list);
    }

    public List getAddressList() {
        return new ArrayList(this.addressList);
    }

    public String getDnsName() {
        return this.dnsName;
    }

    public void setDnsName(String string) {
        this.dnsName = string;
    }

    public static String getNameIP(Device device, String string) {
        String string2 = device.getName();
        String string3 = "";
        if (device.isProfiled()) {
            DevicePreferences devicePreferences = device.getDevPrefs();
            block0 : switch (devicePreferences.getConnectionType()) {
                case 2: {
                    switch (devicePreferences.getFindBy()) {
                        case 0: {
                            string3 = devicePreferences.getIp();
                            break block0;
                        }
                        case 1: {
                            string2 = devicePreferences.getName();
                            string3 = Device.getIP(device);
                            break block0;
                        }
                        case 2: {
                            string3 = devicePreferences.getDnsName();
                        }
                    }
                    break;
                }
                case 1: {
                    string2 = devicePreferences.getPhone();
                    string3 = Device.getIP(device);
                }
            }
        } else {
            string3 = Device.getIP(device);
        }
        if (string2 == null || "".equals(string2)) {
            string2 = string;
        }
        return string2 + " " + string3;
    }

    private static String getIP(Device device) {
        List list = device.getAddressList();
        if (list.size() > 0) {
            return ((InetAddress)list.get(0)).getHostAddress();
        }
        return "";
    }

    private static String getActiveNameIP(Device device, String string) {
        DeviceConnector deviceConnector = device.getDeviceConnector();
        InetAddress inetAddress = null;
        if (deviceConnector != null && (inetAddress = deviceConnector.getInetAddress()) != null) {
            String string2 = device.getName();
            if (device.isProfiled()) {
                DevicePreferences devicePreferences = device.getDevPrefs();
                switch (devicePreferences.getConnectionType()) {
                    case 2: {
                        switch (devicePreferences.getFindBy()) {
                            case 1: {
                                string2 = devicePreferences.getName();
                            }
                        }
                        break;
                    }
                    case 1: {
                        string2 = devicePreferences.getPhone();
                    }
                }
            }
            if (string2 == null || "".equals(string2)) {
                string2 = string;
            }
            return string2 + " " + inetAddress.getHostAddress();
        }
        return Device.getNameIP(device, string);
    }

    public abstract String getPortKey();

    public void setUniquePortId(String string) {
        this.uniquePortId = string;
    }

    public String getUniquePortId() {
        return this.uniquePortId;
    }

    public void showGroupView(boolean bl) {
        Map map = this.getChildren();
        Vector vector = new Vector();
        Vector vector2 = new Vector();
        if (bl) {
            if (this.portsWithGroupsMap != null && this.portsWithGroupsMap.size() > 0) {
                for (VirtualBladeChassis virtualBladeChassis : this.portsWithGroupsMap.keySet()) {
                    VirtualBladeChassis virtualBladeChassis2 = virtualBladeChassis;
                    if (virtualBladeChassis2.getBaseDevice() != this) continue;
                    if (virtualBladeChassis != null) {
                        Vector<String> vector3 = this.portsWithGroupsMap.get(virtualBladeChassis);
                        for (int i = 0; i < vector3.size(); ++i) {
                            String string = vector3.get(i);
                            Port port = this.getPortByTargetDeviceId(string);
                            this.remove(port, port.getName() + port.getTargetDeviceId());
                            virtualBladeChassis2.add(port, port.getName() + port.getTargetDeviceId());
                        }
                    }
                    this.add(virtualBladeChassis2, virtualBladeChassis2.getName() + virtualBladeChassis2.getId());
                    this.firePropertyChange(DEVICE_PORTS_ADDED, null, null);
                }
            }
        } else {
            this.showWithoutGroups();
        }
        DeviceTreeController.getInstance().sort(this.getSortType());
    }

    public void showWithoutGroups() {
        Map map = this.getChildren();
        Vector<Device> vector = new Vector<Device>();
        Vector<Port> vector2 = new Vector<Port>();
        if (map != null) {
            int n;
            Device device;
            for (Object object : map.keySet()) {
                Map map2;
                if (object == null || (object = map.get(object)) == null || !(object instanceof VirtualBladeChassis) || (map2 = (device = (VirtualBladeChassis)object).getChildren()) == null || map2.size() <= 0) continue;
                for (Object k : map2.keySet()) {
                    Object v;
                    if (k == null || (v = map2.get(k.toString())) == null || !(v instanceof Port)) continue;
                    Port port = (Port)v;
                    vector2.add(port);
                }
                if (device.hasChildren()) {
                    device.removeChildren();
                }
                vector.add(device);
            }
            for (n = 0; n < vector2.size(); ++n) {
                device = (Port)vector2.get(n);
                this.add(device, device.getName() + ((Port)device).getTargetDeviceId());
            }
            for (n = 0; n < vector.size(); ++n) {
                device = (VirtualBladeChassis)vector.get(n);
                this.remove(device, device.getName());
            }
            this.firePropertyChange(DEVICE_PORTS_REMOVE, null, null);
        }
    }

    public Map getPortsWithGroupMap() {
        return this.portsWithGroupsMap;
    }

    public void setVbcWithPortsMap(LinkedHashMap<VirtualBladeChassis, Vector<String>> linkedHashMap) {
        this.portsWithGroupsMap = linkedHashMap;
    }

    public void setMultiMonitorPorts(List<MultiMonitorPort> list) {
        this.multiMonitorHandler = new MultiMonitorPortHandler(list);
    }

    public void setMultiMonitorPorts(MultiMonitorPort multiMonitorPort) {
        this.multiMonitorHandler = new MultiMonitorPortHandler(multiMonitorPort);
    }

    public MultiMonitorPortHandler getMultiMonitorPortHandler() {
        return this.multiMonitorHandler;
    }

    public boolean isVirtual() {
        return this.virtual;
    }

    public void setVirtual(Boolean bl) {
        this.virtual = bl;
    }

    public void setRSAEnabled(boolean bl) {
        this.rsaEnabled = bl;
    }

    public boolean isRSAEnabled() {
        return this.rsaEnabled;
    }

    public void setRSAAcceptance(boolean bl) {
        this.requireRSAAcceptance = bl;
    }

    public boolean isRSAAcceptance() {
        return this.requireRSAAcceptance;
    }

    public void setRSAAccepted(boolean bl) {
        this.rsaAccepted = bl;
    }

    public boolean isRSAAccepted() {
        return this.rsaAccepted;
    }

    public boolean isScanSupported() {
        return this.scanSupported;
    }

    public void setScanSupported(boolean bl) {
        this.scanSupported = bl;
    }

    public void setScreenContext(ScreenContext screenContext) {
        this.scrContext = screenContext;
    }
}

