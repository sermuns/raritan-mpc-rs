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
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.util.Arrays;
import java.util.TreeMap;
import javax.swing.JOptionPane;

public class DoImportSelectedMacrosCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doImportSelectedMacrosCommand";
    private static final int EXIT_DIALOG = -1;
    private static final int YES = 0;
    private static final int YES_TO_ALL = 1;
    private static final int NO = 2;
    private static final int NO_TO_ALL = 3;
    private static final int RENAME = 4;
    private static final int CANCEL = 5;
    private static RaritanPropertyResourceBundle bundle;

    public DoImportSelectedMacrosCommand(ScreenContext screenContext) {
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

    private int confirmOverwrite(String string) {
        Object[] objectArray = new Object[]{bundle.getString("Yes"), bundle.getString("yes.to.all"), bundle.getString("No"), bundle.getString("no.to.all"), bundle.getString("macro.import.rename.button"), bundle.getString("generic.dialog.cancel.text")};
        JOptionPane jOptionPane = new JOptionPane(bundle.getString("macro.import.rename.alert").replaceFirst("\n\n", ":\n\n" + string + "\n\n") + "\n ", 1, 2, null, objectArray, objectArray[0]);
        Object object = this.scrContext.getPanelMediator().showDialogOnTop(jOptionPane, bundle.getString("macro.import.title"), (AbstractDisplay)this.getContext().getCommandParameter("importExportSelectedMacrosCommandParent"));
        int n = Arrays.asList(objectArray).indexOf(object);
        return n;
    }

    private String renameMacro(String string) {
        JOptionPane jOptionPane = new JOptionPane(bundle.getString("macro.import.rename.message") + ":\n", -1, 2);
        jOptionPane.setWantsInput(true);
        jOptionPane.setInitialSelectionValue(string);
        Object object = this.scrContext.getPanelMediator().showDialogOnTop(jOptionPane, bundle.getString("macro.import.rename.title"), (AbstractDisplay)this.getContext().getCommandParameter("importExportSelectedMacrosCommandParent"));
        if (Integer.valueOf(0).equals(object)) {
            return jOptionPane.getInputValue().toString();
        }
        return null;
    }

    @Override
    public CommandResult execute() {
        TreeMap treeMap = (TreeMap)this.getContext().getCommandParameter("importExportSelectedMacros");
        if (treeMap == null) {
            System.out.println("Macros TreeMap is null");
        } else if (treeMap.size() < 1) {
            System.out.println("No macros were selected");
        } else {
            int n;
            KeyboardMacrosPreferences keyboardMacrosPreferences = new KeyboardMacrosPreferences();
            boolean[] blArray = new boolean[10];
            for (n = 0; n < 10; ++n) {
                blArray[n] = false;
            }
            String[] stringArray = KeyboardMacrosPreferences.returnNodes();
            for (n = 0; n < stringArray.length; ++n) {
                keyboardMacrosPreferences.importSinglePreferenceInfo(stringArray[n]);
                if (keyboardMacrosPreferences.getHotKeyCombination() == -1) continue;
                blArray[keyboardMacrosPreferences.getHotKeyCombination()] = true;
            }
            boolean bl = false;
            boolean bl2 = false;
            stringArray = treeMap.keySet().toArray(new String[0]);
            int n2 = -1;
            for (n = 0; n < stringArray.length; ++n) {
                if (KeyboardMacrosPreferences.containsNode(stringArray[n])) {
                    if (bl2) continue;
                    if (!bl) {
                        n2 = this.confirmOverwrite(stringArray[n]);
                        if (n2 == -1 || n2 == 5) break;
                        if (n2 == 2) continue;
                        if (n2 == 3) {
                            bl2 = true;
                            continue;
                        }
                        if (n2 == 1) {
                            bl = true;
                        } else if (n2 == 4) {
                            String string = stringArray[n];
                            while ((string = this.renameMacro(stringArray[n])) != null && KeyboardMacrosPreferences.containsNode(string)) {
                                JOptionPane jOptionPane = new JOptionPane(bundle.getString("macro.import.renamed.exists.alert").replaceFirst("%M", string), 2);
                                this.scrContext.getPanelMediator().showDialogOnTop(jOptionPane, bundle.getString("macro.import.renamed.exists.title"), (AbstractDisplay)this.getContext().getCommandParameter("importExportSelectedMacrosCommandParent"));
                            }
                            if (string == null || string.length() < 1) {
                                string = stringArray[n];
                                --n;
                            } else {
                                keyboardMacrosPreferences = (KeyboardMacrosPreferences)treeMap.get(stringArray[n]);
                                treeMap.remove(stringArray[n]);
                                stringArray[n] = string;
                                keyboardMacrosPreferences.setMacroName(string);
                                treeMap.put(stringArray[n], keyboardMacrosPreferences);
                            }
                        }
                    }
                }
                if ((keyboardMacrosPreferences = (KeyboardMacrosPreferences)treeMap.get(stringArray[n])).getHotKeyCombination() > -1 && blArray[keyboardMacrosPreferences.getHotKeyCombination()]) {
                    keyboardMacrosPreferences.setHotKeyCombination(-1);
                }
                keyboardMacrosPreferences.exportPreferences(stringArray[n], false);
            }
            KeyboardMacrosPreferences.exportPreferences();
        }
        return new CommandResult(true, bundle.getString("macro.import.success"));
    }
}

