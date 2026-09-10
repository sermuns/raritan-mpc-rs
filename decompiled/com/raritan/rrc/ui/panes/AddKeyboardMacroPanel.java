/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.ui.commands.DoNewKeyboardMacroCommand;
import com.raritan.rrc.ui.panes.AddModifyKeyboardMacroPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import java.util.Enumeration;

public class AddKeyboardMacroPanel
extends AddModifyKeyboardMacroPanel {
    private static final long serialVersionUID = 5552818180641993930L;

    public AddKeyboardMacroPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.setShell(this.bundle.getString("AddKeyboardMacrosDialog.title"));
        this.ok.setCommand(new DoNewKeyboardMacroCommand(this.scrContext));
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        super.fillComponents(commandContext);
        this.nameField.setText("");
        String[] stringArray = KeyboardMacrosPreferences.returnNodes();
        KeyboardMacrosPreferences keyboardMacrosPreferences = null;
        for (int i = 0; i < stringArray.length; ++i) {
            keyboardMacrosPreferences = new KeyboardMacrosPreferences();
            keyboardMacrosPreferences.importPreferences(stringArray[i]);
            int n = keyboardMacrosPreferences.getHotKeyCombination();
            if (n <= -1) continue;
            this.hotKeyCombinationModel.removeElement(this.hotKeyCombinationType[n + 1]);
        }
        this.hotKeyComboBox.setSelectedIndex(0);
        this.macroSequenceListModel.clear();
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        super.feedCommandContext(commandContext);
        commandContext.setCommandParameter("keyboardMacroName", this.nameField.getText());
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

