/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  netscape.javascript.JSObject
 */
package com.raritan.rrc.util.modem;

import com.raritan.rrc.util.modem.ModemConnector;
import netscape.javascript.JSObject;

public class RRC_RAS_Applet
extends ModemConnector {
    private JSObject win;

    public RRC_RAS_Applet(JSObject jSObject) {
        this.win = jSObject;
    }

    @Override
    public long rasConnect(String string, String string2) {
        long l = this.rasEnumDevices();
        long l2 = 0L;
        int n = 0;
        while ((long)n < l) {
            if (this.rasGetDeviceName(n).equals(string2)) {
                l2 = n;
                break;
            }
            ++n;
        }
        Object[] objectArray = new Object[]{new String(string), new Long(l2)};
        this.win.call("connect", objectArray);
        Object object = this.win.call("getConnectErrCode", null);
        Long l3 = new Long(object.toString());
        return l3;
    }

    @Override
    public long rasEnumConnections() {
        Object object = this.win.call("enumConnections", null);
        Long l = new Long(object.toString());
        return l;
    }

    @Override
    public long rasEnumDevices() {
        Object object = this.win.call("enumDevices", null);
        Long l = new Long(object.toString());
        return l;
    }

    @Override
    public long rasEnumEntries() {
        Object object = this.win.call("enumEntries", null);
        Long l = new Long(object.toString());
        return l;
    }

    @Override
    public String rasGetDeviceName(long l) {
        Object[] objectArray = new Object[]{new Long(l)};
        Object object = this.win.call("getDeviceName", objectArray);
        return (String)object;
    }

    @Override
    public String rasGetDeviceType(long l) {
        Object[] objectArray = new Object[]{new Long(l)};
        Object object = this.win.call("getDeviceType", objectArray);
        return (String)object;
    }

    @Override
    public String rasGetEntryName(long l) {
        Object[] objectArray = new Object[]{new Long(l)};
        Object object = this.win.call("getEntryName", objectArray);
        return (String)object;
    }

    @Override
    public void rasHangUp() {
        this.win.call("HangUp", null);
    }

    @Override
    public String rasGetServerIP() {
        Object object = this.win.call("getServerIP", null);
        return (String)object;
    }
}

