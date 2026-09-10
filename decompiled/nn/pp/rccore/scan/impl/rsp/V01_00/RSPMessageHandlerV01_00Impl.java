/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import java.io.IOException;
import nn.pp.core.NotificationListener;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.ScanSessionEventsListener;
import nn.pp.rccore.scan.impl.commands.AuthHttpIDChallResponse;
import nn.pp.rccore.scan.impl.commands.AuthHttpIDGetChallenge;
import nn.pp.rccore.scan.impl.commands.AuthNamePassRequest;
import nn.pp.rccore.scan.impl.commands.AuthRDMRequest;
import nn.pp.rccore.scan.impl.commands.CommandVisitorV01_00;
import nn.pp.rccore.scan.impl.commands.PongResponse;
import nn.pp.rccore.scan.impl.commands.QuitMessageFromClient;
import nn.pp.rccore.scan.impl.commands.ScanSessionRequest;
import nn.pp.rccore.scan.impl.rsp.ClientCommandsExecutor;
import nn.pp.rccore.scan.impl.rsp.EventsListenerManager;
import nn.pp.rccore.scan.impl.rsp.ScanCoreNotificationEvent;
import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;
import nn.pp.rccore.scan.impl.rsp.V01_00.AuthHttpIDChallenge;
import nn.pp.rccore.scan.impl.rsp.V01_00.AuthResponse;
import nn.pp.rccore.scan.impl.rsp.V01_00.CommandToMessageWriterVisitor;
import nn.pp.rccore.scan.impl.rsp.V01_00.PingRequest;
import nn.pp.rccore.scan.impl.rsp.V01_00.ProtoMessages;
import nn.pp.rccore.scan.impl.rsp.V01_00.ProtoStateMachine;
import nn.pp.rccore.scan.impl.rsp.V01_00.ProtoStates;
import nn.pp.rccore.scan.impl.rsp.V01_00.QuitRequestFromServer;
import nn.pp.rccore.scan.impl.rsp.V01_00.RSPMessageHandlerV01_00;
import nn.pp.rccore.scan.impl.rsp.V01_00.ScanSessionResponse;

public class RSPMessageHandlerV01_00Impl
implements RSPMessageHandlerV01_00,
CommandVisitorV01_00 {
    private volatile ClientCommandsExecutor clientCommandsExecutor;
    private volatile EventsListenerManager eventsListenerManager;
    private volatile MonitoringDataOutputStream os;
    private volatile MonitoringDataInputStream is;
    private final CommandToMessageWriterVisitor messageWriter = new CommandToMessageWriterVisitor();
    private final ProtoStateMachine stateMachine = new ProtoStateMachine();

    @Override
    public void handleAuthResponse(AuthResponse authResponse) {
        if (this.handleMessageReading(ProtoMessages.AUTH_RESPONSE, authResponse.getAck() != 0) && authResponse.getAck() == 0) {
            this.setQuitState();
            for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new ScanCoreNotificationEvent(authResponse.getReason()));
            }
        }
    }

    @Override
    public void handleScanSessionResponse(ScanSessionResponse scanSessionResponse) {
        block4: {
            if (!this.handleMessageReading(ProtoMessages.SCAN_RESPONSE, scanSessionResponse.getAck() != 0)) break block4;
            if (scanSessionResponse.getAck() == 0) {
                this.setQuitState();
                for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                    notificationListener.receivedNotification(new ScanCoreNotificationEvent(scanSessionResponse.getReason()));
                }
            } else {
                for (ScanSessionEventsListener scanSessionEventsListener : this.eventsListenerManager.getListeners(ScanSessionEventsListener.class)) {
                    scanSessionEventsListener.scanSessionCreated(scanSessionResponse.getScanReferralId());
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
                notificationListener.receivedNotification(new ScanCoreNotificationEvent(1, quitRequestFromServer.getReason()));
            }
        }
    }

    @Override
    public void handleAuthNamePassRequest(AuthNamePassRequest authNamePassRequest) throws IOException, ScanCoreException {
        this.handleMessageWriting(authNamePassRequest, ProtoMessages.AUTH_REQUEST);
    }

    @Override
    public void handleAuthRDMRequest(AuthRDMRequest authRDMRequest) throws IOException, ScanCoreException {
        this.handleMessageWriting(authRDMRequest, ProtoMessages.AUTH_REQUEST);
    }

    @Override
    public void handleHttpIDChallengeResponse(AuthHttpIDChallResponse authHttpIDChallResponse) throws IOException, ScanCoreException {
        this.handleMessageWriting(authHttpIDChallResponse, ProtoMessages.AUTH_REQUEST);
    }

    @Override
    public void handleHttpIDGetChallenge(AuthHttpIDGetChallenge authHttpIDGetChallenge) throws IOException, ScanCoreException {
        if (this.handleMessageWriting(authHttpIDGetChallenge, ProtoMessages.AUTH_REQUEST)) {
            AuthHttpIDChallenge authHttpIDChallenge = new AuthHttpIDChallenge().readMessage(this.is);
            byte[] byArray = authHttpIDChallenge.getChallenge();
            this.clientCommandsExecutor.executeClientCommands(new AuthHttpIDChallResponse(byArray, authHttpIDGetChallenge.getHttpSessionID()));
        }
    }

    @Override
    public void handleScanSessionRequest(ScanSessionRequest scanSessionRequest) throws IOException, ScanCoreException {
        this.handleWritingWithWait(scanSessionRequest, ProtoMessages.SCAN_REQUEST, ProtoStates.UNKNOWN, ProtoStates.AUTHENTICATED);
    }

    @Override
    public void handlePongResponse(PongResponse pongResponse) throws IOException, ScanCoreException {
        this.handleMessageWriting(pongResponse, ProtoMessages.PONG);
    }

    @Override
    public void handleQuitMessageFromClient(QuitMessageFromClient quitMessageFromClient) throws IOException, ScanCoreException {
        if (this.handleMessageWriting(quitMessageFromClient, ProtoMessages.QUIT_REQ)) {
            for (ScanSessionEventsListener scanSessionEventsListener : this.eventsListenerManager.getListeners(ScanSessionEventsListener.class)) {
                scanSessionEventsListener.disconnected(null);
            }
        }
    }

    void setEventsListenerManager(EventsListenerManager eventsListenerManager) {
        this.eventsListenerManager = eventsListenerManager;
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
            this.setQuitState();
            bl2 = true;
        }
        if (bl2) {
            for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new ScanCoreNotificationEvent(1, 10001, true, "Current State was " + (Object)((Object)protoStates)));
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean handleMessageWriting(SupportsVisitor<CommandVisitorV01_00> supportsVisitor, ProtoMessages protoMessages) throws IOException, ScanCoreException {
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
                catch (ScanCoreException scanCoreException) {
                    this.setQuitState();
                    throw scanCoreException;
                }
                this.stateMachine.transitionOnMessage(protoMessages);
                return true;
            }
            this.setQuitState();
            bl = true;
        }
        if (bl) {
            for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new ScanCoreNotificationEvent(1, 10001, true, "Current State was " + (Object)((Object)protoStates)));
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleWritingWithWait(SupportsVisitor<CommandVisitorV01_00> supportsVisitor, ProtoMessages protoMessages, ProtoStates protoStates, ProtoStates protoStates2) throws IOException, ScanCoreException {
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
                catch (ScanCoreException scanCoreException) {
                    this.setQuitState();
                    throw scanCoreException;
                }
                this.stateMachine.transitionOnMessage(protoMessages);
            } else {
                this.setQuitState();
                bl = true;
            }
        }
        if (bl) {
            for (NotificationListener notificationListener : this.eventsListenerManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new ScanCoreNotificationEvent(1, 10001, true, "Current State was " + (Object)((Object)protoStates3)));
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
    private void setQuitState() {
        ProtoStateMachine protoStateMachine = this.stateMachine;
        synchronized (protoStateMachine) {
            this.stateMachine.transitionOnMessage(ProtoMessages.QUIT_RESP);
        }
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

