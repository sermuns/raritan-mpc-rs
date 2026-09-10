/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_CHANGE_PASSWORD_DATA
extends TRCOMMAND {
    private int oldPassword_OFFSET = 4;
    private int newPassword_OFFSET = this.oldPassword_OFFSET + 24;
    public static final short CMD_LEN = 52;

    public TRCMD_CHANGE_PASSWORD_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_CHANGE_PASSWORD_DATA() {
        super((short)52);
    }

    public byte[] getOldPassword() {
        return this.getBytes(this.oldPassword_OFFSET, 24);
    }

    public void setOldPassword(byte[] byArray) {
        this.setBytes(byArray, this.oldPassword_OFFSET);
    }

    public byte getOldPassword(int n) {
        return this.getOldPassword()[n];
    }

    public void setOldPassword(byte by, int n) {
        this.setByte(by, this.oldPassword_OFFSET + n);
    }

    public byte[] getNewPassword() {
        return this.getBytes(this.newPassword_OFFSET, 24);
    }

    public void setNewPassword(byte[] byArray) {
        this.setBytes(byArray, this.newPassword_OFFSET);
    }

    public byte getNewPassword(int n) {
        return this.getNewPassword()[n];
    }

    public void setNewPassword(byte by, int n) {
        this.setByte(by, this.newPassword_OFFSET + n);
    }
}

