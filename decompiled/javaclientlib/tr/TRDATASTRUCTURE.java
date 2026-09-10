/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;
import javaclientlib.utils.BigEndianInputStream;
import javaclientlib.utils.BigEndianOutputStream;
import javaclientlib.utils.RRCGeneralException;

public abstract class TRDATASTRUCTURE {
    private int readBytes = 0;
    private int writeBytes = 0;
    private boolean boolBigEndian = false;

    public abstract TRDATASTRUCTURE_DEF getDefinition();

    public short getLength() throws Exception {
        throw new Exception("Length() not Implemented in " + this);
    }

    public void populate(byte[] byArray) throws Exception {
        this.boolBigEndian = false;
        this.readBytes = 0;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byArray);
        if (this.getDefinition() != null) {
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
            try {
                this.readBytes += this.getDefinition().read(this, dataInputStream);
            }
            catch (Exception exception) {
                exception.printStackTrace(System.err);
                throw exception;
            }
        } else {
            System.err.println("TODO-READ[ " + this.getClass().getName() + " ]");
            throw new RRCGeneralException("TRDATASTRUCTURE_DEF not implemented for class: " + this.getClass().getName());
        }
    }

    public void populateBigEndian(byte[] byArray) throws Exception {
        this.boolBigEndian = true;
        this.readBytes = 0;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byArray);
        if (this.getDefinition() != null) {
            BigEndianInputStream bigEndianInputStream = new BigEndianInputStream(byteArrayInputStream);
            this.readBytes += this.getDefinition().read(this, bigEndianInputStream);
        } else {
            System.err.println("TODO[ " + this.getClass().getName() + " ]");
            throw new RRCGeneralException("TRDATASTRUCTURE_DEF not implemented for class: " + this.getClass().getName());
        }
    }

    public byte[] getDataBytes() throws Exception {
        this.boolBigEndian = false;
        this.writeBytes = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.getLength());
        if (this.getDefinition() != null) {
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            this.writeBytes += this.getDefinition().write(this, dataOutputStream);
        } else {
            System.err.println("TODO-WRITE[ " + this.getClass().getName() + " ]");
            throw new RRCGeneralException("TRDATASTRUCTURE_DEF not implemented for class: " + this.getClass().getName());
        }
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getBigEndianBytes() throws Exception {
        this.boolBigEndian = true;
        this.writeBytes = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.getLength());
        if (this.getDefinition() != null) {
            BigEndianOutputStream bigEndianOutputStream = new BigEndianOutputStream(byteArrayOutputStream);
            this.writeBytes += this.getDefinition().write(this, bigEndianOutputStream);
        } else {
            System.err.println("TODO-WRITE[ " + this.getClass().getName() + " ]");
            throw new RRCGeneralException("TRDATASTRUCTURE_DEF not implemented for class: " + this.getClass().getName());
        }
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private void log(String string) {
    }
}

