/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;
import javaclientlib.utils.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DoPowerOnCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doPowerOnCommand";

    public DoPowerOnCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public void doExecute(CommandResult commandResult) {
        this.scrContext.getLogger().logTextDebug(" Started ");
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (!arrayList.isEmpty()) {
            Port port = (Port)arrayList.get(0);
            String string = port.getDeviceConnector().databaseRequest("<Power><On>" + port.getStripTargetDeviceId() + "</On></Power>", 60000);
            boolean bl = false;
            if (string != null) {
                Document document = null;
                try {
                    document = XMLParser.getXMLDocument(string);
                }
                catch (SAXException sAXException) {
                    RRCLogger.log(300, 1, sAXException, "Invalid xml response " + string);
                    this.setFailureResult(commandResult, port.getName());
                    return;
                }
                if (document != null) {
                    NodeList nodeList = document.getElementsByTagName("Error");
                    if (nodeList.getLength() == 0) {
                        return;
                    }
                    if (nodeList.getLength() == 1) {
                        Node node = nodeList.item(0);
                        String string2 = node.getTextContent();
                        try {
                            int n = Integer.parseInt(string2.trim());
                            if (n < 0) {
                                commandResult.setIsSuccess(false);
                                String string3 = this.getBundle().getString("POWER_OPERATION_ON_ERR");
                                string3 = MessageFormat.format(string3, port.getName());
                                string3 = "[" + n + "]: " + string3;
                                commandResult.setStatusMessage(string3);
                                commandResult.setErrorDescription(new Object[]{string3});
                                RRCLogger.log(300, 1, "PowerOn operation failed. Error code : " + n);
                                return;
                            }
                        }
                        catch (NumberFormatException numberFormatException) {
                            RRCLogger.log(300, 1, "incorrect response from server " + string);
                            bl = true;
                        }
                    } else {
                        RRCLogger.log(300, 1, "incorrect response from server " + string);
                        bl = true;
                    }
                } else {
                    bl = true;
                    RRCLogger.log(300, 1, "Parser config error");
                }
            } else {
                RRCLogger.log(300, 1, "Null response from server");
                bl = true;
            }
            if (bl) {
                this.setFailureResult(commandResult, port.getName());
            }
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
    }

    private void setFailureResult(CommandResult commandResult, String string) {
        commandResult.setIsSuccess(false);
        String string2 = this.getBundle().getString("POWER_OPERATION_ON_ERR");
        string2 = MessageFormat.format(string2, string);
        commandResult.setStatusMessage(string2);
        commandResult.setErrorDescription(new Object[]{string2});
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof Port && (device.getDeviceClass().equals("KVM") || device.getDeviceClass().equals("Serial")) && ((Port)device).isOutletPort() && !((Port)device).isSecondaryPort()) {
            if (device.getDeviceConnector().usePowerControlFromCC) {
                return device.getDeviceConnector().getCCPowerControlPermission();
            }
            return true;
        }
        return false;
    }
}

