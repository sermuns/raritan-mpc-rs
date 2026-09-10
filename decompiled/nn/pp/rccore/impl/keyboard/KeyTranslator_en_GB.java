/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.rccore.impl.keyboard.KeyTranslatorBase;

public class KeyTranslator_en_GB
extends KeyTranslatorBase {
    @Override
    public Locale getLocale() {
        return new Locale("en", "GB");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(520, 1, 40);
        this.addKeyMappingByCode(92, 1, 42);
    }
}

