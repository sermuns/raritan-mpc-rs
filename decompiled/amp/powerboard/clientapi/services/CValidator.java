/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.services;

import amp.powerboard.clientapi.command.CCommand;

public abstract class CValidator {
    protected String userName = null;

    public abstract boolean isOperationValid(CCommand var1);

    public void setUser(String string) {
        this.userName = string;
    }
}

