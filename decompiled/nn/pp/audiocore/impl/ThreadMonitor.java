/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl;

public class ThreadMonitor {
    public int value;

    public ThreadMonitor(int n) {
        this.value = n;
    }

    public void setValue(int n) {
        this.value = n;
    }

    public int getValue() {
        return this.value;
    }
}

