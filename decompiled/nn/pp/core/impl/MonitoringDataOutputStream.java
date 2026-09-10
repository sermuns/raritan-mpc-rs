/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class MonitoringDataOutputStream
extends DataOutputStream {
    private OutputStream out;
    private int count;

    public MonitoringDataOutputStream(OutputStream outputStream) {
        super(outputStream);
        this.out = outputStream;
    }

    public OutputStream getStream() {
        return this.out;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(int n) throws IOException {
        this.out.write(n);
        MonitoringDataOutputStream monitoringDataOutputStream = this;
        synchronized (monitoringDataOutputStream) {
            ++this.count;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        if (n2 == 0) {
            return;
        }
        this.out.write(byArray, n, n2);
        MonitoringDataOutputStream monitoringDataOutputStream = this;
        synchronized (monitoringDataOutputStream) {
            this.count += n2;
        }
    }

    @Override
    public void write(byte[] byArray) throws IOException {
        this.write(byArray, 0, byArray.length);
    }

    public final void writeUnsignedShort(int n) throws IOException {
        this.write(n >> 8 & 0xFF);
        this.write(n & 0xFF);
    }

    public final void writeUnsignedShortLE(int n) throws IOException {
        this.write(n & 0xFF);
        this.write(n >> 8 & 0xFF);
    }

    public final void writeUnsignedInt(long l) throws IOException {
        this.write((int)(l >> 24 & 0xFFL));
        this.write((int)(l >> 16 & 0xFFL));
        this.write((int)(l >> 8 & 0xFFL));
        this.write((int)(l & 0xFFL));
    }

    public final void writeUnsignedIntLE(long l) throws IOException {
        this.write((int)(l & 0xFFL));
        this.write((int)(l >> 8 & 0xFFL));
        this.write((int)(l >> 16 & 0xFFL));
        this.write((int)(l >> 24 & 0xFFL));
    }

    public final void writeUnsignedLong(long l) throws IOException {
        this.write((int)(l >> 56 & 0xFFL));
        this.write((int)(l >> 48 & 0xFFL));
        this.write((int)(l >> 40 & 0xFFL));
        this.write((int)(l >> 32 & 0xFFL));
        this.write((int)(l >> 24 & 0xFFL));
        this.write((int)(l >> 16 & 0xFFL));
        this.write((int)(l >> 8 & 0xFFL));
        this.write((int)(l & 0xFFL));
    }

    public final void writeUnsignedLongLE(long l) throws IOException {
        this.write((int)(l & 0xFFL));
        this.write((int)(l >> 8 & 0xFFL));
        this.write((int)(l >> 16 & 0xFFL));
        this.write((int)(l >> 24 & 0xFFL));
        this.write((int)(l >> 32 & 0xFFL));
        this.write((int)(l >> 40 & 0xFFL));
        this.write((int)(l >> 48 & 0xFFL));
        this.write((int)(l >> 56 & 0xFFL));
    }

    public final void write(ProtocolMessage protocolMessage) throws IOException {
        protocolMessage.writeTo(this);
        this.flush();
    }

    public synchronized int getAndClearOut() {
        int n = this.count;
        this.count = 0;
        return n;
    }
}

