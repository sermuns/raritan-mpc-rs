/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.rsp.ClientCommandsExecutor;
import nn.pp.rccore.scan.impl.rsp.EventsListenerManager;
import nn.pp.rccore.scan.impl.rsp.MessageToCommandProducer;
import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;

public interface RSPProtoHandler<V> {
    public MessageToCommandProducer<V> getMessageToCommandProducer(int var1);

    public void executeCommand(MessageToCommandProducer<V> var1);

    public <T> void executeClientCommands(MonitoringDataOutputStream var1, MonitoringDataInputStream var2, SupportsVisitor<T> var3) throws IOException, ScanCoreException;

    public void setClientCommandsExecutor(ClientCommandsExecutor var1);

    public void setEventsListenerManager(EventsListenerManager var1);

    public void handleDisconnection();
}

