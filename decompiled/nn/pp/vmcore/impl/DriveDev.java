/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.File;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.impl.Drive;

public class DriveDev
extends Drive {
    private File file;

    public DriveDev(String string, VMCore.DriveType driveType) {
        super(string, driveType);
        this.file = new File(string);
    }

    public File getFile() {
        return this.file;
    }
}

