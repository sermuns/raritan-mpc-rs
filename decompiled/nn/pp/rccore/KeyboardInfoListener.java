/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.List;
import java.util.Locale;
import nn.pp.rccore.IKeyboardMacro;
import nn.pp.rccore.RCCore;

public interface KeyboardInfoListener {
    public static final int KEYBOARD_MACROS = 1;
    public static final int SOFT_KEYBOARD = 2;
    public static final int LOCAL_KEYBOARD = 4;
    public static final int KEYBOARD_LEDS = 8;
    public static final int SUN_KEYBOARD = 16;
    public static final int ALL = 23;

    public void keyboardMacroListChanged(List<IKeyboardMacro> var1);

    public void softKeyboardMappingChanged(Locale var1);

    public void localKeyboardMappingChanged(Locale var1);

    public void keyboardLedStateChanged(List<RCCore.KeyboardLed> var1);

    public void sunKeyboardSupported(boolean var1);
}

