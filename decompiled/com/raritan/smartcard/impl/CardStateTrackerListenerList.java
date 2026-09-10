/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.SmartCardReaderListener;
import nn.pp.core.impl.ListenerAction;
import nn.pp.core.impl.ListenerList;

public class CardStateTrackerListenerList
extends ListenerList<SmartCardReaderListener> {
    public void fireCardInserted(final String string) {
        this.fire(new ListenerAction<SmartCardReaderListener>(){

            @Override
            public void run() {
                ((SmartCardReaderListener)this.listener).cardInserted(string);
            }
        });
    }

    public void fireCardRemoved(final String string) {
        this.fire(new ListenerAction<SmartCardReaderListener>(){

            @Override
            public void run() {
                ((SmartCardReaderListener)this.listener).cardRemoved(string);
            }
        });
    }

    public void fireCardReaderRemoved(final String string) {
        this.fire(new ListenerAction<SmartCardReaderListener>(){

            @Override
            public void run() {
                ((SmartCardReaderListener)this.listener).cardReaderRemoved(string);
            }
        });
    }
}

