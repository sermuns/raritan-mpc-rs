/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.List;
import java.util.Locale;
import java.util.Vector;
import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.ConnectionEventListener;
import nn.pp.rccore.IKvmPort;
import nn.pp.rccore.KvmPort;
import nn.pp.rccore.impl.ConnectionEventListenerAction;
import nn.pp.rccore.impl.ConnectionEventListenerActionString;
import nn.pp.rccore.impl.StringLocale;

public class ConnectionEventListenerList
extends ListenerList<ConnectionEventListener> {
    public void fireDisconnected(final Exception exception) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).disconnected(exception);
            }
        });
    }

    public void fireConnected() {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).connected();
            }
        });
    }

    public String firePortListChanged(List<? extends IKvmPort> list) {
        final Vector<KvmPort> vector = new Vector<KvmPort>();
        for (IKvmPort iKvmPort : list) {
            vector.add(new KvmPort(iKvmPort));
        }
        String string = new ListenerList.FireResult<String>().fire(new ConnectionEventListenerActionString(){

            @Override
            public void run() {
                this.result = ((ConnectionEventListener)this.listener).portListChanged(vector);
            }
        }, 1);
        if (string != null) {
            return string;
        }
        return null;
    }

    public void fireServerSessionIdChanged(final int n) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).serverSessionIdChanged(n);
            }
        }, 2);
    }

    public void fireConnectedUsersChanged(final int n) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).connectedUsersChanged(n);
            }
        }, 4);
    }

    public void fireMonitorModePermissionChanged(final boolean bl) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).monitorModePermissionChanged(bl);
            }
        }, 8);
    }

    public void fireMonitorModeChanged(final boolean bl) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).monitorModeChanged(bl);
            }
        }, 8);
    }

    public void fireExclusiveModePermissionChanged(final boolean bl) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).exclusiveModePermissionChanged(bl);
            }
        }, 16);
    }

    public void fireExclusiveModeChanged(final boolean bl) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).exclusiveModeChanged(bl);
            }
        }, 16);
    }

    public void fireLanguageChanged(Locale locale) {
        final Locale locale2 = StringLocale.loadLocale(locale);
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).languageChanged(locale2);
            }
        }, 32);
    }

    public void fireProtocolVersionChanged(String string) {
        final String string2 = new String(string);
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).protocolVersionChanged(string2);
            }
        }, 64);
    }

    public void fireDeviceNameChanged(String string) {
        final String string2 = new String(string);
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).deviceNameChanged(string2);
            }
        }, 128);
    }

    public void fireIncomingTrafficSpeed(final int n) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).incomingTrafficSpeed(n);
            }
        }, 256);
    }

    public void fireOutgoingTrafficSpeed(final int n) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).outgoingTrafficSpeed(n);
            }
        }, 256);
    }

    public void fireFramesPerSecond(final int n) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).framesPerSecond(n);
            }
        }, 256);
    }

    public void fireChatWelcomeChanged(String string) {
        final String string2 = new String(string);
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).chatWelcomeChanged(string2);
            }
        }, 512);
    }

    public void fireNewChatMessage(String string) {
        final String string2 = new String(string);
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).newChatMessage(string2);
            }
        }, 512);
    }

    public void fireCimLanguageOptionsSupported(final boolean bl) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).cimLanguageOptionsSupported(bl);
            }
        }, 2048);
    }

    public void fireEthernetGigabitSupported(final boolean bl) {
        this.fire(new ConnectionEventListenerAction(){

            @Override
            public void run() {
                ((ConnectionEventListener)this.listener).ethernetGigabitSupported(bl);
            }
        }, 4096);
    }
}

