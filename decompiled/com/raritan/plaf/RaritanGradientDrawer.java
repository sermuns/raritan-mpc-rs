/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;

public class RaritanGradientDrawer {
    public static void paintGradienLine(Graphics graphics, Point point, Point point2) {
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.drawLine(point.x, point.y, point2.x, point2.y);
    }

    public static void paintGradiendRect(Graphics graphics, Rectangle rectangle, Color color, Color color2, boolean bl) {
        Graphics2D graphics2D = (Graphics2D)graphics;
        Point point = null;
        Point point2 = null;
        if (bl) {
            point = new Point(rectangle.width / 2, rectangle.y);
            point2 = new Point(rectangle.width / 2, rectangle.y + rectangle.height);
        } else {
            point = new Point(rectangle.x, rectangle.height / 2);
            point2 = new Point(rectangle.x + rectangle.width, rectangle.height / 2);
        }
        GradientPaint gradientPaint = new GradientPaint(point, color, point2, color2);
        graphics2D.setPaint(gradientPaint);
        graphics2D.fill(rectangle);
    }

    public static void paintDefaultGradient(Graphics graphics, JComponent jComponent, Color color, Color color2) {
        Rectangle rectangle = null;
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = 0;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, color, color2, true);
    }

    public static void paintDefaultGradientDual(Graphics graphics, JComponent jComponent, Color color, Color color2) {
        Rectangle rectangle = null;
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = 0;
        rectangle.height = rectangle.height / 3 * 2;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, color, color2, true);
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = rectangle.height / 3 * 2;
        rectangle.height /= 3;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, color2, color, true);
    }

    public static void paintDefaultGradientDual(Graphics graphics, JComponent jComponent, Color color, Color color2, Color color3) {
        Rectangle rectangle = null;
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = 0;
        rectangle.height = rectangle.height / 3 * 2;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, color, color2, true);
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = rectangle.height / 3 * 2;
        rectangle.height /= 3;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, color2, color3, true);
    }

    public static void paintDefaultMenuBarGradient(Graphics graphics, JComponent jComponent, Color color, Color color2, Color color3) {
        Rectangle rectangle = null;
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = 0;
        rectangle.height /= 2;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, color, color2, true);
        rectangle = jComponent.getBounds();
        rectangle.x = 0;
        rectangle.y = rectangle.height / 2;
        rectangle.height /= 2;
        RaritanGradientDrawer.paintGradiendRect(graphics, rectangle, color2, color3, true);
    }
}

