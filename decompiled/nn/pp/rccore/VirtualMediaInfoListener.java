/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.List;
import nn.pp.rccore.IUsbProfileList;
import nn.pp.rccore.IVMConfigInfo;
import nn.pp.rccore.IVMMountRequestResponse;

public interface VirtualMediaInfoListener {
    public static final int VM_GENERAL = 1;
    public static final int REMOTE_ISO = 2;
    public static final int USB_PROFILE = 4;
    public static final int ALL = 7;

    public void virtualMediaSupportChanged(boolean var1);

    public void virtualMediaReadOnlyChanged(boolean var1);

    public void virtualMediaDriveCountChanged(int var1);

    public void remoteIsoSupportChanged(boolean var1);

    public void remoteIsoListChanged(List<IVMMountRequestResponse> var1);

    public void remoteIsoMountFinished(IVMMountRequestResponse var1);

    public void UsbProfileListChanged(IUsbProfileList var1);

    public void virtualMediaConfigChanged(IVMConfigInfo var1);
}

