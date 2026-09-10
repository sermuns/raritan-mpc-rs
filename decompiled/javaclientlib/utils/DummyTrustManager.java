/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.net.ssl.X509TrustManager
 */
package javaclientlib.utils;

import com.sun.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

class DummyTrustManager
implements X509TrustManager {
    DummyTrustManager() {
    }

    public boolean isClientTrusted(X509Certificate[] x509CertificateArray) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] x509CertificateArray) {
        return true;
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

