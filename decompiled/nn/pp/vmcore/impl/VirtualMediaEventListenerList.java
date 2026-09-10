/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import nn.pp.core.impl.ListenerList;
import nn.pp.vmcore.VirtualMediaEventListener;
import nn.pp.vmcore.impl.VirtualMediaEventListenerAction;
import nn.pp.vmcore.impl.VirtualMediaEventListenerActionLockFail;

public class VirtualMediaEventListenerList
extends ListenerList<VirtualMediaEventListener> {
    public void fireDisconnected(final Exception exception) {
        this.fire(new VirtualMediaEventListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaEventListener)this.listener).disconnected(exception);
            }
        });
    }

    public VirtualMediaEventListener.LockFailAction fireDriveLockingFailed() {
        VirtualMediaEventListener.LockFailAction lockFailAction = new ListenerList.FireResult<VirtualMediaEventListener.LockFailAction>().fire(new VirtualMediaEventListenerActionLockFail(){

            @Override
            public void run() {
                this.result = ((VirtualMediaEventListener)this.listener).driveLockingFailed();
            }
        }, 1);
        if (lockFailAction != null) {
            return lockFailAction;
        }
        return VirtualMediaEventListener.LockFailAction.CANCEL;
    }

    public void fireVirtualMediaDriveConnected(final boolean bl) {
        this.fire(new VirtualMediaEventListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaEventListener)this.listener).driveConnected(bl);
            }
        }, 2);
    }

    public void fireVirtualMediaDriveDisConnected(final boolean bl) {
        this.fire(new VirtualMediaEventListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaEventListener)this.listener).driveDisconnectedByUser(bl);
            }
        }, 4);
    }
}

