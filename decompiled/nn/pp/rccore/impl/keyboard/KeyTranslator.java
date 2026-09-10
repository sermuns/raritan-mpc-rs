/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Locale;
import nn.pp.rccore.impl.keyboard.KeycodeWithLocation;

public abstract class KeyTranslator {
    private Hashtable<KeycodeWithLocation, Integer> keyMappingsByCode = new Hashtable();
    private Hashtable<Integer, Integer> keyMappingsByCharacter = new Hashtable();
    static final int PseudoVK_RIGHT_SHIFT = 272;
    static final int PseudoVK_RIGHT_CONTROL = 273;
    static final int PseudoVK_RIGHT_WINDOWS = 268;

    public KeyTranslator() {
        this.addKeys();
    }

    protected void addKeyMappingByCode(int n, int n2, int n3) {
        this.keyMappingsByCode.put(new KeycodeWithLocation(n, n2), n3);
    }

    protected void addKeyMappingByCharacter(int n, int n2) {
        this.keyMappingsByCharacter.put(n, n2);
    }

    public int translateKeyEvent(KeyEvent keyEvent) {
        return this.translateKeyEvent(keyEvent.getKeyCode(), keyEvent.getKeyChar(), keyEvent.getKeyLocation());
    }

    public int translateKeyEvent(int n, char c, int n2) {
        Integer n3 = this.getByCode(n, n2);
        if (n3 != null) {
            return n3;
        }
        n3 = this.keyMappingsByCharacter.get(c);
        if (n3 != null) {
            return n3;
        }
        return -1;
    }

    protected Integer getByCode(int n, int n2) {
        Integer n3 = this.keyMappingsByCode.get(new KeycodeWithLocation(n, n2));
        if (n3 == null && n2 == 0) {
            n3 = this.keyMappingsByCode.get(new KeycodeWithLocation(n, 1));
        }
        return n3;
    }

    public abstract Locale getLocale();

    public boolean allowAltGr() {
        return true;
    }

    public void loadKeys() {
    }

    protected void addKeys() {
    }

    public void dispose() {
        if (this.keyMappingsByCode != null) {
            this.keyMappingsByCode = null;
        }
        if (this.keyMappingsByCharacter != null) {
            this.keyMappingsByCharacter = null;
        }
    }
}

