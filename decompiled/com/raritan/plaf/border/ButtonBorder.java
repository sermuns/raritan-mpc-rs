/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

public class ButtonBorder
extends AbstractBorder {
    private static final long serialVersionUID = -1718538469699834454L;
    private static final Color TOP_COLOR = new Color(182, 178, 175);
    private static final Color LEFT_COLOR = new Color(139, 134, 131);
    private static final Color RIGHT_IN_COLOR = new Color(48, 44, 41);
    private static final Color RIGHT_OUT_COLOR = new Color(193, 190, 175);
    private static final Color BOTTON_IN_COLOR = new Color(29, 25, 24);
    private static final Color BOTTON_OUT_COLOR = new Color(166, 167, 153);

    @Override
    public Insets getBorderInsets(Component component, Insets insets) {
        insets.left = 3;
        insets.top = 3;
        insets.right = 4;
        insets.bottom = 4;
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component component) {
        return new Insets(3, 3, 4, 4);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int n, int n2, int n3, int n4) {
        Color color = graphics.getColor();
        graphics.setColor(TOP_COLOR);
        graphics.drawLine(n, n2, n + n3 - 2, n2);
        graphics.setColor(LEFT_COLOR);
        graphics.drawLine(n, n2 + 1, n, n2 + n4 - 2);
        graphics.setColor(RIGHT_IN_COLOR);
        graphics.drawLine(n + n3 - 2, n2 + 1, n + n3 - 2, n2 + n4 - 2);
        graphics.setColor(RIGHT_OUT_COLOR);
        graphics.drawLine(n + n3 - 1, n2 + 2, n + n3 - 1, n2 + n4 - 2);
        graphics.setColor(BOTTON_IN_COLOR);
        graphics.drawLine(n + 1, n2 + n4 - 2, n + n3 - 2, n2 + n4 - 2);
        graphics.setColor(BOTTON_OUT_COLOR);
        graphics.drawLine(n + 2, n2 + n4 - 1, n + n3 - 2, n2 + n4 - 1);
        graphics.setColor(color);
    }
}

