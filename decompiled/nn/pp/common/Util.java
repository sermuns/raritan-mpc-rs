/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import nn.pp.core.JVMVersionInfo;
import nn.pp.core.Platform;

public class Util {
    private Util() {
    }

    public static boolean isJavaWithFocusProblem() {
        JVMVersionInfo jVMVersionInfo = JVMVersionInfo.getJVMVersionInfo();
        return Platform.isWindows() && (!jVMVersionInfo.isJava16() ? jVMVersionInfo.getMinorBuildVersion() >= 14 : jVMVersionInfo.getMinorBuildVersion() >= 10);
    }

    public static int[] getPortNumber(String string) {
        int n = -1;
        int n2 = -1;
        String[] stringArray = string.split("[.]");
        n = Integer.parseInt(stringArray[0]);
        n2 = Integer.parseInt(stringArray[1]) + 1;
        return new int[]{n, n2};
    }
}

