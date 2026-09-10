/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.displays;

import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.commands.ConfirmableCommandInterface;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.Shell;
import com.raritan.tools.ui.panes.displays.ShellFocusObserver;
import com.raritan.tools.ui.panes.displays.ShellInternalFrame;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

public abstract class AbstractDisplay
extends JPanel
implements ActionListener,
WindowFocusListener,
FocusListener {
    protected static final String CMD_HELP = "CMD_HELP";
    private Shell shell;
    protected ShellInternalFrame shellInternalFrame;
    private CommandContext ctx;
    protected Component parentComponent = null;
    protected boolean resizable = false;
    protected boolean isDialog = false;
    protected boolean isNewPanel = false;
    protected boolean isInternalFrame = false;
    protected boolean isParentLimited = false;
    protected ScreenContext scrContext;
    protected RaritanPropertyResourceBundle bundle;
    protected CommandButton ok;
    protected CommandButton cancel;
    protected CommandButton apply;
    protected String helpURL = this.getClass().getName();
    protected Component helpNode = null;

    public AbstractDisplay(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.resizable = false;
        this.shell = new Shell(this.scrContext);
        this.getInputMap(2).put(KeyStroke.getKeyStroke(10, 0), "OK");
        this.getInputMap(2).put(KeyStroke.getKeyStroke(27, 0), "Cancel");
        this.getActionMap().put("OK", new AbstractAction(){
            private static final long serialVersionUID = 7832166575272756880L;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (AbstractDisplay.this.ok != null) {
                    AbstractDisplay.this.ok.doClick();
                }
            }
        });
        this.getActionMap().put("Cancel", new AbstractAction(){
            private static final long serialVersionUID = -5020860591759224987L;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (AbstractDisplay.this.cancel != null) {
                    AbstractDisplay.this.cancel.doClick();
                }
            }
        });
    }

    public void clearMaps() {
        try {
            this.getActionMap().remove("OK");
            System.out.println("OK action map cleared");
        }
        catch (Exception exception) {
            System.out.println("OK action map exception " + exception.getMessage());
        }
        try {
            this.getActionMap().remove("Cancel");
            System.out.println("Cancel action map cleared");
        }
        catch (Exception exception) {
            System.out.println("Cancel action map exception " + exception.getMessage());
        }
        try {
            this.getInputMap(2).remove(KeyStroke.getKeyStroke(10, 0));
            System.out.println("Enter key map cleared");
        }
        catch (Exception exception) {
            System.out.println("Enter key map exception " + exception.getMessage());
        }
        try {
            this.getInputMap(2).remove(KeyStroke.getKeyStroke(27, 0));
            System.out.println("Escape key map cleared");
        }
        catch (Exception exception) {
            System.out.println("Escape key map exception " + exception.getMessage());
        }
    }

    public void setCommandContext(CommandContext commandContext) {
        this.ctx = commandContext;
    }

    public CommandContext getCommandContext() {
        return this.ctx;
    }

    public abstract void makeLayout();

    public boolean isDialog() {
        return this.isDialog;
    }

    public boolean isInternalFrame() {
        return this.isInternalFrame;
    }

    public boolean isParentLimited() {
        return this.isParentLimited;
    }

    public void setShell(String string) {
        if (this.isParentLimited() && this.parentComponent != null) {
            this.shell = new Shell(this.parentComponent, this.scrContext);
        }
        this.shell.setTitle(string);
        this.shell.setResizable(this.resizable);
        this.shell.getContentPane().add(this);
        this.shell.setModal(true);
        this.shell.pack();
        this.shell.setVisible(false);
        this.shell.setClosable(true);
    }

    public void setShellInternalFrame(String string) {
        this.shellInternalFrame = new ShellInternalFrame();
        this.shellInternalFrame.setTitle(string);
        this.shellInternalFrame.setResizable(this.resizable);
        this.shellInternalFrame.setClosable(true);
        this.shellInternalFrame.pack();
        this.shellInternalFrame.setVisible(false);
        this.shellInternalFrame.getContentPane().add(this);
    }

    public Shell getShell() {
        return this.shell;
    }

    public ShellInternalFrame getShellInternalFrame() {
        return this.shellInternalFrame;
    }

    public JPanel doButtonWidget() {
        this.ok = new CommandButton("OK", this.scrContext);
        this.ok.addActionListener(this);
        this.cancel = new CommandButton("Cancel", this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        return jPanel;
    }

    protected static String getClassName(Object object) {
        String string = object.getClass().getName();
        int n = string.lastIndexOf(".");
        return string.substring(n + 1);
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        Component component = focusEvent.getOppositeComponent();
        if (component != null) {
            this.helpNode = component;
        }
    }

    @Override
    public void focusLost(FocusEvent focusEvent) {
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.scrContext.getLogger().logTextDebug("started");
        Object object = actionEvent.getSource();
        if (object instanceof CommandHolder && this.checkPassedCommandConfirm(object)) {
            Command command = ((CommandHolder)object).getCommand();
            this.scrContext.getLogger().logTextDebug("command is " + command.getKey());
            this.scrContext.getLogger().logTextDebug("cmd.isExecutable() " + command.isExecutable());
            if (command.isExecutable()) {
                this.executeCommand(command);
                if (this.ok != null) {
                    this.ok.setEnabled(true);
                }
            }
        }
        this.scrContext.getLogger().logTextDebug("finished");
    }

    protected void setVisibleAfterCommand(Command command) {
        boolean bl = this.getShell().isVisible();
        if (command != null && this.cancel != null && command.equals(this.cancel.getCommand())) {
            bl = false;
        } else if (command != null && this.ok != null && command.equals(this.ok.getCommand())) {
            bl = false;
        }
        if (this.getShell().isVisible() != bl) {
            this.getShell().setVisible(bl);
            if (!bl) {
                this.onPasivateDisplay();
            }
        }
    }

    protected boolean checkPassedCommandConfirm(Object object) {
        boolean bl = true;
        if (object instanceof ConfirmableCommandInterface) {
            boolean bl2 = bl = !((ConfirmableCommandInterface)object).getConfirmation();
        }
        if (!bl) {
            bl = CommonPopups.showConfirmationDialog(this.bundle.getString("confirmation.dialog.command.execution.title"), this.bundle.getString("confirmation.dialog.command.execution.text"), null, this.scrContext) == 2;
        }
        return bl;
    }

    protected CommandResult executeCommand(Command command) {
        command.getContext(true);
        this.setCommandContext(command.getContext());
        this.feedCommandContext(command.getContext());
        CommandResult commandResult = command.execute();
        if (!commandResult.isSuccess() && !commandResult.getStatusMessage().equals("")) {
            this.handleCommandResultErrorDescription(commandResult);
        } else {
            this.handleCommandResult(commandResult);
            this.setVisibleAfterCommand(command);
            this.scrContext.getPanelMediator().showPanel(command.getContext());
        }
        return commandResult;
    }

    protected void handleCommandResult(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    protected void handleCommandResultErrorDescription(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        if (commandResult.getErrorDescription() != null && commandResult.getErrorDescription().length > 0) {
            for (int i = 0; i < commandResult.getErrorDescription().length; ++i) {
                stringBuffer.append(commandResult.getErrorDescription()[i]);
                stringBuffer.append("\n");
            }
        } else if (commandResult.getStatusMessage() != null) {
            stringBuffer.append(commandResult.getStatusMessage());
        } else {
            stringBuffer.append("No command result info.");
        }
        CommonPopups.showCommandResultErrorMessage(stringBuffer.toString(), this, this.scrContext);
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    @Override
    public void windowLostFocus(WindowEvent windowEvent) {
        ShellFocusObserver.setFocusOnLast(this.scrContext);
    }

    @Override
    public void windowGainedFocus(WindowEvent windowEvent) {
    }

    public abstract void fillComponents(CommandContext var1);

    protected abstract void feedCommandContext(CommandContext var1);

    public void onPasivateDisplay() {
    }

    public void onActivateDisplay() {
    }

    public void reset() {
        AbstractDisplay.resetControls(this);
    }

    public static void resetControls(Container container) {
        int n = container.getComponentCount();
        Component component = null;
        for (int i = 0; i < n; ++i) {
            component = container.getComponent(i);
            AbstractDisplay.resetControl(component);
            if (!(component instanceof Container)) continue;
            AbstractDisplay.resetControls((Container)component);
        }
    }

    public static void resetControl(Component component) {
        if (component instanceof JTextComponent) {
            ((JTextComponent)component).setText("");
        } else if (component instanceof JComboBox) {
            try {
                ((JComboBox)component).setSelectedIndex(1);
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        } else if (component instanceof JCheckBox) {
            try {
                ((JCheckBox)component).setSelected(false);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
    }

    public boolean isNewPanel() {
        return this.isNewPanel;
    }

    public void setNewPanel(boolean bl) {
        this.isNewPanel = bl;
    }

    public boolean isBlockingDialog() {
        return false;
    }

    public void setDefaultFocussedComponent() {
    }
}

