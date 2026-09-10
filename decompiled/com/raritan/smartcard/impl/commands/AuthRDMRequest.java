/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.commands;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.commands.CommandVisitorV01_00;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import java.io.IOException;

public class AuthRDMRequest
implements SupportsVisitor<CommandVisitorV01_00> {
    private final String rdmID;

    public AuthRDMRequest(String string) {
        this.rdmID = string;
    }

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, SmartCardException {
        commandVisitorV01_00.handleAuthRDMRequest(this);
    }

    public String getRdmID() {
        return this.rdmID;
    }
}

