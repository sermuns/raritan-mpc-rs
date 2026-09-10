/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.commands;

import java.io.IOException;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.commands.AuthHttpIDChallResponse;
import nn.pp.rccore.scan.impl.commands.AuthHttpIDGetChallenge;
import nn.pp.rccore.scan.impl.commands.AuthNamePassRequest;
import nn.pp.rccore.scan.impl.commands.AuthRDMRequest;
import nn.pp.rccore.scan.impl.commands.PongResponse;
import nn.pp.rccore.scan.impl.commands.QuitMessageFromClient;
import nn.pp.rccore.scan.impl.commands.ScanSessionRequest;

public interface CommandVisitorV01_00 {
    public void handleAuthNamePassRequest(AuthNamePassRequest var1) throws IOException, ScanCoreException;

    public void handleAuthRDMRequest(AuthRDMRequest var1) throws IOException, ScanCoreException;

    public void handleHttpIDGetChallenge(AuthHttpIDGetChallenge var1) throws IOException, ScanCoreException;

    public void handleHttpIDChallengeResponse(AuthHttpIDChallResponse var1) throws IOException, ScanCoreException;

    public void handleQuitMessageFromClient(QuitMessageFromClient var1) throws IOException, ScanCoreException;

    public void handlePongResponse(PongResponse var1) throws IOException, ScanCoreException;

    public void handleScanSessionRequest(ScanSessionRequest var1) throws IOException, ScanCoreException;
}

