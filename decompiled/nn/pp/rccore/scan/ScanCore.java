/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan;

import nn.pp.rccore.scan.ScanSession;

public interface ScanCore {
    public static final String LOGGER_NAME = "nn.pp.rccore.scan";

    public ScanSession createScanSessionWithRdmSessionID(String var1, int var2, boolean var3, String var4, String var5, String var6);

    public ScanSession createScanSessionWithEricKey(String var1, int var2, boolean var3, String var4);

    public ScanSession createScanSessionWithUsernameAndPassword(String var1, int var2, boolean var3, String var4, String var5);
}

