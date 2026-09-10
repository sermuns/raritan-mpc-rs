/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import nn.pp.core.T;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.RedirectedObject;

public class RedirectedFolder
extends RedirectedObject {
    private static boolean doDebug = true;
    private File folder;
    private static final int REMOVABLE_SECTOR_SIZE = 512;
    String m_folder = null;
    int sectorSize = 512;
    static RandomAccessFile log_file;
    BDRVVVFATState m_vvfat;
    int MODE_UNDEFINED = 0;
    int MODE_NORMAL = 1;
    int MODE_MODIFIED = 2;
    int MODE_DIRECTORY = 4;
    int MODE_FAKED = 8;
    int MODE_DELETED = 16;
    int MODE_RENAMED = 32;
    Object[] dlg_options_lock = new Object[]{"Retry", "Ignore", "Abort"};
    Object[] dlg_options_memory = new Object[]{"Ignore", "Abort"};
    Object[] dlg_options_large_file = this.dlg_options_memory;
    boolean file_not_found_except = false;
    int mem_low_counter = 0;
    boolean urgent_warning = false;
    int direntry_size = 32;
    Vector<commit_t> all_commits = new Vector();

    public RedirectedFolder(File file, boolean bl, Logger logger) {
        super(bl, logger);
        this.folder = file;
        this.readOnly = bl;
    }

    @Override
    public void open() throws IOException, VMException {
        try {
            this.build_vvfat(this.readOnly);
        }
        catch (IOException iOException) {
            this.close();
            throw iOException;
        }
        catch (VMException vMException) {
            this.close();
            throw vMException;
        }
    }

    @Override
    public void close() {
        try {
            this.ReleaseLocks(true);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void determineGeometry() throws IOException, VMException {
        this.driveType = VMCore.DriveType.REMOVABLE;
        this.sectorSize = 512;
    }

    @Override
    public int getSectorSize() {
        return this.sectorSize;
    }

    @Override
    public long getLastSectorNo() {
        return this.m_vvfat.total_sectors - 1L;
    }

    @Override
    protected void lockAccess(boolean bl) throws VMException {
    }

    @Override
    public void readSectors(long l, long l2, byte[] byArray) throws IOException, VMException {
        this.ReadSectors_VFat(l, l2, byArray);
    }

    @Override
    public void writeSectors(long l, long l2, byte[] byArray) throws IOException, VMException {
        this.WriteSectors_VFat(l, l2, byArray);
    }

    private void ReleaseLocks(boolean bl) {
        mapping_t mapping_t2 = null;
        if (this.m_vvfat == null) {
            return;
        }
        if (this.m_vvfat.mapping == null) {
            return;
        }
        if (this.m_vvfat.dummy_file_name.exists()) {
            this.m_vvfat.dummy_file_name.delete();
        }
        for (int i = 0; i < this.m_vvfat.mapping.size(); ++i) {
            mapping_t2 = this.m_vvfat.mapping.get((int)i).mapping;
            if (mapping_t2.lock != null) {
                try {
                    mapping_t2.lock.release();
                    mapping_t2.lock = null;
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            if (mapping_t2.file == null) continue;
            try {
                mapping_t2.file.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            mapping_t2.file = null;
        }
        if (bl) {
            this.m_vvfat.fat.clear();
            this.m_vvfat.directory.clear();
            this.m_vvfat.mapping.clear();
            if (this.m_vvfat.image != null) {
                try {
                    this.m_vvfat.image.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.m_vvfat.image_name.delete();
            }
        }
    }

    static void debug(String string, boolean bl, boolean bl2) {
        if (doDebug) {
            if (bl2) {
                System.out.println(string);
            }
            if (bl) {
                try {
                    log_file.writeBytes(string);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
    }

    public direntry_t getDirEntry(byte[] byArray) throws IOException {
        int n;
        direntry_t direntry_t2 = new direntry_t();
        for (n = 0; n < 8; ++n) {
            direntry_t2.name[n] = (char)byArray[n];
        }
        int n2 = 0;
        while (n < 11) {
            direntry_t2.extension[n2] = (char)byArray[n];
            ++n;
            ++n2;
        }
        direntry_t2.attributes = byArray[n];
        direntry_t2.reserved[0] = byArray[++n];
        direntry_t2.reserved[1] = byArray[++n];
        direntry_t2.ctime = byArray[++n + 1] << 8 & 0xFF00 | byArray[n] & 0xFF;
        direntry_t2.cdate = byArray[(n += 2) + 1] << 8 & 0xFF00 | byArray[n] & 0xFF;
        direntry_t2.adate = byArray[(n += 2) + 1] << 8 & 0xFF00 | byArray[n] & 0xFF;
        direntry_t2.begin_hi = byArray[(n += 2) + 1] << 8 & 0xFF00 | byArray[n] & 0xFF;
        direntry_t2.mtime = byArray[(n += 2) + 1] << 8 & 0xFF00 | byArray[n] & 0xFF;
        direntry_t2.mdate = byArray[(n += 2) + 1] << 8 & 0xFF00 | byArray[n] & 0xFF;
        direntry_t2.begin = byArray[(n += 2) + 1] << 8 & 0xFF00 | byArray[n] & 0xFF;
        direntry_t2.size = byArray[(n += 2) + 3] << 24 & 0xFF000000 | byArray[n + 2] << 16 & 0xFF0000 | byArray[n + 1] << 8 & 0xFF00 | byArray[n] & 0xFF;
        return direntry_t2;
    }

    public void fn_init(file_name file_name2) {
        file_name2.len = 0;
        file_name2.sequence_number = 0;
        file_name2.checksum = 256;
    }

    public void build_vvfat(boolean bl) throws IOException, VMException {
        boolean bl2 = false;
        boolean bl3 = false;
        int n = 0;
        this.m_vvfat = new BDRVVVFATState();
        this.m_folder = this.folder.getAbsolutePath();
        File file = new File("log.txt");
        if (file.exists()) {
            file.delete();
        }
        log_file = new RandomAccessFile(file, "rw");
        RedirectedFolder.debug("Build VVFAT\n", bl2, bl3);
        this.m_vvfat.fat_type = 32;
        this.m_vvfat.dir_size = 0L;
        this.m_vvfat.counted_files = 0L;
        RedirectedFolder.debug("Count Foldersize\n", bl2, bl3);
        this.count_size(this.m_folder);
        this.m_vvfat.dir_size /= 1000L;
        this.m_vvfat.dummy_file_name = new File("fat.dummy");
        this.m_vvfat.dummy_file = new RandomAccessFile(this.m_vvfat.dummy_file_name, "rw");
        this.m_vvfat.dummy_file.writeBytes("File-Descriptor-Test");
        this.m_vvfat.dummy_file.close();
        if (this.m_vvfat.counted_files >= 1000L && this.m_vvfat.counted_files < 7500L ? JOptionPane.showConfirmDialog(null, "The Redirection of " + this.m_vvfat.counted_files + " files may take some minutes.\nContinue?", "Warning", 0) == 1 : this.m_vvfat.counted_files >= 7500L && JOptionPane.showConfirmDialog(null, "The Redirection of " + this.m_vvfat.counted_files + " files will take a lot of time and Java VM memory," + " which can cause a KVM-Session timeout\n" + " or an \"Out-of-Memory\"-Error of the Java VM. " + "Continue?", "Warning", 0) == 1) {
            throw new VMException(T._("Building FAT-Structure aborted"));
        }
        if (this.m_vvfat.dir_size <= 4000000L) {
            n = 4;
        } else if (this.m_vvfat.dir_size >= 4000000L && this.m_vvfat.dir_size <= 8000000L) {
            n = 8;
        } else if (this.m_vvfat.dir_size >= 8000000L && this.m_vvfat.dir_size <= 16000000L) {
            n = 16;
        } else if (this.m_vvfat.dir_size >= 16000000L) {
            n = 32;
        }
        this.m_vvfat.sectors_per_cluster = this.m_vvfat.fat_type == 16 ? 64 : n;
        this.m_vvfat.first_sectors_number = 1;
        this.m_vvfat.current_cluster = Integer.MAX_VALUE;
        this.m_vvfat.heads = 63L;
        this.m_vvfat.secs = 63L;
        if (this.m_vvfat.fat_type == 16 && this.m_vvfat.dir_size >= 2000000L) {
            JOptionPane.showMessageDialog(null, "Foldersize is too large for FAT16", "Alert", 0);
            throw new VMException(T._("Directory too large for FAT"));
        }
        this.m_vvfat.cyls = this.m_vvfat.fat_type == 16 ? 1023L : (this.m_vvfat.cyls = (long)(this.m_vvfat.dir_size <= 1000000L ? 1023 : (int)((double)(2L * this.m_vvfat.dir_size) / ((double)(this.m_vvfat.heads * this.m_vvfat.secs) * 0.512))));
        this.m_vvfat.sector_count = this.m_vvfat.total_sectors = this.m_vvfat.cyls * this.m_vvfat.heads * this.m_vvfat.secs;
        if (this.m_vvfat.sector_count > this.m_vvfat.total_sectors) {
            this.m_vvfat.sector_count = this.m_vvfat.total_sectors;
        }
        this.m_vvfat.sector_count = this.m_vvfat.total_sectors;
        this.m_vvfat.downcase_short_names = 1;
        this.init_vvfat();
        if (this.m_vvfat.first_sectors_number == 64) {
            this.init_mbr();
        }
        if (!bl) {
            this.m_vvfat.image_name = new File("tmp_image.vvfat");
            if (this.m_vvfat.image_name.exists()) {
                this.m_vvfat.image_name.delete();
            }
            this.m_vvfat.image = new RandomAccessFile(this.m_vvfat.image_name, "rw");
        }
    }

    public void init_mbr() throws IOException {
        mbr_t mbr_t2 = new mbr_t();
        for (int i = 0; i < 4; ++i) {
            partition_t partition_t2;
            if (i == 0) {
                mbr_t2.partition[i] = partition_t2 = new partition_t();
                partition_t2.attributes = 128;
                partition_t2.start_head = 1;
                partition_t2.start_sector = 1;
                partition_t2.start_cylinder = 0;
                partition_t2.fs_type = this.m_vvfat.fat_type == 12 ? 1 : (this.m_vvfat.fat_type == 16 ? 6 : 11);
                partition_t2.end_head = (int)this.m_vvfat.heads - 1;
                partition_t2.end_sector = 255;
                partition_t2.end_cylinder = 255;
                partition_t2.start_sector_long = this.m_vvfat.secs;
                partition_t2.end_sector_long = this.m_vvfat.sector_count;
                continue;
            }
            mbr_t2.partition[i] = partition_t2 = new partition_t();
        }
        mbr_t2.magic[0] = 85;
        mbr_t2.magic[1] = -86;
        byte[] byArray = mbr_t2.getBytes();
        System.arraycopy(byArray, 0, this.m_vvfat.first_sectors, 446, byArray.length);
    }

    public void count_size(String string) throws IOException {
        boolean bl = false;
        boolean bl2 = false;
        File file = new File(string);
        RedirectedFolder.debug("Reading File/Folder " + file + "\n", bl, bl2);
        File[] fileArray = file.listFiles();
        for (int i = 0; i < fileArray.length; ++i) {
            Object object;
            File file2 = fileArray[i];
            if (file2.isDirectory()) {
                RedirectedFolder.debug("File " + file2 + " is a Directory\n", bl, bl2);
                object = string.concat("/").concat(file2.getName());
                this.count_size((String)object);
                continue;
            }
            try {
                object = new RandomAccessFile(new File(file2.getAbsolutePath()), "r");
                if (object == null) continue;
                this.m_vvfat.dir_size += ((RandomAccessFile)object).length();
                ++this.m_vvfat.counted_files;
                ((RandomAccessFile)object).close();
                continue;
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
    }

    public void init_vvfat() throws IOException, VMException {
        direntry_t direntry_t2;
        boolean bl = false;
        boolean bl2 = false;
        bootsector_t bootsector_t2 = new bootsector_t();
        mapping_t mapping_t2 = new mapping_t();
        int n = -1;
        this.m_vvfat.cluster_size = this.m_vvfat.sectors_per_cluster * 512;
        this.m_vvfat.cluster_buffer = new byte[this.m_vvfat.cluster_size];
        int n2 = 1 + this.m_vvfat.sectors_per_cluster * 512 * 8 / this.m_vvfat.fat_type;
        this.m_vvfat.sectors_per_fat = (int)(this.m_vvfat.sector_count + (long)n2) / n2;
        array_t array_t2 = new array_t();
        array_t2.next = 1;
        this.m_vvfat.directory.add(array_t2);
        array_t2.direntry = direntry_t2 = new direntry_t();
        direntry_t2.attributes = (byte)40;
        direntry_t2.name = "DRV_REDI".toCharArray();
        direntry_t2.extension = "R  ".toCharArray();
        this.init_fat();
        this.m_vvfat.faked_sectors = this.m_vvfat.first_sectors_number + this.m_vvfat.sectors_per_fat * 2;
        this.m_vvfat.cluster_count = this.sector2cluster((int)this.m_vvfat.sector_count);
        array_t2 = new array_t();
        this.m_vvfat.mapping.add(array_t2);
        array_t2.next = 1;
        array_t2.mapping = mapping_t2 = new mapping_t();
        mapping_t2.begin = 0L;
        mapping_t2.dir_index = 0;
        mapping_t2.info.dir.parent_mapping_index = -1;
        mapping_t2.first_mapping_index = -1;
        mapping_t2.path = this.m_folder;
        mapping_t2.mode = this.MODE_DIRECTORY;
        mapping_t2.read_only = 0;
        this.m_vvfat.path = mapping_t2.path;
        long l = this.m_vvfat.fat_type == 32 ? 2 : 0;
        for (n2 = 0; n2 < this.m_vvfat.mapping.size(); ++n2) {
            long l2 = 0L;
            n = this.m_vvfat.fat_type != 32 ? (n2 != 0 ? 1 : 0) : -1;
            mapping_t2 = this.m_vvfat.mapping.get((int)n2).mapping;
            if (mapping_t2.mode == this.MODE_DIRECTORY) {
                mapping_t2.begin = l;
                RedirectedFolder.debug("Entering Dir (reading content): " + mapping_t2.path + "\n", bl, bl2);
                this.read_directory(n2);
                mapping_t2 = this.m_vvfat.mapping.get((int)n2).mapping;
            } else {
                mapping_t2.mode = this.MODE_NORMAL;
                mapping_t2.begin = l;
                if (mapping_t2.end > 0L) {
                    direntry_t direntry_t3 = this.m_vvfat.directory.get((int)mapping_t2.dir_index).direntry;
                    mapping_t2.end = l + 1L + (mapping_t2.end - 1L) / (long)this.m_vvfat.cluster_size;
                    this.set_begin_of_direntry(direntry_t3, mapping_t2.begin);
                } else {
                    mapping_t2.end = l + 1L;
                    n = 0;
                }
            }
            if (n != 0) {
                for (l2 = mapping_t2.begin; l2 < mapping_t2.end - 1L; ++l2) {
                    this.fat_set(l2, l2 + 1L);
                }
                this.fat_set(mapping_t2.end - 1L, this.m_vvfat.max_fat_value);
            }
            if ((l = mapping_t2.end) <= this.m_vvfat.cluster_count) continue;
            RedirectedFolder.debug("Directory does not fit in FAT\n" + this.m_vvfat.fat_type + "\n", bl, bl2);
            throw new VMException(T._("Directory too large for FAT"));
        }
        this.m_vvfat.sectors_of_root_directory = (int)mapping_t2.end * this.m_vvfat.sectors_per_cluster;
        this.m_vvfat.last_cluster_of_root_directory = mapping_t2.end;
        this.fat_set(0L, this.m_vvfat.max_fat_value);
        this.fat_set(1L, this.m_vvfat.max_fat_value);
        this.m_vvfat.current_mapping = null;
        bootsector_t2.jump[0] = -21;
        bootsector_t2.jump[1] = 62;
        bootsector_t2.jump[2] = -112;
        bootsector_t2.name = "DRVREDIR".toCharArray();
        bootsector_t2.sector_size = this.sectorSize;
        bootsector_t2.sectors_per_cluster = (byte)this.m_vvfat.sectors_per_cluster;
        bootsector_t2.reserved_sectors = 1;
        bootsector_t2.number_of_fats = (byte)2;
        bootsector_t2.root_entries = this.m_vvfat.fat_type == 16 ? this.m_vvfat.sectors_of_root_directory * 16 : 0;
        bootsector_t2.total_sectors16 = (int)(this.m_vvfat.sector_count > 65535L ? 0L : this.m_vvfat.sector_count);
        bootsector_t2.media_type = (byte)(this.m_vvfat.fat_type != 12 ? 248 : (this.m_vvfat.sector_count == 5760L ? 249 : 248));
        byte[] byArray = this.m_vvfat.fat.firstElement();
        byArray[0] = bootsector_t2.media_type;
        bootsector_t2.sectors_per_fat = this.m_vvfat.fat_type == 16 ? this.m_vvfat.sectors_per_fat : 0;
        bootsector_t2.sectors_per_track = (int)this.m_vvfat.secs;
        bootsector_t2.number_of_heads = (int)this.m_vvfat.heads;
        bootsector_t2.hidden_sectors = this.m_vvfat.first_sectors_number == 1 ? 0 : 63;
        long l3 = bootsector_t2.total_sectors = this.m_vvfat.sector_count > 65535L ? this.m_vvfat.sector_count : 0L;
        if (this.m_vvfat.fat_type == 16) {
            bootsector_t2.union.fat16.drive_number = (byte)(this.m_vvfat.fat_type == 12 ? 0 : 128);
            bootsector_t2.union.fat16.current_head = 0;
            bootsector_t2.union.fat16.signature = (byte)41;
            bootsector_t2.union.fat16.id = -1396894940;
            bootsector_t2.union.fat16.volume_label = "DRVREDIR   ".toCharArray();
            bootsector_t2.union.fat16.fat_type = "FAT16   ".toCharArray();
        } else if (this.m_vvfat.fat_type == 32) {
            bootsector_t2.union.fat32.drive_number = (short)(this.m_vvfat.fat_type == 12 ? 0 : 128);
            bootsector_t2.union.fat32.current_head = 0;
            bootsector_t2.union.fat32.signature = (short)41;
            bootsector_t2.union.fat32.id = 305419896L;
            bootsector_t2.union.fat32.sectors_per_fat = this.m_vvfat.sectors_per_fat;
            bootsector_t2.union.fat32.flags = 0;
            bootsector_t2.union.fat32.major = 0;
            bootsector_t2.union.fat32.minor = 0;
            bootsector_t2.union.fat32.backup_boot_sector = 6;
            bootsector_t2.union.fat32.first_cluster_of_root_directory = 2;
            bootsector_t2.union.fat32.info_sector = 0;
            bootsector_t2.union.fat32.volume_label = "DRVREDIR   ".toCharArray();
            bootsector_t2.union.fat32.fat_type = "FAT32   ".toCharArray();
        }
        bootsector_t2.magic[0] = 85;
        bootsector_t2.magic[1] = 170;
        byte[] byArray2 = bootsector_t2.getBytes();
        System.arraycopy(byArray2, 0, this.m_vvfat.first_sectors, (this.m_vvfat.first_sectors_number - 1) * 512, 512);
    }

    public void read_directory(int n) throws IOException, VMException {
        direntry_t direntry_t2;
        File[] fileArray;
        boolean bl = false;
        boolean bl2 = false;
        RandomAccessFile randomAccessFile = null;
        FileLock fileLock = null;
        mapping_t mapping_t2 = new mapping_t();
        mapping_t2 = this.m_vvfat.mapping.get((int)n).mapping;
        String string = mapping_t2.path;
        long l = mapping_t2.begin;
        int n2 = mapping_t2.info.dir.parent_mapping_index;
        mapping_t mapping_t3 = n2 >= 0 ? this.m_vvfat.mapping.get((int)n2).mapping : null;
        int n3 = (int)(mapping_t3 != null ? mapping_t3.begin : -1L);
        if (string == null) {
            mapping_t2.end = mapping_t2.begin;
            throw new VMException(T._("Building FAT-Structure failed"));
        }
        mapping_t2.info.dir.first_dir_index = l == 0L && this.m_vvfat.fat_type == 16 || l == 2L && this.m_vvfat.fat_type == 32 ? 0 : this.m_vvfat.directory.size();
        File[] fileArray2 = new File[]{new File("."), new File("..")};
        File file = new File(string);
        File[] fileArray3 = file.listFiles();
        if (fileArray3 != null) {
            fileArray = new File[fileArray2.length + fileArray3.length];
            System.arraycopy(fileArray2, 0, fileArray, 0, fileArray2.length);
            System.arraycopy(fileArray3, 0, fileArray, fileArray2.length, fileArray3.length);
        } else {
            fileArray = new File[fileArray2.length];
            System.arraycopy(fileArray2, 0, fileArray, 0, fileArray2.length);
        }
        for (int i = 0; i < fileArray.length; ++i) {
            boolean bl3;
            File file2 = fileArray[i];
            fileLock = null;
            randomAccessFile = null;
            if (file2.isFile()) {
                try {
                    randomAccessFile = new RandomAccessFile(new File(file2.getAbsolutePath()), "rw");
                }
                catch (FileNotFoundException fileNotFoundException) {
                    block36: {
                        try {
                            this.m_vvfat.dummy_file = new RandomAccessFile("fat.dummy", "rw");
                            try {
                                randomAccessFile = new RandomAccessFile(new File(file2.getAbsolutePath()), "r");
                            }
                            catch (FileNotFoundException fileNotFoundException2) {
                                System.out.println("File: " + file2 + " is read-only.");
                            }
                        }
                        catch (FileNotFoundException fileNotFoundException3) {
                            if (this.file_not_found_except) break block36;
                            if (JOptionPane.showConfirmDialog(null, "The OS-dependent limit of File-Handles is reached. No further File-Locking possible.\nThis may lead to inconsistency in files or their content\nContinue without File-Locking?", "Warning", 0) == 1) {
                                throw new VMException(T._("Building FAT-Structure aborted"));
                            }
                            this.ReleaseLocks(false);
                            this.file_not_found_except = true;
                        }
                    }
                    this.m_vvfat.dummy_file.close();
                }
                if (randomAccessFile == null) {
                    RedirectedFolder.debug("Could not open file: " + file2 + " -> File skipped!\n", bl, bl2);
                    continue;
                }
                try {
                    if (!this.file_not_found_except) {
                        fileLock = randomAccessFile.getChannel().tryLock();
                        int n4 = -1;
                        while (fileLock == null) {
                            n4 = JOptionPane.showOptionDialog(null, "Could not obtain a file-lock. This may lead to inconsistency in files or their content\nPlease choose option?", "Warning", 1, 3, null, this.dlg_options_lock, this.dlg_options_lock[2]);
                            if (n4 == 0) {
                                fileLock = randomAccessFile.getChannel().lock();
                                continue;
                            }
                            if (n4 == 1) break;
                            if (n4 != 2) continue;
                            throw new VMException(T._("Building FAT-Structure aborted"));
                        }
                    }
                }
                catch (NonWritableChannelException nonWritableChannelException) {
                    RedirectedFolder.debug("No WritableChannel-Lock: " + file2 + "\n", bl, bl2);
                }
            } else {
                randomAccessFile = null;
            }
            Runtime runtime = Runtime.getRuntime();
            RedirectedFolder.debug("Total Memory = " + runtime.totalMemory() + " Free Memory = " + runtime.freeMemory() + "\n", bl, bl2);
            if ((double)((float)runtime.freeMemory() / (float)runtime.totalMemory() * 100.0f) < 1.0) {
                ++this.mem_low_counter;
            }
            if (this.mem_low_counter > 100 && !this.urgent_warning) {
                this.urgent_warning = true;
                int n5 = JOptionPane.showOptionDialog(null, "The Standard Java VM Memory is almost used up. This usally happens when too many\nfiles/folders are redirected. It's recommended to abort the current process and reduce\nthe number of files/folder or use the redirection of physical drive instead.", "Attention", 0, 3, null, this.dlg_options_memory, this.dlg_options_memory[1]);
                if (n5 == 0) break;
                if (n5 == 1) {
                    throw new VMException(T._("Building FAT-Structure aborted"));
                }
            }
            boolean bl4 = file2.getName().compareTo(".") == 0;
            boolean bl5 = bl3 = file2.getName().compareTo("..") == 0;
            if (bl3 && l == 2L) continue;
            direntry_t2 = this.create_short_and_long_name(this.m_vvfat.directory.indexOf(this.m_vvfat.directory.lastElement()), file2.getName(), bl4 || bl3 ? 1 : 0, mapping_t2.info.dir.first_dir_index);
            direntry_t2.attributes = (byte)(file2.isDirectory() ? 16 : 32);
            direntry_t2.reserved[1] = 0;
            direntry_t2.reserved[0] = 0;
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.setTime(new Date(file2.lastModified()));
            direntry_t2.ctime = calendar.get(13) / 2 | calendar.get(12) << 5 | calendar.get(10) << 11;
            direntry_t2.cdate = calendar.get(5) | calendar.get(2) + 1 << 5 | calendar.get(1) - 1980 << 9;
            direntry_t2.adate = calendar2.get(5) | calendar2.get(2) + 1 << 5 | calendar2.get(1) - 1980 << 9;
            direntry_t2.mtime = calendar.get(13) / 2 | calendar.get(12) << 5 | calendar.get(10) << 11;
            direntry_t2.mdate = calendar.get(5) | calendar.get(2) + 1 << 5 | calendar.get(1) - 1980 << 9;
            direntry_t2.begin_hi = 0;
            if (bl3) {
                this.set_begin_of_direntry(direntry_t2, n3);
            } else if (bl4) {
                this.set_begin_of_direntry(direntry_t2, l);
            } else {
                direntry_t2.begin = 0;
            }
            if (randomAccessFile != null) {
                if (this.m_vvfat.fat_type != 32) {
                    if (randomAccessFile.length() > Integer.MAX_VALUE) {
                        RedirectedFolder.debug("File " + file2.getName() + " is larger than 2GB\n", bl, bl2);
                        throw new VMException(T._("Too large file in folder found"));
                    }
                } else {
                    long l2 = 0xFFFFFFL;
                    if (randomAccessFile.length() >> 8 > l2) {
                        RedirectedFolder.debug("File " + file2.getName() + " is larger than 4GB\n " + randomAccessFile.length() + "\n", bl, bl2);
                        int n6 = JOptionPane.showOptionDialog(null, "The File " + file2.getName() + " is larger than 4GB and wont be mapped!\nContinue?", "Attention", 0, 3, null, this.dlg_options_large_file, this.dlg_options_large_file[1]);
                        if (n6 == 0) continue;
                        if (n6 == 1) {
                            throw new VMException(T._("Too large file in folder found"));
                        }
                    }
                }
            }
            long l3 = direntry_t2.size = file2.isFile() ? randomAccessFile.length() : 0L;
            if (!bl4 && !bl3 && (file2.isDirectory() || file2.isFile() && randomAccessFile.length() > 0L)) {
                array_t array_t2 = new array_t();
                array_t2.next = this.m_vvfat.mapping.lastElement().next + 1;
                this.m_vvfat.mapping.add(array_t2);
                array_t2.mapping = this.m_vvfat.current_mapping = new mapping_t();
                this.m_vvfat.current_mapping.begin = 0L;
                this.m_vvfat.current_mapping.end = randomAccessFile != null ? randomAccessFile.length() : 0L;
                this.m_vvfat.current_mapping.dir_index = this.m_vvfat.directory.lastElement().next - 1;
                this.m_vvfat.current_mapping.first_mapping_index = -1;
                if (file2.isDirectory()) {
                    this.m_vvfat.current_mapping.mode = this.MODE_DIRECTORY;
                    this.m_vvfat.current_mapping.info.dir.parent_mapping_index = n;
                } else {
                    this.m_vvfat.current_mapping.mode = this.MODE_UNDEFINED;
                    this.m_vvfat.current_mapping.info.file.offset = 0L;
                    this.m_vvfat.current_mapping.info.file.size = randomAccessFile.length();
                }
                this.m_vvfat.current_mapping.path = string.concat("/").concat(file2.getName());
                this.m_vvfat.current_mapping.read_only = 0;
                this.m_vvfat.current_mapping.writing_request = false;
                this.m_vvfat.current_mapping.file = randomAccessFile;
                this.m_vvfat.current_mapping.lock = fileLock;
            }
            if (randomAccessFile == null || !this.file_not_found_except) continue;
            randomAccessFile.close();
        }
        while (this.m_vvfat.directory.size() % (this.direntry_size * this.m_vvfat.sectors_per_cluster) != 0) {
            this.m_vvfat.directory.add(new array_t());
        }
        mapping_t2 = this.m_vvfat.mapping.get((int)n).mapping;
        RedirectedFolder.debug("Mapping.end = first_cluster: " + (l += (long)((this.m_vvfat.directory.size() - mapping_t2.info.dir.first_dir_index) * this.direntry_size / this.m_vvfat.cluster_size)) + "\n", bl, bl2);
        mapping_t2.end = l;
        direntry_t2 = this.m_vvfat.directory.get((int)mapping_t2.dir_index).direntry;
        this.set_begin_of_direntry(direntry_t2, mapping_t2.begin);
    }

    public void init_fat() {
        this.m_vvfat.fat.add(new byte[4]);
        switch (this.m_vvfat.fat_type) {
            case 12: {
                this.m_vvfat.max_fat_value = 4095L;
                break;
            }
            case 16: {
                this.m_vvfat.max_fat_value = 65535L;
                break;
            }
            case 32: {
                this.m_vvfat.max_fat_value = 0xFFFFFFFL;
                break;
            }
            default: {
                this.m_vvfat.max_fat_value = 0L;
            }
        }
    }

    public void fat_set(long l, long l2) {
        if ((long)this.m_vvfat.fat.size() < l + 1L) {
            this.m_vvfat.fat.setSize((int)l + 1);
        }
        if (this.m_vvfat.fat_type == 32) {
            byte[] byArray = new byte[]{(byte)(l2 & 0xFFL), (byte)(l2 >> 8 & 0xFFL), (byte)(l2 >> 16 & 0xFFL), (byte)(l2 >> 24 & 0xFL)};
            this.m_vvfat.fat.set((int)l, byArray);
        } else if (this.m_vvfat.fat_type == 16) {
            byte[] byArray = new byte[]{(byte)(l2 & 0xFFL), (byte)(l2 >> 8 & 0xFFL)};
            this.m_vvfat.fat.set((int)l, byArray);
        }
    }

    public synchronized int ReadSectors_VFat(long l, long l2, byte[] byArray) throws IOException {
        boolean bl = false;
        boolean bl2 = false;
        try {
            byte[] byArray2 = new byte[512];
            int n = 0;
            while ((long)n < l2) {
                if (l >= this.m_vvfat.sector_count) {
                    return -1;
                }
                if (l < (long)(this.m_vvfat.faked_sectors - 1)) {
                    if (l < (long)this.m_vvfat.first_sectors_number) {
                        System.arraycopy(this.m_vvfat.first_sectors, (int)l * 512, byArray, n * 512, 512);
                    } else if (l - (long)this.m_vvfat.first_sectors_number < (long)this.m_vvfat.sectors_per_fat) {
                        byte[] byArray3 = null;
                        if ((l - (long)this.m_vvfat.first_sectors_number) * 128L <= (long)this.m_vvfat.fat.size()) {
                            byArray3 = this.m_vvfat.getFATBytes(((int)l - this.m_vvfat.first_sectors_number) * 128);
                        }
                        if (byArray3 != null) {
                            System.arraycopy(byArray3, 0, byArray, n * 512, byArray3.length);
                        } else {
                            System.arraycopy(byArray2, 0, byArray, n * 512, 512);
                        }
                    } else if (l - (long)this.m_vvfat.first_sectors_number - (long)this.m_vvfat.sectors_per_fat < (long)this.m_vvfat.sectors_per_fat) {
                        byte[] byArray4 = null;
                        if ((l - (long)this.m_vvfat.first_sectors_number - (long)this.m_vvfat.sectors_per_fat) * 128L <= (long)this.m_vvfat.fat.size()) {
                            byArray4 = this.m_vvfat.getFATBytes(((int)l - this.m_vvfat.first_sectors_number - this.m_vvfat.sectors_per_fat) * 128);
                        }
                        if (byArray4 != null) {
                            System.arraycopy(byArray4, 0, byArray, n * 512, byArray4.length);
                        } else {
                            System.arraycopy(byArray2, 0, byArray, n * 512, 512);
                        }
                    }
                } else {
                    long l3 = this.m_vvfat.fat_type == 32 ? l - (long)this.m_vvfat.faked_sectors + (long)(this.m_vvfat.sectors_per_cluster * 2) : l - (long)this.m_vvfat.faked_sectors;
                    long l4 = l3 % (long)this.m_vvfat.sectors_per_cluster;
                    long l5 = l3 / (long)this.m_vvfat.sectors_per_cluster;
                    RedirectedFolder.debug("Start reading Sector " + l + " Number of Sectors " + l2 + " Read cluster: " + l5 + " Sector in Cluster: " + l4 + "\n", bl, bl2);
                    if (this.read_cluster((int)l5) != 0) {
                        Arrays.fill(this.m_vvfat.cluster_buffer, (byte)0);
                    } else {
                        RedirectedFolder.debug("" + this.m_vvfat.current_mapping.path + "\n", bl, bl2);
                    }
                    System.arraycopy(this.m_vvfat.cluster_buffer, (int)l4 * 512, byArray, n * 512, 512);
                }
                ++n;
                ++l;
            }
            return 0;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    private synchronized int read_cluster(int n) throws Exception {
        boolean bl = false;
        boolean bl2 = false;
        if (this.m_vvfat.current_cluster != (long)n) {
            int n2 = 0;
            if (this.m_vvfat.current_mapping == null || this.m_vvfat.current_mapping.begin > (long)n || this.m_vvfat.current_mapping.end <= (long)n) {
                mapping_t mapping_t2 = this.find_mapping_for_cluster(n);
                if (mapping_t2 != null && mapping_t2.mode == this.MODE_DIRECTORY) {
                    this.m_vvfat.current_mapping = mapping_t2;
                    RedirectedFolder.debug("Mapping-Element: " + this.m_vvfat.current_mapping.path + "\n", bl, bl2);
                    long l = this.m_vvfat.current_mapping.info.dir.first_dir_index;
                    long l2 = ((long)n - this.m_vvfat.current_mapping.begin) * (long)this.m_vvfat.cluster_size / (long)this.direntry_size;
                    RedirectedFolder.debug("Dir_Offset: " + l + " Cluster_Offset: " + l2 + "\n", bl, bl2);
                    this.m_vvfat.current_cluster = n;
                    Arrays.fill(this.m_vvfat.cluster_buffer, (byte)0);
                    byte[] byArray = this.m_vvfat.getDataBytes((int)l2 + (int)l, this.m_vvfat.cluster_size);
                    System.arraycopy(byArray, 0, this.m_vvfat.cluster_buffer, 0, byArray.length);
                    return 0;
                }
                if (!this.file_not_found_except) {
                    if (mapping_t2 == null) {
                        RedirectedFolder.debug("no mapping found\n", bl, bl2);
                        return -2;
                    }
                    if (this.m_vvfat.current_mapping != null) {
                        this.m_vvfat.current_mapping = null;
                    }
                    this.m_vvfat.current_mapping = mapping_t2;
                } else if (this.open_file(mapping_t2) != 0) {
                    RedirectedFolder.debug("failed opening file / no mapping found\n", bl, bl2);
                    return -2;
                }
            } else if (this.m_vvfat.current_mapping.mode == this.MODE_DIRECTORY) {
                long l = this.m_vvfat.current_mapping.info.dir.first_dir_index;
                long l3 = ((long)n - this.m_vvfat.current_mapping.begin) * (long)this.m_vvfat.cluster_size / 32L;
                RedirectedFolder.debug("Dir_Offset: " + l + " Cluster_Offset: " + l3 + "\n", bl, bl2);
                this.m_vvfat.current_cluster = n;
                Arrays.fill(this.m_vvfat.cluster_buffer, (byte)0);
                byte[] byArray = this.m_vvfat.getDataBytes((int)l3 + (int)l, this.m_vvfat.cluster_size);
                System.arraycopy(byArray, 0, this.m_vvfat.cluster_buffer, 0, byArray.length);
                return 0;
            }
            long l = (long)this.m_vvfat.cluster_size * ((long)n - this.m_vvfat.current_mapping.begin) + this.m_vvfat.current_mapping.info.file.offset;
            if (!this.file_not_found_except) {
                this.m_vvfat.current_fd = this.m_vvfat.current_mapping.file;
            }
            this.m_vvfat.current_fd.seek(l);
            Arrays.fill(this.m_vvfat.cluster_buffer, (byte)0);
            this.m_vvfat.cluster = this.m_vvfat.cluster_buffer;
            n2 = this.m_vvfat.current_fd.read(this.m_vvfat.cluster, 0, this.m_vvfat.cluster_size);
            if (n2 < 0) {
                this.m_vvfat.current_cluster = -1L;
                return -1;
            }
            this.m_vvfat.current_cluster = n;
        }
        return 0;
    }

    public long cluster2sector(long l) {
        if (l >= 2L) {
            return (long)this.m_vvfat.faked_sectors + (long)this.m_vvfat.sectors_per_cluster * (this.m_vvfat.fat_type == 32 ? l - 2L : l);
        }
        return 0L;
    }

    public long sector2cluster(long l) {
        long l2 = l < (long)this.m_vvfat.faked_sectors ? 0L : (l - (long)this.m_vvfat.faked_sectors + (long)(2 * this.m_vvfat.sectors_per_cluster)) / (long)this.m_vvfat.sectors_per_cluster;
        return l2;
    }

    public synchronized boolean modify_fat(byte[] byArray, long l, long l2, long l3) {
        boolean bl = false;
        boolean bl2 = false;
        byte[] byArray2 = new byte[512];
        byte[] byArray3 = new byte[512];
        byte[] byArray4 = null;
        int n = 0;
        int n2 = 0;
        boolean bl3 = false;
        while (l < l2 + l3 && this.sector2cluster(l2) == 0L) {
            if (l > (long)(this.m_vvfat.first_sectors_number + 2 * this.m_vvfat.sectors_per_fat)) {
                RedirectedFolder.debug("Breaked FAT-Research\n", bl, bl2);
                break;
            }
            if (l == 0L) {
                RedirectedFolder.debug("\nMBR at sector: " + l + "\n", bl, bl2);
                System.arraycopy(byArray, (int)(l - l2) * 512, byArray3, 0, 512);
                if (!Arrays.equals(byArray2, new byte[512])) {
                    this.print_data_buffer(byArray3, false);
                }
            }
            if (l >= (long)this.m_vvfat.first_sectors_number && l < (long)(this.m_vvfat.first_sectors_number + 2 * this.m_vvfat.sectors_per_fat)) {
                System.arraycopy(byArray, (int)(l - l2) * 512, byArray2, 0, 512);
                if (!Arrays.equals(byArray2, new byte[512])) {
                    n2 = l >= (long)(this.m_vvfat.first_sectors_number + this.m_vvfat.sectors_per_fat) ? (int)l - this.m_vvfat.first_sectors_number - this.m_vvfat.sectors_per_fat : (int)l - this.m_vvfat.first_sectors_number;
                    RedirectedFolder.debug("\nFAT at sector: " + l + "\n", bl, bl2);
                    for (int i = 0; i < 512; i += 4) {
                        byte[] byArray5 = new byte[4];
                        System.arraycopy(byArray2, i, byArray5, 0, 4);
                        int n3 = byArray5[0] & 0xFF | byArray5[1] << 8 & 0xFF00 | byArray5[2] << 16 & 0xFF0000 | byArray5[3] << 24 & 0xFF000000;
                        if (n3 != 0) {
                            RedirectedFolder.debug("Offset: " + n2 + " ", bl, bl2);
                            if (!(i != 0 && i != 4 || l != (long)this.m_vvfat.first_sectors_number && l != (long)(this.m_vvfat.first_sectors_number + this.m_vvfat.sectors_per_fat))) {
                                RedirectedFolder.debug("Reserved\n", bl, bl2);
                            } else if (n3 == 0xFFFFFFF) {
                                RedirectedFolder.debug("End-Marker found for Cluster: " + (i / 4 + (n2 >= this.m_vvfat.sectors_per_fat ? n2 - this.m_vvfat.sectors_per_fat : n2) * 128) + "\n", bl, bl2);
                            } else {
                                RedirectedFolder.debug("Cluster " + (i / 4 + (n2 >= this.m_vvfat.sectors_per_fat ? n2 - this.m_vvfat.sectors_per_fat : n2) * 128) + "->" + n3 + "\n", bl, bl2);
                            }
                            n = 0;
                            byArray4 = null;
                            if (i / 4 + n2 * 128 < this.m_vvfat.fat.size()) {
                                try {
                                    byArray4 = this.m_vvfat.fat.get(i / 4 + n2 * 128);
                                    if (byArray4 != null) {
                                        n = byArray4[0] & 0xFF | byArray4[1] << 8 & 0xFF00 | byArray4[2] << 16 & 0xFF0000 | byArray4[3] << 24 & 0xFF000000;
                                    }
                                }
                                catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                RedirectedFolder.debug("Old Value: 0x" + Integer.toHexString(n) + "\n", bl, bl2);
                            } else {
                                RedirectedFolder.debug("No Value Old Value\n", bl, bl2);
                            }
                            if (n3 == n) {
                                RedirectedFolder.debug("SAME VALUES: 0x" + Integer.toHexString(n) + " = 0x" + Integer.toHexString(n3) + "\n", bl, bl2);
                            } else if (n2 != 0 || i > 8) {
                                RedirectedFolder.debug("AT THIS POINT THE FAT HAS BEEN MODIFIED \n", bl, bl2);
                                RedirectedFolder.debug("Write new FAT-Value: 0x" + Integer.toHexString(n3) + " at: " + (i / 4 + n2 * 128) + "\n", bl, bl2);
                                this.fat_set(i / 4 + n2 * 128, n3);
                                try {
                                    mapping_t mapping_t2 = this.find_mapping_for_cluster(i / 4 + n2 * 128);
                                    if (mapping_t2 != null) {
                                        RedirectedFolder.debug("FAT changed --> Write data for mapping: " + mapping_t2.path + "\n", true, true);
                                        if (!mapping_t2.writing_request) {
                                            mapping_t2.writing_request = true;
                                        }
                                    }
                                }
                                catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                bl3 = true;
                            }
                        } else {
                            RedirectedFolder.debug("No FAT-Data --> Set NULL\n", bl, bl2);
                            try {
                                if (i / 4 + n2 * 128 < this.m_vvfat.fat.size()) {
                                    this.m_vvfat.fat.set(i / 4 + n2 * 128, null);
                                }
                            }
                            catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        RedirectedFolder.debug("\n", bl, bl2);
                    }
                }
            }
            ++l;
        }
        return bl3;
    }

    public synchronized void insert_mapping(array_t array_t2, mapping_t mapping_t2) {
        System.out.println("insert_mapping");
        boolean bl = false;
        boolean bl2 = false;
        RedirectedFolder.debug("insert mapping\n", true, true);
        array_t array_t3 = new array_t();
        array_t3.mapping = mapping_t2;
        try {
            int n = this.find_mapping_for_cluster_aux(array_t2.direntry.begin_hi << 16 & 0xFFFF0000 | array_t2.direntry.begin & 0xFFFF, 0, this.m_vvfat.mapping.size());
            if (n > 0) {
                if (n > this.m_vvfat.mapping.size() - 1) {
                    array_t3.next = this.m_vvfat.mapping.lastElement().next + 1;
                    RedirectedFolder.debug("Add Mapping at the End\n", bl, bl2);
                    this.m_vvfat.mapping.add(array_t3);
                } else {
                    RedirectedFolder.debug(".next for actual = " + this.m_vvfat.mapping.get((int)(n - 1)).next + "\n", true, true);
                    array_t3.next = this.m_vvfat.mapping.get((int)(n - 1)).next + 1;
                    RedirectedFolder.debug("Add Mapping at Position: " + n + "\n", bl, bl2);
                    this.m_vvfat.mapping.add(n, array_t3);
                    for (int i = array_t3.next; i < this.m_vvfat.mapping.size(); ++i) {
                        ++this.m_vvfat.mapping.get((int)i).next;
                    }
                }
            } else if (n == 0) {
                array_t3.next = 1;
                this.m_vvfat.mapping.add(array_t3);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            RedirectedFolder.debug("Error inserting mapping " + exception + "\n", bl, bl2);
        }
    }

    public synchronized long handle_incoming_data(byte[] byArray, long l, long l2, long l3, boolean bl) {
        System.out.println("handle_incoming_data");
        boolean bl2 = false;
        boolean bl3 = false;
        try {
            byte[] byArray2 = new byte[512];
            commit_t commit_t2 = null;
            boolean bl4 = false;
            for (long i = l; i < (long)((int)l + (int)l3); ++i) {
                RedirectedFolder.debug("look up for mapping for sector: " + i + "\n", bl2, bl3);
                bl4 = false;
                mapping_t mapping_t2 = this.find_mapping_for_cluster(this.sector2cluster(i));
                System.arraycopy(byArray, (int)(i - l) * 512, byArray2, 0, 512);
                if (mapping_t2 == null) {
                    RedirectedFolder.debug("Really no Mapping for Sector: " + i + "\n", bl2, bl3);
                } else {
                    RedirectedFolder.debug("Sector: " + i + " is a part of Mapping" + mapping_t2.path + " !!!!!\n--> Prepare Commits \n", bl2, bl3);
                    if (mapping_t2.mode == this.MODE_DIRECTORY) {
                        bl4 = true;
                    }
                }
                if (Arrays.equals(byArray2, new byte[512])) {
                    RedirectedFolder.debug("Zeros --> don't store\n", bl2, bl3);
                    continue;
                }
                RedirectedFolder.debug("Data found ==> Store?? \n", bl2, bl3);
                if (mapping_t2 != null && !mapping_t2.writing_request) {
                    RedirectedFolder.debug("Re-Enable Writing for: " + mapping_t2.path + "\n", bl2, bl3);
                    mapping_t2.writing_request = true;
                }
                commit_t2 = new commit_t();
                commit_t2.data = new byte[512];
                System.arraycopy(byArray2, 0, commit_t2.data, 0, 512);
                commit_t2.sector = i;
                commit_t2.direntry_data = bl4;
                this.print_data_buffer(byArray2, false);
                this.all_commits.add(commit_t2);
                RedirectedFolder.debug("Store Data for Sector: " + i + "\n", bl2, bl3);
                RedirectedFolder.debug("LastElement of Commit-Vector: " + this.all_commits.indexOf(this.all_commits.lastElement()) + "\n", bl2, bl3);
                l2 += (i - l) / (long)this.m_vvfat.sectors_per_cluster;
                RedirectedFolder.debug("\n", bl2, bl3);
            }
            return l2;
        }
        catch (Exception exception) {
            RedirectedFolder.debug("Error in data-handling\n", bl2, bl3);
            exception.printStackTrace();
            return l2;
        }
    }

    public synchronized int commit_data(mapping_t mapping_t2) throws Exception {
        boolean bl = true;
        boolean bl2 = true;
        long l = 0L;
        int n = 0;
        byte[] byArray = new byte[512];
        if (mapping_t2.cluster_chain == null || this.m_vvfat.modified_FAT) {
            mapping_t2.cluster_chain = this.get_cluster_chain(mapping_t2);
        }
        if (mapping_t2.cluster_chain == null) {
            RedirectedFolder.debug("could not complete cluster-chain --> FAT-DATA is not complete\n", bl, bl2);
            return -1;
        }
        RedirectedFolder.debug("" + mapping_t2.path + " is split up in clusters: \n", bl, bl2);
        byte[] byArray2 = new byte[this.m_vvfat.sectors_per_cluster * 512];
        byte[] byArray3 = null;
        if (mapping_t2.mode == this.MODE_DIRECTORY) {
            byArray3 = new byte[mapping_t2.info.dir.cluster_num * this.m_vvfat.sectors_per_cluster * 512];
        }
        for (int i = 0; mapping_t2.cluster_chain != null && mapping_t2.cluster_chain[i] != 0L && i < mapping_t2.cluster_chain.length; ++i) {
            boolean bl3 = false;
            if (mapping_t2.cluster_chain[i] == -1L) continue;
            try {
                this.m_vvfat.image.seek(this.cluster2sector(mapping_t2.cluster_chain[i]) * 512L);
                if (this.cluster2sector(mapping_t2.cluster_chain[i]) * 512L + (long)(this.m_vvfat.sectors_per_cluster * 512) > this.m_vvfat.image.length()) {
                    RedirectedFolder.debug("Just copy Part\n", bl, bl2);
                    this.m_vvfat.image.readFully(byArray2, 0, (int)(this.m_vvfat.image.length() - this.cluster2sector(mapping_t2.cluster_chain[i]) * 512L));
                    this.print_data_buffer(byArray2, true);
                } else {
                    RedirectedFolder.debug("copy complete\n", bl, bl2);
                    this.m_vvfat.image.readFully(byArray2, 0, this.m_vvfat.sectors_per_cluster * 512);
                }
                if (Arrays.equals(byArray2, new byte[this.m_vvfat.sectors_per_cluster * 512])) {
                    continue;
                }
            }
            catch (Exception exception) {
                RedirectedFolder.debug("Error in Reading Temp-Image: " + exception + " --> commit later\n", bl, bl2);
                return -2;
            }
            if (mapping_t2.mode == this.MODE_DIRECTORY) {
                System.arraycopy(byArray2, 0, byArray3, i * this.m_vvfat.sectors_per_cluster * 512, this.m_vvfat.sectors_per_cluster * 512);
            } else if (mapping_t2.mode == this.MODE_NORMAL) {
                l = mapping_t2.cluster_chain[i] - mapping_t2.begin;
                if (mapping_t2.file == null) {
                    File file = new File(mapping_t2.path);
                    mapping_t2.file = new RandomAccessFile(file, "rw");
                }
                try {
                    int n2;
                    if (l * (long)this.m_vvfat.sectors_per_cluster * 512L > mapping_t2.info.file.size) {
                        RedirectedFolder.debug("Position out of file-bounds (too large)\n", true, false);
                        break;
                    }
                    if (l * (long)this.m_vvfat.sectors_per_cluster * 512L < 0L) {
                        RedirectedFolder.debug("Position out of file-bounds (negativ)\n", true, false);
                        continue;
                    }
                    if (l * (long)this.m_vvfat.sectors_per_cluster * 512L <= mapping_t2.info.file.size && (l + 1L) * (long)this.m_vvfat.sectors_per_cluster * 512L > mapping_t2.info.file.size) {
                        RedirectedFolder.debug("Last data to write (" + ((int)mapping_t2.info.file.size - (int)l * 512) + ") Bytes\n", bl, bl2);
                        for (n2 = 0; n2 < this.m_vvfat.sectors_per_cluster; ++n2) {
                            System.arraycopy(byArray2, n2 * 512, byArray, 0, 512);
                            if (!Arrays.equals(byArray, new byte[512])) {
                                RedirectedFolder.debug("Seek to: " + (l * (long)this.m_vvfat.sectors_per_cluster + (long)n2) * 512L + "\n", bl, bl2);
                                mapping_t2.file.seek((l * (long)this.m_vvfat.sectors_per_cluster + (long)n2) * 512L);
                                if ((int)mapping_t2.info.file.size - ((int)l * this.m_vvfat.sectors_per_cluster + n2) * 512 >= 512) {
                                    mapping_t2.file.write(byArray, 0, 512);
                                    continue;
                                }
                                mapping_t2.file.write(byArray, 0, (int)mapping_t2.info.file.size - ((int)l * this.m_vvfat.sectors_per_cluster + n2) * 512);
                                continue;
                            }
                            bl3 = true;
                        }
                        mapping_t2.writing_request = false;
                        mapping_t2.file.close();
                    } else {
                        for (n2 = 0; n2 < this.m_vvfat.sectors_per_cluster; ++n2) {
                            System.arraycopy(byArray2, n2 * 512, byArray, 0, 512);
                            if (!Arrays.equals(byArray, new byte[512])) {
                                mapping_t2.file.seek((l * (long)this.m_vvfat.sectors_per_cluster + (long)n2) * 512L);
                                mapping_t2.file.write(byArray, 0, 512);
                                continue;
                            }
                            bl3 = true;
                        }
                    }
                    if (!bl3) {
                        mapping_t2.cluster_chain[i] = -1L;
                    }
                }
                catch (Exception exception) {
                    RedirectedFolder.debug("Error in Writing to File: " + exception + "\n", bl, bl2);
                    exception.printStackTrace();
                }
            }
            Arrays.fill(byArray2, (byte)0);
        }
        if (mapping_t2.mode == this.MODE_DIRECTORY) {
            RedirectedFolder.debug("Found Mapping for Directory: " + mapping_t2.path + "\n", bl, bl2);
            this.parse_direntries(byArray3, mapping_t2);
            mapping_t2.writing_request = false;
        } else if (mapping_t2.mode == this.MODE_NORMAL) {
            RedirectedFolder.debug("Found Mapping for File: " + mapping_t2.path + "\n", bl, bl2);
        }
        return n;
    }

    public synchronized long[] get_cluster_chain(mapping_t mapping_t2) {
        System.out.println("get_cluster_chain");
        boolean bl = true;
        boolean bl2 = false;
        try {
            mapping_t2.cluster_chain = mapping_t2.mode == this.MODE_NORMAL ? new long[(int)mapping_t2.info.file.size / (this.m_vvfat.sectors_per_cluster * 512) + 2] : new long[10000];
            byte[] byArray = null;
            int n = 0;
            int n2 = (int)mapping_t2.begin;
            if ((long)this.m_vvfat.fat.size() > mapping_t2.begin) {
                byArray = this.m_vvfat.fat.get((int)mapping_t2.begin);
                if (byArray == null) {
                    RedirectedFolder.debug("No FAT-ENTRY for this Mapping \n", bl, bl2);
                    RedirectedFolder.debug("No Cluster allocated yet for this mapping --> FAT is incomplete\nFAT-Size: " + this.m_vvfat.fat.size() + "-1 mapping.begin: " + mapping_t2.begin + "\n", bl, bl2);
                    return null;
                }
                mapping_t2.cluster_chain[n] = mapping_t2.begin;
                RedirectedFolder.debug("Cluster_count: " + (n + 1) + " fat-begin (cluster): " + mapping_t2.begin + "\n", bl, bl2);
                while (n2 != 0xFFFFFFF && byArray != null) {
                    try {
                        if (n2 > this.m_vvfat.fat.size()) {
                            RedirectedFolder.debug("FAT not complete yet", bl, bl2);
                            return null;
                        }
                        byArray = this.m_vvfat.fat.get(n2);
                    }
                    catch (Exception exception) {
                        RedirectedFolder.debug("Could not get FAT-Value: " + exception + "\n", bl, bl2);
                        RedirectedFolder.debug("No Cluster allocated yet for this mapping --> FAT is incomplete\nFAT-Size: " + this.m_vvfat.fat.size() + "-1 mapping.begin: " + mapping_t2.begin + "\n", bl, bl2);
                        return null;
                    }
                    n2 = byArray[0] & 0xFF | byArray[1] << 8 & 0xFF00 | byArray[2] << 16 & 0xFF0000 | byArray[3] << 24 & 0xFF000000;
                    RedirectedFolder.debug("Cluster_count: " + (n + 1) + (n2 != 0xFFFFFFF ? " index for next fat: " + n2 : " last cluster ") + "\n", bl, bl2);
                    ++n;
                    if (n2 == 0xFFFFFFF) continue;
                    if ((long)n2 > mapping_t2.end) {
                        mapping_t2.exceeds_mapping_bounds = true;
                    }
                    mapping_t2.cluster_chain[n] = n2;
                }
                mapping_t2.info.dir.cluster_num = n;
                return mapping_t2.cluster_chain;
            }
            RedirectedFolder.debug("No Cluster allocated yet for this mapping --> FAT is incomplete\nFAT-Size: " + this.m_vvfat.fat.size() + "-1 mapping.begin: " + mapping_t2.begin + "\n", bl, bl2);
            return null;
        }
        catch (Exception exception) {
            RedirectedFolder.debug("" + exception + "\n", bl, bl2);
            exception.printStackTrace();
            return null;
        }
    }

    public synchronized mapping_t create_mapping(direntry_t direntry_t2, mapping_t mapping_t2, file_name file_name2) throws Exception {
        Object object;
        System.out.println("create_mapping");
        boolean bl = true;
        boolean bl2 = true;
        mapping_t mapping_t3 = new mapping_t();
        RandomAccessFile randomAccessFile = null;
        String string = new String(file_name2.name, 0, file_name2.len);
        mapping_t3.info.file.size = direntry_t2.size;
        mapping_t3.info.dir.parent_mapping_index = mapping_t2.dir_index;
        mapping_t3.begin = direntry_t2.begin_hi << 16 & 0xFFFF0000 | direntry_t2.begin & 0xFFFF;
        mapping_t3.writing_request = true;
        RedirectedFolder.debug("direntry.attributes: " + direntry_t2.attributes + "\n", bl, bl2);
        if ((direntry_t2.attributes & 0x10) != 0) {
            RedirectedFolder.debug("Create Directory\n", bl, bl2);
            object = new File(mapping_t2.path.concat("/").concat(string));
            ((File)object).mkdir();
            mapping_t3.mode = this.MODE_DIRECTORY;
            int n = this.m_vvfat.directory.size();
            while (n % (this.m_vvfat.fat_type * this.m_vvfat.sectors_per_cluster) != 0) {
                ++n;
            }
            mapping_t3.info.dir.first_dir_index = n;
            mapping_t3.end = mapping_t3.begin + 1L + (long)(1 / this.m_vvfat.sectors_per_cluster);
        } else if ((direntry_t2.attributes & 0x20) != 0) {
            if (direntry_t2.size == 0L && (direntry_t2.begin_hi << 16 & 0xFFFF0000 | direntry_t2.begin & 0xFFFF) == 0) {
                RedirectedFolder.debug("No valid File can be created\n", bl, bl2);
                return null;
            }
            RedirectedFolder.debug("Create File\n", bl, bl2);
            object = new File(mapping_t2.path.concat("/").concat(string));
            try {
                randomAccessFile = new RandomAccessFile((File)object, "rw");
                randomAccessFile.setLength(mapping_t3.info.file.size);
                if (randomAccessFile == null) {
                    RedirectedFolder.debug("Error creating the file !?!?!?!\n", bl, bl2);
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            mapping_t3.mode = this.MODE_NORMAL;
            mapping_t3.end = mapping_t3.begin + 1L + (mapping_t3.info.file.size - 1L) / (long)this.m_vvfat.cluster_size;
        }
        RedirectedFolder.debug("\nCreating a Mapping for: " + string + "\n" + "in Directory: " + mapping_t2.path + "\n" + "with filesize: " + mapping_t3.info.file.size + "\n" + "in Cluster (begin): " + mapping_t3.begin + " (end): " + mapping_t3.end + "\n" + "Startsector: " + this.cluster2sector(mapping_t3.begin) + " Endsector: " + this.cluster2sector(mapping_t3.end) + "\n", bl, bl2);
        mapping_t3.file = randomAccessFile;
        mapping_t3.path = mapping_t2.path.concat("/").concat(string);
        object = this.find_mapping_for_cluster(mapping_t3.begin);
        if (object != null) {
            RedirectedFolder.debug("There is a mapping: " + ((mapping_t)object).path + " for cluster: " + mapping_t3.begin + " ABORT CREATION\n", bl, bl2);
            if (mapping_t3.info.file.size > 0L) {
                RedirectedFolder.debug("Adjust Mapping\n", bl, bl2);
                object = mapping_t3;
                return object;
            }
            RedirectedFolder.debug("Don't create Mapping\n", bl, bl2);
            return null;
        }
        return mapping_t3;
    }

    public void commit_to_image() throws IOException {
        boolean bl = false;
        boolean bl2 = false;
        RedirectedFolder.debug("Commit data to Image\n", bl, bl2);
        long l = 0L;
        for (int i = 0; i < this.all_commits.size(); ++i) {
            l = this.all_commits.get((int)i).sector;
            RedirectedFolder.debug("Image-File-Length: " + this.m_vvfat.image.length() + " " + l * 512L + "\n", bl, bl2);
            if (this.m_vvfat.image.length() < l * 512L) {
                RedirectedFolder.debug("Adjust Image Size: sector_to_write = " + l + " m_vvfat.faked_sectors = " + this.m_vvfat.faked_sectors + " --> size = " + l * 512L + "\n", bl, bl2);
                this.m_vvfat.image.setLength(l * 512L);
            }
            this.m_vvfat.image.seek(l * 512L);
            this.m_vvfat.image.write(this.all_commits.get((int)i).data, 0, 512);
        }
        this.all_commits.clear();
    }

    public int parse_direntries(byte[] byArray, mapping_t mapping_t2) throws Exception {
        boolean bl = true;
        boolean bl2 = true;
        file_name file_name2 = new file_name();
        file_name file_name3 = new file_name();
        this.fn_init(file_name2);
        this.fn_init(file_name3);
        byte[] byArray2 = new byte[32];
        boolean bl3 = false;
        array_t array_t2 = null;
        for (int i = 0; i < byArray.length / 512 * 16; ++i) {
            block45: {
                System.arraycopy(byArray, i * this.direntry_size, byArray2, 0, this.direntry_size);
                direntry_t direntry_t2 = this.getDirEntry(byArray2);
                if (this.parse_long_name(file_name2, byArray2) < 0) {
                    RedirectedFolder.debug("Warning: non-ASCII filename\n", bl, bl2);
                    return -1;
                }
                if (this.parse_short_name(file_name3, direntry_t2) < 0) {
                    RedirectedFolder.debug("Warning: Error in Short-Name\n", bl, bl2);
                    return -1;
                }
                if (this.is_short_name(direntry_t2) && (direntry_t2.attributes & 1) != 0) {
                    if (direntry_t2.equals(null)) {
                        RedirectedFolder.debug("Warning: tried to write to write-protected file\n", bl, bl2);
                        return -1;
                    }
                    Arrays.fill(byArray2, (byte)0);
                }
                if (i + mapping_t2.info.dir.first_dir_index < this.m_vvfat.directory.size()) {
                    array_t2 = this.m_vvfat.directory.get(i + mapping_t2.info.dir.first_dir_index);
                    if (array_t2 == null) {
                        RedirectedFolder.debug("Entry doesnt exist --> insert new one \n", true, false);
                        array_t2 = new array_t();
                        array_t2.direntry = new direntry_t();
                    }
                } else {
                    RedirectedFolder.debug("Entry doesnt exist --> add new one \n", true, false);
                    array_t2 = new array_t();
                    array_t2.direntry = new direntry_t();
                    this.m_vvfat.directory.add(array_t2);
                }
                if (this.is_long_name(direntry_t2) != 0) {
                    if (byArray2[0] == -27) {
                        try {
                            RedirectedFolder.debug("delete / deleted direntry\n", bl, bl2);
                            array_t2.direntry = null;
                            array_t2.fatdata = new byte[32];
                            this.m_vvfat.directory.set(i + mapping_t2.info.dir.first_dir_index, null);
                            bl3 = false;
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                            RedirectedFolder.debug("Error in direntry deletion\n", bl, bl2);
                        }
                    } else if (array_t2.direntry == null && array_t2.fatdata == null) {
                        this.print_data_buffer(byArray2, true);
                        RedirectedFolder.debug("" + new String(byArray2) + "\t\t--> Long Entry" + " new \n", bl, bl2);
                        RedirectedFolder.debug("" + new String(file_name2.name, 0, file_name2.len) + "\n", bl, bl2);
                        array_t2.fatdata = byArray2;
                        bl3 = true;
                        RedirectedFolder.debug("Set Direntry @: " + i + " " + mapping_t2.info.dir.first_dir_index + "\n", bl, bl2);
                        try {
                            if (this.m_vvfat.directory.size() < i + mapping_t2.info.dir.first_dir_index) {
                                this.m_vvfat.directory.setSize(i + mapping_t2.info.dir.first_dir_index);
                            }
                            this.m_vvfat.directory.set(i + mapping_t2.info.dir.first_dir_index, array_t2);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    } else if (Arrays.equals(byArray2, array_t2.fatdata) || Arrays.equals(byArray2, array_t2.direntry != null ? array_t2.direntry.getDirBytes() : null)) {
                        this.print_data_buffer(byArray2, true);
                        RedirectedFolder.debug("" + new String(byArray2) + "\t\t--> Long Entry" + " exists\n", bl, bl2);
                        RedirectedFolder.debug("" + new String(file_name2.name, 0, file_name2.len) + "\n", bl, bl2);
                    } else {
                        RedirectedFolder.debug("" + new String(byArray2) + "\t\t--> Long Entry" + " changed\n", bl, bl2);
                        RedirectedFolder.debug("" + new String(file_name2.name, 0, file_name2.len) + "\n", bl, bl2);
                        bl3 = true;
                    }
                } else if (this.is_short_name(direntry_t2)) {
                    if (array_t2.direntry == null && array_t2.fatdata == null || !Arrays.equals(byArray2, array_t2.direntry.getDirBytes())) {
                        RedirectedFolder.debug("" + new String(byArray2) + "\t\t--> Short Entry" + " new\n", bl, bl2);
                        RedirectedFolder.debug("" + new String(file_name3.name, 0, file_name3.len) + "\n", bl, bl2);
                        array_t2.direntry = direntry_t2;
                        try {
                            mapping_t mapping_t3 = this.create_mapping(direntry_t2, mapping_t2, bl3 ? file_name2 : file_name3);
                            if (mapping_t3 != null) {
                                RedirectedFolder.debug("Commit File with " + mapping_t3.info.file.size + " Bytes\n", bl, bl2);
                                if (mapping_t3.mode == this.MODE_NORMAL) {
                                    RedirectedFolder.debug("Commit-Data\n", bl, bl2);
                                    this.commit_data(mapping_t3);
                                } else if (mapping_t3.mode == this.MODE_DIRECTORY) {
                                    RedirectedFolder.debug("\n Directory Handling\n\n", bl, bl2);
                                }
                                this.insert_mapping(array_t2, mapping_t3);
                                RedirectedFolder.debug("\nRemaining Commit-Vector-Length = " + this.all_commits.size() + "\n", bl, bl2);
                                bl3 = false;
                                RedirectedFolder.debug("Set Direntry @: " + i + " " + mapping_t2.info.dir.first_dir_index + "\n", bl, bl2);
                                try {
                                    if (this.m_vvfat.directory.size() < i + mapping_t2.info.dir.first_dir_index) {
                                        this.m_vvfat.directory.setSize(i + mapping_t2.info.dir.first_dir_index);
                                    }
                                    this.m_vvfat.directory.set(i + mapping_t2.info.dir.first_dir_index, array_t2);
                                }
                                catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                break block45;
                            }
                            RedirectedFolder.debug("Mapping-Creation failed \n", bl, bl2);
                        }
                        catch (Exception exception) {
                            RedirectedFolder.debug("" + exception + "\n", bl, bl2);
                            exception.printStackTrace();
                        }
                    } else if (Arrays.equals(byArray2, array_t2.fatdata) || Arrays.equals(byArray2, array_t2.direntry.getDirBytes())) {
                        RedirectedFolder.debug("" + new String(byArray2) + "\t\t--> Short Entry" + " exits\n", bl, bl2);
                        RedirectedFolder.debug("" + new String(file_name3.name, 0, file_name3.len) + "\n", bl, bl2);
                        bl3 = false;
                    }
                } else if (!this.is_short_name(direntry_t2) && this.is_long_name(direntry_t2) == 0 && !this.is_free(direntry_t2)) {
                    if (this.is_volume_label(direntry_t2)) {
                        RedirectedFolder.debug("Volumelabel: " + new String(byArray2) + "\n", bl, bl2);
                    } else if (byArray2[0] == -27) {
                        if (array_t2.direntry != null) {
                            try {
                                RedirectedFolder.debug("delete direntry - Search in Boundaries of Mapping Vector: 0-" + this.m_vvfat.mapping.size() + "\n", bl, bl2);
                                int n = this.find_mapping_for_cluster_aux(array_t2.direntry.begin_hi << 16 & 0xFFFF0000 | array_t2.direntry.begin & 0xFFFF, 0, this.m_vvfat.mapping.size());
                                mapping_t mapping_t4 = this.find_mapping_for_cluster(array_t2.direntry.begin_hi << 16 & 0xFFFF0000 | array_t2.direntry.begin & 0xFFFF);
                                if (mapping_t4 != null) {
                                    File file = new File(mapping_t4.path);
                                    file.delete();
                                    RedirectedFolder.debug("delete mapping\n", bl, bl2);
                                    this.m_vvfat.mapping.remove(n);
                                }
                                this.m_vvfat.directory.set(i + mapping_t2.info.dir.first_dir_index, null);
                            }
                            catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        } else {
                            RedirectedFolder.debug("Already deleted\n", bl, bl2);
                        }
                    } else {
                        RedirectedFolder.debug("Error !! undefined data: " + i + " " + new String(byArray2) + "\n", bl, bl2);
                    }
                }
            }
            Arrays.fill(byArray2, (byte)0);
        }
        RedirectedFolder.debug("end MODE_DIRECTORY\n", bl, bl2);
        RedirectedFolder.debug("Remaining Mappings: " + this.m_vvfat.mapping.size() + "\n", bl, bl2);
        return 0;
    }

    public synchronized int WriteSectors_VFat(long l, long l2, byte[] byArray) {
        System.out.println("WriteSectors_VFat");
        boolean bl = true;
        boolean bl2 = true;
        try {
            long l3 = 0L;
            long l4 = l;
            mapping_t mapping_t2 = null;
            RedirectedFolder.debug("\n=========================================\n|  Write " + l2 + " sectors beginning from " + l + "  |" + "\n=========================================\n\n", bl, bl2);
            if (l4 < (long)(this.m_vvfat.first_sectors_number + 2 * this.m_vvfat.sectors_per_fat)) {
                this.m_vvfat.modified_FAT = this.modify_fat(byArray, l4, l, l2);
                this.handle_incoming_data(byArray, l, l3, l2, false);
                this.commit_to_image();
            }
            for (l3 = this.sector2cluster(l); l3 <= this.sector2cluster(l + l2 - 1L); ++l3) {
                RedirectedFolder.debug("Cluster " + l3 + " Sector: " + l + " Number of Sectors: " + l2 + " Cluster2Sector(i): " + this.cluster2sector(l3) + "\n", bl, bl2);
                RedirectedFolder.debug("Look up Mapping for Write-Request (find_mapping_for_cluster(" + l3 + "))\n\n", bl, bl2);
                mapping_t2 = this.find_mapping_for_cluster(l3);
                if (mapping_t2 == null) {
                    RedirectedFolder.debug("No mapping\n", bl, bl2);
                } else {
                    RedirectedFolder.debug("Changes in File or Direntry: " + mapping_t2.path + "\n", bl, bl2);
                }
                l3 = this.handle_incoming_data(byArray, l, l3, l2, true);
                this.commit_to_image();
                Runtime runtime = Runtime.getRuntime();
                RedirectedFolder.debug("Total Memory = " + runtime.totalMemory() + " Free Memory = " + runtime.freeMemory(), bl, true);
            }
            RedirectedFolder.debug("Mapping - Vector contains " + this.m_vvfat.mapping.size() + " mappings\n", bl, bl2);
            for (int i = 0; i < this.m_vvfat.mapping.size(); ++i) {
                mapping_t2 = this.m_vvfat.mapping.get((int)i).mapping;
                if (mapping_t2 == null) {
                    RedirectedFolder.debug("No Mapping found!\n", bl, bl2);
                } else {
                    RedirectedFolder.debug("Mapping: " + mapping_t2.path + " found " + (mapping_t2.writing_request ? "--> should be written" : "--> do not to touch (no writing requested)") + "\n", bl, bl2);
                }
                if (!mapping_t2.writing_request) continue;
                RedirectedFolder.debug("" + mapping_t2.path + " is split up in clusters: \n", false, false);
                byte[] byArray2 = new byte[this.m_vvfat.sectors_per_cluster * 512];
                if (mapping_t2.mode == this.MODE_DIRECTORY) {
                    RedirectedFolder.debug("Found Mapping for DIRECTORY: " + mapping_t2.path + "\n", bl, bl2);
                    if (mapping_t2.cluster_chain == null || this.m_vvfat.modified_FAT) {
                        RedirectedFolder.debug("get cluster-chain!\n", bl, bl2);
                        mapping_t2.cluster_chain = this.get_cluster_chain(mapping_t2);
                    }
                    if (mapping_t2.cluster_chain == null) {
                        RedirectedFolder.debug("could not complete cluster-chain --> FAT-DATA is not complete\n", bl, bl2);
                        this.m_vvfat.modified_FAT = false;
                        continue;
                    }
                    byte[] byArray3 = new byte[mapping_t2.info.dir.cluster_num * this.m_vvfat.sectors_per_cluster * 512];
                    RedirectedFolder.debug("Alloc Buffer for " + mapping_t2.info.dir.cluster_num + " Clusters\n", bl, bl2);
                    int n = 0;
                    while (mapping_t2.cluster_chain != null && mapping_t2.cluster_chain[n] != 0L && (mapping_t2.cluster_chain[n] & 0xFFFFFFFL) != 0xFFFFFFFL) {
                        mapping_t mapping_t3;
                        if (mapping_t2.exceeds_mapping_bounds && mapping_t2.cluster_chain[n] > mapping_t2.end && (mapping_t3 = this.find_mapping_for_cluster(mapping_t2.cluster_chain[n])) == null) {
                            mapping_t3 = new mapping_t();
                            array_t array_t2 = new array_t();
                            array_t2.direntry = new direntry_t();
                            mapping_t3.begin = mapping_t3.end = mapping_t2.cluster_chain[n];
                            mapping_t3.path = mapping_t2.path;
                            mapping_t3.cluster_chain = mapping_t2.cluster_chain;
                            mapping_t3.mode = this.MODE_DIRECTORY;
                            mapping_t3.info = mapping_t2.info;
                            this.set_begin_of_direntry(array_t2.direntry, mapping_t2.cluster_chain[n]);
                            array_t2.direntry.attributes = (byte)16;
                            mapping_t3.writing_request = true;
                            this.insert_mapping(array_t2, mapping_t3);
                        }
                        RedirectedFolder.debug("Cluster: " + mapping_t2.cluster_chain[n] + "\n", bl, bl2);
                        try {
                            RedirectedFolder.debug("Offset in Image: " + this.cluster2sector(mapping_t2.cluster_chain[n]) * 512L + "\n", bl, bl2);
                            this.m_vvfat.image.seek(this.cluster2sector(mapping_t2.cluster_chain[n]) * 512L);
                            if (this.cluster2sector(mapping_t2.cluster_chain[n]) * 512L + (long)(this.m_vvfat.sectors_per_cluster * 512) > this.m_vvfat.image.length()) {
                                RedirectedFolder.debug("Just copy Part\n", bl, bl2);
                                this.m_vvfat.image.readFully(byArray2, 0, (int)(this.m_vvfat.image.length() - this.cluster2sector(mapping_t2.cluster_chain[n]) * 512L));
                            } else {
                                RedirectedFolder.debug("copy complete\n", bl, bl2);
                                this.m_vvfat.image.readFully(byArray2, 0, this.m_vvfat.sectors_per_cluster * 512);
                            }
                        }
                        catch (Exception exception) {
                            RedirectedFolder.debug("Error in reading cluster from file: " + this.cluster2sector(mapping_t2.cluster_chain[n]) * 512L + "\n", bl, bl2);
                        }
                        RedirectedFolder.debug("" + byArray2.length + " " + byArray3.length + "\n", false, false);
                        System.arraycopy(byArray2, 0, byArray3, n * this.m_vvfat.sectors_per_cluster * 512, this.m_vvfat.sectors_per_cluster * 512);
                        Arrays.fill(byArray2, (byte)0);
                        ++n;
                    }
                    this.parse_direntries(byArray3, mapping_t2);
                    mapping_t2.writing_request = false;
                    continue;
                }
                if (mapping_t2.mode != this.MODE_NORMAL) continue;
                RedirectedFolder.debug("Found Mapping for FILE: " + mapping_t2.path + " --> commit data\n", bl, bl2);
                this.commit_data(mapping_t2);
            }
            return 0;
        }
        catch (Exception exception) {
            RedirectedFolder.debug(" Fehler in \"WriteSectors_VVFAT\"\n", bl, bl2);
            exception.printStackTrace();
            return -1;
        }
    }

    public int parse_long_name(file_name file_name2, byte[] byArray) {
        try {
            if (byArray[11] != 15) {
                return 1;
            }
            if ((byArray[0] & 0x40) != 0) {
                file_name2.sequence_number = byArray[0] & 0x3F;
                file_name2.checksum = byArray[13];
                file_name2.name[0] = '\u0000';
            } else {
                if ((byArray[0] & 0x3F) != --file_name2.sequence_number) {
                    RedirectedFolder.debug("return -1 ", false, false);
                    return -1;
                }
                if (byArray[13] != file_name2.checksum) {
                    RedirectedFolder.debug("return -2 ", false, false);
                    return -2;
                }
                if (byArray[12] != 0 || byArray[26] != 0 || byArray[27] != 0) {
                    RedirectedFolder.debug("return -3 ", false, false);
                    return -3;
                }
            }
            int n = 13 * (file_name2.sequence_number - 1);
            int n2 = 0;
            int n3 = 0;
            int n4 = 1;
            while (n3 < 13) {
                if (n4 == 11) {
                    n4 = 14;
                } else if (n4 == 26) {
                    n4 = 28;
                }
                if (byArray[n4 + 1] == 0) {
                    file_name2.name[n + n3] = (char)byArray[n4];
                    ++n2;
                } else {
                    if (byArray[n4 + 1] != -1 || (byArray[0] & 0x40) == 0) {
                        RedirectedFolder.debug("return -4 @j: " + n4 + " ", false, false);
                        return -4;
                    }
                    file_name2.name[n + n3] = '\u0000';
                }
                ++n3;
                n4 += 2;
            }
            if ((byArray[0] & 0x40) != 0) {
                file_name2.len = n + n2 - 1;
            }
            return 0;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    public int parse_short_name(file_name file_name2, direntry_t direntry_t2) {
        int n;
        char[] cArray = new String(direntry_t2.name).toLowerCase().toCharArray();
        char[] cArray2 = new String(direntry_t2.extension).toLowerCase().toCharArray();
        file_name2.len = 0;
        if (!this.is_short_name(direntry_t2)) {
            return 1;
        }
        for (n = 7; n >= 0 && direntry_t2.name[n] == ' '; --n) {
        }
        for (int i = 0; i <= n; ++i) {
            if (direntry_t2.name[i] <= ' ' || direntry_t2.name[i] > '\u007f') {
                return -1;
            }
            if (this.m_vvfat.downcase_short_names == 1) {
                file_name2.name[i] = cArray[i];
                ++file_name2.len;
                continue;
            }
            file_name2.name[i] = direntry_t2.name[i];
            ++file_name2.len;
        }
        for (n = 2; n >= 0 && direntry_t2.extension[n] == ' '; --n) {
        }
        if (n >= 0) {
            file_name2.name[i++] = 46;
            ++file_name2.len;
            file_name2.name[i + n + 1] = '\u0000';
            while (n >= 0) {
                if (direntry_t2.extension[n] <= ' ' || direntry_t2.extension[n] > '\u007f') {
                    return -2;
                }
                if (this.m_vvfat.downcase_short_names == 1) {
                    file_name2.name[i + n] = cArray2[n];
                    ++file_name2.len;
                } else {
                    file_name2.name[i + n] = direntry_t2.extension[n];
                    ++file_name2.len;
                }
                --n;
            }
        } else {
            file_name2.name[i + n + 1] = '\u0000';
        }
        return 0;
    }

    public boolean is_short_name(direntry_t direntry_t2) {
        int n;
        boolean bl = !this.is_volume_label(direntry_t2) && this.is_long_name(direntry_t2) == 0 && !this.is_free(direntry_t2);
        boolean bl2 = true;
        if ((direntry_t2.attributes >> 6 & 0xFF) != 0) {
            bl = false;
        }
        for (n = 7; n >= 0; --n) {
            if (bl2) {
                while (n > 0 && direntry_t2.name[n] == ' ') {
                    --n;
                }
                bl2 = false;
            }
            if (!this.check_allowed_chars(direntry_t2.name[n], n)) continue;
            bl = false;
        }
        bl2 = true;
        for (n = 2; n >= 0; --n) {
            if (bl2) {
                while (n > 0 && direntry_t2.extension[n] == ' ') {
                    --n;
                }
                bl2 = false;
            }
            if (!this.check_allowed_chars(direntry_t2.extension[n], n)) continue;
            bl = false;
        }
        return bl;
    }

    public boolean is_free(direntry_t direntry_t2) {
        return direntry_t2.attributes == 0 || direntry_t2.name[0] == '\u00e5';
    }

    public boolean is_volume_label(direntry_t direntry_t2) {
        return direntry_t2.attributes == 40;
    }

    int is_long_name(direntry_t direntry_t2) {
        return direntry_t2.attributes == 15 ? 1 : 0;
    }

    public void set_begin_of_direntry(direntry_t direntry_t2, long l) {
        direntry_t2.begin = (int)(l & 0xFFFFL);
        direntry_t2.begin_hi = (int)(l >> 16 & 0xFFFFL);
    }

    public byte fat_chksum(direntry_t direntry_t2) {
        byte by = 0;
        char[] cArray = new char[11];
        System.arraycopy(direntry_t2.name, 0, cArray, 0, direntry_t2.name.length);
        System.arraycopy(direntry_t2.extension, 0, cArray, direntry_t2.name.length, direntry_t2.extension.length);
        for (int i = 0; i < 11; ++i) {
            by = (byte)((byte)((by & 0xFE) >> 1) | (byte)((by & 1) == 1 ? 128 : 0));
            by = (byte)(by + (byte)cArray[i]);
        }
        return by;
    }

    public direntry_t create_short_and_long_name(int n, String string, int n2, int n3) throws IOException {
        try {
            int n4;
            Object object;
            int n5;
            int n6 = 0;
            if (this.m_vvfat.directory.get(n) != null) {
                n6 = this.m_vvfat.directory.get((int)n).next;
            }
            direntry_t direntry_t2 = new direntry_t();
            direntry_t direntry_t3 = new direntry_t();
            if (n2 == 1) {
                array_t array_t2 = new array_t();
                array_t2.next = this.m_vvfat.directory.size() + 1;
                this.m_vvfat.directory.add(array_t2);
                array_t2.direntry = direntry_t2;
                direntry_t2.name = "        ".toCharArray();
                direntry_t2.extension = "   ".toCharArray();
                System.arraycopy(string.toCharArray(), 0, direntry_t2.name, 0, string.toCharArray().length);
                return direntry_t2;
            }
            direntry_t3 = this.create_long_filename(string.toCharArray());
            int n7 = string.length();
            char[] cArray = string.toCharArray();
            for (n5 = n7 - 1; n5 > 0 && cArray[n5] != '.'; --n5) {
            }
            if (n5 > 0) {
                n7 = n5 > 8 ? 8 : n5;
            } else if (n7 > 8) {
                n7 = 8;
            }
            array_t array_t3 = new array_t();
            array_t3.next = this.m_vvfat.directory.lastElement().next + 1;
            this.m_vvfat.directory.add(array_t3);
            array_t3.direntry = direntry_t2 = new direntry_t();
            direntry_t2.name = "        ".toCharArray();
            direntry_t2.extension = "   ".toCharArray();
            string.getChars(0, n7, direntry_t2.name, 0);
            if (n5 > 0) {
                if (string.length() >= n5 + 4) {
                    string.getChars(n5 + 1, n5 + 4, direntry_t2.extension, 0);
                }
                if (string.length() < n5 + 4) {
                    string.getChars(n5 + 1, string.length(), direntry_t2.extension, 0);
                }
            }
            boolean bl = true;
            for (n7 = 2; n7 >= 0; --n7) {
                if (bl) {
                    while (n7 > 0 && direntry_t2.extension[n7] == ' ') {
                        --n7;
                    }
                    bl = false;
                }
                if (this.check_allowed_chars(direntry_t2.name[n7], n7)) {
                    direntry_t2.extension[n7] = 95;
                    continue;
                }
                if (direntry_t2.extension[n7] < 'a' || direntry_t2.extension[n7] > 'z') continue;
                int n8 = n7;
                direntry_t2.extension[n8] = (char)(direntry_t2.extension[n8] + -32);
            }
            bl = true;
            for (n7 = 7; n7 >= 0; --n7) {
                if (bl) {
                    while (n7 > 0 && direntry_t2.name[n7] == ' ') {
                        --n7;
                    }
                    bl = false;
                }
                if (this.check_allowed_chars(direntry_t2.name[n7], n7)) {
                    direntry_t2.name[n7] = 95;
                    continue;
                }
                if (direntry_t2.name[n7] < 'a' || direntry_t2.name[n7] > 'z') continue;
                int n9 = n7;
                direntry_t2.name[n9] = (char)(direntry_t2.name[n9] + -32);
            }
            int n10 = n3;
            while (true) {
                block31: {
                    block30: {
                        block29: {
                            if ((object = this.m_vvfat.directory.get((int)n10).direntry) != null) break block29;
                            ++n10;
                            break block30;
                        }
                        if (this.is_long_name((direntry_t)object) == 0 && new String(direntry_t2.name).compareTo(new String(((direntry_t)object).name)) == 0 && new String(direntry_t2.extension).compareTo(new String(((direntry_t)object).extension)) == 0) break block31;
                        ++n10;
                    }
                    if (object != direntry_t2) continue;
                }
                if (object.equals(direntry_t2)) break;
                if (direntry_t2.name[7] == ' ') {
                    for (n4 = 6; n4 > 0 && direntry_t2.name[n4] == ' '; --n4) {
                        direntry_t2.name[n4] = 126;
                    }
                }
                for (n4 = 7; n4 > 0 && direntry_t2.name[n4] == '9'; --n4) {
                    direntry_t2.name[n4] = 48;
                }
                if (n4 <= 0) continue;
                if (direntry_t2.name[n4] < '0' || direntry_t2.name[n4] > '9') {
                    direntry_t2.name[n4] = 48;
                    continue;
                }
                int n11 = n4;
                direntry_t2.name[n11] = (char)(direntry_t2.name[n11] + '\u0001');
            }
            if (direntry_t3 != null) {
                n4 = this.fat_chksum(direntry_t2);
                object = this.m_vvfat.directory.get(n6);
                int n12 = n6;
                while (((array_t)object).direntry != direntry_t2 && this.is_long_name(((array_t)object).direntry) == 1) {
                    ((array_t)object).fatdata[13] = n4;
                    object = this.m_vvfat.directory.get(++n12);
                }
            }
            return direntry_t2;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public direntry_t create_long_filename(char[] cArray) throws Exception {
        System.out.println("" + new String(cArray));
        try {
            int n;
            array_t array_t2 = new array_t();
            array_t2.fatdata = new byte[32];
            char[] cArray2 = new char[258];
            int n2 = this.short2long_name(cArray2, cArray);
            int n3 = (n2 + 25) / 26;
            for (n = 0; n < n3; ++n) {
                direntry_t direntry_t2;
                array_t2.next = this.m_vvfat.directory.lastElement().next + 1;
                this.m_vvfat.directory.add(array_t2);
                array_t2.direntry = direntry_t2 = new direntry_t();
                direntry_t2.name = "        ".toCharArray();
                direntry_t2.extension = "   ".toCharArray();
                direntry_t2.attributes = (byte)15;
                direntry_t2.reserved[0] = 0;
                direntry_t2.begin = 0;
                direntry_t2.name[0] = (char)(n3 - n | (n == 0 ? 64 : 0));
                if (n + 1 >= n3) continue;
                array_t2 = new array_t();
                array_t2.fatdata = new byte[32];
            }
            for (n = 0; n < n2; ++n) {
                int n4 = n % 26;
                n4 = n4 < 10 ? 1 + n4 : (n4 < 22 ? 14 + n4 - 10 : 28 + n4 - 22);
                if (n % 26 == 0) {
                    byte[] byArray = array_t2.direntry.getDirBytes();
                    array_t2.fatdata[0] = byArray[0];
                    array_t2.fatdata[11] = byArray[11];
                    array_t2.fatdata[12] = byArray[12];
                    array_t2.fatdata[26] = byArray[26];
                    array_t2.fatdata[27] = byArray[27];
                }
                array_t2 = this.m_vvfat.directory.get(array_t2.next - 1 - (n % 26 == 0 && n != 0 ? 1 : 0));
                array_t2.fatdata[n4] = (byte)cArray2[n];
            }
            byte[] byArray = array_t2.direntry.getDirBytes();
            array_t2.fatdata[0] = byArray[0];
            array_t2.fatdata[11] = byArray[11];
            array_t2.fatdata[12] = byArray[12];
            array_t2.fatdata[26] = byArray[26];
            array_t2.fatdata[27] = byArray[27];
            array_t2.direntry = this.getDirEntry(array_t2.fatdata);
            return array_t2.direntry;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int short2long_name(char[] cArray, char[] cArray2) {
        int n;
        for (n = 0; n < 129 && n < cArray2.length; ++n) {
            cArray[2 * n] = cArray2[n];
            cArray[2 * n + 1] = '\u0000';
        }
        cArray[2 * n + 1] = '\u0000';
        cArray[2 * n] = '\u0000';
        n = 2 * n + 2;
        while (n % 26 > 0) {
            cArray[n] = 255;
            ++n;
        }
        return n;
    }

    public boolean check_allowed_chars(char c, int n) {
        return c <= ' ' && n != 0 || c > '\u007f' || c == '\"' || c == '.' || c == '*' || c == '?' || c == '>' || c == '<' || c == '|' || c == '/' || c == '\\' || c == '[' || c == ']' || c == ';' || c == ',' || c == '+' || c == '=' || c == '\'' || c == '\"';
    }

    private synchronized int find_mapping_for_cluster_aux(long l, int n, int n2) throws Exception {
        try {
            int n3 = n + 1;
            while (true) {
                n3 = (n + n2) / 2;
                mapping_t mapping_t2 = this.m_vvfat.mapping.get((int)n3).mapping;
                if (mapping_t2.begin > mapping_t2.end) {
                    throw new Exception(T._("mapping.begin < mapping.end"));
                }
                if (mapping_t2.begin >= l) {
                    if (n2 == n3) {
                        return n;
                    }
                    n2 = n3;
                    continue;
                }
                if (n == n3) {
                    return mapping_t2.end <= l ? n2 : n;
                }
                n = n3;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception);
            return -1;
        }
    }

    private synchronized mapping_t find_mapping_for_cluster(long l) throws Exception {
        boolean bl = false;
        boolean bl2 = false;
        mapping_t mapping_t2 = null;
        int n = this.find_mapping_for_cluster_aux(l, 0, this.m_vvfat.mapping.indexOf(this.m_vvfat.mapping.lastElement()) + 1);
        RedirectedFolder.debug("Index :" + n + "\n", bl, bl2);
        if (n >= this.m_vvfat.mapping.size()) {
            RedirectedFolder.debug("index >= m_vvfat.mapping.size() ==> NULLPOINTER\n", bl, bl2);
            return null;
        }
        mapping_t2 = this.m_vvfat.mapping.get((int)n).mapping;
        if (mapping_t2.begin > l) {
            RedirectedFolder.debug("mapping.begin>cluster_num  ==> NULLPOINTER\n", bl, bl2);
            return null;
        }
        return mapping_t2;
    }

    private synchronized void vvfat_close_current_file() throws Exception {
        try {
            if (this.m_vvfat.current_mapping != null) {
                this.m_vvfat.current_mapping = null;
                if (this.m_vvfat.current_fd != null) {
                    this.m_vvfat.current_fd.close();
                    this.m_vvfat.current_fd = null;
                }
            }
            this.m_vvfat.current_cluster = -1L;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private synchronized int open_file(mapping_t mapping_t2) throws Exception {
        boolean bl = false;
        boolean bl2 = false;
        try {
            if (mapping_t2 == null) {
                return -1;
            }
            RedirectedFolder.debug("Try to open: " + mapping_t2.path + "\n", bl, bl2);
            File file = new File(mapping_t2.path);
            if ((this.m_vvfat.current_mapping != null || this.m_vvfat.current_mapping.path.compareTo(mapping_t2.path) == 0) && file.isFile()) {
                RedirectedFolder.debug(".isFile()\n", bl, bl2);
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                if (randomAccessFile == null) {
                    return -1;
                }
                this.vvfat_close_current_file();
                this.m_vvfat.current_fd = randomAccessFile;
                this.m_vvfat.current_mapping = mapping_t2;
                return 0;
            }
            return 0;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    public void print_commit_list() {
        boolean bl = false;
        boolean bl2 = false;
        RedirectedFolder.debug("Commit-Vector-Length " + this.all_commits.size() + "\n", bl, bl2);
        for (int i = 0; i < this.all_commits.size(); ++i) {
            RedirectedFolder.debug("Sector: " + this.all_commits.get((int)i).sector + " in Commit-List\n", bl, bl2);
        }
    }

    public void print_data_buffer(byte[] byArray, boolean bl) {
        boolean bl2 = false;
        boolean bl3 = false;
        if (bl) {
            if (log_file == null) {
                return;
            }
            for (int i = 0; i < byArray.length; ++i) {
                RedirectedFolder.debug("0x" + (byArray[i] != 0 ? (byArray[i] < 16 ? (byArray[i] < 0 ? Integer.toHexString(byArray[i] & 0xFF) : "0" + Integer.toHexString(byArray[i] & 0xFF)) : Integer.toHexString(byArray[i] & 0xFF)) : "00") + " ", bl2, bl3);
                if ((i + 1) % 16 == 0) {
                    RedirectedFolder.debug("\n", bl2, bl3);
                }
                if ((i + 1) % 512 != 0) continue;
                RedirectedFolder.debug("\n", bl2, bl3);
            }
        }
    }

    public boolean getDirEntry_proof(direntry_t direntry_t2) {
        boolean bl = false;
        boolean bl2 = false;
        try {
            byte[] byArray = direntry_t2.getDirBytes();
            direntry_t direntry_t3 = this.getDirEntry(byArray);
            byte[] byArray2 = direntry_t3.getDirBytes();
            for (int i = 0; i < byArray2.length; ++i) {
                int n;
                if (byArray[i] == byArray2[i]) continue;
                RedirectedFolder.debug("Error at: " + i + " for mapping " + this.m_vvfat.current_mapping.path + "\n", bl, bl2);
                for (n = 0; n < byArray.length; ++n) {
                    RedirectedFolder.debug(byArray[n] + " ", bl, bl2);
                }
                RedirectedFolder.debug("\n", bl, bl2);
                for (n = 0; n < byArray2.length; ++n) {
                    RedirectedFolder.debug(byArray2[n] + " ", bl, bl2);
                }
                return false;
            }
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public class commit_t {
        boolean direntry_data;
        long sector;
        byte[] data;
    }

    public class file_name {
        char[] name = new char[1024];
        int checksum;
        int len;
        int sequence_number;
    }

    public class mapping_t {
        long begin;
        long end;
        int dir_index;
        int first_mapping_index;
        _info info = new _info();
        String path;
        int mode;
        int read_only;
        RandomAccessFile file;
        FileLock lock;
        long[] cluster_chain;
        boolean writing_request;
        boolean exceeds_mapping_bounds;

        public class _info {
            _file file = new _file();
            _dir dir = new _dir();

            public class _dir {
                int parent_mapping_index;
                int first_dir_index;
                int cluster_num;
            }

            public class _file {
                long offset;
                long size;
            }
        }
    }

    public class direntry_t {
        char[] name = new char[8];
        char[] extension = new char[3];
        byte attributes;
        byte[] reserved = new byte[2];
        int ctime;
        int cdate;
        int adate;
        int begin_hi;
        int mtime;
        int mdate;
        int begin;
        long size;

        public byte[] getDirBytes() throws IOException {
            int n;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (n = 0; n < 8; ++n) {
                byteArrayOutputStream.write(this.name[n] & 0xFF);
            }
            for (n = 0; n < 3; ++n) {
                byteArrayOutputStream.write(this.extension[n] & 0xFF);
            }
            byteArrayOutputStream.write(this.attributes);
            byteArrayOutputStream.write(this.reserved[0]);
            byteArrayOutputStream.write(this.reserved[1]);
            byteArrayOutputStream.write(this.ctime & 0xFF);
            byteArrayOutputStream.write(this.ctime >> 8 & 0xFF);
            byteArrayOutputStream.write(this.cdate & 0xFF);
            byteArrayOutputStream.write(this.cdate >> 8 & 0xFF);
            byteArrayOutputStream.write(this.adate & 0xFF);
            byteArrayOutputStream.write(this.adate >> 8 & 0xFF);
            byteArrayOutputStream.write(this.begin_hi & 0xFF);
            byteArrayOutputStream.write(this.begin_hi >> 8 & 0xFF);
            byteArrayOutputStream.write(this.mtime & 0xFF);
            byteArrayOutputStream.write(this.mtime >> 8 & 0xFF);
            byteArrayOutputStream.write(this.mdate & 0xFF);
            byteArrayOutputStream.write(this.mdate >> 8 & 0xFF);
            byteArrayOutputStream.write(this.begin & 0xFF);
            byteArrayOutputStream.write(this.begin >> 8 & 0xFF);
            byteArrayOutputStream.write((int)this.size & 0xFF);
            byteArrayOutputStream.write((int)this.size >> 8 & 0xFF);
            byteArrayOutputStream.write((int)this.size >> 16 & 0xFF);
            byteArrayOutputStream.write((int)this.size >> 24 & 0xFF);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public class mbr_t {
        byte[] ignored = new byte[446];
        partition_t[] partition = new partition_t[4];
        byte[] magic = new byte[2];

        public byte[] getBytes() throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (int i = 0; i < 4; ++i) {
                partition_t partition_t2 = this.partition[i];
                byte[] byArray = partition_t2.getPartBytes(partition_t2);
                byteArrayOutputStream.write(byArray);
            }
            byteArrayOutputStream.write(this.magic[0]);
            byteArrayOutputStream.write(this.magic[1]);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public class partition_t {
        int attributes;
        int start_head;
        int start_sector;
        int start_cylinder;
        int fs_type;
        int end_head;
        int end_sector;
        int end_cylinder;
        long start_sector_long;
        long end_sector_long;

        public byte[] getPartBytes(partition_t partition_t2) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(partition_t2.attributes & 0xFF);
            byteArrayOutputStream.write(partition_t2.start_head & 0xFF);
            byteArrayOutputStream.write(partition_t2.start_sector & 0xFF);
            byteArrayOutputStream.write(partition_t2.start_cylinder & 0xFF);
            byteArrayOutputStream.write(partition_t2.fs_type & 0xFF);
            byteArrayOutputStream.write(partition_t2.end_head & 0xFF);
            byteArrayOutputStream.write(partition_t2.end_sector & 0xFF);
            byteArrayOutputStream.write(partition_t2.end_cylinder & 0xFF);
            byteArrayOutputStream.write((int)partition_t2.start_sector_long & 0xFF);
            byteArrayOutputStream.write((int)(partition_t2.start_sector_long >> 8) & 0xFF);
            byteArrayOutputStream.write((int)(partition_t2.start_sector_long >> 16) & 0xFF);
            byteArrayOutputStream.write((int)(partition_t2.start_sector_long >> 24) & 0xFF);
            byteArrayOutputStream.write((int)partition_t2.end_sector_long & 0xFF);
            byteArrayOutputStream.write((int)(partition_t2.end_sector_long >> 8) & 0xFF);
            byteArrayOutputStream.write((int)(partition_t2.end_sector_long >> 16) & 0xFF);
            byteArrayOutputStream.write((int)(partition_t2.end_sector_long >> 24) & 0xFF);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public class bootsector_t {
        byte[] jump = new byte[3];
        char[] name = new char[8];
        int sector_size;
        byte sectors_per_cluster;
        int reserved_sectors;
        byte number_of_fats;
        int root_entries;
        int total_sectors16;
        byte media_type;
        int sectors_per_fat;
        int sectors_per_track;
        int number_of_heads;
        long hidden_sectors;
        long total_sectors;
        _union union = new _union();
        short[] magic = new short[2];

        public byte[] getBytes() throws IOException {
            int n;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(RedirectedFolder.this.sectorSize);
            byteArrayOutputStream.write(this.jump[0]);
            byteArrayOutputStream.write(this.jump[1]);
            byteArrayOutputStream.write(this.jump[2]);
            for (n = 0; n < 8; ++n) {
                byteArrayOutputStream.write(this.name[n] & 0xFF);
            }
            byteArrayOutputStream.write(this.sector_size & 0xFF);
            byteArrayOutputStream.write(this.sector_size >> 8 & 0xFF);
            byteArrayOutputStream.write(this.sectors_per_cluster & 0xFF);
            byteArrayOutputStream.write(this.reserved_sectors & 0xFF);
            byteArrayOutputStream.write(this.reserved_sectors >> 8 & 0xFF);
            byteArrayOutputStream.write(this.number_of_fats & 0xFF);
            byteArrayOutputStream.write(this.root_entries & 0xFF);
            byteArrayOutputStream.write(this.root_entries >> 8 & 0xFF);
            byteArrayOutputStream.write(this.total_sectors16 & 0xFF);
            byteArrayOutputStream.write(this.total_sectors16 >> 8 & 0xFF);
            byteArrayOutputStream.write(this.media_type & 0xFF);
            byteArrayOutputStream.write(this.sectors_per_fat & 0xFF);
            byteArrayOutputStream.write(this.sectors_per_fat >> 8 & 0xFF);
            byteArrayOutputStream.write(this.sectors_per_track & 0xFF);
            byteArrayOutputStream.write(this.sectors_per_track >> 8 & 0xFF);
            byteArrayOutputStream.write(this.number_of_heads & 0xFF);
            byteArrayOutputStream.write(this.number_of_heads >> 8 & 0xFF);
            byteArrayOutputStream.write((int)this.hidden_sectors & 0xFF);
            byteArrayOutputStream.write((int)(this.hidden_sectors >> 8) & 0xFF);
            byteArrayOutputStream.write((int)(this.hidden_sectors >> 16) & 0xFF);
            byteArrayOutputStream.write((int)(this.hidden_sectors >> 24) & 0xFF);
            byteArrayOutputStream.write((int)this.total_sectors & 0xFF);
            byteArrayOutputStream.write((int)(this.total_sectors >> 8) & 0xFF);
            byteArrayOutputStream.write((int)(this.total_sectors >> 16) & 0xFF);
            byteArrayOutputStream.write((int)(this.total_sectors >> 24) & 0xFF);
            if (RedirectedFolder.this.m_vvfat.fat_type == 16) {
                byteArrayOutputStream.write(this.union.fat16.drive_number & 0xFF);
                byteArrayOutputStream.write(this.union.fat16.current_head & 0xFF);
                byteArrayOutputStream.write(this.union.fat16.signature & 0xFF);
                byteArrayOutputStream.write(this.union.fat16.id & 0xFF);
                byteArrayOutputStream.write(this.union.fat16.id >> 8 & 0xFF);
                byteArrayOutputStream.write(this.union.fat16.id >> 16 & 0xFF);
                byteArrayOutputStream.write(this.union.fat16.id >> 24 & 0xFF);
                for (n = 0; n < 11; ++n) {
                    byteArrayOutputStream.write(this.union.fat16.volume_label[n] & 0xFF);
                }
                for (n = 0; n < 8; ++n) {
                    byteArrayOutputStream.write(this.union.fat16.fat_type[n] & 0xFF);
                }
                byteArrayOutputStream.write(this.union.fat16.ignored);
            } else if (RedirectedFolder.this.m_vvfat.fat_type == 32) {
                byteArrayOutputStream.write((int)this.union.fat32.sectors_per_fat & 0xFF);
                byteArrayOutputStream.write((int)(this.union.fat32.sectors_per_fat >> 8) & 0xFF);
                byteArrayOutputStream.write((int)(this.union.fat32.sectors_per_fat >> 16) & 0xFF);
                byteArrayOutputStream.write((int)(this.union.fat32.sectors_per_fat >> 24) & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.flags & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.flags >> 8 & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.major);
                byteArrayOutputStream.write(this.union.fat32.minor);
                byteArrayOutputStream.write(this.union.fat32.first_cluster_of_root_directory & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.first_cluster_of_root_directory >> 8 & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.first_cluster_of_root_directory >> 16 & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.first_cluster_of_root_directory >> 24 & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.info_sector & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.info_sector >> 8 & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.backup_boot_sector & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.backup_boot_sector >> 8 & 0xFF);
                byteArrayOutputStream.write(this.union.fat32.reserved);
                byteArrayOutputStream.write(this.union.fat32.drive_number);
                byteArrayOutputStream.write(this.union.fat32.current_head);
                byteArrayOutputStream.write(this.union.fat32.signature);
                byteArrayOutputStream.write((int)this.union.fat32.id & 0xFF);
                byteArrayOutputStream.write((int)(this.union.fat32.id >> 8) & 0xFF);
                byteArrayOutputStream.write((int)(this.union.fat32.id >> 16) & 0xFF);
                byteArrayOutputStream.write((int)(this.union.fat32.id >> 24) & 0xFF);
                for (n = 0; n < 11; ++n) {
                    byteArrayOutputStream.write(this.union.fat32.volume_label[n] & 0xFF);
                }
                for (n = 0; n < 8; ++n) {
                    byteArrayOutputStream.write(this.union.fat32.fat_type[n] & 0xFF);
                }
                byteArrayOutputStream.write(this.union.fat32.ignored);
            }
            byteArrayOutputStream.write(this.magic[0] & 0xFF);
            byteArrayOutputStream.write(this.magic[1] & 0xFF);
            return byteArrayOutputStream.toByteArray();
        }

        class _union {
            _fat16 fat16 = new _fat16();
            _fat32 fat32 = new _fat32();

            class _fat32 {
                long sectors_per_fat;
                int flags;
                byte major;
                byte minor;
                int first_cluster_of_root_directory;
                int info_sector;
                int backup_boot_sector;
                byte[] reserved = new byte[12];
                short drive_number;
                short current_head;
                short signature;
                long id;
                char[] volume_label = new char[11];
                char[] fat_type = new char[8];
                byte[] ignored = new byte[420];
            }

            class _fat16 {
                byte drive_number;
                byte current_head;
                byte signature;
                int id;
                char[] volume_label = new char[11];
                char[] fat_type = new char[8];
                byte[] ignored = new byte[448];
            }
        }
    }

    public class BDRVVVFATState {
        long total_sectors;
        long cyls;
        long heads;
        long secs;
        long translation;
        int first_sectors_number;
        byte[] first_sectors = new byte[32768];
        int fat_type;
        Vector<byte[]> fat = new Vector();
        Vector<array_t> directory = new Vector();
        Vector<array_t> mapping = new Vector();
        int cluster_size;
        int sectors_per_cluster;
        int sectors_per_fat;
        int sectors_of_root_directory;
        long last_cluster_of_root_directory;
        int faked_sectors;
        long sector_count;
        long cluster_count;
        long max_fat_value;
        RandomAccessFile current_fd;
        mapping_t current_mapping;
        byte[] cluster;
        byte[] cluster_buffer;
        long current_cluster;
        String path;
        long dir_size;
        long counted_files;
        RandomAccessFile dummy_file;
        File dummy_file_name;
        int downcase_short_names;
        File image_name;
        RandomAccessFile image = null;
        boolean modified_FAT;
        boolean consitent_FAT;

        public byte[] getFATBytes(int n) throws IOException {
            byte[] byArray;
            int n2 = 0;
            byte[] byArray2 = new byte[4];
            if (n > RedirectedFolder.this.m_vvfat.fat.size()) {
                return null;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(RedirectedFolder.this.sectorSize);
            do {
                byArray = null;
                if (n < RedirectedFolder.this.m_vvfat.fat.size()) {
                    byArray = RedirectedFolder.this.m_vvfat.fat.get(n);
                }
                byteArrayOutputStream.write(byArray != null ? byArray : byArray2);
            } while ((n2 += byArray != null ? byArray.length : 4) < 512 && ++n < RedirectedFolder.this.m_vvfat.fat.size());
            if (n2 < 512) {
                byArray2 = new byte[512 - n2];
                byteArrayOutputStream.write(byArray2);
            }
            byArray = byteArrayOutputStream.toByteArray();
            return byArray;
        }

        public byte[] getDataBytes(int n, int n2) throws IOException {
            int n3 = 0;
            byte[] byArray = new byte[32];
            if (n > RedirectedFolder.this.m_vvfat.directory.size()) {
                return null;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(RedirectedFolder.this.sectorSize);
            do {
                array_t array_t2;
                if ((array_t2 = RedirectedFolder.this.m_vvfat.directory.get(n)) != null && (array_t2.direntry != null || array_t2.fatdata != null)) {
                    if (array_t2.direntry != null && array_t2.fatdata == null) {
                        array_t2.fatdata = array_t2.direntry.getDirBytes();
                        byArray = array_t2.fatdata;
                    } else if (array_t2.direntry == null && array_t2.fatdata != null) {
                        System.arraycopy(array_t2.fatdata, 0, byArray, 0, array_t2.fatdata.length);
                    } else if (array_t2.direntry != null && array_t2.fatdata != null) {
                        if (array_t2.fatdata[11] == 15) {
                            System.arraycopy(array_t2.fatdata, 0, byArray, 0, array_t2.fatdata.length);
                        } else {
                            array_t2.fatdata = array_t2.direntry.getDirBytes();
                            byArray = array_t2.fatdata;
                        }
                    }
                }
                byteArrayOutputStream.write(byArray);
                Arrays.fill(byArray, (byte)0);
            } while ((n3 += byArray.length) < n2 && ++n < RedirectedFolder.this.m_vvfat.directory.size());
            byte[] byArray2 = byteArrayOutputStream.toByteArray();
            return byArray2;
        }
    }

    public class array_t {
        mapping_t mapping = null;
        direntry_t direntry = null;
        byte[] fatdata = null;
        int next;
    }
}

