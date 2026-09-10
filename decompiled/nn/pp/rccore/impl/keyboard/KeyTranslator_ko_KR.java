/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.rccore.impl.keyboard.KeyTranslatorBase;

public class KeyTranslator_ko_KR
extends KeyTranslatorBase {
    @Override
    public Locale getLocale() {
        return new Locale("ko", "KR");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(262, 1, 115);
        this.addKeyMappingByCode(263, 1, 116);
    }
}

