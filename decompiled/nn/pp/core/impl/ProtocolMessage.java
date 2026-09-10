/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;

public class ProtocolMessage
extends MonitoringDataOutputStream {
    private ByteArrayOutputStream baOut = (ByteArrayOutputStream)this.getStream();

    public ProtocolMessage() {
        super(new ByteArrayOutputStream());
    }

    public final void writeTo(OutputStream outputStream) throws IOException {
        this.baOut.writeTo(outputStream);
        this.baOut.reset();
    }
}

