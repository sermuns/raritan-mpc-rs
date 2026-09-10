/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.Applet
 *  java.applet.AppletContext
 *  java.applet.AudioClip
 */
package com.raritan.rrc.ui.applet;

import com.raritan.tools.ui.ScreenContext;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

public class EmbedAppletContext
implements AppletContext {
    private ScreenContext ctx;

    public EmbedAppletContext(ScreenContext screenContext) {
        this.ctx = screenContext;
    }

    public Applet getApplet(String string) {
        return null;
    }

    public Enumeration getApplets() {
        return null;
    }

    public AudioClip getAudioClip(URL uRL) {
        return null;
    }

    public Image getImage(URL uRL) {
        return null;
    }

    public InputStream getStream(String string) {
        return null;
    }

    public Iterator getStreamKeys() {
        return null;
    }

    public void setStream(String string, InputStream inputStream) throws IOException {
    }

    public void showDocument(URL uRL, String string) {
    }

    public void showDocument(URL uRL) {
    }

    public void showStatus(String string) {
    }
}

