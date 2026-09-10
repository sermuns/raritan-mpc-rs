/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.ui.helpers;

import nn.pp.ext.macro.IMacroHelper;
import nn.pp.rccore.KeyboardMacro;

public interface IKVMTargetViewer {
    public int getEricKeyCode(int var1, char var2, int var3);

    public void sendKeyboardMacroDirect(KeyboardMacro var1);

    public IMacroHelper getMacroHelper();

    public int getKeyboardType();
}

