/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.impl.DriveDev;
import nn.pp.vmcore.impl.DriveEnumeratorUnix;
import nn.pp.vmcore.impl.RedirectedObject;

public class DriveEnumeratorMacOsX
extends DriveEnumeratorUnix {
    private static final String prefix = "disk";

    private String getMainName(String string) {
        String[] stringArray = string.split(prefix);
        if (stringArray == null || stringArray.length != 2) {
            return string;
        }
        String string2 = stringArray[1];
        int n = string2.indexOf(115);
        if (n != -1) {
            string2 = string2.substring(0, n);
        }
        return prefix + string2;
    }

    @Override
    protected List<RedirectableObject> enumerateDevDrives(Logger logger) {
        Vector<DriveDev> vector = new Vector<DriveDev>();
        Vector<RedirectableObject> vector2 = new Vector<RedirectableObject>();
        Vector<String> vector3 = new Vector<String>();
        File file = new File("/dev");
        File[] fileArray = file.listFiles();
        for (int i = 0; fileArray != null && i < fileArray.length; ++i) {
            File object = fileArray[i];
            String string = object.getName();
            String string2 = object.getAbsolutePath();
            if (object.isDirectory() || !string.startsWith(prefix) || this.contains(object)) continue;
            VMCore.DriveType driveType = VMCore.DriveType.UNKNOWN;
            try {
                DriveDev driveDev = new DriveDev(string2, VMCore.DriveType.UNKNOWN);
                RedirectedObject redirectedObject = RedirectedObject.loadRedirectedObject(driveDev, true, logger);
                redirectedObject.open();
                try {
                    redirectedObject.determineGeometry();
                    driveType = redirectedObject.getDriveType();
                }
                catch (Exception exception) {
                    redirectedObject.close();
                    throw exception;
                }
                redirectedObject.close();
                if (driveType == VMCore.DriveType.CDROM) {
                    vector.add(new DriveDev(string2, VMCore.DriveType.CDROM));
                    vector3.add(this.getMainName(string2));
                    continue;
                }
                if (driveType == VMCore.DriveType.HARD_DISK_FULL || driveType == VMCore.DriveType.FLOPPY) {
                    vector.add(new DriveDev(string2, driveType));
                    continue;
                }
                vector.add(new DriveDev(string2, VMCore.DriveType.UNKNOWN));
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        for (RedirectableObject redirectableObject : vector) {
            try {
                if (redirectableObject.getDriveType(logger) == VMCore.DriveType.CDROM) {
                    vector2.add(redirectableObject);
                    continue;
                }
                if (vector3.contains(this.getMainName(redirectableObject.getShortName()))) continue;
                vector2.add(redirectableObject);
            }
            catch (Exception exception) {}
        }
        return vector2;
    }
}

