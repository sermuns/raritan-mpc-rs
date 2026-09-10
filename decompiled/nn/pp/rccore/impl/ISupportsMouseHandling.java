/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import nn.pp.rccore.impl.mouse.MouseEventConsumer;
import nn.pp.rccore.impl.mouse.MouseHandler;

public interface ISupportsMouseHandling {
    public void setMouseEventConsumer(MouseEventConsumer var1);

    public MouseHandler getMouseHandler();
}

