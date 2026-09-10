/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.Map;

public interface ILicenseSupport {
    public boolean isFeatureLicensed(String var1);

    public Map<String, Boolean> getLicensedFeatures();

    public void addListener(IListener var1);

    public void removeListener(IListener var1);

    public static interface IListener {
        public void licenseFeatureSupportChanged(String var1, boolean var2);
    }
}

