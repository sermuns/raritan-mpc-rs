/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalCertificateEvent;

public class CGetCertificateEvent
extends CDataEvent {
    private int certType;
    private String cert;

    public CGetCertificateEvent(int n, String string, boolean bl, String string2) {
        super(44, bl);
        this.certType = n;
        this.cert = string;
        this.lockerName = string2;
    }

    public CGetCertificateEvent(boolean bl, String string) {
        super(44, bl);
        this.lockerName = string;
    }

    public int getCertType() {
        return this.certType;
    }

    public String getCert() {
        return this.cert;
    }

    public void setCertificateData(CInternalCertificateEvent cInternalCertificateEvent) {
        this.certType = cInternalCertificateEvent.getCertType();
        this.cert = cInternalCertificateEvent.getCert();
    }
}

