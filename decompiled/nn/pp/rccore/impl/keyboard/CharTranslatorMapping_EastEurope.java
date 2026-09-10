/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_Europe;

public class CharTranslatorMapping_EastEurope
extends CharTranslatorMapping_Europe
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('~', new int[]{524288, 49, 65585});
        this.addMapping('\u02c7', new int[]{524288, 50, 65586, 0x100000, 32, 65568});
        this.addMapping('^', new int[]{524288, 51, 65587, 0x100000, 32, 65568});
        this.addMapping('\u02d8', new int[]{524288, 52, 65588, 0x100000, 32, 65568});
        this.addMapping('\u00b0', new int[]{524288, 53, 65589, 0x100000, 32, 65568});
        this.addMapping('\u02db', new int[]{524288, 54, 65590, 0x100000, 32, 65568});
        this.addMapping('`', new int[]{524288, 55, 65591});
        this.addMapping('\u02d9', new int[]{524288, 56, 65592, 0x100000, 32, 65568});
        this.addMapping('\u00b4', new int[]{524288, 57, 65593, 0x100000, 32, 65568});
        this.addMapping('\u02dd', new int[]{524288, 48, 65584, 0x100000, 32, 65568});
        this.addMapping('\u00a8', new int[]{524288, 222, 65758, 0x100000, 32, 65568});
        this.addMapping('\u00b8', new int[]{524288, 521, 66057, 0x100000, 32, 65568});
        this.addMapping('\u0111', new int[]{93, 65629});
        this.addMapping('\u0110', new int[]{131072, 93, 65629});
        this.addMapping('\\', new int[]{524288, 81, 65617});
        this.addMapping('|', new int[]{524288, 87, 65623});
        this.addMapping('\u00f7', new int[]{524288, 91, 65627});
        this.addMapping('\u00d7', new int[]{524288, 93, 65629});
        this.addMapping('[', new int[]{524288, 70, 65606});
        this.addMapping(']', new int[]{524288, 71, 65607});
        this.addMapping('\u0142', new int[]{524288, 75, 65611});
        this.addMapping('\u0141', new int[]{321});
        this.addMapping('\u00df', new int[]{524288, 549, 66085});
        this.addMapping('\u00a4', new int[]{524288, 92, 65628});
        this.addMapping('@', new int[]{524288, 86, 65622});
        this.addMapping('{', new int[]{524288, 66, 65602});
        this.addMapping('}', new int[]{524288, 78, 65614});
        this.addMapping('\u00a7', new int[]{524288, 77, 65613});
        this.addMapping('\u010d', new int[]{524288, 50, 65586, 0x100000, 67, 65603});
        this.addMapping('\u010c', new int[]{524288, 50, 65586, 0x100000, 131072, 67, 65603});
        this.addMapping('\u010f', new int[]{524288, 50, 65586, 0x100000, 68, 65604});
        this.addMapping('\u010e', new int[]{524288, 50, 65586, 0x100000, 131072, 68, 65604});
        this.addMapping('\u011b', new int[]{524288, 50, 65586, 0x100000, 69, 65605});
        this.addMapping('\u011a', new int[]{524288, 50, 65586, 0x100000, 131072, 69, 65605});
        this.addMapping('\u013e', new int[]{524288, 50, 65586, 0x100000, 76, 65612});
        this.addMapping('\u013d', new int[]{524288, 50, 65586, 0x100000, 131072, 76, 65612});
        this.addMapping('\u0148', new int[]{524288, 50, 65586, 0x100000, 78, 65614});
        this.addMapping('\u0147', new int[]{524288, 50, 65586, 0x100000, 131072, 78, 65614});
        this.addMapping('\u0159', new int[]{524288, 50, 65586, 0x100000, 82, 65618});
        this.addMapping('\u0158', new int[]{524288, 50, 65586, 0x100000, 131072, 82, 65618});
        this.addMapping('\u0161', new int[]{524288, 50, 65586, 0x100000, 83, 65619});
        this.addMapping('\u0160', new int[]{524288, 50, 65586, 0x100000, 131072, 83, 65619});
        this.addMapping('\u0165', new int[]{524288, 50, 65586, 0x100000, 84, 65620});
        this.addMapping('\u0164', new int[]{524288, 50, 65586, 0x100000, 131072, 84, 65620});
        this.addMapping('\u017e', new int[]{524288, 50, 65586, 0x100000, 90, 65626});
        this.addMapping('\u017d', new int[]{524288, 50, 65586, 0x100000, 131072, 90, 65626});
        this.addMapping('\u00e2', new int[]{524288, 51, 65587, 0x100000, 65, 65601});
        this.addMapping('\u00c2', new int[]{524288, 51, 65587, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00ee', new int[]{524288, 51, 65587, 0x100000, 73, 65609});
        this.addMapping('\u00ce', new int[]{524288, 51, 65587, 0x100000, 131072, 73, 65609});
        this.addMapping('\u00f4', new int[]{524288, 51, 65587, 0x100000, 79, 65615});
        this.addMapping('\u00d4', new int[]{524288, 51, 65587, 0x100000, 131072, 79, 65615});
        this.addMapping('\u0103', new int[]{524288, 52, 65588, 0x100000, 65, 65601});
        this.addMapping('\u0102', new int[]{524288, 52, 65588, 0x100000, 131072, 65, 65601});
        this.addMapping('\u016f', new int[]{524288, 53, 65589, 0x100000, 85, 65621});
        this.addMapping('\u016e', new int[]{524288, 53, 65589, 0x100000, 131072, 85, 65621});
        this.addMapping('\u0105', new int[]{524288, 54, 65590, 0x100000, 65, 65601});
        this.addMapping('\u0104', new int[]{524288, 54, 65590, 0x100000, 131072, 65, 65601});
        this.addMapping('\u0119', new int[]{524288, 54, 65590, 0x100000, 69, 65605});
        this.addMapping('\u0118', new int[]{524288, 54, 65590, 0x100000, 131072, 69, 65605});
        this.addMapping('\u017c', new int[]{524288, 56, 65592, 0x100000, 90, 65626});
        this.addMapping('\u017b', new int[]{524288, 56, 65592, 0x100000, 131072, 90, 65626});
        this.addMapping('\u00e1', new int[]{524288, 57, 65593, 0x100000, 65, 65601});
        this.addMapping('\u00c1', new int[]{524288, 57, 65593, 0x100000, 131072, 65, 65601});
        this.addMapping('\u0107', new int[]{524288, 57, 65593, 0x100000, 67, 65603});
        this.addMapping('\u0106', new int[]{524288, 57, 65593, 0x100000, 131072, 67, 65603});
        this.addMapping('\u00e9', new int[]{524288, 57, 65593, 0x100000, 69, 65605});
        this.addMapping('\u00c9', new int[]{524288, 57, 65593, 0x100000, 131072, 69, 65605});
        this.addMapping('\u00ed', new int[]{524288, 57, 65593, 0x100000, 73, 65609});
        this.addMapping('\u00cd', new int[]{524288, 57, 65593, 0x100000, 131072, 73, 65609});
        this.addMapping('\u013a', new int[]{524288, 57, 65593, 0x100000, 76, 65612});
        this.addMapping('\u0139', new int[]{524288, 57, 65593, 0x100000, 131072, 76, 65612});
        this.addMapping('\u0144', new int[]{524288, 57, 65593, 0x100000, 78, 65614});
        this.addMapping('\u0143', new int[]{524288, 57, 65593, 0x100000, 131072, 78, 65614});
        this.addMapping('\u00f3', new int[]{524288, 57, 65593, 0x100000, 79, 65615});
        this.addMapping('\u00d3', new int[]{524288, 57, 65593, 0x100000, 131072, 79, 65615});
        this.addMapping('\u0155', new int[]{524288, 57, 65593, 0x100000, 82, 65618});
        this.addMapping('\u0154', new int[]{524288, 57, 65593, 0x100000, 131072, 82, 65618});
        this.addMapping('\u015b', new int[]{524288, 57, 65593, 0x100000, 83, 65619});
        this.addMapping('\u015a', new int[]{524288, 57, 65593, 0x100000, 131072, 83, 65619});
        this.addMapping('\u00fa', new int[]{524288, 57, 65593, 0x100000, 85, 65621});
        this.addMapping('\u00da', new int[]{524288, 57, 65593, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00fd', new int[]{524288, 57, 65593, 0x100000, 89, 65625});
        this.addMapping('\u00dd', new int[]{524288, 57, 65593, 0x100000, 131072, 89, 65625});
        this.addMapping('\u017a', new int[]{524288, 57, 65593, 0x100000, 90, 65626});
        this.addMapping('\u0179', new int[]{524288, 57, 65593, 0x100000, 131072, 90, 65626});
        this.addMapping('\u0151', new int[]{524288, 48, 65584, 0x100000, 79, 65615});
        this.addMapping('\u0150', new int[]{524288, 48, 65584, 0x100000, 131072, 79, 65615});
        this.addMapping('\u0171', new int[]{524288, 48, 65584, 0x100000, 85, 65621});
        this.addMapping('\u0170', new int[]{524288, 48, 65584, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00e4', new int[]{524288, 222, 65758, 0x100000, 65, 65601});
        this.addMapping('\u00c4', new int[]{524288, 222, 65758, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00eb', new int[]{524288, 222, 65758, 0x100000, 69, 65605});
        this.addMapping('\u00cb', new int[]{524288, 222, 65758, 0x100000, 131072, 69, 65605});
        this.addMapping('\u00f6', new int[]{524288, 222, 65758, 0x100000, 79, 65615});
        this.addMapping('\u00d6', new int[]{524288, 222, 65758, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00fc', new int[]{524288, 222, 65758, 0x100000, 85, 65621});
        this.addMapping('\u00dc', new int[]{524288, 222, 65758, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00e7', new int[]{524288, 521, 66057, 0x100000, 67, 65603});
        this.addMapping('\u00c7', new int[]{524288, 521, 66057, 0x100000, 131072, 67, 65603});
        this.addMapping('\u015f', new int[]{524288, 521, 66057, 0x100000, 83, 65619});
        this.addMapping('\u015e', new int[]{524288, 521, 66057, 0x100000, 131072, 83, 65619});
        this.addMapping('\u0163', new int[]{524288, 521, 66057, 0x100000, 84, 65620});
        this.addMapping('\u0162', new int[]{524288, 521, 66057, 0x100000, 131072, 84, 65620});
    }
}

