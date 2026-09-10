/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CChatEvent;
import amp.powerboard.clientapi.event.CClientListEvent;
import amp.powerboard.clientapi.event.CConfigLockEvent;
import amp.powerboard.clientapi.event.CConfigReportEvent;
import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CGetTCLLoaderStatusEvent;
import amp.powerboard.clientapi.event.CInternalCertStateEvent;
import amp.powerboard.clientapi.event.CInternalCertificateEvent;
import amp.powerboard.clientapi.event.CInternalDataComAndSystemEvent;
import amp.powerboard.clientapi.event.CInternalHWMonitoringEvent;
import amp.powerboard.clientapi.event.CInternalIPACLListEvent;
import amp.powerboard.clientapi.event.CInternalModemEvent;
import amp.powerboard.clientapi.event.CInternalNetworkEvent;
import amp.powerboard.clientapi.event.CInternalPingMonitoringEvent;
import amp.powerboard.clientapi.event.CInternalRadClientEvent;
import amp.powerboard.clientapi.event.CInternalResetEvent;
import amp.powerboard.clientapi.event.CInternalSMTPServerEvent;
import amp.powerboard.clientapi.event.CInternalSNMPAgentEvent;
import amp.powerboard.clientapi.event.CInternalSubscriptionEvent;
import amp.powerboard.clientapi.event.CInternalTimeEvent;
import amp.powerboard.clientapi.event.CInternalUserEvent;
import amp.powerboard.clientapi.event.CInternalUserInfoEvent;
import amp.powerboard.clientapi.event.CInternalUserListEvent;
import amp.powerboard.clientapi.event.CKillEvent;
import amp.powerboard.clientapi.event.CLoginInfoEvent;
import amp.powerboard.clientapi.event.CRequestSessionIdEvent;
import amp.powerboard.clientapi.event.CTCLPrintEvent;
import amp.powerboard.clientapi.event.CTCLResponseEvent;
import amp.powerboard.clientapi.event.CUpgradeReportEvent;
import amp.powerboard.clientapi.event.CVersionReportEvent;
import amp.powerboard.clientapi.net.CMsgInputStream;
import java.io.IOException;
import java.util.Hashtable;

public class CDataEventCreator {
    private int msgopCode;
    private String platform;
    private CDataEvent dataEvent;
    private String subPlatform;

    public CDataEventCreator(int n) {
        this.msgopCode = n;
    }

    public CDataEventCreator(int n, String string) {
        this.msgopCode = n;
        this.platform = string;
    }

    public CDataEventCreator(int n, String string, String string2) {
        this.msgopCode = n;
        this.platform = string;
        this.subPlatform = string2;
    }

    public String ipToString(int n) {
        return (n >>> 24 & 0xFF) + "." + (n >>> 16 & 0xFF) + "." + (n >>> 8 & 0xFF) + "." + (n >>> 0 & 0xFF);
    }

    public CDataEvent getDataEvent() {
        return this.dataEvent;
    }

    public void createClientListEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CClientListEvent cClientListEvent = null;
        int n = cMsgInputStream.readInt();
        String[] stringArray = new String[n];
        int n2 = 0;
        while (n2 < n) {
            short s = cMsgInputStream.readShort();
            stringArray[n2] = cMsgInputStream.readBytes(s);
            ++n2;
        }
        int n3 = cMsgInputStream.readInt();
        cMsgInputStream.close();
        cClientListEvent = new CClientListEvent(n, stringArray, n3);
        if (n == 0) {
            // empty if block
        }
        this.dataEvent = cClientListEvent;
    }

    public void createUserInfoEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalUserInfoEvent cInternalUserInfoEvent = null;
        short s = 0;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        String string = null;
        String string2 = null;
        String string3 = null;
        int n4 = 0;
        int[] nArray = null;
        n = cMsgInputStream.readInt();
        if (n == 0) {
            n2 = cMsgInputStream.readInt();
            s = cMsgInputStream.readShort();
            string = cMsgInputStream.readBytes(s);
            n3 = cMsgInputStream.readInt();
            s = cMsgInputStream.readShort();
            string2 = cMsgInputStream.readBytes(s);
            s = cMsgInputStream.readShort();
            string3 = cMsgInputStream.readBytes(s);
            n4 = cMsgInputStream.readInt();
            if (n4 > 0) {
                nArray = new int[n4];
                int n5 = 0;
                while (n5 < n4) {
                    nArray[n5] = cMsgInputStream.readInt();
                    ++n5;
                }
            }
            cMsgInputStream.close();
        } else {
            cMsgInputStream.close();
        }
        cInternalUserInfoEvent = new CInternalUserInfoEvent(n3, string2, string3, n, n2, string, n4, nArray);
        this.dataEvent = cInternalUserInfoEvent;
    }

    public void createLoginInfoEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CLoginInfoEvent cLoginInfoEvent = null;
        Hashtable<Integer, String> hashtable = null;
        Hashtable<Integer, String> hashtable2 = null;
        int n = cMsgInputStream.readInt();
        short s = cMsgInputStream.readShort();
        String string = cMsgInputStream.readBytes(s);
        if (!(this.platform == null || this.platform.equals("DC") || this.platform.equals("CONSOLE_MANAGER") && this.subPlatform.equals("DC"))) {
            int n2 = cMsgInputStream.readInt();
            hashtable = new Hashtable();
            hashtable2 = new Hashtable<Integer, String>();
            int n3 = 0;
            while (n3 < n2) {
                int n4 = cMsgInputStream.readInt();
                short s2 = cMsgInputStream.readShort();
                String string2 = cMsgInputStream.readBytes(s2);
                hashtable.put(new Integer(n4), string2);
                if (this.platform.startsWith("SecureManage") || this.platform.startsWith("SecureAccess") || this.platform.equals("COMMAND_CENTER") && (this.subPlatform.startsWith("SecureManage") || this.subPlatform.startsWith("SecureAccess"))) {
                    short s3 = cMsgInputStream.readShort();
                    String string3 = cMsgInputStream.readBytes(s3);
                    hashtable2.put(new Integer(n4), string3);
                }
                ++n3;
            }
        } else if (this.platform != null && (this.platform.equals("DC") || this.platform.equals("CONSOLE_MANAGER") && this.subPlatform.equals("DC"))) {
            hashtable = new Hashtable<Integer, String>(1);
            short s4 = cMsgInputStream.readShort();
            String string4 = cMsgInputStream.readBytes(s4);
            hashtable.put(new Integer(1), string4);
        }
        cLoginInfoEvent = this.platform != null ? new CLoginInfoEvent(n, string, hashtable, false) : new CLoginInfoEvent(n, string, false);
        this.dataEvent = cLoginInfoEvent;
    }

    public void createConfigLockEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CConfigLockEvent cConfigLockEvent = null;
        short s = 0;
        String string = null;
        int n = cMsgInputStream.readInt();
        if (n == 14) {
            s = cMsgInputStream.readShort();
            string = cMsgInputStream.readBytes(s);
        }
        cMsgInputStream.close();
        cConfigLockEvent = new CConfigLockEvent(n, string);
        this.dataEvent = cConfigLockEvent;
    }

    public void createNetworkEvent(CMsgInputStream cMsgInputStream, boolean bl) throws IOException, Exception {
        CInternalNetworkEvent cInternalNetworkEvent = null;
        int n = -1;
        String string = null;
        String string2 = null;
        String string3 = null;
        String string4 = null;
        short s = cMsgInputStream.readShort();
        String string5 = cMsgInputStream.readBytes(s);
        int n2 = cMsgInputStream.readInt();
        int n3 = cMsgInputStream.readInt();
        int n4 = cMsgInputStream.readInt();
        int n5 = cMsgInputStream.readInt();
        string = cMsgInputStream.readInt() == 0 ? "0" : "1";
        if (bl) {
            n = cMsgInputStream.readInt();
        }
        String string6 = new String(cMsgInputStream.readBytes(cMsgInputStream.available()));
        cMsgInputStream.close();
        string2 = this.ipToString(n2);
        string3 = this.ipToString(n3);
        string4 = this.ipToString(n4);
        cInternalNetworkEvent = new CInternalNetworkEvent(string5, string2, n5, string3, string4, string, n);
        this.dataEvent = cInternalNetworkEvent;
    }

    public void createHWMonitoringEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalHWMonitoringEvent cInternalHWMonitoringEvent = null;
        int n = cMsgInputStream.readInt() == 0 ? 0 : 1;
        int n2 = cMsgInputStream.readInt();
        cMsgInputStream.close();
        cInternalHWMonitoringEvent = new CInternalHWMonitoringEvent(n, n2);
        this.dataEvent = cInternalHWMonitoringEvent;
    }

    public void createPingMonitoringEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalPingMonitoringEvent cInternalPingMonitoringEvent = null;
        int n = cMsgInputStream.readInt();
        int n2 = cMsgInputStream.readInt();
        int[] nArray = new int[n2];
        int n3 = 0;
        while (n3 < n2) {
            nArray[n3] = cMsgInputStream.readInt();
            ++n3;
        }
        int n4 = cMsgInputStream.readInt();
        cMsgInputStream.close();
        cInternalPingMonitoringEvent = new CInternalPingMonitoringEvent(n, n2, nArray, n4);
        this.dataEvent = cInternalPingMonitoringEvent;
    }

    public void createTCLLoaderStatusEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        String string = null;
        int n = cMsgInputStream.readInt();
        int n2 = cMsgInputStream.readInt();
        int[] nArray = new int[n2];
        int n3 = 0;
        while (n3 < n2) {
            nArray[n3] = cMsgInputStream.readInt();
            ++n3;
        }
        int n4 = cMsgInputStream.readInt();
        short s = cMsgInputStream.readShort();
        string = cMsgInputStream.readBytes(s);
        CGetTCLLoaderStatusEvent cGetTCLLoaderStatusEvent = new CGetTCLLoaderStatusEvent(n, n2, nArray, n4, string);
        this.dataEvent = cGetTCLLoaderStatusEvent;
    }

    public void createTCLResponseEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        String string = "";
        try {
            while (cMsgInputStream.available() > 0) {
                short s = cMsgInputStream.readShort();
                string = string + cMsgInputStream.readBytes(s);
            }
            cMsgInputStream.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.dataEvent = new CTCLResponseEvent(string);
    }

    public void createTCLPrintEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        String string = "";
        try {
            while (cMsgInputStream.available() > 0) {
                short s = cMsgInputStream.readShort();
                string = string + cMsgInputStream.readBytes(s);
            }
            cMsgInputStream.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.dataEvent = new CTCLPrintEvent(string);
    }

    public void createSubscriptionEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalSubscriptionEvent cInternalSubscriptionEvent = null;
        String[] stringArray = null;
        String[] stringArray2 = null;
        int n = cMsgInputStream.readInt();
        stringArray = new String[n];
        stringArray2 = new String[n];
        int n2 = 0;
        while (n2 < n) {
            short s = cMsgInputStream.readShort();
            stringArray[n2] = cMsgInputStream.readBytes(s);
            s = cMsgInputStream.readShort();
            stringArray2[n2] = cMsgInputStream.readBytes(s);
            ++n2;
        }
        cMsgInputStream.close();
        cInternalSubscriptionEvent = new CInternalSubscriptionEvent(n, stringArray, stringArray2);
        this.dataEvent = cInternalSubscriptionEvent;
    }

    public void createGetSMTPServerEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalSMTPServerEvent cInternalSMTPServerEvent = null;
        int n = cMsgInputStream.readInt();
        cMsgInputStream.close();
        cInternalSMTPServerEvent = new CInternalSMTPServerEvent(n);
        this.dataEvent = cInternalSMTPServerEvent;
    }

    public void createGetSNMPAgentEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        short s;
        CInternalSNMPAgentEvent cInternalSNMPAgentEvent = null;
        String[] stringArray = null;
        String string = "";
        String string2 = "";
        stringArray = new String[10];
        int n = cMsgInputStream.readInt();
        int n2 = 0;
        while (n2 < 10) {
            s = cMsgInputStream.readShort();
            stringArray[n2] = cMsgInputStream.readBytes(s);
            ++n2;
        }
        int n3 = cMsgInputStream.readInt();
        s = cMsgInputStream.readShort();
        if (s > 0) {
            string = cMsgInputStream.readBytes(s);
        }
        if ((s = cMsgInputStream.readShort()) > 0) {
            string2 = cMsgInputStream.readBytes(s);
        }
        cMsgInputStream.close();
        cInternalSNMPAgentEvent = new CInternalSNMPAgentEvent(n, stringArray, n3, string, string2);
        this.dataEvent = cInternalSNMPAgentEvent;
    }

    public void createIPACLListEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        int n = cMsgInputStream.readInt();
        int n2 = cMsgInputStream.readInt();
        int[] nArray = null;
        int[] nArray2 = null;
        if (n2 > 0) {
            nArray = new int[n2];
            nArray2 = new int[n2];
            int n3 = 0;
            while (n3 < n2) {
                nArray[n3] = cMsgInputStream.readInt();
                nArray2[n3] = cMsgInputStream.readInt();
                ++n3;
            }
        }
        this.dataEvent = new CInternalIPACLListEvent(n, n2, nArray, nArray2);
    }

    public void createGetDataComAndSystemEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        int n = 0;
        int n2 = cMsgInputStream.readInt();
        int[] nArray = new int[n2 <= 32 ? n2 : 1];
        int[] nArray2 = new int[n2 <= 32 ? n2 : 1];
        int[] nArray3 = new int[n2 <= 32 ? n2 : 1];
        int[] nArray4 = new int[n2 <= 32 ? n2 : 1];
        int[] nArray5 = new int[n2 <= 32 ? n2 : 1];
        String[] stringArray = new String[n2 <= 32 ? n2 : 1];
        int[] nArray6 = new int[n2 <= 32 ? n2 : 1];
        int[] nArray7 = new int[n2 <= 32 ? n2 : 1];
        if (n2 > 32) {
            nArray[0] = n2;
            nArray3[0] = cMsgInputStream.readInt();
            nArray2[0] = cMsgInputStream.readInt();
            nArray4[0] = cMsgInputStream.readInt();
            nArray5[0] = cMsgInputStream.readInt();
            short s = cMsgInputStream.readShort();
            stringArray[0] = cMsgInputStream.readBytes(s);
            nArray6[0] = cMsgInputStream.readInt();
        } else {
            n = n2;
            int n3 = 0;
            while (n3 < n) {
                nArray6[n3] = cMsgInputStream.readInt();
                nArray[n3] = cMsgInputStream.readInt();
                nArray3[n3] = cMsgInputStream.readInt();
                nArray2[n3] = cMsgInputStream.readInt();
                nArray4[n3] = cMsgInputStream.readInt();
                nArray5[n3] = cMsgInputStream.readInt();
                if (this.platform.equals("CEREBUS_X16+") || this.platform.equals("CEREBUS_X32+")) {
                    nArray7[n3] = cMsgInputStream.readInt();
                }
                short s = cMsgInputStream.readShort();
                stringArray[n3] = cMsgInputStream.readBytes(s);
                ++n3;
            }
        }
        CInternalDataComAndSystemEvent cInternalDataComAndSystemEvent = this.platform.equals("CEREBUS_X16+") || this.platform.equals("CEREBUS_X32+") ? new CInternalDataComAndSystemEvent(n, nArray, nArray3, nArray2, nArray4, nArray5, stringArray, nArray6, nArray7) : new CInternalDataComAndSystemEvent(n, nArray, nArray3, nArray2, nArray4, nArray5, stringArray, nArray6);
        cMsgInputStream.close();
        this.dataEvent = cInternalDataComAndSystemEvent;
    }

    public void createGetUserEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        String string = null;
        String string2 = null;
        String string3 = null;
        int n4 = 0;
        int[] nArray = null;
        n = cMsgInputStream.readInt();
        if (n == 0) {
            n3 = cMsgInputStream.readInt();
            short s = cMsgInputStream.readShort();
            string = cMsgInputStream.readBytes(s);
            n2 = cMsgInputStream.readInt();
            s = cMsgInputStream.readShort();
            string2 = cMsgInputStream.readBytes(s);
            s = cMsgInputStream.readShort();
            string3 = cMsgInputStream.readBytes(s);
            n4 = cMsgInputStream.readInt();
            nArray = new int[n4];
            int n5 = 0;
            while (n5 < n4) {
                nArray[n5] = cMsgInputStream.readInt();
                ++n5;
            }
        } else {
            cMsgInputStream.close();
        }
        cMsgInputStream.close();
        CInternalUserEvent cInternalUserEvent = new CInternalUserEvent(n, n3, string, n2, string2, string3, n4, nArray);
        this.dataEvent = cInternalUserEvent;
    }

    public void createGetUserListEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        String[] stringArray = null;
        int[] nArray = null;
        int[] nArray2 = null;
        int[][] nArrayArray = null;
        int n = cMsgInputStream.readInt();
        stringArray = new String[n];
        nArray = new int[n];
        nArray2 = new int[n];
        nArrayArray = new int[n][];
        int n2 = 0;
        while (n2 < n) {
            short s = cMsgInputStream.readShort();
            stringArray[n2] = cMsgInputStream.readBytes(s);
            nArray[n2] = cMsgInputStream.readInt();
            nArray2[n2] = cMsgInputStream.readInt();
            nArrayArray[n2] = new int[nArray2[n2]];
            int n3 = 0;
            while (n3 < nArray2[n2]) {
                nArrayArray[n2][n3] = cMsgInputStream.readInt();
                ++n3;
            }
            ++n2;
        }
        CInternalUserListEvent cInternalUserListEvent = new CInternalUserListEvent(n, stringArray, nArray, nArray2, nArrayArray);
        this.dataEvent = cInternalUserListEvent;
    }

    public void createChatEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        short s = cMsgInputStream.readShort();
        String string = cMsgInputStream.readBytes(s);
        s = cMsgInputStream.readShort();
        String string2 = cMsgInputStream.readBytes(s);
        CChatEvent cChatEvent = new CChatEvent(string, string2);
        this.dataEvent = cChatEvent;
    }

    public void createUpgradeReportEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        String string = null;
        int n = cMsgInputStream.readInt();
        short s = cMsgInputStream.readShort();
        string = s == 0 ? " " : new String(cMsgInputStream.readBytes(s));
        CUpgradeReportEvent cUpgradeReportEvent = new CUpgradeReportEvent(n, string);
        this.dataEvent = cUpgradeReportEvent;
        cMsgInputStream.close();
    }

    public void createResetEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        short s = 0;
        String string = null;
        s = cMsgInputStream.readShort();
        string = cMsgInputStream.readBytes(s);
        CInternalResetEvent cInternalResetEvent = new CInternalResetEvent(string, 0);
        this.dataEvent = cInternalResetEvent;
        cMsgInputStream.close();
    }

    public void createKillEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        String string = null;
        short s = cMsgInputStream.readShort();
        string = cMsgInputStream.readBytes(s);
        CKillEvent cKillEvent = new CKillEvent(string);
        this.dataEvent = cKillEvent;
        cMsgInputStream.close();
    }

    public void createRequestSessionIdEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        int n = cMsgInputStream.readInt();
        int n2 = 0;
        int n3 = 0;
        if (n == 0) {
            n2 = cMsgInputStream.readInt();
            n3 = cMsgInputStream.readInt();
        }
        CRequestSessionIdEvent cRequestSessionIdEvent = new CRequestSessionIdEvent(n, n2, n3);
        this.dataEvent = cRequestSessionIdEvent;
        cMsgInputStream.close();
    }

    public void createVersionReportEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        Object var2_2 = null;
        String string = null;
        try {
            short s = cMsgInputStream.readShort();
            string = cMsgInputStream.readBytes(s);
            cMsgInputStream.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.dataEvent = new CVersionReportEvent(string);
    }

    public void createConfigReportEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        Object var2_2 = null;
        String string = "";
        try {
            while (cMsgInputStream.available() > 0) {
                short s = cMsgInputStream.readShort();
                string = string + cMsgInputStream.readBytes(s);
            }
            String string2 = new String("HP Secure Web Console");
            int n = string.indexOf(string2);
            if (n != -1) {
                int n2 = n + string2.length();
                string = string.substring(0, n) + "Arula Web Console" + string.substring(n2);
            }
            cMsgInputStream.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.dataEvent = new CConfigReportEvent(string);
    }

    public void createCertificateEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalCertificateEvent cInternalCertificateEvent = null;
        int n = cMsgInputStream.readInt();
        short s = cMsgInputStream.readShort();
        String string = cMsgInputStream.readBytes(s);
        cMsgInputStream.close();
        cInternalCertificateEvent = new CInternalCertificateEvent(n, string);
        this.dataEvent = cInternalCertificateEvent;
    }

    public void createModemEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalModemEvent cInternalModemEvent = null;
        int n = cMsgInputStream.readInt();
        int n2 = cMsgInputStream.readInt();
        int n3 = cMsgInputStream.readInt();
        int n4 = cMsgInputStream.readInt();
        int n5 = cMsgInputStream.readInt();
        int n6 = cMsgInputStream.readInt();
        cMsgInputStream.close();
        cInternalModemEvent = new CInternalModemEvent(n, n2, n3, n4, n5, n6);
        this.dataEvent = cInternalModemEvent;
    }

    public void createRadClientEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalRadClientEvent cInternalRadClientEvent = null;
        int n = cMsgInputStream.readInt();
        int n2 = cMsgInputStream.readInt();
        short s = cMsgInputStream.readShort();
        String string = cMsgInputStream.readBytes(s);
        int n3 = cMsgInputStream.readInt();
        int n4 = cMsgInputStream.readInt();
        s = cMsgInputStream.readShort();
        String string2 = cMsgInputStream.readBytes(s);
        int n5 = cMsgInputStream.readInt();
        cMsgInputStream.close();
        cInternalRadClientEvent = new CInternalRadClientEvent(n2, string, n3, n4, string2, n5, n);
        this.dataEvent = cInternalRadClientEvent;
    }

    public void createTimeEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalTimeEvent cInternalTimeEvent = null;
        int n = cMsgInputStream.readInt();
        int n2 = cMsgInputStream.readInt();
        int n3 = cMsgInputStream.readInt();
        int n4 = cMsgInputStream.readInt();
        int n5 = cMsgInputStream.readInt();
        int n6 = cMsgInputStream.readInt();
        cMsgInputStream.close();
        cInternalTimeEvent = new CInternalTimeEvent(n, n2, n3, n4, n5, n6);
        this.dataEvent = cInternalTimeEvent;
    }

    public void createCertStateEvent(CMsgInputStream cMsgInputStream) throws IOException, Exception {
        CInternalCertStateEvent cInternalCertStateEvent = null;
        int n = cMsgInputStream.readInt();
        cMsgInputStream.close();
        cInternalCertStateEvent = new CInternalCertStateEvent(n);
        this.dataEvent = cInternalCertStateEvent;
    }
}

