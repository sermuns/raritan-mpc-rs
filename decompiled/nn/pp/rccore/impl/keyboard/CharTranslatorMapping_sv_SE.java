/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_da_DK;

public class CharTranslatorMapping_sv_SE
extends CharTranslatorMapping_da_DK
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00a7', new int[]{544, 66080});
        this.addMapping('\u00bd', new int[]{131072, 544, 66080});
        this.addMapping('\\', new int[]{524288, 521, 66057});
        this.addMapping('|', new int[]{524288, 153, 65689});
    }
}

