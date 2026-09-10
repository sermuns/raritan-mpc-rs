/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_RFP_MESSAGE_DATA
extends TRCOMMAND {
    private int phase_OFFSET = 4;
    private int rfpData_OFFSET = this.phase_OFFSET + 4;
    private int rfpMessageData_OFFSET = this.rfpData_OFFSET + 4;
    public static final short CMD_LEN = 12;

    public TRRSP_RFP_MESSAGE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_RFP_MESSAGE_DATA() {
        super((short)4096);
    }

    public int getPhase() {
        return this.getInt(this.phase_OFFSET);
    }

    public void setPhase(int n) {
        this.setInt(n, this.phase_OFFSET);
    }

    public int getRfpData() {
        return this.getInt(this.rfpData_OFFSET);
    }

    public void setRfpData(int n) {
        this.setInt(n, this.rfpData_OFFSET);
    }

    public byte[] getRfpMessageData() {
        return this.getBytes(this.rfpMessageData_OFFSET, 4084);
    }

    public void setRfpMessageData(byte[] byArray) {
        this.setBytes(byArray, this.rfpMessageData_OFFSET);
    }
}

