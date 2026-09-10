/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V01_00;

import java.io.IOException;
import nn.pp.audiocore.AudioException;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapAuthChallengeMsgV01_00
extends ProtocolMessage {
    public byte[] challenge;

    public void read(MonitoringDataInputStream monitoringDataInputStream) throws IOException, AudioException {
        byte[] byArray = new byte[73];
        this.challenge = new byte[64];
        monitoringDataInputStream.readFully(byArray);
        if (!new String(byArray).substring(0, 9).equals("RAP CHAL=")) {
            throw new AudioException(T._("Device doesn't have valid authentication challenge format"));
        }
        System.arraycopy(byArray, 9, this.challenge, 0, this.challenge.length);
    }
}

