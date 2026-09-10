/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CInternalStatusEvent;
import amp.powerboard.clientapi.net.CMsgInputStream;
import java.io.IOException;

public class CStatusEventCreator {
    private int msgOpcode;
    private CInternalStatusEvent statusEvent;

    public CStatusEventCreator(int n) {
        this.msgOpcode = n;
    }

    public CInternalStatusEvent getStatusEvent() {
        return this.statusEvent;
    }

    public void createGenericStatusEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        int n = cMsgInputStream.readInt();
        cMsgInputStream.close();
        this.statusEvent = new CInternalStatusEvent(this.msgOpcode, n, 0);
    }

    public void createSetUserEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        int n = cMsgInputStream.readInt();
        if (n == 17) {
            short s = cMsgInputStream.readShort();
            String string = cMsgInputStream.readBytes(s);
        } else {
            Object var4_5 = null;
        }
        cMsgInputStream.close();
        this.statusEvent = new CInternalStatusEvent(8, n, 0);
    }
}

