/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.commands.DoModifyConnectionPropertiesCommand;
import com.raritan.rrc.ui.panes.CompressionPanel;
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
import javaclientlib.tr.TRSRVR_COMP_PARAMS;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class PropertiesPanel
extends AbstractDisplay {
    private static final long serialVersionUID = -7174472063575603330L;
    private CompressionPanel compressionPanel;
    private JTabbedPane addConnectionTabbedPane;
    private IPReach dev;

    public PropertiesPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.makeLayout();
        this.setShell(this.bundle.getString("PropertiesDialog.title"));
    }

    @Override
    public void makeLayout() {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.setLayout(new BoxLayout(this, 3));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.addConnectionTabbedPane = new JTabbedPane();
        this.compressionPanel = new CompressionPanel(false, this.scrContext);
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
        TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = this.compressionPanel.getCompParams();
        commandContext.setCommandParameter("compParams", tRSRVR_COMP_PARAMS);
        int n = this.compressionPanel.getFramesPerSecond();
        commandContext.setCommandParameter("framesPerSecond", new Integer(n));
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.dev = (IPReach)commandContext.getCommandParameter("deviceNode");
        TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = (TRSRVR_COMP_PARAMS)commandContext.getCommandParameter("compParams");
        this.compressionPanel.setCompParams(tRSRVR_COMP_PARAMS);
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

    public void setEnabledProgressiveUpdateChkBox(boolean bl) {
        this.compressionPanel.setEnabledProgressiveUpdateChkBox(bl);
    }

    public void setEnabledColorDepthChkBox(boolean bl) {
        this.compressionPanel.setEnabledColorDepthChkBox(bl);
    }

    public void setEnabledInternetFlowControlChkBox(boolean bl) {
        this.compressionPanel.setEnabledInternetFlowControlChkBox(bl);
    }

    public void setEnabledSmoothingSlider(boolean bl) {
        this.compressionPanel.setEnabledSmoothingSlider(bl);
    }
}

