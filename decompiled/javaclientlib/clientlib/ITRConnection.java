/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.tr.TRSRVR_SERVER_ID;

public interface ITRConnection {
    public void notify(int var1, int var2);

    public boolean login(TRSRVR_SERVER_ID var1, TRLIB_USERINFO var2, boolean var3);

    public boolean loginChallenge(int var1, byte[] var2, byte[] var3);
}

