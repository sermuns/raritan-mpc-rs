/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.common.ResourceLoader;
import nn.pp.common.scan.ScanConnector;
import nn.pp.common.scan.ScanEventListener;
import nn.pp.common.scan.ScanPanelHelper;
import nn.pp.common.scan.TargetState;
import nn.pp.common.scan.VideoSnapshotSelectionListener;
import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.rccore.scan.ScanCore;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.ScanCoreFactory;
import nn.pp.rccore.scan.ScanSession;
import nn.pp.rccore.scan.ScanSessionEventsListener;

public class TSP {
    private static final String NO_VIDEO_FROM_TARGET_SERVER = "No video from target server";
    private static TargetState state = TargetState.INACTIVE;
    private static ScanSession scanSession;

    public static void main(String[] stringArray) {
        Object object2;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
            // empty catch block
        }
        JFrame jFrame = new JFrame("TSP");
        jFrame.setDefaultCloseOperation(0);
        jFrame.setExtendedState(6);
        String[] stringArray2 = new String[]{"P_000d5d1d2d4d_0", "P_000d5d1d2d4d_11", "P_000d5d1d2d4d_4", "P_000d5d1d2d4d_15"};
        String[] stringArray3 = new String[]{"basit_cim1", "Fedora", "vijay_cim1", "WinXP"};
        ArrayList<RemoteConsoleParameters> arrayList = new ArrayList<RemoteConsoleParameters>();
        for (int i = 0; i < stringArray2.length; ++i) {
            object2 = new RemoteConsoleParameters();
            ((RemoteConsoleParameters)object2).targetPortId = stringArray2[i];
            ((RemoteConsoleParameters)object2).portName = stringArray3[i];
            ((RemoteConsoleParameters)object2).username = "test";
            ((RemoteConsoleParameters)object2).password = "raritan";
            ((RemoteConsoleParameters)object2).host = "192.168.50.207";
            ((RemoteConsoleParameters)object2).tcpPort = 443;
            ((RemoteConsoleParameters)object2).ssl = true;
            ((RemoteConsoleParameters)object2).portIndex = stringArray2[i].substring(stringArray2[i].lastIndexOf(95) + 1);
            arrayList.add((RemoteConsoleParameters)object2);
        }
        HashMap<String, RemoteConsoleParameters> hashMap = new HashMap<String, RemoteConsoleParameters>();
        for (RemoteConsoleParameters object3 : arrayList) {
            hashMap.put(object3.targetPortId, object3);
        }
        object2 = new MyLabel();
        MyLabel myLabel = new MyLabel();
        final ScanConnector scanConnector = TSP.createScanConnector(arrayList);
        scanConnector.addScanEventListener(new ScanEventListener((JLabel)object2, hashMap, myLabel){
            final /* synthetic */ JLabel val$statusLabel;
            final /* synthetic */ Map val$nameToRcp;
            final /* synthetic */ JLabel val$osdLabel;
            {
                this.val$statusLabel = jLabel;
                this.val$nameToRcp = map;
                this.val$osdLabel = jLabel2;
            }

            @Override
            public void videoComponentAvailable(String string, JComponent jComponent) {
                state = TargetState.INACTIVE;
                this.val$statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_white.gif"));
                this.val$statusLabel.setText(Integer.parseInt(((RemoteConsoleParameters)this.val$nameToRcp.get((Object)string)).portIndex) + 1 + " - " + ((RemoteConsoleParameters)this.val$nameToRcp.get((Object)string)).portName);
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
                    this.val$statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_green.gif"));
                }
            }

            @Override
            public void displayVideoError(String string, int n) {
                state = TargetState.BUSY;
                this.val$statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_red.gif"));
                this.val$osdLabel.setText("");
                this.val$osdLabel.setToolTipText(null);
            }

            @Override
            public void displayVideoEnd(String string, Image image) {
                state = TargetState.INACTIVE;
                this.val$statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_white.gif"));
                this.val$osdLabel.setText("");
                this.val$osdLabel.setToolTipText(null);
            }

            @Override
            public void displayVideoCommunicationError(String string) {
                state = TargetState.BUSY;
                this.val$statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_red.gif"));
                this.val$osdLabel.setText("");
                this.val$osdLabel.setToolTipText(null);
            }

            @Override
            public void osdMessageReceived(String string, String string2) {
                this.val$osdLabel.setText(string2);
                this.val$osdLabel.setToolTipText(this.val$osdLabel.getText());
                if (TSP.NO_VIDEO_FROM_TARGET_SERVER.equals(string2)) {
                    state = TargetState.DOWN;
                    this.val$statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_yellow.gif"));
                } else {
                    state = TargetState.CONNECTED;
                    this.val$statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_green.gif"));
                }
            }
        });
        scanConnector.setScanInterval(25000);
        scanConnector.setSwitchInterval(10000);
        final ScanPanelHelper scanPanelHelper = new ScanPanelHelper(arrayList, scanConnector, new Dimension(160, 120), 1);
        JMenuBar jMenuBar = new JMenuBar();
        JMenu jMenu = new JMenu("Options");
        JMenu jMenu2 = new JMenu("Size");
        JRadioButtonMenuItem jRadioButtonMenuItem = new JRadioButtonMenuItem("320 * 240");
        jMenu2.add(jRadioButtonMenuItem);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jRadioButtonMenuItem);
        JRadioButtonMenuItem jRadioButtonMenuItem2 = new JRadioButtonMenuItem("160 * 120");
        jMenu2.add(jRadioButtonMenuItem2);
        buttonGroup.add(jRadioButtonMenuItem2);
        ActionListener actionListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String string = ((JRadioButtonMenuItem)actionEvent.getSource()).getText();
                if ("320 * 240".equals(string)) {
                    scanPanelHelper.setThumbnailSize(new Dimension(320, 240));
                } else if ("160 * 120".equals(string)) {
                    scanPanelHelper.setThumbnailSize(new Dimension(160, 120));
                }
            }
        };
        jRadioButtonMenuItem.addActionListener(actionListener);
        jRadioButtonMenuItem2.addActionListener(actionListener);
        jRadioButtonMenuItem2.setSelected(true);
        JMenuItem jMenuItem = new JMenuItem("Pause");
        ActionListener actionListener2 = new ActionListener((JLabel)object2, scanConnector){
            final /* synthetic */ JLabel val$statusLabel;
            final /* synthetic */ ScanConnector val$scanConnector;
            {
                this.val$statusLabel = jLabel;
                this.val$scanConnector = scanConnector;
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                state = TargetState.INACTIVE;
                this.val$statusLabel.setIcon(ResourceLoader.loadImageIcon("Common_led_white.gif"));
                this.val$statusLabel.setText("Paused");
                this.val$scanConnector.pause();
            }
        };
        jMenuItem.addActionListener(actionListener2);
        JMenuItem jMenuItem2 = new JMenuItem("Resume");
        ActionListener actionListener3 = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                scanConnector.resume();
            }
        };
        jMenuItem2.addActionListener(actionListener3);
        JMenu jMenu3 = new JMenu("Split Orientation");
        JRadioButtonMenuItem jRadioButtonMenuItem3 = new JRadioButtonMenuItem("Horizontal");
        JRadioButtonMenuItem jRadioButtonMenuItem4 = new JRadioButtonMenuItem("Vertical");
        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(jRadioButtonMenuItem3);
        buttonGroup2.add(jRadioButtonMenuItem4);
        jRadioButtonMenuItem3.setSelected(true);
        ActionListener actionListener4 = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                scanPanelHelper.setSplitPaneOrientation(1);
            }
        };
        jRadioButtonMenuItem3.addActionListener(actionListener4);
        ActionListener actionListener5 = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                scanPanelHelper.setSplitPaneOrientation(0);
            }
        };
        jRadioButtonMenuItem4.addActionListener(actionListener5);
        jMenu3.add(jRadioButtonMenuItem3);
        jMenu3.add(jRadioButtonMenuItem4);
        jMenu.add(jMenu2);
        jMenu.add(jMenuItem);
        jMenu.add(jMenuItem2);
        jMenu.add(jMenu3);
        jMenuBar.add(jMenu);
        scanPanelHelper.addVideoSnapshotSelectionListener(new VideoSnapshotSelectionListener<RemoteConsoleParameters>(){

            @Override
            public void videoSnapshotSingleClickSelected(RemoteConsoleParameters remoteConsoleParameters) {
                System.out.println("Single click selected... " + remoteConsoleParameters.targetPortId);
            }

            @Override
            public void videoSnapshotDoubleClickSelected(RemoteConsoleParameters remoteConsoleParameters) {
                System.out.println("Double Click Selected.. " + remoteConsoleParameters.targetPortId);
            }
        });
        jFrame.setJMenuBar(jMenuBar);
        jFrame.add(scanPanelHelper.getScanPanel());
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        jPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        ((JComponent)object2).setBorder(BorderFactory.createLoweredBevelBorder());
        myLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new GridBagLayout());
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 1;
        jPanel2.add((Component)object2, gridBagConstraints);
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.gridx = 1;
        jPanel2.add((Component)myLabel, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 1;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        jFrame.add((Component)jPanel, "South");
        jFrame.setVisible(true);
        TSP.createScanSession(arrayList.get(0), stringArray2, new ScanSessionEventsListener(){

            @Override
            public void disconnected(Exception exception) {
                if (exception != null) {
                    scanPanelHelper.getScanConnector().stop();
                    JOptionPane.showMessageDialog(null, "Error received on Scan session connection");
                }
            }

            @Override
            public void scanSessionCreated(int n) {
                scanPanelHelper.getScanConnector().start(n);
            }
        }, new NotificationListener(){

            @Override
            public void receivedNotification(INotificationEvent iNotificationEvent) {
                System.out.println(iNotificationEvent.getErrorCode());
                switch (iNotificationEvent.getErrorCode()) {
                    case -1610612735: 
                    case -1610612734: 
                    case -1610481663: 
                    case -1610481662: 
                    case -1576927228: {
                        scanPanelHelper.getScanConnector().stop();
                        JOptionPane.showMessageDialog(null, "Error Recived from server " + Integer.toHexString(iNotificationEvent.getErrorCode()));
                        break;
                    }
                }
            }

            @Override
            public void textNotification(String string) {
            }
        });
        jFrame.addWindowListener(new WindowListener(){

            @Override
            public void windowOpened(WindowEvent windowEvent) {
            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {
            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {
            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {
            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                scanPanelHelper.getScanConnector().stop();
                if (scanSession != null) {
                    scanSession.close();
                }
                windowEvent.getWindow().dispose();
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {
            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {
            }
        });
    }

    private static ScanConnector createScanConnector(List<RemoteConsoleParameters> list) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (RemoteConsoleParameters remoteConsoleParameters : list) {
            arrayList.add(remoteConsoleParameters.targetPortId);
        }
        ScanConnector scanConnector = new ScanConnector(list);
        return scanConnector;
    }

    private static void createScanSession(final RemoteConsoleParameters remoteConsoleParameters, final String[] stringArray, final ScanSessionEventsListener scanSessionEventsListener, final NotificationListener notificationListener) {
        SwingWorker<ScanSession, Void> swingWorker = new SwingWorker<ScanSession, Void>(){

            @Override
            protected ScanSession doInBackground() throws Exception {
                ScanSession scanSession;
                ScanCore scanCore = ScanCoreFactory.getDefault();
                if (remoteConsoleParameters.username != null && remoteConsoleParameters.password != null) {
                    scanSession = scanCore.createScanSessionWithUsernameAndPassword(remoteConsoleParameters.getURLCompatibleRemoteHost(), remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.username, remoteConsoleParameters.password);
                } else if (remoteConsoleParameters.ericKey != null) {
                    scanSession = scanCore.createScanSessionWithEricKey(remoteConsoleParameters.getURLCompatibleRemoteHost(), remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.ericKey);
                } else if (remoteConsoleParameters.rdmSession != null) {
                    scanSession = scanCore.createScanSessionWithRdmSessionID(remoteConsoleParameters.getURLCompatibleRemoteHost(), remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.rdmSession, remoteConsoleParameters.proxyConnectionId, remoteConsoleParameters.proxyUseSSL);
                } else {
                    throw new Exception("Missing authentication parameters");
                }
                try {
                    scanSession.start(stringArray, scanSessionEventsListener, notificationListener);
                }
                catch (ScanCoreException scanCoreException) {
                    throw scanCoreException;
                }
                catch (IOException iOException) {
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
                    TSP.scanSession = (ScanSession)this.get();
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return;
                }
                catch (ExecutionException executionException) {
                    executionException.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error connecting to the device");
                }
            }
        };
        swingWorker.execute();
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

