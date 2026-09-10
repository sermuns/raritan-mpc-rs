/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import nn.pp.core.NotificationEvent;
import nn.pp.core.T;

public class RfbNotificationEvent
extends NotificationEvent {
    public RfbNotificationEvent(int n, int n2) {
        super(n, n2);
    }

    public RfbNotificationEvent(int n, int n2, boolean bl, String string) {
        super(n, n2, bl, string);
    }

    @Override
    public NotificationEvent clone() {
        return new RfbNotificationEvent(this.flags, this.errorCode);
    }

    @Override
    public String getMessage() {
        return RfbNotificationEvent.getMessageString(this.getErrorCode());
    }

    public static String getMessageString(int n) {
        String string = null;
        switch (n) {
            case 0x10000001: {
                string = T._("Permission denied to perform this operation.\nContact your system administrator.");
                break;
            }
            case 0x10000002: {
                string = T._("Access denied. Your account is currently disabled.\nContact your system administrator.");
                break;
            }
            case 0x10000003: {
                string = T._("Authentication failed.\nPlease check your user name and password; then try again.");
                break;
            }
            case 0x10010004: {
                string = T._("Virtual Media mounting on the Port denied.\nVM Share Mode is set to OFF.\nContact your system administrator.");
                break;
            }
            case 0x10010005: {
                string = T._("Virtual Media mounting on the Port failed.\nThere is an unexpected communication error.\nPlease try again later.");
                break;
            }
            case 0x10010006: {
                string = T._("Virtual Media mounting on the Port failed.\nThe selected mass storage device is already in use.");
                break;
            }
            case 0x10010007: {
                string = T._("Virtual Media mounting on the Port failed.\nPermission to mount the image onto the target is denied.\nContact your system administrator.");
                break;
            }
            case 0x10010008: {
                string = T._("A VM connection is currently in use. Please try again later.");
                break;
            }
            case 0x10010001: {
                string = T._("Maximum number of virtual media connections reached on the Port.\nPlease try again later.");
                break;
            }
            case 0x10010002: {
                string = T._("Virtual Media mounting on the Port failed.\nUnable to connect to the File Server \nor incorrect File Server user name and password.\nContact your system administrator.");
                break;
            }
            case 0x10010003: {
                string = T._("Virtual Media mounting on the Port failed.\nConnection denied by File Server.\nContact your system administrator.");
                break;
            }
            case 0x10020001: {
                string = T._("Exclusive access is active.\nContact your system administrator.");
                break;
            }
            case 0x10020002: {
                string = T._("Connection to the Port denied.\nContact your system administrator.");
                break;
            }
            case 268566531: {
                string = T._("Maximum number of users are already connected to this port.\nPlease try again later.");
                break;
            }
            case 268566532: {
                string = T._("Connecting to the Port failed. This may be due to switching failure or could not detect video.\nPlease try again later.");
                break;
            }
            case 268566533: {
                string = T._("Maximum number of KVM connections has exceeded.\nPlease try again later.");
                break;
            }
            case 268566534: {
                string = T._("Connection to the port denied.\nVirtual Media connection is active and VM Share Mode is set to OFF.\nPlease try again later.");
                break;
            }
            case 285343753: {
                string = T._("WARNING: Your CIM has older firmware.\nSome of the newer features may not work.\nPlease upgrade your CIM to a newer firmware to take advantage\nof new features. Please refer to user manual for more details\non how to upgrade your CIM or contact your system administrator.");
                break;
            }
            case 0x10070001: {
                string = T._("Cannot switch to a different USB profile.\nVirtual Media connections are currently active.\nPlease close all the Virtual Media connections and try again.");
                break;
            }
            case 285671426: {
                string = T._("The currently active profile is different\nthan the designated preferred profile\n for this target.");
                break;
            }
            case 268894211: {
                string = T._("Cannot switch to the specified USB Profile. This profile is not applicable for the connected CIM.");
                break;
            }
            case 0x10040001: {
                string = T._("There was a problem in the communication. Please try again.\nIf you receive this error repeatedly. Contact Raritan technical support for assistance.");
                break;
            }
            case 268697602: {
                string = T._("There was a problem communicating with this device. Please update your client application.\nIf you receive this error repeatedly, contact Raritan techincal support for assistance.");
                break;
            }
            case 268697603: {
                string = T._("There was a problem communicating with this device. Please update your client application.\nIf you receive this error repeatedly, contact Raritan techincal support for assistance.");
                break;
            }
            case 0x10040004: {
                string = T._("There was a problem in the communication. Please try again.\nIf you receive this error repeatedly. Contact Raritan technical support for assistance.");
                break;
            }
            case 0x10060001: {
                string = T._("SSL connection enforced by the device, but unencrypted connection detected.\nPlease reconnect using SSL.");
                break;
            }
            case 268566538: {
                string = T._("Communication Error. Failed to connect to target.");
                break;
            }
            case 302383106: {
                string = T._("Client has been disconnected from target.");
                break;
            }
            case 10001: {
                string = T._("Invalid message from server. Please see logs (if enabled) for details");
                break;
            }
            default: {
                string = T._("Unknown error.\nContact your system administrator.");
            }
        }
        return string;
    }
}

