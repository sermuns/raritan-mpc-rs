/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_RDM_EVENT_DATA
extends TRCOMMAND {
    private int rdmEventData_OFFSET = 4;
    public static final short CMD_LEN = 4;

    public TRRSP_RDM_EVENT_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_RDM_EVENT_DATA() {
        super((short)4);
    }

    public byte[] getRdmEventData() {
        return this.getBytes(this.rdmEventData_OFFSET, 4104);
    }

    public void setRdmEventData(byte[] byArray) {
        this.setBytes(byArray, this.rdmEventData_OFFSET);
    }
}

