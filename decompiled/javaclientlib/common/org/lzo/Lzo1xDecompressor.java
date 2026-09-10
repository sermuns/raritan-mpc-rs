/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common.org.lzo;

import javaclientlib.common.org.lzo.Constants;
import javaclientlib.common.org.lzo.Int;

public final class Lzo1xDecompressor
implements Constants {
    private static final String copyright = "LZO Copyright (C) 1996-1999 Markus F.X.J. Oberhumer <markus.oberhumer@jk.uni-linz.ac.at>";

    public final int decompress(byte[] byArray, int n, int n2, byte[] byArray2, int n3, Int intVal) {
        int n4;
        int n5 = n;
        int n6 = n3;
        if ((n4 = byArray[n5++] & 0xFF) > 17) {
            n4 -= 17;
            do {
                byArray2[n6++] = byArray[n5++];
            } while (--n4 > 0);
            if ((n4 = byArray[n5++] & 0xFF) < 16) {
                return -1;
            }
        }
        block1: while (true) {
            block32: {
                int n7;
                block31: {
                    if (n4 >= 16) break block31;
                    if (n4 == 0) {
                        while (byArray[n5] == 0) {
                            n4 += 255;
                            ++n5;
                        }
                        n4 += 15 + (byArray[n5++] & 0xFF);
                    }
                    n4 += 3;
                    do {
                        byArray2[n6++] = byArray[n5++];
                    } while (--n4 > 0);
                    if ((n4 = byArray[n5++] & 0xFF) >= 16) break block31;
                    if ((n7 = n6 - 2049 - (n4 >> 2) - ((byArray[n5++] & 0xFF) << 2)) < n3) {
                        n4 = -6;
                        break;
                    }
                    n4 = 3;
                    do {
                        byArray2[n6++] = byArray2[n7++];
                    } while (--n4 > 0);
                    n4 = byArray[n5 - 2] & 3;
                    if (n4 == 0) break block32;
                    do {
                        byArray2[n6++] = byArray[n5++];
                    } while (--n4 > 0);
                    n4 = byArray[n5++] & 0xFF;
                }
                while (true) {
                    if (n4 >= 64) {
                        n7 = n6 - 1 - (n4 >> 2 & 7) - ((byArray[n5++] & 0xFF) << 3);
                        n4 = (n4 >> 5) - 1;
                    } else if (n4 >= 32) {
                        if ((n4 &= 0x1F) == 0) {
                            while (byArray[n5] == 0) {
                                n4 += 255;
                                ++n5;
                            }
                            n4 += 31 + (byArray[n5++] & 0xFF);
                        }
                        n7 = n6 - 1 - ((byArray[n5++] & 0xFF) >> 2);
                        n7 -= (byArray[n5++] & 0xFF) << 6;
                    } else if (n4 >= 16) {
                        n7 = n6 - ((n4 & 8) << 11);
                        if ((n4 &= 7) == 0) {
                            while (byArray[n5] == 0) {
                                n4 += 255;
                                ++n5;
                            }
                            n4 += 7 + (byArray[n5++] & 0xFF);
                        }
                        n7 -= (byArray[n5++] & 0xFF) >> 2;
                        if ((n7 -= (byArray[n5++] & 0xFF) << 6) == n6) break block1;
                        n7 -= 16384;
                    } else {
                        n7 = n6 - 1 - (n4 >> 2) - ((byArray[n5++] & 0xFF) << 2);
                        n4 = 0;
                    }
                    if (n7 < n3) {
                        n4 = -6;
                        break block1;
                    }
                    n4 += 2;
                    do {
                        byArray2[n6++] = byArray2[n7++];
                    } while (--n4 > 0);
                    n4 = byArray[n5 - 2] & 3;
                    if (n4 == 0) break;
                    do {
                        byArray2[n6++] = byArray[n5++];
                    } while (--n4 > 0);
                    n4 = byArray[n5++] & 0xFF;
                }
            }
            n4 = byArray[n5++] & 0xFF;
        }
        n5 -= n;
        intVal.setValue(n6 -= n3);
        if (n4 < 0) {
            return n4;
        }
        if (n5 < n2) {
            return -8;
        }
        if (n5 > n2) {
            return -4;
        }
        if (n4 != 1) {
            return -1;
        }
        return 0;
    }
}

