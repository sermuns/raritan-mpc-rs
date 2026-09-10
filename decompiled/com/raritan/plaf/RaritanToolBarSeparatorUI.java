/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

public class RaritanToolBarSeparatorUI
extends BasicToolBarSeparatorUI {
    protected Color colorLeft = new Color(147, 145, 130);
    protected Color colorRight = new Color(255, 254, 251);

    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanToolBarSeparatorUI();
    }

    @Override
    public void installUI(JComponent jComponent) {
        super.installUI(jComponent);
    }

    @Override
    public void uninstallUI(JComponent jComponent) {
        super.uninstallUI(jComponent);
    }

    @Override
    public void paint(Graphics graphics, JComponent jComponent) {
        graphics.setColor(this.colorLeft);
        graphics.drawLine(0, 0, 0, jComponent.getHeight() - 2);
        graphics.setColor(this.colorRight);
        graphics.drawLine(1, 1, 1, jComponent.getHeight() - 1);
    }

    @Override
    public Dimension getMaximumSize(JComponent jComponent) {
        return super.getMinimumSize(jComponent);
    }

    @Override
    public Dimension getMinimumSize(JComponent jComponent) {
        return super.getMaximumSize(jComponent);
    }

    @Override
    public Dimension getPreferredSize(JComponent jComponent) {
        return new Dimension(18, 2);
    }
}

