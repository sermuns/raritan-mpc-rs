/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.KvmStream;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.PopulateKeyboardMenuCommand;
import com.raritan.rrc.ui.components.ContextPopupMenu;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.rrc.ui.models.NewVideoModeEvent;
import com.raritan.rrc.ui.models.NewVideoModeListener;
import com.raritan.rrc.ui.panes.DeviceViewAdapter;
import com.raritan.rrc.ui.panes.ICommandHandler;
import com.raritan.rrc.ui.panes.KvmPanel;
import com.raritan.rrc.ui.panes.KvmViewCommandHandler;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.AbstractUIManager;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandCheckMenuItem;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.util.kbd.KeyHIDValue;
import com.util.kbd.KeyboardKey;
import com.util.kbd.KeyboardMappings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyVetoException;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javaclientlib.clientlib.IKvmData;
import javaclientlib.tr.TRCMD_RESUME_VIDEO_STREAM_DATA;
import javaclientlib.tr.TRLIB_UPDATEINFO;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;
import javaclientlib.tr.TRSRVR_VIDEO_PARAMS;
import javaclientlib.utils.RRCLogger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import nn.pp.common.ResourceLoader;
import nn.pp.ext.pref.IApplicationPreferences;

public class KvmView
extends DeviceViewAdapter
implements IKvmData,
NewVideoModeListener,
FocusListener,
AdjustmentListener,
MouseListener {
    private static final long serialVersionUID = 5760851042261351136L;
    private static final int iAutoScrollBorderSize = 10;
    private boolean targetScreenResolutionFlag = false;
    private static final int MIN_MOUSE_MOVE_TIME = 100;
    private long lLastMouseTicks = 0L;
    private int iLastMouseX = 0;
    private int iLastMouseY = 0;
    private int iLastSendMouseX = 0;
    private int iLastSendMouseY = 0;
    private int iFixMouseX = 0;
    private int iFixMouseY = 0;
    private int iLastMouseDataX = 29000;
    private int iLastMouseDataY = 29000;
    private boolean scrollBarsSet = false;
    private boolean isSingleCursor = false;
    private KvmStream kvmStream;
    private KvmPanel kvmPanel;
    private KvmPort kvmPort;
    private RRCScreenContext scrContext;
    private boolean scaleVideoFlag = false;
    private javax.swing.Timer updateTimer;
    private int lastDataIn;
    private IPReach dev;
    private DeviceConnector devCon;
    private KvmResizer kvmResizer = new KvmResizer();
    private KvmKeyAdapter kvmKeyAdapter = new KvmKeyAdapter();
    private KvmMouseAdapter kvmMouseAdapter = new KvmMouseAdapter();
    private KvmMouseMotionAdapter kvmMouseMotionAdapter = new KvmMouseMotionAdapter();
    private KvmMouseWheelAdapter kvmMouseWheelAdapter = new KvmMouseWheelAdapter();
    private JScrollPane jsp;
    private JLabel northLabel;
    private JLabel southLabel;
    private JLabel westLabel;
    private JLabel eastLabel;
    private boolean mousebutton = true;
    private boolean videoFrozen = true;
    private JLabel southEastLabel;
    private JScrollBar verticalScrollBar;
    private JScrollBar horizontalScrollBar;
    private boolean adjustingVertical = false;
    private int adjustmentValue = 0;
    private javax.swing.Timer timer = null;
    private boolean altFlag = false;
    private boolean syncFlag = false;
    RaritanPropertyResourceBundle bundle;
    private ContextPopupMenu contextMenuKvm = null;
    private KvmViewCommandHandler kvmViewCommandHandler = new KvmViewCommandHandler();
    private boolean paused = false;
    boolean altReleased = false;
    boolean ctrlReleased = false;
    private Point oldPos = new Point(0, 0);
    private UpdateQueue updQ = new UpdateQueue();
    private javax.swing.Timer refreshTimer = null;
    private CPainter cPainter;
    int prcount = 0;

    public KvmView(boolean bl, ScreenContext screenContext, boolean bl2) {
        super((RRCScreenContext)screenContext);
        this.scrContext = (RRCScreenContext)screenContext;
        this.isNewPanel = bl2;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.contextMenuKvm = new ContextPopupMenu(this.scrContext, this);
        this.contextMenuKvm.setOpaque(false);
        this.contextMenuKvm.setVisible(false);
        this.enableInputMethods(false);
        this.setFocusTraversalKeysEnabled(false);
    }

    public void unfreezeVideo() {
        if (this.kvmPanel != null) {
            this.kvmPanel.setOsdMessage("");
        }
        this.videoFrozen = false;
    }

    public void freezeVideo() {
        this.videoFrozen = true;
    }

    public boolean isVideoFrozen() {
        return this.videoFrozen;
    }

    @Override
    public void setUpdateFrequency(long l) {
        if (this.refreshTimer != null) {
            this.refreshTimer.stop();
        }
        this.cPainter = new CPainter();
        this.refreshTimer = new javax.swing.Timer((int)l, this.cPainter);
        this.refreshTimer.start();
    }

    private void onMouseMove(int n, int n2, boolean bl) {
        int n3 = n;
        int n4 = n2;
        if (this.kvmPanel == null) {
            return;
        }
        Rectangle rectangle = this.kvmPanel.getBitmapLocation();
        if (rectangle == null) {
            return;
        }
        if (!rectangle.contains(n3, n4) && !bl) {
            return;
        }
        n4 -= rectangle.y;
        if ((n3 -= rectangle.x) < 0) {
            n3 = 0;
        } else if (n3 >= rectangle.width) {
            n3 = rectangle.width - 1;
        }
        if (n4 < 0) {
            n4 = 0;
        } else if (n4 >= rectangle.height) {
            n4 = rectangle.height - 1;
        }
        this.iLastMouseX = n3;
        this.iLastMouseY = n4;
        this.updateMousePosition(true);
    }

    private void onMouseButton(byte by, int n, int n2) {
        if (this.kvmPanel == null) {
            return;
        }
        this.ResumeVideo();
        this.kvmStream.sendMouseData(by, (short)n, (short)n2, (short)0);
        this.mousebutton = false;
    }

    private void sendFilteredMouseData(byte by, int n, int n2, int n3) {
        if (by == 6) {
            if (n == this.iLastMouseDataX && n2 == this.iLastMouseDataY) {
                return;
            }
            this.iLastMouseDataX = n;
            this.iLastMouseDataY = n2;
            this.syncFlag = true;
        }
        this.ResumeVideo();
        this.kvmStream.sendMouseData(by, (short)n, (short)n2, (short)n3);
    }

    @Override
    public void sendCtrlAltDelete() {
        if (this.kvmStream.isConnected()) {
            this.kvmStream.setKeyState((short)225, true, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)225, false, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)229, true, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)229, false, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)224, true, false);
            this.kvmStream.setKeyState((short)226, true, false);
            this.kvmStream.setKeyState((short)76, true, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)76, false, false);
            this.kvmStream.setKeyState((short)226, false, false);
            this.kvmStream.setKeyState((short)224, false, false);
            this.kvmStream.flushKeyData();
        } else {
            this.scrContext.getLogger().logTextDebug("Cannot send ctrl+alt+delete kvm stream not connected ");
        }
    }

    @Override
    public void sendAltTab() {
        if (this.kvmStream.isConnected()) {
            this.kvmStream.setKeyState((short)225, true, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)225, false, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)229, true, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)229, false, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)226, true, false);
            this.kvmStream.setKeyState((short)43, true, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)226, false, false);
            this.kvmStream.setKeyState((short)43, false, false);
            this.kvmStream.flushKeyData();
        } else {
            this.scrContext.getLogger().logTextDebug("Cannot send LeftAlt+Tab kvm stream not connected ");
        }
    }

    @Override
    public void sendCtrlNumlock() {
        if (this.kvmStream.isConnected()) {
            this.kvmStream.setKeyState((short)225, true, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)225, false, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)229, true, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)229, false, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)224, true, false);
            this.kvmStream.setKeyState((short)83, true, false);
            this.kvmStream.flushKeyData();
            this.kvmStream.setKeyState((short)83, false, false);
            this.kvmStream.setKeyState((short)224, false, false);
            this.kvmStream.flushKeyData();
        } else {
            this.scrContext.getLogger().logTextDebug("Cannot send ctrl+numlock kvm stream not connected ");
        }
    }

    @Override
    public void sendKVMPopupKey() {
        try {
            if (this.kvmStream.isConnected()) {
                this.kvmStream.setKeyState((short)224, true, false);
                this.kvmStream.setKeyState((short)226, true, false);
                this.kvmStream.flushKeyData();
                if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionA"))) {
                    this.kvmStream.setKeyState((short)4, true, false);
                    this.kvmStream.setKeyState((short)4, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionB"))) {
                    this.kvmStream.setKeyState((short)5, true, false);
                    this.kvmStream.setKeyState((short)5, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionC"))) {
                    this.kvmStream.setKeyState((short)6, true, false);
                    this.kvmStream.setKeyState((short)6, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionD"))) {
                    this.kvmStream.setKeyState((short)7, true, false);
                    this.kvmStream.setKeyState((short)7, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionE"))) {
                    this.kvmStream.setKeyState((short)8, true, false);
                    this.kvmStream.setKeyState((short)8, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionF"))) {
                    this.kvmStream.setKeyState((short)9, true, false);
                    this.kvmStream.setKeyState((short)9, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionG"))) {
                    this.kvmStream.setKeyState((short)10, true, false);
                    this.kvmStream.setKeyState((short)10, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionH"))) {
                    this.kvmStream.setKeyState((short)11, true, false);
                    this.kvmStream.setKeyState((short)11, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionI"))) {
                    this.kvmStream.setKeyState((short)12, true, false);
                    this.kvmStream.setKeyState((short)12, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionJ"))) {
                    this.kvmStream.setKeyState((short)13, true, false);
                    this.kvmStream.setKeyState((short)13, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionK"))) {
                    this.kvmStream.setKeyState((short)14, true, false);
                    this.kvmStream.setKeyState((short)14, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionL"))) {
                    this.kvmStream.setKeyState((short)15, true, false);
                    this.kvmStream.setKeyState((short)15, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionM"))) {
                    this.kvmStream.setKeyState((short)16, true, false);
                    this.kvmStream.setKeyState((short)16, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionN"))) {
                    this.kvmStream.setKeyState((short)17, true, false);
                    this.kvmStream.setKeyState((short)17, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionO"))) {
                    this.kvmStream.setKeyState((short)18, true, false);
                    this.kvmStream.setKeyState((short)18, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionP"))) {
                    this.kvmStream.setKeyState((short)19, true, false);
                    this.kvmStream.setKeyState((short)19, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionQ"))) {
                    this.kvmStream.setKeyState((short)20, true, false);
                    this.kvmStream.setKeyState((short)20, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionR"))) {
                    this.kvmStream.setKeyState((short)21, true, false);
                    this.kvmStream.setKeyState((short)21, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionS"))) {
                    this.kvmStream.setKeyState((short)22, true, false);
                    this.kvmStream.setKeyState((short)22, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionT"))) {
                    this.kvmStream.setKeyState((short)23, true, false);
                    this.kvmStream.setKeyState((short)23, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionU"))) {
                    this.kvmStream.setKeyState((short)24, true, false);
                    this.kvmStream.setKeyState((short)24, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionV"))) {
                    this.kvmStream.setKeyState((short)25, true, false);
                    this.kvmStream.setKeyState((short)25, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionW"))) {
                    this.kvmStream.setKeyState((short)26, true, false);
                    this.kvmStream.setKeyState((short)26, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionX"))) {
                    this.kvmStream.setKeyState((short)27, true, false);
                    this.kvmStream.setKeyState((short)27, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionY"))) {
                    this.kvmStream.setKeyState((short)28, true, false);
                    this.kvmStream.setKeyState((short)28, false, false);
                } else if (this.scrContext.getAppSettings().getkeyboardMenuHotkey().equals(this.bundle.getString("KeyboardMenuHotkey.OptionZ"))) {
                    this.kvmStream.setKeyState((short)29, true, false);
                    this.kvmStream.setKeyState((short)29, false, false);
                }
                this.kvmStream.flushKeyData();
                this.kvmStream.setKeyState((short)226, false, false);
                this.kvmStream.setKeyState((short)224, false, false);
                this.kvmStream.flushKeyData();
            } else {
                this.scrContext.getLogger().logTextDebug("Cannot send ctrl+alt+m kvm stream not connected ");
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void enterOnScreenMenu() {
        if (this.kvmStream.isConnected()) {
            this.kvmStream.flushKeyData();
            int n = this.scrContext.getAppSettings().getOsuiHotKey();
            switch (n) {
                case 0: {
                    this.kvmStream.toogleKeyState((short)71);
                    this.kvmStream.toogleKeyState((short)71);
                    break;
                }
                case 1: {
                    this.kvmStream.toogleKeyState((short)83);
                    this.kvmStream.toogleKeyState((short)83);
                    break;
                }
                case 2: {
                    this.kvmStream.toogleKeyState((short)57);
                    this.kvmStream.toogleKeyState((short)57);
                    break;
                }
            }
            this.kvmStream.flushKeyData();
        } else {
            this.scrContext.getLogger().logTextDebug("Cannot send Enter OSUI key kvm stream not connected ");
        }
    }

    @Override
    public void exitOnScreenMenu() {
        try {
            if (this.kvmStream.isConnected()) {
                this.kvmStream.flushKeyData();
                this.kvmStream.toogleKeyState((short)41);
                this.kvmStream.flushKeyData();
            } else {
                this.scrContext.getLogger().logTextDebug("Cannot send escapeKey kvm stream not connected ");
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void synchronizeMouse(boolean bl) {
        if (this.kvmStream.isConnected()) {
            if (bl) {
                this.kvmStream.syncMouse(4);
            } else {
                this.kvmStream.syncMouse(8);
            }
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException interruptedException) {
                System.out.println("waiting in synchronizeMouse interrupted");
            }
            this.iLastSendMouseX = 0;
            this.iLastSendMouseY = 0;
        }
        this.syncFlag = false;
    }

    @Override
    public void refreshScreen() {
        if (this.kvmStream.isConnected()) {
            this.kvmStream.refreshVideo(0, false);
        }
    }

    @Override
    public void autoSenseVideo() {
        if (this.kvmStream.isConnected()) {
            this.kvmStream.refreshVideo(0, true);
        }
    }

    @Override
    public void calibrateColor() {
        if (this.kvmStream.isConnected()) {
            this.kvmStream.refreshVideo(1, false);
        }
    }

    @Override
    public void updateVideoSettings(TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS) {
        if (this.kvmStream.isConnected()) {
            this.kvmStream.setVideoParams(tRSRVR_VIDEO_PARAMS);
        }
    }

    public void updateMousePosition(boolean bl) {
        long l;
        if (this.kvmPanel == null) {
            return;
        }
        int n = this.kvmPanel.getPossiblyScaledXCoord(this.iLastMouseX);
        int n2 = this.kvmPanel.getPossiblyScaledYCoord(this.iLastMouseY);
        if ((n != this.iLastSendMouseX || n2 != this.iLastSendMouseY) && (l = System.currentTimeMillis()) - this.lLastMouseTicks >= 100L) {
            this.lLastMouseTicks = l;
            if (this.kvmPanel != null & ((KvmPort)this.port).isStreamConnected()) {
                this.sendFilteredMouseData((byte)6, (short)n, (short)n2, 0);
                this.iLastSendMouseX = n;
                this.iLastSendMouseY = n2;
            }
        }
    }

    @Override
    public void setCompParameters(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) {
        if (this.kvmStream.isConnected()) {
            ((IPReach)this.port.getDevice()).getConnectionInfo().setCompParams(tRSRVR_COMP_PARAMS);
            this.kvmStream.setCompParams(tRSRVR_COMP_PARAMS);
        }
    }

    @Override
    public ContextPopupMenu getContextPopupMenu() {
        return this.contextMenuKvm;
    }

    @Override
    public void notify(int n, int n2) {
        block2 : switch (n) {
            case 1031: {
                if (this.scrContext.getSelectedPort() != this.kvmPort) break;
                RRCStatusBar rRCStatusBar = (RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel();
                rRCStatusBar.setLEDState((n2 & 1) != 0, (n2 & 2) != 0, (n2 & 4) != 0);
                break;
            }
            case 0: {
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).showAutoSense(n2);
                if (n2 == -1) {
                    boolean bl = this.scrContext.getAppSettings().isDoAutoColorCal();
                    if (bl && this.dev.getColorCalibSpeed() < 3000 && this.kvmStream.isConnected()) {
                        this.kvmStream.refreshVideo(2, false);
                    }
                    this.kvmPort.getDevice().getHandler().finishAutoSensing(this.kvmPanel);
                    break;
                }
                this.freezeVideo();
                this.updQ.clear();
                break;
            }
            case 1: {
                if (this.kvmPort == null || !this.kvmPort.isConnected()) break;
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setConcurrentUsers(n2);
                this.kvmPort.setConcurrUsers(n2);
                break;
            }
            case 2: {
                if (n2 != 0) {
                    this.paused = true;
                    if (this.kvmPanel != null) {
                        this.kvmPanel.setOsdMessage("Video Paused");
                    }
                } else {
                    if (this.devCon != null && this.paused && this.devCon.getProtocolVersion() >= 30) {
                        TRCMD_RESUME_VIDEO_STREAM_DATA tRCMD_RESUME_VIDEO_STREAM_DATA = new TRCMD_RESUME_VIDEO_STREAM_DATA();
                        tRCMD_RESUME_VIDEO_STREAM_DATA.setDeviceID(this.kvmStream.getDeviceID());
                        this.devCon.sendTRCmdEx(tRCMD_RESUME_VIDEO_STREAM_DATA, true, null, 2);
                    }
                    this.paused = false;
                    if (this.kvmPanel != null) {
                        this.kvmPanel.setOsdMessage("");
                    }
                }
                if (this.kvmPanel == null) break;
                this.kvmPanel.repaint();
                break;
            }
            case 3: {
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setBandwidthUsage(0);
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setVideoSensingLabel(this.bundle.getString("StatusBar.noVideo.label"));
                break;
            }
            case 1007: {
                try {
                    switch (this.kvmPort.getDeviceConnector().getServerID().getSecurityFlags() & 0xF00) {
                        default: {
                            ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setSecurityLabel(0);
                            break block2;
                        }
                        case 1024: 
                        case 2048: 
                    }
                    ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setSecurityLabel(1);
                }
                catch (Exception exception) {}
                break;
            }
            case 6: {
                try {
                    CommonPopups.showInfoDialog("Info", this.bundle.getString("FG_RESET_MSG"), null, this.scrContext);
                }
                catch (Exception exception) {}
                break;
            }
        }
        if (this.kvmPanel != null) {
            this.kvmPanel.notify(n, n2);
        }
    }

    @Override
    public void ResumeVideo() {
        if (this.paused) {
            this.notify(2, 0);
            this.paused = false;
        }
    }

    @Override
    public void updateNotify(TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
        this.updQ.enqueue(tRLIB_UPDATEINFO);
    }

    @Override
    public void newVideoModeNotify(TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA) {
        this.updQ.clear();
        if (this.kvmPanel == null) {
            return;
        }
        this.kvmPanel.newVideoModeNotify(tRRSP_NEW_VIDEO_MODE_DATA);
        this.synchronizeMouse(true);
    }

    @Override
    public void setDefaultFocussedComponent() {
        if (this.kvmPanel == null) {
            return;
        }
        this.kvmPanel.requestFocusInWindow();
    }

    @Override
    public void newVideoModePerformed(final NewVideoModeEvent newVideoModeEvent) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                if (KvmView.this.kvmPanel != null) {
                    KvmView.this.kvmPanel.newVideoModeNotify(newVideoModeEvent.getVideoMode());
                }
            }
        });
    }

    @Override
    public void updateNotifyPerformed(final NewVideoModeEvent newVideoModeEvent) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                if (KvmView.this.kvmPanel != null) {
                    KvmView.this.kvmPanel.updateNotify(newVideoModeEvent.getUpdateData());
                }
            }
        });
    }

    @Override
    public boolean disconnect() {
        this.stopUpdateTimer();
        this.updQ.clear();
        this.contextMenuKvm.cleanup();
        if (this.kvmPanel != null) {
            this.kvmPanel.disconnect();
            this.kvmPanel = null;
            System.runFinalization();
            System.gc();
        }
        return true;
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.kvmPort = (KvmPort)commandContext.getCommandParameter("ports");
        if (this.kvmPort == null) {
            this.disconnect();
            return;
        }
        this.dev = (IPReach)this.kvmPort.getDevice();
        this.devCon = this.dev.getDeviceConnector();
        this.makeLayout();
        this.lastDataIn = this.devCon.getDataIn();
        this.updateSpeed();
        this.focusGained(new FocusEvent(this, 1004));
    }

    @Override
    public void makeLayout() {
        this.kvmPort.setView(this);
        this.kvmStream = (KvmStream)this.kvmPort.getStream();
        this.setLayout(new BorderLayout());
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.scrContext.getPanelMediator().getParent();
        this.kvmPanel = new KvmPanel(false, this.scrContext);
        this.kvmPanel.setView(this);
        this.kvmPanel.setSize(raritanDesktopPane.getSize());
        this.kvmPanel.setFocusable(true);
        this.jsp = new JScrollPane();
        this.jsp.setViewportView(this.kvmPanel);
        this.jsp.setHorizontalScrollBarPolicy(31);
        this.jsp.setVerticalScrollBarPolicy(21);
        this.horizontalScrollBar = new JScrollBar(0);
        this.verticalScrollBar = new JScrollBar(1);
        this.horizontalScrollBar.setVisible(false);
        this.verticalScrollBar.setVisible(false);
        this.southEastLabel = new JLabel(ResourceLoader.loadImageIcon(this.bundle.getString("Transparent.image")));
        this.southEastLabel.setBackground(Color.GRAY);
        this.southEastLabel.setPreferredSize(new Dimension(20, 20));
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add((Component)this.horizontalScrollBar, "Center");
        jPanel.add((Component)this.southEastLabel, "East");
        JPanel jPanel2 = this.createCenterPanel();
        this.setHorizontalKvmBorderVisible(false, 0);
        this.setVerticalKvmBorderVisible(false, 0);
        this.setSouthEastLabelVisible(false);
        this.add((Component)jPanel2, "Center");
        this.add((Component)jPanel, "South");
        this.add((Component)this.verticalScrollBar, "East");
        this.addComponentListener(this.kvmResizer);
        this.addKeyListener(this.kvmKeyAdapter);
        this.addFocusListener(this);
        this.jsp.addMouseListener(this.kvmMouseAdapter);
        this.jsp.addMouseMotionListener(this.kvmMouseMotionAdapter);
        this.jsp.addMouseWheelListener(this.kvmMouseWheelAdapter);
        this.horizontalScrollBar.addAdjustmentListener(this);
        this.verticalScrollBar.addAdjustmentListener(this);
        if (this.kvmPort.isConnected()) {
            Port port;
            boolean bl = ((KvmStream)this.kvmPort.getStream()).getCapsLockStatus();
            boolean bl2 = ((KvmStream)this.kvmPort.getStream()).getNumLockStatus();
            boolean bl3 = ((KvmStream)this.kvmPort.getStream()).getScrollLockStatus();
            ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).initKVMStatusBarLabels(bl, bl2, bl3);
            ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setBandwidthUsage(1);
            this.startUpdateTimer();
            IApplicationPreferences iApplicationPreferences = this.scrContext.getAppSettings();
            RRCScreenContext rRCScreenContext = this.scrContext;
            if (iApplicationPreferences.isAlwaysOpenSMM() && (port = rRCScreenContext.getSelectedPort()) != null && port.isConnected() && port instanceof KvmPort && !this.port.isMultiMonitorPort()) {
                System.out.println("ALL CHECKS PASS..open SMM");
                ((KvmPort)port).toggleSingleMouseCursor();
                rRCScreenContext.getMainScreenMediator().selectSingleMouseCursorMode();
            }
            if (iApplicationPreferences.isAlwaysOpenScaled()) {
                port = rRCScreenContext.getSelectedPort();
                if (port != null && port.isConnected() && port instanceof KvmPort) {
                    port.getView().setScaleVideoFlag(true);
                }
                for (CommandCheckMenuItem commandCheckMenuItem : rRCScreenContext.getMainScreenMediator().getCheckScaleVideoMenuItems()) {
                    commandCheckMenuItem.setSelected(true);
                }
                rRCScreenContext.getMainScreenMediator().getToolBarScaleVideoButton().setSelected(true);
            }
            if (iApplicationPreferences.isAlwaysOpenInFS()) {
                this.setTargetScreenResolution(true);
            }
        }
        this.kvmPort.initSingleCursorMode();
    }

    public void startUpdateTimer() {
        this.updateTimer = new javax.swing.Timer(250, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                long l;
                if (KvmView.this.isFocusOwner()) {
                    KvmView.this.updateMousePosition(false);
                    KvmView.this.updateSpeed();
                }
                if (KvmView.this.scrContext.getAppSettings().isAutoSyncMouse() && KvmView.this.syncFlag && KvmView.this.kvmPort.getKvmMouse().isStandardMouse() && (l = System.currentTimeMillis()) - KvmView.this.lLastMouseTicks > 15000L) {
                    KvmView.this.synchronizeMouse(true);
                }
            }
        });
        this.updateTimer.start();
    }

    public void stopUpdateTimer() {
        if (this.updateTimer != null && this.updateTimer.isRunning()) {
            this.updateTimer.stop();
            this.updateTimer = null;
        }
    }

    private void updateSpeed() {
        if (this.dev != null && this.devCon != null) {
            int n = this.dev.getConnectionInfo().getCompParams().getSpeed();
            int n2 = this.devCon.getDataIn();
            if (n2 >= this.lastDataIn) {
                int n3 = (n2 - this.lastDataIn) * 1000 / 250;
                if (n == 0) {
                    try {
                        n = this.kvmStream.getVideoMode().getCompParams().getSpeed();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (n != 0) {
                    if (n3 > 0) {
                        n3 = n3 * 8 / (n / 8) + 1;
                    }
                    if (n3 > 8) {
                        n3 = 8;
                    }
                    try {
                        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setBandwidthUsage(n3);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            this.lastDataIn = n2;
        }
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        if (this.getShellInternalFrame().isSelected()) {
            MPCUtil.clearUnfocusedMenus(this.scrContext);
            super.focusGained(focusEvent);
            DeviceTreeController.getInstance(this.scrContext).showBold();
            if (this.kvmStream != null) {
                boolean bl = this.kvmStream.getCapsLockStatus();
                boolean bl2 = this.kvmStream.getNumLockStatus();
                boolean bl3 = this.kvmStream.getScrollLockStatus();
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).initKVMStatusBarLabels(bl, bl2, bl3);
            }
            if (focusEvent.getOppositeComponent() != null) {
                for (CommandCheckMenuItem commandCheckMenuItem : this.scrContext.getMainScreenMediator().getCheckScaleVideoMenuItems()) {
                    commandCheckMenuItem.setSelected(this.scaleVideoFlag);
                }
                this.scrContext.getMainScreenMediator().getToolBarScaleVideoButton().setSelected(this.scaleVideoFlag);
            }
            if (this.mousebutton) {
                this.updateMousePosition(false);
            } else {
                this.mousebutton = true;
            }
            this.requestFocusInWindow();
        }
    }

    @Override
    public void focusLost(FocusEvent focusEvent) {
        super.focusLost(focusEvent);
        if (this.scrContext != null && this.scrContext.getPanelMediator() != null && this.scrContext.getPanelMediator().getStatusPanel() != null) {
            ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).clearStatusBarLabels();
        }
        if (this.kvmPanel != null) {
            this.kvmPanel.forceRepaint();
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
        int n;
        if (this.kvmPanel == null) {
            return;
        }
        double d = this.horizontalScrollBar.getValue();
        double d2 = this.verticalScrollBar.getValue();
        int n2 = this.kvmPanel.getVisibleRect().x;
        int n3 = this.kvmPanel.getVisibleRect().y;
        d = d / 90.0 * ((double)this.jsp.getHorizontalScrollBar().getMaximum() - this.kvmPanel.getVisibleRect().getWidth());
        d2 = d2 / 90.0 * ((double)this.jsp.getVerticalScrollBar().getMaximum() - this.kvmPanel.getVisibleRect().getHeight());
        if (n2 != (int)d) {
            this.jsp.getHorizontalScrollBar().setValue((int)d);
            this.iFixMouseX = n = this.kvmPanel.getVisibleRect().x;
            this.iLastMouseX = this.iLastMouseX + n - n2;
        }
        if (n3 != (int)d2) {
            this.jsp.getVerticalScrollBar().setValue((int)d2);
            this.iFixMouseY = n = this.kvmPanel.getVisibleRect().y;
            this.iLastMouseY = this.iLastMouseY + n - n3;
        }
        this.onMouseMove(this.iLastMouseX, this.iLastMouseY, false);
        if (this.kvmPanel != null) {
            this.kvmPanel.forceRepaint();
        }
    }

    private void sizeAutoScroll(int n) {
        Dimension dimension = new Dimension(n, n);
        this.northLabel.setPreferredSize(dimension);
        this.southLabel.setPreferredSize(dimension);
        this.eastLabel.setPreferredSize(dimension);
        this.westLabel.setPreferredSize(dimension);
    }

    private JPanel createCenterPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.setBackground(Color.GRAY);
        this.northLabel = new JLabel();
        this.southLabel = new JLabel();
        this.westLabel = new JLabel();
        this.eastLabel = new JLabel();
        this.sizeAutoScroll(10);
        this.northLabel.addMouseListener(this);
        this.southLabel.addMouseListener(this);
        this.westLabel.addMouseListener(this);
        this.eastLabel.addMouseListener(this);
        jPanel.add((Component)this.northLabel, "North");
        jPanel.add((Component)this.southLabel, "South");
        jPanel.add((Component)this.westLabel, "West");
        jPanel.add((Component)this.eastLabel, "East");
        jPanel.add((Component)this.jsp, "Center");
        return jPanel;
    }

    protected void setHorizontalScrollBarVisibility(boolean bl) {
        this.horizontalScrollBar.setVisible(bl);
    }

    protected void setVerticalScrollBarVisibility(boolean bl) {
        this.verticalScrollBar.setVisible(bl);
    }

    protected void setHorizontalKvmBorderVisible(boolean bl, int n) {
        int n2 = (n + 1) / 2;
        this.westLabel.setPreferredSize(new Dimension(n2, 10));
        this.eastLabel.setPreferredSize(new Dimension(n2, 10));
        this.westLabel.setVisible(bl);
        this.eastLabel.setVisible(bl);
    }

    protected void setVerticalKvmBorderVisible(boolean bl, int n) {
        int n2 = (n + 1) / 2;
        this.westLabel.setPreferredSize(new Dimension(10, n2));
        this.eastLabel.setPreferredSize(new Dimension(10, n2));
        this.northLabel.setVisible(bl);
        this.southLabel.setVisible(bl);
    }

    protected void setSouthEastLabelVisible(boolean bl) {
        this.southEastLabel.setVisible(bl);
    }

    protected Dimension fixSize(Dimension dimension) {
        int n = 0;
        int n2 = 0;
        if (this.horizontalScrollBar.isVisible()) {
            n2 = 20;
        }
        if (this.verticalScrollBar.isVisible()) {
            n = 20;
        }
        Dimension dimension2 = new Dimension((int)dimension.getWidth() - n, (int)dimension.getHeight() - n2);
        return dimension2;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        Object object = mouseEvent.getSource();
        if (object.equals(this.northLabel)) {
            this.adjustingVertical = true;
            this.adjustmentValue = -1;
        }
        if (object.equals(this.southLabel)) {
            this.adjustingVertical = true;
            this.adjustmentValue = 1;
        }
        if (object.equals(this.westLabel)) {
            this.adjustingVertical = false;
            this.adjustmentValue = -1;
        }
        if (object.equals(this.eastLabel)) {
            this.adjustingVertical = false;
            this.adjustmentValue = 1;
        }
        this.timer = new javax.swing.Timer(50, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                KvmView.this.adjustScrollBars();
            }
        });
        this.timer.start();
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        if (this.timer != null) {
            this.timer.stop();
            this.timer = null;
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    private void adjustScrollBars() {
        int n = 0;
        n = this.adjustingVertical ? this.verticalScrollBar.getValue() : this.horizontalScrollBar.getValue();
        if ((n += this.adjustmentValue) < 0) {
            n = 0;
        }
        if (this.adjustingVertical) {
            this.verticalScrollBar.setValue(n);
        } else {
            this.horizontalScrollBar.setValue(n);
        }
    }

    @Override
    public KvmPanel getKvmPanel() {
        return this.kvmPanel;
    }

    public Dimension getScrollPaneSize() {
        if (this.isTargetScreenResolution()) {
            return new Dimension((int)this.jsp.getViewport().getSize().getWidth() + this.verticalScrollBar.getSize().width, (int)this.jsp.getViewport().getSize().getHeight() + this.horizontalScrollBar.getSize().height);
        }
        return new Dimension((int)this.jsp.getSize().getWidth(), (int)this.jsp.getSize().getHeight());
    }

    @Override
    public boolean isScaleVideoFlag() {
        return this.scaleVideoFlag;
    }

    @Override
    public void setScaleVideoFlag(boolean bl) {
        this.scaleVideoFlag = bl;
        this.horizontalScrollBar.setValue(0);
        this.verticalScrollBar.setValue(0);
    }

    @Override
    public boolean isTargetScreenResolution() {
        return this.targetScreenResolutionFlag;
    }

    @Override
    public void setTargetScreenResolution(boolean bl) {
        if (this.kvmPort != null && this.kvmPort.isConnected()) {
            int n;
            this.iFixMouseX = 0;
            this.iFixMouseY = 0;
            this.jsp.getHorizontalScrollBar().setValue(0);
            this.jsp.getVerticalScrollBar().setValue(0);
            this.horizontalScrollBar.setValue(0);
            this.verticalScrollBar.setValue(0);
            if (bl) {
                n = 0;
            } else {
                this.altFlag = false;
                n = 10;
            }
            this.sizeAutoScroll(n);
            this.targetScreenResolutionFlag = bl;
            AbstractUIManager abstractUIManager = this.scrContext.getApplication();
            abstractUIManager.changeScreen(this.targetScreenResolutionFlag, this.getShellInternalFrame(), this.port, AbstractUIManager.FullScreenTarget.SINGLE);
            this.grabFocus();
            this.kvmPort.initSingleCursorMode();
        }
    }

    @Override
    public void toggleTargetScreenResolution() {
        this.setTargetScreenResolution(!this.isTargetScreenResolution());
    }

    public boolean isAltFlag() {
        return this.altFlag;
    }

    public int getILastSendMouseX() {
        return this.iLastSendMouseX;
    }

    public int getILastSendMouseY() {
        return this.iLastSendMouseY;
    }

    @Override
    public void setContextMenuKVMVisible(boolean bl) {
        if (bl) {
            this.contextMenuKvm.show(this, 10, 10);
        } else {
            this.contextMenuKvm.setVisible(false);
        }
    }

    @Override
    public boolean isSingleCursor() {
        return this.isSingleCursor;
    }

    @Override
    public void setSingleCursor(boolean bl) {
        this.isSingleCursor = bl;
        if (bl) {
            this.oldPos.setLocation(0, 0);
            this.iLastMouseX = 0;
            this.iLastMouseY = 0;
            this.updateMousePosition(true);
            this.resetMouse();
            this.kvmPanel.setCursor(this.scrContext.getApplication().getBlankCursor());
        } else {
            this.kvmPanel.setCursor(this.scrContext.getApplication().getDefaultCursor());
        }
    }

    public boolean positionedLeft(int n) {
        return n <= 0 && this.horizontalScrollBar != null && this.scrollBarsSet && (!this.horizontalScrollBar.isVisible() || this.horizontalScrollBar.getValue() == 0);
    }

    public boolean positionedRight(int n) {
        if (this.kvmPanel == null) {
            return false;
        }
        return this.horizontalScrollBar != null && this.scrollBarsSet && (!this.horizontalScrollBar.isVisible() || this.horizontalScrollBar.getValue() >= 90) && n >= this.kvmPanel.getBitmapRect().width - 1;
    }

    public boolean positionedTop(int n) {
        return n <= 0 && this.verticalScrollBar != null && this.scrollBarsSet && (!this.verticalScrollBar.isVisible() || this.verticalScrollBar.getValue() == 0);
    }

    public boolean positionedBottom(int n) {
        if (this.kvmPanel == null) {
            return false;
        }
        return this.verticalScrollBar != null && this.scrollBarsSet && (!this.verticalScrollBar.isVisible() || this.verticalScrollBar.getValue() >= 90) && n >= this.kvmPanel.getBitmapRect().height - 1;
    }

    public void knowScrolls(boolean bl) {
        this.scrollBarsSet = bl;
    }

    @Override
    public void removeListeners() {
        this.removeComponentListener(this.kvmResizer);
        this.removeKeyListener(this.kvmKeyAdapter);
        this.jsp.removeMouseListener(this.kvmMouseAdapter);
        this.jsp.removeMouseMotionListener(this.kvmMouseMotionAdapter);
        this.jsp.removeMouseWheelListener(this.kvmMouseWheelAdapter);
        this.refreshTimer.stop();
        this.refreshTimer.removeActionListener(this.cPainter);
        this.cPainter = null;
        this.refreshTimer = null;
        this.updQ = null;
        this.kvmKeyAdapter = null;
        this.kvmMouseAdapter = null;
        this.kvmMouseMotionAdapter = null;
        this.kvmMouseWheelAdapter = null;
        this.kvmResizer = null;
        this.jsp = null;
        this.kvmPanel = null;
    }

    @Override
    public void setViewFocus() {
        try {
            if (this.getShellInternalFrame().isSelected()) {
                this.grabFocus();
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    @Override
    public void forceRepaint() {
        this.kvmPanel.forceRepaint();
    }

    @Override
    public boolean macroMenuActionPerformed(String string) {
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        Port port = this.scrContext.getSelectedPort();
        if (port == null) {
            return false;
        }
        this.ResumeVideo();
        if (StringUtils.notNullOrEmpty(string) && port.isConnected()) {
            KeyboardMappings keyboardMappings = new KeyboardMappings();
            KvmStream kvmStream = (KvmStream)port.getStream();
            if (kvmStream.getKeyState((short)224)) {
                bl = true;
                kvmStream.setKeyState((short)224, false, true);
            }
            if (kvmStream.getKeyState((short)228)) {
                bl2 = true;
                kvmStream.setKeyState((short)228, false, true);
            }
            if (kvmStream.getKeyState((short)226)) {
                bl3 = true;
                kvmStream.setKeyState((short)226, false, true);
            }
            if (kvmStream.getKeyState((short)230)) {
                bl4 = true;
                kvmStream.setKeyState((short)230, false, true);
            }
            StringTokenizer stringTokenizer = new StringTokenizer(string, "&&");
            while (stringTokenizer.hasMoreTokens()) {
                String string2 = stringTokenizer.nextToken();
                String string3 = string2.substring(string2.lastIndexOf(" ") + 1);
                int n = Integer.parseInt(string3);
                KeyboardKey keyboardKey = keyboardMappings.getItem(n);
                if (keyboardKey == null) continue;
                short s = (short)keyboardKey.getKeyCode();
                if (string2.startsWith("p")) {
                    kvmStream.setKeyState(s, true, false);
                } else if (string2.startsWith("r")) {
                    kvmStream.setKeyState(s, false, false);
                }
                kvmStream.flushKeyData();
            }
            if (bl) {
                kvmStream.setKeyState((short)224, true, true);
            }
            if (bl2) {
                kvmStream.setKeyState((short)228, true, true);
            }
            if (bl3) {
                kvmStream.setKeyState((short)226, true, true);
            }
            if (bl4) {
                kvmStream.setKeyState((short)230, true, true);
            }
        }
        this.forceRepaint();
        return true;
    }

    @Override
    public ICommandHandler getCommandHandler() {
        return this.kvmViewCommandHandler;
    }

    public void resetMouse() {
        try {
            if (this.jsp != null) {
                Robot robot = new Robot();
                Point point = this.jsp.getLocationOnScreen();
                int n = (int)point.getX() + this.jsp.getViewport().getSize().width / 2;
                int n2 = (int)point.getY() + this.jsp.getViewport().getSize().height / 2;
                robot.mouseMove(n, n2);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public boolean isFullScreenMode() {
        return this.targetScreenResolutionFlag;
    }

    @Override
    public void setFullScreenMode(boolean bl) {
        this.targetScreenResolutionFlag = bl;
    }

    @Override
    public void addCustomMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.jsp.addMouseMotionListener(mouseMotionListener);
    }

    @Override
    public void removeCustomMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.jsp.removeMouseMotionListener(mouseMotionListener);
    }

    @Override
    public boolean getAbsoluteMouseSupported() {
        this.scrContext.getMainScreenMediator().selectAbsoluteMouseModeView(this.kvmPort.getKvmMouse().isAbsoluteMouse());
        return this.kvmPort.getKvmMouse().enableAbsoluteMouse();
    }

    @Override
    public boolean getIntelligentMouseSupported() {
        this.scrContext.getMainScreenMediator().selectIntelligentMouseModeView(this.kvmPort.getKvmMouse().isIntelligentMouse());
        return this.kvmPort.getKvmMouse().enableIntelligentMouse();
    }

    @Override
    public boolean getStandardMouseSupported() {
        this.scrContext.getMainScreenMediator().selectStandardMouseModeView(this.kvmPort.getKvmMouse().isStandardMouse());
        return this.kvmPort.getKvmMouse().enableStandardMouse();
    }

    @Override
    public void startVideo() {
        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException interruptedException) {
                    RRCLogger.logException(interruptedException);
                }
                MPCUtil.notifyObservers(KvmView.this.scrContext, KvmView.this.kvmPort);
                KvmView.this.setViewFocus();
            }
        });
        thread.start();
    }

    @Override
    public boolean isSunTarget() {
        return this.kvmStream.isTargetType();
    }

    @Override
    public boolean sendKeyboardMacro(String string) {
        return this.macroMenuActionPerformed(string);
    }

    private class KvmMouseWheelAdapter
    implements MouseWheelListener {
        private KvmMouseWheelAdapter() {
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            mouseWheelEvent.consume();
            if (KvmView.this.port != null) {
                int n = mouseWheelEvent.getWheelRotation();
                int n2 = n > 0 ? 1 : -1;
                KvmView.this.ResumeVideo();
                KvmView.this.kvmStream.sendMouseData((byte)8, (short)0, (short)0, (short)n2);
            }
        }
    }

    private class KvmMouseMotionAdapter
    extends MouseMotionAdapter {
        private KvmMouseMotionAdapter() {
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            mouseEvent.consume();
            if (!KvmView.this.contextMenuKvm.isVisible() && KvmView.this.kvmPort.getView().hasFocus() && KvmView.this.port != null) {
                if (KvmView.this.isSingleCursor && (mouseEvent.getX() != ((KvmView)KvmView.this).jsp.getViewport().getSize().width / 2 || mouseEvent.getY() != ((KvmView)KvmView.this).jsp.getViewport().getSize().height / 2)) {
                    int n = ((KvmView)KvmView.this).jsp.getViewport().getSize().width / 2 - mouseEvent.getX();
                    int n2 = ((KvmView)KvmView.this).jsp.getViewport().getSize().height / 2 - mouseEvent.getY();
                    int n3 = (int)KvmView.this.oldPos.getX() - n;
                    int n4 = (int)KvmView.this.oldPos.getY() - n2;
                    KvmView.this.oldPos.setLocation(n3, n4);
                    KvmView.this.iLastMouseX = n3;
                    KvmView.this.iLastMouseY = n4;
                    KvmView.this.updateMousePosition(true);
                    KvmView.this.resetMouse();
                    return;
                }
                if (KvmView.this.isSingleCursor && mouseEvent.getX() == ((KvmView)KvmView.this).jsp.getViewport().getSize().width / 2 && mouseEvent.getY() == ((KvmView)KvmView.this).jsp.getViewport().getSize().height / 2) {
                    return;
                }
                KvmView.this.onMouseMove(mouseEvent.getX() + KvmView.this.iFixMouseX, mouseEvent.getY() + KvmView.this.iFixMouseY, KvmView.this.kvmPort != null && KvmView.this.kvmPort.getKvmMouse().isStandardMouse());
            }
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            mouseEvent.consume();
            if (KvmView.this.kvmPanel == null) {
                return;
            }
            if (!KvmView.this.contextMenuKvm.isVisible() && KvmView.this.kvmPort.getView().hasFocus() && KvmView.this.port != null && mouseEvent.getModifiersEx() == 1024) {
                if (KvmView.this.isSingleCursor && (mouseEvent.getX() != ((KvmView)KvmView.this).jsp.getViewport().getSize().width / 2 || mouseEvent.getY() != ((KvmView)KvmView.this).jsp.getViewport().getSize().height / 2)) {
                    int n = ((KvmView)KvmView.this).jsp.getViewport().getSize().width / 2 - mouseEvent.getX();
                    int n2 = ((KvmView)KvmView.this).jsp.getViewport().getSize().height / 2 - mouseEvent.getY();
                    int n3 = (int)KvmView.this.oldPos.getX() - n;
                    int n4 = (int)KvmView.this.oldPos.getY() - n2;
                    KvmView.this.oldPos.setLocation(n3, n4);
                    KvmView.this.iLastMouseX = n3;
                    KvmView.this.iLastMouseY = n4;
                    KvmView.this.updateMousePosition(true);
                    KvmView.this.resetMouse();
                    return;
                }
                if (KvmView.this.isSingleCursor && mouseEvent.getX() == ((KvmView)KvmView.this).jsp.getViewport().getSize().width / 2 && mouseEvent.getY() == ((KvmView)KvmView.this).jsp.getViewport().getSize().height / 2) {
                    return;
                }
                KvmView.this.onMouseMove(mouseEvent.getX() + KvmView.this.iFixMouseX, mouseEvent.getY() + KvmView.this.iFixMouseY, KvmView.this.kvmPort != null && KvmView.this.kvmPort.getKvmMouse().isStandardMouse());
            }
        }
    }

    private class KvmMouseAdapter
    extends MouseAdapter {
        private boolean mouseIn = false;

        private KvmMouseAdapter() {
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            mouseEvent.consume();
            this.mouseIn = true;
            KvmView.this.updateMousePosition(false);
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            this.mouseIn = false;
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            mouseEvent.consume();
            if (KvmView.this.kvmPanel == null || !this.mouseIn) {
                return;
            }
            if (KvmView.this.port != null) {
                if (!KvmView.this.isSingleCursor) {
                    try {
                        KvmView.this.port.getView().getShellInternalFrame().setSelected(true);
                    }
                    catch (PropertyVetoException propertyVetoException) {
                        RRCLogger.logException(propertyVetoException);
                    }
                    int n = mouseEvent.getX();
                    int n2 = mouseEvent.getY();
                    Rectangle rectangle = KvmView.this.kvmPanel.getBitmapPlace();
                    if (!rectangle.contains(n, n2)) {
                        return;
                    }
                    n2 -= rectangle.y;
                    if ((n -= rectangle.x) < 0) {
                        n = 0;
                    }
                    if (n2 < 0) {
                        n2 = 0;
                    }
                    int n3 = mouseEvent.getModifiersEx();
                    if (KvmView.this.kvmStream.getKeyState(KeyHIDValue.HIDMAP[18]) && (n3 & 0x200) == 0) {
                        KvmView.this.kvmStream.setKeyState(KeyHIDValue.HIDMAP[18], false, true);
                    }
                    if (KvmView.this.kvmStream.getKeyState(KeyHIDValue.HIDMAP[17]) && (n3 & 0x80) == 0) {
                        KvmView.this.kvmStream.setKeyState(KeyHIDValue.HIDMAP[17], false, true);
                    }
                    if (KvmView.this.kvmStream.getKeyState(KeyHIDValue.HIDMAP[16]) && (n3 & 0x40) == 0) {
                        KvmView.this.kvmStream.setKeyState(KeyHIDValue.HIDMAP[16], false, true);
                    }
                    switch (mouseEvent.getButton()) {
                        case 1: {
                            KvmView.this.onMouseButton((byte)0, n + KvmView.this.iFixMouseX, n2 + KvmView.this.iFixMouseY);
                            RRCLogger.log(200, 512, "KVMView: Left mouse button pressed.");
                            break;
                        }
                        case 2: {
                            KvmView.this.onMouseButton((byte)4, n + KvmView.this.iFixMouseX, n2 + KvmView.this.iFixMouseY);
                            RRCLogger.log(200, 512, "KVMView: Middle mouse button pressed.");
                            break;
                        }
                        case 3: {
                            KvmView.this.onMouseButton((byte)2, n + KvmView.this.iFixMouseX, n2 + KvmView.this.iFixMouseY);
                            RRCLogger.log(200, 512, "KVMView: Right mouse button pressed.");
                        }
                    }
                } else {
                    int n = ((KvmView)KvmView.this).jsp.getViewport().getSize().width / 2 - mouseEvent.getX();
                    int n4 = ((KvmView)KvmView.this).jsp.getViewport().getSize().height / 2 - mouseEvent.getY();
                    int n5 = (int)KvmView.this.oldPos.getX() - n;
                    int n6 = (int)KvmView.this.oldPos.getY() - n4;
                    KvmView.this.oldPos.setLocation(n5, n6);
                    switch (mouseEvent.getButton()) {
                        case 1: {
                            KvmView.this.onMouseButton((byte)0, n5, n6);
                            RRCLogger.log(200, 512, "KVMView: Left mouse button pressed.");
                            break;
                        }
                        case 2: {
                            KvmView.this.onMouseButton((byte)4, n5, n6);
                            RRCLogger.log(200, 512, "KVMView: Middle mouse button pressed.");
                            break;
                        }
                        case 3: {
                            KvmView.this.onMouseButton((byte)2, n5, n6);
                            RRCLogger.log(200, 512, "KVMView: Right mouse button pressed.");
                        }
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            mouseEvent.consume();
            if (!this.mouseIn) {
                return;
            }
            if (KvmView.this.port != null) {
                if (!KvmView.this.isSingleCursor) {
                    int n = mouseEvent.getX();
                    int n2 = mouseEvent.getY();
                    Rectangle rectangle = KvmView.this.kvmPanel.getBitmapPlace();
                    if (!rectangle.contains(n, n2)) {
                        return;
                    }
                    n2 -= rectangle.y;
                    if ((n -= rectangle.x) < 0) {
                        n = 0;
                    }
                    if (n2 < 0) {
                        n2 = 0;
                    }
                    switch (mouseEvent.getButton()) {
                        case 1: {
                            KvmView.this.onMouseButton((byte)1, n + KvmView.this.iFixMouseX, n2 + KvmView.this.iFixMouseY);
                            RRCLogger.log(200, 512, "KVMView: Left mouse button released.");
                            break;
                        }
                        case 2: {
                            KvmView.this.onMouseButton((byte)5, n + KvmView.this.iFixMouseX, n2 + KvmView.this.iFixMouseY);
                            RRCLogger.log(200, 512, "KVMView: Middle mouse button released.");
                            break;
                        }
                        case 3: {
                            KvmView.this.onMouseButton((byte)3, n + KvmView.this.iFixMouseX, n2 + KvmView.this.iFixMouseY);
                            RRCLogger.log(200, 512, "KVMView: Right mouse button released.");
                        }
                    }
                } else {
                    int n = ((KvmView)KvmView.this).jsp.getViewport().getSize().width / 2 - mouseEvent.getX();
                    int n3 = ((KvmView)KvmView.this).jsp.getViewport().getSize().height / 2 - mouseEvent.getY();
                    int n4 = (int)KvmView.this.oldPos.getX() - n;
                    int n5 = (int)KvmView.this.oldPos.getY() - n3;
                    KvmView.this.oldPos.setLocation(n4, n5);
                    switch (mouseEvent.getButton()) {
                        case 1: {
                            KvmView.this.onMouseButton((byte)1, n4, n5);
                            RRCLogger.log(200, 512, "KVMView: Left mouse button released.");
                            break;
                        }
                        case 2: {
                            KvmView.this.onMouseButton((byte)5, n4, n5);
                            RRCLogger.log(200, 512, "KVMView: Middle mouse button released.");
                            break;
                        }
                        case 3: {
                            KvmView.this.onMouseButton((byte)3, n4, n5);
                            RRCLogger.log(200, 512, "KVMView: Right mouse button released.");
                        }
                    }
                }
            }
        }
    }

    private class KvmKeyAdapter
    implements KeyListener {
        private boolean isLeftAltDown = false;
        private boolean useKeyTyped = false;
        private boolean isAltGraphDown = false;
        short hidCode;
        int keyCode;
        int keyLoc;
        char keyChar;

        private KvmKeyAdapter() {
        }

        private int getNumpadKeys(int n) {
            switch (n) {
                case 38: {
                    n = 104;
                    break;
                }
                case 40: {
                    n = 98;
                    break;
                }
                case 37: {
                    n = 100;
                    break;
                }
                case 39: {
                    n = 102;
                    break;
                }
                case 12: 
                case 65368: {
                    if (OS.getCurrent() == OS.MAC) {
                        n = 144;
                        break;
                    }
                    n = 101;
                    break;
                }
                case 10: {
                    n |= 0x100;
                    break;
                }
                case 155: {
                    n = 96;
                    break;
                }
                case 127: {
                    n = 110;
                    break;
                }
                case 36: {
                    n = 103;
                    break;
                }
                case 35: {
                    n = 97;
                    break;
                }
                case 33: {
                    n = 105;
                    break;
                }
                case 34: {
                    n = 99;
                }
            }
            return n;
        }

        private KeyboardMacrosPreferences getMacro(int n) {
            if (n >= 48 && n <= 57) {
                return PopulateKeyboardMenuCommand.getMacro(n - 48);
            }
            return null;
        }

        private void processMacro(KeyboardMacrosPreferences keyboardMacrosPreferences) {
            StringTokenizer stringTokenizer = new StringTokenizer(keyboardMacrosPreferences.getMacroSequence(), "&&");
            while (stringTokenizer.hasMoreTokens()) {
                KeyboardMappings keyboardMappings = new KeyboardMappings();
                String string = stringTokenizer.nextToken();
                String string2 = string.substring(string.lastIndexOf(" ") + 1);
                int n = Integer.parseInt(string2);
                KeyboardKey keyboardKey = keyboardMappings.getItem(n);
                if (keyboardKey == null) continue;
                short s = (short)keyboardKey.getKeyCode();
                if (string.startsWith("p")) {
                    KvmView.this.kvmStream.setKeyState(s, true, false);
                } else if (string.startsWith("r")) {
                    KvmView.this.kvmStream.setKeyState(s, false, false);
                }
                KvmView.this.kvmStream.flushKeyData();
            }
        }

        private void keyHandling(KeyEvent keyEvent, boolean bl, boolean bl2) {
            Object object;
            if (KvmView.this.contextMenuKvm.isVisible() || !KvmView.this.kvmPort.getView().hasFocus()) {
                return;
            }
            this.keyCode = keyEvent.getKeyCode();
            this.keyLoc = keyEvent.getKeyLocation();
            this.keyChar = keyEvent.getKeyChar();
            keyEvent.consume();
            if (keyEvent.isAltDown()) {
                KvmView.this.altFlag = true;
                this.setAltGraphDownFlag(keyEvent);
            }
            switch (keyEvent.getKeyLocation()) {
                case 4: {
                    this.keyCode = this.getNumpadKeys(this.keyCode);
                    break;
                }
                case 3: {
                    this.keyCode = (this.keyCode | 0x100) & 0x1FF;
                }
            }
            if (keyEvent.isControlDown() && keyEvent.isAltDown() && bl && this.isLeftAltDown) {
                object = this.getMacro(this.keyCode);
                if (this.keyCode == KvmView.this.scrContext.getPopupKeyCode() || object != null) {
                    KvmView.this.kvmStream.releaseKeyIfPressed((short)224);
                    KvmView.this.kvmStream.flushKeyData();
                    KvmView.this.kvmStream.releaseKeyIfPressed((short)228);
                    KvmView.this.kvmStream.flushKeyData();
                    KvmView.this.kvmStream.releaseKeyIfPressed((short)226);
                    KvmView.this.kvmStream.flushKeyData();
                    KvmView.this.altFlag = false;
                    this.isLeftAltDown = false;
                    if (this.keyCode == KvmView.this.scrContext.getPopupKeyCode()) {
                        KvmView.this.setContextMenuKVMVisible(true);
                    } else {
                        this.processMacro((KeyboardMacrosPreferences)object);
                    }
                    return;
                }
            }
            if (this.isLeftAltDown && keyEvent.getKeyCode() == 18 && !bl) {
                KvmView.this.altReleased = true;
                this.isLeftAltDown = false;
            } else if (KvmView.this.ctrlReleased) {
                KvmView.this.ctrlReleased = false;
                KvmView.this.altReleased = false;
            } else if (keyEvent.getKeyCode() == 17 && !bl) {
                KvmView.this.ctrlReleased = true;
            } else if (KvmView.this.altReleased) {
                KvmView.this.ctrlReleased = false;
                KvmView.this.altReleased = false;
            }
            if (KvmView.this.altReleased && KvmView.this.ctrlReleased) {
                try {
                    object = new Timer(true);
                    ((Timer)object).schedule((TimerTask)new FlashMessage(), 0L, 5000L);
                    KvmView.this.altReleased = false;
                    KvmView.this.ctrlReleased = false;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (KvmView.this.port != null) {
                switch (this.keyCode & 0xFF00) {
                    case 61440: {
                        this.keyCode |= 0x1B0;
                    }
                    case 65280: {
                        this.keyCode &= 0x1FF;
                    }
                }
                switch (this.keyCode) {
                    case 0: 
                    case 92: {
                        switch (this.keyChar) {
                            case '\u00b2': {
                                this.keyCode = 192;
                                break;
                            }
                            case '?': 
                            case '\u00df': {
                                this.keyCode = 301;
                                break;
                            }
                            case '\u00dc': 
                            case '\u00fc': {
                                this.keyCode = 91;
                                break;
                            }
                            case '\u00d6': 
                            case '\u00f6': {
                                this.keyCode = 59;
                                break;
                            }
                            case '%': 
                            case '\u00c4': 
                            case '\u00e4': 
                            case '\u00f9': {
                                this.keyCode = 222;
                                break;
                            }
                            case '_': {
                                this.keyCode |= 0x100;
                                break;
                            }
                            case '\uffff': {
                                this.useKeyTyped = bl;
                            }
                        }
                        break;
                    }
                    case 243: 
                    case 244: {
                        bl = !bl;
                        break;
                    }
                    case 18: {
                        if (this.isAltGraphDown || keyEvent.getKeyLocation() != 2 || !bl) break;
                        this.isLeftAltDown = true;
                        break;
                    }
                    case 20: {
                        if (OS.getCurrent() != OS.SOLARIS && OS.getCurrent() != OS.MAC) break;
                        bl2 = true;
                        break;
                    }
                    case 144: {
                        if (OS.getCurrent() != OS.SOLARIS) break;
                        bl2 = true;
                        break;
                    }
                    case 192: {
                        if (OS.getCurrent() != OS.MAC) break;
                        switch (KeyHIDValue.keyboardLanguage) {
                            case 3: {
                                if (this.keyChar != '\u00b0') break;
                                this.keyCode = 130;
                                break;
                            }
                            case 4: {
                                switch (this.keyChar) {
                                    case '#': 
                                    case '0': 
                                    case '@': 
                                    case '\u0178': 
                                    case '\u2022': 
                                    case '\uffff': {
                                        this.keyCode = 153;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case 77: {
                        if (OS.getCurrent() != OS.MAC || KeyHIDValue.keyboardLanguage != 4) break;
                        switch (this.keyChar) {
                            case ',': 
                            case '?': 
                            case '\u00bf': 
                            case '\u221e': {
                                this.keyCode = 44;
                            }
                        }
                    }
                }
                if (KeyHIDValue.keyboardLanguage == 2 && this.keyCode == 274 && keyEvent.getModifiersEx() == 640) {
                    KvmView.this.kvmStream.setKeyState((short)224, false, false);
                }
                if (this.keyCode >= KeyHIDValue.HIDMAP.length) {
                    return;
                }
                this.hidCode = KeyHIDValue.HIDMAP[this.keyCode];
                if (this.hidCode > 0) {
                    if (bl2) {
                        KvmView.this.kvmStream.setKeyState(this.hidCode, true, false);
                        KvmView.this.kvmStream.setKeyState(this.hidCode, false, false);
                    } else {
                        KvmView.this.kvmStream.setKeyState(this.hidCode, bl, false);
                    }
                    KvmView.this.ResumeVideo();
                    KvmView.this.kvmStream.flushKeyData();
                }
            } else if (!bl) {
                KvmView.this.altFlag = false;
            }
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            this.keyHandling(keyEvent, true, false);
            if (KvmView.this.contextMenuKvm.isVisible()) {
                return;
            }
        }

        @Override
        public void keyTyped(KeyEvent keyEvent) {
            keyEvent.consume();
            this.keyChar = keyEvent.getKeyChar();
            if (this.useKeyTyped) {
                switch (this.keyChar) {
                    case '\\': {
                        this.keyCode = 301;
                    }
                }
                KeyEvent keyEvent2 = new KeyEvent(keyEvent.getComponent(), 401, keyEvent.getWhen(), keyEvent.getModifiers(), this.keyCode, this.keyChar);
                this.keyHandling(keyEvent2, true, true);
                this.useKeyTyped = false;
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            this.keyHandling(keyEvent, false, false);
        }

        private void setAltGraphDownFlag(KeyEvent keyEvent) {
            if (keyEvent.isControlDown() && keyEvent.isAltDown() && keyEvent.getKeyLocation() == 3 && keyEvent.getID() == 401) {
                this.isAltGraphDown = true;
            } else if (keyEvent.getKeyLocation() == 3 && this.isAltGraphDown && keyEvent.getID() == 402) {
                this.isAltGraphDown = false;
            }
        }
    }

    class FlashMessage
    extends TimerTask {
        int num = 1;

        @Override
        public void run() {
            if (this.num > 0) {
                String string = KvmView.this.bundle.getString("Optiondialog.KeyboardShortcutMenuHotkey");
                KvmView.this.kvmPanel.setPopupMessage(string + " : Ctrl+Alt+" + RRCScreenContext.getKvmPopupKey());
                --this.num;
            } else {
                KvmView.this.kvmPanel.setPopupMessage("");
                this.cancel();
                KvmView.this.repaint();
            }
        }
    }

    private class KvmResizer
    extends ComponentAdapter {
        private KvmResizer() {
        }

        @Override
        public void componentResized(ComponentEvent componentEvent) {
            if (KvmView.this.kvmPanel != null && componentEvent.getID() == 101) {
                KvmView.this.kvmPanel.forceRepaint();
                KvmView.this.setViewFocus();
            }
        }

        @Override
        public void componentMoved(ComponentEvent componentEvent) {
            if (KvmView.this.kvmPanel == null) {
                return;
            }
            KvmView.this.kvmPanel.setSize(KvmView.this.getWidth(), KvmView.this.getHeight());
            KvmView.this.kvmPanel.forceRepaint();
        }
    }

    private class CPainter
    implements ActionListener {
        private CPainter() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            while (KvmView.this.kvmPanel != null) {
                CPainter cPainter = this;
                synchronized (cPainter) {
                    if (KvmView.this.kvmPanel != null) {
                        TRLIB_UPDATEINFO tRLIB_UPDATEINFO = KvmView.this.updQ.dequeue();
                        if (tRLIB_UPDATEINFO == null) {
                            return;
                        }
                        KvmView.this.kvmPanel.updateNotify(tRLIB_UPDATEINFO);
                    }
                }
            }
        }
    }

    private class UpdateQueue {
        private Vector list = new Vector(25);

        private UpdateQueue() {
        }

        public synchronized TRLIB_UPDATEINFO dequeue() {
            if (!KvmView.this.videoFrozen && !this.list.isEmpty()) {
                TRLIB_UPDATEINFO tRLIB_UPDATEINFO = (TRLIB_UPDATEINFO)this.list.remove(0);
                return tRLIB_UPDATEINFO;
            }
            return null;
        }

        public synchronized void enqueue(TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
            boolean bl;
            boolean bl2 = bl = this.list.size() > 0;
            while (bl) {
                bl = false;
                Rectangle rectangle = tRLIB_UPDATEINFO.getRectangle();
                Rectangle rectangle2 = new Rectangle(rectangle);
                Rectangle rectangle3 = new Rectangle(rectangle);
                --rectangle2.y;
                rectangle2.height += 2;
                --rectangle3.x;
                rectangle3.width += 2;
                for (int i = 0; !bl && i < this.list.size(); ++i) {
                    TRLIB_UPDATEINFO tRLIB_UPDATEINFO2 = (TRLIB_UPDATEINFO)this.list.get(i);
                    Rectangle rectangle4 = tRLIB_UPDATEINFO2.getRectangle();
                    if (!rectangle4.intersects(rectangle2) && !rectangle4.intersects(rectangle3)) continue;
                    Rectangle rectangle5 = rectangle4.union(rectangle);
                    tRLIB_UPDATEINFO.setRectangle(rectangle5);
                    this.list.remove(i);
                    bl = this.list.size() > 0;
                }
            }
            this.list.add(tRLIB_UPDATEINFO);
        }

        public synchronized void clear() {
            this.list.clear();
        }
    }
}

