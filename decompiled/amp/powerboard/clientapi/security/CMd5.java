/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

public final class CMd5 {
    private int[] state = new int[4];
    private int[] count = new int[2];
    private byte[] buffer = new byte[64];
    private static final int S11 = 7;
    private static final int S12 = 12;
    private static final int S13 = 17;
    private static final int S14 = 22;
    private static final int S21 = 5;
    private static final int S22 = 9;
    private static final int S23 = 14;
    private static final int S24 = 20;
    private static final int S31 = 4;
    private static final int S32 = 11;
    private static final int S33 = 16;
    private static final int S34 = 23;
    private static final int S41 = 6;
    private static final int S42 = 10;
    private static final int S43 = 15;
    private static final int S44 = 21;
    private static final byte[] PADDING = new byte[]{-128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private static int F(int n, int n2, int n3) {
        return n & n2 | ~n & n3;
    }

    private static int G(int n, int n2, int n3) {
        return n & n3 | n2 & ~n3;
    }

    private static int H(int n, int n2, int n3) {
        return n ^ n2 ^ n3;
    }

    private static int I(int n, int n2, int n3) {
        return n2 ^ (n | ~n3);
    }

    private static int ROTATE_LEFT(int n, int n2) {
        return n << n2 | n >>> 32 - n2;
    }

    private static int FF(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        n += CMd5.F(n2, n3, n4) + n5 + n7;
        n = CMd5.ROTATE_LEFT(n, n6);
        return n += n2;
    }

    private static int GG(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        n += CMd5.G(n2, n3, n4) + n5 + n7;
        n = CMd5.ROTATE_LEFT(n, n6);
        return n += n2;
    }

    private static int HH(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        n += CMd5.H(n2, n3, n4) + n5 + n7;
        n = CMd5.ROTATE_LEFT(n, n6);
        return n += n2;
    }

    private static int II(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        n += CMd5.I(n2, n3, n4) + n5 + n7;
        n = CMd5.ROTATE_LEFT(n, n6);
        return n += n2;
    }

    public CMd5() {
        this.reset();
    }

    private void reset() {
        this.count[0] = 0;
        this.count[1] = 0;
        this.state[0] = 1732584193;
        this.state[1] = -271733879;
        this.state[2] = -1732584194;
        this.state[3] = 271733878;
    }

    public void update(byte[] byArray) {
        int n;
        int n2 = this.count[0] >> 3 & 0x3F;
        this.count[0] = this.count[0] + (byArray.length << 3);
        if (this.count[0] < byArray.length << 3) {
            this.count[1] = this.count[1] + 1;
        }
        this.count[1] = this.count[1] + (byArray.length >> 29);
        int n3 = 64 - n2;
        if (byArray.length >= n3) {
            System.arraycopy(byArray, 0, this.buffer, n2, n3);
            this.MD5Transform(this.buffer, 0);
            n = n3;
            while (n + 63 < byArray.length) {
                this.MD5Transform(byArray, n);
                n += 64;
            }
            n2 = 0;
        } else {
            n = 0;
        }
        System.arraycopy(byArray, n, this.buffer, n2, byArray.length - n);
    }

    public byte[] digest() {
        byte[] byArray = new byte[16];
        byte[] byArray2 = new byte[8];
        CMd5.Encode(byArray2, this.count);
        int n = this.count[0] >>> 3 & 0x3F;
        int n2 = n < 56 ? 56 - n : 120 - n;
        byte[] byArray3 = new byte[n2];
        System.arraycopy(PADDING, 0, byArray3, 0, n2);
        this.update(byArray3);
        this.update(byArray2);
        CMd5.Encode(byArray, this.state);
        this.reset();
        int n3 = 0;
        while (n3 < this.buffer.length) {
            this.buffer[n3] = 0;
            ++n3;
        }
        return byArray;
    }

    private void MD5Transform(byte[] byArray, int n) {
        int n2 = this.state[0];
        int n3 = this.state[1];
        int n4 = this.state[2];
        int n5 = this.state[3];
        int[] nArray = CMd5.Decode(byArray, n, 64);
        n2 = CMd5.FF(n2, n3, n4, n5, nArray[0], 7, -680876936);
        n5 = CMd5.FF(n5, n2, n3, n4, nArray[1], 12, -389564586);
        n4 = CMd5.FF(n4, n5, n2, n3, nArray[2], 17, 606105819);
        n3 = CMd5.FF(n3, n4, n5, n2, nArray[3], 22, -1044525330);
        n2 = CMd5.FF(n2, n3, n4, n5, nArray[4], 7, -176418897);
        n5 = CMd5.FF(n5, n2, n3, n4, nArray[5], 12, 1200080426);
        n4 = CMd5.FF(n4, n5, n2, n3, nArray[6], 17, -1473231341);
        n3 = CMd5.FF(n3, n4, n5, n2, nArray[7], 22, -45705983);
        n2 = CMd5.FF(n2, n3, n4, n5, nArray[8], 7, 1770035416);
        n5 = CMd5.FF(n5, n2, n3, n4, nArray[9], 12, -1958414417);
        n4 = CMd5.FF(n4, n5, n2, n3, nArray[10], 17, -42063);
        n3 = CMd5.FF(n3, n4, n5, n2, nArray[11], 22, -1990404162);
        n2 = CMd5.FF(n2, n3, n4, n5, nArray[12], 7, 1804603682);
        n5 = CMd5.FF(n5, n2, n3, n4, nArray[13], 12, -40341101);
        n4 = CMd5.FF(n4, n5, n2, n3, nArray[14], 17, -1502002290);
        n3 = CMd5.FF(n3, n4, n5, n2, nArray[15], 22, 1236535329);
        n2 = CMd5.GG(n2, n3, n4, n5, nArray[1], 5, -165796510);
        n5 = CMd5.GG(n5, n2, n3, n4, nArray[6], 9, -1069501632);
        n4 = CMd5.GG(n4, n5, n2, n3, nArray[11], 14, 643717713);
        n3 = CMd5.GG(n3, n4, n5, n2, nArray[0], 20, -373897302);
        n2 = CMd5.GG(n2, n3, n4, n5, nArray[5], 5, -701558691);
        n5 = CMd5.GG(n5, n2, n3, n4, nArray[10], 9, 38016083);
        n4 = CMd5.GG(n4, n5, n2, n3, nArray[15], 14, -660478335);
        n3 = CMd5.GG(n3, n4, n5, n2, nArray[4], 20, -405537848);
        n2 = CMd5.GG(n2, n3, n4, n5, nArray[9], 5, 568446438);
        n5 = CMd5.GG(n5, n2, n3, n4, nArray[14], 9, -1019803690);
        n4 = CMd5.GG(n4, n5, n2, n3, nArray[3], 14, -187363961);
        n3 = CMd5.GG(n3, n4, n5, n2, nArray[8], 20, 1163531501);
        n2 = CMd5.GG(n2, n3, n4, n5, nArray[13], 5, -1444681467);
        n5 = CMd5.GG(n5, n2, n3, n4, nArray[2], 9, -51403784);
        n4 = CMd5.GG(n4, n5, n2, n3, nArray[7], 14, 1735328473);
        n3 = CMd5.GG(n3, n4, n5, n2, nArray[12], 20, -1926607734);
        n2 = CMd5.HH(n2, n3, n4, n5, nArray[5], 4, -378558);
        n5 = CMd5.HH(n5, n2, n3, n4, nArray[8], 11, -2022574463);
        n4 = CMd5.HH(n4, n5, n2, n3, nArray[11], 16, 1839030562);
        n3 = CMd5.HH(n3, n4, n5, n2, nArray[14], 23, -35309556);
        n2 = CMd5.HH(n2, n3, n4, n5, nArray[1], 4, -1530992060);
        n5 = CMd5.HH(n5, n2, n3, n4, nArray[4], 11, 1272893353);
        n4 = CMd5.HH(n4, n5, n2, n3, nArray[7], 16, -155497632);
        n3 = CMd5.HH(n3, n4, n5, n2, nArray[10], 23, -1094730640);
        n2 = CMd5.HH(n2, n3, n4, n5, nArray[13], 4, 681279174);
        n5 = CMd5.HH(n5, n2, n3, n4, nArray[0], 11, -358537222);
        n4 = CMd5.HH(n4, n5, n2, n3, nArray[3], 16, -722521979);
        n3 = CMd5.HH(n3, n4, n5, n2, nArray[6], 23, 76029189);
        n2 = CMd5.HH(n2, n3, n4, n5, nArray[9], 4, -640364487);
        n5 = CMd5.HH(n5, n2, n3, n4, nArray[12], 11, -421815835);
        n4 = CMd5.HH(n4, n5, n2, n3, nArray[15], 16, 530742520);
        n3 = CMd5.HH(n3, n4, n5, n2, nArray[2], 23, -995338651);
        n2 = CMd5.II(n2, n3, n4, n5, nArray[0], 6, -198630844);
        n5 = CMd5.II(n5, n2, n3, n4, nArray[7], 10, 1126891415);
        n4 = CMd5.II(n4, n5, n2, n3, nArray[14], 15, -1416354905);
        n3 = CMd5.II(n3, n4, n5, n2, nArray[5], 21, -57434055);
        n2 = CMd5.II(n2, n3, n4, n5, nArray[12], 6, 1700485571);
        n5 = CMd5.II(n5, n2, n3, n4, nArray[3], 10, -1894986606);
        n4 = CMd5.II(n4, n5, n2, n3, nArray[10], 15, -1051523);
        n3 = CMd5.II(n3, n4, n5, n2, nArray[1], 21, -2054922799);
        n2 = CMd5.II(n2, n3, n4, n5, nArray[8], 6, 1873313359);
        n5 = CMd5.II(n5, n2, n3, n4, nArray[15], 10, -30611744);
        n4 = CMd5.II(n4, n5, n2, n3, nArray[6], 15, -1560198380);
        n3 = CMd5.II(n3, n4, n5, n2, nArray[13], 21, 1309151649);
        n2 = CMd5.II(n2, n3, n4, n5, nArray[4], 6, -145523070);
        n5 = CMd5.II(n5, n2, n3, n4, nArray[11], 10, -1120210379);
        n4 = CMd5.II(n4, n5, n2, n3, nArray[2], 15, 718787259);
        n3 = CMd5.II(n3, n4, n5, n2, nArray[9], 21, -343485551);
        this.state[0] = this.state[0] + n2;
        this.state[1] = this.state[1] + n3;
        this.state[2] = this.state[2] + n4;
        this.state[3] = this.state[3] + n5;
        int n6 = 0;
        while (n6 < nArray.length) {
            nArray[n6] = 0;
            ++n6;
        }
    }

    private static void Encode(byte[] byArray, int[] nArray) {
        int n = 0;
        int n2 = 0;
        while (n2 < byArray.length) {
            byArray[n2] = (byte)(nArray[n] & 0xFF);
            byArray[n2 + 1] = (byte)(nArray[n] >>> 8 & 0xFF);
            byArray[n2 + 2] = (byte)(nArray[n] >>> 16 & 0xFF);
            byArray[n2 + 3] = (byte)(nArray[n] >>> 24 & 0xFF);
            ++n;
            n2 += 4;
        }
    }

    private static int[] Decode(byte[] byArray, int n, int n2) {
        int[] nArray = new int[n2 >> 2];
        int n3 = 0;
        int n4 = n;
        while (n4 < n2 + n) {
            nArray[n3] = byArray[n4] & 0xFF | (byArray[n4 + 1] & 0xFF) << 8 | (byArray[n4 + 2] & 0xFF) << 16 | (byArray[n4 + 3] & 0xFF) << 24;
            ++n3;
            n4 += 4;
        }
        return nArray;
    }
}

