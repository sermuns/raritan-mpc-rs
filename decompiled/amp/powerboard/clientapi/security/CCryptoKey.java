/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.security;

import amp.powerboard.clientapi.security.CSecureInputStream;
import amp.powerboard.clientapi.security.CSecureOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class CCryptoKey {
    public abstract CSecureInputStream newInputStream(InputStream var1);

    public abstract CSecureOutputStream newOutputStream(OutputStream var1);

    public abstract void StartEncryption();

    public abstract void StopEncryption();
}

