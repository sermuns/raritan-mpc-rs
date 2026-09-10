/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_de_DE;

public class CharTranslatorMapping_de_CH
extends CharTranslatorMapping_de_DE
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00a7', new int[]{544, 66080});
        this.addMapping('\'', new int[]{222, 65758});
        this.addMapping('\u00b0', new int[]{131072, 544, 66080});
        this.addMapping('+', new int[]{131072, 49, 65585});
        this.addMapping('*', new int[]{131072, 51, 65587});
        this.addMapping('\u00e7', new int[]{131072, 52, 65588});
        this.addMapping('?', new int[]{131072, 222, 65758});
        this.addMapping('`', new int[]{131072, 130, 65666, 262144, 32, 65568});
        this.addMapping('\u00a6', new int[]{524288, 49, 65585});
        this.addMapping('@', new int[]{524288, 50, 65586});
        this.addMapping('#', new int[]{524288, 51, 65587});
        this.addMapping('\u00ac', new int[]{524288, 54, 65590});
        this.addMapping('|', new int[]{524288, 55, 65591});
        this.addMapping('\u00a2', new int[]{524288, 56, 65592});
        this.addMapping('\u00b4', new int[]{524288, 222, 65758, 0x100000, 32, 65568});
        this.addMapping('~', new int[]{524288, 130, 65666, 0x100000, 32, 65568});
        this.addMapping('\u00a8', new int[]{135, 65671});
        this.addMapping('!', new int[]{131072, 135, 65671});
        this.addMapping('[', new int[]{524288, 91, 65627, 0x100000, 32, 65568});
        this.addMapping(']', new int[]{524288, 135, 65671, 0x100000, 32, 65568});
        this.addMapping('$', new int[]{515, 66051});
        this.addMapping('\u00a3', new int[]{131072, 515, 66051});
        this.addMapping('{', new int[]{524288, 549, 66085});
        this.addMapping('}', new int[]{524288, 515, 66051});
        this.addMapping('\\', new int[]{524288, 153, 65689});
        this.addMapping('\u00e0', new int[]{131072, 130, 65666, 262144, 65, 65601});
        this.addMapping('\u00c0', new int[]{131072, 130, 65666, 65, 65601});
        this.addMapping('\u00e1', new int[]{524288, 222, 65758, 0x100000, 65, 65601});
        this.addMapping('\u00c1', new int[]{524288, 222, 65758, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00e3', new int[]{524288, 130, 65666, 0x100000, 65, 65601});
        this.addMapping('\u00c3', new int[]{524288, 130, 65666, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00e4', new int[]{135, 65671, 65, 65601});
        this.addMapping('\u00c4', new int[]{135, 65671, 131072, 65, 65601});
        this.addMapping('\u00e8', new int[]{131072, 130, 65666, 262144, 69, 65605});
        this.addMapping('\u00c8', new int[]{131072, 130, 65666, 69, 65605});
        this.addMapping('\u00e9', new int[]{524288, 222, 65758, 0x100000, 69, 65605});
        this.addMapping('\u00c9', new int[]{524288, 222, 65758, 0x100000, 131072, 69, 65605});
        this.addMapping('\u00ec', new int[]{131072, 130, 65666, 262144, 73, 65609});
        this.addMapping('\u00cc', new int[]{131072, 130, 65666, 73, 65609});
        this.addMapping('\u00ed', new int[]{524288, 222, 65758, 0x100000, 73, 65609});
        this.addMapping('\u00cd', new int[]{524288, 222, 65758, 0x100000, 73, 65609});
        this.addMapping('\u00f1', new int[]{524288, 130, 65666, 0x100000, 78, 65614});
        this.addMapping('\u00d1', new int[]{524288, 130, 65666, 0x100000, 131072, 78, 65614});
        this.addMapping('\u00f2', new int[]{131072, 130, 65666, 262144, 79, 65615});
        this.addMapping('\u00d2', new int[]{131072, 130, 65666, 79, 65615});
        this.addMapping('\u00f3', new int[]{524288, 222, 65758, 0x100000, 79, 65615});
        this.addMapping('\u00d3', new int[]{524288, 222, 65758, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00f5', new int[]{524288, 130, 65666, 0x100000, 79, 65615});
        this.addMapping('\u00d5', new int[]{524288, 130, 65666, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00f6', new int[]{135, 65671, 79, 65615});
        this.addMapping('\u00d6', new int[]{135, 65671, 131072, 79, 65615});
        this.addMapping('\u00f9', new int[]{131072, 130, 65666, 262144, 85, 65621});
        this.addMapping('\u00d9', new int[]{131072, 130, 65666, 85, 65621});
        this.addMapping('\u00fa', new int[]{524288, 222, 65758, 0x100000, 85, 65621});
        this.addMapping('\u00da', new int[]{524288, 222, 65758, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00fc', new int[]{135, 65671, 85, 65621});
        this.addMapping('\u00dc', new int[]{135, 65671, 131072, 85, 65621});
        this.addMapping('\u00fd', new int[]{524288, 222, 65758, 0x100000, 89, 65625});
        this.addMapping('\u00dd', new int[]{524288, 222, 65758, 0x100000, 131072, 89, 65625});
        this.addMapping('\u00ff', new int[]{135, 65671, 89, 65625});
    }
}

