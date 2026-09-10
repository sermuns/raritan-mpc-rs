/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import nn.pp.rccore.scan.impl.rsp.V01_00.AuthResponse;
import nn.pp.rccore.scan.impl.rsp.V01_00.PingRequest;
import nn.pp.rccore.scan.impl.rsp.V01_00.QuitRequestFromServer;
import nn.pp.rccore.scan.impl.rsp.V01_00.ScanSessionResponse;

public interface RSPMessageHandlerV01_00 {
    public void handleAuthResponse(AuthResponse var1);

    public void handlePingRequest(PingRequest var1);

    public void handleQuitRequestFromServer(QuitRequestFromServer var1);

    public void handleScanSessionResponse(ScanSessionResponse var1);
}

