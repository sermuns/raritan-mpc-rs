/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.ButtonModel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.border.AbstractBorder;

public class MenuItemBorder
extends AbstractBorder {
    private static final long serialVersionUID = -3899787709209973449L;
    protected Color menuTopLeft = new Color(57, 57, 57);
    protected Color menuBottomRight = new Color(255, 254, 253);
    protected Color menuItemTop = new Color(92, 86, 90);
    protected Color menuItemBottom = new Color(255, 255, 255);

    @Override
    public Insets getBorderInsets(Component component, Insets insets) {
        insets.top = 1;
        insets.left = 1;
        insets.bottom = 1;
        insets.right = 1;
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component component) {
        return new Insets(1, 1, 1, 1);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int n, int n2, int n3, int n4) {
        JMenuItem jMenuItem = (JMenuItem)component;
        ButtonModel buttonModel = jMenuItem.getModel();
        if (component.getParent() instanceof JMenuBar) {
            if (buttonModel.isSelected()) {
                graphics.setColor(this.menuTopLeft);
                graphics.drawLine(n, n2, n, n2 + n4);
                graphics.drawLine(n, n2, n + n3 - 2, n2);
                graphics.setColor(this.menuBottomRight);
                graphics.drawLine(n + 1, n2 + n4 - 1, n + n3, n2 + n4 - 1);
                graphics.drawLine(n + n3 - 1, n2, n + n3 - 1, n2 + n4);
            }
        } else if (buttonModel.isArmed() || buttonModel.isSelected()) {
            graphics.setColor(this.menuItemTop);
            graphics.drawLine(n, n2, n + n3, n2);
            graphics.setColor(this.menuItemBottom);
            graphics.drawLine(n, n2 + n4 - 1, n + n3, n2 + n4 - 1);
        }
    }
}

