/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.mouse;

import nn.pp.rccore.RCCore;

public interface MouseEventConsumer {
    public void consumeAbsoluteMouseEvent(int var1, int var2, int var3);

    public void consumeRelativeMouseEvent(int var1, int var2, int var3);

    public void consumeMouseWheelEvent(int var1, int var2);

    public void handleMouseSync(RCCore.MouseSyncType var1);
}

