/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.rsp.CommandToMessageWriter;

public class RSPVersionAcceptMessage
implements CommandToMessageWriter {
    private final int versionMajor;
    private final int versionMinor;

    public RSPVersionAcceptMessage(int n, int n2) {
        this.versionMajor = n;
        this.versionMinor = n2;
    }

    @Override
    public void writeMessage(MonitoringDataOutputStream monitoringDataOutputStream) throws ScanCoreException, IOException {
        String string = "" + this.versionMajor;
        if (this.versionMajor < 10) {
            string = "0" + string;
        }
        String string2 = "" + this.versionMinor;
        if (this.versionMinor < 10) {
            string2 = "0" + string2;
        }
        String string3 = "e-RIC RSP xx.xx\n".substring(0, 10) + string + "." + string2 + "\n";
        try {
            monitoringDataOutputStream.write(string3.getBytes("ISO-8859-1"));
            monitoringDataOutputStream.flush();
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new ScanCoreException(unsupportedEncodingException);
        }
    }
}

