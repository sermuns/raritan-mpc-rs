/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.core.Platform;
import nn.pp.rccore.impl.keyboard.KeyTranslatorQwertz;

public class KeyTranslator_de_DE
extends KeyTranslatorQwertz {
    @Override
    public Locale getLocale() {
        return new Locale("de", "DE");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(45, 1, 52);
        this.addKeyMappingByCode(47, 1, 52);
        this.addKeyMappingByCode(520, 1, 40);
        this.addKeyMappingByCode(92, 1, 40);
        this.addKeyMappingByCode(130, 1, 0);
        this.addKeyMappingByCode(514, 1, 0);
        this.addKeyMappingByCode(129, 1, 12);
        this.addKeyMappingByCode(61, 1, 12);
        this.addKeyMappingByCode(153, 1, 42);
        this.addKeyMappingByCode(521, 1, 26);
        this.addKeyMappingByCode(108, 4, 101);
        this.addKeyMappingByCode(546, 1, 11);
        this.addKeyMappingByCharacter(63, 11);
        this.addKeyMappingByCharacter(92, 11);
        this.addKeyMappingByCharacter(223, 11);
        this.addKeyMappingByCharacter(95, 52);
        this.addKeyMappingByCharacter(124, 42);
        this.addKeyMappingByCharacter(246, 38);
        this.addKeyMappingByCharacter(214, 38);
        this.addKeyMappingByCharacter(228, 39);
        this.addKeyMappingByCharacter(196, 39);
        this.addKeyMappingByCharacter(252, 25);
        this.addKeyMappingByCharacter(220, 25);
        this.addKeyMappingByCharacter(91, 8);
        this.addKeyMappingByCharacter(93, 9);
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(172, 0);
        }
    }
}

