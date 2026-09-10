/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.awt.event.KeyEvent;
import nn.pp.core.Platform;
import nn.pp.rccore.impl.keyboard.KeyTranslator_fr_FR;

public class KeyTranslator_fr_FR_Impl
extends KeyTranslator_fr_FR {
    @Override
    public int translateKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyChar() == '^' && keyEvent.isAltGraphDown() && Platform.isLinux()) {
            return 9;
        }
        return super.translateKeyEvent(keyEvent);
    }
}

