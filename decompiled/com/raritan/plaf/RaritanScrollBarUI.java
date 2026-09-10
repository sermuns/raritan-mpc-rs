/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import com.raritan.plaf.RaritanLookAndFeel;
import com.raritan.plaf.RaritanScrollButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class RaritanScrollBarUI
extends MetalScrollBarUI {
    protected Color bottomBorderIn = new Color(121, 120, 116);
    protected Color bottomBorderOut = new Color(181, 181, 181);
    protected Color rightBorderIn = new Color(121, 120, 116);
    protected Color rightBorderOut = new Color(212, 210, 211);
    protected Color leftBorder = new Color(178, 178, 178);
    protected Color dotColor = new Color(173, 167, 153);

    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanScrollBarUI();
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
        super.paint(graphics, jComponent);
    }

    @Override
    protected void paintThumb(Graphics graphics, JComponent jComponent, Rectangle rectangle) {
        if (!jComponent.isEnabled()) {
            return;
        }
        graphics.translate(rectangle.x, rectangle.y);
        if (this.scrollbar.getOrientation() == 1) {
            int n;
            graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
            graphics.fillRect(2, 0, rectangle.width - 4, rectangle.height - 2);
            graphics.setColor(this.bottomBorderIn);
            graphics.drawLine(2, rectangle.height - 2, rectangle.width - 3, rectangle.height - 2);
            graphics.setColor(this.bottomBorderOut);
            graphics.drawLine(2, rectangle.height - 1, rectangle.width - 2, rectangle.height - 1);
            graphics.setColor(this.rightBorderIn);
            graphics.drawLine(rectangle.width - 2, 0, rectangle.width - 2, rectangle.height - 2);
            graphics.setColor(this.rightBorderOut);
            graphics.drawLine(rectangle.width - 1, 1, rectangle.width - 1, rectangle.height - 2);
            graphics.setColor(this.dotColor);
            int n2 = rectangle.width / 2 + 2;
            int n3 = rectangle.height / 2;
            int n4 = rectangle.width / 4 - 1;
            for (int i = n = rectangle.height / 4; i < n + n3; i += 2) {
                for (int j = n4; j < n4 + n2; j += 2) {
                    graphics.drawLine(j, i, j, i);
                }
            }
        } else {
            int n;
            graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
            graphics.fillRect(0, 2, rectangle.width - 2, rectangle.height - 4);
            graphics.setColor(this.bottomBorderIn);
            graphics.drawLine(0, rectangle.height - 2, rectangle.width - 2, rectangle.height - 2);
            graphics.setColor(this.bottomBorderOut);
            graphics.drawLine(0, rectangle.height - 1, rectangle.width - 2, rectangle.height - 1);
            graphics.setColor(this.rightBorderIn);
            graphics.drawLine(rectangle.width - 2, 2, rectangle.width - 2, rectangle.height - 2);
            graphics.setColor(this.rightBorderOut);
            graphics.drawLine(rectangle.width - 1, 3, rectangle.width - 1, rectangle.height - 2);
            graphics.setColor(this.dotColor);
            int n5 = rectangle.width / 2;
            int n6 = rectangle.height / 2 + 2;
            int n7 = rectangle.width / 4;
            for (int i = n = rectangle.height / 4 - 1; i < n + n6; i += 2) {
                for (int j = n7; j < n7 + n5; j += 2) {
                    graphics.drawLine(j, i, j, i);
                }
            }
        }
        graphics.translate(-rectangle.x, -rectangle.y);
    }

    @Override
    protected void paintTrack(Graphics graphics, JComponent jComponent, Rectangle rectangle) {
        graphics.translate(rectangle.x, rectangle.y);
        if (this.scrollbar.getOrientation() == 1) {
            if (jComponent.isEnabled()) {
                graphics.setColor(this.leftBorder);
                graphics.drawLine(0, 0, 0, rectangle.height - 1);
            }
        } else if (jComponent.isEnabled()) {
            graphics.setColor(this.leftBorder);
            graphics.drawLine(0, 0, rectangle.width - 1, 0);
        }
        graphics.translate(-rectangle.x, -rectangle.y);
    }

    @Override
    protected JButton createDecreaseButton(int n) {
        return new RaritanScrollButton(n);
    }

    @Override
    protected JButton createIncreaseButton(int n) {
        return new RaritanScrollButton(n);
    }
}

