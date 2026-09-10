/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.rccore.impl.keyboard.KeyTranslatorBase;

public abstract class KeyTranslatorQwertz
extends KeyTranslatorBase {
    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(90, 1, 20);
        this.addKeyMappingByCode(89, 1, 43);
    }
}

