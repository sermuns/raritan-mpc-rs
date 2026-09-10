/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Locale;
import nn.pp.core.Platform;
import nn.pp.rccore.impl.keyboard.KeyTranslator_da_DK;

public class KeyTranslator_no_NO
extends KeyTranslator_da_DK {
    @Override
    public Locale getLocale() {
        return new Locale("no", "NO");
    }

    @Override
    public void addKeys() {
        super.addKeys();
        this.addKeyMappingByCode(92, 1, 12);
        this.addKeyMappingByCharacter(124, 0);
        this.addKeyMappingByCharacter(248, 38);
        this.addKeyMappingByCharacter(216, 38);
        this.addKeyMappingByCharacter(230, 39);
        this.addKeyMappingByCharacter(198, 39);
        if (Platform.isLinux()) {
            this.addKeyMappingByCharacter(166, 0);
        }
    }
}

