/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.services;

import amp.powerboard.clientapi.command.CCommand;
import amp.powerboard.clientapi.command.CCommandHandler;
import amp.powerboard.clientapi.command.CEcho;
import amp.powerboard.clientapi.command.CGetConfigLock;
import amp.powerboard.clientapi.command.CInitialise;
import amp.powerboard.clientapi.command.CLoginInfo;
import amp.powerboard.clientapi.command.CReleaseConfigLock;
import amp.powerboard.clientapi.command.CReset;
import amp.powerboard.clientapi.command.CSetNetwork;
import amp.powerboard.clientapi.command.CSetUser;
import amp.powerboard.clientapi.command.ISendInterface;
import amp.powerboard.clientapi.common.CConst;
import amp.powerboard.clientapi.common.exception.CBoxUnInitialisedException;
import amp.powerboard.clientapi.common.exception.CConfigNotSavedException;
import amp.powerboard.clientapi.common.exception.CConnectionException;
import amp.powerboard.clientapi.common.exception.CDataException;
import amp.powerboard.clientapi.common.exception.CDataFormatException;
import amp.powerboard.clientapi.common.exception.CInvalidUserException;
import amp.powerboard.clientapi.common.exception.CMaxUserExceededException;
import amp.powerboard.clientapi.common.exception.CNotLoggedException;
import amp.powerboard.clientapi.common.exception.CParamMissingException;
import amp.powerboard.clientapi.common.exception.CRoleMismatchException;
import amp.powerboard.clientapi.common.exception.CSecurityException;
import amp.powerboard.clientapi.common.exception.CUserAlreadyLoggedException;
import amp.powerboard.clientapi.event.CClientListEvent;
import amp.powerboard.clientapi.event.CConfigLockEvent;
import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CFatalErrorEvent;
import amp.powerboard.clientapi.event.CGetCertStateEvent;
import amp.powerboard.clientapi.event.CGetCertificateEvent;
import amp.powerboard.clientapi.event.CGetDataComAndSystemEvent;
import amp.powerboard.clientapi.event.CGetHWMonitoringEvent;
import amp.powerboard.clientapi.event.CGetIPACLListEvent;
import amp.powerboard.clientapi.event.CGetModemStatusEvent;
import amp.powerboard.clientapi.event.CGetNetworkEvent;
import amp.powerboard.clientapi.event.CGetPingMonitoringEvent;
import amp.powerboard.clientapi.event.CGetRadClientEvent;
import amp.powerboard.clientapi.event.CGetSMTPServerEvent;
import amp.powerboard.clientapi.event.CGetSNMPAgentEvent;
import amp.powerboard.clientapi.event.CGetSubscriptionsEvent;
import amp.powerboard.clientapi.event.CGetTimeEvent;
import amp.powerboard.clientapi.event.CGetUserEvent;
import amp.powerboard.clientapi.event.CGetUserListEvent;
import amp.powerboard.clientapi.event.CInternalCertStateEvent;
import amp.powerboard.clientapi.event.CInternalCertificateEvent;
import amp.powerboard.clientapi.event.CInternalDataComAndSystemEvent;
import amp.powerboard.clientapi.event.CInternalEvent;
import amp.powerboard.clientapi.event.CInternalHWMonitoringEvent;
import amp.powerboard.clientapi.event.CInternalIPACLListEvent;
import amp.powerboard.clientapi.event.CInternalModemEvent;
import amp.powerboard.clientapi.event.CInternalNetworkEvent;
import amp.powerboard.clientapi.event.CInternalPingMonitoringEvent;
import amp.powerboard.clientapi.event.CInternalRadClientEvent;
import amp.powerboard.clientapi.event.CInternalResetEvent;
import amp.powerboard.clientapi.event.CInternalSMTPServerEvent;
import amp.powerboard.clientapi.event.CInternalSNMPAgentEvent;
import amp.powerboard.clientapi.event.CInternalStatusEvent;
import amp.powerboard.clientapi.event.CInternalSubscriptionEvent;
import amp.powerboard.clientapi.event.CInternalTimeEvent;
import amp.powerboard.clientapi.event.CInternalUserEvent;
import amp.powerboard.clientapi.event.CInternalUserInfoEvent;
import amp.powerboard.clientapi.event.CInternalUserListEvent;
import amp.powerboard.clientapi.event.CLoginInfoEvent;
import amp.powerboard.clientapi.event.CLogoutEvent;
import amp.powerboard.clientapi.event.CResetEvent;
import amp.powerboard.clientapi.event.CStatusEvent;
import amp.powerboard.clientapi.event.CUpgradeReportEvent;
import amp.powerboard.clientapi.event.CUserInfoEvent;
import amp.powerboard.clientapi.event.IDataEventHandler;
import amp.powerboard.clientapi.event.IDataListener;
import amp.powerboard.clientapi.event.IReportEventHandler;
import amp.powerboard.clientapi.event.IStatusEventHandler;
import amp.powerboard.clientapi.event.IStatusListener;
import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CMd5;
import amp.powerboard.clientapi.services.CAdminValidator;
import amp.powerboard.clientapi.services.CObserverValidator;
import amp.powerboard.clientapi.services.COperatorValidator;
import amp.powerboard.clientapi.services.CValidator;
import amp.powerboard.clientapi.services.IBoxInterface;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CCommandService
implements ISendInterface,
IDataListener,
IStatusListener {
    IBoxInterface bIf = null;
    public CCommandHandler cmdHandler = null;
    private Hashtable portNumberToNameMap = null;
    String pform = null;
    String subPlatform = null;
    private CValidator userRoleValidator = null;
    private int userRole = -1;
    private Vector configCommandVector = null;
    private InetAddress iPAddress = null;
    private InetAddress localIPAddress = null;
    private String userName = null;
    private String loginName = null;
    private String userPassword = null;
    private String challengeParameter = null;
    private byte[] hashedPassword = null;
    private byte[] challenge = null;
    private String[] clientList = null;
    private int boxState = 0;
    private int clientId = 0;
    private boolean configIsLocked = false;
    private boolean isLogged = false;
    private boolean lockIsAsked = false;
    private boolean upgradeCalled = false;
    private boolean configIsModified = false;
    private boolean connectionIsActive = true;
    private boolean resetCalled = false;
    private boolean commitCalled = false;
    private int releaseLockParam = 0;
    private int reqOperation = 0;
    private int lockCount = 0;
    private Hashtable boxProperty = null;
    private Hashtable initParameters = null;
    private CInternalEvent internEvent = null;
    private String lockerName = null;

    public CCommandService(IBoxInterface iBoxInterface) {
        this.bIf = iBoxInterface;
        this.cmdHandler = new CCommandHandler(this.pform);
        this.getIDataEventHandler().addDataListener(this);
        this.getIStatusEventHandler().addStatusListener(this);
        this.configCommandVector = new Vector(6, 6);
    }

    public void sendCommand(CCommand cCommand) throws CParamMissingException, CSecurityException, CConnectionException, CNotLoggedException, CDataFormatException {
        if (!this.isLogged) {
            throw new CNotLoggedException();
        }
        if (this.upgradeCalled) {
            throw new CSecurityException("Upgrading the Box,Try after Upgrading Finished");
        }
        if (this.cmdHandler == null) {
            throw new CSecurityException("Connection to Box Closed,Try Again by Reloading");
        }
        if (this.hasRights(cCommand) || this.pform.equals("RX_SHIM")) {
            if (!cCommand.isLockRequired()) {
                this.cmdHandler.sendCommand(cCommand);
            } else if (this.configIsLocked) {
                if (this.configCommandVector.indexOf(cCommand) != -1) {
                    this.configCommandVector.removeElement(cCommand);
                }
                this.cmdHandler.sendCommand(cCommand);
            } else if (this.lockIsAsked) {
                if (this.configCommandVector.indexOf(cCommand) == -1) {
                    this.configCommandVector.addElement(cCommand);
                }
            } else {
                if (this.configCommandVector.indexOf(cCommand) == -1) {
                    this.configCommandVector.addElement(cCommand);
                }
                this.askForConfigLock();
                this.lockIsAsked = true;
            }
        } else {
            throw new CRoleMismatchException();
        }
    }

    public void login(int n, String string, String string2, int n2, IBoxInterface iBoxInterface, boolean bl, CCryptoKey cCryptoKey, CCryptoKey cCryptoKey2, int n3, boolean bl2, String string3, String string4, String string5) throws CUserAlreadyLoggedException, CInvalidUserException, CMaxUserExceededException, IOException, CBoxUnInitialisedException {
        this.boxProperty = iBoxInterface.getBoxProperty();
        this.boxState = Integer.parseInt((String)this.boxProperty.get("BoxState"));
        this.setBoxDetails(string, string2);
        if (this.boxState == 1) {
            throw new CBoxUnInitialisedException();
        }
        if (bl) {
            this.cmdHandler.login(n, (byte)3, this.iPAddress, n2, this.loginName, this.clientId, this.challenge, string2, cCryptoKey, cCryptoKey2, n3, bl2, string3, string4, string5);
        } else {
            this.cmdHandler.login(n, (byte)1, this.iPAddress, n2, this.loginName, this.clientId, this.challenge, string2, cCryptoKey, cCryptoKey2, n3, bl2, string3, string4, string5);
        }
        if (string3.indexOf("_APP") != -1) {
            this.loginName = this.fetchMbox_LoginName();
        }
        this.isLogged = true;
        this.getLoginInfo();
        this.localIPAddress = this.cmdHandler.getLocalAddress();
    }

    public void login(IBoxInterface iBoxInterface, int n, boolean bl, String string, int n2, int n3, int n4, boolean bl2) throws CUserAlreadyLoggedException, CInvalidUserException, CMaxUserExceededException, IOException, CBoxUnInitialisedException {
        this.boxProperty = iBoxInterface.getBoxProperty();
        this.iPAddress = InetAddress.getLocalHost();
        this.pform = string;
        this.cmdHandler.login(this.iPAddress, n, n2, n3, n4, (byte)1, string, bl2);
        this.isLogged = true;
        this.localIPAddress = this.cmdHandler.getLocalAddress();
    }

    private void setBoxDetails(String string, String string2) throws UnknownHostException {
        this.loginName = string;
        this.userPassword = string2;
        String string3 = (String)this.boxProperty.get("ChallengeId");
        this.clientId = Integer.parseInt(string3);
        String string4 = (String)this.boxProperty.get("IPAddress");
        this.iPAddress = InetAddress.getByName(string4);
        this.challengeParameter = (String)this.boxProperty.get("Challenge");
        this.challenge = new byte[this.challengeParameter.length() / 2];
        int n = 0;
        while (n < this.challenge.length) {
            this.challenge[n] = (byte)Integer.parseInt(this.challengeParameter.substring(n * 2, n * 2 + 2), 16);
            ++n;
        }
        byte[] byArray = new byte[string2.length()];
        string2.getBytes(0, string2.length(), byArray, 0);
        CMd5 cMd5 = new CMd5();
        cMd5.update(byArray);
        this.hashedPassword = cMd5.digest();
    }

    public void commandDataArrived(CDataEvent cDataEvent) {
        int n = cDataEvent.getOpcode();
        switch (n) {
            case 4000: {
                this.internEvent = (CInternalEvent)cDataEvent;
                this.connectionIsActive = false;
                try {
                    CEcho cEcho = new CEcho();
                    cEcho.setProperty("REQUEST", 18);
                    cEcho.setProperty("LOCK_REQUIRED", false);
                    this.sendCommand(cEcho);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case 14: {
                CUpgradeReportEvent cUpgradeReportEvent = (CUpgradeReportEvent)cDataEvent;
                int n2 = cUpgradeReportEvent.getpercentage();
                if (n2 <= 100) break;
                this.upgradeCalled = false;
                break;
            }
            case 1503: {
                CClientListEvent cClientListEvent = (CClientListEvent)cDataEvent;
                this.clientList = cClientListEvent.getListOfClients();
                break;
            }
            case 13: {
                CLoginInfoEvent cLoginInfoEvent = (CLoginInfoEvent)cDataEvent;
                this.userName = cLoginInfoEvent.getUserName();
                this.portNumberToNameMap = cLoginInfoEvent.getPortNumberToNameMap();
                if (cLoginInfoEvent.getUserRight() == 1) {
                    this.userRoleValidator = new CAdminValidator();
                    this.userRole = 1;
                } else if (cLoginInfoEvent.getUserRight() == 0) {
                    this.userRoleValidator = new COperatorValidator();
                    this.userRole = 0;
                } else {
                    this.userRoleValidator = new CObserverValidator();
                    this.userRole = 2;
                }
                this.userRoleValidator.setUser(cLoginInfoEvent.getUserName());
                break;
            }
            case 15: {
                CConfigLockEvent cConfigLockEvent = (CConfigLockEvent)cDataEvent;
                this.lockIsAsked = false;
                if (cConfigLockEvent.getmessageStatus() == 14) {
                    this.configIsLocked = false;
                    this.lockCount = 0;
                    this.lockerName = cConfigLockEvent.getLockerName();
                    if (this.resetCalled) {
                        this.resetCalled = false;
                        CResetEvent cResetEvent = new CResetEvent("You are going to Reboot without saving the Configuration;The config was modified by  " + this.lockerName, this.lockerName, this.configIsLocked, this.clientList, 14);
                        this.fireDataEvent(cResetEvent);
                        break;
                    }
                    Enumeration enumeration = this.configCommandVector.elements();
                    while (enumeration.hasMoreElements()) {
                        CCommand cCommand = (CCommand)enumeration.nextElement();
                        try {
                            if (cCommand.isFetchCommand()) {
                                this.cmdHandler.sendCommand(cCommand);
                            } else {
                                CStatusEvent cStatusEvent = new CStatusEvent(cCommand.getOpcode(), 17, 0, this.configIsLocked, this.lockerName, 0);
                                this.fireStatusEvent(cStatusEvent);
                            }
                            this.configCommandVector.removeElement(cCommand);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                } else {
                    this.configIsLocked = true;
                    this.lockerName = this.userName;
                    if (this.resetCalled) {
                        this.resetCalled = false;
                        CResetEvent cResetEvent = new CResetEvent("The following users will be disconnected", this.lockerName, this.configIsLocked, this.clientList, 223);
                        this.fireDataEvent(cResetEvent);
                        break;
                    }
                    if (this.boxState == 1) {
                        try {
                            CSetUser cSetUser = new CSetUser(this.pform);
                            cSetUser.setProperty("USER_ID", 0);
                            cSetUser.setProperty("LOCK_REQUIRED", true);
                            cSetUser.setProperty("LOGIN", (String)this.initParameters.get(CConst.BOX_INIT_LOGIN));
                            String string = (String)this.initParameters.get(CConst.BOX_INIT_CAPABILITY);
                            int n3 = Integer.parseInt(string.trim());
                            cSetUser.setProperty("CAPABILITY", n3);
                            cSetUser.setProperty("USER_NAME", (String)this.initParameters.get(CConst.BOX_INIT_USER_NAME));
                            cSetUser.setProperty("USER_INFO", (String)this.initParameters.get(CConst.BOX_INIT_USER_INFO));
                            cSetUser.setProperty("PASSWORD", (String)this.initParameters.get(CConst.BOX_INIT_PASSWORD));
                            if (!this.pform.equals("DC") && !this.pform.equals("HP")) {
                                cSetUser.setProperty("NUMPORTS", (Integer)this.initParameters.get(CConst.BOX_INIT_NUMPORTS));
                                cSetUser.setProperty("PORTS", (int[])this.initParameters.get(CConst.BOX_INIT_PORTS));
                            }
                            cSetUser.setProperty("LOCK_REQUIRED", false);
                            this.sendCommand(cSetUser);
                        }
                        catch (Exception exception) {}
                        break;
                    }
                    Enumeration enumeration = this.configCommandVector.elements();
                    while (enumeration.hasMoreElements()) {
                        CCommand cCommand = (CCommand)enumeration.nextElement();
                        try {
                            this.sendCommand(cCommand);
                            this.configCommandVector.removeElement(cCommand);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
                break;
            }
            case 2: 
            case 17: {
                this.configIsModified = false;
                this.configIsLocked = false;
                this.lockCount = 0;
                this.lockIsAsked = false;
                break;
            }
            case 222: {
                CInternalResetEvent cInternalResetEvent = (CInternalResetEvent)cDataEvent;
                if (cInternalResetEvent.getResetStatus() == 0) {
                    this.resetCalled = true;
                }
            }
            case 22: 
            case 56: 
            case 99: 
            case 111: 
            case 122: 
            case 255: 
            case 277: 
            case 299: 
            case 366: 
            case 388: 
            case 400: 
            case 444: 
            case 455: 
            case 477: 
            case 499: 
            case 600: {
                this.createAndFireEvents(cDataEvent);
                if (!this.resetCalled) break;
                try {
                    this.releaseResources();
                }
                catch (Exception exception) {}
                break;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void commandStatusArrived(CStatusEvent cStatusEvent) {
        CInternalStatusEvent cInternalStatusEvent = (CInternalStatusEvent)cStatusEvent;
        int n = cInternalStatusEvent.getRealOpcode();
        CStatusEvent cStatusEvent2 = new CStatusEvent(n, cInternalStatusEvent.getStatus(), cInternalStatusEvent.getReqOperation(), this.configIsLocked, this.lockerName, 0);
        block22 : switch (n) {
            case 16: {
                int n2 = cStatusEvent.getStatus();
                if (n2 == 0) {
                    this.lockIsAsked = false;
                    switch (this.releaseLockParam) {
                        case 3: {
                            try {
                                this.releaseConfigLock(4);
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                            this.commitCalled = false;
                            break;
                        }
                        case 4: {
                            this.configIsModified = false;
                            this.configIsLocked = false;
                            this.lockCount = 0;
                            if (this.boxState == 1) {
                                try {
                                    this.releaseResources();
                                }
                                catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            } else {
                                this.lockerName = null;
                                CStatusEvent cStatusEvent3 = this.commitCalled ? new CStatusEvent(51, 0, this.reqOperation, this.configIsLocked, this.lockerName, this.releaseLockParam) : new CStatusEvent(5, 0, this.reqOperation, this.configIsLocked, this.lockerName, this.releaseLockParam);
                                cStatusEvent2 = cStatusEvent3;
                                this.releaseLockParam = 0;
                            }
                            this.commitCalled = false;
                            break;
                        }
                        case 1: {
                            this.configIsLocked = false;
                            this.configIsModified = false;
                            this.lockCount = 0;
                            break;
                        }
                        case 2: {
                            this.configIsModified = false;
                            this.configIsLocked = false;
                            this.lockCount = 0;
                            this.lockerName = null;
                            CStatusEvent cStatusEvent4 = this.commitCalled ? new CStatusEvent(51, 0, this.reqOperation, this.configIsLocked, this.lockerName, this.releaseLockParam) : new CStatusEvent(5, 0, this.reqOperation, this.configIsLocked, this.lockerName, this.releaseLockParam);
                            cStatusEvent2 = cStatusEvent4;
                            this.commitCalled = false;
                            this.releaseLockParam = 0;
                            break;
                        }
                    }
                } else if (n2 == 14) {
                    this.configIsLocked = false;
                    this.lockCount = 0;
                    switch (this.releaseLockParam) {
                        case 1: 
                        case 2: 
                        case 3: 
                        case 4: {
                            CStatusEvent cStatusEvent5 = this.commitCalled ? new CStatusEvent(51, 14, this.reqOperation, this.configIsLocked, this.lockerName, this.releaseLockParam) : new CStatusEvent(5, 14, this.reqOperation, this.configIsLocked, this.lockerName, this.releaseLockParam);
                            cStatusEvent2 = cStatusEvent5;
                            this.commitCalled = false;
                            break;
                        }
                    }
                    this.releaseLockParam = 0;
                } else if (n2 == 15 && this.releaseLockParam == 3) {
                    try {
                        CStatusEvent cStatusEvent6 = this.commitCalled ? new CStatusEvent(51, 0, this.reqOperation, this.configIsLocked, this.lockerName, this.releaseLockParam) : new CStatusEvent(5, 15, this.reqOperation, this.configIsLocked, this.lockerName, this.releaseLockParam);
                        cStatusEvent2 = cStatusEvent6;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                this.commitCalled = false;
                break;
            }
            case 8: {
                int n3 = cStatusEvent.getStatus();
                switch (n3) {
                    case 2: 
                    case 8: 
                    case 14: 
                    case 16: 
                    case 17: {
                        break;
                    }
                    case 0: {
                        this.configIsModified = true;
                        if (this.boxState != 1) break;
                        try {
                            CSetNetwork cSetNetwork = new CSetNetwork();
                            cSetNetwork.setProperty("HOST_NAME", (String)this.initParameters.get(CConst.BOX_INIT_HOST_NAME));
                            cSetNetwork.setProperty("IP_ADDRESS", (String)this.initParameters.get(CConst.BOX_INIT_IP_ADDRESS));
                            cSetNetwork.setProperty("LOCK_REQUIRED", true);
                            cSetNetwork.setProperty("SUBNET", (String)this.initParameters.get(CConst.BOX_INIT_SUBNET));
                            cSetNetwork.setProperty("GATEWAY", (String)this.initParameters.get(CConst.BOX_INIT_GATEWAY));
                            Integer n4 = (Integer)this.initParameters.get(CConst.BOX_INIT_PORT_ADDRESS);
                            cSetNetwork.setProperty("PORT_ADDRESS", n4);
                            cSetNetwork.setProperty("TERMINAL_TYPE", (String)this.initParameters.get(CConst.BOX_INIT_TERMINAL_TYPE));
                            if (!this.pform.equals("HP")) {
                                Integer n5 = (Integer)this.initParameters.get(CConst.BOX_INIT_USE_SSL);
                                int n6 = n5;
                                cSetNetwork.setProperty("USE_SSL", n5);
                                if (n6 == 1) {
                                    byte[] byArray = this.bIf.getPrime1();
                                    cSetNetwork.setProperty("PRIME1", byArray);
                                    byte[] byArray2 = this.bIf.getPrime2();
                                    cSetNetwork.setProperty("PRIME2", byArray2);
                                } else {
                                    byte[] byArray = new byte[32];
                                    cSetNetwork.setProperty("PRIME1", byArray);
                                    byte[] byArray3 = new byte[32];
                                    cSetNetwork.setProperty("PRIME2", byArray3);
                                }
                            }
                            if (this.pform.equals("DC") || this.pform.equals("HP")) {
                                cSetNetwork.setProperty("DEVICE_NAME", (String)this.initParameters.get(CConst.BOX_INIT_DEVICE_NAME));
                            }
                            this.sendCommand(cSetNetwork);
                        }
                        catch (Exception exception) {}
                        break;
                    }
                }
                break;
            }
            case 4: {
                int n7 = cStatusEvent.getStatus();
                try {
                    if (n7 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 26: {
                int n8 = cStatusEvent.getStatus();
                try {
                    if (n8 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 28: {
                int n9 = cStatusEvent.getStatus();
                try {
                    if (n9 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 23: {
                int n10 = cStatusEvent.getStatus();
                try {
                    if (n10 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 24: {
                int n11 = cStatusEvent.getStatus();
                try {
                    if (n11 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 4500: {
                int n12 = cStatusEvent.getStatus();
                try {
                    if (n12 == 0) break;
                }
                catch (Exception exception) {}
                break;
            }
            case 4502: {
                int n13 = cStatusEvent.getStatus();
                try {
                    if (n13 == 0) break;
                }
                catch (Exception exception) {}
                break;
            }
            case 21: {
                int n14 = cStatusEvent.getStatus();
                try {
                    if (n14 == 0) break;
                }
                catch (Exception exception) {}
                break;
            }
            case 11: {
                int n15 = cStatusEvent.getStatus();
                try {
                    if (n15 != 0) break;
                    this.configIsModified = true;
                }
                catch (Exception exception) {}
                break;
            }
            case 3: {
                int n16 = cStatusEvent.getStatus();
                switch (n16) {
                    case 0: {
                        this.upgradeCalled = true;
                        break block22;
                    }
                }
                this.upgradeCalled = false;
                break;
            }
            case 10: {
                int n17 = cStatusEvent.getStatus();
                switch (n17) {
                    case 0: {
                        this.configIsModified = true;
                        break block22;
                    }
                    case 8: {
                        break block22;
                    }
                }
                break;
            }
            case 30: {
                int n18 = cStatusEvent.getStatus();
                try {
                    if (n18 == 0) break;
                }
                catch (Exception exception) {}
                break;
            }
            case 31: {
                int n19 = cStatusEvent.getStatus();
                try {
                    if (n19 == 0) break;
                }
                catch (Exception exception) {}
                break;
            }
            case 32: {
                int n20 = cStatusEvent.getStatus();
                try {
                    if (n20 == 0) break;
                }
                catch (Exception exception) {}
                break;
            }
            case 33: {
                int n21 = cStatusEvent.getStatus();
                try {
                    if (n21 == 0) break;
                }
                catch (Exception exception) {}
                break;
            }
            case 39: {
                int n22 = cStatusEvent.getStatus();
                try {
                    if (n22 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 37: {
                int n23 = cStatusEvent.getStatus();
                try {
                    if (n23 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 34: {
                int n24 = cStatusEvent.getStatus();
                try {
                    if (n24 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 35: {
                int n25 = cStatusEvent.getStatus();
                try {
                    if (n25 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 43: 
            case 46: 
            case 48: 
            case 50: 
            case 61: 
            case 62: {
                int n26 = cStatusEvent.getStatus();
                try {
                    if (n26 != 0) break;
                    this.configIsModified = true;
                    if (this.boxState != 1) break;
                    this.releaseConfigLock(4);
                    this.releaseLockParam = 4;
                }
                catch (Exception exception) {}
                break;
            }
            case 1505: {
                if (cStatusEvent.getStatus() != 18) {
                    this.connectionIsActive = true;
                    break;
                }
                CFatalErrorEvent cFatalErrorEvent = new CFatalErrorEvent(this.internEvent.getData());
                this.fireDataEvent(cFatalErrorEvent);
                break;
            }
        }
        this.fireStatusEvent(cStatusEvent2);
    }

    public IDataEventHandler getIDataEventHandler() {
        return this.cmdHandler.getIDataEventHandler();
    }

    public IStatusEventHandler getIStatusEventHandler() {
        return this.cmdHandler.getIStatusEventHandler();
    }

    public IReportEventHandler getIReportEventHandler() {
        return this.cmdHandler.getIReportEventHandler();
    }

    private boolean hasRights(CCommand cCommand) {
        if (this.userRoleValidator == null) {
            return false;
        }
        return this.userRoleValidator.isOperationValid(cCommand);
    }

    private void askForConfigLock() {
        CGetConfigLock cGetConfigLock = new CGetConfigLock();
        try {
            cGetConfigLock.setProperty("LOCK_REQUIRED", false);
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.cmdHandler.sendCommand(cGetConfigLock);
        }
        catch (Exception exception) {
            this.fireDataEvent(new CFatalErrorEvent("Command cannot be send to the Box"));
        }
    }

    private void releaseConfigLock(int n) {
        CReleaseConfigLock cReleaseConfigLock = new CReleaseConfigLock();
        this.releaseLockParam = n;
        if (this.commitCalled) {
            switch (this.releaseLockParam) {
                case 3: 
                case 4: {
                    this.reqOperation = 23;
                    break;
                }
                case 1: 
                case 2: {
                    this.reqOperation = 24;
                }
            }
        } else {
            switch (this.releaseLockParam) {
                case 3: 
                case 4: {
                    this.reqOperation = 21;
                    break;
                }
                case 1: 
                case 2: {
                    this.reqOperation = 22;
                }
            }
        }
        try {
            cReleaseConfigLock.setProperty("SAVE_STATUS", this.releaseLockParam);
            cReleaseConfigLock.setProperty("LOCK_REQUIRED", false);
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.cmdHandler.sendCommand(cReleaseConfigLock);
        }
        catch (Exception exception) {
            this.fireDataEvent(new CFatalErrorEvent("Lock cannot be Released"));
        }
    }

    public void releaseResources() throws CConfigNotSavedException {
        if (this.configIsModified) {
            throw new CConfigNotSavedException();
        }
        this.fireDataEvent(new CLogoutEvent("You have been logged off."));
        if (this.configCommandVector != null) {
            this.configCommandVector.removeAllElements();
            this.configCommandVector = null;
        }
        if (this.cmdHandler != null) {
            this.cmdHandler.releaseResources();
        }
        this.cmdHandler = null;
        this.setToNull();
    }

    private void getLoginInfo() {
        CLoginInfo cLoginInfo = new CLoginInfo();
        try {
            cLoginInfo.setProperty("USER_NAME", this.loginName);
            cLoginInfo.setProperty("LOCK_REQUIRED", false);
        }
        catch (CDataException cDataException) {
            // empty catch block
        }
        try {
            this.cmdHandler.sendCommand(cLoginInfo);
        }
        catch (Exception exception) {
            this.fireDataEvent(new CFatalErrorEvent("Command cannot be send to the Box"));
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void save(boolean bl) throws CSecurityException {
        if (bl) {
            if (this.configIsModified) {
                this.releaseConfigLock(3);
                return;
            } else {
                if (!this.configIsLocked) throw new CSecurityException(20, "Config Lock is not Obtained.");
                this.releaseConfigLock(1);
            }
            return;
        } else if (this.configIsModified) {
            this.releaseConfigLock(2);
            return;
        } else {
            this.releaseConfigLock(1);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void commit(boolean bl) throws CSecurityException {
        this.commitCalled = true;
        if (bl) {
            if (this.configIsModified) {
                this.releaseConfigLock(4);
                return;
            } else {
                if (!this.configIsLocked) throw new CSecurityException(20, "Config Lock is not Obtained.");
                this.releaseConfigLock(1);
            }
            return;
        } else if (this.configIsModified) {
            this.releaseConfigLock(2);
            return;
        } else {
            this.releaseConfigLock(1);
        }
    }

    public int getUserRole() {
        return this.userRole;
    }

    public String getUserName() {
        return this.userName;
    }

    public void initialiseBox(Hashtable hashtable, IBoxInterface iBoxInterface, CCryptoKey cCryptoKey, CCryptoKey cCryptoKey2, int n, boolean bl, String string, String string2) throws UnknownHostException, CUserAlreadyLoggedException, CInvalidUserException, CMaxUserExceededException, IOException, CSecurityException, CNotLoggedException {
        this.pform = string;
        int n2 = 0;
        this.boxProperty = iBoxInterface.getBoxProperty();
        this.boxState = Integer.parseInt((String)this.boxProperty.get("BoxState"));
        this.initParameters = hashtable;
        this.setBoxDetails("prospero", "prospero");
        this.cmdHandler.login(n2, (byte)1, this.iPAddress, 23, this.loginName, this.clientId, this.challenge, this.userPassword, cCryptoKey, cCryptoKey2, n, bl, this.pform, this.subPlatform, string2);
        this.userRoleValidator = new CAdminValidator();
        this.userRole = 1;
        this.isLogged = true;
        CInitialise cInitialise = new CInitialise();
        try {
            cInitialise.setProperty("DATA_LENGTH", 8);
            cInitialise.setProperty("LOGIN_NAME", "prospero");
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.sendCommand(cInitialise);
        }
        catch (CConnectionException cConnectionException) {
        }
        catch (CDataException cDataException) {}
    }

    public void incLockRequestCount() {
        ++this.lockCount;
    }

    public void decLockRequestCount() {
        --this.lockCount;
        if (this.lockCount < 1 && this.configIsLocked && !this.configIsModified) {
            try {
                this.save(false);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (this.lockCount < 0) {
            this.lockCount = 0;
        }
    }

    private void createAndFireEvents(CDataEvent cDataEvent) {
        int n = cDataEvent.getOpcode();
        CDataEvent cDataEvent2 = null;
        switch (n) {
            case 111: {
                CInternalUserInfoEvent cInternalUserInfoEvent = (CInternalUserInfoEvent)cDataEvent;
                CUserInfoEvent cUserInfoEvent = new CUserInfoEvent(this.configIsLocked, this.lockerName);
                cUserInfoEvent.setUserInfoData(cInternalUserInfoEvent);
                cDataEvent2 = cUserInfoEvent;
                break;
            }
            case 22: {
                CInternalUserListEvent cInternalUserListEvent = (CInternalUserListEvent)cDataEvent;
                CGetUserListEvent cGetUserListEvent = new CGetUserListEvent(this.configIsLocked, this.lockerName);
                cGetUserListEvent.setUserListData(cInternalUserListEvent);
                cDataEvent2 = cGetUserListEvent;
                break;
            }
            case 99: {
                CInternalUserEvent cInternalUserEvent = (CInternalUserEvent)cDataEvent;
                CGetUserEvent cGetUserEvent = new CGetUserEvent(this.configIsLocked, this.lockerName);
                cGetUserEvent.setUserData(cInternalUserEvent);
                cDataEvent2 = cGetUserEvent;
                break;
            }
            case 122: {
                CInternalDataComAndSystemEvent cInternalDataComAndSystemEvent = (CInternalDataComAndSystemEvent)cDataEvent;
                CGetDataComAndSystemEvent cGetDataComAndSystemEvent = new CGetDataComAndSystemEvent(this.configIsLocked, this.lockerName);
                cGetDataComAndSystemEvent.setDataComAndSystem(cInternalDataComAndSystemEvent);
                cDataEvent2 = cGetDataComAndSystemEvent;
                break;
            }
            case 56: {
                CInternalNetworkEvent cInternalNetworkEvent = (CInternalNetworkEvent)cDataEvent;
                CGetNetworkEvent cGetNetworkEvent = new CGetNetworkEvent(this.configIsLocked, this.lockerName);
                cGetNetworkEvent.setNeworkData(cInternalNetworkEvent);
                cDataEvent2 = cGetNetworkEvent;
                break;
            }
            case 277: {
                CInternalHWMonitoringEvent cInternalHWMonitoringEvent = (CInternalHWMonitoringEvent)cDataEvent;
                CGetHWMonitoringEvent cGetHWMonitoringEvent = new CGetHWMonitoringEvent(this.configIsLocked, this.lockerName);
                cGetHWMonitoringEvent.setHWMonitoringData(cInternalHWMonitoringEvent);
                cDataEvent2 = cGetHWMonitoringEvent;
                break;
            }
            case 299: {
                CInternalPingMonitoringEvent cInternalPingMonitoringEvent = (CInternalPingMonitoringEvent)cDataEvent;
                CGetPingMonitoringEvent cGetPingMonitoringEvent = new CGetPingMonitoringEvent(this.configIsLocked, this.lockerName);
                cGetPingMonitoringEvent.setPingMonitoringData(cInternalPingMonitoringEvent);
                cDataEvent2 = cGetPingMonitoringEvent;
                break;
            }
            case 366: {
                CInternalSubscriptionEvent cInternalSubscriptionEvent = (CInternalSubscriptionEvent)cDataEvent;
                CGetSubscriptionsEvent cGetSubscriptionsEvent = new CGetSubscriptionsEvent(this.configIsLocked, this.lockerName);
                cGetSubscriptionsEvent.setSubscriptionData(cInternalSubscriptionEvent);
                cDataEvent2 = cGetSubscriptionsEvent;
                break;
            }
            case 388: {
                CInternalSMTPServerEvent cInternalSMTPServerEvent = (CInternalSMTPServerEvent)cDataEvent;
                CGetSMTPServerEvent cGetSMTPServerEvent = new CGetSMTPServerEvent(this.configIsLocked, this.lockerName);
                cGetSMTPServerEvent.setSMTPServerData(cInternalSMTPServerEvent);
                cDataEvent2 = cGetSMTPServerEvent;
                break;
            }
            case 400: {
                CInternalSNMPAgentEvent cInternalSNMPAgentEvent = (CInternalSNMPAgentEvent)cDataEvent;
                CGetSNMPAgentEvent cGetSNMPAgentEvent = new CGetSNMPAgentEvent(this.configIsLocked, this.lockerName);
                cGetSNMPAgentEvent.setSNMPAgentData(cInternalSNMPAgentEvent);
                cDataEvent2 = cGetSNMPAgentEvent;
                break;
            }
            case 255: {
                CInternalIPACLListEvent cInternalIPACLListEvent = (CInternalIPACLListEvent)cDataEvent;
                CGetIPACLListEvent cGetIPACLListEvent = new CGetIPACLListEvent(this.configIsLocked, this.lockerName);
                cGetIPACLListEvent.setIPACLListData(cInternalIPACLListEvent);
                cDataEvent2 = cGetIPACLListEvent;
                break;
            }
            case 222: {
                this.resetCalled = true;
                CInternalResetEvent cInternalResetEvent = (CInternalResetEvent)cDataEvent;
                CResetEvent cResetEvent = new CResetEvent(cInternalResetEvent.getResetReport(), this.lockerName, this.configIsLocked, this.clientList, cInternalResetEvent.getResetStatus());
                cDataEvent2 = cResetEvent;
                break;
            }
            case 444: {
                CInternalCertificateEvent cInternalCertificateEvent = (CInternalCertificateEvent)cDataEvent;
                CGetCertificateEvent cGetCertificateEvent = new CGetCertificateEvent(this.configIsLocked, this.lockerName);
                cGetCertificateEvent.setCertificateData(cInternalCertificateEvent);
                cDataEvent2 = cGetCertificateEvent;
                break;
            }
            case 600: {
                CInternalModemEvent cInternalModemEvent = (CInternalModemEvent)cDataEvent;
                CGetModemStatusEvent cGetModemStatusEvent = new CGetModemStatusEvent(this.configIsLocked, this.lockerName);
                cGetModemStatusEvent.setModemStatusData(cInternalModemEvent);
                cDataEvent2 = cGetModemStatusEvent;
                break;
            }
            case 499: {
                CInternalRadClientEvent cInternalRadClientEvent = (CInternalRadClientEvent)cDataEvent;
                CGetRadClientEvent cGetRadClientEvent = new CGetRadClientEvent(this.configIsLocked, this.lockerName);
                cGetRadClientEvent.setRadClientData(cInternalRadClientEvent);
                cDataEvent2 = cGetRadClientEvent;
                break;
            }
            case 477: {
                CInternalTimeEvent cInternalTimeEvent = (CInternalTimeEvent)cDataEvent;
                CGetTimeEvent cGetTimeEvent = new CGetTimeEvent(this.configIsLocked, this.lockerName);
                cGetTimeEvent.setTimeData(cInternalTimeEvent);
                cDataEvent2 = cGetTimeEvent;
                break;
            }
            case 455: {
                CInternalCertStateEvent cInternalCertStateEvent = (CInternalCertStateEvent)cDataEvent;
                CGetCertStateEvent cGetCertStateEvent = new CGetCertStateEvent(this.configIsLocked, this.lockerName);
                cGetCertStateEvent.setCertStateData(cInternalCertStateEvent);
                cDataEvent2 = cGetCertStateEvent;
                break;
            }
        }
        if (cDataEvent2 != null) {
            this.fireDataEvent(cDataEvent2);
        }
    }

    public boolean isReadyForUpgrade() {
        return !this.configIsModified;
    }

    public void forcedLogout() {
        this.fireDataEvent(new CLogoutEvent("You have been logged off."));
        this.configCommandVector.removeAllElements();
        this.configCommandVector = null;
        this.cmdHandler.releaseResources();
        this.cmdHandler = null;
        this.userRoleValidator = null;
        this.setToNull();
    }

    private void fireStatusEvent(CStatusEvent cStatusEvent) {
        IStatusListener[] iStatusListenerArray = this.cmdHandler.getStatusListeners();
        int n = 0;
        while (n < iStatusListenerArray.length) {
            IStatusListener iStatusListener = iStatusListenerArray[n];
            if (!(iStatusListener instanceof CCommandService)) {
                iStatusListener.commandStatusArrived(cStatusEvent);
            }
            ++n;
        }
    }

    private void fireDataEvent(CDataEvent cDataEvent) {
        IDataListener[] iDataListenerArray = this.cmdHandler.getDataListeners();
        int n = 0;
        while (n < iDataListenerArray.length) {
            IDataListener iDataListener = iDataListenerArray[n];
            if (!(iDataListener instanceof CCommandService)) {
                iDataListener.commandDataArrived(cDataEvent);
            }
            ++n;
        }
    }

    public void confirmReset() {
        if (this.configIsLocked) {
            CResetEvent cResetEvent = new CResetEvent("You are going to Reboot without saving the Configuration;The config was modified by  " + this.userName, this.lockerName, this.configIsLocked, this.clientList, 14);
            this.fireDataEvent(cResetEvent);
        } else if (this.lockIsAsked) {
            this.resetCalled = true;
        } else {
            this.askForConfigLock();
            this.resetCalled = true;
        }
    }

    public void reset() {
        try {
            CReset cReset = new CReset();
            cReset.setProperty("LOCK_REQUIRED", false);
            cReset.setProperty("USER_NAME", this.getUserName());
            this.sendCommand(cReset);
        }
        catch (Exception exception) {}
    }

    public void cancelReset() {
        block3: {
            if (!this.configIsLocked) break block3;
            try {
                if (!this.configIsModified && this.lockCount == 0) {
                    this.releaseConfigLock(1);
                }
            }
            catch (Exception exception) {}
        }
    }

    public String[] getUserList() {
        return this.clientList;
    }

    public Hashtable getPortNumberToNameMap() {
        return this.portNumberToNameMap;
    }

    private void setToNull() {
        this.bIf = null;
        this.cmdHandler = null;
        this.userRoleValidator = null;
        this.userRole = -1;
        this.configCommandVector = null;
        this.iPAddress = null;
        this.localIPAddress = null;
        this.userName = null;
        this.loginName = null;
        this.userPassword = null;
        this.challengeParameter = null;
        this.hashedPassword = null;
        this.challenge = null;
        this.clientList = null;
        this.boxState = 0;
        this.clientId = 0;
        this.configIsLocked = false;
        this.isLogged = false;
        this.lockIsAsked = false;
        this.upgradeCalled = false;
        this.configIsModified = false;
        this.connectionIsActive = false;
        this.resetCalled = false;
        this.commitCalled = false;
        this.releaseLockParam = 0;
        this.reqOperation = 0;
        this.lockCount = 0;
        this.boxProperty = null;
        this.initParameters = null;
        this.internEvent = null;
        this.lockerName = null;
    }

    public String getLocalIPAddress() {
        return this.localIPAddress.toString();
    }

    public byte[] fetchMbox_mp_con() {
        return this.cmdHandler.fetchMbox_mp_con();
    }

    public byte[] fetchMbox_admin_cmd() {
        return this.cmdHandler.fetchMbox_admin_cmd();
    }

    public byte[] fetchMbox_admin_recmd() {
        return this.cmdHandler.fetchMbox_admin_recmd();
    }

    public byte[] fetchMbox_admin_con() {
        return this.cmdHandler.fetchMbox_admin_con();
    }

    public String fetchMbox_LoginName() {
        return this.cmdHandler.fetchMbox_login_name();
    }
}

