/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.SmartCardSessionEventsListener;
import com.raritan.smartcard.impl.CardTransmitHandler;
import com.raritan.smartcard.impl.EventsListenerManager;
import com.raritan.smartcard.impl.commands.AuthHttpIDChallResponse;
import com.raritan.smartcard.impl.commands.AuthHttpIDGetChallenge;
import com.raritan.smartcard.impl.commands.AuthRDMRequest;
import com.raritan.smartcard.impl.commands.CardInsertedMessage;
import com.raritan.smartcard.impl.commands.CardRemovedMessage;
import com.raritan.smartcard.impl.commands.ClientCommandsExecutor;
import com.raritan.smartcard.impl.commands.CommandVisitorV01_00;
import com.raritan.smartcard.impl.commands.MountCardReaderRequest;
import com.raritan.smartcard.impl.commands.PongResponse;
import com.raritan.smartcard.impl.commands.QuitMessageFromClient;
import com.raritan.smartcard.impl.commands.ResponseAPDU;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import com.raritan.smartcard.impl.crp.SmartCardNotificationEvent;
import com.raritan.smartcard.impl.crp.V01_00.AuthHttpIDChallenge;
import com.raritan.smartcard.impl.crp.V01_00.AuthResponse;
import com.raritan.smartcard.impl.crp.V01_00.CRPMessageHandlerV01_00;
import com.raritan.smartcard.impl.crp.V01_00.CommandAPDURequest;
import com.raritan.smartcard.impl.crp.V01_00.CommandToMessageWriterVisitor;
import com.raritan.smartcard.impl.crp.V01_00.MountCardReaderResponse;
import com.raritan.smartcard.impl.crp.V01_00.PingRequest;
import com.raritan.smartcard.impl.crp.V01_00.ProtoMessages;
import com.raritan.smartcard.impl.crp.V01_00.ProtoStateMachine;
import com.raritan.smartcard.impl.crp.V01_00.ProtoStates;
import com.raritan.smartcard.impl.crp.V01_00.QuitRequestFromServer;
import java.io.IOException;
import nn.pp.core.NotificationListener;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;

public class CRPMessageHandlerV01_00Impl
implements CRPMessageHandlerV01_00,
CommandVisitorV01_00 {
    private volatile ClientCommandsExecutor clientCommandsExecutor;
    private volatile EventsListenerManager eventsListenerManager;
    private volatile MonitoringDataOutputStream os;
    private volatile MonitoringDataInputStream is;
    private final CommandToMessageWriterVisitor messageWriter = new CommandToMessageWriterVisitor();
    private int capduSeqNum;
    private final ProtoStateMachine stateMachine = new ProtoStateMachine();

    @Override
    public void handleAuthResponse(AuthResponse authResponse) {
        if (this.handleMessageReading(ProtoMessages.AUTH_RESPONSE, authResponse.getAck() != 0) && authResponse.getAck() == 0) {
            this.setQuitState();
            for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new SmartCardNotificationEvent(authResponse.getReason()));
            }
        }
    }

    @Override
    public void handleMountCardReaderResponse(MountCardReaderResponse mountCardReaderResponse) {
        block4: {
            if (!this.handleMessageReading(ProtoMessages.MOUNT_CARD_READER_RESPONSE, mountCardReaderResponse.getAck() != 0)) break block4;
            if (mountCardReaderResponse.getAck() == 0) {
                this.setQuitState();
                for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                    notificationListener.receivedNotification(new SmartCardNotificationEvent(mountCardReaderResponse.getReason()));
                }
            } else {
                for (SmartCardSessionEventsListener smartCardSessionEventsListener : this.eventsListenerManager.getListeners(SmartCardSessionEventsListener.class)) {
                    smartCardSessionEventsListener.cardReaderMounted("");
                }
            }
        }
    }

    @Override
    public void handlePingRequest(PingRequest pingRequest) {
        if (this.handleMessageReading(ProtoMessages.PING, true)) {
            this.clientCommandsExecutor.executeClientCommands(new PongResponse());
        }
    }

    @Override
    public void handleQuitRequestFromServer(QuitRequestFromServer quitRequestFromServer) {
        if (this.handleMessageReading(ProtoMessages.QUIT_RESP, true)) {
            for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new SmartCardNotificationEvent(1, quitRequestFromServer.getReason()));
            }
        }
    }

    @Override
    public void handleCommandAPDU(CommandAPDURequest commandAPDURequest) {
        if (this.handleMessageReading(ProtoMessages.CAPDU, true)) {
            this.setCapduSeqNum(commandAPDURequest.getSequenceNum());
            for (CardTransmitHandler cardTransmitHandler : this.eventsListenerManager.getListeners(CardTransmitHandler.class)) {
                cardTransmitHandler.transmit(commandAPDURequest.getCapdu());
            }
        }
    }

    void setClientCommandsExecutor(ClientCommandsExecutor clientCommandsExecutor) {
        this.clientCommandsExecutor = clientCommandsExecutor;
    }

    void setOs(MonitoringDataOutputStream monitoringDataOutputStream) {
        this.os = monitoringDataOutputStream;
    }

    void setIs(MonitoringDataInputStream monitoringDataInputStream) {
        this.is = monitoringDataInputStream;
    }

    @Override
    public void handleAuthRDMRequest(AuthRDMRequest authRDMRequest) throws IOException, SmartCardException {
        this.handleMessageWriting(authRDMRequest, ProtoMessages.AUTH_REQUEST);
    }

    @Override
    public void handleCardInsertedMessage(CardInsertedMessage cardInsertedMessage) throws IOException, SmartCardException {
        this.handleWritingWithWait(cardInsertedMessage, ProtoMessages.CARD_INSERTED, ProtoStates.AUTHENTICATED, ProtoStates.CARD_READER_MOUNTED);
    }

    @Override
    public void handleCardRemovedMessage(CardRemovedMessage cardRemovedMessage) throws IOException, SmartCardException {
        this.handleMessageWriting(cardRemovedMessage, ProtoMessages.CARD_REMOVED);
    }

    @Override
    public void handleHttpIDChallengeResponse(AuthHttpIDChallResponse authHttpIDChallResponse) throws IOException, SmartCardException {
        this.handleMessageWriting(authHttpIDChallResponse, ProtoMessages.AUTH_REQUEST);
    }

    @Override
    public void handleHttpIDGetChallenge(AuthHttpIDGetChallenge authHttpIDGetChallenge) throws IOException, SmartCardException {
        if (this.handleMessageWriting(authHttpIDGetChallenge, ProtoMessages.AUTH_REQUEST)) {
            AuthHttpIDChallenge authHttpIDChallenge = new AuthHttpIDChallenge().readMessage(this.is);
            byte[] byArray = authHttpIDChallenge.getChallenge();
            this.clientCommandsExecutor.executeClientCommands(new AuthHttpIDChallResponse(byArray, authHttpIDGetChallenge.getHttpSessionID()));
        }
    }

    @Override
    public void handleMountCardReaderRequest(MountCardReaderRequest mountCardReaderRequest) throws IOException, SmartCardException {
        this.handleWritingWithWait(mountCardReaderRequest, ProtoMessages.MOUNT_CARD_READER_REQUEST, ProtoStates.UNKNOWN, ProtoStates.AUTHENTICATED);
    }

    @Override
    public void handlePongResponse(PongResponse pongResponse) throws IOException, SmartCardException {
        this.handleMessageWriting(pongResponse, ProtoMessages.PONG);
    }

    @Override
    public void handleQuitMessageFromClient(QuitMessageFromClient quitMessageFromClient) throws IOException, SmartCardException {
        if (this.handleMessageWriting(quitMessageFromClient, ProtoMessages.QUIT_REQ)) {
            for (SmartCardSessionEventsListener smartCardSessionEventsListener : this.eventsListenerManager.getListeners(SmartCardSessionEventsListener.class)) {
                smartCardSessionEventsListener.disconnected(null);
            }
        }
    }

    @Override
    public void handleRAPDU(ResponseAPDU responseAPDU) throws IOException, SmartCardException {
        responseAPDU.setSequenceNum(this.getCapduSeqNum());
        this.handleMessageWriting(responseAPDU, ProtoMessages.RAPDU);
    }

    private synchronized int getCapduSeqNum() {
        return this.capduSeqNum;
    }

    private synchronized void setCapduSeqNum(int n) {
        this.capduSeqNum = n;
    }

    void setEventsListenerManager(EventsListenerManager eventsListenerManager) {
        this.eventsListenerManager = eventsListenerManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setQuitState() {
        ProtoStateMachine protoStateMachine = this.stateMachine;
        synchronized (protoStateMachine) {
            this.stateMachine.transitionOnMessage(ProtoMessages.QUIT_RESP);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean handleMessageReading(ProtoMessages protoMessages, boolean bl) {
        ProtoStates protoStates;
        boolean bl2 = false;
        ProtoStateMachine protoStateMachine = this.stateMachine;
        synchronized (protoStateMachine) {
            protoStates = this.stateMachine.getState();
            if (this.stateMachine.getState() == ProtoStates.TERMINATED) {
                return false;
            }
            if (this.stateMachine.isValidMessage(protoMessages)) {
                if (bl) {
                    this.stateMachine.transitionOnMessage(protoMessages);
                }
                return true;
            }
            if (protoMessages == ProtoMessages.CAPDU && protoStates == ProtoStates.CARD_REMOVED) {
                return false;
            }
            this.setQuitState();
            bl2 = true;
        }
        if (bl2) {
            for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new SmartCardNotificationEvent(1, 10001, true, "Current State was " + (Object)((Object)protoStates)));
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean handleMessageWriting(SupportsVisitor<CommandVisitorV01_00> supportsVisitor, ProtoMessages protoMessages) throws IOException, SmartCardException {
        ProtoStates protoStates;
        boolean bl = false;
        ProtoStateMachine protoStateMachine = this.stateMachine;
        synchronized (protoStateMachine) {
            protoStates = this.stateMachine.getState();
            if (this.stateMachine.getState() == ProtoStates.TERMINATED) {
                return false;
            }
            if (this.stateMachine.isValidMessage(protoMessages)) {
                this.messageWriter.setOs(this.os);
                try {
                    supportsVisitor.accept(this.messageWriter);
                }
                catch (IOException iOException) {
                    this.setQuitState();
                    throw iOException;
                }
                catch (SmartCardException smartCardException) {
                    this.setQuitState();
                    throw smartCardException;
                }
                this.stateMachine.transitionOnMessage(protoMessages);
                return true;
            }
            this.setQuitState();
            bl = true;
        }
        if (bl) {
            for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new SmartCardNotificationEvent(1, 10001, true, "Current State was " + (Object)((Object)protoStates)));
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleWritingWithWait(SupportsVisitor<CommandVisitorV01_00> supportsVisitor, ProtoMessages protoMessages, ProtoStates protoStates, ProtoStates protoStates2) throws IOException, SmartCardException {
        ProtoStates protoStates3;
        boolean bl = false;
        ProtoStateMachine protoStateMachine = this.stateMachine;
        synchronized (protoStateMachine) {
            boolean bl2;
            protoStates3 = this.stateMachine.getState();
            if (this.stateMachine.getState() == ProtoStates.TERMINATED) {
                return;
            }
            while (true) {
                try {
                    bl2 = this.waitForRequiredState(protoMessages, protoStates, protoStates2);
                }
                catch (InterruptedException interruptedException) {
                    continue;
                }
                break;
            }
            if (bl2) {
                if (this.stateMachine.getState() == ProtoStates.TERMINATED) {
                    return;
                }
                this.messageWriter.setOs(this.os);
                try {
                    supportsVisitor.accept(this.messageWriter);
                }
                catch (IOException iOException) {
                    this.setQuitState();
                    throw iOException;
                }
                catch (SmartCardException smartCardException) {
                    this.setQuitState();
                    throw smartCardException;
                }
                this.stateMachine.transitionOnMessage(protoMessages);
            } else {
                this.setQuitState();
                bl = true;
            }
        }
        if (bl) {
            for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new SmartCardNotificationEvent(1, 10001, true, "Current State was " + (Object)((Object)protoStates3)));
            }
        }
    }

    private boolean waitForRequiredState(ProtoMessages protoMessages, ProtoStates protoStates, ProtoStates protoStates2) throws InterruptedException {
        if (!this.stateMachine.isValidMessage(protoMessages)) {
            if (this.stateMachine.getState() == protoStates) {
                this.stateMachine.waitOnState(protoStates2);
            } else {
                return false;
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleDisconnection() {
        ProtoStateMachine protoStateMachine = this.stateMachine;
        synchronized (protoStateMachine) {
            this.stateMachine.transitionOnMessage(ProtoMessages.QUIT_RESP);
        }
    }
}

