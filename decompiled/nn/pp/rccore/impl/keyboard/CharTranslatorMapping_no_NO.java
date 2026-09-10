/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_da_DK;

public class CharTranslatorMapping_no_NO
extends CharTranslatorMapping_da_DK
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('|', new int[]{544, 66080});
        this.addMapping('\\', new int[]{92, 65628});
        this.addMapping('\u00b4', new int[]{524288, 92, 65628, 0x100000, 32, 65568});
        this.addMapping('\u00f8', new int[]{548, 66084});
        this.addMapping('\u00e6', new int[]{549, 66085});
        this.addMapping('\u00d8', new int[]{131072, 548, 66084});
        this.addMapping('\u00c6', new int[]{131072, 549, 66085});
        this.addMapping('\u00e1', new int[]{524288, 129, 65665, 0x100000, 65, 65601});
        this.addMapping('\u00c1', new int[]{524288, 129, 65665, 0x100000, 131072, 65, 65601});
        this.addMapping('\u00e9', new int[]{524288, 129, 65665, 0x100000, 69, 65605});
        this.addMapping('\u00c9', new int[]{524288, 129, 65665, 0x100000, 131072, 69, 65605});
        this.addMapping('\u00ed', new int[]{524288, 129, 65665, 0x100000, 73, 65609});
        this.addMapping('\u00cd', new int[]{524288, 129, 65665, 0x100000, 131072, 73, 65609});
        this.addMapping('\u00f3', new int[]{524288, 129, 65665, 0x100000, 79, 65615});
        this.addMapping('\u00d3', new int[]{524288, 129, 65665, 0x100000, 131072, 79, 65615});
        this.addMapping('\u00fa', new int[]{524288, 129, 65665, 0x100000, 85, 65621});
        this.addMapping('\u00da', new int[]{524288, 129, 65665, 0x100000, 131072, 85, 65621});
        this.addMapping('\u00fd', new int[]{524288, 129, 65665, 0x100000, 89, 65625});
        this.addMapping('\u00dd', new int[]{524288, 129, 65665, 0x100000, 131072, 89, 65625});
    }
}

