/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.core.impl.DeviceConnector;
import nn.pp.core.impl.NotificationListenerList;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.ScanSession;
import nn.pp.rccore.scan.ScanSessionEventsListener;
import nn.pp.rccore.scan.impl.ScanSessionEventsListenerList;
import nn.pp.rccore.scan.impl.commands.QuitMessageFromClient;
import nn.pp.rccore.scan.impl.commands.ScanSessionRequest;
import nn.pp.rccore.scan.impl.rsp.RSPHandler;

public class ScanSessionImpl
implements ScanSession {
    private final String host;
    private final int port;
    private final boolean sslMode;
    private final String rdmSessionID;
    private final String ericKey;
    private final String userName;
    private final String password;
    private final String proxyConnectionId;
    private final String proxyUseSSL;
    private final AtomicReference<Object> handleRef = new AtomicReference();
    private final AtomicReference<Enum<State>> state = new AtomicReference<State>(State.NOT_STARTED);
    private static final Logger LOGGER = Logger.getLogger(ScanSessionImpl.class.getName());
    private volatile DeviceConnector deviceConnector;
    private final RSPHandler protoHandler = new RSPHandler();
    private final NotificationListenerList notificationListenerList = new NotificationListenerList();
    private final ScanSessionEventsListenerList scanSessionEventsListenerList = new ScanSessionEventsListenerList();

    public ScanSessionImpl(String string, int n, boolean bl, String string2, String string3, String string4, String string5, String string6, String string7) {
        this.host = string;
        this.port = n;
        this.sslMode = bl;
        this.rdmSessionID = string2;
        this.ericKey = string3;
        this.userName = string4;
        this.password = string5;
        this.proxyConnectionId = string6;
        this.proxyUseSSL = string7;
    }

    @Override
    public void removeNotificationListener(NotificationListener notificationListener) {
        this.notificationListenerList.removeListener(notificationListener);
    }

    @Override
    public void start(String[] stringArray, ScanSessionEventsListener scanSessionEventsListener, NotificationListener notificationListener) throws ScanCoreException, IOException {
        if (!this.state.compareAndSet(State.NOT_STARTED, State.START_CALLED)) {
            return;
        }
        DeviceConnector deviceConnector = new DeviceConnector(Logger.getLogger("nn.pp.rccore.scan"), null);
        deviceConnector.connect(this.host, this.port, this.sslMode);
        if (this.proxyConnectionId != null) {
            deviceConnector.writeCCSGproxyModePrefix(this.proxyConnectionId);
            if ("yes".equalsIgnoreCase(this.proxyUseSSL)) {
                deviceConnector.connectSSLWithSocket(this.host, this.port);
            }
        }
        this.deviceConnector = deviceConnector;
        try {
            this.protoHandler.init(deviceConnector, Logger.getLogger("nn.pp.rccore.scan"));
        }
        catch (IOException iOException) {
            deviceConnector.disconnect();
            throw iOException;
        }
        this.protoHandler.addListener(ScanSessionEventsListener.class, new Ssel());
        this.protoHandler.addListener(NotificationListener.class, new Nl());
        if (notificationListener != null) {
            this.notificationListenerList.addListener(notificationListener);
        }
        if (scanSessionEventsListener != null) {
            this.scanSessionEventsListenerList.addListener(scanSessionEventsListener);
        }
        try {
            this.protoHandler.connect(null, this.userName, this.password, this.ericKey, this.rdmSessionID);
        }
        catch (IOException iOException) {
            deviceConnector.disconnect();
            throw iOException;
        }
        catch (ScanCoreException scanCoreException) {
            deviceConnector.disconnect();
            throw scanCoreException;
        }
        this.protoHandler.executeClientCommands(new ScanSessionRequest(stringArray));
        if (!this.handleRef.compareAndSet(null, new Object())) {
            LOGGER.log(Level.INFO, "Cleaning up");
            this.protoHandler.executeClientCommands(new QuitMessageFromClient());
        }
    }

    @Override
    public void removeScanSessionEventsListener(ScanSessionEventsListener scanSessionEventsListener) {
        this.scanSessionEventsListenerList.removeListener(scanSessionEventsListener);
    }

    @Override
    public void close() {
        Object object;
        Enum<State> enum_ = this.state.getAndSet(State.CLOSE_CALLED);
        if (!(enum_ != State.START_CALLED || (object = this.handleRef.get()) == null && this.handleRef.compareAndSet(null, Boolean.TRUE))) {
            this.protoHandler.executeClientCommands(new QuitMessageFromClient());
        }
    }

    private class Nl
    implements NotificationListener {
        private Nl() {
        }

        @Override
        public void receivedNotification(INotificationEvent iNotificationEvent) {
            if (iNotificationEvent.isQuit() || iNotificationEvent.isError()) {
                LOGGER.log(Level.INFO, "Trace of Quit", new Exception("TRACE FOR DEBUG"));
                LOGGER.log(Level.INFO, "Reason for Quit - " + iNotificationEvent.getMessage());
                ScanSessionImpl.this.protoHandler.close();
                ScanSessionImpl.this.deviceConnector.disconnect();
            }
            ScanSessionImpl.this.notificationListenerList.fireNotification(iNotificationEvent);
        }

        @Override
        public void textNotification(String string) {
        }
    }

    private class Ssel
    implements ScanSessionEventsListener {
        private Ssel() {
        }

        @Override
        public void scanSessionCreated(int n) {
            ScanSessionImpl.this.scanSessionEventsListenerList.firescanSessionCreated(n);
        }

        @Override
        public void disconnected(Exception exception) {
            LOGGER.log(Level.INFO, "disconnect received", exception);
            ScanSessionImpl.this.protoHandler.close();
            ScanSessionImpl.this.deviceConnector.disconnect();
            ScanSessionImpl.this.scanSessionEventsListenerList.fireDisconnected(exception);
        }
    }

    private static enum State {
        NOT_STARTED,
        START_CALLED,
        CLOSE_CALLED;

    }
}

