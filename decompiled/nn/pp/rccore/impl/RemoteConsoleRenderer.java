/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.util.concurrent.locks.ReadWriteLock;
import javax.swing.JComponent;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.FramebufferRenewer;
import nn.pp.rccore.impl.ImageProvider;

public interface RemoteConsoleRenderer {
    public JComponent getRCJComponent();

    public BufferedImage getSnapshot();

    public BufferedImage getSnapshot(Rectangle var1);

    public void drawRemoteConsoleData(Image var1, int var2, int var3, int var4, int var5);

    public void screenResolutionChanged(Dimension var1);

    public void setOSD(String var1, int var2, boolean var3);

    public void init();

    public void dispose();

    public void setRenewer(FramebufferRenewer var1);

    public void setSingleCursorMode(boolean var1);

    public void setMouseSyncString(String var1);

    public void setMouseSyncKeys(String var1, String var2) throws ParseException;

    public void setCaptureRightAway(boolean var1);

    public void setEnforceSmmHotkeyCheck(boolean var1);

    public void setOsdBgColor(Color var1);

    public void setOsdFgColor(Color var1);

    public void setOsdAlpha(int var1);

    public void setOsdPosition(String var1);

    public void setOsdDisabled(boolean var1);

    public boolean isOsdDisabled();

    public double getScalingX();

    public double getScalingY();

    public boolean isScaleToFit();

    public boolean isScaleToFitKeepAr();

    public void setScaling(double var1, double var3) throws IllegalArgumentException;

    public void setScaleToFit(boolean var1, boolean var2, Dimension var3);

    public RCCore.Interpolation getInterpolation();

    public void setInterpolation(RCCore.Interpolation var1);

    public void setFrameSize(Dimension var1);

    public <CT> CT getCapablity(Class<CT> var1);

    public ReadWriteLock getLock();

    public void setImageProvider(ImageProvider var1);
}

