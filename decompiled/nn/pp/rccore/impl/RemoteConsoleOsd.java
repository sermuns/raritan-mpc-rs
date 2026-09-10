/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComponent;

public class RemoteConsoleOsd {
    private JComponent owner;
    private Color osdBgColor = Color.yellow;
    private Color osdFgColor = Color.blue;
    private int osdAlpha = 50;
    private Position osdPosition = Position.CENTER;
    private Font osdFont;
    private final int defaultOsdHeight = 20;
    private final int defaultOsdMargin = 20;
    private boolean osdBlank = false;
    private String osdMessage;
    private boolean osdShow;
    private int osdWidth;
    private int osdHeight;
    private Timer osdTimer;
    private Image osdImage;
    private Graphics osdGraphics;
    HashMap<String, Position> positionMap;

    public RemoteConsoleOsd(JComponent jComponent) {
        this.owner = jComponent;
        this.osdFont = new Font("Monospaced", 1, 20);
        this.osdImage = new BufferedImage(10, 10, 1);
        this.osdGraphics = this.osdImage.getGraphics();
        this.positionMap = new HashMap();
        this.positionMap.put("TOP", Position.TOP);
        this.positionMap.put("CENTER", Position.CENTER);
        this.positionMap.put("BOTTOM", Position.BOTTOM);
    }

    public void setBgColor(Color color) {
        this.osdBgColor = new Color(color.getRGB());
    }

    public void setFgColor(Color color) {
        this.osdFgColor = new Color(color.getRGB());
    }

    public void setAlpha(int n) {
        if (n >= 0 && n <= 100) {
            this.osdAlpha = n;
        }
    }

    public void setPosition(Position position) {
        this.osdPosition = position;
    }

    public void setPosition(String string) {
        Position position = this.positionMap.get(string.toUpperCase());
        if (position != null) {
            this.setPosition(position);
        }
    }

    public boolean osdBlank() {
        return this.osdBlank;
    }

    public boolean osdShow() {
        return this.osdShow;
    }

    public void renderOsd(Graphics graphics, Dimension dimension) {
        if (this.osdShow) {
            if (this.osdPosition != Position.CENTER && dimension.width != this.osdImage.getWidth(null)) {
                this.generateOsdImage(dimension.width);
            }
            int n = Math.max((dimension.width - this.osdWidth) / 2, 0);
            int n2 = 0;
            n2 = this.osdPosition == Position.CENTER ? Math.max((dimension.height - this.osdHeight) / 2, 0) : (this.osdPosition == Position.BOTTOM ? dimension.height - this.osdHeight : 0);
            Graphics2D graphics2D = (Graphics2D)graphics;
            AlphaComposite alphaComposite = AlphaComposite.getInstance(3, (float)this.osdAlpha / 100.0f);
            graphics2D.setComposite(alphaComposite);
            graphics.drawImage(this.osdImage, n, n2, null);
        }
    }

    public void setOsd(String string, int n, boolean bl, int n2) {
        this.osdBlank = bl;
        this.osdMessage = string;
        this.generateOsdImage(n2);
        if (this.osdTimer != null) {
            this.osdTimer.cancel();
            this.osdTimer = null;
        }
        if (n > 0 && this.osdShow) {
            this.osdTimer = new Timer("OSD");
            this.osdTimer.schedule(new TimerTask(){

                @Override
                public void run() {
                    RemoteConsoleOsd.this.osdShow = false;
                    RemoteConsoleOsd.this.osdTimer.cancel();
                    RemoteConsoleOsd.this.osdTimer = null;
                    RemoteConsoleOsd.this.owner.repaint();
                }
            }, n);
        }
    }

    private void generateOsdImage(int n) {
        if (this.osdMessage != null && this.osdMessage.length() > 0) {
            Graphics2D graphics2D = (Graphics2D)this.osdGraphics;
            Rectangle2D rectangle2D = this.osdFont.getStringBounds(this.osdMessage, graphics2D.getFontRenderContext());
            LineMetrics lineMetrics = this.osdFont.getLineMetrics(this.osdMessage, graphics2D.getFontRenderContext());
            int n2 = (int)rectangle2D.getWidth();
            int n3 = (int)rectangle2D.getHeight();
            if (this.osdPosition == Position.CENTER) {
                this.osdWidth = n2 + 20;
                this.osdHeight = 40;
            } else {
                this.osdWidth = n;
                this.osdHeight = 20;
            }
            this.osdImage = new BufferedImage(this.osdWidth, this.osdHeight, 3);
            this.osdGraphics = this.osdImage.getGraphics();
            this.osdGraphics.setColor(this.osdBgColor);
            this.osdGraphics.fillRect(0, 0, this.osdWidth, this.osdHeight);
            this.osdGraphics.setColor(this.osdFgColor);
            this.osdGraphics.setFont(this.osdFont);
            this.osdGraphics.drawString(this.osdMessage, (this.osdWidth - n2) / 2, (int)((float)(this.osdHeight - (this.osdHeight - n3) / 2) - lineMetrics.getDescent()));
            this.osdShow = true;
        } else {
            this.osdShow = false;
        }
    }

    public static enum Position {
        TOP,
        CENTER,
        BOTTOM;

    }
}

