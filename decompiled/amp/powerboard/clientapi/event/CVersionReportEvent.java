/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CVersionReportEvent
extends CDataEvent {
    private String versionReport;

    public CVersionReportEvent(String string) {
        super(18);
        this.versionReport = string;
    }

    public String getVersionReport() {
        return this.versionReport;
    }
}

