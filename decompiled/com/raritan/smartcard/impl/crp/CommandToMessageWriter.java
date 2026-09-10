/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

import com.raritan.smartcard.SmartCardException;
import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;

public interface CommandToMessageWriter {
    public void writeMessage(MonitoringDataOutputStream var1) throws SmartCardException, IOException;
}

