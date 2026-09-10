/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.util.Util;
import java.util.ArrayList;
import javax.swing.JDialog;

public final class MPCUtil {
    private static boolean isCCMultiMonitorLaunch = false;

    private MPCUtil() {
    }

    public static synchronized void notifyObservers(RRCScreenContext rRCScreenContext, Device device) {
        if (device == null) {
            rRCScreenContext.getSelectedDevicesObservable().setComponent(null);
            return;
        }
        ArrayList<Device> arrayList = new ArrayList<Device>(1);
        arrayList.add(device);
        rRCScreenContext.getSelectedDevicesObservable().setComponent(arrayList);
    }

    public static synchronized boolean isCCLaunched(RRCScreenContext rRCScreenContext) {
        return rRCScreenContext.getApplicationProperty("connection") != null;
    }

    public static synchronized boolean isCCMultiMonitorLaunch() {
        return isCCMultiMonitorLaunch;
    }

    public static synchronized void setCCMultiMonitorLaunch(boolean bl) {
        isCCMultiMonitorLaunch = bl;
    }

    public static synchronized void notifyObserversIfSelected(RRCScreenContext rRCScreenContext, Device device) {
        ArrayList arrayList;
        Object object = rRCScreenContext.getSelectedDevicesObservable().getComponent();
        if (object instanceof ArrayList && (arrayList = (ArrayList)object).size() > 0 && arrayList.get(0).equals(device)) {
            MPCUtil.notifyObservers(rRCScreenContext, device);
        }
    }

    public static void clearUnfocusedMenus(RRCScreenContext rRCScreenContext) {
        ArrayList arrayList = rRCScreenContext.getListOfOpenPorts();
        Port port = null;
        for (int i = 0; arrayList != null && i < arrayList.size(); ++i) {
            port = (Port)rRCScreenContext.getPortByKeyObservable((String)arrayList.get(i));
            if (port == null || !(port.getView() instanceof RFBView)) continue;
            ((RFBView)port.getView()).setContextMenuKVMVisible(false);
        }
    }

    public static synchronized void notifyOpenPortsObservers(RRCScreenContext rRCScreenContext) {
        if (rRCScreenContext != null) {
            rRCScreenContext.sendOpenPortNotification();
        }
    }

    public static void jre17WorkaroundInheritAlwaysOnTop(JDialog jDialog) {
        Util.jre17WorkaroundInheritAlwaysOnTop(jDialog);
    }
}

