/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.macro;

import nn.pp.ext.macro.IMacroHelper;
import nn.pp.ext.macro.IMacroParser;
import nn.pp.ext.macro.KeyboardMacroParserImpl;
import nn.pp.ext.macro.MacroHelperImpl;

public final class MacroFactory {
    private static final MacroFactory macroFactory = new MacroFactory();

    private MacroFactory() {
    }

    public static MacroFactory getInstance() {
        return macroFactory;
    }

    public IMacroParser getMacroParser() {
        return new KeyboardMacroParserImpl();
    }

    public IMacroHelper getMacroHelper() {
        MacroHelperImpl macroHelperImpl = new MacroHelperImpl();
        macroHelperImpl.init();
        return macroHelperImpl;
    }
}

