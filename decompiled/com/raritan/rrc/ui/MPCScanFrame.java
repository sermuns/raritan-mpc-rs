/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.MultiMonitorPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ShowKVMFromScanCommand;
import com.raritan.rrc.ui.commands.ShowKX2KvmPortCommand;
import com.raritan.rrc.ui.panes.ErrorHandlerImpl;
import com.raritan.rrc.ui.panes.RRCShellInternalFrame;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;
import javax.swing.event.InternalFrameEvent;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.common.ResourceLoader;
import nn.pp.common.Util;
import nn.pp.common.scan.ScanConnector;
import nn.pp.common.scan.ScanEventListener;
import nn.pp.common.scan.ScanPanelHelper;
import nn.pp.common.scan.TargetState;
import nn.pp.common.scan.VideoSnapshotSelectionListener;
import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.ext.pref.IApplicationPreferences;
import nn.pp.logging.RemoteConsoleLogger;
import nn.pp.rccore.scan.ScanCore;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.ScanCoreFactory;
import nn.pp.rccore.scan.ScanSession;
import nn.pp.rccore.scan.ScanSessionEventsListener;

public class MPCScanFrame
extends RRCShellInternalFrame {
    private RaritanPropertyResourceBundle bundle;
    private String NO_VIDEO_FROM_TARGET_SERVER;
    private static TargetState state = TargetState.INACTIVE;
    private static ScanSession scanSession;
    private ScanPanelHelper scph;
    ScanConnector scanConnector;
    private String[] portIds;
    private String[] portNos;
    HashMap<String, String> portTypeMap;
    HashMap<String, String> portPermissionMap;
    private IApplicationPreferences appPrefs;
    private String savedDimension;
    private ArrayList<KvmPort> kvmportList;
    JLabel statusLabel;
    JLabel osdLabel;
    JMenuBar bar;
    JMenu menu;
    JMenu sizeMenu;
    JMenuItem s320_240;
    ButtonGroup bg;
    JMenuItem s160_120;
    JMenuItem pause;
    JMenuItem resume;
    JMenu orientation;
    JMenuItem horizontalSplit;
    JMenuItem verticalSplit;
    ButtonGroup bg1;
    JPanel statusBar;
    JPanel p;
    private RRCScreenContext scrContext;
    private String connectedDeviceId = "";

    /*
     * WARNING - void declaration
     */
    public MPCScanFrame(RRCScreenContext rRCScreenContext) {
        void var7_11;
        this.scrContext = rRCScreenContext;
        this.appPrefs = rRCScreenContext.getAppSettings();
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.scrContext.getPanelMediator().getParent();
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.NO_VIDEO_FROM_TARGET_SERVER = this.bundle.getString("NO.VIDEO");
        this.setTitle(this.bundle.getString("Scan.Title"));
        this.setResizable(this.resizable);
        this.setClosable(true);
        this.pack();
        this.setVisible(false);
        this.setFocusable(true);
        this.setResizable(true);
        this.setSize(raritanDesktopPane.getSize());
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.kvmportList = new ArrayList();
        this.kvmportList = this.scrContext.getKvmPortsForScan();
        ArrayList<RemoteConsoleParameters> arrayList = this.getParams();
        final HashMap<String, RemoteConsoleParameters> hashMap = new HashMap<String, RemoteConsoleParameters>();
        this.portIds = new String[arrayList.size()];
        this.portNos = new String[arrayList.size()];
        int n = 0;
        for (RemoteConsoleParameters object2 : arrayList) {
            hashMap.put(object2.targetPortId, object2);
            this.portIds[n] = object2.targetPortId;
            int n2 = -1;
            try {
                if (object2.portIndex.contains(".")) {
                    int[] nArray = Util.getPortNumber(object2.portIndex);
                    String string = Integer.toString(nArray[0] + 1);
                    String string2 = Integer.toString(nArray[1]);
                    this.portNos[n] = string + "-" + string2;
                } else {
                    n2 = Integer.parseInt(object2.portIndex) + 1;
                    this.portNos[n] = Integer.toString(n2);
                }
            }
            catch (NumberFormatException numberFormatException) {
                RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Exception Occured:", numberFormatException);
            }
            catch (Exception exception) {
                RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Exception Occured:", exception);
            }
            ++n;
        }
        this.createLabels();
        this.scanConnector = MPCScanFrame.createScanConnector(arrayList);
        this.scanConnector.addScanEventListener(new ScanEventListener(){

            @Override
            public void videoComponentAvailable(String string, JComponent jComponent) {
                state = TargetState.INACTIVE;
                MPCScanFrame.this.statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_white.gif"));
                String string2 = ((RemoteConsoleParameters)hashMap.get((Object)string)).portIndex;
                boolean bl = false;
                int n = -1;
                int n2 = -1;
                if (string2.contains(".")) {
                    bl = true;
                    int[] nArray = Util.getPortNumber(string2);
                    n = nArray[0];
                    n2 = nArray[1];
                } else {
                    n = Integer.parseInt(string2);
                }
                if (n2 != -1) {
                    MPCScanFrame.this.statusLabel.setText(n + 1 + " - " + n2 + " - " + ((RemoteConsoleParameters)hashMap.get((Object)string)).portName);
                } else {
                    MPCScanFrame.this.statusLabel.setText(n + 1 + " - " + ((RemoteConsoleParameters)hashMap.get((Object)string)).portName);
                }
            }

            @Override
            public void scanAbortedOnConnectError() {
                scanSession.close();
            }

            @Override
            public void scanAborted(INotificationEvent iNotificationEvent) {
                scanSession.close();
                System.out.println(iNotificationEvent.isError());
                System.out.println(iNotificationEvent);
            }

            @Override
            public void displayVideoStart(String string, boolean bl) {
                if (state == TargetState.INACTIVE) {
                    state = TargetState.CONNECTED;
                    MPCScanFrame.this.statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_green.gif"));
                }
            }

            @Override
            public void displayVideoError(String string, int n) {
                state = TargetState.BUSY;
                MPCScanFrame.this.statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_red.gif"));
                MPCScanFrame.this.osdLabel.setText("");
                MPCScanFrame.this.osdLabel.setToolTipText(null);
            }

            @Override
            public void displayVideoEnd(String string, Image image) {
                state = TargetState.INACTIVE;
                MPCScanFrame.this.statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_white.gif"));
                MPCScanFrame.this.osdLabel.setToolTipText(null);
            }

            @Override
            public void displayVideoCommunicationError(String string) {
                state = TargetState.BUSY;
                MPCScanFrame.this.statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_red.gif"));
                MPCScanFrame.this.osdLabel.setText("");
                MPCScanFrame.this.osdLabel.setToolTipText(null);
            }

            @Override
            public void osdMessageReceived(String string, String string2) {
                MPCScanFrame.this.osdLabel.setText(string2);
                MPCScanFrame.this.osdLabel.setToolTipText(MPCScanFrame.this.osdLabel.getText());
                if (MPCScanFrame.this.NO_VIDEO_FROM_TARGET_SERVER.equals(string2)) {
                    state = TargetState.DOWN;
                    MPCScanFrame.this.statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_yellow.gif"));
                } else {
                    state = TargetState.CONNECTED;
                    MPCScanFrame.this.statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_green.gif"));
                }
            }
        });
        this.scanConnector.setScanInterval(this.appPrefs.getScanDisplayInterval() * 1000);
        this.scanConnector.setSwitchInterval(this.appPrefs.getPortScanInterval() * 1000);
        this.savedDimension = this.appPrefs.getScanThumbnailSize();
        String[] stringArray = this.savedDimension.split("x");
        Dimension dimension = new Dimension(160, 120);
        try {
            Dimension dimension2 = new Dimension(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]));
        }
        catch (NumberFormatException numberFormatException) {
            Dimension dimension3 = new Dimension(160, 120);
        }
        String string = "vertical";
        string = this.appPrefs.getScanOrientation();
        boolean bl = false;
        bl = string.equalsIgnoreCase("vertical");
        this.scph = new ScanPanelHelper(arrayList, this.scanConnector, (Dimension)var7_11, bl ? 0 : 1);
        this.createGUI();
        this.add((Component)this.statusBar, "South");
        this.createScanSession(arrayList.get(0), this.portIds, new ScanSessionEventsListener(){

            @Override
            public void disconnected(Exception exception) {
                if (exception != null) {
                    MPCScanFrame.this.scph.getScanConnector().stop();
                    JOptionPane.showMessageDialog(null, "Error received on Scan session connection");
                }
            }

            @Override
            public void scanSessionCreated(int n) {
                MPCScanFrame.this.scph.getScanConnector().start(n);
            }
        }, new NotificationListener(){

            @Override
            public void receivedNotification(INotificationEvent iNotificationEvent) {
                RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Notification received, aborting scan : " + iNotificationEvent.getErrorCode());
                switch (iNotificationEvent.getErrorCode()) {
                    case -1610612735: 
                    case -1610612734: 
                    case -1610481662: 
                    case -1576927228: {
                        MPCScanFrame.this.scph.getScanConnector().stop();
                        ErrorHandlerImpl.getInstance().handleScanError(iNotificationEvent.getErrorCode(), MPCScanFrame.this.scrContext.getApplication().getContentPane(), MPCScanFrame.this.portNos);
                        break;
                    }
                    case -1610481663: {
                        if (MPCScanFrame.this.scph != null) {
                            MPCScanFrame.this.scph.getScanConnector().stop();
                        }
                        ErrorHandlerImpl.getInstance().handleScanError(iNotificationEvent.getErrorCode(), MPCScanFrame.this.scrContext.getApplication().getContentPane(), MPCScanFrame.this.portNos);
                        break;
                    }
                }
            }

            @Override
            public void textNotification(String string) {
            }
        });
        raritanDesktopPane.add(this);
        this.scrContext.setScanFrame(this);
        this.scrContext.setScanFrameOpened(true);
        MPCUtil.notifyObservers(this.scrContext, null);
        this.setVisible(true);
        this.scrContext.focusScanFrame();
    }

    public ScanPanelHelper getScph() {
        return this.scph;
    }

    public void setScph(ScanPanelHelper scanPanelHelper) {
        this.scph = scanPanelHelper;
    }

    public ScanConnector getScanConnector() {
        return this.scanConnector;
    }

    public static ScanSession getScanSession() {
        return scanSession;
    }

    public static void setScanSession(ScanSession scanSession) {
        MPCScanFrame.scanSession = scanSession;
    }

    public void setScanConnector(ScanConnector scanConnector) {
        this.scanConnector = scanConnector;
    }

    private void createLabels() {
        this.statusLabel = new MyLabel();
        this.osdLabel = new MyLabel();
    }

    private void createGUI() {
        this.bar = new JMenuBar();
        this.menu = new JMenu(this.bundle.getString("scan.options"));
        this.sizeMenu = new JMenu(this.bundle.getString("thumbnail.size"));
        this.s320_240 = new JRadioButtonMenuItem("320 x 240");
        this.sizeMenu.add(this.s320_240);
        this.bg = new ButtonGroup();
        this.bg.add(this.s320_240);
        this.s160_120 = new JRadioButtonMenuItem("160 x 120");
        this.sizeMenu.add(this.s160_120);
        this.bg.add(this.s160_120);
        ActionListener actionListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String string = ((JRadioButtonMenuItem)actionEvent.getSource()).getText();
                if ("320 x 240".equals(string)) {
                    MPCScanFrame.this.scph.setThumbnailSize(new Dimension(320, 240));
                } else if ("160 x 120".equals(string)) {
                    MPCScanFrame.this.scph.setThumbnailSize(new Dimension(160, 120));
                }
            }
        };
        this.s320_240.addActionListener(actionListener);
        this.s160_120.addActionListener(actionListener);
        if (this.savedDimension.equals("160x120")) {
            this.s160_120.setSelected(true);
        } else {
            this.s320_240.setSelected(true);
        }
        this.pause = new JMenuItem(this.bundle.getString("Pause.option"));
        ActionListener actionListener2 = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                state = TargetState.INACTIVE;
                MPCScanFrame.this.statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_white.gif"));
                MPCScanFrame.this.osdLabel.setText(MPCScanFrame.this.bundle.getString("paused"));
                MPCScanFrame.this.scanConnector.pause();
            }
        };
        this.pause.addActionListener(actionListener2);
        this.resume = new JMenuItem(this.bundle.getString("resume"));
        ActionListener actionListener3 = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MPCScanFrame.this.scanConnector.resume();
            }
        };
        this.resume.addActionListener(actionListener3);
        this.orientation = new JMenu(this.bundle.getString("OptionDialog.SplitOrientation"));
        this.horizontalSplit = new JRadioButtonMenuItem(this.bundle.getString("horizontal"));
        this.verticalSplit = new JRadioButtonMenuItem(this.bundle.getString("vertical"));
        this.bg1 = new ButtonGroup();
        this.bg1.add(this.horizontalSplit);
        this.bg1.add(this.verticalSplit);
        if (this.appPrefs.getScanOrientation().equalsIgnoreCase("vertical")) {
            this.verticalSplit.setSelected(true);
        } else {
            this.horizontalSplit.setSelected(true);
        }
        ActionListener actionListener4 = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MPCScanFrame.this.scph.setSplitPaneOrientation(1);
            }
        };
        this.horizontalSplit.addActionListener(actionListener4);
        ActionListener actionListener5 = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MPCScanFrame.this.scph.setSplitPaneOrientation(0);
            }
        };
        this.verticalSplit.addActionListener(actionListener5);
        this.orientation.add(this.horizontalSplit);
        this.orientation.add(this.verticalSplit);
        this.menu.add(this.pause);
        this.menu.add(this.resume);
        this.menu.add(new JSeparator());
        this.menu.add(this.sizeMenu);
        this.menu.add(this.orientation);
        this.bar.add(this.menu);
        this.scph.addVideoSnapshotSelectionListener(new VideoSnapshotSelectionListener<RemoteConsoleParameters>(){

            @Override
            public void videoSnapshotSingleClickSelected(RemoteConsoleParameters remoteConsoleParameters) {
            }

            @Override
            public void videoSnapshotDoubleClickSelected(RemoteConsoleParameters remoteConsoleParameters) {
                System.out.println("Double Click Selected.. " + remoteConsoleParameters.targetPortId);
                MPCScanFrame.this.getScanConnector().pause();
                RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Pausing Scan before New Connection.");
                int n = -1;
                for (int i = 0; i < MPCScanFrame.this.portIds.length; ++i) {
                    if (!MPCScanFrame.this.portIds[i].equals(remoteConsoleParameters.targetPortId)) continue;
                    n = i;
                }
                RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "User establishing permanent connection to port:" + remoteConsoleParameters.targetPortId);
                KvmPort kvmPort = (KvmPort)MPCScanFrame.this.kvmportList.get(n);
                System.out.println(kvmPort);
                KvmPort kvmPort2 = kvmPort;
                MPCUtil.notifyObservers(MPCScanFrame.this.scrContext, kvmPort2);
                ShowKVMFromScanCommand showKVMFromScanCommand = new ShowKVMFromScanCommand(MPCScanFrame.this.scrContext, kvmPort);
                if (showKVMFromScanCommand.isExecutable()) {
                    CommandResult commandResult = showKVMFromScanCommand.execute();
                    if (!commandResult.isSuccess() && commandResult.hasErrorDescription()) {
                        MPCScanFrame.this.handleCommandResultErrorDescription(commandResult);
                    } else if (commandResult.isSuccess()) {
                        ArrayList arrayList;
                        MPCScanFrame.this.handleCommandResult(commandResult);
                        MPCScanFrame.this.scrContext.getPanelMediator().showPanel(showKVMFromScanCommand.getContext());
                        if (kvmPort.isPrimaryPort() && (arrayList = (ArrayList)MPCScanFrame.this.scrContext.getSelectedDevicesObservable().getComponent()) != null && arrayList.size() > 0) {
                            List<MultiMonitorPort.PortConfig> list = kvmPort.getSecondaryPortConfigs();
                            block1: for (MultiMonitorPort.PortConfig portConfig : list) {
                                Map map = kvmPort.getDevice().getChildren();
                                if (map == null) continue;
                                for (Object v : map.values()) {
                                    Port port;
                                    if (!(v instanceof Port) || !(port = (Port)v).getTargetDeviceId().equals(portConfig.getPortId())) continue;
                                    arrayList.set(0, port);
                                    ShowKX2KvmPortCommand showKX2KvmPortCommand = new ShowKX2KvmPortCommand(MPCScanFrame.this.scrContext);
                                    showKX2KvmPortCommand.setAllowSecondary(true);
                                    if (!showKX2KvmPortCommand.isExecutable()) continue block1;
                                    showKX2KvmPortCommand.getContext().setCommandParameter("selectedPortDevice", kvmPort.getDevice());
                                    commandResult = showKX2KvmPortCommand.execute();
                                    if (!commandResult.isSuccess() && commandResult.hasErrorDescription()) {
                                        MPCScanFrame.this.handleCommandResultErrorDescription(commandResult);
                                        continue block1;
                                    }
                                    if (!commandResult.isSuccess()) continue block1;
                                    MPCScanFrame.this.handleCommandResult(commandResult);
                                    MPCScanFrame.this.scrContext.getPanelMediator().showPanel(showKX2KvmPortCommand.getContext());
                                    continue block1;
                                }
                            }
                        }
                    }
                }
            }
        });
        this.setJMenuBar(this.bar);
        this.add(this.scph.getScanPanel());
        this.statusBar = new JPanel();
        this.statusBar.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.statusBar.setBorder(BorderFactory.createRaisedBevelBorder());
        this.statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        this.osdLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        this.p = new JPanel();
        this.p.setLayout(new GridBagLayout());
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 1;
        this.p.add((Component)this.statusLabel, gridBagConstraints);
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.gridx = 1;
        this.p.add((Component)this.osdLabel, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 1;
        this.statusBar.add((Component)this.p, gridBagConstraints);
    }

    private static ScanConnector createScanConnector(List<RemoteConsoleParameters> list) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (RemoteConsoleParameters remoteConsoleParameters : list) {
            arrayList.add(remoteConsoleParameters.targetPortId);
        }
        ScanConnector scanConnector = new ScanConnector(list);
        return scanConnector;
    }

    private void createScanSession(final RemoteConsoleParameters remoteConsoleParameters, final String[] stringArray, final ScanSessionEventsListener scanSessionEventsListener, final NotificationListener notificationListener) {
        SwingWorker<ScanSession, Void> swingWorker = new SwingWorker<ScanSession, Void>(){

            @Override
            protected ScanSession doInBackground() throws Exception {
                ScanSession scanSession;
                ScanCore scanCore;
                try {
                    scanCore = ScanCoreFactory.getDefault();
                }
                catch (ScanCoreException scanCoreException) {
                    RemoteConsoleLogger.getInstance().getLogger().log(Level.SEVERE, "Exception on Scan connection", scanCoreException);
                    throw scanCoreException;
                }
                if (remoteConsoleParameters.username != null && remoteConsoleParameters.password != null) {
                    scanSession = scanCore.createScanSessionWithUsernameAndPassword(remoteConsoleParameters.getURLCompatibleRemoteHost(), remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.username, remoteConsoleParameters.password);
                } else if (remoteConsoleParameters.ericKey != null) {
                    scanSession = scanCore.createScanSessionWithEricKey(remoteConsoleParameters.getURLCompatibleRemoteHost(), remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.ericKey);
                } else if (remoteConsoleParameters.rdmSession != null) {
                    scanSession = scanCore.createScanSessionWithRdmSessionID(remoteConsoleParameters.getURLCompatibleRemoteHost(), remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.rdmSession, remoteConsoleParameters.proxyConnectionId, remoteConsoleParameters.proxyUseSSL);
                } else {
                    RemoteConsoleLogger.getInstance().getLogger().log(Level.SEVERE, "Missing authentication parameters");
                    throw new Exception("Missing authentication parameters");
                }
                try {
                    scanSession.start(stringArray, scanSessionEventsListener, notificationListener);
                }
                catch (ScanCoreException scanCoreException) {
                    RemoteConsoleLogger.getInstance().getLogger().log(Level.SEVERE, "Exception on Scan connection", scanCoreException);
                    throw scanCoreException;
                }
                catch (IOException iOException) {
                    RemoteConsoleLogger.getInstance().getLogger().log(Level.SEVERE, "Exception on Scan connection", iOException);
                    throw iOException;
                }
                return scanSession;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            protected void done() {
                try {
                    MPCScanFrame.scanSession = (ScanSession)this.get();
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return;
                }
                catch (ExecutionException executionException) {
                    RemoteConsoleLogger.getInstance().getLogger().log(Level.SEVERE, "Exception on Scan connection", executionException);
                    JOptionPane.showMessageDialog(null, "Error connecting to the device.");
                    MPCScanFrame.this.close();
                }
            }
        };
        swingWorker.execute();
    }

    public void close() {
        try {
            this.scph.getScanConnector().stop();
            if (MPCScanFrame.getScanSession() != null) {
                MPCScanFrame.getScanSession().close();
            }
            this.scrContext.setScanFrame(null);
            this.scrContext.setScanFrameOpened(false);
        }
        catch (Exception exception) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.SEVERE, "Exception Occured while closing scan frame:", exception);
        }
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
        this.close();
        super.internalFrameClosing(internalFrameEvent);
    }

    private static String getPortNumbersAsString(String[] stringArray) {
        int n;
        String string = "";
        for (n = 0; n < stringArray.length; ++n) {
            string = string + "," + stringArray[n];
        }
        n = string.length();
        string = string.substring(1, n - 1);
        return string;
    }

    protected void handleCommandResultErrorDescription(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        if (commandResult.getErrorDescription() != null && commandResult.getErrorDescription().length > 0) {
            for (int i = 0; i < commandResult.getErrorDescription().length; ++i) {
                stringBuffer.append(commandResult.getErrorDescription()[i]);
                stringBuffer.append("\n");
            }
        } else if (commandResult.getStatusMessage() != null) {
            stringBuffer.append(commandResult.getStatusMessage());
        } else {
            stringBuffer.append("No command result info.");
        }
        CommonPopups.showCommandResultErrorMessage(stringBuffer.toString(), null, (ScreenContext)this.scrContext);
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    protected void handleCommandResult(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    private ArrayList<RemoteConsoleParameters> getParams() {
        ArrayList<RemoteConsoleParameters> arrayList = new ArrayList<RemoteConsoleParameters>();
        for (int i = 0; i < this.kvmportList.size(); ++i) {
            RemoteConsoleParameters remoteConsoleParameters = new RemoteConsoleParameters();
            KvmPort kvmPort = this.kvmportList.get(i);
            remoteConsoleParameters.portIndex = kvmPort.isBladePort() ? Integer.toString(kvmPort.getParentIndex()) + "." + Integer.toString(kvmPort.getPortIndex()) : Integer.toString(kvmPort.getPortIndex());
            remoteConsoleParameters.targetPortId = kvmPort.getStripTargetDeviceId();
            remoteConsoleParameters.portName = kvmPort.getName();
            remoteConsoleParameters.host = com.raritan.tools.util.Util.getURLCompatibleIP(kvmPort.getDevice().getIP());
            remoteConsoleParameters.tcpPort = kvmPort.getDevice().getHttpsPort();
            remoteConsoleParameters.rdmSession = "\"" + kvmPort.getDevice().getRdmSessionId() + "\":\"" + kvmPort.getDevice().getRdmSessionKey() + "\"";
            remoteConsoleParameters.ssl = true;
            arrayList.add(remoteConsoleParameters);
            this.setConnectedDeviceId(kvmPort.getDevice().getId());
        }
        return arrayList;
    }

    public String getConnectedDeviceId() {
        return this.connectedDeviceId;
    }

    public void setConnectedDeviceId(String string) {
        this.connectedDeviceId = string;
    }

    public static void main(String[] stringArray) {
        ArrayList<RemoteConsoleParameters> arrayList = new ArrayList<RemoteConsoleParameters>();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        HashMap<String, String> hashMap2 = new HashMap<String, String>();
        RemoteConsoleParameters remoteConsoleParameters = new RemoteConsoleParameters();
        remoteConsoleParameters.portIndex = "0";
        remoteConsoleParameters.targetPortId = "P_000d5d0007cb_0";
        remoteConsoleParameters.portName = "KXG1-LocalPortzzzzzzzzzzzzzzzzzzz";
        remoteConsoleParameters.username = "admin";
        remoteConsoleParameters.password = "raritan";
        remoteConsoleParameters.host = "192.168.52.230";
        remoteConsoleParameters.tcpPort = 443;
        remoteConsoleParameters.ssl = true;
        arrayList.add(remoteConsoleParameters);
        hashMap.put("P_000d5d0007cb_0", "VM");
        hashMap2.put("P_000d5d0007cb_0", "CCC");
        RemoteConsoleParameters remoteConsoleParameters2 = new RemoteConsoleParameters();
        remoteConsoleParameters2.portIndex = "1";
        remoteConsoleParameters2.targetPortId = "P_000d5d0007cb_1";
        remoteConsoleParameters2.portName = "Port2";
        remoteConsoleParameters2.username = "admin";
        remoteConsoleParameters2.password = "raritan";
        remoteConsoleParameters2.host = "192.168.52.230";
        remoteConsoleParameters2.tcpPort = 443;
        remoteConsoleParameters2.ssl = true;
        arrayList.add(remoteConsoleParameters2);
        hashMap.put("P_000d5d0007cb_1", "VM");
        hashMap2.put("P_000d5d0007cb_1", "CCC");
        RemoteConsoleParameters remoteConsoleParameters3 = new RemoteConsoleParameters();
        remoteConsoleParameters3.portIndex = "2";
        remoteConsoleParameters3.targetPortId = "P_000d5d0007cb_2";
        remoteConsoleParameters3.portName = "Port3";
        remoteConsoleParameters3.username = "admin";
        remoteConsoleParameters3.password = "raritan";
        remoteConsoleParameters3.host = "192.168.52.230";
        remoteConsoleParameters3.tcpPort = 443;
        remoteConsoleParameters3.ssl = true;
        arrayList.add(remoteConsoleParameters3);
        hashMap.put("P_000d5d0007cb_2", "VM");
        hashMap2.put("P_000d5d0007cb_2", "CCC");
        RemoteConsoleParameters remoteConsoleParameters4 = new RemoteConsoleParameters();
        remoteConsoleParameters4.portIndex = "3";
        remoteConsoleParameters4.targetPortId = "P_000d5d0007cb_3";
        remoteConsoleParameters4.portName = "Port4";
        remoteConsoleParameters4.username = "admin";
        remoteConsoleParameters4.password = "raritan";
        remoteConsoleParameters4.host = "192.168.52.230";
        remoteConsoleParameters4.tcpPort = 443;
        remoteConsoleParameters4.ssl = true;
        arrayList.add(remoteConsoleParameters4);
        hashMap.put("P_000d5d0007cb_3", "VM");
        hashMap2.put("P_000d5d0007cb_3", "CCC");
        RemoteConsoleParameters remoteConsoleParameters5 = new RemoteConsoleParameters();
        remoteConsoleParameters5.portIndex = "23";
        remoteConsoleParameters5.targetPortId = "P_000d5d0007cb_23";
        remoteConsoleParameters5.portName = "CCSG-Local-Port";
        remoteConsoleParameters5.username = "admin";
        remoteConsoleParameters5.password = "raritan";
        remoteConsoleParameters5.host = "192.168.52.230";
        remoteConsoleParameters5.tcpPort = 443;
        remoteConsoleParameters5.ssl = true;
        arrayList.add(remoteConsoleParameters5);
        hashMap.put("P_000d5d0007cb_23", "VM");
        hashMap2.put("P_000d5d0007cb_23", "CCC");
        RemoteConsoleParameters remoteConsoleParameters6 = new RemoteConsoleParameters();
        remoteConsoleParameters6.portIndex = "30.0";
        remoteConsoleParameters6.targetPortId = "P_000d5d0007cb_29_0";
        remoteConsoleParameters6.portName = "CCSG-Local-Port";
        remoteConsoleParameters6.username = "admin";
        remoteConsoleParameters6.password = "raritan";
        remoteConsoleParameters6.host = "192.168.52.230";
        remoteConsoleParameters6.tcpPort = 443;
        remoteConsoleParameters6.ssl = true;
        arrayList.add(remoteConsoleParameters6);
        hashMap.put("P_000d5d0007cb_29_0", "VM");
        hashMap2.put("P_000d5d0007cb_29_0", "CCC");
        MPCScanFrame mPCScanFrame = new MPCScanFrame(null);
    }

    private static class MyLabel
    extends JLabel {
        private MyLabel() {
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(10, super.getPreferredSize().height);
        }

        @Override
        public Dimension getMinimumSize() {
            return this.getPreferredSize();
        }
    }
}

