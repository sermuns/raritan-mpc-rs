/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class CMsgInputStream
extends InputStream {
    public int opCode;
    private DataInputStream message;
    private ByteArrayInputStream buffer;

    public CMsgInputStream(InputStream inputStream) throws IOException {
        byte[] byArray;
        byte by;
        do {
            this.message = new DataInputStream(inputStream);
            int n = this.message.readInt();
            if (n < 0 || n > 100000) {
                System.out.println("length = " + n);
                System.out.println("Wrong message length. Aborted!!");
                System.exit(1);
            }
            byArray = new byte[n];
            int n2 = 0;
            int n3 = byArray.length;
            while (n3 > 0) {
                n = inputStream.read(byArray, n2, n3);
                if (n == -1) {
                    throw new EOFException();
                }
                n2 += n;
                n3 -= n;
            }
        } while ((by = this.message.readByte()) > 0);
        this.buffer = new ByteArrayInputStream(byArray);
        this.message = new DataInputStream(this.buffer);
        this.opCode = this.message.readInt();
    }

    public void close() throws IOException {
        if (this.buffer.available() != 0) {
            throw new IOException("early EOM");
        }
        this.buffer = null;
        this.message = null;
    }

    public final String readBytes(int n) throws IOException {
        byte[] byArray = new byte[n];
        this.message.read(byArray, 0, n);
        return new String(byArray, 0);
    }

    public int read() throws IOException {
        return this.message.read();
    }

    public final int read(byte[] byArray) throws IOException {
        return this.message.read(byArray);
    }

    public final int read(byte[] byArray, int n, int n2) throws IOException {
        return this.message.read(byArray, n, n2);
    }

    public final boolean readBoolean() throws IOException {
        return this.message.readBoolean();
    }

    public final byte readByte() throws IOException {
        return this.message.readByte();
    }

    public final char readChar() throws IOException {
        return this.message.readChar();
    }

    public final double readDouble() throws IOException {
        return this.message.readDouble();
    }

    public final float readFloat() throws IOException {
        return this.message.readFloat();
    }

    public final void readFully(byte[] byArray) throws IOException {
        this.message.readFully(byArray);
    }

    public final void readFully(byte[] byArray, int n, int n2) throws IOException {
        this.message.readFully(byArray, n, n2);
    }

    public final int readInt() throws IOException {
        return this.message.readInt();
    }

    public final String readLine() throws IOException {
        return this.message.readLine();
    }

    public final long readLong() throws IOException {
        return this.message.readLong();
    }

    public final short readShort() throws IOException {
        return this.message.readShort();
    }

    public final int readUnsignedByte() throws IOException {
        return this.message.readUnsignedByte();
    }

    public final int readUnsignedShort() throws IOException {
        return this.message.readUnsignedShort();
    }

    public final String readUTF() throws IOException {
        return this.message.readUTF();
    }

    public final int skipBytes(int n) throws IOException {
        return this.message.skipBytes(n);
    }

    public final int available() throws IOException {
        return this.buffer.available();
    }
}

