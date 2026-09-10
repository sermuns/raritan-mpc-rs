/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.screens;

import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.commands.ConfirmableCommandInterface;
import com.raritan.tools.commands.DummyCommand;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.screens.ScreenManager;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

public abstract class BaseScreen
extends JPanel
implements ActionListener {
    public String CMD_OK;
    public String CMD_CANCEL;
    protected JPanel pnlButtons;
    protected CommandButton btnOk;
    protected CommandButton btnCancel;
    protected ScreenContext scrContext;
    protected ScreenManager scrManager;

    public BaseScreen(ScreenContext screenContext, ScreenManager screenManager) {
        this.scrContext = screenContext;
        this.scrManager = screenManager;
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.CMD_OK = raritanPropertyResourceBundle.getString("basescreen.command.ok.text");
        this.CMD_CANCEL = raritanPropertyResourceBundle.getString("basescreen.command.cancel.text");
        this.initScreen();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        boolean bl = false;
        Object object = actionEvent.getSource();
        if (object instanceof ConfirmableCommandInterface) {
            bl = ((ConfirmableCommandInterface)actionEvent.getSource()).getConfirmation();
        }
        if (object instanceof CommandHolder) {
            Object object2;
            if (bl) {
                object2 = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
                boolean bl2 = bl = CommonPopups.showConfirmationDialog(((ResourceBundle)object2).getString("confirmation.dialog.command.execution.title"), ((ResourceBundle)object2).getString("confirmation.dialog.command.execution.text"), null, this.scrContext) != 2;
            }
            if (!bl) {
                this.setCursor(Cursor.getPredefinedCursor(3));
                object2 = (CommandHolder)object;
                this.handleCommandResult(object2.getCommand().execute());
                this.scrManager.show();
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    protected void initScreen() {
        this.setLayout(new GridBagLayout());
        this.initComponents();
    }

    protected void constructButtonsPane(JButton[] jButtonArray) {
        this.pnlButtons = new JPanel();
        this.pnlButtons.setLayout(new GridLayout(1, 0, 4, 4));
        if (jButtonArray != null) {
            for (int i = 0; i < jButtonArray.length; ++i) {
                this.pnlButtons.add(jButtonArray[i]);
            }
        }
        this.btnOk = new CommandButton(this.CMD_OK, this.scrContext);
        this.btnOk.setCommand(this.getOkCommand());
        this.btnOk.setConfirmation(this.getOkConfirmation());
        this.btnOk.setIcon(this.getOkIcon());
        this.btnOk.setEnabled(this.getOkCommand().isExecutable());
        this.btnOk.addActionListener(this);
        this.pnlButtons.add(this.btnOk);
        this.btnCancel = new CommandButton(this.CMD_CANCEL, this.scrContext);
        this.btnCancel.setCommand(this.getCancelCommand());
        this.btnCancel.setConfirmation(this.getCancelConfirmation());
        this.btnCancel.setIcon(this.getCancelIcon());
        this.btnCancel.setEnabled(this.getCancelCommand().isExecutable());
        this.btnCancel.addActionListener(this);
        this.pnlButtons.add(this.btnCancel);
    }

    protected Command getOkCommand() {
        return new DummyCommand(this.scrContext);
    }

    protected Command getCancelCommand() {
        return new DummyCommand(this.scrContext);
    }

    protected boolean getOkConfirmation() {
        return false;
    }

    protected boolean getCancelConfirmation() {
        return false;
    }

    protected Icon getOkIcon() {
        return null;
    }

    protected Icon getCancelIcon() {
        return null;
    }

    protected abstract void initComponents();

    protected abstract void handleCommandResult(CommandResult var1);
}

