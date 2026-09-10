/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.text.ParseException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import nn.pp.rccore.IKeyboardMacro;

public class KeyboardMacro
implements IKeyboardMacro {
    private String name;
    private String code;
    private KeyCode[] keycodes;
    private boolean confirm;
    private static final String confirmPrefix = "confirm";

    public KeyboardMacro(String string, String string2, KeyCode[] keyCodeArray, boolean bl) {
        this.name = new String(string);
        this.code = new String(string2);
        this.copyKeycodes(keyCodeArray);
        this.confirm = bl;
    }

    public KeyboardMacro(String string, String string2, String string3) throws ParseException {
        int n;
        this.name = new String(string);
        if (string2.startsWith(confirmPrefix)) {
            this.code = new String(string2.substring(confirmPrefix.length()).trim());
            this.confirm = true;
        } else {
            this.code = new String(string2);
            this.confirm = false;
        }
        Vector<Integer> vector = new Vector<Integer>();
        StringTokenizer stringTokenizer = new StringTokenizer(string3);
        while (stringTokenizer.hasMoreTokens()) {
            try {
                n = Integer.parseInt(stringTokenizer.nextToken(), 16);
                vector.addElement(new Integer(n));
            }
            catch (NumberFormatException numberFormatException) {
                throw new ParseException(string3, 0);
            }
        }
        this.keycodes = new KeyCode[vector.size()];
        n = 0;
        Iterator iterator = vector.iterator();
        while (iterator.hasNext()) {
            this.keycodes[n++] = new KeyCode((Integer)iterator.next());
        }
    }

    public KeyboardMacro(IKeyboardMacro iKeyboardMacro) {
        this.name = new String(iKeyboardMacro.getName());
        this.code = new String(iKeyboardMacro.getCode());
        this.copyKeycodes(iKeyboardMacro.getKeycodes());
        this.confirm = iKeyboardMacro.getConfirm();
    }

    private void copyKeycodes(IKeyboardMacro.IKeyCode[] iKeyCodeArray) {
        if (iKeyCodeArray == null) {
            return;
        }
        this.keycodes = new KeyCode[iKeyCodeArray.length];
        for (int i = 0; i < iKeyCodeArray.length; ++i) {
            this.keycodes[i] = new KeyCode(iKeyCodeArray[i]);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getCodeWithConfirm() {
        if (this.confirm) {
            return "confirm " + this.code;
        }
        return this.code;
    }

    @Override
    public String getNameOrCode() {
        if (this.name != null && this.name.length() > 0) {
            return this.name;
        }
        return this.code;
    }

    @Override
    public IKeyboardMacro.IKeyCode[] getKeycodes() {
        return this.keycodes;
    }

    @Override
    public boolean getConfirm() {
        return this.confirm;
    }

    @Override
    public String getKeycodesAsString() {
        String string = "";
        for (int i = 0; this.keycodes != null && i < this.keycodes.length; ++i) {
            if (string.length() > 0) {
                string = string + " ";
            }
            if (!this.keycodes[i].isPress()) {
                return "";
            }
            string = string + Integer.toHexString(this.keycodes[i].getCode());
        }
        return string;
    }

    public class KeyCode
    implements IKeyboardMacro.IKeyCode {
        private int code;
        private boolean press;
        private boolean isDelay = false;

        public KeyCode(int n, boolean bl) {
            this.code = n;
            this.press = bl;
        }

        public KeyCode(int n) {
            this(n, true);
        }

        public KeyCode(IKeyboardMacro.IKeyCode iKeyCode) {
            this.code = iKeyCode.getCode();
            this.press = iKeyCode.isPress();
            this.isDelay = iKeyCode.isDelay();
        }

        public KeyCode(boolean bl, int n) {
            this.code = n;
            this.isDelay = bl;
            this.press = true;
        }

        @Override
        public int getCode() {
            return this.code;
        }

        @Override
        public boolean isPress() {
            return this.press;
        }

        @Override
        public boolean isDelay() {
            return this.isDelay;
        }
    }
}

