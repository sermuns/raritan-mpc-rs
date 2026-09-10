/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.core.Platform;
import nn.pp.rccore.impl.keyboard.KeyTranslatorBase;

public class KeyTranslator_es_ES
extends KeyTranslatorBase {
    @Override
    public Locale getLocale() {
        return new Locale("es", "ES");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(222, 1, 11);
        this.addKeyMappingByCode(518, 1, 12);
        this.addKeyMappingByCode(128, 1, 25);
        this.addKeyMappingByCode(521, 1, 26);
        this.addKeyMappingByCode(129, 1, 39);
        this.addKeyMappingByCode(153, 1, 42);
        this.addKeyMappingByCode(45, 1, 52);
        this.addKeyMappingByCharacter(186, 0);
        this.addKeyMappingByCharacter(170, 0);
        this.addKeyMappingByCharacter(92, 0);
        this.addKeyMappingByCharacter(241, 38);
        this.addKeyMappingByCharacter(209, 38);
        this.addKeyMappingByCharacter(231, 40);
        this.addKeyMappingByCharacter(199, 40);
        this.addKeyMappingByCharacter(125, 40);
        this.addKeyMappingByCharacter(28, 40);
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(60, 42);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(62, 42);
        }
    }
}

