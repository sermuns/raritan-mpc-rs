/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.Applet
 *  netscape.javascript.JSObject
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.ProductCodes;
import com.raritan.rrc.ui.RRCApplet;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoLoginCommand;
import com.raritan.rrc.ui.commands.ShowDialbackMessageCommand;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.util.ModemOSSupport;
import com.raritan.rrc.util.TaskCompletionNotifier;
import com.raritan.rrc.util.modem.DialStatusListener;
import com.raritan.rrc.util.modem.ModemConnector;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.applet.Applet;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.MissingResourceException;
import javaclientlib.utils.RRCLogger;
import javax.swing.SwingUtilities;
import netscape.javascript.JSObject;

public class DoDialCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "DoDialCommand";
    private RaritanPropertyResourceBundle bundle;
    private Device device;

    public DoDialCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void doExecute(CommandResult commandResult) {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0) {
            this.device = (Device)arrayList.get(0);
            String string = new String(((IPReach)this.device).getConnectionInfo().getUserInfo().getName());
            String string2 = new String(((IPReach)this.device).getConnectionInfo().getUserInfo().getPassword());
            DevicePreferences devicePreferences = this.device.getDevPrefs();
            if (string == null || string.trim().equals("")) {
                commandResult.setStatusMessage("[" + devicePreferences.getPhone() + "]: " + this.bundle.getString("Device.emptyUsername"));
                commandResult.setIsSuccess(false);
                return;
            }
            ModemConnector modemConnector = devicePreferences.getModemConnector();
            if (modemConnector != null) {
                CommonPopups.showInfoDialog(this.bundle.getString("dial.confirm.title"), this.bundle.getString("dial.confirm.text"), null, this.scrContext);
                commandResult.setIsSuccess(false);
                return;
            }
            Object object = null;
            if (this.scrContext.getApplication() instanceof RRCApplet) {
                object = JSObject.getWindow((Applet)((RRCApplet)this.scrContext.getApplication()));
            }
            modemConnector = ModemConnector.getConnector(object, this.scrContext.getLocale());
            devicePreferences.setModemConnector(modemConnector);
            object = devicePreferences.getPhone();
            String string3 = devicePreferences.getModem();
            this.scrContext.getLogger().logStatus(this.bundle.getString("dial.start.msg") + (String)object);
            ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(this.bundle.getString("dial.start.msg") + (String)object);
            this.scrContext.getLogger().logTextInfo(this.bundle.getString("dial.start.msg") + (String)object);
            this.device.setCancelLogin(false);
            final TaskCompletionNotifier taskCompletionNotifier = new TaskCompletionNotifier(this);
            try {
                DialStatusListener dialStatusListener = new DialStatusListener(){

                    @Override
                    public void dialingStatus(final String string) {
                        SwingUtilities.invokeLater(new Runnable(){

                            @Override
                            public void run() {
                                DoDialCommand.this.scrContext.getLogger().logStatus(string);
                                DoDialCommand.this.scrContext.getLogger().logTextInfo(string);
                                ShowDialbackMessageCommand showDialbackMessageCommand = new ShowDialbackMessageCommand(DoDialCommand.this.scrContext);
                                CommandContext commandContext = showDialbackMessageCommand.getContext();
                                commandContext.setCommandParameter("dialbackMessagePanel.message", string);
                                commandContext.setCommandParameter("devices", DoDialCommand.this.device);
                                commandContext.setCommandParameter("taskCompletionNotifier", taskCompletionNotifier);
                                showDialbackMessageCommand.execute();
                            }
                        });
                    }
                };
                modemConnector.addDialStatusListener(dialStatusListener);
                long l = -1L;
                try {
                    l = ProductCodes.getInstance().isKSXG2(devicePreferences.getProductType()) || ProductCodes.getInstance().isKX101G2(devicePreferences.getProductType()) || ProductCodes.getInstance().isKXG2(devicePreferences.getProductType()) || ProductCodes.getInstance().isDominionLX(devicePreferences.getProductType()) ? modemConnector.rasConnectWithIdentity((String)object, string3, string, string2) : modemConnector.rasConnect((String)object, string3);
                }
                catch (Exception exception) {
                    commandResult.setIsSuccess(false);
                    RRCLogger.log(300, 4, exception, "Exception on modem connect");
                    devicePreferences.setModemConnector(null);
                    modemConnector.rasHangUp();
                    modemConnector = null;
                    taskCompletionNotifier.fireTaskCompleted();
                    return;
                }
                finally {
                    if (modemConnector != null) {
                        modemConnector.removeDialStatusListener(dialStatusListener);
                    }
                }
                if (l != 0L) {
                    if (this.device.isCancelLogin()) {
                        RRCLogger.log(300, 4, "Modem connection Cancelled.");
                        commandResult.setIsSuccess(true);
                        return;
                    }
                    String string4 = this.bundle.getString("dial.error.general") + " " + l + ": " + this.getMessageForCode(l);
                    RRCLogger.log(300, 4, "modem connection failed. Message : " + string4);
                    commandResult.setIsSuccess(false);
                    commandResult.setStatusMessage(string4);
                    devicePreferences.setModemConnector(null);
                    modemConnector.rasHangUp();
                    modemConnector = null;
                    return;
                }
                Object object2 = this.bundle.getString("dial.connected.msg") + " " + (String)object;
                commandResult.setIsSuccess(true);
                commandResult.setStatusMessage((String)object2);
                RRCLogger.log(300, 4, "Modem connection sucessfull.");
                this.device.setIPPort(devicePreferences.getPort());
                object2 = null;
                try {
                    object2 = InetAddress.getByName(modemConnector.rasGetServerIP());
                }
                catch (UnknownHostException unknownHostException) {
                    assert (false) : "rasGetServerIP returned a value other than IP address";
                    unknownHostException.printStackTrace();
                }
                this.device.setAddressList(Arrays.asList(object2));
                this.device.setState("DEVICE_DISCONNECTED");
                this.device.firePropertyChange("DEVICE_DISCONNECTED", null, null);
                RRCLogger.log(300, 4, "Attempting DoLoginCommand after modem connection");
                DoLoginCommand doLoginCommand = new DoLoginCommand(this.scrContext);
                doLoginCommand.getContext().setCommandParameter("devices", this.device);
                CommandResult commandResult2 = doLoginCommand.execute();
                commandResult.setIsSuccess(commandResult2.isSuccess());
                commandResult.setStatusMessage(commandResult2.getStatusMessage());
                if (!commandResult2.isSuccess()) {
                    RRCLogger.log(300, 4, "Failure on DoLoginCommand after modem connection. Message : " + commandResult2.getStatusMessage());
                    devicePreferences.setModemConnector(null);
                    modemConnector.rasHangUp();
                    modemConnector = null;
                    return;
                }
                RRCLogger.log(300, 4, "Sucess on DoLoginCommand after modem connection");
            }
            finally {
                taskCompletionNotifier.fireTaskCompleted();
            }
        }
        commandResult.setIsSuccess(true);
    }

    @Override
    public boolean isExecutable() {
        Device device;
        DevicePreferences devicePreferences;
        if (!ModemOSSupport.isOSSupported()) {
            return false;
        }
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        return arrayList == null || arrayList.size() <= 0 || (devicePreferences = (device = (Device)arrayList.get(0)).getDevPrefs()) == null || devicePreferences.getModemConnector() == null;
    }

    @Override
    protected boolean executeWithBlocking() {
        return false;
    }

    private String translateRasErrorCode(long l) {
        if (l < 600L || l > 745L) {
            if (l == 752L) {
                return "dial.error.msg.752";
            }
            return "dial.error.msg.default";
        }
        return "dial.error.msg." + l;
    }

    private String getMessageForCode(long l) {
        try {
            return this.bundle.getString("dial.error.msg." + l);
        }
        catch (MissingResourceException missingResourceException) {
            return this.bundle.getString("dial.error.msg.default");
        }
    }
}

