/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

import com.util.kbd.KeyHIDValue;
import javaclientlib.clientlib.Keyboard;
import javaclientlib.clientlib.TRVideoStream;
import javaclientlib.tr.HIDtoPS2;
import javaclientlib.tr.HIDtoSun;
import javaclientlib.utils.RRCLogger;

public class PS2Keyboard
implements Keyboard {
    private boolean isLShiftDown = false;
    private boolean isRShiftDown = false;
    private boolean isLControlDown = false;
    private boolean isRControlDown = false;
    private boolean isLAltDown = false;
    private boolean isRAltDown = false;
    private boolean isAnyShiftDown = false;
    private boolean isAnyControlDown = false;
    private boolean isAnyAltDown = false;
    private final int US = 0;
    private final int JP = 1;
    private final int NUMBREAKKEYS = 6;
    private byte[] breakKeys = new byte[]{29, 42, 54, 56, -99, -72};
    protected int iCurrentScanCodeSet;
    protected boolean boolNeedSC3Settings;
    protected boolean boolNumLock;
    protected boolean boolScrollLock;
    protected boolean boolCapsLock;
    protected byte[] bySC3KeySettings = new byte[256];
    protected byte[] keyState = new byte[256];
    protected byte shiftStat;
    protected byte ctrlStat;
    protected byte altStat;
    protected byte scrollStat;
    protected boolean pauseStat;
    protected int[][] scanCodeTable;
    protected boolean targetType;
    protected int language;
    private int iKBDataCount;
    private byte[] byKBBuffer;
    private TRVideoStream videoStream;

    public PS2Keyboard(TRVideoStream tRVideoStream) {
        this.setCurrentScanCodeSet(2);
        this.setDefaultSC3KeySettings();
        this.setNeedSC3Settings(false);
        this.clearKeyStatus();
        this.iKBDataCount = 0;
        this.byKBBuffer = new byte[16];
        this.videoStream = tRVideoStream;
    }

    public int getScanCodeSet() {
        return this.iCurrentScanCodeSet;
    }

    @Override
    public boolean getScrollLockStatus() {
        return this.boolScrollLock;
    }

    @Override
    public boolean getNumLockStatus() {
        return this.boolNumLock;
    }

    @Override
    public boolean getCapsLockStatus() {
        return this.boolCapsLock;
    }

    public boolean sC3KeySettingsNeeded() {
        return this.boolNeedSC3Settings;
    }

    public byte getALTKeyState() {
        return this.altStat;
    }

    public byte getCTRLKeyState() {
        return this.ctrlStat;
    }

    public void setCurrentScanCodeSet(int n) {
        this.iCurrentScanCodeSet = n;
    }

    public int getCurrentScanCodeSet() {
        return this.iCurrentScanCodeSet;
    }

    public void setNeedSC3Settings(boolean bl) {
        this.boolNeedSC3Settings = bl;
    }

    public boolean getNeedSC3Settings() {
        return this.boolNeedSC3Settings;
    }

    private static byte[] concatenateArrays(byte[][] byArray) {
        int n;
        int n2 = 0;
        int n3 = 0;
        int n4 = byArray.length;
        for (n = 0; n < n4; ++n) {
            if (byArray[n] == null) continue;
            n3 += byArray[n].length;
        }
        byte[] byArray2 = new byte[n3];
        for (n = 0; n < n4; ++n) {
            if (byArray[n] == null) continue;
            int n5 = byArray[n].length;
            int n6 = 0;
            while (n6 < n5) {
                byArray2[n2] = byArray[n][n6];
                ++n6;
                ++n2;
            }
        }
        return byArray2;
    }

    private void setShiftCtrlAltStatus(short s, boolean bl) {
        switch (s) {
            case 225: {
                this.isLShiftDown = bl;
                break;
            }
            case 229: {
                this.isRShiftDown = bl;
                break;
            }
            case 224: {
                this.isLControlDown = bl;
                break;
            }
            case 228: {
                this.isRControlDown = bl;
                break;
            }
            case 226: {
                this.isLAltDown = bl;
                break;
            }
            case 230: {
                this.isRAltDown = bl;
            }
        }
        this.isAnyShiftDown = this.isLShiftDown || this.isRShiftDown;
        this.isAnyControlDown = this.isLControlDown || this.isRControlDown;
        this.isAnyAltDown = this.isLAltDown || this.isRAltDown;
    }

    private int getCtrlAltEmulationKey(short s) {
        switch (s) {
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: 
            case 64: 
            case 65: 
            case 66: 
            case 67: 
            case 68: 
            case 69: {
                return s - 59;
            }
            case 85: 
            case 86: 
            case 87: {
                return s - 74;
            }
        }
        return -1;
    }

    public boolean setSunKeyState(short s, boolean bl, boolean bl2) {
        byte[] byArray = null;
        boolean bl3 = false;
        int n = bl ? 0 : 1;
        this.keyState[s] = (byte)(bl ? 1 : 0);
        switch (s) {
            case 224: 
            case 225: 
            case 226: 
            case 228: 
            case 229: 
            case 230: {
                this.setShiftCtrlAltStatus(s, bl);
            }
        }
        byArray = HIDtoSun.sunCodes[n][s];
        if (s == 72 && bl) {
            this.pauseStat = true;
        }
        if (this.pauseStat && s != 72) {
            this.pauseStat = false;
            if (s == 4) {
                byArray = HIDtoSun.sunStopASequence;
                bl2 = true;
            }
        } else if (this.isAnyControlDown && this.isAnyAltDown && this.getCtrlAltEmulationKey(s) > -1) {
            int n2 = this.getCtrlAltEmulationKey(s);
            byArray = PS2Keyboard.concatenateArrays(new byte[][]{this.isLControlDown ? HIDtoSun.sunCodes[n][224] : null, this.isRControlDown ? HIDtoSun.sunCodes[n][228] : null, this.isLAltDown ? HIDtoSun.sunCodes[n][226] : null, this.isRAltDown ? HIDtoSun.sunCodes[n][230] : null, HIDtoSun.sunEmulationRemap[n2]});
        } else if (KeyHIDValue.keyboardLanguage != 2 && s == 230) {
            byArray = HIDtoSun.sunCodes[n][226];
        }
        if (byArray != null && byArray.length > 0 && byArray[0] != 0) {
            bl3 = this.outputKeyData(byArray, 0, byArray.length);
        }
        return bl2 ? bl3 & this.flushKeyData() : bl3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean setKeyState(short s, boolean bl, boolean bl2) {
        boolean bl3 = false;
        if (s == 0) {
            if (bl2) {
                bl3 &= this.flushKeyData();
            }
            return bl3;
        }
        if (this.iCurrentScanCodeSet == 3 && !this.getNumLockStatus()) {
            if (s == 89) {
                s = (short)77;
            } else if (s == 90) {
                s = (short)81;
            } else if (s == 91) {
                s = (short)78;
            } else if (s == 92) {
                s = (short)80;
            } else if (s == 94) {
                s = (short)79;
            } else if (s == 95) {
                s = (short)74;
            }
            if (s == 96) {
                s = (short)82;
            }
            if (s == 97) {
                s = (short)75;
            }
            if (s == 98) {
                s = (short)73;
            }
            if (s == 99) {
                s = (short)76;
            }
        }
        if (this.isTargetType()) {
            return this.setSunKeyState(s, bl, bl2);
        }
        PS2Keyboard pS2Keyboard = this;
        synchronized (pS2Keyboard) {
            this.keyState[s] = (byte)(bl ? 1 : 0);
            byte[] byArray = HIDtoPS2.scanCodes[this.iCurrentScanCodeSet - 1][bl ? 0 : 1][s];
            switch (this.iCurrentScanCodeSet) {
                case 1: {
                    switch (s) {
                        case 70: {
                            if (bl) {
                                byArray = HIDtoPS2.makePrScrSC1;
                                break;
                            }
                            byArray = HIDtoPS2.breakPrScrSC1;
                            break;
                        }
                        case 72: {
                            if (!this.isAnyControlDown) break;
                            byArray = HIDtoPS2.scanCodes[this.iCurrentScanCodeSet - 1][bl ? 0 : 1][120];
                        }
                    }
                }
                case 2: {
                    if (s > 223) {
                        this.setShiftCtrlAltStatus(s, bl);
                    }
                    switch (s) {
                        case 72: {
                            if (!this.isAnyControlDown) break;
                            byArray = HIDtoPS2.scanCodes[this.iCurrentScanCodeSet - 1][bl ? 0 : 1][120];
                            break;
                        }
                        case 70: {
                            if (bl) {
                                if (this.isAnyAltDown) {
                                    byArray = HIDtoPS2.makeAlt_AND_PrScrSC2;
                                    break;
                                }
                                if (this.isAnyControlDown || this.isAnyShiftDown) {
                                    byArray = HIDtoPS2.makeCtl_OR_Shift_AND_PrScrSC2;
                                    break;
                                }
                                byArray = HIDtoPS2.makePrScrSC2;
                                break;
                            }
                            if (this.isAnyAltDown) {
                                byArray = HIDtoPS2.breakAlt_AND_PrScrSC2;
                                break;
                            }
                            if (this.isAnyControlDown || this.isAnyShiftDown) {
                                byArray = HIDtoPS2.breakCtl_OR_Shift_AND_PrScrSC2;
                                break;
                            }
                            byArray = HIDtoPS2.breakPrScrSC2;
                            break;
                        }
                        case 84: {
                            if (!this.isAnyShiftDown) break;
                            byArray = PS2Keyboard.concatenateArrays(new byte[][]{this.isLShiftDown && bl ? HIDtoPS2.fakeLShift[this.iCurrentScanCodeSet - 1][1] : null, this.isRShiftDown && bl ? HIDtoPS2.fakeRShift[this.iCurrentScanCodeSet - 1][1] : null, byArray, this.isRShiftDown && !bl ? HIDtoPS2.fakeRShift[this.iCurrentScanCodeSet - 1][0] : null, this.isLShiftDown && !bl ? HIDtoPS2.fakeLShift[this.iCurrentScanCodeSet - 1][0] : null});
                            break;
                        }
                        case 73: 
                        case 74: 
                        case 75: 
                        case 76: 
                        case 77: 
                        case 78: 
                        case 79: 
                        case 80: 
                        case 81: 
                        case 82: {
                            if (this.isAnyShiftDown == this.boolNumLock) break;
                            byArray = this.boolNumLock ? PS2Keyboard.concatenateArrays(new byte[][]{HIDtoPS2.fakeLShift[this.iCurrentScanCodeSet - 1][0], byArray, HIDtoPS2.fakeLShift[this.iCurrentScanCodeSet - 1][1]}) : PS2Keyboard.concatenateArrays(new byte[][]{this.isLShiftDown && bl ? HIDtoPS2.fakeLShift[this.iCurrentScanCodeSet - 1][1] : null, this.isRShiftDown && bl ? HIDtoPS2.fakeRShift[this.iCurrentScanCodeSet - 1][1] : null, byArray, this.isRShiftDown && !bl ? HIDtoPS2.fakeRShift[this.iCurrentScanCodeSet - 1][0] : null, this.isLShiftDown && !bl ? HIDtoPS2.fakeLShift[this.iCurrentScanCodeSet - 1][0] : null});
                        }
                    }
                    break;
                }
                case 3: {
                    int n;
                    if (bl || byArray.length <= 1) break;
                    int n2 = n = byArray[1] < 0 ? 256 + byArray[1] : byArray[1];
                    if (this.bySC3KeySettings[n] != -7 && this.bySC3KeySettings[n] != -9) break;
                    byArray = null;
                }
            }
            if (byArray != null) {
                bl3 = this.outputKeyData(byArray, 0, byArray.length);
            }
            if (bl2) {
                bl3 &= this.flushKeyData();
            }
        }
        return bl3;
    }

    @Override
    public boolean getKeyState(short s) {
        return this.keyState[s] != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean releaseAllPressedKeys() {
        boolean bl = true;
        PS2Keyboard pS2Keyboard = this;
        synchronized (pS2Keyboard) {
            for (short s = 0; s < 256; s = (short)((short)(s + 1))) {
                if (this.keyState[s] == 0) continue;
                bl &= this.setKeyState(s, false, false);
            }
        }
        return bl &= this.flushKeyData();
    }

    @Override
    public boolean releaseKeyIfPressed(short s) {
        boolean bl = true;
        if (this.keyState[s] != 0) {
            bl = this.setKeyState(s, false, false);
        }
        return bl;
    }

    @Override
    public boolean toogleKeyState(short s) {
        boolean bl = this.setKeyState(s, true, false);
        return bl &= this.setKeyState(s, false, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean breakAll() throws Exception {
        boolean bl = true;
        PS2Keyboard pS2Keyboard = this;
        synchronized (pS2Keyboard) {
            for (int i = 0; i < 6; ++i) {
                bl &= this.setKeyState(this.breakKeys[i], false, false);
            }
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScanCode3Data(byte[] byArray, int n) {
        PS2Keyboard pS2Keyboard = this;
        synchronized (pS2Keyboard) {
            if (byArray[0] == -1) {
                for (int i = 0; i < 141; ++i) {
                    this.bySC3KeySettings[i] = byArray[i + 1];
                }
            } else {
                block9: for (int i = 0; i < n; ++i) {
                    byte by = byArray[i];
                    byte by2 = byArray[i + 1];
                    byte by3 = by;
                    switch (by3) {
                        case -9: 
                        case -8: 
                        case -7: 
                        case -6: {
                            for (int j = 0; j < 256; ++j) {
                                this.bySC3KeySettings[j] = by;
                            }
                            continue block9;
                        }
                        case -5: 
                        case -4: 
                        case -3: {
                            this.bySC3KeySettings[by2] = (byte)(by - 4);
                            ++i;
                            continue block9;
                        }
                        case -52: {
                            continue block9;
                        }
                    }
                }
            }
            this.boolNeedSC3Settings = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScanCodeSet(int n) {
        PS2Keyboard pS2Keyboard = this;
        synchronized (pS2Keyboard) {
            if (n != this.iCurrentScanCodeSet && n == 3) {
                this.boolNeedSC3Settings = true;
            }
            this.iCurrentScanCodeSet = n;
        }
    }

    @Override
    public void setLEDState(boolean bl, boolean bl2, boolean bl3) {
        this.boolScrollLock = bl;
        this.boolNumLock = bl2;
        this.boolCapsLock = bl3;
    }

    protected void setDefaultSC3KeySettings() {
        this.setAllSC3KeySettings((byte)-9);
        this.bySC3KeySettings[17] = -8;
        this.bySC3KeySettings[18] = -8;
        this.bySC3KeySettings[20] = -8;
        this.bySC3KeySettings[25] = -8;
        this.bySC3KeySettings[57] = -7;
        this.bySC3KeySettings[88] = -7;
        this.bySC3KeySettings[89] = -8;
        this.bySC3KeySettings[136] = -8;
        this.bySC3KeySettings[140] = -8;
        this.bySC3KeySettings[141] = -8;
        this.bySC3KeySettings[103] = -7;
        this.bySC3KeySettings[110] = -7;
        this.bySC3KeySettings[101] = -7;
        this.bySC3KeySettings[111] = -7;
        this.bySC3KeySettings[109] = -7;
        this.bySC3KeySettings[118] = -7;
        this.bySC3KeySettings[108] = -7;
        this.bySC3KeySettings[107] = -7;
        this.bySC3KeySettings[105] = -7;
        this.bySC3KeySettings[119] = -7;
        this.bySC3KeySettings[117] = -7;
        this.bySC3KeySettings[115] = -7;
        this.bySC3KeySettings[114] = -7;
        this.bySC3KeySettings[112] = -7;
        this.bySC3KeySettings[126] = -7;
        this.bySC3KeySettings[125] = -7;
        this.bySC3KeySettings[116] = -7;
        this.bySC3KeySettings[122] = -7;
        this.bySC3KeySettings[113] = -7;
        this.bySC3KeySettings[132] = -7;
        this.bySC3KeySettings[121] = -7;
        this.bySC3KeySettings[8] = -7;
        this.bySC3KeySettings[7] = -7;
        this.bySC3KeySettings[15] = -7;
        this.bySC3KeySettings[23] = -7;
        this.bySC3KeySettings[31] = -7;
        this.bySC3KeySettings[39] = -7;
        this.bySC3KeySettings[47] = -7;
        this.bySC3KeySettings[55] = -7;
        this.bySC3KeySettings[63] = -7;
        this.bySC3KeySettings[71] = -7;
        this.bySC3KeySettings[79] = -7;
        this.bySC3KeySettings[86] = -7;
        this.bySC3KeySettings[94] = -7;
        this.bySC3KeySettings[87] = -7;
        this.bySC3KeySettings[95] = -7;
        this.bySC3KeySettings[100] = -7;
    }

    protected void setAllSC3KeySettings(byte by) {
        for (int i = 0; i < 142; ++i) {
            this.bySC3KeySettings[i] = by;
        }
    }

    @Override
    public boolean outputKeyData(byte[] byArray, int n, int n2) {
        try {
            boolean bl;
            for (bl = false; bl < byArray.length; bl += 1) {
                boolean bl2;
                int n3 = byArray[bl] & 0x7F;
                boolean bl3 = bl2 = (byArray[bl] & 0x80) != 0;
                if (!bl2) continue;
                n3 |= 0x80;
            }
            bl = true;
            if (n2 + this.iKBDataCount > 16) {
                bl = this.flushKeyData();
            }
            System.arraycopy(byArray, n, this.byKBBuffer, this.iKBDataCount, n2);
            this.iKBDataCount += n2;
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    @Override
    public boolean flushKeyData() {
        try {
            boolean bl = true;
            if (this.iKBDataCount != 0) {
                bl = this.sendKeyData(this.iKBDataCount, this.byKBBuffer);
                this.iKBDataCount = 0;
            }
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean sendKeyData(int n, byte[] byArray) {
        return this.videoStream.sendKeyData(n, byArray);
    }

    protected void clearKeyStatus() {
        for (int i = 0; i < 256; ++i) {
            this.keyState[i] = 0;
        }
        this.altStat = 0;
        this.ctrlStat = 0;
        this.shiftStat = 0;
        this.setLEDState(false, false, false);
    }

    @Override
    public synchronized boolean isTargetType() {
        return this.targetType;
    }

    @Override
    public synchronized void setTargetType(boolean bl) {
        this.targetType = bl;
    }
}

