/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.swing.JApplet
 */
package com.raritan.rrc.ui.panes;

import amp.powerboard.swing.AmpApp;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.PowerPort;
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
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javaclientlib.clientlib.ISerialStream;
import javaclientlib.clientlib.TRSerialStream;
import javaclientlib.utils.RRCLogger;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

public class PowerPortView
extends DeviceViewAdapter {
    private static final long serialVersionUID = 4778443664182470436L;
    private Device dev = null;
    private ScreenContext scrContext = null;
    private EmbedAppletDataHolder holder = null;
    private boolean loaded = false;
    private Class claz = null;
    private HtmlPortThread thread;
    private PowerPort powerPort;
    private AmpApp applet;
    private EmbedAppletClassLoader classLoader;
    private EmbedAppletStub stub;
    protected RaritanPropertyResourceBundle bundle;
    private static final String LOADING_CARD = "loadingCard";
    private static final String APPLET_CARD = "appletCard";
    private JPanel loadingPanel = null;
    private JPanel appletPanel = null;
    private Method setSerialStreamMethod;
    private Method setDataBytesMethod;
    private JProgressBar progress;

    public PowerPortView(boolean bl, ScreenContext screenContext) {
        super((RRCScreenContext)screenContext);
        this.scrContext = screenContext;
        this.isNewPanel = true;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.powerPort = (PowerPort)((ArrayList)((RRCScreenContext)screenContext).getSelectedDevicesObservable().getComponent()).get(0);
        this.makeLayout();
    }

    @Override
    public void makeLayout() {
        this.setLayout(new CardLayout());
        this.loadingPanel = new JPanel();
        this.loadingPanel.setLayout(new GridBagLayout());
        this.progress = new JProgressBar(0, 500);
        this.progress.setIndeterminate(true);
        this.loadingPanel.add((Component)new JLabel(this.bundle.getString("powerBoardLoading.message")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        this.loadingPanel.add((Component)this.progress, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        this.appletPanel = new JPanel();
        this.add(LOADING_CARD, this.loadingPanel);
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)((RRCScreenContext)this.scrContext).getPanelMediator().getParent();
        this.setSize(raritanDesktopPane.getSize());
        this.setFocusable(true);
        this.powerPort.setView(this);
    }

    public void loadApplet(String string) {
        if (string != null && !string.equals("")) {
            try {
                this.loaded = false;
                URL uRL = new URL(string);
                this.holder = new EmbedAppletDataHolder();
                this.stub = new EmbedAppletStub((RRCScreenContext)this.scrContext, this.holder, uRL);
                this.stub.setParameter("Platform", "JavaRRC");
                this.classLoader = new EmbedAppletClassLoader(new URL[]{uRL}, this.getClass().getClassLoader());
                String string2 = "amp.powerboard.swing.AmpApp";
                this.claz = this.classLoader.loadClass(string2);
                try {
                    this.setSerialStreamMethod = this.claz.getMethod("setSerialStream", ISerialStream.class);
                }
                catch (SecurityException securityException) {
                    RRCLogger.logException(securityException);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    RRCLogger.logException(noSuchMethodException);
                }
                this.applet = new AmpApp();
                this.setSerialStream((TRSerialStream)((Object)this.powerPort.getStream()));
                this.applet.setSize((int)this.getSize().getHeight() - 20, (int)this.getSize().getWidth() - 20);
                this.applet.setStub(this.stub);
                this.applet.init();
                this.applet.start();
                this.applet.validate();
                this.loaded = true;
                this.appletPanel = new JPanel();
                this.appletPanel.setLayout(new GridBagLayout());
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.fill = 1;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                this.appletPanel.add((Component)this.applet.getRootPane(), gridBagConstraints);
                this.add(APPLET_CARD, new JScrollPane(this.appletPanel));
                ((CardLayout)this.getLayout()).show(this, APPLET_CARD);
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(this.bundle.getString("powerBoardLoaded.message"));
            }
            catch (ClassCastException classCastException) {
            }
            catch (MalformedURLException malformedURLException) {
            }
            catch (ClassNotFoundException classNotFoundException) {
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
            this.applet.stop();
            this.applet.destroy();
            this.applet = null;
        }
        this.thread = null;
    }

    public JApplet getApplet() {
        return this.applet;
    }

    public synchronized void serialIn(int n, byte[] byArray) {
        while (!this.loaded) {
        }
        try {
            Object[] objectArray = new Object[]{byArray};
            if (this.setDataBytesMethod == null) {
                this.setDataBytesMethod = this.claz.getMethod("setDataBytes", byArray.getClass());
            }
            this.setDataBytesMethod.invoke((Object)this.applet, objectArray);
        }
        catch (InvocationTargetException invocationTargetException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        this.appletPanel.revalidate();
        this.revalidate();
    }

    public void setSerialStream(TRSerialStream tRSerialStream) {
        try {
            this.setSerialStreamMethod.invoke((Object)this.applet, tRSerialStream);
        }
        catch (InvocationTargetException invocationTargetException) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
    }

    @Override
    public boolean hasFocus() {
        return this.getShellInternalFrame() != null && this.getShellInternalFrame().isSelected();
    }

    private class HtmlPortThread
    extends Thread {
        private PowerPortView view;

        public HtmlPortThread(PowerPortView powerPortView2) {
            this.setName("PowerPortThread");
            this.view = powerPortView2;
        }

        @Override
        public void run() {
            if (this.view != null) {
                DeviceConnector deviceConnector = PowerPortView.this.dev.getDeviceConnector();
                PowerPortView.this.loadApplet(new String("http://" + Util.getURLCompatibleIP(deviceConnector.getInetAddress()) + "/AmpPb.jar").trim());
            }
        }
    }
}

