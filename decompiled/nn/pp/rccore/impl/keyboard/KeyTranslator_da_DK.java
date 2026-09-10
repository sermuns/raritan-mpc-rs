/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.rccore.impl.keyboard.KeyTranslatorBase;

public class KeyTranslator_da_DK
extends KeyTranslatorBase {
    @Override
    public Locale getLocale() {
        return new Locale("da", "DK");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(521, 1, 11);
        this.addKeyMappingByCode(129, 1, 12);
        this.addKeyMappingByCode(92, 1, 42);
        this.addKeyMappingByCode(135, 1, 26);
        this.addKeyMappingByCode(222, 1, 40);
        this.addKeyMappingByCode(153, 1, 42);
        this.addKeyMappingByCode(45, 1, 52);
        this.addKeyMappingByCode(544, 1, 0);
        this.addKeyMappingByCode(547, 1, 25);
        this.addKeyMappingByCode(548, 1, 38);
        this.addKeyMappingByCode(549, 1, 39);
        this.addKeyMappingByCharacter(167, 0);
        this.addKeyMappingByCharacter(189, 0);
        this.addKeyMappingByCharacter(124, 13);
        this.addKeyMappingByCharacter(229, 25);
        this.addKeyMappingByCharacter(197, 25);
        this.addKeyMappingByCharacter(248, 39);
        this.addKeyMappingByCharacter(216, 39);
        this.addKeyMappingByCharacter(230, 38);
        this.addKeyMappingByCharacter(198, 38);
    }
}

