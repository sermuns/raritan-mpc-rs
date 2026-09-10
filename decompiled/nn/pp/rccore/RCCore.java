/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.net.ssl.X509TrustManager;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import nn.pp.core.NotificationListener;
import nn.pp.rccore.AudioStatusListener;
import nn.pp.rccore.ConnectionEventListener;
import nn.pp.rccore.IKeyboardMacro;
import nn.pp.rccore.IKvmPort;
import nn.pp.rccore.IUsbProfileList;
import nn.pp.rccore.IVMConfigInfo;
import nn.pp.rccore.IVMMountRequestResponse;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.KeyboardInfoListener;
import nn.pp.rccore.KeyboardListener;
import nn.pp.rccore.MouseEventListener;
import nn.pp.rccore.MouseModeListener;
import nn.pp.rccore.RCException;
import nn.pp.rccore.VideoEventListener;
import nn.pp.rccore.VirtualMediaInfoListener;

public interface RCCore {
    public void dispose();

    public JComponent getRCJComponent();

    public void setX509TrustManager(X509TrustManager var1);

    public void connectRCWithUserLogin(String var1, int var2, boolean var3, String var4, String var5, String var6) throws IOException, RCException;

    public void connectRCWithRdmSession(String var1, int var2, boolean var3, String var4, String var5, String var6, String var7) throws IOException, RCException;

    public void connectRCWithEricKey(String var1, int var2, boolean var3, String var4, String var5) throws IOException, RCException;

    public void disconnect();

    public boolean isEncodingChangeSupported();

    public boolean isEncodingAutoSupported();

    public List<Predefine> getSupportedEncodingPredefines();

    public List<ColorDepth> getSupportedEncodingColorDepths();

    public List<Compression> getSupportedEncodingCompressions();

    public boolean isEncodingLossySupported();

    public boolean isEncodingAuto();

    public ColorDepth getEncodingColorDepth();

    public Compression getEncodingCompression();

    public boolean isEncodingLossy();

    public void setEncodingToAuto() throws IOException;

    public void setEncodingPredefine(Predefine var1) throws IOException;

    public void setEncodingColorDepth(ColorDepth var1, boolean var2) throws IOException;

    public void setEncodingCompression(Compression var1, boolean var2) throws IOException, IllegalArgumentException;

    public void setEncodingLossy(boolean var1, boolean var2) throws IOException;

    public List<ColorDepth> getSupportedColorDepthsForCompression(Compression var1);

    public List<Compression> getSupportedCompressionsForColorDepth(ColorDepth var1);

    public ColorDepth getEncodingColorDepthForPredefine(Predefine var1);

    public Compression getEncodingCompressionForPredefine(Predefine var1);

    public boolean getEncodingLossyForPredefine(Predefine var1);

    public boolean isVideoSettingsSupported();

    public boolean isVideoAutoAdjustSupported();

    public boolean isColorCalibrationSupported();

    public void requestVideoSettingsUpdates() throws IOException;

    public void stopVideoSettingsUpdates() throws IOException;

    public IVideoSettings getVideoSettings();

    public void setVideoSettings(IVideoSettings var1) throws IOException;

    public void saveVideoSettings() throws IOException;

    public void cancelVideoSettings() throws IOException;

    public void resetVideoSettingsAllModes() throws IOException;

    public void resetVideoSettingsThisMode() throws IOException;

    public void resetVideoSettingsThisPort() throws IOException;

    public void requestVideoAutoSense() throws IOException;

    public void requestVideoColorCalibration() throws IOException;

    public boolean isVideoRefreshSupported();

    public void requestVideoRefresh() throws IOException;

    public Dimension getResolution();

    public boolean isMouseModeChangeSupported();

    public List<MouseMode> getSupportedMouseModes();

    public MouseMode getMouseMode();

    public void setMouseMode(MouseMode var1) throws IOException;

    public boolean isSingleCursorModeSupported();

    public boolean isSingleCursorMode();

    public void setSingleCursorMode(boolean var1);

    public void setCaptureRightAway(boolean var1);

    public boolean isMouseSyncSupported();

    public void syncMouse(MouseSyncType var1) throws IOException;

    public boolean isAudioStatusSupportEnabled();

    public void sendKeyboardEvent(int var1, boolean var2) throws IOException;

    public void sendKeyboardMacro(IKeyboardMacro var1, boolean var2);

    public List<KeyboardLed> getKeyboardLedState();

    public List<IKeyboardMacro> getKeyboardMacroList();

    public void setKeyboardMacroList(List<IKeyboardMacro> var1) throws IOException;

    public void enforceSMMHotKeyCheck(boolean var1);

    public void setMouseSyncHotkey(String var1, KeyStroke var2);

    public void setHotkeys(Map<Integer, KeyStroke> var1);

    public Locale getSoftKeyboardMapping();

    public void setSoftKeyboardMapping(Locale var1) throws IOException;

    public List<Locale> getSupportedLocalKeyboardMappings();

    public Locale getLocalKeyboardMapping();

    public void setLocalKeyboardMapping(Locale var1) throws IOException, IllegalArgumentException;

    public boolean isLocalKeyboardMappingPersistent();

    public void setLocalKeyboardMappingPersistent(boolean var1);

    public boolean isVirtualMediaSupported();

    public boolean isVirtualMediaReadOnly();

    public int getVirtualMediaDriveCount();

    public boolean isVirtualMediaRemoteIsoSupported();

    public IUsbProfileList getUsbProfileList();

    public void virtualMediaConfigChanged(IVMConfigInfo var1);

    public void requestUsbProfileChange(int var1) throws IOException;

    public List<IVMMountRequestResponse> getVirtualMediaRemoteIsoList();

    public void mountOrUnmountRemoteIso(IVMMountRequestResponse var1) throws IOException, RCException;

    public List<? extends IKvmPort> getPortList();

    public void switchKvmPort(int var1, String var2) throws IOException, RCException;

    public double getScalingX();

    public double getScalingY();

    public boolean isScaleToFit();

    public boolean isScaleToFitKeepAr();

    public void setScaling(double var1, double var3) throws IllegalArgumentException, UnsupportedOperationException;

    public void setScaleToFit(boolean var1, boolean var2, Dimension var3) throws UnsupportedOperationException;

    public Interpolation getInterpolation();

    public void setInterpolation(Interpolation var1);

    public Smoothing getSmoothing();

    public void setSmoothing(Smoothing var1);

    public void setOSD(String var1, int var2, boolean var3);

    public Cursor getCursor();

    public void setCursor(Cursor var1);

    public int getServerSessionId();

    public boolean hasMonitorModePermission();

    public boolean isMonitorMode();

    public void setMonitorMode(boolean var1);

    public boolean hasExclusiveModePermission();

    public boolean isExclusiveMode();

    public void setExclusiveMode(boolean var1) throws IOException;

    public int getConnectedUsers();

    public BufferedImage getSnapshot(Rectangle var1) throws IllegalArgumentException;

    public BufferedImage getSnapshot();

    public void sendChatMessage(String var1) throws IOException;

    public int getIncomingTrafficSpeed();

    public int getOutgoingTrafficSpeed();

    public int getFramesPerSecond();

    public String getDeviceName();

    public String getProtocolVersion();

    public void addNotificationListener(NotificationListener var1);

    public void removeNotificationListener(NotificationListener var1);

    public void addConnectionEventListener(ConnectionEventListener var1, int var2);

    public void removeConnectionEventListener(ConnectionEventListener var1);

    public void addVideoEventListener(VideoEventListener var1, int var2);

    public void removeVideoEventListener(VideoEventListener var1);

    public void addAudioStatusListener(AudioStatusListener var1);

    public void removeAudioStatusListener(AudioStatusListener var1);

    public void addMouseModeListener(MouseModeListener var1, int var2);

    public void removeMouseModeListener(MouseModeListener var1);

    public void addKeyboardInfoListener(KeyboardInfoListener var1, int var2);

    public void removeKeyboardInfoListener(KeyboardInfoListener var1);

    public void addVirtualMediaInfoListener(VirtualMediaInfoListener var1, int var2);

    public void removeVirtualMediaInfoListener(VirtualMediaInfoListener var1);

    public void addKeyboardListener(KeyboardListener var1);

    public void removeKeyboardListener(KeyboardListener var1);

    public void addMouseEventListener(MouseEventListener var1, int var2);

    public void removeMouseEventListener(MouseEventListener var1);

    public int getKeyCode(int var1, char var2, int var3);

    public void setAppletParameterMap(LinkedHashMap<String, String> var1);

    public boolean isEthernetGigabitSupported();

    public void setLowBandwidth(boolean var1);

    public void setConnectionProperties(boolean var1, Compression var2, ColorDepth var3, Smoothing var4);

    public void sendKeyboardEvent(KeyEvent var1) throws RCException;

    public void sendMouseEvent(boolean var1, int var2, int var3, int var4);

    public void setClientFrameSizeChanged(Dimension var1);

    public void setClientSessionInitProperties(Map<ClientSessionInitProperties, String> var1);

    public <CT> CT getCapablity(Class<CT> var1);

    public boolean isOsdDisabled();

    public void setOsdDisabled(boolean var1);

    public boolean isTrafficSpeed();

    public void setTrafficSpeed(boolean var1);

    public boolean isFPS();

    public void setFPS(boolean var1);

    public void setVideoUpdateDelay(int var1);

    public int getVideoUpdateDelay();

    public void putClientProperty(Object var1, Object var2);

    public Object getClientProperty(Object var1);

    public static enum ClientSessionInitProperties {
        SCAN_REFEENCE_ID;

    }

    public static enum Smoothing {
        LOW{

            @Override
            public Integer eval() {
                return new Integer(0);
            }
        }
        ,
        HIGH{

            @Override
            public Integer eval() {
                return new Integer(1);
            }
        }
        ,
        NONE{

            @Override
            public Integer eval() {
                return new Integer(2);
            }
        };


        public abstract Integer eval();
    }

    public static enum Interpolation {
        NONE,
        FAST,
        GOOD,
        BEST;

    }

    public static enum KeyboardLed {
        LED_CAPSLOCK,
        LED_NUMLOCK,
        LED_SCROLLLOCK;

    }

    public static enum MouseSyncType {
        FAST,
        HARD,
        NORM;

    }

    public static enum MouseMode {
        ABSOLUTE,
        AUTOMATIC,
        STANDARD;

    }

    public static enum Predefine {
        VIDEO_HICOLOR,
        VIDEO,
        LAN_HICOLOR,
        LAN,
        DSL,
        UMTS,
        ISDN,
        MODEM,
        GPRS,
        GSM,
        MODEM_4BIT;

    }

    public static enum Compression {
        VIDEO_OPTIMIZED,
        UNCOMPRESSED,
        LEVEL_1,
        LEVEL_2,
        LEVEL_3,
        LEVEL_4,
        LEVEL_5,
        LEVEL_6,
        LEVEL_7,
        LEVEL_8,
        LEVEL_9;

    }

    public static enum ColorDepth {
        COLOR_32_BIT,
        COLOR_24_BIT,
        COLOR_16_BIT,
        COLOR_8_BIT,
        COLOR_4_BIT,
        GREY_4_BIT,
        GREY_3_BIT,
        GREY_2_BIT,
        BW_1_BIT;

    }
}

