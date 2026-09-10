/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util.modem;

import com.raritan.rrc.util.modem.ModemConnector;
import com.raritan.rrc.util.modem.WinDialingStatus;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import java.util.Locale;
import javaclientlib.utils.RRCLogger;

public class RRC_RAS
extends ModemConnector {
    private Object LOCK = new Object();
    private Object HANGUP_SERIALIZER = new Object();
    private static RRC_RAS _sConnectionOwner = null;
    private WinDialingStatus _dialState;
    private RaritanPropertyResourceBundle _bundle;

    public RRC_RAS(Locale locale) {
        String string = RRC_RAS.getDLLName();
        RRCLogger.log(300, -1, "Loading modem DLL with name " + string);
        System.loadLibrary(string);
        if (locale != null) {
            this._bundle = RaritanResourceBundle.getResourceBundle(locale);
        }
    }

    private static String getDLLName() {
        if ("amd64".equals(System.getProperty("os.arch"))) {
            return "RAS_x64";
        }
        return "RAS";
    }

    private native long connect(String var1, long var2);

    private native long connectWithIdentity(String var1, long var2, String var4, String var5);

    private native long enumConnections();

    private native long enumDevices();

    private native long enumEntries();

    private native String getDeviceName(long var1);

    private native String getDeviceType(long var1);

    private native String getEntryName(long var1);

    private native void hangUp();

    private native String GetIPServer();

    private static synchronized void _setCurrentOwner(RRC_RAS rRC_RAS) {
        _sConnectionOwner = rRC_RAS;
    }

    private static synchronized boolean _isCurrentOwner(RRC_RAS rRC_RAS) {
        return _sConnectionOwner == null ? false : _sConnectionOwner == rRC_RAS;
    }

    private static synchronized boolean _isOwned() {
        return _sConnectionOwner != null;
    }

    @Override
    public long rasConnect(String string, String string2) throws InterruptedException {
        return this.rasConnectWithIdentity(string, string2, "PPP", "PPPpw");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long rasConnectWithIdentity(String string, String string2, String string3, String string4) throws InterruptedException {
        long l;
        if (RRC_RAS._isOwned()) {
            return 633L;
        }
        long l2 = this.rasEnumDevices();
        long l3 = 0L;
        int n = 0;
        while ((long)n < l2) {
            if (this.rasGetDeviceName(n).equals(string2)) {
                l3 = n;
                break;
            }
            ++n;
        }
        Object object = this.LOCK;
        synchronized (object) {
            this._dialState = null;
            l = this.connectWithIdentity(string, l3, string3, string4);
            if (l == 0L) {
                while (this._dialState == null) {
                    try {
                        this.LOCK.wait();
                    }
                    catch (InterruptedException interruptedException) {
                        RRCLogger.log(200, 4, interruptedException, "rasConnectWithIdentity thread Interrupted");
                        throw interruptedException;
                    }
                }
                l = this._dialState.getErrorCode();
            }
        }
        if (l == 0L) {
            RRC_RAS._setCurrentOwner(this);
        } else {
            this.hangUp();
        }
        return l;
    }

    @Override
    public long rasEnumConnections() {
        return this.enumConnections();
    }

    @Override
    public long rasEnumDevices() {
        return this.enumDevices();
    }

    @Override
    public long rasEnumEntries() {
        return this.enumEntries();
    }

    @Override
    public String rasGetDeviceName(long l) {
        return this.getDeviceName(l);
    }

    @Override
    public String rasGetDeviceType(long l) {
        return this.getDeviceType(l);
    }

    @Override
    public String rasGetEntryName(long l) {
        return this.getEntryName(l);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void rasHangUp() {
        Object object = this.HANGUP_SERIALIZER;
        synchronized (object) {
            if (!RRC_RAS._isOwned()) {
                this.hangUp();
            } else if (RRC_RAS._isCurrentOwner(this)) {
                this.hangUp();
                RRC_RAS._setCurrentOwner(null);
            }
        }
    }

    @Override
    public String rasGetServerIP() {
        return this.GetIPServer();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dialingStatus(WinDialingStatus winDialingStatus) {
        RRCLogger.log(300, 4, winDialingStatus.toString());
        if (winDialingStatus.isError() || winDialingStatus.isConnected()) {
            Object object = this.LOCK;
            synchronized (object) {
                this._dialState = winDialingStatus;
                this.LOCK.notify();
            }
        } else if (winDialingStatus.isWaitingForCallback()) {
            String string = "dial.windialstatus." + winDialingStatus.getState();
            if (this._bundle != null) {
                string = this._bundle.getString(string);
            }
            this.fireDialStatusMsg(string);
        }
    }
}

