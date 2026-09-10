/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_LOG_ERROR
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_LOG_ERROR.class, new String[]{"fileName", "fileNumber", "lineNumber", "errorCode", "param"}, new Class[]{Byte.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new int[]{24, 0, 0, 0, 0});
    private byte[] fileName = new byte[24];
    private int fileNumber;
    private int lineNumber;
    private int errorCode;
    private int param;
    public static final short CMD_LEN = 40;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 40;
    }

    public byte[] getFileName() {
        return this.fileName;
    }

    public void setFileName(byte[] byArray) {
        this.fileName = byArray;
    }

    public byte getFileName(int n) {
        return this.fileName[n];
    }

    public void setFileName(int n, byte by) {
        this.fileName[n] = by;
    }

    public int getFileNumber() {
        return this.fileNumber;
    }

    public void setFileNumber(int n) {
        this.fileNumber = n;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(int n) {
        this.lineNumber = n;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int n) {
        this.errorCode = n;
    }

    public int getParam() {
        return this.param;
    }

    public void setParam(int n) {
        this.param = n;
    }
}

