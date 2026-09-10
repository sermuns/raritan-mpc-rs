/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.command.CCommand;
import amp.powerboard.clientapi.common.exception.CDataException;
import amp.powerboard.clientapi.common.exception.CDataFormatException;
import amp.powerboard.clientapi.common.exception.CParamMissingException;
import amp.powerboard.clientapi.net.CMsgOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class CSetNetwork
extends CCommand {
    private Vector nameList = new Vector();
    private Vector dataTypes;
    public static final String HOST_NAME = "HOST_NAME";
    public static final String IP_ADDRESS = "IP_ADDRESS";
    public static final String SUBNET = "SUBNET";
    public static final String GATEWAY = "GATEWAY";
    public static final String PORT_ADDRESS = "PORT_ADDRESS";
    public static final String USE_SSL = "USE_SSL";
    public static final String PRIME1 = "PRIME1";
    public static final String PRIME2 = "PRIME2";
    public static final String TERMINAL_TYPE = "TERMINAL_TYPE";
    public static final String DEVICE_NAME = "DEVICE_NAME";

    public CSetNetwork() {
        super(4);
        this.nameList.addElement(HOST_NAME);
        this.nameList.addElement(IP_ADDRESS);
        this.nameList.addElement(SUBNET);
        this.nameList.addElement(GATEWAY);
        this.nameList.addElement(PORT_ADDRESS);
        this.nameList.addElement(TERMINAL_TYPE);
        this.nameList.addElement(USE_SSL);
        this.nameList.addElement(PRIME1);
        this.nameList.addElement(PRIME2);
        this.nameList.addElement("LOCK_REQUIRED");
        this.dataTypes = new Vector();
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(7));
        this.dataTypes.addElement(new Integer(7));
        this.dataTypes.addElement(new Integer(8));
        try {
            this.setProperty("LOCK_REQUIRED", true);
        }
        catch (CDataException cDataException) {}
    }

    public CSetNetwork(String string) {
        super(4);
        this.nameList.addElement(HOST_NAME);
        this.nameList.addElement(IP_ADDRESS);
        this.nameList.addElement(SUBNET);
        this.nameList.addElement(GATEWAY);
        this.nameList.addElement(PORT_ADDRESS);
        this.nameList.addElement(TERMINAL_TYPE);
        if (!string.equals("HP")) {
            this.nameList.addElement(USE_SSL);
            this.nameList.addElement(PRIME1);
            this.nameList.addElement(PRIME2);
        }
        if (string.equals("DC") || string.equals("HP")) {
            this.nameList.addElement(DEVICE_NAME);
        }
        this.nameList.addElement("LOCK_REQUIRED");
        this.dataTypes = new Vector();
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(1));
        if (!string.equals("HP")) {
            this.dataTypes.addElement(new Integer(3));
            this.dataTypes.addElement(new Integer(7));
            this.dataTypes.addElement(new Integer(7));
        }
        if (string.equals("DC") || string.equals("HP")) {
            this.dataTypes.addElement(new Integer(3));
        }
        this.dataTypes.addElement(new Integer(8));
        try {
            this.setProperty("LOCK_REQUIRED", true);
        }
        catch (CDataException cDataException) {}
    }

    public Vector getNameList() {
        return this.nameList;
    }

    public Vector getDataTypes() {
        return this.dataTypes;
    }

    public void fillStream(CMsgOutputStream cMsgOutputStream) throws CParamMissingException, CDataFormatException, IOException {
        int n = 0;
        Enumeration enumeration = this.nameList.elements();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            int n2 = this.names.indexOf(string);
            if (n2 == -1) {
                throw new CParamMissingException();
            }
            if (string.equals(IP_ADDRESS)) {
                int n3 = this.stringToIp((String)this.values.elementAt(n2));
                cMsgOutputStream.writeInt(n3);
                continue;
            }
            if (string.equals(GATEWAY)) {
                int n4 = this.stringToIp((String)this.values.elementAt(n2));
                cMsgOutputStream.writeInt(n4);
                continue;
            }
            if (string.equals(SUBNET)) {
                int n5 = this.stringToIp((String)this.values.elementAt(n2));
                cMsgOutputStream.writeInt(n5);
                continue;
            }
            if (string.equals(TERMINAL_TYPE)) {
                String string2 = (String)this.values.elementAt(n2);
                if (string2.equals("0")) {
                    cMsgOutputStream.writeInt(0);
                    continue;
                }
                cMsgOutputStream.writeInt(1);
                continue;
            }
            if (string.equals(PORT_ADDRESS)) {
                int n6 = (Integer)this.values.elementAt(n2);
                cMsgOutputStream.writeInt(n6);
                continue;
            }
            if (string.equals(USE_SSL)) {
                n = (Integer)this.values.elementAt(n2);
                cMsgOutputStream.writeInt(n);
                continue;
            }
            if (string.equals(PRIME1) || string.equals(PRIME2)) {
                if (n != 1) continue;
                byte[] byArray = (byte[])this.values.elementAt(n2);
                int n7 = byArray.length;
                cMsgOutputStream.writeShort((short)n7);
                cMsgOutputStream.write(byArray, 0, n7);
                continue;
            }
            if (string.equals("LOCK_REQUIRED")) continue;
            String string3 = (String)this.values.elementAt(n2);
            cMsgOutputStream.writeShort((short)string3.length());
            cMsgOutputStream.writeBytes(string3);
        }
        cMsgOutputStream.close();
    }

    public int stringToIp(String string) throws NumberFormatException {
        int n = 0;
        int n2 = 0;
        if (string.length() != 0) {
            int n3 = 0;
            while (n3 < 4) {
                String string2;
                int n4 = string.indexOf(46, n);
                if (n4 == -1 && n3 < 3 || n4 != -1 && n3 == 3) {
                    throw new NumberFormatException("Invalid IP Argument");
                }
                if (n4 == -1) {
                    string2 = string.substring(n);
                } else {
                    string2 = string.substring(n, n4);
                    n = n4 + 1;
                }
                int n5 = Integer.parseInt(string2, 10);
                if (n5 < 0 || n5 > 255) {
                    throw new NumberFormatException("Invalid IP Argument");
                }
                n2 = n2 * 256 + n5;
                ++n3;
            }
            return n2;
        }
        throw new NumberFormatException("Invalid IP Argument");
    }
}

