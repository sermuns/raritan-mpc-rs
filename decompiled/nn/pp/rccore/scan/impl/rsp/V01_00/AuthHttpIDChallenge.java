/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.rsp.MessageToCommandProducer;

public class AuthHttpIDChallenge
implements MessageToCommandProducer<Object> {
    private final byte[] challenge;

    public AuthHttpIDChallenge() {
        this(null);
    }

    private AuthHttpIDChallenge(byte[] byArray) {
        this.challenge = byArray;
    }

    public AuthHttpIDChallenge readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, ScanCoreException {
        byte[] byArray = new byte[73];
        byte[] byArray2 = new byte[64];
        monitoringDataInputStream.readFully(byArray);
        if (!new String(byArray, "ISO-8859-1").substring(0, 9).equals("RSP CHAL=")) {
            throw new ScanCoreException("Device doesn't have valid authentication challenge format");
        }
        System.arraycopy(byArray, 9, byArray2, 0, byArray2.length);
        return new AuthHttpIDChallenge(byArray2);
    }

    @Override
    public void visit(Object object) {
    }

    public byte[] getChallenge() {
        return this.challenge;
    }
}

