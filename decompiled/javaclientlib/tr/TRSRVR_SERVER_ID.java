/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_SERVER_ID
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_SERVER_ID.class, new String[]{"cbSize", "serverName", "ipAddress", "port", "protocolVersion", "oldestProtocolVersion", "hwVersion", "post", "netFlags", "securityFlags", "options", "frameGrabberInfo", "kvmInfo", "serialInfo", "numVideoDevices", "numSerialDevices", "reserved", "swVersion"}, new Class[]{Integer.TYPE, Byte.TYPE, Integer.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Short.TYPE}, new int[]{0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    private int cbSize;
    private byte[] serverName = new byte[16];
    private int ipAddress;
    private short port;
    private short protocolVersion;
    private short oldestProtocolVersion;
    private short hwVersion;
    private int post;
    private int netFlags;
    private int securityFlags;
    private int options;
    private int frameGrabberInfo;
    private int kvmInfo;
    private int serialInfo;
    private int numVideoDevices;
    private int numSerialDevices;
    private int reserved;
    private short swVersion;
    public static final short CMD_LEN = 74;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    public TRSRVR_SERVER_ID() {
    }

    public TRSRVR_SERVER_ID(byte[] byArray, boolean bl) {
        this();
        int n = 0;
        this.cbSize = bl ? this.getBigInt(byArray, n, 4) : this.getSmallInt(byArray, n, 4);
        n += 4;
        for (int i = 0; i < 16; ++i) {
            this.serverName[i] = byArray[n + i];
        }
        n += 16;
        if (bl) {
            this.ipAddress = this.getBigInt(byArray, n, 4);
            this.port = (short)this.getBigInt(byArray, n + 4, 2);
            this.protocolVersion = (short)this.getBigInt(byArray, n + 6, 2);
            this.oldestProtocolVersion = (short)this.getBigInt(byArray, n + 8, 2);
            this.hwVersion = (short)this.getBigInt(byArray, n + 10, 2);
            this.post = this.getBigInt(byArray, n + 12, 4);
            this.netFlags = this.getBigInt(byArray, n + 16, 4);
            this.securityFlags = this.getBigInt(byArray, n + 20, 4);
            this.options = this.getBigInt(byArray, n + 24, 4);
            this.frameGrabberInfo = this.getBigInt(byArray, n + 28, 4);
            this.kvmInfo = this.getBigInt(byArray, n + 32, 4);
            this.serialInfo = this.getBigInt(byArray, n + 36, 4);
            this.numVideoDevices = this.getBigInt(byArray, n + 40, 4);
            this.numSerialDevices = this.getBigInt(byArray, n + 44, 4);
            this.reserved = this.getBigInt(byArray, n + 48, 4);
            this.swVersion = (short)this.getBigInt(byArray, n + 52, 2);
        } else {
            this.ipAddress = this.getSmallInt(byArray, n, 4);
            this.port = (short)this.getSmallInt(byArray, n + 4, 2);
            this.protocolVersion = (short)this.getSmallInt(byArray, n + 6, 2);
            this.oldestProtocolVersion = (short)this.getSmallInt(byArray, n + 8, 2);
            this.hwVersion = (short)this.getSmallInt(byArray, n + 10, 2);
            this.post = this.getSmallInt(byArray, n + 12, 4);
            this.netFlags = this.getSmallInt(byArray, n + 16, 4);
            this.securityFlags = this.getSmallInt(byArray, n + 20, 4);
            this.options = this.getSmallInt(byArray, n + 24, 4);
            this.frameGrabberInfo = this.getSmallInt(byArray, n + 28, 4);
            this.kvmInfo = this.getSmallInt(byArray, n + 32, 4);
            this.serialInfo = this.getSmallInt(byArray, n + 36, 4);
            this.numVideoDevices = this.getSmallInt(byArray, n + 40, 4);
            this.numSerialDevices = this.getSmallInt(byArray, n + 44, 4);
            this.reserved = this.getSmallInt(byArray, n + 48, 4);
            this.swVersion = (short)this.getSmallInt(byArray, n + 52, 2);
        }
    }

    private int getBigInt(byte[] byArray, int n, int n2) {
        if (n2 == 2) {
            return ((byArray[n + 1] & 0xFF) << 8) + ((byArray[n + 0] & 0xFF) << 0);
        }
        if (n2 == 4) {
            return ((byArray[n + 3] & 0xFF) << 24) + ((byArray[n + 2] & 0xFF) << 16) + ((byArray[n + 1] & 0xFF) << 8) + ((byArray[n + 0] & 0xFF) << 0);
        }
        return 0;
    }

    private int getSmallInt(byte[] byArray, int n, int n2) {
        if (n2 == 2) {
            return ((byArray[n + 0] & 0xFF) << 8) + ((byArray[n + 1] & 0xFF) << 0);
        }
        if (n2 == 4) {
            return ((byArray[n + 0] & 0xFF) << 24) + ((byArray[n + 1] & 0xFF) << 16) + ((byArray[n + 2] & 0xFF) << 8) + ((byArray[n + 3] & 0xFF) << 0);
        }
        return 0;
    }

    @Override
    public short getLength() {
        return 74;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\ncbSize=" + this.getCbSize() + "\n");
        stringBuffer.append("serverName=" + this.getServerNameString() + "\n");
        stringBuffer.append("ipAddress=" + this.getIpAddress() + "\n");
        stringBuffer.append("port=" + this.getPort() + "\n");
        stringBuffer.append("protocolVersion=" + this.getProtocolVersion() + "\n");
        stringBuffer.append("oldestProtocolVersion=" + this.getOldestProtocolVersion() + "\n");
        stringBuffer.append("hwVersion=" + this.getHwVersion() + "\n");
        stringBuffer.append("post=" + this.getPost() + "\n");
        stringBuffer.append("netFlags=" + this.getNetFlags() + "\n");
        stringBuffer.append("securityFlags=" + this.getSecurityFlags() + "\n");
        stringBuffer.append("options=" + this.getOptions() + "\n");
        stringBuffer.append("frameGrabberInfo=" + this.getFrameGrabberInfo() + "\n");
        stringBuffer.append("kvmInfo=" + this.getKvmInfo() + "\n");
        stringBuffer.append("serialInfo=" + this.getSerialInfo() + "\n");
        stringBuffer.append("numVideoDevices=" + this.getNumVideoDevices() + "\n");
        stringBuffer.append("numSerialDevices=" + this.getNumSerialDevices() + "\n");
        stringBuffer.append("reserved=" + this.getReserved() + "\n");
        stringBuffer.append("swVersion=" + this.getSwVersion() + "\n");
        return stringBuffer.toString();
    }

    public void setTRSRVR_SERVER_ID(TRSRVR_SERVER_ID tRSRVR_SERVER_ID) {
        this.cbSize = tRSRVR_SERVER_ID.getCbSize();
        this.serverName = tRSRVR_SERVER_ID.getServerName();
        this.ipAddress = tRSRVR_SERVER_ID.getIpAddress();
        this.port = tRSRVR_SERVER_ID.getPort();
        this.protocolVersion = tRSRVR_SERVER_ID.getProtocolVersion();
        this.oldestProtocolVersion = tRSRVR_SERVER_ID.getOldestProtocolVersion();
        this.hwVersion = tRSRVR_SERVER_ID.getHwVersion();
        this.post = tRSRVR_SERVER_ID.getPost();
        this.netFlags = tRSRVR_SERVER_ID.getNetFlags();
        this.securityFlags = tRSRVR_SERVER_ID.getSecurityFlags();
        this.options = tRSRVR_SERVER_ID.getOptions();
        this.frameGrabberInfo = tRSRVR_SERVER_ID.getFrameGrabberInfo();
        this.kvmInfo = tRSRVR_SERVER_ID.getKvmInfo();
        this.serialInfo = tRSRVR_SERVER_ID.getSerialInfo();
        this.numVideoDevices = tRSRVR_SERVER_ID.getNumVideoDevices();
        this.numSerialDevices = tRSRVR_SERVER_ID.getNumSerialDevices();
        this.reserved = tRSRVR_SERVER_ID.getReserved();
        this.swVersion = tRSRVR_SERVER_ID.getSwVersion();
    }

    public int getCbSize() {
        return this.cbSize;
    }

    public void setCbSize(int n) {
        this.cbSize = n;
    }

    public byte[] getServerName() {
        return this.serverName;
    }

    public String getServerNameString() {
        StringBuffer stringBuffer = new StringBuffer(16);
        for (int i = 0; i < 16 && this.serverName[i] != 0; ++i) {
            stringBuffer.append((char)this.serverName[i]);
        }
        return new String(stringBuffer);
    }

    public void setServerName(byte[] byArray) {
        this.serverName = byArray;
    }

    public int getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(int n) {
        this.ipAddress = n;
    }

    public short getPort() {
        return this.port;
    }

    public void setPort(short s) {
        this.port = s;
    }

    public short getProtocolVersion() {
        return this.protocolVersion;
    }

    public void setProtocolVersion(short s) {
        this.protocolVersion = s;
    }

    public short getOldestProtocolVersion() {
        return this.oldestProtocolVersion;
    }

    public void setOldestProtocolVersion(short s) {
        this.oldestProtocolVersion = s;
    }

    public short getHwVersion() {
        return this.hwVersion;
    }

    public void setHwVersion(short s) {
        this.hwVersion = s;
    }

    public int getPost() {
        return this.post;
    }

    public void setPost(int n) {
        this.post = n;
    }

    public int getNetFlags() {
        return this.netFlags;
    }

    public void setNetFlags(int n) {
        this.netFlags = n;
    }

    public int getSecurityFlags() {
        return this.securityFlags;
    }

    public void setSecurityFlags(int n) {
        this.securityFlags = n;
    }

    public int getOptions() {
        return this.options;
    }

    public void setOptions(int n) {
        this.options = n;
    }

    public int getFrameGrabberInfo() {
        return this.frameGrabberInfo;
    }

    public void setFrameGrabberInfo(int n) {
        this.frameGrabberInfo = n;
    }

    public int getKvmInfo() {
        return this.kvmInfo;
    }

    public void setKvmInfo(int n) {
        this.kvmInfo = n;
    }

    public int getSerialInfo() {
        return this.serialInfo;
    }

    public void setSerialInfo(int n) {
        this.serialInfo = n;
    }

    public int getNumVideoDevices() {
        return this.numVideoDevices;
    }

    public void setNumVideoDevices(int n) {
        this.numVideoDevices = n;
    }

    public int getNumSerialDevices() {
        return this.numSerialDevices;
    }

    public void setNumSerialDevices(int n) {
        this.numSerialDevices = n;
    }

    public int getReserved() {
        return this.reserved;
    }

    public void setReserved(int n) {
        this.reserved = n;
    }

    public short getSwVersion() {
        return this.swVersion;
    }

    public void setSwVersion(short s) {
        this.swVersion = s;
    }
}

