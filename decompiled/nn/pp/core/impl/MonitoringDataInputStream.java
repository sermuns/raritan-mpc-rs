/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

public class MonitoringDataInputStream
extends FilterInputStream {
    private int countC;
    private int buflen = 65536;
    private int count = 0;
    private byte[] buf = new byte[this.buflen];
    private int pos = 0;

    public MonitoringDataInputStream(InputStream inputStream) {
        super(inputStream);
    }

    private final int readAByte() throws IOException {
        if (this.count - this.pos <= 0) {
            return this.in.read();
        }
        return this.buf[this.pos++] & 0xFF;
    }

    private final int readBytes(byte[] byArray, int n, int n2) throws IOException {
        int n3 = this.count - this.pos;
        if (n3 <= 0) {
            return this.in.read(byArray, n, n2);
        }
        int n4 = Math.min(n2, n3);
        System.arraycopy(this.buf, this.pos, byArray, n, n4);
        this.pos += n4;
        return n4;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stall(byte[] byArray, int n, int n2) {
        if (n2 == 0) {
            return;
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            this.countC = this.countC - n2 > 0 ? (this.countC -= n2) : 0;
        }
        int n3 = this.count - this.pos;
        if (n3 > 0) {
            if (n3 + n2 > this.buflen) {
                this.buflen = n3 + n2;
                byte[] byArray2 = new byte[this.buflen];
                System.arraycopy(this.buf, this.pos, byArray2, n2, n3);
                this.buf = byArray2;
            } else {
                System.arraycopy(this.buf, this.pos, this.buf, n2, n3);
            }
        }
        this.pos = 0;
        this.count = n3 + n2;
        System.arraycopy(byArray, n, this.buf, this.pos, n2);
    }

    @Override
    public final int read(byte[] byArray) throws IOException {
        return this.read(byArray, 0, byArray.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final int read(byte[] byArray, int n, int n2) throws IOException {
        int n3 = this.readBytes(byArray, n, n2);
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            this.countC += n3;
        }
        return n3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final int read() throws IOException {
        int n = this.readAByte();
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            ++this.countC;
        }
        return n;
    }

    public final void readFully(byte[] byArray) throws IOException {
        this.readFully(byArray, 0, byArray.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void readFully(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        if (n2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < n2; i += n3) {
            n3 = this.readBytes(byArray, n + i, n2 - i);
            if (n3 >= 0) continue;
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            this.countC += n2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean readBoolean() throws IOException {
        int n = this.readAByte();
        if (n < 0) {
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            ++this.countC;
        }
        return n != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final byte readByte() throws IOException {
        int n = this.readAByte();
        if (n < 0) {
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            ++this.countC;
        }
        return (byte)n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final int readUnsignedByte() throws IOException {
        int n = this.readAByte();
        if (n < 0) {
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            ++this.countC;
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final short readShort() throws IOException {
        int n;
        int n2 = this.readAByte();
        if ((n2 | (n = this.readAByte())) < 0) {
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            this.countC += 2;
        }
        return (short)((n2 << 8) + (n << 0));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final int readUnsignedShort() throws IOException {
        int n;
        int n2 = this.readAByte();
        if ((n2 | (n = this.readAByte())) < 0) {
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            this.countC += 2;
        }
        return (n2 << 8) + (n << 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final char readChar() throws IOException {
        int n;
        int n2 = this.readAByte();
        if ((n2 | (n = this.readAByte())) < 0) {
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            this.countC += 2;
        }
        return (char)((n2 << 8) + (n << 0));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final int readInt() throws IOException {
        int n;
        int n2;
        int n3;
        int n4 = this.readAByte();
        if ((n4 | (n3 = this.readAByte()) | (n2 = this.readAByte()) | (n = this.readAByte())) < 0) {
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            this.countC += 4;
        }
        return (n4 << 24) + (n3 << 16) + (n2 << 8) + (n << 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final long readLong() throws IOException {
        long l;
        long l2;
        long l3;
        long l4;
        long l5;
        long l6;
        long l7;
        long l8 = this.readAByte();
        if ((l8 | (l7 = (long)this.readAByte()) | (l6 = (long)this.readAByte()) | (l5 = (long)this.readAByte()) | (l4 = (long)this.readAByte()) | (l3 = (long)this.readAByte()) | (l2 = (long)this.readAByte()) | (l = (long)this.readAByte())) < 0L) {
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            this.countC += 8;
        }
        return (l8 << 56) + (l7 << 48) + (l6 << 40) + (l5 << 32) + (l4 << 24) + (l3 << 16) + (l2 << 8) + (l << 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final long readUnsignedInt() throws IOException {
        long l;
        long l2;
        long l3;
        long l4 = this.readAByte();
        if ((l4 | (l3 = (long)this.readAByte()) | (l2 = (long)this.readAByte()) | (l = (long)this.readAByte())) < 0L) {
            throw new EOFException();
        }
        MonitoringDataInputStream monitoringDataInputStream = this;
        synchronized (monitoringDataInputStream) {
            this.countC += 4;
        }
        return (l4 << 24) + (l3 << 16) + (l2 << 8) + (l << 0);
    }

    public final String readUTF() throws IOException {
        int n = this.readUnsignedShort();
        StringBuffer stringBuffer = new StringBuffer(n);
        byte[] byArray = new byte[n];
        int n2 = 0;
        this.readFully(byArray, 0, n);
        block5: while (n2 < n) {
            int n3 = byArray[n2] & 0xFF;
            switch (n3 >> 4) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: {
                    ++n2;
                    stringBuffer.append((char)n3);
                    continue block5;
                }
                case 12: 
                case 13: {
                    if ((n2 += 2) > n) {
                        throw new UTFDataFormatException();
                    }
                    byte by = byArray[n2 - 1];
                    if ((by & 0xC0) != 128) {
                        throw new UTFDataFormatException();
                    }
                    stringBuffer.append((char)((n3 & 0x1F) << 6 | by & 0x3F));
                    continue block5;
                }
                case 14: {
                    if ((n2 += 3) > n) {
                        throw new UTFDataFormatException();
                    }
                    byte by = byArray[n2 - 2];
                    byte by2 = byArray[n2 - 1];
                    if ((by & 0xC0) != 128 || (by2 & 0xC0) != 128) {
                        throw new UTFDataFormatException();
                    }
                    stringBuffer.append((char)((n3 & 0xF) << 12 | (by & 0x3F) << 6 | (by2 & 0x3F) << 0));
                    continue block5;
                }
            }
            throw new UTFDataFormatException();
        }
        return new String(stringBuffer);
    }

    public final synchronized int getAndClearIn() {
        int n = this.countC;
        this.countC = 0;
        return n;
    }
}

