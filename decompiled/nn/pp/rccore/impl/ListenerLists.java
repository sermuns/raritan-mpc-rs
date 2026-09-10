/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import nn.pp.core.impl.NotificationListenerList;
import nn.pp.rccore.impl.AudioStatusExListenerList;
import nn.pp.rccore.impl.AudioStatusListenerList;
import nn.pp.rccore.impl.ConnectionEventListenerList;
import nn.pp.rccore.impl.KeyboardInfoListenerList;
import nn.pp.rccore.impl.KeyboardListenerList;
import nn.pp.rccore.impl.LicenseSupportListenerList;
import nn.pp.rccore.impl.MouseEventListenerList;
import nn.pp.rccore.impl.MouseModeListenerList;
import nn.pp.rccore.impl.VideoEventListenerList;
import nn.pp.rccore.impl.VirtualMediaInfoListenerList;

public class ListenerLists {
    public VideoEventListenerList videoEventListenerList = new VideoEventListenerList();
    public AudioStatusListenerList audioStatusListenerList = new AudioStatusListenerList();
    public NotificationListenerList notificationListenerList = new NotificationListenerList();
    public ConnectionEventListenerList connectionEventListenerList = new ConnectionEventListenerList();
    public MouseModeListenerList mouseModeListenerList = new MouseModeListenerList();
    public KeyboardInfoListenerList keyboardInfoListenerList = new KeyboardInfoListenerList();
    public VirtualMediaInfoListenerList virtualMediaInfoListenerList = new VirtualMediaInfoListenerList();
    public KeyboardListenerList keyboardListenerList = new KeyboardListenerList();
    public MouseEventListenerList mouseEventListenerList = new MouseEventListenerList();
    public LicenseSupportListenerList licenseSupportListenerList = new LicenseSupportListenerList();
    public AudioStatusExListenerList audioSettingsExListenerList = new AudioStatusExListenerList();
}

