/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.Timer;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.common.scan.ScanEventListener;
import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.core.T;
import nn.pp.ext.devPref.DevicePrefs;
import nn.pp.logging.RemoteConsoleLogger;
import nn.pp.rccore.RCAdapter;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.RCCoreFactory;
import nn.pp.rccore.RCException;

public class ScanConnector {
    private static final Dimension SCALE_DIMENSION = new Dimension(320, 240);
    private int scanInterval = 10000;
    private int switchInterval = 10000;
    private final Timer scanIntervalTimer;
    private final Timer switchIntervalTimer;
    private final List<RemoteConsoleParameters> remoteConsoleParamsList;
    private Iterator<RemoteConsoleParameters> iterator;
    private RemoteConsoleParameters currentRemoteConsoleParameter;
    private final Map<String, Integer> remoteConsoleParamsIndexes = new HashMap<String, Integer>();
    private final List<ScanEventListener> listeners = new ArrayList<ScanEventListener>();
    private RCCore rccore;
    private boolean paused = true;
    private static final Logger LOGGER = Logger.getLogger(ScanConnector.class.getName());
    private Random switchIntervalRandomizer = new Random();
    private int scanReferralId = -1;

    public ScanConnector(List<RemoteConsoleParameters> list) {
        this.scanIntervalTimer = new Timer(this.scanInterval, new ScanIntervalExpiredAction());
        this.switchIntervalTimer = new Timer(this.switchInterval, new SwitchIntervalTimerExpired());
        this.scanIntervalTimer.setRepeats(false);
        this.switchIntervalTimer.setRepeats(false);
        this.remoteConsoleParamsList = Collections.unmodifiableList(list);
        for (int i = 0; i < list.size(); ++i) {
            this.remoteConsoleParamsIndexes.put(list.get((int)i).targetPortId, i);
        }
        assert (!list.isEmpty());
        this.iterator = this.remoteConsoleParamsList.iterator();
    }

    public int getScanInterval() {
        return this.scanInterval;
    }

    public void setScanInterval(int n) {
        this.scanInterval = n;
        this.scanIntervalTimer.setInitialDelay(n);
    }

    public int getSwitchInterval() {
        return this.switchInterval;
    }

    public void setSwitchInterval(int n) {
        this.switchInterval = n;
        this.switchIntervalRandomizer = new Random();
    }

    public void pause() {
        if (!this.paused) {
            this.paused = true;
            if (this.rccore != null) {
                if (this.scanIntervalTimer.isRunning()) {
                    this.fireDisplayVideoEnd(this.getCurrentRemoteConsoleParameter().targetPortId, this.rccore.getSnapshot());
                } else {
                    this.fireDisplayVideoEnd(this.getCurrentRemoteConsoleParameter().targetPortId, null);
                }
                this.cleanupRcCore();
            }
            this.scanIntervalTimer.stop();
            this.switchIntervalTimer.stop();
        }
    }

    public void resume() {
        if (this.paused) {
            this.paused = false;
            this.createAndConnectRcCore();
        }
    }

    public void resumeAfter(String string) {
        Integer n;
        if (this.paused && (n = this.remoteConsoleParamsIndexes.get(string)) != null) {
            Integer n2 = n;
            Integer n3 = n = Integer.valueOf(n + 1);
            if (n > this.remoteConsoleParamsList.size()) {
                n = 0;
            }
            this.seekPort(n);
            this.resume();
        }
    }

    public void resumeAt(String string) {
        Integer n;
        if (this.paused && (n = this.remoteConsoleParamsIndexes.get(string)) != null) {
            this.seekPort(n);
            this.resume();
        }
    }

    public void stop() {
        this.pause();
    }

    public void start(int n) {
        this.scanReferralId = n;
        this.resume();
    }

    private RemoteConsoleParameters getNextRemoteConsoleParameter() {
        if (!this.iterator.hasNext()) {
            this.iterator = this.remoteConsoleParamsList.iterator();
        }
        this.currentRemoteConsoleParameter = this.iterator.next();
        return this.getCurrentRemoteConsoleParameter();
    }

    private RemoteConsoleParameters getCurrentRemoteConsoleParameter() {
        return this.currentRemoteConsoleParameter;
    }

    private void seekPort(int n) {
        this.iterator = this.remoteConsoleParamsList.listIterator(n);
    }

    private void createAndConnectRcCore() {
        block7: {
            assert (this.rccore == null);
            RemoteConsoleParameters remoteConsoleParameters = this.getNextRemoteConsoleParameter();
            this.rccore = RCCoreFactory.loadGraphicalReadOnlyRCCore(RemoteConsoleLogger.getInstance().getLogger());
            this.rccore.setScaleToFit(true, true, SCALE_DIMENSION);
            this.setConnectionParams(remoteConsoleParameters);
            this.rccore.addNotificationListener(new VideoStateHandler(this.rccore));
            this.rccore.addNotificationListener(new NotificationHandler(this.rccore));
            ConnectionEventHandler connectionEventHandler = new ConnectionEventHandler(this.rccore);
            this.rccore.addConnectionEventListener(connectionEventHandler, 4);
            this.rccore.addVideoEventListener(connectionEventHandler, 32);
            this.fireVideoComponentAvailable(remoteConsoleParameters.targetPortId, this.rccore.getRCJComponent());
            try {
                if (remoteConsoleParameters.username != null && remoteConsoleParameters.password != null) {
                    this.rccore.connectRCWithUserLogin(remoteConsoleParameters.getURLCompatibleRemoteHost(), remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.targetPortId, remoteConsoleParameters.username, remoteConsoleParameters.password);
                    break block7;
                }
                if (remoteConsoleParameters.ericKey != null) {
                    this.rccore.connectRCWithEricKey(remoteConsoleParameters.getURLCompatibleRemoteHost(), remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.targetPortId, remoteConsoleParameters.ericKey);
                    break block7;
                }
                if (remoteConsoleParameters.rdmSession != null) {
                    this.rccore.connectRCWithRdmSession(remoteConsoleParameters.getURLCompatibleRemoteHost(), remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.targetPortId, remoteConsoleParameters.rdmSession, remoteConsoleParameters.proxyConnectionId, remoteConsoleParameters.proxyUseSSL);
                    break block7;
                }
                throw new RCException(T._("Authentication parameters missing!"));
            }
            catch (IOException iOException) {
                LOGGER.log(Level.SEVERE, "Exception on connection");
                this.abortScan();
            }
            catch (RCException rCException) {
                LOGGER.log(Level.SEVERE, "Exception on connection");
                this.abortScan();
            }
        }
    }

    private void setConnectionParams(RemoteConsoleParameters remoteConsoleParameters) {
        DevicePrefs devicePrefs = DevicePrefs.getNode(remoteConsoleParameters.host);
        if (devicePrefs != null) {
            boolean bl = false;
            if (devicePrefs.getG2ConnectionSpeed() == null && devicePrefs.getG2ColorDepth() != null && devicePrefs.getG2Smoothing() != null) {
                bl = true;
            }
            this.rccore.setConnectionProperties(bl, devicePrefs.getG2ConnectionSpeed(), devicePrefs.getG2ColorDepth(), devicePrefs.getG2Smoothing());
        }
        HashMap<RCCore.ClientSessionInitProperties, String> hashMap = new HashMap<RCCore.ClientSessionInitProperties, String>();
        hashMap.put(RCCore.ClientSessionInitProperties.SCAN_REFEENCE_ID, this.scanReferralId + "");
        this.rccore.setClientSessionInitProperties(hashMap);
    }

    private void abortScan() {
        this.cleanup();
        this.fireScanAbortedOnConnectError();
    }

    private void abortScan(INotificationEvent iNotificationEvent) {
        this.cleanup();
        this.fireScanAborted(iNotificationEvent);
    }

    private void cleanup() {
        this.scanIntervalTimer.stop();
        this.switchIntervalTimer.stop();
        this.cleanupRcCore();
    }

    private void cleanupRcCore() {
        if (this.rccore != null) {
            this.rccore.disconnect();
            this.rccore.dispose();
            this.rccore = null;
        }
    }

    public boolean addScanEventListener(ScanEventListener scanEventListener) {
        return this.addScanEventListener_0(scanEventListener);
    }

    private boolean addScanEventListener_0(ScanEventListener scanEventListener) {
        if (!this.listeners.contains(scanEventListener)) {
            return this.listeners.add(scanEventListener);
        }
        return false;
    }

    public boolean removeScanEventListener(ScanEventListener scanEventListener) {
        return this.listeners.remove(scanEventListener);
    }

    private void fireVideoComponentAvailable(String string, JComponent jComponent) {
        for (ScanEventListener scanEventListener : this.listeners) {
            scanEventListener.videoComponentAvailable(string, jComponent);
        }
    }

    private void fireDisplayVideoStart(String string, boolean bl) {
        for (ScanEventListener scanEventListener : this.listeners) {
            scanEventListener.displayVideoStart(string, bl);
        }
    }

    private void fireDisplayVideoEnd(String string, Image image) {
        for (ScanEventListener scanEventListener : this.listeners) {
            scanEventListener.displayVideoEnd(string, image);
        }
    }

    private void fireDisplayVideoError(String string, int n) {
        for (ScanEventListener scanEventListener : this.listeners) {
            scanEventListener.displayVideoError(string, n);
        }
    }

    private void fireDisplayVideoCommunicationError(String string) {
        for (ScanEventListener scanEventListener : this.listeners) {
            scanEventListener.displayVideoCommunicationError(string);
        }
    }

    private void fireScanAborted(INotificationEvent iNotificationEvent) {
        for (ScanEventListener scanEventListener : this.listeners) {
            scanEventListener.scanAborted(iNotificationEvent);
        }
    }

    private void fireScanAbortedOnConnectError() {
        for (ScanEventListener scanEventListener : this.listeners) {
            scanEventListener.scanAbortedOnConnectError();
        }
    }

    private void fireOsdMessageReceived(String string, String string2) {
        for (ScanEventListener scanEventListener : this.listeners) {
            scanEventListener.osdMessageReceived(string, string2);
        }
    }

    private void restartSwitchIntervalTimer(int n) {
        this.switchIntervalTimer.setInitialDelay(n);
        this.switchIntervalTimer.restart();
    }

    private class ConnectionEventHandler
    extends RCAdapter {
        private final RCCore owningRccore;

        public ConnectionEventHandler(RCCore rCCore) {
            this.owningRccore = rCCore;
        }

        @Override
        public void disconnected(Exception exception) {
            if (ScanConnector.this.rccore == this.owningRccore) {
                LOGGER.log(Level.INFO, "communication error, continuing scan", exception);
                ScanConnector.this.fireDisplayVideoCommunicationError(((ScanConnector)ScanConnector.this).getCurrentRemoteConsoleParameter().targetPortId);
                ScanConnector.this.cleanupRcCore();
                ScanConnector.this.scanIntervalTimer.stop();
                assert (!ScanConnector.this.switchIntervalTimer.isRunning());
                ScanConnector.this.restartSwitchIntervalTimer(ScanConnector.this.switchIntervalRandomizer.nextInt(ScanConnector.this.switchInterval + 1));
            }
        }

        @Override
        public void osdMessageReceived(String string, boolean bl) {
            if (ScanConnector.this.rccore == this.owningRccore) {
                ScanConnector.this.fireOsdMessageReceived(((ScanConnector)ScanConnector.this).getCurrentRemoteConsoleParameter().targetPortId, string);
            }
        }
    }

    private class SwitchIntervalTimerExpired
    implements ActionListener {
        private SwitchIntervalTimerExpired() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ScanConnector.this.switchIntervalTimer.stop();
            ScanConnector.this.createAndConnectRcCore();
        }
    }

    private class ScanIntervalExpiredAction
    implements ActionListener {
        private ScanIntervalExpiredAction() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ScanConnector.this.scanIntervalTimer.stop();
            ScanConnector.this.fireDisplayVideoEnd(((ScanConnector)ScanConnector.this).getCurrentRemoteConsoleParameter().targetPortId, ScanConnector.this.rccore.getSnapshot());
            ScanConnector.this.cleanupRcCore();
            assert (!ScanConnector.this.switchIntervalTimer.isRunning());
            ScanConnector.this.restartSwitchIntervalTimer(ScanConnector.this.switchInterval);
        }
    }

    private class NotificationHandler
    implements NotificationListener {
        private final RCCore owningRccore;

        public NotificationHandler(RCCore rCCore) {
            this.owningRccore = rCCore;
        }

        @Override
        public void receivedNotification(INotificationEvent iNotificationEvent) {
            if (ScanConnector.this.rccore == this.owningRccore) {
                switch (iNotificationEvent.getErrorCode()) {
                    case 0x10000001: 
                    case 0x10020001: 
                    case 0x10020002: 
                    case 268566531: 
                    case 268566532: 
                    case 268566533: 
                    case 268566534: 
                    case 0x10040001: 
                    case 0x10040004: {
                        LOGGER.log(Level.INFO, "Nofication received, continuing scan : " + iNotificationEvent.getErrorCode());
                        ScanConnector.this.fireDisplayVideoError(((ScanConnector)ScanConnector.this).getCurrentRemoteConsoleParameter().targetPortId, iNotificationEvent.getErrorCode());
                        ScanConnector.this.cleanupRcCore();
                        assert (!ScanConnector.this.scanIntervalTimer.isRunning());
                        assert (!ScanConnector.this.switchIntervalTimer.isRunning());
                        ScanConnector.this.restartSwitchIntervalTimer(ScanConnector.this.switchIntervalRandomizer.nextInt(ScanConnector.this.switchInterval + 1));
                        break;
                    }
                    case 0x10000003: 
                    case 268697602: 
                    case 268697603: 
                    case 0x10060001: 
                    case 302383106: {
                        LOGGER.log(Level.INFO, "Nofication received, aborting scan : " + iNotificationEvent.getErrorCode());
                        ScanConnector.this.abortScan(iNotificationEvent);
                        break;
                    }
                }
            }
        }

        @Override
        public void textNotification(String string) {
        }
    }

    private class VideoStateHandler
    implements NotificationListener {
        private final RCCore owningRccore;

        public VideoStateHandler(RCCore rCCore) {
            this.owningRccore = rCCore;
        }

        @Override
        public void receivedNotification(INotificationEvent iNotificationEvent) {
            if (ScanConnector.this.rccore == this.owningRccore && iNotificationEvent.getErrorCode() == 302120971) {
                ScanConnector.this.fireDisplayVideoStart(((ScanConnector)ScanConnector.this).getCurrentRemoteConsoleParameter().targetPortId, false);
                assert (!ScanConnector.this.scanIntervalTimer.isRunning());
                ScanConnector.this.scanIntervalTimer.restart();
                ScanConnector.this.rccore.removeNotificationListener(this);
            }
        }

        @Override
        public void textNotification(String string) {
        }
    }
}

