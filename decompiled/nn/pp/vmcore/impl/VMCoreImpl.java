/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.X509TrustManager;
import nn.pp.core.NotificationListener;
import nn.pp.core.T;
import nn.pp.core.impl.DeviceConnector;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.VMAdapter;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.VirtualMediaEventListener;
import nn.pp.vmcore.impl.ListenerLists;
import nn.pp.vmcore.impl.RedirectedObject;
import nn.pp.vmcore.impl.msp.MspHandler;

public class VMCoreImpl
implements VMCore {
    protected Logger logger;
    protected int msIndex;
    private Timer mediaTimer;
    private VMAdapter adapter = new VMAdapter(){

        @Override
        public void disconnected(Exception exception) {
            VMCoreImpl.this.disconnect(true);
        }
    };
    private X509TrustManager trustManager;
    private DeviceConnector connector;
    private boolean connected = false;
    private RedirectableObject redirectable;
    private RedirectedObject redirected;
    private boolean locked = false;
    private MspHandler mspHandler;
    private ListenerLists listeners = new ListenerLists();

    public VMCoreImpl(Logger logger, int n) {
        if (logger == null) {
            logger = Logger.getLogger("Virtual Media");
            logger.setUseParentHandlers(false);
            logger.addHandler(new ConsoleHandler());
            logger.setLevel(Level.SEVERE);
        }
        this.logger = logger;
        this.msIndex = n;
        this.addVirtualMediaEventListener(this.adapter, 0);
    }

    @Override
    public void dispose() {
        if (this.mediaTimer != null) {
            this.mediaTimer.cancel();
            this.mediaTimer = null;
        }
        if (this.connector != null) {
            this.connector = null;
        }
        if (this.adapter != null) {
            this.adapter = null;
        }
        if (this.trustManager != null) {
            this.trustManager = null;
        }
        if (this.mspHandler != null) {
            this.mspHandler = null;
        }
        if (this.redirectable != null) {
            this.redirectable = null;
        }
        if (this.redirected != null) {
            this.redirected = null;
        }
        this.removeVirtualMediaEventListener(this.adapter);
    }

    @Override
    public void setX509TrustManager(X509TrustManager x509TrustManager) {
        this.trustManager = x509TrustManager;
    }

    @Override
    public void connectVMWithUserLogin(String string, int n, boolean bl, String string2, int n2, RedirectableObject redirectableObject, boolean bl2, VMCore.LockFailBehavior lockFailBehavior, String string3, String string4) throws IOException, VMException {
        this.connectToHost(string, n, bl, string2, n2, redirectableObject, bl2, lockFailBehavior, string3, string4, null, null, null, null);
    }

    @Override
    public void connectVMWithRdmSession(String string, int n, boolean bl, String string2, int n2, RedirectableObject redirectableObject, boolean bl2, VMCore.LockFailBehavior lockFailBehavior, String string3, String string4, String string5) throws IOException, VMException {
        this.connectToHost(string, n, bl, string2, n2, redirectableObject, bl2, lockFailBehavior, null, null, null, string3, string4, string5);
    }

    @Override
    public void connectVMWithEricKey(String string, int n, boolean bl, String string2, int n2, RedirectableObject redirectableObject, boolean bl2, VMCore.LockFailBehavior lockFailBehavior, String string3) throws IOException, VMException {
        this.connectToHost(string, n, bl, string2, n2, redirectableObject, bl2, lockFailBehavior, null, null, string3, null, null, null);
    }

    private void connectToHost(String string, int n, boolean bl, String string2, int n2, RedirectableObject redirectableObject, boolean bl2, VMCore.LockFailBehavior lockFailBehavior, String string3, String string4, String string5, String string6, String string7, String string8) throws IOException, VMException {
        this.redirectable = redirectableObject;
        if (this.connected) {
            throw new VMException(T._("Already connected!"));
        }
        this.redirected = RedirectedObject.loadRedirectedObject(redirectableObject, bl2, this.logger);
        try {
            this.redirected.open();
            this.redirected.lockAccess(false, lockFailBehavior, this.listeners.virtualMediaEventListenerList);
            this.locked = true;
            this.redirected.determineGeometry();
            this.connector = new DeviceConnector(this.logger, this.trustManager);
            this.connector.connect(string, n, bl);
            if (!bl && string7 != null && string8 != null) {
                this.connector.writeCCSGproxyModePrefix(string7);
                if (string8.equals("yes")) {
                    System.out.println("CC-Proxy[VM] mode: SSL enabled .. \n");
                    this.connector.connectSSLWithSocket(string, n);
                } else {
                    System.out.println("CC-Proxy[VM] mode: plaintext .. \n");
                }
            }
            this.mspHandler = MspHandler.loadMspHandler(this.connector, this.logger, this.listeners, this.redirected, n2, this.msIndex, bl2);
            this.mspHandler.connect(string2, string3, string4, string5, string6);
            this.mediaTimer = new Timer("MediaChange");
            this.mediaTimer.schedule(new TimerTask(){

                @Override
                public void run() {
                    VMCoreImpl.this.checkMediumChange();
                }
            }, 500L, 500L);
            this.connected = true;
        }
        catch (VMException vMException) {
            this.logger.log(Level.SEVERE, T._("Could not establish Virtual Media session"), vMException);
            this.disconnect(true);
            throw vMException;
        }
        catch (IOException iOException) {
            this.logger.log(Level.SEVERE, T._("Could not establish Virtual Media session"), iOException);
            this.disconnect(true);
            throw iOException;
        }
        catch (Exception exception) {
            this.logger.log(Level.SEVERE, T._("Could not establish Virtual Media session"), exception);
            this.disconnect(true);
            throw new VMException(exception.getMessage());
        }
    }

    @Override
    public void disconnect(boolean bl) {
        this.disconnect();
        if (!bl) {
            this.listeners.virtualMediaEventListenerList.fireVirtualMediaDriveDisConnected(true);
        }
    }

    @Override
    public void disconnect() {
        if (this.mspHandler != null) {
            this.mspHandler.close();
            this.connector.disconnect();
            this.mspHandler.dispose();
            this.mspHandler = null;
            this.connector = null;
        }
        if (this.mediaTimer != null) {
            this.mediaTimer.cancel();
            this.mediaTimer = null;
        }
        if (this.redirected != null) {
            System.out.println("Redirected Drive is not null");
            if (this.locked) {
                try {
                    this.logger.log(Level.INFO, "Unlocking Drive:" + this.redirectable.getShortName());
                    this.redirected.lockAccess(true, VMCore.LockFailBehavior.IGNORE, this.listeners.virtualMediaEventListenerList);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            this.redirected.close();
            this.redirected = null;
        }
        this.redirectable = null;
        this.connected = false;
    }

    private void checkMediumChange() {
        if (this.redirected != null) {
            try {
                RedirectedObject.MediumChangeState mediumChangeState = this.redirected.getMediumChangeState();
                if (mediumChangeState != RedirectedObject.MediumChangeState.NO_CHANGE) {
                    if (mediumChangeState == RedirectedObject.MediumChangeState.REMOVED) {
                        this.logger.log(Level.FINEST, "Medium has been removed.");
                        this.mediumRemovedForChange();
                    } else if (mediumChangeState == RedirectedObject.MediumChangeState.CHANGED) {
                        this.logger.log(Level.FINEST, "Medium has been changed.");
                        this.mediumInsertedAfterChange();
                    }
                }
            }
            catch (Exception exception) {
                this.disconnect(false);
            }
        }
    }

    private void mediumRemovedForChange() throws Exception {
        this.redirected.mediumRemovedForChange();
        this.mspHandler.sendMediumRemoval();
    }

    private void mediumInsertedAfterChange() throws Exception {
        this.redirected.lockAccess(true);
        this.redirected.close();
        this.redirected.open();
        this.redirected.lockAccess(false);
        this.redirected.mediumInsertedAfterChange();
        this.mspHandler.sendMediumChange();
    }

    @Override
    public void addNotificationListener(NotificationListener notificationListener) {
        this.listeners.notificationListenerList.addListener(notificationListener);
    }

    @Override
    public void removeNotificationListener(NotificationListener notificationListener) {
        this.listeners.notificationListenerList.removeListener(notificationListener);
    }

    @Override
    public void addVirtualMediaEventListener(VirtualMediaEventListener virtualMediaEventListener, int n) {
        this.listeners.virtualMediaEventListenerList.addListener(virtualMediaEventListener, n);
    }

    @Override
    public void removeVirtualMediaEventListener(VirtualMediaEventListener virtualMediaEventListener) {
        this.listeners.virtualMediaEventListenerList.removeListener(virtualMediaEventListener);
    }

    @Override
    public void setMsIndex(int n) {
        this.msIndex = n;
    }
}

