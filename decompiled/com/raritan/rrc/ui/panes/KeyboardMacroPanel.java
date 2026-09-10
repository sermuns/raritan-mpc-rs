/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.ui.commands.DoDeleteKeyboardMacroCommand;
import com.raritan.rrc.ui.commands.DoRunKeyboardMacroCommand;
import com.raritan.rrc.ui.commands.ShowAddKeyboardMacroCommand;
import com.raritan.rrc.ui.commands.ShowModifyKeyboardMacroCommand;
import com.raritan.rrc.ui.panes.mediator.RRCPanelMediator;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.ConfirmableCommandInterface;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.text.MessageFormat;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class KeyboardMacroPanel
extends AbstractDisplay
implements ListSelectionListener {
    private static final long serialVersionUID = 6042828474384257396L;
    protected DefaultListModel macroListModel;
    protected JList macroList;
    protected CommandButton runButton;
    protected CommandButton addButton;
    protected CommandButton removeButton;
    protected CommandButton modifyButton;

    public KeyboardMacroPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.macroListModel = new DefaultListModel();
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.makeLayout();
        this.setShell(this.bundle.getString("KeyboardMacrosDialog.title"));
        ((RRCPanelMediator)this.scrContext.getPanelMediator()).setMainKeyboardMacroPanel(this);
    }

    @Override
    public void makeLayout() {
        this.setLayout(new SpringLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.macroList = new JList(this.macroListModel);
        this.macroList.addListSelectionListener(this);
        JScrollPane jScrollPane = new JScrollPane(this.macroList, 20, 30);
        JPanel jPanel = new JPanel(new SpringLayout());
        this.runButton = new CommandButton(this.bundle.getString("RunMacroButton.text"), this.scrContext);
        this.runButton.setCommand(new DoRunKeyboardMacroCommand(this.scrContext));
        this.runButton.setEnabled(false);
        this.runButton.addActionListener(this);
        jPanel.add(this.runButton);
        this.addButton = new CommandButton(this.bundle.getString("AddMacroButton.text"), this.scrContext);
        this.addButton.setCommand(new ShowAddKeyboardMacroCommand(this.scrContext));
        this.addButton.addActionListener(this);
        jPanel.add(this.addButton);
        this.removeButton = new CommandButton(this.bundle.getString("RemoveMacroButton.text"), this.scrContext);
        this.removeButton.setCommand(new DoDeleteKeyboardMacroCommand(this.scrContext));
        this.removeButton.setConfirmation(true);
        this.removeButton.setEnabled(false);
        this.removeButton.addActionListener(this);
        jPanel.add(this.removeButton);
        this.modifyButton = new CommandButton(this.bundle.getString("ModifyMacroButton.text"), this.scrContext);
        this.modifyButton.setCommand(new ShowModifyKeyboardMacroCommand(this.scrContext));
        this.modifyButton.setEnabled(false);
        this.modifyButton.addActionListener(this);
        jPanel.add(this.modifyButton);
        this.cancel = new CommandButton(this.bundle.getString("CloseButton.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        jPanel.add(this.cancel);
        SpringUtilities.makeCompactGrid(jPanel, 5, 1, 5, 5, 5, 5);
        this.add(jScrollPane);
        this.add(jPanel);
        this.add(new JLabel(this.bundle.getString("MacrosLabel.text")));
        this.add(new JLabel(""));
        SpringUtilities.makeCompactGrid(this, 2, 2, 15, 15, 15, 15);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.macroListModel.clear();
        String[] stringArray = KeyboardMacrosPreferences.returnNodes();
        for (int i = 0; i < stringArray.length; ++i) {
            this.macroListModel.addElement(stringArray[i]);
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        commandContext.setCommandParameter("keyboardMacroName", this.macroList.getSelectedValue());
        commandContext.setCommandParameter("keyboardMacroNameList", this.macroList.getSelectedValues());
    }

    public void addMacroToList(String string) {
        this.macroListModel.addElement(string);
    }

    public void removeMacroFromList(String string) {
        this.macroListModel.removeElement(string);
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        Object object = listSelectionEvent.getSource();
        if (object instanceof JList && object == this.macroList) {
            if (this.macroList.getSelectedValue() != null) {
                this.runButton.setEnabled(this.runButton.getCommand().isExecutable());
                this.removeButton.setEnabled(true);
                this.modifyButton.setEnabled(true);
            } else {
                this.runButton.setEnabled(false);
                this.removeButton.setEnabled(false);
                this.modifyButton.setEnabled(false);
            }
        }
    }

    @Override
    protected boolean checkPassedCommandConfirm(Object object) {
        boolean bl = true;
        if (object instanceof ConfirmableCommandInterface) {
            boolean bl2 = bl = !((ConfirmableCommandInterface)object).getConfirmation();
        }
        if (!bl) {
            if (this.macroList.getSelectedValues().length > 1) {
                StringBuilder stringBuilder = new StringBuilder(this.bundle.getString("confirmation.dialog.command.execution.KeyboardMacroMulti.text1"));
                for (Object object2 : this.macroList.getSelectedValues()) {
                    stringBuilder.append("\n     " + object2.toString());
                }
                stringBuilder.append("\n" + this.bundle.getString("confirmation.dialog.command.execution.KeyboardMacroMulti.text2"));
                bl = CommonPopups.showConfirmationDialog(this.bundle.getString("confirmation.dialog.command.execution.KeyboardMacro.title"), stringBuilder, this, this.scrContext) == 2;
            } else {
                MessageFormat messageFormat = new MessageFormat(this.bundle.getString("confirmation.dialog.command.execution.KeyboardMacro.text"));
                bl = CommonPopups.showConfirmationDialog(this.bundle.getString("confirmation.dialog.command.execution.KeyboardMacro.title"), messageFormat.format(this.macroList.getSelectedValues()), this, this.scrContext) == 2;
            }
        }
        return bl;
    }
}

