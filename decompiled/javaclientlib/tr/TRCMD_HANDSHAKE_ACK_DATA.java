/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_HANDSHAKE_ACK_DATA
extends TRCOMMAND {
    private int signature_OFFSET = 4;
    private int clientIp_OFFSET = this.signature_OFFSET + 4;
    private int timeStamp_OFFSET = this.clientIp_OFFSET + 4;
    private int index_OFFSET = this.timeStamp_OFFSET + 4;
    private int random_OFFSET = this.index_OFFSET + 4;
    private int connCount_OFFSET = this.random_OFFSET + 4;
    private int checksum_OFFSET = this.connCount_OFFSET + 4;
    private int clientPort_OFFSET = this.checksum_OFFSET + 4;
    public static final short CMD_LEN = 34;

    public TRCMD_HANDSHAKE_ACK_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_HANDSHAKE_ACK_DATA() {
        super((short)34);
    }

    public int getSignature() {
        return this.getInt(this.signature_OFFSET);
    }

    public void setSignature(int n) {
        this.setInt(n, this.signature_OFFSET);
    }

    public int getClientIp() {
        return this.getInt(this.clientIp_OFFSET);
    }

    public void setClientIp(int n) {
        this.setInt(n, this.clientIp_OFFSET);
    }

    public int getTimeStamp() {
        return this.getInt(this.timeStamp_OFFSET);
    }

    public void setTimeStamp(int n) {
        this.setInt(n, this.timeStamp_OFFSET);
    }

    public int getIndex() {
        return this.getInt(this.index_OFFSET);
    }

    public void setIndex(int n) {
        this.setInt(n, this.index_OFFSET);
    }

    public int getRandom() {
        return this.getInt(this.random_OFFSET);
    }

    public void setRandom(int n) {
        this.setInt(n, this.random_OFFSET);
    }

    public int getConnCount() {
        return this.getInt(this.connCount_OFFSET);
    }

    public void setConnCount(int n) {
        this.setInt(n, this.connCount_OFFSET);
    }

    public int getChecksum() {
        return this.getInt(this.checksum_OFFSET);
    }

    public void setChecksum(int n) {
        this.setInt(n, this.checksum_OFFSET);
    }

    public short getClientPort() {
        return this.getShort(this.clientPort_OFFSET);
    }

    public void setClientPort(short s) {
        this.setShort(s, this.clientPort_OFFSET);
    }

    public void setHandshakeAckData(TRCMD_HANDSHAKE_ACK_DATA tRCMD_HANDSHAKE_ACK_DATA) {
        this.setSignature(tRCMD_HANDSHAKE_ACK_DATA.getSignature());
        this.setClientIp(tRCMD_HANDSHAKE_ACK_DATA.getClientIp());
        this.setTimeStamp(tRCMD_HANDSHAKE_ACK_DATA.getTimeStamp());
        this.setIndex(tRCMD_HANDSHAKE_ACK_DATA.getIndex());
        this.setRandom(tRCMD_HANDSHAKE_ACK_DATA.getRandom());
        this.setConnCount(tRCMD_HANDSHAKE_ACK_DATA.getConnCount());
        this.setChecksum(tRCMD_HANDSHAKE_ACK_DATA.getChecksum());
        this.setClientPort(tRCMD_HANDSHAKE_ACK_DATA.getClientPort());
    }
}

