/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.crp.CommandToMessageWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import nn.pp.core.impl.MonitoringDataOutputStream;

public class CRPHelloMessage
implements CommandToMessageWriter {
    @Override
    public void writeMessage(MonitoringDataOutputStream monitoringDataOutputStream) throws SmartCardException, IOException {
        try {
            monitoringDataOutputStream.write("e-RIC CRP P".getBytes("ISO-8859-1"));
            monitoringDataOutputStream.write(0);
            monitoringDataOutputStream.flush();
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new SmartCardException(unsupportedEncodingException);
        }
    }
}

