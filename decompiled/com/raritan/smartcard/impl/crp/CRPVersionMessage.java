/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.crp.MessageToCommandProducer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import nn.pp.core.impl.MonitoringDataInputStream;

public class CRPVersionMessage
implements MessageToCommandProducer<Object> {
    private final int versionMajor;
    private final int versionMinor;

    public CRPVersionMessage() {
        this(-1, -1);
    }

    private CRPVersionMessage(int n, int n2) {
        this.versionMajor = n;
        this.versionMinor = n2;
    }

    public CRPVersionMessage readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, SmartCardException {
        String string;
        byte[] byArray = new byte[16];
        monitoringDataInputStream.readFully(byArray);
        try {
            string = new String(byArray, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new SmartCardException(unsupportedEncodingException);
        }
        if (!string.substring(0, 10).equals("e-RIC CRP xx.xx\n".substring(0, 10)) || byArray[10] < 48 || byArray[10] > 57 || byArray[11] < 48 || byArray[11] > 57 || byArray[12] != 46 || byArray[13] < 48 || byArray[13] > 57 || byArray[14] < 48 || byArray[14] > 57 || byArray[15] != 10) {
            throw new SmartCardException("Incorrect protocol version format");
        }
        int n = (byArray[10] - 48) * 10 + (byArray[11] - 48);
        int n2 = (byArray[13] - 48) * 10 + (byArray[14] - 48);
        return new CRPVersionMessage(n, n2);
    }

    @Override
    public void visit(Object object) {
    }

    public int getVersionMajor() {
        return this.versionMajor;
    }

    public int getVersionMinor() {
        return this.versionMinor;
    }

    public String getVersionAsString() {
        return "V" + CRPVersionMessage.getFormatted(this.getVersionMajor()) + "_" + CRPVersionMessage.getFormatted(this.getVersionMinor());
    }

    private static String getFormatted(int n) {
        int n2 = n;
        if (n2 < 10) {
            return "0" + n2;
        }
        return "" + n2;
    }
}

