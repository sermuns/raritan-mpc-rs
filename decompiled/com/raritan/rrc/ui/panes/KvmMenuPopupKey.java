/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;

public class KvmMenuPopupKey {
    RaritanPropertyResourceBundle bundle;

    public KvmMenuPopupKey(RRCScreenContext rRCScreenContext) {
        this.bundle = RaritanResourceBundle.getResourceBundle(rRCScreenContext.getLocale());
        String string = rRCScreenContext.getAppSettings().getkeyboardMenuHotkey().replace(this.bundle.getString("KeyboardMenuHotkey.Alt.Indicator"), this.bundle.getString("KeyboardMenuHotkey.LeftAlt.Indicator"));
        if (!string.equals("")) {
            if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionA"))) {
                RRCScreenContext.setKvmPopupKey("A");
                rRCScreenContext.setPopupKeycode(65);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionB"))) {
                RRCScreenContext.setKvmPopupKey("B");
                rRCScreenContext.setPopupKeycode(66);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionC"))) {
                RRCScreenContext.setKvmPopupKey("C");
                rRCScreenContext.setPopupKeycode(67);
            }
            if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionD"))) {
                RRCScreenContext.setKvmPopupKey("D");
                rRCScreenContext.setPopupKeycode(68);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionE"))) {
                RRCScreenContext.setKvmPopupKey("E");
                rRCScreenContext.setPopupKeycode(69);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionF"))) {
                RRCScreenContext.setKvmPopupKey("F");
                rRCScreenContext.setPopupKeycode(70);
            }
            if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionG"))) {
                RRCScreenContext.setKvmPopupKey("G");
                rRCScreenContext.setPopupKeycode(71);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionH"))) {
                RRCScreenContext.setKvmPopupKey("H");
                rRCScreenContext.setPopupKeycode(72);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionI"))) {
                RRCScreenContext.setKvmPopupKey("I");
                rRCScreenContext.setPopupKeycode(73);
            }
            if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionJ"))) {
                RRCScreenContext.setKvmPopupKey("J");
                rRCScreenContext.setPopupKeycode(74);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionK"))) {
                RRCScreenContext.setKvmPopupKey("K");
                rRCScreenContext.setPopupKeycode(75);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionL"))) {
                RRCScreenContext.setKvmPopupKey("L");
                rRCScreenContext.setPopupKeycode(76);
            }
            if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionM"))) {
                RRCScreenContext.setKvmPopupKey("M");
                rRCScreenContext.setPopupKeycode(77);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionN"))) {
                RRCScreenContext.setKvmPopupKey("N");
                rRCScreenContext.setPopupKeycode(78);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionO"))) {
                RRCScreenContext.setKvmPopupKey("O");
                rRCScreenContext.setPopupKeycode(79);
            }
            if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionP"))) {
                RRCScreenContext.setKvmPopupKey("P");
                rRCScreenContext.setPopupKeycode(80);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionQ"))) {
                RRCScreenContext.setKvmPopupKey("Q");
                rRCScreenContext.setPopupKeycode(81);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionR"))) {
                RRCScreenContext.setKvmPopupKey("R");
                rRCScreenContext.setPopupKeycode(82);
            }
            if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionS"))) {
                RRCScreenContext.setKvmPopupKey("S");
                rRCScreenContext.setPopupKeycode(83);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionT"))) {
                RRCScreenContext.setKvmPopupKey("T");
                rRCScreenContext.setPopupKeycode(84);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionU"))) {
                RRCScreenContext.setKvmPopupKey("U");
                rRCScreenContext.setPopupKeycode(85);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionV"))) {
                RRCScreenContext.setKvmPopupKey("V");
                rRCScreenContext.setPopupKeycode(86);
            }
            if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionW"))) {
                RRCScreenContext.setKvmPopupKey("W");
                rRCScreenContext.setPopupKeycode(87);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionX"))) {
                RRCScreenContext.setKvmPopupKey("X");
                rRCScreenContext.setPopupKeycode(88);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionY"))) {
                RRCScreenContext.setKvmPopupKey("Y");
                rRCScreenContext.setPopupKeycode(89);
            } else if (string.equals(this.bundle.getString("KeyboardMenuHotkey.OptionZ"))) {
                RRCScreenContext.setKvmPopupKey("Z");
                rRCScreenContext.setPopupKeycode(90);
            }
        }
    }
}

