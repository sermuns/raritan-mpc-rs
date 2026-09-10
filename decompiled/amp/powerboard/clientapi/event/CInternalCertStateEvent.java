/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalCertStateEvent
extends CDataEvent {
    private int certState;

    public CInternalCertStateEvent(int n) {
        super(455);
        this.certState = n;
    }

    public int getCertState() {
        return this.certState;
    }
}

