/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.command.CCommand;
import amp.powerboard.clientapi.command.CCommandListenerThread;
import amp.powerboard.clientapi.command.ISendInterface;
import amp.powerboard.clientapi.common.exception.CConnectionException;
import amp.powerboard.clientapi.common.exception.CDataFormatException;
import amp.powerboard.clientapi.common.exception.CInvalidUserException;
import amp.powerboard.clientapi.common.exception.CMaxUserExceededException;
import amp.powerboard.clientapi.common.exception.CNotLoggedException;
import amp.powerboard.clientapi.common.exception.CParamMissingException;
import amp.powerboard.clientapi.common.exception.CSecurityException;
import amp.powerboard.clientapi.common.exception.CUserAlreadyLoggedException;
import amp.powerboard.clientapi.event.CEventDispatcher;
import amp.powerboard.clientapi.event.CEventGenerator;
import amp.powerboard.clientapi.event.IDataEventHandler;
import amp.powerboard.clientapi.event.IDataListener;
import amp.powerboard.clientapi.event.IReportEventHandler;
import amp.powerboard.clientapi.event.IStatusEventHandler;
import amp.powerboard.clientapi.event.IStatusListener;
import amp.powerboard.clientapi.net.CMsgOutputStream;
import amp.powerboard.clientapi.net.CSecureSocket;
import amp.powerboard.clientapi.security.CCryptoKey;
import java.io.IOException;
import java.net.InetAddress;

public class CCommandHandler
implements ISendInterface {
    private CEventDispatcher evtDispatcher;
    private CSecureSocket secSocket;
    public byte[] mbox_mp_recmd = null;
    public byte[] mbox_mp_con = null;
    public String mboxLoginName = null;
    private CCommandListenerThread cmdListenerThread;
    private String platform;
    private String subPlatform;

    public CCommandHandler() {
        this.evtDispatcher = new CEventDispatcher();
    }

    public CCommandHandler(String string) {
        this.platform = string;
        this.evtDispatcher = new CEventDispatcher();
    }

    public void sendCommand(CCommand cCommand) throws CParamMissingException, CConnectionException, CSecurityException, CNotLoggedException, CDataFormatException {
        if (cCommand == null) {
            throw new CParamMissingException();
        }
        try {
            cCommand.fillStream(new CMsgOutputStream(this.secSocket.getOutputStream(), cCommand.getOpcode()));
        }
        catch (IOException iOException) {
            throw new CConnectionException("Data cannot be send over the Network,Connection Failed");
        }
    }

    public void login(int n, byte by, InetAddress inetAddress, int n2, String string, int n3, byte[] byArray, String string2, CCryptoKey cCryptoKey, CCryptoKey cCryptoKey2, int n4, boolean bl, String string3, String string4, String string5) throws CUserAlreadyLoggedException, CInvalidUserException, CMaxUserExceededException, IOException {
        if (string3.indexOf("_APP") != -1 && by == 3) {
            string = this.mboxLoginName;
        }
        this.secSocket = new CSecureSocket(n, by, inetAddress, n2, string, n3, byArray, string2, cCryptoKey, cCryptoKey2, n4, string3, string4, string5, this.mbox_mp_recmd, this);
        this.cmdListenerThread = new CCommandListenerThread(this.secSocket.getInputStream(), new CEventGenerator(this.evtDispatcher, string3, string4), bl);
        this.startThread();
    }

    public void login(InetAddress inetAddress, int n, int n2, int n3, int n4, byte by, String string, boolean bl) throws CUserAlreadyLoggedException, CInvalidUserException, CMaxUserExceededException, IOException {
        this.secSocket = new CSecureSocket(inetAddress, n, n2, n3, n4, by, string);
        this.cmdListenerThread = new CCommandListenerThread(this.secSocket.getInputStream(), new CEventGenerator(this.evtDispatcher, string, this.subPlatform), bl);
        this.startThread();
    }

    public byte[] fetchMbox_admin_cmd() {
        return this.secSocket.mbox_admin_cmd;
    }

    public byte[] fetchMbox_admin_recmd() {
        return this.secSocket.mbox_admin_recmd;
    }

    public byte[] fetchMbox_admin_con() {
        return this.secSocket.mbox_admin_con;
    }

    public byte[] fetchMbox_mp_con() {
        return this.mbox_mp_con;
    }

    public String fetchMbox_login_name() {
        return this.mboxLoginName;
    }

    private void startThread() {
        if (this.cmdListenerThread == null) {
            return;
        }
        this.cmdListenerThread.start();
    }

    public IDataEventHandler getIDataEventHandler() {
        return this.evtDispatcher;
    }

    public IStatusEventHandler getIStatusEventHandler() {
        return this.evtDispatcher;
    }

    public IReportEventHandler getIReportEventHandler() {
        return this.evtDispatcher;
    }

    public void save(boolean bl) throws CConnectionException, CSecurityException {
    }

    public void releaseResources() {
        try {
            if (this.cmdListenerThread != null) {
                this.cmdListenerThread.releaseResources();
                if (this.cmdListenerThread.isAlive()) {
                    this.cmdListenerThread.stop();
                }
                this.cmdListenerThread = null;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.secSocket != null) {
                this.secSocket.close();
                this.secSocket = null;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (this.evtDispatcher != null) {
            this.evtDispatcher.releaseResources();
            this.evtDispatcher = null;
        }
    }

    public IDataListener[] getDataListeners() {
        return this.evtDispatcher.getDataListeners();
    }

    public IStatusListener[] getStatusListeners() {
        return this.evtDispatcher.getStatusListeners();
    }

    public void commit(boolean bl) throws CConnectionException, CSecurityException {
    }

    public InetAddress getLocalAddress() {
        InetAddress inetAddress = null;
        try {
            inetAddress = this.secSocket.getLocalAddress();
        }
        catch (Exception exception) {
            inetAddress = null;
        }
        return inetAddress;
    }
}

