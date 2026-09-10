/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_IDENTIFY_REMOTE_DATA
extends TRCOMMAND {
    private int protocolVersion_OFFSET = 4;
    private int osType_OFFSET = this.protocolVersion_OFFSET + 4;
    private int flags_OFFSET = this.osType_OFFSET + 4;
    private int extraData_OFFSET = this.flags_OFFSET + 4;
    private int reserved_OFFSET = this.extraData_OFFSET + 4;
    public static final short CMD_LEN = 36;

    public TRCMD_IDENTIFY_REMOTE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_IDENTIFY_REMOTE_DATA() {
        super((short)36);
    }

    public int getProtocolVersion() {
        return this.getInt(this.protocolVersion_OFFSET);
    }

    public void setProtocolVersion(int n) {
        this.setInt(n, this.protocolVersion_OFFSET);
    }

    public int getOSType() {
        return this.getInt(this.osType_OFFSET);
    }

    public void setOSType(int n) {
        this.setInt(n, this.osType_OFFSET);
    }

    public int getFlags() {
        return this.getInt(this.flags_OFFSET);
    }

    public void setFlags(int n) {
        this.setInt(n, this.flags_OFFSET);
    }

    public int getExtraData() {
        return this.getInt(this.extraData_OFFSET);
    }

    public void setExtraData(int n) {
        this.setInt(n, this.extraData_OFFSET);
    }

    public int[] getReserved() {
        return this.getIntArray(this.reserved_OFFSET, 4);
    }

    public void setReserved(int[] nArray) {
        for (int i = 0; i < nArray.length; ++i) {
            this.setReserved(nArray[i], this.reserved_OFFSET + i * 4);
        }
    }

    public int getReserved(int n) {
        return this.getInt(this.reserved_OFFSET + n * 4);
    }

    public void setReserved(int n, int n2) {
        this.setInt(n, n2);
    }
}

