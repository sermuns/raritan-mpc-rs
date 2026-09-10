/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.commands;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.commands.CommandVisitorV01_00;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import java.io.IOException;

public class MountCardReaderRequest
implements SupportsVisitor<CommandVisitorV01_00> {
    private final int rfbSessionID;
    private final int msindex;

    public MountCardReaderRequest(int n, int n2) {
        this.rfbSessionID = n;
        this.msindex = n2;
    }

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, SmartCardException {
        commandVisitorV01_00.handleMountCardReaderRequest(this);
    }

    public int getRfbSessionID() {
        return this.rfbSessionID;
    }

    public int getMsindex() {
        return this.msindex;
    }
}

