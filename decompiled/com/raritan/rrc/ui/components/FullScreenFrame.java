/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCApplet;
import com.raritan.rrc.ui.RRCMain;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.FullScreenToolBar;
import com.raritan.rrc.ui.screens.RRCScreenManager;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.ui.AbstractUIManager;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.util.ImageHolder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javaclientlib.utils.RRCLogger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class FullScreenFrame
extends JFrame {
    private final RRCScreenContext ctx;
    private final RRCMain rrcMain;
    private final RRCScreenManager screenManager;
    private final RaritanPropertyResourceBundle bundle;
    private final GraphicsConfiguration gc;
    private JInternalFrame viewFrame;
    private Port port;
    private AbstractUIManager.FullScreenTarget target;
    private JComponent northPane;
    private Dimension northPaneSize;
    private Rectangle viewFrameBounds;
    private Border jifBorder;
    private FullScreenToolBar bar;
    private boolean disposed = false;
    private static FullScreenFrame activeFrame = null;
    private static List<FullScreenFrame> allFrames = new Vector<FullScreenFrame>();
    private static boolean ignore = false;
    private WindowAdapter selectionWindowAdapter = new WindowAdapter(){

        @Override
        public void windowActivated(WindowEvent windowEvent) {
            FullScreenFrame.this.activateView();
        }
    };
    private MouseAdapter selectionMouseAdapter = new MouseAdapter(){

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            if (activeFrame != FullScreenFrame.this) {
                FullScreenFrame.this.activateView();
            }
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            if (activeFrame != FullScreenFrame.this) {
                FullScreenFrame.this.activateView();
            }
        }
    };

    public FullScreenFrame(RRCScreenContext rRCScreenContext, RRCMain rRCMain, RRCScreenManager rRCScreenManager, RaritanPropertyResourceBundle raritanPropertyResourceBundle, GraphicsConfiguration graphicsConfiguration, JInternalFrame jInternalFrame, Port port, AbstractUIManager.FullScreenTarget fullScreenTarget) {
        super(graphicsConfiguration);
        this.ctx = rRCScreenContext;
        this.rrcMain = rRCMain;
        this.screenManager = rRCScreenManager;
        this.bundle = raritanPropertyResourceBundle;
        this.gc = graphicsConfiguration;
        this.viewFrame = jInternalFrame;
        this.port = port;
        this.target = fullScreenTarget;
        allFrames.add(this);
        ImageHolder imageHolder = new ImageHolder(rRCScreenContext);
        this.setTitle(raritanPropertyResourceBundle.getString("main.screen.title"));
        this.setIconImage(imageHolder.getImage(raritanPropertyResourceBundle.getString("raritan.icon")));
        this.setUndecorated(true);
        this.northPane = ((BasicInternalFrameUI)jInternalFrame.getUI()).getNorthPane();
        this.northPaneSize = this.northPane.getSize();
        this.jifBorder = jInternalFrame.getBorder();
        this.viewFrameBounds = jInternalFrame.getBounds();
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add((Component)jInternalFrame, "Center");
        boolean bl = false;
        if (rRCScreenContext != null) {
            bl = rRCScreenContext.getAppSettings().isPinMenu();
        }
        this.bar = new FullScreenToolBar(rRCScreenContext, raritanPropertyResourceBundle, jPanel, rRCMain, jInternalFrame, this.jifBorder, this.northPane, this.northPaneSize, this.viewFrameBounds, bl, graphicsConfiguration, port, this.selectionMouseAdapter, fullScreenTarget != AbstractUIManager.FullScreenTarget.SECONDARY, fullScreenTarget == AbstractUIManager.FullScreenTarget.SECONDARY);
        this.getContentPane().add((Component)this.bar, "North");
        this.bar.setVisible(true);
        this.getContentPane().add((Component)jPanel, "Center");
    }

    @Override
    public void setVisible(boolean bl) {
        if (this.disposed) {
            super.setVisible(bl);
            return;
        }
        if (bl) {
            Object object;
            activeFrame = this;
            this.northPane.setVisible(false);
            this.viewFrame.setBorder(new EmptyBorder(0, 0, 0, 0));
            Dimension dimension = this.gc.getBounds().getSize();
            this.northPane.setPreferredSize(new Dimension(0, 0));
            if (this.ctx.getApplication() instanceof RRCApplet) {
                this.setAlwaysOnTop(true);
            }
            switch (this.screenManager.getFullScreenStrategy()) {
                case 1: {
                    this.setSize(dimension.width, dimension.height);
                    break;
                }
                case 2: {
                    object = Toolkit.getDefaultToolkit().getScreenInsets(this.gc);
                    this.setSize(dimension.width - ((Insets)object).left - ((Insets)object).right, dimension.height - ((Insets)object).top - ((Insets)object).bottom);
                    break;
                }
                case 3: {
                    this.setExtendedState(6);
                    break;
                }
                default: {
                    this.rrcMain.getGraphicsDevice().setFullScreenWindow(this);
                }
            }
            this.addWindowListener(this.selectionWindowAdapter);
            if (this.ctx.getSelectedDevicesObservable() != null && this.ctx.getSelectedDevicesObservable().getComponent() != null) {
                object = (Device)((ArrayList)this.ctx.getSelectedDevicesObservable().getComponent()).get(0);
                this.bar.enableMenuBar((Device)object);
            }
        } else {
            this.removeWindowListener(this.selectionWindowAdapter);
            if (this.screenManager.getFullScreenStrategy() == 0) {
                this.rrcMain.getGraphicsDevice().setFullScreenWindow(null);
            }
            RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.ctx.getPanelMediator().getParent();
            this.viewFrame.setSize(raritanDesktopPane.getSize());
            raritanDesktopPane.add(this.viewFrame);
            try {
                if (this.viewFrame.isIcon()) {
                    this.viewFrame.setIcon(false);
                }
            }
            catch (PropertyVetoException propertyVetoException) {
                RRCLogger.logException(propertyVetoException);
            }
            try {
                if (this == activeFrame) {
                    if (!this.viewFrame.isSelected()) {
                        this.viewFrame.setSelected(true);
                    } else {
                        this.viewFrame.toFront();
                    }
                } else {
                    this.viewFrame.setSelected(false);
                }
            }
            catch (PropertyVetoException propertyVetoException) {
                propertyVetoException.printStackTrace();
                RRCLogger.logException(propertyVetoException);
            }
            this.northPane.setPreferredSize(this.northPaneSize);
            this.northPane.setVisible(true);
            this.viewFrame.setBorder(this.jifBorder);
            this.viewFrame.setBounds(this.viewFrameBounds);
        }
        super.setVisible(bl);
    }

    @Override
    public void dispose() {
        this.removeWindowListener(this.selectionWindowAdapter);
        this.bar.dispose();
        allFrames.remove(this);
        if (activeFrame == this || allFrames.isEmpty()) {
            activeFrame = null;
        }
        this.disposed = true;
        super.dispose();
    }

    public Port getPort() {
        return this.port;
    }

    public FullScreenToolBar getFSToolBar() {
        return this.bar;
    }

    public void updateInternalFrameReference(Port port, Port port2, JInternalFrame jInternalFrame, Border border, JComponent jComponent, Dimension dimension, Rectangle rectangle) {
        this.port = port2;
        this.viewFrame = jInternalFrame;
        this.jifBorder = border;
        this.northPane = jComponent;
        this.northPaneSize = dimension;
        this.viewFrameBounds = rectangle;
    }

    public void switchToPortView(Port port, Port port2) {
        this.bar.switchToPortView(port, port2, true);
    }

    public JInternalFrame getViewFrame() {
        return this.bar.getViewFrame();
    }

    public static Container getActiveContentPane() {
        if (activeFrame != null) {
            return activeFrame.getContentPane();
        }
        return null;
    }

    public static Container getContentPaneForPort(Port port) {
        for (FullScreenFrame fullScreenFrame : allFrames) {
            if (fullScreenFrame.getPort() != port) continue;
            return fullScreenFrame;
        }
        return null;
    }

    private void activateView() {
        if (ignore || this.disposed) {
            return;
        }
        ignore = true;
        for (FullScreenFrame fullScreenFrame : allFrames) {
            try {
                fullScreenFrame.viewFrame.setSelected(fullScreenFrame == this);
            }
            catch (Exception exception) {}
        }
        activeFrame = this;
        new Timer().schedule(new TimerTask(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                1 var1_1 = this;
                synchronized (var1_1) {
                    ignore = false;
                    MPCUtil.notifyObservers(FullScreenFrame.this.ctx, FullScreenFrame.this.port);
                }
            }
        }, 10L);
    }
}

