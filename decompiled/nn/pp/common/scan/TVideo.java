/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.common.scan.GridCalculationUtil;
import nn.pp.common.scan.ScanConnector;
import nn.pp.common.scan.ScanEventListener;
import nn.pp.common.scan.TargetState;
import nn.pp.common.scan.VideoSnapshotPanel;
import nn.pp.common.scan.VideoSnapshotSelectionListener;
import nn.pp.common.scan.VideoSnapshotViewer;
import nn.pp.common.ui.helpers.FullScreenMenuHandler;
import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.rccore.RCAdapter;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.RCCoreFactory;
import nn.pp.rccore.VideoEventListener;

public class TVideo {
    private static final int COL_CAL_TIME = 3000;
    private static final int SWITCH_TIME = 3000;
    private static final int SCAN_TIME = 5000;
    static boolean stoppedColorCalTimer;
    static Timer scanTimer;
    static Timer switchTimer;
    static Timer colorCalTimer;
    static VideoSnapshotViewer[] imgDisp;
    static List<VideoSnapshotPanel<Object>> imgPanels;
    static String[] portIds;
    static int index;
    static RCCore rccore;
    static Dimension selectedSize;
    static NotificationListener videoStateNotificationListener;
    static VideoEventListener colorCalListener;
    static JFrame frame;
    static JFrame fullScreenFrame;
    static JPanel videoPanel;
    static FullScreenMenuHandler fullScreenMenuHandler;
    static String selectedPortId;
    static JMenuItem resumeAfter;
    private static ScanConnector scanConnector;

    public static void main(String[] stringArray) throws Exception {
        Object object;
        Object object2;
        frame = new JFrame("TVideo");
        final JMenuBar jMenuBar = new JMenuBar();
        JMenu jMenu = new JMenu("Options");
        JMenu jMenu2 = new JMenu("Size");
        JRadioButtonMenuItem jRadioButtonMenuItem = new JRadioButtonMenuItem("320 * 240");
        jMenu2.add(jRadioButtonMenuItem);
        SizeSelector sizeSelector = new SizeSelector();
        jRadioButtonMenuItem.addActionListener(sizeSelector);
        JRadioButtonMenuItem jRadioButtonMenuItem2 = new JRadioButtonMenuItem("160 * 120");
        jMenu2.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.addActionListener(sizeSelector);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jRadioButtonMenuItem);
        buttonGroup.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem.setSelected(true);
        selectedSize = new Dimension(320, 240);
        JMenuItem jMenuItem = new JMenuItem("Pause");
        ActionListener actionListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                scanConnector.pause();
            }
        };
        jMenuItem.addActionListener(actionListener);
        JMenuItem jMenuItem2 = new JMenuItem("Resume");
        ActionListener actionListener2 = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                scanConnector.resume();
            }
        };
        jMenuItem2.addActionListener(actionListener2);
        resumeAfter = new JMenuItem("Resume After");
        ActionListener actionListener3 = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                scanConnector.resumeAfter(selectedPortId);
                resumeAfter.setEnabled(false);
            }
        };
        resumeAfter.addActionListener(actionListener3);
        resumeAfter.setEnabled(false);
        jMenu.add(jMenu2);
        jMenu.add(jMenuItem);
        jMenu.add(jMenuItem2);
        jMenu.add(resumeAfter);
        jMenuBar.add(jMenu);
        JMenu jMenu3 = new JMenu("View");
        final JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem("Full Screen");
        jCheckBoxMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(actionEvent);
                if (jCheckBoxMenuItem.isSelected()) {
                    JFrame jFrame = new JFrame();
                    JPanel jPanel = new JPanel();
                    jPanel.setLayout(new GridBagLayout());
                    GridBagConstraints gridBagConstraints = new GridBagConstraints();
                    gridBagConstraints.weightx = 1.0;
                    gridBagConstraints.weighty = 1.0;
                    JPanel jPanel2 = new JPanel(){

                        @Override
                        public Dimension getMinimumSize() {
                            return this.getPreferredSize();
                        }
                    };
                    jPanel2.setBorder(BorderFactory.createLineBorder(Color.blue));
                    jPanel2.setLayout(new BorderLayout());
                    jPanel2.add(frame.getContentPane().getComponent(0));
                    jPanel.add((Component)jPanel2, gridBagConstraints);
                    jFrame.add(jPanel);
                    jFrame.setUndecorated(true);
                    jFrame.setAlwaysOnTop(true);
                    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                    jFrame.setSize(dimension.width, dimension.height);
                    jFrame.setVisible(true);
                    jFrame.toFront();
                    fullScreenFrame = jFrame;
                    frame.setVisible(false);
                    fullScreenMenuHandler = new FullScreenMenuHandler(jFrame, jMenuBar, new Component[0], "", true);
                    fullScreenMenuHandler.startSliding(true);
                } else {
                    fullScreenMenuHandler.stopSliding();
                    frame.add(((Container)((Container)fullScreenFrame.getContentPane().getComponent(0)).getComponent(0)).getComponent(0));
                    fullScreenFrame.dispose();
                    fullScreenMenuHandler = null;
                    frame.setJMenuBar(jMenuBar);
                    frame.setVisible(true);
                    frame.pack();
                }
            }
        });
        jMenu3.add(jCheckBoxMenuItem);
        jMenuBar.add(jMenu3);
        frame.setJMenuBar(jMenuBar);
        frame.getContentPane().setLayout(new BorderLayout());
        JPanel jPanel = new JPanel();
        JButton jButton = new JButton("Pause");
        JButton jButton2 = new JButton("Resume");
        jButton.addActionListener(actionListener);
        jButton2.addActionListener(actionListener2);
        jPanel.add(jButton);
        jPanel.add(jButton2);
        frame.setDefaultCloseOperation(3);
        VideoSnapshotViewer videoSnapshotViewer = new VideoSnapshotViewer();
        videoSnapshotViewer.setThumbnailSize(selectedSize);
        VideoSnapshotViewer videoSnapshotViewer2 = new VideoSnapshotViewer();
        videoSnapshotViewer2.setThumbnailSize(selectedSize);
        VideoSnapshotViewer videoSnapshotViewer3 = new VideoSnapshotViewer();
        videoSnapshotViewer3.setThumbnailSize(selectedSize);
        VideoSnapshotViewer videoSnapshotViewer4 = new VideoSnapshotViewer();
        videoSnapshotViewer4.setThumbnailSize(selectedSize);
        portIds = new String[]{"P_000d5d1d2d4d_0", "P_000d5d1d2d4d_11", "P_000d5d1d2d4d_4", "P_000d5d1d2d4d_15"};
        imgDisp = new VideoSnapshotViewer[]{videoSnapshotViewer, videoSnapshotViewer2, videoSnapshotViewer3, videoSnapshotViewer4};
        imgPanels = new ArrayList<VideoSnapshotPanel<Object>>();
        imgPanels.add(new VideoSnapshotPanel<String>(videoSnapshotViewer, portIds[0]));
        imgPanels.add(new VideoSnapshotPanel<String>(videoSnapshotViewer2, portIds[1]));
        imgPanels.add(new VideoSnapshotPanel<String>(videoSnapshotViewer3, portIds[2]));
        imgPanels.add(new VideoSnapshotPanel<String>(videoSnapshotViewer4, portIds[3]));
        for (VideoSnapshotPanel<Object> object42 : imgPanels) {
            object2 = new SelListener();
            object42.addVideoSnapshotSelectionListener((VideoSnapshotSelectionListener<Object>)object2);
        }
        for (int i = 0; i < imgPanels.size(); ++i) {
            VideoSnapshotPanel<Object> videoSnapshotPanel = imgPanels.get(i);
            if (i >= portIds.length) continue;
            videoSnapshotPanel.setInformation(portIds[i]);
        }
        JPanel jPanel2 = new JPanel();
        jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridCalculationUtil.GridLayoutInfo gridLayoutInfo = GridCalculationUtil.calculateColsAndRows(imgPanels.size());
        System.out.println(gridLayoutInfo);
        object2 = new GridLayout(gridLayoutInfo.rows, gridLayoutInfo.cols, 10, 10);
        jPanel2.setLayout((LayoutManager)object2);
        LinkedList<VideoSnapshotPanel<Object>> linkedList = new LinkedList<VideoSnapshotPanel<Object>>(imgPanels);
        for (Integer n : gridLayoutInfo.fillers) {
            linkedList.add(n - 1, (VideoSnapshotPanel<Object>)new JPanel());
        }
        for (JComponent jComponent : linkedList) {
            jComponent.setBorder(BorderFactory.createLineBorder(Color.RED));
            jPanel2.add(jComponent);
        }
        videoPanel = jPanel2;
        JScrollPane jScrollPane = new JScrollPane(jPanel2);
        jScrollPane.setHorizontalScrollBarPolicy(30);
        jScrollPane.setVerticalScrollBarPolicy(20);
        frame.getContentPane().add((Component)jScrollPane, "Center");
        frame.pack();
        frame.setVisible(true);
        final HashMap<String, VideoSnapshotPanel<Object>> hashMap = new HashMap<String, VideoSnapshotPanel<Object>>();
        final HashMap<String, VideoSnapshotViewer> hashMap2 = new HashMap<String, VideoSnapshotViewer>();
        int n = 0;
        for (String string : portIds) {
            hashMap2.put(string, imgDisp[n++]);
        }
        n = 0;
        for (String string : portIds) {
            hashMap.put(string, imgPanels.get(n++));
        }
        ScanEventListener scanEventListener = new ScanEventListener(){

            @Override
            public void videoComponentAvailable(String string, JComponent jComponent) {
                jComponent.setVisible(false);
                ((VideoSnapshotViewer)hashMap2.get(string)).add(jComponent);
                assert (((VideoSnapshotViewer)hashMap2.get(string)).getComponentCount() == 1);
            }

            @Override
            public void scanAbortedOnConnectError() {
                JOptionPane.showMessageDialog(frame, "Unable to connect to KX");
                System.exit(0);
            }

            @Override
            public void displayVideoStart(String string, boolean bl) {
                ((VideoSnapshotViewer)hashMap2.get(string)).getComponent(0).setVisible(true);
                if (bl) {
                    ((VideoSnapshotPanel)hashMap.get(string)).setTargetState(TargetState.DOWN);
                } else {
                    ((VideoSnapshotPanel)hashMap.get(string)).setTargetState(TargetState.CONNECTED);
                }
            }

            @Override
            public void displayVideoEnd(String string, Image image) {
                ((VideoSnapshotPanel)hashMap.get(string)).setTargetState(TargetState.INACTIVE);
                VideoSnapshotViewer videoSnapshotViewer = (VideoSnapshotViewer)hashMap2.get(string);
                JComponent jComponent = (JComponent)videoSnapshotViewer.getComponent(0);
                if (image != null) {
                    videoSnapshotViewer.setSnapshot(image, jComponent.getPreferredSize());
                }
                System.out.println(jComponent.getPreferredSize());
                videoSnapshotViewer.remove(jComponent);
                assert (((VideoSnapshotViewer)hashMap2.get(string)).getComponentCount() == 0);
            }

            @Override
            public void displayVideoError(String string, int n) {
                ((VideoSnapshotPanel)hashMap.get(string)).setTargetState(TargetState.INACTIVE);
                VideoSnapshotViewer videoSnapshotViewer = (VideoSnapshotViewer)hashMap2.get(string);
                videoSnapshotViewer.clearShanpshot();
                videoSnapshotViewer.remove(videoSnapshotViewer.getComponent(0));
                assert (((VideoSnapshotViewer)hashMap2.get(string)).getComponentCount() == 0);
            }

            @Override
            public void displayVideoCommunicationError(String string) {
                ((VideoSnapshotPanel)hashMap.get(string)).setTargetState(TargetState.INACTIVE);
                VideoSnapshotViewer videoSnapshotViewer = (VideoSnapshotViewer)hashMap2.get(string);
                videoSnapshotViewer.clearShanpshot();
                videoSnapshotViewer.remove(videoSnapshotViewer.getComponent(0));
                assert (((VideoSnapshotViewer)hashMap2.get(string)).getComponentCount() == 0);
            }

            @Override
            public void scanAborted(INotificationEvent iNotificationEvent) {
                JOptionPane.showMessageDialog(frame, "Scan Aborted : " + iNotificationEvent);
                System.exit(0);
            }

            @Override
            public void osdMessageReceived(String string, String string2) {
            }
        };
        String[] stringArray2 = new String[]{"P_000d5d1d2d4d_0", "P_000d5d1d2d4d_11", "P_000d5d1d2d4d_4", "P_000d5d1d2d4d_15"};
        String[] stringArray3 = new String[]{"basit_cim1", "Fedora", "vijay_cim1", "WinXP"};
        ArrayList<RemoteConsoleParameters> arrayList = new ArrayList<RemoteConsoleParameters>();
        for (int i = 0; i < stringArray2.length; ++i) {
            object = new RemoteConsoleParameters();
            ((RemoteConsoleParameters)object).targetPortId = stringArray2[i];
            ((RemoteConsoleParameters)object).portName = stringArray3[i];
            ((RemoteConsoleParameters)object).username = "test";
            ((RemoteConsoleParameters)object).password = "raritan";
            ((RemoteConsoleParameters)object).host = "192.168.50.207";
            ((RemoteConsoleParameters)object).tcpPort = 443;
            ((RemoteConsoleParameters)object).ssl = true;
            arrayList.add((RemoteConsoleParameters)object);
        }
        scanConnector = new ScanConnector(arrayList);
        scanConnector.addScanEventListener(scanEventListener);
        scanConnector.start(1);
        frame.pack();
        Rectangle rectangle = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        object = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
        Dimension dimension = new Dimension(rectangle.width - (((Insets)object).left + ((Insets)object).right), rectangle.height - (((Insets)object).top + ((Insets)object).bottom));
        if (frame.getWidth() > dimension.width || frame.getHeight() > dimension.height) {
            frame.setSize(dimension);
        }
    }

    private static void createRCCore() throws Exception {
        rccore = RCCoreFactory.loadGraphicalReadOnlyRCCore(null);
        rccore.setScaleToFit(true, true, selectedSize);
    }

    private static void connectRCCore() throws Exception {
        rccore.addVideoEventListener(colorCalListener, 32);
        rccore.addNotificationListener(videoStateNotificationListener);
        rccore.addConnectionEventListener(new ConnectionEventHandler(), 8191);
        rccore.connectRCWithUserLogin("192.168.50.207", 443, true, portIds[index], "test", "raritan");
    }

    private static void switchVideo() {
        imgDisp[index].clearShanpshot();
        imgDisp[index].getComponent(0).setVisible(true);
        scanTimer.restart();
    }

    private static void switchOnErrorVideo() {
        imgPanels.get(index).setTargetState(TargetState.BUSY);
        imgDisp[index].clearShanpshot();
        imgDisp[index].remove(rccore.getRCJComponent());
        rccore.disconnect();
        rccore.dispose();
        rccore = null;
        switchTimer.restart();
    }

    static {
        index = 0;
    }

    static class ConnectionEventHandler
    extends RCAdapter {
        ConnectionEventHandler() {
        }

        @Override
        public void disconnected(Exception exception) {
            System.out.println("Disconnected " + exception);
        }

        @Override
        public void connected() {
            System.out.println("Connected");
        }
    }

    static class SelListener
    implements VideoSnapshotSelectionListener<Object> {
        SelListener() {
        }

        @Override
        public void videoSnapshotSingleClickSelected(Object object) {
            System.out.println("Selected " + object);
            scanConnector.pause();
            selectedPortId = (String)object;
            resumeAfter.setEnabled(true);
        }

        @Override
        public void videoSnapshotDoubleClickSelected(Object object) {
        }
    }

    static class SizeSelector
    implements ActionListener {
        SizeSelector() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String string = ((JRadioButtonMenuItem)actionEvent.getSource()).getText();
            if ("320 * 240".equals(string)) {
                selectedSize = new Dimension(320, 240);
            } else if ("160 * 120".equals(string)) {
                selectedSize = new Dimension(160, 120);
            }
            for (VideoSnapshotViewer videoSnapshotViewer : imgDisp) {
                videoSnapshotViewer.setThumbnailSize(selectedSize);
            }
            ((JComponent)videoPanel.getRootPane().getContentPane()).revalidate();
            videoPanel.revalidate();
            frame.pack();
        }
    }
}

