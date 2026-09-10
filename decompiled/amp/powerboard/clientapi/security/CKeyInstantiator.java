/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CXorKey;
import amp.powerboard.clientapi.security.RC4CryptoKey;

public class CKeyInstantiator {
    private CCryptoKey consoleDecrypt = null;
    private CCryptoKey consoleEncrypt = null;
    private CCryptoKey commandDecrypt = null;
    private CCryptoKey commandEncrypt = null;

    public CKeyInstantiator(String string) {
        if (string != null && string.length() > 20) {
            try {
                int n = string.length() == 40 ? 10 : (string.length() == 56 ? 14 : 32);
                this.consoleDecrypt = new RC4CryptoKey(string.substring(0, n));
                this.consoleEncrypt = new RC4CryptoKey(string.substring(n, n * 2));
                this.commandDecrypt = new RC4CryptoKey(string.substring(n * 2, n * 3));
                this.commandEncrypt = new RC4CryptoKey(string.substring(n * 3, n * 4));
            }
            catch (Exception exception) {}
        } else {
            this.commandDecrypt = this.commandEncrypt = new CXorKey(55);
            this.consoleEncrypt = this.commandEncrypt;
            this.consoleDecrypt = this.commandEncrypt;
        }
    }

    public CCryptoKey getConsoleDecryptKey() {
        if (this.consoleDecrypt == null) {
            System.err.println("CKeyInstantiator not constructed!");
        }
        return this.consoleDecrypt;
    }

    public CCryptoKey getConsoleEncryptKey() {
        if (this.consoleEncrypt == null) {
            System.err.println("CKeyInstantiator not constructed!");
        }
        return this.consoleEncrypt;
    }

    public CCryptoKey getCommandDecryptKey() {
        if (this.commandDecrypt == null) {
            System.err.println("KeyInstantiator not constructed!");
        }
        return this.commandDecrypt;
    }

    public CCryptoKey getCommandEncryptKey() {
        if (this.commandEncrypt == null) {
            System.err.println("CKeyInstantiator not constructed!");
        }
        return this.commandEncrypt;
    }
}

