/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javaclientlib.clientlib.TRConnection;
import javax.swing.Timer;

public abstract class ConnectionCommand
extends AbstractCommand {
    private RaritanPropertyResourceBundle bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    private final int errCodeMask = 4095;
    private static boolean isBusy = false;
    private static Timer busyTimer = null;
    private static Integer busyBody = new Integer(0);

    public ConnectionCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    public void getTRSRVR_ErrorResult(CommandResult commandResult, KvmPort kvmPort) {
        try {
            int n = TRConnection.getLastError();
            int n2 = n & 0xFFFFF000;
            if ((n &= 0xFFF) != 12) {
                int n3;
                String string;
                if (n2 == 0) {
                    n2 = 0x20001000;
                }
                commandResult.setIsSuccess(false);
                if (n2 == 0x20001000) {
                    string = "TR_ERROR.";
                    n3 = 24;
                } else {
                    string = "TRLIB_ERROR.";
                    n3 = 12;
                }
                if (n > n3) {
                    n = n3;
                }
                string = string + Integer.toString(n);
                string = "[" + kvmPort.getDevice().getNameIP() + "]: " + this.bundle.getString(string) + " (0x" + Integer.toHexString(n2 | n) + ")";
                commandResult.setStatusMessage(string);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean setBusy(boolean bl) {
        Integer n = busyBody;
        synchronized (n) {
            if (isBusy) {
                if (!bl) {
                    if (busyTimer != null && busyTimer.isRunning()) {
                        busyTimer.stop();
                    }
                    isBusy = false;
                }
                return true;
            }
            if (bl) {
                isBusy = true;
                if (busyTimer != null && busyTimer.isRunning()) {
                    busyTimer.stop();
                }
                busyTimer = new Timer(5000, new StayBusy());
                busyTimer.setRepeats(false);
                busyTimer.start();
            }
            return false;
        }
    }

    private class StayBusy
    implements ActionListener {
        private StayBusy() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            isBusy = false;
        }
    }
}

