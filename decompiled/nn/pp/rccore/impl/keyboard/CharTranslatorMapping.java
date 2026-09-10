/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.util.Hashtable;
import java.util.Vector;
import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;

public abstract class CharTranslatorMapping
extends Hashtable<Character, Vector<Integer>>
implements CharTranslatorConstants,
EricVKConstants {
    protected void addMapping(char c, int[] nArray) {
        Vector<Integer> vector = new Vector<Integer>(nArray.length);
        for (int n : nArray) {
            vector.add(n);
        }
        this.put(Character.valueOf(c), vector);
    }

    public CharTranslatorMapping() {
        this.addMappings();
    }

    protected void addMappings() {
    }
}

