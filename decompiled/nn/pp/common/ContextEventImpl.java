/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import nn.pp.common.ContextEvent;

public class ContextEventImpl
implements ContextEvent {
    private String key;

    public ContextEventImpl(String string) {
        this.key = string;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

