/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.rccore.impl.keyboard.KeyTranslator_sl_SL;

public class KeyTranslator_hu_HU
extends KeyTranslator_sl_SL {
    @Override
    public Locale getLocale() {
        return new Locale("hu", "HU");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(48, 1, 0);
        this.addKeyMappingByCode(545, 1, 10);
        this.addKeyMappingByCode(546, 1, 11);
        this.addKeyMappingByCharacter(126, 1);
        this.addKeyMappingByCharacter(96, 7);
        this.addKeyMappingByCharacter(246, 10);
        this.addKeyMappingByCharacter(214, 10);
        this.addKeyMappingByCharacter(252, 11);
        this.addKeyMappingByCharacter(220, 11);
        this.addKeyMappingByCharacter(243, 12);
        this.addKeyMappingByCharacter(211, 12);
        this.addKeyMappingByCharacter(337, 25);
        this.addKeyMappingByCharacter(336, 25);
        this.addKeyMappingByCharacter(247, 25);
        this.addKeyMappingByCharacter(250, 26);
        this.addKeyMappingByCharacter(218, 26);
        this.addKeyMappingByCharacter(215, 26);
        this.addKeyMappingByCharacter(233, 38);
        this.addKeyMappingByCharacter(201, 38);
        this.addKeyMappingByCharacter(36, 38);
        this.addKeyMappingByCharacter(225, 39);
        this.addKeyMappingByCharacter(193, 39);
        this.addKeyMappingByCharacter(223, 39);
        this.addKeyMappingByCharacter(369, 40);
        this.addKeyMappingByCharacter(368, 40);
        this.addKeyMappingByCharacter(164, 40);
        this.addKeyMappingByCharacter(237, 42);
        this.addKeyMappingByCharacter(205, 42);
        this.addKeyMappingByCharacter(60, 42);
    }
}

