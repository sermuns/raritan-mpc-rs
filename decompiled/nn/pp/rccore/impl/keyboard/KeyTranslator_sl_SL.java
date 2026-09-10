/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.rccore.impl.keyboard.KeyTranslatorQwertz;

public class KeyTranslator_sl_SL
extends KeyTranslatorQwertz {
    @Override
    public Locale getLocale() {
        return new Locale("sl", "SL");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(139, 1, 0);
        this.addKeyMappingByCode(222, 1, 11);
        this.addKeyMappingByCode(521, 1, 12);
        this.addKeyMappingByCode(153, 1, 42);
        this.addKeyMappingByCode(45, 1, 52);
        this.addKeyMappingByCode(549, 1, 39);
        this.addKeyMappingByCharacter(353, 25);
        this.addKeyMappingByCharacter(352, 25);
        this.addKeyMappingByCharacter(247, 25);
        this.addKeyMappingByCharacter(273, 26);
        this.addKeyMappingByCharacter(272, 26);
        this.addKeyMappingByCharacter(215, 26);
        this.addKeyMappingByCharacter(269, 38);
        this.addKeyMappingByCharacter(268, 38);
        this.addKeyMappingByCharacter(263, 39);
        this.addKeyMappingByCharacter(262, 39);
        this.addKeyMappingByCharacter(223, 39);
        this.addKeyMappingByCharacter(382, 40);
        this.addKeyMappingByCharacter(381, 40);
        this.addKeyMappingByCharacter(164, 40);
    }
}

