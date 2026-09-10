/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import com.raritan.plaf.RaritanLookAndFeel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

public class RaritanMenuUI
extends BasicMenuUI
implements MouseListener {
    protected Color toolBarBorderLight = new Color(255, 254, 255);
    protected Color toolBarBorderDark = new Color(55, 53, 54);

    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanMenuUI();
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
        super.paint(graphics, jComponent);
    }

    @Override
    protected void paintBackground(Graphics graphics, JMenuItem jMenuItem, Color color) {
        ButtonModel buttonModel = this.menuItem.getModel();
        JComponent jComponent = (JComponent)jMenuItem.getParent();
        if (buttonModel.isSelected()) {
            graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
            graphics.fillRect(0, 0, jMenuItem.getWidth(), jMenuItem.getHeight());
            super.paintBackground(graphics, jMenuItem, color);
        } else if (jMenuItem.getClientProperty("mouseIn") != null && jComponent instanceof JMenuBar) {
            graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
            graphics.fillRect(1, 1, jMenuItem.getWidth(), jMenuItem.getHeight() - 2);
            graphics.setColor(this.toolBarBorderLight);
            graphics.drawLine(1, 1, jMenuItem.getWidth(), 1);
            graphics.drawLine(1, 1, 1, jMenuItem.getHeight() - 2);
            graphics.setColor(this.toolBarBorderDark);
            graphics.drawLine(1, jMenuItem.getHeight() - 2, jMenuItem.getWidth(), jMenuItem.getHeight() - 2);
            graphics.drawLine(jMenuItem.getWidth() - 1, 1, jMenuItem.getWidth() - 1, jMenuItem.getHeight() - 2);
        } else {
            super.paintBackground(graphics, jMenuItem, color);
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

