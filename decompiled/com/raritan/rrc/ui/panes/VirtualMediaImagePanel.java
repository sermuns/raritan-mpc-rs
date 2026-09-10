/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.VMConfigInfo;
import com.raritan.rrc.data.VMInterfaceInfo;
import com.raritan.rrc.data.VirtualMediaBean;
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
import com.raritan.tools.ui.panes.displays.Shell;
import com.raritan.tools.util.Util;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import javaclientlib.utils.RRCLogger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import nn.pp.common.LimitedLengthDocument;
import nn.pp.common.RemoteConsoleParameters;
import nn.pp.core.INotificationEvent;
import nn.pp.core.Platform;
import nn.pp.rccore.IVMMountRequestResponse;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.RCException;
import nn.pp.rccore.VMMountRequestResponse;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.VMAdapter;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;

public class VirtualMediaImagePanel
extends AbstractDisplay
implements ActionListener,
ItemListener {
    private static final long serialVersionUID = 2677565149168348926L;
    protected RaritanPropertyResourceBundle bundle;
    private String portKey;
    private String host;
    private JPanel generalPanel;
    private DefaultComboBoxModel availableDrivesModel;
    private JButton connectButton;
    private JButton cancelButton;
    private JPanel buttonPanel;
    private JRadioButton localDriveRadio;
    private JRadioButton diskImageRadioButton;
    private JRadioButton remoteImageRadioButton;
    private JComboBox availableDrivesCombo;
    private JComboBox hostsCombo;
    private JComboBox imagesCombo;
    private JTextField usersText;
    private JPasswordField userPwdText;
    private JTextField imageText;
    private JButton browseButton;
    private DefaultComboBoxModel hostsModel;
    private DefaultComboBoxModel imagesModel;
    private JPanel mainPanel;
    private static final int DRIVE_NONE = 0;
    private static final int DRIVE_REDIRECTION = 1;
    private static final int LOCAL_ISO_MOUNT = 2;
    private static final int REMOTE_ISO_MOUNT = 3;
    private static final String RADIO_BUTTON_NONE = "vmpanel.RADIO_BUTTON_NONE";
    private static final String RADIO_BUTTON_DISK_IMAGE = "vmpanel.RADIO_BUTTON_DISK_IMAGE";
    private static final String RADIO_BUTTON_PRE_CONF = "vmpanel.RADIO_BUTTON_PRE_CONF";
    private static final String PANEL_NAME = "vmpanel.IMAGE_PANEL_NAME";
    private static final String WARNING_MESSAGE_ISO = "vmpanel.WARNING_MESSAGE_ISO";
    private static final String PANEL_INFO_MESSAGE = "vmpanel.PANEL_INFO_MESSAGE_2";
    private static final String IMAGE = "vmpanel.image";
    private static final String HOSTS = "vmpanel.hosts";
    private static final String REMOTE_FILE_SERVER_USER_NAME = "vmpanel.username";
    private static final String REMOTE_FILE_SERVER_PASSWORD = "vmpanel.password";
    private static final String DISK_IMAGE_PATH = "vmpanel.diskimagepath";
    private static final String BROWSE = "vmpanel.browse";
    private static final String CANCEL = "vmpanel.cancel";
    private static final String CONNECT = "vmpanel.connect";
    private static final String FILE_DIALOG_NAME = "vmpanel.filedialogname";
    private static final String CONNECT_SUCEESS_MESSAGE = "vmpanel.CONNECT_SUCEESS_MESSAGE";
    private static final String CONNECT_PROGRESS_TITLE = "vmpanel.CONNECT_PROGRESS_TITLE";
    private static final String CONNECT_PROGRESS_MESSAGE = "vmpanel.CONNECT_PROGRESS_MESSAGE";
    private boolean isDriveConnected = false;
    private String driveSelected;
    private int connectionType = 0;
    private JDialog vmConnectionInProgressMsgDialog = null;
    private VirtualMediaBean currentVirtualMediaBean = null;
    private List<RedirectableObject> drives;
    private Hashtable<String, Vector<String>> remoteIsos;
    private VMAdapter vmAdapter;

    @Override
    public void makeLayout() {
        try {
            RRCLogger.log(200, 1024, "Client Platform : " + OS.getCurrent());
            this.mainPanel = new JPanel(new SpringLayout());
            this.generalPanel = new JPanel(new GridBagLayout());
            JPanel jPanel = new JPanel(new SpringLayout());
            JLabel jLabel = new JLabel(this.bundle.getString(PANEL_INFO_MESSAGE));
            jPanel.add(jLabel);
            SpringUtilities.makeCompactGrid(jPanel, 1, 1, 15, 1, 1, 1);
            ButtonGroup buttonGroup = new ButtonGroup();
            this.localDriveRadio = new JRadioButton();
            this.localDriveRadio.setText(this.bundle.getString(RADIO_BUTTON_NONE));
            this.localDriveRadio.addActionListener(this);
            buttonGroup.add(this.localDriveRadio);
            if (!Platform.isVirtualMediaSupported()) {
                RRCLogger.log(200, 1024, "Client Platform is " + System.getProperty("os.name") + ". Disabling full Local radio button 1 ");
                this.localDriveRadio.setEnabled(false);
            }
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.anchor = 23;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(6, 2, 0, 0);
            this.generalPanel.add((Component)this.localDriveRadio, gridBagConstraints);
            this.availableDrivesCombo = new JComboBox();
            this.availableDrivesCombo.addItemListener(this);
            if (!Platform.isVirtualMediaSupported()) {
                RRCLogger.log(200, 1024, "Client Platform is " + System.getProperty("os.name") + ". Disabling Drives combo");
                this.availableDrivesCombo.setEnabled(false);
            }
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(4, 22, 0, 6);
            this.generalPanel.add((Component)this.availableDrivesCombo, gridBagConstraints);
            this.diskImageRadioButton = new JRadioButton();
            this.diskImageRadioButton.setText(this.bundle.getString(RADIO_BUTTON_DISK_IMAGE));
            this.diskImageRadioButton.addActionListener(this);
            buttonGroup.add(this.diskImageRadioButton);
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.insets = new Insets(20, 2, 0, 0);
            this.generalPanel.add((Component)this.diskImageRadioButton, gridBagConstraints);
            this.imageText = new JTextField();
            this.imageText.setPreferredSize(new Dimension(this.imageText.getPreferredSize().width, 20));
            JLabel jLabel2 = new JLabel(this.bundle.getString(DISK_IMAGE_PATH));
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.insets = new Insets(4, 22, 0, 0);
            this.generalPanel.add((Component)jLabel2, gridBagConstraints);
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(4, 22, 0, 6);
            this.generalPanel.add((Component)this.imageText, gridBagConstraints);
            this.browseButton = new JButton(this.bundle.getString(BROWSE));
            this.browseButton.addActionListener(this);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.fill = 3;
            gridBagConstraints.insets = new Insets(4, 6, 0, 0);
            this.generalPanel.add((Component)this.browseButton, gridBagConstraints);
            this.remoteImageRadioButton = new JRadioButton();
            this.remoteImageRadioButton.setText(this.bundle.getString(RADIO_BUTTON_PRE_CONF));
            this.remoteImageRadioButton.addActionListener(this);
            buttonGroup.add(this.remoteImageRadioButton);
            gridBagConstraints.gridy = 5;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(20, 2, 0, 0);
            this.generalPanel.add((Component)this.remoteImageRadioButton, gridBagConstraints);
            jLabel2 = new JLabel(this.bundle.getString(HOSTS));
            gridBagConstraints.gridy = 6;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(4, 22, 0, 0);
            this.generalPanel.add((Component)jLabel2, gridBagConstraints);
            this.hostsCombo = new JComboBox(){

                @Override
                public void setSelectedItem(Object object) {
                    super.setSelectedItem(object);
                    this.setToolTipText(object != null ? object.toString() : null);
                }
            };
            this.hostsCombo.addItemListener(this);
            gridBagConstraints.gridy = 7;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(2, 22, 0, 6);
            this.generalPanel.add((Component)this.hostsCombo, gridBagConstraints);
            Dimension dimension = this.hostsCombo.getPreferredSize();
            dimension.width = Util.getSizeOfIPV6Component(this.hostsCombo.getFontMetrics(this.hostsCombo.getFont()));
            this.hostsCombo.setPreferredSize(dimension);
            jLabel2 = new JLabel(this.bundle.getString(IMAGE));
            gridBagConstraints.gridy = 6;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(4, 6, 0, 0);
            this.generalPanel.add((Component)jLabel2, gridBagConstraints);
            this.imagesCombo = new JComboBox();
            this.imagesCombo.addItemListener(this);
            gridBagConstraints.gridy = 7;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(2, 6, 0, 22);
            this.imagesCombo.setPreferredSize(dimension);
            this.generalPanel.add((Component)this.imagesCombo, gridBagConstraints);
            this.usersText = new JTextField();
            this.usersText.setDocument(new LimitedLengthDocument());
            this.usersText.setPreferredSize(new Dimension(this.usersText.getPreferredSize().width, 20));
            this.usersText.addActionListener(this);
            jLabel2 = new JLabel(this.bundle.getString(REMOTE_FILE_SERVER_USER_NAME));
            gridBagConstraints.gridy = 8;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(6, 22, 0, 0);
            this.generalPanel.add((Component)jLabel2, gridBagConstraints);
            gridBagConstraints.gridy = 9;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(2, 22, 12, 6);
            this.generalPanel.add((Component)this.usersText, gridBagConstraints);
            this.userPwdText = new JPasswordField();
            this.userPwdText.setDocument(new LimitedLengthDocument());
            this.userPwdText.setPreferredSize(new Dimension(this.userPwdText.getWidth(), 20));
            this.userPwdText.setEchoChar('*');
            this.userPwdText.addActionListener(this);
            jLabel2 = new JLabel(this.bundle.getString(REMOTE_FILE_SERVER_PASSWORD));
            gridBagConstraints.gridy = 8;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(6, 6, 0, 0);
            this.generalPanel.add((Component)jLabel2, gridBagConstraints);
            gridBagConstraints.gridy = 9;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(2, 6, 12, 22);
            this.generalPanel.add((Component)this.userPwdText, gridBagConstraints);
            this.generalPanel.setBorder(BorderFactory.createEtchedBorder());
            jPanel.setBorder(BorderFactory.createEtchedBorder());
            this.mainPanel.add(jPanel);
            this.mainPanel.add(this.generalPanel);
            this.mainPanel.add(this.doButtonWidget());
            SpringUtilities.makeCompactGrid(this.mainPanel, 3, 1, 1, 1, 1, 1);
            this.add((Component)this.mainPanel, "Center");
            this.cancelButton.enableInputMethods(false);
            this.browseButton.enableInputMethods(false);
            this.connectButton.enableInputMethods(false);
            this.setVisible(true);
            this.resetDrives();
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    public void populateVMPanel(VirtualMediaBean virtualMediaBean) {
        try {
            this.portKey = virtualMediaBean.getPortKey();
            if (Platform.isVirtualMediaSupported()) {
                this.localDriveRadio.setSelected(true);
                this.availableDrivesCombo.setEnabled(true);
                this.populateComboBoxes();
                if (!this.availableDrivesCombo.isEnabled()) {
                    this.localDriveRadio.setEnabled(false);
                }
                this.imageText.setText("");
                boolean bl = !this.localDriveRadio.isEnabled();
                this.diskImageRadioButton.setSelected(bl);
                this.imageText.setEnabled(bl);
                this.browseButton.setEnabled(bl);
            } else {
                this.localDriveRadio.setEnabled(false);
                this.availableDrivesCombo.setSelectedIndex(-1);
                this.availableDrivesCombo.setEnabled(false);
                this.diskImageRadioButton.setSelected(true);
                this.imageText.setEnabled(true);
                this.imageText.setText("");
                this.browseButton.setEnabled(true);
            }
            this.hostsCombo.setSelectedIndex(-1);
            this.hostsCombo.setEnabled(false);
            this.imagesCombo.setSelectedIndex(-1);
            this.imagesCombo.setEnabled(false);
            this.isDriveConnected = false;
            this.imageText.setText("");
            this.usersText.setText("");
            this.usersText.setEnabled(false);
            this.userPwdText.setText("");
            this.userPwdText.setEnabled(false);
            virtualMediaBean.getRFBView().setVMImagePanel(this);
            this.host = virtualMediaBean.getRFBProfile().getRemoteHost();
            virtualMediaBean.getVmCore().addNotificationListener(this.vmAdapter);
            virtualMediaBean.getVmCore().addVirtualMediaEventListener(this.vmAdapter, 7);
            List<IVMMountRequestResponse> list = virtualMediaBean.getRFBView().getRemoteIsoList();
            this.remoteImageRadioButton.setEnabled(list.size() > 0);
        }
        catch (Exception exception) {
            RRCLogger.log(200, 1024, "Unable to populate VM Panel using VMBean values");
            RRCLogger.logException(exception);
        }
    }

    private static void adjustMaximumSize(JComponent jComponent) {
        Dimension dimension = jComponent.getMaximumSize();
        dimension.height = jComponent.getPreferredSize().height;
        dimension.width = 160;
        jComponent.setMaximumSize(dimension);
    }

    private void populateComboBoxes() {
        this.drives = this.getAvailableDrives();
        this.availableDrivesModel = new DefaultComboBoxModel();
        for (RedirectableObject redirectableObject : this.drives) {
            this.availableDrivesModel.addElement(redirectableObject.getLongName());
        }
        this.availableDrivesCombo.setModel(this.availableDrivesModel);
        this.availableDrivesCombo.setEnabled(this.availableDrivesCombo.getItemCount() > 0);
        if (this.availableDrivesCombo.isEnabled()) {
            this.availableDrivesCombo.setSelectedIndex(0);
        }
    }

    public void populateVMPanelBean(VirtualMediaBean virtualMediaBean) {
        try {
            Map map = this.scrContext.getVirtualMediaImageMap();
            VirtualMediaBean virtualMediaBean2 = virtualMediaBean;
            if (virtualMediaBean2 != null) {
                map.remove(this.portKey);
                virtualMediaBean2.setDriveConnected(true);
                virtualMediaBean2.setConnectedDrive(this.driveSelected);
                virtualMediaBean2.setConnectionType(this.connectionType);
                map.put(this.portKey, virtualMediaBean2);
            } else {
                RRCLogger.log(200, 1024, "Error populating Bean. vmBean is NULL.");
            }
            this.scrContext.setVitualMediaImageMap(map);
        }
        catch (Exception exception) {
            RRCLogger.log(200, 1024, "Error populating Bean");
            RRCLogger.logException(exception);
        }
    }

    public VirtualMediaImagePanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        try {
            this.isDialog = bl;
            this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
            this.makeLayout();
            this.setShell(this.bundle.getString(PANEL_NAME));
            this.vmAdapter = new VMAdapter(){

                @Override
                public void disconnected(Exception exception) {
                    VirtualMediaImagePanel.this.disconnected(true);
                }

                @Override
                public void receivedNotification(INotificationEvent iNotificationEvent) {
                    if (iNotificationEvent.getErrorCode() == 822214661) {
                        VirtualMediaImagePanel.this.connected(822214661);
                        return;
                    }
                    ErrorHandlerImpl.getInstance().handleError(iNotificationEvent.getErrorCode(), VirtualMediaImagePanel.this, VirtualMediaImagePanel.this.currentVirtualMediaBean.getRFBView());
                    if (iNotificationEvent.isQuit() || iNotificationEvent.isError()) {
                        VMCore vMCore = null;
                        if (VirtualMediaImagePanel.this.currentVirtualMediaBean != null) {
                            vMCore = VirtualMediaImagePanel.this.currentVirtualMediaBean.getVmCore();
                        }
                        if (vMCore != null) {
                            vMCore.disconnect(true);
                        }
                        if (VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog != null && VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.isShowing()) {
                            VirtualMediaImagePanel.this.hideConnectionInProgressDialog();
                        }
                        VirtualMediaImagePanel.this.connectionType = 0;
                    }
                }

                @Override
                public void driveConnected(boolean bl) {
                    if (bl) {
                        VirtualMediaImagePanel.this.connected(0x32020000);
                    }
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
        Map map = this.scrContext.getVirtualMediaImageMap();
        VirtualMediaBean virtualMediaBean = null;
        if (map != null) {
            virtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
        }
        if (virtualMediaBean != null) {
            this.populateVMPanelBean(virtualMediaBean);
            ((RRCScreenContext)this.scrContext).toggleMenuBar(this.portKey);
            this.showSuccessfulConnectionDialog(virtualMediaBean, n);
            RRCLogger.log(200, 1024, "Drive Redirection established for Drive1.");
        }
    }

    public List<RedirectableObject> getAvailableDrives() {
        ArrayList<RedirectableObject> arrayList = new ArrayList<RedirectableObject>();
        List<RedirectableObject> list = RedirectableObject.getAvailableDrives(RRCLogger.getLogger());
        for (int i = 0; i < list.size(); ++i) {
            try {
                VMCore.DriveType driveType = list.get(i).getDriveType(RRCLogger.getLogger());
                if (driveType != VMCore.DriveType.CDROM) continue;
                arrayList.add(list.get(i));
                continue;
            }
            catch (IOException iOException) {
                RRCLogger.getLogger().log(Level.WARNING, "IO Exception trying to determine drive type of drive: " + list.get(i).getLongName(), iOException);
                continue;
            }
            catch (VMException vMException) {
                RRCLogger.getLogger().log(Level.WARNING, "VM Exception trying to determine drive type of drive: " + list.get(i).getLongName(), vMException);
            }
        }
        return arrayList;
    }

    public void connectSamba() {
        Map map = this.scrContext.getVirtualMediaImageMap();
        if (map != null) {
            final VirtualMediaBean virtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
            if (virtualMediaBean != null) {
                this.currentVirtualMediaBean = virtualMediaBean;
            }
            String string = "";
            if (this.hostsCombo.getModel().getSelectedItem() != null) {
                string = this.hostsCombo.getModel().getSelectedItem().toString();
            }
            String string2 = "";
            if (this.imagesCombo.getModel().getSelectedItem() != null) {
                string2 = this.imagesCombo.getModel().getSelectedItem().toString();
            }
            if (string.equals("") && string2.equals("")) {
                CommonPopups.showWarningDialog(this.bundle.getString("optionpane.warning.tite"), this.bundle.getString("vmpanel.image.error"), this, this.scrContext);
                return;
            }
            RRCLogger.log(200, 1024, "Trying to mount Pre-Configured Image for Drive 1");
            String string3 = string;
            String string4 = string2;
            final VMMountRequestResponse vMMountRequestResponse = new VMMountRequestResponse(1, virtualMediaBean.getVmInterfaceInfo().getInterfaceID(), this.usersText.getText(), new String(this.userPwdText.getPassword()), string3, string4);
            virtualMediaBean.setSambaRequest(vMMountRequestResponse);
            this.driveSelected = this.extractImageName(string2);
            this.showConnectionInProgressDialog(true);
            new Thread(new Runnable(){

                @Override
                public void run() {
                    try {
                        if (virtualMediaBean.getRFBView() != null) {
                            VirtualMediaImagePanel.this.mountOrUnmountRemoteISO(vMMountRequestResponse, virtualMediaBean.getRFBView().getRCCore());
                        } else {
                            VirtualMediaImagePanel.this.hideConnectionInProgressDialog();
                            RRCLogger.log(200, 1024, "Error during Pre-configured image mount for Drive 1. View is Null.");
                        }
                        RRCLogger.log(200, 1024, "Pre-configured mount established for Drive 1.");
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        VirtualMediaImagePanel.this.hideConnectionInProgressDialog();
                        RRCLogger.log(200, 1024, "Error mounting Pre-Configured Image for Drive 1:");
                        RRCLogger.logException(exception);
                        VirtualMediaImagePanel.this.isDriveConnected = false;
                        VirtualMediaImagePanel.this.connectionType = 0;
                        VirtualMediaImagePanel.this.populateVMPanelBean(virtualMediaBean);
                    }
                }
            }).start();
        }
    }

    public void mountOrUnmountRemoteISO(VMMountRequestResponse vMMountRequestResponse, RCCore rCCore) {
        try {
            rCCore.mountOrUnmountRemoteIso(vMMountRequestResponse);
        }
        catch (RCException rCException) {
            RRCLogger.getLogger().log(Level.SEVERE, "RCException occured.", rCException);
        }
        catch (IOException iOException) {
            RRCLogger.getLogger().log(Level.SEVERE, "IOException occured.", iOException);
        }
    }

    public void hideDialogAfterSambaResponse(int n) {
        try {
            this.hideConnectionInProgressDialog();
            Map map = this.scrContext.getVirtualMediaImageMap();
            VirtualMediaBean virtualMediaBean = null;
            if (map != null) {
                virtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
            }
            if (virtualMediaBean != null) {
                if (n > 0) {
                    if (virtualMediaBean.getRFBView() != null) {
                        if (virtualMediaBean.getRFBView().isSambaConnectSuccessful()) {
                            this.isDriveConnected = true;
                            this.connectionType = 3;
                            Shell shell = this.getShell();
                            shell.setVisible(false);
                            shell.getContentPane().removeAll();
                            shell.dispose();
                            virtualMediaBean.getRFBView().setSambaConnectSuccessful(false);
                            this.populateVMPanelBean(virtualMediaBean);
                            int n2 = 0x32020000;
                            if (n == 2) {
                                n2 = 822214661;
                            }
                            this.showSuccessfulConnectionDialog(virtualMediaBean, n2);
                            RRCLogger.log(200, 1024, "Remote ISO Mount established for port" + this.portKey + " for Image:" + this.driveSelected);
                        }
                    } else {
                        RRCLogger.log(200, 1024, "View is NULL when Connecting Remote ISO Mount for port:" + this.portKey);
                    }
                } else if (n == 0) {
                    if (virtualMediaBean.getRFBView() != null) {
                        if (virtualMediaBean.getRFBView().isSambaDisconnectSuccessful()) {
                            this.isDriveConnected = false;
                            this.connectionType = 0;
                            virtualMediaBean.getRFBView().setSambaDisconnectSuccessful(false);
                            this.scrContext.getVirtualMediaImageMap().remove(this.portKey);
                            RRCLogger.log(200, 1024, "Remote ISO Un-Mount established for port" + this.portKey + " for Image:" + this.driveSelected);
                        }
                    } else {
                        RRCLogger.log(200, 1024, "View is NULL when Disconnecting Remote ISO Mount for port:" + this.portKey);
                    }
                } else {
                    RRCLogger.log(200, 1024, "Invalid Response Option for Remote ISO mount for port:" + this.portKey);
                }
                ((RRCScreenContext)this.scrContext).toggleMenuBar(this.portKey);
            } else {
                RRCLogger.log(200, 1024, "vmBean is NULL while disconnecting Remote ISO Mount for port:" + this.portKey);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void showConnectionInProgressDialog(final boolean bl) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                if (VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog == null) {
                    VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog = new JDialog((Dialog)VirtualMediaImagePanel.this.getShell(), true);
                    VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.setSize(440, 110);
                    VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.setTitle(VirtualMediaImagePanel.this.bundle.getString(VirtualMediaImagePanel.CONNECT_PROGRESS_TITLE));
                    VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.setResizable(false);
                    VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.setDefaultCloseOperation(0);
                    VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.getContentPane().setLayout(new BorderLayout());
                    JPanel jPanel = new JPanel(new FlowLayout(1));
                    jPanel.add(new JLabel(VirtualMediaImagePanel.this.bundle.getString(VirtualMediaImagePanel.CONNECT_PROGRESS_MESSAGE)));
                    VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.setLocationRelativeTo(VirtualMediaImagePanel.this);
                    VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.getContentPane().add((Component)new JLabel(" "), "North");
                    VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.getContentPane().add(jPanel);
                }
                VirtualMediaImagePanel.this.vmConnectionInProgressMsgDialog.setVisible(bl);
            }
        });
    }

    private void hideConnectionInProgressDialog() {
        this.vmConnectionInProgressMsgDialog.setVisible(false);
        this.vmConnectionInProgressMsgDialog.dispose();
    }

    private void showSuccessfulConnectionDialog(final VirtualMediaBean virtualMediaBean, int n) {
        String string;
        String string2;
        switch (n) {
            case 0x32020000: {
                string2 = this.bundle.getString("optionpane.success.title");
                string = this.bundle.getString(CONNECT_SUCEESS_MESSAGE);
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
        final String string3 = string;
        final String string4 = string2;
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                CommonPopups.showInfoDialog(string4, string3, virtualMediaBean.getRFBView(), VirtualMediaImagePanel.this.scrContext);
            }
        });
    }

    public void connectDrive() {
        Map map = this.scrContext.getVirtualMediaImageMap();
        if ((VirtualMediaBean)map.get(this.portKey) != null) {
            this.currentVirtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
        }
        VirtualMediaBean virtualMediaBean = this.currentVirtualMediaBean;
        this.driveSelected = this.availableDrivesCombo.getModel().getSelectedItem().toString();
        RRCLogger.log(200, 1024, "Trying to do Drive Redirection for Drive:." + this.driveSelected);
        final RedirectableObject redirectableObject = this.drives.get(this.availableDrivesCombo.getSelectedIndex());
        this.showConnectionInProgressDialog(true);
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    VirtualMediaImagePanel.this.mountSelectedDrive(redirectableObject);
                    VirtualMediaImagePanel.this.scrContext.addSelectedDrive(VirtualMediaImagePanel.this.driveSelected);
                    VirtualMediaImagePanel.this.connectionType = 1;
                }
                catch (Exception exception) {
                    VirtualMediaImagePanel.this.hideConnectionInProgressDialog();
                    String string = exception.getMessage();
                    if (string != null && !string.equals("")) {
                        string = VirtualMediaImagePanel.this.replaceTokens(string, VirtualMediaImagePanel.this.getTokens());
                        CommonPopups.showWarningDialog(VirtualMediaImagePanel.this.bundle.getString("optionpane.warning.tite"), string, VirtualMediaImagePanel.this, VirtualMediaImagePanel.this.scrContext);
                    }
                    RRCLogger.log(200, 1024, "Error Connecting Drive Redirection for Drive 1:");
                    RRCLogger.log(200, 1024, exception.getMessage());
                    VirtualMediaImagePanel.this.isDriveConnected = false;
                    VirtualMediaImagePanel.this.connectionType = 0;
                    VirtualMediaImagePanel.this.disconnected(false);
                }
            }
        }).start();
    }

    protected void mountSelectedDrive(RedirectableObject redirectableObject) {
        RFBView rFBView = ((VirtualMediaBean)this.scrContext.getVirtualMediaImageMap().get(this.portKey)).getRFBView();
        try {
            if (rFBView != null) {
                VMConfigInfo vMConfigInfo = rFBView.getPort().getVmConfigInfo();
                VMInterfaceInfo vMInterfaceInfo = vMConfigInfo.getVMInterface(1);
                this.currentVirtualMediaBean.getVmCore().setMsIndex(vMInterfaceInfo.getInterfaceID());
                RemoteConsoleParameters remoteConsoleParameters = rFBView.getRCParameters();
                this.currentVirtualMediaBean.getVmCore().connectVMWithRdmSession(remoteConsoleParameters.host, remoteConsoleParameters.tcpPort, remoteConsoleParameters.ssl, rFBView.getPortUniqueId(), rFBView.getRfbSessionId(), redirectableObject, true, VMCore.LockFailBehavior.ASK, remoteConsoleParameters.rdmSession, remoteConsoleParameters.proxyConnectionIdVM, remoteConsoleParameters.proxyUseSSL);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            RRCLogger.getLogger().log(Level.SEVERE, "Unexpected Error happened during Virtual Media connection.", exception);
            CommonPopups.showWarningDialog(this.bundle.getString("optionpane.warning.tite"), exception.getMessage(), this, this.scrContext);
            this.hideConnectionInProgressDialog();
        }
    }

    public void sambaMountError() {
        this.showConnectionInProgressDialog(false);
    }

    private void connectIso() {
        if (this.imageText.getText().equals("")) {
            CommonPopups.showWarningDialog(this.bundle.getString("optionpane.warning.tite"), this.bundle.getString("vmpanel.iso.error"), this, this.scrContext);
            return;
        }
        Map map = this.scrContext.getVirtualMediaImageMap();
        VirtualMediaBean virtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
        if (virtualMediaBean == null) {
            virtualMediaBean = this.currentVirtualMediaBean;
        } else {
            this.currentVirtualMediaBean = virtualMediaBean;
        }
        final VirtualMediaBean virtualMediaBean2 = virtualMediaBean;
        this.setFileNameInBean(virtualMediaBean2, this.imageText);
        RRCLogger.log(200, 1024, "Trying to connect ISO image for Drive 1.");
        File file = null;
        boolean bl = false;
        file = new File(virtualMediaBean2.getIsoDirectory(), virtualMediaBean2.getIsoFilename());
        boolean bl2 = bl = file.exists() && file.isFile() && file.canRead() && virtualMediaBean2.getIsoFilename().toLowerCase().endsWith(".iso");
        if (!bl) {
            RRCLogger.log(200, 1024, "Error connecting ISO Drive 1:NOT a valid ISO file.");
            CommonPopups.showWarningDialog(this.bundle.getString("optionpane.warning.tite"), this.bundle.getString(WARNING_MESSAGE_ISO), this, this.scrContext);
            this.disconnected(false);
            return;
        }
        final RedirectableObject redirectableObject = RedirectableObject.getIsoImage(new File(virtualMediaBean2.getIsoDirectory(), virtualMediaBean2.getIsoFilename()));
        this.showConnectionInProgressDialog(true);
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    VirtualMediaImagePanel.this.mountSelectedDrive(redirectableObject);
                    RRCLogger.log(200, 1024, "ISO Image redirected for Drive 1.");
                    VirtualMediaImagePanel.this.connectionType = 2;
                    VirtualMediaImagePanel.this.driveSelected = virtualMediaBean2.getIsoFilename();
                }
                catch (Exception exception) {
                    VirtualMediaImagePanel.this.hideConnectionInProgressDialog();
                    if (exception instanceof FileNotFoundException) {
                        CommonPopups.showWarningDialog(VirtualMediaImagePanel.this.bundle.getString("optionpane.warning.tite"), VirtualMediaImagePanel.this.bundle.getString(VirtualMediaImagePanel.WARNING_MESSAGE_ISO), VirtualMediaImagePanel.this, VirtualMediaImagePanel.this.scrContext);
                        return;
                    }
                    String string = exception.getMessage();
                    if (string != null) {
                        string = VirtualMediaImagePanel.this.replaceTokens(string, VirtualMediaImagePanel.this.getTokens());
                        CommonPopups.showWarningDialog(VirtualMediaImagePanel.this.bundle.getString("optionpane.warning.tite"), string, VirtualMediaImagePanel.this, VirtualMediaImagePanel.this.scrContext);
                    }
                    RRCLogger.log(200, 1024, "Error Connecting Drive Redirection for Drive 1:");
                    RRCLogger.log(200, 1024, exception.getMessage());
                    VirtualMediaImagePanel.this.isDriveConnected = false;
                    VirtualMediaImagePanel.this.connectionType = 0;
                    VirtualMediaImagePanel.this.disconnected(false);
                }
            }
        }).start();
    }

    public void disconnected(boolean bl) {
        RRCLogger.log(200, 1024, "Disconnecting Drive 1 ...");
        if (this.vmConnectionInProgressMsgDialog != null && this.vmConnectionInProgressMsgDialog.isShowing()) {
            this.vmConnectionInProgressMsgDialog.dispose();
        }
        if (this.isShowing()) {
            this.getShell().getContentPane().removeAll();
            this.getShell().dispose();
        }
        Map map = this.scrContext.getVirtualMediaImageMap();
        VirtualMediaBean virtualMediaBean = null;
        if (map != null) {
            virtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
        }
        if (virtualMediaBean != null) {
            if (!bl) {
                virtualMediaBean.getVmCore().disconnect(true);
            }
            virtualMediaBean.setDriveConnected(false);
            this.isDriveConnected = false;
            this.imageText.setText("");
            this.connectionType = 0;
            this.driveSelected = "";
            if (map.containsKey(this.portKey)) {
                map.remove(this.portKey);
            }
            ((RRCScreenContext)this.scrContext).toggleMenuBar(this.portKey);
            if (bl && !virtualMediaBean.getSwitchedFlag()) {
                CommonPopups.showWarningDialog(this.bundle.getString("vmpanel.disconnect.title"), this.bundle.getString("vmpanel.DEVICE_DISCONNECT_NOTE"), this, this.scrContext);
                RRCLogger.log(200, 1024, "Disconnected by the device!");
            } else {
                RRCLogger.log(200, 1024, "Successfully Disconnected Drive Redirection.");
            }
            if (virtualMediaBean.getRFBView() != null) {
                virtualMediaBean.getRFBView().setVMImagePanel(null);
            }
        }
    }

    public void resetDrives() {
        this.localDriveRadio.setSelected(true);
        this.connectButton.setEnabled(true);
        this.availableDrivesCombo.setSelectedIndex(-1);
        this.imageText.setText("");
        this.imagesCombo.setSelectedIndex(-1);
        this.driveOneSetEnabled(false);
    }

    public void resetDrive1() {
        this.localDriveRadio.setSelected(true);
        this.availableDrivesCombo.setSelectedIndex(-1);
        this.usersText.setText("");
        this.userPwdText.setText("");
        this.imageText.setText("");
        this.imagesCombo.setSelectedIndex(-1);
        this.driveOneSetEnabled(false);
    }

    public void driveOneSetEnabled(boolean bl) {
        this.hostsCombo.setEnabled(bl);
        this.imagesCombo.setEnabled(bl);
        this.imageText.setEnabled(bl);
        this.browseButton.setEnabled(bl);
        this.usersText.setEnabled(bl);
        this.userPwdText.setEnabled(bl);
    }

    @Override
    public JPanel doButtonWidget() {
        this.cancelButton = new JButton(this.bundle.getString(CANCEL));
        this.connectButton = new JButton(this.bundle.getString(CONNECT));
        this.buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = 13;
        gridBagConstraints.insets = new Insets(10, 0, 10, 10);
        this.connectButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.buttonPanel.add((Component)this.connectButton, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(10, 0, 10, 22);
        this.buttonPanel.add((Component)this.cancelButton, gridBagConstraints);
        return this.buttonPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
        Object object = actionEvent.getSource();
        if (object instanceof JRadioButton) {
            if (object == this.localDriveRadio) {
                this.populateComboBoxes();
                this.availableDrivesCombo.setModel(this.availableDrivesModel);
                this.availableDrivesCombo.setEnabled(true);
                this.connectButton.setEnabled(true);
                this.imageText.setText("");
                this.imageText.setEnabled(false);
                this.browseButton.setEnabled(false);
                this.hostsCombo.removeAllItems();
                this.hostsCombo.setEnabled(false);
                this.imagesCombo.removeAllItems();
                this.imagesCombo.setEnabled(false);
                this.usersText.setText("");
                this.usersText.setEnabled(false);
                this.userPwdText.setText("");
                this.userPwdText.setEnabled(false);
            }
            if (object == this.diskImageRadioButton) {
                this.imageText.setEnabled(true);
                this.browseButton.setEnabled(true);
                this.connectButton.setEnabled(true);
                this.availableDrivesCombo.removeAllItems();
                this.availableDrivesCombo.setEnabled(false);
                this.hostsCombo.removeAllItems();
                this.hostsCombo.setEnabled(false);
                this.imagesCombo.removeAllItems();
                this.imagesCombo.setEnabled(false);
                this.usersText.setText("");
                this.usersText.setEnabled(false);
                this.userPwdText.setText("");
                this.userPwdText.setEnabled(false);
            }
            if (object == this.remoteImageRadioButton && this.remoteImageRadioButton.isSelected()) {
                this.hostsCombo.setEnabled(true);
                this.imagesCombo.removeAllItems();
                this.imagesCombo.setEnabled(true);
                this.userPwdText.setText("");
                this.userPwdText.setEnabled(true);
                this.userPwdText.setText("");
                this.usersText.setEnabled(true);
                this.connectButton.setEnabled(true);
                this.availableDrivesCombo.removeAllItems();
                this.availableDrivesCombo.setEnabled(false);
                this.imageText.setText("");
                this.imageText.setEnabled(false);
                this.browseButton.setEnabled(false);
                this.populateRemoteIsoInfo();
            }
        }
        if (object instanceof JButton) {
            boolean bl = true;
            Map map = this.scrContext.getVirtualMediaImageMap();
            VirtualMediaBean virtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
            if (virtualMediaBean == null) {
                virtualMediaBean = this.currentVirtualMediaBean;
            } else {
                this.currentVirtualMediaBean = virtualMediaBean;
            }
            if (object == this.browseButton) {
                try {
                    JFileChooser jFileChooser = new JFileChooser(System.getProperty("user.home")){

                        @Override
                        protected JDialog createDialog(Component component) throws HeadlessException {
                            JDialog jDialog = super.createDialog(component);
                            MPCUtil.jre17WorkaroundInheritAlwaysOnTop(jDialog);
                            return jDialog;
                        }
                    };
                    jFileChooser.setDialogType(0);
                    jFileChooser.setDialogTitle(this.bundle.getString(FILE_DIALOG_NAME));
                    jFileChooser.setMultiSelectionEnabled(false);
                    jFileChooser.addChoosableFileFilter(new isoFileFilter());
                    int n = jFileChooser.showOpenDialog(this);
                    if (n == 0) {
                        File file = jFileChooser.getSelectedFile();
                        jFileChooser.setCurrentDirectory(file);
                        virtualMediaBean.setIsoFilename(file.getName());
                        this.imageText.setText(file.getAbsolutePath());
                        this.imageText.setToolTipText(file.getAbsolutePath());
                        file = null;
                    }
                }
                catch (Exception exception) {
                    RRCLogger.log(200, 1024, exception.getMessage());
                }
                bl = false;
            }
            if (object == this.connectButton) {
                if (!this.isDriveConnected) {
                    if (this.localDriveRadio.isSelected()) {
                        if (this.availableDrivesCombo.getModel().getSelectedItem() != null) {
                            this.connectDrive();
                        }
                    } else if (this.diskImageRadioButton.isSelected()) {
                        this.connectIso();
                    } else if (this.remoteImageRadioButton.isSelected()) {
                        this.connectSamba();
                    }
                }
                bl = true;
            }
            if (object == this.cancelButton) {
                this.getShell().setVisible(false);
                this.getShell().dispose();
                bl = true;
                if (virtualMediaBean != null && this.vmAdapter != null) {
                    virtualMediaBean.getVmCore().removeVirtualMediaEventListener(this.vmAdapter);
                    virtualMediaBean.getVmCore().removeNotificationListener(this.vmAdapter);
                }
            }
        }
        if (object instanceof JComboBox) {
            // empty if block
        }
        this.scrContext.getLogger().logTextDebug("finished");
    }

    private void populateRemoteIsoInfo() {
        this.hostsCombo.removeItemListener(this);
        Map map = this.scrContext.getVirtualMediaImageMap();
        VirtualMediaBean virtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
        List<IVMMountRequestResponse> list = virtualMediaBean.getRFBView().getRemoteIsoList();
        this.remoteIsos = new Hashtable();
        for (IVMMountRequestResponse iVMMountRequestResponse : list) {
            Vector<String> vector = this.remoteIsos.get(iVMMountRequestResponse.getHost());
            if (vector == null) {
                vector = new Vector();
            }
            vector.add(iVMMountRequestResponse.getImage());
            this.remoteIsos.put(iVMMountRequestResponse.getHost(), vector);
        }
        this.hostsModel = new DefaultComboBoxModel();
        String[] stringArray = this.remoteIsos.keySet().toArray(new String[0]);
        for (int i = 0; i < stringArray.length; ++i) {
            this.hostsModel.addElement(stringArray[i]);
        }
        this.hostsCombo.setModel(this.hostsModel);
        if (this.hostsCombo.getItemCount() > 0) {
            this.hostsCombo.setEnabled(true);
            this.imagesCombo.setEnabled(true);
            this.usersText.setEnabled(true);
            this.userPwdText.setEnabled(true);
            this.hostsCombo.addItemListener(this);
            this.itemStateChanged(new ItemEvent(this.hostsCombo, 701, this.hostsCombo, 1));
        }
    }

    private VirtualMediaBean setFileNameInBean(VirtualMediaBean virtualMediaBean, JTextField jTextField) {
        int n;
        if (jTextField != null && !jTextField.getText().equals("") && (n = System.getProperty("file.separator").equals("/") ? jTextField.getText().lastIndexOf("/", jTextField.getText().length()) : jTextField.getText().lastIndexOf("\\", jTextField.getText().length())) != -1) {
            virtualMediaBean.setIsoFilename(jTextField.getText().substring(n + 1));
            virtualMediaBean.setIsoDirectory(jTextField.getText().substring(0, n));
        }
        return virtualMediaBean;
    }

    public String extractImageName(String string) {
        String string2 = "";
        if (string != null && !string.equals("")) {
            int n = string.lastIndexOf(47);
            string2 = string.substring(n + 1);
        }
        return string2;
    }

    private void populateImagesCombo2() {
    }

    private void drive1RadioEnable(boolean bl) {
        this.localDriveRadio.setEnabled(Platform.isVirtualMediaSupported() ? bl : false);
        this.diskImageRadioButton.setEnabled(bl);
        this.remoteImageRadioButton.setEnabled(bl);
    }

    public int get_current_port() {
        Map map = this.scrContext.getVirtualMediaImageMap();
        VirtualMediaBean virtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
        return virtualMediaBean.getPort();
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
    }

    public static void main(String[] stringArray) {
        VirtualMediaImagePanel virtualMediaImagePanel = new VirtualMediaImagePanel(true, null);
        virtualMediaImagePanel.makeLayout();
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (this.remoteImageRadioButton.isSelected()) {
            String string;
            if (itemEvent.getSource() == this.imagesCombo) {
                string = (String)this.imagesCombo.getModel().getSelectedItem();
                this.imagesCombo.setToolTipText(string);
            }
            if (itemEvent.getSource() == this.hostsCombo) {
                string = this.hostsCombo.getSelectedItem().toString();
                Vector<String> vector = this.remoteIsos.get(string);
                this.hostsCombo.setToolTipText((String)this.hostsCombo.getModel().getSelectedItem());
                this.imagesModel = new DefaultComboBoxModel();
                for (String string2 : vector) {
                    this.imagesModel.addElement(string2);
                }
                this.imagesCombo.setModel(this.imagesModel);
            }
        }
    }

    public String[] getTokens() {
        Map map = this.scrContext.getVirtualMediaImageMap();
        VirtualMediaBean virtualMediaBean = null;
        String string = "";
        if (map != null) {
            virtualMediaBean = (VirtualMediaBean)map.get(this.portKey);
        }
        if (virtualMediaBean != null && virtualMediaBean.getRFBView() != null) {
            string = virtualMediaBean.getRFBView().getPortName();
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

    private class isoFileFilter
    extends FileFilter {
        private isoFileFilter() {
        }

        @Override
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".iso") || file.isDirectory();
        }

        @Override
        public String getDescription() {
            return VirtualMediaImagePanel.this.bundle.getString("filetype.iso");
        }
    }
}

