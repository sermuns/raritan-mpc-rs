/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CSecureInputStream;
import amp.powerboard.clientapi.security.CXorKey;
import java.io.IOException;
import java.io.InputStream;

public final class CXorInputStream
extends CSecureInputStream {
    public CXorInputStream(InputStream inputStream, CCryptoKey cCryptoKey) {
        super(inputStream, cCryptoKey);
    }

    public int read() throws IOException {
        byte by = ((CXorKey)this.key).xor_key;
        int n = this.in.read();
        if (n == -1) {
            return -1;
        }
        return n ^ by;
    }

    public int read(byte[] byArray, int n, int n2) throws IOException {
        byte by = ((CXorKey)this.key).xor_key;
        byte[] byArray2 = new byte[n2];
        int n3 = this.in.read(byArray2);
        int n4 = 0;
        while (n4 < n3) {
            byArray[n++] = (byte)(byArray2[n4] ^ by);
            ++n4;
        }
        return n3;
    }
}

