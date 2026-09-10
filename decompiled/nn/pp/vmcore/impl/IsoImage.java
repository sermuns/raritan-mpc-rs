/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.RedirectedObject;

public class IsoImage
extends RedirectableObject {
    private File file;

    public IsoImage(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public String getShortName() {
        return this.file.getName();
    }

    @Override
    public String getLongName() {
        return this.file.getName();
    }

    @Override
    public String getRedirectionType() {
        return T._("Image File");
    }

    @Override
    public VMCore.DriveType getDriveType(Logger logger) throws IOException, VMException {
        RedirectedObject redirectedObject = RedirectedObject.loadRedirectedObject(this, true, logger);
        redirectedObject.open();
        redirectedObject.determineGeometry();
        VMCore.DriveType driveType = redirectedObject.getDriveType();
        redirectedObject.close();
        return driveType;
    }
}

