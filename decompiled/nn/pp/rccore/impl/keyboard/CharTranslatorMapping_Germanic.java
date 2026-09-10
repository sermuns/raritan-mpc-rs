/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_EuropeDiacriticChars;

public class CharTranslatorMapping_Germanic
extends CharTranslatorMapping_EuropeDiacriticChars
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('?', new int[]{131072, 521, 66057});
        this.addMapping('\u00b2', new int[]{262144, 524288, 50, 65586});
        this.addMapping('\u00b3', new int[]{262144, 524288, 51, 65587});
        this.addMapping('\u00b5', new int[]{262144, 524288, 77, 65613});
    }
}

