/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.commands;

import com.raritan.smartcard.SmartCardException;
import java.io.IOException;

public interface SupportsVisitor<T> {
    public void accept(T var1) throws IOException, SmartCardException;
}

