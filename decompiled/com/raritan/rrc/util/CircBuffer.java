/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

public class CircBuffer {
    public static final int HISTORY_BUFFER_SIZE = 16384;
    private StringBuffer buffer;

    public CircBuffer() {
        this.buffer = new StringBuffer();
    }

    public CircBuffer(int n) {
        if (n > 0) {
            this.buffer = new StringBuffer(n);
        }
    }

    public CircBuffer(CircBuffer circBuffer) {
        if (circBuffer == null) {
            return;
        }
        if (circBuffer.getSize() != 0) {
            this.buffer = new StringBuffer(circBuffer.getSize());
        }
    }

    public final int getSize() {
        if (this.buffer != null) {
            return this.buffer.capacity();
        }
        return 0;
    }

    public final int getCount() {
        if (this.buffer != null) {
            return this.buffer.length();
        }
        return 0;
    }

    public int getMaxBytesToRead() {
        int n = this.getCount();
        if (n > 16384) {
            n = 16384;
        }
        return n;
    }

    public final boolean isEmpty() {
        return this.buffer.length() == 0;
    }

    public final boolean canHold() {
        return this.buffer.capacity() - this.getSize() > 0;
    }

    public void empty() {
        if (this.buffer != null) {
            this.buffer.delete(0, this.buffer.length());
        }
    }

    private char[] getBuffer() {
        return this.buffer.toString().toCharArray();
    }

    public void readBulk(char[] cArray, int n) {
        if (cArray == null || n == 0 || this.isEmpty()) {
            return;
        }
        if (n <= this.getCount()) {
            System.arraycopy(this.buffer.toString().toCharArray(), 0, cArray, 0, n);
        }
    }

    public void readHistory(byte[] byArray) {
        if (byArray == null) {
            return;
        }
        byte[] byArray2 = this.buffer.substring(0, this.getMaxBytesToRead()).getBytes();
        System.arraycopy(byArray2, 0, byArray, 0, byArray2.length);
    }

    public void writeBulk(char[] cArray, int n) {
        if (cArray == null || n == 0) {
            return;
        }
        if (cArray.length <= n) {
            this.buffer.append(cArray);
        }
    }

    public void finalize() {
        this.empty();
        this.buffer = null;
        try {
            super.finalize();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

