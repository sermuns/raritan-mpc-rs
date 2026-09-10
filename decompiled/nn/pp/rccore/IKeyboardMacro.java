/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

public interface IKeyboardMacro {
    public static final int KEYDELI_ADD = 240;
    public static final int KEYDELI_SEPERATE = 241;
    public static final int KEYDELI_PAUSE = 242;
    public static final int KEYDELI_RELEASE_LAST = 243;

    public String getName();

    public String getCode();

    public String getCodeWithConfirm();

    public String getNameOrCode();

    public IKeyCode[] getKeycodes();

    public boolean getConfirm();

    public String getKeycodesAsString();

    public static interface IKeyCode {
        public int getCode();

        public boolean isPress();

        public boolean isDelay();
    }
}

