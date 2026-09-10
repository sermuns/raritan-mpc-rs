/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.components.FullScreenToolBar;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.net.URL;
import java.util.Locale;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;

public interface AbstractUIManager {
    public Container getContentPane();

    public Container getContentPaneForPort(Port var1);

    public JLayeredPane getLayeredPane();

    public URL getIconBase();

    public Component getOptionComponent();

    public boolean isStandalone();

    public void changeScreen(boolean var1, JInternalFrame var2, Port var3, FullScreenTarget var4);

    public GraphicsDevice getGraphicsDevice();

    public void setGraphicsDevice(String var1);

    public Cursor getDefaultCursor();

    public Cursor getBlankCursor();

    public void disconnect();

    public FullScreenToolBar getFSToolBar(JInternalFrame var1);

    public Locale getLocale();

    public void setIconImage(Image var1);

    public void setFrameVisible(boolean var1);

    public String getAppId();

    public void addDisposedShell(JDialog var1);

    public static enum FullScreenTarget {
        SINGLE,
        PRIMARY,
        SECONDARY;

    }
}

