/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CConfigLockEvent
extends CDataEvent {
    private int messageStatus;

    public CConfigLockEvent(int n, String string) {
        super(15);
        this.messageStatus = n;
        this.lockerName = string;
    }

    public int getmessageStatus() {
        return this.messageStatus;
    }
}

