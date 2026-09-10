/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_fr_FR;

public class CharTranslatorMapping_fr_BE
extends CharTranslatorMapping_fr_FR
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00a7', new int[]{54, 65590});
        this.addMapping('!', new int[]{56, 65592});
        this.addMapping('-', new int[]{45, 65581});
        this.addMapping('\u00b3', new int[]{131072, 192, 65728});
        this.addMapping('_', new int[]{131072, 45, 65581});
        this.addMapping('|', new int[]{524288, 49, 65585});
        this.addMapping('@', new int[]{524288, 50, 65586});
        this.addMapping('^', new int[]{524288, 54, 65590});
        this.addMapping('{', new int[]{524288, 57, 65593});
        this.addMapping('}', new int[]{524288, 48, 65584});
        this.addMapping('*', new int[]{131072, 515, 66051});
        this.addMapping('[', new int[]{524288, 130, 65666});
        this.addMapping(']', new int[]{524288, 515, 66051});
        this.addMapping('\u00b5', new int[]{92, 65628});
        this.addMapping('\u00a3', new int[]{131072, 92, 65628});
        this.addMapping('\u00b4', new int[]{524288, 549, 66085, 0x100000, 32, 65568});
        this.addMapping('`', new int[]{524288, 92, 65628, 0x100000, 32, 65568});
        this.addMapping('\\', new int[]{524288, 153, 65689});
        this.addMapping('~', new int[]{524288, 61, 65597, 0x100000, 32, 65568});
        this.addMapping('\u00c0', new int[]{524288, 92, 65628, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00c8', new int[]{524288, 92, 65628, 0x100000, 131072, 69, 65605});
        this.addMapping('\u00ec', new int[]{524288, 92, 65628, 0x100000, 73, 65609});
        this.addMapping('\u00cc', new int[]{524288, 92, 65628, 0x100000, 131072, 73, 65609});
        this.addMapping('\u00f2', new int[]{524288, 92, 65628, 0x100000, 79, 65615});
        this.addMapping('\u00d2', new int[]{524288, 92, 65628, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00d9', new int[]{524288, 92, 65628, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00e1', new int[]{524288, 549, 66085, 0x100000, 65, 65601});
        this.addMapping('\u00c1', new int[]{524288, 549, 66085, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00e9', new int[]{524288, 549, 66085, 0x100000, 69, 65605});
        this.addMapping('\u00c9', new int[]{524288, 549, 66085, 0x100000, 131072, 69, 65605});
        this.addMapping('\u00ed', new int[]{524288, 549, 66085, 0x100000, 73, 65609});
        this.addMapping('\u00cd', new int[]{524288, 549, 66085, 0x100000, 131072, 73, 65609});
        this.addMapping('\u00f3', new int[]{524288, 549, 66085, 0x100000, 79, 65615});
        this.addMapping('\u00d3', new int[]{524288, 549, 66085, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00fa', new int[]{524288, 549, 66085, 0x100000, 85, 65621});
        this.addMapping('\u00da', new int[]{524288, 549, 66085, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00fd', new int[]{524288, 549, 66085, 0x100000, 89, 65625});
        this.addMapping('\u00dd', new int[]{524288, 549, 66085, 0x100000, 131072, 89, 65625});
        this.addMapping('\u00e3', new int[]{524288, 61, 65597, 0x100000, 65, 65601});
        this.addMapping('\u00c3', new int[]{524288, 61, 65597, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00f1', new int[]{524288, 61, 65597, 0x100000, 78, 65614});
        this.addMapping('\u00d1', new int[]{524288, 61, 65597, 0x100000, 131072, 78, 65614});
        this.addMapping('\u00f5', new int[]{524288, 61, 65597, 0x100000, 79, 65615});
        this.addMapping('\u00d5', new int[]{524288, 61, 65597, 0x100000, 131072, 79, 65615});
    }
}

