/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext;

import nn.pp.ext.macro.IMacroHelper;
import nn.pp.ext.macro.IMacroParser;
import nn.pp.ext.macro.MacroException;
import nn.pp.ext.macro.MacroFactory;
import nn.pp.ext.pref.ApplicationPreferences;
import nn.pp.ext.pref.IApplicationPreferences;

public class ExtensionFactory {
    private static ExtensionFactory factory = null;
    private IApplicationPreferences applicationPreferences = null;

    private ExtensionFactory() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ExtensionFactory getInstance() {
        Class<ExtensionFactory> clazz = ExtensionFactory.class;
        synchronized (ExtensionFactory.class) {
            if (factory == null) {
                factory = new ExtensionFactory();
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return factory;
        }
    }

    public synchronized IApplicationPreferences getApplicationPreferences() {
        if (this.applicationPreferences == null) {
            this.applicationPreferences = new ApplicationPreferences();
        }
        return this.applicationPreferences;
    }

    public IMacroParser getMacroParser() throws MacroException {
        return MacroFactory.getInstance().getMacroParser();
    }

    public IMacroHelper getMacroHelper() throws MacroException {
        return MacroFactory.getInstance().getMacroHelper();
    }
}

