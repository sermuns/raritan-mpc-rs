/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.ui.pan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;
import javax.swing.border.BevelBorder;

public class PanningBorder
extends BevelBorder {
    private int width;
    private int pressedMask;
    private static final int[] directions = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
    private Hashtable<Integer, Rectangle[]> positions;
    private Hashtable<Integer, Point[]> arrows;

    public PanningBorder(int n, int n2) {
        super(1);
        this.width = n;
        this.pressedMask = n2;
        this.positions = new Hashtable();
        this.arrows = new Hashtable();
        this.createDirection(1, 1);
        this.createDirection(2, 2);
        this.createDirection(4, 1);
        this.createDirection(8, 2);
        this.createDirection(16, 1);
        this.createDirection(32, 2);
        this.createDirection(64, 1);
        this.createDirection(128, 2);
    }

    private void createDirection(int n, int n2) {
        Rectangle[] rectangleArray = new Rectangle[n2];
        for (int i = 0; i < n2; ++i) {
            rectangleArray[i] = new Rectangle();
        }
        this.positions.put(n, rectangleArray);
        Point[] pointArray = new Point[6];
        for (int i = 0; i < pointArray.length; ++i) {
            pointArray[i] = new Point();
        }
        this.arrows.put(n, pointArray);
    }

    public boolean pointInDirection(Point point, int n) {
        Rectangle[] rectangleArray = this.positions.get(n);
        if (rectangleArray == null) {
            return false;
        }
        for (Rectangle rectangle : rectangleArray) {
            if (!rectangle.contains(point)) continue;
            return true;
        }
        return false;
    }

    public int getDirectionFromPoint(Point point) {
        int n = 0;
        for (int n2 : directions) {
            if (!this.pointInDirection(point, n2)) continue;
            n |= n2;
        }
        return n;
    }

    private void calculatePositions(int n, int n2, int n3, int n4) {
        int n5 = n3 / 5;
        int n6 = n4 / 5;
        this.addPosition(1, 0, n + n5, n2, n3 - 2 * n5, this.width + 4);
        this.addPosition(2, 0, n + n3 - n5, n2, n5, this.width + 4);
        this.addPosition(2, 1, n + n3 - this.width - 4, n2, this.width + 4, n6);
        this.addPosition(4, 0, n + n3 - this.width - 4, n2 + n6, this.width + 4, n4 - 2 * n6);
        this.addPosition(8, 0, n + n3 - this.width - 4, n2 + n4 - n6, this.width + 4, n6);
        this.addPosition(8, 1, n + n3 - n5, n2 + n4 - this.width - 4, n5, this.width + 4);
        this.addPosition(16, 0, n + n5, n2 + n4 - this.width - 4, n3 - 2 * n5, this.width + 4);
        this.addPosition(32, 0, 0, n2 + n4 - this.width - 4, n5, this.width + 4);
        this.addPosition(32, 1, 0, n2 + n4 - n6, this.width + 4, n6);
        this.addPosition(64, 0, 0, n2 + n6, this.width + 4, n4 - 2 * n6);
        this.addPosition(128, 0, 0, 0, n5, this.width + 4);
        this.addPosition(128, 1, 0, 0, this.width + 4, n6);
    }

    private void addPosition(int n, int n2, int n3, int n4, int n5, int n6) {
        Rectangle[] rectangleArray = this.positions.get(n);
        assert (rectangleArray != null && rectangleArray.length > n2);
        rectangleArray[n2].x = n3;
        rectangleArray[n2].y = n4;
        rectangleArray[n2].width = n5;
        rectangleArray[n2].height = n6;
    }

    private void calculateArrows(int n, int n2, int n3, int n4) {
        this.addArrow(1, new Point(n3 / 2, 3), -1, 1, 1, 1, 0, 1);
        this.addArrow(2, new Point(n3 - 4 - this.width / 2, 2 + this.width / 2), -1, 0, 0, 1, 1, -1);
        this.addArrow(4, new Point(n3 - 5, n4 / 2), -1, -1, -1, 1, -1, 0);
        this.addArrow(8, new Point(n3 - 4 - this.width / 2, n4 - 4 - this.width / 2), -1, 0, 0, -1, 1, 1);
        this.addArrow(16, new Point(n3 / 2, n4 - 4), -1, -1, 1, -1, 0, -1);
        this.addArrow(32, new Point(3 + this.width / 2, n4 - 4 - this.width / 2), 1, 0, 0, -1, -1, 1);
        this.addArrow(64, new Point(3, n4 / 2), 1, -1, 1, 1, 1, 0);
        this.addArrow(128, new Point(3 + this.width / 2, 2 + this.width / 2), 1, 0, 0, 1, -1, -1);
    }

    private void addArrow(int n, Point point, int n2, int n3, int n4, int n5, int n6, int n7) {
        Point[] pointArray = this.arrows.get(n);
        if (pointArray == null) {
            return;
        }
        if (this.width < 2) {
            return;
        }
        pointArray[0] = point;
        pointArray[1] = new Point(point.x + (n2 *= this.width - 4), point.y + (n3 *= this.width - 4));
        pointArray[2] = new Point(point.x + (n4 *= this.width - 4), point.y + (n5 *= this.width - 4));
        pointArray[3] = new Point(pointArray[0].x + n6, pointArray[0].y + n7);
        pointArray[4] = new Point(pointArray[1].x + n6, pointArray[1].y + n7);
        pointArray[5] = new Point(pointArray[2].x + n6, pointArray[2].y + n7);
    }

    private void paintDirection(int n, Component component, Graphics graphics) {
        Point[] pointArray;
        int n2;
        Rectangle[] rectangleArray = this.positions.get(n);
        if (rectangleArray == null || rectangleArray.length == 0 || rectangleArray.length > 2) {
            return;
        }
        Color color = component.getBackground().darker();
        Color color2 = component.getBackground();
        boolean bl = (this.pressedMask & n) != 0;
        graphics.setColor(bl ? color : color2);
        for (n2 = 0; n2 < rectangleArray.length; ++n2) {
            graphics.fillRect(rectangleArray[n2].x, rectangleArray[n2].y, rectangleArray[n2].width, rectangleArray[n2].height);
        }
        for (n2 = 0; n2 < rectangleArray.length; ++n2) {
            if (bl) {
                this.paintLoweredBevel(component, graphics, rectangleArray[n2].x, rectangleArray[n2].y, rectangleArray[n2].width, rectangleArray[n2].height);
                continue;
            }
            this.paintRaisedBevel(component, graphics, rectangleArray[n2].x, rectangleArray[n2].y, rectangleArray[n2].width, rectangleArray[n2].height);
        }
        graphics.setColor(bl ? color : color2);
        for (n2 = 0; n2 < rectangleArray.length; ++n2) {
            for (int i = 0; i < rectangleArray.length; ++i) {
                Rectangle rectangle;
                if (n2 == i || (rectangle = rectangleArray[n2].intersection(rectangleArray[i])).isEmpty()) continue;
                graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }
        if (rectangleArray.length == 2) {
            Rectangle rectangle = rectangleArray[0].union(rectangleArray[1]);
            if (bl) {
                this.paintLoweredBevel(component, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            } else {
                this.paintRaisedBevel(component, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }
        if ((pointArray = this.arrows.get(n)) == null || pointArray.length != 6) {
            return;
        }
        graphics.setColor(Color.BLACK);
        graphics.drawLine(pointArray[0].x, pointArray[0].y, pointArray[1].x, pointArray[1].y);
        graphics.drawLine(pointArray[0].x, pointArray[0].y, pointArray[2].x, pointArray[2].y);
        graphics.setColor(Color.BLACK);
        graphics.drawLine(pointArray[3].x, pointArray[3].y, pointArray[4].x, pointArray[4].y);
        graphics.drawLine(pointArray[3].x, pointArray[3].y, pointArray[5].x, pointArray[5].y);
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int n, int n2, int n3, int n4) {
        this.calculatePositions(n, n2, n3, n4);
        this.calculateArrows(n, n2, n3, n4);
        Color color = graphics.getColor();
        for (int n5 : directions) {
            this.paintDirection(n5, component, graphics);
        }
        graphics.setColor(color);
    }

    @Override
    public Insets getBorderInsets(Component component) {
        int n = this.width + 4;
        return new Insets(n, n, n, n);
    }

    @Override
    public Insets getBorderInsets(Component component, Insets insets) {
        insets.bottom = insets.top = this.width + 4;
        insets.right = insets.top;
        insets.left = insets.top;
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}

