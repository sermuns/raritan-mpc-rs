/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.rsp.CommandToMessageWriter;

public class RSPHelloMessage
implements CommandToMessageWriter {
    @Override
    public void writeMessage(MonitoringDataOutputStream monitoringDataOutputStream) throws ScanCoreException, IOException {
        try {
            monitoringDataOutputStream.write("e-RIC RSP P".getBytes("ISO-8859-1"));
            monitoringDataOutputStream.write(0);
            monitoringDataOutputStream.flush();
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new ScanCoreException(unsupportedEncodingException);
        }
    }
}

