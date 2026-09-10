/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import java.net.InetAddress;
import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRLIB_COMM
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRLIB_COMM.class, new String[]{"findBy", "ipAddress", "serverName", "dnsName", "ipPort"}, new Class[]{Integer.TYPE, Long.TYPE, Byte.TYPE, Byte.TYPE, Integer.TYPE}, new int[]{0, 0, 16, 255, 0});
    private int size;
    private int connType;
    private int findBy;
    private long ipAddress;
    private byte[] serverName = new byte[16];
    private byte[] dnsName = new byte[255];
    private int ipPort;
    private byte[] deviceName = new byte[255];
    private byte[] phoneNumber = new byte[255];
    private byte[] privateKey = new byte[24];
    private InetAddress inetAddress;
    public static final short CMD_LEN = 825;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    public static String long2IPString(long l) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(l >> 24 & 0xFFL);
        stringBuffer.append('.');
        stringBuffer.append(l >> 16 & 0xFFL);
        stringBuffer.append('.');
        stringBuffer.append(l >> 8 & 0xFFL);
        stringBuffer.append('.');
        stringBuffer.append(l & 0xFFL);
        return stringBuffer.toString();
    }

    @Override
    public short getLength() {
        return 825;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int n) {
        this.size = n;
    }

    public int getConnType() {
        return this.connType;
    }

    public void setConnType(int n) {
        this.connType = n;
    }

    public int getFindBy() {
        return this.findBy;
    }

    public void setFindBy(int n) {
        this.findBy = n;
    }

    public long getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(long l) {
        this.ipAddress = l;
    }

    public void setIpAddress(int n) {
        this.ipAddress = n;
        if (this.inetAddress == null) {
            try {
                InetAddress inetAddress = InetAddress.getByName(TRLIB_COMM.long2IPString(n));
                this.setInetAddress(inetAddress);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public byte[] getServerName() {
        return this.serverName;
    }

    public void setServerName(byte[] byArray) {
        this.serverName = byArray;
    }

    public byte[] getDnsName() {
        return this.dnsName;
    }

    public void setDnsName(byte[] byArray) {
        this.dnsName = byArray;
    }

    public int getIpPort() {
        return this.ipPort;
    }

    public void setIpPort(int n) {
        this.ipPort = n;
    }

    public byte[] getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(byte[] byArray) {
        this.deviceName = byArray;
    }

    public byte[] getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(byte[] byArray) {
        this.phoneNumber = byArray;
    }

    public byte[] getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(byte[] byArray) {
        this.privateKey = byArray;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n\t TRLIB_COMM ---------");
        stringBuffer.append("\nserverName =" + new String(this.serverName));
        stringBuffer.append("\ndnsName =" + new String(this.dnsName));
        stringBuffer.append("\ndeviceName=" + new String(this.deviceName));
        stringBuffer.append("\nphoneNumber=" + new String(this.phoneNumber));
        stringBuffer.append("\nprivateKey=" + new String(this.privateKey));
        stringBuffer.append("\nfindBy=" + this.findBy);
        stringBuffer.append("\nipAddr= " + (this.ipAddress >> 24 & 0xFFL) + "." + (this.ipAddress >> 16 & 0xFFL) + "." + (this.ipAddress >> 8 & 0xFFL) + "." + (this.ipAddress & 0xFFL));
        stringBuffer.append("\nipAddress(i)=" + this.ipAddress);
        stringBuffer.append("\ninetAddress=" + this.inetAddress);
        stringBuffer.append("\nipPort =" + this.ipPort);
        return stringBuffer.toString();
    }

    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }
}

