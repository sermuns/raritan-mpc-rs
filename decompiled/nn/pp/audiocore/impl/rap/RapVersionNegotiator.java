/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap;

import java.io.IOException;
import nn.pp.audiocore.AudioException;
import nn.pp.audiocore.impl.rap.RapHandler;
import nn.pp.audiocore.impl.rap.V01_00.RapInitMsgV01_00;
import nn.pp.audiocore.impl.rap.V01_00.RapVersionMsgV01_00;

public class RapVersionNegotiator
extends RapHandler {
    private RapInitMsgV01_00 initMsg;
    private RapVersionMsgV01_00 versionMsg;

    @Override
    protected void loadPdus() {
        this.initMsg = new RapInitMsgV01_00();
        this.versionMsg = new RapVersionMsgV01_00();
    }

    @Override
    protected void processInitialHandshake(int n) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, AudioException {
        this.initMsg.write(this.os);
        this.versionMsg.read(this.is);
        this.versionMajor = this.versionMsg.versionMajor;
        this.versionMinor = this.versionMsg.versionMinor;
        this.versionMsg.write(this.os, this.versionMajor, this.versionMinor);
    }
}

