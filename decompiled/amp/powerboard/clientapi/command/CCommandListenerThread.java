/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.event.IGenerateEvent;
import amp.powerboard.clientapi.net.CMsgInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CCommandListenerThread
extends Thread {
    private InputStream iStream = null;
    private IGenerateEvent evtGenerator = null;
    private boolean bSSLProduct;
    private boolean isActive = true;

    public CCommandListenerThread(InputStream inputStream, IGenerateEvent iGenerateEvent, boolean bl) {
        super("CommandListenerThread");
        this.iStream = inputStream;
        this.evtGenerator = iGenerateEvent;
        this.bSSLProduct = bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        while (this.isActive) {
            Object var5_4;
            try {
                try {
                    CMsgInputStream cMsgInputStream = new CMsgInputStream(this.iStream);
                    this.evtGenerator.generateEventObject(cMsgInputStream, this.bSSLProduct);
                }
                catch (IOException iOException) {
                    if (this.evtGenerator != null && this.isActive) {
                        this.evtGenerator.generateKillEventObject();
                    }
                    this.isActive = false;
                    this.evtGenerator = null;
                    var5_4 = null;
                    continue;
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                    var5_4 = null;
                    continue;
                }
                var5_4 = null;
            }
            catch (Throwable throwable) {
                var5_4 = null;
                throw throwable;
            }
        }
    }

    public void releaseResources() {
        try {
            this.evtGenerator = null;
            this.isActive = false;
            if (this.iStream != null) {
                this.iStream.close();
            }
            this.iStream = null;
        }
        catch (IOException iOException) {}
    }
}

