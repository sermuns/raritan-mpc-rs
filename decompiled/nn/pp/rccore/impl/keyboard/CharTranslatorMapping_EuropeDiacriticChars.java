/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_Europe;

public class CharTranslatorMapping_EuropeDiacriticChars
extends CharTranslatorMapping_Europe
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('`', new int[]{131072, 129, 65665, 262144, 32, 65568});
        this.addMapping('\u00b4', new int[]{129, 65665, 32, 65568});
        this.addMapping('^', new int[]{130, 65666, 32, 65568});
        this.addMapping('~', new int[]{524288, 135, 65671, 0x100000, 32, 65568});
        this.addMapping('\u00a8', new int[]{135, 65671});
        this.addMapping('\u00e0', new int[]{131072, 129, 65665, 262144, 65, 65601});
        this.addMapping('\u00c0', new int[]{131072, 129, 65665, 65, 65601});
        this.addMapping('\u00e1', new int[]{129, 65665, 65, 65601});
        this.addMapping('\u00c1', new int[]{129, 65665, 131072, 65, 65601});
        this.addMapping('\u00e2', new int[]{130, 65666, 65, 65601});
        this.addMapping('\u00c2', new int[]{130, 65666, 131072, 65, 65601});
        this.addMapping('\u00e3', new int[]{524288, 135, 65671, 0x100000, 65, 65601});
        this.addMapping('\u00c3', new int[]{524288, 135, 65671, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00e4', new int[]{135, 65671, 65, 65601});
        this.addMapping('\u00c4', new int[]{135, 65671, 131072, 65, 65601});
        this.addMapping('\u00e8', new int[]{131072, 129, 65665, 262144, 69, 65605});
        this.addMapping('\u00c8', new int[]{131072, 129, 65665, 69, 65605});
        this.addMapping('\u00e9', new int[]{129, 65665, 69, 65605});
        this.addMapping('\u00c9', new int[]{129, 65665, 131072, 69, 65605});
        this.addMapping('\u00ea', new int[]{130, 65666, 69, 65605});
        this.addMapping('\u00ca', new int[]{130, 65666, 131072, 69, 65605});
        this.addMapping('\u00eb', new int[]{135, 65671, 69, 65605});
        this.addMapping('\u00cb', new int[]{135, 65671, 131072, 69, 65605});
        this.addMapping('\u00ec', new int[]{131072, 129, 65665, 262144, 73, 65609});
        this.addMapping('\u00cc', new int[]{131072, 129, 65665, 73, 65609});
        this.addMapping('\u00ed', new int[]{129, 65665, 73, 65609});
        this.addMapping('\u00cd', new int[]{129, 65665, 131072, 73, 65609});
        this.addMapping('\u00ee', new int[]{130, 65666, 73, 65609});
        this.addMapping('\u00ce', new int[]{130, 65666, 131072, 73, 65609});
        this.addMapping('\u00ef', new int[]{135, 65671, 73, 65609});
        this.addMapping('\u00cf', new int[]{135, 65671, 131072, 73, 65609});
        this.addMapping('\u00f1', new int[]{524288, 135, 65671, 0x100000, 78, 65614});
        this.addMapping('\u00d1', new int[]{524288, 135, 65671, 0x100000, 131072, 78, 65614});
        this.addMapping('\u00f2', new int[]{131072, 129, 65665, 262144, 79, 65615});
        this.addMapping('\u00d2', new int[]{131072, 129, 65665, 79, 65615});
        this.addMapping('\u00f3', new int[]{129, 65665, 79, 65615});
        this.addMapping('\u00d3', new int[]{129, 65665, 131072, 79, 65615});
        this.addMapping('\u00f4', new int[]{130, 65666, 79, 65615});
        this.addMapping('\u00d4', new int[]{130, 65666, 131072, 79, 65615});
        this.addMapping('\u00f5', new int[]{524288, 135, 65671, 0x100000, 79, 65615});
        this.addMapping('\u00d5', new int[]{524288, 135, 65671, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00f6', new int[]{135, 65671, 79, 65615});
        this.addMapping('\u00d6', new int[]{135, 65671, 131072, 79, 65615});
        this.addMapping('\u00f9', new int[]{131072, 129, 65665, 262144, 85, 65621});
        this.addMapping('\u00d9', new int[]{131072, 129, 65665, 85, 65621});
        this.addMapping('\u00fa', new int[]{129, 65665, 85, 65621});
        this.addMapping('\u00da', new int[]{129, 65665, 131072, 85, 65621});
        this.addMapping('\u00fb', new int[]{130, 65666, 85, 65621});
        this.addMapping('\u00db', new int[]{130, 65666, 131072, 85, 65621});
        this.addMapping('\u00fc', new int[]{135, 65671, 85, 65621});
        this.addMapping('\u00dc', new int[]{135, 65671, 131072, 85, 65621});
        this.addMapping('\u00fd', new int[]{129, 65665, 89, 65625});
        this.addMapping('\u00dd', new int[]{129, 65665, 131072, 89, 65625});
        this.addMapping('\u00ff', new int[]{135, 65671, 89, 65625});
    }
}

