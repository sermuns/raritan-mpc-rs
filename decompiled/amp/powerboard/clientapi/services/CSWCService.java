/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.services;

import amp.powerboard.clientapi.command.ISendInterface;
import amp.powerboard.clientapi.common.exception.CAlreadyLoggedException;
import amp.powerboard.clientapi.common.exception.CBoxUnInitialisedException;
import amp.powerboard.clientapi.common.exception.CConfigNotSavedException;
import amp.powerboard.clientapi.common.exception.CConsoleAlreadyConnectedException;
import amp.powerboard.clientapi.common.exception.CInvalidUserException;
import amp.powerboard.clientapi.common.exception.CMaxUserExceededException;
import amp.powerboard.clientapi.common.exception.CNotLoggedException;
import amp.powerboard.clientapi.common.exception.CSecurityException;
import amp.powerboard.clientapi.common.exception.CUserAlreadyLoggedException;
import amp.powerboard.clientapi.console.CConsoleHandler;
import amp.powerboard.clientapi.event.IDataEventHandler;
import amp.powerboard.clientapi.event.IReportEventHandler;
import amp.powerboard.clientapi.event.IStatusEventHandler;
import amp.powerboard.clientapi.security.CKeyInstantiator;
import amp.powerboard.clientapi.security.PrimeThread;
import amp.powerboard.clientapi.services.CCommandService;
import amp.powerboard.clientapi.services.IBoxInterface;
import java.awt.Component;
import java.awt.Frame;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class CSWCService
implements IBoxInterface {
    private CConsoleHandler conHandler = null;
    int conCount = 0;
    private CCommandService cmdService = null;
    private Hashtable boxProperty = null;
    private Hashtable portNumberToNameMap = null;
    private String platform = "SC_AMP";
    private String deviceName = "<none>";
    private String login = null;
    private String password = null;
    private String iPAddress = null;
    private String htmlString = null;
    private boolean bBoxPropertySet = false;
    private boolean isAlreadyLoggedIn = false;
    private CKeyInstantiator keyInstantiator = null;
    private int iPortAddress;
    byte[] prime1 = new byte[32];
    byte[] prime2 = new byte[32];
    PrimeThread primeThread = null;
    boolean bPrimeThreadCalled = false;
    int SessionId = 0;
    private Frame parentFrame = null;
    private String subPlatform = "default";

    public CSWCService() {
        this.cmdService = new CCommandService(this);
        this.boxProperty = new Hashtable(10);
    }

    public void setParentFrame(Component component) {
        Component component2 = component;
        while (component2 != null) {
            if (component2 instanceof Frame) {
                this.parentFrame = (Frame)component2;
                return;
            }
            component2 = component2.getParent();
        }
        this.parentFrame = null;
    }

    public Frame getParentFrame() {
        return this.parentFrame;
    }

    public void disposeParentFrame() {
        this.parentFrame.dispose();
        this.parentFrame = null;
    }

    public void login(int n, String string, String string2) throws IOException, CUserAlreadyLoggedException, CAlreadyLoggedException, CInvalidUserException, CMaxUserExceededException, CBoxUnInitialisedException {
        if (!this.isAlreadyLoggedIn) {
            this.login = string;
            this.password = string2;
            if (this.boxProperty == null || this.boxProperty.isEmpty()) {
                this.htmlString = this.connectAndGetHTML();
                this.boxProperty = this.getBoxProperty();
                this.initTasks();
            }
            if (this.cmdService == null) {
                this.cmdService = new CCommandService(this);
            }
            boolean bl = this.SSLProduct();
            if (this.platform.equals("RX_SHIM")) {
                int n2 = this.getSessionId();
                int n3 = this.getDeviceId();
                int n4 = this.getChannelId();
                this.cmdService.login(this, this.iPortAddress, false, this.platform, n2, n3, n4, bl);
            } else {
                this.cmdService.login(n, string, string2, this.iPortAddress, this, false, this.keyInstantiator.getCommandDecryptKey(), this.keyInstantiator.getCommandEncryptKey(), this.getSessionId(), bl, this.platform, this.subPlatform, this.deviceName);
            }
        } else {
            throw new CAlreadyLoggedException();
        }
        this.isAlreadyLoggedIn = true;
    }

    public void reLogin(int n, String string, String string2) throws IOException, CAlreadyLoggedException, CUserAlreadyLoggedException, CInvalidUserException, CMaxUserExceededException, CBoxUnInitialisedException {
        if (!this.isAlreadyLoggedIn) {
            this.login = string;
            this.password = string2;
            if (this.boxProperty == null || this.boxProperty.isEmpty()) {
                this.htmlString = this.connectAndGetHTML();
                this.boxProperty = this.getBoxProperty();
                this.initTasks();
            }
            if (this.cmdService == null) {
                this.cmdService = new CCommandService(this);
            }
        } else {
            throw new CAlreadyLoggedException();
        }
        this.cmdService.login(n, string, string2, this.iPortAddress, this, true, this.keyInstantiator.getCommandDecryptKey(), this.keyInstantiator.getCommandEncryptKey(), this.getSessionId(), this.SSLProduct(), this.platform, this.subPlatform, this.deviceName);
        this.isAlreadyLoggedIn = true;
    }

    public void setIPAddress(String string) {
        this.iPAddress = string;
    }

    public String localIPAddress() {
        if (this.cmdService != null) {
            return this.cmdService.getLocalIPAddress();
        }
        return null;
    }

    public CConsoleHandler accessConsole(int n) throws CNotLoggedException, CConsoleAlreadyConnectedException, CUserAlreadyLoggedException, CMaxUserExceededException, InternalError, IOException, CInvalidUserException {
        if (!this.isAlreadyLoggedIn) {
            throw new CNotLoggedException();
        }
        if (this.conHandler == null) {
            if (this.platform.indexOf("_APP") != -1) {
                this.login = this.cmdService.fetchMbox_LoginName();
            }
            this.conHandler = this.platform.equals("RX_SHIM") ? new CConsoleHandler(this.cmdService, this.getIStatusEventHandler(), this.getIReportEventHandler(), this.getIDataEventHandler(), this.getUserName(), this.iPortAddress, this, this.getSessionId(), this.getDeviceId(), this.getChannelId(), this.platform, this.deviceName) : new CConsoleHandler(this.cmdService, this.getIStatusEventHandler(), this.getIReportEventHandler(), this.getIDataEventHandler(), this.getUserName(), n, this.login, this.password, this.iPortAddress, this, this.keyInstantiator.getConsoleDecryptKey(), this.keyInstantiator.getConsoleEncryptKey(), this.getSessionId(), this.platform, this.subPlatform, this.deviceName, this.cmdService.fetchMbox_mp_con());
            ++this.conCount;
            return this.conHandler;
        }
        ++this.conCount;
        return this.conHandler;
    }

    public void closeConsole() throws IOException, Exception {
        if (this.conHandler != null) {
            --this.conCount;
            if (this.conCount <= 0) {
                this.conHandler.closeConsole();
                this.conHandler = null;
            }
        }
    }

    public ISendInterface getISendInterface() {
        return this.cmdService;
    }

    public IDataEventHandler getIDataEventHandler() {
        return this.cmdService.getIDataEventHandler();
    }

    public IStatusEventHandler getIStatusEventHandler() {
        return this.cmdService.getIStatusEventHandler();
    }

    public IReportEventHandler getIReportEventHandler() {
        return this.cmdService.getIReportEventHandler();
    }

    public void writeBoxProperty(Hashtable hashtable) {
        this.boxProperty = hashtable;
        this.bBoxPropertySet = true;
        this.initTasks();
    }

    private void initTasks() {
        Object v = this.boxProperty.get("PortAddress");
        String string = (String)v;
        string.trim();
        try {
            this.iPortAddress = Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            System.err.println("CSWCService : PortAddress:..." + string + "..." + numberFormatException.toString());
        }
        this.setIPAddress((String)this.boxProperty.get("IPAddress"));
        v = this.boxProperty.get("DeviceName");
        if (v != null) {
            this.deviceName = (String)v;
        }
        if ((v = this.boxProperty.get("Platform")) != null) {
            this.platform = (String)v;
        }
        this.subPlatform = (v = this.boxProperty.get("SubPlatform")) != null ? (String)v : this.platform;
        String string2 = null;
        try {
            v = this.boxProperty.get("RC4Key");
            string2 = (String)v;
            this.keyInstantiator = new CKeyInstantiator(string2);
        }
        catch (Exception exception) {
            this.keyInstantiator = new CKeyInstantiator(string2);
        }
        String string3 = (String)this.boxProperty.get("SESSION_ID");
        this.SessionId = string3 == null ? 0 : Integer.parseInt(string3);
        if (this.isFirstTimeLogin()) {
            this.generatePrimeKeys();
        }
    }

    public void setHTMLString(String string) {
        this.htmlString = string;
        this.boxProperty = this.getBoxProperty();
        this.initTasks();
    }

    public Hashtable getBoxProperty() {
        if (this.boxProperty != null && !this.boxProperty.isEmpty()) {
            return this.boxProperty;
        }
        this.boxProperty = new Hashtable(10);
        String string = this.getProperty("Platform");
        if (string != null) {
            this.boxProperty.put("Platform", string);
            this.platform = string;
        }
        if ((string = this.getProperty("ChallengeId")) != null) {
            this.boxProperty.put("ChallengeId", string);
        }
        if ((string = this.getProperty("Challenge")) != null) {
            this.boxProperty.put("Challenge", string);
        }
        if ((string = this.getProperty("Product")) != null) {
            this.boxProperty.put("Product", string);
        }
        if ((string = this.getProperty("BoxState")) != null) {
            this.boxProperty.put("BoxState", string);
        }
        if ((string = this.getProperty("IPAddress")) != null) {
            this.boxProperty.put("IPAddress", string);
        }
        if (this.iPAddress != null) {
            this.boxProperty.put("IPAddress", this.iPAddress);
        }
        if ((string = this.getProperty("PortAddress")) != null) {
            this.boxProperty.put("PortAddress", string);
        }
        if ((string = this.getProperty("RC4Key")) != null) {
            this.boxProperty.put("RC4Key", string);
        }
        if ((string = this.getProperty("TerminalType")) != null) {
            this.boxProperty.put("TerminalType", string);
        }
        if ((string = this.getProperty("UseFrame")) != null) {
            this.boxProperty.put("UseFrame", string);
        }
        if ((string = this.getProperty("MenuBar")) != null) {
            this.boxProperty.put("MenuBar", string);
        }
        if ((string = this.getProperty("Title")) != null) {
            this.boxProperty.put("Title", string);
        }
        if ((string = this.getProperty("ScrollBar")) != null) {
            this.boxProperty.put("ScrollBar", string);
        }
        if ((string = this.getProperty("SubTitle")) != null) {
            this.boxProperty.put("SubTitle", string);
        }
        if ((string = this.getProperty("Version")) != null) {
            this.boxProperty.put("Version", string);
        }
        if ((string = this.getProperty("DeviceName")) != null) {
            this.boxProperty.put("DeviceName", string);
            this.deviceName = string;
        }
        if ((string = this.getProperty("USERNAME")) != null) {
            this.boxProperty.put("USERNAME", string);
        }
        if ((string = this.getProperty("SessionId")) != null) {
            this.boxProperty.put("SESSION_ID", string);
        }
        if ((string = this.getProperty("PortNumber")) != null) {
            this.boxProperty.put("PortNumber", string);
        }
        if ((string = this.getProperty("SubPlatform")) != null) {
            this.boxProperty.put("SubPlatform", string);
            this.subPlatform = string;
        }
        this.bBoxPropertySet = true;
        return this.boxProperty;
    }

    private String getProperty(String string) {
        String string2 = null;
        try {
            int n = this.htmlString.indexOf(string + " ");
            if (n > -1) {
                string2 = this.htmlString.substring(n);
                if ((string2 = string2.substring(0, string2.indexOf(">") + 1)).indexOf("\"") != -1) {
                    string2 = string2.substring(string2.indexOf("\"") + 1, string2.lastIndexOf("\""));
                } else {
                    int n2 = string2.indexOf("=") + 1;
                    int n3 = string2.indexOf(">");
                    string2 = string2.substring(n2, n3);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (string2 == null) {
            return null;
        }
        return string2.trim();
    }

    private String connectAndGetHTML() throws IOException {
        int n;
        String string = "http://" + this.iPAddress + ":" + "80" + "/";
        if (this.platform.equals("SC_AMP")) {
            string = string + "indexApp.htm";
        } else if (this.platform.equals("CEREBUS_X16") || this.platform.equals("CEREBUS_X16+") || this.platform.equals("CEREBUS_X32+")) {
            string = string + "app/app0/indexApp.htm";
        }
        URL uRL = new URL(string);
        DataInputStream dataInputStream = new DataInputStream(uRL.openStream());
        StringBuffer stringBuffer = new StringBuffer();
        do {
            int n2 = dataInputStream.available();
            byte[] byArray = new byte[n2];
            n = dataInputStream.read(byArray);
            String string2 = new String(byArray);
            stringBuffer.append(string2);
        } while (n != -1);
        return stringBuffer.toString();
    }

    public void logOut() throws IOException, CConfigNotSavedException {
        if (this.cmdService != null) {
            this.cmdService.releaseResources();
            if (this.conHandler != null) {
                this.conHandler.closeConsole();
                this.conHandler = null;
            }
            this.cmdService = null;
            this.isAlreadyLoggedIn = false;
            this.boxProperty = null;
        }
    }

    public int getUserRole() {
        return this.cmdService.getUserRole();
    }

    public String getUserName() {
        return this.cmdService.getUserName();
    }

    public String getLogin() {
        return this.login;
    }

    public void initialiseBox(Hashtable hashtable) throws UnknownHostException, CUserAlreadyLoggedException, CInvalidUserException, CMaxUserExceededException, IOException, CSecurityException, CNotLoggedException {
        this.cmdService.initialiseBox(hashtable, this, this.keyInstantiator.getCommandDecryptKey(), this.keyInstantiator.getCommandEncryptKey(), this.getSessionId(), this.SSLProduct(), this.platform, this.deviceName);
    }

    public void confirmReset() {
        this.cmdService.confirmReset();
    }

    public void reset() {
        this.cmdService.reset();
    }

    public void cancelReset() {
        this.cmdService.cancelReset();
    }

    public boolean SSLProduct() {
        if (!this.bBoxPropertySet) {
            System.err.println("Warning: Calling CSWCService.isSSLVersion() before boxProperty is set.");
            return false;
        }
        if (this.platform.equals("CONSOLE_MANAGER")) {
            String string = (String)this.boxProperty.get("RC4Key");
            if (string == null) {
                return false;
            }
            return string.length() >= 10;
        }
        if (this.platform.equals("RX_SHIM")) {
            return false;
        }
        String string = (String)this.boxProperty.get("Product");
        if ((string = string.trim()) == null) {
            return false;
        }
        return string.compareTo("SCSSL") == 0;
    }

    public boolean SSLEnabled() {
        if (!this.bBoxPropertySet) {
            System.err.println("Warning: Calling CSWCService.isSSLEnabled() before boxProperty is set.");
            return false;
        }
        Object v = this.boxProperty.get("RC4Key");
        return v != null;
    }

    public void generatePrimeKeys() {
        if (this.bPrimeThreadCalled) {
            return;
        }
        this.primeThread = new PrimeThread(this.prime1, this.prime2);
        this.primeThread.setPriority(1);
        this.primeThread.start();
        this.bPrimeThreadCalled = true;
    }

    public byte[] getPrime1() {
        block3: {
            if (!this.bPrimeThreadCalled) {
                this.generatePrimeKeys();
            }
            if (this.primeThread == null || !this.primeThread.isAlive()) break block3;
            try {
                this.primeThread.join();
                this.primeThread = null;
            }
            catch (InterruptedException interruptedException) {
                System.err.println("" + interruptedException);
            }
        }
        return this.prime1;
    }

    public byte[] getPrime2() {
        block3: {
            if (!this.bPrimeThreadCalled) {
                this.generatePrimeKeys();
            }
            if (this.primeThread == null || !this.primeThread.isAlive()) break block3;
            try {
                this.primeThread.join();
                this.primeThread = null;
            }
            catch (InterruptedException interruptedException) {
                System.err.println("" + interruptedException);
            }
        }
        return this.prime2;
    }

    public boolean isFirstTimeLogin() {
        Object v = this.boxProperty.get("BoxState");
        String string = (String)v;
        if (string == null || string.length() == 0) {
            return false;
        }
        string.trim();
        boolean bl = false;
        try {
            int n = Integer.parseInt(string);
            if (n == 1) {
                bl = true;
            }
        }
        catch (NumberFormatException numberFormatException) {
            System.err.println("BoxState:..." + string + "..." + numberFormatException.toString());
        }
        return bl;
    }

    public void incLockRequestCount() {
        this.cmdService.incLockRequestCount();
    }

    public void decLockRequestCount() {
        this.cmdService.decLockRequestCount();
    }

    public boolean isReadyForUpgrade() {
        return this.cmdService.isReadyForUpgrade();
    }

    public void forcedLogout() {
        try {
            if (this.conHandler != null) {
                this.conHandler.closeConsole();
                this.conHandler = null;
            }
            if (this.cmdService != null) {
                this.cmdService.forcedLogout();
                this.cmdService = null;
                this.isAlreadyLoggedIn = false;
                this.boxProperty = null;
            }
        }
        catch (Exception exception) {}
    }

    public int getSessionId() {
        return this.SessionId;
    }

    public String[] getUserList() {
        return this.cmdService.getUserList();
    }

    public Hashtable getPortNumberToNameMap() {
        return this.cmdService.getPortNumberToNameMap();
    }

    public String getPlatform() {
        return this.platform;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public byte[] fetchMbox_admin_cmd() {
        return this.cmdService.fetchMbox_admin_cmd();
    }

    public byte[] fetchMbox_admin_recmd() {
        return this.cmdService.fetchMbox_admin_recmd();
    }

    public byte[] fetchMbox_admin_con() {
        return this.cmdService.fetchMbox_admin_con();
    }

    public String getSubPlatform() {
        return this.subPlatform;
    }

    public int getDeviceId() {
        int n;
        block2: {
            n = -1;
            String string = (String)this.boxProperty.get("DeviceId");
            if (string == null) break block2;
            try {
                n = Integer.parseInt(string);
            }
            catch (NumberFormatException numberFormatException) {}
        }
        return n;
    }

    public int getChannelId() {
        int n;
        block2: {
            n = -1;
            String string = (String)this.boxProperty.get("ChannelId");
            if (string == null) break block2;
            try {
                n = Integer.parseInt(string);
            }
            catch (NumberFormatException numberFormatException) {}
        }
        return n;
    }
}

