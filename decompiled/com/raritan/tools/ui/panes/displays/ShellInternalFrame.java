/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.displays;

import com.raritan.tools.commands.Command;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class ShellInternalFrame
extends JInternalFrame
implements InternalFrameListener {
    private static final long serialVersionUID = 8746040099815503198L;
    private Command command = null;
    private boolean isWindowClosing = false;

    public ShellInternalFrame() {
        this.addInternalFrameListener(this);
    }

    protected void processWindowEvent(InternalFrameEvent internalFrameEvent) {
        InternalFrameListener[] internalFrameListenerArray = this.getInternalFrameListeners();
        if (internalFrameListenerArray.length > 0) {
            InternalFrameListener internalFrameListener = internalFrameListenerArray[0];
            switch (internalFrameEvent.getID()) {
                case 200: {
                    this.internalFrameOpened(internalFrameEvent);
                    break;
                }
                case 201: {
                    this.isWindowClosing = true;
                    internalFrameListener.internalFrameClosing(internalFrameEvent);
                    break;
                }
                case 202: {
                    internalFrameListener.internalFrameClosed(internalFrameEvent);
                    break;
                }
                case 203: {
                    internalFrameListener.internalFrameIconified(internalFrameEvent);
                    break;
                }
                case 204: {
                    internalFrameListener.internalFrameDeiconified(internalFrameEvent);
                    break;
                }
                case 205: {
                    internalFrameListener.internalFrameActivated(internalFrameEvent);
                    break;
                }
                case 206: {
                    internalFrameListener.internalFrameDeactivated(internalFrameEvent);
                    break;
                }
            }
        }
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent internalFrameEvent) {
        try {
            this.setMaximum(true);
        }
        catch (PropertyVetoException propertyVetoException) {
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
        switch (this.getDefaultCloseOperation()) {
            case 1: {
                this.setVisible(false);
                break;
            }
            case 2: {
                this.setVisible(false);
                this.dispose();
                break;
            }
        }
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {
        if (this.command != null) {
            this.command.execute();
        }
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent) {
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {
        try {
            this.setMaximum(true);
        }
        catch (PropertyVetoException propertyVetoException) {
            // empty catch block
        }
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.getDesktopPane();
        int n = 1;
        int n2 = 0;
        JInternalFrame[] jInternalFrameArray = raritanDesktopPane.getAllFrames();
        for (int i = 0; i < jInternalFrameArray.length; ++i) {
            if (!jInternalFrameArray[i].isIcon()) continue;
            JInternalFrame.JDesktopIcon jDesktopIcon = jInternalFrameArray[i].getDesktopIcon();
            if ((n2 + 1) * jDesktopIcon.getWidth() > raritanDesktopPane.getWidth()) {
                ++n;
                n2 = 0;
            }
            jDesktopIcon.setLocation(n2 * jDesktopIcon.getWidth(), raritanDesktopPane.getHeight() - n * jDesktopIcon.getHeight());
            ++n2;
        }
    }

    public Command getCommand() {
        return this.command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean isWindowClosing() {
        return this.isWindowClosing;
    }

    public void setWindowClosing(boolean bl) {
        this.isWindowClosing = bl;
    }
}

