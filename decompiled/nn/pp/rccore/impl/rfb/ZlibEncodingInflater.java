/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.impl.rfb.RfbHandler;

public class ZlibEncodingInflater {
    private Logger logger;
    private byte[] updateBuf;
    static final int updatePortionBufSize = 65536;
    private byte[] updatePortionBuf = new byte[65536];
    private Inflater zlibInflater = null;
    private int uncompressedSize;

    public ZlibEncodingInflater(Logger logger) {
        this.logger = logger;
    }

    public MonitoringDataInputStream getUncompressedDataStream(MonitoringDataInputStream monitoringDataInputStream, RfbHandler rfbHandler, boolean bl, boolean bl2, int n) throws IOException {
        if (bl) {
            this.logger.log(Level.FINEST, T._("Inflating zlib stream compressed data"));
            if (n <= 0) {
                n = rfbHandler.readCompactLen();
            }
            this.uncompressedSize = n;
            if (this.updateBuf == null || n > this.updateBuf.length) {
                this.updateBuf = new byte[n];
            }
            int n2 = n;
            int n3 = 0;
            if (this.zlibInflater == null) {
                this.zlibInflater = new Inflater();
            }
            while (n2 > 0) {
                int n4 = rfbHandler.readCompactLen();
                monitoringDataInputStream.readFully(this.updatePortionBuf, 0, n4);
                this.zlibInflater.setInput(this.updatePortionBuf, 0, n4);
                try {
                    int n5 = this.zlibInflater.inflate(this.updateBuf, n3, n2);
                    n3 += n5;
                    n2 -= n5;
                }
                catch (DataFormatException dataFormatException) {
                    throw new IOException(dataFormatException.toString());
                }
            }
            return new MonitoringDataInputStream(new ByteArrayInputStream(this.updateBuf));
        }
        if (bl2) {
            this.logger.log(Level.INFO, T._("Inflating zlib \"compress\" compressed data"));
            int n6 = (int)monitoringDataInputStream.readUnsignedInt();
            this.uncompressedSize = n = (int)monitoringDataInputStream.readUnsignedInt();
            int n7 = 0;
            int n8 = 0;
            int n9 = n;
            if (this.updateBuf == null || n > this.updateBuf.length) {
                this.updateBuf = new byte[n];
            }
            Inflater inflater = new Inflater();
            while (n7 < n6) {
                int n10 = n6 - n7 > 65536 ? 65536 : (n6 - n7) % 65536;
                monitoringDataInputStream.readFully(this.updatePortionBuf, 0, n10);
                inflater.setInput(this.updatePortionBuf, 0, n10);
                try {
                    int n11 = inflater.inflate(this.updateBuf, n8, n9);
                    n8 += n11;
                    n7 += n10;
                    n9 -= n11;
                }
                catch (DataFormatException dataFormatException) {
                    throw new IOException(dataFormatException.toString());
                }
            }
            inflater.end();
            return new MonitoringDataInputStream(new ByteArrayInputStream(this.updateBuf));
        }
        this.uncompressedSize = n;
        return monitoringDataInputStream;
    }

    public int getUncompressedSize() {
        return this.uncompressedSize;
    }
}

