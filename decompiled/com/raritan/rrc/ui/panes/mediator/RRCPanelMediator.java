/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes.mediator;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.VMInterfaceInfo;
import com.raritan.rrc.data.VirtualMediaBean;
import com.raritan.rrc.data.VirtualMediaLocalBean;
import com.raritan.rrc.ui.RRCApplet;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoSingleMouseModeCommand;
import com.raritan.rrc.ui.commands.ShowTargetScreenResolutionCommand;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.ui.panes.AboutPanel;
import com.raritan.rrc.ui.panes.AddConnectionPanel;
import com.raritan.rrc.ui.panes.AddKeyboardMacroPanel;
import com.raritan.rrc.ui.panes.AppletView;
import com.raritan.rrc.ui.panes.AudioPanel;
import com.raritan.rrc.ui.panes.AudioSettingsPanel;
import com.raritan.rrc.ui.panes.ChangePasswordPanel;
import com.raritan.rrc.ui.panes.ConnectionInfoPanel;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.DialbackMessagePanel;
import com.raritan.rrc.ui.panes.G2PropertiesPanel;
import com.raritan.rrc.ui.panes.G2VideoSettingsPanel;
import com.raritan.rrc.ui.panes.KeyboardMacroPanel;
import com.raritan.rrc.ui.panes.KvmView;
import com.raritan.rrc.ui.panes.LoadDeviceConfigurationPanel;
import com.raritan.rrc.ui.panes.LoginPanel;
import com.raritan.rrc.ui.panes.MacroExportSelectionPanel;
import com.raritan.rrc.ui.panes.MacroImportSelectionPanel;
import com.raritan.rrc.ui.panes.MacroTextInterpreterPanel;
import com.raritan.rrc.ui.panes.ModifyConnectionPanel;
import com.raritan.rrc.ui.panes.ModifyKeyboardMacroPanel;
import com.raritan.rrc.ui.panes.OptionsPanel;
import com.raritan.rrc.ui.panes.PowerPortView;
import com.raritan.rrc.ui.panes.ProgressPanel;
import com.raritan.rrc.ui.panes.PropertiesPanel;
import com.raritan.rrc.ui.panes.RSCView;
import com.raritan.rrc.ui.panes.SaveActivityLogPanel;
import com.raritan.rrc.ui.panes.SaveDeviceConfigurationPanel;
import com.raritan.rrc.ui.panes.SaveDiagnosticLogPanel;
import com.raritan.rrc.ui.panes.SaveTotalConfigurationPanel;
import com.raritan.rrc.ui.panes.SaveUserConfigurationPanel;
import com.raritan.rrc.ui.panes.SendTextToTargetPanel;
import com.raritan.rrc.ui.panes.SerialParametersPanel;
import com.raritan.rrc.ui.panes.SerialSettingsPanel;
import com.raritan.rrc.ui.panes.SerialView;
import com.raritan.rrc.ui.panes.SingleCursorInstructionsPanel;
import com.raritan.rrc.ui.panes.SmartCardAutoMountPanel;
import com.raritan.rrc.ui.panes.SmartCardPanel;
import com.raritan.rrc.ui.panes.StartLoggingPanel;
import com.raritan.rrc.ui.panes.UpdateDevicePanel;
import com.raritan.rrc.ui.panes.UpdateLDAPCertificatePanel;
import com.raritan.rrc.ui.panes.UpdateLDAPKeyPanel;
import com.raritan.rrc.ui.panes.VideoSettingsPanel;
import com.raritan.rrc.ui.panes.VirtualMediaDisconnectImagePanel;
import com.raritan.rrc.ui.panes.VirtualMediaDisconnectLocalPanel;
import com.raritan.rrc.ui.panes.VirtualMediaImagePanel;
import com.raritan.rrc.ui.panes.VirtualMediaLocalPanel;
import com.raritan.rrc.ui.rfbbridge.RFBProfile;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.ui.panes.LogPanel;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.AbstractDisplayStrategy;
import com.raritan.tools.ui.panes.displays.Shell;
import com.raritan.tools.ui.panes.displays.ShellFocusObserver;
import com.raritan.tools.ui.panes.mediator.PanelMediator;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javaclientlib.utils.RRCGeneralException;
import javaclientlib.utils.RRCLogger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import nn.pp.core.Platform;

public class RRCPanelMediator
extends PanelMediator {
    private KeyboardMacroPanel mainKeyboardMacroPanel = null;
    private List listOfDialogs = Collections.synchronizedList(new ArrayList());
    RaritanPropertyResourceBundle bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    public static final WindowAdapter postLinuxWindowAdapter = new WindowAdapter(){

        @Override
        public void windowDeactivated(WindowEvent windowEvent) {
            Window window = windowEvent.getWindow();
            if (windowEvent.getOppositeWindow() == null) {
                RRCApplet.RaritanKeyboardFocusManager raritanKeyboardFocusManager = (RRCApplet.RaritanKeyboardFocusManager)KeyboardFocusManager.getCurrentKeyboardFocusManager();
                raritanKeyboardFocusManager.setGlobalActiveWindow(window);
                raritanKeyboardFocusManager.setGlobalFocusedWindow(window);
            }
            window.removeWindowListener(this);
        }
    };
    private Map vmLocalMap;
    private Map vmImageMap;
    private boolean library_downloaded;

    public RRCPanelMediator(Container container, ScreenContext screenContext) {
        super(container, screenContext);
    }

    @Override
    protected void initDialogPanels() {
        this.panelClasses.put("showAboutCommand", new AbstractDisplayStrategy(AboutPanel.class, true, this.scrContext, true));
        this.panelClasses.put("showSaveTotalConfigurationCommand", new AbstractDisplayStrategy(SaveTotalConfigurationPanel.class, true, this.scrContext));
        this.panelClasses.put("showSaveDeviceConfigurationCommand", new AbstractDisplayStrategy(SaveDeviceConfigurationPanel.class, true, this.scrContext));
        this.panelClasses.put("showSaveUserConfigurationCommand", new AbstractDisplayStrategy(SaveUserConfigurationPanel.class, true, this.scrContext));
        this.panelClasses.put("showSaveActivityLogCommand", new AbstractDisplayStrategy(SaveActivityLogPanel.class, true, this.scrContext));
        this.panelClasses.put("showOptionsCommand", new AbstractDisplayStrategy(OptionsPanel.class, true, this.scrContext));
        this.panelClasses.put("showSaveDiagnosticLogCommand", new AbstractDisplayStrategy(SaveDiagnosticLogPanel.class, true, this.scrContext));
        this.panelClasses.put("showLoadDeviceConfigurationCommand", new AbstractDisplayStrategy(LoadDeviceConfigurationPanel.class, true, this.scrContext));
        this.panelClasses.put("showConnectCommand", new AbstractDisplayStrategy(LoginPanel.class, true, this.scrContext));
        this.panelClasses.put("showNewProfileCommand", new AbstractDisplayStrategy(AddConnectionPanel.class, true, this.scrContext));
        this.panelClasses.put("showModifyProfileCommand", new AbstractDisplayStrategy(ModifyConnectionPanel.class, true, this.scrContext));
        this.panelClasses.put("showAddProfileCommand", new AbstractDisplayStrategy(AddConnectionPanel.class, true, this.scrContext));
        this.panelClasses.put("showUpdateDeviceCommand", new AbstractDisplayStrategy(UpdateDevicePanel.class, true, this.scrContext));
        this.panelClasses.put("showUpdateLDAPCertificateCommand", new AbstractDisplayStrategy(UpdateLDAPCertificatePanel.class, true, this.scrContext));
        this.panelClasses.put("showUpdateLDAPKeyCommand", new AbstractDisplayStrategy(UpdateLDAPKeyPanel.class, true, this.scrContext));
        this.panelClasses.put("showSerialPortCommand", new AbstractDisplayStrategy(SerialView.class, false, this.scrContext, true));
        this.panelClasses.put("showKX2G2SerialPortCommand", new AbstractDisplayStrategy(RSCView.class, false, this.scrContext));
        this.panelClasses.put("showHtmlPortCommand", new AbstractDisplayStrategy(AppletView.class, false, this.scrContext));
        this.panelClasses.put("showURLPortCommand", new AbstractDisplayStrategy(AppletView.class, false, this.scrContext));
        this.panelClasses.put("showPowerPortCommand", new AbstractDisplayStrategy(PowerPortView.class, false, this.scrContext));
        this.panelClasses.put("showOutletPortCommand", new AbstractDisplayStrategy(PowerPortView.class, true, this.scrContext));
        this.panelClasses.put("showVideoSettingsCommandKX1", new AbstractDisplayStrategy(VideoSettingsPanel.class, true, this.scrContext));
        this.panelClasses.put("showVideoSettingsCommandKX2", new AbstractDisplayStrategy(G2VideoSettingsPanel.class, true, this.scrContext));
        this.panelClasses.put("showSerialParametersCommand", new AbstractDisplayStrategy(SerialParametersPanel.class, true, this.scrContext));
        this.panelClasses.put("showSerialSettingsCommand", new AbstractDisplayStrategy(SerialSettingsPanel.class, true, this.scrContext));
        this.panelClasses.put("showSingleCursorInstructionCommand", new AbstractDisplayStrategy(SingleCursorInstructionsPanel.class, true, this.scrContext));
        this.panelClasses.put("showKeyboardMacrosCommand", new AbstractDisplayStrategy(KeyboardMacroPanel.class, true, this.scrContext));
        this.panelClasses.put("showAddKeyboardMacroCommand", new AbstractDisplayStrategy(AddKeyboardMacroPanel.class, true, this.scrContext));
        this.panelClasses.put("showModifyKeyboardMacroCommand", new AbstractDisplayStrategy(ModifyKeyboardMacroPanel.class, true, this.scrContext));
        this.panelClasses.put("showConnectionInfoCommand", new AbstractDisplayStrategy(ConnectionInfoPanel.class, true, this.scrContext));
        this.panelClasses.put("doUpdateDeviceCommand", new AbstractDisplayStrategy(ProgressPanel.class, true, this.scrContext));
        this.panelClasses.put("showChangePasswordCommand", new AbstractDisplayStrategy(ChangePasswordPanel.class, true, this.scrContext));
        this.panelClasses.put("showUserPasswordCommand", new AbstractDisplayStrategy(ChangePasswordPanel.class, true, this.scrContext));
        this.panelClasses.put("showStartLoggingCommand", new AbstractDisplayStrategy(StartLoggingPanel.class, true, this.scrContext));
        this.panelClasses.put("ShowDialbackMessageCommand", new AbstractDisplayStrategy(DialbackMessagePanel.class, true, this.scrContext));
        this.panelClasses.put("showImportMacrosCommand", new AbstractDisplayStrategy(MacroImportSelectionPanel.class, true, this.scrContext));
        this.panelClasses.put("showExportMacrosCommand", new AbstractDisplayStrategy(MacroExportSelectionPanel.class, true, this.scrContext));
        this.panelClasses.put("showSendtextToTargetCommand", new AbstractDisplayStrategy(SendTextToTargetPanel.class, true, this.scrContext));
        this.panelClasses.put("showMacroTextInterpreterCommand", new AbstractDisplayStrategy(MacroTextInterpreterPanel.class, true, this.scrContext));
        this.panelClasses.put("SelectCardReaderCommand", new AbstractDisplayStrategy(SmartCardPanel.class, true, this.scrContext));
        this.panelClasses.put("SmartCardAutoMountCommand", new AbstractDisplayStrategy(SmartCardAutoMountPanel.class, true, this.scrContext, true));
        this.panelClasses.put("ConnectAudioCommand", new AbstractDisplayStrategy(AudioPanel.class, true, this.scrContext));
        this.panelClasses.put("AudioSettingsCommand", new AbstractDisplayStrategy(AudioSettingsPanel.class, true, this.scrContext));
        System.runFinalization();
        System.gc();
    }

    @Override
    protected void initContainerPanels() {
        this.panelClasses.put("logCommand", new AbstractDisplayStrategy(LogPanel.class, false, this.scrContext));
        this.panelClasses.put("statusCommand", new AbstractDisplayStrategy(RRCStatusBar.class, false, this.scrContext));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public synchronized void showPanel(CommandContext commandContext) {
        block58: {
            Device device;
            block60: {
                DeviceView deviceView;
                Object object;
                block59: {
                    Object object2;
                    Container container;
                    Object object3;
                    String string;
                    block57: {
                        block56: {
                            ArrayList arrayList;
                            Object object4;
                            Object object5;
                            RFBView rFBView;
                            ArrayList arrayList2;
                            if (commandContext == null) {
                                return;
                            }
                            string = commandContext.getCommandKey();
                            object = null;
                            deviceView = null;
                            this.scrContext.getLogger().logTextDebug(" Invoked with command key " + string);
                            device = null;
                            ArrayList arrayList3 = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
                            if (arrayList3 != null && arrayList3.size() > 0) {
                                device = (Device)arrayList3.get(0);
                            }
                            if ("showSingleCursorInstructionCommand".equals(string)) {
                                boolean bl;
                                boolean bl2 = ((RRCScreenContext)this.scrContext).getAppSettings().isSingleMouseInstructions();
                                if (device != null && device instanceof Port && device.isConnected() && device.getDeviceClass().equals("KVM") && ((bl = ((KvmPort)device).isSingleCursorMode()) || !bl2)) {
                                    DoSingleMouseModeCommand doSingleMouseModeCommand = new DoSingleMouseModeCommand(this.scrContext);
                                    doSingleMouseModeCommand.execute();
                                    return;
                                }
                            }
                            if (string.equals("showPropertiesCommand") && device != null && device instanceof Port && device.isConnected()) {
                                if (device.getDeviceConnector().isKX2Device()) {
                                    Port port;
                                    DeviceView deviceView2;
                                    if (arrayList3 != null && arrayList3.size() != 0 && arrayList3.get(0) instanceof Port && (object3 = (RFBView)(deviceView2 = (port = (Port)arrayList3.get(0)).getView())) != null) {
                                        object = new G2PropertiesPanel(true, this.scrContext, (RFBView)object3, port.getPortKey());
                                    }
                                } else {
                                    object = new PropertiesPanel(true, this.scrContext);
                                }
                            }
                            if (string.equals("showVirtualMediaLocalPanelCommand") && (arrayList2 = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()) != null && arrayList2.size() != 0 && arrayList2.get(0) instanceof Port) {
                                object3 = (Port)arrayList2.get(0);
                                String string2 = ((Port)object3).getPortKey();
                                this.vmLocalMap = this.scrContext.getVirtualMediaLocalMap();
                                if (this.vmLocalMap == null) {
                                    this.vmLocalMap = new HashMap();
                                }
                                container = ((Port)object3).getView();
                                object2 = null;
                                rFBView = (RFBView)container;
                                if (rFBView != null) {
                                    object2 = rFBView.getProfile();
                                }
                                if (!this.vmLocalMap.containsKey(string2)) {
                                    RRCLogger.log(300, 1, "Creating new bean for VM Panel for port " + string2);
                                    object5 = new VirtualMediaLocalBean((VMInterfaceInfo)commandContext.getCommandParameter("VM_vminterfaceinfo"));
                                    ((VirtualMediaLocalBean)object5).setPortKey(string2);
                                    ((VirtualMediaLocalBean)object5).setPort(((Port)object3).getPortIndex());
                                    if (rFBView != null) {
                                        ((VirtualMediaLocalBean)object5).setRFBView(rFBView);
                                    } else {
                                        RRCLogger.log(300, 1, "Error Setting handler in VmBean for Port: " + string2);
                                    }
                                    try {
                                        if (object2 != null) {
                                            ((VirtualMediaLocalBean)object5).setRFBProfile((RFBProfile)object2);
                                            object2 = ((VirtualMediaLocalBean)object5).getRFBProfile();
                                        } else {
                                            RRCLogger.log(300, 1024, "Error! Profile is NULL " + string2);
                                        }
                                    }
                                    catch (Exception exception) {
                                        RRCLogger.log(300, 1024, exception.getMessage());
                                    }
                                    this.vmLocalMap.put(string2, object5);
                                    this.scrContext.setVitualMediaLocalMap(this.vmLocalMap);
                                } else {
                                    object5 = (VirtualMediaLocalBean)this.vmLocalMap.get(string2);
                                    if (object5 == null) {
                                        this.checkReturnToFullScreen(commandContext);
                                        RRCLogger.log(300, 1024, "VM Bean is NULL while disconnecting" + string2);
                                        return;
                                    }
                                    if (((VirtualMediaLocalBean)object5).isDriveConnected()) {
                                        object = new VirtualMediaDisconnectLocalPanel(this.scrContext, string2, (VirtualMediaLocalBean)object5);
                                    }
                                }
                                if (object == null) {
                                    object5 = new VirtualMediaLocalPanel(true, this.scrContext);
                                    object4 = (VirtualMediaLocalBean)this.vmLocalMap.get(string2);
                                    if (((VirtualMediaLocalPanel)object5).populateVMPanel((VirtualMediaLocalBean)object4)) {
                                        ((AbstractDisplay)object5).setShell(this.bundle.getString("vmpanel.LOCAL_PANEL_NAME") + " " + ((Device)object3).getName());
                                        object = object5;
                                    } else {
                                        CommonPopups.showInfoDialog(this.bundle.getString("vmpanel.nodrives.title"), this.bundle.getString("vmpanel.nodrives.text"), (AbstractDisplay)container, this.scrContext);
                                    }
                                }
                            }
                            if (string.equals("showVirtualMediaPanelCommand") && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()) != null && arrayList.size() != 0 && arrayList.get(0) instanceof Port) {
                                object3 = (Port)arrayList.get(0);
                                String string3 = ((Port)object3).getPortKey();
                                this.vmImageMap = this.scrContext.getVirtualMediaImageMap();
                                if (this.vmImageMap == null) {
                                    this.vmImageMap = new HashMap();
                                }
                                container = ((Port)object3).getView();
                                object2 = null;
                                rFBView = (RFBView)container;
                                if (rFBView != null) {
                                    object2 = rFBView.getProfile();
                                }
                                if (!this.vmImageMap.containsKey(string3)) {
                                    RRCLogger.log(200, 1, "Creating new bean for VM Panel for port " + string3);
                                    object5 = new VirtualMediaBean((VMInterfaceInfo)commandContext.getCommandParameter("VM_vminterfaceinfo"));
                                    ((VirtualMediaBean)object5).setPortKey(string3);
                                    ((VirtualMediaBean)object5).setPort(((Port)object3).getPortIndex());
                                    if (rFBView != null) {
                                        ((VirtualMediaBean)object5).setRFBView(rFBView);
                                    } else {
                                        RRCLogger.log(200, 1, "Error Setting handler in VmBean for Port: " + string3);
                                    }
                                    try {
                                        if (object2 != null) {
                                            ((VirtualMediaBean)object5).setRFBProfile((RFBProfile)object2);
                                            object2 = ((VirtualMediaBean)object5).getRFBProfile();
                                        } else {
                                            RRCLogger.log(200, 1024, "Error! Profile is NULL " + string3);
                                        }
                                    }
                                    catch (Exception exception) {
                                        RRCLogger.log(200, 1024, exception.getMessage());
                                    }
                                    this.vmImageMap.put(string3, object5);
                                    this.scrContext.setVitualMediaImageMap(this.vmImageMap);
                                } else {
                                    object5 = this.scrContext.getVirtualMediaImageMap();
                                    if (object5 == null) {
                                        this.checkReturnToFullScreen(commandContext);
                                        RRCLogger.log(300, 1, "VMImage Map is null when Disconnecting." + string3);
                                        return;
                                    }
                                    object4 = (VirtualMediaBean)object5.get(string3);
                                    if (object4 == null) {
                                        this.checkReturnToFullScreen(commandContext);
                                        RRCLogger.log(300, 1, "VMImage Bean is null when Disconnecting." + string3);
                                        return;
                                    }
                                    if (((VirtualMediaBean)object4).isDriveConnected()) {
                                        object = new VirtualMediaDisconnectImagePanel(this.scrContext, string3, (VirtualMediaBean)object4, rFBView);
                                    }
                                }
                                if (object == null) {
                                    object5 = new VirtualMediaImagePanel(true, this.scrContext);
                                    object4 = (VirtualMediaBean)this.vmImageMap.get(string3);
                                    ((VirtualMediaBean)object4).setVmMountRequestResponseList(rFBView.getVMShareTable());
                                    ((VirtualMediaImagePanel)object5).populateVMPanel((VirtualMediaBean)object4);
                                    ((AbstractDisplay)object5).setShell(this.bundle.getString("vmpanel.IMAGE_PANEL_NAME") + " " + ((Device)object3).getName());
                                    object = object5;
                                }
                            }
                            if (!string.equals("showKvmPortCommand") && !string.equals("doSwitchCommand") && !string.equals("showKX2KvmPortCommand") && !string.equals("showHtmlPortCommand") && !string.equals("showURLPortCommand") && !string.equals("showKVMFromScanCommand")) break block56;
                            if (device != null) {
                                try {
                                    Device device2 = ((Port)device).getDevice();
                                    object = device2.getHandler().initializeDeviceView(commandContext, string, this);
                                }
                                catch (RRCGeneralException rRCGeneralException) {
                                    JOptionPane jOptionPane = new JOptionPane(this.bundle.getString("View.error.fail.create") + System.getProperty("line.separator") + rRCGeneralException.getMessage(), 0, -1, null, new Object[]{this.bundle.getString("basescreen.command.ok.text")});
                                    object3 = jOptionPane.createDialog(null, this.bundle.getString("optionpane.error.title"));
                                    ((Dialog)object3).setModal(true);
                                    ((Dialog)object3).setVisible(true);
                                    RRCLogger.logException(rRCGeneralException);
                                }
                                deviceView = (DeviceView)object;
                                break block57;
                            } else {
                                RRCLogger.log(300, 16, "Device is null");
                            }
                            break block57;
                        }
                        if ("showVideoSettingsCommand".equals(string)) {
                            DeviceView deviceView3 = (DeviceView)commandContext.getCommandParameter("DEVICE_VIEW");
                            object = deviceView3 instanceof KvmView ? this.getTarget(string + "KX1") : this.getTarget(string + "KX2");
                        } else if (!(string.equals("showVirtualMediaLocalPanelCommand") || string.equals("showPropertiesCommand") || string.equals("showVirtualMediaPanelCommand"))) {
                            object = this.getTarget(string);
                        }
                    }
                    if (object == null) break block58;
                    ((AbstractDisplay)object).setCommandContext(commandContext);
                    ((AbstractDisplay)object).fillComponents(commandContext);
                    if (deviceView != null && !string.equals("showVirtualMediaLocalPanelCommand")) {
                        deviceView.customizeTarget((AbstractDisplay)object);
                    }
                    if (!((AbstractDisplay)object).isDialog()) break block59;
                    ((AbstractDisplay)object).onActivateDisplay();
                    Shell shell = ((AbstractDisplay)object).getShell();
                    boolean bl = KeyboardFocusManager.getCurrentKeyboardFocusManager() instanceof RRCApplet.RaritanKeyboardFocusManager;
                    object3 = new RRCApplet.LinuxWindowDeactivatedHandler();
                    if (bl) {
                        shell.addWindowListener((WindowListener)object3);
                    }
                    container = JOptionPane.getFrameForComponent(shell);
                    shell.setLocationRelativeTo((Window)shell.getParent());
                    shell.setVisible(true);
                    if (bl) {
                        shell.removeWindowListener((WindowListener)object3);
                        ((Window)container).addWindowListener(postLinuxWindowAdapter);
                    }
                    if (Platform.isWindows() && (object2 = (Window)shell.getParent()) != null && "sun.plugin.viewer.frame.WNetscapeEmbeddedFrame".equals(object2.getClass().getName())) {
                        ((Component)object2).transferFocus();
                    }
                    break block58;
                }
                if (!(device instanceof Port)) break block58;
                if (this.scrContext.getApplicationProperty("connection") == null && !((Port)device).getBaseDevice().isConnected()) break block60;
                RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.parent;
                raritanDesktopPane.addAbstractDisplayComponent((AbstractDisplay)object);
                if (deviceView != null) {
                    deviceView.startVideo();
                    if (this.scrContext.getApplicationProperty("connection") != null && raritanDesktopPane.getSelectedFrame() != null && !raritanDesktopPane.getSelectedFrame().isMaximum()) {
                        try {
                            raritanDesktopPane.getSelectedFrame().setMaximum(true);
                        }
                        catch (PropertyVetoException propertyVetoException) {
                            RRCLogger.log(200, 512, "PropertyVetoException: system will not allow us to maximize the target window");
                        }
                    }
                }
                break block58;
            }
            RRCLogger.log(200, 4, "Device was disconnected before connecting/switching to the port");
            device.disconnect();
        }
        this.scrContext.getLogger().logTextDebug(" Finished");
    }

    public KeyboardMacroPanel getMainKeyboardMacroPanel() {
        return this.mainKeyboardMacroPanel;
    }

    public void setMainKeyboardMacroPanel(KeyboardMacroPanel keyboardMacroPanel) {
        this.mainKeyboardMacroPanel = keyboardMacroPanel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object showDialogOnTop(JOptionPane jOptionPane, String string, AbstractDisplay abstractDisplay) {
        JDialog jDialog = jOptionPane.createDialog(abstractDisplay == null ? this.getParent() : abstractDisplay, string);
        Object object = this.listOfDialogs;
        synchronized (object) {
            this.listOfDialogs.add(jDialog);
        }
        if (abstractDisplay != null) {
            jDialog.addWindowFocusListener(abstractDisplay);
        }
        MPCUtil.jre17WorkaroundInheritAlwaysOnTop(jDialog);
        jDialog.setModal(true);
        ShellFocusObserver.addShellVisible(jDialog, this.scrContext);
        jDialog.setVisible(true);
        ShellFocusObserver.removeShellVisible(jDialog, this.scrContext);
        object = jOptionPane.getValue();
        List list = this.listOfDialogs;
        synchronized (list) {
            this.listOfDialogs.remove(jDialog);
        }
        jDialog.dispose();
        return object;
    }

    @Override
    public void hideAll() {
        super.hideAll();
        this.hideDialogs();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void hideDialogs() {
        List list = this.listOfDialogs;
        synchronized (list) {
            for (JDialog jDialog : this.listOfDialogs) {
                jDialog.dispose();
            }
            this.listOfDialogs.clear();
        }
    }

    public void checkReturnToFullScreen(CommandContext commandContext) {
        ShowTargetScreenResolutionCommand showTargetScreenResolutionCommand;
        boolean bl;
        Boolean bl2 = (Boolean)commandContext.getCommandParameter("returnInFullscreenMode");
        boolean bl3 = bl = bl2 != null ? bl2 : false;
        if (bl && (showTargetScreenResolutionCommand = new ShowTargetScreenResolutionCommand(this.scrContext)).isExecutable()) {
            showTargetScreenResolutionCommand.execute();
        }
    }
}

