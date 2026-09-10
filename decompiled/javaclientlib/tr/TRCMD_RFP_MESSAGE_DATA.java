/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_RFP_MESSAGE_DATA
extends TRCOMMAND {
    private int phase_OFFSET = 4;
    private int fileData_OFFSET = this.phase_OFFSET + 4;
    private int rfpData_OFFSET = this.fileData_OFFSET + 4;
    public static final short CMD_LEN = 12;

    public TRCMD_RFP_MESSAGE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_RFP_MESSAGE_DATA() {
        super((short)4096);
    }

    public int getPhase() {
        return this.getInt(this.phase_OFFSET);
    }

    public void setPhase(int n) {
        this.setInt(n, this.phase_OFFSET);
    }

    public int getFileData() {
        return this.getInt(this.fileData_OFFSET);
    }

    public void setFileData(int n) {
        this.setInt(n, this.fileData_OFFSET);
    }

    public byte[] getRFPdata() {
        return this.getBytes(this.rfpData_OFFSET, this.getCmdLength() - 12);
    }

    public void setRFPData(byte[] byArray) {
        this.setBytes(byArray, this.rfpData_OFFSET);
    }
}

