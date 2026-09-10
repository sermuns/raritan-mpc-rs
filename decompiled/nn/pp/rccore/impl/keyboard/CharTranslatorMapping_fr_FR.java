/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_EuropeDiacriticChars;

public class CharTranslatorMapping_fr_FR
extends CharTranslatorMapping_EuropeDiacriticChars
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00b2', new int[]{192, 65728});
        this.addMapping('&', new int[]{49, 65585});
        this.addMapping('\u00e9', new int[]{50, 65586});
        this.addMapping('\"', new int[]{51, 65587});
        this.addMapping('\'', new int[]{52, 65588});
        this.addMapping('(', new int[]{53, 65589});
        this.addMapping('-', new int[]{54, 65590});
        this.addMapping('\u00e8', new int[]{55, 65591});
        this.addMapping('_', new int[]{56, 65592});
        this.addMapping('\u00e7', new int[]{57, 65593});
        this.addMapping('\u00e0', new int[]{48, 65584});
        this.addMapping(')', new int[]{522, 66058});
        this.addMapping('=', new int[]{262144, 0x100000, 61, 65597});
        this.addMapping('1', new int[]{131072, 49, 65585});
        this.addMapping('2', new int[]{131072, 50, 65586});
        this.addMapping('3', new int[]{131072, 51, 65587});
        this.addMapping('4', new int[]{131072, 52, 65588});
        this.addMapping('5', new int[]{131072, 53, 65589});
        this.addMapping('6', new int[]{131072, 54, 65590});
        this.addMapping('7', new int[]{131072, 55, 65591});
        this.addMapping('8', new int[]{131072, 56, 65592});
        this.addMapping('9', new int[]{131072, 57, 65593});
        this.addMapping('0', new int[]{131072, 48, 65584});
        this.addMapping('\u00b0', new int[]{131072, 522, 66058});
        this.addMapping('+', new int[]{131072, 0x100000, 61, 65597});
        this.addMapping('~', new int[]{524288, 50, 65586, 0x100000, 32, 65568});
        this.addMapping('#', new int[]{524288, 51, 65587});
        this.addMapping('{', new int[]{524288, 52, 65588});
        this.addMapping('[', new int[]{524288, 53, 65589});
        this.addMapping('|', new int[]{524288, 54, 65590});
        this.addMapping('`', new int[]{524288, 55, 65591, 0x100000, 32, 65568});
        this.addMapping('\\', new int[]{524288, 56, 65592});
        this.addMapping('^', new int[]{524288, 57, 65593});
        this.addMapping('@', new int[]{524288, 48, 65584});
        this.addMapping(']', new int[]{524288, 522, 66058});
        this.addMapping('}', new int[]{524288, 61, 65597});
        this.addMapping('$', new int[]{515, 66051});
        this.addMapping('\u00a8', new int[]{131072, 130, 65666});
        this.addMapping('\u00a3', new int[]{131072, 515, 66051});
        this.addMapping('\u00a4', new int[]{524288, 515, 66051});
        this.addMapping('\u00f9', new int[]{549, 66085});
        this.addMapping('*', new int[]{151, 65687});
        this.addMapping('%', new int[]{131072, 549, 66085});
        this.addMapping('\u00b5', new int[]{131072, 151, 65687});
        this.addMapping(';', new int[]{59, 65595});
        this.addMapping(':', new int[]{513, 66049});
        this.addMapping('!', new int[]{517, 66053});
        this.addMapping('?', new int[]{131072, 44, 65580});
        this.addMapping('.', new int[]{131072, 59, 65595});
        this.addMapping('/', new int[]{131072, 513, 66049});
        this.addMapping('\u00a7', new int[]{131072, 517, 66053});
        this.addMapping('\u00c0', new int[]{524288, 55, 65591, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00e2', new int[]{130, 65666, 65, 65601});
        this.addMapping('\u00c2', new int[]{130, 65666, 131072, 65, 65601});
        this.addMapping('\u00e4', new int[]{131072, 130, 65666, 262144, 65, 65601});
        this.addMapping('\u00c4', new int[]{131072, 130, 65666, 65, 65601});
        this.addMapping('\u00e3', new int[]{524288, 50, 65586, 0x100000, 65, 65601});
        this.addMapping('\u00c3', new int[]{524288, 50, 65586, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00c8', new int[]{524288, 55, 65591, 0x100000, 131072, 69, 65605});
        this.addMapping('\u00ea', new int[]{130, 65666, 69, 65605});
        this.addMapping('\u00ca', new int[]{130, 65666, 131072, 69, 65605});
        this.addMapping('\u00eb', new int[]{131072, 130, 65666, 262144, 69, 65605});
        this.addMapping('\u00cb', new int[]{131072, 130, 65666, 69, 65605});
        this.addMapping('\u00ec', new int[]{524288, 55, 65591, 0x100000, 73, 65609});
        this.addMapping('\u00cc', new int[]{524288, 55, 65591, 0x100000, 131072, 73, 65609});
        this.addMapping('\u00ee', new int[]{130, 65666, 73, 65609});
        this.addMapping('\u00ce', new int[]{130, 65666, 131072, 73, 65609});
        this.addMapping('\u00ef', new int[]{131072, 130, 65666, 262144, 73, 65609});
        this.addMapping('\u00cf', new int[]{131072, 130, 65666, 73, 65609});
        this.addMapping('\u00f1', new int[]{524288, 50, 65586, 0x100000, 78, 65614});
        this.addMapping('\u00d1', new int[]{524288, 50, 65586, 0x100000, 131072, 78, 65614});
        this.addMapping('\u00f2', new int[]{524288, 55, 65591, 0x100000, 79, 65615});
        this.addMapping('\u00d2', new int[]{524288, 55, 65591, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00f4', new int[]{130, 65666, 79, 65615});
        this.addMapping('\u00d4', new int[]{130, 65666, 131072, 79, 65615});
        this.addMapping('\u00f6', new int[]{131072, 130, 65666, 262144, 79, 65615});
        this.addMapping('\u00d6', new int[]{131072, 130, 65666, 79, 65615});
        this.addMapping('\u00f5', new int[]{524288, 50, 65586, 0x100000, 79, 65615});
        this.addMapping('\u00d5', new int[]{524288, 50, 65586, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00d9', new int[]{524288, 55, 65591, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00fb', new int[]{130, 65666, 85, 65621});
        this.addMapping('\u00db', new int[]{130, 65666, 131072, 85, 65621});
        this.addMapping('\u00fc', new int[]{131072, 130, 65666, 262144, 85, 65621});
        this.addMapping('\u00dc', new int[]{131072, 130, 65666, 85, 65621});
        this.addMapping('\u00ff', new int[]{131072, 130, 65666, 262144, 89, 65625});
    }
}

