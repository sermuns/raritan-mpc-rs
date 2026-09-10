/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandFileChooser;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.util.CommandUtil;
import com.raritan.tools.util.io.DefaultFileMapper;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;

public class FileBrowserPanel
extends AbstractDisplay {
    private static final long serialVersionUID = -1301175886685546218L;
    protected CommandFileChooser chooser;
    protected CommandContext prevPanelContext;
    protected String title = "";

    public FileBrowserPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.makeLayout();
        this.setShell(null);
    }

    @Override
    public void setCommandContext(CommandContext commandContext) {
    }

    @Override
    public void makeLayout() {
        this.setLayout(new BorderLayout());
        this.chooser = new CommandFileChooser(this.scrContext);
        this.chooser.addActionListener(this);
        this.add((Component)this.chooser, "North");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("CancelSelection")) {
            Object object = actionEvent.getSource();
            Command command = null;
            if (CommandUtil.isCommandHolder(object)) {
                command = ((CommandHolder)object).getCommand();
            }
            this.setVisibleAfterCommand(command);
            return;
        }
        super.actionPerformed(actionEvent);
    }

    @Override
    protected boolean checkPassedCommandConfirm(Object object) {
        File file;
        if (this.chooser.getDialogType() == 0 && !(file = this.chooser.getSelectedFile()).exists()) {
            String string = this.bundle.getString("FileChooser.OpenFileNotExists");
            string = MessageFormat.format(string, file.getName());
            CommonPopups.showWarningDialog(this.bundle.getString("FileChooser.openDialogTitleText"), string, null, this.scrContext);
            return false;
        }
        return super.checkPassedCommandConfirm(object);
    }

    @Override
    protected void setVisibleAfterCommand(Command command) {
        if (command.equals(this.chooser.getCommand())) {
            this.getShell().setVisible(false);
        } else {
            super.setVisibleAfterCommand(command);
        }
    }

    public void setTitle(String string) {
        this.getShell().setTitle(string);
        this.title = string;
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        if (commandContext.getCommandParameter("filebrowsercontext") != null) {
            this.prevPanelContext = (CommandContext)commandContext.getCommandParameter("filebrowsercontext");
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        File file = this.chooser.getSelectedFile();
        if (commandContext.getCommandParameter("selectedFileMapper") == null) {
            commandContext.setCommandParameter("selectedFileMapper", new DefaultFileMapper());
        }
        commandContext.setCommandParameter("selectedFile", file);
    }

    protected boolean confirmFileOverwrite() {
        File file = this.chooser.getSelectedFile();
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return CommonPopups.showFileOverwriteConfirmationDialog(null, this.scrContext) == 2;
        }
        try {
            if (!file.createNewFile() || !file.delete()) {
                file = null;
            }
        }
        catch (Exception exception) {
            file = null;
        }
        if (file == null) {
            CommonPopups.showInfoDialog(this.title, this.bundle.getString("filebrowser.save.cannotopen"), null, this.scrContext);
        }
        return file != null;
    }
}

