/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.console;

import amp.powerboard.clientapi.console.CConsoleHandler;
import amp.powerboard.clientapi.event.CInternalEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CConsoleListenerThread
extends Thread {
    private static final int BUFFER_SIZE = 2048;
    private InputStream iStream = null;
    private CConsoleHandler consoleHandler = null;
    private boolean stoppedTerminal = false;
    private byte[] buffer;
    private CInternalEvent wrongFormat;

    public CConsoleListenerThread(InputStream inputStream, CConsoleHandler cConsoleHandler) {
        super("CConsoleHandlerThread");
        this.iStream = new BufferedInputStream(inputStream, 2048);
        this.consoleHandler = cConsoleHandler;
        this.buffer = new byte[2048];
    }

    public void run() {
        int n = 0;
        while (true) {
            if (this.stoppedTerminal) {
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                continue;
            }
            try {
                n = this.iStream.available();
                if (n == -1) break;
                if (n > 2048) {
                    n = 2048;
                } else if (n <= 0) {
                    n = 1;
                }
                n = this.iStream.read(this.buffer, 0, n);
                if (n == -1) break;
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException interruptedException) {}
            }
            catch (IOException iOException) {
                this.wrongFormat = new CInternalEvent(0, 5, "Error reading from CONSOLE");
                break;
            }
            this.consoleHandler.fireBytesArrived(this.buffer, n);
        }
    }

    public void releaseResources() {
        try {
            if (this.iStream != null) {
                this.iStream.close();
                this.iStream = null;
                this.buffer = null;
                this.consoleHandler = null;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            this.stop();
        }
        catch (Exception exception) {}
    }

    public void setPaused(boolean bl) {
        this.stoppedTerminal = bl;
    }
}

