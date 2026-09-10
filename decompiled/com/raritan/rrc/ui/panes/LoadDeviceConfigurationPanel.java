/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoLoadConfigurationCommand;
import com.raritan.rrc.ui.panes.FileBrowserPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.util.io.AllFilesFilter;

public class LoadDeviceConfigurationPanel
extends FileBrowserPanel {
    private static final long serialVersionUID = 8182265312457127698L;

    public LoadDeviceConfigurationPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.setTitle(this.bundle.getString("filebrowser.load.deviceconfig.title"));
    }

    @Override
    public void makeLayout() {
        super.makeLayout();
        this.chooser.setDialogType(0);
        this.chooser.setCommand(new DoLoadConfigurationCommand(this.scrContext));
        this.chooser.addChoosableFileFilter(new AllFilesFilter(4));
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        commandContext.setCommandParameter("selectedFileMapper", AllFilesFilter.createFileMapper());
        super.feedCommandContext(commandContext);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        super.fillComponents(commandContext);
        this.chooser.rescanCurrentDirectory();
    }
}

