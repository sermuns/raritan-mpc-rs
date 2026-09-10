/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import nn.pp.rccore.scan.impl.rsp.V01_00.ProtoStates;

public enum ProtoMessages {
    AUTH_REQUEST(false, ProtoStates.UNKNOWN),
    AUTH_RESPONSE(true, ProtoStates.AUTHENTICATED),
    SCAN_REQUEST(false, ProtoStates.UNKNOWN),
    SCAN_RESPONSE(true, ProtoStates.SCAN_SESSION_CREATED),
    PING(false, ProtoStates.UNKNOWN),
    PONG(false, ProtoStates.UNKNOWN),
    QUIT_REQ(true, ProtoStates.TERMINATED),
    QUIT_RESP(true, ProtoStates.TERMINATED);

    private final boolean transition;
    private final ProtoStates toState;

    private ProtoMessages(boolean bl, ProtoStates protoStates) {
        this.transition = bl;
        this.toState = protoStates;
    }

    public boolean isTransition() {
        return this.transition;
    }

    public ProtoStates getToState() {
        return this.toState;
    }
}

