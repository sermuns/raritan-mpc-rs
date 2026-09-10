/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.impl.DriveEnumeratorDefault;

public abstract class DriveEnumeratorUnix
extends DriveEnumeratorDefault {
    private List<String> found = new Vector<String>();

    protected boolean contains(File file) {
        String string;
        String string2 = file.getAbsolutePath();
        try {
            string = file.getCanonicalPath();
        }
        catch (IOException iOException) {
            string = string2;
        }
        for (String string3 : this.found) {
            if (!string.equals(string3)) continue;
            return true;
        }
        this.found.add(string);
        return false;
    }

    protected abstract List<RedirectableObject> enumerateDevDrives(Logger var1);

    @Override
    protected List<RedirectableObject> enumerateDrives(Logger logger) {
        List<RedirectableObject> list = this.enumerateDevDrives(logger);
        Collections.sort(list, new Comparator<RedirectableObject>(){

            @Override
            public int compare(RedirectableObject redirectableObject, RedirectableObject redirectableObject2) {
                return redirectableObject.getLongName().compareTo(redirectableObject2.getLongName());
            }
        });
        return list;
    }
}

