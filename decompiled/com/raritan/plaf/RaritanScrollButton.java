/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import com.raritan.plaf.RaritanLookAndFeel;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.plaf.basic.BasicArrowButton;

public class RaritanScrollButton
extends BasicArrowButton {
    private static final long serialVersionUID = 1691608421562200739L;
    protected Color leftBorder = new Color(178, 178, 178);
    protected Color bottomBorderIn = new Color(121, 120, 116);
    protected Color bottomBorderOut = new Color(181, 180, 185);
    protected Color rightBorderIn = new Color(121, 120, 116);
    protected Color rightBorderOut = new Color(212, 210, 213);
    protected Color upLeftBorderPressed = new Color(72, 71, 69);
    protected Color arrowColor = Color.BLACK;
    protected int arrowHeight = 3;

    public RaritanScrollButton(int n) {
        super(n);
    }

    @Override
    public void paint(Graphics graphics) {
        boolean bl = this.getModel().isPressed();
        int n = this.getSize().width;
        int n2 = this.getSize().height;
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, n, n2);
        graphics.setColor(RaritanLookAndFeel.BASE_BACKGROUND);
        if (this.direction == 1 || this.direction == 5) {
            graphics.fillRect(2, 0, n - 3, n2 - 2);
        } else {
            graphics.fillRect(0, 2, n - 2, n2 - 3);
        }
        switch (this.direction) {
            case 1: {
                graphics.setColor(this.leftBorder);
                graphics.drawLine(0, 0, 0, n2);
                graphics.setColor(this.arrowColor);
                int n3 = (n2 - this.arrowHeight) / 2;
                int n4 = (n - 2) / 2;
                for (int i = 0; i < this.arrowHeight; ++i) {
                    graphics.drawLine(n4 - i, n3 + i, n4 + i + 1, n3 + i);
                }
                break;
            }
            case 5: {
                graphics.setColor(this.leftBorder);
                graphics.drawLine(0, 0, 0, n2);
                graphics.setColor(this.arrowColor);
                int n5 = (n2 - this.arrowHeight) / 2 + this.arrowHeight - 1;
                int n6 = (n - 2) / 2;
                for (int i = 0; i < this.arrowHeight; ++i) {
                    graphics.drawLine(n6 - i, n5 - i, n6 + i + 1, n5 - i);
                }
                break;
            }
            case 3: {
                graphics.setColor(this.leftBorder);
                graphics.drawLine(0, 0, n, 0);
                graphics.setColor(this.arrowColor);
                int n7 = (n - this.arrowHeight) / 2 + this.arrowHeight - 1;
                int n8 = (n2 - 2) / 2;
                for (int i = 0; i < this.arrowHeight; ++i) {
                    graphics.drawLine(n7 - i, n8 - i, n7 - i, n8 + i + 1);
                }
                break;
            }
            case 7: {
                graphics.setColor(this.leftBorder);
                graphics.drawLine(0, 0, n, 0);
                graphics.setColor(this.arrowColor);
                int n9 = (n - this.arrowHeight) / 2;
                int n10 = (n2 - 2) / 2;
                for (int i = 0; i < this.arrowHeight; ++i) {
                    graphics.drawLine(n9 + i, n10 - i, n9 + i, n10 + i + 1);
                }
                break;
            }
        }
        if (bl) {
            if (this.direction == 1 || this.direction == 5) {
                graphics.setColor(this.upLeftBorderPressed);
                graphics.drawLine(2, 0, 2, n2 - 3);
                graphics.drawLine(2, 0, n - 2, 0);
            } else {
                graphics.setColor(this.upLeftBorderPressed);
                graphics.drawLine(0, 2, 0, n2 - 3);
                graphics.drawLine(0, 2, n - 3, 2);
            }
        } else if (this.direction == 1 || this.direction == 5) {
            graphics.setColor(this.bottomBorderIn);
            graphics.drawLine(2, n2 - 2, n - 2, n2 - 2);
            graphics.setColor(this.bottomBorderOut);
            graphics.drawLine(3, n2 - 1, n - 3, n2 - 1);
            graphics.setColor(this.rightBorderIn);
            graphics.drawLine(n - 2, 0, n - 2, n2 - 2);
            graphics.setColor(this.rightBorderOut);
            graphics.drawLine(n - 1, 1, n - 1, n2 - 3);
        } else {
            graphics.setColor(this.bottomBorderIn);
            graphics.drawLine(0, n2 - 2, n - 2, n2 - 2);
            graphics.setColor(this.bottomBorderOut);
            graphics.drawLine(1, n2 - 1, n - 3, n2 - 1);
            graphics.setColor(this.rightBorderIn);
            graphics.drawLine(n - 2, 2, n - 2, n2 - 2);
            graphics.setColor(this.rightBorderOut);
            graphics.drawLine(n - 1, 3, n - 1, n2 - 3);
        }
    }
}

