/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_02;

import java.io.IOException;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.vmcore.VMException;

public class MspAuthResponseMsgV01_02
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string) throws IOException, VMException {
        int n = string.length();
        if (n != 32 && n != 64) {
            throw new VMException(T._("Response has invalid length"));
        }
        byte[] byArray = new byte[n + 9];
        System.arraycopy("MSP RESP=".getBytes("ISO-8859-1"), 0, byArray, 0, 9);
        System.arraycopy(string.getBytes("ISO-8859-1"), 0, byArray, 9, n);
        this.write(byArray);
        monitoringDataOutputStream.write(this);
    }
}

