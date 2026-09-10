/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.util.TreeMap;

public class ShowExportMacrosCommand
extends AbstractCommand {
    private static RaritanPropertyResourceBundle bundle;
    public static final String COMMAND_KEY = "showExportMacrosCommand";

    public ShowExportMacrosCommand(ScreenContext screenContext) {
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

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        KeyboardMacrosPreferences.importPreferences();
        String[] stringArray = KeyboardMacrosPreferences.returnNodes();
        TreeMap<String, KeyboardMacrosPreferences> treeMap = new TreeMap<String, KeyboardMacrosPreferences>();
        KeyboardMacrosPreferences keyboardMacrosPreferences = null;
        for (int i = 0; i < stringArray.length; ++i) {
            keyboardMacrosPreferences = new KeyboardMacrosPreferences();
            keyboardMacrosPreferences.importSinglePreferenceInfo(stringArray[i]);
            treeMap.put(stringArray[i], keyboardMacrosPreferences);
        }
        CommandResult commandResult = new CommandResult(true, "");
        CommandContext commandContext = this.getContext();
        if (treeMap.size() < 1) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage(bundle.getString("macro.export.no.macros.exist"));
        } else {
            commandContext.setCommandParameter("importExportSelectedMacros", treeMap);
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }
}

