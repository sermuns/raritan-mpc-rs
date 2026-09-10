/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_EuropeDiacriticChars;

public class CharTranslatorMapping_pt_PT
extends CharTranslatorMapping_EuropeDiacriticChars
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00ab', new int[]{61, 65597});
        this.addMapping('\u00bb', new int[]{131072, 61, 65597});
        this.addMapping('\u00a7', new int[]{524288, 52, 65588});
        this.addMapping('*', new int[]{131072, 521, 66057});
        this.addMapping('`', new int[]{131072, 129, 65665, 262144, 32, 65568});
        this.addMapping('\u00a8', new int[]{524288, 521, 66057, 0x100000, 32, 65568});
        this.addMapping('\u00e7', new int[]{59, 65595});
        this.addMapping('\u00ba', new int[]{549, 66085});
        this.addMapping('~', new int[]{131, 65667, 32, 65568});
        this.addMapping('\u00c7', new int[]{131072, 59, 65595});
        this.addMapping('\u00aa', new int[]{131072, 549, 66085});
        this.addMapping('^', new int[]{131072, 131, 65667, 262144, 32, 65568});
        this.addMapping('\u00e2', new int[]{131072, 131, 65667, 262144, 65, 65601});
        this.addMapping('\u00c2', new int[]{131072, 131, 65667, 65, 65601});
        this.addMapping('\u00e3', new int[]{131, 65667, 65, 65601});
        this.addMapping('\u00c3', new int[]{131, 65667, 131072, 65, 65601});
        this.addMapping('\u00e4', new int[]{524288, 521, 66057, 0x100000, 65, 65601});
        this.addMapping('\u00c4', new int[]{524288, 521, 66057, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00ea', new int[]{131072, 131, 65667, 262144, 69, 65605});
        this.addMapping('\u00ca', new int[]{131072, 131, 65667, 69, 65605});
        this.addMapping('\u00eb', new int[]{524288, 521, 66057, 0x100000, 69, 65605});
        this.addMapping('\u00cb', new int[]{524288, 521, 66057, 0x100000, 131072, 69, 65605});
        this.addMapping('\u00ee', new int[]{131072, 131, 65667, 262144, 73, 65609});
        this.addMapping('\u00ce', new int[]{131072, 131, 65667, 73, 65609});
        this.addMapping('\u00ef', new int[]{524288, 521, 66057, 0x100000, 73, 65609});
        this.addMapping('\u00cf', new int[]{524288, 521, 66057, 0x100000, 131072, 73, 65609});
        this.addMapping('\u00f1', new int[]{131, 65667, 78, 65614});
        this.addMapping('\u00d1', new int[]{131, 65667, 131072, 78, 65614});
        this.addMapping('\u00f4', new int[]{131072, 131, 65667, 262144, 79, 65615});
        this.addMapping('\u00d4', new int[]{131072, 131, 65667, 79, 65615});
        this.addMapping('\u00f5', new int[]{131, 65667, 79, 65615});
        this.addMapping('\u00d5', new int[]{131, 65667, 131072, 79, 65615});
        this.addMapping('\u00f6', new int[]{524288, 521, 66057, 0x100000, 79, 65615});
        this.addMapping('\u00d6', new int[]{524288, 521, 66057, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00fb', new int[]{131072, 131, 65667, 262144, 85, 65621});
        this.addMapping('\u00db', new int[]{131072, 131, 65667, 131072, 85, 65621});
        this.addMapping('\u00fc', new int[]{524288, 521, 66057, 0x100000, 85, 65621});
        this.addMapping('\u00dc', new int[]{524288, 521, 66057, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00ff', new int[]{524288, 521, 66057, 0x100000, 89, 65625});
    }
}

