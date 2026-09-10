/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.ui.commands.DoModifyKeyboardMacroCommand;
import com.raritan.rrc.ui.panes.AddModifyKeyboardMacroPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.util.kbd.KeyboardKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class ModifyKeyboardMacroPanel
extends AddModifyKeyboardMacroPanel {
    private static final long serialVersionUID = 3198026599694724832L;
    private String oldMacroName;

    public ModifyKeyboardMacroPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.setShell(this.bundle.getString("ModifyKeyboardMacrosDialog.title"));
        this.ok.setCommand(new DoModifyKeyboardMacroCommand(this.scrContext));
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        String string;
        super.fillComponents(commandContext);
        this.oldMacroName = string = (String)commandContext.getCommandParameter("keyboardMacroName");
        this.nameField.setText(string);
        KeyboardMacrosPreferences keyboardMacrosPreferences = new KeyboardMacrosPreferences();
        keyboardMacrosPreferences.importPreferences(string);
        int n = keyboardMacrosPreferences.getHotKeyCombination();
        this.hotKeyCombinationModel.removeAllElements();
        for (int i = 0; i < this.hotKeyCombinationType.length; ++i) {
            this.hotKeyCombinationModel.addElement(this.hotKeyCombinationType[i]);
        }
        String[] stringArray = KeyboardMacrosPreferences.returnNodes();
        KeyboardMacrosPreferences keyboardMacrosPreferences2 = null;
        for (int i = 0; i < stringArray.length; ++i) {
            keyboardMacrosPreferences2 = new KeyboardMacrosPreferences();
            keyboardMacrosPreferences2.importPreferences(stringArray[i]);
            int n2 = keyboardMacrosPreferences2.getHotKeyCombination();
            if (n2 <= -1 || n2 == n) continue;
            this.hotKeyCombinationModel.removeElement(this.hotKeyCombinationType[n2 + 1]);
        }
        this.hotKeyCombinationModel.setSelectedItem(this.hotKeyCombinationType[n + 1]);
        this.macroSequenceListModel.clear();
        StringTokenizer stringTokenizer = new StringTokenizer(keyboardMacrosPreferences.getMacroSequence(), "&&");
        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            String string3 = string2.substring(string2.lastIndexOf(" ") + 1);
            int n3 = Integer.parseInt(string3);
            KeyboardKey keyboardKey = this.keyboardMappings.getItem(n3);
            if (keyboardKey == null) continue;
            KeyboardKey keyboardKey2 = new KeyboardKey(keyboardKey.getIndex(), keyboardKey.getKeyName(), keyboardKey.getKeyCode(), keyboardKey.getKeyLocation());
            if (string2.startsWith("p")) {
                keyboardKey2.setString(this.bundle.getString("press.text") + " " + keyboardKey.getKeyName());
            } else if (string2.startsWith("r")) {
                keyboardKey2.setString(this.bundle.getString("release.text") + " " + keyboardKey.getKeyName());
            }
            this.macroSequenceListModel.addElement(keyboardKey2);
        }
        this.clearButton.setEnabled(true);
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        super.feedCommandContext(commandContext);
        commandContext.setCommandParameter("keyboardMacroName", this.nameField.getText());
        commandContext.setCommandParameter("oldMacroName", this.oldMacroName);
        String string = (String)this.hotKeyComboBox.getSelectedItem();
        commandContext.setCommandParameter("keyboardMacroHotKey", new Integer(this.getHotKeyIndex(string)));
        ArrayList arrayList = new ArrayList();
        Enumeration enumeration = this.macroSequenceListModel.elements();
        while (enumeration.hasMoreElements()) {
            arrayList.add(enumeration.nextElement());
        }
        commandContext.setCommandParameter("macroSequence", arrayList);
    }
}

