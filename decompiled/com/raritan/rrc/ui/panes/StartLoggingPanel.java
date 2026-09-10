/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.commands.DoStartLoggingCommand;
import com.raritan.rrc.ui.panes.FileBrowserPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.util.io.AllFilesFilter;

public class StartLoggingPanel
extends FileBrowserPanel {
    private static final long serialVersionUID = -8425217259916272248L;
    private Port device;

    public StartLoggingPanel(boolean bl, ScreenContext screenContext) {
        super(bl, screenContext);
        this.setTitle(this.bundle.getString("filebrowser.start.logging.title"));
    }

    @Override
    public boolean isDialog() {
        return this.isDialog;
    }

    @Override
    public void makeLayout() {
        super.makeLayout();
        this.chooser.setDialogType(1);
        this.chooser.setCommand(new DoStartLoggingCommand(this.scrContext));
    }

    @Override
    protected boolean checkPassedCommandConfirm(Object object) {
        boolean bl = super.checkPassedCommandConfirm(object);
        return bl && this.confirmFileOverwrite();
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.device = (Port)commandContext.getCommandParameter("devices");
        super.fillComponents(commandContext);
        this.chooser.rescanCurrentDirectory();
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        commandContext.setCommandParameter("devices", this.device);
        commandContext.setCommandParameter("selectedFileMapper", AllFilesFilter.createFileMapper());
        super.feedCommandContext(commandContext);
    }
}

