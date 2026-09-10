/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.impl.crp.V01_00.ProtoMessages;
import com.raritan.smartcard.impl.crp.V01_00.ProtoStates;
import java.util.EnumMap;
import java.util.EnumSet;

public class ProtoStateMachine {
    private static final EnumMap<ProtoStates, EnumSet<ProtoMessages>> table = new EnumMap(ProtoStates.class);
    private static final EnumMap<PingPongState, EnumSet<ProtoMessages>> pingPongTable;
    private static final EnumMap<TransmitState, EnumSet<ProtoMessages>> transmitTable;
    private static final EnumMap<MountRequestState, EnumSet<ProtoMessages>> mountTable;
    private static final EnumMap<AuthState, EnumSet<ProtoMessages>> authTable;
    private ProtoStates state = ProtoStates.UNKNOWN;
    private PingPongState pingPongState = PingPongState.PONG_SENT;
    private TransmitState transmitState = TransmitState.RAPDU_SENT;
    private MountRequestState mountRequestState = MountRequestState.MOUNT_CARD_NOT_REQUESTED;
    private AuthState authState = AuthState.AUTH_NOT_SENT;

    public synchronized void transitionOnMessage(ProtoMessages protoMessages) {
        if (this.isValidMessage(protoMessages)) {
            switch (protoMessages) {
                case PING: {
                    this.pingPongState = PingPongState.PING_RECEIVED;
                    break;
                }
                case PONG: {
                    this.pingPongState = PingPongState.PONG_SENT;
                    break;
                }
                case CAPDU: 
                case RAPDU: {
                    this.transmitState = TransmitState.toState(this.transmitState);
                    break;
                }
                case MOUNT_CARD_READER_REQUEST: {
                    this.mountRequestState = MountRequestState.MOUNT_CARD_REQ;
                    break;
                }
                case MOUNT_CARD_READER_RESPONSE: {
                    this.mountRequestState = MountRequestState.MOUNT_CARD_RESP;
                    break;
                }
                case AUTH_REQUEST: {
                    this.authState = AuthState.AUTH_REQ;
                    break;
                }
                case AUTH_RESPONSE: {
                    this.authState = AuthState.AUTH_RESP;
                    break;
                }
                case CARD_INSERTED: {
                    this.transmitState = TransmitState.RAPDU_SENT;
                    break;
                }
            }
            if (protoMessages.isTransition()) {
                this.state = protoMessages.getToState();
            }
            this.notifyAll();
        }
    }

    public synchronized void waitOnState(ProtoStates protoStates) throws InterruptedException {
        if (this.state == protoStates) {
            return;
        }
        while (this.state != protoStates && this.state != ProtoStates.TERMINATED) {
            this.wait();
        }
    }

    public synchronized boolean isValidMessage(ProtoMessages protoMessages) {
        if (table.get((Object)this.state).contains((Object)protoMessages)) {
            switch (protoMessages) {
                case PING: 
                case PONG: {
                    return pingPongTable.get((Object)this.pingPongState).contains((Object)protoMessages);
                }
                case CAPDU: 
                case RAPDU: {
                    return true;
                }
                case MOUNT_CARD_READER_REQUEST: 
                case MOUNT_CARD_READER_RESPONSE: {
                    return mountTable.get((Object)this.mountRequestState).contains((Object)protoMessages);
                }
                case AUTH_REQUEST: 
                case AUTH_RESPONSE: {
                    return authTable.get((Object)this.authState).contains((Object)protoMessages);
                }
            }
            return true;
        }
        return false;
    }

    public synchronized ProtoStates getState() {
        return this.state;
    }

    static {
        table.put(ProtoStates.UNKNOWN, EnumSet.of(ProtoMessages.AUTH_REQUEST, ProtoMessages.AUTH_RESPONSE, ProtoMessages.QUIT_RESP));
        table.put(ProtoStates.AUTHENTICATED, EnumSet.of(ProtoMessages.MOUNT_CARD_READER_REQUEST, new ProtoMessages[]{ProtoMessages.MOUNT_CARD_READER_RESPONSE, ProtoMessages.PING, ProtoMessages.PONG, ProtoMessages.QUIT_REQ, ProtoMessages.QUIT_RESP}));
        table.put(ProtoStates.CARD_READER_MOUNTED, EnumSet.of(ProtoMessages.CARD_INSERTED, ProtoMessages.PING, ProtoMessages.PONG, ProtoMessages.QUIT_REQ, ProtoMessages.QUIT_RESP));
        table.put(ProtoStates.CARD_INSERTED, EnumSet.of(ProtoMessages.CARD_REMOVED, new ProtoMessages[]{ProtoMessages.CAPDU, ProtoMessages.RAPDU, ProtoMessages.PING, ProtoMessages.PONG, ProtoMessages.QUIT_REQ, ProtoMessages.QUIT_RESP}));
        table.put(ProtoStates.CARD_REMOVED, EnumSet.of(ProtoMessages.CARD_INSERTED, ProtoMessages.PING, ProtoMessages.PONG, ProtoMessages.QUIT_REQ, ProtoMessages.QUIT_RESP));
        table.put(ProtoStates.TERMINATED, EnumSet.noneOf(ProtoMessages.class));
        pingPongTable = new EnumMap(PingPongState.class);
        pingPongTable.put(PingPongState.PING_RECEIVED, EnumSet.of(ProtoMessages.PING, ProtoMessages.PONG));
        pingPongTable.put(PingPongState.PONG_SENT, EnumSet.of(ProtoMessages.PING));
        transmitTable = new EnumMap(TransmitState.class);
        transmitTable.put(TransmitState.CAPDU_RECEIVED, EnumSet.of(ProtoMessages.RAPDU));
        transmitTable.put(TransmitState.RAPDU_SENT, EnumSet.of(ProtoMessages.CAPDU));
        mountTable = new EnumMap(MountRequestState.class);
        mountTable.put(MountRequestState.MOUNT_CARD_NOT_REQUESTED, EnumSet.of(ProtoMessages.MOUNT_CARD_READER_REQUEST));
        mountTable.put(MountRequestState.MOUNT_CARD_REQ, EnumSet.of(ProtoMessages.MOUNT_CARD_READER_RESPONSE));
        mountTable.put(MountRequestState.MOUNT_CARD_RESP, EnumSet.noneOf(ProtoMessages.class));
        authTable = new EnumMap(AuthState.class);
        authTable.put(AuthState.AUTH_NOT_SENT, EnumSet.of(ProtoMessages.AUTH_REQUEST));
        authTable.put(AuthState.AUTH_REQ, EnumSet.of(ProtoMessages.AUTH_REQUEST, ProtoMessages.AUTH_RESPONSE));
        authTable.put(AuthState.AUTH_RESP, EnumSet.noneOf(ProtoMessages.class));
    }

    private static enum AuthState {
        AUTH_NOT_SENT,
        AUTH_REQ,
        AUTH_RESP;

    }

    private static enum MountRequestState {
        MOUNT_CARD_NOT_REQUESTED,
        MOUNT_CARD_REQ,
        MOUNT_CARD_RESP;

    }

    private static enum TransmitState {
        CAPDU_RECEIVED,
        RAPDU_SENT;


        static TransmitState toState(TransmitState transmitState) {
            if (transmitState == CAPDU_RECEIVED) {
                return RAPDU_SENT;
            }
            return CAPDU_RECEIVED;
        }
    }

    private static enum PingPongState {
        PING_RECEIVED,
        PONG_SENT;

    }
}

