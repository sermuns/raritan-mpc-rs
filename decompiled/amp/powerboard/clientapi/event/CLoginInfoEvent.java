/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import java.util.Hashtable;

public class CLoginInfoEvent
extends CDataEvent {
    private int userRights;
    private String userName;
    private Hashtable portNumberToNameMap;

    public CLoginInfoEvent(int n, String string, boolean bl) {
        super(13);
        this.userRights = n;
        this.userName = string;
    }

    public CLoginInfoEvent(int n, String string, Hashtable hashtable, boolean bl) {
        super(13);
        this.userRights = n;
        this.userName = string;
        this.portNumberToNameMap = hashtable;
    }

    public int getUserRight() {
        return this.userRights;
    }

    public String getUserName() {
        return this.userName;
    }

    public Hashtable getPortNumberToNameMap() {
        return this.portNumberToNameMap;
    }
}

