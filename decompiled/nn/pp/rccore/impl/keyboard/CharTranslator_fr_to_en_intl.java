/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_us_intl;
import nn.pp.rccore.impl.keyboard.CharTranslator_fr_to_en;

public class CharTranslator_fr_to_en_intl
extends CharTranslator_fr_to_en {
    @Override
    public Locale getLocale() {
        return new Locale("fr", "en");
    }

    @Override
    public boolean allowAltGr() {
        return true;
    }

    @Override
    protected void addCharsToTranslate() {
        this.charMappings = new CharTranslatorMapping_us_intl();
    }
}

