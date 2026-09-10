/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import java.awt.AWTEvent;
import java.awt.Component;

public class StatusEvent
extends AWTEvent {
    public static final int ID = 34334;
    private static final long serialVersionUID = -5590139190378798483L;
    private final Object message;

    public StatusEvent(Component component, Object object) {
        super(component, 34334);
        if (object == null) {
            throw new IllegalArgumentException();
        }
        this.message = object;
    }

    public Object getMessage() {
        return this.message;
    }
}

