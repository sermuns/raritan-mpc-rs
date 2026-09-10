/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.io.IOException;
import nn.pp.rccore.RCException;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.V01_00.RfbHelloMsgV01_00;
import nn.pp.rccore.impl.rfb.V01_00.RfbVersionMsgV01_00;

public class RfbVersionNegotiator
extends RfbHandler {
    private RfbHelloMsgV01_00 helloMsg;
    private RfbVersionMsgV01_00 versionMsg;

    @Override
    protected void loadPdus() {
        this.helloMsg = new RfbHelloMsgV01_00();
        this.versionMsg = new RfbVersionMsgV01_00();
    }

    @Override
    protected void processInitialHandshake(int n) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, RCException {
        this.helloMsg.write(this.os);
        this.versionMsg.read(this.is);
        this.versionMajor = this.versionMsg.versionMajor;
        this.versionMinor = this.versionMsg.versionMinor;
        this.versionMsg.write(this.os, this.versionMajor, this.versionMinor);
    }

    @Override
    protected boolean updateIsZlib() {
        return false;
    }

    @Override
    protected boolean updateRectIsZlibStreamed() {
        return false;
    }

    @Override
    protected boolean updateRectIsZlibCompress() {
        return false;
    }

    @Override
    protected int getUpdateRectEncoding() {
        return 0;
    }

    @Override
    protected int getUpdateRectSubencoding() {
        return 0;
    }
}

