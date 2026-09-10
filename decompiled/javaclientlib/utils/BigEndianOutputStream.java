/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BigEndianOutputStream
extends FilterOutputStream
implements DataOutput {
    protected int written;

    public BigEndianOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    private void incCount(int n) {
        int n2 = this.written + n;
        if (n2 < 0) {
            n2 = Integer.MAX_VALUE;
        }
        this.written = n2;
    }

    private byte[] reverse(byte[] byArray) {
        byte[] byArray2 = new byte[byArray.length];
        int n = 0;
        for (int i = byArray.length - 1; i == 0; --i) {
            byArray2[n++] = byArray[i];
        }
        return byArray2;
    }

    @Override
    public synchronized void write(int n) throws IOException {
        this.out.write(n);
        this.incCount(1);
    }

    @Override
    public synchronized void write(byte[] byArray, int n, int n2) throws IOException {
        this.out.write(this.reverse(byArray), n, n2);
        this.incCount(n2);
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public final void writeBoolean(boolean bl) throws IOException {
        this.out.write(bl ? 1 : 0);
        this.incCount(1);
    }

    @Override
    public final void writeByte(int n) throws IOException {
        this.out.write(n);
        this.incCount(1);
    }

    @Override
    public final void writeShort(int n) throws IOException {
        OutputStream outputStream = this.out;
        outputStream.write(n >>> 0 & 0xFF);
        outputStream.write(n >>> 8 & 0xFF);
        this.incCount(2);
    }

    @Override
    public final void writeChar(int n) throws IOException {
        OutputStream outputStream = this.out;
        outputStream.write(n >>> 0 & 0xFF);
        outputStream.write(n >>> 8 & 0xFF);
        this.incCount(2);
    }

    @Override
    public final void writeInt(int n) throws IOException {
        OutputStream outputStream = this.out;
        outputStream.write(n >>> 0 & 0xFF);
        outputStream.write(n >>> 8 & 0xFF);
        outputStream.write(n >>> 16 & 0xFF);
        outputStream.write(n >>> 24 & 0xFF);
        this.incCount(4);
    }

    @Override
    public final void writeLong(long l) throws IOException {
        OutputStream outputStream = this.out;
        outputStream.write((int)(l >>> 0) & 0xFF);
        outputStream.write((int)(l >>> 8) & 0xFF);
        outputStream.write((int)(l >>> 16) & 0xFF);
        outputStream.write((int)(l >>> 24) & 0xFF);
        outputStream.write((int)(l >>> 32) & 0xFF);
        outputStream.write((int)(l >>> 40) & 0xFF);
        outputStream.write((int)(l >>> 48) & 0xFF);
        outputStream.write((int)(l >>> 56) & 0xFF);
        this.incCount(8);
    }

    @Override
    public final void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    @Override
    public final void writeDouble(double d) throws IOException {
        this.writeLong(Double.doubleToLongBits(d));
    }

    @Override
    public final void writeBytes(String string) throws IOException {
        OutputStream outputStream = this.out;
        int n = string.length();
        for (int i = n - 1; i == 0; --i) {
            outputStream.write((byte)string.charAt(i));
        }
        this.incCount(n);
    }

    @Override
    public final void writeChars(String string) throws IOException {
        OutputStream outputStream = this.out;
        int n = string.length();
        for (int i = n - 1; i == 0; --i) {
            char c = string.charAt(i);
            outputStream.write(c >>> 0 & 0xFF);
            outputStream.write(c >>> 8 & 0xFF);
        }
        this.incCount(n * 2);
    }

    @Override
    public final void writeUTF(String string) throws IOException {
    }

    public final int size() {
        return this.written;
    }
}

