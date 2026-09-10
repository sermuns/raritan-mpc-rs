/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.displays;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ShellComponentAdapter
extends ComponentAdapter {
    private int shellX;
    private int shellY;
    private int shellXW;
    private int shellYH;
    private int shellWidth = 150;
    private int shellHeight = 150;
    private Component parent;
    private Rectangle rv = new Rectangle(100, 100, 400, 400);
    private Point point = new Point(100, 100);

    public void initMe(Component component) {
        this.shellX = component.getX();
        this.shellY = component.getY();
        this.shellWidth = component.getWidth();
        this.shellHeight = component.getHeight();
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {
        Component component = componentEvent.getComponent();
        this.shellX = component.getX();
        this.shellY = component.getY();
        this.permitMove(component);
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        Component component = componentEvent.getComponent();
        this.shellWidth = component.getWidth();
        this.shellHeight = component.getHeight();
        this.permitMove(component);
    }

    public void setFloatingParent(Component component) {
        this.parent = component;
    }

    private void permitMove(Component component) {
        if (this.parent.isShowing()) {
            this.point = this.parent.getLocationOnScreen();
            this.rv = this.parent.getBounds();
            int n = this.rv.width + this.point.x;
            int n2 = this.rv.height + this.point.y;
            this.shellXW = this.shellX + this.shellWidth;
            this.shellYH = this.shellY + this.shellHeight;
            if (this.shellWidth > this.rv.width) {
                this.shellWidth = this.rv.width;
            }
            if (this.shellHeight > this.rv.height) {
                this.shellHeight = this.rv.height;
            }
            if (this.shellX < this.point.x) {
                this.shellX = this.point.x;
            }
            if (this.shellXW > n) {
                this.shellX = n - this.shellWidth;
            }
            if (this.shellY < this.point.y) {
                this.shellY = this.point.y;
            }
            if (this.shellYH > n2) {
                this.shellY = n2 - this.shellHeight;
            }
            component.removeComponentListener(this);
            component.setBounds(this.shellX, this.shellY, this.shellWidth, this.shellHeight);
            component.addComponentListener(this);
        }
    }
}

