/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.ui.helpers;

import java.awt.Cursor;
import nn.pp.core.T;

public interface ICopyrightHandler {
    public static final String BASE_COPYRIGHT = T._("Copyright (C) 2004-2013 Raritan Computer, Inc.  All rights reserved.");
    public static final String NO_PACKAGES = T._("No open source packages listed.");
    public static final double LICENSE_DIALOG_PROPORTIONAL_SIZE = 0.8;
    public static final int COPYRIGHT_ROWS = 6;
    public static final Cursor waitCursor = new Cursor(3);

    public void setCursor(Cursor var1);

    public void fillCopyrightInfo(String var1, String var2, String var3);
}

