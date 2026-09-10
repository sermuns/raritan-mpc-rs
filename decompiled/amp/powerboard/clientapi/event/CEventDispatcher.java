/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CReportEvent;
import amp.powerboard.clientapi.event.CStatusEvent;
import amp.powerboard.clientapi.event.IDataEventHandler;
import amp.powerboard.clientapi.event.IDataListener;
import amp.powerboard.clientapi.event.IReportEventHandler;
import amp.powerboard.clientapi.event.IReportListener;
import amp.powerboard.clientapi.event.IStatusEventHandler;
import amp.powerboard.clientapi.event.IStatusListener;
import java.util.Enumeration;
import java.util.Vector;

public class CEventDispatcher
implements IDataEventHandler,
IStatusEventHandler,
IReportEventHandler {
    private Vector dataListeners = new Vector(6, 6);
    private Vector statusListeners = new Vector(6, 6);
    private Vector reportListeners = new Vector(6, 6);

    public synchronized void fireDataEvent(CDataEvent cDataEvent) {
        if (this.dataListeners != null) {
            Enumeration enumeration = this.dataListeners.elements();
            while (enumeration.hasMoreElements()) {
                IDataListener iDataListener = (IDataListener)enumeration.nextElement();
                if (iDataListener == null) continue;
                iDataListener.commandDataArrived(cDataEvent);
            }
        }
    }

    public synchronized void fireStatusEvent(CStatusEvent cStatusEvent) {
        Enumeration enumeration = this.statusListeners.elements();
        while (enumeration.hasMoreElements()) {
            IStatusListener iStatusListener = (IStatusListener)enumeration.nextElement();
            iStatusListener.commandStatusArrived(cStatusEvent);
        }
    }

    public synchronized void fireReportEvent(CReportEvent cReportEvent) {
        Enumeration enumeration = this.reportListeners.elements();
        while (enumeration.hasMoreElements()) {
            IReportListener iReportListener = (IReportListener)enumeration.nextElement();
            iReportListener.commandReportArrived(cReportEvent);
        }
    }

    public synchronized void addDataListener(IDataListener iDataListener) {
        if (this.dataListeners.indexOf(iDataListener) == -1) {
            this.dataListeners.addElement(iDataListener);
        }
    }

    public synchronized void removeDataListener(IDataListener iDataListener) {
        if (this.dataListeners.indexOf(iDataListener) == -1) {
            return;
        }
        this.dataListeners.removeElement(iDataListener);
    }

    public synchronized void addStatusListener(IStatusListener iStatusListener) {
        if (this.statusListeners.indexOf(iStatusListener) == -1) {
            this.statusListeners.addElement(iStatusListener);
        }
    }

    public synchronized void removeStatusListener(IStatusListener iStatusListener) {
        if (this.statusListeners.indexOf(iStatusListener) == -1) {
            return;
        }
        this.statusListeners.removeElement(iStatusListener);
    }

    public synchronized void addReportListener(IReportListener iReportListener) {
        if (this.reportListeners.indexOf(iReportListener) == -1) {
            this.reportListeners.addElement(iReportListener);
        }
    }

    public synchronized void removeReportListener(IReportListener iReportListener) {
        if (this.reportListeners.indexOf(iReportListener) == -1) {
            return;
        }
        this.reportListeners.removeElement(iReportListener);
    }

    public synchronized void releaseResources() {
        this.dataListeners.removeAllElements();
        this.statusListeners.removeAllElements();
        this.reportListeners.removeAllElements();
        this.dataListeners = null;
        this.statusListeners = null;
        this.reportListeners = null;
    }

    public synchronized IDataListener[] getDataListeners() {
        int n = this.dataListeners.size();
        Object[] objectArray = new IDataListener[n];
        this.dataListeners.copyInto(objectArray);
        return objectArray;
    }

    public synchronized IStatusListener[] getStatusListeners() {
        int n = this.statusListeners.size();
        Object[] objectArray = new IStatusListener[n];
        this.statusListeners.copyInto(objectArray);
        return objectArray;
    }
}

