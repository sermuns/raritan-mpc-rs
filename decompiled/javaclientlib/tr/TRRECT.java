/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRRECT
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRRECT.class, new String[]{"left", "top", "right", "bottom"}, new Class[]{Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE}, new int[]{0, 0, 0, 0});
    private short left;
    private short top;
    private short right;
    private short bottom;
    static final short CMD_LEN = 8;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    public TRRECT() {
    }

    public TRRECT(byte[] byArray) {
        int n = (byArray[0] & 0xFF) >> 0;
        int n2 = (byArray[1] & 0xFF) >> 0;
        this.left = (short)((n << 8) + (n2 << 0));
        n = (byArray[2] & 0xFF) >> 0;
        n2 = (byArray[3] & 0xFF) >> 0;
        this.top = (short)((n << 8) + (n2 << 0));
        n = (byArray[4] & 0xFF) >> 0;
        n2 = (byArray[5] & 0xFF) >> 0;
        this.right = (short)((n << 8) + (n2 << 0));
        n = (byArray[6] & 0xFF) >> 0;
        n2 = (byArray[7] & 0xFF) >> 0;
        this.bottom = (short)((n << 8) + (n2 << 0));
    }

    @Override
    public short getLength() {
        return 8;
    }

    public short getLeft() {
        return this.left;
    }

    public void setLeft(short s) {
        this.left = s;
    }

    public short getTop() {
        return this.top;
    }

    public void setTop(short s) {
        this.top = s;
    }

    public short getRight() {
        return this.right;
    }

    public void setRight(short s) {
        this.right = s;
    }

    public short getBottom() {
        return this.bottom;
    }

    public void setBottom(short s) {
        this.bottom = s;
    }

    public String toString() {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("\nleft=" + this.getLeft());
            stringBuffer.append("\ntop=" + this.getTop());
            stringBuffer.append("\nright=" + this.getRight());
            stringBuffer.append("\nbottom=" + this.getBottom());
            return stringBuffer.toString();
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }
    }
}

