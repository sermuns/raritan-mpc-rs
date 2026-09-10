/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.rrc.data.DevicePreferences;

public class ClearDevicePreferences {
    public static void main(String[] stringArray) {
        String[] stringArray2 = DevicePreferences.returnNodes();
        Object var2_2 = null;
        for (int i = 0; i < stringArray2.length; ++i) {
            DevicePreferences.deleteNode(stringArray2[i]);
        }
    }
}

