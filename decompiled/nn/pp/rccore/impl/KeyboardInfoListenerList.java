/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.List;
import java.util.Locale;
import java.util.Vector;
import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.KeyboardInfoListener;
import nn.pp.rccore.KeyboardMacro;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.KeyboardInfoListenerAction;
import nn.pp.rccore.impl.StringLocale;

public class KeyboardInfoListenerList
extends ListenerList<KeyboardInfoListener> {
    public void fireKeyboardMacroListChanged(List<KeyboardMacro> list) {
        final Vector<KeyboardMacro> vector = new Vector<KeyboardMacro>(list.size());
        for (KeyboardMacro keyboardMacro : list) {
            vector.add(new KeyboardMacro(keyboardMacro));
        }
        this.fire(new KeyboardInfoListenerAction(){

            @Override
            public void run() {
                ((KeyboardInfoListener)this.listener).keyboardMacroListChanged(vector);
            }
        }, 1);
    }

    public void fireSoftKeyboardMappingChanged(Locale locale) {
        final Locale locale2 = StringLocale.loadLocale(locale);
        this.fire(new KeyboardInfoListenerAction(){

            @Override
            public void run() {
                ((KeyboardInfoListener)this.listener).softKeyboardMappingChanged(locale2);
            }
        }, 2);
    }

    public void fireLocalKeyboardMappingChanged(Locale locale) {
        final Locale locale2 = StringLocale.loadLocale(locale);
        this.fire(new KeyboardInfoListenerAction(){

            @Override
            public void run() {
                ((KeyboardInfoListener)this.listener).localKeyboardMappingChanged(locale2);
            }
        }, 4);
    }

    public void fireKeyboardLedStateChanged(List<RCCore.KeyboardLed> list) {
        final Vector<RCCore.KeyboardLed> vector = new Vector<RCCore.KeyboardLed>(list);
        this.fire(new KeyboardInfoListenerAction(){

            @Override
            public void run() {
                ((KeyboardInfoListener)this.listener).keyboardLedStateChanged(vector);
            }
        }, 8);
    }

    public void fireSunKeyboardSupported(final boolean bl) {
        this.fire(new KeyboardInfoListenerAction(){

            @Override
            public void run() {
                ((KeyboardInfoListener)this.listener).sunKeyboardSupported(bl);
            }
        }, 16);
    }
}

