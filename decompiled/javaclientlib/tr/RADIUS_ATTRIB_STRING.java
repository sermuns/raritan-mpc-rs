/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.RADIUS_ATTRIB_HEADER;

public class RADIUS_ATTRIB_STRING
extends RADIUS_ATTRIB_HEADER {
    private byte[] attribute = new byte[253];
    public static final short CMD_LEN = 257;

    @Override
    public short getLength() {
        return 257;
    }

    public byte[] getAttribute() {
        return this.attribute;
    }

    public String getAttributeAsString() {
        return this.attribute.toString();
    }

    public void setAttribute(byte[] byArray) {
        this.attribute = byArray;
    }

    public void setAttribute(String string) {
        this.attribute = string.getBytes();
    }
}

