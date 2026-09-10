/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.panes.CompressionPanel;
import com.raritan.rrc.ui.panes.ConnectPanel;
import com.raritan.rrc.ui.panes.SecurityPanel;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class AddModifyConnectionPanel
extends AbstractDisplay
implements MouseListener {
    private static final long serialVersionUID = 1L;
    protected ConnectPanel connectPanel;
    protected CompressionPanel compressionPanel;
    protected SecurityPanel securityPanel;
    protected JTabbedPane addConnectionTabbedPane;
    protected RaritanPropertyResourceBundle bundle;

    public AddModifyConnectionPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.makeLayout();
    }

    @Override
    public void makeLayout() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.addConnectionTabbedPane = new JTabbedPane();
        String string = this.bundle.getString("ConnectTab.name");
        this.connectPanel = new ConnectPanel(false, this.scrContext);
        this.addConnectionTabbedPane.add(string, this.connectPanel);
        this.compressionPanel = new CompressionPanel(false, this.scrContext);
        this.compressionPanel.setFocusable(true);
        string = this.bundle.getString("CompressionTab.name");
        this.addConnectionTabbedPane.add(string, this.compressionPanel);
        this.securityPanel = new SecurityPanel(false, this.scrContext);
        this.securityPanel.setFocusable(true);
        string = this.bundle.getString("SecurityTab.name");
        this.addConnectionTabbedPane.add(string, this.securityPanel);
        this.add((Component)this.addConnectionTabbedPane, "Center");
        this.add((Component)this.doButtonWidget(), "Last");
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.connectPanel.fillComponents(commandContext);
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.addActionListener(this);
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        return jPanel;
    }
}

