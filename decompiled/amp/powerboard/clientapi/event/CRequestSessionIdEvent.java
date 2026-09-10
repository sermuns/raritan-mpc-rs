/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CRequestSessionIdEvent
extends CDataEvent {
    private int iStatus;
    private int sessionId;
    private int portNum;

    public CRequestSessionIdEvent(int n, int n2, int n3) {
        super(500);
        this.sessionId = n2;
        this.portNum = n3;
        this.iStatus = n;
    }

    public int getSessionID() {
        return this.sessionId;
    }

    public int getPortNum() {
        return this.portNum;
    }

    public int getStatus() {
        return this.iStatus;
    }
}

