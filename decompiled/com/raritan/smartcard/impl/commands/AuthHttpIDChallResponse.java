/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.commands;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.commands.CommandVisitorV01_00;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import java.io.IOException;

public class AuthHttpIDChallResponse
implements SupportsVisitor<CommandVisitorV01_00> {
    private final byte[] challenge;
    private final String httpSessionID;

    public AuthHttpIDChallResponse(byte[] byArray, String string) throws SmartCardException {
        this.challenge = byArray;
        this.httpSessionID = string;
    }

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, SmartCardException {
        commandVisitorV01_00.handleHttpIDChallengeResponse(this);
    }

    public byte[] getChallenge() {
        return this.challenge;
    }

    public String getHttpSessionID() {
        return this.httpSessionID;
    }
}

