/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import java.util.EventObject;

public class BrowsingEvent
extends EventObject {
    public static final short FOUND = 0;
    public static final short LOST = 1;
    private static final long serialVersionUID = 3515427717844920737L;
    private short mCommand;

    public BrowsingEvent(Object object, short s) {
        super(object);
        this.mCommand = s;
    }

    public short getCommand() {
        return this.mCommand;
    }

    public void setCommand(short s) {
        this.mCommand = s;
    }
}

