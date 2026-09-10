/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.command.CCommand;
import java.util.Vector;

public class CLoginInfo
extends CCommand {
    private Vector nameList = new Vector();
    private Vector dataTypes;
    public static final String USER_NAME = "USER_NAME";

    public CLoginInfo() {
        super(13);
        this.nameList.addElement(USER_NAME);
        this.nameList.addElement("LOCK_REQUIRED");
        this.dataTypes = new Vector();
        this.dataTypes.addElement(new Integer(3));
        this.dataTypes.addElement(new Integer(8));
    }

    public Vector getNameList() {
        return this.nameList;
    }

    public Vector getDataTypes() {
        return this.dataTypes;
    }
}

