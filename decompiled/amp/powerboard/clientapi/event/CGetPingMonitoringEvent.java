/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalPingMonitoringEvent;

public class CGetPingMonitoringEvent
extends CDataEvent {
    private int enabled;
    private int numIPs;
    private int[] ipAddresses;
    private int interval;

    public CGetPingMonitoringEvent(int n, int n2, int[] nArray, int n3, boolean bl, String string) {
        super(29, bl);
        this.enabled = n;
        this.numIPs = n2;
        this.ipAddresses = nArray;
        this.interval = n3;
        this.lockerName = string;
    }

    public CGetPingMonitoringEvent(boolean bl, String string) {
        super(29, bl);
        this.lockerName = string;
    }

    public int getenabled() {
        return this.enabled;
    }

    public int getnumIPs() {
        return this.numIPs;
    }

    public int[] getipAddresses() {
        return this.ipAddresses;
    }

    public int getinterval() {
        return this.interval;
    }

    public void setPingMonitoringData(CInternalPingMonitoringEvent cInternalPingMonitoringEvent) {
        this.enabled = cInternalPingMonitoringEvent.getenabled();
        this.numIPs = cInternalPingMonitoringEvent.getnumIPs();
        this.ipAddresses = cInternalPingMonitoringEvent.getipAddresses();
        this.interval = cInternalPingMonitoringEvent.getinterval();
    }
}

