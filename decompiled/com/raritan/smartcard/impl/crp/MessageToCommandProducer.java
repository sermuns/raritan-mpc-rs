/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

import com.raritan.smartcard.SmartCardException;
import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;

public interface MessageToCommandProducer<V> {
    public MessageToCommandProducer<V> readMessage(MonitoringDataInputStream var1) throws IOException, SmartCardException;

    public void visit(V var1);
}

