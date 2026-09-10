/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

public class CDataEvent {
    private int msgOpcode;
    protected boolean isLockAcquired = false;
    protected String lockerName = null;

    public CDataEvent(int n, boolean bl) {
        this.msgOpcode = n;
        this.isLockAcquired = bl;
    }

    public CDataEvent(int n) {
        this.msgOpcode = n;
        this.isLockAcquired = false;
    }

    public int getOpcode() {
        return this.msgOpcode;
    }

    public boolean getLockStatus() {
        return this.isLockAcquired;
    }

    public String getLockerName() {
        return this.lockerName;
    }
}

