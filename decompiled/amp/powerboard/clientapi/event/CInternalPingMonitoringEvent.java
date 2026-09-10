/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalPingMonitoringEvent
extends CDataEvent {
    private int enabled;
    private int numIPs;
    private int[] ipAddresses;
    private int interval;

    public CInternalPingMonitoringEvent(int n, int n2, int[] nArray, int n3) {
        super(299);
        this.enabled = n;
        this.numIPs = n2;
        this.ipAddresses = nArray;
        this.interval = n3;
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
}

