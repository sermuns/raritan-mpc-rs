/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private MessageDigest objMessageDigest = MessageDigest.getInstance("MD5");

    public String encode(String string) {
        this.objMessageDigest.update(string.getBytes());
        return new String(this.objMessageDigest.digest());
    }

    public String encode(byte[] byArray) {
        this.objMessageDigest.update(byArray);
        return new String(this.objMessageDigest.digest());
    }

    public void update(String string) {
        this.objMessageDigest.update(string.getBytes());
    }

    public void update(byte[] byArray) {
        this.objMessageDigest.update(byArray);
    }

    public void update(byte[] byArray, int n) {
        this.objMessageDigest.update(byArray, 0, n);
    }

    public void update(byte[] byArray, int n, int n2) {
        this.objMessageDigest.update(byArray, n, n2);
    }

    public byte[] encode() {
        return this.objMessageDigest.digest();
    }
}

