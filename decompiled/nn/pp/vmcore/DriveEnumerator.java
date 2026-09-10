/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore;

import java.util.List;
import java.util.logging.Logger;
import nn.pp.vmcore.RedirectableObject;

public interface DriveEnumerator {
    public List<RedirectableObject> getAvailableDrives(Logger var1);
}

