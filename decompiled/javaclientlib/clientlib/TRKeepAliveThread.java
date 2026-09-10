/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

import javaclientlib.clientlib.TRConnection;
import javaclientlib.tr.TRCOMMAND;
import javaclientlib.utils.RRCLogger;

public class TRKeepAliveThread
extends Thread {
    private TRConnection objTRConnection;

    public TRKeepAliveThread(TRConnection tRConnection) {
        this.objTRConnection = tRConnection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        long l;
        TRCOMMAND tRCOMMAND = new TRCOMMAND();
        long l2 = 58000L;
        long l3 = l = l2 >> 1;
        block10: while (true) {
            try {
                while (true) {
                    TRKeepAliveThread tRKeepAliveThread = this;
                    synchronized (tRKeepAliveThread) {
                        this.wait(l3);
                    }
                    l3 = l;
                    if (TRKeepAliveThread.interrupted()) {
                        return;
                    }
                    if (!this.objTRConnection.getAuthenticated()) continue;
                    if (System.currentTimeMillis() - this.objTRConnection.getTimeLastMsg() > l2) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 8, "KAT: Not Responding");
                        }
                        try {
                            this.objTRConnection.disConnect();
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        this.objTRConnection.dbLog("Server not responding\n");
                        break block10;
                    }
                    long l4 = System.currentTimeMillis() - this.objTRConnection.getTimeLastMsgSent();
                    long l5 = System.currentTimeMillis() - this.objTRConnection.getTimeLastMsg();
                    if (l4 >= l || l5 >= l) {
                        tRCOMMAND.setCommand((byte)3);
                        tRCOMMAND.setCmdLength((short)4);
                        tRCOMMAND.setPktID((byte)0);
                        if (this.objTRConnection.sendTRCmd(tRCOMMAND)) continue;
                        this.objTRConnection.disConnect();
                        this.objTRConnection.dbLog("PING Error");
                        break block10;
                    }
                    l3 = l - Math.max(l4, l5);
                }
            }
            catch (InterruptedException interruptedException) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 8, "KAT: InterruptedException");
                }
                return;
            }
            catch (Exception exception) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 8, "KAT: Exception");
                }
                this.objTRConnection.dbNotify(0x20000004, 9);
                RRCLogger.logException(exception);
                try {
                    this.objTRConnection.disConnect();
                }
                catch (Exception exception2) {
                    if (!RRCLogger.logEnabled) continue;
                    RRCLogger.log(100, 8, "TRKeepAliveThread:run - 2 " + exception2.getMessage());
                }
                continue;
            }
            break;
        }
    }
}

