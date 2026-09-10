/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import nn.pp.rccore.impl.keyboard.KeyboardEventConsumer;
import nn.pp.rccore.impl.keyboard.KeyboardHandler;

public interface ISupportsKeyboardHandling {
    public void setKeyboardEventConsumer(KeyboardEventConsumer var1);

    public KeyboardHandler getKeyboardHandler();
}

