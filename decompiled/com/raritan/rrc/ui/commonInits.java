/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.filterPrintStream;
import com.raritan.rrc.ui.panes.ErrorHandlerImpl;
import com.raritan.rrc.ui.panes.KvmMenuPopupKey;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import javaclientlib.tr.Constants;
import javaclientlib.utils.RRCLogger;
import nn.pp.core.JVMVersionInfo;

public final class commonInits {
    public static void initializePseudoConstants(RaritanPropertyResourceBundle raritanPropertyResourceBundle, filterPrintStream filterPrintStream2, RRCScreenContext rRCScreenContext) {
        filterPrintStream2.setChangeTo(raritanPropertyResourceBundle.getString("main.replacementPackageName"));
        System.setErr(filterPrintStream2);
        System.setOut(filterPrintStream2);
        Constants.setShortCoName(raritanPropertyResourceBundle.getString("company.name.short"));
        Constants.setNospaceCoName(raritanPropertyResourceBundle.getString("company.name.nospace"));
        Constants.setDefaultPassword(raritanPropertyResourceBundle.getString("Default.password"));
        String string = raritanPropertyResourceBundle.getString("Default.port.broadcast");
        int n = Integer.parseInt(string);
        com.raritan.tools.util.Constants.setPort(string, n);
        Constants.setPort(n);
        Constants.setAdminAppletJarFile(raritanPropertyResourceBundle.getString("admin.applet.jarfile"));
        Constants.setAdminAppletClass(raritanPropertyResourceBundle.getString("admin.applet.classname"));
        ErrorHandlerImpl.init(raritanPropertyResourceBundle, rRCScreenContext);
        ScreenContext.BUILD = raritanPropertyResourceBundle.getString("about.label.build");
    }

    public static void initializeCommonItems(RRCScreenContext rRCScreenContext) {
        new KvmMenuPopupKey(rRCScreenContext);
    }

    static {
        RRCLogger.log(300, JVMVersionInfo.getSystemProperties().toString());
    }
}

