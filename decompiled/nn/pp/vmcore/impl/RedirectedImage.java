/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.RedirectedObject;

public class RedirectedImage
extends RedirectedObject {
    protected File imageFile;
    protected String imageFileName;
    protected RandomAccessFile file;
    protected static final int ISO_SECTOR_SIZE = 2048;
    protected static final int FLOPPY_SECTOR_SIZE = 512;
    protected static final int REMOVABLE_SECTOR_SIZE = 512;
    protected static final int FIXED_SECTOR_SIZE = 512;
    protected int sectorSize;
    protected long sectorCount;
    protected long lastSectorNo;
    private static final int TAGLEN = 5;
    private static final int ISO9660_MAGIC_POS = 32769;
    private static final String ISOTAG = "CD001";

    public RedirectedImage(File file, boolean bl, Logger logger) {
        super(bl, logger);
        this.imageFile = file;
        this.imageFileName = file.getAbsolutePath();
    }

    @Override
    public synchronized void open() throws IOException {
        this.file = new RandomAccessFile(this.imageFile, this.readOnly ? "r" : "rw");
        if (this.file == null) {
            throw new IOException(T._("Could not open file"));
        }
    }

    @Override
    public synchronized void close() {
        if (this.file != null) {
            try {
                this.file.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.file = null;
    }

    @Override
    public synchronized void determineGeometry() throws IOException {
        if (this.isIso(this.file)) {
            this.driveType = VMCore.DriveType.CDROM;
            this.sectorSize = 2048;
        } else if (this.isFloppy(this.file)) {
            this.driveType = VMCore.DriveType.FLOPPY;
            this.sectorSize = 512;
        } else if (this.isRemovable(this.file)) {
            this.driveType = VMCore.DriveType.REMOVABLE;
            this.sectorSize = 512;
        } else if (this.isFixed(this.file)) {
            this.driveType = VMCore.DriveType.HARD_DISK_FULL;
            this.sectorSize = 512;
        } else {
            throw new IOException(T._("Invalid image file found."));
        }
        this.sectorCount = this.file.length() / (long)this.sectorSize;
        this.lastSectorNo = this.sectorCount - 1L;
    }

    @Override
    public synchronized long getLastSectorNo() {
        return this.lastSectorNo;
    }

    @Override
    public synchronized int getSectorSize() {
        return this.sectorSize;
    }

    @Override
    protected synchronized void lockAccess(boolean bl) throws VMException {
    }

    @Override
    public synchronized void readSectors(long l, long l2, byte[] byArray) throws IOException {
        this.readPartitionSectors(l, l2, byArray, 0);
    }

    @Override
    public synchronized void writeSectors(long l, long l2, byte[] byArray) throws IOException, VMException {
        if (this.readOnly) {
            throw new VMException(T._("Writing not supported"));
        }
        this.writePartitionSectors(l, l2, byArray, 0);
    }

    protected synchronized boolean isFloppy(RandomAccessFile randomAccessFile) throws IOException {
        long l = randomAccessFile.length();
        if (l > 0x2D0000L || l % 512L != 0L) {
            this.logger.log(Level.FINE, "Image file is not a floppy image!");
            return false;
        }
        this.logger.log(Level.FINE, "Image file is floppy image");
        return true;
    }

    protected synchronized boolean isIso(RandomAccessFile randomAccessFile) throws IOException {
        long l = randomAccessFile.length();
        if (l < 32774L || l % 512L != 0L) {
            this.logger.log(Level.FINE, "Image file is not a ISO image, size doesn't match!");
        }
        byte[] byArray = new byte[5];
        try {
            long l2 = randomAccessFile.getFilePointer();
            randomAccessFile.seek(32769L);
            randomAccessFile.readFully(byArray, 0, 5);
            randomAccessFile.seek(l2);
        }
        catch (IOException iOException) {
            return false;
        }
        String string = new String(byArray);
        if (!ISOTAG.equals(string)) {
            this.logger.log(Level.FINE, "Image file is not a ISO image, tag doesn't match (" + string + " - " + ISOTAG + ")");
            return false;
        }
        this.logger.log(Level.FINE, "Image file is ISO image");
        return true;
    }

    protected synchronized boolean isRemovable(RandomAccessFile randomAccessFile) throws IOException {
        long l = randomAccessFile.length();
        if (l % 512L != 0L) {
            this.logger.log(Level.FINE, "Image file is not a removable image!");
            return false;
        }
        this.logger.log(Level.FINE, "Image file is removable image");
        return true;
    }

    protected synchronized boolean isFixed(RandomAccessFile randomAccessFile) throws IOException {
        return false;
    }

    protected void readPartitionSectors(long l, long l2, byte[] byArray, int n) throws IOException {
        this.file.seek(l * (long)this.sectorSize);
        this.file.readFully(byArray, n, (int)(l2 * (long)this.sectorSize));
    }

    protected void writePartitionSectors(long l, long l2, byte[] byArray, int n) throws IOException, VMException {
        this.file.seek(l * (long)this.sectorSize);
        this.file.write(byArray, n, (int)(l2 * (long)this.sectorSize));
    }
}

