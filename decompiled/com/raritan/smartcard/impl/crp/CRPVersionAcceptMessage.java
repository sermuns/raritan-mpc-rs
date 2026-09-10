/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.crp.CommandToMessageWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import nn.pp.core.impl.MonitoringDataOutputStream;

public class CRPVersionAcceptMessage
implements CommandToMessageWriter {
    private final int versionMajor;
    private final int versionMinor;

    public CRPVersionAcceptMessage(int n, int n2) {
        this.versionMajor = n;
        this.versionMinor = n2;
    }

    @Override
    public void writeMessage(MonitoringDataOutputStream monitoringDataOutputStream) throws SmartCardException, IOException {
        String string = "" + this.versionMajor;
        if (this.versionMajor < 10) {
            string = "0" + string;
        }
        String string2 = "" + this.versionMinor;
        if (this.versionMinor < 10) {
            string2 = "0" + string2;
        }
        String string3 = "e-RIC CRP xx.xx\n".substring(0, 10) + string + "." + string2 + "\n";
        try {
            monitoringDataOutputStream.write(string3.getBytes("ISO-8859-1"));
            monitoringDataOutputStream.flush();
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new SmartCardException(unsupportedEncodingException);
        }
    }
}

