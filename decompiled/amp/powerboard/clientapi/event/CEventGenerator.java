/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEventCreator;
import amp.powerboard.clientapi.event.CEventDispatcher;
import amp.powerboard.clientapi.event.CInternalEvent;
import amp.powerboard.clientapi.event.CInternalStatusEvent;
import amp.powerboard.clientapi.event.CKillEvent;
import amp.powerboard.clientapi.event.CStatusEventCreator;
import amp.powerboard.clientapi.event.IGenerateEvent;
import amp.powerboard.clientapi.net.CMsgInputStream;
import java.io.IOException;

public class CEventGenerator
implements IGenerateEvent {
    private CEventDispatcher eventDispatcher;
    private int msgopCode;
    private String platform;
    private String subPlatform;

    public CEventGenerator(CEventDispatcher cEventDispatcher) {
        this.eventDispatcher = cEventDispatcher;
    }

    public CEventGenerator(CEventDispatcher cEventDispatcher, String string) {
        this.platform = string;
        this.eventDispatcher = cEventDispatcher;
    }

    public CEventGenerator(CEventDispatcher cEventDispatcher, String string, String string2) {
        this.platform = string;
        this.eventDispatcher = cEventDispatcher;
        this.subPlatform = string2;
    }

    public void generateKillEventObject() {
        if (this.eventDispatcher != null) {
            this.eventDispatcher.fireDataEvent(new CKillEvent("You have been logged off."));
        }
    }

    public void generateEventObject(CMsgInputStream cMsgInputStream, boolean bl) {
        if (cMsgInputStream == null) {
            CInternalEvent cInternalEvent = new CInternalEvent(0, 1, "YOUR SESSION CLOSED");
            if (this.eventDispatcher != null) {
                this.eventDispatcher.fireDataEvent(cInternalEvent);
            }
        } else {
            this.msgopCode = cMsgInputStream.opCode;
            switch (this.msgopCode) {
                case 1503: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createClientListEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 1: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createUserInfoEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 13: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode, this.platform, this.subPlatform);
                        cDataEventCreator.createLoginInfoEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 2: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createResetEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 15: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createConfigLockEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 16: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 18: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createVersionReportEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 19: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createConfigReportEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 5: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createNetworkEvent(cMsgInputStream, bl);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        iOException.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 4: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 27: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createHWMonitoringEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 26: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 29: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createPingMonitoringEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 28: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 4500: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 4501: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createTCLLoaderStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 4502: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 4503: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createTCLResponseEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 4504: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createTCLPrintEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 34: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 35: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 36: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createSubscriptionEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 37: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 38: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createGetSMTPServerEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 39: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 40: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createGetSNMPAgentEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 23: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 24: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 25: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createIPACLListEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 12: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode, this.platform);
                        cDataEventCreator.createGetDataComAndSystemEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 11: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 9: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createGetUserEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 8: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createSetUserEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 10: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 20: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createGetUserListEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 1501: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createChatEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 17: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createKillEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 3: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 14: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createUpgradeReportEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 500: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createRequestSessionIdEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 30: 
                case 31: 
                case 32: 
                case 33: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 1505: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 3000: {
                    CInternalStatusEvent cInternalStatusEvent = new CInternalStatusEvent(this.msgopCode, 0, 0);
                    if (this.eventDispatcher == null) break;
                    this.eventDispatcher.fireStatusEvent(cInternalStatusEvent);
                    break;
                }
                case 44: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createCertificateEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 43: 
                case 46: 
                case 48: 
                case 50: 
                case 61: 
                case 62: {
                    try {
                        CStatusEventCreator cStatusEventCreator = new CStatusEventCreator(this.msgopCode);
                        cStatusEventCreator.createGenericStatusEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireStatusEvent(cStatusEventCreator.getStatusEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 60: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createModemEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 49: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createRadClientEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 47: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createTimeEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                case 45: {
                    try {
                        CDataEventCreator cDataEventCreator = new CDataEventCreator(this.msgopCode);
                        cDataEventCreator.createCertStateEvent(cMsgInputStream);
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cDataEventCreator.getDataEvent());
                    }
                    catch (IOException iOException) {
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 2, "Error In Reading Data");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 3, "Illegal Data Format");
                        if (this.eventDispatcher == null) break;
                        this.eventDispatcher.fireDataEvent(cInternalEvent);
                    }
                    break;
                }
                default: {
                    CInternalEvent cInternalEvent = new CInternalEvent(this.msgopCode, 4, "Undefined Opcode");
                    if (this.eventDispatcher == null) break;
                    this.eventDispatcher.fireDataEvent(cInternalEvent);
                }
            }
        }
    }
}

