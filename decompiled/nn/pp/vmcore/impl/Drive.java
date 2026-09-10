/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.IOException;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;

public class Drive
extends RedirectableObject
implements Comparable<Drive> {
    protected String driveName;
    protected VMCore.DriveType driveType;

    public Drive(String string, VMCore.DriveType driveType) {
        this.driveName = string;
        this.driveType = driveType;
    }

    @Override
    public VMCore.DriveType getDriveType(Logger logger) throws IOException, VMException {
        return this.getDriveType();
    }

    public VMCore.DriveType getDriveType() {
        return this.driveType;
    }

    public void setDriveType(VMCore.DriveType driveType) {
        this.driveType = driveType;
    }

    private String getDescription() {
        switch (this.driveType) {
            case CDROM: {
                return T._("CD-ROM");
            }
            case HARD_DISK_FULL: {
                return T._("Hard Disk");
            }
            case HARD_DISK_PARTITION: {
                return T._("Hard Disk Partition");
            }
            case REMOVABLE: {
                return T._("Removable");
            }
            case HARD_DISK_FULL_EXTERNAL: {
                return T._("External Hard Disk");
            }
            case HARD_DISK_PARTITION_EXTERNAL: {
                return T._("External Hard Disk Partition");
            }
        }
        return null;
    }

    @Override
    public int compareTo(Drive drive) {
        return this.driveName.compareTo(drive.driveName);
    }

    @Override
    public String getShortName() {
        return this.driveName;
    }

    @Override
    public String getLongName() {
        String string = this.getDescription();
        return string == null ? this.driveName : this.driveName + " (" + string + ")";
    }

    @Override
    public String getRedirectionType() {
        return T._("Local Drive");
    }
}

