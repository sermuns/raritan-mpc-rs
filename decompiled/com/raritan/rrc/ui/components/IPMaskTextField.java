/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.components.MaskTextField;
import java.awt.event.KeyEvent;

class IPMaskTextField
extends MaskTextField {
    public IPMaskTextField(int n, String string) {
        super(n, string);
    }

    @Override
    protected boolean isAlwaysValid(KeyEvent keyEvent) {
        boolean bl = false;
        if (keyEvent.getKeyCode() > 0) {
            switch (keyEvent.getKeyCode()) {
                case 46: 
                case 110: {
                    keyEvent.consume();
                }
                case 8: 
                case 9: 
                case 27: 
                case 127: {
                    bl = true;
                }
            }
        } else {
            switch (keyEvent.getKeyChar()) {
                case '\b': {
                    bl = true;
                }
            }
        }
        return bl;
    }
}

