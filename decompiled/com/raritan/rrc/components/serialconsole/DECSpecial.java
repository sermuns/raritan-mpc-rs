/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import java.awt.Graphics;
import java.util.Hashtable;

class DECSpecial {
    private static final char BITMAP = '\u0000';
    private static final char FILLRECT = '\u0001';
    private static final char GLYPH_WIDTH = '\u0001';
    private static final char GLYPH_HEIGHT = '\u0002';
    private static final char GLYPH_TYPE = '\u0003';
    private static final char GLYPH_DATA = '\u0004';
    private static final char[][] GLYPH_DATA_ARRAY = new char[][]{{'\u2409', '\b', '\b', '\u0000', '\u0000', 'l', '|', 'l', '\u0000', '?', '\f', '\f'}, {'\u240c', '\b', '\b', '\u0000', '|', '`', 'x', '`', '\u001f', '\u0018', '\u001e', '\u0018'}, {'\u240d', '\b', '\b', '\u0000', '<', '`', '<', '\u0000', '\u001e', '\u001b', '\u001e', '\u001b'}, {'\u240a', '\b', '\b', '\u0000', '`', '`', '|', '\u0000', '\u001f', '\u0018', '\u001e', '\u0018'}, {'\u2424', '\b', '\b', '\u0000', 'f', 'v', 'n', 'f', '\u0000', '\u0018', '\u0018', '\u001f'}, {'\u240b', '\b', '\b', '\u0000', '\u0082', '\u00c6', 'l', '8', '\u0000', '?', '\f', '\f'}, {'\u2666', '\b', '\b', '\u0000', '\u0000', '\u0018', '<', '~', '~', '<', '\u0018', '\u0000'}, {'\u2592', '\b', '\b', '\u0000', 'U', '\u00aa', 'U', '\u00aa', 'U', '\u00aa', 'U', '\u00aa'}, {'\u2264', '\b', '\b', '\u0000', '\u0000', '\u0007', '\u0018', '`', '\u0018', '\u0007', '\u0000', '\u007f'}, {'\u2265', '\b', '\b', '\u0000', '\u0000', 'p', '\f', '\u0003', '\f', 'p', '\u0000', '\u007f'}, {'\u00b6', '\b', '\b', '\u0000', '\u0000', '\u007f', '\u00b6', '6', '6', '6', '\u00e6', '\u0000'}, {'\u2260', '\b', '\b', '\u0000', '\u0000', '\u0003', '\u0006', '\u00ff', '\u0018', '\u00ff', '`', '\u00c0'}, {'\u2502', '\b', '\b', '\u0001', '\u3028'}, {'\u2524', '\b', '\b', '\u0001', '\u3028', '\u0431'}, {'\u2510', '\b', '\b', '\u0001', '\u0451', '\u3523'}, {'\u2514', '\b', '\b', '\u0001', '\u3025', '\u5431'}, {'\u2534', '\b', '\b', '\u0001', '\u3024', '\u0481'}, {'\u252c', '\b', '\b', '\u0001', '\u0481', '\u3523'}, {'\u251c', '\b', '\b', '\u0001', '\u3028', '\u5431'}, {'\u2594', '\b', '\b', '\u0001', '\u0081'}, {'\u2580', '\b', '\b', '\u0001', '\u0281'}, {'\u2500', '\b', '\b', '\u0001', '\u0481'}, {'\u25ac', '\b', '\b', '\u0001', '\u0681'}, {'\u253c', '\b', '\b', '\u0001', '\u3028', '\u0481'}, {'\u2518', '\b', '\b', '\u0001', '\u3025', '\u0431'}, {'\u250c', '\b', '\b', '\u0001', '\u3451', '\u3523'}};
    private static Hashtable glyph = new Hashtable();

    DECSpecial() {
    }

    boolean isGlyph(char c) {
        boolean bl = false;
        if (glyph.get(new Integer(c)) != null) {
            bl = true;
        }
        return bl;
    }

    void drawGlyph(Graphics graphics, char c, int n, int n2, int n3, int n4) {
        if (glyph.get(new Integer(c)) == null) {
            return;
        }
        int n5 = (Integer)glyph.get(new Integer(c));
        int n6 = GLYPH_DATA_ARRAY[n5][1];
        int n7 = GLYPH_DATA_ARRAY[n5][2];
        double d = (double)n3 * 1.0 / (double)n6;
        double d2 = (double)n4 * 1.0 / (double)n7;
        switch (GLYPH_DATA_ARRAY[n5][3]) {
            case '\u0000': {
                for (int i = 0; i < n7; ++i) {
                    for (int j = 0; j < n6; ++j) {
                        if (0 == (GLYPH_DATA_ARRAY[n5][i + 4] & 1 << 7 - j)) continue;
                        graphics.fillRect(n + (int)((double)j * d), n2 + (int)((double)i * d2), (int)((double)(j + 1) * d) - (int)((double)j * d), (int)((double)(i + 1) * d2) - (int)((double)i * d2));
                    }
                }
                break;
            }
            case '\u0001': {
                for (int i = 4; i < GLYPH_DATA_ARRAY[n5].length; ++i) {
                    int n8 = (GLYPH_DATA_ARRAY[n5][i] & 0xF000) >> 12;
                    int n9 = (GLYPH_DATA_ARRAY[n5][i] & 0xF00) >> 8;
                    int n10 = (GLYPH_DATA_ARRAY[n5][i] & 0xF0) >> 4;
                    int n11 = GLYPH_DATA_ARRAY[n5][i] & 0xF;
                    graphics.fillRect(n + (int)((double)n8 * d), n2 + (int)((double)n9 * d2), (int)((double)(n8 + n10) * d) - (int)((double)n8 * d), (int)((double)(n9 + n11) * d2) - (int)((double)n9 * d2));
                }
                break;
            }
        }
    }

    static {
        for (int i = 0; i < GLYPH_DATA_ARRAY.length; ++i) {
            glyph.put(new Integer(GLYPH_DATA_ARRAY[i][0]), new Integer(i));
        }
    }
}

