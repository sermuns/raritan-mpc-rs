/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

public class RCException
extends Exception {
    private int errorCode = -1;

    public RCException() {
    }

    public RCException(String message) {
        super(message);
    }

    public RCException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RCException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RCException(int errorCode, String message, Throwable throwable) {
        super(message, throwable);
        this.errorCode = errorCode;
    }

    public RCException(Throwable throwable) {
        super(throwable);
    }

    private String getMessage(String message) {
        if (this.errorCode != -1) {
            message = "[0x" + Integer.toHexString(this.errorCode) + "]: " + message;
        }
        return message;
    }

    @Override
    public String getMessage() {
        return this.getMessage(super.getMessage());
    }

    @Override
    public String getLocalizedMessage() {
        return this.getMessage(super.getLocalizedMessage());
    }
}

