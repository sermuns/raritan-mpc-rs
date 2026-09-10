/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore;

import java.io.IOException;
import javax.net.ssl.X509TrustManager;
import nn.pp.core.NotificationListener;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.VirtualMediaEventListener;

public interface VMCore {
    public void dispose();

    public void setX509TrustManager(X509TrustManager var1);

    public void connectVMWithUserLogin(String var1, int var2, boolean var3, String var4, int var5, RedirectableObject var6, boolean var7, LockFailBehavior var8, String var9, String var10) throws IOException, VMException;

    public void connectVMWithRdmSession(String var1, int var2, boolean var3, String var4, int var5, RedirectableObject var6, boolean var7, LockFailBehavior var8, String var9, String var10, String var11) throws IOException, VMException;

    public void connectVMWithEricKey(String var1, int var2, boolean var3, String var4, int var5, RedirectableObject var6, boolean var7, LockFailBehavior var8, String var9) throws IOException, VMException;

    public void disconnect();

    public void disconnect(boolean var1);

    public void addNotificationListener(NotificationListener var1);

    public void removeNotificationListener(NotificationListener var1);

    public void addVirtualMediaEventListener(VirtualMediaEventListener var1, int var2);

    public void removeVirtualMediaEventListener(VirtualMediaEventListener var1);

    public void setMsIndex(int var1);

    public static enum LockFailBehavior {
        FAIL,
        ASK,
        IGNORE;

    }

    public static enum DriveType {
        UNKNOWN,
        CDROM,
        FLOPPY,
        REMOVABLE,
        HARD_DISK_FULL,
        HARD_DISK_FULL_EXTERNAL,
        HARD_DISK_PARTITION,
        HARD_DISK_PARTITION_EXTERNAL;

    }
}

