/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.net.CMsgInputStream;

public interface IGenerateEvent {
    public void generateEventObject(CMsgInputStream var1, boolean var2);

    public void generateKillEventObject();
}

