/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.List;
import java.util.Vector;
import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.MouseModeListener;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.MouseModeListenerAction;

public class MouseModeListenerList
extends ListenerList<MouseModeListener> {
    public void fireMouseModeChangeSupportChanged(final boolean bl) {
        this.fire(new MouseModeListenerAction(){

            @Override
            public void run() {
                ((MouseModeListener)this.listener).mouseModeChangeSupportChanged(bl);
            }
        }, 1);
    }

    public void fireSupportedMouseModesChanged(List<RCCore.MouseMode> list) {
        final Vector<RCCore.MouseMode> vector = new Vector<RCCore.MouseMode>(list);
        this.fire(new MouseModeListenerAction(){

            @Override
            public void run() {
                ((MouseModeListener)this.listener).supportedMouseModesChanged(vector);
            }
        }, 1);
    }

    public void fireMouseModeChanged(final RCCore.MouseMode mouseMode) {
        this.fire(new MouseModeListenerAction(){

            @Override
            public void run() {
                ((MouseModeListener)this.listener).mouseModeChanged(mouseMode);
            }
        }, 1);
    }

    public void fireSingleCursorModeSupportChanged(final boolean bl) {
        this.fire(new MouseModeListenerAction(){

            @Override
            public void run() {
                ((MouseModeListener)this.listener).singleCursorModeSupportChanged(bl);
            }
        }, 2);
    }

    public void fireSingleCursorModeChanged(final boolean bl) {
        this.fire(new MouseModeListenerAction(){

            @Override
            public void run() {
                ((MouseModeListener)this.listener).singleCursorModeChanged(bl);
            }
        }, 2);
    }

    public void fireMouseSyncSupportChanged(final boolean bl) {
        this.fire(new MouseModeListenerAction(){

            @Override
            public void run() {
                ((MouseModeListener)this.listener).mouseSyncSupportChanged(bl);
            }
        }, 4);
    }
}

