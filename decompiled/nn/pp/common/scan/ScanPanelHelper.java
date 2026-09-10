/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import nn.pp.common.DoubleClickHandler;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.common.ResourceLoader;
import nn.pp.common.Util;
import nn.pp.common.scan.ResizeableVideoSnapshotViewer;
import nn.pp.common.scan.ScanConnector;
import nn.pp.common.scan.ScanConnectorState;
import nn.pp.common.scan.ScanEventListener;
import nn.pp.common.scan.ScanSplitPane;
import nn.pp.common.scan.ScrollableFlowPanel;
import nn.pp.common.scan.ScrollableVerticalFlowPanel;
import nn.pp.common.scan.StackLayout;
import nn.pp.common.scan.TargetState;
import nn.pp.common.scan.VideoSnapshotPanel;
import nn.pp.common.scan.VideoSnapshotSelectionListener;
import nn.pp.common.scan.VideoSnapshotViewer;
import nn.pp.core.INotificationEvent;

public class ScanPanelHelper {
    private static final String NO_VIDEO_FROM_TARGET_SERVER = "No video from target server";
    private static final Map<Integer, Image> overlayImages = new HashMap<Integer, Image>();
    private static final Border EMPTY_BORDER_4;
    private static final Border LINE_BORDER_4;
    private final SequentialScanEventHandler sequentialScanEventHandler = new SequentialScanEventHandler();
    private final ThumbnailScanEventHandler thumbnailScanEventHandler = new ThumbnailScanEventHandler();
    private final List<RemoteConsoleParameters> remoteConsoleParamsList;
    private final Map<String, RemoteConsoleParameters> portIdToRemoteConsoleParams = new HashMap<String, RemoteConsoleParameters>();
    private final JComponent scanPanel;
    private final ScanConnector scanConnector;
    private Dimension thumbnailSize = new Dimension(320, 240);
    private int splitPaneOrientation = 0;

    public ScanPanelHelper(List<RemoteConsoleParameters> list, ScanConnector scanConnector, Dimension dimension, int n) {
        this.remoteConsoleParamsList = list;
        this.scanConnector = scanConnector;
        this.thumbnailSize = dimension;
        this.splitPaneOrientation = n;
        this.scanPanel = this.createAndInitScanPanel();
        for (RemoteConsoleParameters remoteConsoleParameters : list) {
            this.portIdToRemoteConsoleParams.put(remoteConsoleParameters.targetPortId, remoteConsoleParameters);
        }
    }

    private JComponent createAndInitScanPanel() {
        ScanSplitPane scanSplitPane = new ScanSplitPane(this.splitPaneOrientation);
        ResizeableVideoSnapshotViewer resizeableVideoSnapshotViewer = this.sequentialScanEventHandler.getSequentialViewer();
        JComponent jComponent = this.thumbnailScanEventHandler.getThumbnailComponent();
        scanSplitPane.setTopComponent(this.createGlsPaneBackedPanel(resizeableVideoSnapshotViewer));
        scanSplitPane.setBottomComponent(jComponent);
        this.scanConnector.addScanEventListener(this.sequentialScanEventHandler);
        this.scanConnector.addScanEventListener(this.thumbnailScanEventHandler.getStateUpdater());
        this.scanConnector.addScanEventListener(this.thumbnailScanEventHandler.getLoupeUpdater());
        this.scanConnector.addScanEventListener(this.sequentialScanEventHandler.getSnapshotUpdater());
        scanSplitPane.addTopComponentListener(new SequentialViewerVisiblityHandler());
        JComponent jComponent2 = this.thumbnailScanEventHandler.getNewThumbnailComponent();
        Dimension dimension = scanSplitPane.getSize();
        Dimension dimension2 = jComponent2.getPreferredSize();
        if (this.splitPaneOrientation == 1) {
            scanSplitPane.setOrientation(0);
        } else if (this.splitPaneOrientation == 0) {
            scanSplitPane.setOrientation(1);
        }
        return scanSplitPane;
    }

    private JPanel createGlsPaneBackedPanel(ResizeableVideoSnapshotViewer resizeableVideoSnapshotViewer) {
        JPanel jPanel = new JPanel();
        jPanel.setMinimumSize(new Dimension());
        jPanel.setLayout(new StackLayout());
        JComponent jComponent = new JComponent(){};
        DoubleClickHandler doubleClickHandler = new DoubleClickHandler();
        doubleClickHandler.addMouseListener(this.sequentialScanEventHandler.getMouseClickHandler());
        jComponent.addMouseListener(doubleClickHandler);
        jPanel.add((Component)jComponent, "top");
        jPanel.add((Component)resizeableVideoSnapshotViewer, "bottom");
        return jPanel;
    }

    public ScanConnector getScanConnector() {
        return this.scanConnector;
    }

    public JComponent getScanPanel() {
        return this.scanPanel;
    }

    public Dimension getThumbnailSize() {
        return this.thumbnailSize;
    }

    public void setThumbnailSize(Dimension dimension) {
        this.thumbnailSize = dimension;
        this.thumbnailScanEventHandler.setThumbnailSize(dimension);
        this.setSplitPaneDividerLocation();
    }

    private void setSplitPaneDividerLocation() {
        JSplitPane jSplitPane = (JSplitPane)this.scanPanel;
        JComponent jComponent = this.thumbnailScanEventHandler.getNewThumbnailComponent();
        Dimension dimension = jSplitPane.getSize();
        Dimension dimension2 = jComponent.getPreferredSize();
        if (!this.isExpanded()) {
            if (this.splitPaneOrientation == 1) {
                jSplitPane.setDividerLocation(1.0f - (float)dimension2.height / ((float)dimension.height - (float)jSplitPane.getDividerSize()));
            } else {
                jSplitPane.setDividerLocation(1.0f - (float)dimension2.width / ((float)dimension.width - (float)jSplitPane.getDividerSize()));
            }
        }
    }

    private boolean isExpanded() {
        JSplitPane jSplitPane = (JSplitPane)this.scanPanel;
        return this.isExpanded(jSplitPane.getLeftComponent()) || this.isExpanded(jSplitPane.getRightComponent());
    }

    private boolean isExpanded(Component component) {
        JSplitPane jSplitPane = (JSplitPane)this.scanPanel;
        return (jSplitPane.getOrientation() == 1 ? component.getWidth() : component.getHeight()) == 0;
    }

    public void setSplitPaneOrientation(int n) {
        JSplitPane jSplitPane = (JSplitPane)this.scanPanel;
        if (this.splitPaneOrientation != n) {
            this.splitPaneOrientation = n;
            JComponent jComponent = this.thumbnailScanEventHandler.getNewThumbnailComponent();
            JScrollPane jScrollPane = (JScrollPane)jComponent;
            Dimension dimension = jSplitPane.getSize();
            switch (n) {
                case 1: {
                    jSplitPane.setBottomComponent(jComponent);
                    Dimension dimension2 = jComponent.getPreferredSize();
                    jSplitPane.setOrientation(0);
                    jSplitPane.setDividerLocation(1.0f - (float)dimension2.height / ((float)dimension.height - (float)jSplitPane.getDividerSize()));
                    jScrollPane.setHorizontalScrollBarPolicy(30);
                    jScrollPane.setVerticalScrollBarPolicy(21);
                    break;
                }
                case 0: {
                    jSplitPane.setBottomComponent(jComponent);
                    Dimension dimension3 = jComponent.getPreferredSize();
                    jSplitPane.setOrientation(1);
                    jSplitPane.setDividerLocation(1.0f - (float)dimension3.width / ((float)dimension.width - (float)jSplitPane.getDividerSize()));
                    jScrollPane.setHorizontalScrollBarPolicy(31);
                    jScrollPane.setVerticalScrollBarPolicy(20);
                    break;
                }
            }
        }
    }

    public int getSplitPaneOrientation() {
        return this.splitPaneOrientation;
    }

    public void addVideoSnapshotSelectionListener(VideoSnapshotSelectionListener<RemoteConsoleParameters> videoSnapshotSelectionListener) {
        this.thumbnailScanEventHandler.addVideoSnapshotSelectionListener(videoSnapshotSelectionListener);
        this.sequentialScanEventHandler.addVideoSnapshotSelectionListener(videoSnapshotSelectionListener);
    }

    public void removeVideoSnapshotSelectionListener(VideoSnapshotSelectionListener<RemoteConsoleParameters> videoSnapshotSelectionListener) {
        this.thumbnailScanEventHandler.removeVideoSnapshotSelectionListener(videoSnapshotSelectionListener);
        this.sequentialScanEventHandler.removeVideoSnapshotSelectionListener(videoSnapshotSelectionListener);
    }

    static {
        overlayImages.put(320, ResourceLoader.loadImageIcon("Common_loupe_320.png").getImage());
        overlayImages.put(160, ResourceLoader.loadImageIcon("Common_loupe_160.png").getImage());
        EMPTY_BORDER_4 = BorderFactory.createEmptyBorder(4, 4, 4, 4);
        LINE_BORDER_4 = BorderFactory.createLineBorder(Color.BLUE, 4);
    }

    private class SequentialViewerVisiblityHandler
    extends ComponentAdapter {
        private SequentialViewerVisiblityHandler() {
        }

        @Override
        public void componentShown(ComponentEvent componentEvent) {
            ScanPanelHelper.this.scanConnector.removeScanEventListener(ScanPanelHelper.this.thumbnailScanEventHandler);
            ScanPanelHelper.this.scanConnector.addScanEventListener(ScanPanelHelper.this.sequentialScanEventHandler);
            ScanPanelHelper.this.scanConnector.addScanEventListener(ScanPanelHelper.this.thumbnailScanEventHandler.getLoupeUpdater());
            switch (ScanPanelHelper.this.thumbnailScanEventHandler.getScanConnectorState()) {
                case COMP_AVAILABLE: {
                    ScanPanelHelper.this.sequentialScanEventHandler.videoComponentAvailable(ScanPanelHelper.this.thumbnailScanEventHandler.getPortId(), ScanPanelHelper.this.thumbnailScanEventHandler.getVideoComponent());
                    ScanPanelHelper.this.thumbnailScanEventHandler.setOverlayImage(ScanPanelHelper.this.thumbnailScanEventHandler.getPortId());
                    break;
                }
                case VIDEO_START: {
                    ScanPanelHelper.this.sequentialScanEventHandler.videoComponentAvailable(ScanPanelHelper.this.thumbnailScanEventHandler.getPortId(), ScanPanelHelper.this.thumbnailScanEventHandler.getVideoComponent());
                    ScanPanelHelper.this.sequentialScanEventHandler.displayVideoStart(ScanPanelHelper.this.thumbnailScanEventHandler.getPortId(), false);
                    ScanPanelHelper.this.thumbnailScanEventHandler.setOverlayImage(ScanPanelHelper.this.thumbnailScanEventHandler.getPortId());
                    break;
                }
                case VIDEO_END: {
                    ScanPanelHelper.this.thumbnailScanEventHandler.setOverlayImage(ScanPanelHelper.this.thumbnailScanEventHandler.getPortId());
                    break;
                }
                case INIT: 
                case COMM_ERROR: 
                case VIDEO_ERROR: 
                case SCAN_ABORTED: 
                case SCAN_ABORTED_ON_COMM_ERROR: {
                    break;
                }
                default: {
                    throw new AssertionError((Object)"Handle new ScanConnectorState");
                }
            }
            ScanPanelHelper.this.sequentialScanEventHandler.setPortId(ScanPanelHelper.this.thumbnailScanEventHandler.getPortId());
            ScanPanelHelper.this.sequentialScanEventHandler.setScanConnectorState(ScanPanelHelper.this.thumbnailScanEventHandler.getScanConnectorState());
        }

        @Override
        public void componentHidden(ComponentEvent componentEvent) {
            ScanPanelHelper.this.scanConnector.removeScanEventListener(ScanPanelHelper.this.sequentialScanEventHandler);
            ScanPanelHelper.this.scanConnector.addScanEventListener(ScanPanelHelper.this.thumbnailScanEventHandler);
            ScanPanelHelper.this.scanConnector.removeScanEventListener(ScanPanelHelper.this.thumbnailScanEventHandler.getLoupeUpdater());
            ScanPanelHelper.this.thumbnailScanEventHandler.clearOverlayImage();
            switch (ScanPanelHelper.this.sequentialScanEventHandler.getScanConnectorState()) {
                case COMP_AVAILABLE: {
                    ScanPanelHelper.this.thumbnailScanEventHandler.videoComponentAvailable(ScanPanelHelper.this.sequentialScanEventHandler.getPortId(), ScanPanelHelper.this.sequentialScanEventHandler.getVideoComponent());
                    break;
                }
                case VIDEO_START: {
                    ScanPanelHelper.this.thumbnailScanEventHandler.videoComponentAvailable(ScanPanelHelper.this.sequentialScanEventHandler.getPortId(), ScanPanelHelper.this.sequentialScanEventHandler.getVideoComponent());
                    ScanPanelHelper.this.thumbnailScanEventHandler.displayVideoStart(ScanPanelHelper.this.sequentialScanEventHandler.getPortId(), false);
                    break;
                }
                case VIDEO_END: 
                case INIT: 
                case COMM_ERROR: 
                case VIDEO_ERROR: 
                case SCAN_ABORTED: 
                case SCAN_ABORTED_ON_COMM_ERROR: {
                    break;
                }
                default: {
                    throw new AssertionError((Object)"Handle new ScanConnectorState");
                }
            }
            ScanPanelHelper.this.thumbnailScanEventHandler.setPortId(ScanPanelHelper.this.sequentialScanEventHandler.getPortId());
            ScanPanelHelper.this.thumbnailScanEventHandler.setScanConnectorState(ScanPanelHelper.this.sequentialScanEventHandler.getScanConnectorState());
        }
    }

    private class SequentialScanEventHandler
    implements ScanEventListener {
        private String portId;
        private ScanConnectorState scanConnectorState = ScanConnectorState.INIT;
        private final List<VideoSnapshotSelectionListener<RemoteConsoleParameters>> listeners = new ArrayList<VideoSnapshotSelectionListener<RemoteConsoleParameters>>();
        private final ResizeableVideoSnapshotViewer sequentialViewer = new ResizeableVideoSnapshotViewer();
        private final SnapshotUpdater snapshotUpdater = new SnapshotUpdater();
        private final MouseClickHandler mouseClickHandler = new MouseClickHandler();

        private SequentialScanEventHandler() {
        }

        public MouseClickHandler getMouseClickHandler() {
            return this.mouseClickHandler;
        }

        public void setPortId(String string) {
            this.portId = string;
        }

        public void setScanConnectorState(ScanConnectorState scanConnectorState) {
            this.scanConnectorState = scanConnectorState;
        }

        public SnapshotUpdater getSnapshotUpdater() {
            return this.snapshotUpdater;
        }

        public ResizeableVideoSnapshotViewer getSequentialViewer() {
            return this.sequentialViewer;
        }

        public String getPortId() {
            return this.portId;
        }

        public ScanConnectorState getScanConnectorState() {
            return this.scanConnectorState;
        }

        public JComponent getVideoComponent() {
            return (JComponent)this.sequentialViewer.getComponent(0);
        }

        @Override
        public void videoComponentAvailable(String string, JComponent jComponent) {
            this.scanConnectorState = ScanConnectorState.COMP_AVAILABLE;
            this.portId = string;
            jComponent.setVisible(false);
            this.sequentialViewer.add(jComponent);
            assert (this.sequentialViewer.getComponentCount() == 1);
        }

        @Override
        public void displayVideoStart(String string, boolean bl) {
            this.scanConnectorState = ScanConnectorState.VIDEO_START;
            this.sequentialViewer.getComponent(0).setVisible(true);
        }

        @Override
        public void displayVideoEnd(String string, Image image) {
            this.scanConnectorState = ScanConnectorState.VIDEO_END;
            ResizeableVideoSnapshotViewer resizeableVideoSnapshotViewer = this.sequentialViewer;
            if (resizeableVideoSnapshotViewer.getComponentCount() > 0) {
                resizeableVideoSnapshotViewer.remove(resizeableVideoSnapshotViewer.getComponent(0));
            }
            assert (this.sequentialViewer.getComponentCount() == 0);
        }

        @Override
        public void displayVideoError(String string, int n) {
            this.scanConnectorState = ScanConnectorState.VIDEO_ERROR;
            ResizeableVideoSnapshotViewer resizeableVideoSnapshotViewer = this.sequentialViewer;
            resizeableVideoSnapshotViewer.remove(resizeableVideoSnapshotViewer.getComponent(0));
            assert (this.sequentialViewer.getComponentCount() == 0);
        }

        @Override
        public void displayVideoCommunicationError(String string) {
            this.scanConnectorState = ScanConnectorState.COMM_ERROR;
            ResizeableVideoSnapshotViewer resizeableVideoSnapshotViewer = this.sequentialViewer;
            resizeableVideoSnapshotViewer.remove(resizeableVideoSnapshotViewer.getComponent(0));
            assert (this.sequentialViewer.getComponentCount() == 0);
        }

        @Override
        public void scanAborted(INotificationEvent iNotificationEvent) {
            this.scanConnectorState = ScanConnectorState.SCAN_ABORTED;
        }

        @Override
        public void scanAbortedOnConnectError() {
            this.scanConnectorState = ScanConnectorState.SCAN_ABORTED_ON_COMM_ERROR;
        }

        public void addVideoSnapshotSelectionListener(VideoSnapshotSelectionListener<RemoteConsoleParameters> videoSnapshotSelectionListener) {
            if (!this.listeners.contains(videoSnapshotSelectionListener)) {
                this.listeners.add(videoSnapshotSelectionListener);
            }
        }

        public boolean removeVideoSnapshotSelectionListener(VideoSnapshotSelectionListener<RemoteConsoleParameters> videoSnapshotSelectionListener) {
            return this.listeners.remove(videoSnapshotSelectionListener);
        }

        private void fireSingleClickSelection() {
            RemoteConsoleParameters remoteConsoleParameters = (RemoteConsoleParameters)ScanPanelHelper.this.portIdToRemoteConsoleParams.get(this.portId);
            for (VideoSnapshotSelectionListener<RemoteConsoleParameters> videoSnapshotSelectionListener : this.listeners) {
                videoSnapshotSelectionListener.videoSnapshotSingleClickSelected(remoteConsoleParameters);
            }
        }

        private void fireDoubleClickSelection() {
            RemoteConsoleParameters remoteConsoleParameters = (RemoteConsoleParameters)ScanPanelHelper.this.portIdToRemoteConsoleParams.get(this.portId);
            for (VideoSnapshotSelectionListener<RemoteConsoleParameters> videoSnapshotSelectionListener : this.listeners) {
                videoSnapshotSelectionListener.videoSnapshotDoubleClickSelected(remoteConsoleParameters);
            }
        }

        @Override
        public void osdMessageReceived(String string, String string2) {
        }

        private class MouseClickHandler
        extends MouseAdapter {
            private MouseClickHandler() {
            }

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {
                    SequentialScanEventHandler.this.fireSingleClickSelection();
                } else {
                    SequentialScanEventHandler.this.fireDoubleClickSelection();
                }
            }
        }

        private class SnapshotUpdater
        implements ScanEventListener {
            private SnapshotUpdater() {
            }

            @Override
            public void videoComponentAvailable(String string, JComponent jComponent) {
            }

            @Override
            public void displayVideoStart(String string, boolean bl) {
                SequentialScanEventHandler.this.sequentialViewer.clearShanpshot();
            }

            @Override
            public void displayVideoEnd(String string, Image image) {
                if (image != null) {
                    SequentialScanEventHandler.this.sequentialViewer.setSnapshot(image, new Dimension(image.getWidth(null), image.getHeight(null)));
                }
            }

            @Override
            public void displayVideoCommunicationError(String string) {
                SequentialScanEventHandler.this.sequentialViewer.clearShanpshot();
            }

            @Override
            public void displayVideoError(String string, int n) {
                SequentialScanEventHandler.this.sequentialViewer.clearShanpshot();
            }

            @Override
            public void scanAborted(INotificationEvent iNotificationEvent) {
            }

            @Override
            public void scanAbortedOnConnectError() {
            }

            @Override
            public void osdMessageReceived(String string, String string2) {
            }
        }
    }

    private class ThumbnailScanEventHandler
    implements ScanEventListener {
        private String portId;
        private ScanConnectorState scanConnectorState = ScanConnectorState.INIT;
        private final HashMap<String, VideoSnapshotPanel<RemoteConsoleParameters>> snapshotPanelsMap = new LinkedHashMap<String, VideoSnapshotPanel<RemoteConsoleParameters>>();
        private final HashMap<String, VideoSnapshotViewer> scanViewerMap = new HashMap();
        private final StateUpdater stateUpdater = new StateUpdater();
        private final LoupeUpdater loupeUpdater = new LoupeUpdater();
        private final SelectionListener selectionListener = new SelectionListener();
        private JScrollPane thumbnailComponent;
        private JPanel thumbnailPanel;

        private ThumbnailScanEventHandler() {
        }

        public void setPortId(String string) {
            this.portId = string;
        }

        public void setScanConnectorState(ScanConnectorState scanConnectorState) {
            this.scanConnectorState = scanConnectorState;
        }

        public LoupeUpdater getLoupeUpdater() {
            return this.loupeUpdater;
        }

        public StateUpdater getStateUpdater() {
            return this.stateUpdater;
        }

        public String getPortId() {
            return this.portId;
        }

        public ScanConnectorState getScanConnectorState() {
            return this.scanConnectorState;
        }

        public JComponent getVideoComponent() {
            return (JComponent)this.scanViewerMap.get(this.portId).getComponent(0);
        }

        public void setThumbnailSize(Dimension dimension) {
            for (VideoSnapshotViewer videoSnapshotViewer : this.scanViewerMap.values()) {
                videoSnapshotViewer.setThumbnailSize(dimension);
            }
            this.thumbnailPanel.invalidate();
            this.thumbnailPanel.validate();
            this.thumbnailPanel.revalidate();
        }

        public void addVideoSnapshotSelectionListener(VideoSnapshotSelectionListener<RemoteConsoleParameters> videoSnapshotSelectionListener) {
            for (VideoSnapshotPanel<RemoteConsoleParameters> videoSnapshotPanel : this.snapshotPanelsMap.values()) {
                videoSnapshotPanel.addVideoSnapshotSelectionListener(videoSnapshotSelectionListener);
            }
        }

        public void removeVideoSnapshotSelectionListener(VideoSnapshotSelectionListener<RemoteConsoleParameters> videoSnapshotSelectionListener) {
            for (VideoSnapshotPanel<RemoteConsoleParameters> videoSnapshotPanel : this.snapshotPanelsMap.values()) {
                videoSnapshotPanel.removeVideoSnapshotSelectionListener(videoSnapshotSelectionListener);
            }
        }

        public JComponent getThumbnailComponent() {
            if (this.thumbnailComponent == null) {
                this.thumbnailPanel = ScanPanelHelper.this.splitPaneOrientation == 1 ? new ScrollableVerticalFlowPanel() : new ScrollableFlowPanel();
                JScrollPane jScrollPane = new JScrollPane(this.thumbnailPanel);
                if (ScanPanelHelper.this.splitPaneOrientation == 1) {
                    jScrollPane.setHorizontalScrollBarPolicy(30);
                    jScrollPane.setVerticalScrollBarPolicy(21);
                } else if (ScanPanelHelper.this.splitPaneOrientation == 0) {
                    jScrollPane.setVerticalScrollBarPolicy(20);
                    jScrollPane.setHorizontalScrollBarPolicy(31);
                }
                jScrollPane.setMinimumSize(new Dimension());
                for (RemoteConsoleParameters remoteConsoleParameters : ScanPanelHelper.this.remoteConsoleParamsList) {
                    VideoSnapshotViewer videoSnapshotViewer = new VideoSnapshotViewer();
                    videoSnapshotViewer.setThumbnailSize(ScanPanelHelper.this.thumbnailSize);
                    this.scanViewerMap.put(remoteConsoleParameters.targetPortId, videoSnapshotViewer);
                    VideoSnapshotPanel<RemoteConsoleParameters> videoSnapshotPanel = new VideoSnapshotPanel<RemoteConsoleParameters>(videoSnapshotViewer, remoteConsoleParameters);
                    videoSnapshotPanel.setInformation(this.getFormattedPortNumber(remoteConsoleParameters.portIndex) + remoteConsoleParameters.portName);
                    videoSnapshotPanel.setBorder(EMPTY_BORDER_4);
                    videoSnapshotPanel.addVideoSnapshotSelectionListener(this.selectionListener);
                    this.thumbnailPanel.add(videoSnapshotPanel);
                    this.snapshotPanelsMap.put(remoteConsoleParameters.targetPortId, videoSnapshotPanel);
                }
                this.thumbnailComponent = jScrollPane;
            }
            return this.thumbnailComponent;
        }

        private String getFormattedPortNumber(String string) {
            String string2 = "";
            try {
                if (string.contains(".")) {
                    int[] nArray = Util.getPortNumber(string);
                    String string3 = Integer.toString(nArray[0] + 1);
                    String string4 = Integer.toString(nArray[1]);
                    string2 = string3 + "-" + string4 + " - ";
                } else {
                    int n = Integer.parseInt(string) + 1;
                    string2 = Integer.toString(n) + " - ";
                }
                return string2;
            }
            catch (NumberFormatException numberFormatException) {
                return "";
            }
        }

        public JComponent getNewThumbnailComponent() {
            JPanel jPanel = ScanPanelHelper.this.splitPaneOrientation == 1 ? new ScrollableFlowPanel() : new ScrollableVerticalFlowPanel();
            for (VideoSnapshotPanel<RemoteConsoleParameters> videoSnapshotPanel : this.snapshotPanelsMap.values()) {
                jPanel.add(videoSnapshotPanel);
            }
            this.thumbnailPanel = jPanel;
            this.thumbnailComponent.setViewportView(this.thumbnailPanel);
            return this.thumbnailComponent;
        }

        @Override
        public void videoComponentAvailable(String string, JComponent jComponent) {
            this.scanConnectorState = ScanConnectorState.COMP_AVAILABLE;
            this.portId = string;
            jComponent.setVisible(false);
            this.scanViewerMap.get(string).add(jComponent);
            assert (this.scanViewerMap.get(string).getComponentCount() == 1);
        }

        @Override
        public void displayVideoStart(String string, boolean bl) {
            this.scanConnectorState = ScanConnectorState.VIDEO_START;
            this.scanViewerMap.get(string).getComponent(0).setVisible(true);
        }

        @Override
        public void displayVideoEnd(String string, Image image) {
            this.scanConnectorState = ScanConnectorState.VIDEO_END;
            VideoSnapshotViewer videoSnapshotViewer = this.scanViewerMap.get(string);
            videoSnapshotViewer.remove(videoSnapshotViewer.getComponent(0));
            assert (this.scanViewerMap.get(string).getComponentCount() == 0);
        }

        @Override
        public void displayVideoError(String string, int n) {
            this.scanConnectorState = ScanConnectorState.VIDEO_ERROR;
            VideoSnapshotViewer videoSnapshotViewer = this.scanViewerMap.get(string);
            videoSnapshotViewer.remove(videoSnapshotViewer.getComponent(0));
            assert (this.scanViewerMap.get(string).getComponentCount() == 0);
        }

        @Override
        public void displayVideoCommunicationError(String string) {
            this.scanConnectorState = ScanConnectorState.COMM_ERROR;
            VideoSnapshotViewer videoSnapshotViewer = this.scanViewerMap.get(string);
            videoSnapshotViewer.remove(videoSnapshotViewer.getComponent(0));
            assert (this.scanViewerMap.get(string).getComponentCount() == 0);
        }

        @Override
        public void scanAborted(INotificationEvent iNotificationEvent) {
            this.scanConnectorState = ScanConnectorState.SCAN_ABORTED;
        }

        @Override
        public void scanAbortedOnConnectError() {
            this.scanConnectorState = ScanConnectorState.SCAN_ABORTED_ON_COMM_ERROR;
        }

        public void clearOverlayImage() {
            for (VideoSnapshotViewer videoSnapshotViewer : this.scanViewerMap.values()) {
                videoSnapshotViewer.setOverlayImage(null);
            }
        }

        public void clearBorder() {
            for (VideoSnapshotPanel<RemoteConsoleParameters> videoSnapshotPanel : this.snapshotPanelsMap.values()) {
                if (videoSnapshotPanel.getBorder().equals(EMPTY_BORDER_4)) continue;
                videoSnapshotPanel.setBorder(EMPTY_BORDER_4);
            }
        }

        public void clearLedState() {
            for (VideoSnapshotPanel<RemoteConsoleParameters> videoSnapshotPanel : this.snapshotPanelsMap.values()) {
                videoSnapshotPanel.setTargetState(TargetState.INACTIVE);
            }
        }

        public void setOverlayImage(String string) {
            this.scanViewerMap.get(string).setOverlayImage(overlayImages);
        }

        @Override
        public void osdMessageReceived(String string, String string2) {
        }

        private class SelectionListener
        implements VideoSnapshotSelectionListener<RemoteConsoleParameters> {
            private SelectionListener() {
            }

            @Override
            public void videoSnapshotDoubleClickSelected(RemoteConsoleParameters remoteConsoleParameters) {
            }

            @Override
            public void videoSnapshotSingleClickSelected(RemoteConsoleParameters remoteConsoleParameters) {
                ScanPanelHelper.this.scanConnector.pause();
                ScanPanelHelper.this.scanConnector.resumeAt(remoteConsoleParameters.targetPortId);
            }
        }

        private class LoupeUpdater
        implements ScanEventListener {
            private LoupeUpdater() {
            }

            @Override
            public void videoComponentAvailable(String string, JComponent jComponent) {
                ThumbnailScanEventHandler.this.clearOverlayImage();
                ThumbnailScanEventHandler.this.setOverlayImage(string);
            }

            @Override
            public void displayVideoStart(String string, boolean bl) {
            }

            @Override
            public void displayVideoEnd(String string, Image image) {
            }

            @Override
            public void displayVideoCommunicationError(String string) {
                ((VideoSnapshotViewer)ThumbnailScanEventHandler.this.scanViewerMap.get(string)).setOverlayImage(null);
            }

            @Override
            public void displayVideoError(String string, int n) {
                ((VideoSnapshotViewer)ThumbnailScanEventHandler.this.scanViewerMap.get(string)).setOverlayImage(null);
            }

            @Override
            public void scanAborted(INotificationEvent iNotificationEvent) {
            }

            @Override
            public void scanAbortedOnConnectError() {
            }

            @Override
            public void osdMessageReceived(String string, String string2) {
            }
        }

        private class StateUpdater
        implements ScanEventListener {
            private StateUpdater() {
            }

            @Override
            public void videoComponentAvailable(String string, JComponent jComponent) {
                VideoSnapshotPanel videoSnapshotPanel = (VideoSnapshotPanel)ThumbnailScanEventHandler.this.snapshotPanelsMap.get(string);
                videoSnapshotPanel.setTargetState(TargetState.INACTIVE);
                ThumbnailScanEventHandler.this.clearBorder();
                ThumbnailScanEventHandler.this.clearLedState();
                videoSnapshotPanel.setBorder(LINE_BORDER_4);
                videoSnapshotPanel.scrollRectToVisible(new Rectangle(0, 0, videoSnapshotPanel.getWidth(), videoSnapshotPanel.getHeight()));
            }

            @Override
            public void displayVideoStart(String string, boolean bl) {
                VideoSnapshotPanel videoSnapshotPanel = (VideoSnapshotPanel)ThumbnailScanEventHandler.this.snapshotPanelsMap.get(string);
                if (videoSnapshotPanel.getTargetState() == TargetState.INACTIVE) {
                    videoSnapshotPanel.setTargetState(TargetState.CONNECTED);
                }
                ((VideoSnapshotViewer)ThumbnailScanEventHandler.this.scanViewerMap.get(string)).clearShanpshot();
            }

            @Override
            public void displayVideoEnd(String string, Image image) {
                ((VideoSnapshotPanel)ThumbnailScanEventHandler.this.snapshotPanelsMap.get(string)).setTargetState(TargetState.INACTIVE);
                if (image != null) {
                    ((VideoSnapshotViewer)ThumbnailScanEventHandler.this.scanViewerMap.get(string)).setSnapshot(image, nn.pp.core.Util.getScaledDimension(ScanPanelHelper.this.thumbnailSize, new Dimension(image.getWidth(null), image.getHeight(null))));
                }
            }

            @Override
            public void displayVideoCommunicationError(String string) {
                ((VideoSnapshotPanel)ThumbnailScanEventHandler.this.snapshotPanelsMap.get(string)).setTargetState(TargetState.BUSY);
                ((VideoSnapshotViewer)ThumbnailScanEventHandler.this.scanViewerMap.get(string)).clearShanpshot();
            }

            @Override
            public void displayVideoError(String string, int n) {
                ((VideoSnapshotPanel)ThumbnailScanEventHandler.this.snapshotPanelsMap.get(string)).setTargetState(TargetState.BUSY);
                ((VideoSnapshotViewer)ThumbnailScanEventHandler.this.scanViewerMap.get(string)).clearShanpshot();
            }

            @Override
            public void scanAborted(INotificationEvent iNotificationEvent) {
            }

            @Override
            public void scanAbortedOnConnectError() {
            }

            @Override
            public void osdMessageReceived(String string, String string2) {
                if (ScanPanelHelper.NO_VIDEO_FROM_TARGET_SERVER.equals(string2)) {
                    ((VideoSnapshotPanel)ThumbnailScanEventHandler.this.snapshotPanelsMap.get(string)).setTargetState(TargetState.DOWN);
                } else {
                    ((VideoSnapshotPanel)ThumbnailScanEventHandler.this.snapshotPanelsMap.get(string)).setTargetState(TargetState.CONNECTED);
                }
            }
        }
    }
}

