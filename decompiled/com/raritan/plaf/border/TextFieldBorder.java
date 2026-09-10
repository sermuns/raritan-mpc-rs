/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf.border;

import com.raritan.plaf.RaritanLookAndFeel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

public class TextFieldBorder
extends AbstractBorder {
    private static final long serialVersionUID = -4241409721695177628L;
    protected Color colorTop = new Color(62, 59, 52);
    protected Color colorLeft = new Color(67, 64, 59);
    protected Color colorBottom = new Color(245, 241, 229);
    protected Color colorRight = new Color(239, 237, 224);

    @Override
    public Insets getBorderInsets(Component component, Insets insets) {
        insets.right = 1;
        insets.bottom = 1;
        insets.left = 1;
        insets.top = 1;
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
        Color color = graphics.getColor();
        graphics.setColor(this.colorTop);
        graphics.drawLine(n, n2, n + n3, n2);
        graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
        graphics.drawLine(n, n2 + n4 - 1, n + 2, n2 + n4 - 1);
        graphics.setColor(this.colorLeft);
        graphics.drawLine(n, n2 + 1, n, n2 + n4 - 2);
        graphics.setColor(this.colorBottom);
        graphics.drawLine(n + 2, n2 + n4 - 1, n + n3 - 2, n2 + n4 - 1);
        graphics.setColor(this.colorRight);
        graphics.drawLine(n + n3 - 1, n2 + 1, n + n3 - 1, n2 + n4 - 1);
        graphics.setColor(color);
    }
}

