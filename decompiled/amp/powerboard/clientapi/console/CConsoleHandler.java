/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.console;

import amp.powerboard.clientapi.command.CBreak;
import amp.powerboard.clientapi.command.CNewMaster;
import amp.powerboard.clientapi.command.ISendInterface;
import amp.powerboard.clientapi.common.exception.CConnectionException;
import amp.powerboard.clientapi.common.exception.CDataException;
import amp.powerboard.clientapi.common.exception.CInvalidUserException;
import amp.powerboard.clientapi.common.exception.CMaxUserExceededException;
import amp.powerboard.clientapi.common.exception.CNotLoggedException;
import amp.powerboard.clientapi.common.exception.CNotMasterException;
import amp.powerboard.clientapi.common.exception.CSecurityException;
import amp.powerboard.clientapi.common.exception.CUserAlreadyLoggedException;
import amp.powerboard.clientapi.console.CConsoleListenerThread;
import amp.powerboard.clientapi.console.ISendConsole;
import amp.powerboard.clientapi.event.CClientListEvent;
import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CReportEvent;
import amp.powerboard.clientapi.event.CStatusEvent;
import amp.powerboard.clientapi.event.IConsoleEventHandler;
import amp.powerboard.clientapi.event.IConsoleListener;
import amp.powerboard.clientapi.event.IDataEventHandler;
import amp.powerboard.clientapi.event.IDataListener;
import amp.powerboard.clientapi.event.IReportEventHandler;
import amp.powerboard.clientapi.event.IReportListener;
import amp.powerboard.clientapi.event.IStatusEventHandler;
import amp.powerboard.clientapi.event.IStatusListener;
import amp.powerboard.clientapi.net.CSecureSocket;
import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CMd5;
import amp.powerboard.clientapi.security.CXorKey;
import amp.powerboard.clientapi.services.IBoxInterface;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;

public class CConsoleHandler
implements IDataListener,
IReportListener,
IStatusListener,
IConsoleEventHandler,
ISendConsole {
    private CSecureSocket secConnection;
    private OutputStream secureOut;
    private InputStream secureInput;
    private IDataEventHandler dataEventhandler;
    private IStatusEventHandler statusEventhandler;
    private IReportEventHandler reportEventhandler;
    private CConsoleListenerThread consoleListener;
    private ISendInterface sendInterface;
    private Hashtable boxData = null;
    private String userName;
    private boolean isMaster;
    private boolean waitingMasterAck;
    private boolean breakResponsestate;
    private Vector consoleListeners;
    private CXorKey inKey = new CXorKey(55);
    private CXorKey outKey = new CXorKey(55);

    public CConsoleHandler(ISendInterface iSendInterface, IStatusEventHandler iStatusEventHandler, IReportEventHandler iReportEventHandler, IDataEventHandler iDataEventHandler, String string, int n, String string2, String string3, int n2, IBoxInterface iBoxInterface, CCryptoKey cCryptoKey, CCryptoKey cCryptoKey2, int n3, String string4, String string5, String string6, byte[] byArray) throws CInvalidUserException, CUserAlreadyLoggedException, CMaxUserExceededException, InternalError, IOException, UnknownHostException {
        this.sendInterface = iSendInterface;
        this.statusEventhandler = iStatusEventHandler;
        this.reportEventhandler = iReportEventHandler;
        this.dataEventhandler = iDataEventHandler;
        this.boxData = iBoxInterface.getBoxProperty();
        String string7 = (String)this.boxData.get("ChallengeId");
        int n4 = Integer.parseInt(string7);
        InetAddress inetAddress = null;
        String string8 = (String)this.boxData.get("IPAddress");
        inetAddress = InetAddress.getByName(string8);
        String string9 = (String)this.boxData.get("Challenge");
        byte[] byArray2 = new byte[string9.length() / 2];
        int n5 = 0;
        while (n5 < byArray2.length) {
            byArray2[n5] = (byte)Integer.parseInt(string9.substring(n5 * 2, n5 * 2 + 2), 16);
            ++n5;
        }
        byte[] byArray3 = new byte[string3.length()];
        string3.getBytes(0, string3.length(), byArray3, 0);
        CMd5 cMd5 = new CMd5();
        cMd5.update(byArray3);
        byte[] byArray4 = cMd5.digest();
        this.dataEventhandler.addDataListener(this);
        this.statusEventhandler.addStatusListener(this);
        this.dataEventhandler.addDataListener(this);
        this.statusEventhandler.addStatusListener(this);
        this.secConnection = new CSecureSocket(n, 2, inetAddress, n2, string2, n4, byArray2, string3, cCryptoKey, cCryptoKey2, n3, string4, string5, string6, byArray, null);
        this.consoleListener = new CConsoleListenerThread(this.secConnection.getInputStream(), this);
        this.secureOut = this.secConnection.getOutputStream();
        this.secureInput = this.secConnection.getInputStream();
        this.isMaster = false;
        this.waitingMasterAck = true;
        this.breakResponsestate = true;
        this.userName = string;
        this.consoleListener.start();
        this.consoleListeners = new Vector();
    }

    public CConsoleHandler(ISendInterface iSendInterface, IStatusEventHandler iStatusEventHandler, IReportEventHandler iReportEventHandler, IDataEventHandler iDataEventHandler, String string, int n, IBoxInterface iBoxInterface, int n2, int n3, int n4, String string2, String string3) throws CInvalidUserException, CUserAlreadyLoggedException, CMaxUserExceededException, InternalError, IOException, UnknownHostException {
        this.sendInterface = iSendInterface;
        this.statusEventhandler = iStatusEventHandler;
        this.reportEventhandler = iReportEventHandler;
        this.dataEventhandler = iDataEventHandler;
        this.boxData = iBoxInterface.getBoxProperty();
        InetAddress inetAddress = null;
        inetAddress = InetAddress.getLocalHost();
        this.dataEventhandler.addDataListener(this);
        this.statusEventhandler.addStatusListener(this);
        this.dataEventhandler.addDataListener(this);
        this.statusEventhandler.addStatusListener(this);
        this.secConnection = new CSecureSocket(inetAddress, n, n2, n3, n4, 2, string2);
        this.consoleListener = new CConsoleListenerThread(this.secConnection.getInputStream(), this);
        this.secureOut = this.secConnection.getOutputStream();
        this.secureInput = this.secConnection.getInputStream();
        this.isMaster = true;
        this.waitingMasterAck = false;
        this.breakResponsestate = true;
        this.userName = string;
        this.consoleListener.start();
        this.consoleListeners = new Vector();
    }

    private void secureWrite(byte[] byArray, int n, int n2) throws IOException {
        int n3 = 0;
        byte[] byArray2 = new byte[(n2 - n) * 2 + 2];
        int n4 = n;
        while (n4 < n2) {
            if (byArray[n4] == -1) {
                byArray2[n3++] = -1;
            }
            byArray2[n3++] = byArray[n4];
            ++n4;
        }
        while (!this.breakResponsestate) {
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        this.secureOut.write(byArray2, 0, n3);
        this.secureOut.flush();
    }

    public synchronized void addConsoleListener(IConsoleListener iConsoleListener) {
        if (this.consoleListeners.indexOf(iConsoleListener) == -1) {
            this.consoleListeners.addElement(iConsoleListener);
        }
    }

    public synchronized void removeConsoleListener(IConsoleListener iConsoleListener) {
        if (this.consoleListeners.indexOf(iConsoleListener) == -1) {
            return;
        }
        this.consoleListeners.removeElement(iConsoleListener);
    }

    public void commandDataArrived(CDataEvent cDataEvent) {
        if (this.waitingMasterAck && cDataEvent.getOpcode() == 1503) {
            CClientListEvent cClientListEvent = (CClientListEvent)cDataEvent;
            int n = cClientListEvent.getMasterIndex();
            String[] stringArray = cClientListEvent.getListOfClients();
            this.isMaster = n == 0 ? false : stringArray[n - 1].equals(this.userName);
        }
    }

    public void commandStatusArrived(CStatusEvent cStatusEvent) {
        if (cStatusEvent.getOpcode() == 3000) {
            this.breakResponsestate = true;
        }
    }

    public void commandReportArrived(CReportEvent cReportEvent) {
    }

    public void send(byte by) throws CNotMasterException, IOException {
        if (!this.isMaster) {
            throw new CNotMasterException();
        }
        this.secureOut = this.secConnection.getOutputStream();
        byte[] byArray = new byte[]{by};
        this.secureWrite(byArray, 0, 1);
    }

    public void send(byte[] byArray) throws CNotMasterException, IOException {
        if (!this.isMaster) {
            throw new CNotMasterException();
        }
        this.secureWrite(byArray, 0, byArray.length);
    }

    public void send(String string) throws CNotMasterException, IOException {
        if (!this.isMaster) {
            throw new CNotMasterException();
        }
        this.secureOut = this.secConnection.getOutputStream();
        byte[] byArray = string.getBytes();
        this.secureWrite(byArray, 0, byArray.length);
    }

    public synchronized void fireBytesArrived(byte[] byArray, int n) {
        int n2 = this.consoleListeners.size();
        int n3 = 0;
        while (n3 < n2) {
            IConsoleListener iConsoleListener = (IConsoleListener)this.consoleListeners.elementAt(n3);
            iConsoleListener.bytesArrived(byArray, n);
            ++n3;
        }
    }

    public void becomeMaster() throws CSecurityException, CNotLoggedException, CConnectionException {
        if (!this.isMaster) {
            CNewMaster cNewMaster = new CNewMaster();
            try {
                cNewMaster.setProperty("LOCK_REQUIRED", false);
            }
            catch (CDataException cDataException) {
                // empty catch block
            }
            try {
                this.sendInterface.sendCommand(cNewMaster);
            }
            catch (CDataException cDataException) {
                // empty catch block
            }
            this.waitingMasterAck = true;
        }
    }

    public void sendBreak() throws CSecurityException, CNotLoggedException, CConnectionException {
        if (this.isMaster) {
            CBreak cBreak = new CBreak();
            try {
                cBreak.setProperty("LOCK_REQUIRED", false);
            }
            catch (Exception exception) {
                // empty catch block
            }
            try {
                this.sendInterface.sendCommand(cBreak);
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.breakResponsestate = false;
        } else {
            try {
                this.becomeMaster();
            }
            catch (Exception exception) {}
        }
    }

    public void closeConsole() throws IOException {
        if (this.dataEventhandler != null) {
            this.dataEventhandler.removeDataListener(this);
        }
        if (this.secureOut != null) {
            this.secureOut.close();
            this.secureOut = null;
        }
        if (this.secureInput != null) {
            this.secureInput.close();
            this.secureInput = null;
        }
        if (this.secConnection != null) {
            this.secConnection.close();
            this.secConnection = null;
        }
        if (this.consoleListener != null) {
            this.consoleListener.releaseResources();
            this.consoleListener = null;
        }
    }

    public void setPaused(boolean bl) {
        this.consoleListener.setPaused(bl);
    }
}

