/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CStatusEvent;

public class CInternalStatusEvent
extends CStatusEvent {
    private int msgOpcode = 0;
    private int status;
    private int reqOperation = 0;

    public CInternalStatusEvent(int n, int n2, int n3) {
        super(n, n2, n3, false, null, 0);
        this.msgOpcode = n;
        this.status = n2;
        this.reqOperation = n3;
    }

    public int getOpcode() {
        return 44;
    }

    public int getStatus() {
        return this.status;
    }

    public int getReqOperation() {
        return this.reqOperation;
    }

    public int getRealOpcode() {
        return this.msgOpcode;
    }
}

