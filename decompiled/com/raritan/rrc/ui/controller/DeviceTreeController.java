/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.controller;

import com.raritan.protocol.browser.TooManyDevicesException;
import com.raritan.protocol.browser.spi.Browser;
import com.raritan.protocol.browser.spi.DeviceNameAddressProvImpl;
import com.raritan.protocol.csc.RRCDeviceInfo;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoSortCommand;
import com.raritan.rrc.ui.models.DeviceInfoWrapper;
import com.raritan.rrc.ui.models.DeviceListingModel;
import com.raritan.rrc.ui.models.DeviceListingModelListener;
import com.raritan.rrc.ui.panes.mediator.MainScreenMediator;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.ui.components.CommandCheckMenuItem;
import com.raritan.tools.ui.components.MultyObserverComponentInterface;
import com.raritan.tools.ui.panes.BaseTreePanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.SwingUtilities;
import nn.pp.ext.pref.IApplicationPreferences;

public class DeviceTreeController
implements PropertyChangeListener,
MultyObserverComponentInterface {
    private static DeviceTreeController instance;
    private Browser deviceBrowser;
    private RRCScreenContext scrContext;
    private HashMap observables = new HashMap();
    private IApplicationPreferences appPrefs;
    private BaseTreePanel deviceByIPTreePanel = null;
    public static int SORT_TYPE_CHANNEL;
    public static int SORT_TYPE_NAME;
    public static int SORT_TYPE_STATUS;
    private volatile int sortType = SORT_TYPE_CHANNEL;
    private DoSortCommand sortCommand;
    private volatile boolean stopping = false;
    private Hashtable deviceListingMap = new Hashtable();
    private static Comparator ipAddrComparator;
    private DeviceListingModel deviceListingModel;
    private static Object objMutex;
    private static Object mutex;

    private DeviceTreeController(RRCScreenContext rRCScreenContext) {
        this();
        this.setScreenContext(rRCScreenContext);
        this.appPrefs = this.scrContext.getAppSettings();
        MainScreenMediator mainScreenMediator = this.scrContext.getMainScreenMediator();
        this.deviceByIPTreePanel = mainScreenMediator.getDeviceByIPTree();
        this.deviceBrowser = new Browser();
        this.deviceBrowser.setQueryInterval(30);
        this.deviceBrowser.setDefaultDiscoveryPort(this.appPrefs.getBroadcastPort());
        if (this.appPrefs.getShowAll()) {
            this.deviceBrowser.setBroadcastEnabled();
        }
        this.sortCommand = new DoSortCommand(rRCScreenContext);
        this.deviceListingModel = new DeviceListingModel(this.deviceBrowser);
        this.deviceListingModel.addDeviceListingModelListener(new DevListingModelListenerImpl());
        this.deviceListingModel.init();
        rRCScreenContext.setDeviceNameAddressProvider(new DeviceNameAddressProvImpl(this.deviceListingModel));
        this.startBrowser();
    }

    private DeviceTreeController() {
        this.observables = new HashMap();
    }

    private void init() {
        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                DeviceTreeController.this.loadProfiledDevices();
            }
        });
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DeviceTreeController getInstance() {
        Object object = objMutex;
        synchronized (object) {
            if (instance == null) {
                instance = new DeviceTreeController();
            }
        }
        return instance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DeviceTreeController getInstance(RRCScreenContext rRCScreenContext) {
        Object object = mutex;
        synchronized (object) {
            if (instance == null) {
                instance = new DeviceTreeController(rRCScreenContext);
                instance.init();
            }
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    public void setScreenContext(RRCScreenContext rRCScreenContext) {
        this.scrContext = rRCScreenContext;
        if (this.scrContext.getSelectedDevicesObservable() != null) {
            this.addObservable(this.scrContext.getSelectedDevicesObservable());
        }
    }

    private void prepareDevice(IPReach iPReach) {
        iPReach.addPropertyChangeListener(this);
        iPReach.setRootDevice(true);
    }

    public synchronized void removeDevice(IPReach iPReach) {
        DevicePreferences devicePreferences = iPReach.getDevPrefs();
        int n = devicePreferences.getConnectionType();
        if (n == 1) {
            this.deviceListingModel.removePhoneProfiledEntry(devicePreferences.getPhone());
        } else if (n == 2) {
            int n2 = devicePreferences.getFindBy();
            switch (n2) {
                case 0: {
                    this.deviceListingModel.removeIPProfiledEntry(devicePreferences.getInetAddess(), devicePreferences.getPort());
                    break;
                }
                case 1: {
                    this.deviceListingModel.removeNameProfiledEntry(devicePreferences.getName());
                    break;
                }
                case 2: {
                    this.deviceListingModel.removeDNSProfiledEntry(devicePreferences.getDnsName(), devicePreferences.getPort());
                }
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    private void loadProfiledDevices() {
        block17: {
            if (this.scrContext.getApplicationProperty("connection") != null) break block17;
            var1_1 = DevicePreferences.returnNodes();
            var2_2 = null;
            var3_3 = null;
lbl5:
            // 6 sources

            block15: for (var4_4 = 0; var4_4 < var1_1.length; ++var4_4) {
                var2_2 = new DevicePreferences();
                var3_3 = new IPReach();
                var2_2.importPreferences(var1_1[var4_4]);
                if (!StringUtils.notNullOrEmpty(var2_2.getDescription())) continue;
                var3_3.setDevPrefs(var2_2);
                var3_3.setDescription(var2_2.getDescription());
                var3_3.setState("UNAVAILABLE");
                var5_5 = var2_2.getConnectionType();
                block3 : switch (var5_5) {
                    case 1: {
                        this.createPhoneProfiledEntry(var2_2.getPhone(), var3_3);
                        continue block15;
                    }
                    case 2: {
                        switch (var2_2.getFindBy()) {
                            case 0: {
                                var6_6 = null;
                                try {
                                    var6_6 = InetAddress.getByName(var2_2.getIp());
                                }
                                catch (UnknownHostException var7_8) {
                                    if (!DeviceTreeController.$assertionsDisabled) {
                                        throw new AssertionError((Object)"profiled entries have illegal values for IP field");
                                    }
                                    var7_8.printStackTrace();
                                }
                                this.createIPProfiledEntry(var6_6, var2_2.getPort(), var3_3);
                                break block3;
                            }
                            case 1: {
                                try {
                                    this.createNameProfiledEntry(var2_2.getName(), var3_3, true);
                                }
                                catch (TooManyDevicesException var7_9) {}
                                ** GOTO lbl5
                            }
                            case 2: {
                                try {
                                    this.createDNSProfiledEntry(var2_2.getDnsName(), var2_2.getPort(), var3_3, true);
                                    break block3;
                                }
                                catch (TooManyDevicesException var7_10) {
                                    // empty catch block
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void refreshTree() {
        this.deviceBrowser.setDefaultDiscoveryPort(this.appPrefs.getBroadcastPort());
        this.deviceBrowser.refreshDevices(this.getDevicesNotToBeUpdated());
    }

    public void stopBroadcastAndUpdateDevices() {
        this.deviceBrowser.stopBroadcastAndUpdateDevices(this.getDevicesNotToBeUpdated());
    }

    public void startBroadcast() {
        this.deviceBrowser.setBroadcastEnabled();
    }

    private Set getDevicesNotToBeUpdated() {
        Map map = (Map)this.deviceListingMap.clone();
        Iterator iterator = map.values().iterator();
        LinkedHashSet<RRCDeviceInfo> linkedHashSet = new LinkedHashSet<RRCDeviceInfo>();
        while (iterator.hasNext()) {
            DeviceDevInfoHolder deviceDevInfoHolder = (DeviceDevInfoHolder)iterator.next();
            if (!deviceDevInfoHolder.device.isConnected()) continue;
            Iterator iterator2 = deviceDevInfoHolder.devInfos.iterator();
            while (iterator2.hasNext()) {
                linkedHashSet.add(((DeviceInfoWrapper)iterator2.next()).getDeviceInfo());
            }
        }
        return linkedHashSet;
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (!this.stopping) {
            String string = propertyChangeEvent.getPropertyName();
            boolean bl = false;
            boolean bl2 = false;
            Device device = (Device)propertyChangeEvent.getSource();
            if (this.deviceByIPTreePanel != null) {
                if ("DEVICE_PORTS_ADD".equals(string)) {
                    this.deviceByIPTreePanel.invokePopulatePorts(device);
                } else if ("DEVICE_CONNECTION_LOST".equals(string)) {
                    this.deviceByIPTreePanel.removePorts(device);
                    bl2 = true;
                } else if ("DEVICE_PORT_VIEW_ADD".equals(string) || "DEVICE_PORT_VIEW_REMOVE".equals(string)) {
                    bl2 = this.sortDeviceView(device);
                } else if ("DEVICE_STATE_CHANGED".equals(string)) {
                    if (!(device instanceof Port)) {
                        Object object = propertyChangeEvent.getOldValue();
                        Object object2 = propertyChangeEvent.getNewValue();
                        if ("AVAILABLE".equals(object) && "CONNECTED".equals(object2) || "CONNECTED".equals(object) && "AVAILABLE".equals(object2)) {
                            this.deviceByIPTreePanel.setDeviceNodeChanged(device);
                            bl2 = true;
                        }
                        if ("AVAILABLE".equals(object2) && "CONNECTED".equals(object)) {
                            this.updateDeviceOnDisconnect(device);
                        }
                    } else if (device instanceof Port) {
                        Object object = propertyChangeEvent.getOldValue();
                        Object object3 = propertyChangeEvent.getNewValue();
                        if ("AVAILABLE".equals(object) && "UNAVAILABLE".equals(object3) || "UNAVAILABLE".equals(object) && "AVAILABLE".equals(object3) || "UNAVAILABLE".equals(object) && "CONNECTED".equals(object3) || "CONNECTED".equals(object) && "AVAILABLE".equals(object3)) {
                            bl2 = this.sortDeviceView(device);
                        }
                    }
                    bl = true;
                } else if ("DEVICE_ACTIVATED".equals(string)) {
                    if (this.sortType == SORT_TYPE_STATUS && device instanceof Port && ((Port)device).getDeviceClass().equalsIgnoreCase("KVM") && device.getState().equalsIgnoreCase("CONNECTED") && ((Port)device).isActive()) {
                        this.sortCommand.getContext(true).setCommandParameter("sortMode", ((Port)device).getDevice());
                        this.sortCommand.execute();
                        this.deviceByIPTreePanel.invokePopulatePorts(((Port)device).getDevice());
                    }
                } else if ("DEVICE_SELECTED".equals(string)) {
                    this.deviceByIPTreePanel.setSelectedDevice(device);
                } else if ("DEVICE_DESELECTED".equals(string)) {
                    this.deviceByIPTreePanel.deselectTree();
                } else if ("DEVICE_NAME_CHANGED".equals(string)) {
                    if (device instanceof Port) {
                        this.sortDeviceView(device);
                    }
                } else {
                    bl = true;
                }
                if (bl) {
                    SwingUtilities.invokeLater(new Runnable(){

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        @Override
                        public void run() {
                            DeviceTreeController deviceTreeController = DeviceTreeController.getInstance(DeviceTreeController.this.scrContext);
                            synchronized (deviceTreeController) {
                                DeviceTreeController.this.deviceByIPTreePanel.repaint();
                            }
                        }
                    });
                }
                if (bl2) {
                    SwingUtilities.invokeLater(new Runnable(){

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        @Override
                        public void run() {
                            DeviceTreeController deviceTreeController = DeviceTreeController.getInstance(DeviceTreeController.this.scrContext);
                            synchronized (deviceTreeController) {
                                DeviceTreeController.this.deviceByIPTreePanel.refreshTree();
                            }
                        }
                    });
                }
            }
        }
    }

    private boolean sortDeviceView(Device device) {
        if ((this.sortType == SORT_TYPE_STATUS || this.sortType == SORT_TYPE_NAME) && device instanceof Port && ((Port)device).getDeviceClass().equalsIgnoreCase("KVM")) {
            this.sortCommand.getContext(true).setCommandParameter("sortMode", ((Port)device).getDevice());
            this.sortCommand.execute();
            this.deviceByIPTreePanel.invokePopulatePorts(((Port)device).getDevice());
            return false;
        }
        return true;
    }

    @Override
    public List getObservables() {
        return new ArrayList(this.observables.values());
    }

    @Override
    public void clearObservables() {
        if (this.observables != null && this.observables.size() > 0) {
            Iterator iterator = this.observables.values().iterator();
            Observable observable = null;
            while (iterator.hasNext()) {
                observable = (Observable)iterator.next();
                observable.deleteObserver(this);
            }
            this.observables.clear();
        }
    }

    @Override
    public void addObservable(Observable observable) {
        this.observables.put(this.hashCode() + "", this);
        observable.addObserver(this);
    }

    @Override
    public void removeObservable(Observable observable) {
        this.observables.remove(this.hashCode() + "");
        observable.deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object object) {
        ArrayList arrayList;
        if (this.scrContext != null && this.deviceByIPTreePanel != null && (arrayList = (ArrayList)this.scrContext.getSelectedDevicesObservable().getComponent()) != null && arrayList.size() > 0) {
            final Device device = (Device)arrayList.get(0);
            if (SwingUtilities.isEventDispatchThread()) {
                this.deviceByIPTreePanel.setSelectedDevice(device);
            } else {
                SwingUtilities.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        DeviceTreeController.this.deviceByIPTreePanel.setSelectedDevice(device);
                    }
                });
            }
        }
    }

    public void populateParagonPorts(Paragon paragon) {
        this.deviceByIPTreePanel.invokePopulatePorts(paragon);
    }

    public Browser getDeviceBrowser() {
        return this.deviceBrowser;
    }

    public void stop() {
        this.stopping = true;
        Map map = (Map)this.deviceListingMap.clone();
        for (DeviceDevInfoHolder deviceDevInfoHolder : map.values()) {
            if (!deviceDevInfoHolder.device.isConnected()) continue;
            deviceDevInfoHolder.device.disconnect();
        }
    }

    public void startBrowser() {
        if (this.scrContext.getApplicationProperty("connection") == null) {
            this.deviceBrowser.setScreenContext(this.scrContext);
            this.deviceBrowser.start();
        }
    }

    public synchronized void showWithoutTargets(boolean bl) {
        CommandCheckMenuItem commandCheckMenuItem = this.scrContext.getMainScreenMediator().getCheckShowWithoutTargetsMenuItems().get(0);
        boolean bl2 = commandCheckMenuItem.isSelected();
        this.deviceByIPTreePanel.setShowWithoutTargets(bl2);
    }

    public synchronized void showWithoutGroups(boolean bl) {
        this.deviceByIPTreePanel.doGroup(bl);
    }

    public synchronized void showPowerstrips(boolean bl) {
        CommandCheckMenuItem commandCheckMenuItem = this.scrContext.getMainScreenMediator().getCheckShowPowerstripsMenuItems().get(0);
        boolean bl2 = commandCheckMenuItem.isSelected();
        this.deviceByIPTreePanel.setShowPowerstrips(bl2);
    }

    public synchronized void showTools(boolean bl) {
        CommandCheckMenuItem commandCheckMenuItem = this.scrContext.getMainScreenMediator().getCheckShowToolsMenuItems().get(0);
        boolean bl2 = commandCheckMenuItem.isSelected();
        this.deviceByIPTreePanel.setShowTools(bl2);
    }

    public void sort(int n) {
        this.sortType = n;
        this.deviceByIPTreePanel.doSort(this.sortType);
    }

    public void setSortType(int n) {
        this.sortType = n;
    }

    public void sort(Device device) {
        if (device.isConnected() && device.hasChildren()) {
            device.sort(this.sortType);
        }
    }

    public void showBold() {
        this.deviceByIPTreePanel.selNodeChanged();
    }

    public void treeNodeChanged(Device device) {
        this.deviceByIPTreePanel.setDeviceNodeChanged(device);
    }

    public boolean isProfileByPhoneExists(String string) {
        return this.deviceListingModel.isProfileByPhoneExists(string);
    }

    public boolean isProfileByIPExists(InetAddress inetAddress) {
        return this.deviceListingModel.isProfileByIPExists(inetAddress);
    }

    public boolean isProfileByNameExists(String string) {
        return this.deviceListingModel.isProfileByNameExists(string);
    }

    public boolean isProfileByDNSExists(String string) {
        return this.deviceListingModel.isProfileByDNSExists(string);
    }

    public void createPhoneProfiledEntry(String string, IPReach iPReach) {
        iPReach.setProfiled(true);
        iPReach.setModemProfiled(true);
        this.deviceListingModel.addPhoneProfiledEntry(string, iPReach);
    }

    public void createIPProfiledEntry(InetAddress inetAddress, int n, IPReach iPReach) {
        iPReach.setProfiled(true);
        iPReach.setModemProfiled(false);
        this.deviceListingModel.addIPProfiledEntry(inetAddress, n, iPReach);
    }

    public void createNameProfiledEntry(String string, IPReach iPReach, boolean bl) throws TooManyDevicesException {
        iPReach.setProfiled(true);
        iPReach.setModemProfiled(false);
        this.deviceListingModel.addNameProfiledEntry(string, iPReach, bl);
    }

    public void createDNSProfiledEntry(String string, int n, IPReach iPReach, boolean bl) throws TooManyDevicesException {
        iPReach.setProfiled(true);
        iPReach.setModemProfiled(false);
        this.deviceListingModel.addDNSProfiledEntry(string, n, iPReach, bl);
    }

    private void updateDeviceOnDisconnect(final Device device) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                Map map = (Map)DeviceTreeController.this.deviceListingMap.clone();
                Iterator iterator = map.entrySet().iterator();
                String string = null;
                DeviceDevInfoHolder deviceDevInfoHolder = null;
                while (iterator.hasNext()) {
                    Map.Entry entry = iterator.next();
                    if (((DeviceDevInfoHolder)entry.getValue()).device != device) continue;
                    string = (String)entry.getKey();
                    deviceDevInfoHolder = (DeviceDevInfoHolder)entry.getValue();
                    break;
                }
                if (string != null) {
                    assert (deviceDevInfoHolder != null);
                    if (DeviceTreeController.this.deviceByIPTreePanel.deleteDeviceIfAvailable(string)) {
                        DeviceTreeController.populateDevice(deviceDevInfoHolder.device, deviceDevInfoHolder.devInfos);
                        DeviceTreeController.setPreferencesOnNonProfiledDevices(deviceDevInfoHolder.device, deviceDevInfoHolder.devInfos);
                        DeviceTreeController.this.deviceByIPTreePanel.addDevice(string, deviceDevInfoHolder.device);
                    }
                }
            }
        });
    }

    private static void setPreferencesOnNonProfiledDevices(Device device, Set<DeviceInfoWrapper> set) {
        if (!device.isProfiled()) {
            Set<DeviceInfoWrapper> set2 = set;
            if (set.size() > 1) {
                set2 = new TreeSet<DeviceInfoWrapper>(ipAddrComparator);
                set2.addAll(set);
            }
            device.setDevPrefs(null);
            Iterator<DeviceInfoWrapper> iterator = set2.iterator();
            while (iterator.hasNext()) {
                RRCDeviceInfo rRCDeviceInfo = iterator.next().getDeviceInfo();
                DevicePreferences devicePreferences = DevicePreferences.getNode(rRCDeviceInfo.getHost());
                if (devicePreferences == null) continue;
                device.setDevPrefs(devicePreferences);
                break;
            }
        }
    }

    private static void populateDevice(Device device, Set treeSet) {
        Iterator iterator;
        Object object;
        Set set = treeSet;
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        if (treeSet.size() > 1) {
            set = new TreeSet(ipAddrComparator);
            set.addAll(treeSet);
        }
        if (device.isProfiled()) {
            object = device.getDevPrefs();
            iterator = set.iterator();
            while (iterator.hasNext()) {
                RRCDeviceInfo rRCDeviceInfo = ((DeviceInfoWrapper)iterator.next()).getDeviceInfo();
                if (rRCDeviceInfo.getPort() == ((DevicePreferences)object).getPort()) continue;
                set = Collections.EMPTY_SET;
                break;
            }
        }
        device.setName("");
        device.setDnsName(null);
        object = null;
        iterator = set.iterator();
        boolean bl = true;
        while (iterator.hasNext()) {
            RRCDeviceInfo rRCDeviceInfo = ((DeviceInfoWrapper)iterator.next()).getDeviceInfo();
            if (bl) {
                device.setName(rRCDeviceInfo.getName());
                device.setIPPort(rRCDeviceInfo.getPort());
                object = rRCDeviceInfo.getDnsName();
                bl = false;
            } else if (object != null && !((String)object).equals(rRCDeviceInfo.getDnsName())) {
                object = null;
            }
            arrayList.add(rRCDeviceInfo.getInetAddress());
        }
        device.setAddressList(arrayList);
        device.setDnsName((String)object);
        if (set.size() > 0) {
            device.setState("AVAILABLE");
        } else {
            device.setState("UNAVAILABLE");
        }
    }

    static {
        SORT_TYPE_CHANNEL = 0;
        SORT_TYPE_NAME = 1;
        SORT_TYPE_STATUS = 2;
        ipAddrComparator = new IPAddrComparator();
        objMutex = new Object();
        mutex = new Object();
    }

    private static class DeviceDevInfoHolder {
        private final Device device;
        private final Set devInfos;

        public DeviceDevInfoHolder(Device device, Set set) {
            this.device = device;
            this.devInfos = set;
        }
    }

    private static class IPAddrComparator
    implements Comparator {
        private IPAddrComparator() {
        }

        public int compare(Object object, Object object2) {
            if (((DeviceInfoWrapper)object).getDeviceInfo().getInetAddress() instanceof Inet6Address) {
                return -1;
            }
            return 1;
        }
    }

    private class DevListingModelListenerImpl
    implements DeviceListingModelListener {
        private DevListingModelListenerImpl() {
        }

        @Override
        public void createEntry(final String string, Set set) {
            Iterator iterator = set.iterator();
            if (iterator.hasNext()) {
                final IPReach iPReach = new IPReach();
                DeviceTreeController.populateDevice(iPReach, set);
                iPReach.setContext(DeviceTreeController.this.scrContext);
                DeviceTreeController.setPreferencesOnNonProfiledDevices(iPReach, set);
                DeviceTreeController.this.deviceListingMap.put(string, new DeviceDevInfoHolder(iPReach, set));
                DeviceTreeController.this.prepareDevice(iPReach);
                SwingUtilities.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        DeviceTreeController.this.deviceByIPTreePanel.addDevice(string, iPReach);
                    }
                });
            }
        }

        @Override
        public void createProfiledEntry(final String string, Set set, Object object) {
            final IPReach iPReach = (IPReach)object;
            DeviceTreeController.populateDevice(iPReach, set);
            iPReach.setContext(DeviceTreeController.this.scrContext);
            DeviceTreeController.this.deviceListingMap.put(string, new DeviceDevInfoHolder(iPReach, set));
            DeviceTreeController.this.prepareDevice(iPReach);
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    DeviceTreeController.this.deviceByIPTreePanel.addDevice(string, iPReach);
                }
            });
        }

        @Override
        public void deleteEntry(final String string) {
            final DeviceDevInfoHolder deviceDevInfoHolder = (DeviceDevInfoHolder)DeviceTreeController.this.deviceListingMap.remove(string);
            assert (deviceDevInfoHolder != null);
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (!deviceDevInfoHolder.device.isProfiled() && deviceDevInfoHolder.device.isConnected()) {
                        deviceDevInfoHolder.device.disconnect();
                    }
                    DeviceTreeController.this.deviceByIPTreePanel.deleteDevice(string);
                }
            });
        }

        @Override
        public void updateEntry(final String string, final Set set) {
            final DeviceDevInfoHolder deviceDevInfoHolder = (DeviceDevInfoHolder)DeviceTreeController.this.deviceListingMap.get(string);
            if (deviceDevInfoHolder.device.isConnected()) {
                DeviceTreeController.this.deviceListingMap.put(string, new DeviceDevInfoHolder(deviceDevInfoHolder.device, set));
                return;
            }
            Object v = DeviceTreeController.this.deviceListingMap.remove(string);
            assert (v != null);
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    DeviceTreeController.this.deviceByIPTreePanel.deleteDevice(string);
                    DeviceTreeController.populateDevice(deviceDevInfoHolder.device, set);
                    DeviceTreeController.setPreferencesOnNonProfiledDevices(deviceDevInfoHolder.device, set);
                }
            });
            DeviceTreeController.this.deviceListingMap.put(string, new DeviceDevInfoHolder(deviceDevInfoHolder.device, set));
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    DeviceTreeController.this.deviceByIPTreePanel.addDevice(string, deviceDevInfoHolder.device);
                }
            });
        }
    }
}

