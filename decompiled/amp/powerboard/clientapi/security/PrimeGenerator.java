/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import java.math.BigInteger;
import java.util.Random;

public class PrimeGenerator {
    BigInteger iprime1;
    BigInteger iprime2;

    public PrimeGenerator() throws Exception {
        Random random = new Random();
        BigInteger bigInteger = new BigInteger(255, 8, random);
    }

    public byte[] GetPrime1(boolean bl) {
        byte[] byArray = new byte[32];
        if (bl) {
            Random random = new Random();
            this.iprime1 = new BigInteger(255, 8192, random);
            byte[] byArray2 = this.iprime1.toByteArray();
            int n = 0;
            while (n < 32) {
                byArray[n] = byArray2[n];
                ++n;
            }
            return byArray;
        }
        int[] nArray = new int[]{209, 40, 162, 53, 109, 238, 26, 190, 73, 14, 140, 8, 222, 133, 76, 12, 172, 26, 4, 31, 231, 164, 55, 77, 138, 9, 50, 133, 175, 118, 37, 213};
        int n = 0;
        while (n < 32) {
            byArray[n] = (byte)nArray[n];
            ++n;
        }
        return byArray;
    }

    public byte[] GetPrime2(boolean bl) {
        byte[] byArray = new byte[32];
        if (bl) {
            Random random = new Random();
            this.iprime2 = new BigInteger(255, 8192, random);
            byte[] byArray2 = this.iprime2.toByteArray();
            int n = 0;
            while (n < 32) {
                byArray[n] = byArray2[n];
                ++n;
            }
            return byArray;
        }
        int[] nArray = new int[]{195, 119, 183, 173, 207, 149, 38, 147, 179, 172, 123, 131, 163, 166, 224, 166, 53, 0, 1, 115, 62, 191, 119, 220, 166, 199, 217, 72, 211, 186, 87, 185};
        int n = 0;
        while (n < 32) {
            byArray[n] = (byte)nArray[n];
            ++n;
        }
        return byArray;
    }
}

