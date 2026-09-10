/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KvmPortPermissionHelper;
import com.raritan.rrc.data.KvmStream;
import com.raritan.rrc.data.MassStorageDevice;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.Stream;
import com.raritan.rrc.data.USBProfilesInfo;
import com.raritan.rrc.data.VMConfigInfo;
import com.raritan.rrc.data.VMInterfaceInfo;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.smartcard.SmartCardCore;
import com.raritan.smartcard.SmartCardInitException;
import com.raritan.tools.ui.ScreenContext;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javaclientlib.clientlib.IKvmData;
import javaclientlib.tr.TRLIB_UPDATEINFO;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;
import javaclientlib.tr.TRSRVR_TARGET_PARAMS;
import javaclientlib.utils.RRCLogger;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import nn.pp.audiocore.AudioAdapter;
import nn.pp.audiocore.AudioCore;
import nn.pp.audiocore.AudioCoreFactory;
import nn.pp.audiocore.AudioDevice;
import nn.pp.audiocore.AudioEventListener;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.common.audio.AudioBean;
import nn.pp.common.audio.AudioIndexHelper;
import nn.pp.common.smartcard.SmartCardBean;
import nn.pp.common.smartcard.SmartCardIndexHelper;
import nn.pp.core.INotificationEvent;
import nn.pp.core.Platform;
import nn.pp.rccore.IAudioStatusExSupport;
import nn.pp.rccore.IKvmPort;
import nn.pp.rccore.ILicenseSupport;
import nn.pp.vmcore.WorkstationUnlockDetectorFactory;

public class KvmPort
extends Port
implements IKvmData {
    private KvmStream kvmStream;
    private DeviceView aDeviceView;
    private boolean singleCursorMode = false;
    private KvmMouse kvmMouse;
    private static final int OVERRIDE_LOCAL_USER = 1;
    private VMConfigInfo vmConfigInfo;
    private volatile VMConfigInfo defaultVMConfigInfo;
    private USBProfilesInfo usbProfilesInfo;
    private SmartCardIndexHelper smartCardIndexHelper = new SmartCardIndexHelper(null);
    private AudioIndexHelper audioIndexHelper = new AudioIndexHelper(null);
    private final KvmPortPermissionHelper portPermissionHelper = new KvmPortPermissionHelper(this);
    private final SmartCardAction smartCardAction = new SmartCardAction();
    private final SelectCardReaderAction selectCardReaderAction = new SelectCardReaderAction();
    private final AudioAction audioAction = new AudioAction();
    private final ConnectAudioAction connectAudioAction = new ConnectAudioAction();
    private final AudioSettingsAction audioSettingsAction = new AudioSettingsAction();
    private volatile boolean rfbSwitchCompleted;
    private final AudioCore audiocore = AudioCoreFactory.loadAudioCore(RRCLogger.getLogger());
    private boolean audioConnected = false;
    private PortAudioAdapter audioAdapter = new PortAudioAdapter();
    private ILicenseSupport license;
    private IAudioStatusExSupport audioEx;

    private boolean isMassStorageConnected(int n) {
        VMConfigInfo vMConfigInfo = this.getVmConfigInfo();
        for (VMInterfaceInfo vMInterfaceInfo : vMConfigInfo.getListofInterfaces()) {
            MassStorageDevice massStorageDevice;
            if (!vMInterfaceInfo.isInUse() || (massStorageDevice = vMInterfaceInfo.getMassStorageDevice(n)) == null || !massStorageDevice.isConnected()) continue;
            return true;
        }
        return false;
    }

    private boolean isLocalMSConnected() {
        return this.isMassStorageConnected(4);
    }

    private boolean isCdIsoMSConnected() {
        return this.isMassStorageConnected(1);
    }

    private int getMassStorageIndex(int n) {
        VMConfigInfo vMConfigInfo = this.getVmConfigInfo();
        for (VMInterfaceInfo vMInterfaceInfo : vMConfigInfo.getListofInterfaces()) {
            MassStorageDevice massStorageDevice = vMInterfaceInfo.getMassStorageDevice(n);
            if (massStorageDevice == null) continue;
            return vMInterfaceInfo.getInterfaceID();
        }
        return -1;
    }

    private int getLocalMsIndex() {
        return this.getMassStorageIndex(4);
    }

    private int getCdIsoMsIndex() {
        return this.getMassStorageIndex(1);
    }

    private void audioConnectionClosed(Exception exception) {
        if (!this.isConnected()) {
            return;
        }
        String string = exception != null ? exception.getMessage() : null;
        String string2 = string == null ? this.bundle.getString("Audio.audioConnectionClosed") : MessageFormat.format(this.bundle.getString("Audio.audioConnectionClosedWithReason"), string);
        RRCLogger.log(150, string2);
        this.getView().getAudioErrorsAndMessagesHandler().errorMessage(this.getView().getShell(), string2, this.getViewName());
        this.connectAudioAction.setConnectionState(false);
    }

    private static VMConfigInfo getDefaultVMConfigInfo() {
        ArrayList<VMInterfaceInfo> arrayList = new ArrayList<VMInterfaceInfo>();
        ArrayList<MassStorageDevice> arrayList2 = new ArrayList<MassStorageDevice>();
        arrayList2.add(new MassStorageDevice(1));
        arrayList.add(new VMInterfaceInfo(0, arrayList2));
        arrayList2 = new ArrayList();
        arrayList2.add(new MassStorageDevice(4));
        arrayList.add(new VMInterfaceInfo(1, arrayList2));
        return new VMConfigInfo(arrayList);
    }

    public KvmPort(ScreenContext screenContext) {
        super(screenContext);
        this.kvmMouse = new KvmMouse();
        this.smartCardAction.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals("enabled")) {
                    KvmPort.this.selectCardReaderAction.setEnabled((Boolean)propertyChangeEvent.getNewValue());
                    if (KvmPort.this.scrContext != null) {
                        ((RRCScreenContext)KvmPort.this.scrContext).getSmartCardObserver().forceNotifyObservers(null);
                    }
                } else if (propertyChangeEvent.getPropertyName().equals("ShortDescription")) {
                    String string = (String)propertyChangeEvent.getNewValue();
                    if (null == string) {
                        string = KvmPort.this.bundle.getString("SmartCardSelectCardReader.name");
                    }
                    KvmPort.this.selectCardReaderAction.putValue("ShortDescription", string);
                    if (KvmPort.this.scrContext != null) {
                        ((RRCScreenContext)KvmPort.this.scrContext).getSmartCardObserver().forceNotifyObservers(null);
                    }
                }
            }
        });
        this.audioAction.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals("enabled")) {
                    KvmPort.this.connectAudioAction.setEnabled((Boolean)propertyChangeEvent.getNewValue());
                    if (KvmPort.this.scrContext != null) {
                        ((RRCScreenContext)KvmPort.this.scrContext).getAudioObserver().forceNotifyObservers(null);
                    }
                } else if (propertyChangeEvent.getPropertyName().equals("ShortDescription")) {
                    String string = (String)propertyChangeEvent.getNewValue();
                    if (null == string) {
                        string = KvmPort.this.bundle.getString("Audio.connectAudio");
                    }
                    KvmPort.this.connectAudioAction.putValue("ShortDescription", string);
                    if (KvmPort.this.scrContext != null) {
                        ((RRCScreenContext)KvmPort.this.scrContext).getAudioObserver().forceNotifyObservers(null);
                    }
                }
            }
        });
        this.audiocore.addAudioEventListener(this.audioAdapter);
    }

    @Override
    public Stream getStream() {
        return this.kvmStream;
    }

    @Override
    public DeviceView getView() {
        return this.aDeviceView;
    }

    public void setView(DeviceView deviceView) {
        this.aDeviceView = deviceView;
        if (this.aDeviceView != null) {
            DevicePreferences devicePreferences = this.device.getDevPrefs();
            if (devicePreferences != null) {
                this.aDeviceView.setUpdateFrequency(200 / (devicePreferences.getFramesPerSecond() + 1));
            } else {
                this.aDeviceView.setUpdateFrequency(50L);
            }
        }
    }

    public boolean isStreamConnected() {
        return this.kvmStream != null ? this.kvmStream.isConnected() : false;
    }

    @Override
    public void connect() {
        Object object;
        Object object2;
        Object object3;
        if (this.isConnected()) {
            return;
        }
        if (this.kvmStream == null) {
            object3 = (RRCScreenContext)this.getDevice().getContext();
            object2 = ((RRCScreenContext)object3).getAppSettings();
            this.kvmStream = new KvmStream(this, object2.getKeyboardType());
            object = this.device.getDevPrefs();
            if (object != null) {
                this.kvmStream.setMinFrameUpdateTime(200 / (((DevicePreferences)object).getFramesPerSecond() + 1));
            }
        }
        object3 = this.getId();
        object2 = this.getParent();
        if (object2 != null && object2 instanceof Paragon) {
            object = (Paragon)object2;
            object3 = ((Paragon)object).getPortal();
        }
        if (this.kvmStream.startVideoStream((String)object3, this.getTargetDeviceId(), 0)) {
            this.setConnected(true);
            this.setState("CONNECTED");
            this.firePropertyChange("DEVICE_PORT_VIEW_ADD", null, null);
        }
    }

    public void forceConnection() {
        Object object;
        Object object2;
        Object object3;
        if (this.kvmStream == null) {
            object3 = (RRCScreenContext)this.getDevice().getContext();
            object2 = ((RRCScreenContext)object3).getAppSettings();
            this.kvmStream = new KvmStream(this, object2.getKeyboardType());
            object = this.device.getDevPrefs();
            if (object != null) {
                this.kvmStream.setMinFrameUpdateTime(200 / (((DevicePreferences)object).getFramesPerSecond() + 1));
            }
        }
        object3 = this.getId();
        object2 = this.getParent();
        if (object2 != null && object2 instanceof Paragon) {
            object = (Paragon)object2;
            object3 = ((Paragon)object).getPortal();
        }
        if (this.kvmStream.startVideoStream((String)object3, this.getTargetDeviceId(), 1)) {
            this.setConnected(true);
            this.setState("CONNECTED");
            this.firePropertyChange("DEVICE_PORT_VIEW_ADD", null, null);
        }
    }

    @Override
    public void disconnect() {
        this.disconnect(true);
    }

    public void disconnect(boolean bl) {
        this.disconnectDevice();
        super.disconnect();
        if (this.isBladePort() && this.getParentBladeChassis() != null) {
            this.getParentBladeChassis().setState("AVAILABLE");
        }
        if (this.aDeviceView != null) {
            this.aDeviceView.removeListeners();
        }
        this.aDeviceView = null;
        this.singleCursorMode = false;
        this.setDeviceView(null);
        this.setVmConfigInfo(null);
        this.defaultVMConfigInfo = null;
        this.setUsbProfilesInfo(null);
        this.rfbSwitchCompleted = false;
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).clearStatusBarLabels();
        this.firePropertyChange("DEVICE_PORT_VIEW_REMOVE", null, null);
        if (bl) {
            Port port;
            if (this.isPrimaryPort()) {
                for (Port port2 : this.getSecondaryPorts()) {
                    if (port2 == null || !(port2 instanceof KvmPort)) continue;
                    ((KvmPort)port2).disconnect(false);
                }
            } else if (this.isSecondaryPort() && (port = this.getPrimaryPort()) != null) {
                port.disconnect();
            }
        }
    }

    public boolean disconnectDevice() {
        if (!this.isConnected()) {
            return false;
        }
        boolean bl = true;
        this.setConnected(false);
        if (this.aDeviceView != null) {
            bl = this.aDeviceView.disconnect();
        }
        if (this.kvmStream != null && this.kvmStream.isConnected()) {
            this.kvmStream.stopVideoStream();
        }
        this.kvmStream = null;
        this.setActive(false);
        this.setState("AVAILABLE");
        if (this.equals(((IPReach)this.getBaseDevice()).getActiveKvmPort())) {
            ((IPReach)this.getBaseDevice()).setActiveKvmPort(null);
        }
        if (this.audiocore != null) {
            this.audiocore.disconnect();
        }
        this.audioConnected = false;
        return bl;
    }

    public void setUpKvmMouse(TRSRVR_TARGET_PARAMS tRSRVR_TARGET_PARAMS) {
        this.kvmMouse.setUp(new String(tRSRVR_TARGET_PARAMS.getIConnection()), tRSRVR_TARGET_PARAMS.getICaps(), tRSRVR_TARGET_PARAMS.getISettings());
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                MPCUtil.notifyObservers((RRCScreenContext)KvmPort.this.scrContext, KvmPort.this);
            }
        });
    }

    public KvmMouse getKvmMouse() {
        return this.kvmMouse;
    }

    @Override
    public void notify(final int n, final int n2) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                if (KvmPort.this.aDeviceView != null) {
                    KvmPort.this.aDeviceView.notify(n, n2);
                }
            }
        });
    }

    @Override
    public void setUpdateFrequency(long l) {
        if (this.aDeviceView != null) {
            this.aDeviceView.setUpdateFrequency(l);
        }
    }

    @Override
    public void updateNotify(TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
        if (this.aDeviceView != null) {
            this.aDeviceView.updateNotify(tRLIB_UPDATEINFO);
        }
    }

    @Override
    public void newVideoModeNotify(final TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                if (KvmPort.this.aDeviceView != null) {
                    KvmPort.this.aDeviceView.newVideoModeNotify(tRRSP_NEW_VIDEO_MODE_DATA);
                }
            }
        });
    }

    public void toggleSingleMouseCursor() {
        boolean bl = this.singleCursorMode = !this.singleCursorMode;
        if (this.aDeviceView != null) {
            this.doSetMouseMode();
        }
    }

    private void doSetMouseMode() {
        this.aDeviceView.setSingleCursor(this.singleCursorMode);
    }

    public boolean isSingleCursorMode() {
        return this.singleCursorMode;
    }

    public void initSingleCursorMode() {
    }

    public synchronized VMConfigInfo getVmConfigInfo() {
        if (this.vmConfigInfo != null) {
            return this.vmConfigInfo;
        }
        if ("VM".equals(this.getPortType())) {
            if (this.defaultVMConfigInfo == null) {
                this.defaultVMConfigInfo = KvmPort.getDefaultVMConfigInfo();
            }
            return this.defaultVMConfigInfo;
        }
        return null;
    }

    public synchronized void setVmConfigInfo(VMConfigInfo vMConfigInfo) {
        this.vmConfigInfo = vMConfigInfo;
        if (vMConfigInfo != null) {
            PropertyChangeListener propertyChangeListener = new PropertyChangeListener(){

                @Override
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getPropertyName().equals("connected")) {
                        KvmPort.this.smartCardAction.evaluateEnabled();
                        KvmPort.this.audioAction.evaluateEnabled();
                    }
                }
            };
            List list = vMConfigInfo.getListofInterfaces();
            for (VMInterfaceInfo vMInterfaceInfo : list) {
                List list2 = vMInterfaceInfo.getListofMassStorageDev();
                for (MassStorageDevice massStorageDevice : list2) {
                    massStorageDevice.addPropertyChangeListener(propertyChangeListener);
                }
            }
        }
    }

    public synchronized USBProfilesInfo getUsbProfilesInfo() {
        return this.usbProfilesInfo;
    }

    public synchronized void setUsbProfilesInfo(USBProfilesInfo uSBProfilesInfo) {
        this.usbProfilesInfo = uSBProfilesInfo;
    }

    @Override
    public void setConcurrUsers(int n) {
        super.setConcurrUsers(n);
        this.smartCardAction.setUsersCount(n);
        this.audioAction.setUsersCount(n);
    }

    public void setLicenseSupport(ILicenseSupport iLicenseSupport) {
        this.license = iLicenseSupport;
    }

    public void setAudioEx(IAudioStatusExSupport iAudioStatusExSupport) {
        this.audioEx = iAudioStatusExSupport;
    }

    public String getTargetSpeakerFormat() {
        IAudioStatusExSupport.AudioStatus audioStatus;
        if (this.audioEx != null && (audioStatus = this.audioEx.getSpeakerStatus()) != null && audioStatus.isConnected()) {
            return audioStatus.getFormat();
        }
        return null;
    }

    public String getTargetCaptureFormat() {
        IAudioStatusExSupport.AudioStatus audioStatus;
        if (this.audioEx != null && (audioStatus = this.audioEx.getMicrophoneStatus()) != null && audioStatus.isConnected()) {
            return audioStatus.getFormat();
        }
        return null;
    }

    public void updateLicenses() {
        this.smartCardAction.evaluateEnabled();
        this.audioAction.evaluateEnabled();
    }

    public void setSmartCardIndexHelper(SmartCardIndexHelper smartCardIndexHelper) {
        this.smartCardIndexHelper = smartCardIndexHelper;
        this.smartCardAction.setSmartCardAvailable();
    }

    public void setAudioIndexHelper(AudioIndexHelper audioIndexHelper) {
        this.audioIndexHelper = audioIndexHelper;
        this.audioAction.setAudioAvailable();
    }

    public boolean isRfbSwitchCompleted() {
        return this.rfbSwitchCompleted;
    }

    public void setRfbSwitchCompleted(boolean bl) {
        this.rfbSwitchCompleted = bl;
        this.smartCardAction.setSwitchCompleted(bl);
        this.audioAction.setSwitchCompleted(bl);
    }

    public SmartCardAction getSmartCardAction() {
        return this.smartCardAction;
    }

    public AudioAction getAudioAction() {
        return this.audioAction;
    }

    public ConnectAudioAction getConnectAudioAction() {
        return this.connectAudioAction;
    }

    public AudioSettingsAction getAudioSettingsAction() {
        return this.audioSettingsAction;
    }

    public AudioCore getAudioCore() {
        return this.audiocore;
    }

    @Override
    public void setPortType(String string) {
        super.setPortType(string);
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                KvmPort.this.smartCardAction.evaluateEnabled();
                KvmPort.this.audioAction.evaluateEnabled();
            }
        });
    }

    public KvmPortPermissionHelper getPortPermissionHelper() {
        return this.portPermissionHelper;
    }

    public SelectCardReaderAction getSelectCardReaderAction() {
        return this.selectCardReaderAction;
    }

    public void reevaluatePermissions() {
        this.audioAction.evaluateEnabled();
        this.smartCardAction.evaluateEnabled();
    }

    public class KvmMouse {
        private int mouseAbsolute = 0;
        private int mouseIntelligent = 0;
        private int mouseStandard = 2;
        private int mouseCapability;
        private String mouseConnection = "ps2";
        public static final String ITYPE_MOUSE = "Mouse";
        public static final String ITYPE_KEYBOARD = "Keyboard";
        public static final String ICONNECTION_PS2 = "ps2";
        public static final String ICONNECTION_USB = "usb";
        public static final String ICONNECTION_UNKNOWN = "unknown";
        private static final int MOUSE_CAP_IMPOSSIBLE = 0;
        private static final int MOUSE_CAP_CAN_BE = 1;
        private static final int MOUSE_CAP_IS_RIGHT_NOW = 2;

        private void setUp(String string, int n, int n2) {
            this.mouseConnection = string.startsWith(ICONNECTION_PS2) ? ICONNECTION_PS2 : (string.startsWith(ICONNECTION_USB) ? ICONNECTION_USB : ICONNECTION_UNKNOWN);
            this.mouseAbsolute = (n & 2) != 0 ? 1 : 0;
            this.mouseIntelligent = (n & 1) == 1 ? 1 : 0;
            this.mouseStandard = 1;
            if (((n2 &= n) & 2) != 0) {
                this.mouseAbsolute = 2;
            } else if ((n2 & 1) == 1) {
                this.mouseIntelligent = 2;
            } else {
                this.mouseStandard = 2;
            }
        }

        public boolean enableAbsoluteMouse() {
            return this.mouseAbsolute >= 1;
        }

        public boolean enableIntelligentMouse() {
            return this.mouseIntelligent >= 1;
        }

        public boolean enableStandardMouse() {
            return this.mouseStandard >= 1;
        }

        public boolean isAbsoluteMouse() {
            return this.mouseAbsolute == 2;
        }

        public boolean isStandardMouse() {
            return this.mouseStandard == 2;
        }

        public boolean isIntelligentMouse() {
            return this.mouseIntelligent == 2;
        }
    }

    private class PortAudioAdapter
    extends AudioAdapter {
        private PortAudioAdapter() {
        }

        @Override
        public void disconnected(Exception exception) {
            KvmPort.this.audioConnectionClosed(exception);
            KvmPort.this.audiocore.removeNotificationListener(this);
        }

        @Override
        public void audioConnected(boolean bl) {
            KvmPort.this.connectAudioAction.setConnectionState(true);
            KvmPort.this.audiocore.addNotificationListener(this);
        }

        @Override
        public void receivedNotification(INotificationEvent iNotificationEvent) {
            KvmPort.this.getView().getAudioErrorsAndMessagesHandler().notificationReceived(KvmPort.this.getView().getShell(), iNotificationEvent, KvmPort.this.getViewName());
        }

        @Override
        public void playbackDeviceStateChanged(AudioEventListener.DeviceState deviceState) {
            KvmPort.this.getView().setPlaybackState(deviceState);
        }

        @Override
        public void captureDeviceStateChanged(AudioEventListener.DeviceState deviceState) {
            KvmPort.this.getView().setCaptureState(deviceState);
        }
    }

    class AudioSettingsAction
    extends AbstractAction {
        public AudioSettingsAction() {
            super(KvmPort.this.bundle.getString("Audio.audioSettings"));
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
        }
    }

    public class ConnectAudioAction
    extends AbstractAction {
        private AudioBean bean;

        public ConnectAudioAction() {
            super(KvmPort.this.bundle.getString("Audio.connectAudio"));
            this.setConnectionState(false);
        }

        public void disconnectAudio() {
            if (KvmPort.this.audiocore != null) {
                KvmPort.this.audiocore.disconnect();
            }
            this.setConnectionState(false);
            KvmPort.this.audiocore.removeNotificationListener(KvmPort.this.audioAdapter);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
        }

        public AudioBean getAudioBean() {
            RemoteConsoleParameters remoteConsoleParameters = KvmPort.this.getView().getRemoteConsoleParameters();
            if (this.bean == null) {
                this.bean = new AudioBean(remoteConsoleParameters.host, remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, remoteConsoleParameters.username, remoteConsoleParameters.password, remoteConsoleParameters.rdmSession, remoteConsoleParameters.proxyConnectionIdVM, remoteConsoleParameters.proxyUseSSL, remoteConsoleParameters.ericKey, KvmPort.this.getView().getAudioErrorsAndMessagesHandler());
            }
            this.bean.setRfbSessionId(KvmPort.this.getView().getRfbSessionId());
            int n = -1;
            int n2 = -1;
            if (KvmPort.this.selectCardReaderAction.smartCardBean != null) {
                n = KvmPort.this.selectCardReaderAction.smartCardBean.getMsindex();
            }
            if (KvmPort.this.audioIndexHelper.isAudioAvailable()) {
                n2 = KvmPort.this.audioIndexHelper.getAudioIndex(KvmPort.this.isLocalMSConnected(), KvmPort.this.isCdIsoMSConnected(), KvmPort.this.getView().getSmartCardStatusListener().isConnected(), KvmPort.this.getLocalMsIndex(), KvmPort.this.getCdIsoMsIndex(), n);
            }
            this.bean.setMsindex(n2);
            this.bean.setPortId(KvmPort.this.getUniquePortId());
            return this.bean;
        }

        public void setConnectionState(boolean bl) {
            KvmPort.this.audioConnected = bl;
            this.putValue("connected", bl);
            if (KvmPort.this.scrContext != null) {
                ((RRCScreenContext)KvmPort.this.scrContext).getAudioObserver().forceNotifyObservers(null);
            }
            KvmPort.this.smartCardAction.evaluateEnabled();
            if (!bl) {
                this.bean = null;
            }
        }

        public boolean isConnected() {
            return KvmPort.this.audioConnected;
        }
    }

    public class AudioAction
    extends AdditionalVmAction {
        public AudioAction() {
            super(KvmPort.this.bundle.getString("Audio.name"));
            this.setEnabled(false);
        }

        private void setAudioAvailable() {
            this.evaluateEnabled();
        }

        private boolean isBothVMMounted() {
            int n = 0;
            List list = KvmPort.this.vmConfigInfo.getListofInterfaces();
            for (VMInterfaceInfo vMInterfaceInfo : list) {
                if (!vMInterfaceInfo.isInUse()) continue;
                ++n;
            }
            if (KvmPort.this.getView().getSmartCardStatusListener().isConnected()) {
                ++n;
            }
            return n >= 2;
        }

        @Override
        public void evaluateEnabled() {
            boolean bl = KvmPort.this.getPortType().equals("VM");
            boolean bl2 = KvmPort.this.license == null ? true : KvmPort.this.license.isFeatureLicensed("audio");
            this.setEnabled(Platform.isAudioSupported() && this.isSwitchCompleted() && KvmPort.this.audiocore != null && bl2 && AudioDevice.audioDevicesAvailable() && bl && KvmPort.this.audioIndexHelper.isAudioAvailable() && KvmPort.this.portPermissionHelper.getKvmPermission() == IKvmPort.KvmPermission.CONTROL && !this.isBothVMMounted() && KvmPort.this.isTopLeft());
            if (this.isSwitchCompleted() && !this.isEnabled()) {
                String string = "";
                if (!bl2) {
                    string = KvmPort.this.bundle.getString("AudioTooltip.License");
                } else if (!Platform.isAudioSupported()) {
                    string = KvmPort.this.bundle.getString("AudioTooltip.RequiredOs");
                } else if (!AudioDevice.audioDevicesAvailable()) {
                    string = KvmPort.this.bundle.getString("AudioTooltip.NoDevices");
                } else if (!bl || !KvmPort.this.audioIndexHelper.isAudioAvailable()) {
                    String string2 = MessageFormat.format(KvmPort.this.bundle.getString("AudioTooltip.NotAVmCim"), KvmPort.this.getViewName());
                    String string3 = KvmPort.this.bundle.getString("CommonTooltip.ContactSysadmin");
                    string = string2 + "\n" + string3;
                } else if (KvmPort.this.portPermissionHelper.getKvmPermission() != IKvmPort.KvmPermission.CONTROL) {
                    String string4 = MessageFormat.format(KvmPort.this.bundle.getString("AudioTooltip.Denied"), KvmPort.this.getViewName());
                    String string5 = KvmPort.this.bundle.getString("CommonTooltip.ContactSysadmin");
                    string = string4 + "\n" + string5;
                } else if (this.isBothVMMounted()) {
                    string = KvmPort.this.bundle.getString("AudioTooltip.UsbResources");
                } else if (!KvmPort.this.isTopLeft()) {
                    string = KvmPort.this.bundle.getString("AudioTooltip.NotTopLeft");
                }
                RRCLogger.log(300, "AudioMenu is disabled because of - " + string);
                this.putValue("ShortDescription", "<html>" + string.replace("\n", "<br>") + "</html>");
            } else {
                this.putValue("ShortDescription", null);
            }
        }
    }

    public class SelectCardReaderAction
    extends AbstractAction {
        private SmartCardBean smartCardBean;

        public SelectCardReaderAction() {
            super(KvmPort.this.bundle.getString("SmartCardSelectCardReader.name"));
            this.setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
        }

        public SmartCardBean getConfiguredSmartCardBean() {
            RemoteConsoleParameters remoteConsoleParameters = KvmPort.this.getView().getRemoteConsoleParameters();
            if (this.smartCardBean == null) {
                this.smartCardBean = new SmartCardBean(remoteConsoleParameters.host, remoteConsoleParameters.tcpPort, true, remoteConsoleParameters.rdmSession, remoteConsoleParameters.ericKey, KvmPort.this.getView().getSmartCardErrorsAndMessagesHandler(), WorkstationUnlockDetectorFactory.getWorkstationUnlockDetector());
                DeviceView.SmartCardStatusListeners smartCardStatusListeners = KvmPort.this.getView().getSmartCardStatusListener();
                this.smartCardBean.addPropertyChangeListener("CARD_READER_MOUNTED", smartCardStatusListeners);
                this.smartCardBean.addPropertyChangeListener("DISCONNECTED", smartCardStatusListeners);
                this.smartCardBean.addPropertyChangeListener("QUIT_NOTIFICATION", smartCardStatusListeners);
                this.smartCardBean.addPropertyChangeListener("NOTIFICATION_FROM_SERVER", smartCardStatusListeners);
                this.smartCardBean.addPropertyChangeListener("NO_PROTO_SPPORTED", smartCardStatusListeners);
                this.smartCardBean.addPropertyChangeListener("CANCEL_ISSUED", smartCardStatusListeners);
                this.smartCardBean.addPropertyChangeListener("QUIT_ISSUED", smartCardStatusListeners);
            }
            int n = -1;
            int n2 = -1;
            if (KvmPort.this.connectAudioAction.bean != null) {
                n2 = KvmPort.this.connectAudioAction.bean.getMsindex();
            }
            if (KvmPort.this.smartCardIndexHelper.isSmartCardAvailable()) {
                n = KvmPort.this.smartCardIndexHelper.getSmartCardIndex(KvmPort.this.isLocalMSConnected(), KvmPort.this.isCdIsoMSConnected(), KvmPort.this.audioConnected, KvmPort.this.getLocalMsIndex(), KvmPort.this.getCdIsoMsIndex(), n2);
            }
            this.smartCardBean.setMsindex(n);
            this.smartCardBean.setRfbSessionId(KvmPort.this.getView().getRfbSessionId());
            this.smartCardBean.setTag(KvmPort.this.getViewName());
            return this.smartCardBean;
        }

        public SmartCardBean getSmartCardBean() {
            return this.smartCardBean;
        }

        public void resetSmartCardBean() {
            this.smartCardBean = null;
        }
    }

    public class SmartCardAction
    extends AdditionalVmAction {
        public SmartCardAction() {
            super(KvmPort.this.bundle.getString("SmartCard.name"));
            this.setEnabled(false);
        }

        public void setSmartCardAvailable() {
            this.evaluateEnabled();
        }

        private boolean isBothVMMounted() {
            int n = 0;
            List list = KvmPort.this.vmConfigInfo.getListofInterfaces();
            for (VMInterfaceInfo vMInterfaceInfo : list) {
                if (!vMInterfaceInfo.isInUse()) continue;
                ++n;
            }
            if (KvmPort.this.audioConnected) {
                ++n;
            }
            return n >= 2;
        }

        @Override
        public void evaluateEnabled() {
            boolean bl;
            boolean bl2 = bl = KvmPort.this.license == null ? true : KvmPort.this.license.isFeatureLicensed("smartcard");
            assert (SwingUtilities.isEventDispatchThread());
            if (!this.isSwitchCompleted()) {
                return;
            }
            SmartCardCore smartCardCore = ((RRCScreenContext)KvmPort.this.scrContext).getSmartCardCore();
            SmartCardInitException.ExceptionCause exceptionCause = ((RRCScreenContext)KvmPort.this.scrContext).getSmartCardExceptionCause();
            boolean bl3 = KvmPort.this.getPortType().equals("VM");
            this.setEnabled(this.isSwitchCompleted() && smartCardCore != null && bl && bl3 && KvmPort.this.smartCardIndexHelper.isSmartCardAvailable() && KvmPort.this.portPermissionHelper.getKvmPermission() == IKvmPort.KvmPermission.CONTROL && this.usersCount == 1 && !this.isBothVMMounted() && KvmPort.this.isTopLeft());
            if (this.isSwitchCompleted() && !this.isEnabled()) {
                String string = "";
                if (!bl) {
                    string = KvmPort.this.bundle.getString("SmartCardTooltip.License");
                } else if (smartCardCore == null) {
                    switch (exceptionCause) {
                        case UNSUPPORTED_JRE: {
                            string = KvmPort.this.bundle.getString("SmartCardTooltip.RequiredJvm");
                            break;
                        }
                        case NO_SUPPORTED_SMARTCARD_IMPL: {
                            string = KvmPort.this.bundle.getString("SmartCardTooltip.RequiredEnvironment");
                            break;
                        }
                        case UNSUPPORTED_OS: {
                            string = KvmPort.this.bundle.getString("SmartCardTooltip.RequiredOs");
                            break;
                        }
                        case UNKNOWN_ERROR: {
                            string = KvmPort.this.bundle.getString("SmartCardTooltip.UnknownError") + "\n" + KvmPort.this.bundle.getString("CommonTooltip.ContactSysadmin");
                        }
                    }
                } else if (!bl3 || !KvmPort.this.smartCardIndexHelper.isSmartCardAvailable()) {
                    String string2 = MessageFormat.format(KvmPort.this.bundle.getString("SmartCardTooltip.NotAVmCim"), KvmPort.this.getViewName());
                    String string3 = KvmPort.this.bundle.getString("CommonTooltip.ContactSysadmin");
                    string = string2 + "\n" + string3;
                } else if (KvmPort.this.portPermissionHelper.getKvmPermission() != IKvmPort.KvmPermission.CONTROL) {
                    String string4 = MessageFormat.format(KvmPort.this.bundle.getString("SmartCardTooltip.Denied"), KvmPort.this.getViewName());
                    String string5 = KvmPort.this.bundle.getString("CommonTooltip.ContactSysadmin");
                    string = string4 + "\n" + string5;
                } else if (this.usersCount != 1) {
                    string = MessageFormat.format(KvmPort.this.bundle.getString("SmartCardTooltip.Exclusive"), KvmPort.this.getViewName());
                } else if (this.isBothVMMounted()) {
                    string = KvmPort.this.bundle.getString("SmartCardTooltip.UsbResources");
                } else if (!KvmPort.this.isTopLeft()) {
                    string = KvmPort.this.bundle.getString("SmartCardTooltip.NotTopLeft");
                }
                RRCLogger.log(300, "SmartCardMenu is disabled because of - " + string);
                this.putValue("ShortDescription", "<html>" + string.replace("\n", "<br>") + "</html>");
            } else {
                this.putValue("ShortDescription", null);
            }
        }
    }

    abstract class AdditionalVmAction
    extends AbstractAction {
        protected int usersCount;

        public AdditionalVmAction(String string) {
            super(string);
            this.usersCount = 1;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
        }

        public void setUsersCount(int n) {
            this.usersCount = n;
            this.evaluateEnabled();
        }

        public boolean isSwitchCompleted() {
            return KvmPort.this.isRfbSwitchCompleted();
        }

        public void setSwitchCompleted(boolean bl) {
            this.evaluateEnabled();
        }

        public abstract void evaluateEnabled();
    }
}

