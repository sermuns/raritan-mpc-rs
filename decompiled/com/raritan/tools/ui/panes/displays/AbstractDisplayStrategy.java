/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.displays;

import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.lang.reflect.InvocationTargetException;

public class AbstractDisplayStrategy {
    private Class abstractDisplayClazz;
    private boolean isDialog;
    private boolean isNewPanel;
    private ScreenContext scrContext;

    public AbstractDisplayStrategy(Class clazz, boolean bl, ScreenContext screenContext) {
        this.abstractDisplayClazz = clazz;
        this.isDialog = bl;
        this.scrContext = screenContext;
    }

    public AbstractDisplayStrategy(Class clazz, boolean bl, ScreenContext screenContext, boolean bl2) {
        this.abstractDisplayClazz = clazz;
        this.isDialog = bl;
        this.scrContext = screenContext;
        this.isNewPanel = bl2;
    }

    public AbstractDisplay create() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        if (this.isNewPanel) {
            return (AbstractDisplay)this.abstractDisplayClazz.getConstructor(Boolean.TYPE, ScreenContext.class, Boolean.TYPE).newInstance(new Boolean(this.isDialog), this.scrContext, new Boolean(this.isNewPanel));
        }
        AbstractDisplay abstractDisplay = (AbstractDisplay)this.abstractDisplayClazz.getConstructor(Boolean.TYPE, ScreenContext.class).newInstance(new Boolean(this.isDialog), this.scrContext);
        return abstractDisplay;
    }

    public Class getAbstractDisplayClass() {
        return this.abstractDisplayClazz;
    }
}

