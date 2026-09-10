/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalHWMonitoringEvent;

public class CGetHWMonitoringEvent
extends CDataEvent {
    private int isEnabled;
    private int interval;

    public CGetHWMonitoringEvent(int n, int n2, boolean bl, String string) {
        super(27, bl);
        this.isEnabled = n;
        this.interval = n2;
        this.lockerName = string;
    }

    public CGetHWMonitoringEvent(boolean bl, String string) {
        super(27, bl);
        this.lockerName = string;
    }

    public int getisEnabled() {
        return this.isEnabled;
    }

    public int getinterval() {
        return this.interval;
    }

    public void setHWMonitoringData(CInternalHWMonitoringEvent cInternalHWMonitoringEvent) {
        this.isEnabled = cInternalHWMonitoringEvent.getisEnabled();
        this.interval = cInternalHWMonitoringEvent.getinterval();
    }
}

