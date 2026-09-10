/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.displays;

import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.ShellComponentAdapter;
import com.raritan.tools.ui.panes.displays.ShellFocusObserver;
import com.raritan.tools.util.Util;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Shell
extends JDialog
implements WindowListener,
WindowFocusListener {
    private static final long serialVersionUID = 941050393440965891L;
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final Point screenCenter = new Point(Shell.screenSize.width / 2, Shell.screenSize.height / 2);
    private int defaultCloseOperation = 1;
    private ShellComponentAdapter sca;
    protected ScreenContext scrContext;
    private boolean alwaysOnTopFlag = false;
    private boolean blockingDialog = false;

    public Shell(ScreenContext screenContext) {
        super(JOptionPane.getFrameForComponent(screenContext.getApplication().getContentPane()), true);
        this.scrContext = screenContext;
        this.initShell();
        this.setAlwaysOnTop(true);
        this.setModal(true);
        this.setFocusable(true);
    }

    public Shell(Component component, ScreenContext screenContext) {
        super(JOptionPane.getFrameForComponent(screenContext.getApplication().getContentPane()), true);
        this.scrContext = screenContext;
        this.initShell();
        this.sca = new ShellComponentAdapter();
        this.sca.setFloatingParent(component);
        this.addComponentListener(this.sca);
    }

    private void initShell() {
        this.addWindowListener(this);
        this.setWindowFocusListener(true);
        this.setFocusableWindowState(true);
    }

    public ShellComponentAdapter getShellComponentAdapter() {
        return this.sca;
    }

    public void setCentered() {
        int n = this.getWidth() / 2;
        int n2 = this.getHeight() / 2;
        Point point = new Point(Shell.screenCenter.x - n, Shell.screenCenter.y - n2);
        this.setLocation(point);
    }

    public void setWindowFocusListener(boolean bl) {
        if (bl) {
            this.addWindowFocusListener(this);
        } else {
            this.removeWindowFocusListener(this);
        }
    }

    @Override
    public void setVisible(final boolean bl) {
        if (bl) {
            ShellFocusObserver.addShellVisible(this, this.scrContext);
        } else {
            ShellFocusObserver.removeShellVisible(this, this.scrContext);
        }
        Util.jre17WorkaroundInheritAlwaysOnTop(this);
        if (this.isBlockingDialog()) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    Shell.super.setVisible(bl);
                    if (bl) {
                        Shell.this.toFront();
                    }
                }
            });
        } else {
            super.setVisible(bl);
        }
        if (!bl) {
            this.scrContext.getApplication().addDisposedShell(this);
        }
    }

    @Override
    public void windowGainedFocus(WindowEvent windowEvent) {
    }

    @Override
    public void windowLostFocus(WindowEvent windowEvent) {
        ShellFocusObserver.setFocusOnLast(this.scrContext);
    }

    public void setClosable(boolean bl) {
        this.defaultCloseOperation = bl ? 1 : 0;
        this.setDefaultCloseOperation(this.defaultCloseOperation);
    }

    @Override
    protected void processWindowEvent(WindowEvent windowEvent) {
        WindowListener[] windowListenerArray = this.getWindowListeners();
        if (windowListenerArray.length > 0) {
            block9: for (int i = 0; i < windowListenerArray.length; ++i) {
                WindowListener windowListener = windowListenerArray[i];
                switch (windowEvent.getID()) {
                    case 200: {
                        this.windowOpened(windowEvent);
                        continue block9;
                    }
                    case 201: {
                        windowListener.windowClosing(windowEvent);
                        continue block9;
                    }
                    case 202: {
                        windowListener.windowClosed(windowEvent);
                        continue block9;
                    }
                    case 203: {
                        windowListener.windowIconified(windowEvent);
                        continue block9;
                    }
                    case 204: {
                        windowListener.windowDeiconified(windowEvent);
                        continue block9;
                    }
                    case 205: {
                        windowListener.windowActivated(windowEvent);
                        continue block9;
                    }
                    case 206: {
                        windowListener.windowDeactivated(windowEvent);
                        continue block9;
                    }
                }
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        switch (this.defaultCloseOperation) {
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
    public void windowActivated(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {
        if (this.alwaysOnTopFlag) {
            this.toFront();
        }
    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {
    }

    public boolean isBlockingDialog() {
        return this.blockingDialog;
    }

    public void setBlockingDialog(boolean bl) {
        this.blockingDialog = bl;
    }
}

