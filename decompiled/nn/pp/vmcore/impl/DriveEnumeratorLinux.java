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

public class DriveEnumeratorLinux
extends DriveEnumeratorUnix {
    @Override
    protected List<RedirectableObject> enumerateDevDrives(Logger logger) {
        Object object;
        Vector<DriveDev> vector = new Vector<DriveDev>();
        Vector<RedirectableObject> vector2 = new Vector<RedirectableObject>();
        File file = new File("/dev");
        File[] fileArray = file.listFiles();
        for (int i = 0; fileArray != null && i < fileArray.length; ++i) {
            File comparable = fileArray[i];
            object = comparable.getName();
            if (comparable.isDirectory()) continue;
            if (((String)object).startsWith("fd") && ((String)object).length() < 4 && !this.contains(comparable)) {
                vector.add(new DriveDev(comparable.getAbsolutePath(), VMCore.DriveType.FLOPPY));
            }
            if (((String)object).startsWith("cdrom") && !((String)object).startsWith("cdrom-sg") && !this.contains(comparable)) {
                vector.add(new DriveDev(comparable.getAbsolutePath(), VMCore.DriveType.CDROM));
            }
            if ((((String)object).startsWith("scd") || ((String)object).startsWith("sr")) && !this.contains(comparable)) {
                vector.add(new DriveDev(comparable.getAbsolutePath(), VMCore.DriveType.CDROM));
            }
            if (!((String)object).startsWith("hd") && !((String)object).startsWith("sd") || this.contains(comparable)) continue;
            vector.add(new DriveDev(comparable.getAbsolutePath(), VMCore.DriveType.UNKNOWN));
        }
        for (DriveDev driveDev : vector) {
            try {
                object = RedirectedObject.loadRedirectedObject(driveDev, true, logger);
                ((RedirectedObject)object).open();
                if (driveDev.getDriveType() == VMCore.DriveType.UNKNOWN) {
                    try {
                        ((RedirectedObject)object).determineGeometry();
                    }
                    catch (Exception exception) {
                        ((RedirectedObject)object).close();
                        throw exception;
                    }
                    VMCore.DriveType driveType = ((RedirectedObject)object).getDriveType();
                    if (driveType == VMCore.DriveType.HARD_DISK_FULL) {
                        driveDev.setDriveType(driveType);
                    }
                }
                ((RedirectedObject)object).close();
                vector2.add(driveDev);
            }
            catch (Exception exception) {}
        }
        return vector2;
    }
}

