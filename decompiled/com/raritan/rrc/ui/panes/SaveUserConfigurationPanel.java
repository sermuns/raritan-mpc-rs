/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoSaveUserConfigurationCommand;
import com.raritan.rrc.ui.panes.FileBrowserPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.util.io.AllFilesFilter;
import java.io.File;

public class SaveUserConfigurationPanel
extends FileBrowserPanel {
    private static final long serialVersionUID = -220224341399793291L;

    public SaveUserConfigurationPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.setTitle(this.bundle.getString("filebrowser.save.userconfig.title"));
    }

    @Override
    public boolean isDialog() {
        return this.isDialog;
    }

    @Override
    public void makeLayout() {
        super.makeLayout();
        this.chooser.setDialogType(1);
        this.chooser.setCommand(new DoSaveUserConfigurationCommand(this.scrContext));
        this.chooser.addChoosableFileFilter(new AllFilesFilter(4));
    }

    @Override
    protected boolean checkPassedCommandConfirm(Object object) {
        this.verifyFileExtension();
        boolean bl = super.checkPassedCommandConfirm(object);
        return bl && this.confirmFileOverwrite();
    }

    private void verifyFileExtension() {
        File file = this.chooser.getSelectedFile();
        String string = file.getName();
        int n = string.lastIndexOf(46);
        if (n < 0) {
            file = new File(file.getParentFile(), string + "." + "RFP".toLowerCase());
        }
        this.chooser.setSelectedFile(file);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        super.fillComponents(commandContext);
        this.chooser.rescanCurrentDirectory();
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        commandContext.setCommandParameter("selectedFileMapper", AllFilesFilter.createFileMapper());
        super.feedCommandContext(commandContext);
    }
}

