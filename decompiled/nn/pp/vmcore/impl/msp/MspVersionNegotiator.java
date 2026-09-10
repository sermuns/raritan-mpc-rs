/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp;

import java.io.IOException;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.msp.MspHandler;
import nn.pp.vmcore.impl.msp.V01_00.MspHelloMsgV01_00;
import nn.pp.vmcore.impl.msp.V01_00.MspVersionMsgV01_00;

public class MspVersionNegotiator
extends MspHandler {
    private MspHelloMsgV01_00 helloMsg;
    private MspVersionMsgV01_00 versionMsg;

    @Override
    protected void loadPdus() {
        this.helloMsg = new MspHelloMsgV01_00();
        this.versionMsg = new MspVersionMsgV01_00();
    }

    @Override
    protected void processInitialHandshake(int n) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, VMException {
        this.helloMsg.write(this.os);
        this.versionMsg.read(this.is);
        this.versionMajor = this.versionMsg.versionMajor;
        this.versionMinor = this.versionMsg.versionMinor;
        this.versionMsg.write(this.os, this.versionMajor, this.versionMinor);
    }
}

