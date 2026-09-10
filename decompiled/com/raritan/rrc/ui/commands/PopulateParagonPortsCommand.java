/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceFactory;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class PopulateParagonPortsCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "populateParagonPortsCommand";
    private RaritanPropertyResourceBundle bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    private Paragon device = null;

    public PopulateParagonPortsCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        this.device = (Paragon)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
        this.device.setContext(this.scrContext);
        DeviceFactory deviceFactory = DeviceFactory.getInstance();
        try {
            deviceFactory.updateDevice(this.device, this.device.getDocument(), this.scrContext, false);
            this.device.connect();
            DeviceTreeController.getInstance((RRCScreenContext)this.scrContext).populateParagonPorts(this.device);
            if (this.device.hasChildren()) {
                this.device.firePropertyChange("DEVICE_PORTS_ADD", null, null);
            }
        }
        catch (ParserConfigurationException parserConfigurationException) {
            this.processXMLError();
        }
        catch (SAXException sAXException) {
            this.processXMLError();
        }
        catch (IOException iOException) {
            this.processXMLError();
        }
        return commandResult;
    }

    private void processXMLError() {
        if (this.scrContext != null && this.bundle != null && this.device != null) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(PopulateParagonPortsCommand.this.scrContext.getLocale());
                    if (raritanPropertyResourceBundle != null) {
                        CommonPopups.showCommandResultErrorMessage("[" + PopulateParagonPortsCommand.this.device.getNameIP() + "]: " + raritanPropertyResourceBundle.getString("corruptXmlError.message"), null, PopulateParagonPortsCommand.this.scrContext);
                    }
                }
            });
        }
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) instanceof Paragon) {
            return !device.isConnected();
        }
        return false;
    }
}

