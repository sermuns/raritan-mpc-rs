/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CResetEvent
extends CDataEvent {
    private int resetStatus = 0;
    private String resetReport;
    private String[] clientList = null;

    public CResetEvent(String string, String string2, boolean bl, String[] stringArray, int n) {
        super(2, bl);
        this.lockerName = string2;
        this.resetReport = string;
        this.clientList = stringArray;
        this.resetStatus = n;
    }

    public String getResetReport() {
        return this.resetReport;
    }

    public int getResetStatus() {
        return this.resetStatus;
    }

    public String[] getClientList() {
        return this.clientList;
    }
}

