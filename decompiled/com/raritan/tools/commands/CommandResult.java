/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.commands;

import java.io.Serializable;

public class CommandResult
implements Serializable {
    private static final long serialVersionUID = -8562694698723962418L;
    private boolean isSuccess;
    private String statusMessage;
    private Object[] errorDescription;

    public CommandResult() {
        this.isSuccess = false;
        this.statusMessage = "";
        this.errorDescription = null;
    }

    public CommandResult(boolean bl, String string) {
        this.isSuccess = bl;
        this.statusMessage = string;
    }

    public void setIsSuccess(boolean bl) {
        this.isSuccess = bl;
    }

    public void setErrorDescription(Object[] objectArray) {
        this.errorDescription = objectArray;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public void setStatusMessage(String string) {
        this.statusMessage = string;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public Object[] getErrorDescription() {
        return this.errorDescription;
    }

    public boolean hasErrorDescription() {
        boolean bl;
        boolean bl2 = bl = this.errorDescription != null && this.errorDescription.length > 0;
        if (!bl) {
            bl = this.statusMessage != null && !"".equals(this.statusMessage.trim());
        }
        return bl;
    }
}

