/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.rfbbridge;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.KvmPortPermissionHelper;
import com.raritan.rrc.data.MassStorageDevice;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.USBProfile;
import com.raritan.rrc.data.USBProfilesInfo;
import com.raritan.rrc.data.VMConfigInfo;
import com.raritan.rrc.data.VMInterfaceInfo;
import com.raritan.rrc.data.VirtualMediaBean;
import com.raritan.rrc.data.VirtualMediaLocalBean;
import com.raritan.rrc.ui.MonitorSettingsHandler;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.AudioMenuCommand;
import com.raritan.rrc.ui.commands.ConnectAudioCommand;
import com.raritan.rrc.ui.commands.DeSelectPortViewCommand;
import com.raritan.rrc.ui.commands.DoDisconnectCommand;
import com.raritan.rrc.ui.commands.DoEnterOnscreenMenuCommand;
import com.raritan.rrc.ui.commands.DoExitOnscreenMenuCommand;
import com.raritan.rrc.ui.commands.SelectPortViewCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaImagePanelCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaLocalPanelCommand;
import com.raritan.rrc.ui.commands.SmartCardAutoMountCommand;
import com.raritan.rrc.ui.components.ContextPopupMenu;
import com.raritan.rrc.ui.components.FullScreenToolBar;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.DeviceViewAdapter;
import com.raritan.rrc.ui.panes.ErrorHandlerImpl;
import com.raritan.rrc.ui.panes.G2CompressionPanel;
import com.raritan.rrc.ui.panes.ICommandHandler;
import com.raritan.rrc.ui.panes.OptionsPanel;
import com.raritan.rrc.ui.panes.PropertiesPanel;
import com.raritan.rrc.ui.panes.RFBViewCommandHandler;
import com.raritan.rrc.ui.panes.RRCShellInternalFrame;
import com.raritan.rrc.ui.panes.VirtualMediaImagePanel;
import com.raritan.rrc.ui.panes.VirtualMediaLocalPanel;
import com.raritan.rrc.ui.panes.mediator.MainScreenMediator;
import com.raritan.rrc.ui.rfbbridge.MainScreenRCAdapter;
import com.raritan.rrc.ui.rfbbridge.RFBProfile;
import com.raritan.rrc.ui.rfbbridge.VideoSettingsRCAdapter;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.AbstractUIManager;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandCheckMenuItem;
import com.raritan.tools.ui.components.CommandMenu;
import com.raritan.tools.ui.components.CommandMenuItem;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.ShellInternalFrame;
import com.util.kbd.KeyboardUtil;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import javaclientlib.utils.RRCGeneralException;
import javaclientlib.utils.RRCLogger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import nn.pp.audiocore.AudioEventListener;
import nn.pp.common.ApplicationContext;
import nn.pp.common.ContextEvent;
import nn.pp.common.ContextListener;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.common.VMFeatures;
import nn.pp.common.audio.AudioErrorsAndMessageHandler;
import nn.pp.common.audio.AudioIndexHelper;
import nn.pp.common.smartcard.SmartCardBean;
import nn.pp.common.smartcard.SmartCardErrorsAndMessagesHandler;
import nn.pp.common.smartcard.SmartCardIndexHelper;
import nn.pp.common.ui.helpers.IKVMTargetViewer;
import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationEvent;
import nn.pp.core.T;
import nn.pp.ext.ExtensionFactory;
import nn.pp.ext.devPref.DevicePrefs;
import nn.pp.ext.macro.IMacro;
import nn.pp.ext.macro.IMacroHelper;
import nn.pp.ext.macro.MacroException;
import nn.pp.ext.macro.MacroFactory;
import nn.pp.ext.pref.IApplicationPreferences;
import nn.pp.rccore.IAudioStatusExSupport;
import nn.pp.rccore.IKvmPort;
import nn.pp.rccore.ILicenseSupport;
import nn.pp.rccore.IMultiMonitorTargetSupport;
import nn.pp.rccore.IUsbProfile;
import nn.pp.rccore.IUsbProfileList;
import nn.pp.rccore.IVMMountRequestResponse;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.KeyboardMacro;
import nn.pp.rccore.RCAdapter;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.RCCoreFactory;
import nn.pp.rccore.RCException;
import nn.pp.rccore.UsbProfile;
import nn.pp.rccore.VMMountRequestResponse;
import nn.pp.rccore.impl.KeyValuePair;
import nn.pp.rccore.impl.rfb.RfbNotificationEvent;

public class RFBView
extends DeviceViewAdapter
implements IKVMTargetViewer,
FocusListener {
    private static String defkbd;
    private JComponent console = null;
    private RFBProfile profile = null;
    private KvmPort kvmPort;
    private int userCount;
    public static final int MOUSE_MODE_ABSOLUTE = 0;
    public static final int MOUSE_MODE_INTELLI = 1;
    public static final int MOUSE_MODE_STANDARD = 2;
    private int mouseMode;
    private RFBViewCommandHandler rFBViewCommandHandler = new RFBViewCommandHandler();
    private JScrollPane jsp;
    private VMMountRequestResponse vmMountsResponse = null;
    private VMMountRequestResponse[] vmShareTable = null;
    private JLabel northLabel;
    private JLabel southLabel;
    private JLabel westLabel;
    private JLabel eastLabel;
    private JLabel northWestLabel;
    private JLabel southEastLabel;
    private Timer timer = null;
    private boolean adjustingVertical = false;
    private int adjustmentValue = 0;
    private JScrollBar hScrollBar = new JScrollBar(0);
    private JScrollBar vScrollBar = new JScrollBar(1);
    private AllListener allListener = new AllListener();
    private static final Dimension VIEW_BORER_DIM;
    private boolean scrollBothDirections = false;
    private boolean showScrollBorder = false;
    private boolean isFullScreenMode = false;
    private boolean scaleVideoFlag = false;
    private int rfbSessionId = -1;
    private ContextPopupMenu contextMenuKvm = null;
    private boolean isSambaConnectSuccessful = false;
    private boolean isSambaDisconnectSuccessful = false;
    private int mspConnectionSuccess = 0;
    private VirtualMediaImagePanel vmImagePanel = null;
    private VirtualMediaLocalPanel vmLocalPanel = null;
    private String ccVMPermissions = "";
    private static final int UNIT_INCREMENT = 10;
    private int dataIn = 0;
    private int dataOut = 0;
    private int fps = 0;
    private LinkedList<KvmPort> switchPortQueue = new LinkedList();
    private HashMap capabilityMap;
    private RemoteConsoleParameters rcparams;
    private RCCore rccore;
    private ILicenseSupport license;
    private IAudioStatusExSupport audioEx;
    private RCAdapter rcAdapter;
    private VideoSettingsRCAdapter vsAdapter;
    private boolean connected;
    private boolean fullyConnected = false;
    private boolean videoSettingsSupported = false;
    private boolean viewFinalized = false;
    private List<INotificationEvent> collectedNotifications = new Vector<INotificationEvent>();
    public static final String NO_VIDEO_FROM_TARGET_SERVER = "No video from target server";
    private boolean monitorOnly;
    private boolean exclusiveMode;
    private boolean singleCursorMode;
    private List<IVMMountRequestResponse> remoteIsoList;
    private IVideoSettings vidSettings = null;
    private Hashtable<Integer, KeyStroke> hotkeyMap;
    private Hashtable<Integer, KeyValuePair<Integer, String>> categoryMap;
    private IMacroHelper macroHelper;
    private RCCore.Compression currentCompression;
    private RCCore.ColorDepth currentColorDepth;
    private RCCore.Smoothing currentSmoothing;
    private boolean isAutoSelected = false;
    private boolean isInitialEndoingAuto = false;
    private boolean isSunTarget;
    private boolean colorCalibSupported = false;
    private boolean cimLangOptsSupported = true;
    private boolean isQuitEvent = false;
    private ApplicationContext ctx;
    private ContextListener ctxListener;
    private final SmartCardErrorsAndMessagesHandlerImpl smartCardErrorsAndMessagesHandlerImpl = new SmartCardErrorsAndMessagesHandlerImpl();
    private int keyboardType = 0;
    private boolean capsLockOn = false;
    private boolean numLockOn = false;
    private boolean scrollLockOn = false;
    private AudioEventListener.DeviceState playbackState = AudioEventListener.DeviceState.DISCONNECTED;
    private AudioEventListener.DeviceState captureState = AudioEventListener.DeviceState.DISCONNECTED;
    IApplicationPreferences appPrefs;
    private ILicenseSupport.IListener licenseListener = new ILicenseSupport.IListener(){

        @Override
        public void licenseFeatureSupportChanged(String string, boolean bl) {
            RFBView.this.kvmPort.updateLicenses();
        }
    };
    private AudioErrorsAndMessageHandlerImpl audioErrorsAndMessageHandlerImpl = new AudioErrorsAndMessageHandlerImpl();

    public RFBView(ScreenContext screenContext, RFBProfile rFBProfile) {
        super((RRCScreenContext)screenContext);
        this.profile = rFBProfile;
        RRCLogger.log(200, "Connecting to target Id " + this.profile.targetId);
        this.contextMenuKvm = new ContextPopupMenu(this.scrContext, this);
        this.contextMenuKvm.setOpaque(false);
        this.contextMenuKvm.setVisible(false);
        this.rcparams = new RemoteConsoleParameters();
        this.rcparams.host = rFBProfile.getRemoteHost();
        this.rcparams.ssl = rFBProfile.sslRequired;
        this.rcparams.sessionId = -1;
        this.rcparams.username = rFBProfile.username;
        this.rcparams.password = rFBProfile.password;
        this.rcparams.ericKey = rFBProfile.connectId;
        this.rcparams.rdmSession = rFBProfile.rdmSession;
        this.rcparams.rdmSessionMultiPortTag = rFBProfile.getRdmSessionMultiPortTag();
        this.rcparams.tcpPort = rFBProfile.primaryPort;
        this.rcparams.proxyConnectionIdVM = rFBProfile.proxyModeConnectionID;
        this.rcparams.proxyUseSSL = rFBProfile.proxyUseSSL;
        this.appPrefs = ((RRCScreenContext)this.scrContext).getAppSettings();
        this.rcparams.targetPortId = MPCUtil.isCCLaunched((RRCScreenContext)screenContext) ? rFBProfile.portIdRDM : rFBProfile.targetId;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.setColor(Color.YELLOW);
        super.paintComponent(graphics);
    }

    public void init() {
        RRCLogger.log(300, "init Started");
        this.setLayout(new BorderLayout());
        this.rcAdapter = new MainScreenRCAdapter(this);
        this.vsAdapter = new VideoSettingsRCAdapter(this);
        this.ctx = ApplicationContext.getInstance();
        this.ctxListener = new ContextListener(){

            @Override
            public void contextChanged(ContextEvent contextEvent) {
                String string = contextEvent.getKey();
                if (string.equals("HOTKEYMAP")) {
                    RFBView.this.populateHotkeyMaps();
                } else if (string.equals("SCROLLBORDERS")) {
                    RFBView.this.showScrollBars();
                }
            }

            @Override
            public void contextDestroyed(ContextEvent contextEvent) {
            }

            @Override
            public void contextInitialized(ContextEvent contextEvent) {
            }
        };
        this.ctx.registerListener(this.ctxListener);
        this.macroHelper = MacroFactory.getInstance().getMacroHelper();
        this.rccore = RCCoreFactory.loadGraphicalRCCore(RRCLogger.getLogger());
        this.rccore.addVideoEventListener(this.rcAdapter, 48);
        this.rccore.addVideoEventListener(this.vsAdapter, 24);
        this.rccore.addKeyboardListener(this.rcAdapter);
        this.rccore.addConnectionEventListener(this.rcAdapter, 223);
        this.rccore.addNotificationListener(this.rcAdapter);
        this.rccore.addVirtualMediaInfoListener(this.rcAdapter, 7);
        this.rccore.addMouseModeListener(this.rcAdapter, 7);
        this.license = this.rccore.getCapablity(ILicenseSupport.class);
        if (this.license != null) {
            this.license.addListener(this.licenseListener);
        }
        this.kvmPort.setLicenseSupport(this.license);
        this.audioEx = this.rccore.getCapablity(IAudioStatusExSupport.class);
        this.kvmPort.setAudioEx(this.audioEx);
        this.hotkeyMap = new Hashtable();
        this.categoryMap = new Hashtable();
        this.populateHotkeyMaps();
        IApplicationPreferences iApplicationPreferences = ExtensionFactory.getInstance().getApplicationPreferences();
        iApplicationPreferences.importPreferences();
        this.keyboardTypeChanged(iApplicationPreferences.getKeyboardType());
        this.rccore.addKeyboardInfoListener(this.rcAdapter, 24);
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        int n = rRCScreenContext.getAppSettings().getKeyboardType();
        Locale locale = KeyboardUtil.getLocale(n);
        RRCLogger.log(300, "Keyboard Number and locale is  " + n + " : " + locale);
        if (MPCUtil.isCCLaunched(rRCScreenContext)) {
            if (this.kvmPort != null) {
                String string;
                HashMap hashMap = this.kvmPort.getDeviceConnector().getConnectionMap();
                if (this.scrContext != null) {
                    try {
                        string = ((RRCScreenContext)this.scrContext).getParamValue(hashMap, "VirtualMedia", true);
                        this.setCCVMPermissions(string);
                        RRCLogger.log(300, "Setting VM Permissions when launched from CC:" + string);
                    }
                    catch (RRCGeneralException rRCGeneralException) {
                        RRCLogger.log(300, "Unable to set VM Permissions when launched from CC");
                    }
                }
                if ("Control".equals(string = (String)hashMap.get("Access"))) {
                    this.kvmPort.getPortPermissionHelper().setKvmPermission(new KvmPortPermissionHelper.KvmPermission(IKvmPort.KvmPermission.CONTROL, true));
                }
            }
            if (this.ccVMPermissions != null && !this.ccVMPermissions.equals("")) {
                if (this.ccVMPermissions.toLowerCase().equals("n")) {
                    RRCLogger.log(300, "NO VM Permissions when launched from CC. Disabling VM Menu:" + this.ccVMPermissions);
                    this.disableVMMenu();
                    this.kvmPort.getPortPermissionHelper().setVmPermission(new KvmPortPermissionHelper.VmPermission(IKvmPort.VmPermission.DENY, true));
                } else if (this.ccVMPermissions.toLowerCase().equals("w")) {
                    this.kvmPort.getPortPermissionHelper().setVmPermission(new KvmPortPermissionHelper.VmPermission(IKvmPort.VmPermission.READWRITE, true));
                } else {
                    this.kvmPort.getPortPermissionHelper().setVmPermission(new KvmPortPermissionHelper.VmPermission(IKvmPort.VmPermission.READONLY, true));
                }
            }
        }
        RRCLogger.log(300, "init Finished");
    }

    private void populateHotkeyMaps() {
        this.hotkeyMap.clear();
        this.categoryMap.clear();
        int n = 0;
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        String string = rRCScreenContext.getAppSettings().getkeyboardMenuHotkey();
        this.hotkeyMap.put(n, KeyStroke.getKeyStroke("ctrl alt " + string.charAt(string.length() - 1)));
        this.categoryMap.put(n, new KeyValuePair<Integer, String>(3, "EXIT_FULLSCREEN_VALUE"));
        ++n;
        List<Object> list = new Vector(0);
        try {
            list = MacroFactory.getInstance().getMacroParser().getKeyboardMacros();
        }
        catch (MacroException macroException) {
            RRCLogger.log(150, "MacroException getting current keyboard macros, so Keyboard Macro functionality will be disabled", macroException);
        }
        for (IMacro iMacro : list) {
            if (iMacro.getHotKey() <= -1) continue;
            this.hotkeyMap.put(n, KeyStroke.getKeyStroke("ctrl alt " + iMacro.getHotKey()));
            this.categoryMap.put(n, new KeyValuePair<Integer, String>(1, iMacro.getSequence()));
            ++n;
        }
        this.rccore.setHotkeys(this.hotkeyMap);
    }

    private JPanel getConsolePanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        this.showScrollBorder = ((RRCScreenContext)this.scrContext).getAppSettings().isShowScrollBorders();
        this.northLabel = new JLabel();
        this.southLabel = new JLabel();
        this.westLabel = new JLabel();
        this.eastLabel = new JLabel();
        this.northWestLabel = new JLabel();
        this.southEastLabel = new JLabel();
        this.northLabel.setPreferredSize(VIEW_BORER_DIM);
        this.southLabel.setPreferredSize(VIEW_BORER_DIM);
        this.eastLabel.setPreferredSize(VIEW_BORER_DIM);
        this.westLabel.setPreferredSize(VIEW_BORER_DIM);
        this.northLabel.setMinimumSize(VIEW_BORER_DIM);
        this.southLabel.setMinimumSize(VIEW_BORER_DIM);
        this.eastLabel.setMinimumSize(VIEW_BORER_DIM);
        this.westLabel.setMinimumSize(VIEW_BORER_DIM);
        this.northWestLabel.setPreferredSize(VIEW_BORER_DIM);
        this.southEastLabel.setPreferredSize(VIEW_BORER_DIM);
        boolean bl = this.showScrollBorder && !this.isScaleVideoFlag();
        this.northLabel.setVisible(bl);
        this.southLabel.setVisible(bl);
        this.eastLabel.setVisible(bl);
        this.westLabel.setVisible(bl);
        this.northWestLabel.setVisible(bl);
        this.southEastLabel.setVisible(bl);
        this.northLabel.addMouseListener(this.allListener);
        this.southLabel.addMouseListener(this.allListener);
        this.westLabel.addMouseListener(this.allListener);
        this.eastLabel.addMouseListener(this.allListener);
        this.northWestLabel.addMouseListener(this.allListener);
        this.southEastLabel.addMouseListener(this.allListener);
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new BorderLayout());
        jPanel2.add((Component)this.southEastLabel, "East");
        jPanel2.add((Component)this.southLabel, "Center");
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new BorderLayout());
        jPanel3.add((Component)this.northWestLabel, "West");
        jPanel3.add((Component)this.northLabel, "Center");
        jPanel.add((Component)jPanel3, "North");
        jPanel.add((Component)jPanel2, "South");
        jPanel.add((Component)this.westLabel, "West");
        jPanel.add((Component)this.eastLabel, "East");
        JPanel jPanel4 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 10;
        gridBagConstraints.fill = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        jPanel4.add((Component)this.console, gridBagConstraints);
        this.jsp = new JScrollPane();
        this.jsp.setViewportView(jPanel4);
        this.jsp.setHorizontalScrollBarPolicy(31);
        this.jsp.setVerticalScrollBarPolicy(21);
        this.jsp.addComponentListener(this.allListener);
        jPanel.add((Component)this.jsp, "Center");
        return jPanel;
    }

    public void handleHotkeys(int n) {
        RRCLogger.log(300, "Hotkey " + n + " detected");
        switch (this.categoryMap.get(n).getKey()) {
            case 3: {
                if (this.isContextMenuKVMVisible()) break;
                this.setContextMenuKVMVisible(true);
                break;
            }
            case 1: {
                this.sendKeyboardMacro(this.categoryMap.get(n).getValue());
                break;
            }
            default: {
                this.showFSExitTip();
            }
        }
    }

    @Override
    public void startVideo() {
        RRCLogger.log(300, "startVideo Started");
        if (this.kvmPort != null) {
            G2CompressionPanel.resetPanel(this.kvmPort.getPortKey());
            this.start();
            MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, this.kvmPort);
        } else {
            RRCLogger.log(150, "KvmPort is null, unable to set view");
        }
        RRCLogger.log(300, "startVideo Finished");
    }

    private void setPortState() {
        BladeChassis bladeChassis;
        this.kvmPort.setConnected(true);
        this.kvmPort.setState("CONNECTED");
        this.kvmPort.firePropertyChange("DEVICE_PORT_VIEW_ADD", null, this);
        this.doAutoSettings();
        if (this.kvmPort.isBladePort() && (bladeChassis = this.kvmPort.getParentBladeChassis()) != null) {
            bladeChassis.setConnected(true);
            bladeChassis.setState("CONNECTED");
        }
    }

    private void doAutoSettings() {
        RRCLogger.log(300, "doAutoSetting start");
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        RRCLogger.log(300, "Auto sync mouse: " + rRCScreenContext.getAppSettings().isAutoSyncMouse());
        if (rRCScreenContext.getAppSettings().isAutoSyncMouse()) {
            this.doMouseSyncSoft();
        }
        RRCLogger.log(300, "doAutoSetting end");
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.kvmPort = (KvmPort)commandContext.getCommandParameter("ports");
        if (this.kvmPort != null) {
            this.kvmPort.setView(this);
            this.init();
            this.kvmPort.setConnected(true);
            if (this.scrContext.getApplicationProperty("connection") != null) {
                this.shellInternalFrame.addInternalFrameListener(new InternalFrameAdapter(){

                    @Override
                    public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
                        MPCUtil.notifyObservers((RRCScreenContext)RFBView.this.scrContext, RFBView.this.kvmPort);
                    }
                });
            }
        } else {
            RRCLogger.log(150, "KvmPort is null, unable to set view");
            return;
        }
    }

    private boolean isConnected() {
        return this.fullyConnected;
    }

    public void start() {
        this.connectRemoteConsole();
        this.console = this.rccore.getRCJComponent();
        this.console.addComponentListener(this.allListener);
        this.hScrollBar.addAdjustmentListener(this.allListener);
        this.hScrollBar.setUnitIncrement(10);
        this.vScrollBar.addAdjustmentListener(this.allListener);
        this.vScrollBar.setUnitIncrement(10);
        this.add((Component)this.getConsolePanel(), "Center");
        this.add((Component)this.hScrollBar, "South");
        this.add((Component)this.vScrollBar, "East");
        this.focusGained(new FocusEvent(this, 1004));
    }

    public void connectRemoteConsole() {
        IMultiMonitorTargetSupport iMultiMonitorTargetSupport;
        this.setVisible(true);
        LinkedHashMap linkedHashMap = (LinkedHashMap)this.kvmPort.getBaseDevice().getAppletParameters();
        String[] stringArray = linkedHashMap.keySet().toArray(new String[0]);
        DevicePreferences devicePreferences = this.getPort().getDevice().getDevPrefs();
        if (devicePreferences != null && (devicePreferences.getG2ColorDepth() != null || devicePreferences.getG2ConnectionSpeed() != null || devicePreferences.getG2Smoothing() != null)) {
            boolean bl = false;
            if (devicePreferences.getG2ConnectionSpeed() == null && devicePreferences.getG2ColorDepth() != null && devicePreferences.getG2Smoothing() != null) {
                bl = true;
            }
            this.rccore.setConnectionProperties(bl, devicePreferences.getG2ConnectionSpeed(), devicePreferences.getG2ColorDepth(), devicePreferences.getG2Smoothing());
            this.setCurrentCompression(devicePreferences.getG2ConnectionSpeed());
            this.setCurrentColorDepth(devicePreferences.getG2ColorDepth());
            this.setCurrentSmoothing(devicePreferences.getG2Smoothing());
            this.setAutoSelected(bl);
        } else if (this.getPort().getDevice().isModemProfiled()) {
            this.rccore.setLowBandwidth(true);
            this.setCurrentCompression(RCCore.Compression.LEVEL_8);
        }
        this.rccore.setAppletParameterMap(linkedHashMap);
        if ((this.port.isMultiMonitorPort() || MPCUtil.isCCMultiMonitorLaunch() || !MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext)) && (iMultiMonitorTargetSupport = this.rccore.getCapablity(IMultiMonitorTargetSupport.class)) != null) {
            HashMap<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> hashMap = new HashMap<IMultiMonitorTargetSupport.ClientSessionInitProperties, String>();
            hashMap.put(IMultiMonitorTargetSupport.ClientSessionInitProperties.MULTI_MONITOR_ASSOCIATION_ID, StringUtils.notNullOrEmpty(this.rcparams.rdmSessionMultiPortTag) ? this.rcparams.rdmSessionMultiPortTag : this.scrContext.getApplication().getAppId());
            iMultiMonitorTargetSupport.setClientSessionInitProperties(hashMap);
        }
        try {
            try {
                if (this.rcparams.username != null && this.rcparams.password != null) {
                    this.rccore.connectRCWithUserLogin(this.rcparams.host, this.rcparams.tcpPort, this.rcparams.ssl, this.rcparams.targetPortId, this.rcparams.username, this.rcparams.password);
                } else if (this.rcparams.ericKey != null && this.rcparams.ericKey.length() > 0) {
                    this.rccore.connectRCWithEricKey(this.rcparams.host, this.rcparams.tcpPort, this.rcparams.ssl, this.rcparams.targetPortId, this.rcparams.ericKey);
                } else if (this.rcparams.rdmSession != null && this.rcparams.rdmSession.length() > 0) {
                    this.rccore.connectRCWithRdmSession(this.rcparams.host, this.rcparams.tcpPort, this.rcparams.ssl, this.rcparams.targetPortId, this.rcparams.rdmSession, this.rcparams.proxyConnectionIdVM, this.rcparams.proxyUseSSL);
                } else {
                    throw new RCException(T._("Authentication parameters missing!"));
                }
                this.connected = true;
            }
            catch (IOException iOException) {
                RRCLogger.log(100, "I/O Error connecting to remote device", iOException);
                throw new RCException(T._("I/O Error connecting to remote device"));
            }
        }
        catch (RCException rCException) {
            RRCLogger.log(200, "RCException connecting to remote device", rCException);
            this.setStatusMessage(T._("Error connecting to remote device"));
        }
    }

    public Dimension getBlankSize() {
        Dimension dimension = this.getSize();
        return dimension;
    }

    public void canvasSizeChanged(Dimension dimension) {
        RRCLogger.log(200, "Canvas size changed to " + dimension);
        this.showScrollBars();
        if (this.vScrollBar != null && this.vScrollBar.isVisible()) {
            this.vScrollBar.setValue(0);
        }
        if (this.hScrollBar != null && this.hScrollBar.isVisible()) {
            this.hScrollBar.setValue(0);
        }
        this.validate();
        this.console.requestFocusInWindow();
    }

    public void changeWindowMode() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void rcConnected() {
        RFBView rFBView = this;
        synchronized (rFBView) {
            Object object;
            this.setPortState();
            if (!this.switchPortQueue.isEmpty()) {
                object = new Thread(new Runnable(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void run() {
                        RFBView rFBView = RFBView.this;
                        synchronized (rFBView) {
                            KvmPort kvmPort = (KvmPort)RFBView.this.switchPortQueue.removeLast();
                            RFBView.this.switchPortQueue.clear();
                            RFBView.this.switchKvmPort(RFBView.this.kvmPort, kvmPort);
                        }
                    }
                });
                ((Thread)object).start();
            }
            RRCLogger.log(300, "Connected for view " + this.getPortName() + "_" + this.getPortIndex());
            if (this.isAllPortsConnected() && (object = this.getPrimaryPortView()) != null) {
                super.allPortsConnected();
            }
        }
    }

    private boolean isAllPortsConnected() {
        if (this.port.isSecondaryPort()) {
            return this.getPrimaryPortView().isAllPortsConnected();
        }
        Vector<RFBView> vector = new Vector<RFBView>();
        vector.add(this);
        vector.addAll(this.getSecondaryPortViews());
        for (RFBView rFBView : vector) {
            if (rFBView.isConnected()) continue;
            return false;
        }
        return true;
    }

    private void allPortsConnected() {
        ((KvmPort)this.port).reevaluatePermissions();
        for (Port port : this.port.getAssociatedPorts()) {
            ((KvmPort)port).reevaluatePermissions();
        }
        final Runnable runnable = new Runnable(){

            @Override
            public void run() {
                IApplicationPreferences iApplicationPreferences;
                if (RFBView.this.port.isPrimaryPort()) {
                    RFBView.this.adjustMultiMonitorPortWindows();
                    try {
                        RFBView.this.shellInternalFrame.setSelected(true);
                    }
                    catch (PropertyVetoException propertyVetoException) {
                        // empty catch block
                    }
                }
                if ((iApplicationPreferences = ExtensionFactory.getInstance().getApplicationPreferences()).isAlwaysOpenInFS()) {
                    RFBView.this.setTargetScreenResolution(true);
                }
                for (RFBView rFBView : RFBView.this.getSecondaryPortViews()) {
                    rFBView.finalizeView();
                }
                RFBView.this.finalizeView();
                if (RFBView.this.port.isPrimaryPort()) {
                    new Thread(){

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000L);
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                            SwingUtilities.invokeLater(new Runnable(){

                                @Override
                                public void run() {
                                    try {
                                        RFBView.this.shellInternalFrame.setSelected(true);
                                        MPCUtil.notifyObservers((RRCScreenContext)RFBView.this.scrContext, RFBView.this.port);
                                    }
                                    catch (Exception exception) {
                                        // empty catch block
                                    }
                                }
                            });
                        }
                    }.start();
                }
            }
        };
        new Thread(){

            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                SwingUtilities.invokeLater(runnable);
            }
        }.start();
    }

    private void finalizeView() {
        if (this.viewFinalized) {
            return;
        }
        this.viewFinalized = true;
        for (INotificationEvent iNotificationEvent : this.collectedNotifications) {
            if (iNotificationEvent.isError()) {
                ErrorHandlerImpl.getInstance().handleError(iNotificationEvent.getErrorCode(), this.scrContext.getApplication().getContentPaneForPort(this.port), this);
                continue;
            }
            if (iNotificationEvent.isWarning()) {
                ErrorHandlerImpl.getInstance().handleWarning(iNotificationEvent.getErrorCode(), this.scrContext.getApplication().getContentPaneForPort(this.port), this);
                continue;
            }
            if (!iNotificationEvent.isInfo()) continue;
            ErrorHandlerImpl.getInstance().handleInfo(iNotificationEvent.getErrorCode(), this.scrContext.getApplication().getContentPaneForPort(this.port), this);
        }
    }

    private void disconnectAssociatedPorts() {
        for (Port port : this.port.getAssociatedPorts()) {
            ((KvmPort)port).disconnect(false);
        }
    }

    public void networkError() {
        try {
            if (!this.shellInternalFrame.isClosed() && this.kvmPort.isConnected()) {
                this.close();
                this.kvmPort.setConnected(false);
                this.kvmPort.setState("AVAILABLE");
                this.kvmPort.firePropertyChange("DEVICE_PORT_VIEW_REMOVE", this, null);
                this.closeSelf();
            }
        }
        catch (PropertyVetoException propertyVetoException) {
            RRCLogger.log(100, "", propertyVetoException);
        }
    }

    public void setWLANQuality(int n) {
    }

    public void setTitle(String string) {
    }

    public void setFrameTitle(String string) {
    }

    public void setSunTarget(boolean bl) {
        this.isSunTarget = bl;
    }

    @Override
    public boolean isSunTarget() {
        return this.isSunTarget;
    }

    public void setCimLanguageOptionsSupported(boolean bl) {
        this.cimLangOptsSupported = bl;
    }

    public boolean isCimLanguageOptionsSupported() {
        return this.cimLangOptsSupported;
    }

    public static void main(String[] stringArray) {
    }

    public RFBProfile getProfile() {
        return this.profile;
    }

    public void setProfile(RFBProfile rFBProfile) {
        this.profile = rFBProfile;
    }

    public synchronized void close() {
        this.removeListeners();
    }

    @Override
    public synchronized boolean disconnect() {
        if (this.kvmPort != null) {
            RRCLogger.log(300, "Disconnecting port " + this.kvmPort.getPortIndex());
        }
        this.removeListeners();
        if (MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext) && !this.isQuitEvent()) {
            this.scrContext.getApplication().disconnect();
        }
        return true;
    }

    @Override
    public synchronized void removeListeners() {
        if ((RRCScreenContext)this.scrContext != null) {
            this.closeOpenVMConnections(false);
        }
        this.disconnectSmartCard(this.kvmPort);
        this.disconnectAudio(this.kvmPort);
        if (this.rccore != null) {
            if (this.connected) {
                this.rccore.disconnect();
                this.connected = false;
            }
            this.rccore.removeVideoEventListener(this.vsAdapter);
            this.rccore.removeVideoEventListener(this.rcAdapter);
            this.rccore.removeKeyboardListener(this.rcAdapter);
            this.rccore.removeConnectionEventListener(this.rcAdapter);
            this.rccore.removeNotificationListener(this.rcAdapter);
            this.rccore.removeVirtualMediaInfoListener(this.rcAdapter);
            this.rccore.removeMouseModeListener(this.rcAdapter);
            this.rccore.removeKeyboardInfoListener(this.rcAdapter);
            this.rccore = null;
        }
        if (this.license != null) {
            this.license.removeListener(this.licenseListener);
            this.license = null;
        }
        this.audioEx = null;
        if (this.contextMenuKvm != null) {
            this.contextMenuKvm.cleanup();
            this.contextMenuKvm = null;
        }
        if (this.ctx != null && this.ctxListener != null) {
            this.ctx.removeListener(this.ctxListener);
        }
        this.jsp = null;
        this.rcAdapter = null;
        this.vsAdapter = null;
        this.profile = null;
        this.rcparams = null;
        this.console = null;
        this.profile = null;
        this.kvmPort = null;
        this.vmMountsResponse = null;
        this.vmShareTable = null;
        this.northLabel = null;
        this.southLabel = null;
        this.eastLabel = null;
        this.westLabel = null;
        this.northWestLabel = null;
        this.southEastLabel = null;
        this.hScrollBar = null;
        this.vScrollBar = null;
        this.allListener = null;
        this.vmImagePanel = null;
        this.vmLocalPanel = null;
        this.ccVMPermissions = null;
        if (this.switchPortQueue != null) {
            this.switchPortQueue.clear();
            this.switchPortQueue = null;
        }
        if (this.capabilityMap != null) {
            this.capabilityMap.clear();
            this.capabilityMap = null;
        }
        if (this.remoteIsoList != null) {
            this.remoteIsoList.clear();
            this.remoteIsoList = null;
        }
        this.vidSettings = null;
        if (this.hotkeyMap != null) {
            this.hotkeyMap.clear();
            this.hotkeyMap = null;
        }
        if (this.categoryMap != null) {
            this.categoryMap.clear();
            this.categoryMap = null;
        }
        this.macroHelper = null;
        this.currentColorDepth = null;
        this.currentCompression = null;
        this.currentSmoothing = null;
        this.ctxListener = null;
        this.ctx = null;
        System.runFinalization();
        System.gc();
    }

    @Override
    public void keyboardTypeChanged(int n) {
        Locale locale = null;
        RRCLogger.log(300, "New keyboard type is: " + n);
        this.keyboardType = n;
        locale = KeyboardUtil.getLocale(n);
        if (locale == null) {
            RRCLogger.log(150, "Unknown keyboard type " + n);
        } else {
            RRCLogger.log(300, "Setting locale to " + locale);
            try {
                this.rccore.setLocalKeyboardMapping(locale);
            }
            catch (IOException iOException) {
                RRCLogger.log(150, "IOException trying to set local keyboard mapping", iOException);
            }
        }
    }

    @Override
    public void sendCtrlAltDelete() {
        RRCLogger.log(300, "Sending CAD");
        this.sendKeyboardMacro("p 0&&p 2&&p 16&&r 16&&r 2&&r 0");
    }

    @Override
    public void sendAltTab() {
        RRCLogger.log(300, "Sending LeftAlt+Tab");
        this.sendKeyboardMacro("p 2&&p 19&&r 19&&r 2");
    }

    @Override
    public void sendCtrlNumlock() {
        RRCLogger.log(300, "Sending CTRL-NUMLOCK");
        this.sendKeyboardMacro("p 0&&p 8&&r 8&&r 0");
    }

    private void setTargetScreenResolution(boolean bl, AbstractUIManager.FullScreenTarget fullScreenTarget) {
        this.setFullScreenMode(bl);
        AbstractUIManager abstractUIManager = ((RRCScreenContext)this.scrContext).getApplication();
        abstractUIManager.changeScreen(bl, this.getShellInternalFrame(), this.port, fullScreenTarget);
        if (this.isScaleVideoFlag()) {
            this.rccore.setScaleToFit(true, true, this.getShellInternalFrame().getSize());
        }
        if (bl) {
            this.setViewFocus();
        }
    }

    @Override
    public void setTargetScreenResolution(boolean bl) {
        if (this.port.isPrimaryPort()) {
            this.setTargetScreenResolution(bl, AbstractUIManager.FullScreenTarget.PRIMARY);
            for (RFBView rFBView : this.getSecondaryPortViews()) {
                rFBView.setTargetScreenResolution(bl, AbstractUIManager.FullScreenTarget.SECONDARY);
            }
        } else if (this.port.isSecondaryPort() && MonitorSettingsHandler.getInstance().getMonitorCount() > 1) {
            RFBView rFBView = this.getPrimaryPortView();
            rFBView.setTargetScreenResolution(bl);
        } else {
            this.setTargetScreenResolution(bl, AbstractUIManager.FullScreenTarget.SINGLE);
        }
    }

    @Override
    public void toggleTargetScreenResolution() {
        this.setTargetScreenResolution(!this.isFullScreenMode());
    }

    private void adjustMultiMonitorPortWindows() {
        Vector<RFBView> vector = new Vector<RFBView>();
        vector.add(this);
        vector.addAll(this.getSecondaryPortViews());
        if (vector.size() == 1) {
            return;
        }
        int n = 0;
        int n2 = 0;
        for (RFBView object : vector) {
            Point point = object.getPort().getMonitorLocation();
            if (point.x > n) {
                n = point.x;
            }
            if (point.y <= n2) continue;
            n2 = point.y;
        }
        Dimension dimension = this.getShellInternalFrame().getSize();
        dimension.width /= n + 1;
        dimension.height /= n2 + 1;
        for (RFBView rFBView : vector) {
            ShellInternalFrame shellInternalFrame = rFBView.getShellInternalFrame();
            Point point = rFBView.getPort().getMonitorLocation();
            try {
                shellInternalFrame.setMaximum(false);
                shellInternalFrame.setSize(dimension);
                shellInternalFrame.setLocation(point.x * dimension.width, point.y * dimension.height);
            }
            catch (PropertyVetoException propertyVetoException) {}
        }
    }

    @Override
    public void synchronizeMouse(boolean bl) {
        if (this.isConnected()) {
            try {
                this.rccore.syncMouse(RCCore.MouseSyncType.HARD);
            }
            catch (IOException iOException) {
                RRCLogger.log(150, "I/O Error performing hard mouse sync.", iOException);
            }
        }
    }

    public void doMouseSyncSoft() {
        if (this.isConnected()) {
            try {
                this.rccore.syncMouse(RCCore.MouseSyncType.NORM);
            }
            catch (IOException iOException) {
                RRCLogger.log(150, "I/O Error performing soft mouse sync.", iOException);
            }
        }
    }

    @Override
    public boolean isCommandOperable(AbstractCommand abstractCommand) {
        boolean bl = super.isCommandOperable(abstractCommand);
        if (abstractCommand != null) {
            Class<?> clazz = abstractCommand.getClass();
            if (DoEnterOnscreenMenuCommand.class.equals(clazz) || DoExitOnscreenMenuCommand.class.equals(clazz)) {
                bl = false;
            }
        } else {
            RRCLogger.log(300, "isCommandOperable: Command is null");
        }
        return bl;
    }

    @Override
    public void setViewFocus() {
        try {
            if (this.getShellInternalFrame().isSelected()) {
                RRCLogger.log(300, "Grabbing focus");
                this.console.requestFocus();
                this.console.requestFocusInWindow();
            }
        }
        catch (Exception exception) {
            RRCLogger.log(100, "Exception in RFBView.setViewFocus: ", exception);
        }
    }

    @Override
    public void customizeTarget(AbstractDisplay abstractDisplay) {
        if (abstractDisplay != null) {
            if (abstractDisplay.isDialog()) {
                if (OptionsPanel.class.equals(abstractDisplay.getClass())) {
                    OptionsPanel optionsPanel = (OptionsPanel)abstractDisplay;
                    optionsPanel.setEnabledKvmSwitchOSUIHotKey(false);
                } else if (PropertiesPanel.class.equals(abstractDisplay.getClass())) {
                    PropertiesPanel propertiesPanel = (PropertiesPanel)abstractDisplay;
                    propertiesPanel.setEnabledProgressiveUpdateChkBox(false);
                    propertiesPanel.setEnabledSmoothingSlider(false);
                    propertiesPanel.setEnabledInternetFlowControlChkBox(false);
                }
            }
        } else {
            RRCLogger.log(300, "customizeTarget: aDisplay is null");
        }
    }

    @Override
    public boolean macroMenuActionPerformed(String string) {
        RRCLogger.log(300, "Macro sequence: " + string);
        return this.sendKeyboardMacro(string);
    }

    @Override
    public void sendKeyboardMacroDirect(KeyboardMacro keyboardMacro) {
        this.sendKeyboardMacro(keyboardMacro);
    }

    @Override
    public boolean sendKeyboardMacro(String string) {
        if (string.length() < 3) {
            return false;
        }
        String[] stringArray = string.split("&&");
        Vector<KeyboardMacro.KeyCode> vector = new Vector<KeyboardMacro.KeyCode>();
        KeyboardMacro keyboardMacro = new KeyboardMacro("", "", null, false);
        block4: for (int i = 0; i < stringArray.length; ++i) {
            char c = stringArray[i].charAt(0);
            int n = Integer.parseInt(stringArray[i].substring(2));
            switch (c) {
                case 's': {
                    int n2 = this.macroHelper.getKeyMappings().getItem(n).getKeyCode();
                    KeyboardMacro keyboardMacro2 = keyboardMacro;
                    keyboardMacro2.getClass();
                    vector.add(new KeyboardMacro.KeyCode(keyboardMacro2, true, n2));
                    continue block4;
                }
                case 'p': 
                case 'r': {
                    int n3 = this.macroHelper.getKeyMappings().getItem(n).getKeyLocation();
                    Locale locale = Locale.US;
                    int n4 = ((RRCScreenContext)this.scrContext).getAppSettings().getKeyboardType();
                    if (n4 == 5 || n4 == 1) {
                        locale = KeyboardUtil.getLocale(n4);
                    }
                    n = this.macroHelper.getVKCode((short)n, locale);
                    if (this.isSunTarget && n == 3) {
                        n = 456;
                    }
                    char c2 = this.macroHelper.getKeyChar((short)n, locale);
                    n = this.rccore.getKeyCode(n, c2, n3);
                    KeyboardMacro keyboardMacro3 = keyboardMacro;
                    keyboardMacro3.getClass();
                    vector.add(new KeyboardMacro.KeyCode(keyboardMacro3, n, c == 'p'));
                }
            }
        }
        keyboardMacro = new KeyboardMacro("", "", vector.toArray(new KeyboardMacro.KeyCode[0]), false);
        return this.sendKeyboardMacro(keyboardMacro);
    }

    public boolean sendKeyboardMacro(KeyboardMacro keyboardMacro) {
        if (keyboardMacro.getConfirm() && JOptionPane.showOptionDialog(this, MessageFormat.format(T._("Do you really want to send {0}?"), keyboardMacro.getNameOrCode()), T._("Confirmation"), 0, 3, null, null, null) == 1) {
            return false;
        }
        this.rccore.sendKeyboardMacro(keyboardMacro, true);
        return true;
    }

    public void setMouseMode(RCCore.MouseMode mouseMode) {
        try {
            this.rccore.setMouseMode(mouseMode);
        }
        catch (IOException iOException) {
            RRCLogger.log(150, "IO Exception attempting to set mouse mode", iOException);
        }
    }

    public void setMouseModeMenuItem(RCCore.MouseMode mouseMode) {
        if (this.rccore.getMouseMode() != mouseMode) {
            this.setMouseMode(mouseMode);
        }
        if (mouseMode == RCCore.MouseMode.ABSOLUTE) {
            ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectAbsoluteMouseModeView(true);
            this.mouseMode = 0;
        } else if (mouseMode == RCCore.MouseMode.AUTOMATIC) {
            ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectIntelligentMouseModeView(true);
            this.mouseMode = 1;
        } else {
            ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectStandardMouseModeView(true);
            this.mouseMode = 2;
        }
        MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, this.kvmPort);
    }

    public void setMouseAbsolute() {
        this.setMouseMode(RCCore.MouseMode.ABSOLUTE);
        this.mouseMode = 0;
    }

    public void setMouseIntelligent() {
        this.setMouseMode(RCCore.MouseMode.AUTOMATIC);
        this.mouseMode = 1;
    }

    public void setMouseStandard() {
        this.setMouseMode(RCCore.MouseMode.STANDARD);
        this.mouseMode = 2;
    }

    @Override
    public void setSingleCursor(boolean bl) {
        this.setFrameSize();
        this.setSingleCursorMode(bl);
        this.rccore.setSingleCursorMode(bl);
        this.rccore.setCaptureRightAway(bl);
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        FullScreenToolBar fullScreenToolBar = rRCScreenContext.getApplication().getFSToolBar(this.getShellInternalFrame());
        if (fullScreenToolBar != null) {
            fullScreenToolBar.setLabelText();
            fullScreenToolBar.setToolBarVisible(true);
        }
        if (bl) {
            RRCScreenContext cfr_ignored_0 = (RRCScreenContext)this.scrContext;
            String string = this.bundle.getString("SingleMouseModeInstructions3.text") + RRCScreenContext.getKvmPopupKey();
            this.setStatusMessage(string + ", " + this.bundle.getString("FullScreen.exitSMM"));
            this.shellInternalFrame.setTitle(MessageFormat.format(this.bundle.getString("SingleMouseModeInstructions6.text"), this.kvmPort.getViewName(), string + this.bundle.getString("SingleMouseModeInstructions5.text")));
        } else {
            this.setStatusMessage(this.bundle.getString("Status.rrcLoaded"));
            this.shellInternalFrame.setTitle(this.kvmPort.getViewName());
            for (Port port : this.getPort().getAssociatedPorts()) {
                if (!((KvmPort)port).isSingleCursorMode()) continue;
                ((KvmPort)port).toggleSingleMouseCursor();
            }
        }
    }

    public int getMouseMode() {
        RCCore.MouseMode mouseMode = RCCore.MouseMode.STANDARD;
        if (this.rccore != null) {
            mouseMode = this.rccore.getMouseMode();
        }
        if (mouseMode == RCCore.MouseMode.ABSOLUTE) {
            return 0;
        }
        if (mouseMode == RCCore.MouseMode.AUTOMATIC) {
            return 1;
        }
        return 2;
    }

    @Override
    public ICommandHandler getCommandHandler() {
        return this.rFBViewCommandHandler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void switchKvmPort(KvmPort kvmPort, KvmPort kvmPort2) {
        RFBView rFBView = this;
        synchronized (rFBView) {
            try {
                if (this.fullyConnected && this.switchPortQueue.isEmpty()) {
                    Object object;
                    RRCLogger.log(300, "switchKvmPort Started");
                    RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
                    this.closeOpenVMConnections(true);
                    this.disconnectSmartCard(this.kvmPort);
                    this.disconnectAudio(this.kvmPort);
                    this.disconnectAssociatedPorts();
                    KvmPort kvmPort3 = this.kvmPort;
                    kvmPort3.setState("AVAILABLE");
                    kvmPort3.setConnected(false);
                    kvmPort3.setActive(false);
                    kvmPort3.setLicenseSupport(null);
                    kvmPort3.setAudioEx(null);
                    if (kvmPort3.isBladePort() && (object = kvmPort3.getParentBladeChassis()) != null) {
                        ((Device)object).setState("AVAILABLE");
                    }
                    rRCScreenContext.removePortInObservable(kvmPort3);
                    if (kvmPort3.equals(((IPReach)kvmPort3.getBaseDevice()).getActiveKvmPort())) {
                        ((IPReach)kvmPort3.getBaseDevice()).setActiveKvmPort(null);
                    }
                    this.kvmPort = kvmPort2;
                    this.port = kvmPort2;
                    RRCLogger.log(300, "Switching to " + kvmPort2.getPortIndex());
                    this.rccore.switchKvmPort(this.port.getPortIndex(), this.port.getUniquePortId());
                    this.kvmPort.setView(this);
                    this.kvmPort.setDevView(this);
                    this.kvmPort.setLicenseSupport(this.license);
                    this.kvmPort.setAudioEx(this.audioEx);
                    this.kvmPort.setActive(true);
                    this.kvmPort.setContext(this.scrContext);
                    ((IPReach)this.kvmPort.getBaseDevice()).setActiveKvmPort(this.kvmPort);
                    this.setPortState();
                    rRCScreenContext.addPortInObservable(this.kvmPort);
                    this.shellInternalFrame.setTitle(this.kvmPort.getViewName());
                    object = new DoDisconnectCommand(this.scrContext);
                    SelectPortViewCommand selectPortViewCommand = new SelectPortViewCommand(this.scrContext);
                    DeSelectPortViewCommand deSelectPortViewCommand = new DeSelectPortViewCommand(this.scrContext);
                    selectPortViewCommand.getContext().setCommandParameter("selectedPortDevice", this.kvmPort);
                    ((AbstractCommand)object).getContext().setCommandParameter("selectedPortDevice", this.kvmPort);
                    ((RRCShellInternalFrame)this.shellInternalFrame).setFrameClosedCommand((Command)object);
                    ((RRCShellInternalFrame)this.shellInternalFrame).setFrameActivatedCommand(selectPortViewCommand);
                    ((RRCShellInternalFrame)this.shellInternalFrame).setFrameDeActivatedCommand(deSelectPortViewCommand);
                    ((RRCShellInternalFrame)this.shellInternalFrame).setDeviceView(this);
                    G2CompressionPanel.resetPanel(this.kvmPort.getPortKey());
                    RRCLogger.log(300, "switchKvmPort Finished");
                    try {
                        this.shellInternalFrame.setMaximum(true);
                    }
                    catch (PropertyVetoException propertyVetoException) {}
                } else {
                    this.switchPortQueue.addLast(kvmPort2);
                }
            }
            catch (RCException rCException) {
                RRCLogger.getLogger().log(Level.WARNING, "Could not switch to KvmPort: " + kvmPort2.getPortIndex(), rCException);
            }
            catch (IOException iOException) {
                RRCLogger.getLogger().log(Level.WARNING, "Could not switch to KvmPort: " + kvmPort2.getPortIndex(), iOException);
            }
        }
    }

    @Override
    public void refreshScreen() {
        try {
            this.rccore.requestVideoRefresh();
        }
        catch (IOException iOException) {
            RRCLogger.log(100, "I/O Error requesting Video refresh.", iOException);
        }
    }

    @Override
    public void calibrateColor() {
        try {
            this.rccore.requestVideoColorCalibration();
        }
        catch (IOException iOException) {
            RRCLogger.log(100, "I/O Error requesting Color Calibration.", iOException);
        }
    }

    @Override
    public void autoSenseVideo() {
        try {
            this.rccore.requestVideoAutoSense();
        }
        catch (IOException iOException) {
            RRCLogger.log(100, "I/O Error requesting Auto Sense.", iOException);
        }
    }

    public long getInData() {
        return this.rccore.getIncomingTrafficSpeed();
    }

    public long getOutData() {
        return this.rccore.getOutgoingTrafficSpeed();
    }

    public int getFpsCount() {
        return this.rccore.getFramesPerSecond();
    }

    public Dimension getScreenSize() {
        return this.console.getSize();
    }

    public void writeVideoSettings(IVideoSettings iVideoSettings) {
        try {
            this.rccore.setVideoSettings(iVideoSettings);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void saveVideoSettings() {
        try {
            this.rccore.saveVideoSettings();
        }
        catch (IOException iOException) {
            RRCLogger.log(150, "IO Exception trying to save Video Settings", iOException);
        }
    }

    public void resetCurrentVideoSettings() {
        try {
            this.rccore.cancelVideoSettings();
            this.rccore.stopVideoSettingsUpdates();
        }
        catch (IOException iOException) {
            RRCLogger.log(150, "IO Exception trying to remove Video Settings Updates during GUI disposal.", iOException);
        }
    }

    public void requestVideoSettings() {
        try {
            this.rccore.requestVideoSettingsUpdates();
        }
        catch (IOException iOException) {
            RRCLogger.log(150, "Unable to request Video Settings Updates");
        }
    }

    public IVideoSettings getVideoSettings() {
        return this.vidSettings;
    }

    @Override
    public void processComponentEvent(ComponentEvent componentEvent) {
        if (this.console != null) {
            // empty if block
        }
    }

    private void adjustScrollBars() {
        if (this.scrollBothDirections) {
            this.scrollVertical();
            this.scrollHorizontal();
        } else if (this.adjustingVertical) {
            this.scrollVertical();
        } else {
            this.scrollHorizontal();
        }
    }

    private void scrollVertical() {
        int n = this.vScrollBar.getValue();
        if ((n += this.adjustmentValue * 10) < 0) {
            n = 0;
        }
        if (n < this.vScrollBar.getMaximum()) {
            this.vScrollBar.setValue(n);
        } else {
            this.vScrollBar.setValue(this.vScrollBar.getMaximum());
        }
    }

    private void scrollHorizontal() {
        int n = this.hScrollBar.getValue();
        if ((n += this.adjustmentValue * 10) < 0) {
            n = 0;
        }
        if (n < this.hScrollBar.getMaximum()) {
            this.hScrollBar.setValue(n);
        } else {
            this.hScrollBar.setValue(this.hScrollBar.getMaximum());
        }
    }

    public void showScrollBars() {
        JScrollPane jScrollPane = this.jsp;
        JComponent jComponent = this.console;
        if (jScrollPane != null && jComponent != null) {
            boolean bl;
            boolean bl2 = bl = jScrollPane.getSize().width < jComponent.getSize().width;
            if (this.isTargetScreenResolution()) {
                bl = jScrollPane.getViewport().getSize().width + this.vScrollBar.getSize().width < jComponent.getSize().width;
            }
            bl = bl && !this.isScaleVideoFlag();
            this.showScrollBorder = ((RRCScreenContext)this.scrContext).getAppSettings().isShowScrollBorders();
            this.showScrollBorder = this.showScrollBorder && !this.isTargetScreenResolution();
            this.hScrollBar.setVisible(bl);
            this.eastLabel.setVisible(bl && this.showScrollBorder);
            this.westLabel.setVisible(bl && this.showScrollBorder);
            boolean bl3 = bl = jScrollPane.getSize().height < jComponent.getSize().height;
            if (this.isTargetScreenResolution()) {
                bl = jScrollPane.getViewport().getSize().height + this.hScrollBar.getSize().height < jComponent.getSize().height;
            }
            bl = bl && !this.isScaleVideoFlag();
            this.vScrollBar.setVisible(bl);
            this.northLabel.setVisible(bl && this.showScrollBorder);
            this.northWestLabel.setVisible(bl && this.showScrollBorder);
            this.southLabel.setVisible(bl && this.showScrollBorder);
            this.southEastLabel.setVisible(bl && this.showScrollBorder);
            if (this.hScrollBar.isVisible()) {
                this.hScrollBar.setMaximum(jComponent.getSize().width - jScrollPane.getViewport().getSize().width + RFBView.VIEW_BORER_DIM.width);
            }
            if (this.vScrollBar.isVisible()) {
                this.vScrollBar.setMaximum(jComponent.getSize().height - jScrollPane.getViewport().getSize().height + RFBView.VIEW_BORER_DIM.height);
            }
        }
    }

    public void setCapsLockStatus(boolean bl) {
        this.setCapsLockOn(bl);
        ((RRCStatusBar)((RRCScreenContext)this.kvmPort.getContext()).getPanelMediator().getStatusPanel()).setCapsLockStatus(bl);
    }

    public void setNumLockStatus(boolean bl) {
        this.setNumLockOn(bl);
        ((RRCStatusBar)((RRCScreenContext)this.kvmPort.getContext()).getPanelMediator().getStatusPanel()).setNumLockStatus(bl);
    }

    public void setScrollLockStatus(boolean bl) {
        this.setScrollLockOn(bl);
        ((RRCStatusBar)((RRCScreenContext)this.kvmPort.getContext()).getPanelMediator().getStatusPanel()).setScrollsLockStatus(bl);
    }

    public void setConcurrentUsers(int n) {
        this.kvmPort.setConcurrUsers(n);
        ((RRCStatusBar)((RRCScreenContext)this.kvmPort.getContext()).getPanelMediator().getStatusPanel()).setConcurrentUsers(n);
    }

    public void setKvmPort(IKvmPort iKvmPort) {
        this.kvmPort.getPortPermissionHelper().setKvmPermission(new KvmPortPermissionHelper.KvmPermission(iKvmPort.getKvmPermission(), false));
        this.kvmPort.getPortPermissionHelper().setVmPermission(new KvmPortPermissionHelper.VmPermission(iKvmPort.getVmPermission(), false));
    }

    public Dimension getViewportSize() {
        JScrollPane jScrollPane = this.jsp;
        if (jScrollPane != null) {
            return jScrollPane.getViewport().getSize();
        }
        return new Dimension();
    }

    public String getCurrentMouseMode() {
        return null;
    }

    public void setCurrentMouseMode(String string) {
    }

    @Override
    public void setIsAbsoluteMouseSupported(boolean bl) {
        this.isAbsoluteMouseSupported = bl;
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        MPCUtil.notifyObservers(rRCScreenContext, this.kvmPort);
        rRCScreenContext.getMainScreenMediator().enableAbsoluteMouseModeView(this.isAbsoluteMouseSupported);
        RRCLogger.log(300, "Absolute Mouse Supported: " + bl);
    }

    @Override
    public void setIsIntelligentMouseSupported(boolean bl) {
        this.isIntelligentMouseSupported = bl;
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        MPCUtil.notifyObservers(rRCScreenContext, this.kvmPort);
        rRCScreenContext.getMainScreenMediator().enableIntelligentMouseModeView(this.isIntelligentMouseSupported);
        RRCLogger.log(300, "Intelligent Mouse Supported: " + bl);
    }

    @Override
    public void setIsStandardMouseSupported(boolean bl) {
        this.isStandardMouseSupported = bl;
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        MPCUtil.notifyObservers(rRCScreenContext, this.kvmPort);
        rRCScreenContext.getMainScreenMediator().enableStandardMouseModeView(this.isStandardMouseSupported);
        RRCLogger.log(300, "Standard Mouse Supported: " + bl);
    }

    public void setMouseMode(int n) {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        MPCUtil.notifyObservers(rRCScreenContext, this.kvmPort);
        this.refreshMouseMenu();
        switch (n) {
            case 0: {
                rRCScreenContext.getMainScreenMediator().selectAbsoluteMouseModeView(this.isAbsoluteMouseSupported);
                RRCLogger.log(300, "Setting mouse mode to MOUSE_MODE_ABSOLUTE: " + n);
                break;
            }
            case 1: {
                rRCScreenContext.getMainScreenMediator().selectIntelligentMouseModeView(this.isIntelligentMouseSupported);
                RRCLogger.log(300, "Setting mouse mode to MOUSE_MODE_INTELLI: " + n);
                break;
            }
            case 2: {
                rRCScreenContext.getMainScreenMediator().selectStandardMouseModeView(this.isStandardMouseSupported);
                RRCLogger.log(300, "Setting mouse mode to MOUSE_MODE_STANDARD: " + n);
                break;
            }
            default: {
                RRCLogger.log(300, "Unknown mouse mode: " + n);
            }
        }
    }

    public void refreshMouseMenu() {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        rRCScreenContext.getMainScreenMediator().enableAbsoluteMouseModeView(this.isAbsoluteMouseSupported);
        rRCScreenContext.getMainScreenMediator().enableIntelligentMouseModeView(this.isIntelligentMouseSupported);
        rRCScreenContext.getMainScreenMediator().enableStandardMouseModeView(this.isStandardMouseSupported);
    }

    public void handleFocus(Component component) {
        component.requestFocusInWindow();
    }

    @Override
    protected void processEvent(AWTEvent aWTEvent) {
        super.processEvent(aWTEvent);
    }

    @Override
    public boolean isTargetScreenResolution() {
        return this.isFullScreenMode();
    }

    public boolean isContextMenuKVMVisible() {
        return this.contextMenuKvm.isVisible();
    }

    @Override
    public void setContextMenuKVMVisible(boolean bl) {
        Object object;
        if (!bl) {
            this.getContextPopupMenu().setVisible(bl);
            return;
        }
        String string = "";
        string = this.port.getViewName() + this.port.getPortIndex();
        Map map = this.scrContext.getVirtualMediaLocalMap();
        Map map2 = this.scrContext.getVirtualMediaImageMap();
        if (map != null && map.size() > 0) {
            object = (VirtualMediaLocalBean)map.get(string);
            if (object != null && ((VirtualMediaLocalBean)object).isDriveConnected()) {
                this.getContextPopupMenu().getVirtualMediaLocalMenu().setText(this.bundle.getString("VirtualMediaDisconnect") + " " + ((VirtualMediaLocalBean)object).getConnectedDrive());
            } else {
                this.getContextPopupMenu().getVirtualMediaLocalMenu().setText(this.bundle.getString("VirtualMediaLocalConnect.name"));
            }
        } else {
            this.getContextPopupMenu().getVirtualMediaLocalMenu().setText(this.bundle.getString("VirtualMediaLocalConnect.name"));
        }
        this.getContextPopupMenu().getVirtualMediaLocalMenu().setToolTipText(((ShowVirtualMediaLocalPanelCommand)this.getContextPopupMenu().getVirtualMediaLocalMenu().getCommand()).getToolTip());
        if (map2 != null && map2.size() > 0) {
            object = (VirtualMediaBean)map2.get(string);
            if (object != null && ((VirtualMediaBean)object).isDriveConnected()) {
                this.getContextPopupMenu().getVirtualMediaImageMenu().setText(this.bundle.getString("VirtualMediaDisconnect") + " " + ((VirtualMediaBean)object).getConnectedDrive());
            } else {
                this.getContextPopupMenu().getVirtualMediaImageMenu().setText(this.bundle.getString("VirtualMediaImageConnect.name"));
            }
        } else {
            this.getContextPopupMenu().getVirtualMediaImageMenu().setText(this.bundle.getString("VirtualMediaImageConnect.name"));
        }
        this.getContextPopupMenu().getVirtualMediaImageMenu().setToolTipText(((ShowVirtualMediaImagePanelCommand)this.getContextPopupMenu().getVirtualMediaImageMenu().getCommand()).getToolTip());
        object = (ConnectAudioCommand)this.getContextPopupMenu().getConnectAudioMenu().getCommand();
        if (((ConnectAudioCommand)object).isExecutable()) {
            this.getContextPopupMenu().getConnectAudioMenu().setToolTipText(((ConnectAudioCommand)object).isConnected() ? this.bundle.getString("Audio.disconnectAudio") : this.bundle.getString("Audio.connectAudio"));
        } else {
            this.getContextPopupMenu().getConnectAudioMenu().setToolTipText(((AudioMenuCommand)object).getToolTip());
        }
        this.getContextPopupMenu().getConnectAudioMenu().setText(((ConnectAudioCommand)object).isConnected() ? this.bundle.getString("Audio.disconnectAudio") : this.bundle.getString("Audio.connectAudio"));
        CommandMenuItem commandMenuItem = this.getContextPopupMenu().getVirtualMediaLocalMenu();
        commandMenuItem.setEnabled(commandMenuItem.getCommand().isExecutable());
        commandMenuItem = this.getContextPopupMenu().getVirtualMediaImageMenu();
        commandMenuItem.setEnabled(commandMenuItem.getCommand().isExecutable());
        this.getContextPopupMenu().show(this, 10, 10);
    }

    @Override
    public ContextPopupMenu getContextPopupMenu() {
        return this.contextMenuKvm;
    }

    @Override
    public boolean isScaleVideoFlag() {
        return this.scaleVideoFlag;
    }

    @Override
    public boolean isSingleCursor() {
        return this.singleCursorMode;
    }

    @Override
    public void setScaleVideoFlag(boolean bl) {
        this.scaleVideoFlag = bl;
        if (this.vScrollBar.isVisible()) {
            this.vScrollBar.setValue(0);
        }
        if (this.hScrollBar.isVisible()) {
            this.hScrollBar.setValue(0);
        }
        this.rccore.setScaleToFit(bl, true, this.getShellInternalFrame().getSize());
        this.rccore.setInterpolation(bl ? RCCore.Interpolation.FAST : RCCore.Interpolation.NONE);
        if (this.scaleVideoFlag) {
            this.removeAll();
            this.add((Component)this.console, "Center");
            this.validate();
        } else {
            this.remove(this.console);
            this.add((Component)this.getConsolePanel(), "Center");
            this.add((Component)this.hScrollBar, "South");
            this.add((Component)this.vScrollBar, "East");
            this.validate();
        }
        this.showScrollBars();
    }

    public void setSmoothing(int n) {
    }

    public int getFullScreenExitKeycode() {
        return -1;
    }

    public void setVMMountsResponse(VMMountRequestResponse vMMountRequestResponse) {
        this.vmMountsResponse = vMMountRequestResponse;
        this.processVMMountResponse(this.vmMountsResponse);
    }

    public VMMountRequestResponse getVMMountsResponse() {
        return this.vmMountsResponse;
    }

    public void setVMShareTable(VMMountRequestResponse[] vMMountRequestResponseArray) {
        this.vmShareTable = vMMountRequestResponseArray;
    }

    public VMMountRequestResponse[] getVMShareTable() {
        return this.vmShareTable;
    }

    public void processVMMountResponse(IVMMountRequestResponse iVMMountRequestResponse) {
        try {
            RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
            int n = iVMMountRequestResponse.getRetCode();
            RfbNotificationEvent rfbNotificationEvent = new RfbNotificationEvent(0, n);
            if (rfbNotificationEvent.isInfo() || rfbNotificationEvent.isWarning()) {
                if (iVMMountRequestResponse.getOption() == 1) {
                    int n2 = 1;
                    if (rfbNotificationEvent.isWarning()) {
                        n2 = 2;
                    }
                    this.setSambaConnectSuccessful(true);
                    if (this.vmImagePanel != null) {
                        this.vmImagePanel.hideDialogAfterSambaResponse(n2);
                        RRCLogger.log(200, "Pre-configured Image Mount Connect Successful for Port: " + this.port);
                    } else {
                        RRCLogger.log(300, "VMImagePanel is NULL. Cannot Hide Dialog.");
                    }
                } else if (iVMMountRequestResponse.getOption() == 0) {
                    this.setSambaDisconnectSuccessful(true);
                    if (this.vmImagePanel != null) {
                        this.vmImagePanel.hideDialogAfterSambaResponse(0);
                        RRCLogger.log(200, "Pre-configured Image Mount Disconnect Successful for Port: " + this.port);
                    } else {
                        RRCLogger.log(300, "VMImagePanel is NULL. Cannot Hide Dialog.");
                    }
                } else {
                    RRCLogger.log(150, "Unknown Option from Server for Remote ISO Mount.");
                }
            }
            if (rfbNotificationEvent.isError()) {
                if (this.vmImagePanel instanceof VirtualMediaImagePanel) {
                    this.vmImagePanel.sambaMountError();
                }
                RRCLogger.log(150, "Error Occured during Remote ISO Image Mount. Error Code:" + rfbNotificationEvent.getMessageGroupErrorCode() + " for Port:" + this.getPortIndex());
                ErrorHandlerImpl.getInstance().handleError(n, this.vmImagePanel, this);
            }
        }
        catch (Exception exception) {
            RRCLogger.log(150, "Exception during VM Mount: ", exception);
        }
    }

    private void showSingleMouseAfterConnected() {
        Object object;
        IApplicationPreferences iApplicationPreferences = ((RRCScreenContext)this.scrContext).getAppSettings();
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        if (iApplicationPreferences.isAlwaysOpenSMM() && (object = rRCScreenContext.getSelectedPort()) != null && ((Device)object).isConnected() && object instanceof KvmPort && !this.port.isMultiMonitorPort()) {
            ((KvmPort)object).toggleSingleMouseCursor();
            rRCScreenContext.getMainScreenMediator().selectSingleMouseCursorMode();
            FullScreenToolBar jComponent = rRCScreenContext.getApplication().getFSToolBar(this.getShellInternalFrame());
            if (jComponent != null) {
                jComponent.setLabelText();
                jComponent.setToolBarVisible(true);
            }
        }
        if (iApplicationPreferences.isAlwaysOpenScaled()) {
            this.setScaleVideoFlag(true);
            for (CommandCheckMenuItem commandCheckMenuItem : rRCScreenContext.getMainScreenMediator().getCheckScaleVideoMenuItems()) {
                commandCheckMenuItem.setSelected(true);
            }
            rRCScreenContext.getMainScreenMediator().getToolBarScaleVideoButton().setSelected(true);
        }
    }

    public void notify(final INotificationEvent iNotificationEvent) {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        if (iNotificationEvent.getErrorCode() == 302120971) {
            String string;
            DevicePrefs devicePrefs;
            this.kvmPort.setRfbSwitchCompleted(true);
            this.showSingleMouseAfterConnected();
            RemoteConsoleParameters remoteConsoleParameters = this.rcparams;
            if (remoteConsoleParameters != null && (devicePrefs = DevicePrefs.getNode(remoteConsoleParameters.host)) != null && (string = devicePrefs.getCardReaderName()) != null) {
                this.startAutoMountingCardReader(string);
            }
        } else if (iNotificationEvent.isQuit()) {
            this.setQuitEvent(true);
            if (iNotificationEvent.isInfo()) {
                ErrorHandlerImpl.getInstance().handleInfo(iNotificationEvent.getErrorCode(), rRCScreenContext.getApplication().getContentPane(), this);
            } else {
                ErrorHandlerImpl.getInstance().handleError(iNotificationEvent.getErrorCode(), rRCScreenContext.getApplication().getContentPane(), this);
            }
            this.close();
            try {
                this.closeSelf();
            }
            catch (PropertyVetoException propertyVetoException) {}
        } else if (iNotificationEvent.isError()) {
            if (!this.viewFinalized) {
                this.collectedNotifications.add(iNotificationEvent);
            } else {
                ErrorHandlerImpl.getInstance().handleError(iNotificationEvent.getErrorCode(), rRCScreenContext.getApplication().getContentPane(), this);
            }
        } else if (iNotificationEvent.isWarning()) {
            if (!this.viewFinalized) {
                this.collectedNotifications.add(iNotificationEvent);
            } else {
                SwingUtilities.invokeLater(new Runnable(){
                    RRCScreenContext rrcScreenContext;
                    {
                        this.rrcScreenContext = (RRCScreenContext)RFBView.this.scrContext;
                    }

                    @Override
                    public void run() {
                        ErrorHandlerImpl.getInstance().handleWarning(iNotificationEvent.getErrorCode(), this.rrcScreenContext.getApplication().getContentPane(), RFBView.this);
                    }
                });
            }
        } else if (iNotificationEvent.isInfo() && iNotificationEvent.isRFB()) {
            switch (iNotificationEvent.getErrorCode()) {
                case 302055433: 
                case 302055434: 
                case 302055435: 
                case 302055436: {
                    this.vmImagePanel.disconnected(true);
                }
            }
        }
        RRCLogger.log(200, iNotificationEvent.toString());
    }

    public String[] getTokens(int[] nArray) {
        KvmPort kvmPort = this.kvmPort;
        if (kvmPort != null) {
            assert (nArray.length > 0);
            if (nArray[0] == 285671426) {
                USBProfilesInfo uSBProfilesInfo = kvmPort.getUsbProfilesInfo();
                USBProfile uSBProfile = uSBProfilesInfo.getActiveUSBProfile();
                USBProfile uSBProfile2 = uSBProfilesInfo.getPreferredUSBProfile();
                return new String[]{uSBProfile.getProfileName(), uSBProfile2.getProfileName()};
            }
            return new String[]{this.kvmPort.getName()};
        }
        return null;
    }

    private void handleResizing() {
        if (this.isScaleVideoFlag()) {
            this.rccore.setScaleToFit(true, true, this.getSize());
        }
    }

    private void startAutoMountingCardReader(String string) {
        assert (SwingUtilities.isEventDispatchThread());
        KvmPort kvmPort = this.kvmPort;
        if (kvmPort != null) {
            if (kvmPort.getSelectCardReaderAction().isEnabled()) {
                SmartCardBean smartCardBean = kvmPort.getSelectCardReaderAction().getConfiguredSmartCardBean();
                this.scrContext.getPanelMediator().showPanel(new SmartCardAutoMountCommand(this.scrContext, smartCardBean, string, T._("SmartCard")).getContext());
            } else {
                RRCLogger.log(300, "Not performing AUTO_MOUNT of SmartCard. Reason is - " + kvmPort.getSmartCardAction().getValue("ShortDescription"));
            }
        }
    }

    public boolean isSambaConnectSuccessful() {
        return this.isSambaConnectSuccessful;
    }

    public void setSambaConnectSuccessful(boolean bl) {
        this.isSambaConnectSuccessful = bl;
    }

    public boolean isSambaDisconnectSuccessful() {
        return this.isSambaDisconnectSuccessful;
    }

    public void setSambaDisconnectSuccessful(boolean bl) {
        this.isSambaDisconnectSuccessful = bl;
    }

    public void toggleTraffic() {
    }

    public void sessionTerminated() {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        ErrorHandlerImpl.getInstance().handleError("RFB_PROTO_ERR.SESSION.TERMINATED", (Component)rRCScreenContext.getApplication().getContentPane());
    }

    public void setCapabilityMap(HashMap hashMap) {
        this.capabilityMap = hashMap;
    }

    public HashMap getCapabilityMap() {
        return this.capabilityMap;
    }

    public String getDeviceName() {
        return null;
    }

    public void setDeviceName(String string) {
    }

    public boolean hasUserClosedTheWindow() {
        return this.shellInternalFrame.isWindowClosing();
    }

    public void setFrameSize() {
        if (this.rccore != null) {
            this.rccore.setClientFrameSizeChanged(this.getSize());
        }
    }

    protected void finalize() throws Throwable {
        RRCLogger.log(300, "Finalizing " + this.getPortIndex());
        super.finalize();
    }

    public String getPortIndex() {
        return this.kvmPort == null ? "" : Integer.toString(this.kvmPort.getPortIndex());
    }

    public String getPortName() {
        return this.kvmPort == null || this.kvmPort.getDescription() == null ? "" : this.kvmPort.getDescription();
    }

    public String getPortUniqueId() {
        return this.kvmPort == null || this.kvmPort.getStripTargetDeviceId() == null ? "" : this.kvmPort.getStripTargetDeviceId();
    }

    public void setVMImagePanel(VirtualMediaImagePanel virtualMediaImagePanel) {
        this.vmImagePanel = virtualMediaImagePanel;
    }

    public VirtualMediaImagePanel getVMImagePanel() {
        return this.vmImagePanel;
    }

    public void setVMLocalPanel(VirtualMediaLocalPanel virtualMediaLocalPanel) {
        this.vmLocalPanel = virtualMediaLocalPanel;
    }

    public VirtualMediaLocalPanel getVMLocalPanel() {
        return this.vmLocalPanel;
    }

    public void setCCVMPermissions(String string) {
        this.ccVMPermissions = string;
    }

    public String getCCVMPermissions() {
        return this.ccVMPermissions;
    }

    public void disableVMMenu() {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        if (rRCScreenContext != null) {
            for (CommandMenu commandMenu : rRCScreenContext.getMainScreenMediator().getVirtualMediaMenus()) {
                commandMenu.removeObservable(rRCScreenContext.getSelectedDevicesObservable());
            }
        }
        if (this.contextMenuKvm != null) {
            this.getContextPopupMenu().getVirtualMediaLocalMenu().removeObservable(rRCScreenContext.getSelectedDevicesObservable());
            this.getContextPopupMenu().getVirtualMediaImageMenu().removeObservable(rRCScreenContext.getSelectedDevicesObservable());
        }
    }

    public void setTrafficData(int n, int n2, int n3) {
        this.dataIn = n;
        this.dataOut = n2;
        this.fps = n3;
    }

    public synchronized boolean isVMLocalConnectionActive() {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        KvmPort kvmPort = this.kvmPort;
        if (rRCScreenContext != null && kvmPort != null) {
            VirtualMediaLocalBean virtualMediaLocalBean;
            String string = kvmPort.getViewName() + kvmPort.getPortIndex();
            Map map = rRCScreenContext.getVirtualMediaLocalMap();
            if (map != null && (virtualMediaLocalBean = (VirtualMediaLocalBean)map.get(string)) != null && virtualMediaLocalBean.isDriveConnected()) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isVMImageConnectionActive() {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        KvmPort kvmPort = this.kvmPort;
        if (rRCScreenContext != null && kvmPort != null) {
            VirtualMediaBean virtualMediaBean;
            String string = kvmPort.getViewName() + kvmPort.getPortIndex();
            Map map = rRCScreenContext.getVirtualMediaImageMap();
            if (map != null && (virtualMediaBean = (VirtualMediaBean)map.get(string)) != null && virtualMediaBean.isDriveConnected()) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isVMConnectionActive() {
        return this.isVMLocalConnectionActive() || this.isVMImageConnectionActive();
    }

    public void disconnectSmartCard(final KvmPort kvmPort) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.disconnectSmartCardonEDT(kvmPort);
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    RFBView.this.disconnectSmartCardonEDT(kvmPort);
                }
            });
        }
    }

    private void disconnectSmartCardonEDT(KvmPort kvmPort) {
        if (kvmPort != null) {
            kvmPort.setRfbSwitchCompleted(false);
            kvmPort.getSmartCardAction().setEnabled(false);
            kvmPort.getSelectCardReaderAction().setEnabled(false);
            SmartCardBean smartCardBean = kvmPort.getSelectCardReaderAction().getSmartCardBean();
            if (smartCardBean != null) {
                smartCardBean.quitSession();
                kvmPort.getSelectCardReaderAction().resetSmartCardBean();
            }
        }
    }

    public void disconnectAudio(final KvmPort kvmPort) {
        if (SwingUtilities.isEventDispatchThread()) {
            if (kvmPort != null) {
                kvmPort.getConnectAudioAction().disconnectAudio();
            }
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (kvmPort != null) {
                        kvmPort.getConnectAudioAction().disconnectAudio();
                    }
                }
            });
        }
    }

    public synchronized void closeOpenVMConnections(boolean bl) {
        block25: {
            try {
                String string = "";
                if ((RRCScreenContext)this.scrContext != null) {
                    if (this.kvmPort != null) {
                        Object object;
                        Object object2;
                        string = this.kvmPort.getViewName() + this.kvmPort.getPortIndex();
                        Map map = this.scrContext.getVirtualMediaLocalMap();
                        Map map2 = this.scrContext.getVirtualMediaImageMap();
                        if (map != null && map.size() > 0) {
                            object2 = (VirtualMediaLocalBean)map.get(string);
                            if (object2 != null) {
                                if (bl) {
                                    ((VirtualMediaLocalBean)object2).setSwitchedFlag(true);
                                }
                                if (((VirtualMediaLocalBean)object2).isDriveConnected()) {
                                    RRCLogger.log(300, "Disconnecting Drive 1 Virtual Media Connection for port:" + this.port.getPortIndex());
                                    ((VirtualMediaLocalBean)object2).getVmCore().disconnect(true);
                                    ((VirtualMediaLocalBean)object2).setDriveConnected(false);
                                    object = ((VirtualMediaLocalBean)object2).getConnectedDrive();
                                    if (this.scrContext.getSelectedDrives().contains(object)) {
                                        this.scrContext.removeSelectedDrive((String)object);
                                        RRCLogger.log(300, -1, "Removing drive from ArrayList:" + (String)object);
                                    }
                                    RRCLogger.log(300, "Disconnected handlers for Drive 1 Virtual Media Connection for port:" + this.port.getPortIndex());
                                }
                            } else {
                                RRCLogger.log(300, "Not disconnecting Drive 1 VM connection. No VMBean for " + string);
                            }
                            map.remove(string);
                        } else {
                            RRCLogger.log(300, "VMLocalMap is empty. Not disconnecting Drive 1 VM connection for " + string);
                        }
                        if (map2 != null && map2.size() > 0) {
                            object2 = (VirtualMediaBean)map2.get(string);
                            if (object2 != null) {
                                if (bl) {
                                    ((VirtualMediaBean)object2).setSwitchedFlag(true);
                                }
                                if (((VirtualMediaBean)object2).isDriveConnected()) {
                                    RRCLogger.log(300, "Disconnecting Drive 2 Virtual Media Connection for port:" + this.port.getPortIndex());
                                    if (((VirtualMediaBean)object2).getConnectionType() == 3) {
                                        object = ((VirtualMediaBean)object2).getSambaRequestDrive1();
                                        if (object != null) {
                                            ((VMMountRequestResponse)object).setOption(0);
                                            try {
                                                this.rccore.mountOrUnmountRemoteIso((IVMMountRequestResponse)object);
                                                RRCLogger.log(300, "Sending Remote ISO mount Disconnect request :" + ((VMMountRequestResponse)object).toString());
                                            }
                                            catch (IOException iOException) {
                                                RRCLogger.log(150, "Error Disconnecting Samba Mount for port " + string, iOException);
                                            }
                                            if (this.isSambaDisconnectSuccessful()) {
                                                ((VirtualMediaBean)object2).setDriveConnected(false);
                                                this.setSambaDisconnectSuccessful(false);
                                                ((VirtualMediaBean)object2).setConnectionType(0);
                                            }
                                        }
                                    } else {
                                        ((VirtualMediaBean)object2).getVmCore().disconnect(true);
                                    }
                                    ((VirtualMediaBean)object2).setDriveConnected(false);
                                    if (((VirtualMediaBean)object2).getConnectionType() == 1) {
                                        object = ((VirtualMediaBean)object2).getConnectedDrive();
                                        if (this.scrContext.getSelectedDrives().contains(object)) {
                                            this.scrContext.removeSelectedDrive((String)object);
                                            RRCLogger.log(300, -1, "Removing drive from ArrayList:" + (String)object);
                                        }
                                    }
                                    ((VirtualMediaBean)object2).setConnectionType(0);
                                    RRCLogger.log(300, "Disconnected handlers for Drive 2 Virtual Media Connection for port:" + this.port.getPortIndex());
                                    map2.remove(string);
                                }
                            } else {
                                RRCLogger.log(300, "Not disconnecting Drive 2 VM connection. No VMBean for " + string);
                            }
                            map2.remove(string);
                        } else {
                            RRCLogger.log(300, "VMImageMap is empty. Not disconnecting Drive 2 VM connection for " + string);
                        }
                        ((RRCScreenContext)this.scrContext).toggleMenuBar(string);
                        break block25;
                    }
                    RRCLogger.log(300, "KvmPort is NULL. Cannot close VM Connections.");
                    break block25;
                }
                RRCLogger.log(300, "RRCScreenContext is NULL. Cannot close VM Connections.");
            }
            catch (Exception exception) {
                RRCLogger.log(300, "Exception occured! Cannot close Drive Redirections.");
                RRCLogger.log(150, "", exception);
            }
        }
    }

    @Override
    public void sendKVMPopupKey() {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        int n = -1;
        String string = rRCScreenContext.getAppSettings().getkeyboardMenuHotkey();
        if (string != null && !"".equals(string.trim())) {
            if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionA"))) {
                n = 65;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionB"))) {
                n = 66;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionC"))) {
                n = 67;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionD"))) {
                n = 68;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionE"))) {
                n = 69;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionF"))) {
                n = 70;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionG"))) {
                n = 71;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionH"))) {
                n = 72;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionI"))) {
                n = 73;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionJ"))) {
                n = 74;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionK"))) {
                n = 75;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionL"))) {
                n = 76;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionM"))) {
                n = 77;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionN"))) {
                n = 78;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionO"))) {
                n = 79;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionP"))) {
                n = 80;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionQ"))) {
                n = 81;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionR"))) {
                n = 82;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionS"))) {
                n = 83;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionT"))) {
                n = 84;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionU"))) {
                n = 85;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionV"))) {
                n = 86;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionW"))) {
                n = 87;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionX"))) {
                n = 88;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionY"))) {
                n = 89;
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionZ"))) {
                n = 90;
            }
            char c = KeyEvent.getKeyText(n).charAt(0);
        } else {
            RRCLogger.log(300, "Unable to send KVM popup because getkeyboardMenuHotkey() returned null or empth String");
        }
    }

    public Object getAssociatedPort() {
        return this.kvmPort;
    }

    public void osdStateChanged(boolean bl, String string) {
        if (bl && NO_VIDEO_FROM_TARGET_SERVER.equalsIgnoreCase(string)) {
            this.scrContext.getLogger().logStatus(this.bundle.getString("Status.nosignal"));
            this.setStatusMessage(this.bundle.getString("Status.nosignal"));
        } else {
            this.scrContext.getLogger().logStatus(this.bundle.getString("Status.rrcLoaded"));
            this.setStatusMessage(this.bundle.getString("Status.rrcLoaded"));
        }
    }

    @Override
    public boolean isFullScreenMode() {
        return this.isFullScreenMode;
    }

    @Override
    public void setFullScreenMode(boolean bl) {
        this.isFullScreenMode = bl;
    }

    @Override
    public void addCustomMouseMotionListener(MouseMotionListener mouseMotionListener) {
        if (this.console != null) {
            for (MouseMotionListener mouseMotionListener2 : this.console.getMouseMotionListeners()) {
                if (mouseMotionListener2 != mouseMotionListener) continue;
                return;
            }
            this.console.addMouseMotionListener(mouseMotionListener);
        }
    }

    @Override
    public void addCustomMouseListener(MouseListener mouseListener) {
        if (this.console != null) {
            for (MouseListener mouseListener2 : this.console.getMouseListeners()) {
                if (mouseListener2 != mouseListener) continue;
                return;
            }
            this.console.addMouseListener(mouseListener);
        }
    }

    @Override
    public void removeCustomMouseMotionListener(MouseMotionListener mouseMotionListener) {
        if (this.console != null) {
            this.console.removeMouseMotionListener(mouseMotionListener);
        }
    }

    @Override
    public void removeCustomMouseListener(MouseListener mouseListener) {
        if (this.console != null) {
            this.console.removeMouseListener(mouseListener);
        }
    }

    @Override
    public boolean getAbsoluteMouseSupported() {
        ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectAbsoluteMouseModeView(this.getMouseMode() == 0);
        return this.isAbsoluteMouseSupported;
    }

    @Override
    public boolean getIntelligentMouseSupported() {
        ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectIntelligentMouseModeView(this.getMouseMode() == 1);
        return this.isIntelligentMouseSupported;
    }

    @Override
    public boolean getStandardMouseSupported() {
        ((RRCScreenContext)this.scrContext).getMainScreenMediator().selectStandardMouseModeView(this.getMouseMode() == 2);
        return this.isStandardMouseSupported;
    }

    public boolean isColorCalibrationSupported() {
        return this.colorCalibSupported;
    }

    public void setUsbProfileList(IUsbProfileList iUsbProfileList) {
        final USBProfilesInfo uSBProfilesInfo = RFBView.createUSBProfileInfo(iUsbProfileList);
        assert (this.kvmPort != null);
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                RFBView.this.kvmPort.setUsbProfilesInfo(uSBProfilesInfo);
                MPCUtil.notifyObservers((RRCScreenContext)RFBView.this.scrContext, RFBView.this.kvmPort);
            }
        });
    }

    static USBProfilesInfo createUSBProfileInfo(IUsbProfileList iUsbProfileList) {
        assert (iUsbProfileList != null);
        List<IUsbProfile> list = iUsbProfileList.getProfiles();
        assert (list != null);
        int n = list.size();
        RRCLogger.log(300, "UsbProfileListEntrys " + n);
        if (n > 0) {
            IUsbProfile iUsbProfile;
            RRCLogger.log(300, "USBProfiles " + RFBView.getPrintableString(list.toArray(new UsbProfile[n])));
            ArrayList<USBProfile> arrayList = new ArrayList<USBProfile>();
            for (int i = 0; i < n; ++i) {
                iUsbProfile = (UsbProfile)list.get(i);
                arrayList.add(new USBProfile(((UsbProfile)iUsbProfile).getId(), ((UsbProfile)iUsbProfile).getName(), ((UsbProfile)iUsbProfile).isSelected()));
            }
            IUsbProfile iUsbProfile2 = iUsbProfileList.getActive();
            assert (iUsbProfile2 != null);
            RRCLogger.log(300, "ActiveUSBProfile ID : " + iUsbProfile2.getId() + " ActiveUSBProfile Name : " + iUsbProfile2.getName());
            iUsbProfile = iUsbProfileList.getPreferred();
            assert (iUsbProfile != null);
            RRCLogger.log(300, "PreferredUSBProfile ID : " + iUsbProfile.getId() + " PreferredUSBProfile Name : " + iUsbProfile.getName());
            return new USBProfilesInfo(arrayList, new USBProfile(iUsbProfile2.getId(), iUsbProfile2.getName()), new USBProfile(iUsbProfile.getId(), iUsbProfile.getName()));
        }
        return null;
    }

    public void setVirtualMediaConfig(int[] nArray) {
        int n;
        VMConfigInfo vMConfigInfo = RFBView.getVMConfigInfo(nArray);
        assert (this.kvmPort != null);
        ArrayList<VMFeatures> arrayList = null;
        ArrayList<VMFeatures> arrayList2 = null;
        for (n = 0; n < nArray.length; ++n) {
            if ((nArray[n] & 8) != 0) {
                if (arrayList == null) {
                    arrayList = new ArrayList<VMFeatures>(10);
                }
                arrayList.add(new VMFeatures(n, nArray[n]));
            }
            if ((nArray[n] & 0x10) == 0) continue;
            if (arrayList2 == null) {
                arrayList2 = new ArrayList<VMFeatures>(10);
            }
            arrayList2.add(new VMFeatures(n, nArray[n]));
        }
        this.kvmPort.setSmartCardIndexHelper(new SmartCardIndexHelper(arrayList));
        this.kvmPort.setAudioIndexHelper(new AudioIndexHelper(arrayList2));
        if (this.isGreaterThanRfb128()) {
            int n2;
            n = 1;
            for (n2 = 0; n2 < nArray.length && n != 0; n &= nArray[n2] == 0 ? 1 : 0, ++n2) {
            }
            n2 = n == 0 ? 1 : 0;
            this.kvmPort.getVmConfigInfo().setCimActive(n2 != 0);
            if (n2 != 0) {
                VMConfigInfo vMConfigInfo2 = this.kvmPort.getVmConfigInfo();
                vMConfigInfo2.copyStateTo(vMConfigInfo);
                this.kvmPort.setVmConfigInfo(vMConfigInfo);
            }
            MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, this.kvmPort);
        } else if (!this.isVMConnectionActive()) {
            this.kvmPort.setVmConfigInfo(vMConfigInfo);
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    MPCUtil.notifyObservers((RRCScreenContext)RFBView.this.scrContext, RFBView.this.kvmPort);
                }
            });
        } else {
            int n3;
            n = 1;
            for (n3 = 0; n3 < nArray.length && n != 0; n &= nArray[n3] == 0 ? 1 : 0, ++n3) {
            }
            n3 = n == 0 ? 1 : 0;
            this.kvmPort.getVmConfigInfo().setCimActive(n3 != 0);
        }
    }

    static VMConfigInfo getVMConfigInfo(int[] nArray) {
        assert (nArray != null);
        assert (RFBView.checkIfStartingZeroes(nArray));
        RRCLogger.log(300, "VMCFG" + RFBView.getPrintableString(nArray));
        ArrayList<VMInterfaceInfo> arrayList = new ArrayList<VMInterfaceInfo>();
        for (int i = 0; i < nArray.length; ++i) {
            ArrayList<MassStorageDevice> arrayList2 = new ArrayList<MassStorageDevice>();
            if ((nArray[i] & 1) != 0) {
                arrayList2.add(new MassStorageDevice(1));
            }
            if ((nArray[i] & 2) != 0) assert (false);
            if ((nArray[i] & 4) != 0) {
                arrayList2.add(new MassStorageDevice(4));
            }
            if (arrayList2.size() <= 0) continue;
            arrayList.add(new VMInterfaceInfo(i, arrayList2));
        }
        return new VMConfigInfo(arrayList);
    }

    private static boolean checkIfStartingZeroes(int[] nArray) {
        if (nArray.length > 1 && nArray[0] == 0) {
            for (int i = 1; i < nArray.length; ++i) {
                if (nArray[i] == 0) continue;
                return false;
            }
        }
        return true;
    }

    private static String getPrintableString(int[] nArray) {
        String string = "[";
        for (int i = 0; i < nArray.length; ++i) {
            string = string + nArray[i] + ",";
        }
        string = string + "]";
        return string;
    }

    private static String getPrintableString(UsbProfile[] usbProfileArray) {
        String string = "[";
        for (int i = 0; i < usbProfileArray.length; ++i) {
            string = string + usbProfileArray[i].getId() + "|" + usbProfileArray[i].getName() + ",";
        }
        string = string + "]";
        return string;
    }

    public void changeActiveUSBProfile(USBProfile uSBProfile) {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        try {
            if (this.isConnected()) {
                this.rccore.requestUsbProfileChange(uSBProfile.getProfileID());
            } else {
                RRCLogger.log(300, "Not connected, unable to change Active USBProfile");
                ErrorHandlerImpl.getInstance().handleError("RFB_VIEW_ERR_NOT_CONNECTED", (Component)rRCScreenContext.getApplication().getContentPane());
            }
        }
        catch (IOException iOException) {
            RRCLogger.logException(iOException);
            RRCLogger.log(300, "Error changing Active USBProfile");
            ErrorHandlerImpl.getInstance().handleError("RFB_VIEW_ERR_ACTIVE_USB_PROFILE_CHANGE", (Component)rRCScreenContext.getApplication().getContentPane());
        }
    }

    public void setVideoSettings(IVideoSettings iVideoSettings) {
        this.vidSettings = iVideoSettings;
    }

    public void setFullyConnected(boolean bl) {
        this.fullyConnected = bl;
    }

    public void setMonitorOnly(boolean bl) {
        this.monitorOnly = bl;
    }

    public void setColorCalibrationSupported(boolean bl) {
        this.colorCalibSupported = bl;
    }

    public RCCore.Compression getCurrrentCompression() {
        return this.currentCompression;
    }

    public void setCurrentCompression(RCCore.Compression compression) {
        this.currentCompression = compression;
    }

    public RCCore.ColorDepth getCurrentColorDepth() {
        return this.currentColorDepth;
    }

    public void setCurrentColorDepth(RCCore.ColorDepth colorDepth) {
        this.currentColorDepth = colorDepth;
    }

    public RCCore.Smoothing getCurrentSmoothing() {
        return this.currentSmoothing;
    }

    public void setCurrentSmoothing(RCCore.Smoothing smoothing) {
        this.currentSmoothing = smoothing;
    }

    public boolean isAutoSelected() {
        return this.isAutoSelected;
    }

    public void setAutoSelected(boolean bl) {
        this.isAutoSelected = bl;
    }

    public boolean getIsInitialEndcoingAuto() {
        return this.isInitialEndoingAuto;
    }

    public void setInitialEndcoingAuto(boolean bl) {
        this.isInitialEndoingAuto = bl;
    }

    public RCCore getRCCore() {
        return this.rccore;
    }

    public List<IVMMountRequestResponse> getRemoteIsoList() {
        return this.remoteIsoList;
    }

    public void setRemoteIsoList(List<IVMMountRequestResponse> list) {
        this.remoteIsoList = list;
    }

    public void setSessionId(int n) {
        this.rcparams.sessionId = n;
    }

    public void setRfbSessionId(int n) {
        this.rfbSessionId = n;
    }

    @Override
    public int getRfbSessionId() {
        return this.rfbSessionId;
    }

    public void setSingleCursorMode(boolean bl) {
        this.singleCursorMode = bl;
    }

    public void setStatusMessage(String string) {
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(string);
    }

    public void clearStatusMessage() {
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(this.bundle.getString("Status.rrcLoaded"));
    }

    public String getTargetPortId() {
        if (MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext)) {
            return this.rcparams.targetPortId;
        }
        return this.kvmPort.getUniquePortId();
    }

    public String getUniqueRdmPortId() {
        return this.rcparams.targetPortId;
    }

    public boolean getVideoSettingsSupported() {
        return this.videoSettingsSupported;
    }

    public void setVideoSettingsSupported(boolean bl) {
        this.videoSettingsSupported = bl;
    }

    public void enableMouseModes(List<RCCore.MouseMode> list) {
        MainScreenMediator mainScreenMediator = ((RRCScreenContext)this.scrContext).getMainScreenMediator();
        StringBuffer stringBuffer = new StringBuffer();
        mainScreenMediator.enableAbsoluteMouseModeView(list.contains((Object)RCCore.MouseMode.ABSOLUTE));
        mainScreenMediator.enableIntelligentMouseModeView(list.contains((Object)RCCore.MouseMode.AUTOMATIC));
        mainScreenMediator.enableStandardMouseModeView(list.contains((Object)RCCore.MouseMode.STANDARD));
        this.isAbsoluteMouseSupported = list.contains((Object)RCCore.MouseMode.ABSOLUTE);
        this.isIntelligentMouseSupported = list.contains((Object)RCCore.MouseMode.AUTOMATIC);
        this.isStandardMouseSupported = list.contains((Object)RCCore.MouseMode.STANDARD);
        for (RCCore.MouseMode mouseMode : list) {
            stringBuffer.append((stringBuffer.length() < 1 ? "" : ", ") + (Object)((Object)mouseMode));
        }
        RRCLogger.log(200, "Supported Mouse Modes: " + stringBuffer.toString());
    }

    public RemoteConsoleParameters getRCParameters() {
        return this.rcparams;
    }

    protected void showFSExitTip() {
        IApplicationPreferences iApplicationPreferences = ((RRCScreenContext)this.scrContext).getAppSettings();
        String string = "";
        if (iApplicationPreferences != null) {
            string = iApplicationPreferences.getkeyboardMenuHotkey().toUpperCase();
        }
        if (string != "" && string.contains("ALT")) {
            string = string.replace("ALT", "LeftALT");
        }
        this.rccore.setOSD(this.bundle.getString("Optiondialog.KeyboardShortcutMenuHotkey") + ": " + string, 4000, false);
    }

    public KvmPort getPort() {
        return this.kvmPort;
    }

    @Override
    public SmartCardErrorsAndMessagesHandler getSmartCardErrorsAndMessagesHandler() {
        return this.smartCardErrorsAndMessagesHandlerImpl;
    }

    @Override
    public DeviceView.SmartCardStatusListeners getSmartCardStatusListener() {
        return this.smartCardErrorsAndMessagesHandlerImpl;
    }

    @Override
    public AudioErrorsAndMessageHandler getAudioErrorsAndMessagesHandler() {
        return this.audioErrorsAndMessageHandlerImpl;
    }

    @Override
    public RemoteConsoleParameters getRemoteConsoleParameters() {
        return this.getRCParameters();
    }

    @Override
    public int getEricKeyCode(int n, char c, int n2) {
        return this.rccore.getKeyCode(n, c, n2);
    }

    private boolean isGreaterThanRfb128() {
        String string;
        RCCore rCCore = this.rccore;
        if (rCCore != null && (string = rCCore.getProtocolVersion()).length() == 5) {
            int n = Integer.parseInt(string.substring(0, 2));
            int n2 = Integer.parseInt(string.substring(3, 5));
            if (n >= 1 && n2 >= 28) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IMacroHelper getMacroHelper() {
        return this.macroHelper;
    }

    @Override
    public int getKeyboardType() {
        return this.keyboardType;
    }

    public boolean isCapsLockOn() {
        return this.capsLockOn;
    }

    public void setCapsLockOn(boolean bl) {
        this.capsLockOn = bl;
    }

    public boolean isNumLockOn() {
        return this.numLockOn;
    }

    public void setNumLockOn(boolean bl) {
        this.numLockOn = bl;
    }

    public boolean isScrollLockOn() {
        return this.scrollLockOn;
    }

    public void setScrollLockOn(boolean bl) {
        this.scrollLockOn = bl;
    }

    public AudioEventListener.DeviceState getPlaybackState() {
        return this.playbackState;
    }

    @Override
    public void setPlaybackState(AudioEventListener.DeviceState deviceState) {
        this.playbackState = deviceState;
        MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, this.kvmPort);
    }

    public AudioEventListener.DeviceState getCaptureState() {
        return this.captureState;
    }

    @Override
    public void setCaptureState(AudioEventListener.DeviceState deviceState) {
        this.captureState = deviceState;
        MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, this.kvmPort);
    }

    public boolean isQuitEvent() {
        return this.isQuitEvent;
    }

    public void setQuitEvent(boolean bl) {
        this.isQuitEvent = bl;
    }

    private RFBView getPrimaryPortView() {
        if (this.port.isPrimaryOrSinglePort()) {
            return this;
        }
        Port port = this.port.getPrimaryPort();
        if (port != null) {
            return (RFBView)port.getView();
        }
        return null;
    }

    private List<RFBView> getSecondaryPortViews() {
        Vector<RFBView> vector = new Vector<RFBView>();
        for (Port port : this.port.getSecondaryPorts()) {
            RFBView rFBView = (RFBView)port.getView();
            if (rFBView == null) continue;
            vector.add(rFBView);
        }
        return vector;
    }

    static {
        VIEW_BORER_DIM = new Dimension(10, 10);
    }

    private class AudioErrorsAndMessageHandlerImpl
    implements AudioErrorsAndMessageHandler {
        private AudioErrorsAndMessageHandlerImpl() {
        }

        @Override
        public void errorMessage(Window window, String string, String string2) {
            CommonPopups.showErrorMessage(string, window, RFBView.this.scrContext, string2);
        }

        @Override
        public void notificationReceived(Window window, INotificationEvent iNotificationEvent, String string) {
            ErrorHandlerImpl.getInstance().handleError(iNotificationEvent.getErrorCode(), window, RFBView.this, string);
        }
    }

    private class SmartCardErrorsAndMessagesHandlerImpl
    extends DeviceView.SmartCardStatusListeners
    implements SmartCardErrorsAndMessagesHandler {
        private SmartCardErrorsAndMessagesHandlerImpl() {
        }

        @Override
        public void errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes errorCodes, Object object, Window window, String string) {
            Window window2 = window;
            if (object instanceof Throwable) {
                RRCLogger.log(300, -1, (Throwable)object, (Object)((Object)errorCodes) + "");
            } else {
                RRCLogger.log(300, (Object)((Object)errorCodes) + "" + (object == null ? "" : object));
            }
            switch (errorCodes) {
                case INCOMPATIBLE_VERSION_OF_DEVICE_PROTOCOL: {
                    String string2 = T._("The client application and this device have incompatible SmartCard feature protocol versions.");
                    String string3 = T._("Please update your client, or contact your System Administrator, if you require this feature.");
                    String string4 = "<html>" + string2 + "<br>" + string3 + "</html>";
                    CommonPopups.showErrorMessage(string4, window2, RFBView.this.scrContext, string);
                    break;
                }
                case NO_SUPPORTED_PROTOCOL: {
                    String string5 = T._("Error accessing the smart card. Please make sure the card is inserted correctly and makes proper contact.");
                    String string6 = T._("If the problem persists, contact your System Administrator.");
                    String string7 = "<html>" + string5 + "<br>" + string6 + "</html>";
                    CommonPopups.showErrorMessage(string7, window2, RFBView.this.scrContext, string);
                    break;
                }
                case COMMUNICATION_ERROR_OCCURED: {
                    CommonPopups.showErrorMessage(T._("Unexpected communication error. Please try again later."), window2, RFBView.this.scrContext, string);
                    break;
                }
                case UNKNOWN_ERROR: {
                    CommonPopups.showErrorMessage(T._("Unknown error. Contact your System Administrator"), window2, RFBView.this.scrContext, string);
                    break;
                }
                case CARD_READER_DOES_NOT_EXIST: {
                    String string8 = T._("Selected Smart Card Reader no longer detected, ");
                    String string9 = T._("please refresh list, select another and try again...");
                    String string10 = "<html>" + string8 + "<br>" + string9 + "</html>";
                    CommonPopups.showErrorMessage(string10, window2, RFBView.this.scrContext, string);
                }
            }
        }

        @Override
        public void notificationReceived(NotificationEvent notificationEvent, Window window, String string) {
            ErrorHandlerImpl.getInstance().handleError(notificationEvent.getErrorCode(), window, RFBView.this, string);
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("DISCONNECTED")) {
                Object object = propertyChangeEvent.getNewValue();
                if (object != null) {
                    if (object instanceof IOException) {
                        this.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.COMMUNICATION_ERROR_OCCURED, object, JOptionPane.getFrameForComponent(RFBView.this.scrContext.getApplication().getContentPane()), T._("SmartCard"));
                    } else {
                        this.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.UNKNOWN_ERROR, object, JOptionPane.getFrameForComponent(RFBView.this.scrContext.getApplication().getContentPane()), T._("SmartCard"));
                    }
                }
                this.connected = false;
            } else if (propertyChangeEvent.getPropertyName().equals("QUIT_NOTIFICATION")) {
                ErrorHandlerImpl.getInstance().handleError(((NotificationEvent)propertyChangeEvent.getNewValue()).getErrorCode(), JOptionPane.getFrameForComponent(RFBView.this.scrContext.getApplication().getContentPane()), RFBView.this, T._("SmartCard"));
                this.connected = false;
            } else if (!propertyChangeEvent.getPropertyName().equals("NOTIFICATION_FROM_SERVER")) {
                if (propertyChangeEvent.getPropertyName().equals("NO_PROTO_SPPORTED")) {
                    this.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.NO_SUPPORTED_PROTOCOL, null, JOptionPane.getFrameForComponent(RFBView.this.scrContext.getApplication().getContentPane()), T._("SmartCard"));
                    this.connected = false;
                } else if (propertyChangeEvent.getPropertyName().equals("CARD_READER_MOUNTED")) {
                    this.connected = true;
                } else if (propertyChangeEvent.getPropertyName().equals("CANCEL_ISSUED") || propertyChangeEvent.getPropertyName().equals("QUIT_ISSUED")) {
                    this.connected = false;
                }
            }
            RFBView.this.getPort().getAudioAction().evaluateEnabled();
        }
    }

    class AllListener
    extends MouseAdapter
    implements AdjustmentListener,
    ComponentListener {
        AllListener() {
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            Object object = mouseEvent.getSource();
            RFBView.this.scrollBothDirections = false;
            if (object == RFBView.this.northLabel) {
                RFBView.this.adjustingVertical = true;
                RFBView.this.adjustmentValue = -1;
            }
            if (object == RFBView.this.southLabel) {
                RFBView.this.adjustingVertical = true;
                RFBView.this.adjustmentValue = 1;
            }
            if (object == RFBView.this.westLabel) {
                RFBView.this.adjustingVertical = false;
                RFBView.this.adjustmentValue = -1;
            }
            if (object == RFBView.this.eastLabel) {
                RFBView.this.adjustingVertical = false;
                RFBView.this.adjustmentValue = 1;
            }
            if (object == RFBView.this.northWestLabel) {
                RFBView.this.scrollBothDirections = true;
                RFBView.this.adjustmentValue = -1;
            }
            if (object == RFBView.this.southEastLabel) {
                RFBView.this.scrollBothDirections = true;
                RFBView.this.adjustmentValue = 1;
            }
            RFBView.this.timer = new Timer(50, new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    RFBView.this.adjustScrollBars();
                }
            });
            RFBView.this.timer.start();
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            if (RFBView.this.timer != null) {
                RFBView.this.timer.stop();
                RFBView.this.timer = null;
            }
        }

        @Override
        public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
            if (adjustmentEvent.getSource() == RFBView.this.hScrollBar) {
                RFBView.this.jsp.getHorizontalScrollBar().setValue(RFBView.this.hScrollBar.getValue());
            } else if (adjustmentEvent.getSource() == RFBView.this.vScrollBar) {
                RFBView.this.jsp.getVerticalScrollBar().setValue(RFBView.this.vScrollBar.getValue());
            }
        }

        @Override
        public void componentHidden(ComponentEvent componentEvent) {
        }

        @Override
        public void componentMoved(ComponentEvent componentEvent) {
        }

        @Override
        public void componentResized(ComponentEvent componentEvent) {
            if (RFBView.this.isScaleVideoFlag()) {
                RFBView.this.handleResizing();
            } else {
                RFBView.this.showScrollBars();
            }
        }

        @Override
        public void componentShown(ComponentEvent componentEvent) {
        }
    }
}

