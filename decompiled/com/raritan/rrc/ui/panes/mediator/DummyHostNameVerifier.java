/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes.mediator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

class DummyHostNameVerifier
implements HostnameVerifier {
    DummyHostNameVerifier() {
    }

    @Override
    public boolean verify(String string, SSLSession sSLSession) {
        return true;
    }
}

