/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.commands;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.commands.CommandVisitorV01_00;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import java.io.IOException;

public class ResponseAPDU
implements SupportsVisitor<CommandVisitorV01_00> {
    private final byte[] data;
    private int sequenceNum;

    public ResponseAPDU(byte[] byArray) {
        this.data = byArray;
    }

    @Override
    public void accept(CommandVisitorV01_00 commandVisitorV01_00) throws IOException, SmartCardException {
        commandVisitorV01_00.handleRAPDU(this);
    }

    public byte[] getData() {
        return this.data;
    }

    public synchronized int getSequenceNum() {
        return this.sequenceNum;
    }

    public synchronized void setSequenceNum(int n) {
        this.sequenceNum = n;
    }
}

