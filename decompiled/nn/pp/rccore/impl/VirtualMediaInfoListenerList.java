/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.List;
import java.util.Vector;
import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.UsbProfileList;
import nn.pp.rccore.VMConfigInfo;
import nn.pp.rccore.VMMountRequestResponse;
import nn.pp.rccore.VirtualMediaInfoListener;
import nn.pp.rccore.impl.VirtualMediaInfoListenerAction;

public class VirtualMediaInfoListenerList
extends ListenerList<VirtualMediaInfoListener> {
    public void fireVirtualMediaSupportChanged(final boolean bl) {
        this.fire(new VirtualMediaInfoListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaInfoListener)this.listener).virtualMediaSupportChanged(bl);
            }
        }, 1);
    }

    public void fireVirtualMediaReadOnlyChanged(final boolean bl) {
        this.fire(new VirtualMediaInfoListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaInfoListener)this.listener).virtualMediaReadOnlyChanged(bl);
            }
        }, 1);
    }

    public void fireVirtualMediaDriveCountChanged(final int n) {
        this.fire(new VirtualMediaInfoListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaInfoListener)this.listener).virtualMediaDriveCountChanged(n);
            }
        }, 1);
    }

    public void fireRemoteIsoSupportChanged(final boolean bl) {
        this.fire(new VirtualMediaInfoListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaInfoListener)this.listener).remoteIsoSupportChanged(bl);
            }
        }, 2);
    }

    public void fireRemoteIsoListChanged(List<VMMountRequestResponse> list) {
        final Vector<VMMountRequestResponse> vector = new Vector<VMMountRequestResponse>(list);
        this.fire(new VirtualMediaInfoListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaInfoListener)this.listener).remoteIsoListChanged(vector);
            }
        }, 2);
    }

    public void fireRemoteIsoMountFinished(VMMountRequestResponse vMMountRequestResponse) {
        final VMMountRequestResponse vMMountRequestResponse2 = new VMMountRequestResponse(vMMountRequestResponse);
        this.fire(new VirtualMediaInfoListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaInfoListener)this.listener).remoteIsoMountFinished(vMMountRequestResponse2);
            }
        }, 2);
    }

    public void fireVirtualMediaConfigChanged(VMConfigInfo vMConfigInfo) {
        final VMConfigInfo vMConfigInfo2 = vMConfigInfo;
        this.fire(new VirtualMediaInfoListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaInfoListener)this.listener).virtualMediaConfigChanged(vMConfigInfo2);
            }
        }, 4);
    }

    public void fireUsbProfileListChanged(UsbProfileList usbProfileList) {
        final UsbProfileList usbProfileList2 = usbProfileList;
        this.fire(new VirtualMediaInfoListenerAction(){

            @Override
            public void run() {
                ((VirtualMediaInfoListener)this.listener).UsbProfileListChanged(usbProfileList2);
            }
        }, 4);
    }
}

