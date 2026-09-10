/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class CMsgOutputStream
extends OutputStream {
    private OutputStream out;
    private DataOutputStream message;
    private ByteArrayOutputStream buffer;
    public byte abort;

    public CMsgOutputStream(OutputStream outputStream, int n) throws IOException {
        this.out = outputStream;
        this.buffer = new ByteArrayOutputStream();
        this.message = new DataOutputStream(this.buffer);
        this.message.writeInt(n);
        this.abort = 0;
    }

    public void close() throws IOException {
        this.message = new DataOutputStream(this.out);
        this.message.writeInt(this.buffer.size());
        this.buffer.writeTo(this.out);
        this.message.writeByte(this.abort);
        this.message = null;
        this.buffer = null;
    }

    public final void write(byte[] byArray, int n, int n2) throws IOException {
        this.message.write(byArray, n, n2);
    }

    public final void write(int n) throws IOException {
        this.message.write(n);
    }

    public final void writeBoolean(boolean bl) throws IOException {
        this.message.writeBoolean(bl);
    }

    public final void writeByte(int n) throws IOException {
        this.message.writeByte(n);
    }

    public final void writeBytes(String string) throws IOException {
        this.message.writeBytes(string);
    }

    public final void writeChar(int n) throws IOException {
        this.message.writeChar(n);
    }

    public final void writeChars(String string) throws IOException {
        this.message.writeChars(string);
    }

    public final void writeDouble(double d) throws IOException {
        this.message.writeDouble(d);
    }

    public final void writeFloat(float f) throws IOException {
        this.message.writeFloat(f);
    }

    public final void writeInt(int n) throws IOException {
        this.message.writeInt(n);
    }

    public final void writeLong(long l) throws IOException {
        this.message.writeLong(l);
    }

    public final void writeShort(short s) throws IOException {
        this.message.writeShort(s);
    }

    public final void writeUTF(String string) throws IOException {
        this.message.writeUTF(string);
    }
}

