/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoSaveDeviceConfigurationCommand;
import com.raritan.rrc.ui.panes.FileBrowserPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.util.io.AllFilesFilter;
import java.io.File;

public class SaveDeviceConfigurationPanel
extends FileBrowserPanel {
    private static final long serialVersionUID = -5219254633560134387L;

    public SaveDeviceConfigurationPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.setTitle(this.bundle.getString("filebrowser.save.deviceconfig.title"));
    }

    @Override
    public boolean isDialog() {
        return this.isDialog;
    }

    @Override
    public void makeLayout() {
        super.makeLayout();
        this.chooser.setDialogType(1);
        this.chooser.setCommand(new DoSaveDeviceConfigurationCommand(this.scrContext));
        this.chooser.addChoosableFileFilter(new AllFilesFilter(4));
    }

    @Override
    protected boolean checkPassedCommandConfirm(Object object) {
        this.verifyFileExtension();
        boolean bl = super.checkPassedCommandConfirm(object);
        return bl && this.confirmFileOverwrite();
    }

    private boolean fileExists(File file) {
        return file != null && file.exists();
    }

    private void verifyFileExtension() {
        File file = this.chooser.getSelectedFile();
        String string = file.getName();
        boolean bl = false;
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

