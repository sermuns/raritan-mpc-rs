/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.rccore.scan.ScanCoreException;

public interface CommandToMessageWriter {
    public void writeMessage(MonitoringDataOutputStream var1) throws ScanCoreException, IOException;
}

