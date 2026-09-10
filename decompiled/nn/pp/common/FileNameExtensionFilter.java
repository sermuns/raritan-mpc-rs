/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.filechooser.FileFilter;

public final class FileNameExtensionFilter
extends FileFilter {
    private final String description;
    private final String[] extensions;
    private final String[] lowerCaseExtensions;

    public FileNameExtensionFilter(String string, String ... stringArray) {
        if (stringArray == null || stringArray.length == 0) {
            throw new IllegalArgumentException("Extensions must be non-null and not empty");
        }
        this.description = string;
        this.extensions = new String[stringArray.length];
        this.lowerCaseExtensions = new String[stringArray.length];
        for (int i = 0; i < stringArray.length; ++i) {
            if (stringArray[i] == null || stringArray[i].length() == 0) {
                throw new IllegalArgumentException("Each extension must be non-null and not empty");
            }
            this.extensions[i] = stringArray[i];
            this.lowerCaseExtensions[i] = stringArray[i].toLowerCase(Locale.ENGLISH);
        }
    }

    @Override
    public boolean accept(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                return true;
            }
            String string = file.getName();
            int n = string.lastIndexOf(46);
            if (n > 0 && n < string.length() - 1) {
                String string2 = string.substring(n + 1).toLowerCase(Locale.ENGLISH);
                for (String string3 : this.lowerCaseExtensions) {
                    if (!string2.equals(string3)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String[] getExtensions() {
        String[] stringArray = new String[this.extensions.length];
        System.arraycopy(this.extensions, 0, stringArray, 0, this.extensions.length);
        return stringArray;
    }

    public String toString() {
        return super.toString() + "[description=" + this.getDescription() + " extensions=" + Arrays.asList(this.getExtensions()) + "]";
    }
}

