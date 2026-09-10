/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.util.Timer;
import java.util.TimerTask;

public class PingTimer {
    private static Timer pingTimer = PingTimer.createTimer();

    static synchronized void schedule(TimerTask timerTask) {
        try {
            pingTimer.schedule(timerTask, 1000L, 20000L);
        }
        catch (IllegalStateException illegalStateException) {
            pingTimer = PingTimer.createTimer();
            pingTimer.schedule(timerTask, 1000L, 20000L);
        }
    }

    private static Timer createTimer() {
        return new Timer("RFB Ping");
    }
}

