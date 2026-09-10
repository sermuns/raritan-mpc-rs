/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.NotificationEvent;
import nn.pp.core.T;
import nn.pp.core.impl.DeviceConnector;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolHandler;
import nn.pp.rccore.IAudioStatusExSupport;
import nn.pp.rccore.IMultiMonitorTargetSupport;
import nn.pp.rccore.IVMMountRequestResponse;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.KeyboardMacro;
import nn.pp.rccore.KvmPort;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.RCException;
import nn.pp.rccore.UsbProfile;
import nn.pp.rccore.UsbProfileList;
import nn.pp.rccore.VMConfigInfo;
import nn.pp.rccore.VMMountRequestResponse;
import nn.pp.rccore.VideoSettings;
import nn.pp.rccore.impl.FramebufferRenewer;
import nn.pp.rccore.impl.ISupportsKeyboardHandling;
import nn.pp.rccore.impl.ISupportsMouseHandling;
import nn.pp.rccore.impl.KeyValuePair;
import nn.pp.rccore.impl.ListenerLists;
import nn.pp.rccore.impl.RemoteConsoleRenderer;
import nn.pp.rccore.impl.StringLocale;
import nn.pp.rccore.impl.keyboard.KeyboardEventConsumer;
import nn.pp.rccore.impl.mouse.MouseEventConsumer;
import nn.pp.rccore.impl.rfb.ImageDecoder;
import nn.pp.rccore.impl.rfb.ImageDecoderHextile;
import nn.pp.rccore.impl.rfb.ImageDecoderLrle;
import nn.pp.rccore.impl.rfb.ImageDecoderRaw;
import nn.pp.rccore.impl.rfb.ImageDecoderRawVsc;
import nn.pp.rccore.impl.rfb.ImageDecoderTight;
import nn.pp.rccore.impl.rfb.PingTimer;
import nn.pp.rccore.impl.rfb.RfbEncoding;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;
import nn.pp.rccore.impl.rfb.RfbRenderer;
import nn.pp.rccore.impl.rfb.RfbVersionNegotiator;
import nn.pp.rccore.impl.rfb.RfbVideoSettingsHandler;
import nn.pp.rccore.impl.rfb.ZlibEncodingInflater;

public abstract class RfbHandler
extends ProtocolHandler<RCException>
implements FramebufferRenewer,
MouseEventConsumer,
KeyboardEventConsumer {
    protected ListenerLists listeners;
    protected RemoteConsoleRenderer renderer;
    protected RfbEncoding encoding;
    protected RfbVideoSettingsHandler videoSettingsHandler;
    protected UsbProfileList usbProfileList;
    protected VMConfigInfo vmConfig;
    private boolean monitorOnly = false;
    protected boolean lowBandwidth = false;
    private boolean fpsEnabled;
    private int fps = 0;
    private TimerTask pingTimerTask;
    private Timer fpsTimer;
    private byte[] framebufferUpdateRectBuffer = null;
    private ZlibEncodingInflater inflater;
    protected String osdText;
    protected int osdTimeout;
    protected boolean osdBlank;
    protected int framebufferWidth;
    protected int framebufferHeight;
    protected int framebufferWidthPadded;
    protected int framebufferHeightPadded;
    protected String serverCommandName;
    protected String serverCommandValue;
    protected int noFramebufferUpdateRects;
    protected int framebufferUpdateSize;
    protected int framebufferUpdateFlags;
    protected int framebufferUpdateRectX;
    protected int framebufferUpdateRectY;
    protected int framebufferUpdateRectW;
    protected int framebufferUpdateRectH;
    protected int framebufferUpdateRectEncoding;
    protected int framebufferUpdateRectSize;
    private int hotkeyHighestIndex = 0;
    private String[] hotkeyKeyCodes;
    private String[] hotkeyReadableCodes;
    private String[] hotkeyNames;
    protected boolean videoSettingsAvailable = false;
    protected boolean videoRefreshAvailable = false;
    protected boolean videoSettingsOffsetOnly = false;
    protected boolean videoSettingsPermStandard = false;
    protected boolean videoSettingsPermFull = false;
    protected boolean colorCalibration = false;
    private String mouseSyncKeyCode;
    private String mouseSyncReadableCode;
    private boolean localKeyboardMappingPersistent = false;
    protected List<KvmPort> portList;
    protected RfbPixelFormat currentRfbPixelFormat = RfbPixelFormat.rfbPixelFormat16Bit;
    private List<RCCore.MouseMode> supportedMouseModes = new Vector<RCCore.MouseMode>();
    private List<RCCore.KeyboardLed> keyboardLeds = new Vector<RCCore.KeyboardLed>();
    protected LinkedHashMap<String, String> appletParameterMap;
    protected UsbProfile selectedUsbProfile;
    protected List<UsbProfile> usbProfiles;
    protected MonitoringDataInputStream isFbUpd;
    protected MonitoringDataInputStream isFbUpdRect;
    private ImageDecoder imageDecoderLrle;
    private ImageDecoder imageDecoderRaw;
    private ImageDecoder imageDecoderRawVsc;
    private ImageDecoder imageDecoderHextile;
    private ImageDecoder imageDecoderTight;
    private RfbRenderer rfbRenderer;
    RCCore.MouseMode currentMouseMode;
    private RCCore.ColorDepth customColorDepth = null;
    private RCCore.Compression customCompression = null;
    private RCCore.Smoothing customSmoothing = null;
    private int videoUpdateDelay;
    private Map<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> clientSessionInitProps;

    public static RfbHandler loadRfbHandler(DeviceConnector deviceConnector, Logger logger, ListenerLists listenerLists, RemoteConsoleRenderer remoteConsoleRenderer) throws IOException, RCException {
        RfbVersionNegotiator rfbVersionNegotiator = new RfbVersionNegotiator();
        rfbVersionNegotiator.init(deviceConnector, logger, listenerLists, null);
        rfbVersionNegotiator.negotiateProtocolVersion();
        RfbHandler rfbHandler = RfbHandler.loadRfbHandler(deviceConnector, logger, listenerLists, remoteConsoleRenderer, rfbVersionNegotiator.versionMajor, rfbVersionNegotiator.versionMinor);
        rfbHandler.versionMajor = rfbVersionNegotiator.versionMajor;
        rfbHandler.versionMinor = rfbVersionNegotiator.versionMinor;
        return rfbHandler;
    }

    public static RfbHandler loadRfbHandler(DeviceConnector deviceConnector, Logger logger, ListenerLists listenerLists, RemoteConsoleRenderer remoteConsoleRenderer, int n, int n2) throws IOException, RCException {
        RfbHandler rfbHandler;
        try {
            String string = RfbHandler.getVersionString(n, n2, '_');
            String string2 = "nn.pp.rccore.impl.rfb.V" + string + ".RfbHandlerV" + string;
            logger.log(Level.FINE, T._("Trying to load protocol handler:") + " " + string2);
            rfbHandler = (RfbHandler)Class.forName(string2).newInstance();
        }
        catch (Throwable throwable) {
            String string = T._("Unable to load protocol handler!");
            logger.log(Level.SEVERE, string, throwable);
            throw new RCException(string);
        }
        rfbHandler.init(deviceConnector, logger, listenerLists, remoteConsoleRenderer);
        return rfbHandler;
    }

    public void setAppletParmeterMap(LinkedHashMap<String, String> linkedHashMap) {
    }

    protected Map<String, String> getCapabilityMap() {
        return null;
    }

    @Override
    protected String getProtocolName() {
        return "RFB";
    }

    @Override
    protected RCException loadException(String string) {
        return new RCException(string);
    }

    public RfbRenderer getRfbRenderer() {
        return this.rfbRenderer;
    }

    @Override
    protected void disconnected(Exception exception) {
        if (this.listeners != null) {
            this.listeners.connectionEventListenerList.fireDisconnected(exception);
        }
    }

    public void init(DeviceConnector deviceConnector, Logger logger, ListenerLists listenerLists, RemoteConsoleRenderer remoteConsoleRenderer) throws IOException {
        this.listeners = listenerLists;
        this.renderer = remoteConsoleRenderer;
        super.init(deviceConnector, logger);
        if (remoteConsoleRenderer != null) {
            remoteConsoleRenderer.setRenewer(this);
            if (remoteConsoleRenderer instanceof ISupportsMouseHandling) {
                ((ISupportsMouseHandling)((Object)remoteConsoleRenderer)).setMouseEventConsumer(this);
            }
            if (remoteConsoleRenderer instanceof ISupportsKeyboardHandling) {
                ((ISupportsKeyboardHandling)((Object)remoteConsoleRenderer)).setKeyboardEventConsumer(this);
            }
            this.inflater = new ZlibEncodingInflater(logger);
            this.rfbRenderer = new RfbRenderer(remoteConsoleRenderer, logger);
            remoteConsoleRenderer.setImageProvider(this.rfbRenderer);
        }
    }

    @Override
    public void dispose() {
        if (this.pingTimerTask != null) {
            this.pingTimerTask.cancel();
        }
        if (this.fpsTimer != null) {
            this.fpsTimer.cancel();
        }
        if (this.encoding != null) {
            this.encoding = null;
        }
        if (this.renderer != null) {
            this.renderer.dispose();
            this.renderer = null;
        }
        if (this.rfbRenderer != null) {
            this.rfbRenderer.dispose();
            this.rfbRenderer = null;
        }
        if (this.listeners != null) {
            this.listeners = null;
        }
        if (this.videoSettingsHandler != null) {
            this.videoSettingsHandler = null;
        }
        if (this.usbProfileList != null) {
            this.usbProfileList = null;
        }
        if (this.vmConfig != null) {
            this.vmConfig = null;
        }
        if (this.framebufferUpdateRectBuffer != null) {
            this.framebufferUpdateRectBuffer = null;
        }
        if (this.inflater != null) {
            this.inflater = null;
        }
        if (this.portList != null) {
            this.portList = null;
        }
        if (this.currentRfbPixelFormat != null) {
            this.currentRfbPixelFormat = null;
        }
        if (this.supportedMouseModes != null) {
            this.supportedMouseModes = null;
        }
        if (this.keyboardLeds != null) {
            this.keyboardLeds = null;
        }
        if (this.appletParameterMap != null) {
            this.appletParameterMap = null;
        }
        if (this.selectedUsbProfile != null) {
            this.selectedUsbProfile = null;
        }
        if (this.usbProfiles != null) {
            this.usbProfiles = null;
        }
        if (this.isFbUpd != null) {
            this.isFbUpd = null;
        }
        if (this.isFbUpdRect != null) {
            this.isFbUpdRect = null;
        }
        if (this.imageDecoderLrle != null) {
            this.imageDecoderLrle = null;
        }
        if (this.imageDecoderRaw != null) {
            this.imageDecoderRaw = null;
        }
        if (this.imageDecoderRawVsc != null) {
            this.imageDecoderRawVsc = null;
        }
        if (this.imageDecoderHextile != null) {
            this.imageDecoderHextile = null;
        }
        if (this.imageDecoderTight != null) {
            this.imageDecoderTight = null;
        }
        if (this.isFbUpd != null) {
            this.isFbUpd = null;
        }
        System.gc();
        System.runFinalization();
        super.dispose();
    }

    protected void sendIncrementalFramebufferUpdateRequest() throws IOException {
        this.writeFramebufferUpdateRequest(0, 0, this.framebufferWidth, this.framebufferHeight, true);
    }

    protected void sendFullFramebufferUpdateRequest() throws IOException {
        this.writeFramebufferUpdateRequest(0, 0, this.framebufferWidth, this.framebufferHeight, false);
    }

    @Override
    protected void initialHandshakeFinished() throws IOException {
        super.initialHandshakeFinished();
        this.pingTimerTask = new TimerTask(){
            int serial = 0;

            @Override
            public void run() {
                try {
                    RfbHandler.this.writePingRequest(this.serial++);
                }
                catch (IOException iOException) {
                    RfbHandler.this.logger.log(Level.WARNING, T._("Could not write ping request"), iOException);
                }
            }
        };
        PingTimer.schedule(this.pingTimerTask);
        if (this.fpsEnabled) {
            this.fpsTimer = new Timer("FPS");
            this.fpsTimer.schedule(new TimerTask(){

                @Override
                public void run() {
                    int n = RfbHandler.this.getAndClearFps();
                    RfbHandler.this.listeners.connectionEventListenerList.fireFramesPerSecond(n);
                }
            }, 1000L, 1000L);
        }
        this.listeners.connectionEventListenerList.fireProtocolVersionChanged(RfbHandler.getVersionString(this.versionMajor, this.versionMinor, '.'));
        this.listeners.connectionEventListenerList.fireConnected();
        this.listeners.notificationListenerList.fireTextNotification(MessageFormat.format(T._("Successfully connected to {0}"), this.host));
    }

    private synchronized void increaseFps() {
        ++this.fps;
    }

    private synchronized int getAndClearFps() {
        int n = this.fps;
        this.fps = 0;
        return n;
    }

    public int readCompactLen() throws IOException {
        int n = this.is.readUnsignedByte();
        int n2 = n & 0x7F;
        if ((n & 0x80) != 0) {
            n = this.is.readUnsignedByte();
            n2 |= (n & 0x7F) << 7;
            if ((n & 0x80) != 0) {
                n = this.is.readUnsignedByte();
                n2 |= (n & 0xFF) << 14;
            }
        }
        return n2;
    }

    public void setMonitorOnly(boolean bl) {
        this.monitorOnly = bl;
    }

    public void setEncodingToAuto(boolean bl) throws IOException {
        this.encoding.setEncodingToAuto();
        if (bl) {
            this.writeSetEncodingsMsg(this.encoding.getRfbEncodings());
            this.writeSetPixelFormatMsg(this.encoding.getRfbPixelFormat());
            this.sendFullFramebufferUpdateRequest();
        }
    }

    public void setEncodingPredefine(RCCore.Predefine predefine) throws IOException {
        this.encoding.setPredefine(predefine);
        this.writeSetEncodingsMsg(this.encoding.getRfbEncodings());
        this.writeSetPixelFormatMsg(this.encoding.getRfbPixelFormat());
        this.sendFullFramebufferUpdateRequest();
    }

    public void setEncodingColorDepth(RCCore.ColorDepth colorDepth, boolean bl) throws IOException {
        this.encoding.setColorDepth(colorDepth);
        if (bl) {
            this.writeSetEncodingsMsg(this.encoding.getRfbEncodings());
            this.writeSetPixelFormatMsg(this.encoding.getRfbPixelFormat());
            this.sendFullFramebufferUpdateRequest();
        }
    }

    public void setEncodingCompression(RCCore.Compression compression, boolean bl) throws IOException {
        this.encoding.setCompression(compression);
        if (bl) {
            this.writeSetEncodingsMsg(this.encoding.getRfbEncodings());
            this.writeSetPixelFormatMsg(this.encoding.getRfbPixelFormat());
            this.sendFullFramebufferUpdateRequest();
        }
    }

    public void setEncodingLossy(boolean bl, boolean bl2) throws IOException {
        this.encoding.setLossy(bl);
        if (bl2) {
            this.writeSetEncodingsMsg(this.encoding.getRfbEncodings());
            this.writeSetPixelFormatMsg(this.encoding.getRfbPixelFormat());
            this.sendFullFramebufferUpdateRequest();
        }
    }

    public void sendPropertyChange(String string, String string2) throws IOException {
        this.writeUserPropChangeEvent(string, string2);
    }

    public void setLocalKeyboardMappingPersistent(boolean bl) {
        this.localKeyboardMappingPersistent = bl;
    }

    public void requestVideoSettingsUpdates() throws IOException {
        this.videoSettingsHandler.requestVideoSettingsUpdates();
    }

    public void stopVideoSettingsUpdates() throws IOException {
        this.videoSettingsHandler.stopVideoSettingsUpdates();
    }

    public void setVideoSettings(IVideoSettings iVideoSettings) throws IOException {
        this.videoSettingsHandler.setVideoSettings(iVideoSettings);
    }

    public void requestVideoAutoSense() throws IOException {
        this.videoSettingsHandler.requestVideoAutoSense();
    }

    public void requestVideoColorCalibration() throws IOException {
        this.videoSettingsHandler.requestVideoColorCalibration();
    }

    public void requestVideoRefresh() throws IOException {
        this.writeVideoRefreshMsg();
    }

    public void saveVideoSettings() throws IOException {
        this.videoSettingsHandler.saveVideoSettings();
    }

    public void cancelVideoSettings() throws IOException {
        this.videoSettingsHandler.cancelVideoSettings();
    }

    public void resetVideoSettingsAllModes() throws IOException {
        this.videoSettingsHandler.resetVideoSettingsAllModes();
    }

    public void resetVideoSettingsThisMode() throws IOException {
        this.videoSettingsHandler.resetVideoSettingsThisMode();
    }

    public void resetVideoSettingsThisPort() throws IOException {
        this.videoSettingsHandler.resetVideoSettingsThisPort();
    }

    public void sendChatMessage(String string) throws IOException {
        this.writeUtf8StringMsg(string);
    }

    public void switchKvmPort(int n, String string) throws IOException, RCException {
        this.writeKvmSwitchEventMsg(n, string);
    }

    public List<RCCore.ColorDepth> getSupportedColorDepthsForCompression(RCCore.Compression compression) {
        return this.encoding.getSupportedColorDepthsForCompression(compression);
    }

    public List<RCCore.Compression> getSupportedCompressionsForColorDepth(RCCore.ColorDepth colorDepth) {
        return this.encoding.getSupportedCompressionsForColorDepth(colorDepth);
    }

    public RCCore.ColorDepth getColorDepthForPredefine(RCCore.Predefine predefine) {
        return this.encoding.getColorDepthForPredefine(predefine);
    }

    public RCCore.Compression getCompressionForPredefine(RCCore.Predefine predefine) {
        return this.encoding.getCompressionForPredefine(predefine);
    }

    public boolean getLossyForPredefine(RCCore.Predefine predefine) {
        return this.encoding.getLossyForPredefine(predefine);
    }

    public void mountOrUnmountRemoteIso(IVMMountRequestResponse iVMMountRequestResponse) throws IOException {
        this.writeVmMountsRequest(iVMMountRequestResponse);
    }

    public void setMouseMode(RCCore.MouseMode mouseMode) throws IOException {
        String string;
        if (mouseMode == RCCore.MouseMode.ABSOLUTE) {
            string = "absolute";
        } else if (mouseMode == RCCore.MouseMode.AUTOMATIC) {
            string = "auto";
        } else if (mouseMode == RCCore.MouseMode.STANDARD) {
            string = "direct";
        } else {
            return;
        }
        this.writeSetConnectionParameterMsg("current_mouse_mode", string);
    }

    public void changeUsbProfile(int n) throws IOException {
        this.writeUsbProfileSelect(n);
    }

    @Override
    public void renewFramebuffer() {
        if (this.connected && !this.inInitialHandshake) {
            try {
                this.sendFullFramebufferUpdateRequest();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    public void consumeAbsoluteMouseEvent(int n, int n2, int n3) {
        if (this.connected && !this.inInitialHandshake && !this.monitorOnly) {
            try {
                this.writePointerEvent(false, n, n2, 0, n3);
            }
            catch (IOException iOException) {
                this.logger.log(Level.SEVERE, T._("Could not write mouse event!"), iOException);
            }
        }
    }

    @Override
    public void consumeRelativeMouseEvent(int n, int n2, int n3) {
        if (this.connected && !this.inInitialHandshake && !this.monitorOnly) {
            try {
                this.writePointerEvent(true, n, n2, 0, n3);
            }
            catch (IOException iOException) {
                this.logger.log(Level.SEVERE, T._("Could not write mouse event!"), iOException);
            }
        }
    }

    @Override
    public void consumeMouseWheelEvent(int n, int n2) {
        if (this.connected && !this.inInitialHandshake && !this.monitorOnly) {
            try {
                if (this.currentMouseMode == RCCore.MouseMode.ABSOLUTE) {
                    this.writePointerEvent(false, 0, 0, n, n2);
                } else {
                    this.writePointerEvent(true, 0, 0, n, n2);
                }
            }
            catch (IOException iOException) {
                this.logger.log(Level.SEVERE, T._("Could not write mouse event!"), iOException);
            }
        }
    }

    @Override
    public void handleMouseSync(RCCore.MouseSyncType mouseSyncType) {
        if (this.connected && !this.inInitialHandshake && !this.monitorOnly) {
            int n = mouseSyncType == RCCore.MouseSyncType.FAST ? 2 : (mouseSyncType == RCCore.MouseSyncType.HARD ? 1 : 0);
            try {
                this.writeMouseSyncEvent(n);
            }
            catch (IOException iOException) {
                this.logger.log(Level.SEVERE, T._("Could not write mouse sync event!"), iOException);
            }
        }
    }

    @Override
    public void consumeKeyboardEvent(int n, boolean bl) {
        if (this.connected && !this.inInitialHandshake && !this.monitorOnly) {
            try {
                this.writeKeyboardEvent(n, bl);
            }
            catch (IOException iOException) {
                this.logger.log(Level.SEVERE, T._("Could not write keyboard event!"), iOException);
            }
        }
    }

    public void hotkeyDetected(int n) {
    }

    protected boolean notificationIsQuit() {
        return false;
    }

    private static String getQuitReasonMsg(int n) {
        switch (n) {
            case 1: {
                return T._("No Permission");
            }
            case 2: {
                return T._("Exclusive Access Active");
            }
            case 3: {
                return T._("Manually Rejected");
            }
            case 4: {
                return T._("Server Password Disabled");
            }
            case 5: {
                return T._("Loopback Connection is Senseless");
            }
            case 6: {
                return T._("Authentication Failed");
            }
            case 7: {
                return T._("Access to this KVM Port Denied");
            }
            case 8: {
                return T._("Too Many Clients Active Simultaneously");
            }
            case 16: {
                return T._("Unexpected Server Error");
            }
            case 17: {
                return T._("Bad Protocol Version");
            }
            case 18: {
                return T._("Protocol Error");
            }
            case 19: {
                return T._("Internal Server Error");
            }
            case 20: {
                return T._("Wrong Replay Parameters");
            }
            case 21: {
                return T._("Unable to Establish SSL Connection");
            }
        }
        return T._("Unknown Reason");
    }

    private static String[] resizeArray(String[] stringArray, int n) {
        int n2 = Array.getLength(stringArray);
        Object object = Array.newInstance(stringArray.getClass().getComponentType(), n);
        int n3 = Math.min(n2, n);
        if (n3 > 0) {
            System.arraycopy(stringArray, 0, object, 0, n3);
        }
        return (String[])object;
    }

    private boolean portListContains(String string) {
        if (this.portList != null) {
            for (KvmPort kvmPort : this.portList) {
                if (!kvmPort.equals(string)) continue;
                return true;
            }
        }
        return false;
    }

    protected void processConnectionParameter(List<KeyValuePair<String, String>> list) throws RCException {
        for (KeyValuePair<String, String> keyValuePair : list) {
            String string = keyValuePair.getKey();
            String string2 = keyValuePair.getValue();
            this.processConnectionParameter(string, string2);
        }
    }

    protected boolean processConnectionParameter(String string, String string2) throws RCException {
        String[] stringArray;
        if (string2.equals("")) {
            return false;
        }
        if (string.equalsIgnoreCase("hwenc") || string.equalsIgnoreCase("hw_enc")) {
            boolean bl = string2.equalsIgnoreCase("yes");
            this.listeners.videoEventListenerList.fireEncodingLossySupportedChanged(bl);
            this.listeners.videoEventListenerList.fireEncodingAutoSupportedChanged(true);
            this.listeners.videoEventListenerList.fireSupportedEncodingPredefinesChanged(this.encoding.getSupportedPredefines());
            this.listeners.videoEventListenerList.fireSupportedEncodingColorDepthsChanged(this.encoding.getSupportedColorDepths());
            this.listeners.videoEventListenerList.fireSupportedEncodingCompressionsChanged(this.encoding.getSupportedCompressions());
            if (this.encoding != null) {
                this.encoding.hwEncSupported(bl);
            }
            return true;
        }
        if (string.equalsIgnoreCase("selenc")) {
            if (this.lowBandwidth) {
                this.encoding.setEncodingTypeFromRfb("preconf");
            } else {
                this.encoding.setEncodingTypeFromRfb(string2);
            }
            return true;
        }
        if (string.equalsIgnoreCase("fixenc")) {
            if (this.lowBandwidth) {
                this.encoding.setEncodingPredefineFromRfb("modem_4bit");
            } else {
                this.encoding.setEncodingPredefineFromRfb(string2);
            }
            return true;
        }
        if (string.equalsIgnoreCase("AdvEncCR")) {
            this.encoding.setEncodingCompressionFromRfb(string2);
            return true;
        }
        if (string.equalsIgnoreCase("AdvEncCD")) {
            this.encoding.setEncodingColorDepthFromRfb(string2);
            return true;
        }
        if (string.equalsIgnoreCase("BOARD_NAME")) {
            this.listeners.connectionEventListenerList.fireDeviceNameChanged(string2);
            return true;
        }
        if (string.equalsIgnoreCase("vs_enc")) {
            this.listeners.videoEventListenerList.fireEncodingChangeAllowed(string2.equalsIgnoreCase("yes"));
            return true;
        }
        if (string.equalsIgnoreCase("drive_redirection_no_drives")) {
            int n = Integer.parseInt(string2);
            this.listeners.virtualMediaInfoListenerList.fireVirtualMediaSupportChanged(n != 0);
            this.listeners.virtualMediaInfoListenerList.fireVirtualMediaDriveCountChanged(n);
            return true;
        }
        if (string.equalsIgnoreCase("drive_redirection_read_only")) {
            this.listeners.virtualMediaInfoListenerList.fireVirtualMediaReadOnlyChanged(string2.equalsIgnoreCase("yes"));
            return true;
        }
        if (string.equalsIgnoreCase("audio_support")) {
            this.listeners.audioStatusListenerList.fireAudioSupportChanged(string2.equalsIgnoreCase("yes"));
        }
        if (string.equalsIgnoreCase("osd_bgcolor")) {
            Color color = new Color(Integer.valueOf(string2.substring(1), 16));
            this.renderer.setOsdBgColor(color);
            return true;
        }
        if (string.equalsIgnoreCase("osd_fgcolor")) {
            Color color = new Color(Integer.valueOf(string2.substring(1), 16));
            this.renderer.setOsdFgColor(color);
            return true;
        }
        if (string.equalsIgnoreCase("osd_alpha")) {
            int n = Integer.valueOf(string2, 10);
            this.renderer.setOsdAlpha(n);
            return true;
        }
        if (string.equalsIgnoreCase("osd_position")) {
            this.renderer.setOsdPosition(string2);
            return true;
        }
        if (string.toLowerCase().startsWith("hotkey_")) {
            String string3;
            int n;
            if (this.hotkeyReadableCodes == null) {
                this.hotkeyReadableCodes = new String[1];
            }
            if (this.hotkeyHighestIndex < (n = Integer.valueOf(string3 = string.substring("hotkey_".length()), 10).intValue())) {
                this.hotkeyHighestIndex = n;
            }
            this.hotkeyReadableCodes = RfbHandler.resizeArray(this.hotkeyReadableCodes, n + 1);
            this.hotkeyReadableCodes[n] = new String(string2);
            return true;
        }
        if (string.toLowerCase().startsWith("hotkeycode_")) {
            if (this.hotkeyKeyCodes == null) {
                this.hotkeyKeyCodes = new String[1];
            }
            String string4 = string.substring("hotkeycode_".length());
            int n = Integer.valueOf(string4, 10);
            this.hotkeyKeyCodes = RfbHandler.resizeArray(this.hotkeyKeyCodes, n + 1);
            this.hotkeyKeyCodes[n] = new String(string2);
            return true;
        }
        if (string.toLowerCase().startsWith("hotkeyname_")) {
            if (this.hotkeyNames == null) {
                this.hotkeyNames = new String[1];
            }
            String string5 = string.substring("hotkeyname_".length());
            int n = Integer.valueOf(string5, 10);
            this.hotkeyNames = RfbHandler.resizeArray(this.hotkeyNames, n + 1);
            this.hotkeyNames[n] = new String(string2);
            return true;
        }
        if (string.equalsIgnoreCase("localkbd_mapping")) {
            if (this.localKeyboardMappingPersistent && string2.length() > 0) {
                Locale locale = StringLocale.loadLocale(string2);
                this.listeners.keyboardInfoListenerList.fireLocalKeyboardMappingChanged(locale);
            }
            return true;
        }
        if (string.equalsIgnoreCase("softkbd_mapping")) {
            if (string2.length() > 0) {
                Locale locale = StringLocale.loadLocale(string2);
                this.listeners.keyboardInfoListenerList.fireSoftKeyboardMappingChanged(locale);
            }
            return true;
        }
        if (string.equalsIgnoreCase("video_settings")) {
            this.videoSettingsAvailable = string2.equalsIgnoreCase("yes");
            return true;
        }
        if (string.equalsIgnoreCase("refresh_video")) {
            this.videoRefreshAvailable = string2.equalsIgnoreCase("yes");
            return true;
        }
        if (string.equalsIgnoreCase("vs_type")) {
            this.videoSettingsOffsetOnly = string2.equalsIgnoreCase("offset");
            return true;
        }
        if (string.equalsIgnoreCase("vs_perm_std")) {
            this.videoSettingsPermStandard = string2.equalsIgnoreCase("yes");
            return true;
        }
        if (string.equalsIgnoreCase("vs_perm_adv")) {
            this.videoSettingsPermFull = string2.equalsIgnoreCase("yes");
            return true;
        }
        if (string.equalsIgnoreCase("color_calibration")) {
            this.colorCalibration = string2.equalsIgnoreCase("yes");
            return true;
        }
        if (string.equalsIgnoreCase("eth_gigabit")) {
            this.listeners.connectionEventListenerList.fireEthernetGigabitSupported(string2.equalsIgnoreCase("yes"));
            return true;
        }
        if (string.equalsIgnoreCase("mousesync_key")) {
            this.mouseSyncReadableCode = string2;
            return true;
        }
        if (string.equalsIgnoreCase("mousesync_keycode")) {
            this.mouseSyncKeyCode = string2;
            return true;
        }
        if (string.equalsIgnoreCase("use_iip")) {
            this.listeners.mouseModeListenerList.fireMouseSyncSupportChanged(string2.equalsIgnoreCase("yes"));
            this.listeners.mouseModeListenerList.fireSingleCursorModeSupportChanged(string2.equalsIgnoreCase("yes"));
            return true;
        }
        if (string.equalsIgnoreCase("EXCLUSIVE_MOUSE")) {
            this.listeners.mouseModeListenerList.fireSingleCursorModeChanged(string2.equalsIgnoreCase("yes"));
        }
        if (string.equalsIgnoreCase("monitor_mode")) {
            if (string2.equalsIgnoreCase("yes")) {
                this.listeners.connectionEventListenerList.fireMonitorModeChanged(true);
                this.listeners.connectionEventListenerList.fireMonitorModePermissionChanged(true);
            } else if (string2.equalsIgnoreCase("sticky")) {
                this.listeners.connectionEventListenerList.fireMonitorModeChanged(true);
                this.listeners.connectionEventListenerList.fireMonitorModePermissionChanged(false);
            } else {
                this.listeners.connectionEventListenerList.fireMonitorModeChanged(false);
                this.listeners.connectionEventListenerList.fireMonitorModePermissionChanged(true);
            }
            return true;
        }
        if (string.equalsIgnoreCase("exclusive_perm")) {
            if (string2.equalsIgnoreCase("on")) {
                this.listeners.connectionEventListenerList.fireExclusiveModeChanged(true);
                this.listeners.connectionEventListenerList.fireExclusiveModePermissionChanged(true);
            } else if (string2.equalsIgnoreCase("off")) {
                this.listeners.connectionEventListenerList.fireExclusiveModeChanged(false);
                this.listeners.connectionEventListenerList.fireExclusiveModePermissionChanged(true);
            } else if (string2.equalsIgnoreCase("sticky")) {
                this.listeners.connectionEventListenerList.fireExclusiveModeChanged(true);
                this.listeners.connectionEventListenerList.fireExclusiveModePermissionChanged(false);
            } else if (string2.equalsIgnoreCase("no")) {
                this.listeners.connectionEventListenerList.fireExclusiveModeChanged(false);
                this.listeners.connectionEventListenerList.fireExclusiveModePermissionChanged(false);
            }
            return true;
        }
        if (string.equalsIgnoreCase("mouse_caps")) {
            this.listeners.mouseModeListenerList.fireMouseModeChangeSupportChanged(string2.equalsIgnoreCase("yes"));
            return true;
        }
        if (string.equalsIgnoreCase("vmmount_caps")) {
            this.listeners.virtualMediaInfoListenerList.fireRemoteIsoSupportChanged(string2.equalsIgnoreCase("yes"));
            return true;
        }
        if (string.toLowerCase().startsWith("license_") && (stringArray = string.split("_")).length == 2) {
            this.listeners.licenseSupportListenerList.fireLicenseFeatureSupportChanged(stringArray[1], string2.equalsIgnoreCase("yes"));
        }
        if (string.equalsIgnoreCase("charset")) {
            if (string2.length() > 0) {
                T.setCharset(string2);
            }
            return true;
        }
        if (string.equalsIgnoreCase("cim_lang_option")) {
            this.listeners.connectionEventListenerList.fireCimLanguageOptionsSupported(string2.equalsIgnoreCase("yes"));
            return true;
        }
        if (string.equalsIgnoreCase("devicename")) {
            this.listeners.connectionEventListenerList.fireDeviceNameChanged(string2);
            return true;
        }
        return false;
    }

    protected boolean processServerCommand(String string, String string2) throws RCException {
        if (string.equalsIgnoreCase("rc_users")) {
            int n = Integer.parseInt(string2);
            this.listeners.connectionEventListenerList.fireConnectedUsersChanged(n);
            return true;
        }
        if (string.equalsIgnoreCase("charset")) {
            T.setCharset(string2);
            return true;
        }
        if (string.equalsIgnoreCase("exclusive_mode")) {
            boolean bl = string2.equalsIgnoreCase("active");
            this.listeners.connectionEventListenerList.fireExclusiveModeChanged(bl);
            return true;
        }
        if (string.equalsIgnoreCase("support_mouse_absolute")) {
            if (string2.equalsIgnoreCase("yes")) {
                if (!this.supportedMouseModes.contains((Object)RCCore.MouseMode.ABSOLUTE)) {
                    this.supportedMouseModes.add(RCCore.MouseMode.ABSOLUTE);
                }
            } else if (this.supportedMouseModes.contains((Object)RCCore.MouseMode.ABSOLUTE)) {
                this.supportedMouseModes.remove((Object)RCCore.MouseMode.ABSOLUTE);
            }
            this.listeners.mouseModeListenerList.fireSupportedMouseModesChanged(this.supportedMouseModes);
            return true;
        }
        if (string.equalsIgnoreCase("support_mouse_auto")) {
            if (string2.equalsIgnoreCase("yes")) {
                if (!this.supportedMouseModes.contains((Object)RCCore.MouseMode.AUTOMATIC)) {
                    this.supportedMouseModes.add(RCCore.MouseMode.AUTOMATIC);
                }
            } else if (this.supportedMouseModes.contains((Object)RCCore.MouseMode.AUTOMATIC)) {
                this.supportedMouseModes.remove((Object)RCCore.MouseMode.AUTOMATIC);
            }
            this.listeners.mouseModeListenerList.fireSupportedMouseModesChanged(this.supportedMouseModes);
            return true;
        }
        if (string.equalsIgnoreCase("support_mouse_direct")) {
            if (string2.equalsIgnoreCase("yes")) {
                if (!this.supportedMouseModes.contains((Object)RCCore.MouseMode.STANDARD)) {
                    this.supportedMouseModes.add(RCCore.MouseMode.STANDARD);
                }
            } else if (this.supportedMouseModes.contains((Object)RCCore.MouseMode.STANDARD)) {
                this.supportedMouseModes.remove((Object)RCCore.MouseMode.STANDARD);
            }
            this.listeners.mouseModeListenerList.fireSupportedMouseModesChanged(this.supportedMouseModes);
            return true;
        }
        if (string.equalsIgnoreCase("current_mouse_mode")) {
            RCCore.MouseMode mouseMode;
            if (string2.equalsIgnoreCase("absolute")) {
                mouseMode = RCCore.MouseMode.ABSOLUTE;
            } else if (string2.equalsIgnoreCase("auto")) {
                mouseMode = RCCore.MouseMode.AUTOMATIC;
            } else if (string2.equalsIgnoreCase("direct")) {
                mouseMode = RCCore.MouseMode.STANDARD;
            } else {
                return true;
            }
            this.listeners.mouseModeListenerList.fireMouseModeChanged(mouseMode);
            return true;
        }
        if (string.equalsIgnoreCase("capslockled")) {
            if (string2.equalsIgnoreCase("on")) {
                if (!this.keyboardLeds.contains((Object)RCCore.KeyboardLed.LED_CAPSLOCK)) {
                    this.keyboardLeds.add(RCCore.KeyboardLed.LED_CAPSLOCK);
                }
            } else if (this.keyboardLeds.contains((Object)RCCore.KeyboardLed.LED_CAPSLOCK)) {
                this.keyboardLeds.remove((Object)RCCore.KeyboardLed.LED_CAPSLOCK);
            }
            this.listeners.keyboardInfoListenerList.fireKeyboardLedStateChanged(this.keyboardLeds);
            return true;
        }
        if (string.equalsIgnoreCase("numlockled")) {
            if (string2.equalsIgnoreCase("on")) {
                if (!this.keyboardLeds.contains((Object)RCCore.KeyboardLed.LED_NUMLOCK)) {
                    this.keyboardLeds.add(RCCore.KeyboardLed.LED_NUMLOCK);
                }
            } else if (this.keyboardLeds.contains((Object)RCCore.KeyboardLed.LED_NUMLOCK)) {
                this.keyboardLeds.remove((Object)RCCore.KeyboardLed.LED_NUMLOCK);
            }
            this.listeners.keyboardInfoListenerList.fireKeyboardLedStateChanged(this.keyboardLeds);
            return true;
        }
        if (string.equalsIgnoreCase("scrolllockled")) {
            if (string2.equalsIgnoreCase("on")) {
                if (!this.keyboardLeds.contains((Object)RCCore.KeyboardLed.LED_SCROLLLOCK)) {
                    this.keyboardLeds.add(RCCore.KeyboardLed.LED_SCROLLLOCK);
                }
            } else if (this.keyboardLeds.contains((Object)RCCore.KeyboardLed.LED_SCROLLLOCK)) {
                this.keyboardLeds.remove((Object)RCCore.KeyboardLed.LED_SCROLLLOCK);
            }
            this.listeners.keyboardInfoListenerList.fireKeyboardLedStateChanged(this.keyboardLeds);
            return true;
        }
        if (string.equalsIgnoreCase("devicename")) {
            this.listeners.connectionEventListenerList.fireDeviceNameChanged(string2);
            return true;
        }
        if (string.equalsIgnoreCase("audio")) {
            String[] stringArray;
            boolean bl = false;
            boolean bl2 = false;
            String string3 = null;
            String string4 = null;
            for (String string5 : stringArray = string2.split(";")) {
                String[] stringArray2 = string5.split("=");
                if (stringArray2.length != 2) continue;
                if (stringArray2[0].equalsIgnoreCase("spk")) {
                    bl = stringArray2[1].equalsIgnoreCase("true");
                    continue;
                }
                if (stringArray2[0].equalsIgnoreCase("spkformat")) {
                    string3 = stringArray2[1];
                    continue;
                }
                if (stringArray2[0].equalsIgnoreCase("mic")) {
                    bl2 = stringArray2[1].equalsIgnoreCase("true");
                    continue;
                }
                if (!stringArray2[0].equalsIgnoreCase("micformat")) continue;
                string4 = stringArray2[1];
            }
            this.listeners.audioSettingsExListenerList.fireAudioSettingsChanged(new IAudioStatusExSupport.AudioStatus(bl, string3), new IAudioStatusExSupport.AudioStatus(bl2, string4));
        }
        return false;
    }

    protected int readServerId() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected List<KvmPort> readPortListMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readAuthCaps() throws IOException, RCException {
        throw new UnsupportedOperationException();
    }

    public void writeLogin(String string, int n, int n2) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readSessionChallenge() throws IOException, RCException {
        throw new UnsupportedOperationException();
    }

    public void writeChallengeResponse(String string) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readAuthSuccessfulMsg() throws IOException, RCException {
        throw new UnsupportedOperationException();
    }

    protected int readQuitMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected NotificationEvent readUserNotificationMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected String readUtf8StringMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeUtf8StringMsg(String string) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected List<KeyValuePair<String, String>> readConnectionParameterMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected String readServerRCMessageMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readOSDStateMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeClientInitMsg(String string) throws IOException, RCException {
        throw new UnsupportedOperationException();
    }

    protected void writeKvmSwitchEventMsg(int n, String string) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected String readKeyboardLayout() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readServerFBFormat() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readServerCommand() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected int readPingRequest() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writePingRequest(int n) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected int readPingReply() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writePingReply(int n) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected RfbPixelFormat readAckPixelFormatMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readBandwidthRequestMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeBandwidthReplyMsg(int n) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeSetEncodingsMsg(int[] nArray) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeSetPixelFormatMsg(RfbPixelFormat rfbPixelFormat) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeFramebufferUpdateRequest(int n, int n2, int n3, int n4, boolean bl) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readFramebufferUpdate() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readFramebufferUpdateRect() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writePointerEvent(boolean bl, int n, int n2, int n3, int n4) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeMouseSyncEvent(int n) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeKeyboardEvent(int n, boolean bl) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeUserPropChangeEvent(String string, String string2) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void writeVideoSettingsRequest(int n) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void writeVideoSettingsEvent(int n, int n2) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeVideoRefreshMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readVideoQualityMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected VideoSettings readVideoSettingsMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected List<VMMountRequestResponse> readVmShareList() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected VMMountRequestResponse readVmMountsResponse() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeVmMountsRequest(IVMMountRequestResponse iVMMountRequestResponse) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeSetConnectionParameterMsg(String string, String string2) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected UsbProfileList readUsbProfileList() throws IOException, RCException {
        throw new UnsupportedOperationException();
    }

    protected void writeUsbProfileSelect(int n) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected VMConfigInfo readVirtualMediaConfig() throws IOException, RCException {
        throw new UnsupportedOperationException();
    }

    protected void readCapabilityTableMessage() throws IOException {
    }

    protected void writeAssociatedTagMsg(String string) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void setLowBandwidth(boolean bl) {
    }

    public RCCore.ColorDepth setCustomColorDepth(RCCore.ColorDepth colorDepth) {
        RCCore.ColorDepth colorDepth2 = null;
        List<RCCore.ColorDepth> list = this.encoding.getSupportedColorDepths();
        if (list.contains((Object)colorDepth)) {
            colorDepth2 = colorDepth;
        }
        this.customColorDepth = colorDepth2;
        return colorDepth2;
    }

    public RCCore.Compression setCustomCompression(RCCore.Compression compression) {
        RCCore.Compression compression2 = null;
        List<RCCore.Compression> list = this.encoding.getSupportedCompressions();
        if (list.contains((Object)compression)) {
            compression2 = compression;
        }
        this.customCompression = compression2;
        return compression2;
    }

    public RCCore.Smoothing setCustomSmoothing(RCCore.Smoothing smoothing) {
        RCCore.Smoothing smoothing2 = null;
        List<RCCore.Smoothing> list = this.encoding.getSupportedSmoothingValues();
        if (list.contains((Object)smoothing)) {
            smoothing2 = smoothing;
        }
        this.customSmoothing = smoothing2;
        return smoothing2;
    }

    protected Map<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> getClientSessionInitProps() {
        return this.clientSessionInitProps;
    }

    public void setClientSessionInitProps(Map<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> map) {
        this.clientSessionInitProps = map;
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, RCException {
        throw new UnsupportedOperationException();
    }

    protected void processServerInit() throws IOException {
        int n = this.readServerId();
        this.listeners.connectionEventListenerList.fireServerSessionIdChanged(n);
    }

    protected void processPortList() throws IOException {
        List<KvmPort> list = this.readPortListMsg();
        String string = this.listeners.connectionEventListenerList.firePortListChanged(list);
        this.portList = list;
        if (string != null) {
            this.portID = string;
        }
    }

    protected void processFramebufferUpdate() throws IOException, RCException {
        int n = this.videoUpdateDelay;
        if (n == 0) {
            this.sendIncrementalFramebufferUpdateRequest();
        }
        this.increaseFps();
        this.readFramebufferUpdate();
        this.isFbUpd = this.inflater.getUncompressedDataStream(this.is, this, this.updateIsZlib(), false, this.framebufferUpdateSize);
        Rectangle rectangle = new Rectangle();
        for (int i = 0; i < this.noFramebufferUpdateRects; ++i) {
            this.readFramebufferUpdateRect();
            if (this.framebufferUpdateRectX + this.framebufferUpdateRectW > this.framebufferWidthPadded || this.framebufferUpdateRectY + this.framebufferUpdateRectH > this.framebufferHeightPadded) {
                throw new RCException(MessageFormat.format(T._("Framebuffer update rectangle too large: {0}x{1} at ({2},{3}"), new Integer(this.framebufferUpdateRectW), new Integer(this.framebufferUpdateRectH), new Integer(this.framebufferUpdateRectX), new Integer(this.framebufferUpdateRectY)));
            }
            Rectangle rectangle2 = this.processFramebufferUpdateRect();
            rectangle = rectangle.union(rectangle2);
        }
        if (rectangle.x + rectangle.width > this.framebufferWidth) {
            rectangle.width = this.framebufferWidth - rectangle.x;
        }
        if (rectangle.y + rectangle.height > this.framebufferHeight) {
            rectangle.height = this.framebufferHeight - rectangle.y;
        }
        this.rfbRenderer.render(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        if (n != 0) {
            try {
                Thread.sleep(n);
                this.sendIncrementalFramebufferUpdateRequest();
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    protected void processUserNotification() throws IOException, RCException {
        if (this.notificationIsQuit()) {
            int n = this.readQuitMsg();
            throw new RCException(RfbHandler.getQuitReasonMsg(n));
        }
        NotificationEvent notificationEvent = this.readUserNotificationMsg();
        this.listeners.notificationListenerList.fireNotification(notificationEvent);
        if (notificationEvent.isQuit()) {
            throw new RCException(notificationEvent.getErrorCode(), notificationEvent.getMessage());
        }
    }

    protected void processUtf8String() throws IOException, RCException {
        String string = this.readUtf8StringMsg();
        if (this.inInitialHandshake) {
            this.listeners.connectionEventListenerList.fireChatWelcomeChanged(string);
        } else {
            this.listeners.connectionEventListenerList.fireNewChatMessage(string);
        }
    }

    protected void processKeyboardLayout() throws IOException, RCException {
        String string = this.readKeyboardLayout();
        this.listeners.keyboardInfoListenerList.fireSunKeyboardSupported(string.equalsIgnoreCase("sun"));
    }

    protected void processOSDState() throws IOException, RCException {
        this.readOSDStateMsg();
        this.logger.log(Level.FINER, MessageFormat.format(T._("Got OSD message, message: \"{0}\", timeout: {1}, blank: {2}"), this.osdText, new Integer(this.osdTimeout), this.osdBlank ? T._("true") : T._("false")));
        this.renderer.setOSD(this.osdText, this.osdTimeout, this.osdBlank);
        this.listeners.videoEventListenerList.fireOsdMessageReceived(this.osdText, this.osdBlank);
    }

    protected void processVideoQuality() throws IOException, RCException {
        this.readVideoQualityMsg();
    }

    protected void processVideoSettings() throws IOException, RCException {
        VideoSettings videoSettings = this.readVideoSettingsMsg();
        this.videoSettingsHandler.setLastVideoSettings(videoSettings);
        this.listeners.videoEventListenerList.fireVideoSettingsUpdated(videoSettings);
    }

    private boolean isCustomConnectionProperties() {
        return this.customColorDepth != null || this.customCompression != null || this.customSmoothing != null;
    }

    protected void processConnectionParameterList() throws IOException, RCException {
        KeyValuePair<String, String> keyValuePair2;
        this.hotkeyHighestIndex = 0;
        List<KeyValuePair<String, String>> list = this.readConnectionParameterMsg();
        Set<String> set = null;
        String string3 = "";
        String string2 = "";
        if (this.getCapabilityMap() != null && this.getCapabilityMap().size() > 0) {
            set = this.appletParameterMap.keySet();
            for (String string3 : set) {
                string2 = this.appletParameterMap.get(string3);
                keyValuePair2 = new KeyValuePair<String, String>(string3, string2);
                list.add(keyValuePair2);
            }
        }
        if (this.isCustomConnectionProperties()) {
            for (KeyValuePair<String, String> keyValuePair2 : list) {
                if (!"selenc".equalsIgnoreCase(keyValuePair2.getKey())) continue;
                list.remove(keyValuePair2);
                break;
            }
            list.add(0, new KeyValuePair<String, String>("selenc", "manual"));
        }
        this.processConnectionParameter(list);
        if (this.isCustomConnectionProperties()) {
            if (this.customColorDepth != null) {
                this.encoding.setEncodingColorDepth(this.customColorDepth);
            }
            if (this.customCompression != null) {
                this.encoding.setEncodingCompression(this.customCompression);
            }
            if (this.customSmoothing != null) {
                this.rfbRenderer.setSmoothing(this.customSmoothing);
            }
        }
        int n = 0;
        if (this.hotkeyKeyCodes != null) {
            n = Math.min(this.hotkeyKeyCodes.length, this.hotkeyHighestIndex + 1);
        }
        if (n > 0) {
            keyValuePair2 = new Vector();
            for (int i = 0; i < n; ++i) {
                String string4 = this.hotkeyKeyCodes != null && i + 1 <= this.hotkeyKeyCodes.length && this.hotkeyKeyCodes[i] != null ? this.hotkeyKeyCodes[i] : "";
                String string5 = this.hotkeyReadableCodes != null && i + 1 <= this.hotkeyReadableCodes.length && this.hotkeyReadableCodes[i] != null ? this.hotkeyReadableCodes[i] : "";
                String string6 = this.hotkeyNames != null && i + 1 <= this.hotkeyNames.length && this.hotkeyNames[i] != null ? this.hotkeyNames[i] : "";
                try {
                    ((Vector)((Object)keyValuePair2)).add(new KeyboardMacro(string6, string5, string4));
                    continue;
                }
                catch (ParseException parseException) {
                    this.logger.log(Level.WARNING, T._("Could not parse keyboard hotkey"), parseException);
                }
            }
            this.listeners.keyboardInfoListenerList.fireKeyboardMacroListChanged((List<KeyboardMacro>)((Object)keyValuePair2));
        }
        this.listeners.videoEventListenerList.fireVideoSettingsSupportChanged(this.videoSettingsAvailable && (this.videoSettingsPermStandard || this.videoSettingsPermFull));
        this.listeners.videoEventListenerList.fireVideoAutoAdjustSupportChanged(this.videoSettingsAvailable && !this.videoSettingsOffsetOnly);
        this.listeners.videoEventListenerList.fireColorCalibrationSupportChanged(this.videoSettingsAvailable && !this.videoSettingsOffsetOnly && this.colorCalibration);
        this.listeners.videoEventListenerList.fireVideoRefreshSupportChanged(this.videoRefreshAvailable);
        if (this.mouseSyncKeyCode != null && this.mouseSyncKeyCode.length() > 0 && this.mouseSyncReadableCode != null && this.mouseSyncReadableCode.length() > 0) {
            try {
                this.renderer.setMouseSyncKeys(this.mouseSyncReadableCode, this.mouseSyncKeyCode);
            }
            catch (ParseException parseException) {
                this.logger.log(Level.WARNING, T._("Could not parse Mouse Sync hotkey, single mouse mode not possible"), parseException);
            }
        }
    }

    protected void processAckPixelFormat() throws IOException, RCException {
        RfbPixelFormat rfbPixelFormat = this.readAckPixelFormatMsg();
        if (rfbPixelFormat == null) {
            throw new RCException(T._("Invalid Pixel Format received"));
        }
        this.currentRfbPixelFormat = rfbPixelFormat;
    }

    protected void processAuthCaps() throws IOException, RCException {
        this.readAuthCaps();
    }

    protected void processSessionChallenge() throws IOException, RCException {
        this.readSessionChallenge();
    }

    protected void processAuthSuccessful() throws IOException, RCException {
        this.readAuthSuccessfulMsg();
        this.logger.log(Level.INFO, T._("Successfully logged in."));
    }

    protected void processServerFBFormat() throws IOException, RCException {
        this.readServerFBFormat();
        this.framebufferWidthPadded = this.framebufferWidth;
        this.framebufferHeightPadded = this.framebufferHeight;
        if (this.framebufferWidthPadded % 16 != 0) {
            this.framebufferWidthPadded = (this.framebufferWidthPadded / 16 + 1) * 16;
        }
        if (this.framebufferHeightPadded % 16 != 0) {
            this.framebufferHeightPadded = (this.framebufferHeightPadded / 16 + 1) * 16;
        }
        this.logger.log(Level.INFO, MessageFormat.format(T._("New resolution: {0} x {1}"), new Integer(this.framebufferWidth), new Integer(this.framebufferHeight)));
        Dimension dimension = new Dimension(this.framebufferWidth, this.framebufferHeight);
        this.rfbRenderer.setResolution(this.framebufferWidth, this.framebufferHeight);
        this.renderer.screenResolutionChanged(dimension);
        this.listeners.videoEventListenerList.fireResolutionChanged(dimension);
        if (!this.inInitialHandshake) {
            this.sendFullFramebufferUpdateRequest();
        }
    }

    protected void processServerRCMessage() throws IOException, RCException {
        String string = this.readServerRCMessageMsg();
        this.logger.log(Level.FINER, MessageFormat.format(T._("Got RC message: \"{0}\""), string));
        this.listeners.notificationListenerList.fireTextNotification(string);
    }

    protected void processServerCommand() throws IOException, RCException {
        this.readServerCommand();
        this.logger.log(Level.FINER, MessageFormat.format(T._("Got Server command: \"{0}\" - \"{1}\""), this.serverCommandName, this.serverCommandValue));
        this.processServerCommand(this.serverCommandName, this.serverCommandValue);
    }

    protected void processPingRequest() throws IOException, RCException {
        int n = this.readPingRequest();
        this.writePingReply(n);
    }

    protected void processPingReply() throws IOException, RCException {
        this.readPingReply();
    }

    protected void processBandwidthRequest() throws IOException, RCException {
        this.writeBandwidthReplyMsg(1);
        this.readBandwidthRequestMsg();
        this.writeBandwidthReplyMsg(2);
        this.logger.log(Level.INFO, T._("Bandwidth measurement done."));
    }

    protected void processMouseCapsResponse() throws IOException, RCException {
        throw new UnsupportedOperationException();
    }

    protected void processVmMountsResponse() throws IOException, RCException {
        VMMountRequestResponse vMMountRequestResponse = this.readVmMountsResponse();
        this.listeners.virtualMediaInfoListenerList.fireRemoteIsoMountFinished(vMMountRequestResponse);
    }

    protected void processVmShareTable() throws IOException {
        List<VMMountRequestResponse> list = this.readVmShareList();
        this.listeners.virtualMediaInfoListenerList.fireRemoteIsoListChanged(list);
    }

    protected void processUsbProfileList() throws IOException, RCException {
        this.readUsbProfileList();
        this.listeners.virtualMediaInfoListenerList.fireUsbProfileListChanged(this.usbProfileList);
    }

    protected void processVirtualMediaConfig() throws IOException, RCException {
        VMConfigInfo vMConfigInfo = this.readVirtualMediaConfig();
        this.listeners.virtualMediaInfoListenerList.fireVirtualMediaConfigChanged(vMConfigInfo);
    }

    @Override
    protected void processProtocol() throws IOException, RCException {
        while (this.shouldRun) {
            int n = this.readServerMessageType();
            switch (n) {
                case 5: {
                    this.processServerInit();
                    break;
                }
                case 4: {
                    this.processPortList();
                    break;
                }
                case 0: {
                    this.processFramebufferUpdate();
                    break;
                }
                case 3: {
                    this.processUserNotification();
                    break;
                }
                case 7: {
                    this.processUtf8String();
                    break;
                }
                case 8: {
                    this.processVideoSettings();
                    break;
                }
                case 9: {
                    this.processKeyboardLayout();
                    break;
                }
                case 16: {
                    this.processOSDState();
                    break;
                }
                case 17: {
                    this.processVideoQuality();
                    break;
                }
                case 18: {
                    this.processConnectionParameterList();
                    break;
                }
                case 19: {
                    this.processAckPixelFormat();
                    break;
                }
                case 32: {
                    this.processAuthCaps();
                    break;
                }
                case 33: {
                    this.processSessionChallenge();
                    break;
                }
                case 34: {
                    this.processAuthSuccessful();
                    break;
                }
                case 128: {
                    this.processServerFBFormat();
                    break;
                }
                case 131: {
                    this.processServerRCMessage();
                    break;
                }
                case 132: {
                    this.processServerCommand();
                    break;
                }
                case 148: {
                    this.processPingRequest();
                    break;
                }
                case 149: {
                    this.processPingReply();
                    break;
                }
                case 150: {
                    this.processBandwidthRequest();
                    break;
                }
                case 165: {
                    this.processMouseCapsResponse();
                    break;
                }
                case 166: {
                    this.processVmMountsResponse();
                    break;
                }
                case 167: {
                    this.processVmShareTable();
                    break;
                }
                case 170: {
                    this.processUsbProfileList();
                    break;
                }
                case 168: {
                    this.processVirtualMediaConfig();
                    break;
                }
                default: {
                    this.logger.log(Level.SEVERE, T._("Unknown Protocol message received:") + " " + n);
                    throw new RCException(T._("Protocol Error: Unknown Protocol message received"));
                }
            }
            if (!this.inInitialHandshake) continue;
            this.processInitialHandshake(n);
        }
    }

    protected abstract boolean updateIsZlib();

    protected abstract boolean updateRectIsZlibStreamed();

    protected abstract boolean updateRectIsZlibCompress();

    protected boolean updateRectIsZlib() {
        return this.updateRectIsZlibStreamed() || this.updateRectIsZlibCompress();
    }

    protected abstract int getUpdateRectEncoding();

    protected abstract int getUpdateRectSubencoding();

    protected int readHardwareEncodingSize() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readHardwareEncodingPadding() throws IOException {
        throw new UnsupportedOperationException();
    }

    private void ensureFramebufferUpdateRectBuffer(int n) {
        if (this.framebufferUpdateRectBuffer == null || this.framebufferUpdateRectBuffer.length < n) {
            this.framebufferUpdateRectBuffer = new byte[n];
        }
    }

    private void processLrleEncoding() throws IOException, RCException {
        ReadWriteLock readWriteLock;
        if (this.imageDecoderLrle == null) {
            this.imageDecoderLrle = new ImageDecoderLrle(this.logger, this);
        }
        if (this.framebufferUpdateRectSize <= 0) {
            this.framebufferUpdateRectSize = this.readHardwareEncodingSize();
        }
        if ((readWriteLock = this.renderer.getLock()) instanceof ReentrantReadWriteLock) assert (((ReentrantReadWriteLock)readWriteLock).isWriteLockedByCurrentThread());
        this.imageDecoderLrle.decodeImage(this.isFbUpdRect, this.rfbRenderer.getFramebufferImageMemory(), this.rfbRenderer.getFramebufferImageMemoryWidth(), this.framebufferWidth, this.framebufferHeight, this.getUpdateRectEncoding(), this.getUpdateRectSubencoding(), this.framebufferUpdateRectX, this.framebufferUpdateRectY, this.framebufferUpdateRectW, this.framebufferUpdateRectH, this.currentRfbPixelFormat);
        if (!this.updateRectIsZlib()) {
            this.readHardwareEncodingPadding();
        }
    }

    private void processRawEncoding() throws IOException, RCException {
        ReadWriteLock readWriteLock;
        this.logger.log(Level.FINE, T._("Decoding RAW encoded rect."));
        if (this.imageDecoderRaw == null) {
            this.imageDecoderRaw = new ImageDecoderRaw(this.logger, this);
        }
        if ((readWriteLock = this.renderer.getLock()) instanceof ReentrantReadWriteLock) assert (((ReentrantReadWriteLock)readWriteLock).isWriteLockedByCurrentThread());
        this.imageDecoderRaw.decodeImage(this.isFbUpdRect, this.rfbRenderer.getFramebufferImageMemory(), this.rfbRenderer.getFramebufferImageMemoryWidth(), this.framebufferWidth, this.framebufferHeight, this.getUpdateRectEncoding(), this.getUpdateRectSubencoding(), this.framebufferUpdateRectX, this.framebufferUpdateRectY, this.framebufferUpdateRectW, this.framebufferUpdateRectH, this.currentRfbPixelFormat);
    }

    private void processRawVscEncoding() throws IOException, RCException {
        ReadWriteLock readWriteLock;
        this.logger.log(Level.FINE, T._("Decoding RAW VSC encoded rect."));
        if (this.imageDecoderRawVsc == null) {
            this.imageDecoderRawVsc = new ImageDecoderRawVsc(this.logger, this);
        }
        if ((readWriteLock = this.renderer.getLock()) instanceof ReentrantReadWriteLock) assert (((ReentrantReadWriteLock)readWriteLock).isWriteLockedByCurrentThread());
        this.imageDecoderRawVsc.decodeImage(this.isFbUpdRect, this.rfbRenderer.getFramebufferImageMemory(), this.rfbRenderer.getFramebufferImageMemoryWidth(), this.framebufferWidth, this.framebufferHeight, this.getUpdateRectEncoding(), this.getUpdateRectSubencoding(), this.framebufferUpdateRectX, this.framebufferUpdateRectY, this.framebufferUpdateRectW, this.framebufferUpdateRectH, this.currentRfbPixelFormat);
    }

    private void processHextileEncoding() throws IOException, RCException {
        ReadWriteLock readWriteLock;
        this.logger.log(Level.FINE, T._("Decoding Hextile encoded rect."));
        if (this.imageDecoderHextile == null) {
            this.imageDecoderHextile = new ImageDecoderHextile(this.logger, this);
        }
        if ((readWriteLock = this.renderer.getLock()) instanceof ReentrantReadWriteLock) assert (((ReentrantReadWriteLock)readWriteLock).isWriteLockedByCurrentThread());
        this.imageDecoderHextile.decodeImage(this.isFbUpdRect, this.rfbRenderer.getFramebufferImageMemory(), this.rfbRenderer.getFramebufferImageMemoryWidth(), this.framebufferWidth, this.framebufferHeight, this.getUpdateRectEncoding(), this.getUpdateRectSubencoding(), this.framebufferUpdateRectX, this.framebufferUpdateRectY, this.framebufferUpdateRectW, this.framebufferUpdateRectH, this.currentRfbPixelFormat);
    }

    private void processTightEncoding() throws IOException, RCException {
        ReadWriteLock readWriteLock;
        this.logger.log(Level.FINE, T._("Decoding Tight encoded rect."));
        if (this.imageDecoderTight == null) {
            this.imageDecoderTight = new ImageDecoderTight(this.logger, this);
        }
        if ((readWriteLock = this.renderer.getLock()) instanceof ReentrantReadWriteLock) assert (((ReentrantReadWriteLock)readWriteLock).isWriteLockedByCurrentThread());
        this.imageDecoderTight.decodeImage(this.isFbUpdRect, this.rfbRenderer.getFramebufferImageMemory(), this.rfbRenderer.getFramebufferImageMemoryWidth(), this.framebufferWidth, this.framebufferHeight, this.getUpdateRectEncoding(), this.getUpdateRectSubencoding(), this.framebufferUpdateRectX, this.framebufferUpdateRectY, this.framebufferUpdateRectW, this.framebufferUpdateRectH, this.currentRfbPixelFormat);
    }

    private void processUnknownEncoding() throws IOException, RCException {
        String string = MessageFormat.format(T._("Unknown rectangle encoding {0} found"), "0x" + Integer.toHexString(this.framebufferUpdateRectEncoding));
        if (this.framebufferUpdateRectSize < 0) {
            throw new RCException(string);
        }
        this.logger.log(Level.WARNING, string);
        this.ensureFramebufferUpdateRectBuffer(this.framebufferUpdateRectSize);
        this.isFbUpdRect.readFully(this.framebufferUpdateRectBuffer, 0, this.framebufferUpdateRectSize);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Rectangle processFramebufferUpdateRect() throws IOException, RCException {
        this.isFbUpdRect = this.inflater.getUncompressedDataStream(this.isFbUpd, this, this.isFbUpd == this.is ? this.updateRectIsZlibStreamed() : false, this.isFbUpd == this.is ? this.updateRectIsZlibCompress() : false, this.framebufferUpdateRectSize);
        this.framebufferUpdateRectSize = this.inflater.getUncompressedSize();
        ReadWriteLock readWriteLock = this.renderer.getLock();
        if (readWriteLock != null) {
            readWriteLock.writeLock().lock();
        }
        try {
            switch (this.getUpdateRectEncoding()) {
                case 11: 
                case 128: {
                    this.processLrleEncoding();
                    break;
                }
                case 0: {
                    this.processRawEncoding();
                    break;
                }
                case 10: {
                    this.processRawVscEncoding();
                    break;
                }
                case 5: {
                    this.processHextileEncoding();
                    break;
                }
                case 7: 
                case 9: {
                    this.processTightEncoding();
                    break;
                }
                default: {
                    this.processUnknownEncoding();
                }
            }
            Rectangle rectangle = new Rectangle(this.framebufferUpdateRectX, this.framebufferUpdateRectY, this.framebufferUpdateRectW, this.framebufferUpdateRectH);
            Rectangle rectangle2 = rectangle = this.rfbRenderer.updatePixel(rectangle);
            return rectangle2;
        }
        finally {
            if (readWriteLock != null) {
                readWriteLock.writeLock().unlock();
            }
        }
    }

    public boolean isFPS() {
        return this.fpsEnabled;
    }

    public void setFPS(boolean bl) {
        this.fpsEnabled = bl;
    }

    public int getVideoUpdateDelay() {
        return this.videoUpdateDelay;
    }

    public void setVideoUpdateDelay(int n) {
        this.videoUpdateDelay = n;
    }
}

