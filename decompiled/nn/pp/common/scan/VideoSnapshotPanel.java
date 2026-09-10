/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import nn.pp.common.DoubleClickHandler;
import nn.pp.common.ResourceLoader;
import nn.pp.common.scan.StackLayout;
import nn.pp.common.scan.TargetState;
import nn.pp.common.scan.VideoSnapshotSelectionListener;
import nn.pp.common.scan.VideoSnapshotViewer;

public class VideoSnapshotPanel<M>
extends JPanel {
    private VideoSnapshotViewer videoSnapshotViewer;
    private final JLabel targetInfo = new SizeRestrictedLabel();
    private final M momento;
    private TargetState targetState = TargetState.INACTIVE;
    private final List<VideoSnapshotSelectionListener<M>> listeners = new ArrayList<VideoSnapshotSelectionListener<M>>();
    private static final EnumMap<TargetState, ImageIcon> ICON_MAP = new EnumMap(TargetState.class);

    public VideoSnapshotPanel(VideoSnapshotViewer videoSnapshotViewer, M m) {
        this.videoSnapshotViewer = videoSnapshotViewer;
        this.momento = m;
        this.layoutComponents();
    }

    private void layoutComponents() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new StackLayout());
        JComponent jComponent = new JComponent(){};
        DoubleClickHandler doubleClickHandler = new DoubleClickHandler();
        doubleClickHandler.addMouseListener(new MouseClickHandler());
        jComponent.addMouseListener(doubleClickHandler);
        jPanel.add((Component)jComponent, "top");
        jPanel.add((Component)this.videoSnapshotViewer, "bottom");
        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.add((Component)jPanel, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(6, 0, 0, 0);
        gridBagConstraints.fill = 2;
        this.targetInfo.setText("ASAS");
        this.setTargetState(TargetState.INACTIVE);
        this.add((Component)this.targetInfo, gridBagConstraints);
    }

    public void setInformation(String string) {
        this.targetInfo.setText(string);
        this.targetInfo.setToolTipText(string);
    }

    public TargetState getTargetState() {
        return this.targetState;
    }

    public void setTargetState(TargetState targetState) {
        this.setTargetState0(targetState);
    }

    private void setTargetState0(TargetState targetState) {
        this.targetState = targetState;
        this.targetInfo.setIcon(ICON_MAP.get((Object)targetState));
    }

    public void addVideoSnapshotSelectionListener(VideoSnapshotSelectionListener<M> videoSnapshotSelectionListener) {
        if (!this.listeners.contains(videoSnapshotSelectionListener)) {
            this.listeners.add(videoSnapshotSelectionListener);
        }
    }

    public boolean removeVideoSnapshotSelectionListener(VideoSnapshotSelectionListener<M> videoSnapshotSelectionListener) {
        return this.listeners.remove(videoSnapshotSelectionListener);
    }

    private void fireSingleClickSelection() {
        for (VideoSnapshotSelectionListener<M> videoSnapshotSelectionListener : this.listeners) {
            videoSnapshotSelectionListener.videoSnapshotSingleClickSelected(this.momento);
        }
    }

    private void fireDoubleClickSelection() {
        for (VideoSnapshotSelectionListener<M> videoSnapshotSelectionListener : this.listeners) {
            videoSnapshotSelectionListener.videoSnapshotDoubleClickSelected(this.momento);
        }
    }

    static {
        ICON_MAP.put(TargetState.CONNECTED, ResourceLoader.loadImageIcon("Common_led_green.gif"));
        ICON_MAP.put(TargetState.DOWN, ResourceLoader.loadImageIcon("Common_led_yellow.gif"));
        ICON_MAP.put(TargetState.BUSY, ResourceLoader.loadImageIcon("Common_led_red.gif"));
        ICON_MAP.put(TargetState.INACTIVE, ResourceLoader.loadImageIcon("Common_led_white.gif"));
    }

    private class MouseClickHandler
    extends MouseAdapter {
        private MouseClickHandler() {
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 1) {
                VideoSnapshotPanel.this.fireSingleClickSelection();
            } else {
                VideoSnapshotPanel.this.fireDoubleClickSelection();
            }
        }
    }

    private class SizeRestrictedLabel
    extends JLabel {
        private SizeRestrictedLabel() {
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension dimension = super.getPreferredSize();
            return new Dimension(10, dimension.height);
        }

        @Override
        public Dimension getMinimumSize() {
            return this.getPreferredSize();
        }
    }
}

