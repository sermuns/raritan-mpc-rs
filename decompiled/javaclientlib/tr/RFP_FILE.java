/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public class RFP_FILE {
    byte[] fileName = new byte[256];
    int metaFileName;
    int length;
    int maxLength;
    int isScript;
    byte[] script = new byte[16];
    int isSigned;
    byte[] signature = new byte[400];
    int isEncrypted;
    byte[] model = new byte[20];
    byte[] version = new byte[12];
    byte[] versionMin = new byte[12];
    byte[] versionMax = new byte[12];
    public static final int RFP_MAX_FILENAME = 256;
    public static final int RFP_MAX_SCRIPT = 16;
    public static final int RFP_MAX_MODEL = 20;
    public static final int RFP_MAX_VERSION = 12;
    public static final int RFP_MAX_VERSIONMIN = 12;
    public static final int RFP_MAX_VERSIONMAX = 12;
    public static final int RFP_MAX_SIGNATURE = 400;

    public byte[] getFileName() {
        return this.fileName;
    }

    public void setFileName(byte[] byArray) {
        this.fileName = byArray;
    }

    public int getMetaFileName() {
        return this.metaFileName;
    }

    public void setMetaFileName(int n) {
        this.metaFileName = n;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int n) {
        this.length = n;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(int n) {
        this.maxLength = n;
    }

    public int getIsScript() {
        return this.isScript;
    }

    public void setIsScript(int n) {
        this.isScript = n;
    }

    public byte[] getScript() {
        return this.script;
    }

    public void setScript(byte[] byArray) {
        this.script = byArray;
    }

    public int getIsSigned() {
        return this.isSigned;
    }

    public void setIsSigned(int n) {
        this.isSigned = n;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public void setSignature(byte[] byArray) {
        this.signature = byArray;
    }

    public int getIsEncrypted() {
        return this.isEncrypted;
    }

    public void setIsEncrypted(int n) {
        this.isEncrypted = n;
    }

    public byte[] getModel() {
        return this.model;
    }

    public void setModel(byte[] byArray) {
        this.model = byArray;
    }

    public byte[] getVersion() {
        return this.version;
    }

    public void setVersion(byte[] byArray) {
        this.version = byArray;
    }

    public byte[] getVersionMin() {
        return this.versionMin;
    }

    public void setVersionMin(byte[] byArray) {
        this.versionMin = byArray;
    }

    public byte[] getVersionMax() {
        return this.versionMax;
    }

    public void setVersionMax(byte[] byArray) {
        this.versionMax = byArray;
    }
}

