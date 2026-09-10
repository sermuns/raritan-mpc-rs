/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class RaritanMenuItemUI
extends BasicMenuItemUI
implements MouseInputListener,
MenuKeyListener {
    private MenuKeyListener basicMenuKeyListener = null;

    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanMenuItemUI();
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
    protected MouseInputListener createMouseInputListener(JComponent jComponent) {
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
        int n = mouseEvent.getModifiers();
        if ((n & 0x1C) != 0) {
            MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
        } else {
            menuSelectionManager.setSelectedPath(this.getPath());
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
        int n = mouseEvent.getModifiers();
        if ((n & 0x1C) != 0) {
            MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
        } else {
            MenuElement[] menuElementArray = menuSelectionManager.getSelectedPath();
            if (menuElementArray.length > 1 && menuElementArray[menuElementArray.length - 1] == this.menuItem) {
                MenuElement[] menuElementArray2 = new MenuElement[menuElementArray.length - 1];
                int n2 = menuElementArray.length - 1;
                for (int i = 0; i < n2; ++i) {
                    menuElementArray2[i] = menuElementArray[i];
                }
                menuSelectionManager.setSelectedPath(menuElementArray2);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (this.menuItem.isEnabled()) {
            MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
            Point point = mouseEvent.getPoint();
            if (point.x >= 0 && point.x < this.menuItem.getWidth() && point.y >= 0 && point.y < this.menuItem.getHeight()) {
                this.doClick(menuSelectionManager);
            } else {
                menuSelectionManager.processMouseEvent(mouseEvent);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
    }

    @Override
    protected MenuKeyListener createMenuKeyListener(JComponent jComponent) {
        this.basicMenuKeyListener = super.createMenuKeyListener(jComponent);
        return this;
    }

    @Override
    public void menuKeyPressed(MenuKeyEvent menuKeyEvent) {
        MenuElement[] menuElementArray = MenuSelectionManager.defaultManager().getSelectedPath();
        if (menuElementArray.length == 1 && (menuKeyEvent.getKeyCode() == 38 || menuKeyEvent.getKeyCode() == 224 || menuKeyEvent.getKeyCode() == 40 || menuKeyEvent.getKeyCode() == 225)) {
            MenuElement menuElement;
            boolean bl = menuKeyEvent.getKeyCode() == 38 || menuKeyEvent.getKeyCode() == 224;
            MenuElement[] menuElementArray2 = menuElementArray[0].getSubElements();
            if (menuElementArray2 != null && (menuElement = this.getNextElement(menuElementArray2, bl)) != null) {
                MenuElement[] menuElementArray3 = new MenuElement[]{menuElementArray[0], menuElement};
                MenuSelectionManager.defaultManager().setSelectedPath(menuElementArray3);
                menuKeyEvent.consume();
            }
        }
        if (!menuKeyEvent.isConsumed() && this.basicMenuKeyListener != null) {
            this.basicMenuKeyListener.menuKeyPressed(menuKeyEvent);
        }
    }

    @Override
    public void menuKeyReleased(MenuKeyEvent menuKeyEvent) {
        if (this.basicMenuKeyListener != null) {
            this.basicMenuKeyListener.menuKeyReleased(menuKeyEvent);
        }
    }

    @Override
    public void menuKeyTyped(MenuKeyEvent menuKeyEvent) {
        if (this.basicMenuKeyListener != null) {
            this.basicMenuKeyListener.menuKeyTyped(menuKeyEvent);
        }
    }

    private MenuElement getNextElement(MenuElement[] menuElementArray, boolean bl) {
        MenuElement menuElement = null;
        int n = menuElementArray.length;
        int n2 = 0;
        int n3 = 1;
        if (bl) {
            n2 = menuElementArray.length - 1;
            n3 = -1;
        }
        int n4 = n2;
        int n5 = 0;
        while (n5 < n) {
            if (menuElementArray[n4].getComponent().isEnabled()) {
                menuElement = menuElementArray[n4];
                break;
            }
            n4 += n3;
        }
        return menuElement;
    }
}

