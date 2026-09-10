/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.audio;

import java.io.IOException;

public class CircularBufferStream {
    private int posWrite = 0;
    private int posRead = 0;
    private int bufSize = 0;
    private int bufBytes = 0;
    private byte[] bufCircular = null;

    public CircularBufferStream(int n) {
        this.bufCircular = new byte[n];
        this.bufSize = n;
    }

    public void bufReset() {
        this.posWrite = 0;
        this.posRead = 0;
        this.bufBytes = 0;
    }

    public int getAvailableBytes() {
        return this.bufBytes;
    }

    public int getWritableBytes() {
        return this.bufSize - this.bufBytes;
    }

    public int read(byte[] byArray, int n) throws IOException {
        int n2 = 0;
        int n3 = this.posWrite - this.posRead;
        int n4 = this.bufSize - this.posRead;
        if (this.bufBytes > this.bufSize) {
            this.bufReset();
            return 0;
        }
        if (n3 >= n && n3 > 0) {
            n2 = n;
        } else if (n3 < n && n3 > 0) {
            n2 = n3;
        } else if (n3 <= 0) {
            n2 = n2 > n ? n : (this.posWrite > n - (n2 = n4) ? n : (n2 += this.posWrite));
        }
        try {
            if (n2 < n4) {
                System.arraycopy(this.bufCircular, this.posRead, byArray, 0, n2);
                this.posRead += n2;
            } else if (n2 == n4) {
                System.arraycopy(this.bufCircular, this.posRead, byArray, 0, n2);
                this.posRead = 0;
            } else {
                System.arraycopy(this.bufCircular, this.posRead, byArray, 0, n4);
                System.arraycopy(this.bufCircular, 0, byArray, n4, n2 - n4);
                this.posRead = n2 - n4;
            }
            this.bufBytes -= n2;
        }
        catch (Exception exception) {
            throw new IOException("Buffer Error");
        }
        return n2;
    }

    public void write(byte[] byArray, int n, int n2) throws IOException {
        int n3 = this.bufSize - this.posWrite;
        if (n2 == 0) {
            return;
        }
        try {
            if (n3 > n2) {
                System.arraycopy(byArray, n, this.bufCircular, this.posWrite, n2);
                this.posWrite += n2;
            } else {
                System.arraycopy(byArray, n, this.bufCircular, this.posWrite, n3);
                System.arraycopy(byArray, n + n3, this.bufCircular, 0, n2 - n3);
                this.posWrite = n2 - n3;
            }
            this.bufBytes += n2;
        }
        catch (Exception exception) {
            throw new IOException("Buffer Error");
        }
    }
}

