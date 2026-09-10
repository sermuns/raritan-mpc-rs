/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.IOException;
import java.util.logging.Logger;
import nn.pp.core.Platform;
import nn.pp.core.T;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.VirtualMediaEventListener;
import nn.pp.vmcore.impl.DriveDev;
import nn.pp.vmcore.impl.DriveNative;
import nn.pp.vmcore.impl.Folder;
import nn.pp.vmcore.impl.IsoImage;
import nn.pp.vmcore.impl.RedirectedDriveLinux;
import nn.pp.vmcore.impl.RedirectedDriveMacOsX;
import nn.pp.vmcore.impl.RedirectedDriveNative;
import nn.pp.vmcore.impl.RedirectedFolder;
import nn.pp.vmcore.impl.RedirectedImage;
import nn.pp.vmcore.impl.VirtualMediaEventListenerList;

public abstract class RedirectedObject {
    protected boolean readOnly;
    protected VMCore.DriveType driveType;
    protected Logger logger;

    protected RedirectedObject(boolean bl, Logger logger) {
        this.readOnly = bl;
        this.logger = logger;
    }

    public static RedirectedObject loadRedirectedObject(RedirectableObject redirectableObject, boolean bl, Logger logger) throws VMException {
        if (redirectableObject instanceof IsoImage) {
            return new RedirectedImage(((IsoImage)redirectableObject).getFile(), bl, logger);
        }
        if (redirectableObject instanceof DriveDev) {
            if (Platform.isMac()) {
                return new RedirectedDriveMacOsX(((DriveDev)redirectableObject).getFile(), bl, logger);
            }
            if (Platform.isLinux()) {
                return new RedirectedDriveLinux(((DriveDev)redirectableObject).getFile(), bl, logger);
            }
        } else {
            if (redirectableObject instanceof DriveNative) {
                return new RedirectedDriveNative((DriveNative)redirectableObject, bl, logger);
            }
            if (redirectableObject instanceof Folder) {
                return new RedirectedFolder(((Folder)redirectableObject).getFolder(), bl, logger);
            }
        }
        throw new VMException(T._("No implementation found!"));
    }

    public VMCore.DriveType getDriveType() {
        return this.driveType;
    }

    public synchronized void lockAccess(boolean bl, VMCore.LockFailBehavior lockFailBehavior, VirtualMediaEventListenerList virtualMediaEventListenerList) throws VMException {
        while (true) {
            try {
                this.lockAccess(bl);
                return;
            }
            catch (VMException vMException) {
                if (lockFailBehavior == VMCore.LockFailBehavior.IGNORE) {
                    return;
                }
                if (lockFailBehavior == VMCore.LockFailBehavior.ASK) {
                    VirtualMediaEventListener.LockFailAction lockFailAction = virtualMediaEventListenerList.fireDriveLockingFailed();
                    if (lockFailAction != VirtualMediaEventListener.LockFailAction.IGNORE) continue;
                    return;
                    if (lockFailAction == VirtualMediaEventListener.LockFailAction.RETRY) continue;
                    throw vMException;
                }
                throw vMException;
            }
            break;
        }
    }

    public abstract void open() throws IOException, VMException;

    public abstract void close();

    protected abstract void lockAccess(boolean var1) throws VMException;

    public abstract void determineGeometry() throws IOException, VMException;

    public abstract int getSectorSize();

    public abstract long getLastSectorNo();

    public abstract void readSectors(long var1, long var3, byte[] var5) throws IOException, VMException;

    public abstract void writeSectors(long var1, long var3, byte[] var5) throws IOException, VMException;

    public synchronized MediumChangeState getMediumChangeState() {
        return MediumChangeState.NO_CHANGE;
    }

    public synchronized void mediumRemovedForChange() throws Exception {
    }

    public synchronized void mediumInsertedAfterChange() throws Exception {
    }

    public static enum MediumChangeState {
        NO_CHANGE,
        REMOVED,
        CHANGED;

    }
}

