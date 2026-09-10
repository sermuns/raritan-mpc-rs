/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.macro;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import nn.pp.ext.macro.IMacro;
import nn.pp.ext.macro.MacroException;
import org.xml.sax.SAXException;

public interface IMacroParser {
    public static final int NUMBER_OF_ATTRIBUTES = 3;

    public IMacro getKeyboardMacro(String var1);

    public List<IMacro> getKeyboardMacros() throws MacroException;

    public List<IMacro> getKeyboardMacros(String var1) throws MacroException;

    public void createKeyboardMacro(IMacro var1) throws MacroException;

    public void createKeyboardMacro(IMacro var1, boolean var2) throws MacroException;

    public void saveKeyboardMacro() throws MacroException;

    public void saveKeyboardMacro(String var1) throws MacroException;

    public void updateKeyboardMacro(IMacro var1, String var2) throws MacroException;

    public void updateKeyboardMacro(IMacro var1, String var2, boolean var3) throws MacroException;

    public void deleteKeyboardMacro(IMacro var1) throws MacroException;

    public void deleteKeyboardMacro(IMacro var1, boolean var2) throws MacroException;

    public SortedMap<String, IMacro> getValidMacros(File var1) throws IOException, SAXException;

    public String getBackupFilename();
}

