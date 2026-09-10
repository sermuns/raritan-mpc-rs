/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.HashMap;
import java.util.Map;
import nn.pp.rccore.ILicenseSupport;
import nn.pp.rccore.impl.LicenseSupportListenerList;

public class LicenseSupport
implements ILicenseSupport,
ILicenseSupport.IListener {
    private LicenseSupportListenerList list;
    private Map<String, Boolean> features;

    public LicenseSupport(LicenseSupportListenerList licenseSupportListenerList) {
        this.list = licenseSupportListenerList;
        this.addListener(this);
    }

    @Override
    public boolean isFeatureLicensed(String string) {
        if (this.features == null) {
            return true;
        }
        Boolean bl = this.features.get(string.toLowerCase());
        if (bl != null) {
            return bl;
        }
        return false;
    }

    @Override
    public Map<String, Boolean> getLicensedFeatures() {
        return this.features;
    }

    @Override
    public void licenseFeatureSupportChanged(String string, boolean bl) {
        if (this.features == null) {
            this.features = new HashMap<String, Boolean>();
        }
        this.features.put(string.toLowerCase(), bl);
    }

    @Override
    public void addListener(ILicenseSupport.IListener iListener) {
        this.list.addListener(iListener);
    }

    @Override
    public void removeListener(ILicenseSupport.IListener iListener) {
        this.list.removeListener(iListener);
    }
}

