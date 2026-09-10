/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CUpgradeReportEvent
extends CDataEvent {
    private int percentage;
    private String upgradeReport;

    public CUpgradeReportEvent(int n, String string) {
        super(14);
        this.percentage = n;
        this.upgradeReport = string;
    }

    public int getpercentage() {
        return this.percentage;
    }

    public String getupgradeReport() {
        return this.upgradeReport;
    }
}

