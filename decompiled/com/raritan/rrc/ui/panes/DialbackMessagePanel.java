/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.commands.DoCancelLoginCommand;
import com.raritan.rrc.util.TaskCompletionListener;
import com.raritan.rrc.util.TaskCompletionNotifier;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DialbackMessagePanel
extends AbstractDisplay
implements TaskCompletionListener {
    private JOptionPane optionPane;
    private IPReach device;

    public DialbackMessagePanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.makeLayout();
        this.setShell(this.bundle.getString("dialbackmessagepanel.title"));
        this.getShell().setClosable(false);
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        commandContext.setCommandParameter("devices", this.device);
    }

    @Override
    public void makeLayout() {
        this.doButtonWidget();
        this.optionPane = new JOptionPane("", 1, -1, null, new Object[]{this.cancel});
        this.add(this.optionPane);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.optionPane.setMessage(commandContext.getCommandParameter("dialbackMessagePanel.message"));
        this.repaint();
        this.device = (IPReach)commandContext.getCommandParameter("devices");
        TaskCompletionNotifier taskCompletionNotifier = (TaskCompletionNotifier)commandContext.getCommandParameter("taskCompletionNotifier");
        taskCompletionNotifier.addTaskCompletionListener(this);
    }

    @Override
    public JPanel doButtonWidget() {
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new DoCancelLoginCommand(this.scrContext));
        this.cancel.addActionListener(this);
        return null;
    }

    @Override
    public void taskCompleted(Object object) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                DialbackMessagePanel.this.getShell().setVisible(false);
            }
        });
    }
}

