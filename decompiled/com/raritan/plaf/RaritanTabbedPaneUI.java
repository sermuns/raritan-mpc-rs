/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import com.raritan.plaf.RaritanGradientDrawer;
import com.raritan.plaf.RaritanLookAndFeel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class RaritanTabbedPaneUI
extends BasicTabbedPaneUI {
    public static final int SHADOW_WIDTH = 4;
    protected Color bgFirst = new Color(236, 233, 216);
    protected Color bgSecond = new Color(186, 182, 171);
    protected Color shadowFrom = new Color(158, 155, 143);
    protected Color shadowTo = new Color(236, 233, 216);
    protected Color topHighlightLeft = new Color(62, 59, 54);
    protected Color topHighlightTop = new Color(147, 143, 132);
    protected Color topHighlightCorner = new Color(170, 167, 148);
    protected Color selTopHighlightLeft = new Color(159, 155, 146);
    protected Color selTopHighlightTop = new Color(199, 193, 193);
    protected Color selTopHighlightRight = new Color(47, 46, 42);
    protected Color topBorder = new Color(147, 143, 132);
    protected Color leftBorder = new Color(159, 155, 146);
    protected Color bottomBorderIn = new Color(66, 63, 58);
    protected Color bottomBorder = new Color(143, 143, 133);
    protected Color bottomBorderOut = new Color(204, 201, 182);
    protected Color rightBorderIn = new Color(66, 63, 58);
    protected Color rightBorder = new Color(143, 143, 133);
    protected Color rightBorderOut = new Color(204, 201, 182);

    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanTabbedPaneUI();
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
    protected void paintTab(Graphics graphics, int n, Rectangle[] rectangleArray, int n2, Rectangle rectangle, Rectangle rectangle2) {
        super.paintTab(graphics, n, rectangleArray, n2, rectangle, rectangle2);
        Rectangle rectangle3 = rectangleArray[n2];
        int n3 = this.tabPane.getSelectedIndex();
        if (this.lastTabInRun(this.tabPane.getTabCount(), this.selectedRun) != n3 && n2 == n3 + 1) {
            if (n == 1) {
                Rectangle rectangle4 = new Rectangle((int)rectangle3.getX(), (int)rectangle3.getY() + 1, 4, (int)rectangle3.getHeight());
                RaritanGradientDrawer.paintGradiendRect(graphics, rectangle4, this.shadowFrom, this.shadowTo, false);
            } else if (n == 3) {
                Rectangle rectangle5 = new Rectangle((int)rectangle3.getX(), (int)rectangle3.getY(), 4, (int)rectangle3.getHeight() - 2);
                RaritanGradientDrawer.paintGradiendRect(graphics, rectangle5, this.shadowFrom, this.shadowTo, false);
            } else if (n == 2) {
                Rectangle rectangle6 = new Rectangle((int)rectangle3.getX() + 1, (int)rectangle3.getY(), (int)rectangle3.getWidth(), 4);
                RaritanGradientDrawer.paintGradiendRect(graphics, rectangle6, this.shadowFrom, this.shadowTo, true);
            } else if (n == 4) {
                Rectangle rectangle7 = new Rectangle((int)rectangle3.getX(), (int)rectangle3.getY(), (int)rectangle3.getWidth() - 2, 4);
                RaritanGradientDrawer.paintGradiendRect(graphics, rectangle7, this.shadowFrom, this.shadowTo, true);
            }
        }
    }

    @Override
    protected void paintTabBackground(Graphics graphics, int n, int n2, int n3, int n4, int n5, int n6, boolean bl) {
        JComponent jComponent = (JComponent)this.tabPane.getComponent(n2);
        Color color = null;
        if (bl) {
            color = jComponent instanceof JScrollPane ? ((JScrollPane)jComponent).getViewport().getView().getBackground() : jComponent.getBackground();
        } else {
            graphics.setColor(this.tabPane.getBackgroundAt(n2));
        }
        graphics.setColor(color);
        switch (n) {
            case 2: {
                if (bl) {
                    graphics.fillRect(n3 + 1, n4 + 1, n5 - 2, n6 - 3);
                    break;
                }
                Rectangle rectangle = new Rectangle(n3 + 1, n4 + 1, n5 - 2, n6 - 3);
                RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, this.bgSecond, this.bgFirst, false);
                break;
            }
            case 4: {
                if (bl) {
                    graphics.fillRect(n3, n4 + 1, n5 - 2, n6 - 3);
                    break;
                }
                Rectangle rectangle = new Rectangle(n3, n4 + 1, n5 - 2, n6 - 3);
                RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, this.bgFirst, this.bgSecond, false);
                break;
            }
            case 3: {
                if (bl) {
                    graphics.fillRect(n3 + 1, n4, n5 - 3, n6 - 1);
                    break;
                }
                Rectangle rectangle = new Rectangle(n3 + 1, n4, n5 - 1, n6 - 1);
                RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, this.bgSecond, this.bgFirst, true);
                break;
            }
            default: {
                if (bl) {
                    graphics.fillRect(n3 + 1, n4 + 1, n5 - 2, n6 - 1);
                    break;
                }
                Rectangle rectangle = new Rectangle(n3 + 1, n4 + 1, n5 - 1, n6 - 1);
                RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, this.bgFirst, this.bgSecond, true);
            }
        }
    }

    @Override
    protected void paintTabBorder(Graphics graphics, int n, int n2, int n3, int n4, int n5, int n6, boolean bl) {
        int n7 = this.getRunForTab(this.tabPane.getTabCount(), n2);
        int n8 = this.lastTabInRun(this.tabPane.getTabCount(), n7);
        switch (n) {
            case 2: {
                throw new RuntimeException("Code not finished");
            }
            case 4: {
                throw new RuntimeException("Code not finished");
            }
            case 3: {
                if (bl) {
                    graphics.setColor(this.selTopHighlightLeft);
                    graphics.drawLine(n3, n4 + 1, n3, n4 + n6 - 2);
                    graphics.setColor(this.selTopHighlightTop);
                    graphics.drawLine(n3, n4 + n6 - 2, n3 + n5 - 1, n4 + n6 - 2);
                    graphics.setColor(this.selTopHighlightRight);
                    graphics.drawLine(n3 + n5 - 1, n4 + 1, n3 + n5 - 1, n4 + n6 - 3);
                    break;
                }
                graphics.setColor(this.topHighlightLeft);
                graphics.drawLine(n3, n4, n3, n4 + n6 - 2);
                graphics.setColor(this.topHighlightCorner);
                graphics.drawLine(n3, n4 + n6 - 2, n3 + 1, n4 + n6 - 2);
                graphics.setColor(this.topHighlightTop);
                graphics.drawLine(n3 + 2, n4 + n6 - 2, n3 + n5 - 2, n4 + n6 - 2);
                graphics.setColor(this.topHighlightCorner);
                graphics.drawLine(n3 + n5 - 1, n4 + n6 - 2, n3 + n5, n4 + n6 - 2);
                if (this.tabPane.getTabCount() != n2 + 1) break;
                graphics.setColor(this.topHighlightTop);
                graphics.drawLine(n3 + n5, n4, n3 + n5, n4 + n6 - 2);
                break;
            }
            default: {
                if (bl) {
                    graphics.setColor(this.selTopHighlightLeft);
                    graphics.drawLine(n3, n4 + 1, n3, n4 + n6);
                    graphics.setColor(this.selTopHighlightTop);
                    graphics.drawLine(n3, n4, n3 + n5 - 1, n4);
                    graphics.setColor(this.selTopHighlightRight);
                    graphics.drawLine(n3 + n5 - 1, n4 + 1, n3 + n5 - 1, n4 + n6);
                    break;
                }
                graphics.setColor(this.topHighlightLeft);
                graphics.drawLine(n3, n4 + 1, n3, n4 + n6);
                graphics.setColor(this.topHighlightCorner);
                graphics.drawLine(n3, n4, n3 + 1, n4);
                graphics.setColor(this.topHighlightTop);
                graphics.drawLine(n3 + 2, n4, n3 + n5 - 2, n4);
                graphics.setColor(this.topHighlightCorner);
                graphics.drawLine(n3 + n5 - 1, n4, n3 + n5, n4);
                if (n2 != n8) break;
                graphics.setColor(this.topHighlightTop);
                graphics.drawLine(n3 + n5, n4 + 1, n3 + n5, n4 + n6);
            }
        }
    }

    @Override
    protected void paintContentBorder(Graphics graphics, int n, int n2) {
        JComponent jComponent = (JComponent)this.tabPane.getComponent(n2);
        Color color = null;
        color = jComponent instanceof JScrollPane ? ((JScrollPane)jComponent).getViewport().getView().getBackground() : jComponent.getBackground();
        int n3 = this.tabPane.getWidth();
        int n4 = this.tabPane.getHeight();
        Insets insets = this.tabPane.getInsets();
        int n5 = insets.left;
        int n6 = insets.top;
        int n7 = n3 - insets.right - insets.left;
        int n8 = n4 - insets.top - insets.bottom;
        switch (n) {
            case 2: {
                n7 -= (n5 += this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth)) - insets.left;
                break;
            }
            case 4: {
                n7 -= this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth);
                break;
            }
            case 3: {
                n8 -= this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight);
                break;
            }
            default: {
                n8 -= (n6 += this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight)) - insets.top;
            }
        }
        graphics.setColor(color);
        graphics.fillRect(n5, n6, n7, n8);
        this.paintContentBorderTopEdge(graphics, n, n2, n5, n6, n7, n8);
        this.paintContentBorderLeftEdge(graphics, n, n2, n5, n6, n7, n8);
        this.paintContentBorderBottomEdge(graphics, n, n2, n5, n6, n7, n8);
        this.paintContentBorderRightEdge(graphics, n, n2, n5, n6, n7, n8);
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics graphics, int n, int n2, int n3, int n4, int n5, int n6) {
        Rectangle rectangle = n2 < 0 ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.topBorder);
        if (n != 1 || n2 < 0 || rectangle.y + rectangle.height + 1 < n4 || rectangle.x < n3 || rectangle.x > n3 + n5) {
            graphics.drawLine(n3, n4, n3 + n5 - 2, n4);
        } else {
            if (n2 != 0) {
                graphics.drawLine(n3, n4, rectangle.x, n4);
            }
            if (rectangle.x + rectangle.width < n3 + n5 - 2) {
                graphics.drawLine(rectangle.x + rectangle.width - 1, n4, n3 + n5 - 2, n4);
            } else {
                graphics.setColor(Color.GREEN);
                graphics.drawLine(n3 + n5 - 2, n4, n3 + n5 - 2, n4);
            }
        }
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics graphics, int n, int n2, int n3, int n4, int n5, int n6) {
        Rectangle rectangle = n2 < 0 ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.leftBorder);
        if (n != 2 || n2 < 0 || rectangle.x + rectangle.width + 1 < n3 || rectangle.y < n4 || rectangle.y > n4 + n6) {
            graphics.drawLine(n3, n4, n3, n4 + n6 - 2);
        }
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics graphics, int n, int n2, int n3, int n4, int n5, int n6) {
        Rectangle rectangle;
        Rectangle rectangle2 = rectangle = n2 < 0 ? null : this.getTabBounds(n2, this.calcRect);
        if (n != 3 || n2 < 0 || rectangle.y - 1 > n6 || rectangle.x < n3 || rectangle.x > n3 + n5) {
            graphics.setColor(this.bottomBorderIn);
            graphics.drawLine(n3, n4 + n6 - 2, n3 + n5 - 1, n4 + n6 - 2);
            graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
            graphics.drawLine(n3, n4 + n6 - 1, n3 + 1, n4 + n6 - 1);
            graphics.setColor(this.bottomBorder);
            graphics.drawLine(n3 + 1, n4 + n6 - 1, n3 + n5 - 1, n4 + n6 - 1);
            graphics.setColor(this.bottomBorderOut);
            graphics.drawLine(n3 + n5 - 2, n4 + n6 - 1, n3 + n5 - 1, n4 + n6 - 1);
            graphics.drawLine(n3 + 2, n4 + n6, n3 + n5 - 2, n4 + n6);
        } else {
            if (n2 != 0) {
                graphics.drawLine(n3, rectangle.y + 1, rectangle.x, rectangle.y + 1);
            }
            if (rectangle.x + rectangle.width < n3 + n5 - 2) {
                graphics.drawLine(rectangle.x + rectangle.width - 1, rectangle.y + 1, n3 + n5 - 2, rectangle.y + 1);
            } else {
                graphics.setColor(Color.GREEN);
                graphics.drawLine(n3 + n5 - 2, n4, n3 + n5 - 2, n4);
            }
        }
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics graphics, int n, int n2, int n3, int n4, int n5, int n6) {
        Rectangle rectangle;
        Rectangle rectangle2 = rectangle = n2 < 0 ? null : this.getTabBounds(n2, this.calcRect);
        if (n != 4 || n2 < 0 || rectangle.x - 1 > n5 || rectangle.y < n4 || rectangle.y > n4 + n6) {
            graphics.setColor(this.rightBorderIn);
            graphics.drawLine(n3 + n5 - 2, n4, n3 + n5 - 2, n4 + n6 - 2);
            graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
            graphics.drawLine(n3 + n5 - 1, n4, n3 + n5 - 1, n4 + 1);
            graphics.setColor(this.rightBorder);
            graphics.drawLine(n3 + n5 - 1, n4 + 1, n3 + n5 - 1, n4 + n6 - 1);
            graphics.setColor(this.rightBorderOut);
            graphics.drawLine(n3 + n5 - 1, n4 + n6 - 2, n3 + n5 - 1, n4 + n6 - 1);
            graphics.drawLine(n3 + n5, n4 + 2, n3 + n5, n4 + n6 - 2);
        }
    }
}

