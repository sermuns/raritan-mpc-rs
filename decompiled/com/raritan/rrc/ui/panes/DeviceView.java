/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DeSelectPortViewCommand;
import com.raritan.rrc.ui.commands.DoDisconnectCommand;
import com.raritan.rrc.ui.commands.PopulateKeyboardMenuCommand;
import com.raritan.rrc.ui.commands.SelectPortViewCommand;
import com.raritan.rrc.ui.components.ContextPopupMenu;
import com.raritan.rrc.ui.panes.ICommandHandler;
import com.raritan.rrc.ui.panes.RRCShellInternalFrame;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import javaclientlib.tr.TRLIB_UPDATEINFO;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;
import javaclientlib.tr.TRSRVR_VIDEO_PARAMS;
import nn.pp.audiocore.AudioEventListener;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.common.audio.AudioErrorsAndMessageHandler;
import nn.pp.common.smartcard.SmartCardErrorsAndMessagesHandler;

public abstract class DeviceView
extends AbstractDisplay
implements PropertyChangeListener,
FocusListener {
    public static final String DEVICE_VIEW_WINDOW_CLOSED = "DEVICE_VIEW_WINDOW_CLOSED";
    public static final String DEVICE_VIEW_ACTIVATED = "DEVICE_VIEW_ACTIVATED";
    protected Port port = null;
    private boolean bHasFocus = false;
    private int keyCode;
    protected boolean isAbsoluteMouseSupported = false;
    protected boolean isIntelligentMouseSupported = false;
    protected boolean isStandardMouseSupported = false;
    private SmartCardStatusListeners smartCardStatusListeners = new SmartCardStatusListeners();

    public DeviceView(RRCScreenContext rRCScreenContext) {
        super(rRCScreenContext);
        this.port = (Port)((ArrayList)rRCScreenContext.getSelectedDevicesObservable().getComponent()).get(0);
        this.port.setContext(this.scrContext);
        this.port.setDevView(this);
        this.setName(this.port.getViewName());
        this.setShellInternalFrame(this.port.getViewName());
        this.isInternalFrame = true;
        this.setFocusable(true);
        ((RRCScreenContext)this.scrContext).addPortInObservable(this.port);
    }

    protected void closeSelf() throws PropertyVetoException {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        rRCScreenContext.getOpenPortsObservable().setComponent(null);
        rRCScreenContext.removePortInObservable(this.port);
        Device device = this.port.getBaseDevice();
        if (device.getActiveKvmPort() == this.port) {
            device.setActiveKvmPort(null);
        }
        this.shellInternalFrame.setClosed(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String string = propertyChangeEvent.getPropertyName();
        if (DEVICE_VIEW_WINDOW_CLOSED.equalsIgnoreCase(string)) {
            if (this.port.isConnected()) {
                this.port.setDevView(null);
                this.port.disconnect();
            }
        } else if (DEVICE_VIEW_ACTIVATED.equalsIgnoreCase(string) && !this.port.isActive()) {
            this.port.setActive(true);
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
    }

    @Override
    public void setShellInternalFrame(String string) {
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)((RRCScreenContext)this.scrContext).getPanelMediator().getParent();
        this.shellInternalFrame = new RRCShellInternalFrame();
        this.shellInternalFrame.setTitle(string);
        this.shellInternalFrame.setResizable(this.resizable);
        this.shellInternalFrame.setClosable(true);
        this.shellInternalFrame.pack();
        this.shellInternalFrame.setVisible(false);
        this.shellInternalFrame.setFocusable(true);
        this.shellInternalFrame.getContentPane().add(this);
        this.shellInternalFrame.setResizable(true);
        this.shellInternalFrame.setSize(raritanDesktopPane.getSize());
        this.shellInternalFrame.setMaximizable(true);
        this.shellInternalFrame.setIconifiable(true);
        this.shellInternalFrame.setCommand(new DoDisconnectCommand(this.scrContext));
        DoDisconnectCommand doDisconnectCommand = new DoDisconnectCommand(this.scrContext);
        SelectPortViewCommand selectPortViewCommand = new SelectPortViewCommand(this.scrContext);
        DeSelectPortViewCommand deSelectPortViewCommand = new DeSelectPortViewCommand(this.scrContext);
        selectPortViewCommand.getContext().setCommandParameter("selectedPortDevice", this.port);
        doDisconnectCommand.getContext().setCommandParameter("selectedPortDevice", this.port);
        deSelectPortViewCommand.getContext().setCommandParameter("selectedPortDevice", this.port);
        ((RRCShellInternalFrame)this.shellInternalFrame).setFrameClosedCommand(doDisconnectCommand);
        ((RRCShellInternalFrame)this.shellInternalFrame).setFrameActivatedCommand(selectPortViewCommand);
        ((RRCShellInternalFrame)this.shellInternalFrame).setDeviceView(this);
        ((RRCShellInternalFrame)this.shellInternalFrame).setFrameDeActivatedCommand(deSelectPortViewCommand);
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        this.bHasFocus = true;
    }

    @Override
    public void focusLost(FocusEvent focusEvent) {
        this.bHasFocus = false;
    }

    @Override
    public boolean hasFocus() {
        return this.bHasFocus;
    }

    public abstract boolean disconnect();

    public abstract void setUpdateFrequency(long var1);

    public abstract boolean isTargetScreenResolution();

    public abstract void removeListeners();

    public abstract void notify(int var1, int var2);

    public abstract void updateNotify(TRLIB_UPDATEINFO var1);

    public abstract void newVideoModeNotify(TRRSP_NEW_VIDEO_MODE_DATA var1);

    public abstract void setSingleCursor(boolean var1);

    public abstract boolean isSingleCursor();

    public abstract boolean isScaleVideoFlag();

    public abstract void setTargetScreenResolution(boolean var1);

    public abstract void autoSenseVideo();

    public abstract void calibrateColor();

    public abstract void enterOnScreenMenu();

    public abstract void exitOnScreenMenu();

    public abstract void synchronizeMouse(boolean var1);

    public abstract void setCompParameters(TRSRVR_COMP_PARAMS var1);

    public abstract void refreshScreen();

    public abstract void ResumeVideo();

    public abstract ContextPopupMenu getContextPopupMenu();

    public abstract void setContextMenuKVMVisible(boolean var1);

    public abstract void sendCtrlAltDelete();

    public abstract void sendAltTab();

    public abstract void sendCtrlNumlock();

    public abstract void sendKVMPopupKey();

    public abstract void updateVideoSettings(TRSRVR_VIDEO_PARAMS var1);

    public abstract void toggleTargetScreenResolution();

    public abstract void setScaleVideoFlag(boolean var1);

    public abstract void setViewFocus();

    public abstract void keyboardTypeChanged(int var1);

    public abstract boolean isCommandOperable(AbstractCommand var1);

    public abstract void customizeTarget(AbstractDisplay var1);

    public abstract void forceRepaint();

    public abstract boolean macroMenuActionPerformed(String var1);

    public abstract ICommandHandler getCommandHandler();

    public abstract void setFullScreenMode(boolean var1);

    public abstract boolean isFullScreenMode();

    public abstract void addCustomMouseMotionListener(MouseMotionListener var1);

    public abstract void removeCustomMouseMotionListener(MouseMotionListener var1);

    public abstract void addCustomMouseListener(MouseListener var1);

    public abstract void removeCustomMouseListener(MouseListener var1);

    public abstract boolean getAbsoluteMouseSupported();

    public abstract boolean getIntelligentMouseSupported();

    public abstract boolean getStandardMouseSupported();

    public abstract void setIsAbsoluteMouseSupported(boolean var1);

    public abstract void setIsIntelligentMouseSupported(boolean var1);

    public abstract void setIsStandardMouseSupported(boolean var1);

    public void startVideo() {
    }

    private KeyboardMacrosPreferences getMacro(int n) {
        if (n >= 48 && n <= 57) {
            return PopulateKeyboardMenuCommand.getMacro(n - 48);
        }
        return null;
    }

    public String getKeyCode(KeyEvent keyEvent, boolean bl, boolean bl2) {
        this.keyCode = keyEvent.getKeyCode();
        if (keyEvent.isControlDown() && keyEvent.isAltDown() && bl && bl2) {
            KeyboardMacrosPreferences keyboardMacrosPreferences = this.getMacro(this.keyCode);
            if (this.keyCode == ((RRCScreenContext)this.scrContext).getPopupKeyCode() || keyboardMacrosPreferences != null) {
                bl2 = false;
                if (keyboardMacrosPreferences != null) {
                    return keyboardMacrosPreferences.getMacroSequence();
                }
            }
        }
        return String.valueOf(this.keyCode);
    }

    public boolean isSunTarget() {
        return false;
    }

    public boolean sendKeyboardMacro(String string) {
        return false;
    }

    public boolean allowMaximumSize() {
        return true;
    }

    public SmartCardErrorsAndMessagesHandler getSmartCardErrorsAndMessagesHandler() {
        return null;
    }

    public AudioErrorsAndMessageHandler getAudioErrorsAndMessagesHandler() {
        return null;
    }

    public SmartCardStatusListeners getSmartCardStatusListener() {
        return this.smartCardStatusListeners;
    }

    public RemoteConsoleParameters getRemoteConsoleParameters() {
        return null;
    }

    public int getRfbSessionId() {
        return -1;
    }

    public void setPlaybackState(AudioEventListener.DeviceState deviceState) {
    }

    public void setCaptureState(AudioEventListener.DeviceState deviceState) {
    }

    public class SmartCardStatusListeners
    implements PropertyChangeListener {
        protected boolean connected = false;

        public boolean isConnected() {
            return this.connected;
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        }
    }
}

