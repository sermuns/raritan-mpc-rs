/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalModemEvent
extends CDataEvent {
    private int enabled;
    private int numRings;
    private int baudRate;
    private int pppServerIp;
    private int pppClientIp;
    private int modemStatus;

    public CInternalModemEvent(int n, int n2, int n3, int n4, int n5, int n6) {
        super(600);
        this.enabled = n;
        this.numRings = n2;
        this.baudRate = n3;
        this.pppServerIp = n4;
        this.pppClientIp = n5;
        this.modemStatus = n6;
    }

    public int getEnabled() {
        return this.enabled;
    }

    public int getNumRings() {
        return this.numRings;
    }

    public int getBaudRate() {
        return this.baudRate;
    }

    public int getPppServerIp() {
        return this.pppServerIp;
    }

    public int getPppClientIp() {
        return this.pppClientIp;
    }

    public int getModemStatus() {
        return this.modemStatus;
    }
}

