/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_00;

import java.io.IOException;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.RCException;

public class RfbVersionMsgV01_00
extends ProtocolMessage {
    public int versionMajor;
    public int versionMinor;

    public void read(MonitoringDataInputStream monitoringDataInputStream) throws IOException, RCException {
        byte[] byArray = new byte[16];
        monitoringDataInputStream.readFully(byArray);
        String string = new String(byArray);
        if (!string.substring(0, 10).equals("e-RIC RFB xx.xx\n".substring(0, 10)) || byArray[10] < 48 || byArray[10] > 57 || byArray[11] < 48 || byArray[11] > 57 || byArray[12] != 46 || byArray[13] < 48 || byArray[13] > 57 || byArray[14] < 48 || byArray[14] > 57 || byArray[15] != 10) {
            throw new RCException(T._("Device hasn't a valid server version (protocol error)"));
        }
        this.versionMajor = (byArray[10] - 48) * 10 + (byArray[11] - 48);
        this.versionMinor = (byArray[13] - 48) * 10 + (byArray[14] - 48);
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n, int n2) throws IOException {
        String string = "" + n;
        if (n < 10) {
            string = "0" + string;
        }
        String string2 = "" + n2;
        if (n2 < 10) {
            string2 = "0" + string2;
        }
        String string3 = "e-RIC RFB xx.xx\n".substring(0, 10) + string + "." + string2 + "\n";
        byte[] byArray = string3.getBytes();
        this.write(byArray);
        monitoringDataOutputStream.write(this);
    }
}

