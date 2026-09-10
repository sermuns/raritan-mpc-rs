/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

public final class RC4 {
    private int x;
    private int y;
    byte[] S = new byte[256];

    public RC4(byte[] byArray, int n) {
        this.initKey(byArray, n);
    }

    public void rc4(byte[] byArray, byte[] byArray2, int n) {
        int n2 = 0;
        while (n2 < n) {
            this.x = this.x + 1 & 0xFF;
            this.y = this.S[this.x] + this.y & 0xFF;
            byte by = this.S[this.x];
            this.S[this.x] = this.S[this.y];
            this.S[this.y] = by;
            int n3 = this.S[this.x] + this.S[this.y] & 0xFF;
            byArray2[n2] = (byte)(byArray[n2] ^ this.S[n3]);
            ++n2;
        }
    }

    private void initKey(byte[] byArray, int n) {
        int n2 = 0;
        int n3 = 0;
        this.y = 0;
        this.x = 0;
        int n4 = 0;
        while (n4 < 256) {
            this.S[n4] = (byte)n4;
            ++n4;
        }
        int n5 = 0;
        while (n5 < 256) {
            n3 = (byArray[n2] & 0xFF) + this.S[n5] + n3 & 0xFF;
            byte by = this.S[n5];
            this.S[n5] = this.S[n3];
            this.S[n3] = by;
            n2 = (n2 + 1) % n;
            ++n5;
        }
    }
}

