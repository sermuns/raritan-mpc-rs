/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.AppletContext
 *  java.applet.AppletStub
 */
package com.raritan.rrc.ui.applet;

import com.raritan.rrc.ui.applet.EmbedAppletContext;
import com.raritan.rrc.ui.applet.EmbedAppletDataHolder;
import com.raritan.tools.ui.ScreenContext;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.MalformedURLException;
import java.net.URL;

public class EmbedAppletStub
implements AppletStub {
    private EmbedAppletDataHolder holder;
    private EmbedAppletContext context;
    private URL connUrl;

    public EmbedAppletStub(ScreenContext screenContext, EmbedAppletDataHolder embedAppletDataHolder, URL uRL) {
        this.holder = embedAppletDataHolder;
        this.context = new EmbedAppletContext(screenContext);
        this.connUrl = uRL;
    }

    public void appletResize(int n, int n2) {
    }

    public AppletContext getAppletContext() {
        return this.context;
    }

    public URL getCodeBase() {
        try {
            int n = this.connUrl.getPort();
            URL uRL = new URL(this.connUrl.getProtocol() + "://" + this.connUrl.getHost() + (n == -1 ? "" : ":" + n) + "/" + (this.holder.getCodeBase() == null ? "" : this.holder.getCodeBase()));
            return uRL;
        }
        catch (MalformedURLException malformedURLException) {
            return null;
        }
    }

    public URL getDocumentBase() {
        try {
            return new URL(this.holder.getCodeBase());
        }
        catch (MalformedURLException malformedURLException) {
            return null;
        }
    }

    public String getParameter(String string) {
        return this.holder.getParameter(string);
    }

    public void setParameter(String string, String string2) {
        this.holder.setParameter(string, string2);
    }

    public boolean isActive() {
        return true;
    }
}

