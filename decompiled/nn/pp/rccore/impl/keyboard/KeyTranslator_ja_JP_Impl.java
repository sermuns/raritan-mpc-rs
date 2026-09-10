/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.awt.event.KeyEvent;
import nn.pp.rccore.impl.keyboard.KeyTranslator_ja_JP;

public class KeyTranslator_ja_JP_Impl
extends KeyTranslator_ja_JP {
    @Override
    public int translateKeyEvent(KeyEvent keyEvent) {
        int n = super.translateKeyEvent(keyEvent);
        switch (keyEvent.getKeyCode()) {
            case 240: {
                if (keyEvent.getID() == 401) {
                    n = 28;
                    break;
                }
                if (keyEvent.getID() != 402) break;
                n = 110;
                break;
            }
            case 92: {
                if (keyEvent.getKeyChar() != '_') break;
                n = 114;
            }
        }
        return n;
    }
}

