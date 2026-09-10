/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.swing;

import amp.powerboard.clientapi.console.CConsoleHandler;
import amp.powerboard.clientapi.event.CClientListEvent;
import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CLoginInfoEvent;
import amp.powerboard.clientapi.event.IConsoleListener;
import amp.powerboard.clientapi.event.IDataListener;
import amp.powerboard.component.AcpSwcService;
import amp.powerboard.component.Constants;
import amp.powerboard.component.PowerboardInterface;
import amp.powerboard.component.RelayConfig;
import amp.powerboard.component.RelayStatus;
import amp.powerboard.component.SystemStatus;
import amp.powerboard.swing.DialogSetup;
import amp.powerboard.swing.Powerboard;
import amp.powerboard.utils.PbResource;
import java.awt.Component;
import java.awt.Frame;
import java.util.Enumeration;
import java.util.Hashtable;
import javaclientlib.clientlib.ISerialStream;
import javax.swing.JOptionPane;

public class StateManager
implements IDataListener,
IConsoleListener {
    public static final long TIMEOUT = 1800000L;
    public static final int UNIDENTIFIED_MODE = 0;
    public static final int LOGOUT_MODE = 2;
    public static final int ACTION_MODE = 3;
    public static final int PULSE_MODE = 4;
    public static final int REFRESH_MODE = 5;
    public static final int SETUP_MODE = 6;
    public static final int SAVE_MODE = 7;
    public static final int NO_COMMAND = 0;
    public static final int LOGOUT_COMMAND = 2;
    public static final int ACTION_COMMAND = 3;
    public static final int PULSE_COMMAND = 4;
    public static final int SETUP_COMMAND = 5;
    public static final int FIND_COMMAND = 6;
    static final byte SEVEN_BITS_MASK = 127;
    static final byte EIGHT_BITS_MASK = -1;
    byte controlMask = (byte)127;
    boolean master = false;
    boolean wasMaster = false;
    boolean requestForMaster = false;
    boolean warned = false;
    boolean confirmationDisabled = true;
    boolean enableDebug = false;
    boolean dataArrived = false;
    int userRole;
    int users = 0;
    int cycleTime;
    int relays;
    long timer = 0L;
    int numOutlets = 20;
    float alarmThreshold;
    int mode = 0;
    int lastCommand = 0;
    String userName = null;
    String masterName = null;
    String baudRate;
    String parityData;
    String unitMode;
    String unitId = "";
    String lastId = "NONE";
    String title = "PowerBoard";
    private String platform;
    String bytes = "";
    String password;
    String command;
    Hashtable changeList = new Hashtable(10);
    Hashtable nameChangeList = new Hashtable(20);
    private AcpSwcService swcService = null;
    private CConsoleHandler consoleHandler;
    private ISerialStream serialStream;
    private Powerboard powerboard = new Powerboard(this.title, this);
    PowerboardInterface applet;
    RelayStatus relayStatus;
    SystemStatus systemStatus;
    RelayConfig relayConfig;
    DialogSetup setup;

    public StateManager() {
        JOptionPane.setRootFrame(this.powerboard.getFrame());
    }

    public void writeSwcService(AcpSwcService acpSwcService) {
        this.swcService = acpSwcService;
        this.swcService.getIDataEventHandler().addDataListener(this);
    }

    public AcpSwcService readSwcService() {
        return this.swcService;
    }

    public void setSerialStream(ISerialStream iSerialStream) {
        this.serialStream = iSerialStream;
        this.master = true;
        this.mode = 5;
        this.lastCommand = 6;
    }

    public ISerialStream getSerialStream() {
        return this.serialStream;
    }

    public void setPassword(String string) {
        this.timer = System.currentTimeMillis() + 1800000L;
        this.password = string;
    }

    public void setPlatform(String string) {
        this.platform = string;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setTitle(String string) {
        this.title = string;
    }

    public Component getPowerboardUI() {
        return this.powerboard.getPowerboardUI();
    }

    public void setPowerboardInterface(PowerboardInterface powerboardInterface) {
        this.powerboard.setPowerboardAppletInterface(powerboardInterface);
        this.applet = powerboardInterface;
    }

    public void commandDataArrived(CDataEvent cDataEvent) {
        switch (cDataEvent.getOpcode()) {
            case 13: {
                CLoginInfoEvent cLoginInfoEvent = (CLoginInfoEvent)cDataEvent;
                if (this.userName == null) {
                    this.userName = cLoginInfoEvent.getUserName();
                    if (this.masterName != null) {
                        this.master = this.masterName.equals(this.userName);
                        this.wasMaster = this.masterName.equals(this.userName);
                    }
                }
                this.swcService.getIDataEventHandler().removeDataListener(this);
                this.swcService.getIDataEventHandler().addDataListener(this);
                break;
            }
            case 1503: {
                CClientListEvent cClientListEvent = (CClientListEvent)cDataEvent;
                int n = cClientListEvent.getMasterIndex();
                String[] stringArray = cClientListEvent.getListOfClients();
                this.users = stringArray.length;
                if (this.powerboard != null) {
                    this.powerboard.updateUsers(stringArray.length);
                }
                if (n != 0) {
                    this.masterName = stringArray[n - 1];
                    if (this.userName != null) {
                        if (this.masterName.equals(this.userName)) {
                            this.master = true;
                            if (this.userRole != 2 && !this.warned) {
                                this.warned = true;
                                this.refresh();
                            }
                        } else {
                            this.master = false;
                            JOptionPane.showMessageDialog(this.getFrame(), PbResource.getString("PbAccessErrorMessage"), PbResource.getString("PbConnectionErrorMessage.title"), 0);
                            System.out.println("\nAnother user is operating on the PowerBoard.");
                        }
                        this.wasMaster = stringArray[n - 1].equals(this.userName);
                    }
                } else if (n == 0) {
                    if (this.wasMaster) {
                        this.master = false;
                    }
                    this.wasMaster = false;
                }
                if (this.requestForMaster) {
                    this.requestForMaster = false;
                }
                this.swcService.getIDataEventHandler().removeDataListener(this);
                this.swcService.getIDataEventHandler().addDataListener(this);
            }
        }
    }

    public void connect(int n) {
        try {
            this.consoleHandler = this.swcService.accessConsole(n);
            this.consoleHandler.addConsoleListener(this);
            this.userRole = this.swcService.getUserRole();
            if (this.swcService.getPlatform().equals("RX_SHIM")) {
                this.master = true;
                this.refresh();
            }
        }
        catch (Exception exception) {
            System.out.println("Exception: " + exception.toString());
        }
    }

    public void bytesArrived(byte[] byArray, int n) {
        int n2 = 0;
        while (n2 < n) {
            byArray[n2] = (byte)(byArray[n2] & this.controlMask);
            ++n2;
        }
        this.bytes = this.bytes + new String(byArray, 0, n);
        String string = this.getCompleteMessage();
        if (string != null) {
            if (this.enableDebug) {
                System.out.println(string);
            }
            this.parseData(string);
        }
    }

    private String getCompleteMessage() {
        int n;
        int n2 = this.bytes.indexOf(Constants.PROMPT_MENU_8);
        int n3 = this.bytes.indexOf(Constants.PROMPT_MENU_12);
        int n4 = this.bytes.indexOf(Constants.PROMPT_MENU_18);
        int n5 = this.bytes.indexOf(Constants.PROMPT_MENU_20);
        int n6 = this.bytes.indexOf(Constants.PROMPT_ENTER);
        int n7 = this.bytes.indexOf(Constants.PROMPT_ENTER_1);
        int n8 = this.bytes.indexOf(Constants.PROMPT_SAVE);
        int n9 = this.bytes.indexOf(Constants.PROMPT_REQUEST);
        int n10 = this.bytes.indexOf(Constants.PROMPT_UNIT_ID);
        int n11 = this.bytes.indexOf(Constants.PROMPT_ENABLE);
        int n12 = this.bytes.indexOf(Constants.PROMPT_DISABLE);
        int n13 = this.bytes.indexOf(Constants.ID_TURN_OFF);
        int n14 = this.bytes.indexOf(Constants.ID_TURN_ON);
        int n15 = -1;
        int n16 = 0;
        if (n2 != -1) {
            n15 = n2;
            n16 = Constants.PROMPT_MENU_8.length();
        } else if (n3 != -1) {
            n15 = n3;
            n16 = Constants.PROMPT_MENU_12.length();
        } else if (n4 != -1) {
            n15 = n4;
            n16 = Constants.PROMPT_MENU_18.length();
        } else if (n5 != -1) {
            n15 = n5;
            n16 = Constants.PROMPT_MENU_20.length();
        } else if (n6 != -1) {
            n15 = n6;
            n16 = Constants.PROMPT_ENTER.length();
        } else if (n7 != -1) {
            n15 = n7;
            n16 = Constants.PROMPT_ENTER_1.length();
        } else if (n8 != -1) {
            n15 = n8;
            n16 = Constants.PROMPT_SAVE.length();
        } else if (n9 != -1) {
            n15 = n9;
            n16 = Constants.PROMPT_REQUEST.length();
        } else if (n10 != -1) {
            n15 = n10;
            n16 = Constants.PROMPT_UNIT_ID.length();
        } else if (n11 != -1) {
            n15 = n11;
            n16 = Constants.PROMPT_ENABLE.length();
        } else if (n12 != -1) {
            n15 = n12;
            n16 = Constants.PROMPT_DISABLE.length();
        } else if (n13 != -1) {
            n15 = n13;
            n = this.bytes.indexOf("?");
            n16 = n - n15 + 1;
        } else if (n14 != -1) {
            n15 = n14;
            n = this.bytes.indexOf("?");
            n16 = n - n15 + 1;
        }
        if (n15 != -1) {
            String string = this.bytes.substring(0, n15 + n16);
            this.bytes = n15 + n16 == this.bytes.length() ? "" : this.bytes.substring(n15 + n16);
            this.dataArrived = true;
            return string;
        }
        return null;
    }

    void parseData(String string) {
        if (this.enableDebug) {
            System.out.println("swing statemanager -- Mode: " + this.mode + "\tlastCommand: " + this.lastCommand);
        }
        if (string.indexOf(Constants.ID_MAIN_8) != -1 || string.indexOf(Constants.ID_MAIN_12) != -1 || string.indexOf(Constants.ID_MAIN_18) != -1 || string.indexOf(Constants.ID_MAIN_20) != -1) {
            if (this.enableDebug) {
                System.out.println("ID_MAIN");
            }
            block1 : switch (this.mode) {
                case 0: {
                    this.relayStatus = new RelayStatus(string);
                    this.numOutlets = this.relayStatus.getNumOutlets();
                    this.systemStatus = new SystemStatus(string);
                    this.unitId = this.systemStatus.getUnitId();
                    if (this.powerboard == null) break;
                    if (!this.powerboard.isInitialized()) {
                        this.powerboard.initUI(this.numOutlets);
                    }
                    this.powerboard.updateUI(this.relayStatus, this.systemStatus);
                    break;
                }
                case 5: {
                    if (this.lastCommand != 6) break;
                    if (this.confirmationDisabled) {
                        this.relayStatus = new RelayStatus(string);
                        this.systemStatus = new SystemStatus(string);
                        this.unitId = this.systemStatus.getUnitId();
                        this.numOutlets = this.relayStatus.getNumOutlets();
                        if (this.powerboard != null && !this.powerboard.isInitialized()) {
                            this.powerboard.initUI(this.numOutlets);
                        }
                        if (this.userRole != 2) {
                            this.powerboard.enableAll(true);
                        }
                        if (this.swcService != null && this.swcService.getPlatform().equals("RX_SHIM") || this.serialStream != null && this.getPlatform().equalsIgnoreCase(Constants.PLATFORM_JAVARRC)) {
                            this.applet.addStateManager();
                        } else if (!this.powerboard.isVisible()) {
                            this.powerboard.getContentPane().add(this.powerboard.getPowerboardUI());
                            this.powerboard.setVisible(true);
                        }
                        if (this.powerboard != null) {
                            this.powerboard.updateUI(this.relayStatus, this.systemStatus);
                        }
                        this.mode = 0;
                        break;
                    }
                    this.send("config\r");
                    break;
                }
                case 3: {
                    if (this.command != null) {
                        try {
                            this.send(this.command);
                            this.command = null;
                        }
                        catch (Exception exception) {
                            System.out.println("$ Exception: " + exception.toString());
                        }
                        break;
                    }
                    this.relayStatus = new RelayStatus(string);
                    this.systemStatus = new SystemStatus(string);
                    this.unitId = this.systemStatus.getUnitId();
                    this.powerboard.updateUI(this.relayStatus, this.systemStatus);
                    this.mode = 0;
                    break;
                }
                case 6: 
                case 7: {
                    if (this.enableDebug) {
                        System.out.println("SETUP/SAVE MODE: ");
                    }
                    switch (this.lastCommand) {
                        case 6: {
                            this.lastCommand = 5;
                            this.send("config\r");
                            break block1;
                        }
                        case 2: {
                            this.relayStatus = new RelayStatus(string);
                            this.systemStatus = new SystemStatus(string);
                            this.unitId = this.systemStatus.getUnitId();
                            this.powerboard.updateUI(this.relayStatus, this.systemStatus);
                            this.mode = 0;
                        }
                    }
                }
            }
        }
        if (string.indexOf(Constants.ID_SETUP) != -1) {
            if (this.enableDebug) {
                System.out.println("ID_SETUP");
            }
            switch (this.mode) {
                case 5: {
                    if (!this.confirmationDisabled) {
                        this.send("3\r");
                        break;
                    }
                    this.send("\r");
                    break;
                }
                case 6: {
                    if (this.lastCommand == 5) {
                        this.send("6\r");
                        break;
                    }
                    this.subMenuExit();
                    break;
                }
                case 7: {
                    if (this.changeList.size() > 0) {
                        if (this.changeList.containsKey("5")) {
                            this.send("5\r");
                            break;
                        }
                        if (!this.changeList.containsKey("6")) break;
                        this.send("6\r");
                        break;
                    }
                    if (this.nameChangeList.size() > 0) {
                        this.send("2\r");
                        break;
                    }
                    this.lastCommand = 2;
                    this.subMenuExit();
                    break;
                }
                case 2: {
                    this.mode = 0;
                    this.subMenuExit();
                }
            }
        }
        if (string.indexOf(Constants.PROMPT_UNIT_ID) != -1) {
            if (this.enableDebug) {
                System.out.println("PROMPT_UNIT_ID");
            }
            switch (this.mode) {
                case 7: {
                    if (!this.changeList.containsKey("5")) break;
                    this.send(this.changeList.get("5") + "\r");
                    this.changeList.remove("5");
                }
            }
        }
        if (string.indexOf(Constants.PROMPT_REQUEST) != -1 && string.indexOf(Constants.ID_CONTROL_FEATURES) != -1) {
            if (this.enableDebug) {
                System.out.println("ID_CONTROL_FEATURES");
            }
            switch (this.mode) {
                case 7: {
                    if (this.nameChangeList.size() > 0) {
                        Enumeration enumeration = this.nameChangeList.keys();
                        if (!enumeration.hasMoreElements()) break;
                        String string2 = (String)enumeration.nextElement();
                        this.command = (String)this.nameChangeList.get(string2);
                        this.send(string2 + "\r");
                        this.nameChangeList.remove(string2);
                        this.powerboard.setDefaultCursor();
                        break;
                    }
                    this.lastCommand = 2;
                    this.subMenuExit();
                }
            }
        }
        if (string.indexOf(Constants.ID_CURRENT_OUTLET) != -1) {
            switch (this.mode) {
                case 7: {
                    this.lastId = Constants.ID_CURRENT_OUTLET;
                }
            }
        }
        if (string.indexOf(Constants.ID_ALARM) != -1) {
            if (this.enableDebug) {
                System.out.println("ID_ALARM");
            }
            switch (this.mode) {
                case 6: {
                    this.alarmThreshold = new Float(string.substring(string.indexOf(Constants.ID_ALARM) + Constants.ID_ALARM.length(), string.indexOf(Constants.ST_AMPS)).trim()).floatValue();
                    if (this.setup != null) {
                        this.setup.dispose();
                        this.setup = null;
                    }
                    this.powerboard.setDefaultCursor();
                    this.setup = new DialogSetup(this, "Config", false);
                    this.setup.setVisible(true);
                    break;
                }
                case 7: {
                    this.lastId = Constants.ID_ALARM;
                }
            }
        }
        if (string.indexOf(Constants.PROMPT_ENTER_1) != -1) {
            if (this.enableDebug) {
                System.out.println("Prompt Enter 1");
            }
            switch (this.mode) {
                case 7: {
                    this.send(this.changeList.get("6") + "\r");
                    this.changeList.remove("6");
                    this.lastId = "NONE";
                }
            }
        }
        if (string.indexOf(Constants.PROMPT_ENTER) != -1) {
            if (this.enableDebug) {
                System.out.println("Prompt Enter");
            }
            switch (this.mode) {
                case 7: {
                    if (this.command == null) break;
                    this.send(this.command + "\r");
                    this.command = null;
                    this.lastId = "NONE";
                }
            }
        }
        if (string.indexOf(Constants.PROMPT_SAVE) != -1) {
            if (this.enableDebug) {
                System.out.println("Prompt Y/N");
            }
            switch (this.mode) {
                case 7: {
                    this.send("Y\r");
                }
            }
        }
        if (string.indexOf(Constants.PROMPT_ENABLE) != -1) {
            if (this.enableDebug) {
                System.out.println("PROMPT_ENABLE");
            }
            switch (this.mode) {
                case 5: {
                    this.confirmationDisabled = true;
                    this.send("N\r");
                }
            }
        }
        if (string.indexOf(Constants.PROMPT_DISABLE) != -1) {
            if (this.enableDebug) {
                System.out.println("PROMPT_DISABLE");
            }
            switch (this.mode) {
                case 5: {
                    this.confirmationDisabled = true;
                    this.send("Y\r");
                }
            }
        }
        if (string.indexOf(Constants.ID_TURN_OFF) != -1) {
            this.send("Y\r");
        }
        if (string.indexOf(Constants.ID_TURN_ON) != -1) {
            this.send("Y\r");
        }
    }

    void findState() {
        try {
            this.lastCommand = 6;
            this.dataArrived = false;
            this.send("\r");
        }
        catch (Exception exception) {
            System.out.println("Exception: " + exception.toString());
        }
    }

    void subMenuExit() {
        try {
            this.send("\r");
        }
        catch (Exception exception) {
            System.out.println("Exception: " + exception.toString());
        }
    }

    void mainLogout() {
        try {
            this.send("\r");
        }
        catch (Exception exception) {
            System.out.println("Exception: " + exception.toString());
        }
    }

    void login() {
    }

    public void sendOff(int n) {
        this.powerboard.setWaitCursor();
        this.command = "Off " + n + "\r";
        String[] stringArray = this.relayStatus.getRelayNames();
        StringBuffer stringBuffer = new StringBuffer(PbResource.getString("PowerConfirmOffMessage.text"));
        stringBuffer.append(stringArray[n - 1]);
        int n2 = JOptionPane.showOptionDialog(this.getFrame(), stringBuffer.toString(), PbResource.getString("PowerConfirmOffMessage.title"), 0, -1, null, null, null);
        this.setConfirmation(n2 == 0);
    }

    public void sendOn(int n) {
        block5: {
            try {
                this.powerboard.updateStatus("Ready to toggle relay ...");
                this.powerboard.setWaitCursor();
                this.mode = 3;
                this.command = null;
                if (this.master) {
                    this.send("On " + n + "\r");
                    break block5;
                }
                try {
                    this.requestForMaster = true;
                    this.consoleHandler.becomeMaster();
                }
                catch (Exception exception) {
                    System.out.println("$ Could not become master - exception: " + exception.toString());
                }
            }
            catch (Exception exception) {
                System.out.println("Exception: " + exception.toString());
            }
        }
    }

    void sendPulse(String string) {
        this.powerboard.updateStatus("Ready to pulse relay ...");
        this.mode = 4;
        this.command = string;
        if (this.master) {
            this.findState();
        } else {
            try {
                this.requestForMaster = true;
                this.consoleHandler.becomeMaster();
            }
            catch (Exception exception) {
                System.out.println("$ Could not become master - exception: " + exception.toString());
            }
        }
    }

    void refresh() {
        block6: {
            block5: {
                this.powerboard.updateStatus("Ready to get status ...");
                this.mode = 5;
                this.command = null;
                if (!this.master) break block5;
                this.findState();
                int n = 60;
                System.out.print("Connecting .");
                while (!this.dataArrived && n > 0) {
                    try {
                        System.out.print(".");
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException interruptedException) {
                        System.out.println("Timer interrupted");
                    }
                    --n;
                }
                if (this.dataArrived) break block6;
                System.out.println("\nConnection to power strip timed out.");
                JOptionPane.showMessageDialog(this.getFrame(), PbResource.getString("PbConnectionErrorMessage"), PbResource.getString("PbConnectionErrorMessage.title"), 0);
                break block6;
            }
            try {
                this.requestForMaster = true;
                this.consoleHandler.becomeMaster();
            }
            catch (Exception exception) {
                System.out.println("$ Could not become master - exception: " + exception.toString());
            }
        }
    }

    void send(String string) {
        block4: {
            try {
                if (this.getPlatform().equalsIgnoreCase(Constants.PLATFORM_JAVARRC)) {
                    if (this.serialStream == null) break block4;
                    byte[] byArray = string.getBytes();
                    this.serialStream.serialOut(byArray.length, byArray);
                    break block4;
                }
                if (this.consoleHandler != null) {
                    this.consoleHandler.send(string);
                }
            }
            catch (Exception exception) {
                if (this.mode == 0) break block4;
                this.powerboard.updateStatus("Ready");
                JOptionPane.showMessageDialog(this.getFrame(), PbResource.getString("UserInterruptMessage.text"), PbResource.getString("UserInterruptMessage.title"), 0);
                this.mode = 0;
            }
        }
    }

    public void readSetupData() {
        this.powerboard.setWaitCursor();
        this.powerboard.updateStatus("Ready to get setup data ...");
        this.mode = 6;
        if (this.master) {
            this.findState();
        } else {
            try {
                this.requestForMaster = true;
                this.consoleHandler.becomeMaster();
            }
            catch (Exception exception) {
                System.out.println("$ Could not become master - exception: " + exception.toString());
            }
        }
    }

    public void save(Hashtable hashtable, Hashtable hashtable2) {
        this.changeList = hashtable;
        this.nameChangeList = hashtable2;
        if (hashtable.size() > 0 || hashtable2.size() > 0) {
            this.powerboard.updateStatus("Ready to save setup data ...");
            this.powerboard.setWaitCursor();
            this.mode = 7;
            if (this.master) {
                this.findState();
            } else {
                try {
                    this.requestForMaster = true;
                    this.consoleHandler.becomeMaster();
                }
                catch (Exception exception) {
                    System.out.println("$ Could not become master - exception: " + exception.toString());
                }
            }
        } else if (this.enableDebug) {
            System.out.println("No changes to save.");
        }
    }

    public void closeAll() {
        if (this.setup != null) {
            this.setup.setVisible(false);
            this.setup.dispose();
            this.setup = null;
        }
        if (this.powerboard != null) {
            this.powerboard.dispose();
            this.powerboard = null;
        }
    }

    public void cancelLogin() {
        this.mode = 0;
    }

    public Frame getFrame() {
        return this.powerboard.getFrame();
    }

    public void setConfirmation(boolean bl) {
        block7: {
            if (bl) {
                try {
                    this.powerboard.updateStatus("Ready to toggle relay ...");
                    this.powerboard.setWaitCursor();
                    this.mode = 3;
                    if (this.master) {
                        this.send(this.command);
                        this.command = null;
                        break block7;
                    }
                    try {
                        this.requestForMaster = true;
                        this.consoleHandler.becomeMaster();
                    }
                    catch (Exception exception) {
                        System.out.println("$ Could not become master - exception: " + exception.toString());
                    }
                }
                catch (Exception exception) {
                    System.out.println("Exception: " + exception.toString());
                }
            } else {
                this.powerboard.setDefaultCursor();
                this.command = null;
            }
        }
    }

    public int getNumOutlets() {
        return this.numOutlets;
    }

    public float getAlarmThreshold() {
        return this.alarmThreshold;
    }

    public String getUnitId() {
        return this.unitId;
    }

    public RelayStatus getRelayStatus() {
        return this.relayStatus;
    }
}

