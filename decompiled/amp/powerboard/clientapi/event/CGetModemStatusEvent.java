/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalModemEvent;

public class CGetModemStatusEvent
extends CDataEvent {
    private int enabled;
    private int numRings;
    private int baudRate;
    private int pppServerIp;
    private int pppClientIp;
    private int modemStatus;

    public CGetModemStatusEvent(int n, int n2, int n3, int n4, int n5, int n6, boolean bl, String string) {
        super(60, bl);
        this.enabled = n;
        this.numRings = n2;
        this.baudRate = n3;
        this.pppServerIp = n4;
        this.pppClientIp = n5;
        this.modemStatus = n6;
        this.lockerName = string;
    }

    public CGetModemStatusEvent(boolean bl, String string) {
        super(60, bl);
        this.lockerName = string;
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

    public void setModemStatusData(CInternalModemEvent cInternalModemEvent) {
        this.enabled = cInternalModemEvent.getEnabled();
        this.numRings = cInternalModemEvent.getNumRings();
        this.baudRate = cInternalModemEvent.getBaudRate();
        this.pppServerIp = cInternalModemEvent.getPppServerIp();
        this.pppClientIp = cInternalModemEvent.getPppClientIp();
        this.modemStatus = cInternalModemEvent.getModemStatus();
    }
}

