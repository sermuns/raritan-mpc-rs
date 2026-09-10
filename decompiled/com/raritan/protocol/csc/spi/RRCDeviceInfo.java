/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.csc.spi;

import java.net.InetAddress;
import java.net.SocketAddress;

public class RRCDeviceInfo
implements com.raritan.protocol.csc.RRCDeviceInfo {
    protected String m_Name = null;
    protected String m_Model = null;
    protected String m_Type = null;
    protected String m_ClusterId = null;
    protected String m_Version = null;
    private int m_Port = 0;
    private String deviceID;
    private InetAddress inetAddress;
    private String dnsName;
    private SocketAddress socketAddress;
    protected String m_ProductName = null;

    public RRCDeviceInfo() {
    }

    public RRCDeviceInfo(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public RRCDeviceInfo(String string, String string2, String string3, String string4, String string5) {
        this.m_Name = string;
        this.m_Model = string3;
        this.m_Type = string2;
        this.m_ClusterId = string4;
        this.m_Version = string5;
    }

    @Override
    public String getClusterId() {
        return this.m_ClusterId;
    }

    @Override
    public String getModel() {
        return this.m_Model;
    }

    @Override
    public String getName() {
        return this.m_Name;
    }

    @Override
    public String getType() {
        return this.m_Type;
    }

    @Override
    public String getVersion() {
        return this.m_Version;
    }

    public void setClusterId(String string) {
        this.m_ClusterId = string;
    }

    public void setModel(String string) {
        this.m_Model = string;
    }

    public void setName(String string) {
        this.m_Name = string;
    }

    public void setType(String string) {
        this.m_Type = string;
    }

    public void setVersion(String string) {
        this.m_Version = string;
    }

    @Override
    public String getHost() {
        return this.getInetAddress().getHostAddress();
    }

    @Override
    public int getPort() {
        return this.m_Port;
    }

    public void setPort(int n) {
        this.m_Port = n;
    }

    @Override
    public String getProductName() {
        return this.m_ProductName;
    }

    public void setProductName(String string) {
        this.m_ProductName = string;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("\n---------- Device Info -------------");
        stringBuffer.append("\n\tName: " + this.m_Name);
        stringBuffer.append("\n\tType: " + this.m_Type);
        stringBuffer.append("\n\tModel: " + this.m_Model);
        stringBuffer.append("\n\tVersion: " + this.m_Version);
        stringBuffer.append("\n\tClusterID: " + this.m_ClusterId);
        stringBuffer.append("\n\tProductName: " + this.m_ProductName);
        stringBuffer.append("\n\tPort: " + this.m_Port);
        stringBuffer.append("\n\tdeviceID: " + this.deviceID);
        stringBuffer.append("\n\tinetAddress: " + this.inetAddress);
        stringBuffer.append("\n\tDNSName: " + this.dnsName);
        return stringBuffer.toString();
    }

    public void setDeviceID(String string) {
        this.deviceID = string;
    }

    @Override
    public String getDeviceID() {
        return this.deviceID;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    @Override
    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    public void setDnsName(String string) {
        this.dnsName = string;
    }

    @Override
    public String getDnsName() {
        return this.dnsName;
    }

    @Override
    public SocketAddress getSocketAddress() {
        return this.socketAddress;
    }
}

