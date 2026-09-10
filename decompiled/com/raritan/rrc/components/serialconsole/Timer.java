/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import com.raritan.rrc.components.serialconsole.VDU;

class Timer
implements Runnable {
    private VDU vdu;
    private Thread blinker;
    private boolean blinkCharacters = false;
    private int speed = 500;
    private boolean running;

    protected Timer(VDU vDU) {
        this.vdu = vDU;
        this.blinker = new Thread(this);
        this.blinkCharacters = false;
    }

    protected void start() {
        this.running = true;
        this.blinker.start();
    }

    protected void stop() {
        this.running = false;
    }

    protected void setBlinkChars(boolean bl) {
        this.blinkCharacters = bl;
    }

    @Override
    public void run() {
        while (this.running) {
            if (this.blinkCharacters) {
                this.vdu.reDrawBlink();
            } else {
                this.vdu.reDrawCursor();
            }
            try {
                Thread.sleep(this.speed);
            }
            catch (InterruptedException interruptedException) {}
        }
    }
}

