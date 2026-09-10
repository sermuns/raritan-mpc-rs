/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalSMTPServerEvent;

public class CGetSMTPServerEvent
extends CDataEvent {
    private int ipAddress;

    public CGetSMTPServerEvent(int n, boolean bl, String string) {
        super(38, bl);
        this.ipAddress = n;
        this.lockerName = string;
    }

    public CGetSMTPServerEvent(boolean bl, String string) {
        super(38, bl);
        this.lockerName = string;
    }

    public int getipAddress() {
        return this.ipAddress;
    }

    public void setSMTPServerData(CInternalSMTPServerEvent cInternalSMTPServerEvent) {
        this.ipAddress = cInternalSMTPServerEvent.getipAddress();
    }
}

