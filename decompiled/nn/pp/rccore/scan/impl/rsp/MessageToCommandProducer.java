/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.scan.ScanCoreException;

public interface MessageToCommandProducer<V> {
    public MessageToCommandProducer<V> readMessage(MonitoringDataInputStream var1) throws IOException, ScanCoreException;

    public void visit(V var1);
}

