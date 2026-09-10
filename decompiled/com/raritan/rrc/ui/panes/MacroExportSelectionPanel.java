/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.ui.commands.DoExportSelectedMacrosCommand;
import com.raritan.rrc.ui.panes.MacroSelectionPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JPanel;

public class MacroExportSelectionPanel
extends MacroSelectionPanel {
    private static final long serialVersionUID = 2028595561540428644L;
    private File exportFileName = null;

    public MacroExportSelectionPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
    }

    @Override
    public JPanel doButtonWidget() {
        JPanel jPanel = super.doButtonWidget();
        this.ok.setCommand(new DoExportSelectedMacrosCommand(this.scrContext));
        return jPanel;
    }

    @Override
    public void feedCommandContext(CommandContext commandContext) {
        super.feedCommandContext(commandContext);
        commandContext.setCommandParameter("exportSelectedMacrosFilename", this.exportFileName);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        super.fillComponents(commandContext);
        this.setShell(this.bundle.getString("macro.export.selection.title"));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object == this.ok) {
            this.ok.setEnabled(false);
            this.exportFileName = this.selectFileForExport();
            if (this.exportFileName == null) {
                this.ok.setEnabled(true);
                return;
            }
        }
        super.actionPerformed(actionEvent);
    }

    private File selectFileForExport() {
        File file = null;
        while ((file = KeyboardMacrosPreferences.chooseXmlFile(this.bundle.getString("macro.export.file.dialog.title"), 1, this)) != null && file.exists()) {
            int n = CommonPopups.showFileOverwriteConfirmationDialog(this, this.scrContext);
            if (n == 1) continue;
            file.delete();
            break;
        }
        return file;
    }
}

