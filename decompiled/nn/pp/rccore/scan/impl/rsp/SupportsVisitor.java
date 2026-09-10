/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

import java.io.IOException;
import nn.pp.rccore.scan.ScanCoreException;

public interface SupportsVisitor<T> {
    public void accept(T var1) throws IOException, ScanCoreException;
}

