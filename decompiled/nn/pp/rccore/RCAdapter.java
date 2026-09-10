/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.Locale;
import nn.pp.core.INotificationEvent;
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
import nn.pp.rccore.RCCore;
import nn.pp.rccore.VideoEventListener;
import nn.pp.rccore.VirtualMediaInfoListener;

public class RCAdapter
implements VideoEventListener,
KeyboardInfoListener,
KeyboardListener,
MouseModeListener,
MouseEventListener,
ConnectionEventListener,
NotificationListener,
VirtualMediaInfoListener,
AudioStatusListener {
    @Override
    public void textNotification(String string) {
    }

    @Override
    public void receivedNotification(INotificationEvent iNotificationEvent) {
    }

    @Override
    public void encodingChangeAllowed(boolean bl) {
    }

    @Override
    public void encodingAutoSupportedChanged(boolean bl) {
    }

    @Override
    public void encodingLossySupportedChanged(boolean bl) {
    }

    @Override
    public void supportedEncodingPredefinesChanged(List<RCCore.Predefine> list) {
    }

    @Override
    public void supportedEncodingColorDepthsChanged(List<RCCore.ColorDepth> list) {
    }

    @Override
    public void supportedEncodingCompressionsChanged(List<RCCore.Compression> list) {
    }

    @Override
    public void encodingAutoChanged(boolean bl) {
    }

    @Override
    public void encodingColorDepthChanged(RCCore.ColorDepth colorDepth) {
    }

    @Override
    public void encodingCompressionChanged(RCCore.Compression compression) {
    }

    @Override
    public void encodingLossyChanged(boolean bl) {
    }

    @Override
    public void resolutionChanged(Dimension dimension) {
    }

    @Override
    public void videoSettingsSupportChanged(boolean bl) {
    }

    @Override
    public void videoAutoAdjustSupportChanged(boolean bl) {
    }

    @Override
    public void colorCalibrationSupportChanged(boolean bl) {
    }

    @Override
    public void videoRefreshSupportChanged(boolean bl) {
    }

    @Override
    public void videoSettingsUpdated(IVideoSettings iVideoSettings) {
    }

    @Override
    public void osdMessageReceived(String string, boolean bl) {
    }

    @Override
    public void videoDataUpdated(Rectangle rectangle) {
    }

    @Override
    public void keyboardMacroListChanged(List<IKeyboardMacro> list) {
    }

    @Override
    public void softKeyboardMappingChanged(Locale locale) {
    }

    @Override
    public void localKeyboardMappingChanged(Locale locale) {
    }

    @Override
    public void keyboardLedStateChanged(List<RCCore.KeyboardLed> list) {
    }

    @Override
    public void sunKeyboardSupported(boolean bl) {
    }

    @Override
    public void disconnected(Exception exception) {
    }

    @Override
    public void connected() {
    }

    @Override
    public String portListChanged(List<? extends IKvmPort> list) {
        return null;
    }

    @Override
    public void serverSessionIdChanged(int n) {
    }

    @Override
    public void connectedUsersChanged(int n) {
    }

    @Override
    public void monitorModePermissionChanged(boolean bl) {
    }

    @Override
    public void monitorModeChanged(boolean bl) {
    }

    @Override
    public void exclusiveModePermissionChanged(boolean bl) {
    }

    @Override
    public void exclusiveModeChanged(boolean bl) {
    }

    @Override
    public void languageChanged(Locale locale) {
    }

    @Override
    public void protocolVersionChanged(String string) {
    }

    @Override
    public void deviceNameChanged(String string) {
    }

    @Override
    public void framesPerSecond(int n) {
    }

    @Override
    public void incomingTrafficSpeed(int n) {
    }

    @Override
    public void outgoingTrafficSpeed(int n) {
    }

    @Override
    public void chatWelcomeChanged(String string) {
    }

    @Override
    public void newChatMessage(String string) {
    }

    @Override
    public void cimLanguageOptionsSupported(boolean bl) {
    }

    @Override
    public void ethernetGigabitSupported(boolean bl) {
    }

    @Override
    public void keyboardEvent(int n, boolean bl) {
    }

    @Override
    public void hotkeyDetected(int n) {
    }

    @Override
    public void ctrlAltReleaseDetected(boolean bl) {
    }

    @Override
    public void audioSupportChanged(boolean bl) {
    }

    @Override
    public void mouseModeChangeSupportChanged(boolean bl) {
    }

    @Override
    public void supportedMouseModesChanged(List<RCCore.MouseMode> list) {
    }

    @Override
    public void mouseModeChanged(RCCore.MouseMode mouseMode) {
    }

    @Override
    public void singleCursorModeSupportChanged(boolean bl) {
    }

    @Override
    public void singleCursorModeChanged(boolean bl) {
    }

    @Override
    public void mouseSyncSupportChanged(boolean bl) {
    }

    @Override
    public void absoluteMouseEvent(int n, int n2, int n3) {
    }

    @Override
    public void relativeMouseEvent(int n, int n2, int n3) {
    }

    @Override
    public void mouseWheelEvent(int n, int n2) {
    }

    @Override
    public void virtualMediaSupportChanged(boolean bl) {
    }

    @Override
    public void virtualMediaReadOnlyChanged(boolean bl) {
    }

    @Override
    public void virtualMediaDriveCountChanged(int n) {
    }

    @Override
    public void virtualMediaConfigChanged(IVMConfigInfo iVMConfigInfo) {
    }

    @Override
    public void UsbProfileListChanged(IUsbProfileList iUsbProfileList) {
    }

    @Override
    public void remoteIsoSupportChanged(boolean bl) {
    }

    @Override
    public void remoteIsoListChanged(List<IVMMountRequestResponse> list) {
    }

    @Override
    public void remoteIsoMountFinished(IVMMountRequestResponse iVMMountRequestResponse) {
    }
}

