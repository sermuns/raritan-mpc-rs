/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.core.Platform;
import nn.pp.rccore.impl.keyboard.KeyTranslatorBase;

public class KeyTranslator_pt_PT
extends KeyTranslatorBase {
    @Override
    public Locale getLocale() {
        return new Locale("pt", "PT");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(92, 1, 0);
        this.addKeyMappingByCode(222, 1, 11);
        this.addKeyMappingByCode(521, 1, 25);
        this.addKeyMappingByCode(129, 1, 26);
        this.addKeyMappingByCode(131, 1, 40);
        this.addKeyMappingByCode(153, 1, 42);
        this.addKeyMappingByCode(45, 1, 52);
        this.addKeyMappingByCode(549, 1, 39);
        this.addKeyMappingByCharacter(171, 12);
        this.addKeyMappingByCharacter(187, 12);
        this.addKeyMappingByCharacter(231, 38);
        this.addKeyMappingByCharacter(199, 38);
        this.addKeyMappingByCharacter(186, 39);
        this.addKeyMappingByCharacter(170, 39);
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(184, 12);
        }
    }
}

