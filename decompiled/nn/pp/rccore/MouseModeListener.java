/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.List;
import nn.pp.rccore.RCCore;

public interface MouseModeListener {
    public static final int MOUSE_MODES = 1;
    public static final int SINGLE_CURSOR = 2;
    public static final int MOUSE_SYNC = 4;
    public static final int ALL = 7;

    public void mouseModeChangeSupportChanged(boolean var1);

    public void supportedMouseModesChanged(List<RCCore.MouseMode> var1);

    public void mouseModeChanged(RCCore.MouseMode var1);

    public void singleCursorModeSupportChanged(boolean var1);

    public void singleCursorModeChanged(boolean var1);

    public void mouseSyncSupportChanged(boolean var1);
}

