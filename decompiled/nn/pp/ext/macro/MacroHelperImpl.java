/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.macro;

import com.util.kbd.KeyHIDValue;
import com.util.kbd.KeyboardKey;
import com.util.kbd.KeyboardMappings;
import com.util.kbd.KeyboardUtil;
import java.util.Locale;
import nn.pp.ext.macro.IMacroHelper;

public class MacroHelperImpl
extends KeyboardUtil
implements IMacroHelper {
    private static KeyboardMappings keyboardMappings = null;

    MacroHelperImpl() {
    }

    @Override
    public void init() {
        keyboardMappings = new KeyboardMappings();
    }

    @Override
    public int getVKCode(short[] sArray, short s) {
        short[] sArray2 = sArray == null ? KeyHIDValue.HIDMAP : sArray;
        KeyboardKey keyboardKey = keyboardMappings.getItem(s);
        if (keyboardKey == null) {
            return -1;
        }
        return KeyboardUtil.getVKCodeForMacroCode(sArray2, keyboardKey.getKeyCode());
    }

    @Override
    public int getVKCode(short s, Locale locale) {
        KeyboardKey keyboardKey = keyboardMappings.getItem(s);
        if (keyboardKey.getKeyCode() == 230) {
            return 65406;
        }
        if (keyboardKey.getKeyCode() == 104) {
            return 61440;
        }
        if (keyboardKey.getKeyCode() == 105) {
            return 61441;
        }
        if (keyboardKey.getKeyCode() == 106) {
            return 61442;
        }
        if (keyboardKey.getKeyCode() == 107) {
            return 61443;
        }
        return KeyboardUtil.getVKCodeForMacroCode(keyboardKey.getKeyCode(), locale);
    }

    @Override
    public char getKeyChar(short[] sArray, short s) {
        KeyboardKey keyboardKey = keyboardMappings.getItem(s);
        char c = '\n';
        if (keyboardKey != null && keyboardKey.getKeyName() != null && keyboardKey.getKeyName().length() == 1) {
            c = keyboardKey.getKeyName().charAt(0);
        }
        return c;
    }

    @Override
    public char getKeyChar(short s, Locale locale) {
        return this.getKeyChar(KeyHIDValue.HIDMAP, s);
    }

    @Override
    public String getKeyName(int n) {
        KeyboardKey keyboardKey = keyboardMappings.getItem(n);
        if (keyboardKey != null) {
            return keyboardKey.getKeyName();
        }
        return null;
    }

    @Override
    public KeyboardMappings getKeyMappings() {
        return keyboardMappings;
    }
}

