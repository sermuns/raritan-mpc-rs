/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class CCTField
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(CCTField.class, new String[]{"BitCount", "OpCode"}, new Class[]{Byte.TYPE, Byte.TYPE}, new int[]{0, 0});
    byte byBitCount;
    byte byOpCode;
    public static final short CMD_LEN = 2;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 2;
    }

    public void setBitCount(byte by) {
        this.byBitCount = by;
    }

    public byte getBitCount() {
        return this.byBitCount;
    }

    public void setOpCode(byte by) {
        this.byOpCode = by;
    }

    public byte getOpCode() {
        return this.byOpCode;
    }
}

