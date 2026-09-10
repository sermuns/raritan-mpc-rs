/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.List;
import nn.pp.rccore.IUsbProfile;

public interface IUsbProfileList {
    public List<IUsbProfile> getProfiles();

    public IUsbProfile getActive();

    public IUsbProfile getPreferred();
}

