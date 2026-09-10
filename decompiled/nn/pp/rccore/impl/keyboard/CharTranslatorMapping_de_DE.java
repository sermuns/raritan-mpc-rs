/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_Germanic;

public class CharTranslatorMapping_de_DE
extends CharTranslatorMapping_Germanic
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00df', new int[]{546, 66082});
        this.addMapping('\u00b0', new int[]{131072, 130, 65666});
        this.addMapping('\u00a7', new int[]{131072, 51, 65587});
        this.addMapping('?', new int[]{131072, 546, 66082});
        this.addMapping('\\', new int[]{524288, 546, 66082});
        this.addMapping('*', new int[]{131072, 521, 66057});
        this.addMapping('#', new int[]{520, 66056});
        this.addMapping('\'', new int[]{131072, 520, 66056});
        this.addMapping('@', new int[]{524288, 81, 65617});
        this.addMapping('~', new int[]{524288, 521, 66057});
        this.addMapping('|', new int[]{524288, 153, 65689});
        this.addMapping('\u00e4', new int[]{222, 65758});
        this.addMapping('\u00c4', new int[]{131072, 222, 65758});
        this.addMapping('\u00f6', new int[]{59, 65595});
        this.addMapping('\u00d6', new int[]{131072, 59, 65595});
        this.addMapping('\u00fc', new int[]{91, 65627});
        this.addMapping('\u00dc', new int[]{131072, 91, 65627});
    }
}

