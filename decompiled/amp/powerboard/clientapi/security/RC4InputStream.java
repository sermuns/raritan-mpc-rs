/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CSecureInputStream;
import amp.powerboard.clientapi.security.RC4;
import amp.powerboard.clientapi.security.RC4CryptoKey;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class RC4InputStream
extends CSecureInputStream {
    RC4 rc4_key;

    public RC4InputStream(InputStream inputStream, CCryptoKey cCryptoKey) {
        super(inputStream, cCryptoKey);
        this.rc4_key = ((RC4CryptoKey)this.key).inKey;
    }

    public int read() throws IOException {
        byte[] byArray = new byte[1];
        byte[] byArray2 = new byte[1];
        int n = this.in.read();
        if (n == -1) {
            throw new EOFException();
        }
        byArray[0] = (byte)n;
        if (this.encrypt) {
            this.rc4_key.rc4(byArray, byArray2, 1);
            int n2 = byArray2[0];
            if (n2 < 0) {
                n2 += 256;
            }
            return n2;
        }
        return byArray[0] ^ 0x37;
    }

    public int read(byte[] byArray, int n, int n2) throws IOException {
        byte[] byArray2 = new byte[n2];
        int n3 = this.in.read(byArray2, 0, n2);
        if (this.encrypt) {
            if (n != 0) {
                byte[] byArray3 = new byte[n3];
                this.rc4_key.rc4(byArray2, byArray3, n3);
                System.arraycopy(byArray3, 0, byArray, n, n3);
            } else {
                this.rc4_key.rc4(byArray2, byArray, n3);
            }
        } else {
            int n4 = 0;
            while (n4 < n2) {
                byArray[n4 + n] = (byte)(byArray2[n4] ^ 0x37);
                ++n4;
            }
        }
        return n3;
    }
}

