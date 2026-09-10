/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.rfbbridge;

import com.raritan.rrc.ui.rfbbridge.RFBView;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;
import javaclientlib.utils.RRCLogger;
import nn.pp.core.INotificationEvent;
import nn.pp.rccore.IKvmPort;
import nn.pp.rccore.IUsbProfileList;
import nn.pp.rccore.IVMConfigInfo;
import nn.pp.rccore.IVMMountRequestResponse;
import nn.pp.rccore.RCAdapter;
import nn.pp.rccore.RCCore;

public class MainScreenRCAdapter
extends RCAdapter {
    RFBView owner;

    public MainScreenRCAdapter(RFBView rFBView) {
        this.owner = rFBView;
    }

    @Override
    public void disconnected(Exception exception) {
        if (this.owner != null) {
            this.owner.disconnect();
        }
    }

    @Override
    public void connected() {
        this.owner.setFullyConnected(true);
        if (this.owner.getVideoSettingsSupported()) {
            try {
                this.owner.getRCCore().requestVideoSettingsUpdates();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.owner.rcConnected();
        this.owner.setFrameSize();
    }

    @Override
    public void resolutionChanged(Dimension dimension) {
        this.owner.clearStatusMessage();
    }

    @Override
    public void hotkeyDetected(int n) {
        this.owner.handleHotkeys(n);
    }

    @Override
    public String portListChanged(List<? extends IKvmPort> list) {
        String string = this.owner.getTargetPortId();
        if (string != null) {
            for (IKvmPort iKvmPort : list) {
                if (!iKvmPort.equals(string)) continue;
                this.owner.setKvmPort(iKvmPort);
                return iKvmPort.getUniquePortId();
            }
        }
        return null;
    }

    @Override
    public void monitorModeChanged(boolean bl) {
        this.owner.setMonitorOnly(bl);
    }

    @Override
    public void serverSessionIdChanged(int n) {
        this.owner.setRfbSessionId(n);
    }

    @Override
    public void osdMessageReceived(String string, boolean bl) {
        this.owner.osdStateChanged(bl, string);
    }

    @Override
    public void remoteIsoListChanged(List<IVMMountRequestResponse> list) {
        this.owner.setRemoteIsoList(list);
    }

    @Override
    public void receivedNotification(INotificationEvent iNotificationEvent) {
        this.owner.notify(iNotificationEvent);
    }

    @Override
    public void remoteIsoMountFinished(IVMMountRequestResponse iVMMountRequestResponse) {
        this.owner.processVMMountResponse(iVMMountRequestResponse);
    }

    @Override
    public void supportedMouseModesChanged(List<RCCore.MouseMode> list) {
        this.owner.enableMouseModes(list);
    }

    @Override
    public void mouseModeChanged(RCCore.MouseMode mouseMode) {
        this.owner.setMouseModeMenuItem(mouseMode);
    }

    @Override
    public void sunKeyboardSupported(boolean bl) {
        this.owner.setSunTarget(bl);
    }

    @Override
    public void cimLanguageOptionsSupported(boolean bl) {
        this.owner.setCimLanguageOptionsSupported(bl);
    }

    @Override
    public void UsbProfileListChanged(IUsbProfileList iUsbProfileList) {
        this.owner.setUsbProfileList(iUsbProfileList);
    }

    @Override
    public void virtualMediaConfigChanged(IVMConfigInfo iVMConfigInfo) {
        this.owner.setVirtualMediaConfig(iVMConfigInfo.getVMConfig());
    }

    @Override
    public void connectedUsersChanged(int n) {
        this.owner.setConcurrentUsers(n);
    }

    @Override
    public void ctrlAltReleaseDetected(boolean bl) {
        if (bl) {
            this.owner.showFSExitTip();
        }
    }

    @Override
    public void keyboardLedStateChanged(List<RCCore.KeyboardLed> list) {
        if (this.owner != null) {
            RRCLogger.log(300, "LED Status: " + list);
            if (list.contains((Object)RCCore.KeyboardLed.LED_CAPSLOCK)) {
                this.owner.setCapsLockStatus(true);
            } else {
                this.owner.setCapsLockStatus(false);
            }
            if (list.contains((Object)RCCore.KeyboardLed.LED_NUMLOCK)) {
                this.owner.setNumLockStatus(true);
            } else {
                this.owner.setNumLockStatus(false);
            }
            if (list.contains((Object)RCCore.KeyboardLed.LED_SCROLLLOCK)) {
                this.owner.setScrollLockStatus(true);
            } else {
                this.owner.setScrollLockStatus(false);
            }
        } else {
            RRCLogger.log(300, "LED Status: RFBView is null");
        }
    }
}

