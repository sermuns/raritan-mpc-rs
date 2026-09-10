/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.EventsListenerManager;
import com.raritan.smartcard.impl.commands.ClientCommandsExecutor;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import com.raritan.smartcard.impl.crp.MessageToCommandProducer;
import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;

public interface CRPProtoHandler<V> {
    public MessageToCommandProducer<V> getMessageToCommandProducer(int var1);

    public void executeCommand(MessageToCommandProducer<V> var1);

    public <T> void executeClientCommands(MonitoringDataOutputStream var1, MonitoringDataInputStream var2, SupportsVisitor<T> var3) throws IOException, SmartCardException;

    public void setClientCommandsExecutor(ClientCommandsExecutor var1);

    public void setEventsListenerManager(EventsListenerManager var1);

    public void handleDisconnection();
}

