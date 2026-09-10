/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V01_00;

import java.io.IOException;
import nn.pp.audiocore.AudioException;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapAuthResponseMsgV01_00
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string) throws IOException, AudioException {
        int n = string.length();
        if (n != 32 && n != 64) {
            throw new AudioException(T._("Response has invalid length"));
        }
        byte[] byArray = new byte[n + 9];
        System.arraycopy("RAP RESP=".getBytes("ISO-8859-1"), 0, byArray, 0, 9);
        System.arraycopy(string.getBytes("ISO-8859-1"), 0, byArray, 9, n);
        this.write(byArray);
        monitoringDataOutputStream.write(this);
    }
}

