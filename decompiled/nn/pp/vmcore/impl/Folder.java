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

public class Folder
extends RedirectableObject {
    private File folder;

    public Folder(File file) {
        this.folder = file;
    }

    public File getFolder() {
        return this.folder;
    }

    @Override
    public String getLongName() {
        return this.folder.getName();
    }

    @Override
    public String getRedirectionType() {
        return T._("Local Folder");
    }

    @Override
    public String getShortName() {
        return this.folder.getName();
    }

    @Override
    public VMCore.DriveType getDriveType(Logger logger) throws IOException, VMException {
        return VMCore.DriveType.REMOVABLE;
    }
}

