/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.commands.DoModifyConnectionPropertiesCommand;
import com.raritan.rrc.ui.panes.G2CompressionPanel;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import nn.pp.rccore.RCCore;

public class G2PropertiesPanel
extends AbstractDisplay {
    private static final long serialVersionUID = -7174472063575603330L;
    private G2CompressionPanel compressionPanel;
    private RFBView rfbView;
    private String portKey;
    private JTabbedPane addConnectionTabbedPane;
    private IPReach dev;

    public G2PropertiesPanel(boolean bl, ScreenContext screenContext, RFBView rFBView, String string) {
        super(screenContext);
        this.isDialog = bl;
        this.portKey = string;
        this.rfbView = rFBView;
        this.makeLayout();
        this.setShell(this.bundle.getString("PropertiesDialog.title"));
    }

    @Override
    public void makeLayout() {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.setLayout(new BoxLayout(this, 3));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.addConnectionTabbedPane = new JTabbedPane();
        this.compressionPanel = new G2CompressionPanel(false, this.scrContext, this.portKey, this.rfbView);
        String string = raritanPropertyResourceBundle.getString("CompressionTab.name");
        this.addConnectionTabbedPane.add(string, this.compressionPanel);
        this.add((Component)this.addConnectionTabbedPane, "Center");
        this.add((Component)this.doButtonWidget(), "Last");
        this.ok.setCommand(new DoModifyConnectionPropertiesCommand(this.scrContext));
        this.apply.setCommand(new DoModifyConnectionPropertiesCommand(this.scrContext));
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        commandContext.setCommandParameter("deviceNode", this.dev);
        commandContext.setCommandParameter("g2ColorDepth", (Object)this.compressionPanel.allColorDepths.get(this.compressionPanel.colorDepthComboBox.getSelectedItem().toString()));
        commandContext.setCommandParameter("g2Compression", (Object)this.compressionPanel.allConnSpeeds.get(this.compressionPanel.connSpeedComboBox.getSelectedItem().toString()));
        commandContext.setCommandParameter("g2Smoothing", (Object)RCCore.Smoothing.values()[this.compressionPanel.smoothing.getValue()]);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.dev = (IPReach)commandContext.getCommandParameter("deviceNode");
        this.compressionPanel.setDefaultFocussedComponent();
        this.apply.setEnabled(false);
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.addActionListener(this);
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        this.apply = new CommandButton(this.bundle.getString("OptionDialog.applyLabel"), this.scrContext);
        this.apply.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        jPanel.add(this.apply);
        return jPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
        Object object = actionEvent.getSource();
        this.scrContext.getLogger().logTextDebug("started");
        if (object instanceof JComboBox) {
            this.apply.setEnabled(true);
        } else {
            this.apply.setEnabled(false);
        }
        this.scrContext.getLogger().logTextDebug("finished");
    }

    public void setApplyButton(boolean bl) {
        this.apply.setEnabled(bl);
    }

    public void setEnabledConnSpeedComboBox(boolean bl) {
        this.compressionPanel.setEnabledConnSpeedComboBox(bl);
    }

    public void setEnabledColorDepthChkBox(boolean bl) {
        this.compressionPanel.setEnabledColorDepthChkBox(bl);
    }
}

