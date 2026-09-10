/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CSecureInputStream;
import amp.powerboard.clientapi.security.CSecureOutputStream;
import amp.powerboard.clientapi.security.RC4;
import amp.powerboard.clientapi.security.RC4InputStream;
import amp.powerboard.clientapi.security.RC4OutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class RC4CryptoKey
extends CCryptoKey {
    public byte[] key = new byte[16];
    public int keyLen;
    public RC4 inKey;
    public RC4 outKey;
    private CSecureInputStream inpStream = null;
    private CSecureOutputStream outStream = null;

    public RC4CryptoKey(String string) {
        this.keyLen = string.length() / 2;
        int n = 0;
        while (n < this.keyLen) {
            this.key[n] = (byte)Integer.parseInt(string.substring(n * 2, n * 2 + 2), 16);
            ++n;
        }
    }

    public RC4CryptoKey(byte[] byArray) {
        this.keyLen = byArray.length;
        int n = 0;
        while (n < this.keyLen) {
            this.key[n] = byArray[n];
            ++n;
        }
    }

    public CSecureInputStream newInputStream(InputStream inputStream) {
        this.inKey = new RC4(this.key, this.keyLen);
        this.inpStream = new RC4InputStream(inputStream, this);
        return this.inpStream;
    }

    public CSecureOutputStream newOutputStream(OutputStream outputStream) {
        this.outKey = new RC4(this.key, this.keyLen);
        this.outStream = new RC4OutputStream(outputStream, this);
        return this.outStream;
    }

    public void StartEncryption() {
        if (this.inpStream != null) {
            this.inpStream.StartEncryption();
        }
        if (this.outStream != null) {
            this.outStream.StartEncryption();
        }
    }

    public void StopEncryption() {
        if (this.inpStream != null) {
            this.inpStream.StopEncryption();
        }
        if (this.outStream != null) {
            this.outStream.StopEncryption();
        }
    }
}

