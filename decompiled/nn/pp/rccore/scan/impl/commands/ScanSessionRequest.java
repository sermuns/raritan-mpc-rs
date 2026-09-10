/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.commands;

import java.io.IOException;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.commands.CommandVisitorV01_00;
import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;

public class ScanSessionRequest
implements SupportsVisitor<CommandVisitorV01_00> {
    private final String[] portIds;

    public ScanSessionRequest(String[] stringArray) {
        this.portIds = stringArray;
    }

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, ScanCoreException {
        commandVisitorV01_00.handleScanSessionRequest(this);
    }

    public String[] getPortIds() {
        return this.portIds;
    }
}

