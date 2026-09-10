/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
import javaclientlib.utils.RRCLogger;
import javax.swing.JOptionPane;
import nn.pp.ext.macro.IMacro;
import nn.pp.ext.macro.MacroFactory;
import org.xml.sax.SAXException;

public class ShowImportMacrosCommand
extends AbstractCommand {
    private static RaritanPropertyResourceBundle bundle;
    public static final String COMMAND_KEY = "showImportMacrosCommand";

    public ShowImportMacrosCommand(ScreenContext screenContext) {
        super(screenContext);
        bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private TreeMap getMacrosFromFile(StringBuffer stringBuffer) {
        this.scrContext.getLogger().logTextDebug(" Started ");
        SortedMap<String, IMacro> sortedMap = null;
        File file = KeyboardMacrosPreferences.chooseXmlFile(bundle.getString("macro.import.file.dialog.title"), 0, JOptionPane.getFrameForComponent(this.scrContext.getApplication().getContentPane()));
        if (file == null) {
            stringBuffer.append(bundle.getString("macro.import.file.selection.cancelled"));
            return null;
        }
        if (!file.exists()) {
            stringBuffer.append(bundle.getString("macro.import.file.selection.not.exist") + ": " + file.toString());
        } else if (!file.canRead()) {
            stringBuffer.append(bundle.getString("macro.import.file.selection.unreadable") + ": " + file.toString());
        }
        if (stringBuffer.length() < 1) {
            try {
                sortedMap = MacroFactory.getInstance().getMacroParser().getValidMacros(file);
            }
            catch (IOException iOException) {
                RRCLogger.log(300, 1, iOException, "Exception on parsing KeyboardMacroFile : " + file);
                stringBuffer.append(bundle.getString("macro.import.file.selection.invalid"));
            }
            catch (SAXException sAXException) {
                RRCLogger.log(300, 1, sAXException, "Exception on parsing KeyboardMacroFile : " + file);
                stringBuffer.append(bundle.getString("macro.import.file.selection.invalid"));
            }
        }
        TreeMap<Object, Object> treeMap = null;
        if (sortedMap != null) {
            treeMap = new TreeMap<String, IMacro>(sortedMap);
            for (String string : sortedMap.keySet()) {
                KeyboardMacrosPreferences keyboardMacrosPreferences = new KeyboardMacrosPreferences();
                IMacro iMacro = (IMacro)sortedMap.get(string);
                keyboardMacrosPreferences.setMacroName(string);
                keyboardMacrosPreferences.setHotKeyCombination(iMacro.getHotKey());
                keyboardMacrosPreferences.setMacroSequence(iMacro.getSequence());
                treeMap.put(string, keyboardMacrosPreferences);
            }
            this.getContext().setCommandParameter("importExportSelectedMacros", treeMap);
        } else {
            treeMap = new TreeMap();
        }
        return treeMap;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult(true, "");
        StringBuffer stringBuffer = new StringBuffer();
        TreeMap treeMap = this.getMacrosFromFile(stringBuffer);
        if (stringBuffer.length() > 0) {
            commandResult.setIsSuccess(false);
            if (treeMap != null) {
                commandResult.setStatusMessage(stringBuffer.toString());
            }
        } else if (treeMap.size() < 1) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage(bundle.getString("macro.import.none.in.file"));
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }
}

