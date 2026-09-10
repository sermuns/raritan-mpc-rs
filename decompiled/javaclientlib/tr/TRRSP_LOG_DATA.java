/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_LOG;

public class TRRSP_LOG_DATA
extends TRCOMMAND {
    private int context_OFFSET = 4;
    private int count_OFFSET = this.context_OFFSET + 4;
    private int events_OFFSET = this.count_OFFSET + 4;
    public static final short CMD_LEN = 32;

    public TRRSP_LOG_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_LOG_DATA() {
        super((short)32);
    }

    public int getContext() {
        return this.getInt(this.context_OFFSET);
    }

    public void setContext(int n) {
        this.setInt(n, this.context_OFFSET);
    }

    public int getCount() {
        return this.getInt(this.count_OFFSET);
    }

    public void setCount(int n) {
        this.setInt(n, this.count_OFFSET);
    }

    public TRSRVR_LOG getEvents() throws Exception {
        byte[] byArray = this.getBytes(this.events_OFFSET, 20);
        TRSRVR_LOG tRSRVR_LOG = new TRSRVR_LOG();
        tRSRVR_LOG.populate(byArray);
        return tRSRVR_LOG;
    }

    public void setEvents(TRSRVR_LOG tRSRVR_LOG) throws Exception {
        this.setBytes(tRSRVR_LOG.getDataBytes(), this.events_OFFSET);
    }
}

