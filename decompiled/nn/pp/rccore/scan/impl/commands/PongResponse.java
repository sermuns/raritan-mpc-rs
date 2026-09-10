/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.commands;

import java.io.IOException;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.commands.CommandVisitorV01_00;
import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;

public class PongResponse
implements SupportsVisitor<CommandVisitorV01_00> {
    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, ScanCoreException {
        commandVisitorV01_00.handlePongResponse(this);
    }
}

