/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.command.CCommand;
import java.util.Vector;

public class CEcho
extends CCommand {
    private Vector nameList = new Vector();
    private Vector dataTypes = new Vector();
    public static final String REQUEST = "REQUEST";

    public CEcho() {
        super(1505);
        this.nameList.addElement(REQUEST);
        this.nameList.addElement("LOCK_REQUIRED");
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

