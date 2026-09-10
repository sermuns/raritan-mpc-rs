/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

public class CReportEvent {
    private int msgOpcode;
    private String report;

    public CReportEvent(int n, String string) {
        this.msgOpcode = n;
        this.report = string;
    }

    public int getOpcode() {
        return this.msgOpcode;
    }

    public String getReport() {
        return this.report;
    }
}

