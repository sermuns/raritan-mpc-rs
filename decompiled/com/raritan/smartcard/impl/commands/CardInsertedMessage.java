/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.commands;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.commands.CommandVisitorV01_00;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import java.io.IOException;

public class CardInsertedMessage
implements SupportsVisitor<CommandVisitorV01_00> {
    private final String proto;
    private final byte[] atrBytes;

    public CardInsertedMessage(String string, byte[] byArray) {
        this.proto = string;
        this.atrBytes = byArray;
    }

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, SmartCardException {
        commandVisitorV01_00.handleCardInsertedMessage(this);
    }

    public String getProto() {
        return this.proto;
    }

    public byte[] getAtrBytes() {
        return this.atrBytes;
    }
}

