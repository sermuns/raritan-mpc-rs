/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.command.CCommand;
import amp.powerboard.clientapi.common.exception.CDataException;
import java.util.Vector;

public class CInitialise
extends CCommand {
    private Vector nameList = new Vector();
    private Vector dataTypes;
    public static final String DATA_LENGTH = "DATA_LENGTH";
    public static final String LOGIN_NAME = "LOGIN_NAME";

    public CInitialise() {
        super(15);
        this.nameList.addElement(DATA_LENGTH);
        this.nameList.addElement(LOGIN_NAME);
        this.nameList.addElement("LOCK_REQUIRED");
        this.dataTypes = new Vector();
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(8));
        try {
            this.setProperty("LOCK_REQUIRED", false);
        }
        catch (CDataException cDataException) {}
    }

    public Vector getNameList() {
        return this.nameList;
    }

    public Vector getDataTypes() {
        return this.dataTypes;
    }
}

