/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.RADIUS_ATTRIB_HEADER;

public class RADIUS_ATTRIB_VALUE
extends RADIUS_ATTRIB_HEADER {
    private int value;
    public static final short CMD_LEN = 261;

    @Override
    public short getLength() {
        return 261;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int n) {
        this.value = n;
    }
}

