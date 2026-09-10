/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.KvmView;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Locale;
import javaclientlib.clientlib.IKvmData;
import javaclientlib.tr.TRLIB_UPDATEINFO;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;
import javax.swing.JPanel;

public class KvmPanel
extends JPanel
implements IKvmData {
    private static final long serialVersionUID = -3617865031948452752L;
    private static final int WHITE = -1;
    protected boolean boolRefreshFullScreen = false;
    private String idsAutosensing;
    private String idsCalibratingColor;
    private String idsNoVideo;
    private String idsVideoRange;
    private int hImageSize = 0;
    private int vImageSize = 0;
    private double xScale = 0.0;
    private double yScale = 0.0;
    private KvmView kvmView = null;
    private Insets objInsets = this.getInsets();
    private int iHeight = this.getHeight();
    private int iWidth = this.getWidth();
    private int iPaintableWidth;
    private int iPaintableHeight;
    private final Font font;
    private boolean boolNewVideoMode;
    private boolean hasVideo;
    private TRLIB_UPDATEINFO updateInfo;
    private String mOsdString;
    private TRRSP_NEW_VIDEO_MODE_DATA objNewVideoModeData;
    private ScreenContext scrContext;
    private String popupMessage;

    public KvmPanel(boolean bl, ScreenContext screenContext) {
        this.iPaintableWidth = this.iWidth - this.objInsets.right - this.objInsets.left - 1;
        this.iPaintableHeight = this.iHeight - this.objInsets.top - this.objInsets.bottom - 1;
        this.font = new Font("Verdanna", 0, 30);
        this.boolNewVideoMode = false;
        this.hasVideo = false;
        this.updateInfo = null;
        this.mOsdString = "";
        this.objNewVideoModeData = null;
        this.popupMessage = "";
        this.scrContext = screenContext;
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.idsAutosensing = raritanPropertyResourceBundle.getString("KVMPanel.autosensing");
        this.idsCalibratingColor = raritanPropertyResourceBundle.getString("KVMPanel.colorCalibrating");
        this.idsNoVideo = raritanPropertyResourceBundle.getString("KVMPanel.noVideoSignal");
        this.idsVideoRange = raritanPropertyResourceBundle.getString("KVMPanel.videoOutOfRange");
        this.setFocusable(true);
        this.setOpaque(true);
        MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, ((RRCScreenContext)this.scrContext).getSelectedPort());
    }

    public void forceRepaint() {
        this.repaint(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void notify(int n, int n2) {
        switch (n) {
            case 5: {
                if (n2 != -1) {
                    this.setOsdMessage(this.idsCalibratingColor + " (" + n2 + ")");
                } else {
                    this.setOsdMessage("");
                }
                this.forceRepaint();
                break;
            }
            case 0: {
                if (n2 != -1) {
                    this.setOsdMessage(this.idsAutosensing + " (" + n2 + ")");
                } else if (this.hasVideo) {
                    this.setOsdMessage("");
                }
                this.forceRepaint();
                break;
            }
            case 3: {
                this.hasVideo = false;
                if (n2 != 0) {
                    this.setOsdMessage(this.idsNoVideo);
                } else {
                    this.setOsdMessage("");
                }
                this.forceRepaint();
                break;
            }
            case 4: {
                if (n2 != 0) {
                    this.setOsdMessage(this.idsVideoRange);
                } else {
                    this.hasVideo = true;
                    this.setOsdMessage("");
                }
                this.forceRepaint();
                break;
            }
            case 10: {
                this.setOsdMessage("Access terminated by local user - Local port has priority.");
                this.forceRepaint();
                break;
            }
        }
    }

    public void forceExistingVideoModeNotify() {
        if (this.objNewVideoModeData != null) {
            this.newVideoModeNotify(this.objNewVideoModeData);
        }
    }

    @Override
    public void newVideoModeNotify(TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA) {
        this.objNewVideoModeData = tRRSP_NEW_VIDEO_MODE_DATA;
        this.hImageSize = tRRSP_NEW_VIDEO_MODE_DATA.getActualHSize();
        this.vImageSize = tRRSP_NEW_VIDEO_MODE_DATA.getActualVSize();
        this.setPreferredSize(new Dimension(this.hImageSize, this.vImageSize));
        this.revalidate();
        this.objInsets = this.getInsets();
        this.iHeight = this.getHeight();
        this.iWidth = this.getWidth();
        this.iPaintableWidth = this.iWidth - this.objInsets.right - this.objInsets.left - 1;
        this.iPaintableHeight = this.iHeight - this.objInsets.top - this.objInsets.bottom - 1;
        this.boolNewVideoMode = true;
        if (this.kvmView != null) {
            this.kvmView.unfreezeVideo();
        }
    }

    @Override
    public void updateNotify(TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
        Rectangle rectangle = this.getBitmapPlace();
        this.updateInfo = tRLIB_UPDATEINFO;
        Rectangle rectangle2 = this.updateInfo.getRectangle();
        rectangle2.x += rectangle.x;
        rectangle2.y += rectangle.y;
        if (this.kvmView.isScaleVideoFlag()) {
            this.repaint(0, 0, this.getWidth(), this.getHeight());
        } else {
            this.repaint(rectangle2);
        }
    }

    public Rectangle getBitmapPlace() {
        this.setKvmBorders();
        Dimension dimension = this.kvmView.fixSize(this.getSize());
        Rectangle rectangle = new Rectangle(this.kvmView.isScaleVideoFlag() ? 0 : (dimension.width - this.hImageSize) / 2, this.kvmView.isScaleVideoFlag() ? 0 : (dimension.height - this.vImageSize) / 2, this.kvmView.isScaleVideoFlag() ? dimension.width : this.hImageSize, this.kvmView.isScaleVideoFlag() ? dimension.height : this.vImageSize);
        if (rectangle.x < 0) {
            rectangle.x = 0;
        }
        if (rectangle.y < 0) {
            rectangle.y = 0;
        }
        return rectangle;
    }

    public Rectangle getBitmapLocation() {
        if (this.kvmView.isScaleVideoFlag()) {
            int n;
            int n2 = (int)((double)this.objNewVideoModeData.getActualVSize() / this.yScale + 0.5);
            int n3 = (int)((double)this.objNewVideoModeData.getActualHSize() / this.xScale + 0.5);
            Rectangle rectangle = this.getVisibleRect();
            if (n2 > rectangle.height) {
                n2 = rectangle.height;
            }
            if (n3 > rectangle.width) {
                n3 = rectangle.width;
            }
            n = (n = rectangle.height - n2) < 0 ? 0 : (n >>= 1);
            int n4 = rectangle.width - n3;
            n4 = n4 < 0 ? 0 : (n4 >>= 1);
            return new Rectangle(n4, n, n3, n2);
        }
        return this.getBitmapPlace();
    }

    public Rectangle getBitmapRect() {
        Dimension dimension = this.kvmView.fixSize(this.getSize());
        Rectangle rectangle = new Rectangle(this.kvmView.isScaleVideoFlag() ? 0 : (dimension.width - this.hImageSize) / 2, this.kvmView.isScaleVideoFlag() ? 0 : (dimension.height - this.vImageSize) / 2, this.kvmView.isScaleVideoFlag() ? dimension.width : this.hImageSize, this.kvmView.isScaleVideoFlag() ? dimension.height : this.vImageSize);
        return rectangle;
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        this.paintComponent(graphics);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        int n;
        int n2;
        int n3;
        Serializable serializable;
        super.paintComponent(graphics);
        graphics.setColor(Color.DARK_GRAY);
        Image image = null;
        if (!this.kvmView.isVideoFrozen() && this.updateInfo != null) {
            image = this.updateInfo.getImage();
        }
        if (image != null) {
            if (this.kvmView.isScaleVideoFlag()) {
                serializable = this.getParent().getBounds();
                ((Rectangle)serializable).x = 0;
                ((Rectangle)serializable).y = 0;
                double d = (double)this.objNewVideoModeData.getActualVSize() / (double)this.objNewVideoModeData.getActualHSize();
                this.xScale = (double)this.hImageSize / (double)((Rectangle)serializable).width;
                this.yScale = (double)this.vImageSize / (double)((Rectangle)serializable).height;
                if (this.xScale >= this.yScale) {
                    n3 = (int)((double)((Rectangle)serializable).width * d + 0.5);
                    n2 = ((Rectangle)serializable).height - n3;
                    ((Rectangle)serializable).y = n2 >> 1;
                    ((Rectangle)serializable).height -= n2;
                    this.yScale = (double)this.vImageSize / (double)((Rectangle)serializable).height;
                } else {
                    n3 = (int)((double)((Rectangle)serializable).height / d + 0.5);
                    n2 = ((Rectangle)serializable).width - n3;
                    ((Rectangle)serializable).x = n2 >> 1;
                    ((Rectangle)serializable).width -= n2;
                    this.xScale = (double)this.hImageSize / (double)((Rectangle)serializable).width;
                }
                graphics.drawImage(image, ((Rectangle)serializable).x, ((Rectangle)serializable).y, ((Rectangle)serializable).x + ((Rectangle)serializable).width, ((Rectangle)serializable).y + ((Rectangle)serializable).height, 0, 0, this.hImageSize, this.vImageSize, this);
            } else {
                serializable = this.getBitmapPlace();
                Rectangle rectangle = ((Rectangle)serializable).intersection(graphics.getClipBounds());
                if (!rectangle.isEmpty() && (n = (int)(graphics.drawImage(image, rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, rectangle.x - ((Rectangle)serializable).x, rectangle.y - ((Rectangle)serializable).y, rectangle.x + rectangle.width - ((Rectangle)serializable).x, rectangle.y + rectangle.height - ((Rectangle)serializable).y, this) ? 1 : 0)) == 0) {
                    System.out.println("error in drawImage");
                }
            }
        }
        if (!this.getOsdMessage().equals("")) {
            graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
            if (Locale.getDefault().getLanguage().equalsIgnoreCase(Locale.ENGLISH.getLanguage())) {
                graphics.setFont(this.font);
            } else {
                serializable = graphics.getFont();
                serializable = ((Font)serializable).deriveFont(1, (float)((Font)serializable).getSize() * 2.0f);
                graphics.setFont((Font)serializable);
            }
            serializable = graphics.getFontMetrics();
            int n4 = ((FontMetrics)serializable).stringWidth(this.getOsdMessage());
            n = (this.getVisibleRect().width - n4) / 2;
            n3 = this.getVisibleRect().height / 2;
            graphics.setColor(Color.WHITE);
            graphics.fillRect(n, n3 - ((FontMetrics)serializable).getHeight() + 5, n4 + 5, ((FontMetrics)serializable).getHeight());
            graphics.setColor(Color.BLUE);
            graphics.drawString(this.getOsdMessage(), n, n3);
        }
        if (!"".equals(this.popupMessage)) {
            serializable = new Font("Verdana", 1, 15);
            FontMetrics fontMetrics = graphics.getFontMetrics((Font)serializable);
            n = fontMetrics.stringWidth(this.getPopupMessage());
            n3 = this.getVisibleRect().width - n;
            n2 = this.getVisibleRect().height;
            graphics.drawRect(n3 / 2, n2 - fontMetrics.getHeight() - 10 + 5, n + 5, fontMetrics.getHeight());
            graphics.setColor(Color.BLACK);
            graphics.fillRect(n3 / 2, n2 - fontMetrics.getHeight() - 10 + 5, n + 5, fontMetrics.getHeight());
            graphics.setColor(Color.GREEN);
            graphics.setFont((Font)serializable);
            graphics.drawString(this.getPopupMessage(), n3 / 2, n2 - 10);
            this.repaint();
        }
    }

    public void setOsdMessage(String string) {
        this.mOsdString = string;
    }

    private String getOsdMessage() {
        return this.mOsdString;
    }

    public void setPopupMessage(String string) {
        this.popupMessage = string;
    }

    public String getPopupMessage() {
        return this.popupMessage;
    }

    public int getPossiblyScaledXCoord(int n) {
        return (int)(this.kvmView.isScaleVideoFlag() ? (double)n * this.xScale : (double)n);
    }

    public int getPossiblyScaledYCoord(int n) {
        return (int)(this.kvmView.isScaleVideoFlag() ? (double)n * this.yScale : (double)n);
    }

    private void setKvmBorders() {
        int n;
        if (this.kvmView.isScaleVideoFlag()) {
            this.kvmView.setHorizontalKvmBorderVisible(false, 0);
            this.kvmView.setVerticalKvmBorderVisible(false, 0);
            this.kvmView.setHorizontalScrollBarVisibility(false);
            this.kvmView.setVerticalScrollBarVisibility(false);
            this.kvmView.setSouthEastLabelVisible(false);
            this.kvmView.knowScrolls(true);
            return;
        }
        boolean bl = ((RRCScreenContext)this.scrContext).getAppSettings().isShowScrollBorders();
        boolean bl2 = false;
        boolean bl3 = false;
        Dimension dimension = this.getSize();
        if (this.kvmView.getScrollPaneSize().getWidth() < dimension.getWidth() - 1.0) {
            bl2 = true;
            this.kvmView.setHorizontalScrollBarVisibility(true);
            if (bl) {
                n = (int)(dimension.getWidth() - this.kvmView.getScrollPaneSize().getWidth());
                if (n > 20) {
                    n = 20;
                }
                this.kvmView.setHorizontalKvmBorderVisible(true, n);
            } else {
                this.kvmView.setHorizontalKvmBorderVisible(false, 0);
            }
        } else {
            bl2 = false;
            this.kvmView.setHorizontalScrollBarVisibility(false);
            this.kvmView.setHorizontalKvmBorderVisible(false, 0);
        }
        if (this.kvmView.getScrollPaneSize().getHeight() < dimension.getHeight() - 1.0) {
            bl3 = true;
            this.kvmView.setVerticalScrollBarVisibility(true);
            if (bl) {
                n = (int)(dimension.getHeight() - this.kvmView.getScrollPaneSize().getHeight());
                if (n > 20) {
                    n = 20;
                }
                this.kvmView.setVerticalKvmBorderVisible(true, n);
            } else {
                this.kvmView.setVerticalKvmBorderVisible(false, 0);
            }
        } else {
            bl3 = false;
            this.kvmView.setVerticalScrollBarVisibility(false);
            this.kvmView.setVerticalKvmBorderVisible(false, 0);
        }
        if (bl2 && bl3) {
            this.kvmView.setSouthEastLabelVisible(true);
        } else {
            this.kvmView.setSouthEastLabelVisible(false);
        }
        this.kvmView.knowScrolls(true);
    }

    protected void setView(KvmView kvmView) {
        this.kvmView = kvmView;
    }

    protected void disconnect() {
        this.updateInfo = null;
    }
}

