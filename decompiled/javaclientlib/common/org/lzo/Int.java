/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common.org.lzo;

public class Int
extends Number {
    private static final long serialVersionUID = 8781419805789660524L;
    private int value = 0;

    public Int() {
        this(0);
    }

    public Int(int n) {
        this.value = n;
    }

    public void setValue(int n) {
        this.value = n;
    }

    public void add(int n) {
        this.value += n;
    }

    public void sub(int n) {
        this.value -= n;
    }

    @Override
    public byte byteValue() {
        return (byte)this.value;
    }

    @Override
    public short shortValue() {
        return (short)this.value;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }
}

