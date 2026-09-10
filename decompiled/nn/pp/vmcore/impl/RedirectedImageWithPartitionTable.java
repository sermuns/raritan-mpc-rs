/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.RedirectedImage;

public class RedirectedImageWithPartitionTable
extends RedirectedImage {
    private static final int FAT16_PARTITION_TYPE = 6;
    private static final int FAT32_PARTITION_TYPE = 11;
    private static final int NTFS_PARTITION_TYPE = 7;
    private static final UUID BASIC_DATA_PARTITION_GUID = UUID.fromString("EBD0A0A2-B9E5-4433-87C0-68B6B72699C7");
    private static final int FAT_MAGIC_POS = 54;
    private static final int NTFS_MAGIC_POS = 3;
    private static final String FAT16_MAGIC_STRING = "FAT16   ";
    private static final String FAT32_MAGIC_STRING = "FAT32   ";
    private static final String NTFS_MAGIC_STRING = "NTFS    ";
    private static final long MAX_32_BIT_SECTORNO = 0xFFFFFFFFL;
    private static final int HEADER_SECTORS = 256;
    private static final int FOOTER_SECTORS = 64;
    private boolean useMbr = false;
    private byte[] header = new byte[131072];
    private byte[] footer = new byte[32768];

    public RedirectedImageWithPartitionTable(File file, boolean bl, Logger logger) {
        super(file, bl, logger);
    }

    @Override
    public synchronized void determineGeometry() throws IOException {
        super.determineGeometry();
        this.createMbrIfNeeded();
    }

    @Override
    public synchronized void readSectors(long l, long l2, byte[] byArray) throws IOException {
        this.doReadSectors(l, l2, byArray, 0);
    }

    @Override
    public synchronized void writeSectors(long l, long l2, byte[] byArray) throws IOException, VMException {
        if (this.readOnly) {
            throw new VMException(T._("Writing not supported"));
        }
        this.doWriteSectors(l, l2, byArray, 0);
    }

    private long crc32(byte[] byArray, int n, int n2) {
        CRC32 cRC32 = new CRC32();
        cRC32.update(byArray, n, n2);
        return cRC32.getValue();
    }

    private long crc32(byte[] byArray) {
        return this.crc32(byArray, 0, byArray.length);
    }

    private boolean matchMagic(String string, int n, String string2) throws IOException {
        byte[] byArray = new byte[string2.length()];
        try {
            long l = this.file.getFilePointer();
            this.file.seek(n);
            this.file.readFully(byArray, 0, string2.length());
            this.file.seek(l);
        }
        catch (IOException iOException) {
            return false;
        }
        String string3 = new String(byArray);
        if (!string2.equals(string3)) {
            this.logger.log(Level.FINE, "Image file is not a " + string + " image, tag doesn't match (" + string3 + " - " + string2 + ")");
            return false;
        }
        this.logger.log(Level.FINE, "Image file is " + string + " image");
        return true;
    }

    private boolean isFat16() throws IOException {
        return this.matchMagic("FAT16", 54, FAT16_MAGIC_STRING);
    }

    private boolean isFat32() throws IOException {
        return this.matchMagic("FAT32", 54, FAT32_MAGIC_STRING);
    }

    private boolean isNtfs() throws IOException {
        return this.matchMagic("NTFS", 3, NTFS_MAGIC_STRING);
    }

    private void createMbrIfNeeded() throws IOException {
        byte by;
        if (!this.isRemovable(this.file)) {
            return;
        }
        if (this.isFat16()) {
            by = 6;
        } else if (this.isFat32()) {
            by = 11;
        } else if (this.isNtfs()) {
            by = 7;
        } else {
            return;
        }
        if (this.sectorCount > 0xFFFFFFFFL) {
            this.createGpt();
        } else {
            this.createMbr(by);
        }
        this.lastSectorNo += 320L;
        this.useMbr = true;
    }

    private void createMbr(byte by) throws IOException {
        Mbr mbr = new Mbr();
        mbr.partTable[0].status = (byte)-128;
        mbr.partTable[0].chsStart[0] = -1;
        mbr.partTable[0].chsStart[1] = -1;
        mbr.partTable[0].chsStart[2] = -1;
        mbr.partTable[0].partType = by;
        mbr.partTable[0].chsLast[0] = -1;
        mbr.partTable[0].chsLast[1] = -1;
        mbr.partTable[0].chsLast[2] = -1;
        mbr.partTable[0].lbaFirstBlock = 256L;
        mbr.partTable[0].size = this.sectorCount;
        byte[] byArray = mbr.toByteArray();
        System.arraycopy(byArray, 0, this.header, 0, byArray.length);
    }

    private void createGpt() throws IOException {
        long l;
        long l2;
        long l3;
        PrimaryGpt primaryGpt = new PrimaryGpt();
        long l4 = this.sectorCount + 256L + 64L;
        long l5 = l4 > 0xFFFFFFFFL ? 0xFFFFFFFFL : l4;
        primaryGpt.protectiveMbr.partTable[0].status = 0;
        primaryGpt.protectiveMbr.partTable[0].chsStart[0] = 0;
        primaryGpt.protectiveMbr.partTable[0].chsStart[1] = 32;
        primaryGpt.protectiveMbr.partTable[0].chsStart[2] = 0;
        primaryGpt.protectiveMbr.partTable[0].partType = (byte)-18;
        primaryGpt.protectiveMbr.partTable[0].chsLast[0] = -1;
        primaryGpt.protectiveMbr.partTable[0].chsLast[1] = -1;
        primaryGpt.protectiveMbr.partTable[0].chsLast[2] = -1;
        primaryGpt.protectiveMbr.partTable[0].lbaFirstBlock = 1L;
        primaryGpt.protectiveMbr.partTable[0].size = l5;
        primaryGpt.header.myLba = 1L;
        primaryGpt.header.alternateLba = l4 - 1L;
        primaryGpt.header.firstUsableLba = 256L;
        primaryGpt.header.lastUsableLba = l4 - 64L - 1L;
        primaryGpt.header.diskGuid = UUID.randomUUID();
        primaryGpt.header.partitionEntryLba = 2L;
        ((Gpt.GptPartTable)primaryGpt.partitionTable).partTable[0].partitionTypeGuid = BASIC_DATA_PARTITION_GUID;
        ((Gpt.GptPartTable)primaryGpt.partitionTable).partTable[0].uniquePartitionGuid = UUID.randomUUID();
        ((Gpt.GptPartTable)primaryGpt.partitionTable).partTable[0].startingLba = 256L;
        ((Gpt.GptPartTable)primaryGpt.partitionTable).partTable[0].endingLba = this.sectorCount + 256L - 1L;
        ((Gpt.GptPartTable)primaryGpt.partitionTable).partTable[0].attributes = 0L;
        ((Gpt.GptPartTable)primaryGpt.partitionTable).partTable[0].partitionName = "Raritan Virtual Media";
        SecondaryGpt secondaryGpt = new SecondaryGpt(primaryGpt);
        secondaryGpt.header.myLba = primaryGpt.header.alternateLba;
        secondaryGpt.header.alternateLba = 1L;
        secondaryGpt.header.partitionEntryLba = l4 - 33L;
        primaryGpt.header.partitionEntryArrayCrc32 = l3 = this.crc32(primaryGpt.partitionTable.toByteArray());
        secondaryGpt.header.partitionEntryArrayCrc32 = l3;
        primaryGpt.header.headerCrc32 = l2 = this.crc32(primaryGpt.header.toByteArray(), 0, primaryGpt.header.headerSize);
        secondaryGpt.header.headerCrc32 = l = this.crc32(secondaryGpt.header.toByteArray(), 0, secondaryGpt.header.headerSize);
        byte[] byArray = primaryGpt.toByteArray();
        System.arraycopy(byArray, 0, this.header, 0, byArray.length);
        byArray = secondaryGpt.toByteArray();
        System.arraycopy(byArray, 0, this.footer, this.footer.length - byArray.length, byArray.length);
    }

    private void doReadSectors(long l, long l2, byte[] byArray, int n) throws IOException {
        if (!this.useMbr) {
            this.readPartitionSectors(l, l2, byArray, n);
            return;
        }
        if (l < 256L) {
            int n2 = (int)Math.min(l2, 256L - l);
            System.arraycopy(this.header, (int)(l * (long)this.sectorSize), byArray, n, n2 * this.sectorSize);
            l += (long)n2;
            n += n2 * this.sectorSize;
            if ((l2 -= (long)n2) == 0L) {
                return;
            }
            this.doReadSectors(l, l2, byArray, n);
            return;
        }
        if (l < this.sectorCount + 256L) {
            int n3 = (int)Math.min(l2, this.sectorCount + 256L - l);
            this.readPartitionSectors(l - 256L, n3, byArray, n);
            l += (long)n3;
            n += n3 * this.sectorSize;
            if ((l2 -= (long)n3) == 0L) {
                return;
            }
            this.doReadSectors(l, l2, byArray, n);
            return;
        }
        if (l > this.lastSectorNo) {
            return;
        }
        int n4 = (int)Math.min(l2, this.lastSectorNo + 1L - l);
        System.arraycopy(this.footer, (int)(l - this.sectorCount - 256L) * this.sectorSize, byArray, n, n4 * this.sectorSize);
    }

    private void doWriteSectors(long l, long l2, byte[] byArray, int n) throws IOException, VMException {
        if (!this.useMbr) {
            this.writePartitionSectors(l, l2, byArray, n);
            return;
        }
        if (l < 256L) {
            int n2 = (int)Math.min(l2, 256L - l);
            l += (long)n2;
            n += n2 * this.sectorSize;
            if ((l2 -= (long)n2) == 0L) {
                return;
            }
            this.doWriteSectors(l, l2, byArray, n);
            return;
        }
        if (l > this.lastSectorNo) {
            return;
        }
        if (l < this.sectorCount + 256L) {
            int n3 = (int)Math.min(l2, this.sectorCount + 256L - l);
            this.writePartitionSectors(l - 256L, n3, byArray, n);
            return;
        }
    }

    private class SecondaryGpt
    extends Gpt {
        Gpt.GptPartTable partitionTable;
        Gpt.GptHeader header;

        SecondaryGpt(PrimaryGpt primaryGpt) {
            this.partitionTable = new Gpt.GptPartTable(primaryGpt.partitionTable);
            this.header = new Gpt.GptHeader(primaryGpt.header);
        }

        @Override
        void copyToStream(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
            this.partitionTable.copyToStream(monitoringDataOutputStream);
            this.header.copyToStream(monitoringDataOutputStream);
        }
    }

    private class PrimaryGpt
    extends Gpt {
        Mbr protectiveMbr;
        Gpt.GptHeader header;
        Gpt.GptPartTable partitionTable;

        private PrimaryGpt() {
            this.protectiveMbr = new Mbr();
            this.header = (Gpt)this.new Gpt.GptHeader();
            this.partitionTable = (Gpt)this.new Gpt.GptPartTable();
        }

        @Override
        void copyToStream(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
            this.protectiveMbr.copyToStream(monitoringDataOutputStream);
            this.header.copyToStream(monitoringDataOutputStream);
            this.partitionTable.copyToStream(monitoringDataOutputStream);
        }
    }

    private abstract class Gpt
    extends Writable {
        private Gpt() {
        }

        byte[] uuidToByteArray(UUID uUID) {
            int n;
            byte[] byArray = new byte[16];
            long l = uUID.getMostSignificantBits();
            long l2 = uUID.getLeastSignificantBits();
            for (n = 0; n < 8; ++n) {
                byArray[n] = (byte)(l >>> 8 * (7 - n));
            }
            for (n = 8; n < 16; ++n) {
                byArray[n] = (byte)(l2 >>> 8 * (7 - n));
            }
            n = byArray[0];
            byArray[0] = byArray[3];
            byArray[3] = n;
            n = byArray[1];
            byArray[1] = byArray[2];
            byArray[2] = n;
            n = byArray[4];
            byArray[4] = byArray[5];
            byArray[5] = n;
            n = byArray[6];
            byArray[6] = byArray[7];
            byArray[7] = n;
            return byArray;
        }

        class GptPartTable
        extends Writable {
            private Entry[] partTable;

            private GptPartTable() {
                this.partTable = new Entry[128];
                for (int i = 0; i < this.partTable.length; ++i) {
                    this.partTable[i] = new Entry();
                }
            }

            private GptPartTable(GptPartTable gptPartTable) {
                this.partTable = new Entry[128];
                for (int i = 0; i < this.partTable.length; ++i) {
                    this.partTable[i] = new Entry(gptPartTable.partTable[i]);
                }
            }

            @Override
            void copyToStream(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
                for (int i = 0; i < this.partTable.length; ++i) {
                    this.partTable[i].copyToStream(monitoringDataOutputStream);
                }
            }

            class Entry
            extends Writable {
                UUID partitionTypeGuid;
                UUID uniquePartitionGuid;
                long startingLba;
                long endingLba;
                long attributes;
                String partitionName;
                byte[] dummyUuid;

                private Entry() {
                    this.dummyUuid = new byte[16];
                }

                private Entry(Entry entry) {
                    this.dummyUuid = new byte[16];
                    this.partitionTypeGuid = entry.partitionTypeGuid;
                    this.uniquePartitionGuid = entry.uniquePartitionGuid;
                    this.startingLba = entry.startingLba;
                    this.endingLba = entry.endingLba;
                    this.attributes = entry.attributes;
                    this.partitionName = entry.partitionName;
                }

                @Override
                void copyToStream(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
                    if (this.partitionTypeGuid != null) {
                        monitoringDataOutputStream.write(Gpt.this.uuidToByteArray(this.partitionTypeGuid));
                    } else {
                        monitoringDataOutputStream.write(this.dummyUuid);
                    }
                    if (this.uniquePartitionGuid != null) {
                        monitoringDataOutputStream.write(Gpt.this.uuidToByteArray(this.uniquePartitionGuid));
                    } else {
                        monitoringDataOutputStream.write(this.dummyUuid);
                    }
                    monitoringDataOutputStream.writeUnsignedLongLE(this.startingLba);
                    monitoringDataOutputStream.writeUnsignedLongLE(this.endingLba);
                    monitoringDataOutputStream.writeUnsignedLongLE(this.attributes);
                    byte[] byArray = this.partitionName != null ? this.partitionName.getBytes() : new byte[]{};
                    for (int i = 0; i < 36; ++i) {
                        monitoringDataOutputStream.write(i < byArray.length ? byArray[i] : 0);
                        monitoringDataOutputStream.write(0);
                    }
                }
            }
        }

        class GptHeader
        extends Writable {
            byte[] signature;
            int revision;
            int headerSize;
            long headerCrc32;
            long myLba;
            long alternateLba;
            long firstUsableLba;
            long lastUsableLba;
            UUID diskGuid;
            long partitionEntryLba;
            int numberOfPartitionEntries;
            int sizeOfPartitionEntries;
            long partitionEntryArrayCrc32;
            byte[] reservedBlock;

            private GptHeader() {
                this.signature = "EFI PART".getBytes();
                this.revision = 65536;
                this.headerSize = 92;
                this.numberOfPartitionEntries = 128;
                this.sizeOfPartitionEntries = 128;
                this.reservedBlock = new byte[420];
            }

            private GptHeader(GptHeader gptHeader) {
                this.signature = "EFI PART".getBytes();
                this.revision = 65536;
                this.headerSize = 92;
                this.numberOfPartitionEntries = 128;
                this.sizeOfPartitionEntries = 128;
                this.reservedBlock = new byte[420];
                this.headerCrc32 = gptHeader.headerCrc32;
                this.myLba = gptHeader.myLba;
                this.alternateLba = gptHeader.alternateLba;
                this.firstUsableLba = gptHeader.firstUsableLba;
                this.lastUsableLba = gptHeader.lastUsableLba;
                this.diskGuid = gptHeader.diskGuid;
                this.partitionEntryLba = gptHeader.partitionEntryLba;
                this.partitionEntryArrayCrc32 = gptHeader.partitionEntryArrayCrc32;
            }

            @Override
            void copyToStream(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
                monitoringDataOutputStream.write(this.signature);
                monitoringDataOutputStream.writeUnsignedIntLE(this.revision);
                monitoringDataOutputStream.writeUnsignedIntLE(this.headerSize);
                monitoringDataOutputStream.writeUnsignedIntLE(this.headerCrc32);
                monitoringDataOutputStream.writeUnsignedIntLE(0L);
                monitoringDataOutputStream.writeUnsignedLongLE(this.myLba);
                monitoringDataOutputStream.writeUnsignedLongLE(this.alternateLba);
                monitoringDataOutputStream.writeUnsignedLongLE(this.firstUsableLba);
                monitoringDataOutputStream.writeUnsignedLongLE(this.lastUsableLba);
                monitoringDataOutputStream.write(Gpt.this.uuidToByteArray(this.diskGuid));
                monitoringDataOutputStream.writeUnsignedLongLE(this.partitionEntryLba);
                monitoringDataOutputStream.writeUnsignedIntLE(this.numberOfPartitionEntries);
                monitoringDataOutputStream.writeUnsignedIntLE(this.sizeOfPartitionEntries);
                monitoringDataOutputStream.writeUnsignedIntLE(this.partitionEntryArrayCrc32);
                monitoringDataOutputStream.write(this.reservedBlock);
            }
        }
    }

    private class Mbr
    extends Writable {
        byte[] codeArea;
        int diskSignature;
        Entry[] partTable;

        Mbr() {
            this.codeArea = new byte[440];
            this.partTable = new Entry[4];
            for (int i = 0; i < this.partTable.length; ++i) {
                this.partTable[i] = new Entry();
            }
        }

        @Override
        void copyToStream(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
            monitoringDataOutputStream.write(this.codeArea);
            monitoringDataOutputStream.writeUnsignedIntLE(this.diskSignature);
            monitoringDataOutputStream.writeUnsignedShortLE(0);
            for (int i = 0; i < this.partTable.length; ++i) {
                this.partTable[i].copyToStream(monitoringDataOutputStream);
            }
            monitoringDataOutputStream.writeUnsignedShortLE(43605);
        }

        class Entry
        extends Writable {
            static final byte BOOTABLE = -128;
            static final byte NOTBOOTABLE = 0;
            byte status;
            byte[] chsStart;
            byte partType;
            byte[] chsLast;
            long lbaFirstBlock;
            long size;

            Entry() {
                this.chsStart = new byte[3];
                this.chsLast = new byte[3];
            }

            @Override
            void copyToStream(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
                monitoringDataOutputStream.write(this.status);
                monitoringDataOutputStream.write(this.chsStart);
                monitoringDataOutputStream.write(this.partType);
                monitoringDataOutputStream.write(this.chsLast);
                monitoringDataOutputStream.writeUnsignedIntLE(this.lbaFirstBlock);
                monitoringDataOutputStream.writeUnsignedIntLE(this.size);
            }
        }
    }

    private abstract class Writable {
        private Writable() {
        }

        abstract void copyToStream(MonitoringDataOutputStream var1) throws IOException;

        byte[] toByteArray() throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
            MonitoringDataOutputStream monitoringDataOutputStream = new MonitoringDataOutputStream(byteArrayOutputStream);
            this.copyToStream(monitoringDataOutputStream);
            monitoringDataOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
    }
}

