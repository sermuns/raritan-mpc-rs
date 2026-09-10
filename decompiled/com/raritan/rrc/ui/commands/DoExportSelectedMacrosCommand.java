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
import java.util.TreeMap;

public class DoExportSelectedMacrosCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doExportSelectedMacrosCommand";
    private static RaritanPropertyResourceBundle bundle;

    public DoExportSelectedMacrosCommand(ScreenContext screenContext) {
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
        TreeMap treeMap = (TreeMap)this.getContext().getCommandParameter("importExportSelectedMacros");
        File file = (File)this.getContext().getCommandParameter("exportSelectedMacrosFilename");
        if (treeMap == null) {
            System.out.println("Macros TreeMap is null");
            return new CommandResult(false, "Something has gone wrong");
        }
        return KeyboardMacrosPreferences.doExport(treeMap, file, bundle);
    }
}

