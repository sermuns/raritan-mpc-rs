/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CSecureOutputStream;
import amp.powerboard.clientapi.security.RC4;
import amp.powerboard.clientapi.security.RC4CryptoKey;
import java.io.IOException;
import java.io.OutputStream;

public final class RC4OutputStream
extends CSecureOutputStream {
    RC4 rc4_key;

    public RC4OutputStream(OutputStream outputStream, CCryptoKey cCryptoKey) {
        super(outputStream, cCryptoKey);
        this.rc4_key = ((RC4CryptoKey)this.key).outKey;
    }

    public void write(byte[] byArray, int n, int n2) throws IOException {
        byte[] byArray2 = new byte[n2];
        if (this.encrypt) {
            this.rc4_key.rc4(byArray, byArray2, n2);
        } else {
            int n3 = 0;
            while (n3 < n2) {
                byArray2[n3] = (byte)(byArray[n3] ^ 0x37);
                ++n3;
            }
        }
        this.out.write(byArray2);
    }

    public void write(int n) throws IOException {
        byte[] byArray = new byte[4];
        byte[] byArray2 = new byte[4];
        byArray2[0] = (byte)n;
        if (this.encrypt) {
            this.rc4_key.rc4(byArray2, byArray, 1);
        } else {
            byArray[0] = (byte)(byArray2[0] ^ 0x37);
        }
        this.out.write(byArray[0]);
    }
}

