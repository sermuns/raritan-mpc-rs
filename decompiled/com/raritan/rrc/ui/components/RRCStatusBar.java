/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.ui.models.StatusEvent;
import com.raritan.rrc.ui.panes.KvmView;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.rrc.util.NameValuePair;
import com.raritan.tools.commands.Command;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.components.MultyObserverComponentInterface;
import com.raritan.tools.ui.panes.RaritanStatusBarPanel;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import nn.pp.audiocore.AudioEventListener;
import nn.pp.common.ResourceLoader;

public class RRCStatusBar
extends RaritanStatusBarPanel
implements MultyObserverComponentInterface {
    private final HashMap observables = new HashMap();
    public static final int STATUS_NOT_SECURE = 0;
    public static final int STATUS_SECURE = 1;
    private static final long serialVersionUID = 1L;
    private static final int PROGRESS_BAR_MAXIMUM = 100;
    private RaritanPropertyResourceBundle bundle;
    private boolean capsLockOn = false;
    private boolean numLockOn = false;
    private boolean scrollLockOn = false;
    private int concurrentUsers = 0;
    private JLabel videoSensingLabel;
    private JLabel bandwidthUsageLabel;
    private JLabel securityLabel;
    private JPanel audioStatePanel;
    private JLabel playbackStatus;
    private JLabel captureStatus;
    private Timer playbackTimer;
    private Timer captureTimer;
    private JLabel concurrentConnectionsLabel;
    private JLabel capsLockLabel;
    private JLabel numLockLabel;
    private JLabel scrollLockLabel;
    private JProgressBar rfpOperationProgreessBar;
    private CommandButton cancelRFPOperationButton;
    private JPanel progressPanel;
    private boolean isKX = false;
    private KXTimer kxTimer = null;
    private int playbackCount = 0;
    private int captureCount = 0;

    public RRCStatusBar(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.enableEvents(34334L);
    }

    public void initRFPProgressComponents(boolean bl) {
        this.isKX = bl;
        this.kxTimer = bl ? new KXTimer() : null;
        this.rfpOperationProgreessBar.setMaximum(100);
        this.progressPanel.setVisible(true);
    }

    public void setRFPOperationProgress(int n) {
        if (!this.isKX) {
            this.rfpOperationProgreessBar.setValue(n);
        }
    }

    public void hideRFPProgressComponents() {
        if (this.kxTimer != null) {
            this.kxTimer.Stop();
            this.kxTimer = null;
        }
        this.rfpOperationProgreessBar.setValue(0);
        this.progressPanel.setVisible(false);
    }

    @Override
    public void makeLayout() {
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.label = new JLabel();
        this.videoSensingLabel = new JLabel(" ");
        this.videoSensingLabel.setBorder(BorderFactory.createBevelBorder(1));
        this.bandwidthUsageLabel = new JLabel(" ");
        this.bandwidthUsageLabel.setBorder(BorderFactory.createBevelBorder(1));
        this.securityLabel = new JLabel(" ");
        this.securityLabel.setBorder(BorderFactory.createBevelBorder(1));
        this.audioStatePanel = new JPanel(new GridLayout(1, 2));
        this.audioStatePanel.setBorder(BorderFactory.createBevelBorder(1));
        this.playbackStatus = new JLabel((Icon)null, 0);
        this.captureStatus = new JLabel((Icon)null, 0);
        this.audioStatePanel.add(this.playbackStatus);
        this.audioStatePanel.add(this.captureStatus);
        this.concurrentConnectionsLabel = new JLabel(" ");
        this.concurrentConnectionsLabel.setBorder(BorderFactory.createBevelBorder(1));
        this.capsLockLabel = new JLabel(" ");
        this.capsLockLabel.setBorder(BorderFactory.createBevelBorder(1));
        this.numLockLabel = new JLabel(" ");
        this.numLockLabel.setBorder(BorderFactory.createBevelBorder(1));
        this.scrollLockLabel = new JLabel(" ");
        this.scrollLockLabel.setBorder(BorderFactory.createBevelBorder(1));
        this.setLayout(new GridLayout(1, 2));
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add((Component)this.label, "Before");
        this.progressPanel = new JPanel(new FlowLayout());
        this.rfpOperationProgreessBar = new JProgressBar();
        this.cancelRFPOperationButton = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancelRFPOperationButton.setEnabled(false);
        this.progressPanel.add(this.rfpOperationProgreessBar);
        this.progressPanel.setVisible(false);
        jPanel.add((Component)this.progressPanel, "After");
        this.add(jPanel);
        JPanel jPanel2 = new JPanel(new GridLayout(1, 2));
        jPanel2.add(this.videoSensingLabel);
        JPanel jPanel3 = new JPanel(new GridLayout(1, 6));
        jPanel3.add(this.bandwidthUsageLabel);
        jPanel3.add(this.securityLabel);
        jPanel3.add(this.audioStatePanel);
        jPanel3.add(this.concurrentConnectionsLabel);
        jPanel3.add(this.capsLockLabel);
        jPanel3.add(this.numLockLabel);
        jPanel3.add(this.scrollLockLabel);
        jPanel2.add(jPanel3);
        this.add(jPanel2);
        this.playbackTimer = new Timer(500, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                RRCStatusBar.this.togglePlaybackIcon();
            }
        });
        this.captureTimer = new Timer(500, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                RRCStatusBar.this.toggleCaptureIcon();
            }
        });
    }

    public void setCapsLockStatus(boolean bl) {
        if (bl) {
            this.capsLockLabel.setText(this.bundle.getString("StatusBar.capsLock.label"));
        } else {
            this.capsLockLabel.setText(" ");
        }
        this.capsLockOn = bl;
    }

    public void setNumLockStatus(boolean bl) {
        if (bl) {
            this.numLockLabel.setText(this.bundle.getString("StatusBar.numLock.label"));
        } else {
            this.numLockLabel.setText(" ");
        }
        this.numLockOn = bl;
    }

    public void setScrollsLockStatus(boolean bl) {
        if (bl) {
            this.scrollLockLabel.setText(this.bundle.getString("StatusBar.scrollLock.label"));
        } else {
            this.scrollLockLabel.setText(" ");
        }
        this.scrollLockOn = bl;
    }

    public void setLEDState(boolean bl, boolean bl2, boolean bl3) {
        this.setScrollsLockStatus(bl);
        this.setNumLockStatus(bl2);
        this.setCapsLockStatus(bl3);
    }

    public void clearStatusBarLabels() {
        this.videoSensingLabel.setText(" ");
        this.bandwidthUsageLabel.setIcon(null);
        this.securityLabel.setIcon(null);
        this.playbackStatus.setIcon(null);
        this.playbackStatus.setToolTipText(null);
        this.captureStatus.setIcon(null);
        this.captureStatus.setToolTipText(null);
        this.concurrentConnectionsLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("Empty.image")));
        this.capsLockLabel.setText(" ");
        this.capsLockOn = false;
        this.numLockLabel.setText(" ");
        this.numLockOn = false;
        this.scrollLockLabel.setText(" ");
        this.scrollLockOn = false;
        this.playbackTimer.stop();
        this.captureTimer.stop();
    }

    public void initKVMStatusBarLabels(boolean bl, boolean bl2, boolean bl3) {
        this.setBandwidthUsage(0);
        this.securityLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("lockClosed.image")));
        this.setConcurrentUsers(this.concurrentUsers);
        this.setLEDState(bl3, bl2, bl);
        this.setPlaybackStatus(AudioEventListener.DeviceState.DISCONNECTED);
        this.setCaptureStatus(AudioEventListener.DeviceState.DISCONNECTED);
    }

    public void showAutoSense(int n) {
        if (n == -1) {
            this.videoSensingLabel.setText(" ");
        } else {
            this.videoSensingLabel.setText(this.bundle.getString("StatusBar.autoSensingVideo.label") + " (" + n + ")");
        }
    }

    public void setConcurrentUsers(int n) {
        this.concurrentUsers = n;
        if (n > 1) {
            this.concurrentConnectionsLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("multipleUsers.image")));
        } else if (n == 1) {
            this.concurrentConnectionsLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("singleUser.image")));
        } else {
            this.concurrentConnectionsLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("Empty.image")));
        }
    }

    public void setBandwidthUsage(int n) {
        switch (n) {
            case 0: {
                this.bandwidthUsageLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("bandwidthScale0.image")));
                break;
            }
            case 1: {
                this.bandwidthUsageLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("bandwidthScale1.image")));
                break;
            }
            case 2: {
                this.bandwidthUsageLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("bandwidthScale2.image")));
                break;
            }
            case 3: {
                this.bandwidthUsageLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("bandwidthScale3.image")));
                break;
            }
            case 4: {
                this.bandwidthUsageLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("bandwidthScale4.image")));
                break;
            }
            case 5: {
                this.bandwidthUsageLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("bandwidthScale5.image")));
                break;
            }
            case 6: {
                this.bandwidthUsageLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("bandwidthScale6.image")));
                break;
            }
            case 7: {
                this.bandwidthUsageLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("bandwidthScale7.image")));
                break;
            }
            case 8: {
                this.bandwidthUsageLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("bandwidthScale8.image")));
                break;
            }
        }
    }

    public void setVideoSensingLabel(String string) {
        this.videoSensingLabel.setText(string);
    }

    public void setCancelRFPOperationCommand(Command command) {
        this.cancelRFPOperationButton.addActionListener(this);
        this.cancelRFPOperationButton.setCommand(command);
        this.cancelRFPOperationButton.setEnabled(false);
    }

    public void setSecurityLabel(int n) {
        if (n == 1) {
            this.securityLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("lockClosed.image")));
        } else if (n == 0) {
            this.securityLabel.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("lockOpen.image")));
        }
    }

    public void setPlaybackStatus(AudioEventListener.DeviceState deviceState) {
        switch (deviceState) {
            case DISCONNECTED: {
                this.playbackTimer.stop();
                this.playbackStatus.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("speakerDisabled.image")));
                this.playbackStatus.setToolTipText(this.bundle.getString("speakerDisabled.tooltip"));
                break;
            }
            case MUTED: {
                this.playbackTimer.stop();
                this.playbackStatus.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("speakerOff.image")));
                this.playbackStatus.setToolTipText(this.bundle.getString("speakerOff.tooltip"));
                break;
            }
            case PLAYING: {
                this.playbackTimer.start();
                this.playbackStatus.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("speakerOn.image")));
                this.playbackStatus.setToolTipText(this.bundle.getString("speakerOn.tooltip"));
            }
        }
    }

    public void setCaptureStatus(AudioEventListener.DeviceState deviceState) {
        switch (deviceState) {
            case DISCONNECTED: {
                this.captureTimer.stop();
                this.captureStatus.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("micDisabled.image")));
                this.captureStatus.setToolTipText(this.bundle.getString("micDisabled.tooltip"));
                break;
            }
            case MUTED: {
                this.captureTimer.stop();
                this.captureStatus.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("micOff.image")));
                this.captureStatus.setToolTipText(this.bundle.getString("micOff.tooltip"));
                break;
            }
            case PLAYING: {
                this.captureTimer.start();
                this.captureStatus.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString("micOn.image")));
                this.captureStatus.setToolTipText(this.bundle.getString("micOn.tooltip"));
            }
        }
    }

    private void togglePlaybackIcon() {
        this.playbackStatus.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(this.playbackCount == 0 ? "speakerOn2.image" : "speakerOn.image")));
        if (++this.playbackCount > 1) {
            this.playbackCount = 0;
        }
    }

    private void toggleCaptureIcon() {
        this.captureStatus.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(this.captureCount == 0 ? "micOn2.image" : "micOn.image")));
        if (++this.captureCount > 1) {
            this.captureCount = 0;
        }
    }

    @Override
    protected void processEvent(AWTEvent aWTEvent) {
        if (aWTEvent.getID() == 34334) {
            Object object = ((StatusEvent)aWTEvent).getMessage();
            if (this.isKX && object instanceof NameValuePair) {
                if (!this.isKX) {
                    NameValuePair nameValuePair = (NameValuePair)object;
                    Point point = (Point)nameValuePair.getValue();
                    int n = (int)((double)point.x / (double)point.y * 100.0);
                    this.rfpOperationProgreessBar.setValue(n);
                    this.validate();
                }
                return;
            }
        } else {
            super.processEvent(aWTEvent);
        }
    }

    public void setTextLabel(String string) {
        this.label.setText(string);
    }

    @Override
    public void addObservable(Observable observable) {
        this.observables.put(this.hashCode() + "", this);
        observable.addObserver(this);
    }

    @Override
    public void clearObservables() {
        if (this.observables != null && this.observables.size() > 0) {
            for (Observable observable : this.observables.values()) {
                observable.deleteObserver(this);
            }
            this.observables.clear();
        }
    }

    @Override
    public List getObservables() {
        return new ArrayList(this.observables.values());
    }

    @Override
    public void removeObservable(Observable observable) {
        this.observables.remove(this.hashCode() + "");
        observable.deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object object) {
        if (object instanceof ArrayList) {
            Object e = ((ArrayList)object).get(0);
            if (e instanceof KvmPort) {
                KvmPort kvmPort = (KvmPort)e;
                if (kvmPort.isConnected()) {
                    if (kvmPort.getDeviceView() instanceof KvmView) {
                        this.setConcurrentUsers(kvmPort.getConcurrUsers());
                    } else if (kvmPort.getDeviceView() instanceof RFBView) {
                        this.setConcurrentUsers(kvmPort.getConcurrUsers());
                        RFBView rFBView = (RFBView)kvmPort.getView();
                        if (rFBView != null) {
                            this.setCapsLockStatus(rFBView.isCapsLockOn());
                            this.setNumLockStatus(rFBView.isNumLockOn());
                            this.setScrollsLockStatus(rFBView.isScrollLockOn());
                            this.setPlaybackStatus(rFBView.getPlaybackState());
                            this.setCaptureStatus(rFBView.getCaptureState());
                        }
                    }
                } else {
                    this.clearStatusBarLabels();
                }
            } else {
                this.clearStatusBarLabels();
            }
        }
    }

    private class KXTimer
    implements ActionListener {
        private Timer timer = new Timer(100, this);

        public KXTimer() {
            this.timer.setRepeats(true);
            this.timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int n = RRCStatusBar.this.rfpOperationProgreessBar.getValue() + 1;
            if (n > RRCStatusBar.this.rfpOperationProgreessBar.getMaximum()) {
                n = RRCStatusBar.this.rfpOperationProgreessBar.getMinimum();
            }
            RRCStatusBar.this.rfpOperationProgreessBar.setValue(n);
        }

        public void Stop() {
            if (this.timer != null) {
                this.timer.stop();
            }
        }
    }
}

