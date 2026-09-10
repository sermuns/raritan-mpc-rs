/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.File;
import java.util.logging.Logger;
import nn.pp.vmcore.impl.RedirectedDriveDev;

public class RedirectedDriveMacOsX
extends RedirectedDriveDev {
    public RedirectedDriveMacOsX(File file, boolean bl, Logger logger) {
        super(file, bl, logger);
    }

    @Override
    protected boolean devNameIndicatesHarddisk() {
        String[] stringArray = this.imageFile.getName().split("isk");
        if (stringArray == null || stringArray.length < 2) {
            return false;
        }
        if ((stringArray = stringArray[1].split("s")) != null && stringArray.length >= 2) {
            return false;
        }
        File file = new File("/dev");
        File[] fileArray = file.listFiles();
        for (int i = 0; fileArray != null && i < fileArray.length; ++i) {
            String string;
            File file2 = fileArray[i];
            if (file2.isDirectory() || !(string = file2.getAbsolutePath()).startsWith(this.imageFileName) || string.equals(this.imageFileName)) continue;
            return true;
        }
        return false;
    }
}

