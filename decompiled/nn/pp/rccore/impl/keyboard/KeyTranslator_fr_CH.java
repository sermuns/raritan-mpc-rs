/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.rccore.impl.keyboard.KeyTranslatorQwertz;

public class KeyTranslator_fr_CH
extends KeyTranslatorQwertz {
    @Override
    public Locale getLocale() {
        return new Locale("fr", "CH");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(135, 1, 26);
        this.addKeyMappingByCharacter(167, 0);
        this.addKeyMappingByCharacter(176, 0);
        this.addKeyMappingByCharacter(39, 11);
        this.addKeyMappingByCharacter(63, 11);
        this.addKeyMappingByCharacter(180, 11);
        this.addKeyMappingByCharacter(94, 12);
        this.addKeyMappingByCharacter(96, 12);
        this.addKeyMappingByCharacter(126, 12);
        this.addKeyMappingByCharacter(232, 25);
        this.addKeyMappingByCharacter(252, 25);
        this.addKeyMappingByCharacter(91, 25);
        this.addKeyMappingByCharacter(168, 26);
        this.addKeyMappingByCharacter(33, 26);
        this.addKeyMappingByCharacter(93, 26);
        this.addKeyMappingByCharacter(233, 38);
        this.addKeyMappingByCharacter(246, 38);
        this.addKeyMappingByCharacter(224, 39);
        this.addKeyMappingByCharacter(228, 39);
        this.addKeyMappingByCharacter(123, 39);
        this.addKeyMappingByCharacter(36, 40);
        this.addKeyMappingByCharacter(163, 40);
        this.addKeyMappingByCharacter(125, 40);
        this.addKeyMappingByCharacter(60, 42);
        this.addKeyMappingByCharacter(62, 42);
        this.addKeyMappingByCharacter(92, 42);
        this.addKeyMappingByCharacter(45, 52);
        this.addKeyMappingByCharacter(95, 52);
    }
}

