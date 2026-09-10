/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_02;

import java.io.IOException;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.vmcore.VMException;

public class MspAuthChallengeMsgV01_02
extends ProtocolMessage {
    public byte[] challenge;

    public void read(MonitoringDataInputStream monitoringDataInputStream) throws IOException, VMException {
        byte[] byArray = new byte[73];
        this.challenge = new byte[64];
        monitoringDataInputStream.readFully(byArray);
        if (!new String(byArray).substring(0, 9).equals("MSP CHAL=")) {
            throw new VMException(T._("Device doesn't have valid authentication challenge format"));
        }
        System.arraycopy(byArray, 9, this.challenge, 0, this.challenge.length);
    }
}

