/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMappingBase;

public class CharTranslatorMapping_en_GB
extends CharTranslatorMappingBase
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00ac', new int[]{131072, 192, 65728});
        this.addMapping('\"', new int[]{131072, 50, 65586});
        this.addMapping('\u00a3', new int[]{131072, 51, 65587});
        this.addMapping('\u00a6', new int[]{524288, 192, 65728});
        this.addMapping('\u20ac', new int[]{524288, 52, 65588});
        this.addMapping('#', new int[]{520, 66056});
        this.addMapping('@', new int[]{131072, 222, 65758});
        this.addMapping('~', new int[]{131072, 520, 66056});
        this.addMapping('\u00e1', new int[]{524288, 65, 65601});
        this.addMapping('\u00c1', new int[]{524288, 65, 65601});
        this.addMapping('\u00e9', new int[]{524288, 69, 65605});
        this.addMapping('\u00c9', new int[]{524288, 69, 65605});
        this.addMapping('\u00ed', new int[]{524288, 73, 65609});
        this.addMapping('\u00cd', new int[]{524288, 73, 65609});
        this.addMapping('\u00f3', new int[]{524288, 79, 65615});
        this.addMapping('\u00d3', new int[]{524288, 79, 65615});
        this.addMapping('\u00fa', new int[]{524288, 85, 65621});
        this.addMapping('\u00da', new int[]{524288, 85, 65621});
    }
}

