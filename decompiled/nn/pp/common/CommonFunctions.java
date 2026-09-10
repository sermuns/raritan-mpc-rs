/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import com.util.kbd.KeyHIDValue;
import com.util.kbd.KeyboardKey;
import com.util.kbd.KeyboardMappings;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import nn.pp.common.ui.helpers.IKVMTargetViewer;
import nn.pp.common.ui.helpers.IMacroCreatorDialog;
import nn.pp.core.T;
import nn.pp.logging.RemoteConsoleLogger;
import nn.pp.rccore.KeyboardMacro;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping;
import nn.pp.rccore.impl.keyboard.CharTranslatorMappingBase;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_da_DK;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_de_CH;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_de_DE;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_en_GB;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_es_ES;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_fr_BE;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_fr_FR;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_hu_HU;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_it_IT;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_no_NO;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_pt_PT;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_sl_SL;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_sv_SE;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping_us_intl;

public class CommonFunctions
implements CharTranslatorConstants {
    private static final String PRESS_TEXT = T._("Press ");
    private static final String RELEASE_TEXT = T._("Release ");

    private static Vector<Integer> constructKeyEventList(char c, Hashtable<Character, Vector<Integer>> hashtable) {
        Collection collection = hashtable.get(Character.valueOf(c));
        if (collection == null) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "No lookup code available for character: [" + c + "] - skipping");
            return new Vector<Integer>(0);
        }
        Vector<Integer> vector = new Vector<Integer>(collection);
        if (Character.isUpperCase(c)) {
            if (vector.contains(0x1E0000)) {
                vector.set(vector.indexOf(0x1E0000), 131072);
            } else if (!vector.contains(131072) && !vector.contains(262144)) {
                vector.insertElementAt(131072, 0);
            }
        }
        boolean bl = false;
        boolean bl2 = false;
        block7: for (int i = 0; i < vector.size(); ++i) {
            switch ((Integer)vector.get(i)) {
                case 131072: {
                    vector.remove(i);
                    if (bl) {
                        --i;
                        continue block7;
                    }
                    bl = true;
                    vector.insertElementAt(16, i);
                    continue block7;
                }
                case 262144: 
                case 393216: {
                    vector.remove(i);
                    if (bl) {
                        bl = false;
                        vector.insertElementAt(65552, i);
                        continue block7;
                    }
                    --i;
                    continue block7;
                }
                case 524288: {
                    vector.remove(i);
                    if (bl2) {
                        --i;
                        continue block7;
                    }
                    bl2 = true;
                    vector.insertElementAt(65406, i);
                    continue block7;
                }
                case 0x100000: 
                case 0x180000: {
                    vector.remove(i);
                    if (bl2) {
                        bl2 = false;
                        vector.insertElementAt(130942, i);
                        continue block7;
                    }
                    --i;
                    continue block7;
                }
                case 0x1E0000: {
                    vector.remove(i);
                    vector.insertElementAt(393216, i);
                    vector.insertElementAt(0x180000, i);
                    --i;
                    continue block7;
                }
            }
        }
        if (bl2) {
            vector.add(130942);
        }
        if (bl) {
            vector.add(65552);
        }
        return vector;
    }

    private static String convertKeyCodeToMacroCode(int n, KeyboardMappings keyboardMappings) {
        String string;
        int n2 = (n | 0x10000) ^ 0x10000;
        String string2 = string = (n & 0x10000) == 65536 ? "r " : "p ";
        if (n2 == 65406) {
            n2 = 274;
        }
        short s = KeyHIDValue.HIDMAP[n2];
        for (int i = 0; i < keyboardMappings.length(); ++i) {
            KeyboardKey keyboardKey = keyboardMappings.getItem(i);
            if (keyboardKey.getKeyCode() != s || keyboardKey.getKeyName().contains("ms Delay")) continue;
            return string + String.valueOf(keyboardKey.getIndex());
        }
        RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "Character HIDMAP table: Code not found: " + n2);
        return "";
    }

    private static CharTranslatorMapping getCharMappings(int n) {
        switch (n) {
            case 8: {
                return new CharTranslatorMapping_da_DK();
            }
            case 2: {
                return new CharTranslatorMapping_en_GB();
            }
            case 0: 
            case 1: 
            case 5: {
                return new CharTranslatorMappingBase();
            }
            case 4: {
                return new CharTranslatorMapping_fr_FR();
            }
            case 6: {
                return new CharTranslatorMapping_fr_BE();
            }
            case 3: {
                return new CharTranslatorMapping_de_DE();
            }
            case 10: {
                return new CharTranslatorMapping_de_CH();
            }
            case 11: {
                return new CharTranslatorMapping_hu_HU();
            }
            case 13: {
                return new CharTranslatorMapping_it_IT();
            }
            case 7: {
                return new CharTranslatorMapping_no_NO();
            }
            case 15: {
                return new CharTranslatorMapping_pt_PT();
            }
            case 14: {
                return new CharTranslatorMapping_sl_SL();
            }
            case 12: {
                return new CharTranslatorMapping_es_ES();
            }
            case 9: {
                return new CharTranslatorMapping_sv_SE();
            }
        }
        return new CharTranslatorMapping_us_intl();
    }

    private static Vector<Integer> constructKeyListFromText(String string, CharTranslatorMapping charTranslatorMapping) {
        int n;
        Vector<Integer> vector = new Vector<Integer>();
        for (n = 0; n < string.length(); ++n) {
            vector.addAll(CommonFunctions.constructKeyEventList(string.charAt(n), charTranslatorMapping));
        }
        if (vector.size() > 1) {
            int n2 = 65535;
            block4: for (n = 1; n < vector.size(); ++n) {
                int n3 = vector.get(n);
                int n4 = vector.get(n - 1);
                switch (n3) {
                    case 16: 
                    case 17: 
                    case 18: 
                    case 65406: {
                        if (n3 != (n4 & n2) || n3 == n4) continue block4;
                        vector.remove(n);
                        vector.remove(n - 1);
                        --n;
                        continue block4;
                    }
                }
            }
        }
        return vector;
    }

    public static void sendTextToTarget(int n, String string, IKVMTargetViewer iKVMTargetViewer) {
        CharTranslatorMapping charTranslatorMapping = CommonFunctions.getCharMappings(n);
        Vector<Integer> vector = CommonFunctions.constructKeyListFromText(string, charTranslatorMapping);
        KeyboardMacro keyboardMacro = new KeyboardMacro("", "", null, false);
        Vector<KeyboardMacro.KeyCode> vector2 = new Vector<KeyboardMacro.KeyCode>();
        int n2 = 65535;
        if (vector.size() < 1) {
            return;
        }
        int n3 = vector.get(0);
        block4: for (int i = 0; i < vector.size(); ++i) {
            n3 = vector.get(i);
            switch (n3 & n2) {
                case 65406: {
                    KeyboardMacro keyboardMacro2 = keyboardMacro;
                    keyboardMacro2.getClass();
                    vector2.add(keyboardMacro2.new KeyboardMacro.KeyCode(iKVMTargetViewer.getEricKeyCode(n3 & n2, '\u0000', 3), (n3 & 0x10000) == 0));
                    continue block4;
                }
                case 16: 
                case 17: 
                case 18: {
                    KeyboardMacro keyboardMacro3 = keyboardMacro;
                    keyboardMacro3.getClass();
                    vector2.add(keyboardMacro3.new KeyboardMacro.KeyCode(iKVMTargetViewer.getEricKeyCode(n3 & n2, '\u0000', 2), (n3 & 0x10000) == 0));
                    continue block4;
                }
                default: {
                    KeyboardMacro keyboardMacro4 = keyboardMacro;
                    keyboardMacro4.getClass();
                    vector2.add(keyboardMacro4.new KeyboardMacro.KeyCode(iKVMTargetViewer.getEricKeyCode(n3 & n2, '\u0000', 1), (n3 & 0x10000) == 0));
                }
            }
        }
        keyboardMacro = new KeyboardMacro("", "", vector2.toArray(new KeyboardMacro.KeyCode[0]), false);
        iKVMTargetViewer.sendKeyboardMacroDirect(keyboardMacro);
    }

    private static int spaceNeededForKeyReleases(Set<String> set) {
        switch (set.size()) {
            case 0: {
                return 0;
            }
        }
        int n = 0;
        for (String string : set) {
            n += string.length() + 2;
        }
        return n;
    }

    public static void constructMacroFromText(int n, String string, KeyboardMappings keyboardMappings, IMacroCreatorDialog iMacroCreatorDialog) {
        KeyHIDValue.setHIDMap(n);
        CharTranslatorMapping charTranslatorMapping = CommonFunctions.getCharMappings(n);
        Vector<Integer> vector = CommonFunctions.constructKeyListFromText(string, charTranslatorMapping);
        if (vector.size() < 1) {
            return;
        }
        HashSet<String> hashSet = new HashSet<String>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CommonFunctions.convertKeyCodeToMacroCode(vector.get(0), keyboardMappings));
        for (int i = 1; i < vector.size(); ++i) {
            String string2 = CommonFunctions.convertKeyCodeToMacroCode(vector.get(i), keyboardMappings);
            if (string2.startsWith("p")) {
                hashSet.add(string2.replaceFirst("p", "r"));
            }
            if (stringBuilder.length() + string2.length() + CommonFunctions.spaceNeededForKeyReleases(hashSet) > 8192) {
                RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "Converting text to macro results in a macro that's too long, truncating...");
                if (string2.startsWith("p")) {
                    hashSet.remove(string2.replaceFirst("p", "r"));
                }
                for (String string3 : hashSet) {
                    stringBuilder.append("&&" + string3);
                }
                break;
            }
            if (string2.startsWith("r")) {
                hashSet.remove(string2);
            }
            stringBuilder.append("&&" + string2);
        }
        iMacroCreatorDialog.fillSequenceModel(stringBuilder.toString());
    }

    /*
     * Enabled aggressive block sorting
     */
    public static Vector<String> getMacroSequenceForModel(String string, KeyboardMappings keyboardMappings) {
        String[] stringArray = string.split("&&");
        Vector<String> vector = new Vector<String>();
        if (stringArray.length == 0 || stringArray[0].length() < 3) {
            return vector;
        }
        String string2 = "";
        block5: for (int i = 0; i < stringArray.length && stringArray[i].length() > 0; ++i) {
            char c = stringArray[i].charAt(0);
            int n = Integer.parseInt(stringArray[i].substring(2));
            switch (c) {
                case 'p': {
                    if (n == 112 || n == 113) continue block5;
                    string2 = PRESS_TEXT + keyboardMappings.getItem(n).getKeyName();
                    break;
                }
                case 'r': {
                    string2 = (n == 112 || n == 113 ? "" : RELEASE_TEXT) + keyboardMappings.getItem(n).getKeyName();
                    break;
                }
                case 's': {
                    string2 = keyboardMappings.getItem(n).getKeyName();
                }
            }
            vector.add(string2);
        }
        return vector;
    }

    public static String getGNULicenseHTML(String string) {
        return "<html>" + T._("Open Source License Notification") + "<br>&nbsp;&nbsp;" + T._("Raritan, Inc. uses Open Source software in this product,") + "<br>&nbsp;&nbsp;" + T._("Including software licensed under the ") + "<a href=\"http://" + string + "/license/licenseGPL.txt\">" + T._("GNU General Public License (\"GPL\")") + "</a><br>&nbsp;&nbsp;" + T._("and the ") + "<a href=\"http://" + string + "/license/licenseLGPL.txt\">" + T._("GNU Lesser General Public License (\"LGPL\")") + "</a></html>";
    }

    public static String getOldLicenseFilePath() {
        return "/license/licences.asp.in";
    }

    public static String getLicenseFilePath() {
        return "/license/licences.asp";
    }

    public static String getLicenseActionDescr() {
        return "get license info.";
    }

    public static String getCopyrightFilePath() {
        return "/license/copyrights.txt";
    }

    public static String getCopyrightActionDescr() {
        return "get copyrights.";
    }
}

