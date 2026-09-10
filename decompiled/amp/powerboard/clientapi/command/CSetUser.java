/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.command.CCommand;
import amp.powerboard.clientapi.common.exception.CDataException;
import amp.powerboard.clientapi.common.exception.CDataFormatException;
import amp.powerboard.clientapi.common.exception.CParamMissingException;
import amp.powerboard.clientapi.net.CMsgOutputStream;
import amp.powerboard.clientapi.security.CMd5;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class CSetUser
extends CCommand {
    private Vector nameList = new Vector();
    private Vector dataTypes;
    public static final String USER_ID = "USER_ID";
    public static final String LOGIN = "LOGIN";
    public static final String CAPABILITY = "CAPABILITY";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_INFO = "USER_INFO";
    public static final String PASSWORD = "PASSWORD";
    public static final String NUMPORTS = "NUMPORTS";
    public static final String PORTS = "PORTS";

    public CSetUser() {
        super(8);
        this.nameList.addElement(USER_ID);
        this.nameList.addElement(LOGIN);
        this.nameList.addElement(CAPABILITY);
        this.nameList.addElement(USER_NAME);
        this.nameList.addElement(USER_INFO);
        this.nameList.addElement(NUMPORTS);
        this.nameList.addElement(PORTS);
        this.nameList.addElement(PASSWORD);
        this.nameList.addElement("LOCK_REQUIRED");
        this.dataTypes = new Vector();
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(9));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(8));
        try {
            this.setProperty("LOCK_REQUIRED", true);
        }
        catch (CDataException cDataException) {}
    }

    public CSetUser(String string) {
        super(8);
        this.nameList.addElement(USER_ID);
        this.nameList.addElement(LOGIN);
        this.nameList.addElement(CAPABILITY);
        this.nameList.addElement(USER_NAME);
        this.nameList.addElement(USER_INFO);
        if (!string.equals("DC") && !string.equals("HP")) {
            this.nameList.addElement(NUMPORTS);
            this.nameList.addElement(PORTS);
        }
        this.nameList.addElement(PASSWORD);
        this.nameList.addElement("LOCK_REQUIRED");
        this.dataTypes = new Vector();
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(3));
        if (!string.equals("DC") && !string.equals("HP")) {
            this.dataTypes.addElement(new Integer(1));
            this.dataTypes.addElement(new Integer(9));
        }
        this.dataTypes.addElement(new Integer(3));
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
            Object object;
            String string = (String)enumeration.nextElement();
            int n2 = this.names.indexOf(string);
            if (n2 == -1) {
                throw new CParamMissingException();
            }
            if (string.equals(PASSWORD)) {
                object = (String)this.values.elementAt(n2);
                if (object == null || ((String)object).length() == 0) {
                    cMsgOutputStream.writeShort((short)0);
                } else {
                    byte[] byArray = this.getHashedPassword((String)object);
                    cMsgOutputStream.writeShort((short)16);
                    cMsgOutputStream.write(byArray);
                }
            } else if (string.equals(NUMPORTS)) {
                n = (Integer)this.values.elementAt(n2);
                cMsgOutputStream.writeInt(n);
            } else if (string.equals(PORTS)) {
                object = null;
                object = (int[])this.values.elementAt(n2);
                int n3 = 0;
                while (n3 < n) {
                    cMsgOutputStream.writeInt((int)object[n3]);
                    ++n3;
                }
            } else {
                object = this.values.elementAt(n2);
                if (object instanceof String) {
                    String string2 = (String)object;
                    cMsgOutputStream.writeShort((short)string2.length());
                    cMsgOutputStream.writeBytes(string2);
                } else if (object instanceof Integer) {
                    int n4 = (Integer)object;
                    cMsgOutputStream.writeInt(n4);
                } else if (object instanceof Short) {
                    short s = (Short)object;
                    cMsgOutputStream.writeShort(s);
                } else if (object instanceof Long) {
                    long l = (Long)object;
                    cMsgOutputStream.writeLong(l);
                } else if (!(object instanceof Boolean)) {
                    throw new CDataFormatException();
                }
            }
            string = null;
        }
        cMsgOutputStream.close();
    }

    private byte[] getHashedPassword(String string) {
        byte[] byArray = new byte[string.length()];
        string.getBytes(0, string.length(), byArray, 0);
        CMd5 cMd5 = new CMd5();
        cMd5.update(byArray);
        byte[] byArray2 = cMd5.digest();
        return byArray2;
    }

    public String getUserName() {
        int n = this.names.indexOf(USER_NAME);
        String string = (String)this.values.elementAt(n);
        return string;
    }
}

