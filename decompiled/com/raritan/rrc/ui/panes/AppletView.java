/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.AppletStub
 *  javax.swing.JApplet
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.HtmlPort;
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
import com.raritan.tools.util.Util;
import java.applet.AppletStub;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import javaclientlib.tr.Constants;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class AppletView
extends DeviceViewAdapter {
    private static final long serialVersionUID = 4778443664182470436L;
    private Device dev = null;
    private ScreenContext scrContext = null;
    private EmbedAppletDataHolder holder = null;
    private boolean loaded = true;
    private Class claz = null;
    private HtmlPortThread thread;
    private HtmlPort htmlPort;
    private JApplet applet;
    private EmbedAppletClassLoader classLoader;
    private EmbedAppletStub stub;
    protected RaritanPropertyResourceBundle bundle;
    private static final String LOADING_CARD = "loadingCard";
    private static final String APPLET_CARD = "appletCard";
    private JPanel loadingPanel = null;
    private JPanel appletPanel = null;
    private JProgressBar progress;

    public AppletView(boolean bl, ScreenContext screenContext) {
        super((RRCScreenContext)screenContext);
        this.scrContext = screenContext;
        this.isNewPanel = true;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.htmlPort = (HtmlPort)((ArrayList)((RRCScreenContext)screenContext).getSelectedDevicesObservable().getComponent()).get(0);
        this.makeLayout();
    }

    @Override
    public void makeLayout() {
        this.setLayout(new CardLayout());
        this.loadingPanel = new JPanel();
        this.loadingPanel.setLayout(new GridBagLayout());
        this.progress = new JProgressBar(0, 500);
        this.progress.setIndeterminate(true);
        this.loadingPanel.add((Component)new JLabel(this.bundle.getString("loading.message")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        this.loadingPanel.add((Component)this.progress, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        this.appletPanel = new JPanel();
        this.add(LOADING_CARD, this.loadingPanel);
        this.add(APPLET_CARD, this.appletPanel);
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)((RRCScreenContext)this.scrContext).getPanelMediator().getParent();
        this.setSize(raritanDesktopPane.getSize());
        this.setFocusable(true);
        this.htmlPort.setView(this);
    }

    public void loadApplet(String string) {
        if (string != null && !string.equals("")) {
            try {
                String string2;
                String string3;
                this.htmlPort.connect();
                URL uRL = new URL(string);
                this.holder = new EmbedAppletDataHolder();
                this.stub = new EmbedAppletStub((RRCScreenContext)this.scrContext, this.holder, uRL);
                if (this.htmlPort.getDevice().getDeviceConnector() != null) {
                    string3 = this.htmlPort.getDevice().getDeviceConnector().getUsername();
                    string2 = this.htmlPort.getDevice().getDeviceConnector().getPassword();
                    this.stub.setParameter("jrrc.exit", "1");
                    this.stub.setParameter("jrrc.sessionID", this.htmlPort.getSessionId());
                }
                this.stub.setParameter("host", uRL.getHost());
                string3 = String.valueOf(this.htmlPort.getDevice().getIPPort());
                this.stub.setParameter("login.port", string3);
                this.classLoader = new EmbedAppletClassLoader(new URL[]{uRL}, null);
                string2 = Constants.ADMIN_APPLET_CLASSNAME;
                this.claz = this.classLoader.loadClass(string2);
                this.applet = (JApplet)this.claz.newInstance();
                this.applet.setSize((int)this.getSize().getHeight() - 20, (int)this.getSize().getWidth() - 20);
                this.applet.setStub((AppletStub)this.stub);
                if (!this.htmlPort.isConnected()) {
                    return;
                }
                URLClassLoader uRLClassLoader = new URLClassLoader(new URL[]{new URL("file:./")}, null);
                Thread.currentThread().setContextClassLoader(uRLClassLoader);
                this.applet.init();
                this.applet.start();
                this.applet.validate();
                this.appletPanel.setLayout(new GridBagLayout());
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.fill = 1;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                Object var7_13 = null;
                this.appletPanel.add((Component)this.applet.getRootPane(), gridBagConstraints);
                this.appletPanel.revalidate();
                ((CardLayout)this.getLayout()).show(this, APPLET_CARD);
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(this.bundle.getString("loaded.message"));
                this.loaded = true;
            }
            catch (ClassCastException classCastException) {
            }
            catch (MalformedURLException malformedURLException) {
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (InstantiationException instantiationException) {
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
            catch (NullPointerException nullPointerException) {
                // empty catch block
            }
        }
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.dev = (Device)commandContext.getCommandParameter("selectedPortDevice");
        if (this.dev != null) {
            ((CardLayout)this.getLayout()).show(this, LOADING_CARD);
            this.thread = new HtmlPortThread(this);
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
        if (this.applet != null && this.applet.isVisible()) {
            this.applet.setStub(null);
            try {
                this.applet.destroy();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.applet = null;
        }
        this.thread.interrupt();
        this.thread = null;
    }

    public JApplet getApplet() {
        return this.applet;
    }

    @Override
    public void setDefaultFocussedComponent() {
        if (this.applet != null) {
            this.applet.requestFocusInWindow();
        }
    }

    @Override
    public boolean hasFocus() {
        return this.getShellInternalFrame() != null && this.getShellInternalFrame().isSelected();
    }

    private class HtmlPortThread
    extends Thread {
        private AppletView view;

        public HtmlPortThread(AppletView appletView2) {
            this.setName("KXMThread");
            this.view = appletView2;
        }

        @Override
        public void run() {
            if (this.view != null) {
                String string = "http://";
                DeviceConnector deviceConnector = AppletView.this.dev.getDeviceConnector();
                AppletView.this.loadApplet(new String(string + Util.getURLCompatibleIP(deviceConnector.getInetAddress(), AppletView.this.dev.getHttpsPort()) + "/" + Constants.ADMIN_APPLET_JARFILE).trim());
            }
        }
    }
}

