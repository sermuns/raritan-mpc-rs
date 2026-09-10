/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common.org.lzo;

public class Adler32 {
    private static final int BASE = 65521;
    private static final int NMAX = 2775;
    private int s1 = 1;
    private int s2 = 0;

    public void reset() {
        this.s1 = 1;
        this.s2 = 0;
    }

    public long getValue() {
        return (long)this.s2 << 16 | (long)this.s1;
    }

    public void update(int n) {
        this.s1 += n & 0xFF;
        this.s2 += this.s1;
        this.s1 %= 65521;
        this.s2 %= 65521;
    }

    public void update(byte[] byArray, int n, int n2) {
        while (n2 > 0) {
            int n3 = n2 < 2775 ? n2 : 2775;
            n2 -= n3;
            while (n3 >= 16) {
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                this.s1 += byArray[n++] & 0xFF;
                this.s2 += this.s1;
                n3 -= 16;
            }
            if (n3 != 0) {
                do {
                    this.s1 += byArray[n++] & 0xFF;
                    this.s2 += this.s1;
                } while (--n3 > 0);
            }
            this.s1 %= 65521;
            this.s2 %= 65521;
        }
    }

    public void update(byte[] byArray) {
        this.update(byArray, 0, byArray.length);
    }
}

