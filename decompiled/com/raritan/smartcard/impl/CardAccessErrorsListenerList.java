/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.CardAccessErrorsListener;
import nn.pp.core.impl.ListenerAction;
import nn.pp.core.impl.ListenerList;

public class CardAccessErrorsListenerList
extends ListenerList<CardAccessErrorsListener> {
    public void fireNoProtocolSupported(final String string) {
        this.fire(new ListenerAction<CardAccessErrorsListener>(){

            @Override
            public void run() {
                ((CardAccessErrorsListener)this.listener).noProtocolSupported(string);
            }
        });
    }

    public void fireErrorOnAccessingCard(final String string) {
        this.fire(new ListenerAction<CardAccessErrorsListener>(){

            @Override
            public void run() {
                ((CardAccessErrorsListener)this.listener).errorOnAccessingCard(string);
            }
        });
    }
}

