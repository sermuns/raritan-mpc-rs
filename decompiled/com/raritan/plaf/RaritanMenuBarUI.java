/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import com.raritan.plaf.RaritanGradientDrawer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class RaritanMenuBarUI
extends BasicMenuBarUI {
    protected Color colorTopUp = new Color(227, 225, 202);
    protected Color colorTopDown = new Color(219, 216, 199);
    protected Color colorCenterUp = new Color(216, 213, 196);
    protected Color colorCenterDown = new Color(205, 202, 185);
    protected Color colorBottomUp = new Color(203, 200, 183);
    protected Color colorBottomDown = new Color(201, 198, 181);

    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanMenuBarUI();
    }

    @Override
    public void installUI(JComponent jComponent) {
        super.installUI(jComponent);
    }

    @Override
    public void update(Graphics graphics, JComponent jComponent) {
        super.update(graphics, jComponent);
    }

    @Override
    public void uninstallUI(JComponent jComponent) {
        super.uninstallUI(jComponent);
    }

    @Override
    public void paint(Graphics graphics, JComponent jComponent) {
        Rectangle rectangle = null;
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = 0;
        rectangle.height /= 3;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, this.colorTopUp, this.colorTopDown, true);
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = rectangle.height / 3;
        rectangle.height /= 3;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, this.colorCenterUp, this.colorCenterDown, true);
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = rectangle.height / 3 * 2;
        rectangle.height /= 3;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, this.colorBottomUp, this.colorBottomDown, true);
    }
}

