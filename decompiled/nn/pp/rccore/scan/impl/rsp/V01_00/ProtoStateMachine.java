/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import java.util.EnumMap;
import java.util.EnumSet;
import nn.pp.rccore.scan.impl.rsp.V01_00.ProtoMessages;
import nn.pp.rccore.scan.impl.rsp.V01_00.ProtoStates;

public class ProtoStateMachine {
    private static final EnumMap<ProtoStates, EnumSet<ProtoMessages>> table = new EnumMap(ProtoStates.class);
    private static final EnumMap<PingPongState, EnumSet<ProtoMessages>> pingPongTable;
    private static final EnumMap<AuthState, EnumSet<ProtoMessages>> authTable;
    private ProtoStates state = ProtoStates.UNKNOWN;
    private PingPongState pingPongState = PingPongState.PONG_SENT;
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
                case AUTH_REQUEST: {
                    this.authState = AuthState.AUTH_REQ;
                    break;
                }
                case AUTH_RESPONSE: {
                    this.authState = AuthState.AUTH_RESP;
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
        table.put(ProtoStates.AUTHENTICATED, EnumSet.of(ProtoMessages.SCAN_REQUEST, new ProtoMessages[]{ProtoMessages.SCAN_RESPONSE, ProtoMessages.PING, ProtoMessages.PONG, ProtoMessages.QUIT_REQ, ProtoMessages.QUIT_RESP}));
        table.put(ProtoStates.SCAN_SESSION_CREATED, EnumSet.of(ProtoMessages.PING, ProtoMessages.PONG, ProtoMessages.QUIT_REQ, ProtoMessages.QUIT_RESP));
        table.put(ProtoStates.TERMINATED, EnumSet.noneOf(ProtoMessages.class));
        pingPongTable = new EnumMap(PingPongState.class);
        pingPongTable.put(PingPongState.PING_RECEIVED, EnumSet.of(ProtoMessages.PING, ProtoMessages.PONG));
        pingPongTable.put(PingPongState.PONG_SENT, EnumSet.of(ProtoMessages.PING));
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

    private static enum PingPongState {
        PING_RECEIVED,
        PONG_SENT;

    }
}

