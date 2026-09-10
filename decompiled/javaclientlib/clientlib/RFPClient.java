/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.InetAddress;
import javaclientlib.clientlib.IProgress;
import javaclientlib.clientlib.TRConnection;
import javaclientlib.common.RFPParser;
import javaclientlib.tr.Constants;
import javaclientlib.tr.RFP;
import javaclientlib.tr.TRCMD_RFP_MESSAGE_DATA;
import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRLIB_COMM;
import javaclientlib.tr.TRLIB_REFERRAL_COMM;
import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.tr.TRRSP_RFP_MESSAGE_DATA;
import javaclientlib.tr.TRSRVR_SERIAL_PARAMS;
import javaclientlib.utils.Monitor;
import javaclientlib.utils.RRCLogger;

public class RFPClient
extends RFPParser
implements IProgress {
    public static final Object mutex = new Object();
    boolean isKX = false;
    private boolean boolCancel = false;
    private int iError;
    private FileOutputStream fileReceive;
    private int iReceived;
    private int iReceiveFileLength;
    private TRConnection objConnection;
    private Monitor objMonitor = new Monitor();
    private boolean autoReboot = false;
    private boolean done = false;
    private int iState = 2;
    public static final int RFP_CLIENT_STATE_NULL = 0;
    public static final int RFP_CLIENT_STATE_SEND = 1;
    public static final int RFP_CLIENT_STATE_WAIT_RESULT = 2;
    public static final int RFP_CLIENT_STATE_RECV_START = 3;
    public static final int RFP_CLIENT_STATE_RECV_DATA = 4;
    public static final int RFP_CLIENT_STATE_RECV_DONE = 5;
    public static final String TR_META_FILE_TOTAL_CONFIG = "All";
    public static final String TR_META_FILE_CONFIG = "DeviceAndPortConfigFile";
    public static final String TR_META_FILE_CONFIG_1 = "DeviceConfigFile";
    public static final String TR_META_FILE_CONFIG_2 = "MP432PortConfigFile";
    public static final String TR_META_FILE_USER_CONFIG = "UserConfigFile";
    public static final String TR_META_FILE_LOGEXPORT = "LogExportFile";
    public static final String TR_META_FILE_DEBUG = "DebugLogFile";
    public static final int TR_RFP_STRIP_HEADER = 1;
    public static final int TRLIB_ERROR_IO_CANCEL = 0;
    public static final int ERROR_WRITE_FAULT = 0;

    public void resetValues() {
        this.boolCancel = false;
        this.iState = 2;
        this.objMonitor = new Monitor();
        this.autoReboot = false;
        this.done = false;
    }

    public void setTRConnection(TRConnection tRConnection) throws Exception {
        Object var2_2 = null;
        Method method = null;
        Class[] classArray = new Class[]{Class.forName("javaclientlib.tr.TRCOMMAND"), Class.forName("java.lang.Object")};
        Class<?> clazz = this.getClass();
        Method method2 = clazz.getDeclaredMethod("TRRSP_RFP_Message_Glue", classArray);
        Object var7_7 = null;
        Object var8_8 = null;
        if (this.objConnection != null) {
            this.objConnection.setExtRspHandler(35, method, var8_8, var7_7, null, null, null);
        }
        this.objConnection = tRConnection;
        this.objConnection.setExtRspHandler(35, method2, this, this, method, var8_8, var7_7);
    }

    public TRConnection getTRConnection() {
        return this.objConnection;
    }

    public RFP getInfo(String string) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(string, "r");
            int n = this.parseFile(randomAccessFile);
            randomAccessFile.close();
            if (n != 0) {
                return null;
            }
            return this.getHeader();
        }
        catch (Exception exception) {
            return null;
        }
    }

    public boolean createRFPConnection() {
        return this.objConnection.createRFPSocketConnection();
    }

    public boolean closeRFPConnection() {
        return this.objConnection.closeRFPSocketConnection();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean send(String string, int n, boolean bl) throws Exception {
        this.resetValues();
        this.isKX = bl;
        FileInputStream fileInputStream = null;
        int n2 = 0;
        TRCMD_RFP_MESSAGE_DATA tRCMD_RFP_MESSAGE_DATA = new TRCMD_RFP_MESSAGE_DATA();
        if (this.objConnection == null) {
            return false;
        }
        try {
            boolean bl2;
            this.objConnection.setPingDevice(false);
            fileInputStream = new FileInputStream(string);
            this.boolCancel = false;
            this.iError = 0;
            int n3 = fileInputStream.available();
            int n4 = 4084;
            if (n4 > n3) {
                n4 = n3;
            }
            int n5 = 0;
            this.done = false;
            this.iState = 1;
            this.objMonitor.notifying();
            boolean bl3 = this.sendRFPMessage(1, n3);
            if (this.objConnection.isKX2Device()) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 64, "DoUpdateDevice thread notfiy all...");
                }
                Object object = mutex;
                synchronized (object) {
                    mutex.notifyAll();
                }
            }
            do {
                if ((n2 = fileInputStream.read(tRCMD_RFP_MESSAGE_DATA.toByteArray(), 12, n4)) < 0) {
                    this.iState = 0;
                    fileInputStream.close();
                    break;
                }
                n5 += n2;
                if (n2 > 0) {
                    tRCMD_RFP_MESSAGE_DATA.setCommand((byte)53);
                    tRCMD_RFP_MESSAGE_DATA.setCmdLength((short)(12 + n2));
                    tRCMD_RFP_MESSAGE_DATA.setPktID((byte)0);
                    tRCMD_RFP_MESSAGE_DATA.setPhase(2);
                    tRCMD_RFP_MESSAGE_DATA.setFileData(n5);
                    bl3 = this.objConnection.sendRFPTRCmd(tRCMD_RFP_MESSAGE_DATA);
                    if (!bl3) {
                        this.iState = 0;
                        this.boolCancel = true;
                        fileInputStream.close();
                        break;
                    }
                    if (this.objConnection.isKX2Device()) {
                        do {
                            bl2 = this.objMonitor.waiting(500);
                        } while (this.objConnection.isConnected() && !this.boolCancel && bl2);
                        if (this.iError != 0 && !this.objConnection.isConnected()) {
                            this.iError = 0x20000001;
                            break;
                        }
                        if (this.iError != 0) {
                            this.iState = 0;
                            fileInputStream.close();
                        }
                    } else if (this.objConnection.getServerID().getProtocolVersion() >= 21) {
                        do {
                            bl2 = this.objMonitor.waiting(500);
                        } while (this.objConnection.isConnected() && !this.boolCancel && bl2);
                        if (this.iError != 0 && !this.objConnection.isConnected()) {
                            this.iError = 0x20000001;
                            break;
                        }
                        if (this.iError != 0) {
                            this.iState = 0;
                            fileInputStream.close();
                        }
                    }
                }
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 64, "Update Device...Sent..." + n5);
                }
                this.progress(n3, n5);
            } while (n2 > 0 && !this.boolCancel);
            if (this.boolCancel) {
                TRConnection.setLastError(this.iError | 0x2002000);
                this.iState = 0;
                fileInputStream.close();
                return false;
            }
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 64, "Sending done and state is wait result");
            }
            this.iState = 2;
            bl3 = this.sendRFPMessage(3, 0);
            if (!bl3) {
                this.iState = 0;
                fileInputStream.close();
                return false;
            }
            do {
                bl2 = this.objMonitor.waiting(500);
            } while (this.objConnection.isConnected() && !this.boolCancel && !this.autoReboot && !this.done);
            if (this.iError != 0) {
                TRConnection.setLastError(this.iError | 0x2002000);
                this.iState = 0;
                fileInputStream.close();
                return false;
            }
            fileInputStream.close();
            this.iState = 0;
            return true;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            this.iState = 0;
            return false;
        }
    }

    public boolean sendRFPMessage(int n, int n2) throws Exception {
        TRCMD_RFP_MESSAGE_DATA tRCMD_RFP_MESSAGE_DATA = new TRCMD_RFP_MESSAGE_DATA();
        tRCMD_RFP_MESSAGE_DATA.setCmdLength((short)12);
        tRCMD_RFP_MESSAGE_DATA.setCommand((byte)53);
        tRCMD_RFP_MESSAGE_DATA.setPhase(n);
        tRCMD_RFP_MESSAGE_DATA.setFileData(n2);
        this.objConnection.sendRFPTRCmd(tRCMD_RFP_MESSAGE_DATA);
        return true;
    }

    public boolean getNeedAutoReboot() {
        return this.autoReboot;
    }

    @Override
    public void progress(int n, int n2) {
    }

    public boolean receive(String string, String string2, int n) throws Exception {
        this.resetValues();
        StringBuffer stringBuffer = new StringBuffer();
        boolean bl = false;
        if (string.equalsIgnoreCase(TR_META_FILE_CONFIG)) {
            stringBuffer.append("<?xml version=\"1.0\"?><" + Constants.COMPANY_NAME_NOSPACE + "_File_Package>");
            stringBuffer.append("<RFP_File>");
            stringBuffer.append("<MetaFileName>DeviceConfigFile</MetaFileName>");
            stringBuffer.append("</RFP_File>");
            stringBuffer.append("<RFP_File>");
            stringBuffer.append("<MetaFileName>MP432PortConfigFile</MetaFileName>");
            stringBuffer.append("</RFP_File>");
            stringBuffer.append("</" + Constants.COMPANY_NAME_NOSPACE + "_File_Package>");
        } else if (string.equalsIgnoreCase(TR_META_FILE_USER_CONFIG) || string.equalsIgnoreCase(TR_META_FILE_LOGEXPORT) || string.equalsIgnoreCase(TR_META_FILE_DEBUG)) {
            stringBuffer.append("<?xml version=\"1.0\"?><" + Constants.COMPANY_NAME_NOSPACE + "_File_Package>");
            stringBuffer.append("<RFP_File>");
            stringBuffer.append("<MetaFileName>" + string + "</MetaFileName>");
            stringBuffer.append("</RFP_File></" + Constants.COMPANY_NAME_NOSPACE + "_File_Package>");
        } else {
            String string3 = this.objConnection.isKX2Device() ? "MetaFileName" : "szfileName";
            stringBuffer.append("<?xml version=\"1.0\"?><" + Constants.COMPANY_NAME_NOSPACE + "_File_Package>");
            stringBuffer.append("<RFP_File><" + string3 + ">" + string);
            stringBuffer.append("</" + string3 + "></RFP_File></" + Constants.COMPANY_NAME_NOSPACE + "_File_Package>");
        }
        return this.receiveEx(stringBuffer.toString(), string2, n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean sendRFPMessagePackage(String string) {
        DoRcvRFPThread doRcvRFPThread = null;
        int n = string.length() + 1;
        int n2 = 0;
        TRCMD_RFP_MESSAGE_DATA tRCMD_RFP_MESSAGE_DATA = new TRCMD_RFP_MESSAGE_DATA();
        try {
            int n3;
            this.iError = 0;
            this.log("rfp request" + string);
            if (this.objConnection == null) {
                TRConnection.setLastError(-1);
                return false;
            }
            this.boolCancel = false;
            this.objMonitor = new Monitor();
            this.iReceived = 0;
            this.iReceiveFileLength = 0;
            this.iState = 3;
            boolean bl = this.sendRFPMessage(0, n);
            if (this.objConnection.isKX2Device()) {
                doRcvRFPThread = new DoRcvRFPThread();
                doRcvRFPThread.start();
                Thread.sleep(500L);
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 64, "DoUpdateDevice thread notfiy all...");
                }
                Object object = mutex;
                synchronized (object) {
                    mutex.notifyAll();
                }
            }
            if (!bl) {
                TRConnection.setLastError(-1);
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(400, 65, "Sending RFP Request_Start failed.");
                }
                return false;
            }
            int n4 = 4084;
            n2 = 0;
            tRCMD_RFP_MESSAGE_DATA.setRFPData(string.getBytes());
            do {
                n3 = n > n4 ? n4 : n;
                tRCMD_RFP_MESSAGE_DATA.setPhase(1);
                tRCMD_RFP_MESSAGE_DATA.setFileData(n);
                tRCMD_RFP_MESSAGE_DATA.setCmdLength((short)(12 + n3));
                tRCMD_RFP_MESSAGE_DATA.setCommand((byte)53);
                tRCMD_RFP_MESSAGE_DATA.setPktID((byte)0);
                bl = this.objConnection.sendRFPTRCmd(tRCMD_RFP_MESSAGE_DATA);
                if (bl) continue;
                TRConnection.setLastError(-1);
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(400, 65, "Sending RFP Message failed.");
                }
                return false;
            } while ((n2 += n3) < n);
            bl = this.sendRFPMessage(2, 0);
            if (!bl) {
                TRConnection.setLastError(-1);
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(400, 65, "Sending RFP Request_Done failed.");
                }
                return false;
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        do {
            boolean bl = this.objMonitor.waiting(500);
        } while (this.objConnection.isConnected() && !this.boolCancel && !this.autoReboot && !this.done);
        TRConnection.setLastError(this.iError | 0x2002000);
        if (this.iError != 0) {
            this.iState = 0;
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean receiveEx(String string, String string2, int n) throws Exception {
        DoRcvRFPThread doRcvRFPThread = null;
        int n2 = string.length() + 1;
        int n3 = 0;
        TRCMD_RFP_MESSAGE_DATA tRCMD_RFP_MESSAGE_DATA = new TRCMD_RFP_MESSAGE_DATA();
        try {
            boolean bl;
            int n4;
            this.log("rfp request" + string);
            if (this.objConnection == null) {
                return false;
            }
            this.boolCancel = false;
            this.fileReceive = new FileOutputStream(string2);
            this.objMonitor = new Monitor();
            this.iReceived = 0;
            this.iReceiveFileLength = 0;
            this.iError = 0;
            this.iState = 3;
            boolean bl2 = this.sendRFPMessage(0, n2);
            if (this.objConnection.isKX2Device()) {
                doRcvRFPThread = new DoRcvRFPThread();
                doRcvRFPThread.start();
                Thread.sleep(500L);
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 64, "DoUpdateDevice thread notfiy all...");
                }
                Object object = mutex;
                synchronized (object) {
                    mutex.notifyAll();
                }
            }
            if (!bl2) {
                this.fileReceive.close();
                return false;
            }
            int n5 = 4084;
            n3 = 0;
            this.iState = 3;
            tRCMD_RFP_MESSAGE_DATA.setRFPData(string.getBytes());
            do {
                n4 = n2 > n5 ? n5 : n2;
                tRCMD_RFP_MESSAGE_DATA.setPhase(1);
                tRCMD_RFP_MESSAGE_DATA.setFileData(n2);
                tRCMD_RFP_MESSAGE_DATA.setCmdLength((short)(12 + n4));
                tRCMD_RFP_MESSAGE_DATA.setCommand((byte)53);
                tRCMD_RFP_MESSAGE_DATA.setPktID((byte)0);
                bl2 = this.objConnection.sendRFPTRCmd(tRCMD_RFP_MESSAGE_DATA);
                if (!bl2) {
                    this.fileReceive.close();
                    return false;
                }
                string = string + n4;
            } while ((n3 += n4) < n2);
            bl2 = this.sendRFPMessage(2, 0);
            if (!bl2) {
                this.fileReceive.close();
                return false;
            }
            do {
                bl = this.objMonitor.waiting(500);
            } while (this.objConnection.isConnected() && !this.boolCancel && bl);
            if (doRcvRFPThread != null) {
                doRcvRFPThread.interrupt();
            }
            if (this.iError != 0 && !this.objConnection.isConnected()) {
                this.iError = 0x20000001;
            }
            if (this.iError != 0) {
                TRConnection.setLastError(this.iError | 0x2002000);
                this.iState = 0;
                this.fileReceive.close();
                return false;
            }
            if ((n & 1) == 1) {
                return this.stripHeader(string2);
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        this.fileReceive.close();
        return true;
    }

    public boolean stripHeader(String string) {
        boolean bl = true;
        RandomAccessFile randomAccessFile = null;
        FileOutputStream fileOutputStream = null;
        byte[] byArray = new byte[1024];
        try {
            int n;
            randomAccessFile = new RandomAccessFile(string, "r");
            int n2 = this.parseFile(randomAccessFile);
            randomAccessFile.seek(this.iDataOffset);
            fileOutputStream = new FileOutputStream("RFP_Tmp9.545");
            do {
                if ((n = randomAccessFile.read(byArray, 0, 1024)) < 0) {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
                fileOutputStream.write(byArray, 0, n);
                int n3 = byArray.length;
                if (n3 == n) continue;
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (fileOutputStream == null) continue;
                fileOutputStream.close();
            } while (n == 1024);
            randomAccessFile.close();
            fileOutputStream.close();
            randomAccessFile = null;
            fileOutputStream = null;
            File file = new File("RFP_Tmp9.545");
            File file2 = new File(string);
            bl = file2.delete();
            bl = file.renameTo(file2);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return bl;
    }

    private String dumpState(TRRSP_RFP_MESSAGE_DATA tRRSP_RFP_MESSAGE_DATA) {
        String string = "UNKNOWN(" + this.iState + ")";
        String string2 = "UNKNOWN(" + tRRSP_RFP_MESSAGE_DATA.getPhase() + ")";
        String string3 = "UNKNOWN(" + tRRSP_RFP_MESSAGE_DATA.getRfpData() + ")";
        switch (this.iState) {
            case 4: {
                string = "RECV_DATA";
                break;
            }
            case 0: {
                string = "NULL";
                break;
            }
            case 5: {
                string = "DONE";
                break;
            }
            case 3: {
                string = "RECV_START";
                break;
            }
            case 1: {
                string = "SEND";
                break;
            }
            case 2: {
                string = "WAIT_RESULT";
            }
        }
        switch (tRRSP_RFP_MESSAGE_DATA.getPhase()) {
            case 4: {
                string2 = "CANCEL";
                break;
            }
            case 3: {
                string2 = "DONE";
                break;
            }
            case 2: {
                string2 = "DATA";
                break;
            }
            case 5: {
                string2 = "RESULT";
                break;
            }
            case 1: {
                string2 = "REQUEST_DATA";
            }
        }
        switch (tRRSP_RFP_MESSAGE_DATA.getRfpData()) {
            case 25: {
                string3 = "AUTO_REBOOT";
                break;
            }
            case 0: {
                string3 = "UPDATE_SUCCESS";
                break;
            }
            case 24: {
                string3 = "PACKET_ACK";
                break;
            }
            case 13: {
                string3 = "INTERNAL";
            }
        }
        return "State = " + string + " Phase = " + string2 + " Data = " + string3;
    }

    public boolean TRRSP_RFP_Message(TRCOMMAND tRCOMMAND) {
        TRRSP_RFP_MESSAGE_DATA tRRSP_RFP_MESSAGE_DATA = new TRRSP_RFP_MESSAGE_DATA(tRCOMMAND.toByteArray());
        if (RRCLogger.logEnabled) {
            RRCLogger.log(200, 64, "RFP MESSAGE : " + this.dumpState(tRRSP_RFP_MESSAGE_DATA));
        }
        try {
            if (tRRSP_RFP_MESSAGE_DATA.getPhase() == 4) {
                if (this.iState != 0) {
                    this.iError = tRRSP_RFP_MESSAGE_DATA.getRfpData() != 0 ? tRRSP_RFP_MESSAGE_DATA.getRfpData() : 0;
                    this.iState = 0;
                    this.cancel();
                }
                return true;
            }
            switch (this.iState) {
                case 0: {
                    this.iError = 0;
                    if (tRRSP_RFP_MESSAGE_DATA.getRfpData() != 0) {
                        if (tRRSP_RFP_MESSAGE_DATA.getRfpData() == 25) {
                            this.autoReboot = true;
                            this.iError = 0;
                        } else if (tRRSP_RFP_MESSAGE_DATA.getRfpData() == 0) {
                            this.done = true;
                            this.iError = 0;
                        } else {
                            this.iError = tRRSP_RFP_MESSAGE_DATA.getRfpData();
                            this.iError = this.objConnection.getServerID().getProtocolVersion() < 21 ? (this.iError += 0x20001000) : (this.iError += 0x2002000);
                        }
                    } else {
                        this.iError = 0;
                    }
                    this.objMonitor.notifying();
                    return true;
                }
                case 1: {
                    if (this.objConnection.isKX2Device()) {
                        switch (tRRSP_RFP_MESSAGE_DATA.getPhase()) {
                            case 5: {
                                if (tRRSP_RFP_MESSAGE_DATA.getRfpData() != 24) {
                                    this.iError = 0x2002000 + tRRSP_RFP_MESSAGE_DATA.getRfpData();
                                    this.boolCancel = true;
                                } else {
                                    this.iError = 0;
                                }
                                this.objMonitor.notifying();
                                return true;
                            }
                        }
                        System.err.println("Unexpected RFP Code:" + tRRSP_RFP_MESSAGE_DATA.getPhase());
                        break;
                    }
                    if (this.objConnection.getServerID().getProtocolVersion() < 21) break;
                    switch (tRRSP_RFP_MESSAGE_DATA.getPhase()) {
                        case 5: {
                            if (tRRSP_RFP_MESSAGE_DATA.getRfpData() != 24) {
                                this.iError = 0x2002000 + tRRSP_RFP_MESSAGE_DATA.getRfpData();
                                this.boolCancel = true;
                            } else {
                                this.iError = 0;
                            }
                            this.objMonitor.notifying();
                            return true;
                        }
                    }
                    System.err.println("Unexpected RFP Code:" + tRRSP_RFP_MESSAGE_DATA.getPhase());
                    break;
                }
                case 2: {
                    switch (tRRSP_RFP_MESSAGE_DATA.getPhase()) {
                        case 3: {
                            this.iError += 0x2002000;
                            this.objMonitor.notifying();
                            return true;
                        }
                        case 5: {
                            if (tRRSP_RFP_MESSAGE_DATA.getRfpData() != 0) {
                                if (tRRSP_RFP_MESSAGE_DATA.getRfpData() == 25) {
                                    this.autoReboot = true;
                                    this.iError = 0;
                                } else {
                                    this.iError = tRRSP_RFP_MESSAGE_DATA.getRfpData();
                                    this.iError = this.objConnection.getServerID().getProtocolVersion() < 21 ? (this.iError += 0x20001000) : (this.iError += 0x2002000);
                                }
                            } else {
                                this.iError = 0;
                                this.done = true;
                            }
                            this.objMonitor.notifying();
                            return true;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (tRRSP_RFP_MESSAGE_DATA.getPhase()) {
                        case 1: {
                            this.iState = 4;
                            this.iReceiveFileLength = tRRSP_RFP_MESSAGE_DATA.getRfpData();
                            this.iReceived = 0;
                            this.progress(this.iReceiveFileLength, 0);
                            return true;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (tRRSP_RFP_MESSAGE_DATA.getPhase()) {
                        case 2: {
                            int n = tRRSP_RFP_MESSAGE_DATA.getCmdLength() - 12;
                            this.fileReceive.write(tRRSP_RFP_MESSAGE_DATA.toByteArray(), 12, n);
                            int n2 = n;
                            if (n2 + this.iReceived > this.iReceiveFileLength) break;
                            this.iReceived += n2;
                            this.progress(this.iReceiveFileLength, this.iReceived);
                            if (this.iReceived == this.iReceiveFileLength) {
                                this.iState = 5;
                            }
                            return true;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (tRRSP_RFP_MESSAGE_DATA.getPhase()) {
                        case 3: {
                            this.iState = 0;
                            this.iError = 0;
                            this.objMonitor.notifying();
                            return true;
                        }
                    }
                }
            }
            System.out.println("before cancel RFP state " + this.dumpState(tRRSP_RFP_MESSAGE_DATA));
            this.cancel();
            this.iState = 0;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return true;
    }

    public void cancel() {
        if (this.iError == 0) {
            this.iError = 0;
        }
        this.boolCancel = true;
        this.objMonitor.notifying();
    }

    public boolean TRRSP_RFP_Message_Glue(TRCOMMAND tRCOMMAND, Object object) {
        TRRSP_RFP_MESSAGE_DATA tRRSP_RFP_MESSAGE_DATA = new TRRSP_RFP_MESSAGE_DATA(tRCOMMAND.toByteArray());
        RFPClient rFPClient = (RFPClient)object;
        return rFPClient.TRRSP_RFP_Message(tRRSP_RFP_MESSAGE_DATA);
    }

    private void log(String string) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 64, "RFPCLient::" + string);
        }
    }

    public static void main(String[] stringArray) {
        TRLIB_COMM tRLIB_COMM = new TRLIB_COMM();
        TRLIB_USERINFO tRLIB_USERINFO = new TRLIB_USERINFO();
        TRLIB_REFERRAL_COMM tRLIB_REFERRAL_COMM = new TRLIB_REFERRAL_COMM();
        TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS = new TRSRVR_SERIAL_PARAMS();
        boolean bl = false;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName("172.16.35.103");
            tRLIB_COMM.setIpAddress(inetAddress.hashCode());
            tRLIB_COMM.setConnType(2);
            tRLIB_COMM.setServerName("IP Reach".getBytes());
            tRLIB_COMM.setDnsName("IP Reach".getBytes());
            tRLIB_COMM.setIpPort(5000);
            tRLIB_COMM.setFindBy(0);
            tRLIB_USERINFO.setName("admin".getBytes());
            tRLIB_USERINFO.setPassword(Constants.DEFAULT_PASSWORD.getBytes());
            tRLIB_REFERRAL_COMM.setVersion(1);
            TRConnection tRConnection = new TRConnection();
            RFPClient rFPClient = new RFPClient();
            RFP rFP = new RFP();
            Thread.sleep(10L);
            rFP = rFPClient.getInfo("c:/temp.RFP");
            if (rFP != null) {
                for (int i = 0; i < 64; ++i) {
                    if (rFP.getRfpFile(i) == null) continue;
                    if (rFP.getRfpFile(i).getFileName() != null) {
                        System.out.println("fileName  " + new String(rFP.getRfpFile(i).getFileName()));
                    }
                    if (rFP.getRfpFile(i).getScript() != null) {
                        System.out.println("script    " + new String(rFP.getRfpFile(i).getScript()));
                    }
                    if (rFP.getRfpFile(i).getSignature() != null) {
                        System.out.println("signature " + new String(rFP.getRfpFile(i).getSignature()));
                    }
                    if (rFP.getRfpFile(i).getModel() != null) {
                        System.out.println("model     " + new String(rFP.getRfpFile(i).getModel()));
                    }
                    if (rFP.getRfpFile(i).getVersion() != null) {
                        System.out.println("version   " + new String(rFP.getRfpFile(i).getVersion()));
                    }
                    if (rFP.getRfpFile(i).getVersionMin() != null) {
                        System.out.println("versionMin" + new String(rFP.getRfpFile(i).getVersionMin()));
                    }
                    if (rFP.getRfpFile(i).getVersionMax() == null) continue;
                    System.out.println("versionMax" + new String(rFP.getRfpFile(i).getVersionMax()));
                }
            }
            rFPClient.stripHeader("c:/temp.RFP");
            System.out.print("Disconnect Connection");
            System.out.println("--------------------------------");
            tRConnection.disConnect();
            System.out.println("**************+Disconnect Successful **********");
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    private class DoRcvRFPThread
    extends Thread {
        protected DoRcvRFPThread() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 64, "DoRecieveRFP thread waiting...");
            }
            Object object = mutex;
            synchronized (object) {
                try {
                    mutex.wait();
                }
                catch (InterruptedException interruptedException) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.logException(interruptedException);
                    }
                    return;
                }
            }
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 64, "DoRecieveRFP thread awake...");
            }
            RFPClient.this.objConnection.readRFPResponseMessage();
        }
    }
}

