/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.X509TrustManager;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import nn.pp.core.NotificationListener;
import nn.pp.core.T;
import nn.pp.core.impl.DeviceConnector;
import nn.pp.rccore.AudioStatusListener;
import nn.pp.rccore.ConnectionEventListener;
import nn.pp.rccore.IAudioStatusExSupport;
import nn.pp.rccore.IKeyboardMacro;
import nn.pp.rccore.IKvmPort;
import nn.pp.rccore.ILicenseSupport;
import nn.pp.rccore.IMultiMonitorTargetSupport;
import nn.pp.rccore.IUsbProfileList;
import nn.pp.rccore.IVMConfigInfo;
import nn.pp.rccore.IVMMountRequestResponse;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.KeyboardInfoListener;
import nn.pp.rccore.KeyboardListener;
import nn.pp.rccore.KeyboardMacro;
import nn.pp.rccore.KvmPort;
import nn.pp.rccore.MouseEventListener;
import nn.pp.rccore.MouseModeListener;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.RCException;
import nn.pp.rccore.VMMountRequestResponse;
import nn.pp.rccore.VideoEventListener;
import nn.pp.rccore.VideoSettings;
import nn.pp.rccore.VirtualMediaInfoListener;
import nn.pp.rccore.impl.AudioStatusExSupport;
import nn.pp.rccore.impl.ISupportsKeyboardHandling;
import nn.pp.rccore.impl.ISupportsMouseHandling;
import nn.pp.rccore.impl.LicenseSupport;
import nn.pp.rccore.impl.ListenerLists;
import nn.pp.rccore.impl.MonitoringDeviceConnector;
import nn.pp.rccore.impl.MultiMonitorTargetSupport;
import nn.pp.rccore.impl.RemoteConsoleRenderer;
import nn.pp.rccore.impl.keyboard.KeyboardHandler;
import nn.pp.rccore.impl.mouse.MouseHandler;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbRenderer;

public abstract class RCCoreImpl
implements RCCore,
VideoEventListener,
AudioStatusListener,
MouseModeListener,
KeyboardInfoListener,
VirtualMediaInfoListener,
ConnectionEventListener {
    protected Logger logger;
    protected RemoteConsoleRenderer renderer;
    private boolean localKeyboardMappingPersistent = false;
    private boolean fpsEnabled = true;
    private boolean trafficSpeedEnabled = true;
    private final Map<Object, Object> clientProperties = Collections.synchronizedMap(new HashMap());
    protected ListenerLists listeners = new ListenerLists();
    private LicenseSupport licenseSupport;
    private AudioStatusExSupport audioSettingsSupport;
    private MultiMonitorTargetSupport multiMonitorTargetSupport;
    private boolean encodingChangeAllowed;
    private boolean autoSupported;
    private List<RCCore.Predefine> predefines;
    private List<RCCore.ColorDepth> depths;
    private List<RCCore.Compression> compressions;
    private boolean lossySupported;
    private boolean currentAuto;
    private RCCore.ColorDepth currentDepth;
    private RCCore.Compression currentCompression;
    private boolean currentLossy;
    private boolean videoSettingsSupported;
    private boolean videoAutoAdjustSupported;
    private boolean colorCalibrationSupported;
    private boolean videoRefreshSupported;
    private VideoSettings videoSettings;
    private boolean videoSettingsRequested;
    private Dimension resolution;
    private boolean cimLangOptionsSupported;
    private boolean ethernetGigabitSupported;
    private boolean audiosupport;
    private boolean mouseModeChangeSupported;
    private List<RCCore.MouseMode> mouseModes;
    private RCCore.MouseMode currentMouseMode;
    private boolean singleCursorSupported;
    private boolean singleCursorActive;
    private boolean mouseSyncSupported;
    private List<IKeyboardMacro> keyboardMacros;
    private Locale softKeyboardLocale;
    private Locale localKeyboardLocale;
    private List<RCCore.KeyboardLed> keyboardState;
    private boolean sunKeyboardSupported;
    private boolean virtualMediaSupported;
    private boolean virtualMediaReadOnly;
    private int virtualMediaDriveCount;
    private boolean remoteIsoSupported;
    private List<IVMMountRequestResponse> remoteIsoList;
    private IVMConfigInfo vmConfig;
    private IUsbProfileList usbProfileList;
    private List<KvmPort> kvmPortList;
    private int serverSessionId;
    private int connectedUsers;
    private boolean monitorModeAllowed;
    private boolean monitorMode;
    private boolean exclusiveModeAllowed;
    private boolean exclusiveMode;
    private String protocolVersion;
    private String deviceName;
    private int inSpeed;
    private int outSpeed;
    private int fps;
    private X509TrustManager trustManager;
    private DeviceConnector connector;
    private RfbHandler rfbHandler;
    private String proxyConnectionId;
    private String proxyUseSSL;
    private LinkedHashMap<String, String> appletParameterMap;
    private boolean lowBandwidth;
    private boolean customAuto;
    private RCCore.Compression customCompression;
    private RCCore.ColorDepth customColorDepth;
    private RCCore.Smoothing customSmoothing;
    Dimension clientFrameSize;
    private Map<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> clientSessionInitProps;

    protected abstract void loadRenderer();

    public RCCoreImpl(Logger logger) {
        this.licenseSupport = new LicenseSupport(this.listeners.licenseSupportListenerList);
        this.audioSettingsSupport = new AudioStatusExSupport(this.listeners.audioSettingsExListenerList);
        this.multiMonitorTargetSupport = new MultiMonitorTargetSupport(this);
        this.encodingChangeAllowed = false;
        this.autoSupported = false;
        this.predefines = new Vector<RCCore.Predefine>();
        this.depths = new Vector<RCCore.ColorDepth>();
        this.compressions = new Vector<RCCore.Compression>();
        this.lossySupported = false;
        this.currentAuto = false;
        this.currentDepth = RCCore.ColorDepth.COLOR_16_BIT;
        this.currentCompression = RCCore.Compression.LEVEL_5;
        this.currentLossy = false;
        this.videoSettingsSupported = false;
        this.videoAutoAdjustSupported = false;
        this.colorCalibrationSupported = false;
        this.videoRefreshSupported = false;
        this.videoSettingsRequested = false;
        this.resolution = new Dimension(640, 480);
        this.cimLangOptionsSupported = false;
        this.ethernetGigabitSupported = false;
        this.audiosupport = false;
        this.mouseModeChangeSupported = false;
        this.mouseModes = new Vector<RCCore.MouseMode>();
        this.singleCursorSupported = false;
        this.singleCursorActive = false;
        this.mouseSyncSupported = false;
        this.keyboardMacros = new Vector<IKeyboardMacro>();
        this.softKeyboardLocale = null;
        this.localKeyboardLocale = null;
        this.keyboardState = new Vector<RCCore.KeyboardLed>();
        this.sunKeyboardSupported = false;
        this.virtualMediaSupported = false;
        this.virtualMediaReadOnly = false;
        this.virtualMediaDriveCount = 0;
        this.remoteIsoSupported = false;
        this.remoteIsoList = new Vector<IVMMountRequestResponse>();
        this.kvmPortList = new Vector<KvmPort>();
        this.serverSessionId = -1;
        this.connectedUsers = 0;
        this.monitorModeAllowed = false;
        this.monitorMode = false;
        this.exclusiveModeAllowed = false;
        this.exclusiveMode = false;
        this.protocolVersion = "1.0";
        this.deviceName = T._("Device");
        this.inSpeed = 0;
        this.outSpeed = 0;
        this.fps = 0;
        this.lowBandwidth = false;
        this.customAuto = false;
        this.customCompression = null;
        this.customColorDepth = null;
        this.customSmoothing = null;
        this.clientSessionInitProps = Collections.emptyMap();
        if (logger == null) {
            logger = Logger.getLogger("Remote Console");
            logger.setUseParentHandlers(false);
            logger.addHandler(new ConsoleHandler());
            logger.setLevel(Level.SEVERE);
        }
        this.logger = logger;
        this.addVideoEventListener(this, 127);
        this.addAudioStatusListener(this);
        this.addMouseModeListener(this, 7);
        this.addKeyboardInfoListener(this, 23);
        this.addVirtualMediaInfoListener(this, 7);
        this.addConnectionEventListener(this, 8191);
    }

    public void init() {
        this.loadRenderer();
    }

    @Override
    public void encodingChangeAllowed(boolean bl) {
        this.encodingChangeAllowed = bl;
    }

    @Override
    public boolean isEncodingChangeSupported() {
        return this.encodingChangeAllowed;
    }

    @Override
    public void encodingAutoSupportedChanged(boolean bl) {
        this.autoSupported = bl;
    }

    @Override
    public boolean isEncodingAutoSupported() {
        return this.autoSupported;
    }

    @Override
    public void supportedEncodingPredefinesChanged(List<RCCore.Predefine> list) {
        this.predefines = new Vector<RCCore.Predefine>(list);
    }

    @Override
    public List<RCCore.Predefine> getSupportedEncodingPredefines() {
        return this.predefines;
    }

    @Override
    public void supportedEncodingColorDepthsChanged(List<RCCore.ColorDepth> list) {
        this.depths = new Vector<RCCore.ColorDepth>(list);
    }

    @Override
    public List<RCCore.ColorDepth> getSupportedEncodingColorDepths() {
        return this.depths;
    }

    @Override
    public void supportedEncodingCompressionsChanged(List<RCCore.Compression> list) {
        this.compressions = new Vector<RCCore.Compression>(list);
    }

    @Override
    public List<RCCore.Compression> getSupportedEncodingCompressions() {
        return this.compressions;
    }

    @Override
    public void encodingLossySupportedChanged(boolean bl) {
        this.lossySupported = bl;
    }

    @Override
    public boolean isEncodingLossySupported() {
        return this.lossySupported;
    }

    @Override
    public void encodingAutoChanged(boolean bl) {
        this.currentAuto = bl;
    }

    @Override
    public boolean isEncodingAuto() {
        return this.currentAuto;
    }

    @Override
    public void encodingColorDepthChanged(RCCore.ColorDepth colorDepth) {
        this.currentDepth = colorDepth;
    }

    @Override
    public RCCore.ColorDepth getEncodingColorDepth() {
        return this.currentDepth;
    }

    @Override
    public void encodingCompressionChanged(RCCore.Compression compression) {
        this.currentCompression = compression;
    }

    @Override
    public RCCore.Compression getEncodingCompression() {
        return this.currentCompression;
    }

    @Override
    public void encodingLossyChanged(boolean bl) {
        this.currentLossy = bl;
    }

    @Override
    public boolean isEncodingLossy() {
        return this.currentLossy;
    }

    @Override
    public void videoSettingsSupportChanged(boolean bl) {
        this.videoSettingsSupported = bl;
    }

    @Override
    public boolean isVideoSettingsSupported() {
        return this.videoSettingsSupported;
    }

    @Override
    public void videoAutoAdjustSupportChanged(boolean bl) {
        this.videoAutoAdjustSupported = bl;
    }

    @Override
    public boolean isVideoAutoAdjustSupported() {
        return this.videoAutoAdjustSupported;
    }

    @Override
    public void colorCalibrationSupportChanged(boolean bl) {
        this.colorCalibrationSupported = bl;
    }

    @Override
    public boolean isColorCalibrationSupported() {
        return this.colorCalibrationSupported;
    }

    @Override
    public void videoRefreshSupportChanged(boolean bl) {
        this.videoRefreshSupported = bl;
    }

    @Override
    public boolean isVideoRefreshSupported() {
        return this.videoRefreshSupported;
    }

    @Override
    public void videoSettingsUpdated(IVideoSettings iVideoSettings) {
        this.videoSettings = new VideoSettings(iVideoSettings);
    }

    @Override
    public VideoSettings getVideoSettings() {
        return this.videoSettingsRequested ? this.videoSettings : null;
    }

    @Override
    public void resolutionChanged(Dimension dimension) {
        Dimension dimension2;
        this.resolution = dimension2 = new Dimension(dimension);
    }

    @Override
    public Dimension getResolution() {
        return this.resolution;
    }

    @Override
    public void osdMessageReceived(String string, boolean bl) {
    }

    @Override
    public void cimLanguageOptionsSupported(boolean bl) {
        this.cimLangOptionsSupported = bl;
    }

    public boolean isCimLanguageOptionsSupported() {
        return this.cimLangOptionsSupported;
    }

    @Override
    public boolean isEthernetGigabitSupported() {
        return this.ethernetGigabitSupported;
    }

    @Override
    public void ethernetGigabitSupported(boolean bl) {
        this.ethernetGigabitSupported = bl;
    }

    @Override
    public void videoDataUpdated(Rectangle rectangle) {
    }

    @Override
    public void audioSupportChanged(boolean bl) {
        this.audiosupport = bl;
    }

    @Override
    public boolean isAudioStatusSupportEnabled() {
        return this.audiosupport;
    }

    @Override
    public void mouseModeChangeSupportChanged(boolean bl) {
        this.mouseModeChangeSupported = bl;
    }

    @Override
    public boolean isMouseModeChangeSupported() {
        return this.mouseModeChangeSupported;
    }

    @Override
    public void supportedMouseModesChanged(List<RCCore.MouseMode> list) {
        this.mouseModes = new Vector<RCCore.MouseMode>(list);
    }

    @Override
    public List<RCCore.MouseMode> getSupportedMouseModes() {
        return this.mouseModes;
    }

    @Override
    public void mouseModeChanged(RCCore.MouseMode mouseMode) {
        this.currentMouseMode = mouseMode;
    }

    @Override
    public RCCore.MouseMode getMouseMode() {
        return this.currentMouseMode;
    }

    @Override
    public void singleCursorModeSupportChanged(boolean bl) {
        this.singleCursorSupported = bl;
    }

    @Override
    public boolean isSingleCursorModeSupported() {
        return this.singleCursorSupported;
    }

    @Override
    public void singleCursorModeChanged(boolean bl) {
        this.singleCursorActive = bl;
    }

    @Override
    public boolean isSingleCursorMode() {
        return this.singleCursorActive;
    }

    @Override
    public void mouseSyncSupportChanged(boolean bl) {
        this.mouseSyncSupported = bl;
    }

    @Override
    public boolean isMouseSyncSupported() {
        return this.mouseSyncSupported;
    }

    @Override
    public void keyboardMacroListChanged(List<IKeyboardMacro> list) {
        Vector<IKeyboardMacro> vector = new Vector<IKeyboardMacro>(list.size());
        for (IKeyboardMacro iKeyboardMacro : list) {
            vector.add(new KeyboardMacro(iKeyboardMacro));
        }
        this.keyboardMacros = vector;
    }

    @Override
    public List<IKeyboardMacro> getKeyboardMacroList() {
        return this.keyboardMacros;
    }

    @Override
    public void softKeyboardMappingChanged(Locale locale) {
        Locale locale2;
        this.softKeyboardLocale = locale2 = new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant());
    }

    @Override
    public Locale getSoftKeyboardMapping() {
        return this.softKeyboardLocale;
    }

    @Override
    public void localKeyboardMappingChanged(Locale locale) {
        Locale locale2;
        this.localKeyboardLocale = locale2 = new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant());
    }

    @Override
    public Locale getLocalKeyboardMapping() {
        return this.localKeyboardLocale;
    }

    @Override
    public void keyboardLedStateChanged(List<RCCore.KeyboardLed> list) {
        this.keyboardState = new Vector<RCCore.KeyboardLed>(list);
    }

    @Override
    public List<RCCore.KeyboardLed> getKeyboardLedState() {
        return this.keyboardState;
    }

    @Override
    public void sunKeyboardSupported(boolean bl) {
        this.sunKeyboardSupported = bl;
    }

    public boolean isSunKeyboardSupported() {
        return this.sunKeyboardSupported;
    }

    @Override
    public void virtualMediaSupportChanged(boolean bl) {
        this.virtualMediaSupported = bl;
    }

    @Override
    public boolean isVirtualMediaSupported() {
        return this.virtualMediaSupported;
    }

    @Override
    public void virtualMediaReadOnlyChanged(boolean bl) {
        this.virtualMediaReadOnly = bl;
    }

    @Override
    public boolean isVirtualMediaReadOnly() {
        return this.virtualMediaReadOnly;
    }

    @Override
    public void virtualMediaDriveCountChanged(int n) {
        this.virtualMediaDriveCount = n;
    }

    @Override
    public int getVirtualMediaDriveCount() {
        return this.virtualMediaDriveCount;
    }

    @Override
    public void remoteIsoSupportChanged(boolean bl) {
        this.remoteIsoSupported = bl;
    }

    @Override
    public boolean isVirtualMediaRemoteIsoSupported() {
        return this.remoteIsoSupported;
    }

    @Override
    public void remoteIsoListChanged(List<IVMMountRequestResponse> list) {
        Vector<IVMMountRequestResponse> vector = new Vector<IVMMountRequestResponse>(list);
        for (IVMMountRequestResponse iVMMountRequestResponse : list) {
            vector.add(new VMMountRequestResponse(iVMMountRequestResponse));
        }
        this.remoteIsoList = vector;
    }

    @Override
    public List<IVMMountRequestResponse> getVirtualMediaRemoteIsoList() {
        return this.remoteIsoList;
    }

    @Override
    public void remoteIsoMountFinished(IVMMountRequestResponse iVMMountRequestResponse) {
    }

    @Override
    public void virtualMediaConfigChanged(IVMConfigInfo iVMConfigInfo) {
        this.vmConfig = iVMConfigInfo;
    }

    public IVMConfigInfo getVMConfigInfo() {
        return this.vmConfig;
    }

    @Override
    public void UsbProfileListChanged(IUsbProfileList iUsbProfileList) {
        this.usbProfileList = iUsbProfileList;
    }

    @Override
    public IUsbProfileList getUsbProfileList() {
        return this.usbProfileList;
    }

    @Override
    public void requestUsbProfileChange(int n) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.changeUsbProfile(n);
        }
    }

    @Override
    public void disconnected(Exception exception) {
    }

    @Override
    public void connected() {
    }

    @Override
    public void languageChanged(Locale locale) {
    }

    @Override
    public void chatWelcomeChanged(String string) {
    }

    @Override
    public void newChatMessage(String string) {
    }

    @Override
    public String portListChanged(List<? extends IKvmPort> list) {
        Vector<KvmPort> vector = new Vector<KvmPort>();
        for (IKvmPort iKvmPort : list) {
            vector.add(new KvmPort(iKvmPort));
        }
        this.kvmPortList = vector;
        return null;
    }

    public List<KvmPort> getPortList() {
        return this.kvmPortList;
    }

    @Override
    public void serverSessionIdChanged(int n) {
        this.serverSessionId = n;
    }

    @Override
    public int getServerSessionId() {
        return this.serverSessionId;
    }

    @Override
    public void connectedUsersChanged(int n) {
        this.connectedUsers = n;
    }

    @Override
    public int getConnectedUsers() {
        return this.connectedUsers;
    }

    @Override
    public void monitorModePermissionChanged(boolean bl) {
        this.monitorModeAllowed = bl;
    }

    @Override
    public boolean hasMonitorModePermission() {
        return this.monitorModeAllowed;
    }

    @Override
    public void monitorModeChanged(boolean bl) {
        this.monitorMode = bl;
    }

    @Override
    public boolean isMonitorMode() {
        return this.monitorMode;
    }

    @Override
    public void exclusiveModePermissionChanged(boolean bl) {
        this.exclusiveModeAllowed = bl;
    }

    @Override
    public boolean hasExclusiveModePermission() {
        return this.exclusiveModeAllowed;
    }

    @Override
    public void exclusiveModeChanged(boolean bl) {
        this.exclusiveMode = bl;
    }

    @Override
    public boolean isExclusiveMode() {
        return this.exclusiveMode;
    }

    @Override
    public void protocolVersionChanged(String string) {
        String string2;
        this.protocolVersion = string2 = new String(string);
    }

    @Override
    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public void deviceNameChanged(String string) {
        String string2;
        this.deviceName = string2 = new String(string);
    }

    @Override
    public String getDeviceName() {
        return this.deviceName;
    }

    @Override
    public void incomingTrafficSpeed(int n) {
        this.inSpeed = n;
    }

    @Override
    public int getIncomingTrafficSpeed() {
        return this.inSpeed;
    }

    @Override
    public void outgoingTrafficSpeed(int n) {
        this.outSpeed = n;
    }

    @Override
    public int getOutgoingTrafficSpeed() {
        return this.outSpeed;
    }

    @Override
    public void framesPerSecond(int n) {
        this.fps = n;
    }

    @Override
    public int getFramesPerSecond() {
        return this.fps;
    }

    @Override
    public boolean isLocalKeyboardMappingPersistent() {
        return this.localKeyboardMappingPersistent;
    }

    @Override
    public void setLocalKeyboardMappingPersistent(boolean bl) {
        this.localKeyboardMappingPersistent = bl;
        if (this.rfbHandler != null) {
            this.rfbHandler.setLocalKeyboardMappingPersistent(this.localKeyboardMappingPersistent);
        }
    }

    @Override
    public JComponent getRCJComponent() {
        return this.renderer.getRCJComponent();
    }

    @Override
    public BufferedImage getSnapshot(Rectangle rectangle) {
        return this.renderer.getSnapshot(rectangle);
    }

    @Override
    public BufferedImage getSnapshot() {
        return this.renderer.getSnapshot();
    }

    @Override
    public double getScalingX() {
        return this.renderer.getScalingX();
    }

    @Override
    public double getScalingY() {
        return this.renderer.getScalingY();
    }

    @Override
    public boolean isScaleToFit() {
        return this.renderer.isScaleToFit();
    }

    @Override
    public boolean isScaleToFitKeepAr() {
        return this.renderer.isScaleToFitKeepAr();
    }

    @Override
    public void setScaling(double d, double d2) throws IllegalArgumentException, UnsupportedOperationException {
        this.renderer.setScaling(d, d2);
    }

    @Override
    public void setScaleToFit(boolean bl, boolean bl2, Dimension dimension) throws UnsupportedOperationException {
        this.renderer.setScaleToFit(bl, bl2, dimension);
    }

    @Override
    public RCCore.Interpolation getInterpolation() {
        return this.renderer.getInterpolation();
    }

    @Override
    public void setInterpolation(RCCore.Interpolation interpolation) {
        this.renderer.setInterpolation(interpolation);
    }

    @Override
    public void syncMouse(RCCore.MouseSyncType mouseSyncType) {
        MouseHandler mouseHandler = RCCoreImpl.getMouseHandler(this.renderer);
        if (mouseHandler != null) {
            mouseHandler.doMouseSync(mouseSyncType);
        } else {
            this.rfbHandler.handleMouseSync(mouseSyncType);
        }
    }

    @Override
    public void setSingleCursorMode(boolean bl) {
        this.renderer.setSingleCursorMode(bl);
    }

    @Override
    public void setCaptureRightAway(boolean bl) {
        this.renderer.setCaptureRightAway(bl);
    }

    @Override
    public void enforceSMMHotKeyCheck(boolean bl) {
        if (this.renderer != null) {
            this.renderer.setEnforceSmmHotkeyCheck(bl);
        }
    }

    @Override
    public void setMouseSyncHotkey(String string, KeyStroke keyStroke) {
        KeyboardHandler keyboardHandler = RCCoreImpl.getKeyboardHandler(this.renderer);
        if (keyboardHandler != null) {
            keyboardHandler.setMouseSyncHotkey(string, keyStroke);
        }
    }

    @Override
    public void setHotkeys(Map<Integer, KeyStroke> map) {
        KeyboardHandler keyboardHandler = null;
        if (this.renderer != null) {
            keyboardHandler = RCCoreImpl.getKeyboardHandler(this.renderer);
        }
        if (keyboardHandler != null) {
            keyboardHandler.setHotkeys(map);
        }
    }

    @Override
    public List<Locale> getSupportedLocalKeyboardMappings() {
        KeyboardHandler keyboardHandler = RCCoreImpl.getKeyboardHandler(this.renderer);
        if (keyboardHandler != null) {
            return keyboardHandler.getLocalKeyboardMappings();
        }
        return null;
    }

    @Override
    public void setLocalKeyboardMapping(Locale locale) throws IOException {
        KeyboardHandler keyboardHandler = RCCoreImpl.getKeyboardHandler(this.renderer);
        if (keyboardHandler != null) {
            keyboardHandler.setLocalKeyboardMapping(locale);
        }
        if (this.localKeyboardMappingPersistent) {
            this.setLocalKeyboardMappingToRfb(locale);
        }
    }

    @Override
    public void sendKeyboardMacro(IKeyboardMacro iKeyboardMacro, boolean bl) {
        KeyboardHandler keyboardHandler;
        if (this.renderer != null && (keyboardHandler = RCCoreImpl.getKeyboardHandler(this.renderer)) != null) {
            keyboardHandler.sendKeyboardMacro(iKeyboardMacro, bl);
        }
    }

    @Override
    public void setOSD(String string, int n, boolean bl) {
        this.renderer.setOSD(string, n, bl);
    }

    @Override
    public Cursor getCursor() {
        if (this.renderer != null && this.renderer instanceof Component) {
            return ((Component)((Object)this.renderer)).getCursor();
        }
        return null;
    }

    @Override
    public void setCursor(Cursor cursor) {
        if (this.renderer != null && this.renderer instanceof Component) {
            ((Component)((Object)this.renderer)).setCursor(cursor);
        }
    }

    @Override
    public void setMonitorMode(boolean bl) {
        this.monitorMode = bl;
        if (this.rfbHandler != null) {
            this.rfbHandler.setMonitorOnly(bl);
        }
    }

    @Override
    public RCCore.Smoothing getSmoothing() {
        if (this.rfbHandler != null) {
            return this.rfbHandler.getRfbRenderer().getSmoothing();
        }
        return RfbRenderer.defaultSmoothing;
    }

    @Override
    public void setSmoothing(RCCore.Smoothing smoothing) {
        if (this.rfbHandler != null) {
            this.rfbHandler.getRfbRenderer().setSmoothing(smoothing);
        }
    }

    @Override
    public void setEncodingToAuto() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.setEncodingToAuto(true);
        }
    }

    @Override
    public void setEncodingPredefine(RCCore.Predefine predefine) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.setEncodingPredefine(predefine);
        }
    }

    @Override
    public void setEncodingColorDepth(RCCore.ColorDepth colorDepth, boolean bl) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.setEncodingColorDepth(colorDepth, bl);
        }
    }

    @Override
    public void setEncodingCompression(RCCore.Compression compression, boolean bl) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.setEncodingCompression(compression, bl);
        }
    }

    @Override
    public void setEncodingLossy(boolean bl, boolean bl2) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.setEncodingLossy(bl, bl2);
        }
    }

    @Override
    public void sendKeyboardEvent(int n, boolean bl) {
        if (this.rfbHandler != null) {
            this.rfbHandler.consumeKeyboardEvent(n, bl);
        }
    }

    @Override
    public void setExclusiveMode(boolean bl) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.sendPropertyChange("exclusive", bl ? "on" : "off");
        }
    }

    @Override
    public void setKeyboardMacroList(List<IKeyboardMacro> list) throws IOException {
        if (this.rfbHandler != null) {
            int n = 0;
            for (IKeyboardMacro iKeyboardMacro : list) {
                this.rfbHandler.sendPropertyChange("HOTKEY_" + n, iKeyboardMacro.getCodeWithConfirm());
                String string = iKeyboardMacro.getName();
                if (string != null) {
                    this.rfbHandler.sendPropertyChange("HOTKEYNAME_" + n, string);
                }
                this.rfbHandler.sendPropertyChange("HOTKEYCODE_" + n, iKeyboardMacro.getKeycodesAsString());
                ++n;
            }
        }
    }

    @Override
    public void setSoftKeyboardMapping(Locale locale) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.sendPropertyChange("softkbd_mapping", locale.toString());
        }
    }

    private void setLocalKeyboardMappingToRfb(Locale locale) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.sendPropertyChange("localkbd_mapping", locale.toString());
        }
    }

    @Override
    public void sendChatMessage(String string) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.sendChatMessage(string);
        }
    }

    @Override
    public void switchKvmPort(int n, String string) throws IOException, RCException {
        if (this.rfbHandler != null) {
            this.rfbHandler.switchKvmPort(n, string);
        }
    }

    @Override
    public void requestVideoSettingsUpdates() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.requestVideoSettingsUpdates();
        }
        this.videoSettingsRequested = true;
    }

    @Override
    public void stopVideoSettingsUpdates() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.stopVideoSettingsUpdates();
        }
        this.videoSettingsRequested = false;
    }

    @Override
    public void setVideoSettings(IVideoSettings iVideoSettings) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.setVideoSettings(iVideoSettings);
        }
    }

    @Override
    public void requestVideoAutoSense() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.requestVideoAutoSense();
        }
    }

    @Override
    public void requestVideoColorCalibration() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.requestVideoColorCalibration();
        }
    }

    @Override
    public void requestVideoRefresh() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.requestVideoRefresh();
        }
    }

    @Override
    public void saveVideoSettings() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.saveVideoSettings();
        }
    }

    @Override
    public void cancelVideoSettings() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.cancelVideoSettings();
        }
    }

    @Override
    public void resetVideoSettingsAllModes() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.resetVideoSettingsAllModes();
        }
    }

    @Override
    public void resetVideoSettingsThisMode() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.resetVideoSettingsThisMode();
        }
    }

    @Override
    public void resetVideoSettingsThisPort() throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.resetVideoSettingsThisPort();
        }
    }

    @Override
    public void mountOrUnmountRemoteIso(IVMMountRequestResponse iVMMountRequestResponse) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.mountOrUnmountRemoteIso(iVMMountRequestResponse);
        }
    }

    @Override
    public void setMouseMode(RCCore.MouseMode mouseMode) throws IOException {
        if (this.rfbHandler != null) {
            this.rfbHandler.setMouseMode(mouseMode);
        }
    }

    @Override
    public List<RCCore.ColorDepth> getSupportedColorDepthsForCompression(RCCore.Compression compression) {
        if (this.rfbHandler != null) {
            return this.rfbHandler.getSupportedColorDepthsForCompression(compression);
        }
        return null;
    }

    @Override
    public List<RCCore.Compression> getSupportedCompressionsForColorDepth(RCCore.ColorDepth colorDepth) {
        if (this.rfbHandler != null) {
            return this.rfbHandler.getSupportedCompressionsForColorDepth(colorDepth);
        }
        return null;
    }

    @Override
    public RCCore.ColorDepth getEncodingColorDepthForPredefine(RCCore.Predefine predefine) {
        if (this.rfbHandler != null) {
            return this.rfbHandler.getColorDepthForPredefine(predefine);
        }
        return null;
    }

    @Override
    public RCCore.Compression getEncodingCompressionForPredefine(RCCore.Predefine predefine) {
        if (this.rfbHandler != null) {
            return this.rfbHandler.getCompressionForPredefine(predefine);
        }
        return null;
    }

    @Override
    public boolean getEncodingLossyForPredefine(RCCore.Predefine predefine) {
        if (this.rfbHandler != null) {
            return this.rfbHandler.getLossyForPredefine(predefine);
        }
        return false;
    }

    @Override
    public void setX509TrustManager(X509TrustManager x509TrustManager) {
        this.trustManager = x509TrustManager;
    }

    @Override
    public void connectRCWithRdmSession(String string, int n, boolean bl, String string2, String string3, String string4, String string5) throws IOException, RCException {
        this.renderer.init();
        this.connectToHost(string, n, bl, string2, null, null, null, string3, string4, string5);
    }

    @Override
    public void connectRCWithEricKey(String string, int n, boolean bl, String string2, String string3) throws IOException, RCException {
        this.renderer.init();
        this.connectToHost(string, n, bl, string2, null, null, string3, null);
    }

    @Override
    public void connectRCWithUserLogin(String string, int n, boolean bl, String string2, String string3, String string4) throws IOException, RCException {
        this.renderer.init();
        this.connectToHost(string, n, bl, string2, string3, string4, null, null);
    }

    private void connectToHost(String string, int n, boolean bl, String string2, String string3, String string4, String string5, String string6, String string7, String string8) throws IOException, RCException {
        this.proxyConnectionId = string7;
        this.proxyUseSSL = string8;
        this.connectToHost(string, n, bl, string2, string3, string4, string5, string6);
    }

    private void connectToHost(String string, int n, boolean bl, String string2, String string3, String string4, String string5, String string6) throws IOException, RCException {
        this.listeners.notificationListenerList.fireTextNotification(MessageFormat.format(T._("Connecting to {0}..."), string));
        this.connector = this.trafficSpeedEnabled ? new MonitoringDeviceConnector(this.logger, this.trustManager, this.listeners.connectionEventListenerList) : new DeviceConnector(this.logger, this.trustManager);
        this.connector.connect(string, n, bl);
        if (this.proxyConnectionId != null && this.connector.getOutputStream() != null) {
            this.connector.writeCCSGproxyModePrefix(this.proxyConnectionId);
        }
        if (!bl && this.proxyConnectionId != null && this.proxyUseSSL != null) {
            if (this.proxyUseSSL.equals("yes")) {
                System.out.println("CC-Proxy[KVM] mode: SSL enabled .. \n");
                this.connector.connectSSLWithSocket(string, n);
            } else {
                System.out.println("CC-Proxy[KVM] mode: plaintext .. \n");
            }
        }
        try {
            Enum enum_;
            this.rfbHandler = RfbHandler.loadRfbHandler(this.connector, this.logger, this.listeners, this.renderer);
            this.rfbHandler.setClientSessionInitProps(this.clientSessionInitProps);
            this.rfbHandler.setLocalKeyboardMappingPersistent(this.localKeyboardMappingPersistent);
            this.rfbHandler.setLowBandwidth(this.lowBandwidth);
            this.rfbHandler.setFPS(this.fpsEnabled);
            if (this.customAuto) {
                this.rfbHandler.setEncodingToAuto(false);
            } else {
                RCCore.Compression compression;
                enum_ = this.rfbHandler.setCustomColorDepth(this.customColorDepth);
                if (this.customColorDepth != null && enum_ != null) {
                    this.customColorDepth = enum_;
                }
                if (this.customCompression != null && (compression = this.rfbHandler.setCustomCompression(this.customCompression)) != null) {
                    this.customCompression = compression;
                }
            }
            enum_ = this.rfbHandler.setCustomSmoothing(this.customSmoothing);
            if (enum_ != null) {
                this.customSmoothing = enum_;
            }
            this.rfbHandler.setAppletParmeterMap(this.appletParameterMap);
            this.rfbHandler.connect(string2, string3, string4, string5, string6);
            if (this.monitorMode) {
                this.rfbHandler.setMonitorOnly(true);
            }
        }
        catch (IOException iOException) {
            this.connector.disconnect();
            throw iOException;
        }
        catch (RCException rCException) {
            this.connector.disconnect();
            throw rCException;
        }
    }

    @Override
    public void disconnect() {
        if (this.renderer != null) {
            this.renderer.dispose();
        }
        if (this.connector != null) {
            this.connector.disconnect();
        }
        if (this.rfbHandler != null) {
            this.rfbHandler.close();
            this.rfbHandler.dispose();
            this.rfbHandler = null;
        }
        this.dispose();
    }

    @Override
    public void dispose() {
        this.removeVideoEventListener(this);
        this.removeAudioStatusListener(this);
        this.removeMouseModeListener(this);
        this.removeKeyboardInfoListener(this);
        this.removeVirtualMediaInfoListener(this);
        if (this.renderer != null) {
            this.renderer = null;
        }
        if (this.connector != null) {
            this.connector = null;
        }
        if (this.trustManager != null) {
            this.trustManager = null;
        }
        if (this.listeners != null) {
            this.listeners = null;
        }
        if (this.predefines != null) {
            this.predefines = null;
        }
        if (this.depths != null) {
            this.depths = null;
        }
        if (this.compressions != null) {
            this.compressions = null;
        }
        if (this.currentDepth != null) {
            this.currentDepth = null;
        }
        if (this.currentCompression != null) {
            this.currentCompression = null;
        }
        if (this.videoSettings != null) {
            this.videoSettings = null;
        }
        if (this.mouseModes != null) {
            this.mouseModes = null;
        }
        if (this.currentMouseMode != null) {
            this.currentMouseMode = null;
        }
        if (this.keyboardMacros != null) {
            this.keyboardMacros = null;
        }
        if (this.keyboardState != null) {
            this.keyboardState = null;
        }
        if (this.remoteIsoList != null) {
            this.remoteIsoList = null;
        }
        if (this.vmConfig != null) {
            this.vmConfig = null;
        }
        if (this.usbProfileList != null) {
            this.usbProfileList = null;
        }
        if (this.kvmPortList != null) {
            this.kvmPortList = null;
        }
        if (this.appletParameterMap != null) {
            this.appletParameterMap = null;
        }
    }

    @Override
    public void addVideoEventListener(VideoEventListener videoEventListener, int n) {
        if (this.listeners != null) {
            this.listeners.videoEventListenerList.addListener(videoEventListener, n);
        }
    }

    @Override
    public void removeVideoEventListener(VideoEventListener videoEventListener) {
        if (this.listeners != null) {
            this.listeners.videoEventListenerList.removeListener(videoEventListener);
        }
    }

    @Override
    public void addNotificationListener(NotificationListener notificationListener) {
        if (this.listeners != null) {
            this.listeners.notificationListenerList.addListener(notificationListener);
        }
    }

    @Override
    public void removeNotificationListener(NotificationListener notificationListener) {
        if (this.listeners != null) {
            this.listeners.notificationListenerList.removeListener(notificationListener);
        }
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener connectionEventListener, int n) {
        if (this.listeners != null) {
            this.listeners.connectionEventListenerList.addListener(connectionEventListener, n);
        }
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener connectionEventListener) {
        if (this.listeners != null) {
            this.listeners.connectionEventListenerList.removeListener(connectionEventListener);
        }
    }

    @Override
    public void addAudioStatusListener(AudioStatusListener audioStatusListener) {
        if (this.listeners != null) {
            this.listeners.audioStatusListenerList.addListener(audioStatusListener);
        }
    }

    @Override
    public void removeAudioStatusListener(AudioStatusListener audioStatusListener) {
        if (this.listeners != null) {
            this.listeners.audioStatusListenerList.removeListener(audioStatusListener);
        }
    }

    @Override
    public void addMouseModeListener(MouseModeListener mouseModeListener, int n) {
        if (this.listeners != null) {
            this.listeners.mouseModeListenerList.addListener(mouseModeListener, n);
        }
    }

    @Override
    public void removeMouseModeListener(MouseModeListener mouseModeListener) {
        if (this.listeners != null) {
            this.listeners.mouseModeListenerList.removeListener(mouseModeListener);
        }
    }

    @Override
    public void addKeyboardInfoListener(KeyboardInfoListener keyboardInfoListener, int n) {
        if (this.listeners != null) {
            this.listeners.keyboardInfoListenerList.addListener(keyboardInfoListener, n);
        }
    }

    @Override
    public void removeKeyboardInfoListener(KeyboardInfoListener keyboardInfoListener) {
        if (this.listeners != null) {
            this.listeners.keyboardInfoListenerList.removeListener(keyboardInfoListener);
        }
    }

    @Override
    public void addVirtualMediaInfoListener(VirtualMediaInfoListener virtualMediaInfoListener, int n) {
        if (this.listeners != null) {
            this.listeners.virtualMediaInfoListenerList.addListener(virtualMediaInfoListener, n);
        }
    }

    @Override
    public void removeVirtualMediaInfoListener(VirtualMediaInfoListener virtualMediaInfoListener) {
        if (this.listeners != null) {
            this.listeners.virtualMediaInfoListenerList.removeListener(virtualMediaInfoListener);
        }
    }

    @Override
    public void addKeyboardListener(KeyboardListener keyboardListener) {
        if (this.listeners != null) {
            this.listeners.keyboardListenerList.addListener(keyboardListener);
        }
    }

    @Override
    public void removeKeyboardListener(KeyboardListener keyboardListener) {
        if (this.listeners != null) {
            this.listeners.keyboardListenerList.removeListener(keyboardListener);
        }
    }

    @Override
    public void addMouseEventListener(MouseEventListener mouseEventListener, int n) {
        if (this.listeners != null) {
            this.listeners.mouseEventListenerList.addListener(mouseEventListener, n);
        }
    }

    @Override
    public void removeMouseEventListener(MouseEventListener mouseEventListener) {
        if (this.listeners != null) {
            this.listeners.mouseEventListenerList.removeListener(mouseEventListener);
        }
    }

    @Override
    public int getKeyCode(int n, char c, int n2) {
        KeyboardHandler keyboardHandler = RCCoreImpl.getKeyboardHandler(this.renderer);
        if (keyboardHandler != null) {
            return keyboardHandler.translateKeyEvent(n, c, n2);
        }
        return -1;
    }

    @Override
    public void setAppletParameterMap(LinkedHashMap<String, String> linkedHashMap) {
        this.appletParameterMap = linkedHashMap;
    }

    @Override
    public void setLowBandwidth(boolean bl) {
        this.lowBandwidth = bl;
    }

    @Override
    public void setConnectionProperties(boolean bl, RCCore.Compression compression, RCCore.ColorDepth colorDepth, RCCore.Smoothing smoothing) {
        this.customAuto = bl;
        this.customCompression = compression;
        this.customColorDepth = colorDepth;
        this.customSmoothing = smoothing;
    }

    @Override
    public void sendKeyboardEvent(KeyEvent keyEvent) throws RCException {
        if (keyEvent != null) {
            KeyboardHandler keyboardHandler = null;
            if (this.renderer != null) {
                keyboardHandler = RCCoreImpl.getKeyboardHandler(this.renderer);
            }
            if (keyboardHandler != null) {
                if (keyEvent.getID() == 401) {
                    keyboardHandler.keyPressed(keyEvent);
                } else if (keyEvent.getID() == 402) {
                    keyboardHandler.keyReleased(keyEvent);
                } else if (keyEvent.getID() == 400) {
                    keyboardHandler.keyTyped(keyEvent);
                } else {
                    throw new RCException("Invalid Keyboard Event Specified");
                }
            }
        }
    }

    @Override
    public void sendMouseEvent(boolean bl, int n, int n2, int n3) {
        if (bl) {
            this.rfbHandler.consumeRelativeMouseEvent(n, n2, n3);
        } else {
            this.rfbHandler.consumeAbsoluteMouseEvent(n, n2, n3);
        }
    }

    @Override
    public void setClientFrameSizeChanged(Dimension dimension) {
        this.clientFrameSize = dimension;
        this.renderer.setFrameSize(dimension);
    }

    @Override
    public void setClientSessionInitProperties(Map<RCCore.ClientSessionInitProperties, String> map) {
        HashMap<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> hashMap = new HashMap<IMultiMonitorTargetSupport.ClientSessionInitProperties, String>();
        for (RCCore.ClientSessionInitProperties clientSessionInitProperties : map.keySet()) {
            hashMap.put(IMultiMonitorTargetSupport.ClientSessionInitProperties.valueOf(clientSessionInitProperties.name()), map.get((Object)clientSessionInitProperties));
        }
        this.setClientSessionInitProperties2(hashMap);
    }

    public void setClientSessionInitProperties2(Map<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> map) {
        this.clientSessionInitProps = Collections.unmodifiableMap(map);
    }

    private static KeyboardHandler getKeyboardHandler(RemoteConsoleRenderer remoteConsoleRenderer) {
        if (remoteConsoleRenderer instanceof ISupportsKeyboardHandling) {
            return ((ISupportsKeyboardHandling)((Object)remoteConsoleRenderer)).getKeyboardHandler();
        }
        return null;
    }

    private static MouseHandler getMouseHandler(RemoteConsoleRenderer remoteConsoleRenderer) {
        if (remoteConsoleRenderer instanceof ISupportsMouseHandling) {
            return ((ISupportsMouseHandling)((Object)remoteConsoleRenderer)).getMouseHandler();
        }
        return null;
    }

    @Override
    public <CT> CT getCapablity(Class<CT> clazz) {
        if (clazz == ILicenseSupport.class) {
            return (CT)this.licenseSupport;
        }
        if (clazz == IAudioStatusExSupport.class) {
            return (CT)this.audioSettingsSupport;
        }
        if (clazz == IMultiMonitorTargetSupport.class) {
            return (CT)this.multiMonitorTargetSupport;
        }
        return null;
    }

    @Override
    public boolean isOsdDisabled() {
        return this.renderer != null ? this.renderer.isOsdDisabled() : false;
    }

    @Override
    public void setOsdDisabled(boolean bl) {
        if (this.renderer != null) {
            this.renderer.setOsdDisabled(bl);
        }
    }

    @Override
    public boolean isFPS() {
        return this.fpsEnabled;
    }

    @Override
    public boolean isTrafficSpeed() {
        return this.trafficSpeedEnabled;
    }

    @Override
    public void setFPS(boolean bl) {
        this.fpsEnabled = bl;
    }

    @Override
    public void setTrafficSpeed(boolean bl) {
        this.trafficSpeedEnabled = bl;
    }

    @Override
    public int getVideoUpdateDelay() {
        RfbHandler rfbHandler = this.rfbHandler;
        if (rfbHandler != null) {
            return rfbHandler.getVideoUpdateDelay();
        }
        return 0;
    }

    @Override
    public void setVideoUpdateDelay(int n) {
        RfbHandler rfbHandler = this.rfbHandler;
        if (rfbHandler != null) {
            rfbHandler.setVideoUpdateDelay(n);
        }
    }

    @Override
    public Object getClientProperty(Object object) {
        return this.clientProperties.get(object);
    }

    @Override
    public void putClientProperty(Object object, Object object2) {
        this.clientProperties.put(object, object2);
    }
}

