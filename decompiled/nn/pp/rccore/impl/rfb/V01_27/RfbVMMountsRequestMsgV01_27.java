/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.IVMMountRequestResponse;

public class RfbVMMountsRequestMsgV01_27
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, IVMMountRequestResponse iVMMountRequestResponse) throws IOException {
        byte[] byArray = iVMMountRequestResponse.getUser().getBytes("ISO-8859-1");
        byte[] byArray2 = iVMMountRequestResponse.getPassword().getBytes("ISO-8859-1");
        byte[] byArray3 = iVMMountRequestResponse.getHost().getBytes("ISO-8859-1");
        byte[] byArray4 = iVMMountRequestResponse.getImage().getBytes("ISO-8859-1");
        this.write(166);
        this.write(iVMMountRequestResponse.getOption());
        this.write(iVMMountRequestResponse.getIndex());
        this.write(byArray.length);
        this.write(byArray2.length);
        this.write(byArray3.length);
        this.write(byArray4.length);
        this.write(0);
        this.write(byArray);
        this.write(byArray2);
        this.write(byArray3);
        this.write(byArray4);
        monitoringDataOutputStream.write(this);
    }
}

