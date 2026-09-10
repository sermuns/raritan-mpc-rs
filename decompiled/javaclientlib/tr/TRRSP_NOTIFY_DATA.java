/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_NOTIFY_DATA
extends TRCOMMAND {
    private int event_OFFSET = 4;
    private int param_OFFSET = this.event_OFFSET + 4;
    private int deviceID_OFFSET = this.param_OFFSET + 4;
    private int notifyData_OFFSET = this.deviceID_OFFSET + 1;
    public static final short CMD_LEN = 13;

    public TRRSP_NOTIFY_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_NOTIFY_DATA() {
        super((short)13);
    }

    public int getEvent() {
        return this.getInt(this.event_OFFSET);
    }

    public void setEvent(int n) {
        this.setInt(n, this.event_OFFSET);
    }

    public int getParam() {
        return this.getInt(this.param_OFFSET);
    }

    public void setParam(int n) {
        this.setInt(n, this.param_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public byte[] getNotifyData() {
        return this.getBytes(this.notifyData_OFFSET, 4095);
    }

    public void setNotifyData(byte[] byArray) {
        this.setBytes(byArray, this.notifyData_OFFSET);
    }
}

