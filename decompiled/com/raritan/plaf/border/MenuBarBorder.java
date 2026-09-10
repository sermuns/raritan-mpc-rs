/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

public class MenuBarBorder
extends AbstractBorder {
    private static final long serialVersionUID = 1586719947281261186L;
    private static final Color topOut = new Color(69, 65, 64);
    private static final Color topIn = new Color(218, 216, 201);
    private static final Color leftRight = new Color(152, 150, 151);
    private static final Color bottomOut = new Color(92, 86, 90);
    private static final Color bottomCenter = new Color(203, 202, 184);
    private static final Color bottomIn = new Color(207, 204, 187);

    @Override
    public Insets getBorderInsets(Component component, Insets insets) {
        insets.top = 2;
        insets.left = 1;
        insets.bottom = 3;
        insets.right = 1;
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component component) {
        return new Insets(2, 1, 3, 1);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int n, int n2, int n3, int n4) {
        Color color = graphics.getColor();
        graphics.setColor(topOut);
        graphics.drawLine(n, n2, n + n3, n2);
        graphics.setColor(topIn);
        graphics.drawLine(n + 1, n2 + 1, n + n3 - 2, n2 + 1);
        graphics.setColor(leftRight);
        graphics.drawLine(n, n2 + 1, n, n2 + n4 - 2);
        graphics.drawLine(n + n3 - 1, n2 + 1, n + n3 - 1, n2 + n4 - 2);
        graphics.setColor(bottomOut);
        graphics.drawLine(n, n2 + n4 - 1, n + n3, n2 + n4 - 1);
        graphics.setColor(bottomCenter);
        graphics.drawLine(n + 1, n2 + n4 - 2, n + n3 - 2, n2 + n4 - 2);
        graphics.setColor(bottomIn);
        graphics.drawLine(n + 1, n2 + n4 - 3, n + n3 - 2, n2 + n4 - 3);
        graphics.setColor(color);
    }
}

