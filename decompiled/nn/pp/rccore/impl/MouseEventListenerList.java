/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.MouseEventListener;
import nn.pp.rccore.impl.MouseEventListenerAction;

public class MouseEventListenerList
extends ListenerList<MouseEventListener> {
    public void fireAbsoluteMouseEvent(final int n, final int n2, final int n3) {
        this.fire(new MouseEventListenerAction(){

            @Override
            public void run() {
                ((MouseEventListener)this.listener).absoluteMouseEvent(n, n2, n3);
            }
        }, 1);
    }

    public void fireRelativeMouseEvent(final int n, final int n2, final int n3) {
        this.fire(new MouseEventListenerAction(){

            @Override
            public void run() {
                ((MouseEventListener)this.listener).relativeMouseEvent(n, n2, n3);
            }
        }, 2);
    }

    public void fireMouseWheelEvent(final int n, final int n2) {
        this.fire(new MouseEventListenerAction(){

            @Override
            public void run() {
                ((MouseEventListener)this.listener).mouseWheelEvent(n, n2);
            }
        }, 4);
    }
}

