/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import com.raritan.plaf.RaritanGradientDrawer;
import com.raritan.plaf.RaritanLookAndFeel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.plaf.metal.MetalComboBoxButton;

public class RaritanButtonUI
extends MetalButtonUI
implements MouseListener {
    protected Border border = null;
    protected final Color colorTopUp = new Color(227, 225, 202);
    protected final Color colorTopDown = new Color(219, 216, 199);
    protected Color colorCenterUp = new Color(216, 213, 196);
    protected Color colorCenterDown = new Color(205, 202, 185);
    protected Color colorBottomUp = new Color(203, 200, 183);
    protected Color colorBottomDown = new Color(201, 198, 181);
    protected Color bgFirstOver = new Color(222, 220, 197);
    protected Color bgSecondOver = new Color(180, 177, 162);
    protected Color bgFirstPressedBack = new Color(219, 217, 202);
    protected Color bgSecondPressedBack = new Color(149, 148, 130);
    protected Color bgPressedBackLine = new Color(211, 210, 192);
    protected Color bgFirstPressed = new Color(180, 176, 164);
    protected Color bgSecondPressed = new Color(227, 222, 200);
    protected Color toolBarBorderLight = new Color(255, 254, 255);
    protected Color toolBarBorderDark = new Color(55, 53, 54);

    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanButtonUI();
    }

    @Override
    public void installUI(JComponent jComponent) {
        super.installUI(jComponent);
        jComponent.addMouseListener(this);
    }

    @Override
    public void update(Graphics graphics, JComponent jComponent) {
        super.update(graphics, jComponent);
    }

    @Override
    public void uninstallUI(JComponent jComponent) {
        super.uninstallUI(jComponent);
        jComponent.removeMouseListener(this);
    }

    @Override
    public void paint(Graphics graphics, JComponent jComponent) {
        if (!(jComponent instanceof MetalComboBoxButton)) {
            if (jComponent.getParent() instanceof JToolBar) {
                if (jComponent.getClientProperty("mouseIn") != null) {
                    graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
                    graphics.fillRect(0, 0, jComponent.getWidth(), jComponent.getHeight());
                    graphics.setColor(this.toolBarBorderLight);
                    graphics.drawLine(0, 0, jComponent.getWidth(), 0);
                    graphics.drawLine(0, 0, 0, jComponent.getHeight() - 2);
                    graphics.setColor(this.toolBarBorderDark);
                    graphics.drawLine(1, jComponent.getHeight() - 1, jComponent.getWidth(), jComponent.getHeight() - 1);
                    graphics.drawLine(jComponent.getWidth() - 1, 0, jComponent.getWidth() - 1, jComponent.getHeight());
                }
            } else if (jComponent.isOpaque()) {
                if (jComponent.getClientProperty("mouseIn") == null) {
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
                } else {
                    RaritanGradientDrawer.paintDefaultGradient(graphics, jComponent, this.bgFirstOver, this.bgSecondOver);
                }
            }
        }
        super.paint(graphics, jComponent);
    }

    @Override
    protected void paintFocus(Graphics graphics, AbstractButton abstractButton, Rectangle rectangle, Rectangle rectangle2, Rectangle rectangle3) {
        super.paintFocus(graphics, abstractButton, rectangle, rectangle2, rectangle3);
    }

    @Override
    protected void paintButtonPressed(Graphics graphics, AbstractButton abstractButton) {
        if (!(abstractButton instanceof MetalComboBoxButton)) {
            if (abstractButton.getParent() instanceof JToolBar) {
                graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
                graphics.fillRect(0, 0, abstractButton.getWidth(), abstractButton.getHeight());
                graphics.setColor(this.toolBarBorderDark);
                graphics.drawLine(0, 0, abstractButton.getWidth() - 2, 0);
                graphics.drawLine(0, 0, 0, abstractButton.getHeight());
                graphics.setColor(this.toolBarBorderLight);
                graphics.drawLine(1, abstractButton.getHeight() - 1, abstractButton.getWidth(), abstractButton.getHeight() - 1);
                graphics.drawLine(abstractButton.getWidth() - 1, 1, abstractButton.getWidth() - 1, abstractButton.getHeight());
            } else {
                Rectangle rectangle = null;
                RaritanGradientDrawer.paintDefaultGradient(graphics, abstractButton, this.bgFirstPressedBack, this.bgSecondPressedBack);
                graphics.setColor(this.bgPressedBackLine);
                graphics.drawLine(0, abstractButton.getHeight() - 3, abstractButton.getWidth(), abstractButton.getHeight() - 3);
                rectangle = abstractButton.getBounds();
                rectangle.x = 2;
                rectangle.y = 2;
                rectangle.width = abstractButton.getWidth() - 6;
                rectangle.height = abstractButton.getHeight() - 5;
                RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, this.bgFirstPressed, this.bgSecondPressed, true);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        JComponent jComponent = (JComponent)mouseEvent.getComponent();
        jComponent.putClientProperty("mouseIn", "TRUE");
        jComponent.repaint();
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        JComponent jComponent = (JComponent)mouseEvent.getComponent();
        jComponent.putClientProperty("mouseIn", null);
        jComponent.repaint();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }
}

