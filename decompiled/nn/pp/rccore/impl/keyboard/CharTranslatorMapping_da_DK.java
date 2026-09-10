/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_Germanic;

public class CharTranslatorMapping_da_DK
extends CharTranslatorMapping_Germanic
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\u00bd', new int[]{544, 66080});
        this.addMapping('\u00a7', new int[]{131072, 544, 66080});
        this.addMapping('\u00a4', new int[]{131072, 52, 65588});
        this.addMapping('$', new int[]{524288, 52, 65588});
        this.addMapping('|', new int[]{524288, 129, 65665});
        this.addMapping('\u00e5', new int[]{547, 66083});
        this.addMapping('\u00c5', new int[]{131072, 547, 66083});
        this.addMapping('^', new int[]{131072, 135, 65671, 262144, 32, 65568});
        this.addMapping('\u00e6', new int[]{548, 66084});
        this.addMapping('\u00f8', new int[]{549, 66085});
        this.addMapping('\u00c6', new int[]{131072, 548, 66084});
        this.addMapping('\u00d8', new int[]{131072, 549, 66085});
        this.addMapping('*', new int[]{131072, 222, 65758});
        this.addMapping('\\', new int[]{524288, 153, 65689});
        this.addMapping('\u00e2', new int[]{131072, 135, 65671, 262144, 65, 65601});
        this.addMapping('\u00c2', new int[]{131072, 135, 65671, 65, 65601});
        this.addMapping('\u00ea', new int[]{131072, 135, 65671, 262144, 69, 65605});
        this.addMapping('\u00ca', new int[]{131072, 135, 65671, 69, 65605});
        this.addMapping('\u00ee', new int[]{131072, 135, 65671, 262144, 73, 65609});
        this.addMapping('\u00ce', new int[]{131072, 135, 65671, 73, 65609});
        this.addMapping('\u00f4', new int[]{131072, 135, 65671, 262144, 79, 65615});
        this.addMapping('\u00d4', new int[]{131072, 135, 65671, 79, 65615});
        this.addMapping('\u00fb', new int[]{131072, 135, 65671, 262144, 85, 65621});
        this.addMapping('\u00db', new int[]{131072, 135, 65671, 85, 65621});
    }
}

