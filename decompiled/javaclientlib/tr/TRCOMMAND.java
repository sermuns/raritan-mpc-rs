/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRBASECOMMAND;

public class TRCOMMAND
extends TRBASECOMMAND {
    private int cmdLength_OFFSET = 0;
    private int command_OFFSET = this.cmdLength_OFFSET + 2;
    private int pktID_OFFSET = this.command_OFFSET + 1;
    public static final short CMD_LENGTH = 4;

    public TRCOMMAND(byte[] byArray) {
        super(byArray);
    }

    public TRCOMMAND(short s) {
        super(s);
    }

    public TRCOMMAND(int n) {
        super(n);
    }

    public TRCOMMAND() {
        super((short)4);
    }

    public int getCmdLength() {
        return this.getUShort(this.cmdLength_OFFSET);
    }

    public void setCmdLength(short s) {
        this.setShort(s, this.cmdLength_OFFSET);
    }

    public byte getCommand() {
        return this.getByte(this.command_OFFSET);
    }

    public void setCommand(byte by) {
        this.setByte(by, this.command_OFFSET);
    }

    public byte getPktID() {
        return this.getByte(this.pktID_OFFSET);
    }

    public void setPktID(byte by) {
        this.setByte(by, this.pktID_OFFSET);
    }
}

