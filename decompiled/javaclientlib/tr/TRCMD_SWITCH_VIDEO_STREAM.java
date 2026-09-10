/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_SWITCH_VIDEO_STREAM
extends TRCOMMAND {
    private int xmlMessage_OFFSET = 4;
    public static final short CMD_LEN = 4108;

    public TRCMD_SWITCH_VIDEO_STREAM(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_SWITCH_VIDEO_STREAM() {
        super((short)4108);
    }

    public void setXMLMessage(byte[] byArray) {
        this.setBytes(byArray, this.xmlMessage_OFFSET);
    }

    public byte[] getXMLMessage() {
        return this.getBytes(this.xmlMessage_OFFSET, 4104);
    }
}

