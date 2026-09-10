/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CKillEvent
extends CDataEvent {
    private String killReport;

    public CKillEvent(String string) {
        super(17);
        this.killReport = string;
    }

    public String getKillReport() {
        return this.killReport;
    }
}

