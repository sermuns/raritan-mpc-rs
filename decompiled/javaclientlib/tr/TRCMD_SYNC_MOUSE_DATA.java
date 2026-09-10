/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_SYNC_MOUSE_DATA
extends TRCOMMAND {
    private int syncFlags_OFFSET = 4;
    private int deviceID_OFFSET = this.syncFlags_OFFSET + 4;
    public static final short CMD_LEN = 9;

    public TRCMD_SYNC_MOUSE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_SYNC_MOUSE_DATA() {
        super((short)9);
    }

    public int getSyncFlags() {
        return this.getInt(this.syncFlags_OFFSET);
    }

    public void setSyncFlags(int n) {
        this.setInt(n, this.syncFlags_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

