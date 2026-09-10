/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.applet;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;

public class EmbedAppletClassLoader
extends URLClassLoader {
    public EmbedAppletClassLoader(URL[] uRLArray, ClassLoader classLoader) {
        super(uRLArray, classLoader);
    }

    @Override
    protected PermissionCollection getPermissions(CodeSource codeSource) {
        Permission permission;
        PermissionCollection permissionCollection = super.getPermissions(codeSource);
        URL uRL = codeSource.getLocation();
        try {
            URLConnection uRLConnection = uRL.openConnection();
            permission = uRLConnection.getPermission();
        }
        catch (Exception exception) {
            permission = null;
            Object var5_5 = null;
        }
        permission = new AllPermission();
        permissionCollection.add(permission);
        return permissionCollection;
    }
}

