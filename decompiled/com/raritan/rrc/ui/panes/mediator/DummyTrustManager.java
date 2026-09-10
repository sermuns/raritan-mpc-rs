/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes.mediator;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

class DummyTrustManager
implements X509TrustManager {
    DummyTrustManager() {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509CertificateArray, String string) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509CertificateArray, String string) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

