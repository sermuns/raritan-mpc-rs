/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CLogoutEvent
extends CDataEvent {
    private String logoutReport;

    public CLogoutEvent(String string) {
        super(177);
        this.logoutReport = string;
    }

    public String getLogoutReport() {
        return this.logoutReport;
    }
}

