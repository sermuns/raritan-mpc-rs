/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

import javaclientlib.tr.TRLIB_UPDATEINFO;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;

public interface IKvmData {
    public void notify(int var1, int var2);

    public void updateNotify(TRLIB_UPDATEINFO var1);

    public void newVideoModeNotify(TRRSP_NEW_VIDEO_MODE_DATA var1);
}

