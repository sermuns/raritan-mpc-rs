/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.rccore.impl.keyboard.CharTranslator;
import nn.pp.rccore.impl.keyboard.CharTranslatorMappingBase;

public class CharTranslator_fr_to_en
extends CharTranslator {
    @Override
    public Locale getLocale() {
        return new Locale("fr", "US");
    }

    @Override
    public boolean allowAltGr() {
        return false;
    }

    @Override
    protected void addCharsToTranslate() {
        this.charMappings = new CharTranslatorMappingBase();
    }
}

