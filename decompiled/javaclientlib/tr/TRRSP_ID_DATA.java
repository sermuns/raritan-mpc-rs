/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_SERVER_ID;

public class TRRSP_ID_DATA
extends TRCOMMAND {
    private int serverID_OFFSET = 4;
    public static final short CMD_LEN = 78;

    public TRRSP_ID_DATA() {
        super((short)78);
    }

    public TRRSP_ID_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRSRVR_SERVER_ID getServerID() throws Exception {
        byte[] byArray = this.getBytes(this.serverID_OFFSET, 74);
        TRSRVR_SERVER_ID tRSRVR_SERVER_ID = new TRSRVR_SERVER_ID();
        tRSRVR_SERVER_ID.populate(byArray);
        return tRSRVR_SERVER_ID;
    }

    public void setServerID(TRSRVR_SERVER_ID tRSRVR_SERVER_ID) throws Exception {
        this.setBytes(tRSRVR_SERVER_ID.getDataBytes(), this.serverID_OFFSET);
    }
}

