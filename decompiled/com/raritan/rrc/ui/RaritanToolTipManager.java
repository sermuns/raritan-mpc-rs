/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui;

import java.awt.Component;
import java.awt.Window;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

public class RaritanToolTipManager {
    private static RaritanToolTipManager sm_instance;

    public static synchronized RaritanToolTipManager getInstance() {
        if (sm_instance == null) {
            sm_instance = new RaritanToolTipManager();
        }
        return sm_instance;
    }

    private class RaritanPopupFactory
    extends PopupFactory {
        RaritanPopup popup = new RaritanPopup();

        private RaritanPopupFactory() {
        }

        @Override
        public Popup getPopup(Component component, Component component2, int n, int n2) throws IllegalArgumentException {
            Window window = SwingUtilities.windowForComponent(component);
            if (window != null && !window.isFocused()) {
                return this.popup;
            }
            return super.getPopup(component, component2, n, n2);
        }

        private class RaritanPopup
        extends Popup {
            private RaritanPopup() {
            }

            @Override
            public void hide() {
            }

            @Override
            public void show() {
            }
        }
    }
}

