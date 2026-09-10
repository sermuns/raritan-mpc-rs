/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.component;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class CenterToScreen {
    Rectangle abounds;

    public CenterToScreen(Rectangle rectangle) {
        this.abounds = rectangle;
    }

    public Rectangle calculatePosition() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle rectangle = new Rectangle(dimension);
        int n = rectangle.x + (rectangle.width - this.abounds.width) / 2;
        int n2 = rectangle.y + (rectangle.height - this.abounds.height) / 2;
        int n3 = this.abounds.width;
        int n4 = this.abounds.height;
        return new Rectangle(n, n2, n3, n4);
    }
}

