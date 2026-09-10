/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl;

import nn.pp.core.impl.ListenerAction;
import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.scan.ScanSessionEventsListener;

public class ScanSessionEventsListenerList
extends ListenerList<ScanSessionEventsListener> {
    public void fireDisconnected(final Exception exception) {
        this.fire(new ListenerAction<ScanSessionEventsListener>(){

            @Override
            public void run() {
                ((ScanSessionEventsListener)this.listener).disconnected(exception);
            }
        });
    }

    public void firescanSessionCreated(final int n) {
        this.fire(new ListenerAction<ScanSessionEventsListener>(){

            @Override
            public void run() {
                ((ScanSessionEventsListener)this.listener).scanSessionCreated(n);
            }
        });
    }
}

