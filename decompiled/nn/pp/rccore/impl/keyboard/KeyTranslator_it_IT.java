/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.core.Platform;
import nn.pp.rccore.impl.keyboard.KeyTranslatorBase;

public class KeyTranslator_it_IT
extends KeyTranslatorBase {
    @Override
    public Locale getLocale() {
        return new Locale("it", "IT");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(92, 1, 0);
        this.addKeyMappingByCode(222, 1, 11);
        this.addKeyMappingByCode(521, 1, 26);
        this.addKeyMappingByCode(153, 1, 42);
        this.addKeyMappingByCode(45, 1, 52);
        this.addKeyMappingByCode(549, 1, 39);
        this.addKeyMappingByCode(550, 1, 40);
        this.addKeyMappingByCharacter(236, 12);
        this.addKeyMappingByCharacter(94, 12);
        this.addKeyMappingByCharacter(27, 12);
        this.addKeyMappingByCharacter(232, 25);
        this.addKeyMappingByCharacter(233, 25);
        this.addKeyMappingByCharacter(91, 25);
        this.addKeyMappingByCharacter(123, 25);
        this.addKeyMappingByCharacter(242, 38);
        this.addKeyMappingByCharacter(231, 38);
        this.addKeyMappingByCharacter(64, 38);
        this.addKeyMappingByCharacter(224, 39);
        this.addKeyMappingByCharacter(176, 39);
        this.addKeyMappingByCharacter(35, 39);
        this.addKeyMappingByCharacter(249, 40);
        this.addKeyMappingByCharacter(167, 40);
        this.addKeyMappingByCharacter(28, 40);
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(204, 12);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(126, 12);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(200, 25);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(201, 25);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(210, 38);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(199, 38);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(192, 39);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(217, 40);
        }
    }
}

