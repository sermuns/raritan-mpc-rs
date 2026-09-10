/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMappingBase;

public class CharTranslatorMapping_Europe
extends CharTranslatorMappingBase
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\"', new int[]{131072, 50, 65586});
        this.addMapping('&', new int[]{131072, 54, 65590});
        this.addMapping('/', new int[]{131072, 55, 65591});
        this.addMapping('(', new int[]{131072, 56, 65592});
        this.addMapping(')', new int[]{131072, 57, 65593});
        this.addMapping('=', new int[]{131072, 48, 65584});
        this.addMapping('?', new int[]{131072, 222, 65758});
        this.addMapping('@', new int[]{524288, 50, 65586});
        this.addMapping('\u00a3', new int[]{524288, 51, 65587});
        this.addMapping('{', new int[]{524288, 55, 65591});
        this.addMapping('[', new int[]{524288, 56, 65592});
        this.addMapping(']', new int[]{524288, 57, 65593});
        this.addMapping('}', new int[]{524288, 48, 65584});
        this.addMapping('\u20ac', new int[]{524288, 69, 65605});
        this.addMapping('<', new int[]{153, 65689});
        this.addMapping('>', new int[]{131072, 153, 65689});
        this.addMapping(';', new int[]{131072, 44, 65580});
        this.addMapping(':', new int[]{131072, 46, 65582});
        this.addMapping('+', new int[]{521, 66057});
    }
}

