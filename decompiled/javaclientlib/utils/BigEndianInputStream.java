/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

import java.io.DataInput;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BigEndianInputStream
extends FilterInputStream
implements DataInput {
    private char[] lineBuffer;

    public BigEndianInputStream(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public final int readInt() throws IOException {
        int n;
        int n2;
        int n3;
        InputStream inputStream = this.in;
        int n4 = inputStream.read();
        if ((n4 | (n3 = inputStream.read()) | (n2 = inputStream.read()) | (n = inputStream.read())) < 0) {
            throw new EOFException();
        }
        return (n << 24) + (n2 << 16) + (n3 << 8) + (n4 << 0);
    }

    @Override
    public final short readShort() throws IOException {
        int n;
        InputStream inputStream = this.in;
        int n2 = inputStream.read();
        if ((n2 | (n = inputStream.read())) < 0) {
            throw new EOFException();
        }
        return (short)((n << 8) + (n2 << 0));
    }

    @Override
    public final char readChar() throws IOException {
        int n;
        InputStream inputStream = this.in;
        int n2 = inputStream.read();
        if ((n2 | (n = inputStream.read())) < 0) {
            throw new EOFException();
        }
        return (char)((n << 8) + (n2 << 0));
    }

    @Override
    public final long readLong() throws IOException {
        InputStream inputStream = this.in;
        long l = this.readInt();
        int n = this.readInt();
        return ((long)n << 32) + (l & 0xFFFFFFFFL);
    }

    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public final void readFully(byte[] byArray) throws IOException {
    }

    @Override
    public final void readFully(byte[] byArray, int n, int n2) throws IOException {
    }

    @Override
    public final int skipBytes(int n) throws IOException {
        return 0;
    }

    @Override
    public final boolean readBoolean() throws IOException {
        int n = this.in.read();
        if (n < 0) {
            throw new EOFException();
        }
        return n != 0;
    }

    @Override
    public final byte readByte() throws IOException {
        int n = this.in.read();
        if (n < 0) {
            throw new EOFException();
        }
        return (byte)n;
    }

    @Override
    public final int readUnsignedByte() throws IOException {
        return 0;
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        return 0;
    }

    @Override
    public final String readLine() throws IOException {
        return null;
    }

    @Override
    public final String readUTF() throws IOException {
        return null;
    }

    public static final String readUTF(DataInput dataInput) throws IOException {
        return null;
    }
}

