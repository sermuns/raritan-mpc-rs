/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.KeyTranslator;
import nn.pp.rccore.impl.keyboard.KeyTranslator_en_US;

public abstract class CharTranslator
extends KeyTranslator
implements CharTranslatorConstants {
    private boolean isLeftShiftDown = false;
    private boolean isRightShiftDown = false;
    private boolean isAltGrDown = false;
    protected KeyTranslator targetLanguage = null;
    protected Hashtable<Character, Vector<Integer>> charMappings = new Hashtable();
    protected HashSet<Integer> ctrlModifiedKeys = new HashSet();

    protected CharTranslator() {
        this.setTargetTranslator();
        this.addCharsToTranslate();
    }

    protected void setTargetTranslator() {
        this.targetLanguage = new KeyTranslator_en_US();
    }

    @Override
    protected void addKeys() {
        this.addKeyMappingByCode(8, 1, 13);
        this.addKeyMappingByCode(20, 1, 28);
        this.addKeyMappingByCode(27, 1, 59);
        this.addKeyMappingByCode(112, 1, 60);
        this.addKeyMappingByCode(113, 1, 61);
        this.addKeyMappingByCode(114, 1, 62);
        this.addKeyMappingByCode(115, 1, 63);
        this.addKeyMappingByCode(116, 1, 64);
        this.addKeyMappingByCode(117, 1, 65);
        this.addKeyMappingByCode(118, 1, 66);
        this.addKeyMappingByCode(119, 1, 67);
        this.addKeyMappingByCode(120, 1, 68);
        this.addKeyMappingByCode(121, 1, 69);
        this.addKeyMappingByCode(122, 1, 70);
        this.addKeyMappingByCode(123, 1, 71);
        this.addKeyMappingByCode(154, 1, 72);
        this.addKeyMappingByCode(145, 1, 73);
        this.addKeyMappingByCode(19, 1, 74);
        this.addKeyMappingByCode(155, 1, 75);
        this.addKeyMappingByCode(36, 1, 76);
        this.addKeyMappingByCode(33, 1, 77);
        this.addKeyMappingByCode(127, 1, 78);
        this.addKeyMappingByCode(35, 1, 79);
        this.addKeyMappingByCode(34, 1, 80);
        this.addKeyMappingByCode(38, 1, 81);
        this.addKeyMappingByCode(37, 1, 82);
        this.addKeyMappingByCode(40, 1, 83);
        this.addKeyMappingByCode(39, 1, 84);
        this.addKeyMappingByCode(525, 1, 106);
    }

    protected abstract void addCharsToTranslate();

    @Override
    public abstract Locale getLocale();

    protected boolean anyShiftDown() {
        return this.isLeftShiftDown | this.isRightShiftDown;
    }

    protected void addCharMappings(char c, int[] nArray) {
        Vector<Integer> vector = new Vector<Integer>(nArray.length);
        for (int n : nArray) {
            vector.add(n);
        }
        this.charMappings.put(Character.valueOf(c), vector);
    }

    private Vector<Integer> restoreShiftStates(boolean bl, boolean bl2) {
        Vector<Integer> vector = new Vector<Integer>();
        if (bl != this.isLeftShiftDown) {
            vector.add((this.isLeftShiftDown ? 65536 : 0) | this.translateTargetKeyEvent(16, '\u0000', 2));
            this.isLeftShiftDown = bl;
        }
        if (bl2 != this.isRightShiftDown) {
            vector.add((this.isRightShiftDown ? 65536 : 0) | this.translateTargetKeyEvent(16, '\u0000', 3));
            this.isRightShiftDown = bl2;
        }
        return vector;
    }

    private Vector<Integer> restoreAltGrState(boolean bl) {
        Vector<Integer> vector = new Vector<Integer>();
        if (!this.allowAltGr()) {
            return vector;
        }
        if (bl != this.isAltGrDown) {
            vector.add((this.isAltGrDown ? 65536 : 0) | this.translateTargetKeyEvent(65406, '\u0000', 3));
            this.isAltGrDown = bl;
        }
        return vector;
    }

    private void interpretKeyList(Vector<Integer> vector, boolean bl, boolean bl2, boolean bl3) {
        block9: for (int i = 0; i < vector.size(); ++i) {
            int n = vector.get(i);
            switch (n) {
                case 524288: {
                    if (this.isAltGrDown || !this.allowAltGr()) {
                        vector.remove(i);
                        --i;
                        continue block9;
                    }
                    vector.set(i, this.translateTargetKeyEvent(65406, '\u0000', 3));
                    this.isAltGrDown = true;
                    continue block9;
                }
                case 0x100000: {
                    if (this.isAltGrDown && this.allowAltGr()) {
                        vector.set(i, 0x10000 | this.translateTargetKeyEvent(65406, '\u0000', 3));
                        this.isAltGrDown = false;
                        continue block9;
                    }
                    vector.remove(i);
                    --i;
                    continue block9;
                }
                case 131072: {
                    if (this.anyShiftDown()) {
                        vector.remove(i);
                        --i;
                        continue block9;
                    }
                    vector.set(i, this.translateTargetKeyEvent(16, '\u0000', 2));
                    this.isLeftShiftDown = true;
                    continue block9;
                }
                case 262144: {
                    if (this.isLeftShiftDown) {
                        vector.insertElementAt(0x10000 | this.translateTargetKeyEvent(16, '\u0000', 2), i);
                        this.isLeftShiftDown = false;
                        ++i;
                    }
                    if (this.isRightShiftDown) {
                        vector.insertElementAt(0x10000 | this.translateTargetKeyEvent(16, '\u0000', 3), i);
                        this.isRightShiftDown = false;
                        ++i;
                    }
                    vector.remove(i);
                    --i;
                    continue block9;
                }
                case 393216: {
                    int n2 = vector.size();
                    vector.remove(i);
                    vector.addAll(i, this.restoreShiftStates(bl, bl2));
                    i = i + vector.size() - n2;
                    continue block9;
                }
                case 0x180000: {
                    int n2 = vector.size();
                    vector.remove(i);
                    vector.addAll(i, this.restoreAltGrState(bl3));
                    i = i + vector.size() - n2;
                    continue block9;
                }
                case 0x1E0000: {
                    int n2 = vector.size();
                    vector.remove(i);
                    vector.addAll(i, this.restoreShiftStates(bl, bl2));
                    vector.addAll(i + vector.size() - n2 + 1, this.restoreAltGrState(bl3));
                    i = i + vector.size() - n2;
                    continue block9;
                }
                default: {
                    int n3 = n & 0x10000;
                    n = (n | 0x10000) ^ 0x10000;
                    vector.set(i, n3 | this.translateTargetKeyEvent(n, '\u0000', 1));
                }
            }
        }
    }

    public Vector<Integer> translateChar(KeyEvent keyEvent) {
        Vector<Integer> vector;
        char c = keyEvent.getKeyChar();
        Vector<Integer> vector2 = vector = this.charMappings.get(Character.valueOf(c)) == null ? null : new Vector<Integer>((Collection)this.charMappings.get(Character.valueOf(c)));
        if (vector != null) {
            boolean bl = this.isLeftShiftDown;
            boolean bl2 = this.isRightShiftDown;
            boolean bl3 = this.isAltGrDown;
            this.interpretKeyList(vector, bl, bl2, bl3);
            vector.addAll(this.restoreShiftStates(bl, bl2));
            vector.addAll(this.restoreAltGrState(bl3));
        }
        return vector;
    }

    @Override
    public int translateKeyEvent(KeyEvent keyEvent) {
        int n = keyEvent.getKeyLocation();
        int n2 = keyEvent.getKeyCode();
        int n3 = keyEvent.getID();
        int n4 = keyEvent.getModifiersEx();
        if ((n4 & 0x80) > 0 && n2 != 17 && (n4 & 0x200) == 0 && n3 == 401) {
            this.ctrlModifiedKeys.add(n2);
            return this.targetLanguage.translateKeyEvent(keyEvent);
        }
        if (this.ctrlModifiedKeys.contains(n2) && n3 == 402) {
            this.ctrlModifiedKeys.remove(n2);
            return this.targetLanguage.translateKeyEvent(keyEvent);
        }
        if (n2 == 16) {
            if (n == 2) {
                this.isLeftShiftDown = n3 == 401;
            } else if (n == 3) {
                this.isRightShiftDown = n3 == 401;
            }
        } else if (n2 == 65406) {
            this.isAltGrDown = n3 == 401;
        } else if (n2 == 18 && n == 3) {
            boolean bl = this.isAltGrDown = n3 == 401;
        }
        if (n != 1) {
            return this.targetLanguage.translateKeyEvent(keyEvent);
        }
        Integer n5 = this.getByCode(n2, n);
        if (n5 == null) {
            return -1;
        }
        return n5;
    }

    public int translateTargetKeyEvent(int n, char c, int n2) {
        return this.targetLanguage.translateKeyEvent(n, c, n2);
    }

    @Override
    public int translateKeyEvent(int n, char c, int n2) {
        return this.translateTargetKeyEvent(n, c, n2);
    }
}

