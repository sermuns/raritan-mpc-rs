/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.impl.crp.V01_00.ProtoStates;

public enum ProtoMessages {
    AUTH_REQUEST(false, ProtoStates.UNKNOWN),
    AUTH_RESPONSE(true, ProtoStates.AUTHENTICATED),
    MOUNT_CARD_READER_REQUEST(false, ProtoStates.UNKNOWN),
    MOUNT_CARD_READER_RESPONSE(true, ProtoStates.CARD_READER_MOUNTED),
    CARD_INSERTED(true, ProtoStates.CARD_INSERTED),
    CARD_REMOVED(true, ProtoStates.CARD_REMOVED),
    CAPDU(false, ProtoStates.UNKNOWN),
    RAPDU(false, ProtoStates.UNKNOWN),
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

