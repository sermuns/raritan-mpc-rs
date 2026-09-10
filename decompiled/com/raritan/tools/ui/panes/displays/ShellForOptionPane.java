/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.displays;

import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.Shell;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;

public class ShellForOptionPane
extends Shell {
    private JOptionPane pane;
    private boolean visible;

    public ShellForOptionPane(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public void setVisible(boolean bl) {
        if (this.visible == bl) {
            return;
        }
        this.visible = bl;
        if (bl) {
            this.setLocationRelativeTo(this.getOwner());
        }
        super.setVisible(bl);
    }

    public void setOptionPane(final JOptionPane jOptionPane) {
        this.pane = jOptionPane;
        Container container = this.getContentPane();
        container.removeAll();
        container.setLayout(new BorderLayout());
        container.add((Component)jOptionPane, "Center");
        this.pack();
        jOptionPane.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (ShellForOptionPane.this.isVisible() && propertyChangeEvent.getSource() == jOptionPane && propertyChangeEvent.getPropertyName().equals("value") && propertyChangeEvent.getNewValue() != null && propertyChangeEvent.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
                    ShellForOptionPane.this.performAction(propertyChangeEvent.getNewValue());
                    jOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    ShellForOptionPane.this.setVisible(false);
                }
            }
        });
    }

    protected void performAction(Object object) {
    }
}

