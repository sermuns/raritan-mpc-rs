/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.command.CCommand;
import java.util.Vector;

public class CGetConfigLock
extends CCommand {
    private Vector nameList = new Vector();
    private Vector dataTypes;

    public CGetConfigLock() {
        super(15);
        this.nameList.addElement("LOCK_REQUIRED");
        this.dataTypes = new Vector();
        this.dataTypes.addElement(new Integer(8));
    }

    public Vector getNameList() {
        return this.nameList;
    }

    public Vector getDataTypes() {
        return this.dataTypes;
    }

    public boolean isFetchCommand() {
        return true;
    }
}

