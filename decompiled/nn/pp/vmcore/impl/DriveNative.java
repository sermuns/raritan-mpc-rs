/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.impl.Drive;

public class DriveNative
extends Drive {
    private long nativePointer;

    public DriveNative(String string, VMCore.DriveType driveType, long l) {
        super(string, driveType);
        this.nativePointer = l;
    }

    public long getNativePointer() {
        return this.nativePointer;
    }
}

