/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.Applet
 *  javax.swing.JApplet
 *  netscape.javascript.JSObject
 */
package com.raritan.rrc.ui;

import com.raritan.plaf.RaritanRootPane;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.MonitorSettingsHandler;
import com.raritan.rrc.ui.RRCMain;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.CloseAppletCommand;
import com.raritan.rrc.ui.components.CommandMenuItemCache;
import com.raritan.rrc.ui.components.FullScreenToolBar;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.rrc.ui.panes.mediator.RRCPanelMediator;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.smartcard.SmartCardCoreFactory;
import com.raritan.tools.commands.ConfirmableCommandInterface;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.ui.AbstractUIManager;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.State;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.util.image.DualImageFactory;
import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;
import javaclientlib.utils.RRCLogger;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import netscape.javascript.JSObject;
import nn.pp.common.Util;
import nn.pp.core.JVMVersionInfo;
import nn.pp.core.Platform;

public class RRCApplet
extends JApplet
implements AbstractUIManager {
    private RRCMain main;
    private boolean connected;
    private static final long serialVersionUID = 176995716690523002L;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String[] APPLET_PARAMETERS = new String[]{"default.locale", "application.log.level", "application.log.output", "application.resource.file", "log.panel.history", "log.timestamp.format", "help.url.root", "connection", "dialog.panels", "dialog.panels_cc"};
    private String DocumentBuilderFactory = "javax.xml.parsers.DocumentBuilderFactory";
    private String TransformerFactory = "javax.xml.transform.TransformerFactory";
    private String SAXParserFactory = "javax.xml.parsers.SAXParserFactory";
    private String XSLTCDTMManager = "com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager";
    private Component component;
    private RRCScreenContext context;
    private RaritanRootPane customRootPane = null;
    private static RaritanPropertyResourceBundle bundle = null;
    private List<JDialog> disposedShells = new Vector<JDialog>();

    public void init() {
        Object object;
        boolean bl;
        Frame frame = JOptionPane.getFrameForComponent((Component)((Object)this));
        boolean bl2 = bl = Platform.isLinux() && !"javax.swing.SwingUtilities$SharedOwnerFrame".equals(frame.getClass().getName());
        if (bl) {
            object = new RaritanKeyboardFocusManager();
            KeyboardFocusManager.setCurrentKeyboardFocusManager((KeyboardFocusManager)object);
            Frame frame2 = JOptionPane.getFrameForComponent((Component)((Object)this));
            frame2.addWindowListener(RRCPanelMediator.postLinuxWindowAdapter);
        }
        try {
            object = Logger.getLogger("java.util.prefs");
            ((Logger)object).setUseParentHandlers(false);
            System.setProperty(this.DocumentBuilderFactory, "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            System.setProperty(this.XSLTCDTMManager, "com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager");
            System.setProperty(this.TransformerFactory, "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
            System.setProperty(this.SAXParserFactory, "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
            UIManager.setLookAndFeel("com.raritan.plaf.RaritanLookAndFeel");
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (InstantiationException instantiationException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
            // empty catch block
        }
        this.setSize(800, 600);
        this.context = (RRCScreenContext)RRCScreenContext.getNewInstance(this.getAppletParameters());
        this.context.setApplication(this);
        this.context.setState(State.INIT);
        this.context.setAppletContext(this.getAppletContext());
        this.context.setAppletCodeBase(this.getCodeBase());
        MonitorSettingsHandler.init(this.context);
        this.main = new RRCMain(this, this.context);
        this.main.loadScreenManager();
        bundle = this.main.getBundle();
        object = this.context;
        try {
            SwingUtilities.invokeLater(new Runnable((RRCScreenContext)object){
                final /* synthetic */ RRCScreenContext val$scrCtx;
                {
                    this.val$scrCtx = rRCScreenContext;
                }

                @Override
                public void run() {
                    if (!RRCApplet.this.main.getScreenManager().show()) {
                        CommonPopups.showErrorMessage(bundle.getString("error.connect.port"), RRCApplet.this.getContentPane(), this.val$scrCtx, bundle.getString("optionpane.error.title"));
                        if (MPCUtil.isCCLaunched(RRCApplet.this.context) && RRCApplet.this.component != null) {
                            ((JFrame)RRCApplet.this.component).dispose();
                        }
                    } else if (RRCApplet.this.context.getSelectView() != null) {
                        RRCApplet.this.context.getSelectView().setViewFocus();
                    }
                    RRCApplet.this.connected = true;
                }
            });
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        if (RRCLogger.logEnabled && this.component != null && this.context != null) {
            CommonPopups.showWarningDialog(bundle.getString("optionpane.warning.tite"), bundle.getString("View.warning.log.on"), this.component, this.context);
        }
        RRCLogger.log(200, 4, JVMVersionInfo.getSystemProperties().toString());
        RRCLogger.log(200, 4, "Multi-platform Client Version: " + bundle.getString("about.label.build"));
        RRCLogger.log(200, 4, "Max memory: " + Runtime.getRuntime().maxMemory());
        RRCLogger.log(200, 4, "Disable Direct Draw: " + System.getProperty("sun.java2d.noddraw"));
        if (this.getParameter("connection") != null) {
            this.getFrameComponent();
        }
    }

    @Override
    public Component getOptionComponent() {
        return this;
    }

    @Override
    public URL getIconBase() {
        return this.main.getIconBase();
    }

    protected JRootPane createRootPane() {
        try {
            this.customRootPane = new RaritanRootPane(this);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return this.customRootPane;
    }

    public void destroy() {
        if (MPCUtil.isCCLaunched(this.context)) {
            ArrayList arrayList = this.context.getListOfOpenPorts();
            int n = arrayList.size();
            for (int i = 0; i < n; ++i) {
                Port port;
                String string = arrayList.get(i) != null ? arrayList.get(i).toString() : null;
                if (string == null || this.context == null || !(port = (Port)this.context.getPortByKeyObservable(string)).isConnected()) continue;
                port.disconnect();
            }
        } else {
            DeviceTreeController.getInstance().stop();
        }
        SmartCardCoreFactory.reset();
        if (this.customRootPane != null) {
            this.customRootPane.destroy();
            this.customRootPane = null;
        }
        if (this.context != null) {
            this.context.destroy();
            this.context = null;
        }
        if (this.main != null) {
            this.main.dispose();
            this.main = null;
        }
        if (this.customRootPane != null) {
            this.customRootPane.destroy();
            this.customRootPane = null;
        }
        DeviceTreeController.clearInstance();
        DualImageFactory.clearCachedImages();
        CommandMenuItemCache.destroy();
        super.destroy();
    }

    private Map getAppletParameters() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        String string = null;
        for (int i = 0; i < APPLET_PARAMETERS.length; ++i) {
            string = this.getParameter(APPLET_PARAMETERS[i]);
            if (string == null) continue;
            hashMap.put(APPLET_PARAMETERS[i], string);
        }
        return hashMap;
    }

    @Override
    public boolean isStandalone() {
        return false;
    }

    @Override
    public Container getContentPane() {
        Container container = this.main.getActiveContentPane();
        return container != null ? container : super.getContentPane();
    }

    @Override
    public Container getContentPaneForPort(Port port) {
        Container container = this.main.getContentPaneForPort(port);
        return container != null ? container : this.getContentPane();
    }

    public JComponent getUIComponent() {
        return this.getRootPane();
    }

    public Component getFrameComponent() {
        if (this.component == null) {
            Object object;
            String string = this.getParameter("title");
            String string2 = this.getParameter("InternalFrame");
            if (string2 == null || !string2.equals("true")) {
                object = this.main.getInnerWindowAdapter();
                ((RRCMain.InnerWindowAdapter)object).setConfirmation(true);
                ((RRCMain.InnerWindowAdapter)object).setCommand(new CloseAppletCommand(this.context));
                this.component = new JFrame(string, this.main.getGraphicsConfig());
                ((JFrame)this.component).setContentPane(this.getContentPane());
                ((JFrame)this.component).setDefaultCloseOperation(0);
                ((JFrame)this.component).addWindowListener((WindowListener)object);
            } else {
                object = new InnerFrameAdapter();
                ((InnerFrameAdapter)object).setConfirmation(true);
                this.component = new JInternalFrame(bundle.getString("main.screen.title"));
                ((JInternalFrame)this.component).setContentPane(this.getContentPane());
                ((JInternalFrame)this.component).setDefaultCloseOperation(0);
                ((JInternalFrame)this.component).addInternalFrameListener((InternalFrameListener)object);
            }
            this.component.setVisible(true);
            this.component.setSize(this.getWidth(), this.getHeight());
            object = this.context.getApplicationProperties();
            ((Properties)object).put("embeddedInFrame", "true");
            this.context.setApplicationProperties((Properties)object);
        }
        return this.component;
    }

    public boolean connect() {
        return this.main.getScreenManager() != null && this.main.getScreenManager().getScreenContext() != null && this.main.getScreenManager().getScreenContext().getSelectedDevicesObservable() != null;
    }

    private void appletClosed() {
        this.firePropertyChange("connection", "opened", "closed");
    }

    @Override
    public void disconnect() {
        ArrayList arrayList = (ArrayList)this.main.getScreenManager().getScreenContext().getSelectedDevicesObservable().getComponent();
        if (arrayList != null) {
            Device device = (Device)arrayList.get(0);
            if (device != null && device.isConnected()) {
                device.disconnect();
            }
            if (this.component != null) {
                if (this.component instanceof JInternalFrame) {
                    ((JInternalFrame)this.component).dispose();
                } else {
                    ((JFrame)this.component).dispose();
                }
            }
        }
        this.appletClosed();
    }

    public void stop() {
        try {
            RRCLogger.closeFileHandler();
            if (MPCUtil.isCCLaunched(this.context)) {
                JSObject jSObject = JSObject.getWindow((Applet)this);
                JSObject jSObject2 = (JSObject)jSObject.getMember("parent");
            } else {
                String string = this.getCodeBase().getHost();
                String string2 = "";
                int n = this.getCodeBase().getPort();
                if (n != -1) {
                    string2 = ":" + n;
                }
                URL uRL = new URL("https://" + string + string2 + "/exit.html");
                this.getAppletContext().showDocument(uRL);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.appletClosed();
    }

    @Override
    public synchronized void changeScreen(boolean bl, JInternalFrame jInternalFrame, Port port, AbstractUIManager.FullScreenTarget fullScreenTarget) {
        if (bl) {
            this.main.enterFullScreen(jInternalFrame, port, fullScreenTarget);
        } else {
            this.main.returnFromFullScreen();
        }
    }

    @Override
    public Cursor getBlankCursor() {
        return this.main.getBlankCursor();
    }

    @Override
    public Cursor getDefaultCursor() {
        return this.main.getDefaultCursor();
    }

    @Override
    public GraphicsDevice getGraphicsDevice() {
        return this.main.getGraphicsDevice();
    }

    @Override
    public void setGraphicsDevice(String string) {
        this.main.setGraphicsDevice(string);
    }

    @Override
    public FullScreenToolBar getFSToolBar(JInternalFrame jInternalFrame) {
        return this.main.getFSToolBar(jInternalFrame);
    }

    public void closeAllWindows() {
        this.context.getPanelMediator().hideAll();
    }

    @Override
    public void setFrameVisible(boolean bl) {
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                for (JDialog jDialog : RRCApplet.this.disposedShells) {
                    jDialog.dispose();
                }
                RRCApplet.this.disposedShells.clear();
            }
        };
        if (bl) {
            SwingUtilities.invokeLater(runnable);
        } else {
            runnable.run();
        }
        if (this.component == null) {
            super.getContentPane().getComponent(0).setVisible(bl);
            this.invalidate();
            this.validate();
        } else if (Util.isJavaWithFocusProblem()) {
            Frame frame = JOptionPane.getFrameForComponent((Component)((Object)this));
            frame.setVisible(bl);
            if (bl) {
                frame.setFocusableWindowState(true);
                frame.invalidate();
                frame.validate();
                frame.requestFocus();
            }
        } else {
            super.setVisible(bl);
            if (bl) {
                this.getContentPane().invalidate();
                this.getContentPane().validate();
            }
        }
    }

    @Override
    public void setIconImage(Image image) {
    }

    @Override
    public String getAppId() {
        return this.main.getAppId();
    }

    @Override
    public void addDisposedShell(JDialog jDialog) {
        if (!this.disposedShells.contains(jDialog)) {
            this.disposedShells.add(jDialog);
        }
    }

    private class InnerFrameAdapter
    extends InternalFrameAdapter
    implements ConfirmableCommandInterface {
        private boolean confirmation = false;

        private InnerFrameAdapter() {
        }

        @Override
        public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
            boolean bl = false;
            if (this.getConfirmation()) {
                boolean bl2 = bl = CommonPopups.showExitConfirmationDialog(RRCApplet.this.getContentPane(), (ScreenContext)RRCApplet.this.context) != 2;
            }
            if (!bl) {
                RRCApplet.this.disconnect();
            }
        }

        @Override
        public void setConfirmation(boolean bl) {
            this.confirmation = bl;
        }

        @Override
        public boolean getConfirmation() {
            return this.confirmation;
        }
    }

    public static class LinuxWindowDeactivatedHandler
    extends WindowAdapter {
        @Override
        public void windowDeactivated(WindowEvent windowEvent) {
            RaritanKeyboardFocusManager raritanKeyboardFocusManager = (RaritanKeyboardFocusManager)KeyboardFocusManager.getCurrentKeyboardFocusManager();
            Window window = windowEvent.getWindow();
            if (windowEvent.getOppositeWindow() == null) {
                raritanKeyboardFocusManager.setGlobalActiveWindow(window);
                raritanKeyboardFocusManager.setGlobalFocusedWindow(window);
            }
        }
    }

    public static class RaritanKeyboardFocusManager
    extends DefaultKeyboardFocusManager {
        @Override
        public void setGlobalActiveWindow(Window window) {
            super.setGlobalActiveWindow(window);
        }

        @Override
        public void setGlobalFocusedWindow(Window window) {
            super.setGlobalFocusedWindow(window);
        }
    }
}

