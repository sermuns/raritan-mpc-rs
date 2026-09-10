/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoSingleMouseModeCommand;
import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Robot;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

public class SingleCursorInstructionsPanel
extends AbstractDisplay {
    private static final long serialVersionUID = -4386781648327798198L;
    private RRCScreenContext scrCtx;
    protected RaritanPropertyResourceBundle bundle;
    private JCheckBox showChkBox;
    private JLabel label3;

    public SingleCursorInstructionsPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.scrCtx = (RRCScreenContext)screenContext;
        this.makeLayout();
        this.setShell(this.bundle.getString("SingleMouseModeInstructions.title"));
    }

    @Override
    public void makeLayout() {
        this.setLayout(new SpringLayout());
        JPanel jPanel = new JPanel(new FlowLayout(1));
        JLabel jLabel = new JLabel("<html>" + this.bundle.getString("SingleMouseModeInstructions1.text") + "<br>" + this.bundle.getString("SingleMouseModeInstructions1cont.text") + "</html>");
        jPanel.add(jLabel);
        this.add(jPanel);
        JPanel jPanel2 = new JPanel(new FlowLayout(1));
        JLabel jLabel2 = new JLabel(this.bundle.getString("SingleMouseModeInstructions2.text"));
        jPanel2.add(jLabel2);
        this.add(jPanel2);
        JPanel jPanel3 = new JPanel(new FlowLayout(1));
        SingleCursorInstructionsPanel singleCursorInstructionsPanel = this;
        this.label3 = new JLabel(this.bundle.getString("SingleMouseModeInstructions3.text") + singleCursorInstructionsPanel.scrCtx.getKvmPopupKey() + this.bundle.getString("SingleMouseModeInstructions5.text"));
        jPanel3.add(this.label3);
        this.add(jPanel3);
        JPanel jPanel4 = new JPanel(new FlowLayout(1));
        this.showChkBox = new JCheckBox(this.bundle.getString("SingleMouseModeInstructions4.text"));
        this.showChkBox.setSelected(true);
        jPanel4.add(this.showChkBox);
        this.add(jPanel4);
        JPanel jPanel5 = new JPanel(new FlowLayout(1));
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.setCommand(new DoSingleMouseModeCommand(this.scrContext));
        this.ok.addActionListener(this);
        jPanel5.add(this.ok);
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        jPanel5.add(this.cancel);
        this.add(jPanel5);
        SpringUtilities.makeCompactGrid(this, 5, 1, 15, 10, 15, 5);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        SingleCursorInstructionsPanel singleCursorInstructionsPanel = this;
        this.label3.setText(this.bundle.getString("SingleMouseModeInstructions3.text") + singleCursorInstructionsPanel.scrCtx.getKvmPopupKey() + this.bundle.getString("SingleMouseModeInstructions5.text"));
        this.showChkBox.setSelected(((RRCScreenContext)this.scrContext).getAppSettings().isSingleMouseInstructions());
        if (OS.getCurrent() == OS.MAC) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    SingleCursorInstructionsPanel.this.repaint();
                }
            });
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        commandContext.setCommandParameter("showSingleCursorModeInstructions", new Boolean(this.showChkBox.isSelected()));
        if (OS.getCurrent() == OS.MAC && "doSingleMouseModeCommand".equals(commandContext.getCommandKey())) {
            try {
                new Robot().mouseMove(0, 0);
            }
            catch (AWTException aWTException) {
                // empty catch block
            }
        }
    }
}

