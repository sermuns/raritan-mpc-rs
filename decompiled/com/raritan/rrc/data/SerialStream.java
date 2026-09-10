/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.SerialPort;
import com.raritan.rrc.data.Stream;
import javaclientlib.clientlib.TRSerialStream;

public class SerialStream
extends TRSerialStream
implements Stream {
    private SerialPort serialPort;

    public SerialStream(SerialPort serialPort) {
        super(serialPort.getDeviceConnector());
        this.serialPort = serialPort;
    }

    @Override
    public void serialIn(int n, byte[] byArray) {
        if (this.serialPort != null) {
            this.serialPort.serialIn(n, byArray);
        }
    }
}

