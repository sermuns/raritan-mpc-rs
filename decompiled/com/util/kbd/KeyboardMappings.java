/*
 * Decompiled with CFR 0.152.
 */
package com.util.kbd;

import com.util.kbd.KeyboardKey;
import java.util.ArrayList;
import nn.pp.core.T;

public class KeyboardMappings {
    private ArrayList<KeyboardKey> keyMappings = new ArrayList();

    public int length() {
        return this.keyMappings.size();
    }

    public KeyboardKey getItem(int n) {
        if (n < 0 || n >= this.keyMappings.size()) {
            return null;
        }
        return this.keyMappings.get(n);
    }

    public KeyboardMappings() {
        this.keyMappings.add(0, new KeyboardKey(0, T._("Left Ctrl"), 224, 2));
        this.keyMappings.add(1, new KeyboardKey(1, T._("Right Ctrl"), 228, 3));
        this.keyMappings.add(2, new KeyboardKey(2, T._("Left Alt"), 226, 2));
        this.keyMappings.add(3, new KeyboardKey(3, T._("Right Alt"), 230, 3));
        this.keyMappings.add(4, new KeyboardKey(4, T._("Left Shift"), 225, 2));
        this.keyMappings.add(5, new KeyboardKey(5, T._("Right Shift"), 229, 3));
        this.keyMappings.add(6, new KeyboardKey(6, T._("Scroll Lock"), 71, 1));
        this.keyMappings.add(7, new KeyboardKey(7, T._("Caps Lock"), 57, 1));
        this.keyMappings.add(8, new KeyboardKey(8, T._("Num Lock"), 83, 4));
        this.keyMappings.add(9, new KeyboardKey(9, T._("Left Windows Key"), 227, 2));
        this.keyMappings.add(10, new KeyboardKey(10, T._("Right Windows Key"), 231, 3));
        this.keyMappings.add(11, new KeyboardKey(11, T._("Menu Key"), 101, 1));
        this.keyMappings.add(12, new KeyboardKey(12, T._("Print Screen/SysRq"), 70, 1));
        this.keyMappings.add(13, new KeyboardKey(13, T._("Pause"), 72, 1));
        this.keyMappings.add(14, new KeyboardKey(14, T._("ESC"), 41, 1));
        this.keyMappings.add(15, new KeyboardKey(15, T._("Enter"), 40, 1));
        this.keyMappings.add(16, new KeyboardKey(16, T._("Delete"), 76, 1));
        this.keyMappings.add(17, new KeyboardKey(17, T._("Insert"), 73, 1));
        this.keyMappings.add(18, new KeyboardKey(18, T._("Space Bar"), 44, 1));
        this.keyMappings.add(19, new KeyboardKey(19, T._("Tab"), 43, 1));
        this.keyMappings.add(20, new KeyboardKey(20, T._("Backspace"), 42, 1));
        this.keyMappings.add(21, new KeyboardKey(21, T._("Home"), 74, 1));
        this.keyMappings.add(22, new KeyboardKey(22, T._("End"), 77, 1));
        this.keyMappings.add(23, new KeyboardKey(23, T._("Page Up"), 75, 1));
        this.keyMappings.add(24, new KeyboardKey(24, T._("Page Down"), 78, 1));
        this.keyMappings.add(25, new KeyboardKey(25, T._("Right Arrow"), 79, 1));
        this.keyMappings.add(26, new KeyboardKey(26, T._("Left Arrow"), 80, 1));
        this.keyMappings.add(27, new KeyboardKey(27, T._("Up Arrow"), 82, 1));
        this.keyMappings.add(28, new KeyboardKey(28, T._("Down Arrow"), 81, 1));
        this.keyMappings.add(29, new KeyboardKey(29, T._("Key Pad ."), 99, 4));
        this.keyMappings.add(30, new KeyboardKey(30, T._("Key Pad 0"), 98, 4));
        this.keyMappings.add(31, new KeyboardKey(31, T._("Key Pad 1"), 89, 4));
        this.keyMappings.add(32, new KeyboardKey(32, T._("Key Pad 2"), 90, 4));
        this.keyMappings.add(33, new KeyboardKey(33, T._("Key Pad 3"), 91, 4));
        this.keyMappings.add(34, new KeyboardKey(34, T._("Key Pad 4"), 92, 4));
        this.keyMappings.add(35, new KeyboardKey(35, T._("Key Pad 5"), 93, 4));
        this.keyMappings.add(36, new KeyboardKey(36, T._("Key Pad 6"), 94, 4));
        this.keyMappings.add(37, new KeyboardKey(37, T._("Key Pad 7"), 95, 4));
        this.keyMappings.add(38, new KeyboardKey(38, T._("Key Pad 8"), 96, 4));
        this.keyMappings.add(39, new KeyboardKey(39, T._("Key Pad 9"), 97, 4));
        this.keyMappings.add(40, new KeyboardKey(40, T._("Key Pad Enter"), 88, 4));
        this.keyMappings.add(41, new KeyboardKey(41, T._("Key Pad +"), 87, 4));
        this.keyMappings.add(42, new KeyboardKey(42, T._("Key Pad -"), 86, 4));
        this.keyMappings.add(43, new KeyboardKey(43, T._("Key Pad *"), 85, 4));
        this.keyMappings.add(44, new KeyboardKey(44, T._("Key Pad /"), 84, 4));
        this.keyMappings.add(45, new KeyboardKey(45, T._("F1"), 58, 1));
        this.keyMappings.add(46, new KeyboardKey(46, T._("F2"), 59, 1));
        this.keyMappings.add(47, new KeyboardKey(47, T._("F3"), 60, 1));
        this.keyMappings.add(48, new KeyboardKey(48, T._("F4"), 61, 1));
        this.keyMappings.add(49, new KeyboardKey(49, T._("F5"), 62, 1));
        this.keyMappings.add(50, new KeyboardKey(50, T._("F6"), 63, 1));
        this.keyMappings.add(51, new KeyboardKey(51, T._("F7"), 64, 1));
        this.keyMappings.add(52, new KeyboardKey(52, T._("F8"), 65, 1));
        this.keyMappings.add(53, new KeyboardKey(53, T._("F9"), 66, 1));
        this.keyMappings.add(54, new KeyboardKey(54, T._("F10"), 67, 1));
        this.keyMappings.add(55, new KeyboardKey(55, T._("F11"), 68, 1));
        this.keyMappings.add(56, new KeyboardKey(56, T._("F12"), 69, 1));
        this.keyMappings.add(57, new KeyboardKey(57, T._("A"), 4, 1));
        this.keyMappings.add(58, new KeyboardKey(58, T._("B"), 5, 1));
        this.keyMappings.add(59, new KeyboardKey(59, T._("C"), 6, 1));
        this.keyMappings.add(60, new KeyboardKey(60, T._("D"), 7, 1));
        this.keyMappings.add(61, new KeyboardKey(61, T._("E"), 8, 1));
        this.keyMappings.add(62, new KeyboardKey(62, T._("F"), 9, 1));
        this.keyMappings.add(63, new KeyboardKey(63, T._("G"), 10, 1));
        this.keyMappings.add(64, new KeyboardKey(64, T._("H"), 11, 1));
        this.keyMappings.add(65, new KeyboardKey(65, T._("I"), 12, 1));
        this.keyMappings.add(66, new KeyboardKey(66, T._("J"), 13, 1));
        this.keyMappings.add(67, new KeyboardKey(67, T._("K"), 14, 1));
        this.keyMappings.add(68, new KeyboardKey(68, T._("L"), 15, 1));
        this.keyMappings.add(69, new KeyboardKey(69, T._("M"), 16, 1));
        this.keyMappings.add(70, new KeyboardKey(70, T._("N"), 17, 1));
        this.keyMappings.add(71, new KeyboardKey(71, T._("O"), 18, 1));
        this.keyMappings.add(72, new KeyboardKey(72, T._("P"), 19, 1));
        this.keyMappings.add(73, new KeyboardKey(73, T._("Q"), 20, 1));
        this.keyMappings.add(74, new KeyboardKey(74, T._("R"), 21, 1));
        this.keyMappings.add(75, new KeyboardKey(75, T._("S"), 22, 1));
        this.keyMappings.add(76, new KeyboardKey(76, T._("T"), 23, 1));
        this.keyMappings.add(77, new KeyboardKey(77, T._("U"), 24, 1));
        this.keyMappings.add(78, new KeyboardKey(78, T._("V"), 25, 1));
        this.keyMappings.add(79, new KeyboardKey(79, T._("W"), 26, 1));
        this.keyMappings.add(80, new KeyboardKey(80, T._("X"), 27, 1));
        this.keyMappings.add(81, new KeyboardKey(81, T._("Y"), 28, 1));
        this.keyMappings.add(82, new KeyboardKey(82, T._("Z"), 29, 1));
        this.keyMappings.add(83, new KeyboardKey(83, T._("1"), 30, 1));
        this.keyMappings.add(84, new KeyboardKey(84, T._("2"), 31, 1));
        this.keyMappings.add(85, new KeyboardKey(85, T._("3"), 32, 1));
        this.keyMappings.add(86, new KeyboardKey(86, T._("4"), 33, 1));
        this.keyMappings.add(87, new KeyboardKey(87, T._("5"), 34, 1));
        this.keyMappings.add(88, new KeyboardKey(88, T._("6"), 35, 1));
        this.keyMappings.add(89, new KeyboardKey(89, T._("7"), 36, 1));
        this.keyMappings.add(90, new KeyboardKey(90, T._("8"), 37, 1));
        this.keyMappings.add(91, new KeyboardKey(91, T._("9"), 38, 1));
        this.keyMappings.add(92, new KeyboardKey(92, T._("0"), 39, 1));
        this.keyMappings.add(93, new KeyboardKey(93, T._("- (Minus)"), 45, 1));
        this.keyMappings.add(94, new KeyboardKey(94, T._("= (Equals)"), 46, 1));
        this.keyMappings.add(95, new KeyboardKey(95, T._("[ (Left Bracket)"), 47, 1));
        this.keyMappings.add(96, new KeyboardKey(96, T._("] (Right Bracket)"), 48, 1));
        this.keyMappings.add(97, new KeyboardKey(97, T._("\\ (Back Slash)"), 49, 1));
        this.keyMappings.add(98, new KeyboardKey(98, T._("; (Semi-colon)"), 51, 1));
        this.keyMappings.add(99, new KeyboardKey(99, T._("' (Apostrophe)"), 52, 1));
        this.keyMappings.add(100, new KeyboardKey(100, T._("` (Grave)"), 53, 1));
        this.keyMappings.add(101, new KeyboardKey(101, T._(", (Comma)"), 54, 1));
        this.keyMappings.add(102, new KeyboardKey(102, T._(". (Period)"), 55, 1));
        this.keyMappings.add(103, new KeyboardKey(103, T._("/ (Slash)"), 56, 1));
        this.keyMappings.add(104, new KeyboardKey(104, T._("Japan Kana"), 136, 1));
        this.keyMappings.add(105, new KeyboardKey(105, T._("Japan Convert"), 138, 1));
        this.keyMappings.add(106, new KeyboardKey(106, T._("Japan No Convert"), 139, 1));
        this.keyMappings.add(107, new KeyboardKey(107, T._("Japan Yen"), 137, 1));
        this.keyMappings.add(108, new KeyboardKey(108, T._("Japan Circumflex"), 46, 1));
        this.keyMappings.add(109, new KeyboardKey(109, T._("Japan @"), 47, 1));
        this.keyMappings.add(110, new KeyboardKey(110, T._("Japan :"), 52, 1));
        this.keyMappings.add(111, new KeyboardKey(111, T._("Japan Kanji"), 53, 1));
        this.keyMappings.add(112, new KeyboardKey(112, T._("25ms Delay"), 25, 0));
        this.keyMappings.add(113, new KeyboardKey(113, T._("100ms Delay"), 100, 0));
        this.keyMappings.add(114, new KeyboardKey(114, T._("Korea Hanja"), 145, 1));
        this.keyMappings.add(115, new KeyboardKey(115, T._("Korea Hangul"), 144, 1));
        this.keyMappings.add(116, new KeyboardKey(116, T._("Japan Ro"), 135, 1));
        this.keyMappings.add(117, new KeyboardKey(117, T._("Sun Stop"), 120, 0));
        this.keyMappings.add(118, new KeyboardKey(118, T._("Sun Again"), 121, 0));
        this.keyMappings.add(119, new KeyboardKey(119, T._("Sun Undo"), 122, 0));
        this.keyMappings.add(120, new KeyboardKey(120, T._("Sun Cut"), 123, 0));
        this.keyMappings.add(121, new KeyboardKey(121, T._("Sun Copy"), 124, 0));
        this.keyMappings.add(122, new KeyboardKey(122, T._("Sun Paste"), 125, 0));
        this.keyMappings.add(123, new KeyboardKey(123, T._("Sun Find"), 126, 0));
        this.keyMappings.add(124, new KeyboardKey(124, T._("Sun Mute"), 127, 0));
        this.keyMappings.add(125, new KeyboardKey(125, T._("Sun Volume Up"), 128, 0));
        this.keyMappings.add(126, new KeyboardKey(126, T._("Sun Volume Down"), 129, 0));
        this.keyMappings.add(127, new KeyboardKey(127, T._("Sun Props"), 163, 0));
        this.keyMappings.add(128, new KeyboardKey(128, T._("Sun Front"), 234, 0));
        this.keyMappings.add(129, new KeyboardKey(129, T._("Sun Help"), 117, 0));
        this.keyMappings.add(130, new KeyboardKey(130, T._("Sun Compose"), 101, 1));
        this.keyMappings.add(131, new KeyboardKey(131, T._("Sun Open"), 235, 0));
        this.keyMappings.add(132, new KeyboardKey(132, T._("500ms Delay"), 500, 0));
        this.keyMappings.add(133, new KeyboardKey(133, T._("1000ms Delay"), 1000, 0));
        this.keyMappings.add(134, new KeyboardKey(134, T._("< (Less-Than - European KBs)"), 100, 1));
        this.keyMappings.add(135, new KeyboardKey(135, T._("F13"), 104, 1));
        this.keyMappings.add(136, new KeyboardKey(136, T._("F14"), 105, 1));
        this.keyMappings.add(137, new KeyboardKey(137, T._("F15"), 106, 1));
        this.keyMappings.add(138, new KeyboardKey(138, T._("F16"), 107, 1));
    }
}

