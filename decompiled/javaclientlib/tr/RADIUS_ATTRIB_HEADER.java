/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public class RADIUS_ATTRIB_HEADER {
    private byte type;
    private byte lengthAttribute;
    public static final short CMD_LEN = 6;

    public short getLength() {
        return 6;
    }

    public byte getType() {
        return this.type;
    }

    public void setType(byte by) {
        this.type = by;
    }

    public byte getLengthAttribute() {
        return this.lengthAttribute;
    }

    public void setLengthAttribute(byte by) {
        this.lengthAttribute = by;
    }

    public void setLengthAttribute(int n) {
        this.lengthAttribute = (byte)n;
    }

    public void setLength(int n) {
        this.lengthAttribute = (byte)n;
    }
}

