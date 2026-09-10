/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  netscape.security.PrivilegeManager
 */
package com.raritan.rrc.components.serialconsole;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import netscape.security.PrivilegeManager;

public class ConfigManager {
    private File file = null;
    private FileOutputStream fos = null;
    private FileInputStream fis = null;
    private boolean accessGranted = false;
    private boolean writeAccessGranted = false;

    public ConfigManager() {
        String string = null;
        try {
            this.accessGranted = true;
        }
        catch (Exception exception) {
            this.accessGranted = exception.toString().indexOf("ClassNotFoundException") != -1;
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
        if (this.accessGranted) {
            try {
                string = System.getProperty("user.home");
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (string != null) {
                try {
                    this.file = new File(string, "rcconfig.txt");
                    this.fis = new FileInputStream(this.file);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
    }

    public void writeConfig(Hashtable hashtable) {
        if (hashtable != null) {
            try {
                PrivilegeManager.enablePrivilege((String)"UniversalFileAccess");
                this.writeAccessGranted = true;
            }
            catch (Exception exception) {
                this.writeAccessGranted = exception.toString().indexOf("ClassNotFoundException") != -1;
            }
            catch (NoClassDefFoundError noClassDefFoundError) {
                // empty catch block
            }
            if (this.writeAccessGranted) {
                try {
                    if (this.file != null) {
                        this.fos = new FileOutputStream(this.file);
                    }
                    if (this.fos != null) {
                        Enumeration enumeration = hashtable.keys();
                        Enumeration enumeration2 = hashtable.elements();
                        while (enumeration.hasMoreElements()) {
                            this.fos.write(((String)enumeration.nextElement() + "=").getBytes());
                            this.fos.write(((String)enumeration2.nextElement()).getBytes());
                            this.fos.flush();
                            this.fos.close();
                            this.fos = null;
                        }
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    protected Hashtable readConfig() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        if (this.accessGranted) {
            try {
                int n = this.fis.available();
                if (n > 0) {
                    byte[] byArray = new byte[n];
                    this.fis.read(byArray);
                    this.fis.close();
                    this.fis = null;
                    StringTokenizer stringTokenizer = new StringTokenizer(new String(byArray), "\n");
                    StringTokenizer stringTokenizer2 = null;
                    while (stringTokenizer.hasMoreTokens()) {
                        stringTokenizer2 = new StringTokenizer(new String(stringTokenizer.nextToken()), "=");
                        if (stringTokenizer2.countTokens() != 2) continue;
                        hashtable.put(stringTokenizer2.nextToken(), stringTokenizer2.nextToken());
                    }
                    return hashtable;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    protected void closeConfig() {
        try {
            if (this.fis != null) {
                this.fis.close();
                this.fis = null;
            }
            if (this.fos != null) {
                this.fos.close();
                this.fos = null;
            }
            this.file = null;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

