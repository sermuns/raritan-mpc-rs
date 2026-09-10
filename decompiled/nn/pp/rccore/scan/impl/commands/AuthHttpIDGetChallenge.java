/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.commands;

import java.io.IOException;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.commands.CommandVisitorV01_00;
import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;

public class AuthHttpIDGetChallenge
implements SupportsVisitor<CommandVisitorV01_00> {
    private final String httpSessionID;

    public AuthHttpIDGetChallenge(String string) {
        this.httpSessionID = string;
    }

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, ScanCoreException {
        commandVisitorV01_00.handleHttpIDGetChallenge(this);
    }

    public String getHttpSessionID() {
        return this.httpSessionID;
    }
}

