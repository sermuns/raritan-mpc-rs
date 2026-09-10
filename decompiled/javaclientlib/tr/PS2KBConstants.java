/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public interface PS2KBConstants {
    public static final int CNTRL_KB = 1;
    public static final int CNTRL_MS = 2;
    public static final int CNTRL_ALL = 3;
    public static final int PCK_TYPE_KBD = 16384;
    public static final int PCK_TYPE_MSD = 20480;
    public static final int PCK_TYPE_SVM = 57344;
    public static final int PCK_TYPE_RDW = 53248;
    public static final int CMD_PCK_MASK = 3840;
    public static final int CMD_DATA_MASK = 255;
    public static final int CMD_PCK_TYPE_LED = 0;
    public static final int CMD_LED_SCR_MASK = 16;
    public static final int CMD_LED_NUM_MASK = 32;
    public static final int CMD_LED_CAP_MASK = 64;
    public static final int CMD_LED_SC_MASK = 12;
    public static final int CMD_LED_SC_SHIFT = 2;
    public static final int CMD_LED_MASK = 112;
    public static final int KBEventBufSize = 10;
    public static final int MsEventBufSize = 64;
    public static final int SpecialCodeLShift = 240;
    public static final int SpecialCodeRShift = 241;
    public static final int SpecialCodeLCtrl = 242;
    public static final int SpecialCodeRCtrl = 243;
    public static final int SpecialCodeLAlt = 244;
    public static final int SpecialCodeRAlt = 245;
    public static final int SpecialCodePadEnter = 246;
    public static final int SpecialCodePrntScn = 247;
    public static final int SpecialCodePause = 248;
    public static final int DIKeySwitch = 31;
    public static final int DIKeySetVideo = 47;
    public static final int RetModeSwitch = -1;
    public static final int RetModeSetVideo = -2;
    public static final int RetModeCaqHack = -3;
    public static final int KBTMaxSC3KeyCode = 141;
    public static final byte SC3_Typematic = -9;
    public static final byte SC3_MakeBreak = -8;
    public static final byte SC3_MakeOnly = -7;
    public static final byte SC3_MakeBreakTypematic = -6;
    public static final byte SC3_Typematic_One = -5;
    public static final byte SC3_MakeBreak_One = -4;
    public static final byte SC3_MakeOnly_One = -3;
    public static final int LeftMask = 1;
    public static final int RightMask = 2;
    public static final int CenterMask = 4;
}

