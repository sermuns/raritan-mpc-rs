/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.AppletContext
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.BrowserLaunch;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.OKButtonCommand;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.util.Util;
import java.applet.AppletContext;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import javaclientlib.utils.RRCLogger;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import nn.pp.common.ClipboardHandler;
import nn.pp.common.CommonFunctions;
import nn.pp.common.KXHttpConnection;
import nn.pp.common.ResourceLoader;
import nn.pp.common.ui.helpers.AboutSwingWorker;
import nn.pp.common.ui.helpers.ICopyrightHandler;
import nn.pp.core.JVMVersionInfo;

public class AboutPanel
extends AbstractDisplay
implements ICopyrightHandler {
    private static final long serialVersionUID = -5187897952459950462L;
    private static final int LICENSE_DIALOG_BORDER_SIZE = 10;
    private static final double LICENSE_DIALOG_PROPORTIONAL_SIZE = 0.8;
    private AboutSwingWorker worker = null;
    private KXHttpConnection conn = null;
    private String baseCopyright = null;
    private JPanel copyrightData = null;
    private JEditorPane licPane = null;
    private JEditorPane pkgPane = null;
    private JEditorPane copyright = null;
    private JScrollPane licJsp = null;
    private JScrollPane pkgJsp = null;
    private JScrollPane copyrightJsp = null;

    public AboutPanel(boolean bl, ScreenContext screenContext, boolean bl2) {
        super(screenContext);
        this.isDialog = bl;
        this.isNewPanel = bl2;
        this.setShell(this.bundle.getString("about.panel.title"));
        this.makeLayout();
    }

    @Override
    public void makeLayout() {
        Object object;
        Serializable serializable;
        JPanel jPanel;
        int n;
        JPanel jPanel2;
        block5: {
            jPanel2 = new JPanel(new GridBagLayout());
            n = 1;
            jPanel2.add((Component)new JLabel(ResourceLoader.loadImageIcon(this.bundle.getString("RaritanLogo.image"))), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.5, 10, 2, new Insets(30, 6, 27, 6), 0, 0));
            jPanel2.add((Component)new JLabel(this.bundle.getString("about.label.productname")), new GridBagConstraints(0, n++, 1, 1, 1.0, 0.1, 10, 0, new Insets(3, 6, 3, 6), 0, 0));
            jPanel2.add((Component)new JLabel(this.bundle.getString("about.label.version") + ": " + this.bundle.getString("about.label.build")), new GridBagConstraints(0, n++, 1, 1, 1.0, 0.1, 10, 0, new Insets(3, 6, 3, 6), 0, 0));
            jPanel = new JPanel(new GridLayout(0, 2));
            Map<String, String> map = JVMVersionInfo.getSystemProperties();
            jPanel.add(new JLabel(this.bundle.getString("about.label.jvmversion") + ": ", 11));
            jPanel.add(new JLabel(map.get("JVM Version"), 10));
            jPanel.add(new JLabel(this.bundle.getString("about.label.jvmvendor") + ": ", 11));
            jPanel.add(new JLabel(map.get("JVM Vendor"), 10));
            jPanel.add(new JLabel(this.bundle.getString("about.label.operating.system") + ": ", 11));
            jPanel.add(new JLabel(map.get("Operating System"), 10));
            jPanel.add(new JLabel(this.bundle.getString("about.label.operating.system.version") + ": ", 11));
            jPanel.add(new JLabel(map.get("Operating System Version"), 10));
            jPanel.add(new JLabel(this.bundle.getString("about.label.system.architecture") + ": ", 11));
            jPanel.add(new JLabel(map.get("System Architecture"), 10));
            jPanel2.add((Component)jPanel, new GridBagConstraints(0, n++, 1, 5, 1.0, 0.1, 10, 0, new Insets(6, 6, 6, 6), 0, 0));
            String string = this.getDeviceIP();
            try {
                if (string != null) {
                    this.licPane = new JEditorPane("text/html", ""){

                        @Override
                        public Dimension getPreferredScrollableViewportSize() {
                            FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
                            int n = fontMetrics.getHeight() * 6;
                            return new Dimension(480, n);
                        }
                    };
                    this.licPane.setEditable(false);
                    this.licPane.setOpaque(true);
                    this.licJsp = new JScrollPane(this.licPane, 22, 30);
                    this.pkgPane = new JEditorPane("text/html", ""){

                        @Override
                        public Dimension getPreferredScrollableViewportSize() {
                            FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
                            int n = fontMetrics.getHeight() * 6;
                            return new Dimension(480, n);
                        }
                    };
                    this.pkgPane.setEditable(false);
                    this.pkgPane.setOpaque(true);
                    this.pkgJsp = new JScrollPane(this.pkgPane, 22, 30);
                    this.copyright = new JEditorPane(){

                        @Override
                        public Dimension getPreferredScrollableViewportSize() {
                            FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
                            int n = fontMetrics.getHeight() * 6;
                            return new Dimension(480, n);
                        }
                    };
                    this.copyright.setText(BASE_COPYRIGHT);
                    this.copyright.setEditable(false);
                    this.copyright.setOpaque(true);
                    this.copyrightJsp = new JScrollPane(this.copyright, 22, 30);
                    serializable = UIManager.getFont("Label.font");
                    if (serializable != null) {
                        object = "body { font-family: " + ((Font)serializable).getFamily() + "; " + "font-size: " + ((Font)serializable).getSize() + "pt; }";
                        ((HTMLDocument)this.licPane.getDocument()).getStyleSheet().addRule((String)object);
                        ((HTMLDocument)this.pkgPane.getDocument()).getStyleSheet().addRule((String)object);
                        ((HTMLDocument)this.pkgPane.getDocument()).getStyleSheet().addRule((String)object);
                    }
                    jPanel2.add((Component)this.licJsp, new GridBagConstraints(0, 5 + n++, 1, 1, 1.0, 0.2, 10, 2, new Insets(3, 6, 6, 6), 0, 0));
                    jPanel2.add((Component)this.pkgJsp, new GridBagConstraints(0, 5 + n++, 1, 1, 1.0, 0.2, 10, 2, new Insets(3, 6, 6, 6), 0, 0));
                    jPanel2.add((Component)this.copyrightJsp, new GridBagConstraints(0, 5 + n++, 1, 1, 1.0, 0.2, 10, 2, new Insets(3, 6, 6, 6), 0, 0));
                    object = new HyperlinkListener(){

                        @Override
                        public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                            if (HyperlinkEvent.EventType.ACTIVATED.equals(hyperlinkEvent.getEventType())) {
                                AppletContext appletContext = AboutPanel.this.scrContext.getAppletContext();
                                if (appletContext != null) {
                                    appletContext.showDocument(hyperlinkEvent.getURL(), "_blank");
                                } else {
                                    BrowserLaunch.openURL(hyperlinkEvent.getURL().toString(), null);
                                }
                            }
                        }
                    };
                    this.licPane.addHyperlinkListener((HyperlinkListener)object);
                    this.pkgPane.addHyperlinkListener((HyperlinkListener)object);
                    this.worker = new AboutSwingWorker(this, CommonFunctions.getLicenseFilePath(), CommonFunctions.getLicenseActionDescr(), string);
                    this.worker.start();
                    this.setCursor(waitCursor);
                } else {
                    jPanel2.add((Component)new JLabel(this.bundle.getString("about.label.copyright")), new GridBagConstraints(0, 5 + n++, 1, 1, 1.0, 0.1, 10, 0, new Insets(3, 6, 3, 6), 0, 0));
                }
            }
            catch (Exception exception) {
                if (!RRCLogger.logEnabled) break block5;
                RRCLogger.logException(exception);
            }
        }
        serializable = new JPanel(new FlowLayout(1));
        ((Container)serializable).add(this.doButtonWidget());
        object = new JButton("Copy to Clipboard");
        ((Container)serializable).add((Component)object);
        ((AbstractButton)object).addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < jPanel.getComponentCount(); i += 2) {
                    stringBuffer.append(((JLabel)jPanel.getComponent(i)).getText());
                    stringBuffer.append(((JLabel)jPanel.getComponent(i + 1)).getText());
                    stringBuffer.append("\n");
                }
                ClipboardHandler.getInstance().copyToClipboard(stringBuffer.toString());
            }
        });
        jPanel2.add((Component)serializable, new GridBagConstraints(0, 5 + n++, 1, 1, 1.0, 0.2, 10, 2, new Insets(3, 6, 6, 6), 0, 0));
        this.add(jPanel2);
        this.getShell().pack();
    }

    private String getDeviceIP() {
        DeviceConnector deviceConnector;
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList == null || arrayList.size() == 0) {
            return null;
        }
        Device device2 = device = (Device)arrayList.get(0);
        if (device instanceof Port) {
            device2 = ((Port)device).getBaseDevice();
        } else if (device instanceof BladeChassis) {
            device2 = ((BladeChassis)device).getBaseDevice();
        } else if (device instanceof Paragon) {
            device2 = ((Paragon)device).getBaseDevice();
        }
        if ((device2.isConnected() || MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext) && device.isConnected()) && (deviceConnector = device2.getDeviceConnector()).hasGNULicenseInfo()) {
            InetAddress inetAddress = deviceConnector.getInetAddress();
            return Util.getURLCompatibleIP(inetAddress);
        }
        return null;
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.setCommand(new OKButtonCommand(this.scrContext));
        this.ok.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(1));
        jPanel.add(this.ok);
        return jPanel;
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillCopyrightInfo(String string, String string2, String string3) {
        if (string != null && string.length() > 0) {
            this.copyright.setText(BASE_COPYRIGHT + "\n" + string);
        }
        if (string2 != null && string2.length() > 0) {
            this.pkgPane.setText(string2);
        } else {
            this.pkgPane.setText(NO_PACKAGES);
        }
        if (string3 != null && string3.length() > 0) {
            this.licPane.setText(string3);
        }
        this.setCursor(Cursor.getDefaultCursor());
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                Point point = new Point(0, 0);
                AboutPanel.this.licJsp.getViewport().setViewPosition(point);
                AboutPanel.this.pkgJsp.getViewport().setViewPosition(point);
                AboutPanel.this.copyrightJsp.getViewport().setViewPosition(point);
            }
        });
    }
}

