/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoSaveActivityLogCommand;
import com.raritan.rrc.ui.panes.FileBrowserPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.util.io.AllFilesFilter;
import java.io.File;

public class SaveActivityLogPanel
extends FileBrowserPanel {
    private static final long serialVersionUID = -7017529872865510276L;

    public SaveActivityLogPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.setTitle(this.bundle.getString("filebrowser.save.activitylog.title"));
    }

    @Override
    public void makeLayout() {
        super.makeLayout();
        this.chooser.setDialogType(1);
        this.chooser.setCommand(new DoSaveActivityLogCommand(this.scrContext));
        this.chooser.addChoosableFileFilter(new AllFilesFilter(3));
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
            file = new File(file.getParentFile(), string + "." + "TXT".toLowerCase());
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

