/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

public interface MouseEventListener {
    public static final int BUTTON1 = 1;
    public static final int BUTTON2 = 2;
    public static final int BUTTON3 = 4;
    public static final int ABSOLUTE_EVENTS = 1;
    public static final int RELATIVE_EVENTS = 2;
    public static final int WHEEL_EVENTS = 4;
    public static final int ALL = 7;

    public void absoluteMouseEvent(int var1, int var2, int var3);

    public void relativeMouseEvent(int var1, int var2, int var3);

    public void mouseWheelEvent(int var1, int var2);
}

