/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.ILicenseSupport;
import nn.pp.rccore.impl.LicenseSupportListenerAction;

public class LicenseSupportListenerList
extends ListenerList<ILicenseSupport.IListener> {
    public void fireLicenseFeatureSupportChanged(final String string, final boolean bl) {
        this.fire(new LicenseSupportListenerAction(){

            @Override
            public void run() {
                ((ILicenseSupport.IListener)this.listener).licenseFeatureSupportChanged(string, bl);
            }
        });
    }
}

