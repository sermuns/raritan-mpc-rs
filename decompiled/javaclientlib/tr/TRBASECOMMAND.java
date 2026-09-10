/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public class TRBASECOMMAND {
    private byte[] byData;

    public TRBASECOMMAND() {
        if (this.byData == null) {
            this.byData = new byte[4108];
        }
    }

    protected TRBASECOMMAND(short s) {
        if (this.byData == null) {
            short s2 = s;
            this.byData = new byte[s2];
        }
    }

    protected TRBASECOMMAND(int n) {
        if (this.byData == null) {
            this.byData = new byte[n];
        }
    }

    public TRBASECOMMAND(byte[] byArray) {
        this.byData = byArray;
    }

    public byte[] toByteArray() {
        return this.byData;
    }

    public void fromByteArray(byte[] byArray) {
        this.byData = byArray;
    }

    public void setBoolean(boolean bl, int n) {
        this.byData[n] = (byte)(bl ? 1 : 0);
    }

    public boolean getBoolean(int n) {
        return this.byData[n] == 1;
    }

    public void setByte(byte by, int n) {
        this.byData[n] = by;
    }

    public byte getByte(int n) {
        return this.byData[n];
    }

    public void setShort(short s, int n) {
        this.byData[n + 1] = (byte)(s >>> 0 & 0xFF);
        this.byData[n + 0] = (byte)(s >>> 8 & 0xFF);
    }

    public short getShort(int n) {
        int n2 = (this.byData[n + 0] & 0xFF) >> 0;
        int n3 = (this.byData[n + 1] & 0xFF) >> 0;
        return (short)((n2 << 8) + (n3 << 0));
    }

    public int getUShort(int n) {
        int n2 = (this.byData[n + 0] & 0xFF) >> 0;
        int n3 = (this.byData[n + 1] & 0xFF) >> 0;
        return (n2 << 8) + (n3 << 0);
    }

    public void setInt(int n, int n2) {
        this.byData[n2 + 3] = (byte)(n >>> 0 & 0xFF);
        this.byData[n2 + 2] = (byte)(n >>> 8 & 0xFF);
        this.byData[n2 + 1] = (byte)(n >>> 16 & 0xFF);
        this.byData[n2 + 0] = (byte)(n >>> 24 & 0xFF);
    }

    public int getInt(int n) {
        int n2 = (this.byData[n + 0] & 0xFF) >> 0;
        int n3 = (this.byData[n + 1] & 0xFF) >> 0;
        int n4 = (this.byData[n + 2] & 0xFF) >> 0;
        int n5 = (this.byData[n + 3] & 0xFF) >> 0;
        return (n2 << 24) + (n3 << 16) + (n4 << 8) + (n5 << 0);
    }

    public void setBytes(byte[] byArray, int n) {
        System.arraycopy(byArray, 0, this.byData, n, byArray.length);
    }

    public byte[] getBytes(int n, int n2) {
        byte[] byArray = new byte[n2];
        System.arraycopy(this.byData, n, byArray, 0, byArray.length);
        return byArray;
    }

    public int[] getIntArray(int n, int n2) {
        int[] nArray = new int[n2];
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = this.getInt(n);
            n += 4;
        }
        return nArray;
    }
}

