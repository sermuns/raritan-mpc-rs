/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

import javaclientlib.utils.RRCUtil;

public class RC4Cipher {
    public int keySize = 256;
    private int[] state = new int[256];
    private int x;
    private int y;

    public RC4Cipher(byte[] byArray) {
        this.setKey(byArray);
    }

    public void setKey(byte[] byArray) {
        int n;
        for (n = 0; n < 256; ++n) {
            this.state[n] = (byte)n;
        }
        this.x = 0;
        this.y = 0;
        int n2 = 0;
        int n3 = 0;
        for (n = 0; n < 256; ++n) {
            n3 = byArray[n2] + this.state[n] + n3 & 0xFF;
            int n4 = this.state[n];
            this.state[n] = this.state[n3];
            this.state[n3] = n4;
            n2 = (n2 + 1) % byArray.length;
        }
    }

    public final byte encrypt(byte by) {
        return (byte)(by ^ this.state[this.nextState()]);
    }

    public final byte decrypt(byte by) {
        return (byte)(by ^ this.state[this.nextState()]);
    }

    public final void encrypt(byte[] byArray, int n, byte[] byArray2, int n2, int n3) {
        while (n3-- > 0) {
            int n4;
            this.x = this.x + 1 & 0xFF;
            int n5 = this.state[this.x];
            this.y = this.y + n5 & 0xFF;
            this.state[this.x] = n4 = this.state[this.y];
            this.state[this.y] = n5;
            byArray2[n2++] = (byte)(byArray[n++] ^ this.state[n5 + n4 & 0xFF]);
        }
    }

    public final void decrypt(byte[] byArray, int n, byte[] byArray2, int n2, int n3) {
        while (n3-- > 0) {
            int n4;
            this.x = this.x + 1 & 0xFF;
            int n5 = this.state[this.x];
            this.y = this.y + n5 & 0xFF;
            this.state[this.x] = n4 = this.state[this.y];
            this.state[this.y] = n5;
            byArray2[n2++] = (byte)(byArray[n++] ^ this.state[n5 + n4 & 0xFF]);
        }
    }

    private final int nextState() {
        int n;
        this.x = this.x + 1 & 0xFF;
        this.y = this.y + this.state[this.x] & 0xFF;
        int n2 = this.state[this.x];
        this.state[this.x] = n = this.state[this.y];
        this.state[this.y] = n2;
        return n2 + n & 0xFF;
    }

    public static void main(String[] stringArray) {
        byte[] byArray = new byte[]{120, -65, -57, -57, -24, 36, -29, -29, 48, 10, 25, -21, 19, -65, -37, -58};
        byte[] byArray2 = new byte[]{-26, 0, -71, 8, 0, -17, 39, -1, 114, -115, 78, 87, -31, 123};
        byte[] byArray3 = new byte[byArray2.length];
        RC4Cipher rC4Cipher = new RC4Cipher(byArray);
        RRCUtil.print("B4 Encrypt", byArray2, 0, byArray2.length);
        rC4Cipher = new RC4Cipher(byArray);
        rC4Cipher.encrypt(byArray2, 0, byArray3, 0, byArray2.length);
        RRCUtil.print("After Encrypt", byArray3, 0, byArray3.length);
        rC4Cipher = new RC4Cipher(byArray);
        rC4Cipher.decrypt(byArray3, 0, byArray2, 0, byArray3.length);
        RRCUtil.print("After Decrypt", byArray2, 0, byArray2.length);
    }
}

