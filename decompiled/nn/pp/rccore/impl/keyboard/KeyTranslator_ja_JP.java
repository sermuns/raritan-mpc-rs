/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.rccore.impl.keyboard.KeyTranslatorBase;

public class KeyTranslator_ja_JP
extends KeyTranslatorBase {
    @Override
    public Locale getLocale() {
        return new Locale("ja", "JP");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(512, 1, 25);
        this.addKeyMappingByCode(513, 1, 39);
        this.addKeyMappingByCode(514, 1, 12);
        this.addKeyMappingByCode(91, 1, 26);
        this.addKeyMappingByCode(92, 1, 112);
        this.addKeyMappingByCode(93, 1, 40);
        this.addKeyMappingByCode(243, 1, 0);
        this.addKeyMappingByCode(244, 1, 0);
        this.addKeyMappingByCode(241, 1, 110);
        this.addKeyMappingByCode(242, 1, 110);
        this.addKeyMappingByCode(245, 1, 110);
        this.addKeyMappingByCode(28, 1, 109);
        this.addKeyMappingByCode(29, 1, 108);
        this.addKeyMappingByCode(240, 1, 110);
        this.addKeyMappingByCode(348, 1, 114);
    }
}

