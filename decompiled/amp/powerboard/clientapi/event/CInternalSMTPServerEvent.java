/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalSMTPServerEvent
extends CDataEvent {
    private int ipAddress;

    public CInternalSMTPServerEvent(int n) {
        super(388);
        this.ipAddress = n;
    }

    public int getipAddress() {
        return this.ipAddress;
    }
}

