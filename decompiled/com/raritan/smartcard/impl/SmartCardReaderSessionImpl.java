/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.CardAccessErrorsListener;
import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.SmartCardReaderListener;
import com.raritan.smartcard.SmartCardReaderSession;
import com.raritan.smartcard.SmartCardSessionEventsListener;
import com.raritan.smartcard.impl.CardAccessErrorsListenerList;
import com.raritan.smartcard.impl.CardAccessManager;
import com.raritan.smartcard.impl.CardStateTrackerListenerList;
import com.raritan.smartcard.impl.SmartCardReaderListener16;
import com.raritan.smartcard.impl.SmartCardSessionEventsListenerList;
import com.raritan.smartcard.impl.crp.CRPHandler;
import com.raritan.smartcard.impl.crp.ServerCardEventsListenerImpl;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardTerminal;
import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.core.WorkstationUnlockDetector;
import nn.pp.core.impl.DeviceConnector;
import nn.pp.core.impl.NotificationListenerList;

public class SmartCardReaderSessionImpl
implements SmartCardReaderSession {
    private static final int CLEANUP_WAIT_TIME = 60000;
    private final String cardReaderName;
    private final String host;
    private final int port;
    private final boolean sslMode;
    private final int rfbSessionId;
    private final String rdmSessionID;
    private final String ericKey;
    private final String tag;
    private final CardAccessManager cardAccessManager;
    private final CardStateTrackerListenerList cardStateListenerList;
    private final CardAccessErrorsListenerList cardAccessErrorsListenerList;
    private final NotificationListenerList notificationListenerList;
    private final SmartCardSessionEventsListenerList smartCardSessionEventsListenerList;
    private final Scrl scrl;
    private final Cael cael;
    private final CRPHandler protoHandler;
    private final AtomicReference<Object> handleRef = new AtomicReference();
    private final AtomicReference<Enum<State>> state = new AtomicReference<State>(State.NOT_STARTED);
    private static final Logger LOGGER = Logger.getLogger(SmartCardReaderSessionImpl.class.getName());
    private final int msindex;
    private volatile DeviceConnector deviceConnector;

    public SmartCardReaderSessionImpl(String string, String string2, int n, boolean bl, int n2, int n3, String string3, String string4, String string5, CardAccessManager cardAccessManager) {
        this.cardReaderName = string;
        this.host = string2;
        this.port = n;
        this.sslMode = bl;
        this.rfbSessionId = n2;
        this.msindex = n3;
        this.rdmSessionID = string3;
        this.ericKey = string4;
        this.tag = string5;
        this.cardAccessManager = cardAccessManager;
        this.scrl = new Scrl();
        this.cardStateListenerList = new CardStateTrackerListenerList();
        this.cael = new Cael();
        this.cardAccessErrorsListenerList = new CardAccessErrorsListenerList();
        this.notificationListenerList = new NotificationListenerList();
        this.smartCardSessionEventsListenerList = new SmartCardSessionEventsListenerList();
        this.protoHandler = new CRPHandler();
    }

    @Override
    public void close() {
        Enum<State> enum_ = this.state.getAndSet(State.CLOSE_CALLED);
        if (enum_ == State.START_CALLED) {
            this.close(true);
        }
    }

    private void close(boolean bl) {
        Object object = this.handleRef.get();
        if (object instanceof Boolean) {
            return;
        }
        CardAccessManager.Handle handle = (CardAccessManager.Handle)this.handleRef.get();
        if (handle != null || !this.handleRef.compareAndSet(null, Boolean.TRUE)) {
            handle = (CardAccessManager.Handle)this.handleRef.get();
            this.cardAccessManager.unmountSmartCardReader(handle);
            if (bl) {
                this.startCleanupTimer();
            }
        }
    }

    private void startCleanupTimer() {
        Timer timer = new Timer("Cleanup Timer for " + this.cardReaderName + "-" + this.tag, true);
        timer.schedule((TimerTask)new CleanupTask(timer), 60000L);
    }

    @Override
    public void start(SmartCardReaderListener smartCardReaderListener, CardAccessErrorsListener cardAccessErrorsListener, NotificationListener notificationListener, SmartCardSessionEventsListener smartCardSessionEventsListener, WorkstationUnlockDetector workstationUnlockDetector) throws SmartCardException, IOException {
        CardAccessManager.Handle handle;
        if (!this.state.compareAndSet(State.NOT_STARTED, State.START_CALLED)) {
            return;
        }
        DeviceConnector deviceConnector = new DeviceConnector(Logger.getLogger("com.raritan.smartcard"), null);
        deviceConnector.connect(this.host, this.port, this.sslMode);
        this.deviceConnector = deviceConnector;
        try {
            this.protoHandler.init(deviceConnector, Logger.getLogger("com.raritan.smartcard"));
        }
        catch (IOException iOException) {
            deviceConnector.disconnect();
            throw iOException;
        }
        this.protoHandler.addListener(SmartCardSessionEventsListener.class, new Scsel());
        this.protoHandler.addListener(NotificationListener.class, new Nl());
        if (notificationListener != null) {
            this.notificationListenerList.addListener(notificationListener);
        }
        if (smartCardSessionEventsListener != null) {
            this.smartCardSessionEventsListenerList.addListener(smartCardSessionEventsListener);
        }
        try {
            this.protoHandler.connect(null, null, null, this.ericKey, this.rdmSessionID);
        }
        catch (IOException iOException) {
            deviceConnector.disconnect();
            throw iOException;
        }
        catch (SmartCardException smartCardException) {
            deviceConnector.disconnect();
            throw smartCardException;
        }
        if (smartCardReaderListener != null) {
            this.cardStateListenerList.addListener(smartCardReaderListener);
        }
        if (cardAccessErrorsListener != null) {
            this.cardAccessErrorsListenerList.addListener(cardAccessErrorsListener);
        }
        if (!this.handleRef.compareAndSet(null, handle = this.cardAccessManager.mountSmartCardReader(this.cardReaderName, this.tag, new ServerCardEventsListenerImpl(this.protoHandler, this.rfbSessionId, this.msindex, this.protoHandler), this.scrl, this.cael, this.protoHandler, workstationUnlockDetector))) {
            LOGGER.log(Level.INFO, "Cleaning up");
            this.cardAccessManager.unmountSmartCardReader(handle);
            this.startCleanupTimer();
        }
    }

    @Override
    public void addSmartCardReaderListener(SmartCardReaderListener smartCardReaderListener) {
        if (smartCardReaderListener != null) {
            this.cardStateListenerList.addListener(smartCardReaderListener);
        }
    }

    @Override
    public void removeSmartCardReaderListener(SmartCardReaderListener smartCardReaderListener) {
        this.cardStateListenerList.removeListener(smartCardReaderListener);
    }

    @Override
    public void removeCardAccessErrorsListener(CardAccessErrorsListener cardAccessErrorsListener) {
        this.cardAccessErrorsListenerList.removeListener(cardAccessErrorsListener);
    }

    @Override
    public void removeNotificationListener(NotificationListener notificationListener) {
        this.notificationListenerList.removeListener(notificationListener);
    }

    @Override
    public void removeSmartCardSessionEventsListener(SmartCardSessionEventsListener smartCardSessionEventsListener) {
        this.smartCardSessionEventsListenerList.removeListener(smartCardSessionEventsListener);
    }

    @Override
    public void simulateRemoveAndReinsert() {
        Object object = this.handleRef.get();
        if (object instanceof CardAccessManager.Handle) {
            this.cardAccessManager.simulateRemoveAndReinsert((CardAccessManager.Handle)object);
        }
    }

    private class Scsel
    implements SmartCardSessionEventsListener {
        private Scsel() {
        }

        @Override
        public void cardReaderMounted(String string) {
            SmartCardReaderSessionImpl.this.smartCardSessionEventsListenerList.firecardReaderMounted(string);
        }

        @Override
        public void disconnected(Exception exception) {
            LOGGER.log(Level.INFO, "disconnect received", exception);
            SmartCardReaderSessionImpl.this.close(false);
            SmartCardReaderSessionImpl.this.protoHandler.close();
            SmartCardReaderSessionImpl.this.deviceConnector.disconnect();
            SmartCardReaderSessionImpl.this.smartCardSessionEventsListenerList.fireDisconnected(exception);
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
                SmartCardReaderSessionImpl.this.close(false);
                SmartCardReaderSessionImpl.this.protoHandler.close();
                SmartCardReaderSessionImpl.this.deviceConnector.disconnect();
            }
            SmartCardReaderSessionImpl.this.notificationListenerList.fireNotification(iNotificationEvent);
        }

        @Override
        public void textNotification(String string) {
        }
    }

    private class Cael
    implements CardAccessErrorsListener {
        private Cael() {
        }

        @Override
        public void errorOnAccessingCard(String string) {
            SmartCardReaderSessionImpl.this.cardAccessErrorsListenerList.fireErrorOnAccessingCard(string);
        }

        @Override
        public void noProtocolSupported(String string) {
            SmartCardReaderSessionImpl.this.cardAccessErrorsListenerList.fireNoProtocolSupported(string);
        }
    }

    private class Scrl
    implements SmartCardReaderListener16 {
        private Scrl() {
        }

        @Override
        public void cardInserted(CardTerminal cardTerminal) {
            SmartCardReaderSessionImpl.this.cardStateListenerList.fireCardInserted(SmartCardReaderSessionImpl.this.cardReaderName);
        }

        @Override
        public void cardReaderRemoved(String string) {
            SmartCardReaderSessionImpl.this.cardStateListenerList.fireCardReaderRemoved(string);
        }

        @Override
        public void cardRemoved(CardTerminal cardTerminal) {
            SmartCardReaderSessionImpl.this.cardStateListenerList.fireCardRemoved(SmartCardReaderSessionImpl.this.cardReaderName);
        }
    }

    private class CleanupTask
    extends TimerTask {
        private final Timer cleanupTimer;

        public CleanupTask(Timer timer) {
            this.cleanupTimer = timer;
        }

        @Override
        public void run() {
            LOGGER.log(Level.INFO, "Cleaning up");
            SmartCardReaderSessionImpl.this.deviceConnector.disconnect();
            this.cleanupTimer.cancel();
        }
    }

    private static enum State {
        NOT_STARTED,
        START_CALLED,
        CLOSE_CALLED;

    }
}

