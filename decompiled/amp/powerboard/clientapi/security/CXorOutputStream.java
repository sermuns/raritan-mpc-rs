/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CSecureOutputStream;
import amp.powerboard.clientapi.security.CXorKey;
import java.io.IOException;
import java.io.OutputStream;

public final class CXorOutputStream
extends CSecureOutputStream {
    public CXorOutputStream(OutputStream outputStream, CCryptoKey cCryptoKey) {
        super(outputStream, cCryptoKey);
    }

    public void write(byte[] byArray, int n, int n2) throws IOException {
        byte by = ((CXorKey)this.key).xor_key;
        byte[] byArray2 = new byte[n2];
        int n3 = 0;
        while (n3 < n2) {
            byArray2[n3] = (byte)(byArray[n++] ^ by);
            ++n3;
        }
        this.out.write(byArray2);
    }

    public void write(int n) throws IOException {
        byte by = ((CXorKey)this.key).xor_key;
        this.out.write(n ^ by);
    }
}

