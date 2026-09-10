/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.VolatileImage;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.rccore.impl.ListenerLists;
import nn.pp.rccore.impl.RemoteConsoleRendererGraphical;

public class RemoteConsoleRendererGraphicalVolatileImage
extends RemoteConsoleRendererGraphical {
    private VolatileImage vimage;
    private boolean initialPaint = true;

    RemoteConsoleRendererGraphicalVolatileImage(Logger logger, ListenerLists listenerLists) {
        super(logger, listenerLists);
    }

    @Override
    public String toString() {
        return T._("Renderer with Volatile Image acceleration");
    }

    @Override
    protected void createImage() {
        if (this.vimage != null) {
            this.vimage.flush();
        }
        if (this.graphics != null) {
            this.graphics.dispose();
        }
        this.vimage = this.createVolatileImage(this.dimension.width, this.dimension.height);
        this.image = this.vimage;
        if (this.vimage != null) {
            this.graphics = this.vimage.createGraphics();
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
        if (this.image == null) {
            return;
        }
        Dimension dimension = this.getPreferredSize();
        do {
            switch (this.vimage.validate(this.getGraphicsConfiguration())) {
                case 2: {
                    this.createImage();
                }
                case 1: {
                    this.graphics.dispose();
                    this.graphics = this.vimage.createGraphics();
                    this.graphics.setColor(Color.black);
                    this.graphics.fillRect(0, 0, dimension.width, dimension.height);
                    this.renewer.renewFramebuffer();
                    break;
                }
                case 0: {
                    if (!this.initialPaint) break;
                    this.renewer.renewFramebuffer();
                }
            }
            this.paintContentInterpolated(graphics, dimension);
            this.initialPaint = false;
        } while (this.vimage.contentsLost());
    }

    @Override
    public void dispose() {
        if (this.vimage != null) {
            this.vimage = null;
            super.dispose();
        }
    }
}

