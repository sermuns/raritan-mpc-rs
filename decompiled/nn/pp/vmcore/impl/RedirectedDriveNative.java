/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.DriveEnumeratorImpl;
import nn.pp.vmcore.impl.DriveNative;
import nn.pp.vmcore.impl.RedirectedObject;

public class RedirectedDriveNative
extends RedirectedObject {
    private DriveNative nativeDrive;
    private long redirectedNative = 0L;

    private native long loadNative(long var1);

    private native void freeNative(long var1);

    private native int openNative(long var1);

    private native int closeNative(long var1);

    private native int lockAccessNative(long var1, boolean var3);

    private native int getDriveGeometryNative(long var1);

    private native long getSectorSizeNative(long var1);

    private native long getLastSectorNoNative(long var1);

    private native int readSectorsNative(long var1, long var3, long var5, byte[] var7);

    private native int writeSectorsNative(long var1, long var3, long var5, byte[] var7);

    private native RedirectedObject.MediumChangeState getMediumChangeStateNative(long var1);

    private native void mediumRemovedForChangeNative(long var1);

    private native void mediumInsertedAfterChangeNative(long var1);

    public RedirectedDriveNative(DriveNative driveNative, boolean bl, Logger logger) {
        super(bl, logger);
        this.nativeDrive = driveNative;
        this.driveType = driveNative.getDriveType();
    }

    @Override
    public synchronized void open() throws VMException {
        if (this.redirectedNative != 0L) {
            this.freeNative(this.redirectedNative);
        }
        this.redirectedNative = this.loadNative(this.nativeDrive.getNativePointer());
        if (this.redirectedNative == 0L) {
            throw new VMException(T._("Could not open redirected drive object."));
        }
        int n = this.openNative(this.redirectedNative);
        if (n != 0) {
            throw new VMException(n, T._("Could not open drive:") + " " + this.getErrorString(n));
        }
    }

    @Override
    public synchronized void close() {
        if (this.redirectedNative != 0L) {
            this.closeNative(this.redirectedNative);
            this.freeNative(this.redirectedNative);
            this.redirectedNative = 0L;
        }
    }

    @Override
    public synchronized void determineGeometry() throws VMException {
        int n;
        if (this.redirectedNative != 0L && (n = this.getDriveGeometryNative(this.redirectedNative)) != 0) {
            throw new VMException(n, T._("Could not query drive geometry:") + " " + this.getErrorString(n));
        }
    }

    @Override
    public synchronized long getLastSectorNo() {
        if (this.redirectedNative != 0L) {
            return this.getLastSectorNoNative(this.redirectedNative);
        }
        return 1L;
    }

    @Override
    public synchronized int getSectorSize() {
        if (this.redirectedNative != 0L) {
            return (int)this.getSectorSizeNative(this.redirectedNative);
        }
        return 1;
    }

    @Override
    protected synchronized void lockAccess(boolean bl) throws VMException {
        if (this.redirectedNative != 0L) {
            int n = this.lockAccessNative(this.redirectedNative, bl);
            if (!bl && n != 0) {
                throw new VMException(n, T._("Could not lock drive access:") + " " + this.getErrorString(n));
            }
            DriveEnumeratorImpl.driveLocked(this.nativeDrive, !bl);
        }
    }

    @Override
    public synchronized void readSectors(long l, long l2, byte[] byArray) throws VMException {
        int n;
        if (this.redirectedNative != 0L && (n = this.readSectorsNative(this.redirectedNative, l, l2, byArray)) != 0) {
            throw new VMException(n, T._("Could not read from drive:") + " " + this.getErrorString(n));
        }
    }

    @Override
    public synchronized void writeSectors(long l, long l2, byte[] byArray) throws VMException {
        int n;
        if (this.readOnly) {
            throw new VMException(T._("Writing not supported"));
        }
        if (this.redirectedNative != 0L && (n = this.writeSectorsNative(this.redirectedNative, l, l2, byArray)) != 0) {
            throw new VMException(n, T._("Could not read from drive:") + " " + this.getErrorString(n));
        }
    }

    @Override
    public synchronized RedirectedObject.MediumChangeState getMediumChangeState() {
        if (this.redirectedNative != 0L) {
            return this.getMediumChangeStateNative(this.redirectedNative);
        }
        return RedirectedObject.MediumChangeState.NO_CHANGE;
    }

    @Override
    public synchronized void mediumRemovedForChange() throws Exception {
        if (this.redirectedNative != 0L) {
            this.mediumRemovedForChangeNative(this.redirectedNative);
        }
    }

    @Override
    public synchronized void mediumInsertedAfterChange() throws Exception {
        if (this.redirectedNative != 0L) {
            this.mediumInsertedAfterChangeNative(this.redirectedNative);
        }
    }

    private String getErrorString(int n) {
        switch (n) {
            case 0: 
            case 0x62000000: {
                return T._("No error");
            }
            case 0x60000002: {
                return T._("Cannot open drive, probably an access rights problem");
            }
            case 0x60000003: {
                return T._("No Medium inserted or drive access error");
            }
            case 0x60000004: {
                return T._("I/O error while accessing the drive");
            }
            case 0x60000005: {
                return T._("This drive type does not support Drive Redirection");
            }
            case 0x60000006: {
                return T._("Locking drive failed");
            }
            case 1627389959: {
                return T._("Medium changing in progress");
            }
        }
        return T._("Unknown error");
    }
}

