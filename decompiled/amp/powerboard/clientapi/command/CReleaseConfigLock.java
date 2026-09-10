/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.command.CCommand;
import java.util.Vector;

public class CReleaseConfigLock
extends CCommand {
    private Vector nameList = new Vector();
    private Vector dataTypes;
    public static final String SAVE_STATUS = "SAVE_STATUS";

    public CReleaseConfigLock() {
        super(16);
        this.nameList.addElement(SAVE_STATUS);
        this.nameList.addElement("LOCK_REQUIRED");
        this.dataTypes = new Vector();
        this.dataTypes.addElement(new Integer(1));
        this.dataTypes.addElement(new Integer(8));
    }

    public Vector getNameList() {
        return this.nameList;
    }

    public Vector getDataTypes() {
        return this.dataTypes;
    }
}

