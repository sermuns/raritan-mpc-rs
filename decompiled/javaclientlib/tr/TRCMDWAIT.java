/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.utils.Monitor;

public class TRCMDWAIT {
    private TRCOMMAND response;
    private Monitor event;
    private long startTime;
    private int expRespCommand;
    private boolean sync;
    private byte pktID;
    private byte[] availableData = null;

    public TRCOMMAND getResponse() {
        return this.response;
    }

    public void setResponse(TRCOMMAND tRCOMMAND) {
        this.response = tRCOMMAND;
    }

    public Monitor getEvent() {
        return this.event;
    }

    public void setEvent(Monitor monitor) {
        this.event = monitor;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long l) {
        this.startTime = l;
    }

    public int getExpRespCommand() {
        return this.expRespCommand;
    }

    public void setExpRespCommand(int n) {
        this.expRespCommand = n;
    }

    public boolean getSync() {
        return this.sync;
    }

    public void setSync(boolean bl) {
        this.sync = bl;
    }

    public byte getPktID() {
        return this.pktID;
    }

    public void setPktID(byte by) {
        this.pktID = by;
    }

    public byte[] getAvailableData() {
        return this.availableData;
    }

    public void setAvailableData(byte[] byArray) {
        this.availableData = byArray;
    }
}

