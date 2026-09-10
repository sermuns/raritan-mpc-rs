/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CConfigReportEvent
extends CDataEvent {
    private String configReport;

    public CConfigReportEvent(String string) {
        super(19);
        this.configReport = string;
    }

    public String getConfigReport() {
        return this.configReport;
    }
}

