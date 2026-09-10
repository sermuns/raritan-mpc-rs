/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.SmartCardSessionEventsListener;
import nn.pp.core.impl.ListenerAction;
import nn.pp.core.impl.ListenerList;

public class SmartCardSessionEventsListenerList
extends ListenerList<SmartCardSessionEventsListener> {
    public void fireDisconnected(final Exception exception) {
        this.fire(new ListenerAction<SmartCardSessionEventsListener>(){

            @Override
            public void run() {
                ((SmartCardSessionEventsListener)this.listener).disconnected(exception);
            }
        });
    }

    public void firecardReaderMounted(final String string) {
        this.fire(new ListenerAction<SmartCardSessionEventsListener>(){

            @Override
            public void run() {
                ((SmartCardSessionEventsListener)this.listener).cardReaderMounted(string);
            }
        });
    }
}

