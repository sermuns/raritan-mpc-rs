/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.core.Platform;
import nn.pp.rccore.impl.keyboard.KeyTranslator_da_DK;

public class KeyTranslator_sv_SE
extends KeyTranslator_da_DK {
    @Override
    public Locale getLocale() {
        return new Locale("sv", "SE");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(92, 1, 11);
        this.addKeyMappingByCharacter(124, 42);
        this.addKeyMappingByCharacter(229, 25);
        this.addKeyMappingByCharacter(197, 25);
        this.addKeyMappingByCharacter(228, 39);
        this.addKeyMappingByCharacter(196, 39);
        this.addKeyMappingByCharacter(246, 38);
        this.addKeyMappingByCharacter(214, 38);
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(182, 0);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(248, 38);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(216, 38);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(230, 39);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(198, 39);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(180, 40);
        }
    }
}

