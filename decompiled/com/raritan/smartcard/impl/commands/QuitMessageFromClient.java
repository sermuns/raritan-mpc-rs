/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.commands;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.commands.CommandVisitorV01_00;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import java.io.IOException;

public class QuitMessageFromClient
implements SupportsVisitor<CommandVisitorV01_00> {
    private final int reason = 1912733699;

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, SmartCardException {
        commandVisitorV01_00.handleQuitMessageFromClient(this);
    }

    public int getReason() {
        return 1912733699;
    }
}

