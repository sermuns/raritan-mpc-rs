/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_CHANNEL_DATA
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_CHANNEL_DATA.class, new String[]{"channelName", "kvmPortMap"}, new Class[]{Byte.TYPE, Integer.TYPE}, new int[]{32, 16});
    private byte[] channelName = new byte[32];
    private int kvmPortType;
    private int kvmPortCount;
    private int[] kvmPortMap = new int[16];
    private int flags;
    public static final short CMD_LEN = 112;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 112;
    }

    public byte[] getChannelName() {
        return this.channelName;
    }

    public void setChannelName(byte[] byArray) {
        this.channelName = byArray;
    }

    public byte getChannelName(int n) {
        return this.channelName[n];
    }

    public void setChannelName(int n, byte by) {
        this.channelName[n] = by;
    }

    public int getKvmPortType() {
        return this.kvmPortType;
    }

    public void setKvmPortType(int n) {
        this.kvmPortType = n;
    }

    public int getKvmPortCount() {
        return this.kvmPortCount;
    }

    public void setKvmPortCount(int n) {
        this.kvmPortCount = n;
    }

    public int[] getKvmPortMap() {
        return this.kvmPortMap;
    }

    public void setKvmPortMap(int[] nArray) {
        this.kvmPortMap = nArray;
    }

    public int getKvmPortMap(int n) {
        return this.kvmPortMap[n];
    }

    public void setKvmPortMap(int n, int n2) {
        this.kvmPortMap[n] = n2;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int n) {
        this.flags = n;
    }
}

