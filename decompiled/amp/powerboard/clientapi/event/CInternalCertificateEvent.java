/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalCertificateEvent
extends CDataEvent {
    private int certType;
    private String cert;

    public CInternalCertificateEvent(int n, String string) {
        super(444);
        this.certType = n;
        this.cert = string;
    }

    public int getCertType() {
        return this.certType;
    }

    public String getCert() {
        return this.cert;
    }
}

