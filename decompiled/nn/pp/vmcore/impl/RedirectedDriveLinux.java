/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.File;
import java.util.logging.Logger;
import nn.pp.vmcore.impl.RedirectedDriveDev;

public class RedirectedDriveLinux
extends RedirectedDriveDev {
    public RedirectedDriveLinux(File file, boolean bl, Logger logger) {
        super(file, bl, logger);
    }

    @Override
    protected boolean devNameIndicatesHarddisk() {
        if (this.filenameEndsWithNumber()) {
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

    private boolean filenameEndsWithNumber() {
        char c = this.imageFileName.charAt(this.imageFileName.length() - 1);
        return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
    }
}

