/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore;

public class VMException
extends Exception {
    private int errorCode = -1;

    public VMException() {
    }

    public VMException(String string) {
        super(string);
    }

    public VMException(int n, String string) {
        super(string);
        this.errorCode = n;
    }

    public VMException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public VMException(int n, String string, Throwable throwable) {
        super(string, throwable);
        this.errorCode = n;
    }

    public VMException(Throwable throwable) {
        super(throwable);
    }

    private String getMessage(String string) {
        if (this.errorCode != -1) {
            string = "[0x" + Integer.toHexString(this.errorCode) + "]: " + string;
        }
        return string;
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

