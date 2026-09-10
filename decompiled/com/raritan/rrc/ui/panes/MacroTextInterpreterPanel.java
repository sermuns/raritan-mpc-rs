/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoMacroTextInterpreterCommand;
import com.raritan.rrc.ui.panes.SendTextToTargetPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.util.kbd.KeyboardMappings;
import javax.swing.JPanel;
import nn.pp.common.ui.helpers.IMacroCreatorDialog;

public class MacroTextInterpreterPanel
extends SendTextToTargetPanel {
    private static final long serialVersionUID = 2028595561540428645L;
    private KeyboardMappings kmap = null;
    private IMacroCreatorDialog myParent = null;

    public MacroTextInterpreterPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
    }

    @Override
    public JPanel doButtonWidget() {
        JPanel jPanel = super.doButtonWidget();
        this.ok.setCommand(new DoMacroTextInterpreterCommand(this.scrContext));
        return jPanel;
    }

    @Override
    public void feedCommandContext(CommandContext commandContext) {
        targetUSIntlSelected = this.targetIsUSIntl.isSelected();
        commandContext.setCommandParameter("macroTextInterpreter", this.kmap);
        commandContext.setCommandParameter("macroTextInterpreterParent", this.myParent);
        commandContext.setCommandParameter("macroTextInterpreterText", this.textPad.getText());
        commandContext.setCommandParameter("macroTextInterpreterLang", targetUSIntlSelected && this.keyboardType == 0 ? 255 : this.keyboardType);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        super.fillComponents(commandContext);
        this.kmap = (KeyboardMappings)commandContext.getCommandParameter("macroTextInterpreter");
        this.myParent = (IMacroCreatorDialog)commandContext.getCommandParameter("macroTextInterpreterParent");
    }
}

