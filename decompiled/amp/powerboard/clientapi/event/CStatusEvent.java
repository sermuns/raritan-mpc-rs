/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

public class CStatusEvent {
    private int msgOpcode;
    private int status;
    private int reqOperation = 0;
    private boolean isLockAcquired = false;
    private int saveParam = 0;
    private String lockerName;

    public CStatusEvent(int n, int n2, int n3, boolean bl, String string, int n4) {
        this.msgOpcode = n;
        this.status = n2;
        this.reqOperation = n3;
        this.isLockAcquired = bl;
        this.lockerName = string;
        this.saveParam = n4;
    }

    public int getOpcode() {
        return this.msgOpcode;
    }

    public int getStatus() {
        return this.status;
    }

    public int getReqOperation() {
        return this.reqOperation;
    }

    public boolean isLockAcquired() {
        return this.isLockAcquired;
    }

    public String getLockerName() {
        return this.lockerName;
    }

    public int getSaveParam() {
        return this.saveParam;
    }
}

