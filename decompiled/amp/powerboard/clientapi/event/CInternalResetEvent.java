/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalResetEvent
extends CDataEvent {
    private int resetStatus = 0;
    private String resetReport;

    public CInternalResetEvent(String string, int n) {
        super(222);
        this.resetReport = string;
        this.resetStatus = n;
    }

    public String getResetReport() {
        return this.resetReport;
    }

    public int getResetStatus() {
        return this.resetStatus;
    }
}

