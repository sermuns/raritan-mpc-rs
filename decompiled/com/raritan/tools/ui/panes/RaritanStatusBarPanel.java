/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes;

import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class RaritanStatusBarPanel
extends AbstractDisplay {
    private static final long serialVersionUID = 2928428417269155017L;
    protected JLabel label;

    public RaritanStatusBarPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.makeLayout();
    }

    @Override
    public void makeLayout() {
        this.label = new JLabel();
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(BorderFactory.createBevelBorder(1));
        this.add(this.label);
    }

    @Override
    public void setCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        String string = commandContext.getCommandResult("logtext").toString();
        if (string != null && !string.equals("")) {
            this.label.setText(string);
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }
}

