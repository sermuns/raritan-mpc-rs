/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import java.util.TreeSet;
import javaclientlib.tr.RFP_FILE;

public class RFP {
    byte[] title = new byte[80];
    byte[] description = new byte[160];
    byte[] copyRight = new byte[80];
    byte[] publisher = new byte[80];
    int isSigned;
    int fileCount;
    RFP_FILE[] rfpFile = new RFP_FILE[64];
    TreeSet rpTags = new TreeSet();
    public static final int RFP_MAX_TITLE = 80;
    public static final int RFP_MAX_DESCRIPTION = 160;
    public static final int RFP_MAX_COPYRIGHT = 80;
    public static final int RFP_MAX_PUBLISHER = 80;
    public static final int RFP_MAX_FILES = 64;

    public void addRPID(String string) {
        this.rpTags.add(string);
    }

    public String[] getAllRPIDs() {
        return this.rpTags.toArray(new String[0]);
    }

    public byte[] getTitle() {
        return this.title;
    }

    public void setTitle(byte[] byArray) {
        this.title = byArray;
    }

    public byte[] getDescription() {
        return this.description;
    }

    public void setDescription(byte[] byArray) {
        this.description = byArray;
    }

    public byte[] getCopyRight() {
        return this.copyRight;
    }

    public void setCopyRight(byte[] byArray) {
        this.copyRight = byArray;
    }

    public byte[] getPublisher() {
        return this.publisher;
    }

    public void setPublisher(byte[] byArray) {
        this.publisher = byArray;
    }

    public int getIsSigned() {
        return this.isSigned;
    }

    public void setIsSigned(int n) {
        this.isSigned = n;
    }

    public int getFileCount() {
        return this.fileCount;
    }

    public void setFileCount(int n) {
        this.fileCount = n;
    }

    public RFP_FILE[] getRfpFile() {
        return this.rfpFile;
    }

    public void setRfpFile(RFP_FILE[] rFP_FILEArray) {
        this.rfpFile = rFP_FILEArray;
    }

    public RFP_FILE getRfpFile(int n) {
        return this.rfpFile[n];
    }

    public void setRfpFile(int n, RFP_FILE rFP_FILE) {
        this.rfpFile[n] = rFP_FILE;
    }
}

