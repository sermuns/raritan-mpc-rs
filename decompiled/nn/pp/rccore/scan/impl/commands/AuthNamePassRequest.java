/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.commands;

import java.io.IOException;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.commands.CommandVisitorV01_00;
import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;

public class AuthNamePassRequest
implements SupportsVisitor<CommandVisitorV01_00> {
    private final String userName;
    private final String password;

    public AuthNamePassRequest(String string, String string2) {
        this.userName = string;
        this.password = string2;
    }

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, ScanCoreException {
        commandVisitorV01_00.handleAuthNamePassRequest(this);
    }

    public String getPassword() {
        return this.password;
    }

    public String getUserName() {
        return this.userName;
    }
}

