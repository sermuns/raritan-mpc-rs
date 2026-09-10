/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;
import nn.pp.core.Platform;
import nn.pp.core.T;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.DriveEnumeratorDefault;
import nn.pp.vmcore.impl.DriveEnumeratorLinux;
import nn.pp.vmcore.impl.DriveEnumeratorMacOsX;
import nn.pp.vmcore.impl.DriveEnumeratorWindows;
import nn.pp.vmcore.impl.Folder;
import nn.pp.vmcore.impl.IsoImage;

public abstract class RedirectableObject {
    public static List<RedirectableObject> getAvailableDrives(Logger logger) {
        DriveEnumeratorDefault driveEnumeratorDefault = Platform.isWindows() ? new DriveEnumeratorWindows() : (Platform.isLinux() ? new DriveEnumeratorLinux() : (Platform.isMacOsX() ? new DriveEnumeratorMacOsX() : new DriveEnumeratorDefault()));
        return driveEnumeratorDefault.getAvailableDrives(logger);
    }

    public static RedirectableObject getIsoImage(File file) {
        return new IsoImage(file);
    }

    public static RedirectableObject getRedirectedFolder(File file) {
        return new Folder(file);
    }

    public abstract String getShortName();

    public abstract String getLongName();

    public abstract String getRedirectionType();

    public abstract VMCore.DriveType getDriveType(Logger var1) throws IOException, VMException;

    public String toString() {
        return MessageFormat.format(T._("{0} - {1}"), this.getRedirectionType(), this.getShortName());
    }
}

