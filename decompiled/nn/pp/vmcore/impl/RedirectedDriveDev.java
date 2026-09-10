/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.vmcore.impl.RedirectedImageWithPartitionTable;
import nn.pp.vmcore.impl.RedirectedObject;

public abstract class RedirectedDriveDev
extends RedirectedImageWithPartitionTable {
    private boolean mediumChanging = false;

    public RedirectedDriveDev(File file, boolean bl, Logger logger) {
        super(file, bl, logger);
    }

    @Override
    public synchronized void open() throws IOException {
        this.file = new BlockDevice(this.imageFile, this.readOnly ? "r" : "rw");
        if (this.file == null) {
            throw new IOException(T._("Could not open file"));
        }
        this.mediumChanging = false;
    }

    @Override
    protected synchronized boolean isRemovable(RandomAccessFile randomAccessFile) throws IOException {
        boolean bl;
        boolean bl2 = bl = super.isRemovable(randomAccessFile) || super.isFixed(randomAccessFile);
        if (!bl) {
            return bl;
        }
        return !this.devNameIndicatesHarddisk();
    }

    @Override
    protected synchronized boolean isFixed(RandomAccessFile randomAccessFile) throws IOException {
        boolean bl;
        boolean bl2 = bl = super.isRemovable(randomAccessFile) || super.isFixed(randomAccessFile);
        if (!bl) {
            return bl;
        }
        return this.devNameIndicatesHarddisk();
    }

    protected abstract boolean devNameIndicatesHarddisk();

    @Override
    public synchronized RedirectedObject.MediumChangeState getMediumChangeState() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.imageFile, "r");
            if (this.mediumChanging) {
                return RedirectedObject.MediumChangeState.CHANGED;
            }
            long l = randomAccessFile.length();
            long l2 = l / (long)this.sectorSize;
            randomAccessFile.close();
            if (l == 0L || l2 == this.sectorCount) {
                return RedirectedObject.MediumChangeState.NO_CHANGE;
            }
            return RedirectedObject.MediumChangeState.CHANGED;
        }
        catch (IOException iOException) {
            if (this.mediumChanging) {
                return RedirectedObject.MediumChangeState.NO_CHANGE;
            }
            return RedirectedObject.MediumChangeState.REMOVED;
        }
    }

    @Override
    public synchronized void mediumRemovedForChange() throws Exception {
        this.mediumChanging = true;
    }

    @Override
    public synchronized void mediumInsertedAfterChange() throws Exception {
        this.mediumChanging = false;
    }

    private class BlockDevice
    extends RandomAccessFile {
        long length;
        private int hops;
        private static final int MatchSectorSize = 512;
        private static final int Match = 0;
        private static final int Before = 1;
        private static final int After = 2;

        public BlockDevice(File file, String string) throws FileNotFoundException {
            super(file, string);
            this.length = 0L;
            this.hops = 0;
        }

        public BlockDevice(String string, String string2) throws FileNotFoundException {
            super(string, string2);
            this.length = 0L;
            this.hops = 0;
        }

        @Override
        public long length() throws IOException {
            if (this.length != 0L) {
                return this.length;
            }
            this.length = super.length();
            if (this.length != 0L) {
                return this.length;
            }
            this.length = this.detectLength();
            return this.length;
        }

        private int match(long l) throws IOException {
            long l2 = l * 512L;
            byte[] byArray = new byte[512];
            ++this.hops;
            this.seek(l2);
            try {
                this.readFully(byArray);
            }
            catch (Exception exception) {
                return 2;
            }
            try {
                this.readFully(byArray);
            }
            catch (Exception exception) {
                return 0;
            }
            return 1;
        }

        private long findStartSearchSector() throws IOException {
            long l = 1024L;
            while (this.match(l) != 2) {
                l *= 8L;
            }
            return l;
        }

        private long findLengthSector(long l, long l2) throws IOException {
            if (l > l2) {
                return -1L;
            }
            long l3 = l + (l2 - l) / 2L;
            int n = this.match(l3);
            if (n == 1) {
                return this.findLengthSector(l3 + 1L, l2);
            }
            if (n == 2) {
                return this.findLengthSector(l, l3 - 1L);
            }
            return l3;
        }

        private long detectLength() throws IOException {
            long l = this.findLengthSector(0L, this.findStartSearchSector());
            if (l < 0L) {
                throw new IOException(T._("Could not detect block device length"));
            }
            return (l + 1L) * 512L;
        }
    }
}

