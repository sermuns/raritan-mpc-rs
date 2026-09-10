/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core;

public interface INotificationEvent {
    public int getErrorCode();

    public int getGroupId();

    public int getMessageGroupErrorCode();

    public int getMessageGroupId();

    public boolean isDebug();

    public boolean isError();

    public boolean isWarning();

    public boolean isInfo();

    public boolean isQuit();

    public boolean isClientGeneratedError();

    public Severity getSeverityType();

    public String getMessage();

    public boolean isRFB();

    public boolean isMSP();

    public INotificationEvent clone();

    public static enum Severity {
        DEBUG,
        ERROR,
        WARNING,
        INFO,
        UNKNOWN;

    }
}

