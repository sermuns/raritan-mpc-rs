/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui;

import com.raritan.protocol.browser.DeviceNameAddressProvider;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.VirtualMediaBean;
import com.raritan.rrc.data.VirtualMediaLocalBean;
import com.raritan.rrc.ui.MPCScanFrame;
import com.raritan.rrc.ui.RRCApplication;
import com.raritan.rrc.ui.commands.DoRunKeyboardMacroCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaImagePanelCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaLocalPanelCommand;
import com.raritan.rrc.ui.components.RRCMenuBar;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.KvmMenuPopupKey;
import com.raritan.rrc.ui.panes.RRCShellInternalFrame;
import com.raritan.rrc.ui.panes.mediator.MainScreenMediator;
import com.raritan.smartcard.SmartCardCore;
import com.raritan.smartcard.SmartCardCoreFactory;
import com.raritan.smartcard.SmartCardInitException;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.State;
import com.raritan.tools.ui.components.CommandMenu;
import com.raritan.tools.ui.components.CommandMenuItem;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.util.ObservableContainer;
import com.raritan.tools.util.config.ConfigurableException;
import com.raritan.tools.util.config.ConfigurationManager;
import com.util.kbd.KeyboardUtil;
import java.awt.Component;
import java.awt.Image;
import java.beans.PropertyVetoException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javaclientlib.utils.RRCGeneralException;
import javaclientlib.utils.RRCLogger;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import nn.pp.common.help.HelpManager;
import nn.pp.core.Platform;
import nn.pp.ext.pref.ApplicationPreferences;
import nn.pp.ext.pref.IApplicationPreferences;
import nn.pp.logging.RemoteConsoleLogger;

public class RRCScreenContext
extends ScreenContext {
    private static final String INITIAL_PROPERTIES_FILE = "JavaRRC.properties";
    private MainScreenMediator mainScreenMediator;
    private IApplicationPreferences appSettings;
    private RRCMenuBar menubar;
    private ObservableContainer selectedDevices = new ObservableContainer("Selected Devices");
    private ObservableContainer openPorts = new ObservableContainer("Open Ports");
    private ObservableContainer scanFrameObserver = new ObservableContainer("ScanFrameObserver");
    private final ObservableContainer smartCardObserver = new ObservableContainer("SmartCardObserver");
    private final ObservableContainer audioObserver = new ObservableContainer("AudioObserver");
    private HashMap arrOpenPorts = new HashMap();
    private ArrayList listOpenPorts = new ArrayList();
    private ArrayList arrScanFrame = new ArrayList();
    private final HashMap userInfoMap = new HashMap();
    private static String kvmPopupKey;
    private int popupKeycode;
    private HelpManager helpManager;
    private DeviceNameAddressProvider devNameAddrProvider;
    private final SmartCardCore smartCardCore;
    private final SmartCardInitException.ExceptionCause smartCardExceptionCause;
    private MPCScanFrame scanFrame = null;
    public static final int PROFILE_BY_NAME = 0;
    public static final int PROFILE_BY_IP = 1;
    public static final int PROFILE_BY_DNS = 2;
    public static final int PROFILE_BY_SCAN = 3;
    public static final int DEVICEVIEW_BY_NAME = 0;
    public static final int DEVICEVIEW_BY_IP = 1;
    public static final int DEVICEVIEW_BY_DNS = 2;
    public static final int DEVICEVIEW_BY_SCAN = 3;
    private int viewBy = 0;
    private boolean monitorCountMatch = false;
    private boolean scanFrameOpened = false;
    private ArrayList<KvmPort> kvmPortsForScan;

    private RRCScreenContext(State state) {
        super(state);
        this.appSettings = new ApplicationPreferences();
        this.appSettings.importPreferences();
        RRCLogger.setLog(RemoteConsoleLogger.getInstance().getLogger().getLevel(), -1, this.appSettings.isEnableLogging());
        SmartCardCore smartCardCore = null;
        SmartCardInitException.ExceptionCause exceptionCause = null;
        try {
            smartCardCore = SmartCardCoreFactory.getDefault();
        }
        catch (SmartCardInitException smartCardInitException) {
            RRCLogger.log(300, "Exception occured on smart card initialization ", smartCardInitException);
            exceptionCause = smartCardInitException.getExceptionCause();
        }
        this.smartCardCore = smartCardCore;
        this.smartCardExceptionCause = exceptionCause;
    }

    public static ScreenContext getNewInstance(Map map) {
        RRCScreenContext rRCScreenContext = new RRCScreenContext(State.LOGGEDOUT);
        try {
            Properties properties = ConfigurationManager.loadConfiguration(RRCScreenContext.class, INITIAL_PROPERTIES_FILE);
            if (map != null && map.size() > 0) {
                RRCScreenContext.mergeProperties(properties, map);
            }
            rRCScreenContext.setApplicationProperties(properties);
        }
        catch (ConfigurableException configurableException) {
            // empty catch block
        }
        return rRCScreenContext;
    }

    public synchronized void setMainScreenMediator(MainScreenMediator mainScreenMediator) {
        this.mainScreenMediator = mainScreenMediator;
    }

    public MainScreenMediator getMainScreenMediator() {
        return this.mainScreenMediator;
    }

    public ObservableContainer getSelectedDevicesObservable() {
        return this.selectedDevices;
    }

    public ObservableContainer getOpenPortsObservable() {
        return this.openPorts;
    }

    public Object getPortByKeyObservable(Port port) {
        String string = this.createPortKey(port);
        return this.arrOpenPorts.get(string);
    }

    public Object getPortByKeyObservable(String string) {
        return this.arrOpenPorts.get(string);
    }

    public void addPortInObservable(Port port) {
        String string = this.createPortKey(port);
        this.arrOpenPorts.put(string, port);
        this.listOpenPorts.add(string);
        this.getOpenPortsObservable().setComponent(this.arrOpenPorts);
    }

    private String createPortKey(Port port) {
        Device device = port.getDevice();
        DeviceConnector deviceConnector = device.getDeviceConnector();
        InetAddress inetAddress = deviceConnector.getInetAddress();
        String string = port.getId();
        if (((IPReach)port.getDevice()).isKvmSwitch()) {
            string = port.getTargetDeviceId();
        }
        return inetAddress.getHostAddress() + " " + string;
    }

    public void removePortInObservable(Port port) {
        String string = this.createPortKey(port);
        this.arrOpenPorts.remove(string);
        this.listOpenPorts.remove(string);
        this.getOpenPortsObservable().setComponent(this.arrOpenPorts);
    }

    public void sendOpenPortNotification() {
        this.getOpenPortsObservable().setComponent(this.arrOpenPorts);
    }

    public HashMap getOpenPortsObservableByDeviceIP(long l) {
        HashMap hashMap = new HashMap();
        Iterator iterator = this.arrOpenPorts.keySet().iterator();
        String string = null;
        while (iterator.hasNext()) {
            string = (String)iterator.next();
            if (string.indexOf(String.valueOf(l)) != 0) continue;
            hashMap.put(string, this.arrOpenPorts.get(string));
        }
        return hashMap;
    }

    @Override
    public void destroy() {
        this.selectedDevices.deleteObservers();
        this.openPorts.deleteObservers();
        this.selectedDevices = null;
        this.openPorts = null;
        this.mainScreenMediator = null;
        super.destroy();
    }

    private static void mergeProperties(Properties properties, Map map) {
        String string3 = null;
        String string2 = null;
        for (String string3 : map.keySet()) {
            string2 = (String)map.get(string3);
            properties.put(string3, string2);
        }
    }

    @Override
    public void createNotificationHandlers() {
    }

    @Override
    public void destroyNotificationHandlers() {
    }

    public IApplicationPreferences getAppSettings() {
        return this.appSettings;
    }

    public HashMap getUserInfoMap() {
        return this.userInfoMap;
    }

    public ArrayList getListOfOpenPorts() {
        return this.listOpenPorts;
    }

    public void setSelectedPort(Port port) {
        this.getSelectedDevicesObservable().setComponent(port);
    }

    public Port getSelectedPort() {
        Device device;
        ArrayList arrayList;
        ObservableContainer observableContainer = this.getSelectedDevicesObservable();
        if (observableContainer != null && (arrayList = (ArrayList)observableContainer.getComponent()) != null && arrayList.get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port) {
            return (Port)device;
        }
        return null;
    }

    @Override
    public void resetDefaultFocus() {
        AbstractDisplay abstractDisplay = null;
        Port port = this.getSelectedPort();
        if (port != null) {
            String string = "";
            string = port.getViewName() + port.getPortIndex();
            this.toggleMenuBar(string);
        }
        if (port != null && port.getView() != null) {
            abstractDisplay = port.getView();
        }
        if (abstractDisplay != null && abstractDisplay.getShellInternalFrame() != null && abstractDisplay.getShellInternalFrame().isSelected()) {
            ((DeviceView)abstractDisplay).setViewFocus();
        }
    }

    public void resetDesktopFocus() {
        DeviceView deviceView = null;
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.getPanelMediator().getParent();
        JInternalFrame[] jInternalFrameArray = raritanDesktopPane.getAllFrames();
        if (jInternalFrameArray != null && jInternalFrameArray.length > 0) {
            RRCShellInternalFrame rRCShellInternalFrame = (RRCShellInternalFrame)jInternalFrameArray[jInternalFrameArray.length - 1];
            deviceView = rRCShellInternalFrame.getDeviceView();
            if (deviceView == null) {
                return;
            }
            try {
                rRCShellInternalFrame.setSelected(true);
                deviceView.setViewFocus();
            }
            catch (PropertyVetoException propertyVetoException) {
                // empty catch block
            }
        }
    }

    public void focusScanFrame() {
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.getPanelMediator().getParent();
        JInternalFrame[] jInternalFrameArray = raritanDesktopPane.getAllFrames();
        System.out.println("No. Of frames:" + jInternalFrameArray.length);
        if (jInternalFrameArray != null && jInternalFrameArray.length > 0) {
            for (int i = 0; i < jInternalFrameArray.length; ++i) {
                try {
                    RRCShellInternalFrame rRCShellInternalFrame = (RRCShellInternalFrame)jInternalFrameArray[i];
                    if (!(rRCShellInternalFrame instanceof MPCScanFrame)) {
                        rRCShellInternalFrame.toBack();
                        rRCShellInternalFrame.setFocusable(false);
                        rRCShellInternalFrame.setSelected(false);
                        rRCShellInternalFrame.setIcon(true);
                        continue;
                    }
                    ((MPCScanFrame)rRCShellInternalFrame).setSelected(true);
                    ((MPCScanFrame)rRCShellInternalFrame).requestFocus();
                    ((MPCScanFrame)rRCShellInternalFrame).requestFocusInWindow();
                    continue;
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static void setKvmPopupKey(String string) {
        kvmPopupKey = string;
    }

    public static String getKvmPopupKey() {
        return kvmPopupKey;
    }

    public void setPopupKeycode(int n) {
        this.popupKeycode = n;
    }

    public int getPopupKeyCode() {
        if (this.popupKeycode <= 0 && kvmPopupKey != null && !"".equals(kvmPopupKey.trim())) {
            new KvmMenuPopupKey(this);
        }
        return this.popupKeycode;
    }

    public void setMonitorCountMatch(boolean bl) {
        this.monitorCountMatch = bl;
    }

    public boolean isMonitorCountMatch() {
        return this.monitorCountMatch;
    }

    public void setScanFrameOpened(boolean bl) {
        this.scanFrameOpened = bl;
    }

    public boolean isScanFrameOpened() {
        return this.scanFrameOpened;
    }

    public MPCScanFrame getScanFrame() {
        return this.scanFrame;
    }

    public void setScanFrame(MPCScanFrame mPCScanFrame) {
        if (mPCScanFrame != null) {
            this.arrScanFrame.add(mPCScanFrame);
        } else {
            this.arrScanFrame.remove(0);
        }
        this.getScanFrameObserver().setComponent(this.arrScanFrame);
        this.scanFrame = mPCScanFrame;
    }

    public DeviceView getSelectView() {
        Port port = this.getSelectedPort();
        if (port != null) {
            return port.getView();
        }
        return null;
    }

    public void toggleMenuBar(String string) {
        for (CommandMenu commandMenu : this.getMainScreenMediator().getVirtualMediaMenus()) {
            this.toggleMenuBar(string, commandMenu);
        }
    }

    private void toggleMenuBar(String string, CommandMenu commandMenu) {
        Object object;
        Object object2;
        if (commandMenu.isPopupMenuVisible()) {
            return;
        }
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.getLocale());
        String string2 = "";
        String string3 = "";
        Map map = this.getVirtualMediaLocalMap();
        string2 = map != null ? ((object2 = (VirtualMediaLocalBean)map.get(string)) != null && ((VirtualMediaLocalBean)object2).isDriveConnected() ? raritanPropertyResourceBundle.getString("VirtualMediaDisconnect") + " " + ((VirtualMediaLocalBean)object2).getConnectedDrive() : raritanPropertyResourceBundle.getString("VirtualMediaLocalConnect.name")) : raritanPropertyResourceBundle.getString("VirtualMediaLocalConnect.name");
        object2 = this.getVirtualMediaImageMap();
        string3 = object2 != null ? ((object = (VirtualMediaBean)object2.get(string)) != null && ((VirtualMediaBean)object).isDriveConnected() ? raritanPropertyResourceBundle.getString("VirtualMediaDisconnect") + " " + ((VirtualMediaBean)object).getConnectedDrive() : raritanPropertyResourceBundle.getString("VirtualMediaImageConnect.name")) : raritanPropertyResourceBundle.getString("VirtualMediaImageConnect.name");
        object = commandMenu;
        CommandMenuItem commandMenuItem = null;
        Component[] componentArray = ((JMenu)object).getMenuComponents();
        for (int i = 0; i < componentArray.length; ++i) {
            commandMenuItem = (CommandMenuItem)componentArray[i];
            commandMenuItem.removeObservable(this.getSelectedDevicesObservable());
            commandMenuItem.removeActionListener((RRCMenuBar)this.getMainScreenMediator().getKeyboardMenus().get(0).getParent());
        }
        ((JMenu)object).removeAll();
        commandMenuItem = new CommandMenuItem(string2, (ScreenContext)this){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((ShowVirtualMediaLocalPanelCommand)this.getCommand()).getToolTip());
            }
        };
        commandMenuItem.setCommand(new ShowVirtualMediaLocalPanelCommand(this));
        commandMenuItem.addActionListener((RRCMenuBar)this.getMainScreenMediator().getKeyboardMenus().get(0).getParent());
        commandMenuItem.addObservable(this.getSelectedDevicesObservable());
        if (this.getLocale() == Locale.US || this.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString("MnemonicKey.VirtualMediaLocalConnectIndex")));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString("MnemonicKey.VirtualMediaLocalConnect").toCharArray()[0]).charValue()));
        commandMenuItem.setToolTipText(((ShowVirtualMediaLocalPanelCommand)commandMenuItem.getCommand()).getToolTip());
        ((JMenu)object).add(commandMenuItem);
        if (!Platform.isVirtualMediaSupported()) {
            commandMenuItem.setEnabled(false);
        }
        commandMenuItem = new CommandMenuItem(string3, (ScreenContext)this){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((ShowVirtualMediaImagePanelCommand)this.getCommand()).getToolTip());
            }
        };
        commandMenuItem.setCommand(new ShowVirtualMediaImagePanelCommand(this));
        commandMenuItem.addActionListener((RRCMenuBar)this.getMainScreenMediator().getKeyboardMenus().get(0).getParent());
        commandMenuItem.addObservable(this.getSelectedDevicesObservable());
        if (this.getLocale() == Locale.US || this.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString("MnemonicKey.VirtualMediaImageConnectIndex")));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString("MnemonicKey.VirtualMediaImageConnect").toCharArray()[0]).charValue()));
        commandMenuItem.setToolTipText(((ShowVirtualMediaImagePanelCommand)commandMenuItem.getCommand()).getToolTip());
        ((JMenu)object).add(commandMenuItem);
        ((Component)object).repaint();
    }

    public String getParamValue(Map map, String string, boolean bl) throws RRCGeneralException {
        String string2 = "";
        if (map.get(string) == null && bl) {
            throw new RRCGeneralException(string + " is missing");
        }
        string2 = (String)map.get(string);
        return string2;
    }

    public void addPredefinedMacro(String string, int n, String string2) {
        try {
            if (!KeyboardMacrosPreferences.containsNode(string)) {
                KeyboardMacrosPreferences keyboardMacrosPreferences = new KeyboardMacrosPreferences();
                keyboardMacrosPreferences.setMacroName(string);
                keyboardMacrosPreferences.setHotKeyCombination(n);
                keyboardMacrosPreferences.setMacroSequence(string2);
                keyboardMacrosPreferences.exportPreferences(string);
                for (CommandMenu commandMenu : this.getMainScreenMediator().getKeyboardMenus()) {
                    CommandMenuItem commandMenuItem = new CommandMenuItem(string, (ScreenContext)this);
                    commandMenuItem.setCommand(new DoRunKeyboardMacroCommand(this));
                    commandMenuItem.addActionListener((RRCMenuBar)commandMenu.getParent());
                    commandMenu.add(commandMenuItem);
                }
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    public synchronized HelpManager getHelpManager() {
        return this.helpManager;
    }

    public synchronized void setHelpManager(HelpManager helpManager) {
        this.helpManager = helpManager;
    }

    public static HelpManager createHelpManager(String string, Image image, Locale locale) {
        ResourceBundle resourceBundle = PropertyResourceBundle.getBundle("com.raritan.rrc.resources.help.HelpMap", locale);
        assert (resourceBundle != null) : "HelpMap file missing";
        HelpManager helpManager = new HelpManager(resourceBundle, RRCApplication.class.getClassLoader(), "nn/pp/common/help/", string, image);
        return helpManager;
    }

    @Override
    public synchronized DeviceNameAddressProvider getDeviceNameAddressProvider() {
        return this.devNameAddrProvider;
    }

    public synchronized void setDeviceNameAddressProvider(DeviceNameAddressProvider deviceNameAddressProvider) {
        this.devNameAddrProvider = deviceNameAddressProvider;
    }

    public synchronized void setDeviceViewBy(int n) {
        this.viewBy = n;
    }

    public synchronized int getCreateProfileBy() {
        return this.viewBy;
    }

    public SmartCardCore getSmartCardCore() {
        return this.smartCardCore;
    }

    public SmartCardInitException.ExceptionCause getSmartCardExceptionCause() {
        return this.smartCardExceptionCause;
    }

    public ObservableContainer getSmartCardObserver() {
        return this.smartCardObserver;
    }

    public ObservableContainer getAudioObserver() {
        return this.audioObserver;
    }

    public ObservableContainer getScanFrameObserver() {
        return this.scanFrameObserver;
    }

    public void setMenuBar(RRCMenuBar rRCMenuBar) {
        this.menubar = rRCMenuBar;
    }

    public RRCMenuBar getMenuBar() {
        return this.menubar;
    }

    public void setKvmPortsForScan(ArrayList<KvmPort> arrayList) {
        this.kvmPortsForScan = new ArrayList();
        this.kvmPortsForScan = arrayList;
    }

    public ArrayList<KvmPort> getKvmPortsForScan() {
        return this.kvmPortsForScan;
    }

    public void setSelectedKvmPort(KvmPort kvmPort) {
    }
}

