/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core;

import nn.pp.core.INotificationEvent;

public abstract class NotificationEvent
implements INotificationEvent {
    static byte upperNibbleMask = (byte)-16;
    static byte lowerNibbleMask = (byte)15;
    protected int flags;
    protected int errorCode;
    private int severity;
    private INotificationEvent.Severity severityType;
    private int groupId;
    private int messageGroupId;
    private int messageGroupErrorCode;
    protected boolean clientGeneratedError = false;
    public static final byte ProtocolRFB = 1;
    public static final byte ProtocolRDM_1 = 2;
    public static final byte ProtocolMSP = 3;
    public static final byte ProtocolRDM_2 = 6;
    public static final byte SeverityError = 0;
    public static final byte SeverityWarning = 1;
    public static final byte SeverityInfo = 2;
    public static final byte SeverityDebug = 3;
    public static final byte UserNotfFlagQuit = 1;

    public NotificationEvent(int n, int n2) {
        this.flags = n;
        this.errorCode = n2;
        this.groupId = (n2 >> 24 & upperNibbleMask) >> 4;
        this.severity = n2 >> 24 & lowerNibbleMask;
        this.messageGroupId = n2 << 8 >> 24;
        this.messageGroupErrorCode = n2 << 16 >> 16;
    }

    public NotificationEvent(int n, int n2, boolean bl, String string) {
        this.setClientgeneratedError(bl);
        this.flags = n;
        this.errorCode = n2;
        if (!this.isClientGeneratedError()) {
            this.groupId = (n2 >> 24 & upperNibbleMask) >> 4;
            this.severity = n2 >> 24 & lowerNibbleMask;
            this.messageGroupId = n2 << 8 >> 24;
            this.messageGroupErrorCode = n2 << 16 >> 16;
        }
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public int getGroupId() {
        return this.groupId;
    }

    @Override
    public int getMessageGroupErrorCode() {
        return this.messageGroupErrorCode;
    }

    @Override
    public int getMessageGroupId() {
        return this.messageGroupId;
    }

    @Override
    public INotificationEvent.Severity getSeverityType() {
        if (this.severity == 3) {
            return INotificationEvent.Severity.DEBUG;
        }
        if (this.severity == 0) {
            return INotificationEvent.Severity.ERROR;
        }
        if (this.severity == 1) {
            return INotificationEvent.Severity.WARNING;
        }
        if (this.severity == 2) {
            return INotificationEvent.Severity.INFO;
        }
        return INotificationEvent.Severity.UNKNOWN;
    }

    public int getSeverity() {
        return this.severity;
    }

    @Override
    public boolean isDebug() {
        return this.severity == 3;
    }

    @Override
    public boolean isError() {
        return this.severity == 0;
    }

    @Override
    public boolean isWarning() {
        return this.severity == 1;
    }

    @Override
    public boolean isInfo() {
        return this.severity == 2;
    }

    @Override
    public boolean isQuit() {
        return (this.flags & 1) != 0;
    }

    @Override
    public boolean isMSP() {
        return this.groupId == 3;
    }

    @Override
    public boolean isRFB() {
        return this.groupId == 1;
    }

    public void setClientgeneratedError(boolean bl) {
        this.clientGeneratedError = bl;
    }

    @Override
    public boolean isClientGeneratedError() {
        return this.clientGeneratedError;
    }

    @Override
    public abstract String getMessage();

    public String toString() {
        return "[flags=" + this.flags + " errorCode=" + this.errorCode + " groupId=" + this.groupId + " severity=" + this.severity + " messageGroupId=" + this.messageGroupId + " messageGroupErrorCode=" + this.messageGroupErrorCode + "]";
    }

    @Override
    public abstract INotificationEvent clone();
}

