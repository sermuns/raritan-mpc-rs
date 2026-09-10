/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_MOUSE_DATA
extends TRCOMMAND {
    private int deviceID_OFFSET = 4;
    private int function_OFFSET = this.deviceID_OFFSET + 1;
    private int x_OFFSET = this.function_OFFSET + 1;
    private int y_OFFSET = this.x_OFFSET + 2;
    public static final short CMD_LEN = 10;

    public TRCMD_MOUSE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_MOUSE_DATA() {
        super((short)10);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public byte getFunction() {
        return this.getByte(this.function_OFFSET);
    }

    public void setFunction(byte by) {
        this.setByte(by, this.function_OFFSET);
    }

    public short getX() {
        return this.getShort(this.x_OFFSET);
    }

    public void setX(short s) {
        this.setShort(s, this.x_OFFSET);
    }

    public short getY() {
        return this.getShort(this.y_OFFSET);
    }

    public void setY(short s) {
        this.setShort(s, this.y_OFFSET);
    }
}

