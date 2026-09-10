/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.PrimeGenerator;

public class PrimeThread
extends Thread {
    PrimeGenerator primeGenerator;
    byte[] thisPrime1;
    byte[] thisPrime2;
    byte[] prime1;
    byte[] prime2;

    public PrimeThread(byte[] byArray, byte[] byArray2) {
        this.prime1 = byArray;
        this.prime2 = byArray2;
        try {
            this.primeGenerator = new PrimeGenerator();
        }
        catch (Exception exception) {}
    }

    public void run() {
        this.thisPrime1 = this.primeGenerator.GetPrime1(true);
        this.thisPrime2 = this.primeGenerator.GetPrime2(true);
        int n = 0;
        while (n < 32) {
            this.prime1[n] = this.thisPrime1[n];
            this.prime2[n] = this.thisPrime2[n];
            ++n;
        }
        this.stop();
        this.destroy();
    }
}

