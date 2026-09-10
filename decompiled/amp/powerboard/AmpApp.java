/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  amp.powerboard.component.StateManager
 *  java.applet.Applet
 */
package amp.powerboard;

import amp.powerboard.UserAlreadyLoggedDlg;
import amp.powerboard.clientapi.common.exception.CAlreadyLoggedException;
import amp.powerboard.clientapi.common.exception.CBoxUnInitialisedException;
import amp.powerboard.clientapi.common.exception.CInvalidUserException;
import amp.powerboard.clientapi.common.exception.CMaxUserExceededException;
import amp.powerboard.clientapi.common.exception.CUserAlreadyLoggedException;
import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CLoginInfoEvent;
import amp.powerboard.clientapi.event.IDataListener;
import amp.powerboard.component.AcpSwcService;
import amp.powerboard.component.MessageBox;
import amp.powerboard.component.PowerboardInterface;
import amp.powerboard.component.StateManager;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

public class AmpApp
extends Applet
implements IDataListener,
PowerboardInterface {
    public Hashtable boxProperty = null;
    private String platform = null;
    private String subPlatform = null;
    int sPort = 1;
    String portName = "";
    String deviceName = "";
    boolean cleaned = false;
    String hostName;
    static final String CONSOLE_VERSION = "PowerBoard 3.1";
    static final String CONSOLE_DATE = "October 26, 2001";
    boolean debug = false;
    AcpSwcService acpSwcService1 = new AcpSwcService();
    StateManager stateManager1 = new StateManager();

    public void init() {
        this.setLayout(null);
        this.setBackground(Color.white);
        this.setSize(479, 65);
        this.showStatus("Attempting connection to power strip...");
        this.initialize();
        SymMouse aSymMouse = new SymMouse();
    }

    private void initialize() {
        this.boxProperty = new Hashtable(10);
        String sValue = this.getParameter("Platform");
        if (sValue != null) {
            this.boxProperty.put("Platform", sValue);
            this.platform = sValue;
        }
        if ((sValue = this.getParameter("SubPlatform")) != null) {
            this.boxProperty.put("SubPlatform", sValue);
            this.subPlatform = sValue;
        }
        if (this.platform.indexOf("_APP") != -1 || this.platform.equals("COMMAND_CENTER")) {
            sValue = this.getParameter("ChallengeId");
            if (sValue != null) {
                this.boxProperty.put("ChallengeId", sValue);
            }
            if ((sValue = this.getParameter("Challenge")) != null) {
                this.boxProperty.put("Challenge", sValue);
            }
            if ((sValue = this.getParameter("Product")) != null) {
                this.boxProperty.put("Product", sValue);
            }
            if ((sValue = this.getParameter("BoxState")) != null) {
                this.boxProperty.put("BoxState", sValue);
            }
            this.hostName = this.getCodeBase().getHost();
            if (this.hostName == null || this.hostName.equals("")) {
                sValue = this.getParameter("IPAddress");
                if (sValue != null) {
                    this.boxProperty.put("IPAddress", sValue);
                }
            } else {
                this.boxProperty.put("IPAddress", this.hostName);
            }
            if ((sValue = this.getParameter("PortAddress")) != null) {
                this.boxProperty.put("PortAddress", sValue);
            }
            if ((sValue = this.getParameter("RC4Key")) != null) {
                this.boxProperty.put("RC4Key", sValue);
            }
            if ((sValue = this.getParameter("TerminalType")) != null) {
                this.boxProperty.put("TerminalType", sValue);
            }
            if ((sValue = this.getParameter("UseFrame")) != null) {
                this.boxProperty.put("UseFrame", sValue);
            }
            if ((sValue = this.getParameter("MenuBar")) != null) {
                this.boxProperty.put("MenuBar", sValue);
            }
            if ((sValue = this.getParameter("Title")) != null) {
                this.boxProperty.put("Title", sValue);
            }
            if ((sValue = this.getParameter("ScrollBar")) != null) {
                this.boxProperty.put("ScrollBar", sValue);
            }
            if ((sValue = this.getParameter("SubTitle")) != null) {
                this.boxProperty.put("SubTitle", sValue);
            }
            if ((sValue = this.getParameter("Version")) != null) {
                this.boxProperty.put("Version", sValue);
            }
            if ((sValue = this.getParameter("DeviceName")) != null) {
                this.boxProperty.put("DeviceName", sValue);
                this.deviceName = sValue;
            }
            if ((sValue = this.getParameter("USERNAME")) != null) {
                this.boxProperty.put("USERNAME", sValue);
            }
            if ((sValue = this.getParameter("SessionId")) != null) {
                this.boxProperty.put("SESSION_ID", sValue);
            }
            if ((sValue = this.getParameter("PortNumber")) != null) {
                this.boxProperty.put("PortNumber", sValue);
                this.sPort = Integer.parseInt(sValue);
            }
            this.acpSwcService1.writeBoxProperty(this.boxProperty);
        } else if (this.platform.equals("RX_SHIM")) {
            int boxState;
            sValue = this.getParameter("BoxState");
            if (sValue != null && (boxState = Integer.parseInt(this.getParameter("BoxState"))) == 1) {
                System.out.println("PowerBoard Applet Installed.");
                return;
            }
            String hostName = this.getParameter("IPAddress");
            if (hostName == null || hostName.equals("")) {
                hostName = this.getCodeBase().getHost();
            }
            this.boxProperty.put("IPAddress", hostName);
            sValue = this.getParameter("PortAddress");
            if (sValue != null) {
                this.boxProperty.put("PortAddress", sValue);
            }
            if ((sValue = this.getParameter("SessionId")) != null) {
                this.boxProperty.put("SESSION_ID", sValue);
            }
            if ((sValue = this.getParameter("DeviceId")) != null) {
                this.boxProperty.put("DeviceId", sValue);
            }
            if ((sValue = this.getParameter("ChannelId")) != null) {
                this.boxProperty.put("ChannelId", sValue);
            }
            this.acpSwcService1.writeBoxProperty(this.boxProperty);
        }
        this.acpSwcService1.setParentFrame((Component)((Object)this));
        this.acpSwcService1.getIDataEventHandler().addDataListener(this);
        this.stateManager1.writeSwcService(this.acpSwcService1);
        if (this.platform.equals("CONSOLE_MANAGER")) {
            this.cm_login();
        } else {
            this.login();
        }
    }

    private void login() {
        String sLoginName = "arula111";
        String sPassword = "arula111";
        if (this.platform.equals("SC_AMP")) {
            int t = 0;
            try {
                t = Integer.parseInt(this.getParameter("Debug"));
            }
            catch (Exception e) {
                // empty catch block
            }
            if (t == 1) {
                sLoginName = this.getParameter("Login");
                sPassword = this.getParameter("Passwd");
            }
        } else if (this.platform.equals("COMMAND_CENTER")) {
            sLoginName = (String)this.boxProperty.get("USERNAME");
        }
        if (sLoginName != null && sPassword != null) {
            try {
                if (this.acpSwcService1 != null) {
                    if (this.debug) {
                        this.acpSwcService1.login(11, sLoginName, sPassword);
                    } else {
                        this.acpSwcService1.login(this.sPort, sLoginName, sPassword);
                    }
                } else {
                    System.out.println("Console: acpSwcService not set.");
                }
            }
            catch (CUserAlreadyLoggedException e) {
                if (this.platform.equals("COMMAND_CENTER")) {
                    new MessageBox(this.acpSwcService1, this.acpSwcService1.getParentFrame(), "Error", "The private account of " + this.deviceName + " is in use. Please check with your administrator ", true).showMessage();
                } else {
                    try {
                        this.acpSwcService1.reLogin(this.sPort, sLoginName, sPassword);
                    }
                    catch (Exception eee) {
                        System.out.println("[AmpApp_Relogin] : " + eee.getMessage());
                    }
                }
            }
            catch (CMaxUserExceededException e) {
                new MessageBox(this.acpSwcService1, this.acpSwcService1.getParentFrame(), "Console Port Access", "Maximum Users Exceeded", true).showMessage();
            }
            catch (CInvalidUserException e) {
                System.out.println("Invalid User Exception.");
            }
            catch (CAlreadyLoggedException e) {
                System.out.println("[AL] Already Logged Exception");
            }
            catch (CBoxUnInitialisedException e) {
                System.out.println("[BUI] Box UnInitialised Exception.");
            }
            catch (IOException e) {
                System.out.println("[IO] IO Exception");
            }
            if (this.platform.equals("RX_SHIM")) {
                this.stateManager1.setPowerboardInterface((PowerboardInterface)this);
                if (this.debug) {
                    this.stateManager1.connect(11);
                } else {
                    this.stateManager1.connect(this.sPort);
                }
            }
            if (this.platform.equals("SC_AMP")) {
                // empty if block
            }
        }
    }

    private void cm_login() {
        if (this.acpSwcService1 == null) {
            System.out.println("Console: acpSwcService not set.");
        }
        int i = 1;
        while (i <= 3) {
            block10: {
                String sLoginName;
                String sPassword = sLoginName = "arula" + Integer.toString(i);
                try {
                    this.acpSwcService1.login(this.sPort, sLoginName, sPassword);
                    break;
                }
                catch (CUserAlreadyLoggedException e) {
                    if (i == 3) {
                        UserAlreadyLoggedDlg ualiDlg = new UserAlreadyLoggedDlg(this.acpSwcService1.getParentFrame(), "ASC Warning" + this.portName, true, this.acpSwcService1, this.sPort, sLoginName, sPassword);
                        ualiDlg.setVisible(true);
                    }
                }
                catch (CMaxUserExceededException e) {
                    new MessageBox(this.acpSwcService1, this.acpSwcService1.getParentFrame(), "Console Port Access", "Maximum Users Exceeded", true).showMessage();
                }
                catch (CInvalidUserException e) {
                    System.out.println("CInvalidUserException.");
                    break;
                }
                catch (CAlreadyLoggedException e) {
                    System.out.println("CAlreadyLoggedException.");
                    break;
                }
                catch (CBoxUnInitialisedException e) {
                    System.out.println("CBoxUnInitialisedException.");
                    break;
                }
                catch (IOException e) {
                    if (i != 3) break block10;
                    UserAlreadyLoggedDlg ualiDlg = new UserAlreadyLoggedDlg(this.acpSwcService1.getParentFrame(), "ASC Warning" + this.portName, true, this.acpSwcService1, this.sPort, sLoginName, sPassword);
                    ualiDlg.setVisible(true);
                }
            }
            ++i;
        }
    }

    public void stop() {
        this.finalCleanUp();
    }

    public void commandDataArrived(CDataEvent dataEvent) {
        switch (dataEvent.getOpcode()) {
            case 13: {
                CLoginInfoEvent loginInfoEvent = (CLoginInfoEvent)dataEvent;
                if (this.platform.equals("SC_AMP") || this.platform.equals("CONSOLE_MANAGER") && this.subPlatform.equals("DC")) {
                    this.sPort = 1;
                }
                if (this.acpSwcService1.getPortNumberToNameMap() != null && this.acpSwcService1.getPortNumberToNameMap().get(new Integer(this.sPort)) != null) {
                    this.portName = " - " + this.acpSwcService1.getPortNumberToNameMap().get(new Integer(this.sPort)).toString();
                    this.stateManager1.setTitle("PowerBoard" + this.portName);
                }
                this.stateManager1.setPowerboardInterface((PowerboardInterface)this);
                if (this.debug) {
                    this.stateManager1.connect(11);
                    break;
                }
                this.stateManager1.connect(this.sPort);
                break;
            }
            case 17: {
                new MessageBox(this.acpSwcService1, this.acpSwcService1.getParentFrame(), "Forced Logout", "You have been logged out.", false).showMessage();
                if (this.acpSwcService1.isFirstTimeLogin()) break;
                this.finalCleanUp();
                break;
            }
            case 2: {
                if (this.acpSwcService1.isFirstTimeLogin()) break;
                this.finalCleanUp();
                break;
            }
            case 177: {
                if (this.acpSwcService1.isFirstTimeLogin()) break;
                this.finalCleanUp();
            }
        }
    }

    public void chatCallback() {
    }

    public void scriptCallback() {
    }

    public void userListCallback() {
    }

    public void helpCallback() {
        int pos;
        String helpLink = this.getCodeBase().toString() + "help/console.htm";
        if (this.platform.equals("SC_AMP")) {
            helpLink = this.getCodeBase().toString() + "app/help/console.htm";
        } else if (this.platform.equals("COMMAND_CENTER") && (pos = this.getCodeBase().toString().indexOf("cgi-bin")) != -1) {
            helpLink = this.getCodeBase().toString().substring(0, pos) + "CommandCenter/help/console.htm";
        }
        try {
            this.getAppletContext().showDocument(new URL(helpLink), "ConsoleHelp");
        }
        catch (MalformedURLException ex) {
            System.out.println("Cannot find Help page.");
        }
    }

    public Image getImage(String name_p) {
        MediaTracker tracker = new MediaTracker((Component)((Object)this));
        Image image = this.getImage(this.getCodeBase(), "images/" + name_p);
        tracker.addImage(image, 0);
        try {
            tracker.waitForAll();
        }
        catch (InterruptedException e) {
            System.out.println("Error loading images");
        }
        return image;
    }

    public void finalCleanUp() {
        if (!this.cleaned) {
            this.cleaned = true;
            if (this.stateManager1 != null) {
                this.stateManager1.closeAll();
                this.stateManager1 = null;
            }
            if (this.acpSwcService1 != null) {
                this.acpSwcService1.removeAllInstances();
                this.acpSwcService1.forcedLogout();
                this.acpSwcService1 = null;
            }
        }
    }

    public void consoleCallback() {
    }

    public void powerHelpCallback() {
        int pos;
        String helpLink = this.getCodeBase().toString() + "help/pbhelp.htm";
        if (this.platform.equals("SC_AMP")) {
            helpLink = this.getCodeBase().toString() + "app/help/pbhelp.htm";
        } else if (this.platform.equals("COMMAND_CENTER") && (pos = this.getCodeBase().toString().indexOf("cgi-bin")) != -1) {
            helpLink = this.getCodeBase().toString().substring(0, pos) + "CommandCenter/help/pbhelp.htm";
        }
        try {
            this.getAppletContext().showDocument(new URL(helpLink), "PowerboardHelp");
        }
        catch (MalformedURLException ex) {
            System.out.println("Cannot find Help page.");
        }
    }

    public URL getCodebaseURL() {
        return this.getCodeBase();
    }

    public void addStateManager() {
        this.add((Component)this.stateManager1);
    }

    class SymMouse
    extends MouseAdapter {
        SymMouse() {
        }

        public void mouseClicked(MouseEvent event) {
            Object object = event.getSource();
        }
    }
}

