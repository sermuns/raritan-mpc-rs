/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CSecureInputStream;
import amp.powerboard.clientapi.security.CSecureOutputStream;
import amp.powerboard.clientapi.security.CXorInputStream;
import amp.powerboard.clientapi.security.CXorOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class CXorKey
extends CCryptoKey {
    public byte xor_key;

    public CXorKey(int n) {
        this.xor_key = (byte)n;
    }

    public CSecureInputStream newInputStream(InputStream inputStream) {
        return new CXorInputStream(inputStream, this);
    }

    public CSecureOutputStream newOutputStream(OutputStream outputStream) {
        return new CXorOutputStream(outputStream, this);
    }

    public void StartEncryption() {
    }

    public void StopEncryption() {
    }
}

