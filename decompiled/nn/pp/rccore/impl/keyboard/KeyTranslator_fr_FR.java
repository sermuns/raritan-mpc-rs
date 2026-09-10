/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.core.Platform;
import nn.pp.rccore.impl.keyboard.KeyTranslatorBase;

public class KeyTranslator_fr_FR
extends KeyTranslatorBase {
    @Override
    public Locale getLocale() {
        return new Locale("fr", "FR");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(81, 1, 29);
        this.addKeyMappingByCode(87, 1, 43);
        this.addKeyMappingByCode(65, 1, 15);
        this.addKeyMappingByCode(90, 1, 16);
        this.addKeyMappingByCode(77, 1, 38);
        this.addKeyMappingByCode(150, 1, 1);
        this.addKeyMappingByCode(152, 1, 3);
        this.addKeyMappingByCode(222, 1, 4);
        this.addKeyMappingByCode(519, 1, 5);
        this.addKeyMappingByCode(45, 1, 6);
        this.addKeyMappingByCode(151, 1, 40);
        this.addKeyMappingByCode(153, 1, 42);
        this.addKeyMappingByCode(523, 1, 8);
        this.addKeyMappingByCode(44, 1, 49);
        this.addKeyMappingByCode(59, 1, 50);
        this.addKeyMappingByCode(513, 1, 51);
        this.addKeyMappingByCode(517, 1, 52);
        this.addKeyMappingByCode(522, 1, 11);
        this.addKeyMappingByCode(130, 1, 25);
        this.addKeyMappingByCode(515, 1, 26);
        this.addKeyMappingByCode(549, 1, 39);
        this.addKeyMappingByCharacter(178, 0);
        this.addKeyMappingByCharacter(249, 39);
        this.addKeyMappingByCharacter(37, 39);
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(172, 0);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(126, 2);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(233, 2);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(50, 2);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(35, 3);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(123, 4);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(91, 5);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(124, 6);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(45, 6);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(96, 7);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(232, 7);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(55, 7);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(95, 8);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(92, 8);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(231, 9);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(199, 9);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(57, 9);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(64, 10);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(224, 10);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(48, 10);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(41, 11);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(176, 11);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(93, 11);
        }
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(125, 12);
        }
    }
}

