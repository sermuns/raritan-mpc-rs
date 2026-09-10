/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalDataComAndSystemEvent
extends CDataEvent {
    private int[] baudRate;
    private int[] chkParity;
    private int[] parityBits;
    private int[] recvPace;
    private int[] xmitPace;
    private String[] deviceName;
    private int[] port;
    private int numPorts = 0;
    private int[] hwFlowControl;

    public CInternalDataComAndSystemEvent(int n, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4, int[] nArray5, String[] stringArray, int[] nArray6) {
        super(122);
        this.numPorts = n;
        this.baudRate = nArray;
        this.chkParity = nArray3;
        this.parityBits = nArray2;
        this.recvPace = nArray4;
        this.xmitPace = nArray5;
        this.deviceName = stringArray;
        this.port = nArray6;
    }

    public CInternalDataComAndSystemEvent(int n, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4, int[] nArray5, String[] stringArray, int[] nArray6, int[] nArray7) {
        this(n, nArray, nArray2, nArray3, nArray4, nArray5, stringArray, nArray6);
        this.hwFlowControl = nArray7;
    }

    public int getbaudRate() {
        return this.baudRate[0];
    }

    public int getchkParity() {
        return this.chkParity[0];
    }

    public int getparityBits() {
        return this.parityBits[0];
    }

    public int getrecvPace() {
        return this.recvPace[0];
    }

    public int getxmitPace() {
        return this.xmitPace[0];
    }

    public String getdeviceName() {
        return this.deviceName[0];
    }

    public int getport() {
        return this.port[0];
    }

    public int[] getbaudRateX16() {
        return this.baudRate;
    }

    public int[] getchkParityX16() {
        return this.chkParity;
    }

    public int[] getparityBitsX16() {
        return this.parityBits;
    }

    public int[] getrecvPaceX16() {
        return this.recvPace;
    }

    public int[] getxmitPaceX16() {
        return this.xmitPace;
    }

    public String[] getdeviceNameX16() {
        return this.deviceName;
    }

    public int[] getportX16() {
        return this.port;
    }

    public int getNumPortsX16() {
        return this.numPorts;
    }

    public int[] getHwFlowControl() {
        return this.hwFlowControl;
    }
}

