/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.AppletStub
 *  javax.swing.JApplet
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.G2SerialPort;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.applet.EmbedAppletClassLoader;
import com.raritan.rrc.ui.applet.EmbedAppletDataHolder;
import com.raritan.rrc.ui.applet.EmbedAppletStub;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.ui.panes.DeviceViewAdapter;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.ui.panes.displays.ShellInternalFrame;
import com.raritan.tools.util.Util;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javaclientlib.utils.RRCLogger;
import javax.swing.JApplet;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class RSCView
extends DeviceViewAdapter {
    private static final long serialVersionUID = 4778443664182470436L;
    private Device dev = null;
    private ScreenContext scrContext = null;
    private EmbedAppletDataHolder holder = null;
    private Class claz = null;
    private SerialPortThread thread;
    private G2SerialPort serialPort;
    private JApplet applet;
    private EmbedAppletClassLoader classLoader;
    private EmbedAppletStub stub;
    protected RaritanPropertyResourceBundle bundle;
    private static final String LOADING_CARD = "loadingCard";
    private static final String APPLET_CARD = "appletCard";
    private JPanel loadingPanel = null;
    private JPanel appletPanel = null;
    private JProgressBar progress;
    private Method defFocusMethod = null;

    public RSCView(boolean bl, ScreenContext screenContext) {
        super((RRCScreenContext)screenContext);
        this.scrContext = screenContext;
        this.isNewPanel = true;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.serialPort = (G2SerialPort)((ArrayList)((RRCScreenContext)screenContext).getSelectedDevicesObservable().getComponent()).get(0);
        this.serialPort.setView(this);
        this.makeLayout();
    }

    @Override
    public void makeLayout() {
        this.setLayout(new CardLayout());
        this.loadingPanel = new JPanel();
        this.loadingPanel.setLayout(new GridBagLayout());
        this.progress = new JProgressBar(0, 500);
        this.progress.setIndeterminate(true);
        this.loadingPanel.add((Component)new JLabel(this.bundle.getString("RSC_LOADING_MSG")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        this.loadingPanel.add((Component)this.progress, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        this.appletPanel = new JPanel();
        this.add(LOADING_CARD, this.loadingPanel);
        this.add(APPLET_CARD, this.appletPanel);
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)((RRCScreenContext)this.scrContext).getPanelMediator().getParent();
        this.setSize(raritanDesktopPane.getSize());
        this.setFocusable(true);
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(this.bundle.getString("RSC_LOADING_MSG"));
    }

    public void loadApplet(String string, String string2, int n) {
        if (string2 != null && !string2.equals("")) {
            try {
                this.holder = new EmbedAppletDataHolder();
                this.stub = new EmbedAppletStub((RRCScreenContext)this.scrContext, this.holder, new URL(string, string2, n, this.serialPort.getRSCJarName()));
                this.stub.setParameter("host", string2);
                IPReach iPReach = (IPReach)this.serialPort.getDevice();
                this.stub.setParameter("TCPPort", String.valueOf(iPReach.getIPPort()));
                this.stub.setParameter("target", Integer.toString(this.serialPort.getPortIndex() + 1));
                RRCLogger.log(300, 4, "Connecting to port " + Integer.toString(this.serialPort.getPortIndex() + 1));
                this.stub.setParameter("SessionID", iPReach.getRdmSessionId());
                this.stub.setParameter("SessionKey", iPReach.getRdmSessionKey());
                RRCLogger.log(300, 4, "Connecting to serial port:" + Integer.toString(this.serialPort.getPortIndex() + 1) + " on Device " + string2 + "using TCP Port " + String.valueOf(iPReach.getIPPort()) + " .RDM Session Info:" + iPReach.getRdmSessionId() + ":" + iPReach.getRdmSessionKey());
                this.classLoader = new EmbedAppletClassLoader(new URL[]{this.serialPort.getDevice().getRscFileHandle().toURL()}, null);
                String string3 = "com.raritan.serialconsole.sx.ui.InternalFrameSerialApplet";
                this.claz = this.classLoader.loadClass(string3);
                JApplet jApplet = (JApplet)this.claz.newInstance();
                this.setApplet(jApplet);
                jApplet.setSize((int)this.getSize().getHeight() - 20, (int)this.getSize().getWidth() - 20);
                jApplet.setStub((AppletStub)this.stub);
                URLClassLoader uRLClassLoader = new URLClassLoader(new URL[]{new URL("file:./")}, null);
                Thread.currentThread().setContextClassLoader(uRLClassLoader);
                jApplet.init();
                jApplet.start();
                jApplet.validate();
                Method method = jApplet.getClass().getMethod("getFrameComponent", null);
                Object object = method.invoke((Object)jApplet, null);
                Method method2 = jApplet.getClass().getMethod("setDefaultFocus", null);
                this.setDefaultFocusMethod(method2);
                method = jApplet.getClass().getMethod("addObserver", Observer.class);
                method.invoke((Object)jApplet, new RSCObserver(this));
                if (!(object instanceof JInternalFrame)) {
                    throw new IllegalArgumentException("Unknown component " + object.getClass().getName());
                }
                this.appletPanel.setLayout(new BorderLayout());
                JInternalFrame jInternalFrame = (JInternalFrame)object;
                this.appletPanel.add((Component)jInternalFrame.getRootPane(), "Center");
                this.appletPanel.revalidate();
                ((CardLayout)this.getLayout()).show(this, APPLET_CARD);
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel("");
                method = jApplet.getClass().getMethod("connect", null);
                object = method.invoke((Object)jApplet, null);
                method = jApplet.getClass().getMethod("syncScreenSize", null);
                method.invoke((Object)jApplet, null);
                if (object instanceof Boolean) {
                    this.serialPort.setConnected((Boolean)object);
                }
            }
            catch (ClassCastException classCastException) {
                RRCLogger.logException(classCastException);
            }
            catch (MalformedURLException malformedURLException) {
                RRCLogger.logException(malformedURLException);
            }
            catch (SecurityException securityException) {
                RRCLogger.logException(securityException);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                RRCLogger.logException(noSuchMethodException);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                RRCLogger.logException(illegalArgumentException);
            }
            catch (IllegalAccessException illegalAccessException) {
                RRCLogger.logException(illegalAccessException);
            }
            catch (InvocationTargetException invocationTargetException) {
                RRCLogger.logException(invocationTargetException);
            }
            catch (ClassNotFoundException classNotFoundException) {
                RRCLogger.logException(classNotFoundException);
            }
            catch (InstantiationException instantiationException) {
                RRCLogger.logException(instantiationException);
            }
        }
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.dev = (Device)commandContext.getCommandParameter("selectedPortDevice");
        if (this.dev != null) {
            ((CardLayout)this.getLayout()).show(this, LOADING_CARD);
            this.thread = new SerialPortThread(this);
            this.thread.start();
            return;
        }
    }

    @Override
    public void feedCommandContext(CommandContext commandContext) {
        this.stub = null;
        this.classLoader = null;
        this.claz = null;
        this.holder = null;
        JApplet jApplet = this.getApplet();
        if (jApplet != null && jApplet.isVisible()) {
            jApplet.setStub(null);
            try {
                jApplet.destroy();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            jApplet = null;
        }
        this.thread.interrupt();
        this.thread = null;
    }

    public synchronized JApplet getApplet() {
        return this.applet;
    }

    private synchronized void setApplet(JApplet jApplet) {
        this.applet = jApplet;
    }

    @Override
    public void setViewFocus() {
        Method method = this.getDefaultFocusMethod();
        if (method != null && this.applet != null) {
            try {
                method.invoke((Object)this.applet, null);
            }
            catch (Exception exception) {
                RRCLogger.logException(exception);
            }
        }
    }

    @Override
    public void setDefaultFocussedComponent() {
        JApplet jApplet = this.getApplet();
        if (jApplet != null) {
            jApplet.requestFocusInWindow();
        }
    }

    public void disconnected() {
        ((RRCScreenContext)this.scrContext).removePortInObservable(this.serialPort);
        this.getShellInternalFrame().dispose();
    }

    private synchronized void setDefaultFocusMethod(Method method) {
        this.defFocusMethod = method;
    }

    private synchronized Method getDefaultFocusMethod() {
        return this.defFocusMethod;
    }

    @Override
    public boolean hasFocus() {
        ShellInternalFrame shellInternalFrame = this.getShellInternalFrame();
        return shellInternalFrame != null && shellInternalFrame.isSelected();
    }

    @Override
    public boolean allowMaximumSize() {
        return false;
    }

    class RSCObserver
    implements Observer {
        private RSCView view;

        public RSCObserver(RSCView rSCView2) {
            this.view = rSCView2;
        }

        @Override
        public void update(Observable observable, Object object) {
            if (object.toString().startsWith("Exited")) {
                this.view.disconnected();
            } else if (object instanceof Exception) {
                Exception exception = (Exception)object;
                JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(RSCView.this), exception.getMessage(), RSCView.this.bundle.getString("optionpane.error.title"), 0);
                this.view.disconnected();
            } else if ("pack".equals(object)) {
                RSCView.this.getShellInternalFrame().pack();
                RSCView.this.setViewFocus();
            }
        }
    }

    private class SerialPortThread
    extends Thread {
        private RSCView view;

        public SerialPortThread(RSCView rSCView2) {
            this.setName("RSCThread");
            this.view = rSCView2;
        }

        @Override
        public void run() {
            if (this.view != null) {
                RSCView.this.loadApplet("https", Util.getURLCompatibleIP(RSCView.this.dev.getDeviceConnector().getInetAddress()), RSCView.this.dev.getHttpsPort());
            }
        }
    }
}

