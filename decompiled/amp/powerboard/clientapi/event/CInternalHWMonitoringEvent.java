/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalHWMonitoringEvent
extends CDataEvent {
    private int isEnabled;
    private int interval;

    public CInternalHWMonitoringEvent(int n, int n2) {
        super(277);
        this.isEnabled = n;
        this.interval = n2;
    }

    public int getisEnabled() {
        return this.isEnabled;
    }

    public int getinterval() {
        return this.interval;
    }
}

