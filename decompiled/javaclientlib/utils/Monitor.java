/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

public class Monitor {
    private boolean boolNotified = false;
    private boolean boolWaitTimedOut = true;

    public synchronized boolean waiting(int n) {
        try {
            if (this.boolNotified) {
                this.boolNotified = false;
                return false;
            }
            this.boolWaitTimedOut = true;
            this.wait(n);
            this.boolNotified = false;
        }
        catch (InterruptedException interruptedException) {
            System.out.println("RFPMonitor -- Monitor: InterruptedException");
        }
        return this.boolWaitTimedOut;
    }

    public synchronized void notifying() {
        this.boolNotified = true;
        this.boolWaitTimedOut = false;
        this.notifyAll();
    }

    public synchronized boolean isNotified() {
        return this.boolNotified;
    }

    public synchronized boolean isExpired() {
        return this.boolWaitTimedOut;
    }
}

