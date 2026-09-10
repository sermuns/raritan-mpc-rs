/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.Command;
import com.raritan.tools.ui.panes.displays.ShellInternalFrame;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;

public class RRCShellInternalFrame
extends ShellInternalFrame {
    private static final long serialVersionUID = -4032147001877990192L;
    private Command frameClosedCommand;
    private Command frameActivatedCommand;
    private DeviceView deviceView;
    private Command frameDeActivatedCommand;

    public RRCShellInternalFrame() {
        this.setDefaultCloseOperation(2);
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {
        if (this.frameClosedCommand != null) {
            this.setWindowClosing(true);
            this.frameClosedCommand.execute();
        }
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
        if (this.frameActivatedCommand != null) {
            this.frameActivatedCommand.execute();
        }
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
        super.internalFrameClosing(internalFrameEvent);
        this.setWindowClosing(true);
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {
        if (this.deviceView != null && this.deviceView.allowMaximumSize()) {
            super.internalFrameDeiconified(internalFrameEvent);
        }
        if (this.frameActivatedCommand != null) {
            this.frameActivatedCommand.execute();
        }
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
        super.internalFrameIconified(internalFrameEvent);
        if (this.frameDeActivatedCommand != null) {
            this.frameDeActivatedCommand.execute();
        }
    }

    public void setFrameActivatedCommand(Command command) {
        this.frameActivatedCommand = command;
    }

    public void setFrameClosedCommand(Command command) {
        this.frameClosedCommand = command;
    }

    public void setDeviceView(DeviceView deviceView) {
        this.deviceView = deviceView;
    }

    public DeviceView getDeviceView() {
        return this.deviceView;
    }

    public void setFrameDeActivatedCommand(Command command) {
        this.frameDeActivatedCommand = command;
    }

    @Override
    public void setSelected(boolean bl) throws PropertyVetoException {
        JInternalFrame jInternalFrame;
        if (bl && this.getDesktopPane() != null && (jInternalFrame = this.getDesktopPane().getSelectedFrame()) != null && this != jInternalFrame) {
            jInternalFrame.setSelected(false);
        }
        super.setSelected(bl);
    }
}

