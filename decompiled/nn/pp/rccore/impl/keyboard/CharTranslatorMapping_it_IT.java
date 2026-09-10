/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_Europe;

public class CharTranslatorMapping_it_IT
extends CharTranslatorMapping_Europe
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00ec', new int[]{61, 65597});
        this.addMapping('\u00a3', new int[]{131072, 51, 65587});
        this.addMapping('^', new int[]{131072, 61, 65597});
        this.addMapping('\u00e8', new int[]{91, 65627});
        this.addMapping('\u00e9', new int[]{131072, 91, 65627});
        this.addMapping('*', new int[]{131072, 521, 66057});
        this.addMapping('[', new int[]{524288, 91, 65627});
        this.addMapping(']', new int[]{524288, 521, 66057});
        this.addMapping('{', new int[]{131072, 524288, 91, 65627});
        this.addMapping('}', new int[]{131072, 524288, 521, 66057});
        this.addMapping('\u00f2', new int[]{59, 65595});
        this.addMapping('\u00e0', new int[]{549, 66085});
        this.addMapping('\u00f9', new int[]{550, 66086});
        this.addMapping('\u00e7', new int[]{131072, 59, 65595});
        this.addMapping('\u00b0', new int[]{131072, 549, 66085});
        this.addMapping('\u00a7', new int[]{131072, 550, 66086});
        this.addMapping('@', new int[]{524288, 59, 65595});
        this.addMapping('#', new int[]{524288, 549, 66085});
    }
}

