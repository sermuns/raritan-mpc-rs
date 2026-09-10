/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_PAUSE_VIDEO_STREAM_DATA
extends TRCOMMAND {
    private static final int flags_OFFSET = 4;
    private static final int deviceID_OFFSET = 8;
    public static final short CMD_LEN = 9;

    public TRCMD_PAUSE_VIDEO_STREAM_DATA(byte[] byArray) {
        super(byArray);
        this.setCommand((byte)62);
        this.setCmdLength((short)9);
        this.setDeviceID(255);
    }

    public TRCMD_PAUSE_VIDEO_STREAM_DATA(short s) {
        super(s);
        this.setCommand((byte)62);
        this.setCmdLength((short)9);
        this.setDeviceID(255);
    }

    public TRCMD_PAUSE_VIDEO_STREAM_DATA() {
        super((short)4);
        this.setCommand((byte)62);
        this.setCmdLength((short)9);
        this.setDeviceID(255);
    }

    public byte getDeviceID() {
        return this.getByte(8);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, 8);
        this.setInt(0, 4);
    }

    public void setDeviceID(int n) {
        this.setDeviceID((byte)n);
    }

    private void setFlags(int n) {
        this.setInt(n, 4);
    }
}

