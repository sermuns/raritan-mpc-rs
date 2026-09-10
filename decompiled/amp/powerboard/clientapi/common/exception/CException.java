/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

public class CException
extends Exception {
    private int exceptionId;
    private String exceptionStr;

    public CException(int n, String string) {
        super(string);
        this.exceptionId = n;
        this.exceptionStr = string;
    }
}

