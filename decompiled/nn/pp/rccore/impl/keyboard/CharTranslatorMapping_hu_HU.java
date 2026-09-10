/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_EastEurope;

public class CharTranslatorMapping_hu_HU
extends CharTranslatorMapping_EastEurope
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00a7', new int[]{131072, 48, 65584});
        this.addMapping('\'', new int[]{131072, 49, 65585});
        this.addMapping('+', new int[]{131072, 51, 65587});
        this.addMapping('!', new int[]{131072, 52, 65588});
        this.addMapping('/', new int[]{131072, 54, 65590});
        this.addMapping('=', new int[]{131072, 55, 65591});
        this.addMapping('\u02dd', new int[]{524288, 545, 66081, 0x100000, 32, 65568});
        this.addMapping('\u00a8', new int[]{524288, 546, 66082, 0x100000, 32, 65568});
        this.addMapping('\u00b8', new int[]{524288, 61, 65597, 0x100000, 32, 65568});
        this.addMapping('\u20ac', new int[]{524288, 85, 65621});
        this.addMapping('\u0111', new int[]{524288, 83, 65619});
        this.addMapping('\u0110', new int[]{272});
        this.addMapping('$', new int[]{524288, 59, 65595});
        this.addMapping('<', new int[]{524288, 153, 65689});
        this.addMapping('>', new int[]{524288, 89, 65625});
        this.addMapping('#', new int[]{524288, 88, 65624});
        this.addMapping('&', new int[]{524288, 67, 65603});
        this.addMapping(';', new int[]{524288, 44, 65580});
        this.addMapping('*', new int[]{524288, 45, 65581});
        this.addMapping('\u0151', new int[]{524288, 545, 66081, 0x100000, 79, 65615});
        this.addMapping('\u0150', new int[]{524288, 545, 66081, 0x100000, 131072, 79, 65615});
        this.addMapping('\u0171', new int[]{524288, 545, 66081, 0x100000, 85, 65621});
        this.addMapping('\u0170', new int[]{524288, 545, 66081, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00e4', new int[]{524288, 546, 66082, 0x100000, 65, 65601});
        this.addMapping('\u00c4', new int[]{524288, 546, 66082, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00eb', new int[]{524288, 546, 66082, 0x100000, 69, 65605});
        this.addMapping('\u00cb', new int[]{524288, 546, 66082, 0x100000, 131072, 69, 65605});
        this.addMapping('\u00f6', new int[]{524288, 546, 66082, 0x100000, 79, 65615});
        this.addMapping('\u00d6', new int[]{524288, 546, 66082, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00fc', new int[]{524288, 546, 66082, 0x100000, 85, 65621});
        this.addMapping('\u00dc', new int[]{524288, 546, 66082, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00e7', new int[]{524288, 61, 65597, 0x100000, 67, 65603});
        this.addMapping('\u00c7', new int[]{524288, 61, 65597, 0x100000, 131072, 67, 65603});
        this.addMapping('\u015f', new int[]{524288, 61, 65597, 0x100000, 83, 65619});
        this.addMapping('\u015e', new int[]{524288, 61, 65597, 0x100000, 131072, 83, 65619});
        this.addMapping('\u0163', new int[]{524288, 61, 65597, 0x100000, 84, 65620});
        this.addMapping('\u0162', new int[]{524288, 61, 65597, 0x100000, 131072, 84, 65620});
    }
}

