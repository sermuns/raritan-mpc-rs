/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_CHANNEL_DATA;

public class TRRSP_VIDEO_DEVICE_DATA
extends TRCOMMAND {
    private int deviceID_OFFSET = 4;
    private int flags_OFFSET = this.deviceID_OFFSET + 1;
    private int channelData_OFFSET = this.flags_OFFSET + 4;
    public static final short CMD_LEN = 121;

    public TRRSP_VIDEO_DEVICE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_VIDEO_DEVICE_DATA() {
        super((short)121);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public int getFlags() {
        return this.getInt(this.flags_OFFSET);
    }

    public void setFlags(int n) {
        this.setInt(n, this.flags_OFFSET);
    }

    public TRSRVR_CHANNEL_DATA getChannelData() throws Exception {
        byte[] byArray = this.getBytes(this.channelData_OFFSET, 112);
        TRSRVR_CHANNEL_DATA tRSRVR_CHANNEL_DATA = new TRSRVR_CHANNEL_DATA();
        tRSRVR_CHANNEL_DATA.populate(byArray);
        return tRSRVR_CHANNEL_DATA;
    }

    public void setChannelData(TRSRVR_CHANNEL_DATA tRSRVR_CHANNEL_DATA) throws Exception {
        this.setBytes(tRSRVR_CHANNEL_DATA.getDataBytes(), this.channelData_OFFSET);
    }
}

