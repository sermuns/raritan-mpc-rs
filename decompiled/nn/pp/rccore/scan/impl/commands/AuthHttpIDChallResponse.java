/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.commands;

import java.io.IOException;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.commands.CommandVisitorV01_00;
import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;

public class AuthHttpIDChallResponse
implements SupportsVisitor<CommandVisitorV01_00> {
    private final byte[] challenge;
    private final String httpSessionID;

    public AuthHttpIDChallResponse(byte[] byArray, String string) throws ScanCoreException {
        this.challenge = byArray;
        this.httpSessionID = string;
    }

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, ScanCoreException {
        commandVisitorV01_00.handleHttpIDChallengeResponse(this);
    }

    public byte[] getChallenge() {
        return this.challenge;
    }

    public String getHttpSessionID() {
        return this.httpSessionID;
    }
}

