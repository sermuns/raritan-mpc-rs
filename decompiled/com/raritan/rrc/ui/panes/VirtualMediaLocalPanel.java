/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.VMConfigInfo;
import com.raritan.rrc.data.VMInterfaceInfo;
import com.raritan.rrc.data.VirtualMediaLocalBean;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.ErrorHandlerImpl;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javaclientlib.utils.RRCLogger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.core.INotificationEvent;
import nn.pp.core.Platform;
import nn.pp.logging.RemoteConsoleLogger;
import nn.pp.rccore.IKvmPort;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.VMAdapter;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.VirtualMediaEventListener;

public class VirtualMediaLocalPanel
extends AbstractDisplay
implements ActionListener,
ItemListener {
    private static final long serialVersionUID = 2677565149168348926L;
    protected RaritanPropertyResourceBundle bundle;
    private String portKey;
    private String host;
    private JPanel generalPanel;
    private JCheckBox readWriteCheckBoxConn1;
    private boolean overrideReadWrite;
    private DefaultComboBoxModel availableDrivesModel1;
    private ComboBoxType availableDrivesCombo1;
    private JButton connectButton;
    private JButton cancelButton;
    private JPanel buttonPanel;
    private JPanel firstPanel;
    private JPanel mainPanel;
    private static final String PANEL_NAME = "vmpanel.LOCAL_PANEL_NAME";
    private static final String WARNING_MESSAGE = "vmpanel.WARNING_MESSAGE";
    private static final String POPUP_OPTION_YES = "vmpanel.POPUP_OPTION_YES";
    private static final String POPUP_OPTION_NO = "vmpanel.POPUP_OPTION_NO";
    private static final String PANEL_INFO_MESSAGE = "vmpanel.PANEL_INFO_MESSAGE";
    private static final String DRIVE_OR_HOST_NAME = "vmpanel.localdrive";
    private static final String READ_WRITE = "vmpanel.readwrite";
    private static final String CANCEL = "vmpanel.cancel";
    private static final String CONNECT = "vmpanel.connect";
    private static final String CONNECT_SUCEESS_MESSAGE = "vmpanel.CONNECT_SUCEESS_MESSAGE";
    private static final String CONNECT_SUCEESS_MESSAGE_LONG = "vmpanel.CONNECT_SUCEESS_MESSAGE_LONG";
    private static final String CONNECT_PROGRESS_TITLE = "vmpanel.CONNECT_PROGRESS_TITLE";
    private static final String CONNECT_PROGRESS_MESSAGE = "vmpanel.CONNECT_PROGRESS_MESSAGE";
    private boolean isDriveConnected = false;
    private String driveSelected;
    private JDialog vmConnectionInProgressMsgDialog = null;
    private VirtualMediaLocalBean currentVirtualMediaBean = null;
    private VMAdapter vmAdapter;
    private List<RedirectableObject> drives;

    @Override
    public void makeLayout() {
        try {
            RRCLogger.log(200, 1024, "Client Platform : " + OS.getCurrent());
            this.mainPanel = new JPanel(new SpringLayout());
            this.generalPanel = new JPanel(new SpringLayout());
            this.firstPanel = new JPanel(new SpringLayout());
            JPanel jPanel = new JPanel(new SpringLayout());
            JLabel jLabel = new JLabel(this.bundle.getString(PANEL_INFO_MESSAGE));
            jPanel.add(jLabel);
            SpringUtilities.makeCompactGrid(jPanel, 1, 1, 6, 1, 1, 1);
            JPanel jPanel2 = new JPanel(new SpringLayout());
            this.availableDrivesCombo1 = new ComboBoxType();
            this.availableDrivesCombo1.addItemListener(this);
            jPanel2.add(new JLabel(this.bundle.getString(DRIVE_OR_HOST_NAME)));
            jPanel2.add(this.availableDrivesCombo1);
            SpringUtilities.makeCompactGrid(jPanel2, 2, 1, 10, 1, 1, 1);
            JPanel jPanel3 = new JPanel(new SpringLayout());
            String string = this.bundle.getString(READ_WRITE);
            this.readWriteCheckBoxConn1 = new JCheckBox(string);
            this.readWriteCheckBoxConn1.setSelected(false);
            this.readWriteCheckBoxConn1.setEnabled(Platform.isVirtualMediaReadWriteSupported());
            this.readWriteCheckBoxConn1.addActionListener(this);
            jPanel3.add(this.readWriteCheckBoxConn1);
            SpringUtilities.makeCompactGrid(jPanel3, 1, 1, 7, 0, 0, 10);
            this.firstPanel.add(jPanel2);
            SpringUtilities.makeCompactGrid(this.firstPanel, 1, 1, 1, 1, 1, 1);
            this.generalPanel.add(this.firstPanel);
            this.generalPanel.add(jPanel3);
            SpringUtilities.makeCompactGrid(this.generalPanel, 2, 1, 1, 1, 1, 1);
            this.generalPanel.setBorder(BorderFactory.createEtchedBorder());
            jPanel.setBorder(BorderFactory.createEtchedBorder());
            this.mainPanel.add(jPanel);
            this.mainPanel.add(this.generalPanel);
            this.mainPanel.add(this.doButtonWidget());
            SpringUtilities.makeCompactGrid(this.mainPanel, 3, 1, 1, 1, 1, 1);
            this.add((Component)this.mainPanel, "Center");
            this.setVisible(true);
            this.cancelButton.enableInputMethods(false);
            this.connectButton.enableInputMethods(false);
            this.resetDrives();
            this.availableDrivesCombo1.addKeyListener(new KeyAdapter(){

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() != 33 || !keyEvent.isControlDown() || keyEvent.isAltDown()) {
                        // empty if block
                    }
                }
            });
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    public boolean populateVMPanel(VirtualMediaLocalBean virtualMediaLocalBean) {
        this.overrideReadWrite = false;
        this.drives = this.getAvailableDrives();
        if (this.drives.size() == 0) {
            return false;
        }
        try {
            KvmPort kvmPort;
            this.portKey = virtualMediaLocalBean.getPortKey();
            this.readWriteCheckBoxConn1.setSelected(false);
            this.isDriveConnected = false;
            if (virtualMediaLocalBean.getRFBView() != null && (kvmPort = virtualMediaLocalBean.getRFBView().getPort()) != null) {
                if (kvmPort.getPortPermissionHelper().getVmPermission() == IKvmPort.VmPermission.READWRITE && Platform.isVirtualMediaReadWriteSupported()) {
                    this.readWriteCheckBoxConn1.setSelected(false);
                    this.readWriteCheckBoxConn1.setEnabled(true);
                } else {
                    this.readWriteCheckBoxConn1.setSelected(false);
                    this.readWriteCheckBoxConn1.setEnabled(false);
                }
            }
            virtualMediaLocalBean.getRFBView().setVMLocalPanel(this);
            this.host = virtualMediaLocalBean.getRFBProfile().getRemoteHost();
            if (Platform.isVirtualMediaSupported()) {
                this.populateComboBoxes();
            }
            virtualMediaLocalBean.getVmCore().addNotificationListener(this.vmAdapter);
            virtualMediaLocalBean.getVmCore().addVirtualMediaEventListener(this.vmAdapter, 7);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            RRCLogger.log(200, 1024, "Unable to populate VM Panel using VMBean values");
            RRCLogger.logException(exception);
            return false;
        }
        return true;
    }

    public List<RedirectableObject> getAvailableDrives() {
        ArrayList<RedirectableObject> arrayList = new ArrayList<RedirectableObject>();
        List<RedirectableObject> list = RedirectableObject.getAvailableDrives(RRCLogger.getLogger());
        for (int i = 0; i < list.size(); ++i) {
            VMCore.DriveType driveType = VMCore.DriveType.UNKNOWN;
            try {
                driveType = list.get(i).getDriveType(RRCLogger.getLogger());
            }
            catch (IOException iOException) {
                RRCLogger.getLogger().log(Level.WARNING, "IO Exception trying to determine drive type of drive: " + list.get(i).getLongName(), iOException);
            }
            catch (VMException vMException) {
                RRCLogger.getLogger().log(Level.WARNING, "VM Exception trying to determine drive type of drive: " + list.get(i).getLongName(), vMException);
            }
            if (driveType != VMCore.DriveType.FLOPPY && driveType != VMCore.DriveType.REMOVABLE && driveType != VMCore.DriveType.HARD_DISK_PARTITION && driveType != VMCore.DriveType.HARD_DISK_PARTITION_EXTERNAL && driveType != VMCore.DriveType.UNKNOWN) continue;
            arrayList.add(list.get(i));
        }
        return arrayList;
    }

    private void populateComboBoxes() throws VMException, IOException {
        this.availableDrivesModel1 = new DefaultComboBoxModel();
        for (RedirectableObject redirectableObject : this.drives) {
            this.availableDrivesModel1.addElement(redirectableObject.getLongName());
        }
        this.availableDrivesCombo1.setModel(this.availableDrivesModel1);
        this.availableDrivesCombo1.setEnabled(this.availableDrivesCombo1.getItemCount() > 0);
        if (this.availableDrivesCombo1.isEnabled()) {
            this.availableDrivesCombo1.setSelectedIndex(0);
            Object object = this.drives.get(0).getDriveType(RRCLogger.getLogger());
            if (object != VMCore.DriveType.FLOPPY && object != VMCore.DriveType.REMOVABLE && object != VMCore.DriveType.HARD_DISK_PARTITION_EXTERNAL && !this.overrideReadWrite || !Platform.isVirtualMediaReadWriteSupported()) {
                this.readWriteCheckBoxConn1.setSelected(false);
                this.readWriteCheckBoxConn1.setEnabled(false);
            }
        }
    }

    public void populateVMPanelBean() {
        try {
            Map map = this.scrContext.getVirtualMediaLocalMap();
            VirtualMediaLocalBean virtualMediaLocalBean = (VirtualMediaLocalBean)map.get(this.portKey);
            map.remove(this.portKey);
            if ((VirtualMediaLocalBean)map.get(this.portKey) != null) {
                this.currentVirtualMediaBean = (VirtualMediaLocalBean)map.get(this.portKey);
            }
            virtualMediaLocalBean = this.currentVirtualMediaBean;
            virtualMediaLocalBean.setReadWriteCheckBoxSelected(this.readWriteCheckBoxConn1.isSelected());
            virtualMediaLocalBean.setDriveConnected(true);
            virtualMediaLocalBean.setConnectedDrive(this.driveSelected);
            map.put(this.portKey, virtualMediaLocalBean);
            this.scrContext.setVitualMediaLocalMap(map);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            RRCLogger.log(200, 1024, "Error populating Bean");
            RRCLogger.logException(exception);
        }
    }

    public VirtualMediaLocalPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        try {
            this.isDialog = bl;
            this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
            this.makeLayout();
            this.setShell(this.bundle.getString(PANEL_NAME));
            this.vmAdapter = new VMAdapter(){

                @Override
                public void disconnected(Exception exception) {
                    VirtualMediaLocalPanel.this.disconnected(true);
                }

                @Override
                public void receivedNotification(INotificationEvent iNotificationEvent) {
                    if (iNotificationEvent.getErrorCode() == 822214661) {
                        VirtualMediaLocalPanel.this.connected(822214661);
                        return;
                    }
                    ErrorHandlerImpl.getInstance().handleError(iNotificationEvent.getErrorCode(), VirtualMediaLocalPanel.this, VirtualMediaLocalPanel.this.currentVirtualMediaBean.getRFBView());
                    if (iNotificationEvent.isQuit() || iNotificationEvent.isError()) {
                        VMCore vMCore = null;
                        if (VirtualMediaLocalPanel.this.currentVirtualMediaBean != null) {
                            vMCore = VirtualMediaLocalPanel.this.currentVirtualMediaBean.getVmCore();
                        }
                        if (vMCore != null) {
                            vMCore.disconnect(true);
                        }
                        if (VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog != null && VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.isShowing()) {
                            VirtualMediaLocalPanel.this.hideConnectionInProgressDialog();
                        }
                    }
                }

                @Override
                public void driveConnected(boolean bl) {
                    if (bl) {
                        VirtualMediaLocalPanel.this.connected(0x32020000);
                    }
                }

                @Override
                public VirtualMediaEventListener.LockFailAction driveLockingFailed() {
                    return VirtualMediaEventListener.LockFailAction.IGNORE;
                }
            };
        }
        catch (Exception exception) {
            RRCLogger.log(200, 1024, "Error Creating Virtual Media Dialog");
            RRCLogger.logException(exception);
        }
    }

    private void connected(int n) {
        if (this.vmConnectionInProgressMsgDialog != null && this.vmConnectionInProgressMsgDialog.isShowing()) {
            this.hideConnectionInProgressDialog();
        }
        if (this.isShowing()) {
            this.getShell().getContentPane().removeAll();
            this.getShell().dispose();
        }
        Map map = this.scrContext.getVirtualMediaLocalMap();
        VirtualMediaLocalBean virtualMediaLocalBean = null;
        if (map != null) {
            virtualMediaLocalBean = (VirtualMediaLocalBean)map.get(this.portKey);
        }
        if (virtualMediaLocalBean != null) {
            this.populateVMPanelBean();
            ((RRCScreenContext)this.scrContext).toggleMenuBar(this.portKey);
            this.showSuccessfulConnectionDialog(virtualMediaLocalBean, n);
            RRCLogger.log(200, 1024, "Drive Redirection established for Drive1.");
        }
    }

    private void showConnectionInProgressDialog(final boolean bl) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                if (VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog == null) {
                    VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog = new JDialog((Dialog)VirtualMediaLocalPanel.this.getShell(), true);
                    MPCUtil.jre17WorkaroundInheritAlwaysOnTop(VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog);
                    VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.setSize(440, 110);
                    VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.setTitle(VirtualMediaLocalPanel.this.bundle.getString(VirtualMediaLocalPanel.CONNECT_PROGRESS_TITLE));
                    VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.setDefaultCloseOperation(0);
                    VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.setResizable(false);
                    VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.getContentPane().setLayout(new BorderLayout());
                    JPanel jPanel = new JPanel(new FlowLayout(1));
                    jPanel.add(new JLabel(VirtualMediaLocalPanel.this.bundle.getString(VirtualMediaLocalPanel.CONNECT_PROGRESS_MESSAGE)));
                    VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.setLocationRelativeTo(VirtualMediaLocalPanel.this);
                    VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.getContentPane().add((Component)new JLabel(" "), "North");
                    VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.getContentPane().add(jPanel);
                }
                VirtualMediaLocalPanel.this.vmConnectionInProgressMsgDialog.setVisible(bl);
            }
        });
    }

    private void hideConnectionInProgressDialog() {
        this.vmConnectionInProgressMsgDialog.setVisible(false);
        this.vmConnectionInProgressMsgDialog.dispose();
    }

    private void showSuccessfulConnectionDialog(VirtualMediaLocalBean virtualMediaLocalBean, int n) {
        Object object;
        String string;
        String string2;
        switch (n) {
            case 0x32020000: {
                string2 = this.bundle.getString("optionpane.success.title");
                string = this.bundle.getString(CONNECT_SUCEESS_MESSAGE);
                try {
                    object = this.drives.get(this.availableDrivesCombo1.getSelectedIndex()).getDriveType(RemoteConsoleLogger.getInstance().getLogger());
                    if (object != VMCore.DriveType.HARD_DISK_FULL && object != VMCore.DriveType.HARD_DISK_PARTITION && object != VMCore.DriveType.HARD_DISK_FULL_EXTERNAL && object != VMCore.DriveType.HARD_DISK_PARTITION_EXTERNAL && object != VMCore.DriveType.UNKNOWN) break;
                    string = string + "\n" + this.bundle.getString(CONNECT_SUCEESS_MESSAGE_LONG);
                }
                catch (Exception exception) {}
                break;
            }
            case 822214661: {
                string2 = this.bundle.getString("optionpane.warning.tite");
                string = this.bundle.getString("Usb.Not.Connected");
                break;
            }
            default: {
                string2 = this.bundle.getString("optionpane.error.title");
                string = this.bundle.getString("unknown.msp.status");
            }
        }
        object = string;
        final String string3 = string2;
        SwingUtilities.invokeLater(new Runnable((String)object, virtualMediaLocalBean){
            final /* synthetic */ String val$message;
            final /* synthetic */ VirtualMediaLocalBean val$vmBean;
            {
                this.val$message = string2;
                this.val$vmBean = virtualMediaLocalBean;
            }

            @Override
            public void run() {
                CommonPopups.showInfoDialog(string3, this.val$message, this.val$vmBean.getRFBView(), VirtualMediaLocalPanel.this.scrContext);
            }
        });
    }

    public void connectDrive1() {
        int n;
        String[] stringArray;
        Map map = this.scrContext.getVirtualMediaLocalMap();
        if ((VirtualMediaLocalBean)map.get(this.portKey) != null) {
            this.currentVirtualMediaBean = (VirtualMediaLocalBean)map.get(this.portKey);
        }
        VirtualMediaLocalBean virtualMediaLocalBean = this.currentVirtualMediaBean;
        this.driveSelected = this.availableDrivesCombo1.getModel().getSelectedItem().toString();
        boolean bl = this.readWriteCheckBoxConn1.isSelected();
        RRCLogger.log(200, 1024, "Trying to do Drive Redirection for Drive1:." + this.driveSelected + " with ReadWrite:" + bl);
        if (bl) {
            stringArray = new String[]{POPUP_OPTION_YES, POPUP_OPTION_NO};
            n = CommonPopups.showConfirmationDialog(this.bundle.getString("optionpane.warning.tite"), this.bundle.getString(WARNING_MESSAGE), this, this.scrContext, stringArray);
            if (n == 2) {
                bl = true;
            } else {
                this.readWriteCheckBoxConn1.setSelected(false);
                bl = false;
            }
        }
        stringArray = this.drives.get(this.availableDrivesCombo1.getSelectedIndex());
        n = this.readWriteCheckBoxConn1.isSelected();
        this.showConnectionInProgressDialog(true);
        new Thread(new Runnable((RedirectableObject)stringArray, n != 0){
            final /* synthetic */ RedirectableObject val$selDrive;
            final /* synthetic */ boolean val$enableReadWrite;
            {
                this.val$selDrive = redirectableObject;
                this.val$enableReadWrite = bl;
            }

            @Override
            public void run() {
                try {
                    VirtualMediaLocalPanel.this.mountSelectedDrive(this.val$selDrive, this.val$enableReadWrite);
                    VirtualMediaLocalPanel.this.scrContext.addSelectedDrive(VirtualMediaLocalPanel.this.driveSelected);
                }
                catch (Exception exception) {
                    VirtualMediaLocalPanel.this.hideConnectionInProgressDialog();
                    String string = exception.getMessage();
                    if (string != null && !string.equals("")) {
                        string = VirtualMediaLocalPanel.this.replaceTokens(string, VirtualMediaLocalPanel.this.getTokens());
                        CommonPopups.showWarningDialog(VirtualMediaLocalPanel.this.bundle.getString("optionpane.warning.tite"), string, VirtualMediaLocalPanel.this, VirtualMediaLocalPanel.this.scrContext);
                    }
                    RRCLogger.log(200, 1024, "Error Connecting Drive Redirection for Drive 1:");
                    RRCLogger.log(200, 1024, exception.getMessage());
                    VirtualMediaLocalPanel.this.isDriveConnected = false;
                    VirtualMediaLocalPanel.this.disconnected(false);
                }
            }
        }).start();
    }

    protected void mountSelectedDrive(RedirectableObject redirectableObject, boolean bl) {
        RFBView rFBView = ((VirtualMediaLocalBean)this.scrContext.getVirtualMediaLocalMap().get(this.portKey)).getRFBView();
        boolean bl2 = !bl;
        try {
            if (rFBView != null) {
                VMConfigInfo vMConfigInfo = rFBView.getPort().getVmConfigInfo();
                VMInterfaceInfo vMInterfaceInfo = vMConfigInfo.getVMInterface(4);
                this.currentVirtualMediaBean.getVmCore().setMsIndex(vMInterfaceInfo.getInterfaceID());
                RemoteConsoleParameters remoteConsoleParameters = rFBView.getRCParameters();
                this.currentVirtualMediaBean.getVmCore().connectVMWithRdmSession(remoteConsoleParameters.host, remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, rFBView.getPortUniqueId(), rFBView.getRfbSessionId(), redirectableObject, bl2, VMCore.LockFailBehavior.ASK, remoteConsoleParameters.rdmSession, remoteConsoleParameters.proxyConnectionIdVM, remoteConsoleParameters.proxyUseSSL);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            RRCLogger.getLogger().log(Level.SEVERE, "Unexpected Error happened during Virtual Media connection.", exception);
            CommonPopups.showWarningDialog(this.bundle.getString("optionpane.warning.tite"), exception.getMessage(), this, this.scrContext);
            this.hideConnectionInProgressDialog();
        }
    }

    public void disconnected(boolean bl) {
        RRCLogger.log(200, 1024, "Disconnecting Drive 1 ...");
        if (this.vmConnectionInProgressMsgDialog.isShowing()) {
            this.vmConnectionInProgressMsgDialog.dispose();
        }
        if (this.isShowing()) {
            this.getShell().getContentPane().removeAll();
            this.getShell().dispose();
        }
        Map map = this.scrContext.getVirtualMediaLocalMap();
        VirtualMediaLocalBean virtualMediaLocalBean = null;
        if (map != null) {
            virtualMediaLocalBean = (VirtualMediaLocalBean)map.get(this.portKey);
        }
        if (virtualMediaLocalBean != null) {
            if (!bl) {
                virtualMediaLocalBean.getVmCore().disconnect(true);
            }
            virtualMediaLocalBean.setDriveConnected(false);
            this.isDriveConnected = false;
            this.driveSelected = "";
            if (map.containsKey(this.portKey)) {
                map.remove(this.portKey);
            }
            ((RRCScreenContext)this.scrContext).toggleMenuBar(this.portKey);
            if (bl && !virtualMediaLocalBean.getSwitchedFlag()) {
                CommonPopups.showWarningDialog(this.bundle.getString("vmpanel.disconnect.title"), this.bundle.getString("vmpanel.DEVICE_DISCONNECT_NOTE"), this, this.scrContext);
                RRCLogger.log(200, 1024, "Disconnected by the device!");
            } else {
                RRCLogger.log(200, 1024, "Successfully Disconnected Drive Redirection.");
            }
            if (virtualMediaLocalBean.getRFBView() != null) {
                virtualMediaLocalBean.getRFBView().setVMLocalPanel(null);
            }
        }
    }

    public void resetDrives() {
        this.availableDrivesCombo1.setSelectedIndex(-1);
        this.driveOneSetEnabled(true);
    }

    public void resetDrive1() {
        this.availableDrivesCombo1.setSelectedIndex(-1);
        this.driveOneSetEnabled(true);
    }

    public void driveOneSetEnabled(boolean bl) {
        this.availableDrivesCombo1.setEnabled(bl);
        this.readWriteCheckBoxConn1.setEnabled(bl && Platform.isVirtualMediaReadWriteSupported());
    }

    @Override
    public JPanel doButtonWidget() {
        this.cancelButton = new JButton(this.bundle.getString(CANCEL));
        this.connectButton = new JButton(this.bundle.getString(CONNECT));
        this.buttonPanel = new JPanel(new SpringLayout());
        this.connectButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.buttonPanel.add(this.connectButton);
        this.buttonPanel.add(this.cancelButton);
        SpringUtilities.makeCompactGrid(this.buttonPanel, 1, 2, 190, 9, 10, 9);
        return this.buttonPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
        Object object = actionEvent.getSource();
        this.scrContext.getLogger().logTextDebug("started");
        if (object instanceof JButton) {
            boolean bl = true;
            Map map = this.scrContext.getVirtualMediaLocalMap();
            VirtualMediaLocalBean virtualMediaLocalBean = (VirtualMediaLocalBean)map.get(this.portKey);
            if ((VirtualMediaLocalBean)map.get(this.portKey) != null) {
                this.currentVirtualMediaBean = (VirtualMediaLocalBean)map.get(this.portKey);
            }
            virtualMediaLocalBean = this.currentVirtualMediaBean;
            if (object == this.connectButton) {
                if (!virtualMediaLocalBean.isDriveConnected() && this.availableDrivesCombo1.getModel().getSelectedItem() != null) {
                    this.connectDrive1();
                }
                bl = true;
            }
            if (object == this.cancelButton) {
                this.getShell().setVisible(false);
                this.getShell().dispose();
                bl = true;
                if (virtualMediaLocalBean != null && this.vmAdapter != null) {
                    virtualMediaLocalBean.getVmCore().removeVirtualMediaEventListener(this.vmAdapter);
                    virtualMediaLocalBean.getVmCore().removeNotificationListener(this.vmAdapter);
                }
            }
        }
        this.scrContext.getLogger().logTextDebug("finished");
    }

    public int get_current_port() {
        Map map = this.scrContext.getVirtualMediaLocalMap();
        VirtualMediaLocalBean virtualMediaLocalBean = (VirtualMediaLocalBean)map.get(this.portKey);
        return virtualMediaLocalBean.getPort();
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
    }

    public static void main(String[] stringArray) {
        VirtualMediaLocalPanel virtualMediaLocalPanel = new VirtualMediaLocalPanel(true, null);
        virtualMediaLocalPanel.makeLayout();
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        try {
            Map map = this.scrContext.getVirtualMediaLocalMap();
            VirtualMediaLocalBean virtualMediaLocalBean = (VirtualMediaLocalBean)map.get(this.portKey);
            if (itemEvent.getSource() == this.availableDrivesCombo1) {
                KvmPort kvmPort;
                if (this.availableDrivesCombo1.getSelectedIndex() >= 0) {
                    VMCore.DriveType driveType = this.drives.get(this.availableDrivesCombo1.getSelectedIndex()).getDriveType(RRCLogger.getLogger());
                    if (driveType != VMCore.DriveType.FLOPPY && driveType != VMCore.DriveType.REMOVABLE && driveType != VMCore.DriveType.HARD_DISK_PARTITION_EXTERNAL && !this.overrideReadWrite || !Platform.isVirtualMediaReadWriteSupported() && !this.overrideReadWrite) {
                        this.readWriteCheckBoxConn1.setSelected(false);
                        this.readWriteCheckBoxConn1.setEnabled(false);
                    } else {
                        KvmPort kvmPort2;
                        boolean bl = true;
                        if (virtualMediaLocalBean.getRFBView() != null && (kvmPort2 = virtualMediaLocalBean.getRFBView().getPort()) != null && kvmPort2.getPortPermissionHelper().getVmPermission() != IKvmPort.VmPermission.READWRITE) {
                            bl = false;
                        }
                        this.readWriteCheckBoxConn1.setEnabled(bl);
                        if (!bl) {
                            this.readWriteCheckBoxConn1.setSelected(false);
                        }
                    }
                } else if (virtualMediaLocalBean.getRFBView() != null && (kvmPort = virtualMediaLocalBean.getRFBView().getPort()) != null) {
                    if (kvmPort.getPortPermissionHelper().getVmPermission() == IKvmPort.VmPermission.READWRITE && Platform.isVirtualMediaReadWriteSupported()) {
                        this.readWriteCheckBoxConn1.setSelected(false);
                        this.readWriteCheckBoxConn1.setEnabled(true);
                    } else {
                        this.readWriteCheckBoxConn1.setSelected(false);
                        this.readWriteCheckBoxConn1.setEnabled(false);
                    }
                }
            }
        }
        catch (IOException iOException) {
        }
        catch (VMException vMException) {
            // empty catch block
        }
    }

    public void hideDialogAfterSambaResponse(int n) {
    }

    public String[] getTokens() {
        Map map = this.scrContext.getVirtualMediaLocalMap();
        VirtualMediaLocalBean virtualMediaLocalBean = null;
        String string = "";
        if (map != null) {
            virtualMediaLocalBean = (VirtualMediaLocalBean)map.get(this.portKey);
        }
        if (virtualMediaLocalBean != null && virtualMediaLocalBean.getRFBView() != null) {
            string = virtualMediaLocalBean.getRFBView().getPortName();
        }
        return new String[]{string};
    }

    private String replaceTokens(String string, String[] stringArray) {
        if (stringArray != null && stringArray.length > 0) {
            return MessageFormat.format(string, stringArray);
        }
        RRCLogger.log(200, -1, "Tokens are null");
        return null;
    }

    class ComboBoxType
    extends JComboBox {
        ComboBoxType() {
        }

        @Override
        public Dimension getSize() {
            return new Dimension(200, 20);
        }

        @Override
        public Rectangle getBounds() {
            Rectangle rectangle = new Rectangle(super.getBounds().x, super.getBounds().y, this.getSize().width, this.getSize().height);
            return rectangle;
        }

        @Override
        public Dimension getMaximumSize() {
            return this.getSize();
        }

        @Override
        public Dimension getPreferredSize() {
            return this.getSize();
        }
    }
}

