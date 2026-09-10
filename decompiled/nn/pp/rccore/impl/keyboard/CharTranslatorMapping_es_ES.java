/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_EuropeDiacriticChars;

public class CharTranslatorMapping_es_ES
extends CharTranslatorMapping_EuropeDiacriticChars
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00ba', new int[]{192, 65728});
        this.addMapping('\u00a1', new int[]{61, 65597});
        this.addMapping('\u00aa', new int[]{131072, 192, 65728});
        this.addMapping('\u00b7', new int[]{131072, 51, 65587});
        this.addMapping('\u00bf', new int[]{131072, 61, 65597});
        this.addMapping('\\', new int[]{524288, 192, 65728});
        this.addMapping('|', new int[]{524288, 49, 65585});
        this.addMapping('#', new int[]{524288, 51, 65587});
        this.addMapping('~', new int[]{524288, 52, 65588, 0x100000, 32, 65568});
        this.addMapping('\u00ac', new int[]{524288, 54, 65590});
        this.addMapping('`', new int[]{128, 65664, 32, 65568});
        this.addMapping('+', new int[]{521, 66057});
        this.addMapping('^', new int[]{131072, 128, 65664, 262144, 32, 65568});
        this.addMapping('*', new int[]{131072, 521, 66057});
        this.addMapping('\u20ac', new int[]{524288, 69, 65605});
        this.addMapping('[', new int[]{524288, 128, 65664});
        this.addMapping(']', new int[]{524288, 521, 66057});
        this.addMapping('\u00b4', new int[]{129, 65665, 32, 65568});
        this.addMapping('\u00e7', new int[]{92, 65628});
        this.addMapping('\u00a8', new int[]{131072, 129, 65665, 262144, 32, 65568});
        this.addMapping('\u00c7', new int[]{131072, 92, 65628});
        this.addMapping('{', new int[]{524288, 129, 65665});
        this.addMapping('}', new int[]{524288, 92, 65628});
        this.addMapping('\u00e0', new int[]{128, 65664, 65, 65601});
        this.addMapping('\u00c0', new int[]{128, 65664, 131072, 65, 65601});
        this.addMapping('\u00e8', new int[]{128, 65664, 69, 65605});
        this.addMapping('\u00c8', new int[]{128, 65664, 131072, 69, 65605});
        this.addMapping('\u00ec', new int[]{128, 65664, 73, 65609});
        this.addMapping('\u00cc', new int[]{128, 65664, 131072, 73, 65609});
        this.addMapping('\u00f2', new int[]{128, 65664, 79, 65615});
        this.addMapping('\u00d2', new int[]{128, 65664, 131072, 79, 65615});
        this.addMapping('\u00f9', new int[]{128, 65664, 85, 65621});
        this.addMapping('\u00d9', new int[]{128, 65664, 131072, 85, 65621});
        this.addMapping('\u00e2', new int[]{131072, 128, 65664, 262144, 65, 65601});
        this.addMapping('\u00c2', new int[]{131072, 128, 65664, 65, 65601});
        this.addMapping('\u00ea', new int[]{131072, 128, 65664, 262144, 69, 65605});
        this.addMapping('\u00ca', new int[]{131072, 128, 65664, 69, 65605});
        this.addMapping('\u00ee', new int[]{131072, 128, 65664, 262144, 73, 65609});
        this.addMapping('\u00ce', new int[]{131072, 128, 65664, 73, 65609});
        this.addMapping('\u00f4', new int[]{131072, 128, 65664, 262144, 79, 65615});
        this.addMapping('\u00d4', new int[]{131072, 128, 65664, 79, 65615});
        this.addMapping('\u00fb', new int[]{131072, 128, 65664, 262144, 85, 65621});
        this.addMapping('\u00db', new int[]{131072, 128, 65664, 85, 65621});
        this.addMapping('\u00e4', new int[]{131072, 129, 65665, 262144, 65, 65601});
        this.addMapping('\u00c4', new int[]{131072, 129, 65665, 65, 65601});
        this.addMapping('\u00eb', new int[]{131072, 129, 65665, 262144, 69, 65605});
        this.addMapping('\u00cb', new int[]{131072, 129, 65665, 69, 65605});
        this.addMapping('\u00ef', new int[]{131072, 129, 65665, 262144, 73, 65609});
        this.addMapping('\u00cf', new int[]{131072, 129, 65665, 73, 65609});
        this.addMapping('\u00f6', new int[]{131072, 129, 65665, 262144, 79, 65615});
        this.addMapping('\u00d6', new int[]{131072, 129, 65665, 79, 65615});
        this.addMapping('\u00fc', new int[]{131072, 129, 65665, 262144, 85, 65621});
        this.addMapping('\u00dc', new int[]{131072, 129, 65665, 85, 65621});
        this.addMapping('\u00ff', new int[]{131072, 129, 65665, 262144, 89, 65625});
        this.addMapping('\u00e3', new int[]{524288, 52, 65588, 0x100000, 65, 65601});
        this.addMapping('\u00c3', new int[]{524288, 52, 65588, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00f1', new int[]{524288, 52, 65588, 0x100000, 78, 65614});
        this.addMapping('\u00d1', new int[]{524288, 52, 65588, 0x100000, 131072, 78, 65614});
        this.addMapping('\u00f5', new int[]{524288, 52, 65588, 0x100000, 79, 65615});
        this.addMapping('\u00d5', new int[]{524288, 52, 65588, 0x100000, 131072, 79, 65615});
    }
}

