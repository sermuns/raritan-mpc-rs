/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp;

import nn.pp.core.NotificationEvent;
import nn.pp.core.T;

public class MspNotificationEvent
extends NotificationEvent {
    public MspNotificationEvent(int n, int n2) {
        super(n, n2);
    }

    @Override
    public NotificationEvent clone() {
        return new MspNotificationEvent(this.flags, this.errorCode);
    }

    @Override
    public String getMessage() {
        return MspNotificationEvent.getMessageString(this.getErrorCode());
    }

    public static String getMessageString(int n) {
        switch (n) {
            case 0x30000001: {
                return T._("Virtual Media mounting on the Port denied.\nContact your system administrator.");
            }
            case 0x30000002: {
                return T._("Virtual Media mounting on the Port denied.\nContact your system administrator.");
            }
            case 0x30000003: {
                return T._("Permission to transfer files from target to the virtual drive denied.\nContact your system administrator.");
            }
            case 0x30010001: {
                return T._("Virtual Media mounting on the Port failed. Please try the again.\nIf you receive this error repeatedly, contact technical support for assistance.");
            }
            case 805371906: {
                return T._("Virtual Media mounting on the Port unavailable.\nContact your system administrator");
            }
            case 0x30010003: {
                return T._("There is already a Virtual Media connection established to the selected drive.\nPlease disconnect the current virtual media connection before\nre-establishing a new Virtual media connection.");
            }
            case 805371908: {
                return T._("There is already another virtual media connection active on the selected image.\nPlease disconnect the current virtual media connection before\nre-establishing a new Virtual media connection.");
            }
            case 805371909: {
                return T._("Virtual Media mounting on the Port denied.\nVM Share Mode is set to OFF.\nContact your system administrator.");
            }
            case 805437441: {
                return T._("There was a problem with Virtual Media communication. Please try the again.\nIf you receive this error repeatedly, contact technical support for assistance.");
            }
            case 0x30020002: {
                return T._("Virtual Media mounting on the Port failed. Please try again.\nIf you receive this error repeatedly, contact technical support for assistance");
            }
            case 805371910: {
                return T._("A VM connection is currently in use. Please try again later.");
            }
            case 0x32020003: {
                return T._("User cancelled connection.");
            }
            case 838991876: {
                return T._("Device cancelled connection.");
            }
            case 805371911: {
                return T._("Communication Error. Failed to connect Virtual Media to target.");
            }
        }
        return T._("Unknown error");
    }
}

