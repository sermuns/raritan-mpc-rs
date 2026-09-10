/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.common.exception.CDataException;
import amp.powerboard.clientapi.common.exception.CDataFormatException;
import amp.powerboard.clientapi.common.exception.CParamMissingException;
import amp.powerboard.clientapi.net.CMsgOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public abstract class CCommand {
    protected Vector names;
    protected Vector values;
    private int opCode;
    public static final String LOCK_REQUIRED = "LOCK_REQUIRED";

    public CCommand(int n) {
        this.opCode = n;
        this.names = new Vector();
        this.values = new Vector();
    }

    public int getOpcode() {
        return this.opCode;
    }

    public void setProperty(String string, int n) throws CParamMissingException {
        if (string == null) {
            throw new CParamMissingException();
        }
        int n2 = 0;
        if (this.names.contains(string)) {
            n2 = this.names.indexOf(string);
            this.names.removeElementAt(n2);
            this.values.removeElementAt(n2);
        }
        this.names.addElement(string);
        this.values.addElement(new Integer(n));
    }

    public void setProperty(String string, String string2) throws CParamMissingException {
        if (string == null || string2 == null) {
            throw new CParamMissingException();
        }
        int n = 0;
        if (this.names.contains(string)) {
            n = this.names.indexOf(string);
            this.names.removeElementAt(n);
            this.values.removeElementAt(n);
        }
        this.names.addElement(string);
        this.values.addElement(string2);
    }

    public void setProperty(String string, long l) throws CParamMissingException {
        if (string == null) {
            throw new CParamMissingException();
        }
        int n = 0;
        if (this.names.contains(string)) {
            n = this.names.indexOf(string);
            this.names.removeElementAt(n);
            this.values.removeElementAt(n);
        }
        this.names.addElement(string);
        this.values.addElement(new Long(l));
    }

    public void setProperty(String string, short s) throws CDataException {
        if (string == null) {
            throw new CParamMissingException();
        }
        int n = 0;
        if (this.names.contains(string)) {
            n = this.names.indexOf(string);
            this.names.removeElementAt(n);
            this.values.removeElementAt(n);
        }
        this.names.addElement(string);
        this.values.addElement(new Short(s));
    }

    public void setProperty(String string, double d) throws CParamMissingException {
        if (string == null) {
            throw new CParamMissingException();
        }
        int n = 0;
        if (this.names.contains(string)) {
            n = this.names.indexOf(string);
            this.names.removeElementAt(n);
            this.values.removeElementAt(n);
        }
        this.names.addElement(string);
        this.values.addElement(new Double(d));
    }

    public void setProperty(String string, boolean bl) throws CParamMissingException {
        if (string == null) {
            throw new CParamMissingException();
        }
        int n = 0;
        if (this.names.contains(string)) {
            n = this.names.indexOf(string);
            this.names.removeElementAt(n);
            this.values.removeElementAt(n);
        }
        this.names.addElement(string);
        this.values.addElement(new Boolean(bl));
    }

    public void setProperty(String string, byte[] byArray) throws CParamMissingException {
        if (string == null) {
            throw new CParamMissingException();
        }
        int n = 0;
        if (this.names.contains(string)) {
            n = this.names.indexOf(string);
            this.names.removeElementAt(n);
            this.values.removeElementAt(n);
        }
        this.names.addElement(string);
        this.values.addElement(byArray);
    }

    public void setProperty(String string, int[] nArray) throws CParamMissingException {
        if (string == null) {
            throw new CParamMissingException();
        }
        int n = 0;
        if (this.names.contains(string)) {
            n = this.names.indexOf(string);
            this.names.removeElementAt(n);
            this.values.removeElementAt(n);
        }
        this.names.addElement(string);
        this.values.addElement(nArray);
    }

    public void setProperty(String string, String[] stringArray) throws CParamMissingException {
        if (string == null) {
            throw new CParamMissingException();
        }
        int n = 0;
        if (this.names.contains(string)) {
            n = this.names.indexOf(string);
            this.names.removeElementAt(n);
            this.values.removeElementAt(n);
        }
        this.names.addElement(string);
        this.values.addElement(stringArray);
    }

    public void fillStream(CMsgOutputStream cMsgOutputStream) throws CParamMissingException, CDataFormatException, IOException {
        Vector vector = this.getNameList();
        Vector vector2 = this.getDataTypes();
        int n = 0;
        if (vector.isEmpty() || vector2.isEmpty()) {
            cMsgOutputStream.close();
        } else {
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                String string = (String)enumeration.nextElement();
                int n2 = this.names.indexOf(string);
                if (n2 == -1) {
                    throw new CParamMissingException();
                }
                int n3 = (Integer)vector2.elementAt(n);
                switch (n3) {
                    case 1: {
                        try {
                            int n4 = (Integer)this.values.elementAt(n2);
                            cMsgOutputStream.writeInt(n4);
                            break;
                        }
                        catch (Exception exception) {
                            throw new CDataFormatException();
                        }
                    }
                    case 3: {
                        try {
                            Object e = this.values.elementAt(n2);
                            String string2 = (String)e;
                            cMsgOutputStream.writeShort((short)string2.length());
                            cMsgOutputStream.writeBytes(string2);
                            break;
                        }
                        catch (Exception exception) {
                            throw new CDataFormatException();
                        }
                    }
                    case 2: {
                        try {
                            long l = (Long)this.values.elementAt(n2);
                            cMsgOutputStream.writeLong(l);
                            break;
                        }
                        catch (Exception exception) {
                            throw new CDataFormatException();
                        }
                    }
                    case 4: {
                        try {
                            short s = (Short)this.values.elementAt(n2);
                            cMsgOutputStream.writeShort(s);
                            break;
                        }
                        catch (Exception exception) {
                            throw new CDataFormatException();
                        }
                    }
                    case 5: {
                        try {
                            double d = (Double)this.values.elementAt(n2);
                            cMsgOutputStream.writeDouble(d);
                            break;
                        }
                        catch (Exception exception) {
                            throw new CDataFormatException();
                        }
                    }
                    case 7: {
                        try {
                            byte[] byArray = (byte[])this.values.elementAt(n2);
                            short s = (short)byArray.length;
                            cMsgOutputStream.writeShort(s);
                            cMsgOutputStream.write(byArray, 0, byArray.length);
                            break;
                        }
                        catch (Exception exception) {
                            throw new CDataFormatException();
                        }
                    }
                    case 9: {
                        try {
                            int[] nArray = (int[])this.values.elementAt(n2);
                            int n5 = 0;
                            while (n5 < nArray.length) {
                                cMsgOutputStream.writeInt(nArray[n5]);
                                ++n5;
                            }
                            break;
                        }
                        catch (Exception exception) {
                            throw new CDataFormatException();
                        }
                    }
                    case 8: {
                        break;
                    }
                    default: {
                        throw new CDataFormatException();
                    }
                }
                ++n;
            }
            cMsgOutputStream.close();
        }
    }

    public abstract Vector getNameList();

    public abstract Vector getDataTypes();

    public boolean isLockRequired() {
        Vector vector = this.getNameList();
        int n = 0;
        while (n < vector.size()) {
            String string = (String)vector.elementAt(n);
            if (string.equals(LOCK_REQUIRED)) {
                int n2 = this.names.indexOf(string);
                Boolean bl = (Boolean)this.values.elementAt(n2);
                return bl;
            }
            ++n;
        }
        return false;
    }

    public boolean isFetchCommand() {
        return false;
    }
}

