/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_ACCESS_REQUEST_DATA
extends TRCOMMAND {
    private final int flags_OFFSET = 4;
    private final int permissions_OFFSET = 8;
    private final int radiusPacketData_OFFSET = 12;
    public static final short CMD_LEN = 12;

    public TRCMD_ACCESS_REQUEST_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_ACCESS_REQUEST_DATA() {
        super((short)4108);
    }

    public int getFlags() {
        return this.getInt(4);
    }

    public void setFlags(int n) {
        this.setInt(n, 4);
    }

    public int getPermissions() {
        return this.getInt(8);
    }

    public void setPermissions(int n) {
        this.setInt(n, 8);
    }

    public byte[] getRadiusPacketData() {
        return this.getBytes(12, 4096);
    }

    public void setRadiusPacketData(byte[] byArray) {
        this.setBytes(byArray, 12);
    }
}

