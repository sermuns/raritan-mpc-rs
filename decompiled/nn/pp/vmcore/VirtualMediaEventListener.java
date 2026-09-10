/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore;

public interface VirtualMediaEventListener {
    public static final int DRIVE_LOCKING = 1;
    public static final int DRIVE_CONNECTED = 2;
    public static final int DRIVE_DISCONNECTED_BY_USER = 4;
    public static final int ALL = 7;

    public void driveConnected(boolean var1);

    public void driveDisconnectedByUser(boolean var1);

    public void disconnected(Exception var1);

    public LockFailAction driveLockingFailed();

    public static enum LockFailAction {
        RETRY,
        IGNORE,
        CANCEL;

    }
}

