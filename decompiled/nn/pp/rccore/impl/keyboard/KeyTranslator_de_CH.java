/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.core.Platform;
import nn.pp.rccore.impl.keyboard.KeyTranslatorQwertz;

public class KeyTranslator_de_CH
extends KeyTranslatorQwertz {
    @Override
    public Locale getLocale() {
        return new Locale("de", "CH");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(135, 1, 26);
        this.addKeyMappingByCode(222, 1, 11);
        this.addKeyMappingByCode(130, 1, 12);
        this.addKeyMappingByCode(515, 1, 40);
        this.addKeyMappingByCode(153, 1, 42);
        this.addKeyMappingByCode(45, 1, 52);
        this.addKeyMappingByCode(544, 1, 0);
        this.addKeyMappingByCode(549, 1, 39);
        this.addKeyMappingByCharacter(167, 0);
        this.addKeyMappingByCharacter(176, 0);
        this.addKeyMappingByCharacter(232, 25);
        this.addKeyMappingByCharacter(252, 25);
        this.addKeyMappingByCharacter(200, 25);
        this.addKeyMappingByCharacter(220, 25);
        this.addKeyMappingByCharacter(91, 25);
        this.addKeyMappingByCharacter(233, 38);
        this.addKeyMappingByCharacter(246, 38);
        this.addKeyMappingByCharacter(201, 38);
        this.addKeyMappingByCharacter(214, 38);
        this.addKeyMappingByCharacter(224, 39);
        this.addKeyMappingByCharacter(228, 39);
        this.addKeyMappingByCharacter(192, 39);
        this.addKeyMappingByCharacter(196, 39);
        this.addKeyMappingByCharacter(123, 39);
        this.addKeyMappingByCharacter(36, 40);
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(168, 26);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(33, 26);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(93, 26);
        }
    }
}

