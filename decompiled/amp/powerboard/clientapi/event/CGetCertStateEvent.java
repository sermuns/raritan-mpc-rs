/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalCertStateEvent;

public class CGetCertStateEvent
extends CDataEvent {
    private int certState;

    public CGetCertStateEvent(int n, boolean bl, String string) {
        super(45, bl);
        this.certState = n;
        this.lockerName = string;
    }

    public CGetCertStateEvent(boolean bl, String string) {
        super(45, bl);
        this.lockerName = string;
    }

    public int getCertState() {
        return this.certState;
    }

    public void setCertStateData(CInternalCertStateEvent cInternalCertStateEvent) {
        this.certState = cInternalCertStateEvent.getCertState();
    }
}

