/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.io.IOException;
import java.util.logging.Logger;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.RCException;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;

public abstract class ImageDecoder {
    protected Logger logger;
    protected RfbHandler rfbHandler;
    private int bufsize = 65536;
    private byte[] buf = new byte[this.bufsize];
    private int count = 0;
    private int pos = 0;

    public ImageDecoder(Logger logger, RfbHandler rfbHandler) {
        this.logger = logger;
        this.rfbHandler = rfbHandler;
    }

    public abstract void decodeImage(MonitoringDataInputStream var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, RfbPixelFormat var12) throws IOException, RCException;

    protected final int readBufferedByte(MonitoringDataInputStream monitoringDataInputStream) throws IOException {
        if (this.count - this.pos <= 0) {
            this.count = monitoringDataInputStream.read(this.buf);
            this.pos = 0;
        }
        return this.buf[this.pos++] & 0xFF;
    }

    protected final void finishBufferedReading(MonitoringDataInputStream monitoringDataInputStream) {
        monitoringDataInputStream.stall(this.buf, this.pos, this.count - this.pos);
        this.count = 0;
        this.pos = 0;
    }
}

