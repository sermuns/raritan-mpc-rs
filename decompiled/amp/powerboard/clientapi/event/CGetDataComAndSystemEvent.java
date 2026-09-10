/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalDataComAndSystemEvent;

public class CGetDataComAndSystemEvent
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

    public CGetDataComAndSystemEvent(int n, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4, int[] nArray5, String[] stringArray, int[] nArray6, boolean bl, String string) {
        super(12, bl);
        this.numPorts = n;
        this.baudRate = nArray;
        this.chkParity = nArray2;
        this.parityBits = nArray3;
        this.recvPace = nArray4;
        this.xmitPace = nArray5;
        this.deviceName = stringArray;
        this.port = nArray6;
        this.lockerName = string;
    }

    public CGetDataComAndSystemEvent(boolean bl, String string) {
        super(12, bl);
        this.lockerName = string;
    }

    public CGetDataComAndSystemEvent(int n, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4, int[] nArray5, String[] stringArray, int[] nArray6, int[] nArray7, boolean bl, String string) {
        this(n, nArray, nArray2, nArray3, nArray4, nArray5, stringArray, nArray6, bl, string);
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

    public void setDataComAndSystem(CInternalDataComAndSystemEvent cInternalDataComAndSystemEvent) {
        int n = cInternalDataComAndSystemEvent.getNumPortsX16();
        this.baudRate = new int[n != 0 ? n : 1];
        this.chkParity = new int[n != 0 ? n : 1];
        this.parityBits = new int[n != 0 ? n : 1];
        this.recvPace = new int[n != 0 ? n : 1];
        this.xmitPace = new int[n != 0 ? n : 1];
        this.deviceName = new String[n != 0 ? n : 1];
        this.port = new int[n != 0 ? n : 1];
        this.hwFlowControl = new int[n != 0 ? n : 1];
        if (cInternalDataComAndSystemEvent.getNumPortsX16() == 0) {
            this.baudRate[0] = cInternalDataComAndSystemEvent.getbaudRate();
            this.chkParity[0] = cInternalDataComAndSystemEvent.getchkParity();
            this.parityBits[0] = cInternalDataComAndSystemEvent.getparityBits();
            this.recvPace[0] = cInternalDataComAndSystemEvent.getrecvPace();
            this.xmitPace[0] = cInternalDataComAndSystemEvent.getxmitPace();
            this.deviceName[0] = cInternalDataComAndSystemEvent.getdeviceName();
            this.port[0] = cInternalDataComAndSystemEvent.getport();
        } else {
            this.numPorts = cInternalDataComAndSystemEvent.getNumPortsX16();
            this.baudRate = cInternalDataComAndSystemEvent.getbaudRateX16();
            this.chkParity = cInternalDataComAndSystemEvent.getchkParityX16();
            this.parityBits = cInternalDataComAndSystemEvent.getparityBitsX16();
            this.recvPace = cInternalDataComAndSystemEvent.getrecvPaceX16();
            this.xmitPace = cInternalDataComAndSystemEvent.getxmitPaceX16();
            this.deviceName = cInternalDataComAndSystemEvent.getdeviceNameX16();
            this.port = cInternalDataComAndSystemEvent.getportX16();
            this.hwFlowControl = cInternalDataComAndSystemEvent.getHwFlowControl();
        }
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

