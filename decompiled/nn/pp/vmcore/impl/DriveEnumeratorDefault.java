/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import nn.pp.vmcore.DriveEnumerator;
import nn.pp.vmcore.RedirectableObject;

public class DriveEnumeratorDefault
implements DriveEnumerator {
    private static List<String> lockedDrives = new Vector<String>();
    private static Object lockedDrivesMutex = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void driveLocked(RedirectableObject redirectableObject, boolean bl) {
        String string = redirectableObject.getLongName();
        Object object = lockedDrivesMutex;
        synchronized (object) {
            if (bl) {
                if (!lockedDrives.contains(string)) {
                    lockedDrives.add(string);
                }
            } else if (lockedDrives.contains(string)) {
                lockedDrives.remove(string);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<RedirectableObject> getAvailableDrives(Logger logger) {
        List<RedirectableObject> list = this.enumerateDrives(logger);
        if (list == null) {
            return null;
        }
        Vector<RedirectableObject> vector = new Vector<RedirectableObject>();
        Object object = lockedDrivesMutex;
        synchronized (object) {
            for (RedirectableObject redirectableObject : list) {
                if (lockedDrives.contains(redirectableObject.getLongName())) continue;
                vector.add(redirectableObject);
            }
        }
        return vector;
    }

    protected List<RedirectableObject> enumerateDrives(Logger logger) {
        return new Vector<RedirectableObject>();
    }
}

