/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.macro;

import com.util.kbd.KeyboardMappings;
import java.util.Locale;

public interface IMacroHelper {
    public void init();

    public int getVKCode(short[] var1, short var2);

    public int getVKCode(short var1, Locale var2);

    public char getKeyChar(short[] var1, short var2);

    public char getKeyChar(short var1, Locale var2);

    public String getKeyName(int var1);

    public KeyboardMappings getKeyMappings();
}

