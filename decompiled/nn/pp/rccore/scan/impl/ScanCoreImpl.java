/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl;

import nn.pp.rccore.scan.ScanCore;
import nn.pp.rccore.scan.ScanSession;
import nn.pp.rccore.scan.impl.ScanSessionImpl;

public class ScanCoreImpl
implements ScanCore {
    @Override
    public ScanSession createScanSessionWithEricKey(String string, int n, boolean bl, String string2) {
        return new ScanSessionImpl(string, n, bl, null, string2, null, null, null, null);
    }

    @Override
    public ScanSession createScanSessionWithRdmSessionID(String string, int n, boolean bl, String string2, String string3, String string4) {
        return new ScanSessionImpl(string, n, bl, string2, null, null, null, string3, string4);
    }

    @Override
    public ScanSession createScanSessionWithUsernameAndPassword(String string, int n, boolean bl, String string2, String string3) {
        return new ScanSessionImpl(string, n, bl, null, null, string2, string3, null, null);
    }
}

