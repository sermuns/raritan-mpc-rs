/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  netscape.javascript.JSObject
 */
package com.raritan.rrc.util.modem;

import com.raritan.rrc.util.ModemOSSupport;
import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.modem.DialStatusListener;
import com.raritan.rrc.util.modem.PPPDConnector;
import com.raritan.rrc.util.modem.RRC_RAS;
import com.raritan.rrc.util.modem.RRC_RAS_Applet;
import java.util.ArrayList;
import java.util.Locale;
import netscape.javascript.JSObject;

public abstract class ModemConnector {
    public static final int ERROR_NONE = 0;
    public static final int ERROR_UNSPECIFIED = 1;
    public static final int ERROR_PORT_INUSE = 633;
    public static final int ERROR_MODEM_REPORTED = 651;
    public static final int ERROR_AUTH_FAILED = 691;
    private ArrayList _dialStatusListeners = new ArrayList();

    protected ModemConnector() {
    }

    public static ModemConnector getConnector(JSObject jSObject) {
        return ModemConnector.getConnector(jSObject, null);
    }

    public static ModemConnector getConnector(JSObject jSObject, Locale locale) {
        if (!ModemOSSupport.isOSSupported()) {
            System.out.println(0);
            return null;
        }
        OS oS = OS.valueOf(ModemOSSupport.getOSname());
        if (oS == OS.WINDOWS) {
            if (jSObject != null) {
                return new RRC_RAS_Applet(jSObject);
            }
            return new RRC_RAS(locale);
        }
        if (oS == OS.LINUX || oS == OS.SOLARIS || oS == OS.MAC) {
            return new PPPDConnector(locale);
        }
        return null;
    }

    public long rasConnect(String string, String string2) throws Exception {
        return 0L;
    }

    public long rasConnectWithIdentity(String string, String string2, String string3, String string4) throws Exception {
        return 0L;
    }

    public long rasEnumConnections() {
        return 0L;
    }

    public long rasEnumDevices() {
        return 0L;
    }

    public long rasEnumEntries() {
        return 0L;
    }

    public String rasGetDeviceName(long l) {
        return "";
    }

    public String rasGetDeviceType(long l) {
        return "";
    }

    public String rasGetEntryName(long l) {
        return "";
    }

    public void rasHangUp() {
    }

    public String rasGetServerIP() {
        return "";
    }

    public synchronized void addDialStatusListener(DialStatusListener dialStatusListener) {
        this._dialStatusListeners.add(dialStatusListener);
    }

    public synchronized void removeDialStatusListener(DialStatusListener dialStatusListener) {
        this._dialStatusListeners.remove(dialStatusListener);
    }

    protected synchronized void fireDialStatusMsg(String string) {
        for (DialStatusListener dialStatusListener : this._dialStatusListeners) {
            dialStatusListener.dialingStatus(string);
        }
    }
}

