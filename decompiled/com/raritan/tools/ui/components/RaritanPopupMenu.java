/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandMenuItem;
import com.raritan.tools.ui.components.MenuActionListenerImplementation;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public abstract class RaritanPopupMenu
extends JPopupMenu {
    protected ScreenContext scrContext;
    protected RaritanPropertyResourceBundle bundle;
    protected MenuActionListenerImplementation mali;

    public RaritanPopupMenu(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.mali = new MenuActionListenerImplementation(this.scrContext);
    }

    public RaritanPopupMenu(String string) {
        super(string);
    }

    public abstract void setContext();

    public void addContext(Object[] objectArray) {
        for (int i = 0; i < objectArray.length; ++i) {
            Object object = objectArray[i];
            if (object instanceof CommandMenuItem) {
                this.add((CommandMenuItem)object);
                continue;
            }
            if (object instanceof JPopupMenu.Separator) {
                this.addSeparator();
                continue;
            }
            if (object instanceof JMenu) {
                this.add((JMenu)object);
                continue;
            }
            if (object instanceof String) {
                this.add(new CommandMenuItem((String)object, this.scrContext));
                continue;
            }
            this.scrContext.getLogger().logTextDebug("Unhandled class type");
        }
    }

    public void addContext(List list) {
        Object var2_2 = null;
        for (int i = 0; i < list.size(); ++i) {
            var2_2 = list.get(i);
            this.addToMenu(var2_2);
        }
    }

    private void addToMenu(Object object) {
        if (object instanceof CommandMenuItem) {
            this.add((CommandMenuItem)object);
        } else if (object instanceof JPopupMenu.Separator) {
            this.addSeparator();
        } else if (object instanceof JMenu) {
            this.add((JMenu)object);
        } else if (object instanceof String) {
            this.add(new CommandMenuItem((String)object, this.scrContext));
        } else {
            this.scrContext.getLogger().logTextDebug("Unhandled class type");
        }
    }
}

