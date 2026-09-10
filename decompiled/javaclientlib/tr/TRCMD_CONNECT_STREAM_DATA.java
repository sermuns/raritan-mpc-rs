/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_CONNECT_STREAM_DATA
extends TRCOMMAND {
    private int xmlMessage_OFFSET = 4;
    public static final short CMD_LEN = 4108;

    public TRCMD_CONNECT_STREAM_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_CONNECT_STREAM_DATA() {
        super((short)4108);
    }

    public byte[] getXMLMessage() {
        return this.getBytes(this.xmlMessage_OFFSET, 4108);
    }

    public void setXMLMessage(byte[] byArray) {
        this.setBytes(byArray, this.xmlMessage_OFFSET);
    }
}

