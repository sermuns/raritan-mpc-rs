/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import java.util.ArrayList;
import java.util.HashMap;
import nn.pp.common.ContextEventImpl;
import nn.pp.common.ContextListener;

public final class ApplicationContext
extends HashMap {
    public static final String KEY_HOTKEYMAP = "HOTKEYMAP";
    public static final String KEY_KEYBOARDLANG = "KEYBOARDLANGUAGE";
    public static final String KEY_SCROLL_BORDERS = "SCROLLBORDERS";
    private static ApplicationContext appCtx = null;
    private ArrayList<ContextListener> listeners;

    private ApplicationContext() {
    }

    public static ApplicationContext getInstance() {
        return appCtx;
    }

    public Object getAttribute(String string) {
        return this.get(string);
    }

    public Object setAttribute(String string, Object object) {
        Object object2 = this.put(string, object);
        this.fireContextChanged(string);
        return object2;
    }

    public void registerListener(ContextListener contextListener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(contextListener);
    }

    public void removeListener(ContextListener contextListener) {
        if (this.listeners != null) {
            this.listeners.remove(contextListener);
        }
    }

    private void fireContextChanged(String string) {
        if (this.listeners == null) {
            return;
        }
        for (ContextListener contextListener : this.listeners) {
            contextListener.contextChanged(new ContextEventImpl(string){});
        }
    }

    static {
        appCtx = new ApplicationContext();
    }
}

