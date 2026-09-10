/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoImportSelectedMacrosCommand;
import com.raritan.rrc.ui.panes.MacroSelectionPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import javax.swing.JPanel;

public class MacroImportSelectionPanel
extends MacroSelectionPanel {
    private static final long serialVersionUID = 2028595561540428643L;

    public MacroImportSelectionPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
    }

    @Override
    public JPanel doButtonWidget() {
        JPanel jPanel = super.doButtonWidget();
        this.ok.setCommand(new DoImportSelectedMacrosCommand(this.scrContext));
        return jPanel;
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        super.fillComponents(commandContext);
        this.setShell(this.bundle.getString("macro.import.selection.title"));
    }
}

