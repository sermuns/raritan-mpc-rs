/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CCryptoKey;
import java.io.IOException;
import java.io.InputStream;

public abstract class CSecureInputStream
extends InputStream {
    protected InputStream in;
    protected CCryptoKey key;
    protected boolean encrypt;

    public CSecureInputStream(InputStream inputStream, CCryptoKey cCryptoKey) {
        this.in = inputStream;
        this.key = cCryptoKey;
        this.encrypt = false;
    }

    public int available() throws IOException {
        return this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
    }

    public long skip(long l) throws IOException {
        return this.in.skip(l);
    }

    public void StartEncryption() {
        this.encrypt = true;
    }

    public void StopEncryption() {
        this.encrypt = false;
    }
}

