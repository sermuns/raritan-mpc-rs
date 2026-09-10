/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CCryptoKey;
import java.io.OutputStream;

public abstract class CSecureOutputStream
extends OutputStream {
    protected OutputStream out;
    protected CCryptoKey key;
    protected boolean encrypt;

    public CSecureOutputStream(OutputStream outputStream, CCryptoKey cCryptoKey) {
        this.out = outputStream;
        this.key = cCryptoKey;
        this.encrypt = false;
    }

    public void StartEncryption() {
        this.encrypt = true;
    }

    public void StopEncryption() {
        this.encrypt = false;
    }
}

