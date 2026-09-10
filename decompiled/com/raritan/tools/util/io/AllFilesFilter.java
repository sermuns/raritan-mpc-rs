/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util.io;

import com.raritan.tools.util.io.DefaultFileMapper;
import com.raritan.tools.util.io.FileMapper;
import java.io.File;
import javax.swing.filechooser.FileFilter;

public class AllFilesFilter
extends FileFilter {
    public static final String LDAP_FILE_EXTENSION = "DB";
    public static final String HMX_EXTENSION = "RFP";
    public static final String TEXT_FILE_EXTENSION = "TXT";
    public static final int LDAP_CERTIFICATE_FILE_TYPE = 1;
    public static final int LDAP_KEY_FILE_TYPE = 2;
    public static final int TEXT_FILE_TYPE = 3;
    public static final int RFP_FILE_TYPE = 4;
    public static final String CERTIFICATE_EXTENSION = "CRT";
    private String description = "LDAP Certificate (*." + "DB".toLowerCase() + ")";

    public AllFilesFilter() {
    }

    public AllFilesFilter(int n) {
        switch (n) {
            case 1: {
                this.description = "LDAP Certificate (*." + LDAP_FILE_EXTENSION.toLowerCase() + ")";
                break;
            }
            case 2: {
                this.description = "LDAP Key (*." + LDAP_FILE_EXTENSION.toLowerCase() + ")";
                break;
            }
            case 3: {
                this.description = "Text Files (*." + TEXT_FILE_EXTENSION.toLowerCase() + ")";
                break;
            }
            case 4: {
                this.description = "RFP Files (*." + HMX_EXTENSION.toLowerCase() + ")";
            }
        }
    }

    @Override
    public boolean accept(File file) {
        if (file != null && file.isDirectory()) {
            return true;
        }
        String string = this.getExtension(file);
        if (string.equalsIgnoreCase(CERTIFICATE_EXTENSION)) {
            return true;
        }
        if (string.equalsIgnoreCase(HMX_EXTENSION)) {
            return true;
        }
        if (string.equalsIgnoreCase(TEXT_FILE_EXTENSION)) {
            return true;
        }
        return string.equalsIgnoreCase(LDAP_FILE_EXTENSION);
    }

    public static FileMapper createFileMapper() {
        return new DefaultFileMapper();
    }

    private String getExtension(File file) {
        String string;
        int n;
        if (file != null && (n = (string = file.getName()).lastIndexOf(46)) > 0 && n < string.length() - 1) {
            return string.substring(n + 1).toLowerCase();
        }
        return "";
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}

