/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.KeyboardListener;
import nn.pp.rccore.impl.KeyboardListenerAction;

public class KeyboardListenerList
extends ListenerList<KeyboardListener> {
    public void fireKeyboardEvent(final int n, final boolean bl) {
        this.fire(new KeyboardListenerAction(){

            @Override
            public void run() {
                ((KeyboardListener)this.listener).keyboardEvent(n, bl);
            }
        });
    }

    public void fireHotkeyDetected(final int n) {
        this.fire(new KeyboardListenerAction(){

            @Override
            public void run() {
                ((KeyboardListener)this.listener).hotkeyDetected(n);
            }
        });
    }

    public void fireCtrlAltReleaseDetected(final boolean bl) {
        this.fire(new KeyboardListenerAction(){

            @Override
            public void run() {
                ((KeyboardListener)this.listener).ctrlAltReleaseDetected(bl);
            }
        });
    }
}

